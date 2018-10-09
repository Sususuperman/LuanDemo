package com.hywy.luanhzt.task;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.entity.EventSupervise;
import com.hywy.luanhzt.entity.RiverCourse;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 事物立项~相关水文站
 *
 * @author Superman
 */

public class GetEventShuiwenTask extends BaseRequestTask {

    public GetEventShuiwenTask(Context context) {
        super(context);
    }


    @Override
    public String url() {
        return HttpUrl.getUrl(HttpUrl.RMS_APP_ST_STBPRP_R_FINDSTCD);
    }

    @Override
    public Object request(String json) throws Exception {
        Type type = new TypeToken<List<RiverCourse>>() {
        }.getType();
        List<RiverCourse> list = new Gson().fromJson(json, type);

        return list;
    }

    @Override
    public boolean isPost() {
        return false;
    }
}
