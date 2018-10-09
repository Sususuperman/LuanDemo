package com.hywy.luanhzt.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs.android.task.Task;
import com.cs.common.base.BaseToolbarActivity;
import com.cs.common.handler.BaseHandler_;
import com.cs.common.handler.SpringViewHandler;
import com.cs.common.listener.OnPostListenter;
import com.cs.common.utils.DateUtils;
import com.cs.common.utils.IToast;
import com.cs.common.utils.StringUtils;
import com.google.gson.Gson;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.action.RxAction;
import com.hywy.luanhzt.adapter.SpinnerListAdapter;
import com.hywy.luanhzt.app.App;
import com.hywy.luanhzt.entity.Adnm;
import com.hywy.luanhzt.entity.EventSupervise;
import com.hywy.luanhzt.entity.UserTree;
import com.hywy.luanhzt.entity.WarnType;
import com.hywy.luanhzt.entity.action.AdcdMultiAction;
import com.hywy.luanhzt.key.Key;
import com.hywy.luanhzt.task.GetAdnmListTask;
import com.hywy.luanhzt.task.GetEventTypeTask;
import com.hywy.luanhzt.task.GetWarnTypeTask;
import com.hywy.luanhzt.task.PostYujingTask;
import com.hywy.luanhzt.view.dialog.dialogplus.DialogPlus;
import com.hywy.luanhzt.view.dialog.dialogplus.ListHolder;
import com.hywy.luanhzt.view.dialog.dialogplus.OnItemClickListener;
import com.superman.treeview.treelist.Node;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import rx.functions.Action1;

public class YuJingActivity extends BaseToolbarActivity implements OnItemClickListener {
    @Bind(R.id.yj_name)
    TextView yjName;
    @Bind(R.id.tv_user)
    TextView tvUser;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.tv_event)
    TextView tvEvent;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.yj_content)
    TextView tvContent;
    @Bind(R.id.iv_add)
    ImageView ivAdd;
    @Bind(R.id.btn)
    Button btn;

    private List<WarnType> warnTypes;
    private SpinnerListAdapter spinnerAdapter;
    private List<Adnm> adnms;
    private List<Node> nodes;

    public static void startAction(Context activity) {
        Intent intent = new Intent(activity, YuJingActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yu_jing);
        init();
        initData();
        initListeners();
    }

    private void init() {
        setTitleBulider(new Bulider().title(getString(R.string.title_yujing)).backicon(R.drawable.ic_arrow_back_white_24dp));

        spinnerAdapter = new SpinnerListAdapter(this);
        tvUser.setText(App.getInstance().getAccount().getNAME());
        tvTime.setText(DateUtils.transforMillToDateInfo(System.currentTimeMillis() / 1000));
    }

    private void initData() {
//        tvName.setText(plan.getREACH_NAME());
//        tvUser.setText("超级管理员");//当前登录人
//        tvTime.setText(DateUtils.transforMillToDate(System.currentTimeMillis()));
        initEventList();
        initAddressList();
    }

    private void initAddressList() {
        SpringViewHandler handler = new SpringViewHandler(this);
        handler.setBuilder(new BaseHandler_.Builder().isShowToast(false));
        Map<String, Object> params = new HashMap<>();
        handler.request(params, new GetAdnmListTask(this));
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                adnms = (List<Adnm>) result.get(Key.RESULT);
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {

            }
        });
    }

    private void initEventList() {
        SpringViewHandler handler = new SpringViewHandler(this);
        handler.setBuilder(new BaseHandler_.Builder().isShowToast(false));
        Map<String, Object> params = new HashMap<>();
        handler.request(params, new GetWarnTypeTask(this));
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                warnTypes = (List<WarnType>) result.get(Key.RESULT);
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {

            }
        });
    }

    @OnClick({R.id.layout_event, R.id.layout_address})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_event:
                showEventList(warnTypes);
                break;
            case R.id.layout_address:
                UserTree2Activity.startAction(this, "");
//                showAddressList(adnms);
                break;
        }
    }

    private void initListeners() {
        mRxManager.on(RxAction.YUJING_CHOOSE_ADCD, new Action1<AdcdMultiAction>() {
            @Override
            public void call(AdcdMultiAction adcdMultiAction) {
                List<Node> selectedNode = adcdMultiAction.nodes;
                nodes = selectedNode;
                StringBuilder sbId = new StringBuilder();
                StringBuilder sbName = new StringBuilder();
                if (StringUtils.hasLength(App.getInstance().getAccount().getADCD())) {
                    sbId.append(App.getInstance().getAccount().getADCD() + ",");
                }
                if (StringUtils.hasLength(App.getInstance().getAccount().getADNM())) {
                    sbName.append(App.getInstance().getAccount().getADNM() + ",");
                }

                for (Node n : selectedNode) {
                    sbId.append(n.getId() + ",");
                    sbName.append(n.getName() + ",");
                }
                tvAddress.setTag(sbId.deleteCharAt(sbId.length() - 1));
                tvAddress.setText(sbName.deleteCharAt(sbName.length() - 1));
            }
        });
    }


    @OnClick(R.id.btn)
    public void submit() {
        if ("".equals(yjName.getText().toString().trim())) {
            IToast.toast("请输入预警主题");
            return;
        }

        if ("".equals(tvEvent.getText().toString().trim())) {
            IToast.toast("请选择预警类型");
            return;
        }

        if ("".equals(tvAddress.getText().toString().trim())) {
            IToast.toast("请选择发布对象");
            return;
        }

        if ("".equals(tvContent.getText().toString().trim())) {
            IToast.toast("请输入预警详情");
            return;
        }

        SpringViewHandler handler = new SpringViewHandler(this);
        Task task = new PostYujingTask(this);
        try {
            handler.request(getParams(), task);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                IToast.toast("发布成功");
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
        JSONObject obj = new JSONObject();
        obj.put("WARNTHEME", yjName.getText().toString());
        obj.put("WARNPEOPLE", App.getInstance().getAccount().getUserId());
        obj.put("TIME", tvTime.getText().toString());
        obj.put("WARNCONTENT", tvContent.getText().toString());


        WarnType w = (WarnType) tvEvent.getTag();
        obj.put("WARNTYPECODE", w.getWARNTYPECODE());
        StringBuilder a = (StringBuilder) tvAddress.getTag();
        obj.put("WARNCITY", a.toString());//发布对象
        Map<String, Object> params = new HashMap<>();
        params.put("data", obj.toString());
        return params;
    }

    private void showEventList(List<WarnType> list) {
        spinnerAdapter.setList(list);
        showRadioDialog();
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
        Object object = spinnerAdapter.getItem(position);
        if (object instanceof WarnType) {
            WarnType event = (WarnType) object;
            tvEvent.setTag(event);
            tvEvent.setText(event.getWARNTYPEDES());
        }
        dialog.dismiss();
    }
}
