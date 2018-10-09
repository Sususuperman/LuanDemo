package com.hywy.luanhzt;

import com.cs.common.utils.Logger;
import com.hywy.luanhzt.app.App;

/**
 * @author Superman
 */

public class HttpUrl {

    /**
     * 六安河长环境
     */
    public static String getUrl(String url) {
        return App.getInstance().getApiURL() + url;
    }

    /**
     * 一张图arcgis加载图层URL
     */
    public static String getLayerUrl(String type) {
        return "http://218.22.195.54:7011/arcgis/rest/services/liuandjyxt/MapServer/" + type;
    }

    /**
     * 六安市影像图层
     */
    public static String getLuanImageServer() {
        return "http://218.22.195.54:7011/arcgis/rest/services/lamaplv18/MapServer";
    }

    /**
     * 全球影像图层
     */
    public static String getEarthServer() {
        return "http://server.arcgisonline.com/arcgis/rest/services/ESRI_Imagery_World_2D/MapServer";
    }

    /***************登录接口************/
    public static final String RMS_LOGIN_LOGIN = "/RMS/login_login";
    /***************首页菜单界面************/
    public static final String RMS_APP_MENU_APP_MENULIST = "/RMS/app-menu/app_menuList";
    /**
     * 获取巡查日志
     */
    public static final String RMS_APP_PATROL_LOG_LIST = "/RMS/app-patrol_log/applist";
    /**
     * 获取巡查日志~我的河道详情~日志
     */
    public static final String RMS_APP_PATROL_LOG_GETALLLIST = "/RMS/app-patrol_log/getAlllist";
    /**
     * 水雨情监测
     */
    public static final String RMS_APP_PPTN_R_FINDSTPPTNRS = "/RMS/app_pptn_r/appPptnlist";

    /**
     * 水库水位监测
     */
    public static final String RMS_APP_RSVR_R_RSAPPLIST = "/RMS/app_rsvr_r/rsapplist";


    /**
     * 水质监测
     */
    public static final String RMS_APP_AWQMD_WQLIST = "/RMS/app-awqmd/wqlist";

    /**
     * 河道水文
     */
    public static final String RMS_APP_APP_STRIVER_STLIST = "/RMS/app-striver/stlist";

    /**
     * 取水口
     */
    public static final String RMS_APP_INTAKE_R_FINDINTAKES_APP = "/RMS/app_intake_r/findIntakes_app";
    /**
     * 水雨情日月时查询
     */
    public static final String RMS_APP_PPTN_R_FXLIST = "/RMS/app_pptn_r/appTjList";

    /**
     * 河道水文日月时查询
     */
    public static final String RMS_APP_STRIVER_FXLIST = "/RMS/app-striver/appTjList";

    /**
     * 水库水文日月时查询
     */
    public static final String RMS_APP_RSVR_R_FXLIST = "/RMS/app_rsvr_r/appTjList";
    /**
     * 水质日月时查询
     */
    public static final String RMS_APP_AWQMD_FXLIST = "/RMS/app-awqmd/appTjList";

    /**
     * 取水口日月时查询
     */
    public static final String RMS_APP_INTAKE_R_GETYDM_APP = "/RMS/app_intake_r/appTjList";
    /**
     * 一张图菜单接口
     */
    public static final String RMS_APP_ROLESYSTEM_APPMAPZTREEQX = "/RMS/app_roleSystem/appmapztreeqx";
    /**
     * 一张图行政区划图层接口
     */
    public static final String RMS_APP_MAP_FINDMAPURL = "/RMS/app-Map/findMapUrl";
    /**
     * 事物督办列表
     */
    public static final String RMS_APP_EVENT_CREATE_APPLIST = "/RMS/app-event_create/applist";

    /**
     * 创建事件
     */
    public static final String RMS_APP_EVENT_PROCESS_APPDONEXT = "/RMS/app-event_process/AppdoNext";

