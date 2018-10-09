package com.hywy.luanhzt.adapter.item;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cs.common.utils.StringUtils;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.entity.Reservoir;
import com.hywy.luanhzt.entity.TakeWater;

import java.text.DecimalFormat;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * 取用水监测
 *
 * @author Superman
 */

public class TakeWaterItem extends AbstractFlexibleItem<TakeWaterItem.EntityViewHolder> {
    private TakeWater takeWater;

    public TakeWater getData() {
        return takeWater;
    }

    public TakeWaterItem(TakeWater takeWater) {
        this.takeWater = takeWater;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_take_water;
    }

    @Override
    public TakeWaterItem.EntityViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new TakeWaterItem.EntityViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    static class EntityViewHolder extends FlexibleViewHolder {
        TextView tv_title_r;
        TextView tv_address_r;
        TextView tv_water_r;
        TextView tv_stream_r;
        TextView tv_time_r;

        public EntityViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            tv_title_r = (TextView) view.findViewById(R.id.tv_title_r);
            tv_address_r = (TextView) view.findViewById(R.id.tv_address_r);
            tv_water_r = (TextView) view.findViewById(R.id.water_height);
            tv_stream_r = (TextView) view.findViewById(R.id.stream_day);
            tv_time_r = (TextView) view.findViewById(R.id.tv_time_r);
        }
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, EntityViewHolder holder, int position, List payloads) {
        if (takeWater != null) {
            if (StringUtils.hasLength(takeWater.getSWFNM())) {
                holder.tv_title_r.setText(takeWater.getSWFNM());
            }
            if (takeWater.getREACH_NAME() != null) {
                holder.tv_address_r.setText(takeWater.getREACH_NAME());
            }
            if(StringUtils.hasLength(takeWater.getSUMQ())){
                holder.tv_water_r.setText(takeWater.getSUMQ() + "m³");
            }else {
                holder.tv_water_r.setText( "--m³");
            }
            holder.tv_stream_r.setText(takeWater.getQ() + "m³/s");
            holder.tv_time_r.setText(takeWater.getMNTM());

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
//        if (o instanceof WaterAndRainItem) {
//            WaterAndRainItem odata = (WaterAndRainItem) o;
//            return waterRain.getOrganID() == odata.getData().getOrganID();
//        }

        return false;
    }
}
