package com.hywy.luanhzt.activity.fragment;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.cs.common.adapter.BaseListFlexAdapter;
import com.cs.common.adapter.layoutmanager.FullyLinearLayoutManager;
import com.cs.common.base.BaseFragment;
import com.cs.common.handler.BaseHandler_;
import com.cs.common.handler.SpringViewHandler;
import com.cs.common.listener.OnPostListenter;
import com.cs.common.utils.IToast;
import com.cs.common.utils.StringUtils;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.MapRotationChangedEvent;
import com.esri.arcgisruntime.mapping.view.MapRotationChangedListener;
import com.esri.arcgisruntime.mapping.view.MapScaleChangedEvent;
import com.esri.arcgisruntime.mapping.view.MapScaleChangedListener;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.util.ListChangedEvent;
import com.esri.arcgisruntime.util.ListChangedListener;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.action.RxAction;
import com.hywy.luanhzt.activity.SiteDetailsActivity;
import com.hywy.luanhzt.adapter.item.MapClassifyItem;
import com.hywy.luanhzt.entity.AdcdLayerInfo;
import com.hywy.luanhzt.entity.MapClassify;
import com.hywy.luanhzt.entity.Reservoir;
import com.hywy.luanhzt.entity.RiverCourse;
import com.hywy.luanhzt.entity.TakeWater;
import com.hywy.luanhzt.entity.WaterQuality;
import com.hywy.luanhzt.entity.WaterRain;
import com.hywy.luanhzt.entity.action.LayerAction;
import com.hywy.luanhzt.entity.action.SiteLayerAction;
import com.hywy.luanhzt.key.Key;
import com.hywy.luanhzt.task.GetAdcdLayerListTask;
import com.hywy.luanhzt.task.GetMapClassifyTask;
import com.hywy.luanhzt.task.GetSiteInMapTask;
import com.hywy.luanhzt.view.MyDrawerLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import ch.ielse.view.SwitchView;
import rx.functions.Action1;

/**
 * Created by weifei on 17/1/19.
 */

public class ChildFragment2 extends BaseFragment {
    @Bind(R.id.map)
    com.esri.arcgisruntime.mapping.view.MapView map;
    @Bind(R.id.mapView)
    MapView mapView;
    @Bind(R.id.radio_layout)
    RadioGroup radio_layout;
    @Bind(R.id.btn2)
    RadioButton btn2;
    @Bind(R.id.btn1)
    RadioButton btn1;
    @Bind(R.id.drawer_layout)
    MyDrawerLayout drawerLayout;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerView;
    @Bind(R.id.iv_compass)
    ImageView ivCompass;

    ActionBarDrawerToggle mDrawerToggle; //侧滑菜单状态监听器

    private AMap aMap;
    private BaseListFlexAdapter mAdapter;

    private LayerList layerList;//图层url集合
    private List<Object> objects;//测站列表

    ArcGISMap arcGISMap;
    private List<WaterRain> list;
    private GraphicsOverlay graphicsOverlay;

    public final SpatialReference wgs84 = SpatialReference.create(4326);
    private Callout mCallout;
    private ArcGISTiledLayer luanImageLayer, defaltImageLayer;

    public static ChildFragment2 newInstance() {
        Bundle args = new Bundle();
        ChildFragment2 fragment = new ChildFragment2();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main2;
    }

