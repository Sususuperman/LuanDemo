package com.hywy.luanhzt.adapter.item;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs.common.utils.StringUtils;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.entity.YuJing;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;


/**
 * 预警item
 *
 * @author Superman
 */
public class YuJingItem extends AbstractFlexibleItem<YuJingItem.EntityViewHolder> {
    private YuJing yuJing;

    public YuJingItem(YuJing yuJing) {
        this.yuJing = yuJing;
    }

    public YuJing getData() {
        return yuJing;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }


    @Override
    public int getLayoutRes() {
        return R.layout.item_yujing_layout;
    }

    @Override
    public EntityViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        YuJingItem.EntityViewHolder viewHolder = new EntityViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
        return viewHolder;
    }


    static class EntityViewHolder extends FlexibleViewHolder {
        TextView title, name, content, time;
        ImageView type;

        public EntityViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            title = (TextView) view.findViewById(R.id.title);
            name = (TextView) view.findViewById(R.id.name);
            time = (TextView) view.findViewById(R.id.time);
            content = (TextView) view.findViewById(R.id.content);
            type = (ImageView) view.findViewById(R.id.type);
        }
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, YuJingItem.EntityViewHolder holder, int position, List payloads) {
        holder.title.setText(yuJing.getWARNTHEME());
        holder.name.setText(yuJing.getWARNPEOPLE());
        holder.time.setText(yuJing.getTIME());
        holder.content.setText(yuJing.getWARNCONTENT());

        if (StringUtils.hasLength(yuJing.getWARNSOUCRE())) {
            holder.type.setImageResource(R.drawable.icon_rgyj);
        } else {
            holder.type.setImageResource(R.drawable.icon_zdyj);
        }

    }

}
