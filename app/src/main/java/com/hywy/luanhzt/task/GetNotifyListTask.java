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

/**
 * 获取通知公告列表
 *
 * @author Superman
 */

public class GetNotifyListTask extends BaseRequestTask {

    public GetNotifyListTask(Context context) {
        super(context);
    }


    @Override
    public String url() {
        return HttpUrl.getUrl(HttpUrl.RMS_APP_TZGG_FINDTZGGS);
    }

    @Override
    public Object request(String json) throws Exception {
        Type type = new TypeToken<List<Notify>>() {
        }.getType();
        List<Notify> list = new Gson().fromJson(json, type);
        List<NotifyItem> items = new ArrayList<>();
        for (Notify notify : list) {
            NotifyItem item = new NotifyItem(notify);
            items.add(item);
        }
        result.put(Key.ITEMS, items);
        return list;
    }

    @Override
    public boolean isPost() {
        return false;
    }
}
