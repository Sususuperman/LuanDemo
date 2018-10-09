package com.hywy.luanhzt.treeview.bean;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.hywy.luanhzt.R;
import com.superman.treeview.bean.TreeNode;
import com.superman.treeview.bean.TreeViewBinder;

/**
 * BranchViewBinder
 *
 * @author 林zero
 * @date 2018/1/17
 */

public class BranchViewBinder extends TreeViewBinder<BranchViewBinder.ViewHolder> {

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

    @Override
    public ViewHolder creatViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public void bindViewHolder(ViewHolder holder, int position, TreeNode treeNode) {
        ((TextView) holder.findViewById(R.id.tvName)).setText(((BranchNode) treeNode.getValue()).getName());
        holder.findViewById(R.id.ivNode).setRotation(treeNode.isExpanded() ? 180 : 0);
        ((CheckBox) holder.findViewById(R.id.ivCheck)).setChecked(treeNode.isChecked());
//        holder.findViewById(R.id.llParent).setBackgroundColor(holder.itemView.getContext().getResources().getColor(treeNode.isChecked() ? R.color.gray_light : R.color.white));
    }

    class ViewHolder extends TreeViewBinder.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
