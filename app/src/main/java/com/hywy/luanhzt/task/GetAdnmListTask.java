package com.hywy.luanhzt.task;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.entity.Adnm;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 下级区县列表
 *
 * @author Superman
 */

public class GetAdnmListTask extends BaseRequestTask {

    public GetAdnmListTask(Context context) {
        super(context);
    }


    @Override
    public String url() {
        return HttpUrl.getUrl(HttpUrl.RMS_APP_APP_ADCD_FINDADCDBYUSER);
    }

    @Override
    public Object request(String json) throws Exception {
        Type type = new TypeToken<List<Adnm>>() {
        }.getType();
        List<Adnm> list = new Gson().fromJson(json, type);

        return list;
    }

    @Override
    public boolean isPost() {
        return false;
    }
}
