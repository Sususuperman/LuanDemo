package com.hywy.luanhzt.adapter.item;

import android.support.v4.content.ContextCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cs.common.utils.ImageLoaderUtils;
import com.cs.common.utils.StringUtils;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.entity.Inspection;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;


/**
 * @author Superman
 */
public class InspectionItem extends AbstractFlexibleItem<InspectionItem.EntityViewHolder> {
    private Inspection data;

    public InspectionItem(Inspection data) {
        this.data = data;
    }


    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Inspection)
            return data.getLOG_ID() == ((Inspection) o).getLOG_ID();
        return false;
    }

    public void setData(Inspection data) {
        this.data = data;
    }

    public Inspection getData() {
        return data;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_inspection_list;
    }

    @Override
    public EntityViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new EntityViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }


    static class EntityViewHolder extends FlexibleViewHolder {
        TextView log_name;
        TextView river_name;
        TextView address;
        TextView time;
        LinearLayout layout_selected;
        ImageView item_img;
        TextView type;

        public EntityViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            layout_selected = (LinearLayout) view.findViewById(R.id.layout_selected);
            log_name = (TextView) view.findViewById(R.id.log_name);
            river_name = (TextView) view.findViewById(R.id.river_name);
            address = (TextView) view.findViewById(R.id.address);
            time = (TextView) view.findViewById(R.id.time);
            item_img = (ImageView) view.findViewById(R.id.item_img);
            type = (TextView) view.findViewById(R.id.type);
        }
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, EntityViewHolder holder, int position, List payloads) {
        if (StringUtils.hasLength(data.getPATROL_LOG_NAME()))
            holder.log_name.setText(data.getPATROL_LOG_NAME());
        else
            holder.log_name.setText("");

        if (StringUtils.hasLength(data.getREACH_NAME()))
            holder.river_name.setText(data.getREACH_NAME());
        else
            holder.river_name.setText("");

        if (StringUtils.hasLength(data.getADNM()))
            holder.address.setText(data.getADNM());
        else
            holder.address.setText("");

        if (StringUtils.hasLength(data.getSTARTTIME()))
            holder.time.setText(data.getSTARTTIME());
        else
            holder.time.setText("");

        if (StringUtils.isNotNullList(data.getPATROL_NOTE())) {
            ImageLoaderUtils.display(holder.item_img.getContext(), holder.item_img, data.getPATROL_NOTE().get(0).getATTACH_URL());
        } else {
            ImageLoaderUtils.display(holder.item_img.getContext(), holder.item_img, R.drawable.icon_empty_image);
        }
        if (data.getDATA_TYPE().equals(Inspection.DATA_LOCAL)) {
            holder.type.setVisibility(View.VISIBLE);
        } else {
            holder.type.setVisibility(View.GONE);
        }
    }
}