    @Override
    protected void initView() {
        mAdapter = new BaseListFlexAdapter(getActivity());
        recyclerView.setLayoutManager(new FullyLinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);
//        ArcGISRuntimeEnvironment.setLicense("runtimelite,1000,rud4530363064,none,E9PJD4SZ8Y80C2EN0097");//设置arcgis通行证key值
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, R.string.back, R.string.back);
        drawerLayout.setDrawerListener(mDrawerToggle);
        objects = new ArrayList<>();
        initData();
    }


    /***切换地图**/
    private void initListeners() {
        mRxManager.on(RxAction.ACTION_ADD_ARCGIS_LAYERS, new Action1<LayerAction>() {
            @Override
            public void call(LayerAction action) {
                addArcgisLayers(action);
            }

        });
        mRxManager.on(RxAction.ACTION_ADD_ARCGIS_SITE_LAYERS, new Action1<SiteLayerAction>() {
            @Override
            public void call(SiteLayerAction action) {
                addSiteArcgisLayers(action);
            }

        });

        layerList.addListChangedListener(new ListChangedListener<Layer>() {
            @Override
            public void listChanged(ListChangedEvent<Layer> listChangedEvent) {
            }
        });

        radio_layout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                ArcGISTiledLayer tiledLayer = new ArcGISTiledLayer(HttpUrl.getLuanImageServer());
//                arcGISMap.getBasemap().getBaseLayers().add(tiledLayer);
                if (radioGroup.getCheckedRadioButtonId() == R.id.btn1) {
                    if (defaltImageLayer != null) {
                        defaltImageLayer.setVisible(true);
                    } else {
                        defaltImageLayer = new ArcGISTiledLayer(HttpUrl.getEarthServer());
                        defaltImageLayer.setVisible(true);
                        arcGISMap.getBasemap().getBaseLayers().add(defaltImageLayer);
                    }
                    if (luanImageLayer != null && luanImageLayer.isVisible()) {
                        luanImageLayer.setVisible(false);
                    }
                } else {
                    if (luanImageLayer != null) {
                        luanImageLayer.setVisible(true);
                    } else {
                        luanImageLayer = new ArcGISTiledLayer(HttpUrl.getLuanImageServer());
                        luanImageLayer.setVisible(true);
                        arcGISMap.getBasemap().getBaseLayers().add(luanImageLayer);
                    }
                    if (defaltImageLayer != null && defaltImageLayer.isVisible()) {
                        defaltImageLayer.setVisible(false);
                    }
                }
            }
        });
    }


    /**
     * 点击弹出气泡的监听
     */
    private void initCalloutListener() {
        // add listener to handle screen taps
        map.setOnTouchListener(new DefaultMapViewOnTouchListener(getActivity(), map) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                identifyGraphic(motionEvent);
                return true;
            }
        });
    }

    /**
     * Identifies the Graphic at the tapped point.
     *
     * @param motionEvent containing a tapped screen point
     */
    private void identifyGraphic(MotionEvent motionEvent) {
        // get the screen point
        android.graphics.Point screenPoint = new android.graphics.Point(Math.round(motionEvent.getX()),
                Math.round(motionEvent.getY()));
        Point point = map.screenToLocation(screenPoint);
        // convert to WGS84 for lat/lon format
        Point wgs84Point = (Point) GeometryEngine.project(point, wgs84);//屏幕坐标转化为经纬度
        // from the graphics overlay, get graphics near the tapped location
        final ListenableFuture<IdentifyGraphicsOverlayResult> identifyResultsFuture = map
                .identifyGraphicsOverlayAsync(graphicsOverlay, screenPoint, 10, false);
        identifyResultsFuture.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    IdentifyGraphicsOverlayResult identifyGraphicsOverlayResult = identifyResultsFuture.get();
                    List<Graphic> graphics = identifyGraphicsOverlayResult.getGraphics();
                    // if a graphic has been identified
                    if (graphicsOverlay.getGraphics().size() > 0) {
                        for (int i = 0; i < graphicsOverlay.getGraphics().size(); i++) {
                            Point point1 = (Point) graphicsOverlay.getGraphics().get(i).getGeometry();
                            double x1 = point1.getX();
                            double y1 = point1.getY();
                            if (mCallout != null && mCallout.isShowing()) {
                                mCallout.dismiss();
                            }
                            if (Math.sqrt((wgs84Point.getX() - x1) * (wgs84Point.getX() - x1) + (wgs84Point.getY() - y1) * (wgs84Point.getY() - y1)) < 0.05) {
                                showCallout(point, objects.get(i));
                                break;
                            }
                        }
                    } else {
                        // if no graphic identified
                        mCallout.dismiss();
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    /**
     * arcgis根据两个点经纬度，计算两点之间的距离
     *
     * @return
     */
    private double jisuanLong(Point p1, Point p2) {
        double a = Rad(p1.getX()) - Rad(p2.getX());
        double b = Rad(p1.getY()) - Rad(p2.getY());
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(Rad(p1.getY())) * Math.cos(Rad(p2.getY())) * Math.pow(Math.sin(b / 2), 2)));
        s = s * 6378137.0;//WGS1984坐标系： 6378137.0
        s = Math.round(s * 10000) / 10000;
        return s;
    }


    private static double Rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 气泡弹出
     * Shows the Graphic's attributes as a Callout.
     */
    private void showCallout(final Point point, Object object) {
        View view = View.inflate(getActivity(), R.layout.layout_callout, null);
        TextView tv1 = (TextView) view.findViewById(R.id.tv1);
        TextView tv2 = (TextView) view.findViewById(R.id.tv2);
        TextView tv3 = (TextView) view.findViewById(R.id.tv3);
        TextView tv4 = (TextView) view.findViewById(R.id.tv4);
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView group = (TextView) view.findViewById(R.id.group);
        TextView user = (TextView) view.findViewById(R.id.user);
        TextView phone = (TextView) view.findViewById(R.id.phone);
        TextView details = (TextView) view.findViewById(R.id.details);
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (object instanceof WaterRain) {
                    WaterRain waterRain = (WaterRain) object;
                    SiteDetailsActivity.startAction(getActivity(), waterRain);
                } else if (object instanceof Reservoir) {
                    Reservoir reservoir = (Reservoir) object;
                    SiteDetailsActivity.startAction(getActivity(), reservoir);
                } else if (object instanceof WaterQuality) {
                    WaterQuality waterQuality = (WaterQuality) object;
                    SiteDetailsActivity.startAction(getActivity(), waterQuality);
                } else if (object instanceof RiverCourse) {
                    RiverCourse riverCourse = (RiverCourse) object;
                    SiteDetailsActivity.startAction(getActivity(), riverCourse);
                } else if (object instanceof TakeWater) {
                    TakeWater takeWater = (TakeWater) object;
                    SiteDetailsActivity.startAction(getActivity(), takeWater);
                }
            }
        });
        // set the text of the Callout to graphic's attributes
        if (object instanceof WaterRain) {
            WaterRain waterRain = (WaterRain) object;
            name.setText(waterRain.getSTNM());
            group.setText(waterRain.getRVNM());
            user.setText(waterRain.getADNM());
        } else if (object instanceof Reservoir) {
            Reservoir reservoir = (Reservoir) object;
            name.setText(reservoir.getSTNM());
            group.setText(reservoir.getRVNM());
            user.setText(reservoir.getADNM());
        } else if (object instanceof WaterQuality) {
            WaterQuality waterQuality = (WaterQuality) object;
            name.setText(waterQuality.getSTNM());
            initLevelCode(group, waterQuality);
            user.setText(waterQuality.getPEOPLE());
        } else if (object instanceof RiverCourse) {
            RiverCourse riverCourse = (RiverCourse) object;
            name.setText(riverCourse.getSTNM());
            group.setText(riverCourse.getRVNM());
            user.setText(riverCourse.getADNM());
        } else if (object instanceof TakeWater) {
            TakeWater takeWater = (TakeWater) object;
            tv1.setText("取水口名称：");
            tv2.setText("瞬时流量：");
            tv3.setText("累计取水量：");
            view.findViewById(R.id.layout).setVisibility(View.VISIBLE);
            tv4.setText("采集时间：");
            name.setText(takeWater.getSWFNM());
            group.setText(takeWater.getQ() + "m³");
            user.setText(takeWater.getSUMQ() + "m³/s");
            phone.setText(takeWater.getMNTM());
        }
        // get callout, set content and show
        mCallout = map.getCallout();
        mCallout.setLocation(point);
        mCallout.setContent(view);
        mCallout.show();
        mCallout.setPassTouchEventsToMapView(true);
    }

    private void initLevelCode(TextView group, WaterQuality waterQuality) {
        if (StringUtils.hasLength(waterQuality.getLEVEL_CODE())) {

            if (waterQuality.getLEVEL_CODE().equals("1")) {
                group.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), R.drawable.ic_i), null, null, null);
            } else if (waterQuality.getLEVEL_CODE().equals("2")) {
                group.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), R.drawable.ic_ii), null, null, null);
            } else if (waterQuality.getLEVEL_CODE().equals("3")) {
                group.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), R.drawable.ic_iii), null, null, null);
            } else if (waterQuality.getLEVEL_CODE().equals("4")) {
                group.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), R.drawable.ic_iv), null, null, null);
            } else if (waterQuality.getLEVEL_CODE().equals("5")) {
                group.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), R.drawable.ic_v), null, null, null);
            }

        } else {
            group.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }

    private void initData() {
        SpringViewHandler handler = new SpringViewHandler(getActivity());
        Map<String, Object> params = new HashMap<>();
        handler.request(params, new GetMapClassifyTask(getActivity()));
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                List<MapClassify> list = (List<MapClassify>) result.get(Key.RESULT);
                for (MapClassify mapClassify : list) {
                    MapClassifyItem item = new MapClassifyItem(mapClassify, null);
                    mAdapter.addItem(item);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {

            }
        });
    }

    private String url_cun;//村屯url

    /**
     * 初始化刚进来时的默认行政区划图层
     */
    private void initAdcdLayers() {
        SpringViewHandler handler = new SpringViewHandler(getActivity());
        Map<String, Object> params = new HashMap<>();
        handler.request(params, new GetAdcdLayerListTask(getActivity()));
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                List<AdcdLayerInfo> list = (List<AdcdLayerInfo>) result.get(Key.RESULT);

                if (StringUtils.isNotNullList(list)) {
                    for (AdcdLayerInfo info : list) {
                        if (StringUtils.hasLength(info.getNT())) {
                            url_cun = info.getURL();
//                            ArcGISTiledLayer tiledLayer = new ArcGISTiledLayer(info.getURL());
//                            arcGISMap.getBasemap().getBaseLayers().add(tiledLayer);
//                            map.setMap(arcGISMap);
                        } else {
                            layerList.add(new FeatureLayer(new ServiceFeatureTable(info.getURL() + "/" + info.getTYPE())));
                        }
                    }
                    //center for initial viewpoint
                    // 这个Point是按照经纬度模式 跟osmdroid定位刚好反过来
                    //patialReference.create(4326) 参考坐标系   4326 是WGS84 的坐标
//                    if (StringUtils.hasLength(list.get(0).getLTTD()) && StringUtils.hasLength(list.get(0).getLGTD())) {
//                        Point center = new Point(Double.parseDouble(list.get(0).getLGTD()), Double.parseDouble(list.get(0).getLTTD())
//                                , SpatialReference.create(4326));
                    // create the Waterloo location point
                    // set the map views's viewpoint centered on Waterloo and scaled
//                        map.setViewpointCenterAsync(center, 7000);
                    //set initial viewpoint
                    //9000 是缩放比例   这缩放比例参数也是独树一帜啊
//                        arcGISMap.setInitialViewpoint(new Viewpoint(center, 3000));
//                        arcGISMap = new ArcGISMap(Basemap.Type.IMAGERY,  Double.parseDouble(list.get(0).getLTTD()), Double.parseDouble(list.get(0).getLGTD()), 8);
//                        map.setMap(arcGISMap);
//                    }


//                    AdcdLayerInfo i = list.get(0);
//                    if (StringUtils.hasLength(i.getLGTD()) && StringUtils.hasLength(i.getLTTD())) {
//                        Point point = new Point(Double.parseDouble(i.getLGTD()), Double.parseDouble(i.getLTTD()));
//                        arcGISMap = new ArcGISMap(Basemap.Type.IMAGERY, Double.parseDouble(i.getLTTD()), Double.parseDouble(i.getLGTD()), 8);
//                    }
//                    map.setMap(arcGISMap);
                }

            }

            @Override
            public void OnPostFail(Map<String, Object> result) {

            }
        });

    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initArcgisLayers();
        graphicsOverlay = addGraphicsOverlay(map);
        initListeners();
        initCalloutListener();
