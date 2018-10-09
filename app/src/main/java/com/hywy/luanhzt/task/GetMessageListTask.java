package com.hywy.luanhzt.task;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.adapter.item.MessageItem;
import com.hywy.luanhzt.adapter.item.NotifyItem;
import com.hywy.luanhzt.entity.Message;
import com.hywy.luanhzt.entity.Notify;
import com.hywy.luanhzt.key.Key;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取留言簿列表
 *
 * @author Superman
 */

public class GetMessageListTask extends BaseRequestTask {

    public GetMessageListTask(Context context) {
        super(context);
    }


    @Override
    public String url() {
        return HttpUrl.getUrl(HttpUrl.RMS_APP_TXL_LISTLY);
    }

    @Override
    public Object request(String json) throws Exception {
        Type type = new TypeToken<List<Message>>() {
        }.getType();
        List<Message> list = new Gson().fromJson(json, type);
        List<MessageItem> items = new ArrayList<>();
        for (Message message : list) {
            MessageItem item = new MessageItem(message);
            items.add(item);
        }
        result.put(Key.ITEMS, items);
        return list;
    }

    @Override
    public boolean isPost() {
        return false;
    }
}
