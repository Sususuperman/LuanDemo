package com.hywy.luanhzt.task;

import android.content.Context;

import com.hywy.luanhzt.HttpUrl;

import java.util.Map;


/**
 * 新建事物立项
 *
 * @author Superman
 */
public class PostCreateEventProgramTask extends BaseRequestTask {
    public PostCreateEventProgramTask(Context context) {
        super(context);
    }

    @Override
    public String url() {
        return HttpUrl.getUrl(HttpUrl.RMS_APP_EVENT_CREATE_APPSAVES);
    }

    @Override
    public void postAfter(Map<String, Object> params) {
        params.remove("USER_ID");
    }

    @Override
    public boolean isPost() {
        return true;
    }

    @Override
    public Object request(String json) throws Exception {
//        String js = new JSONObject(json).getString("data");
//        Organ organ = new Gson().fromJson(json,Organ.class);
        return json;
    }
}