//        initMap(savedInstanceState);
    }


    private boolean isAdded;//标识是否已经添加过村屯图层
    ArcGISTiledLayer cunLayer = null;

    /*****
     * 初始化图层
     */
    private void initArcgisLayers() {
//        arcGISMap = new ArcGISMap(Basemap.Type.IMAGERY, 31.847976, 116.305890, 8);
        arcGISMap = new ArcGISMap();
        layerList = arcGISMap.getOperationalLayers();
//        String[] strs = getResources().getStringArray(R.array.layer_types);
//        for (String str : strs) {
//            layerList.add(new FeatureLayer(new ServiceFeatureTable(HttpUrl.gaetLayerUrl(str))));
//        }

        if (btn2.isChecked()) {
            luanImageLayer = new ArcGISTiledLayer(HttpUrl.getLuanImageServer());
            arcGISMap.getBasemap().getBaseLayers().add(luanImageLayer);
            defaltImageLayer = null;
            map.setMap(arcGISMap);
        } else {
//            arcGISMap = new ArcGISMap(Basemap.Type.IMAGERY, 31.847976, 116.305890, 8);
            defaltImageLayer = new ArcGISTiledLayer(HttpUrl.getEarthServer());
            arcGISMap.getBasemap().getBaseLayers().add(defaltImageLayer);
            luanImageLayer = null;
            map.setMap(arcGISMap);
            map.setViewpoint(new Viewpoint(31.847976,116.305890,  2417200));
        }

        map.addMapRotationChangedListener(new MapRotationChangedListener() {
            @Override
            public void mapRotationChanged(MapRotationChangedEvent mapRotationChangedEvent) {
                startIvCompass((float) map.getMapRotation());
            }
        });
        map.addMapScaleChangedListener(new MapScaleChangedListener() {
            @Override
            public void mapScaleChanged(MapScaleChangedEvent mapScaleChangedEvent) {

                if (Math.ceil(mapScaleChangedEvent.getSource().getMapScale()) <= 30000.0) {//向上取整
                    if (cunLayer == null) {
                        if (StringUtils.hasLength(url_cun)) {
                            cunLayer = new ArcGISTiledLayer(url_cun);
                            arcGISMap.getBasemap().getBaseLayers().add(cunLayer);
                            map.setMap(arcGISMap);
                        }
                    } else {
                        cunLayer.setVisible(true);
                    }
                } else {
                    if (cunLayer != null) {
                        cunLayer.setVisible(false);
                    }
                }
//                IToast.toast(mapScaleChangedEvent.getSource().getMapScale() + "");
            }
        });
        initAdcdLayers();
    }

    float lastBearing = 0;
    RotateAnimation rotateAnimation;

    /***
     *设置ImageView旋转动画
     * @param bearing
     */
    private void startIvCompass(float bearing) {
        bearing = 360 - bearing;
        rotateAnimation = new RotateAnimation(lastBearing, bearing, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setFillAfter(true);

        ivCompass.startAnimation(rotateAnimation);
        lastBearing = bearing;
    }


    private GraphicsOverlay addGraphicsOverlay(com.esri.arcgisruntime.mapping.view.MapView mapView) {
        //create the graphics overlay
        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        //add the overlay to the map view
        map.getGraphicsOverlays().add(graphicsOverlay);
        return graphicsOverlay;
    }

    /*****
     * 添加或删除图层
     */
    public void addArcgisLayers(LayerAction action) {
        for (FeatureLayer layer : action.layers) {
            if (action.type == LayerAction.ADD_ACTION) {
                arcGISMap.getOperationalLayers().add(layer);
            } else if (action.type == LayerAction.REMOVE_ACTION) {
                arcGISMap.getOperationalLayers().remove(layer);
            }
        }
    }

    private void addSiteArcgisLayers(SiteLayerAction action) {
        if (action.type == SiteLayerAction.ADD_ACTION) {
            for (Graphic graphic : action.list) {
                graphicsOverlay.getGraphics().add(graphic);
            }
            objects.addAll(action.objects);
        } else if (action.type == SiteLayerAction.REMOVE_ACTION) {
            for (Graphic graphic : action.list) {
                graphicsOverlay.getGraphics().remove(graphic);
            }
            objects.removeAll(action.objects);
            if (mCallout != null && mCallout.isShowing()) {
                mCallout.dismiss();
            }
        }
    }

    @OnClick({R.id.btn_open, R.id.btn_reset})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open:
                drawerLayout.openDrawer(Gravity.END);
                break;
            case R.id.btn_reset:
                initArcgisLayers();
                break;
        }
    }

    @Override
    public void onPause() {
        if (map != null)
            map.pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        if (map != null)
            map.resume();
        super.onResume();
    }


    @Override
    public void onDestroy() {
        if (map != null)
            map.dispose();
        super.onDestroy();
    }

}