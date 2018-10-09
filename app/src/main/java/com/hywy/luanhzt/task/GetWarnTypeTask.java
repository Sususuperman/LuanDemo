package com.hywy.luanhzt.task;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.entity.EventSupervise;
import com.hywy.luanhzt.entity.WarnType;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 预警类型列表
 *
 * @author Superman
 */

public class GetWarnTypeTask extends BaseRequestTask {

    public GetWarnTypeTask(Context context) {
        super(context);
    }


    @Override
    public String url() {
        return HttpUrl.getUrl(HttpUrl.RMS_APP_WARN_R_APPLISTTYPE);
    }

    @Override
    public Object request(String json) throws Exception {
        Type type = new TypeToken<List<WarnType>>() {
        }.getType();
        List<WarnType> list = new Gson().fromJson(json, type);
        return list;
    }

    @Override
    public boolean isPost() {
        return false;
    }
}
