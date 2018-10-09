package com.hywy.luanhzt.task;

import android.content.Context;
import android.util.Log;

import com.cs.common.Key;
import com.cs.common.utils.HttpUtils;
import com.google.gson.Gson;
import com.hywy.luanhzt.Const;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.app.App;
import com.hywy.luanhzt.dao.AccountDao;
import com.hywy.luanhzt.entity.Account;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * 个人头像更换
 *
 * @author Superman
 */
public class PostReplaceHeadTask extends ValidSourceTask {
    public PostReplaceHeadTask(Context context) {
        super(context, null);
    }

    @Override
    protected Map<String, Object> sendData(Map<String, Object> params) throws Exception {
        App app = (App) context.getApplicationContext();
        String content = HttpUtils.postString(HttpUrl.getUrl(HttpUrl.RMS_APP_USER_APPUPLOADFILE), params);
        JSONObject jo = new JSONObject(content);
        Map<String, Object> result = new HashMap<String, Object>();
        String path = "";
        if (jo.has("msg")) {
            if (jo.getString("msg").equals(Const.SUCCESS)) {// 成功
                if (!jo.isNull("path")) {
                    path = jo.getString("path");
                }
                result.put("msg", jo.getString("msg"));
                result.put(Key.RESULT, path);
            } else {
                result.put("msg", jo.getString("msg"));// 失败原因
            }
        }
        return result;
    }
}
