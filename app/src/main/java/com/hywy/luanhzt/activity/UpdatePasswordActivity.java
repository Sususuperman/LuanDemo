package com.hywy.luanhzt.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.cs.android.task.Task;
import com.cs.common.base.BaseToolbarActivity;
import com.cs.common.handler.BaseHandler_;
import com.cs.common.handler.SpringViewHandler;
import com.cs.common.listener.OnPostListenter;
import com.cs.common.utils.IToast;
import com.cs.common.utils.ViewUtils;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.app.App;
import com.hywy.luanhzt.entity.Account;
import com.hywy.luanhzt.task.PostRestPasswordTask;
import com.hywy.luanhzt.task.PostVerifyPassTask;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

public class UpdatePasswordActivity extends BaseToolbarActivity {
    @Bind(R.id.old_pass)
    EditText old_pass;//旧密码
    @Bind(R.id.new_userpass_u)
    EditText user_new;//新密码
    @Bind(R.id.show_pdw1)
    ImageView show_new;
    @Bind(R.id.userpass2_u)
    EditText user_con;//确认密码
    @Bind(R.id.show_pdw2)
    ImageView show_con;
    private Account account;

    public static void startAction(Activity context) {
        Intent intent = new Intent(context, UpdatePasswordActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        init();
    }

    /**
     * 初始化
     */
    public void init() {
        setTitleBulider(new Bulider().backicon(R.drawable.ic_arrow_back_white_24dp).title("设置新密码"));
//        show_new.setOnTouchListener(new ShowOrHidePassWordUtils(user_new));
//        show_con.setOnTouchListener(new ShowOrHidePassWordUtils(user_con));

        account = App.getInstence().getAccount();

    }

    @OnClick(R.id.submit)
    public void onClickSubmit() {
        judge();
    }

    private void judge() {
        String old_pas = old_pass.getText().toString().trim();
        String new_pas = user_new.getText().toString().trim();
        String con_pas = user_con.getText().toString().trim();
        if (old_pas.length() == 0) {
            IToast.toast("请输入旧密码");
            old_pass.requestFocus();
            return;
        }
        if (new_pas.length() < 8) {
            IToast.toast("密码长度不能小于8位");
            user_new.requestFocus();
            return;
        }

        if (!new_pas.equals(con_pas)) {
            user_con.requestFocus();
            IToast.toast("两次密码不一致");
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("PASSWORD", old_pas);
        params.put("USER_ID",account.getUserId());
        verify(params);
    }
 /*
   *验证旧密码
     */

    private void verify(Map<String, Object> params) {
        SpringViewHandler handler = new SpringViewHandler(this);
        handler.setBuilder(new BaseHandler_.Builder().isLogin(true));
        Task task = new PostVerifyPassTask(this);
        handler.request(params, task);
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                String new_pas = user_new.getText().toString().trim();
                String con_pas = user_con.getText().toString().trim();

//                if (new_pas.length() < 8) {
//                    IToast.toast("密码长度不能小于8位");
//                    user_new.requestFocus();
//                    return;
//                }

                if (!new_pas.equals(con_pas)) {
                    user_con.requestFocus();
                    IToast.toast("两次密码不一致");
                    return;
                }
                Map<String, Object> params = new HashMap<>();
                params.put("PASSWORD", new_pas);
                params.put("USER_ID",account.getUserId());
                submit(params);
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {
                String msg = result.containsKey("msg") ? result.get("msg").toString() : "旧密码错误";
                IToast.toast(msg);
            }
        });
    }
      /*
      *提交重置密码
     */

    private void submit(Map<String, Object> params) {
        SpringViewHandler handler = new SpringViewHandler(this);
        handler.setBuilder(new BaseHandler_.Builder().isLogin(true));
        Task task = new PostRestPasswordTask(this);
        handler.request(params, task);
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                IToast.toast("修改成功");
                finish();
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {
                String msg = result.containsKey("msg") ? result.get("msg").toString() : "修改失败";
                IToast.toast(msg);
            }
        });
    }

}
