package com.hywy.luanhzt.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.route.DistanceResult;
import com.amap.api.services.route.DistanceSearch;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;
import com.cs.common.base.BaseActivity;
import com.cs.common.handler.BaseHandler_;
import com.cs.common.handler.SpringViewHandler;
import com.cs.common.handler.WaitDialog;
import com.cs.common.listener.OnPostListenter;
import com.cs.common.utils.DateUtils;
import com.cs.common.utils.IToast;
import com.cs.common.utils.PhoneUtil;
import com.cs.common.utils.StringUtils;
import com.hywy.amap.Locate;
import com.hywy.amap.PoiItem;
import com.hywy.amap.SingleOverlay;
import com.hywy.amap.Util;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.adapter.PlanListAdapter;
import com.hywy.luanhzt.entity.Plan;
import com.hywy.luanhzt.key.Key;
import com.hywy.luanhzt.task.GetPlanListTask;
import com.hywy.luanhzt.task.PostCreatePlanTask;
import com.hywy.luanhzt.view.dialog.dialogplus.DialogPlus;
import com.hywy.luanhzt.view.dialog.dialogplus.ListHolder;
import com.hywy.luanhzt.view.dialog.dialogplus.OnItemClickListener;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.OnClick;

public class MobileWorkActivity extends BaseActivity implements OnItemClickListener, TraceListener, DistanceSearch.OnDistanceSearchListener {
    @Bind(R.id.mapView)
    MapView mapView;
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

    private AMap aMap;
    private WaitDialog waitDialog;

    private SingleOverlay mMyOverlay;//我的位置

    private boolean isFirstLatLng;//是否是第一次定位
    private LatLng oldLatLng;//以前的定位点

    private Locate locale;

    private List<Plan> list;
    private PlanListAdapter adapter;
    private Plan plan;
    private PolylineOptions mPolyoptions, tracePolytion;
    private List<TraceLocation> mTracelocationlist = new ArrayList<TraceLocation>();
    private Polyline mpolyline;
    private int tracesize = 30;
    private int mDistance = 0;//轨迹长度

    private JSONArray array;//用于存放巡查路线坐标信息

    private DistanceSearch distanceSearch;
    private DistanceSearch.DistanceQuery query;

    public static void startAction(Context context) {
        Intent intent = new Intent(context, MobileWorkActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_work);
        init();
        initMap(savedInstanceState);
        initpolyline();
        initData();
        initListeners();
    }

    private void init() {
        array = new JSONArray();
    }

