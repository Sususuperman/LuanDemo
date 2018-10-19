package com.hywy.luanhzt.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cs.android.task.Task;
import com.cs.common.base.BaseActivity;
import com.cs.common.handler.BaseHandler_;
import com.cs.common.handler.SpringViewHandler;
import com.cs.common.listener.OnPostListenter;
import com.cs.common.utils.DialogTools;
import com.cs.common.utils.IToast;
import com.cs.common.utils.StringUtils;
import com.cs.common.view.SoftKeyBoardSatusView;
import com.cs.common.view.percent.PercentLinearLayout;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.adapter.item.RiverItem;
import com.hywy.luanhzt.app.App;
import com.hywy.luanhzt.entity.River;
import com.hywy.luanhzt.key.Key;
import com.hywy.luanhzt.task.GetAppMenuTask;
import com.hywy.luanhzt.task.GetForgetPwdTask;
import com.hywy.luanhzt.task.GetRiverListTask;
import com.hywy.luanhzt.task.LoginTask;
import com.hywy.luanhzt.utils.SystemUtils;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;


//import com.tencent.bugly.crashreport.CrashReport;

/**
 * 登录面，验证用户的登录信息并获取用户信息
 *
 * @author charming
 */
public class LoginActivity extends BaseActivity implements OnClickListener,
        SoftKeyBoardSatusView.SoftkeyBoardListener {
    @Bind(R.id.login_status_view)
    SoftKeyBoardSatusView loginStatusView;
    @Bind(R.id.top_bg)
    PercentLinearLayout topBg;
    @Bind(R.id.username)
    EditText username;
    @Bind(R.id.userpass)
    EditText userpass;
    @Bind(R.id.login_btn)
    Button loginBtn;
    @Bind(R.id.bottom_bg)
    PercentLinearLayout bottomBg;
    @Bind(R.id.scrollView)
    ScrollView scrollView;
    @Bind(R.id.rb1)
    RadioButton btn1;
    @Bind(R.id.rb2)
    RadioButton btn2;


    //    @Bind(R.id.tv_http)
//    TextView tvHttp;
    //    @Bind(R.id.et_ip)
//    EditText etIp;
//    @Bind(R.id.et_port)
//    EditText etPort;
    @Bind(R.id.togglePwd)
    ToggleButton toggleButton;

    @Bind(R.id.forget_pwd)
    TextView tvForget;


    private String url;
    private Animation my_Translate;

    public static final int MAIN = 20;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_login);
        init();
        initListeners();
    }

    private void initListeners() {
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //如果选中，显示密码
                    userpass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //否则隐藏密码
                    userpass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    /**
     * 初始化页面
     */
    protected void init() {
        my_Translate = AnimationUtils.loadAnimation(this, R.anim.login_animation);
        bottomBg.setAnimation(my_Translate);
        loginStatusView.setSoftKeyBoardListener(this);

        RequestPermmisons();
//        username.setText("admin");
//        userpass.setText("1");
    }

    private void RequestPermmisons() {
        Acp.getInstance(this).request(new AcpOptions.Builder()
                        .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
                                , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {
                    }

                    @Override
                    public void onDenied(List<String> permissions) {

                    }
                });
    }

    @OnClick({R.id.login_btn, R.id.tv_settings, R.id.forget_pwd, R.id.shuoming})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:// 登录
                login();
                break;
            case R.id.shuoming:// 登录
                IToast.toast("在水利局WiFi环境下请使用内网");
                break;
            case R.id.tv_settings:
