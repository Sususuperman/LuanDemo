package com.hywy.luanhzt.task;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.adapter.item.MessageItem;
import com.hywy.luanhzt.adapter.item.WaterAndRainItem;
import com.hywy.luanhzt.entity.Message;
import com.hywy.luanhzt.entity.Reservoir;
import com.hywy.luanhzt.entity.RiverCourse;
import com.hywy.luanhzt.entity.RiverDetails;
import com.hywy.luanhzt.entity.TakeWater;
import com.hywy.luanhzt.entity.WaterQuality;
import com.hywy.luanhzt.entity.WaterRain;
import com.hywy.luanhzt.key.Key;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取一张图测站信息列表
 *
 * @author Superman
 */

public class GetSiteInMapTask extends BaseRequestTask {
    private String name;

    public GetSiteInMapTask(Context context, String name) {
        super(context);
        this.name = name;
    }


    @Override
    public String url() {
        return HttpUrl.getUrl(HttpUrl.RMS_APP_MAP_LISTSTION);
    }

    @Override
    public Object request(String json) throws Exception {
        if (name.equals("ylz")) {//雨量站
            Type type = new TypeToken<List<WaterRain>>() {
            }.getType();
            List<WaterRain> list = new Gson().fromJson(json, type);
            return list;
        } else if (name.equals("hdswz")) {//河道水位站
            Type type = new TypeToken<List<RiverCourse>>() {
            }.getType();
            List<RiverCourse> list = new Gson().fromJson(json, type);
            return list;
        } else if (name.equals("skswz")) {//水库水位
            Type type = new TypeToken<List<Reservoir>>() {
            }.getType();
            List<Reservoir> list = new Gson().fromJson(json, type);
            return list;
        }else if (name.equals("qsk")) {//取水口
            Type type = new TypeToken<List<TakeWater>>() {
            }.getType();
            List<TakeWater> list = new Gson().fromJson(json, type);
            return list;
        } else {//水质站
            Type type = new TypeToken<List<WaterQuality>>() {
            }.getType();
            List<WaterQuality> list = new Gson().fromJson(json, type);
            return list;
        }
    }

    @Override
    public boolean isPost() {
        return false;
    }
}
