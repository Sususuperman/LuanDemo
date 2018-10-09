package com.hywy.luanhzt.task;

import android.content.Context;
import android.util.Log;

import com.cs.common.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.adapter.item.InspectionItem;
import com.hywy.luanhzt.adapter.item.XunChaRecord2Item;
import com.hywy.luanhzt.entity.Inspection;
import com.hywy.luanhzt.key.Key;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * 河道~日志记录
 *
 * @author Superman
 */
public class GetXcInRiverTask extends BaseRequestTask {
    private int index;

    public GetXcInRiverTask(Context context, int index) {
        super(context);
        this.index = index;
    }

    public GetXcInRiverTask(Context context) {
        super(context);
    }

    @Override

    public String url() {
        return HttpUrl.getUrl(HttpUrl.RMS_APP_PATROL_LOG_LIST);
    }


    @Override
    public boolean isPost() {
        return false;
    }

    @Override
    public Object request(String json) throws Exception {
        Type type = new TypeToken<List<Inspection>>() {
        }.getType();
        List<Inspection> list = new Gson().fromJson(json, type);

        List<XunChaRecord2Item> items = new ArrayList<>();
        if (StringUtils.isNotNullList(list)) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                Inspection r = list.get(i);
                XunChaRecord2Item item = new XunChaRecord2Item(r);
                items.add(item);
            }
        }
            result.put(Key.ITEMS, items);
        return result;
    }
}
