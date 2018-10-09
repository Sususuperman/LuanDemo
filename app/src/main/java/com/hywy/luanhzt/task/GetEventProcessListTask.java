package com.hywy.luanhzt.task;

import android.content.Context;

import com.cs.common.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.adapter.item.EventListItem;
import com.hywy.luanhzt.entity.EventSupervise;
import com.hywy.luanhzt.key.Key;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 事物管理~未处理列表task
 *
 * @author Superman
 */

public class GetEventProcessListTask extends BaseRequestTask {

    private int key;

    public GetEventProcessListTask(Context context) {
        super(context);
    }

    public GetEventProcessListTask(Context context, int key) {
        super(context);
        this.key = key;
    }


    @Override
    public String url() {
        return HttpUrl.getUrl(HttpUrl.RMS_APP_EVENT_PROCESS_APP_GETALLLIST);
    }

    @Override
    public Object request(String json) throws Exception {
        Type type = new TypeToken<List<EventSupervise>>() {
        }.getType();
        List<EventSupervise> list = new Gson().fromJson(json, type);
        if (StringUtils.isNotNullList(list)) {
            List<EventListItem> items = new ArrayList<>();
            int size = list.size();
            for (int i = 0; i < size; i++) {
                EventSupervise r = list.get(i);
                EventListItem item = new EventListItem(r);
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
