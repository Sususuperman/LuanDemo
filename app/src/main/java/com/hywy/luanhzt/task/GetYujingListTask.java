package com.hywy.luanhzt.task;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.adapter.item.YuJingItem;
import com.hywy.luanhzt.entity.YuJing;
import com.hywy.luanhzt.key.Key;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取预警列表
 *
 * @author Superman
 */

public class GetYujingListTask extends BaseRequestTask {

    public GetYujingListTask(Context context) {
        super(context);
    }


    @Override
    public String url() {
        return HttpUrl.getUrl(HttpUrl.RMS_APP_WARN_R_APPFINDALLLIST);
    }

    @Override
    public Object request(String json) throws Exception {
        Type type = new TypeToken<List<YuJing>>() {
        }.getType();
        List<YuJing> list = new Gson().fromJson(json, type);
        List<YuJingItem> items = new ArrayList<>();
        for (YuJing yuJing : list) {
            YuJingItem item = new YuJingItem(yuJing);
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