    /**
     * 我的河道列表
     */
    public static final String RMS_APP_RIVERWAY_RIVERWAYLISTTREE = "/RMS/app_riverway/riverwaylisttree";
    /**
     * 我的直属河道列表
     */
    public static final String RMS_APP_RIVERWAY_RIVERWAYLISTTREEAPP = "/RMS/app_riverway/riverwaylisttreeapp";
    /**
     * 河道详情
     */
    public static final String RMS_APP_RIVERWAY_RIVERWAYLIST = "/RMS/app_riverway/riverwaylist";
    /**
     * 移动巡查·选择巡查计划
     */
    public static final String RMS_APP_PATROL_PLAN_GETPLANLIST = "/RMS/app-patrol_plan/getPlanList";

    /**
     * 移动巡查·巡查路线上报
     */
    public static final String RMS_APP_PATROL_LINE_APPSAVE = "/RMS/app-patrol_line/appSave";
    /**
     * 移动巡查.新建巡查日志
     */
    public static final String RMS_APP_TROL_LOG_APPSAVE = "/RMS/app-patrol_log/appSave";


    /**
     * 查询所有河段名称
     */
    public static final String RMS_APP_PATROL_PLAN_FINDREACHBASE = "/RMS/app-patrol_plan/findReachBase";
    /**
     * 查询市和区县行政区划
     */
    public static final String RMS_APP_ERMISSIONS_GETQXLIST = "/RMS/app-permissions/getQxList";

    /**
     * 事件类型
     */
    public static final String RMS_APP_EVENTANALY_FINDEVENTTYPE = "/RMS/app-eventAnaly/findEVENTTYPE";

    /**
     * 移动巡查~问题上报
     */
    public static final String RMS_APP_APP_PATROL_EVENT_APPSAVE = "/RMS/app-patrol_event/appSave";
    /**
     * 事件上报~上报对象列表
     */
    public static final String RMS_APP_PERMISSIONS_FINDNT = "/RMS/app-permissions/findNt";


    /**
     * 通知公告
     */
    public static final String RMS_APP_TZGG_FINDTZGGS = "/RMS/app_tzgg/findTzggs";

    /**
     * 预警发布
     */
    public static final String RMS_APP_WARN_R_APPSAVE = "/RMS/app_warn_r/appSave";


    /**
     * 预警列表
     */
    public static final String RMS_APP_WARN_R_APPFINDALLLIST = "/RMS/app_warn_r/AppfindAllList";

    /**
     * 我的上报列表
     */
    public static final String RMS_APP_PATROL_EVENT_USERLIST = "/RMS/app-patrol_event/Userlist";

    /**
     * 我的河道~详情~河道记录~事件列表
     */
    public static final String RMS_APP_PATROL_EVENT_GETALLLIST = "/RMS/app-patrol_event/getAllList";

    /**
     * 获取其相关用户信息(河长办)
     */
    public static final String RMS_APP_TXL_LIST = "/RMS/app-Txl/list";
    /**
     * 获取其相关用户信息(河长)
     */
    public static final String RMS_APP_TXL_HZLIST = "/RMS/app-Txl/hzlist";
    /**
     * 获取其相关用户信息(协作单位)
     */
    public static final String RMS_APP_TXL_LISTMAIN = "/RMS/app-Txl/listMain";
    /**
     * 获取留言簿列表
     */
    public static final String RMS_APP_TXL_LISTLY = "/RMS/app-Txl/listLy";
    /**
     * 获取留言簿详情列表
     */
    public static final String RMS_APP_TXL_LISTLYXX = "/RMS/app-Txl/listLyxx";
    /**
     * 留言提交
     */
    public static final String RMS_APP_TXL_APPSAVE = "/RMS/app-Txl/appSave";

    /**
     * 视频点查询
     */
    public static final String RMS_APP_VIDEO_VIDEOLIST = "/RMS/app-video/videolist";

    /**
     * 版本更新接口
     */
    public static final String RMS_APP_UPDATE_APPLIST = "/RMS/app-update/Applist";
    /**
     * 移动巡查选择河段列表接口
     */
    public static final String RMS_APP_ATROL_PLAN_FINDREACHBASE = "/RMS/app-patrol_plan/findReachBase";
    /**
     * 下级区县列表接口
     */
    public static final String RMS_APP_APP_ADCD_FINDADCDBYUSER = "/RMS/app_adcd/findADCDByUser";

