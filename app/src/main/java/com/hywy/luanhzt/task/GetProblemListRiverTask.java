package com.hywy.luanhzt.task;

import android.content.Context;

import com.cs.common.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.adapter.item.ProblemReportItem;
import com.hywy.luanhzt.dao.ProblemDao;
import com.hywy.luanhzt.entity.ProblemReport;
import com.hywy.luanhzt.key.Key;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 我的河道详情~事件列表
 *
 * @author Superman
 */

public class GetProblemListRiverTask extends BaseRequestTask {

    public GetProblemListRiverTask(Context context) {
        super(context);
    }


    @Override
    public String url() {
        return HttpUrl.getUrl(HttpUrl.RMS_APP_PATROL_EVENT_GETALLLIST);
    }

    @Override
    public Object request(String json) throws Exception {
        Type type = new TypeToken<List<ProblemReport>>() {
        }.getType();
        List<ProblemReport> list = new Gson().fromJson(json, type);
        /**********************************/;
        List<ProblemReportItem> items = new ArrayList<>();
        for (ProblemReport problemReport : list) {
            ProblemReportItem item = new ProblemReportItem(problemReport);
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
