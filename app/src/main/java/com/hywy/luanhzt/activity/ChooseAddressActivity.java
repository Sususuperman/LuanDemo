package com.hywy.luanhzt.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.cs.common.adapter.BaseListFlexAdapter;
import com.cs.common.adapter.item.ProgressItem;
import com.cs.common.base.BaseToolbarActivity;
import com.cs.common.utils.StringUtils;
import com.hywy.amap.Locate;
import com.hywy.amap.SingleOverlay;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.adapter.item.AddressAroundItem;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import eu.davidea.flexibleadapter.FlexibleAdapter;


public class ChooseAddressActivity extends BaseToolbarActivity {
    public static final int request_address = 10032;
    private MapView mapView;
    private AMap aMap;
    private SingleOverlay mMyOverlay;//我的位置
    private SingleOverlay mPinOverlay;//大头针
    private Marker mPinMarker;//大头针对应的marker
    private boolean isMoveMap = true;//地图移动标志。地图移动会连续执行，这个标志控制只设置一次
    private View mInfoWindow;
    private String chooseAddress;
    private double chooseLat;
    private double chooseLong;
    private boolean isPostAddress = true;//是否获取中文位置信息

    private com.amap.api.services.core.PoiItem poiItem;
    @Bind(R.id.sliding_layout)
    SlidingUpPanelLayout slidingPaneLayout;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerView;
    @Bind(R.id.search)
    TextView search;
    @Bind(R.id.ic_location)
    ImageView location;

    private BaseListFlexAdapter mAdapter;
    private int current_position;
    private boolean move_refresh;//用于控制搜索回来之后数据不至于被连续覆盖。

