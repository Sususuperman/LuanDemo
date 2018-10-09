package com.hywy.luanhzt.task;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.adapter.item.PlanItem;
import com.hywy.luanhzt.entity.Account;
import com.hywy.luanhzt.entity.Plan;
import com.hywy.luanhzt.entity.River;
import com.hywy.luanhzt.entity.User;
import com.hywy.luanhzt.key.Key;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 事务督办~选择人员列表
 *
 * @author Superman
 */

public class GetUserListTask extends BaseRequestTask {
    private Map<String, River> titleMap = new HashMap<>();
    private String id = "";

    public GetUserListTask(Context context) {
        super(context);
    }


    @Override
    public String url() {
        return HttpUrl.getUrl(HttpUrl.RMS_APP_PERMISSIONS_GETPERLIST);
    }

    @Override
    public Object request(String json) throws Exception {
        Type type = new TypeToken<List<User>>() {
        }.getType();
        List<User> list = new Gson().fromJson(json, type);
        return list;
    }

    @Override
    public boolean isPost() {
        return false;
    }
}
