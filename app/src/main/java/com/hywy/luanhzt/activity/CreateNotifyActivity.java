package com.hywy.luanhzt.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.cs.android.task.Task;
import com.cs.common.base.BaseToolbarActivity;
import com.cs.common.handler.BaseHandler_;
import com.cs.common.handler.SpringViewHandler;
import com.cs.common.listener.OnPostListenter;
import com.cs.common.utils.DateUtils;
import com.cs.common.utils.DialogTools;
import com.cs.common.utils.IToast;
import com.cs.common.utils.StringUtils;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.adapter.SpinnerListAdapter;
import com.hywy.luanhzt.app.App;
import com.hywy.luanhzt.entity.Adnm;
import com.hywy.luanhzt.entity.EventSupervise;
import com.hywy.luanhzt.entity.Notify;
import com.hywy.luanhzt.key.Key;
import com.hywy.luanhzt.task.GetEventTypeTask;
import com.hywy.luanhzt.task.GetNotifyTypeTask;
import com.hywy.luanhzt.task.PostNotifyTask;
import com.hywy.luanhzt.view.dialog.dialogplus.DialogPlus;
import com.hywy.luanhzt.view.dialog.dialogplus.ListHolder;
import com.hywy.luanhzt.view.dialog.dialogplus.OnItemClickListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 通知公告发布
 */
public class CreateNotifyActivity extends BaseToolbarActivity implements OnItemClickListener {
    @Bind(R.id.notify_name)
    TextView notifyName;
    @Bind(R.id.tv_type)
    TextView tvType;
    @Bind(R.id.tv_user)
    TextView tvUser;
    @Bind(R.id.notify_content)
    TextView tvContent;
    @Bind(R.id.btn)
    Button btn;

    private List<EventSupervise> events;
    private SpinnerListAdapter spinnerAdapter;
    private List<Adnm> adnms;
    private static final int request_code_create = 10091;

    public static void startAction(Activity activity) {
        Intent intent = new Intent(activity, CreateNotifyActivity.class);
        activity.startActivityForResult(intent, request_code_create);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notify);
        init();
        initData();
    }

    private void init() {
        setTitleBulider(new Bulider().title(getString(R.string.title_notify_create)).backicon(R.drawable.ic_arrow_back_white_24dp));

        spinnerAdapter = new SpinnerListAdapter(this);
        tvUser.setText(App.getInstance().getAccount().getNAME());
//        tvTime.setText(DateUtils.transforMillToDateInfo(System.currentTimeMillis() / 1000));
    }

    private void initData() {
        initEventList();
    }

    private void initEventList() {
        SpringViewHandler handler = new SpringViewHandler(this);
        handler.setBuilder(new BaseHandler_.Builder().isShowToast(false));
        Map<String, Object> params = new HashMap<>();
        handler.request(params, new GetEventTypeTask(this));
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                events = (List<EventSupervise>) result.get(Key.RESULT);
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {

            }
        });
    }

    @OnClick({R.id.layout_event})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_event:
                showEventList();
                break;
        }
    }

    @OnClick(R.id.btn)
    public void submit() {
        if ("".equals(notifyName.getText().toString().trim())) {
            IToast.toast("请输入信息标题");
            return;
        }

        if ("".equals(tvType.getText().toString().trim())) {
            IToast.toast("请选择信息类型");
            return;
        }

        if ("".equals(tvContent.getText().toString().trim())) {
            IToast.toast("请输入信息内容");
            return;
        }

        SpringViewHandler handler = new SpringViewHandler(this);
        Task task = new PostNotifyTask(this);
        try {
            handler.request(getParams(), task);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                IToast.toast("发布成功");
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {
//                IToast.toast(R.string.submit_fail);
            }
        });

    }

    /**
     * 请求参数
     */
    private Map<String, Object> getParams() throws JSONException {
        Map<String, Object> params = new HashMap<>();
        params.put("INFO", notifyName.getText().toString());
        String i = (String) tvType.getTag();
        params.put("INFOTYPE", i);
        params.put("INFOMAN", App.getInstance().getAccount().getUserId());
        params.put("TM", DateUtils.transforMillToDateInfo(System.currentTimeMillis() / 1000));
        params.put("INFOCONTENT", tvContent.getText().toString());
        return params;
    }

    private void showEventList() {
        SpringViewHandler handler = new SpringViewHandler(this);
        Task task = new GetNotifyTypeTask(this);
        Map<String, Object> params = new HashMap();
        handler.request(params, task);
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                List<Notify> list = (List<Notify>) result.get(Key.RESULT);
                if (StringUtils.isNotNullList(list)) {
                    List<String> types = new ArrayList<>();
                    for (Notify notify : list) {
                        types.add(notify.getINFOTYPE());
                    }
                    DialogTools.showListViewDialog(CreateNotifyActivity.this, "提示", types, new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            tvType.setTag(list.get(i).getINFOTYPE_ID());
                            tvType.setText(types.get(i));
                        }
                    });
                }
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {
//                IToast.toast(R.string.submit_fail);
            }
        });

    }

    private void showAddressList(List<Adnm> list) {
        spinnerAdapter.setList(list);
        showRadioDialog();
    }

    /**
     * 显示单选dialog
     */
    private void showRadioDialog() {
        DialogPlus dialogPlus = new DialogPlus.Builder(this)
                .setContentHolder(new ListHolder())
                .setAdapter(spinnerAdapter)
                .setGravity(DialogPlus.Gravity.CENTER)
                .setCancelable(true)
                .setInAnimation(0)
                .setOutAnimation(0)
                .setOnItemClickListener(this)
                .create();
        View view = dialogPlus.getHolderView();
        view.setBackgroundResource(R.drawable.bg_btn_default);
        dialogPlus.show();
    }

    @Override
    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
    }
}
