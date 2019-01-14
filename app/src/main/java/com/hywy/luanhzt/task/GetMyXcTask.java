package com.hywy.luanhzt.task;

import android.content.Context;
import android.util.Log;

import com.cs.common.utils.IToast;
import com.cs.common.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.adapter.item.InspectionItem;
import com.hywy.luanhzt.dao.LogDao;
import com.hywy.luanhzt.dao.ProblemDao;
import com.hywy.luanhzt.entity.Inspection;
import com.hywy.luanhzt.key.Key;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Superman
 */
public class GetMyXcTask extends BaseRequestTask {
    private int index;

    public GetMyXcTask(Context context, int index) {
        super(context);
        this.index = index;
    }

    public GetMyXcTask(Context context) {
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

        List<InspectionItem> items = new ArrayList<>();
        if (StringUtils.isNotNullList(list)) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                Inspection r = list.get(i);
                InspectionItem item = new InspectionItem(r);
                items.add(item);
            }
        }

        //查询数据库追加list
        List<InspectionItem> items_local = new ArrayList<>();
        LogDao dao = new LogDao(context);
        if (StringUtils.isNotNullList(dao.select())) {
            for (Inspection inspection : dao.select()) {
                InspectionItem item = new InspectionItem(inspection);
                items_local.add(item);
            }
        }
        List<InspectionItem> start_items;
        if (items.size() > 5) {
            start_items = items.subList(0, 5);//截取前五条数据
        } else {
            start_items = items;
        }
        if (index != 0) {
            result.put(Key.ITEMS, start_items);
        } else {
            items.addAll(0, items_local);
            result.put(Key.ITEMS, items);
        }
        return result;
    }
}
