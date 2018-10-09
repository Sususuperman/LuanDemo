package com.hywy.luanhzt.configure;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.cs.common.utils.IToast;
import com.cs.common.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hywy.luanhzt.activity.EventListActivity;
import com.hywy.luanhzt.activity.EventSuperviseActivity;
import com.hywy.luanhzt.activity.MenuManageActivity;
import com.hywy.luanhzt.activity.MobileWork2Activity;
import com.hywy.luanhzt.activity.NotifyActivity;
import com.hywy.luanhzt.activity.OrganizeInfoActivity;
import com.hywy.luanhzt.activity.ProblemReportActivity;
import com.hywy.luanhzt.activity.RiverMonitorActivity;
import com.hywy.luanhzt.activity.VideoInfoActivity;
import com.hywy.luanhzt.activity.VideoListActivity;
import com.hywy.luanhzt.activity.YuJingListActivity;
import com.hywy.luanhzt.entity.AppMenu;
import com.hywy.luanhzt.entity.MenuEntity;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Superman
 */

public class AppConfigure {

    /**
     * 从接口拿到menu信息与本地菜单配置文件进行对比，更新最新的菜单。
     * 本地为最全的菜单列表，从中筛选权限菜单，menujson为json字符串
     *
     * @param menujson
     * @param menus
     * @return
     */
    public static List<MenuEntity> saveMenuEntities(List<AppMenu> menus, String menujson) {
        List<MenuEntity> allMenulist = new ArrayList<>();
        //Json的解析类对象
        JsonParser parser = new JsonParser();
        //将JSON的String 转成一个JsonArray对象
        JsonArray jsonArray = parser.parse(menujson).getAsJsonArray();
        Gson gson = new Gson();
        if (StringUtils.isNotNullList(menus)) {
            for (AppMenu menu : menus) {
                //加强for循环遍历JsonArray
                for (JsonElement indexArr : jsonArray) {
                    //使用GSON，直接转成Bean对象
                    MenuEntity menuEntity = gson.fromJson(indexArr, MenuEntity.class);
                    if (menu.getMENU_KEY().equals(menuEntity.getId())) {
                        allMenulist.add(menuEntity);
                    }
                }
            }
        }
        return allMenulist;
    }

    public static void startAction(Context context, MenuEntity menuEntity) {
        switch (menuEntity.getId()) {
            case "shiwuduban"://事件督办
                EventSuperviseActivity.startAction(context);
                break;
            case "yidongxuncha"://移动巡查
                MobileWork2Activity.startAction(context);
                break;
            case "yujingfabu"://预警发布
                YuJingListActivity.startAction(context);
                break;
            case "shipinjiankong"://视频监控
                VideoListActivity.startAction(context);
                break;
            case "tongzhigonggao"://通知公告
                NotifyActivity.startAction(context);
//                VideoListActivity.startAction(context);
                break;
            case "hehujiance"://河湖监测
                RiverMonitorActivity.startAction(context);
                break;
            case "zuzhijigou"://组织信息
                OrganizeInfoActivity.startAction(context);
                break;
            case "shijianchuli"://事件处理
                EventListActivity.startAction(context);
                break;
            case "wentishangbao"://问题上报
                ProblemReportActivity.startAction((Activity) context);
                break;
            case "xunchaguanli"://巡查管理
                MobileWork2Activity.startAction(context);
                break;
            case "shiwuguanli"://事务管理
                EventSuperviseActivity.startAction(context);
                break;
            case "all"://更多
                Intent intent = new Intent();
                intent.setClass(context, MenuManageActivity.class);
                context.startActivity(intent);
                break;
        }
    }

    public static void showToast() {
        IToast.toast("正在努力开发中！");
    }

    private static Intent createIntent(Context context, String title, Class activity) {
        Intent intent = new Intent(context, activity);
        intent.putExtra("title", title);
        return intent;
    }
}
