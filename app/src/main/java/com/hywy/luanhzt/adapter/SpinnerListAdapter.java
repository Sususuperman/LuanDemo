package com.hywy.luanhzt.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cs.common.adapter.BaseListAdapter;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.entity.Adnm;
import com.hywy.luanhzt.entity.EventSupervise;
import com.hywy.luanhzt.entity.River;
import com.hywy.luanhzt.entity.RiverCourse;
import com.hywy.luanhzt.entity.User;
import com.hywy.luanhzt.entity.WarnType;
import com.hywy.luanhzt.entity.WaterQuality;

/**
 * @author Superman
 * @date 2018/7/9
 */

public class SpinnerListAdapter extends BaseListAdapter {
    public SpinnerListAdapter(Context context) {
        super(context);
    }

    @Override
    protected View getView(LayoutInflater layoutInflater, View convertView, ViewGroup parent, int position) {
        convertView = layoutInflater.inflate(R.layout.item_spinner, parent, false);
        TextView name = (TextView) convertView.findViewById(R.id.address);
        name.setTextSize(15);
        name.setTextColor(ContextCompat.getColor(mContext, R.color.font_1));
        Object object = getItem(position);
        if (object instanceof Adnm) {
            Adnm adnm = (Adnm) object;
            if (adnm.getADNM() != null) {
                name.setText(adnm.getADNM());
            }
        } else if (object instanceof River) {
            River river = (River) object;
            if (river.getREACH_NAME() != null) {
                name.setText(river.getREACH_NAME());
            }
        } else if (object instanceof User) {
            User user = (User) object;
            if (user.getNAME() != null) {
                name.setText(user.getNAME());
            }
        } else if (object instanceof WaterQuality) {
            WaterQuality waterQuality = (WaterQuality) object;
            if (waterQuality.getSTNM() != null) {
                name.setText(waterQuality.getSTNM());
            }
        } else if (object instanceof RiverCourse) {
            RiverCourse riverCourse = (RiverCourse) object;
            if (riverCourse.getSTNM() != null) {
                name.setText(riverCourse.getSTNM());
            }
        } else if (object instanceof WarnType) {
            WarnType warnType = (WarnType) object;
            if (warnType.getWARNTYPEDES() != null) {
                name.setText(warnType.getWARNTYPEDES());
            }
        } else {
            EventSupervise event = (EventSupervise) object;
            if (event.getEVENT_TYPE_NAME() != null) {
                name.setText(event.getEVENT_TYPE_NAME());
            }
        }

        return convertView;
    }
}
