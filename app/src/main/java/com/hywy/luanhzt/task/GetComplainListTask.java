package com.hywy.luanhzt.task;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.adapter.item.ComplainItem;
import com.hywy.luanhzt.entity.Complain;
import com.hywy.luanhzt.key.Key;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 投诉列表task
 *
 * @author Superman
 */

public class GetComplainListTask extends BaseRequestTask {

    public GetComplainListTask(Context context) {
        super(context);
    }
    @Override
    public String url() {
        return HttpUrl.getUrl(HttpUrl.RMS_APP_COMPLAIN_NEW_GETALLLIST);
    }

    @Override
    public Object request(String json) throws Exception {
        Type type = new TypeToken<List<Complain>>() {
        }.getType();
        List<Complain> list = new Gson().fromJson(json, type);
        List<ComplainItem> items = new ArrayList<>();
        for (Complain complain : list) {
            ComplainItem item = new ComplainItem(complain);
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
