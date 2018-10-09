package com.hywy.luanhzt.adapter.item;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;


import com.hywy.luanhzt.R;
import com.hywy.luanhzt.activity.RiverDetailsActivity;
import com.hywy.luanhzt.adapter.RiverGridAdapter;
import com.hywy.luanhzt.entity.River;
import com.hywy.luanhzt.view.GridViewForScrollView;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.flexibleadapter.items.ISectionable;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * 河道网格列表item
 *
 * @author Superman
 */

public class RiverGridItem extends AbstractFlexibleItem<RiverGridItem.EntityViewHolder> implements ISectionable<RiverGridItem.EntityViewHolder, MapClassifyHeaderItem>, IFilterable {
    private River river;
    private MapClassifyHeaderItem headerItem;
    private RiverGridAdapter mAdapter;

    public River getData() {
        return river;
    }

    public RiverGridItem(River river) {
        this.river = river;
    }

    public RiverGridItem(River river, MapClassifyHeaderItem header) {
        this.river = river;
        this.headerItem = header;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_grid_river;
    }

    @Override
    public RiverGridItem.EntityViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new RiverGridItem.EntityViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public boolean filter(String constraint) {
        return false;
    }

    @Override
    public MapClassifyHeaderItem getHeader() {
        return headerItem;
    }

    @Override
    public void setHeader(MapClassifyHeaderItem header) {
        this.headerItem = header;
    }

    static class EntityViewHolder extends FlexibleViewHolder {
        GridViewForScrollView gridview;

        public EntityViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            gridview = (GridViewForScrollView) view.findViewById(R.id.gridview);
        }
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, EntityViewHolder holder, int position, List payloads) {
        mAdapter = new RiverGridAdapter(holder.gridview.getContext());
        if (river != null && river.getChild() != null) {
            mAdapter.setList(river.getChild());
            holder.gridview.setAdapter(mAdapter);
            holder.gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    RiverDetailsActivity.startAction(holder.gridview.getContext(), river.getChild().get(i));
                }
            });

        }
    }


    /**
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof RiverItem) {
            RiverGridItem odata = (RiverGridItem) o;
            return river.getREACH_CODE().equals(odata.getData().getREACH_CODE());
        }
        return false;
    }
}
