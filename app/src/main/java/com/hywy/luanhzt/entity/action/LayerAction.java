package com.hywy.luanhzt.entity.action;


import com.esri.arcgisruntime.layers.FeatureLayer;

import java.util.List;

public class LayerAction {
    public static final int REMOVE_ACTION = 0;//移除
    public static final int ADD_ACTION = 1;//创建
    public List<FeatureLayer> layers;
    public int type;
    public int id;

    public LayerAction(int type, List<FeatureLayer> layers) {
        this.type = type;
        this.layers = layers;
    }
}
