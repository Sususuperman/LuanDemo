package com.hywy.luanhzt.task;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.entity.TakeWater;
import com.hywy.luanhzt.entity.WaterQualityChart;
import com.hywy.luanhzt.key.Key;

import java.lang.reflect.Type;
import java.util.List;


/**
 * 取水口日月时查询
 *
 * @author Superman
 */
public class GetTakeWaterBytimeTask extends BaseRequestTask {


    public GetTakeWaterBytimeTask(Context context) {
        super(context);
    }

    @Override

    public String url() {
        return HttpUrl.getUrl(HttpUrl.RMS_APP_INTAKE_R_GETYDM_APP);
    }


    @Override
    public boolean isPost() {
        return false;
    }

    @Override
    public Object request(String json) throws Exception {
        Type type = new TypeToken<List<TakeWater>>() {
        }.getType();
        result.put(Key.CHART, "TakeWaterChart");
        List<TakeWater> list = new Gson().fromJson(json, type);
        return list;
    }
}
