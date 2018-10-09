package com.hywy.luanhzt.treeview.bean;


import com.hywy.luanhzt.R;
import com.superman.treeview.bean.LayoutItem;

/**
 * BranchNode
 *
 * @date 2018/1/14
 */

public class BranchNode implements LayoutItem {
    private String name;
    private String id;
    public BranchNode(String name,String id) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_branch;
    }

    @Override
    public int getToggleId() {
        return R.id.ivNode;
    }

    @Override
    public int getCheckedId() {
        return R.id.ivCheck;
    }

    @Override
    public int getClickId() {
        return R.id.tvName;
    }
}
