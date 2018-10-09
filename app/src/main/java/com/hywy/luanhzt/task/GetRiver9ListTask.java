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
 * 九大河流
 *
 * @author Superman
 */

public class GetRiver9ListTask extends BaseRequestTask {
    private Map<String, River> titleMap = new HashMap<>();
    private String id = "";

    public GetRiver9ListTask(Context context) {
        super(context);
    }


    @Override
    public String url() {
        return HttpUrl.getUrl(HttpUrl.RMS_APP_PERMISSIONS_RIVERWAYLISTTREE);
    }

    @Override
    public Object request(String json) throws Exception {
        Type type = new TypeToken<List<River>>() {
        }.getType();
        List<River> list = new Gson().fromJson(json, type);
        return list;
    }

    @Override
    public boolean isPost() {
        return false;
    }
}
