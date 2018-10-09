package com.hywy.luanhzt.task;

import android.content.Context;

import com.google.gson.Gson;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.entity.RiverDetails;

/**
 * 河道详情
 *
 * @author Superman
 */

public class GetRiverDetailsTask extends BaseRequestTask {

    public GetRiverDetailsTask(Context context) {
        super(context);
    }


    @Override
    public String url() {
        return HttpUrl.getUrl(HttpUrl.RMS_APP_RIVERWAY_RIVERWAYLIST);
    }

    @Override
    public Object request(String json) throws Exception {
        RiverDetails rd = new Gson().fromJson(json, RiverDetails.class);
        return rd;
    }

    @Override
    public boolean isPost() {
        return false;
    }
}
