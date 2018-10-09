package com.hywy.luanhzt.task;

import android.content.Context;

import com.hywy.luanhzt.HttpUrl;

import java.util.Map;


/**
 * 通知公告发布
 *
 * @author Superman
 */
public class PostNotifyTask extends BaseRequestTask {
    public PostNotifyTask(Context context) {
        super(context);
    }

    @Override
    public String url() {
        return HttpUrl.getUrl(HttpUrl.RMS_APP_TZGG_SAVE);
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
        return json;
    }
}
