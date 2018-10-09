package com.hywy.luanhzt.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;
import com.cs.common.adapter.BaseListFlexAdapter;
import com.cs.common.adapter.layoutmanager.FullyLinearLayoutManager;
import com.cs.common.base.BaseToolbarActivity;
import com.cs.common.handler.WaitDialog;
import com.cs.common.utils.StringUtils;
import com.cs.common.view.ImagePagerActivity;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.PolylineBuilder;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.hywy.amap.Locate;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.adapter.ImagesAdapter;
import com.hywy.luanhzt.adapter.item.OptionsItem;
import com.hywy.luanhzt.entity.AttachMent;
import com.hywy.luanhzt.entity.Inspection;
import com.hywy.luanhzt.entity.Patrol;
import com.hywy.luanhzt.entity.PatrolRoute;
import com.hywy.luanhzt.entity.Plan;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 巡查详情
 *
 * @author Superman
 */
public class XcDetailsActivity extends BaseToolbarActivity implements AdapterView.OnItemClickListener {
    @Bind(R.id.arcgis_map)
    com.esri.arcgisruntime.mapping.view.MapView arcMapView;

    @Bind(R.id.mapView)
    MapView mapView;
    @Bind(R.id.image_grid)
    GridView imageGrid;

    @Bind(R.id.user_name)
    TextView userName;
    //    @Bind(R.id.area)
//    TextView adDress;
//    @Bind(R.id.xuncha_plan)
//    TextView xunchaPlan;
    @Bind(R.id.xuncha_river)
    TextView xunchaRiver;
    @Bind(R.id.other_pro)
    TextView otherPro;
    @Bind(R.id.handle_sitation)
    TextView handleSit;
    @Bind(R.id.start_time)
    TextView startTime;
    @Bind(R.id.end_time)
    TextView endTime;
//    @Bind(R.id.table_layout)
//    TableLayout tableLayout;

    private AMap aMap;
    private Inspection inspection;
    private WaitDialog waitDialog;
    private ImagesAdapter imageAdapter;

    @Bind(R.id.recyclerview)
    RecyclerView recyclerView;
    private BaseListFlexAdapter mAdapter;

