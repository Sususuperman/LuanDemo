package com.hywy.luanhzt.entity.action;


import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.hywy.luanhzt.entity.MapClassify;

import java.util.List;

public class SiteLayerAction {
    public static final int REMOVE_ACTION = 0;//移除
    public static final int ADD_ACTION = 1;//创建
    public MapClassify mapClassify;
    public int type;
    public List<Graphic> list;
    public List<Object> objects;

    public SiteLayerAction(int type, MapClassify mapClassify, List<Graphic> list, List<Object> objects) {
        this.type = type;
        this.mapClassify = mapClassify;
        this.list = list;
        this.objects = objects;
    }
}
