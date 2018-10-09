package com.hywy.luanhzt.task;

import android.content.Context;

import com.cs.common.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.entity.News;
import com.hywy.luanhzt.key.Key;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 新闻列表
 *
 * @author Superman
 */

public class GetNewsListTask extends BaseRequestTask {

    public GetNewsListTask(Context context) {
        super(context);
    }


    @Override
    public String url() {
        return HttpUrl.getUrl(HttpUrl.RMS_APP_IMG_LIST);
    }

    @Override
    public Object request(String json) throws Exception {
        Type type = new TypeToken<List<News>>() {
        }.getType();
        List<News> list = new Gson().fromJson(json, type);
        if (StringUtils.isNotNullList(list)) {
            return list.size() > 3 ? list.subList(0, 2) : list;
        }
        return list;
    }

    @Override
    public boolean isPost() {
        return false;
    }
}
