package com.hywy.luanhzt.adapter.item;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs.common.utils.StringUtils;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.entity.River;
import com.hywy.luanhzt.entity.RiverFile;

import java.text.DecimalFormat;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.flexibleadapter.items.ISectionable;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * 一盒一侧item
 *
 * @author Superman
 */

public class RiverFileItem extends AbstractFlexibleItem<RiverFileItem.EntityViewHolder> implements ISectionable<RiverFileItem.EntityViewHolder, MapClassifyHeaderItem>, IFilterable {
    private RiverFile riverFile;
    private MapClassifyHeaderItem headerItem;

    public RiverFile getData() {
        return riverFile;
    }

    public RiverFileItem(RiverFile riverFile) {
        this.riverFile = riverFile;
    }


    @Override
    public int getLayoutRes() {
        return R.layout.item_river_file;
    }

    @Override
    public RiverFileItem.EntityViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new RiverFileItem.EntityViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
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
        TextView tv_name;

        public EntityViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            tv_name = (TextView) view.findViewById(R.id.name);
        }
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, EntityViewHolder holder, int position, List payloads) {
        if (riverFile != null) {
            if (StringUtils.hasLength(riverFile.getFILE_NAME())) {
                holder.tv_name.setText(riverFile.getFILE_NAME());
            }

        }
    }

    /**
     * 将数据保留两位小数
     */
    private String getTwoDecimal(double num) {
        DecimalFormat dFormat = new DecimalFormat("#########0.00");
        String str = dFormat.format(num);
        return str;
    }

    /**
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof RiverFileItem) {
            RiverFileItem odata = (RiverFileItem) o;
            return riverFile.getID().equals(odata.getData().getID());
        }
        return false;
    }
}
