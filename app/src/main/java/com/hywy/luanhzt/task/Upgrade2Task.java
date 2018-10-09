package com.hywy.luanhzt.task;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.cs.common.utils.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hywy.luanhzt.Const;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.adapter.item.PlanItem;
import com.hywy.luanhzt.entity.Plan;
import com.hywy.luanhzt.entity.Upgrade;
import com.hywy.luanhzt.key.Key;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Upgrade2Task extends BaseRequestTask {

    public Upgrade2Task(Context context, Map<String, Object> params) {
        super(context, params);
    }
    public Upgrade2Task(Context context) {
        super(context);
    }

    @Override
    public String url() {
        return HttpUrl.getUpgradeUrl();
    }

    @Override
    public boolean isPost() {
        return false;
    }

    @Override
    public Object request(String json) throws Exception {
        Type type = new TypeToken<List<Upgrade>>() {
        }.getType();
        List<Upgrade> list = new Gson().fromJson(json, type);
        return list;
    }

}
