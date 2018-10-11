package com.hywy.luanhzt.task;

import android.content.Context;

import com.cs.common.utils.HttpUtils;
import com.cs.common.utils.StringUtils;
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
public class GetForgetPwdTask extends ValidSourceTask {
    public GetForgetPwdTask(Context context, Map<String, Object> params) {
        super(context, params);
    }

    public GetForgetPwdTask(Context context) {
        this(context, null);
    }

    @Override
    protected Map<String, Object> sendData(Map<String, Object> params)
            throws Exception {
        String content = HttpUtils.getString(HttpUrl.getUrl(HttpUrl.RMS_APP_LOGIN_R_FINDPASSWORD), params);
        JSONObject jo = new JSONObject(content);
        Map<String, Object> result = new HashMap<String, Object>();
        if (jo.has("msg")) {
            String msg = jo.getString("msg");
            if (StringUtils.hasLength(msg)) {
                if (msg.equals(Const.SUCCESS)) {// 成功
                    JSONObject data = jo.getJSONObject("Data");
                    result.put(Key.RESULT, data.toString());
                    result.put("msg", Const.SUCCESS);
                } else {
                    result.put("msg", "err");// 失败原因
                }
            }
        } else {
            result.put("msg", "err");
        }

        return result;
    }
}
