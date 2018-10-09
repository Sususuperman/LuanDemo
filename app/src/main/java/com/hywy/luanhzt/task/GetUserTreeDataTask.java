package com.hywy.luanhzt.task;

import android.content.Context;

import com.cs.common.utils.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hywy.luanhzt.Const;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.app.App;
import com.hywy.luanhzt.dao.AccountDao;
import com.hywy.luanhzt.entity.Account;
import com.hywy.luanhzt.entity.Adnm;
import com.hywy.luanhzt.entity.UserTree;
import com.hywy.luanhzt.key.Key;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 根据行政区划和role_id获取用户树状结构
 *
 * @author james
 */
public class GetUserTreeDataTask extends ValidSourceTask {
    public GetUserTreeDataTask(Context context, Map<String, Object> params) {
        super(context, params);
    }

    public GetUserTreeDataTask(Context context) {
        this(context, null);
    }

    @Override
    protected Map<String, Object> sendData(Map<String, Object> params)
            throws Exception {
        String content = HttpUtils.getString(HttpUrl.getUrl(HttpUrl.RMS_APP_PERMISSIONS_USERZTREE), params);
        JSONObject jo = new JSONObject(content);
        Map<String, Object> result = new HashMap<String, Object>();
        if (jo.has("zTreeNodes")) {
            if (jo.getString("zTreeNodes") != null && !jo.getString("zTreeNodes").equals("")) {// 成功
                String zTreeNodes = (String) jo.get("zTreeNodes");
                Type type = new TypeToken<List<UserTree>>() {
                }.getType();
                List<UserTree> list = new Gson().fromJson(zTreeNodes, type);
                result.put(Key.RESULT, list);
                result.put("msg", "success");
            } else {// 用户名或密码错误
                result.put("msg", "err");// 失败原因
            }
        } else {
            result.put("msg", "err");
        }

        return result;
    }
}
