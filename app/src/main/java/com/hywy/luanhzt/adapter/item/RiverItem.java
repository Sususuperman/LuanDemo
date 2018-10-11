package com.hywy.luanhzt.adapter.item;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs.common.utils.StringUtils;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.entity.River;

import java.text.DecimalFormat;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.flexibleadapter.items.ISectionable;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * 河道列表item
 *
 * @author Superman
 */

public class RiverItem extends AbstractFlexibleItem<RiverItem.EntityViewHolder> implements ISectionable<RiverItem.EntityViewHolder, MapClassifyHeaderItem>, IFilterable {
    private River river;
    private MapClassifyHeaderItem headerItem;

    public River getData() {
        return river;
    }

    public RiverItem(River river) {
        this.river = river;
    }

    public RiverItem(River river, MapClassifyHeaderItem header) {
        this.river = river;
        this.headerItem = header;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_river;
    }

    @Override
    public RiverItem.EntityViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new RiverItem.EntityViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
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
        ImageView iv_status;

        public EntityViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            tv_name = (TextView) view.findViewById(R.id.name);
            iv_status = (ImageView) view.findViewById(R.id.iv_status);
        }
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, EntityViewHolder holder, int position, List payloads) {
        if (river != null) {
            if(StringUtils.hasLength(river.getRV_NAME())){
                holder.tv_name.setText(river.getRV_NAME());
            }
            if (StringUtils.hasLength(river.getREACH_NAME())) {
                holder.tv_name.setText(river.getREACH_NAME());
            }

            if(StringUtils.hasLength(river.getREACH_LEVEL())){
                switch (river.getREACH_LEVEL()) {
                    case "1":
                        holder.iv_status.setImageResource(R.drawable.ic_i);
                        break;
                    case "2":
                        holder.iv_status.setImageResource(R.drawable.ic_ii);
                        break;
                    case "3":
                        holder.iv_status.setImageResource(R.drawable.ic_iii);
                        break;
                    case "4":
                        holder.iv_status.setImageResource(R.drawable.ic_iv);
                        break;
                    case "5":
                        holder.iv_status.setImageResource(R.drawable.ic_v);
                        break;
                    case "6":
                        holder.iv_status.setImageResource(R.drawable.ic_v_);
                        break;
                }
            }

//            if (reservoir.getSTLC() != null) {
//                holder.tv_address_r.setText(reservoir.getSTLC());
//            }
//            holder.tv_water_r.setText(getTwoDecimal(reservoir.getRZ()) + "m");
//            holder.tv_stream_r.setText(getTwoDecimal(reservoir.getOTQ()) + "m³/s");
//            holder.tv_time_r.setText(reservoir.getTM());

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
        if (o instanceof RiverItem) {
            RiverItem odata = (RiverItem) o;
            return river.getREACH_CODE().equals(odata.getData().getREACH_CODE());
        }
        return false;
    }
}
