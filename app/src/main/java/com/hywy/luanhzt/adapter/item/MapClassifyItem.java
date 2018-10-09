package com.hywy.luanhzt.adapter.item;

import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cs.common.baserx.RxManager;
import com.cs.common.handler.BaseHandler_;
import com.cs.common.handler.SpringViewHandler;
import com.cs.common.listener.OnPostListenter;
import com.cs.common.utils.StringUtils;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.action.RxAction;
import com.hywy.luanhzt.activity.fragment.ChildFragment2;
import com.hywy.luanhzt.app.App;
import com.hywy.luanhzt.entity.MapClassify;
import com.hywy.luanhzt.entity.Reservoir;
import com.hywy.luanhzt.entity.RiverCourse;
import com.hywy.luanhzt.entity.TakeWater;
import com.hywy.luanhzt.entity.WaterQuality;
import com.hywy.luanhzt.entity.WaterRain;
import com.hywy.luanhzt.entity.action.LayerAction;
import com.hywy.luanhzt.entity.action.SiteLayerAction;
import com.hywy.luanhzt.key.Key;
import com.hywy.luanhzt.task.GetSiteInMapTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.ielse.view.SwitchView;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.flexibleadapter.items.ISectionable;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * 一张图分类
 *
 * @author Superman
 */

public class MapClassifyItem extends AbstractFlexibleItem<MapClassifyItem.EntityViewHolder> implements ISectionable<MapClassifyItem.EntityViewHolder, MapClassifyHeaderItem>, IFilterable {
    private MapClassify mapClassify;
    private MapClassifyHeaderItem headerItem;

    private boolean isOpened;//按钮开关状态，默认是关闭的
    RxManager mRxManager;
    List<Graphic> graphics;
    List<Object> objects;

    public MapClassify getData() {
        return mapClassify;
    }

    public MapClassifyItem(MapClassify mapClassify, MapClassifyHeaderItem headerItem) {
        this.mapClassify = mapClassify;
        this.headerItem = headerItem;
        mRxManager = new RxManager();
        graphics = new ArrayList<>();
        objects = new ArrayList<>();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_map_classify;
    }

    @Override
    public EntityViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new EntityViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public boolean filter(String constraint) {
        return false;
    }

    @Override
    public MapClassifyHeaderItem getHeader() {
        return headerItem;
    }

    @Override
    public void setHeader(MapClassifyHeaderItem header) {
        headerItem = header;
    }

    static class EntityViewHolder extends FlexibleViewHolder {
        TextView tv_name;
        SwitchView switchView;
        LinearLayout layout;

