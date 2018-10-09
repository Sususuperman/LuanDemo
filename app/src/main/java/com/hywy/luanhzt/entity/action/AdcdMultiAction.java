package com.hywy.luanhzt.entity.action;


import com.esri.arcgisruntime.layers.FeatureLayer;
import com.superman.treeview.treelist.Node;

import java.util.List;

public class AdcdMultiAction {
    public List<Node> nodes;

    public AdcdMultiAction( List<Node> layers) {
        this.nodes = layers;
    }
}