    public static void startAction(Activity activity, Inspection inspection) {
        Intent intent = new Intent(activity, XcDetailsActivity.class);
        intent.putExtra("inspection", inspection);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xc_details);
        init();
//        initMap(savedInstanceState);
        initPolyline();
    }


    private void init() {
        inspection = getIntent().getParcelableExtra("inspection");
        if (StringUtils.hasLength(inspection.getPATROL_LOG_NAME())) {
            setTitleBulider(new Bulider().title(inspection.getPATROL_LOG_NAME()).backicon(R.drawable.ic_arrow_back_white_24dp));
        } else
            setTitleBulider(new Bulider().title("巡查日志").backicon(R.drawable.ic_arrow_back_white_24dp));

        imageAdapter = new ImagesAdapter(this);
        imageGrid.setAdapter(imageAdapter);
        List<AttachMent> attachMents = inspection.getPATROL_NOTE();
        if (attachMents != null && attachMents.size() > 0) {
            imageAdapter.setList(attachMents);
        }
        imageGrid.setOnItemClickListener(this);
        mAdapter = new BaseListFlexAdapter(this);
        recyclerView.setLayoutManager(new FullyLinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);


        initText();
        initPatrols();

        ArcGISMap arcGISMap = new ArcGISMap(Basemap.createImagery());
        arcMapView.setMap(arcGISMap);
    }


    private void initText() {
        setTextView(userName, inspection.getNAME());
//        setTextView(xunchaPlan, inspection.getPATROL_PLAN_NAME());
        setTextView(xunchaRiver, inspection.getREACH_NAME());
//        setTextView(adDress, inspection.getADNM());
        setTextView(otherPro, inspection.getOTHER_SITUATION());
        setTextView(handleSit, inspection.getDISPOSE_SITUATION());
        setTextView(startTime, inspection.getSTARTTIME());
        setTextView(endTime, inspection.getENDTIME());
    }


    private void initPatrols() {
        if (StringUtils.isNotNullList(inspection.getPATROL_SITUATION())) {
            List<Patrol> patrols = inspection.getPATROL_SITUATION();
            for (Patrol patrol : patrols) {
                Plan.OPTIONSBean bean = new Plan.OPTIONSBean();
                bean.setOPTIONS_ID(patrol.getOPTIONS_ID());
                bean.setOPTIONS_NAME(patrol.getOPTIONS_NAME());
                OptionsItem item = new OptionsItem(bean,false);
                mAdapter.addItem(item);
            }
        }
        mAdapter.notifyDataSetChanged();
    }


    private void setTextView(TextView tv, String str) {
        if (StringUtils.hasLength(str)) {
            tv.setText(str);
        }
    }

    /**
     * 初始化地图
     */
    private void initMap(Bundle arg0) {
        mapView.onCreate(arg0);// 此方法必须重写
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        startLocation(new Locate.OnLocationListener() {

            @Override
            public void onReceiveLocation(AMapLocation aMapLocation) {
                if (waitDialog != null)
                    waitDialog.dismiss();
            }

            @Override
            public void onReceiveFail() {
                if (waitDialog != null)
                    waitDialog.dismiss();
            }
        });

    }

    private void startLocation(Locate.OnLocationListener listener) {
        if (waitDialog != null)
            waitDialog.show();
        //启动定位
        Locate locale = new Locate(this);
        locale.start();
        locale.setOnLocationListener(listener);
    }

    /**
     * 初始化地图上的线
     *****/
    private void initPolyline() {

        List<LatLng> latLngs = new ArrayList<LatLng>();
        List<PatrolRoute> planPoints = inspection.getPlanpoints();

        List<LatLng> latLngs2 = new ArrayList<>();
        List<PatrolRoute> points = inspection.getPoints();


        PointCollection mPoints2 = new PointCollection(SpatialReferences.getWgs84());//84坐标系;
        if (planPoints != null && planPoints.size() > 0) {
            for (PatrolRoute pr : planPoints) {
//                latLngs.add(new LatLng(pr.getLTTD(), pr.getLGTD()));
                mPoints2.add(new Point(pr.getLGTD(), pr.getLTTD()));
            }
        }

        /****************************************/
        PointCollection mPoints = new PointCollection(SpatialReferences.getWgs84());//84坐标系;
        if (points != null && points.size() > 0) {
            for (PatrolRoute pr : points) {
                latLngs2.add(new LatLng(pr.getLTTD(), pr.getLGTD()));
                //arcgis地图中x是longtitude  y是latitude
                mPoints.add(new Point(pr.getLGTD(), pr.getLTTD()));
            }
        }
        //实际巡查路线
        if (mPoints.size() > 0) {
//            aMap.addPolyline(new PolylineOptions().
//                    addAll(latLngs2).width(10).color(ContextCompat.getColor(this, R.color.material_color_green_300)));

            Point p = mPoints.get(0);
            ArcGISMap arcGISMap = new ArcGISMap(Basemap.Type.IMAGERY, p.getY(), p.getX(), 17);
            arcMapView.setMap(arcGISMap);
            drawArcgisLine(mPoints, R.color.material_color_green_300);

            //如果列表坐标存在则将地图显示在该位置附近 计划巡查路线
            if (mPoints2.size() > 0) {
//            LatLng latLng = latLngs.get(0);//构造一个位置
//            //设置中心点和缩放比例
//            aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
//            aMap.addPolyline(new PolylineOptions().
//                    addAll(latLngs).width(10).color(ContextCompat.getColor(this, R.color.red_notice)));
                drawArcgisLine(mPoints2, R.color.red_notice);
            }
        }


    }

    /**
     * 画arcgis图线
     *
     * @param mPoints
     */
    private void drawArcgisLine(PointCollection mPoints, int color) {
        com.esri.arcgisruntime.geometry.Polyline mPolyline = new com.esri.arcgisruntime.geometry.Polyline(mPoints);//点画线
        GraphicsOverlay overlay = new GraphicsOverlay();
        arcMapView.getGraphicsOverlays().add(overlay);

        SimpleLineSymbol lineSym = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, ContextCompat.getColor(this, color), 5);
        Graphic graphicTrail = new Graphic(mPolyline, lineSym);
        overlay.getGraphics().add(graphicTrail);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        AttachMent attachMent = imageAdapter.getItem(i);
        ImagePagerActivity.startShowImages(view.getContext(), imageAdapter.getImagePaths(), i);
    }
}
