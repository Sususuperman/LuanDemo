package com.hywy.luanhzt.task;

import android.content.Context;

import com.cs.common.utils.HttpUtils;
import com.hywy.luanhzt.Const;
import com.hywy.luanhzt.HttpUrl;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by weifei on 17/6/14.
 */

public class PostVerifyPassTask extends ValidSourceTask {

    public PostVerifyPassTask(Context context) {
        super(context, null);
    }

    @Override
    protected Map<String, Object> sendData(Map<String, Object> params) throws Exception {

        String content = HttpUtils.postString(HttpUrl.getServiceUrl(HttpUrl.RMS_APP_USER_CONTRAST),params);

        JSONObject jo = new JSONObject(content);
        Map<String, Object> result = new HashMap<String, Object>();

        if (jo.getString("msg") .equals(Const.SUCCESS) )  {// 成功
            result.put("msg",jo.getString("msg"));
        }else{
            result.put("msg",jo.getString("msg"));
        }

        return result;
    }
}