//                chooseHttp();
                startActivity(new Intent(this, IpSettingsActivity.class));
                break;
            case R.id.forget_pwd:
                DialogTools.showEditDialog(this, "请输入用户名", "提示", "", new DialogTools.OnEditListener() {
                    @Override
                    public void onEdit(String content) {
                        if (StringUtils.hasLength(content)) {
                            forget(content);
                        } else {
                            IToast.toast("请输入用户名！");
                        }
                    }
                });
                break;
        }
    }

    private void forget(String content) {
        if (!StringUtils.hasLength(App.getInstance().getApiURL())) {
            if (btn1.isChecked()) {
                App.getInstance().setApiURL("http://218.22.195.54:7007");
            } else if (btn2.isChecked()) {
                App.getInstance().setApiURL("http://10.34.97.20:7003");
            }
        }
        SpringViewHandler handler = new SpringViewHandler(this);
        Task task = new GetForgetPwdTask(this);
        Map<String, Object> params = new HashMap<>();
        params.put("USERNAME", content);
        handler.request(params, task);
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                String json = (String) result.get(Key.RESULT);
                JSONObject jo = null;
                try {
                    jo = new JSONObject(json);
                    String name = "", phone = "";
                    if (jo.has("NAME")) {
                        name = jo.getString("NAME");
                    }
                    if (jo.has("PHONE")) {
                        phone = jo.getString("PHONE");
                    }
                    showCallDialog(name, phone);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void OnPostFail(Map<String, Object> result) {

            }
        });
    }

    private void showCallDialog(String name, String phone) {
        DialogTools.showConfirmDialog(this, "提示", "请联系" + name + "进行密码重置，电话：" + phone, "立即联系", new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StringUtils.hasLength(phone)) {
                    Acp.getInstance(LoginActivity.this).request(new AcpOptions.Builder()
                                    .setPermissions(Manifest.permission.CALL_PHONE)
                                    .build(),
                            new AcpListener() {
                                @Override
                                public void onGranted() {
                                    SystemUtils.call(LoginActivity.this, phone);
                                }

                                @Override
                                public void onDenied(List<String> permissions) {

                                }
                            });
                } else {
                    IToast.toast("号码为空！");
                }
            }
        });
    }

    private void chooseHttp() {
        final List<String> list = new ArrayList<>();
        list.add("http");
        list.add("https");
        DialogTools.showListViewDialog(this, "提示", list, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                tvHttp.setText(list.get(i));
            }
        });
    }


    /**
     * 登录
     */
    private void login() {
        final String uname = username.getText().toString().trim();
        final String pass = userpass.getText().toString().trim();
//        final String ip = etIp.getText().toString().trim();
//        final String port = etPort.getText().toString().trim();
        if (uname.equals("")) {// 用户名为空
            Toast.makeText(this, R.string.name_empty, Toast.LENGTH_SHORT).show();
        } else if (pass.equals("")) {// 密码为空
            Toast.makeText(this, R.string.password_empty, Toast.LENGTH_SHORT).show();
        }
//        else if (ip.equals("")) {
//            Toast.makeText(this, "IP不能为空！", Toast.LENGTH_SHORT).show();
//        } else if (port.equals("")) {
//            Toast.makeText(this, "端口号不能为空！", Toast.LENGTH_SHORT).show();
//        }
        else {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("tm", System.currentTimeMillis());//当前时间戳
            params.put("KEYDATA", uname + ",fh," + pass + ",fh," + "1");//用户名,fh,密码,fh,code
            params.put("password", pass);
            login(params);
        }
    }

    /**
     * 登录
     *
     * @param params
     */
    private void login(Map<String, Object> params) {
        App app = (App) getApplication();
        if (!StringUtils.hasLength(app.getApiURL())) {
            if (btn1.isChecked()) {
                app.setApiURL("http://218.22.195.54:7007");
            } else if (btn2.isChecked()) {
                app.setApiURL("http://10.34.97.20:7007");
            }
//            app.setApiURL("http://218.22.195.54:7007");
        }
//        app.setApiURL(tvHttp.getText().toString() + "://" + etIp.getText().toString().trim() + ":" + etPort.getText().toString());
        // 有网络，网络登陆
        if (app.getNetworkMng().isCanConnect()) {
            SpringViewHandler handler = new SpringViewHandler(this);
            BaseHandler_.Builder builder = new BaseHandler_.Builder();
            builder.isLogin(true);
            builder.isShowToast(false);
            handler.request(params, new LoginTask(this));
            handler.setListener(new OnPostListenter() {
                @Override
                public void OnPostSuccess(Map<String, Object> result) {
                    hideSoftInput(username);
                    initAppMenu();
                }

                @Override
                public void OnPostFail(Map<String, Object> result) {
                    String failure = "";
                    if (result != null && result.containsKey("msg")) {
                        failure = result.get("msg").toString();
                    } else {
                        failure = getString(R.string.login_failure);
                    }
                    Toast.makeText(LoginActivity.this, failure, Toast.LENGTH_SHORT).show();
                    app.setApiURL("");
                }
            });
        } else {// 无网络，不能登陆，提示用户
            Toast.makeText(this, R.string.netWorkError, Toast.LENGTH_SHORT).show();
        }
    }

    private void initAppMenu() {
        SpringViewHandler handler = new SpringViewHandler(this);
        Map<String, Object> params = new HashMap<>();
        handler.setBuilder(new BaseHandler_.Builder().isShowToast(false).isWait(false));
        handler.request(params, new GetAppMenuTask(this));
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                MainActivity.startAction(LoginActivity.this);
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {
//                if (app.getMenu1() == null) {
//                    findViewById(R.id.bottom_bar1).setVisibility(View.GONE);
//                } else if (app.getMenu2() == null) {
//                    findViewById(R.id.bottom_bar2).setVisibility(View.GONE);
//                } else if (app.getMenu3() == null) {
//                    findViewById(R.id.bottom_bar3).setVisibility(View.GONE);
//                }

            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(this, ClearActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("isFinish", ClearActivity.wNoLoginEnd);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void keyBoardStatus(int w, int h, int oldw, int oldh) {

    }

    @Override
    public void keyBoardVisable(int move) {
        loginBtn.getScrollY();
        Message message = new Message();
        message.what = WHAT_SCROLL;
        message.obj = move;
        handler.sendMessageDelayed(message, 100);
    }

    @Override
    public void keyBoardInvisable(int move) {
        handler.sendEmptyMessageDelayed(WHAT_BTN_VISABEL, 200);
    }


    final int WHAT_SCROLL = 0, WHAT_BTN_VISABEL = WHAT_SCROLL + 1;
    private Handler handler = new Handler() {

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case WHAT_SCROLL:
                    int move = (Integer) msg.obj;
                    scrollView.smoothScrollBy(0, move);
                    break;
                case WHAT_BTN_VISABEL:
                    break;
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (isCancel(resultCode)) {
            switch (requestCode) {
                case MAIN://表示更新成功
                    break;
                case -1://表示更新失败
                    break;
                case request_activity_finish://表示更新失败
                    MainActivity.startAction(LoginActivity.this);
                    break;
            }
        }
    }


    private void startCountDownTime(long time) {
        /**
         * 最简单的倒计时类，实现了官方的CountDownTimer类（没有特殊要求的话可以使用）
         * 即使退出activity，倒计时还能进行，因为是创建了后台的线程。
         * 有onTick，onFinsh、cancel和start方法
         */
        CountDownTimer timer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //每隔countDownInterval秒会回调一次onTick()方法
                Log.d("CountDownTimer", "onTick  " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                Log.d("CountDownTimer", "onFinish -- 倒计时结束");
            }
        };
        timer.start();// 开始计时
        //timer.cancel(); // 取消
    }
}
