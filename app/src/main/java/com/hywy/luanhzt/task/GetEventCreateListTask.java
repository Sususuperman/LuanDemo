package com.hywy.luanhzt.task;

import android.content.Context;

import com.cs.common.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.adapter.item.DealItem;
import com.hywy.luanhzt.adapter.item.EventListItem;
import com.hywy.luanhzt.entity.Deal;
import com.hywy.luanhzt.entity.EventSupervise;
import com.hywy.luanhzt.key.Key;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 事件立项列表task
 *
 * @author Supermane
 */

public class GetEventCreateListTask extends BaseRequestTask {

    private int key;

    public GetEventCreateListTask(Context context) {
        super(context);
    }

    public GetEventCreateListTask(Context context, int key) {
        super(context);
        this.key = key;
    }


    @Override
    public String url() {
        return HttpUrl.getUrl(HttpUrl.RMS_APP_EVENT_CREATE_GETALLLIST);
    }

    @Override
    public Object request(String json) throws Exception {
        Type type = new TypeToken<List<EventSupervise>>() {
        }.getType();
        List<EventSupervise> list = new Gson().fromJson(json, type);
        if (StringUtils.isNotNullList(list)) {
            EventSupervise eventSupervise = list.get(0);
            if (StringUtils.isNotNullList(eventSupervise.getDeals())) {
                List<Deal> deals = eventSupervise.getDeals();
                List<DealItem> items = new ArrayList<>();
                int size = deals.size();
                for (int i = 0; i < size; i++) {
                    Deal deal = deals.get(i);
                    DealItem item = new DealItem(deal);
                    items.add(item);
                }
                result.put(Key.ITEMS, items);
            }
        }
        return list;
    }

    @Override
    public boolean isPost() {
        return false;
    }
}
