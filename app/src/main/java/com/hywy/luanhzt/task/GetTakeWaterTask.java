package com.hywy.luanhzt.task;

import android.content.Context;

import com.cs.common.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.adapter.item.RiverCourseItem;
import com.hywy.luanhzt.adapter.item.TakeWaterItem;
import com.hywy.luanhzt.entity.RiverCourse;
import com.hywy.luanhzt.entity.TakeWater;
import com.hywy.luanhzt.key.Key;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 取用水监测
 *
 * @author Superman
 */

public class GetTakeWaterTask extends BaseRequestTask {

    public GetTakeWaterTask(Context context) {
        super(context);
    }


    @Override
    public String url() {
        return HttpUrl.getUrl(HttpUrl.RMS_APP_INTAKE_R_FINDINTAKES_APP);
    }

    @Override
    public Object request(String json) throws Exception {
        Type type = new TypeToken<List<TakeWater>>() {
        }.getType();
        List<TakeWater> list = new Gson().fromJson(json, type);
        if (StringUtils.isNotNullList(list)) {
            List<TakeWaterItem> items = new ArrayList<>();
            int size = list.size();
            for (int i = 0; i < size; i++) {
                TakeWater t = list.get(i);
                TakeWaterItem item = new TakeWaterItem(t);
                items.add(item);
            }
            result.put(Key.ITEMS, items);
        }
        return list;
    }

    @Override
    public boolean isPost() {
        return false;
    }
}
