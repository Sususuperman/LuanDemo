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
 * 我的河道列表
 *
 * @author Superman
 */

public class GetRiverListTask extends BaseRequestTask {
    private Map<String, River> titleMap = new HashMap<>();
    private String id = "";

    public GetRiverListTask(Context context) {
        super(context);
    }

    public GetRiverListTask(Context context, String id) {
        super(context);
        this.id = id;
    }

    @Override
    public String url() {
        return HttpUrl.getUrl(HttpUrl.RMS_APP_RIVERWAY_RIVERWAYLISTTREE);
    }

    @Override
    public Object request(String json) throws Exception {
        Type type = new TypeToken<List<River>>() {
        }.getType();
        List<River> list = new Gson().fromJson(json, type);
        List<RiverItem> items = new ArrayList<>();
        for (River river : list) {
            List<River> childs = river.getChild();
            //根据id进行筛选
            if (StringUtils.hasLength(id)) {
                if (river.getRV_CODE().equals(id)) {
                    if (StringUtils.isNotNullList(childs)) {
                        for (int i = 0; i < childs.size(); i++) {
                            River r = childs.get(i);
                            RiverItem riverItem = null;
                            if (i == 0) {
                                riverItem = new RiverItem(r, new MapClassifyHeaderItem(river.getRV_NAME()));
                            } else {
                                riverItem = new RiverItem(r);
                            }
                            items.add(riverItem);
                        }
                        result.put(Key.ITEMS, items);
                    }
                }
            } else {
                if (StringUtils.isNotNullList(childs)) {
                    for (int i = 0; i < childs.size(); i++) {
                        River r = childs.get(i);
                        RiverItem riverItem = null;
                        if (i == 0) {
                            riverItem = new RiverItem(r, new MapClassifyHeaderItem(river.getRV_NAME()));
                        } else {
                            riverItem = new RiverItem(r);
                        }
                        items.add(riverItem);
                    }
                    result.put(Key.ITEMS, items);
                }
            }
        }

//        if (StringUtils.isNotNullList(list)) {
//            List<RiverItem> items = new ArrayList<>();
//            int size = list.size();
//            for (int i = 0; i < size; i++) {
//                River r = list.get(i);
//                RiverItem item = new RiverItem(r);
//                items.add(item);
//            }
//            result.put(Key.ITEMS, items);
//        }
        return list;
    }

    @Override
    public boolean isPost() {
        return false;
    }
}
