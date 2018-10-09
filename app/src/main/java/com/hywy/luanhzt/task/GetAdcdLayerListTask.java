package com.hywy.luanhzt.task;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.entity.AdcdLayerInfo;
import com.hywy.luanhzt.entity.MapClassify;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 一张图初始化图层接口
 *
 * @author Superman
 */

public class GetAdcdLayerListTask extends BaseRequestTask {

    public GetAdcdLayerListTask(Context context) {
        super(context);
    }


    @Override
    public String url() {
        return HttpUrl.getUrl(HttpUrl.RMS_APP_MAP_FINDMAPURL);
    }

    @Override
    public Object request(String json) throws Exception {
        Type type = new TypeToken<List<AdcdLayerInfo>>() {
        }.getType();
        List<AdcdLayerInfo> list = new Gson().fromJson(json, type);
        return list;
    }

    @Override
    public boolean isPost() {
        return false;
    }
}