    /**
     * 查询所有用户
     */
    public static final String RMS_APP_PERMISSIONS_GETPERLIST = "/RMS/app-permissions/getPerList";
    /**
     * 结束事物
     */
    public static final String RMS_APP_EVENT_PROCESS_DOEND = "/RMS/app-event_process/doEnd";

    /**
     * 一张图~查询测站信息
     */
    public static final String RMS_APP_MAP_LISTSTION = "/RMS/app-Map/listStion";

    /**
     * 事件处理~催办
     */
    public static final String RMS_APP_EVENT_PROCESS_SENDMSG = "/RMS/app-event_process/sendMsg";

    /**
     * 我的~意见反馈
     */
    public static final String RMS_APP_QUESTION_APPSAVE = "/RMS/app-question/appSave";

    /**
     * 我的~个人头像更换
     */
    public static final String RMS_APP_USER_APPUPLOADFILE = "/RMS/app_user/appUploadFile";

    /**
     * 用户树状结构
     */
    public static final String RMS_APP_PERMISSIONS_USERZTREE = "/RMS/app-permissions/userZtree";
    /**
     * 行政区划树状结构
     */
    public static final String RMS_APP_PERMISSIONS_ADCDZTREEAPP = "/RMS/app-permissions/adcdZtreeApp";
    /**
     * 验证旧密码是否正确
     */
    public static final String RMS_APP_USER_CONTRAST = "/RMS/app_user/contrast";
    /**
     * 修改密码
     */
    public static final String RMS_APP_USER_EDITPASS = "/RMS/app_user/editPass";

    /**
     * 我的河道 九大河流
     */
    public static final String RMS_APP_PERMISSIONS_RIVERWAYLISTTREE = "/RMS/app-permissions/riverwaylisttree";
    /**
     * 新闻列表
     */
    public static final String RMS_APP_IMG_LIST = "/RMS/app_img/list";
    /**
     * 事务管理~事务列表
     */
    public static final String RMS_APP_EVENT_CREATE_GETALLLIST = "/RMS/app-event_create/getAllList";
    /**
     * 事务管理~事物立项
     */
    public static final String RMS_APP_EVENT_CREATE_APPSAVES = "/RMS/app-event_create/appSaves";
    /**
     * 事务管理~事物立项~相关水质站
     */
    public static final String RMS_APP_ST_STBPRP_B_FINDSTCD = "/RMS/app_st_stbprp_b/findSTCD";
    /**
     * 事务管理~事物立项~相关水文站
     */
    public static final String RMS_APP_ST_STBPRP_R_FINDSTCD = "/RMS/app_st_stbprp_r/findSTCD";
    /**
     * 事务管理~未处理
     */
    public static final String RMS_APP_EVENT_PROCESS_APP_GETALLLIST = "/RMS/app-event_process/app-getAllList";
    /* 通知公告发布
  */
    public static final String RMS_APP_TZGG_SAVE = "/RMS/app_tzgg/save";

    /* 通知公告类型查询
     */
    public static final String RMS_APP_TZGG_FINDINFOTYPE = "/RMS/app_tzgg/findinfotype";

    /* 预警发布预警类型查询
         */
    public static final String RMS_APP_WARN_R_APPLISTTYPE = "/RMS/app_warn_r/Applisttype";

    /* 我的河道详情~投诉列表
        */
    public static final String RMS_APP_COMPLAIN_NEW_GETALLLIST = "/RMS/app-complain_new/getAllList";

    /*********************************************************************************************************/
    public static String getServiceUrl(String url) {
        return App.getInstance().getApiURL() + url;
    }

//    /**
//     * 正式版本
//     */
//    public static String getUrl(String url) {
//        return App.getInstance().getInsureApiURL() + url;
//    }
//
//    public static String getServiceUrl(String url) {
//        return App.getInstance().getServiceApiURL() + url;
//    }

    /**
     * 更新地址
     *
     * @return
     */
    public static String getUpgradeUrl() {
        return HttpUrl.getUrl(RMS_APP_UPDATE_APPLIST);
    }


    /**
     * 文件下载地址
     *
     * @param url
     * @return
     */
    public static String getFileDownloadUrl(String url) {
//        if(Logger.DEBUG_STATE){
//            return getUrl(url);
//        }else {
//            return App.getInstence().getTablesURL() + url;
//        }
        return url;
    }


}
