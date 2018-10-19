package com.hywy.luanhzt.task;

import android.content.Context;

import com.cs.common.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.adapter.item.MapClassifyHeaderItem;
import com.hywy.luanhzt.adapter.item.RiverItem;
import com.hywy.luanhzt.entity.River;
import com.hywy.luanhzt.key.Key;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的直属河道列表
 *
 * @author Superman
 */

public class GetMyRiverListTask extends BaseRequestTask {
    private Map<String, River> titleMap = new HashMap<>();

    public GetMyRiverListTask(Context context) {
        super(context);
    }

    @Override
    public String url() {
        return HttpUrl.getUrl(HttpUrl.RMS_APP_RIVERWAY_APPRIVERLIST);
    }

    @Override
    public Object request(String json) throws Exception {
        Type type = new TypeToken<List<River>>() {
        }.getType();
        List<River> list = new Gson().fromJson(json, type);
        List<RiverItem> items = new ArrayList<>();
        if (StringUtils.isNotNullList(list)) {
            for (River river : list) {
                if(river.getTYPE()!=0){
                    RiverItem item = new RiverItem(river);
                    items.add(item);
                }
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
