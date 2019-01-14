package com.hywy.luanhzt.dao;

import android.content.Context;

import com.cs.common.basedao.BaseDao;
import com.cs.common.utils.SharedUtils;
import com.cs.common.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hywy.luanhzt.app.App;
import com.hywy.luanhzt.entity.Inspection;
import com.hywy.luanhzt.entity.ProblemReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Superman on 2018/8/13.
 */

public class LogDao extends BaseDao {
    String tabName = "logDb";
    String logName = "log";
    App app;
    String userId = "";

    public LogDao(Context context) {
        super(context);
        app = App.getInstance();
        userId = app.getAccount().getUserId();
    }

    /**
     * 存储
     *
     * @param list
     */
    public void replace(List<Inspection> list) {
        SharedUtils.setString(App.getInstance().getApplicationContext(), tabName, userId + logName, new Gson().toJson(list));
    }

    /**
     * 插入
     *
     * @param inspection
     */
    public void insert(Inspection inspection) {
        List<Inspection> list = select();
        if (StringUtils.isNotNullList(list)) {
            list.add(inspection);
        } else {
            list = new ArrayList<>();
            list.add(inspection);
        }
        replace(list);
    }


    /**
     * 查询问题上报本地数据,使用userid+name的形式当key
     */
    public List<Inspection> select() {
        String name = SharedUtils.getString(App.getInstance().getApplicationContext(), tabName, userId + logName, "");
        return new Gson().fromJson(name, new TypeToken<List<Inspection>>() {
        }.getType());
    }

    /**
     * 存储问题上报本地数据,使用userid+name的形式当key
     */
    public void update(Inspection inspection) {
        List<Inspection> list = select();
        if (StringUtils.isNotNullList(list)) {
            for (int i = 0; i < list.size(); i++) {
                Inspection inspection2 = list.get(i);
                if (inspection2.getLOG_ID() == inspection.getLOG_ID()) {
                    list.set(i, inspection);
                }
            }
        }
        replace(list);
    }


    /**
     * 存储问题上报本地数据,使用userid+name的形式当key
     */
    public void delete(Inspection inspection) {
        List<Inspection> list = select();
        if (StringUtils.isNotNullList(list)) {
            for (int i = 0; i < list.size(); i++) {
                Inspection inspection2 = list.get(i);
                if (inspection2.getLOG_ID() == inspection.getLOG_ID()) {
                    list.remove(i);
                }
            }
        }
        replace(list);
    }

}