    private void initData() {
        SpringViewHandler handler = new SpringViewHandler(this);
        Map<String, Object> params = new HashMap<>();
        handler.request(params, new GetPlanListTask(this));
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                list = (List<Plan>) result.get(Key.RESULT);
                if (StringUtils.isNotNullList(list)) {

                }
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {

            }
        });
    }

    /**
     * 初始化地图
     */
    private void initMap(Bundle arg0) {
        isFirstLatLng = true;

        mapView.onCreate(arg0);// 此方法必须重写
        if (aMap == null) {
            aMap = mapView.getMap();
        }
//        //设置地图的放缩级别
//        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
        //地图缩放按钮,true为显示，false为隐藏
        aMap.getUiSettings().setZoomControlsEnabled(false);
//        aMap.getUiSettings().setLogoBottomMargin(-200);


        if (mMyOverlay == null)
            mMyOverlay = new SingleOverlay(this, aMap);
        mMyOverlay.setIcon(R.drawable.icon_map_current_location);
        waitDialog = new WaitDialog(this, "定位中...", false, false);

        distanceSearch = new DistanceSearch(this);

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
        //未开始巡查前只进行定位一次
        RequestPermmisons(false);

        radio_layout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (radioGroup.getCheckedRadioButtonId() == R.id.btn1) {
                    aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                } else {
                    aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                }
            }
        });

        distanceSearch.setDistanceSearchListener(this);
    }

    private void RequestPermmisons(final boolean b) {
        Acp.getInstance(this).request(new AcpOptions.Builder()
                        .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                        .build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {
                        LocationAndShowMarks(b);
                    }

                    @Override
                    public void onDenied(List<String> permissions) {

                    }
                });
    }

    //定位并显示mark
    private void LocationAndShowMarks(final boolean b) {
        startLocation(new Locate.OnLocationListener() {
            @Override
            public void onReceiveLocation(AMapLocation location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                addUserMarker(latLng);

                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                //  通过定位后获得的经纬度 为地图添加中心点 和地图比例
                if (isFirstLatLng) {
                    //记录第一次的定位信息
                    oldLatLng = latLng;
                    isFirstLatLng = false;
                }


                // 位置有变化
                if (oldLatLng != latLng) {
                    Log.e("Amap", location.getLatitude() + "," + location.getLongitude());
                    mPolyoptions.add(latLng);
                    mTracelocationlist.add(Util.parseTraceLocation(location));
                    redrawline();

                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("PATROL_LOGN", latLng.longitude);
                        obj.put("PATROL_LAT", latLng.latitude);
                        obj.put("TM", DateUtils.transforMillToDateInfo(System.currentTimeMillis() / 1000));
                        array.put(obj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    LatLonPoint start = new LatLonPoint(oldLatLng.latitude, oldLatLng.longitude);
//                    LatLonPoint dest = new LatLonPoint(location.getLatitude(), location.getLongitude());
//                    List<LatLonPoint> latLonPoints = new ArrayList<LatLonPoint>();
//                    latLonPoints.add(start);
//                    query.setOrigins(latLonPoints);
//                    query.setDestination(dest);
//                    //设置测量方式，支持直线和驾车
//                    query.setType(DistanceSearch.TYPE_DRIVING_DISTANCE);
//                    distanceSearch.calculateRouteDistanceAsyn(query);
                    if (mTracelocationlist.size() > tracesize - 1) {
                        trace();
                    }
//                    setUpMap(oldLatLng, latLng);
                    oldLatLng = latLng;
                }


                if (waitDialog != null)
                    waitDialog.dismiss();
            }

            @Override
            public void onReceiveFail() {
                if (waitDialog != null)
                    waitDialog.dismiss();
            }
        }, b);
    }

    /**
     * 实时轨迹画线
     */
    private void redrawline() {
        if (mPolyoptions.getPoints().size() > 1) {
            if (mpolyline != null) {
                mpolyline.setPoints(mPolyoptions.getPoints());
            } else {
                mpolyline = aMap.addPolyline(mPolyoptions);
            }
        }
    }

    /***
     * 轨迹纠偏
     */
    private void trace() {
        List<TraceLocation> locationList = new ArrayList<>(mTracelocationlist);
        LBSTraceClient mTraceClient = new LBSTraceClient(getApplicationContext());
        //参数1是lineID,lineID： 用于标示一条轨迹，支持多轨迹纠偏，如果多条轨迹调起纠偏接口，则lineID需不同。
        mTraceClient.queryProcessedTrace(1, locationList, LBSTraceClient.TYPE_AMAP, this);
//        TraceLocation lastlocation = mTracelocationlist.get(mTracelocationlist.size() - 1);
        mTracelocationlist.clear();
//        mTracelocationlist.add(lastlocation);
    }

    /**
     * 绘制两个坐标点之间的线段,从以前位置到现在位置
     */

    private void setUpMap(LatLng oldData, LatLng newData) {
        // 绘制一个大地曲线
        aMap.addPolyline((new PolylineOptions())
                .add(oldData, newData)
                .geodesic(true).width(10).color(ContextCompat.getColor(this, R.color.font_green)));
//        AMapUtils.calculateLineDistance();

    }


    private void startLocation(Locate.OnLocationListener listener, boolean b) {
        if (waitDialog != null)
            waitDialog.show();
        //启动定位
        locale = new Locate(this, b);
        locale.start();
        locale.setOnLocationListener(listener);
    }

    /**
     * 地图上显示当前用户的位置
     */
    private void addUserMarker(LatLng latLng) {
        mMyOverlay.removeFromMap();
        mMyOverlay.setPoi(createPoiItem(latLng));
        mMyOverlay.addToMap();//显示我的位置
        mMyOverlay.zoomToSpan();
    }

    private PoiItem createPoiItem(LatLng latLng) {
        PoiItem item = new PoiItem();
        item.setTitle("title");
        item.setSnippet("snippet");
        item.setLatLng(latLng);
        return item;
    }


    @OnClick({R.id.iv_back, R.id.feedback, R.id.iv_start, R.id.iv_end, R.id.layout_search})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.feedback://问题上报
                ProblemReportActivity.startAction(this);
                break;
            case R.id.iv_start://开始巡查
                if (tvPlan.getTag() != null) {
                    //开始连续定位
                    RequestPermmisons(true);
                    startClock();
                    //启动定时器，每30秒请求一次接口，实在是想不通为什么这样做
                    startTimer();
                    findViewById(R.id.iv_start).setBackgroundResource(R.drawable.icon_circle_pause);

                } else {
                    IToast.toast("请选择巡查计划");
                }
                break;
            case R.id.iv_end://结束巡查
                locale.stop();
                tvTime.stop();
                if (tvPlan.getTag() != null)
                    CreateLogActivity.startAction(this, plan, array,0);
                break;
            case R.id.layout_search://选择巡查计划
                if (StringUtils.isNotNullList(list)) {
                    showRadioDialog(list);
                }
                break;
        }
    }

    private void startClock() {
        //计时器开启
        tvTime.setBase(SystemClock.elapsedRealtime());//计时器清零
        int hour = (int) ((SystemClock.elapsedRealtime() - tvTime.getBase()) / 1000 / 60);
        tvTime.setFormat("0" + String.valueOf(hour) + ":%s");
        tvTime.start();// 开始计时
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
                System.out.println("run " + System.currentTimeMillis());
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

        tvPlan.setTag(plan);
        tvPlan.setText(plan.getPATROL_PLAN_NAME());
        dialog.dismiss();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        setMapviewDestory();
        super.onDestroy();
    }

    private void setMapviewDestory() {
        mapView.onDestroy();
    }


    @Override
    public void onRequestFailed(int i, String s) {

    }

    @Override
    public void onTraceProcessing(int i, int i1, List<LatLng> list) {
    }

    /**
     * 轨迹纠偏成功回调。
     *
     * @param lineID      纠偏的线路ID
     * @param linepoints  纠偏结果
     * @param distance    总距离
     * @param waitingtime 等待时间
     */
    @Override
    public void onFinished(int lineID, List<LatLng> linepoints, int distance, int waitingtime) {
        if (lineID == 1) {
            if (linepoints != null && linepoints.size() > 0) {
//                mTraceoverlay.add(linepoints);
                mDistance += distance;
//                mTraceoverlay.setDistance(mTraceoverlay.getDistance() + distance);
//                if (mlocMarker == null) {
//                    mlocMarker = mAMap.addMarker(new MarkerOptions().position(linepoints.get(linepoints.size() - 1))
//                            .icon(BitmapDescriptorFactory
//                                    .fromResource(R.drawable.point))
//                            .title("距离：" + mDistance + "米"));
//                    mlocMarker.showInfoWindow();
                Toast.makeText(this, "距离" + mDistance, Toast.LENGTH_SHORT).show();
                tvDistance.setText(mDistance + "米");
            } else {
//                    mlocMarker.setTitle("距离：" + mDistance + "米");
                Toast.makeText(this, "距离" + mDistance, Toast.LENGTH_SHORT).show();
//                    mlocMarker.setPosition(linepoints.get(linepoints.size() - 1));
//                    mlocMarker.showInfoWindow();
//                }
            }
        }
    }

    @Override
    public void onDistanceSearched(DistanceResult distanceResult, int i) {
        if (i == 1000) {//成功
//            distanceResult.getDistanceResults()
        }
    }
}
