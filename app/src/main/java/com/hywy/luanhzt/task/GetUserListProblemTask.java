package com.hywy.luanhzt.task;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.entity.EventSupervise;
import com.hywy.luanhzt.entity.User;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 事件上报~上报对象列表
 *
 * @author Superman
 */

public class GetUserListProblemTask extends BaseRequestTask {

    public GetUserListProblemTask(Context context) {
        super(context);
    }


    @Override
    public String url() {
        return HttpUrl.getUrl(HttpUrl.RMS_APP_PERMISSIONS_FINDNT);
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