        public EntityViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            switchView = (SwitchView) view.findViewById(R.id.switchview);
            layout = (LinearLayout) view.findViewById(R.id.map_classify_layout);
        }

    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, final EntityViewHolder holder, int position, List payloads) {

        if (mapClassify != null) {
            if (StringUtils.hasLength(mapClassify.getText())) {
                holder.tv_name.setText(mapClassify.getText());
            }

            if (mapClassify.getParent().equals("#")) {
                holder.layout.setBackgroundResource(R.color.gray_light);
                holder.switchView.setVisibility(View.GONE);
                holder.layout.setPadding(10, 0, 0, 0);
                holder.tv_name.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            } else {
                holder.layout.setBackgroundResource(R.color.white);
                holder.switchView.setVisibility(View.VISIBLE);
                holder.layout.setPadding(15, 15, 15, 15);

                if (StringUtils.hasLength(mapClassify.getIconSkin())) {
                    int drawableId = holder.tv_name.getContext().getResources().getIdentifier("icon_" + mapClassify.getIconSkin(),
                            "drawable",
                            holder.tv_name.getContext().getPackageName());
                    if (drawableId != 0) {
                        holder.tv_name.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(holder.tv_name.getContext(), drawableId), null, null, null);
                    } else
                        holder.tv_name.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }

            }

            holder.switchView.setOpened(isOpened);

            List<FeatureLayer> list = new ArrayList<>();
            if (mapClassify.getTYPE().equals("0")) {

            } else if (mapClassify.getTYPE().equals("1")) {
                if (StringUtils.hasLength(mapClassify.getLAYERS())) {
                    String[] strs = StringUtils.delimitedListToStringArray(mapClassify.getLAYERS(), ",");
                    for (String str : strs) {
                        FeatureLayer featureLayer = new FeatureLayer(new ServiceFeatureTable(HttpUrl.getLayerUrl(str)));
                        list.add(featureLayer);
                    }
                    holder.switchView.setTag(list);
//                    mapClassify.setList(list);
                }
            }

        }

        holder.switchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOpened = holder.switchView.isOpened();
                initLayers(holder.switchView);
            }
        });
    }

    private void initLayers(SwitchView switchView) {
        List<FeatureLayer> list = (List<FeatureLayer>) switchView.getTag();

        //加载图层
        if (isOpened) {
            if (mapClassify.getTYPE().equals("0")) {
//                mRxManager.post(RxAction.ACTION_ADD_ARCGIS_SITE_LAYERS, new SiteLayerAction(SiteLayerAction.ADD_ACTION, mapClassify));
                getSiteInfo(mapClassify, switchView, graphics);
            } else if (mapClassify.getTYPE().equals("1")) {
                if (StringUtils.isNotNullList(list)) {
                    mRxManager.post(RxAction.ACTION_ADD_ARCGIS_LAYERS, new LayerAction(LayerAction.ADD_ACTION, list));
                }
            }
        } else {
            if (mapClassify.getTYPE().equals("0")) {
                mRxManager.post(RxAction.ACTION_ADD_ARCGIS_SITE_LAYERS, new SiteLayerAction(SiteLayerAction.REMOVE_ACTION, mapClassify, graphics, objects));
            } else if (mapClassify.getTYPE().equals("1")) {
                if (StringUtils.isNotNullList(list)) {
                    mRxManager.post(RxAction.ACTION_ADD_ARCGIS_LAYERS, new LayerAction(LayerAction.REMOVE_ACTION, list));
                }
            }
        }
    }

    private void getSiteInfo(MapClassify mapClassify, SwitchView switchView, List<Graphic> graphics) {
        SpringViewHandler handler = new SpringViewHandler(switchView.getContext());
        handler.setBuilder(new BaseHandler_.Builder().isShowToast(false).isWait(false));
        Map<String, Object> params = new HashMap<>();
        params.put("NAME_CODE", mapClassify.getIconSkin());
        params.put("ADCD", App.getInstance().getAccount().getADCD());
        handler.request(params, new GetSiteInMapTask(switchView.getContext(), mapClassify.getIconSkin()));
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                int drawableId = switchView.getContext().getResources().getIdentifier("icon_layer_" + mapClassify.getIconSkin(),
                        "drawable",
                        switchView.getContext().getPackageName());
                PictureMarkerSymbol symbol = new PictureMarkerSymbol((BitmapDrawable) ContextCompat.getDrawable(switchView.getContext(), drawableId));
                symbol.setHeight(30);
                symbol.setWidth(30);
                symbol.loadAsync();
                List<Object> list = (List<Object>) result.get(Key.RESULT);
                objects = list;
                if (StringUtils.isNotNullList(list)) {
                    symbol.addDoneLoadingListener(new Runnable() {
                        @Override
                        public void run() {
                            for (Object object : list) {
                                if (object instanceof WaterRain) {
                                    WaterRain waterRain = (WaterRain) object;
                                    if (StringUtils.hasLength(waterRain.getLGTD()) && StringUtils.hasLength(waterRain.getLTTD())) {
                                        Point point = new Point(Double.parseDouble(waterRain.getLGTD()), Double.parseDouble(waterRain.getLTTD()), ChildFragment2.newInstance().wgs84);
                                        Graphic graphic = new Graphic(point, symbol);
                                        graphics.add(graphic);
                                    }
                                } else if (object instanceof Reservoir) {
                                    Reservoir reservoir = (Reservoir) object;
                                    if (StringUtils.hasLength(reservoir.getLGTD()) && StringUtils.hasLength(reservoir.getLTTD())) {
                                        Point point = new Point(Double.parseDouble(reservoir.getLGTD()), Double.parseDouble(reservoir.getLTTD()), ChildFragment2.newInstance().wgs84);
                                        Graphic graphic = new Graphic(point, symbol);
                                        graphics.add(graphic);
                                    }
                                } else if (object instanceof WaterQuality) {
                                    WaterQuality waterQuality = (WaterQuality) object;
                                    if (StringUtils.hasLength(waterQuality.getLGTD()) && StringUtils.hasLength(waterQuality.getLTTD()) && !waterQuality.getLGTD().equals("null") && !waterQuality.getLTTD().equals("null")) {
                                        Point point = new Point(Double.parseDouble(waterQuality.getLGTD()), Double.parseDouble(waterQuality.getLTTD()), ChildFragment2.newInstance().wgs84);
                                        Graphic graphic = new Graphic(point, symbol);
                                        graphics.add(graphic);
                                    }
                                } else if (object instanceof RiverCourse) {
                                    RiverCourse riverCourse = (RiverCourse) object;
                                    if (StringUtils.hasLength(riverCourse.getLGTD()) && StringUtils.hasLength(riverCourse.getLTTD())) {
                                        Point point = new Point(Double.parseDouble(riverCourse.getLGTD()), Double.parseDouble(riverCourse.getLTTD()), ChildFragment2.newInstance().wgs84);
                                        Graphic graphic = new Graphic(point, symbol);
                                        graphics.add(graphic);
                                    }
                                } else if (object instanceof TakeWater) {
                                    TakeWater takeWater = (TakeWater) object;
                                    if (takeWater.getLGTD() != 0 && takeWater.getLTTD() != 0) {
                                        Point point = new Point(takeWater.getLGTD(), takeWater.getLTTD(), ChildFragment2.newInstance().wgs84);
                                        Graphic graphic = new Graphic(point, symbol);
                                        graphics.add(graphic);
                                    }
                                }

                            }
                            mRxManager.post(RxAction.ACTION_ADD_ARCGIS_SITE_LAYERS, new SiteLayerAction(SiteLayerAction.ADD_ACTION, mapClassify, graphics, list));

                        }
                    });

                }
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {

            }
        });
    }

    /**
     */
    @Override
    public boolean equals(Object o) {
        return false;
    }
}
