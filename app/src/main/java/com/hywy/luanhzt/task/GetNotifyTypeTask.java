package com.hywy.luanhzt.task;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.adapter.item.NotifyItem;
import com.hywy.luanhzt.entity.Notify;
import com.hywy.luanhzt.key.Key;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 获取通知公告类型列表
 *
 * @author Superman
 */

public class GetNotifyTypeTask extends BaseRequestTask {

    public GetNotifyTypeTask(Context context) {
        super(context);
    }

    @Override
    public void postAfter(Map<String, Object> params) {
    }

    @Override
    public String url() {
        return HttpUrl.getUrl(HttpUrl.RMS_APP_TZGG_FINDINFOTYPE);
    }

    @Override
    public Object request(String json) throws Exception {
        Type type = new TypeToken<List<Notify>>() {
        }.getType();
        List<Notify> list = new Gson().fromJson(json, type);
        return list;
    }

    @Override
    public boolean isPost() {
        return false;
    }
}
