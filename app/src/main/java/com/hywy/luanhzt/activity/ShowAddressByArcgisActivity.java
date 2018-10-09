package com.hywy.luanhzt.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Menu;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.cs.common.base.BaseToolbarActivity;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.hywy.amap.PoiItem;
import com.hywy.amap.SingleOverlay;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.entity.WaterRain;
import com.hywy.luanhzt.entity.action.SiteLayerAction;
import com.hywy.luanhzt.key.Key;

import java.util.List;

import butterknife.Bind;


public class ShowAddressByArcgisActivity extends BaseToolbarActivity {
    @Bind(R.id.mapView)
    MapView mapView;
    ArcGISMap arcGISMap;
    private double chooseLat;
    private double chooseLong;
    private GraphicsOverlay graphicsOverlay;
    public final SpatialReference wgs84 = SpatialReference.create(4326);

    public static void startAction(Activity context, double lng, double lat) {
        Intent intent = new Intent(context, ShowAddressByArcgisActivity.class);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_address_arcgis);
        updateTitle();
        init();
        initArcgisLayers();
        addSiteArcgisLayers();
        initListener();

    }

    public void updateTitle() {
        setTitleBulider(new Bulider().backicon(R.drawable.ic_arrow_back_white_24dp).title("显示地标"));
    }

    private void init() {
        Intent intent = getIntent();
        chooseLat = intent.getDoubleExtra("lat", 0);
        chooseLong = intent.getDoubleExtra("lng", 0);
    }

    /*****
     * 初始化图层
     */
    private void initArcgisLayers() {
        arcGISMap = new ArcGISMap(Basemap.Type.IMAGERY, chooseLat, chooseLong, 8);
        mapView.setMap(arcGISMap);
        graphicsOverlay = addGraphicsOverlay();
    }

    private void addSiteArcgisLayers() {
        PictureMarkerSymbol symbol = new PictureMarkerSymbol((BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.ic_map_insure_marker_checked));
        symbol.setHeight(30);
        symbol.setWidth(30);
        Point point = new Point(chooseLong, chooseLat, wgs84);
        Graphic graphic = new Graphic(point, symbol);
        graphicsOverlay.getGraphics().add(graphic);
    }
    private GraphicsOverlay addGraphicsOverlay() {
        //create the graphics overlay
        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        //add the overlay to the map view
        mapView.getGraphicsOverlays().add(graphicsOverlay);
        return graphicsOverlay;
    }

    private void initListener() {
    }


    private AnimationDrawable animationDrawable;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onPause() {
        mapView.pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mapView.resume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        mapView.dispose();
        super.onDestroy();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