    public static void startAction(Activity context, double lng, double lat, String address) {
        Intent intent = new Intent(context, ChooseAddressActivity.class);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        if (address != null)
            intent.putExtra("address", address);
        context.startActivityForResult(intent, request_address);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_address);
        updateTitle();
        init();
        initMap(savedInstanceState);
        initListener();
    }

    public void updateTitle() {
        setTitleBulider(new Bulider().backicon(R.drawable.ic_arrow_back_white_24dp).title("位置"));
    }

    private void init() {
        Intent intent = getIntent();
        chooseLat = intent.getDoubleExtra("lat", 0);
        chooseLong = intent.getDoubleExtra("lng", 0);
        if (intent.hasExtra("address")) {
            chooseAddress = intent.getStringExtra("address");
            isPostAddress = false;
        }

        mAdapter = new BaseListFlexAdapter(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        search.setHint(getString(R.string.search));
        search.setFocusable(false);

        move_refresh = true;
    }

    /**
     * @param position
     */
    private void setItemChecked(int position) {
        AddressAroundItem citem = (AddressAroundItem) mAdapter.getItem(position);
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            AddressAroundItem item = (AddressAroundItem) mAdapter.getItem(i);
            if (item.equals(citem)) {
                item.setChecked(true);
            } else {
                item.setChecked(false);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 初始化地图
     */
    private void initMap(Bundle arg0) {
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(arg0);// 此方法必须重写
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.getUiSettings().setZoomControlsEnabled(false);

            aMap.setOnMapTouchListener(new AMap.OnMapTouchListener() {
                @Override
                public void onTouch(MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                        move_refresh = true;
                    }
                }
            });
        }
        mMyOverlay = new SingleOverlay(this, aMap);
        mMyOverlay.setIcon(R.drawable.icon_map_current_location);
        mPinOverlay = new SingleOverlay(this, aMap);
        mPinOverlay.setIcon(R.drawable.icon_map_pin);
    }

    private void initListener() {

        //请求定位权限
        RequestPermmisons();

        mAdapter.setEndlessScrollListener(new FlexibleAdapter.EndlessScrollListener() {
            @Override
            public void onLoadMore() {
                if (!mAdapter.isNomore()) {
                    mAdapter.setClear(false);
                    mAdapter.setPage(mAdapter.getPage() + 1);
                    showAround(poiItem, mAdapter.getPage() + 1);

                } else {
                    mAdapter.onLoadMoreComplete(null);
                }
            }
        }, new ProgressItem());

        mAdapter.initializeListeners(new FlexibleAdapter.OnItemClickListener() {
            @Override
            public boolean onItemClick(int position) {
                setItemChecked(position);
                current_position = position;
                AddressAroundItem aroundItem = (AddressAroundItem) mAdapter.getItem(position);
                com.amap.api.services.core.PoiItem p = aroundItem.getData();
                showMark(p);
                move_refresh = false;
                return false;
            }
        });
        slidingPaneLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
//                int height = PhoneUtil.px2dip(ChooseAddressActivity.this, PhoneUtil.getDisplayHight(ChooseAddressActivity.this));
//                int toolHeight = PhoneUtil.px2dip(ChooseAddressActivity.this, getToolbar().getMeasuredHeight());
//                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
//                    location.scrollBy(0, PhoneUtil.dp2px(ChooseAddressActivity.this, 100));
//                } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
//                    location.scrollBy(0, -PhoneUtil.dp2px(ChooseAddressActivity.this, 100));
//                }
            }
        });
    }

    private void RequestPermmisons() {
        Acp.getInstance(this).request(new AcpOptions.Builder()
                        .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                        .build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {
                        startLocation();
                    }

                    @Override
                    public void onDenied(List<String> permissions) {

                    }
                });
    }

    @OnClick(R.id.search)
    public void search() {
        SearchAddressActivity.startAction(this);
    }

    @OnClick(R.id.ic_location)
    public void location() {
        RequestPermmisons();
    }

    private void startLocation() {
        final Locate locale = new Locate(this);
        //启动定位
        locale.start();
        locale.setOnLocationListener(new Locate.OnLocationListener() {
            @Override
            public void onReceiveLocation(AMapLocation location) {
                addUserMarker(location.getLatitude(), location.getLongitude());
                if (chooseLat == 0 || chooseLat == 0)
                    addPoiMrker(location.getLatitude(), location.getLongitude());
                LatLonPoint latLonPoint = new LatLonPoint(location.getLatitude(), location.getLongitude());
                com.amap.api.services.core.PoiItem p =
                        new com.amap.api.services.core.PoiItem("", latLonPoint, location.getAddress(), "");
                poiItem = p;
                aMap.moveCamera(CameraUpdateFactory.zoomTo(25));
                showAround(p, 1);
                move_refresh = false;
            }

            @Override
            public void onReceiveFail() {

            }
        });

        if (chooseLat != 0 && chooseLong != 0) {
            addPoiMrker(chooseLat, chooseLong);
//            hideBubbleLoading();
        }


        //地图移动事件
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                mPinMarker.setPosition(cameraPosition.target);
                if (isMoveMap) {//这里只执行一次就可以了
                    isMoveMap = false;
                }
            }

            @Override
            public void onCameraChangeFinish(final CameraPosition cameraPosition) {
                isMoveMap = true;
//                showBubbleLoading();
                hidePoi();

                jumpPoint(mPinMarker);

                //把经纬度转换成地址信息
                locale.searchAddress(cameraPosition.target.latitude, cameraPosition.target.longitude);
                locale.setOnGeoSearchLintener(new Locate.OnGeoSearchLintener() {
                    @Override
                    public void onRegeocodeSearched(RegeocodeResult arg0, int arg1) {
                        if (arg0 != null) {
                            chooseLat = cameraPosition.target.latitude;
                            chooseLong = cameraPosition.target.longitude;
                            chooseAddress = arg0.getRegeocodeAddress().getFormatAddress();
                            //                            hideBubbleLoading();
                            LatLonPoint latLonPoint = new LatLonPoint(chooseLat, chooseLong);
                            com.amap.api.services.core.PoiItem p =
                                    new com.amap.api.services.core.PoiItem("", latLonPoint, chooseAddress, "");
                            poiItem = p;
                            if (move_refresh) {
                                mAdapter.setPage(1);
                                showAround(p, mAdapter.getPage());
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * 周边搜索
     */
    private void showAround(final com.amap.api.services.core.PoiItem poiItem, final int pageNum) {
        PoiSearch.Query query = new PoiSearch.Query("", "", "");
        query.setPageSize(20);
        query.setPageNum(pageNum);
        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setBound(new PoiSearch.SearchBound(poiItem.getLatLonPoint(), 500));//设置周边搜索的中心点以及半径
        poiSearch.searchPOIAsyn();
        poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int i) {
                ArrayList<PoiItem> list = poiResult.getPois();
                List<AddressAroundItem> items = new ArrayList<>();
                if (pageNum == 1) {
                    mAdapter.clear();
                    list.add(0, poiItem);
                }
                for (int j = 0; j < list.size(); j++) {
                    com.amap.api.services.core.PoiItem poiItem = list.get(j);
                    AddressAroundItem addressAroundItem = new AddressAroundItem(poiItem);
                    //默认第一个选中,只有请求第一组数据的时候才设置为选中
                    if (j == 0) {
                        if (pageNum == 1) {
                            addressAroundItem.setChecked(true);
                            current_position = 0;
                        }
                    }
//                    if (StringUtils.hasLength(poiItem.getTitle())) {
                    items.add(addressAroundItem);
//                    }
//                    //获取POI的地址
//                    Log.i("poiitem", poiItem.getTitle() + "," + poiItem.getSnippet());
                }
                mAdapter.addItems(mAdapter.getItemCount() - 1, items);
                mAdapter.notifyDataSetChanged();
                mAdapter.setNomore(list.size() < mAdapter.getCount());
                mAdapter.onLoadMoreComplete(items);
            }

            @Override
            public void onPoiItemSearched(com.amap.api.services.core.PoiItem poiItem, int i) {

            }
        });
    }

    /**
     * 地图上显示当前用户的位置
     */
    private void addUserMarker(double lat, double lng) {
        com.hywy.amap.PoiItem item = new com.hywy.amap.PoiItem();
        item.setTitle("title");
        item.setSnippet("snippet");
        item.setLatLng(new LatLng(lat, lng));
        mMyOverlay.setPoi(item);
        mMyOverlay.addToMap();//显示我的位置
    }

    /**
     * 地图上显示大头针的位置
     */
    private void addPoiMrker(double lat, double lng) {
        com.hywy.amap.PoiItem item = new com.hywy.amap.PoiItem();
        item.setTitle("title");//title 和 snippet必须添加否则气泡不显示
        item.setSnippet("snippet");
        item.setLatLng(new LatLng(lat, lng));
        mPinOverlay.setPoi(item);//显示大头针位置
        mPinOverlay.addToMap();
        mPinMarker = mPinOverlay.getMyMarker();
        mPinMarker.setAnchor(0.5f, 1.27f);
        mPinMarker.hideInfoWindow();//去掉infowindow显示
        mPinOverlay.zoomToSpan();
        hidePoi();
    }

    private void hidePoi() {
        if (isHaveLocal()) {
            mPinOverlay.getMyMarker().setVisible(true);
//            if (!mPinMarker.isInfoWindowShown())
//                mPinMarker.showInfoWindow();
        }
    }

    /**
     * 判断是否已经定到位置
     *
     * @return
     */
    private boolean isHaveLocal() {
        return mMyOverlay.getMyMarker() != null && mMyOverlay.getMyMarker().getPosition() != null;
    }


    private AnimationDrawable animationDrawable;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            if (chooseLat != 0 && chooseLong != 0) {
                Intent intent = new Intent();
                AddressAroundItem addressAroundItem = (AddressAroundItem) mAdapter.getItem(current_position);
                com.amap.api.services.core.PoiItem poiItem = addressAroundItem.getData();
                LatLonPoint latLonPoint = poiItem.getLatLonPoint();

                intent.putExtra("lat", latLonPoint.getLatitude());
                intent.putExtra("lng", latLonPoint.getLongitude());
                if (StringUtils.hasLength(poiItem.getSnippet())) {
                    intent.putExtra("address", poiItem.getSnippet());
                } else {
                    intent.putExtra("address", poiItem.getTitle());
                }
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Snackbar.make(mapView, "无效的地址", Snackbar.LENGTH_SHORT).show();
            }

        } else if (item.getItemId() == R.id.action_search) {
            SearchAddressActivity.startAction(this);
        }
        return super.onOptionsItemSelected(item);
    }


    SearchView searchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchandsave, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        return super.onCreateOptionsMenu(menu);
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
    public void onDestroy() {
        setMapviewDestory();
        super.onDestroy();
    }

    private void setMapviewDestory() {
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isCancel(resultCode)) {
            if (data != null) {
                poiItem = (com.amap.api.services.core.PoiItem) data.getExtras().get("poiitem");
                showAround(poiItem, 1);
                showMark(poiItem);
                move_refresh = false;
            }
        }
    }

    private void showMark(com.amap.api.services.core.PoiItem poiItem) {
        LatLng curLatlng = new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude());
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(curLatlng));
    }

    /**
     * marker点击时跳动一下
     */
    public void jumpPoint(final Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = aMap.getProjection();
        final LatLng markerLatlng = marker.getPosition();
        Point markerPoint = proj.toScreenLocation(markerLatlng);
        markerPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(markerPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * markerLatlng.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * markerLatlng.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    /**
     * 判断是否到达底部或者顶部，1底部，-1顶部
     *
     * @param direction
     * @return
     */
    private boolean canScrollVertically(int direction) {
        final int offset = recyclerView.computeVerticalScrollOffset();
        final int range = recyclerView.computeVerticalScrollRange() - recyclerView.computeVerticalScrollExtent();
        if (range == 0) return false;
        if (direction < 0) {
            return offset > 0;
        } else {
            return offset < range - 1;
        }
    }
}
