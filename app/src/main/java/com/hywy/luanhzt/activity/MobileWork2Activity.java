package com.hywy.luanhzt.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.RequiresPermission;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.amap.api.maps.model.PolylineOptions;
import com.cs.common.base.BaseActivity;
import com.cs.common.handler.BaseHandler_;
import com.cs.common.handler.SpringViewHandler;
import com.cs.common.listener.OnPostListenter;
import com.cs.common.utils.DateUtils;
import com.cs.common.utils.DialogTools;
import com.cs.common.utils.IToast;
import com.cs.common.utils.PhoneUtil;
import com.cs.common.utils.StringUtils;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.location.LocationDataSource;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.adapter.PlanListAdapter;
import com.hywy.luanhzt.entity.Plan;
import com.hywy.luanhzt.key.Key;
import com.hywy.luanhzt.task.GetPlanListTask;
import com.hywy.luanhzt.task.PostCreatePlanTask;
import com.hywy.luanhzt.utils.SystemUtils;
import com.hywy.luanhzt.view.dialog.dialogplus.DialogPlus;
import com.hywy.luanhzt.view.dialog.dialogplus.ListHolder;
import com.hywy.luanhzt.view.dialog.dialogplus.OnItemClickListener;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.OnClick;

public class MobileWork2Activity extends BaseActivity implements OnItemClickListener {
    @Bind(R.id.mapView)
    MapView mMapView;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.layout_search)
    LinearLayout layoutSearch;
    @Bind(R.id.tv_plan)
    TextView tvPlan;

    @Bind(R.id.feedback)
    ImageView ivFeed;
    @Bind(R.id.iv_start)
    ImageView ivStart;
    @Bind(R.id.iv_end)
    ImageView ivEnd;
    @Bind(R.id.tv_time)
    Chronometer tvTime;
    @Bind(R.id.tv_distance)
    TextView tvDistance;

    @Bind(R.id.river_name)
    TextView riverName;
    @Bind(R.id.user_name)
    TextView userName;
    @Bind(R.id.user_phone)
    ImageView userPhone;

    @Bind(R.id.radio_layout)
    RadioGroup radio_layout;

    private boolean isFirstLatLng;//是否是第一次定位

    private List<Plan> list;
    private PlanListAdapter adapter;
    private Plan plan;
    private PolylineOptions mPolyoptions, tracePolytion;
    private int mDistance = 0;//轨迹长度

    private LocationDisplay mLocationDisplay;
    private Point oldTrailPoint;
    private PointCollection mPoints;
    private com.esri.arcgisruntime.geometry.Polyline mPolyline;

    private long mRecordTime;//用于纠正 秒表的时间显示时间戳


    private JSONArray array;//用于存放巡查路线坐标信息
    private boolean isStart;//用于标识是否开始巡查

    private long logId;//日志id 点击开始巡查的时候生成，用于关联问题和日志
    private Point newTrailPoint;
    private boolean isRelocate;

    public static void startAction(Context context) {
        Intent intent = new Intent(context, MobileWork2Activity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_work2);
        init();
        initMap();
        initpolyline();
        initData();
        initListeners();
    }

    private void init() {
        array = new JSONArray();
        initClock();
    }

    private void initData() {
        SpringViewHandler handler = new SpringViewHandler(this);
        Map<String, Object> params = new HashMap<>();
        handler.request(params, new GetPlanListTask(this));
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                list = (List<Plan>) result.get(Key.RESULT);
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {

            }
        });
    }

    /**
     * 初始化地图
     */
    private void initMap() {
        isFirstLatLng = true;
        mPoints = new PointCollection(SpatialReferences.getWgs84());//84坐标系
        // Get the MapView from layout and set a map with the BasemapType Imagery
        mMapView = (MapView) findViewById(R.id.mapView);
//        ArcGISMap mMap = new ArcGISMap(Basemap.createImagery());
        ArcGISTiledLayer tiledLayer = new ArcGISTiledLayer(HttpUrl.getLuanImageServer());
        ArcGISTiledLayer tiledLayer2 = new ArcGISTiledLayer("http://218.22.195.54:7011/arcgis/rest/services/liuanct/MapServer");
//        Basemap basemap = new Basemap(tiledLayer);
//        ArcGISMap mMap = new ArcGISMap(basemap);
        ArcGISMap  mMap = new ArcGISMap();
        mMap.getBasemap().getBaseLayers().add(tiledLayer);
        mMap.getBasemap().getBaseLayers().add(tiledLayer2);
        mMapView.setMap(mMap);
//
        // get the MapView's LocationDisplay
        mLocationDisplay = mMapView.getLocationDisplay();

        addDataSourceStatusChangedListener();
        addLocationChangedListener();

    }

    /***
     * 初始化绘制线的对象类
     * tracePolytion：纠偏对象类
     * */
    private void initpolyline() {
        mPolyoptions = new PolylineOptions();
        mPolyoptions.width(10f);
        mPolyoptions.color(Color.RED);
        tracePolytion = new PolylineOptions();
        tracePolytion.width(40);
        tracePolytion.color(Color.GREEN);
    }

    private void initListeners() {
        RequestPermmisons();

        radio_layout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (radioGroup.getCheckedRadioButtonId() == R.id.btn1) {
//                    mMapView.setMap(new ArcGISMap(Basemap.createImageryWithLabels()));
                } else {
//                    mMapView.setMap(new ArcGISMap(Basemap.createImageryWithLabels()));
                }
            }
        });

    }

    private void RequestPermmisons() {
        Acp.getInstance(this).request(new AcpOptions.Builder()
                        .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
                        .build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {
//                        addDataSourceStatusChangedListener();
                        mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
                        if (!mLocationDisplay.isStarted())
                            mLocationDisplay.startAsync();
                    }

                    @Override
                    public void onDenied(List<String> permissions) {

                    }
                });
    }

    /***
     * 添加数据状态变化监听，arcgis会自动定位
     */
    private void addDataSourceStatusChangedListener() {
        // Listen to changes in the status of the location data source.
        mLocationDisplay.addDataSourceStatusChangedListener(new LocationDisplay.DataSourceStatusChangedListener() {
            @Override
            public void onStatusChanged(LocationDisplay.DataSourceStatusChangedEvent dataSourceStatusChangedEvent) {
                Point p = dataSourceStatusChangedEvent.getSource().getLocation().getPosition();
                // If LocationDisplay started OK, then continue.
                if (dataSourceStatusChangedEvent.isStarted()) {
                    return;
                }
                // No error is reported, then continue.
                if (dataSourceStatusChangedEvent.getError() == null)
                    return;

            }
        });
//
//        mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
//        if (!mLocationDisplay.isStarted())
//            mLocationDisplay.startAsync();
    }

    /**
     * 添加定位位置变化监听
     */
    private void addLocationChangedListener() {
        mLocationDisplay.addLocationChangedListener(new LocationDisplay.LocationChangedListener() {
            @Override
            public void onLocationChanged(LocationDisplay.LocationChangedEvent locationChangedEvent) {
                LocationDataSource.Location location = locationChangedEvent.getLocation();
                newTrailPoint = location.getPosition();//GPS坐标`
                if (newTrailPoint != null && isFirstLatLng) {
//                    ArcGISMap mMap = new ArcGISMap(Basemap.Type.IMAGERY, newTrailPoint.getY(), newTrailPoint.getX(), 15);
//                    ArcGISTiledLayer tiledLayer = new ArcGISTiledLayer(HttpUrl.getLuanImageServer());
//                    Basemap basemap = new Basemap(tiledLayer);
//                    ArcGISMap mMap = new ArcGISMap(basemap);
////                    Point p = new Point(newTrailPoint.getY(), newTrailPoint.getX());
////                    mMap.setInitialViewpoint(new Viewpoint(p, 3000));
//                    mMapView.setMap(mMap);
                }

                if (isRelocate) {
                    initListeners();
                    isRelocate = false;
                }

                if (newTrailPoint != null)
                    startXuncha(newTrailPoint);
            }
        });
    }

    /***开始巡查***************************************/
    private void startXuncha(Point newTrailPoint) {
        if (isFirstLatLng) {
            //记录第一次的定位信息
            oldTrailPoint = newTrailPoint;
//            mPoints.add(newTrailPoint);
            Log.i("sss", "points" + mPoints.toString());
            isFirstLatLng = false;
//            mMapView.getMap().setInitialViewpoint(new Viewpoint(newTrailPoint, 9000));
        }

        //位置有变化
        if (!oldTrailPoint.equals(newTrailPoint)) {
//            mPoints.add(newTrailPoint);
//            mDistance += createDistance(oldTrailPoint, newTrailPoint);
//            tvDistance.setText(mDistance + "米");
////                    IToast.toast("" + mDistance);
//            Log.i("sss", "points" + mPoints.toString());
//            mMapView.setViewpointCenterAsync(newTrailPoint);

            if (isStart) {
                mDistance += createDistance(oldTrailPoint, newTrailPoint);
                tvDistance.setText(mDistance + "米");
//                    IToast.toast("" + mDistance);
                Log.i("sss", "points" + mPoints.toString());
                mMapView.setViewpointCenterAsync(newTrailPoint);
                mPoints.add(newTrailPoint);
                drawLine();
                JSONObject obj = new JSONObject();
                try {
                    obj.put("PATROL_LOGN", newTrailPoint.getX());
                    obj.put("PATROL_LAT", newTrailPoint.getY());
                    obj.put("TM", DateUtils.transforMillToDateInfo(System.currentTimeMillis() / 1000));
                    array.put(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            oldTrailPoint = newTrailPoint;
        }
    }

    /**
     * 绘制线
     */
    private void drawLine() {
        mPolyline = new com.esri.arcgisruntime.geometry.Polyline(mPoints);//点画线
        GraphicsOverlay overlay = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(overlay);

        overlay.getGraphics().clear();//重绘线
        SimpleLineSymbol lineSym = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 5);
        Graphic graphicTrail = new Graphic(mPolyline, lineSym);
        overlay.getGraphics().add(graphicTrail);
    }

    /***
     * 计算两坐标点之间的长度，采用84坐标系
     * @param p1
     * @param p2
     * @return
     */
    private double createDistance(Point p1, Point p2) {

        // Create a world equidistant cylindrical spatial reference for measuring planar distance.
        SpatialReference equidistantSpatialRef = SpatialReference.create(54002);
// Project the points from geographic to the projected coordinate system.
        Point edinburghProjected = (Point) GeometryEngine.project(p1, equidistantSpatialRef);
        Point darEsSalaamProjected = (Point) GeometryEngine.project(p2, equidistantSpatialRef);


        double d = GeometryEngine.distanceBetween(edinburghProjected, darEsSalaamProjected);
        return d;
    }


    @OnClick({R.id.iv_back, R.id.feedback, R.id.iv_start, R.id.iv_end, R.id.layout_search, R.id.btn_reset})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.feedback://问题上报
                if (logId != 0) {
                    ProblemReportActivity.startAction(this, logId, newTrailPoint.getY() + "", newTrailPoint.getX() + "");
                } else
                    ProblemReportActivity.startAction(this);
                break;
            case R.id.btn_reset://复位
                initListeners();
                break;
            case R.id.iv_start://开始巡查
                if (tvPlan.getTag() != null) {
                    findViewById(R.id.info_layout).setVisibility(View.VISIBLE);
                }
                if (!isStart) {
                    if (mLocationDisplay.isStarted()) {
//                        addLocationChangedListener();
                        startClock();//开始计时
                        //启动定时器，每30秒请求一次接口，实在是想不通为什么这样做
                        startTimer();
                        ivStart.setImageResource(R.drawable.icon_circle_pause);
                        isStart = true;
                        if (logId == 0) {
                            logId = System.currentTimeMillis();
                        }
                    } else {
                        //打开定位
                        RequestPermmisons();
                    }
                } else {
                    removeLocationChangedListener();
                    stopClock();//停止计时
                    ivStart.setImageResource(R.drawable.icon_circle_start);
                    isStart = false;
                }
//                }
//                else {
//                    IToast.toast("请选择巡查计划");
//                }
                break;
            case R.id.iv_end://结束巡查
                if (isStart) {
                    tvTime.stop();
                    if (tvTime.getBase() != 0) {
                        finish();
                        CreateLogActivity.startAction(this, plan, array, logId);
                    }
                }
                break;
            case R.id.layout_search://选择巡查计划
                if (StringUtils.isNotNullList(list)) {
                    showRadioDialog(list);
                }
                break;
            case R.id.user_phone://拨打电话
                if (plan != null && StringUtils.hasLength(plan.getPHONE())) {
                    callPhone(plan.getPHONE(), this);
                }
                break;
        }
    }

    private void callPhone(final String phone, final Context context) {
        if (phone == null) return;

        DialogTools.showConfirmDialog(this, "", "确认要拨打 '" + phone + "'号码？\n", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermmisons(context, phone);
            }
        });
    }

    private void requestPermmisons(final Context context, final String phone) {
        Acp.getInstance(context).request(new AcpOptions.Builder()
                        .setPermissions(Manifest.permission.CALL_PHONE)
                        .build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {
                        SystemUtils.call(context, phone);
                    }

                    @Override
                    public void onDenied(List<String> permissions) {

                    }
                });
    }

    private void stopClock() {
        tvTime.stop();
        mRecordTime = SystemClock.elapsedRealtime();
    }

    private void startClock() {
        if (mRecordTime != 0) {
            tvTime.setBase(tvTime.getBase() + (SystemClock.elapsedRealtime() - mRecordTime));
        } else {
            tvTime.setBase(SystemClock.elapsedRealtime());
        }
        tvTime.start();
    }

    private void removeLocationChangedListener() {
        mLocationDisplay.removeLocationChangedListener(new LocationDisplay.LocationChangedListener() {
            @Override
            public void onLocationChanged(LocationDisplay.LocationChangedEvent locationChangedEvent) {

            }
        });
    }

    private void initClock() {
        //计时器开启
//        tvTime.setBase(SystemClock.elapsedRealtime());//计时器清零
        int hour = (int) ((SystemClock.elapsedRealtime() - tvTime.getBase()) / 1000 / 60);
        tvTime.setFormat("0" + String.valueOf(hour) + ":%s");
    }

    private void startTimer() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    postPlan();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    /**
     * 巡查计划上报
     */
    private void postPlan() throws JSONException {
        SpringViewHandler handler = new SpringViewHandler(this);
        handler.setBuilder(new BaseHandler_.Builder().isShowToast(false));
        Map<String, Object> params = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("PATROL_ID", System.currentTimeMillis());
        jsonObject.put("data", array);
        params.put("data", jsonObject.toString());
        handler.request(params, new PostCreatePlanTask(this));
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                IToast.toast("成功");
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {
                IToast.toast("失败");
            }
        });
    }

    /**
     * 显示单选dialog
     */
    private void showRadioDialog(List<Plan> list) {
        int marginright = PhoneUtil.dp2px(this, 20);
        int marginleft = PhoneUtil.dp2px(this, 55);
        int margintop = PhoneUtil.dp2px(this, 70);
        //弹窗显示
        adapter = new PlanListAdapter(this);
        adapter.setList(list);
        DialogPlus dialogPlus = new DialogPlus.Builder(this)
                .setContentHolder(new ListHolder())
                .setMargins(marginleft, margintop, marginright, 0)
                .setAdapter(adapter)
                .setGravity(DialogPlus.Gravity.TOP)
                .setCancelable(true)
                .setInAnimation(0)
                .setOutAnimation(0)
                .setOnItemClickListener(this)
                .create();
        View view = dialogPlus.getHolderView();
        view.setBackgroundResource(R.drawable.bg_btn_default);
        dialogPlus.show();
    }

    @Override
    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
        plan = adapter.getItem(position);

        riverName.setText(plan.getREACH_NAME());
        userName.setText(plan.getNAME());

        tvPlan.setTag(plan);
        tvPlan.setText(plan.getPATROL_PLAN_NAME());
        dialog.dismiss();
    }

    @Override
    public void onPause() {
        mMapView.pause();
        super.onPause();
    }

    @Override
    protected void onRestart() {
        initListeners();
        mMapView.resume();
        super.onRestart();
    }

    @Override
    protected void onStart() {
        initListeners();
        super.onStart();
    }

    @Override
    public void onResume() {
        initListeners();
        mMapView.resume();
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        mMapView.dispose();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isRelocate = true;
    }
}
