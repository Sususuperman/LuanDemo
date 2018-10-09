package com.hywy.luanhzt.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cs.common.base.BaseToolbarActivity;
import com.cs.common.handler.SpringViewHandler;
import com.cs.common.listener.OnPostListenter;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.activity.fragment.RVFragment1;
import com.hywy.luanhzt.activity.fragment.RVFragment2;
import com.hywy.luanhzt.activity.fragment.RVFragment3;
import com.hywy.luanhzt.activity.fragment.RVFragment4;
import com.hywy.luanhzt.entity.River;
import com.hywy.luanhzt.entity.RiverDetails;
import com.hywy.luanhzt.entity.RiverFile;
import com.hywy.luanhzt.key.Key;
import com.hywy.luanhzt.task.GetRiverDetailsTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * 河道详情
 **/
public class RiverDetailsActivity extends BaseToolbarActivity implements View.OnClickListener {

    @Bind(R.id.rb_basic)
    RadioButton rb_basic;
    @Bind(R.id.rb_water)
    RadioButton rb_water;
    @Bind(R.id.rb_one)
    RadioButton rb_one;
    @Bind(R.id.rb_record)
    RadioButton rb_record;

    @Bind(R.id.fragment_contacts)
    FrameLayout fragment_contacts;
    @Bind(R.id.radio_group)
    RadioGroup radioGroup;

    private RVFragment1 rvFragment1;
    private RVFragment2 rvFragment2;
    private RVFragment3 rvFragment3;
    private RVFragment4 rvFragment4;
    private River river;
    private RiverDetails rd;
    private RiverDetails.HDBaseBean hdBaseBean;
    private ArrayList<RiverFile> yhycListBean;
    private RiverDetails.SZBaseBean szBaseBean;
    private ArrayList<RiverDetails.XCJHListBean> xcjhListBean;
    private String reach_code;

    public static void startAction(Context context, River river) {
        Intent intent = new Intent(context, RiverDetailsActivity.class);
        intent.putExtra("river", river);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_river_details);
        init();
        initData();
        initListeners();
    }

    private void initData() {
        SpringViewHandler handler = new SpringViewHandler(this);
        Map<String, Object> params = new HashMap<>();
        params.put("REACH_CODE", river.getREACH_CODE());
        handler.request(params, new GetRiverDetailsTask(this));
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                rd = (RiverDetails) result.get(Key.RESULT);
                if (rd.getHDBase() != null && rd.getHDBase().size() > 0) {
                    hdBaseBean = rd.getHDBase().get(0);
                    hdBaseBean.setREACH_NAME(river.getREACH_NAME());
                    reach_code = river.getREACH_CODE();
                }
                if (rd.getYHYCList() != null && rd.getYHYCList().size() > 0) {
                    yhycListBean = (ArrayList<RiverFile>) rd.getYHYCList();
                }
                if (rd.getSZBase() != null && rd.getSZBase().size() > 0) {
                    szBaseBean = rd.getSZBase().get(0);
                    szBaseBean.setREACH_NAME(river.getREACH_NAME());
                }
                if (rd.getXCJHList() != null && rd.getXCJHList().size() > 0) {
                    xcjhListBean = (ArrayList<RiverDetails.XCJHListBean>) rd.getXCJHList();
                }
                setCurSelTab(0);
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {

            }
        });
    }


    private void init() {
        river = getIntent().getParcelableExtra("river");
        this.setTitleBulider(new Bulider().backicon(R.drawable.ic_arrow_back_white_24dp).title(river.getREACH_NAME()));
    }

    private void initListeners() {
        rb_basic.setOnClickListener(this);
        rb_water.setOnClickListener(this);
        rb_one.setOnClickListener(this);
        rb_record.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rb_basic:
                setCurSelTab(0);
                break;
            case R.id.rb_water:
                setCurSelTab(1);
                break;
            case R.id.rb_one:
                setCurSelTab(2);
                break;
            case R.id.rb_record:
                setCurSelTab(3);
                break;
        }
    }

    private void setCurSelTab(int nIndex) {
        FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
        hideAllFragment(fm);
        switch (nIndex) {
            case 0:
                if (null == rvFragment1) {
                    rvFragment1 = RVFragment1.newInstance(hdBaseBean);
                    fm.add(R.id.fragment_contacts, rvFragment1);
                } else {
                    fm.show(rvFragment1);
                }
                rb_basic.setChecked(true);
                break;
            case 1:
                if (null == rvFragment2) {
                    rvFragment2 = RVFragment2.newInstance(szBaseBean);
                    fm.add(R.id.fragment_contacts, rvFragment2);
                } else {
                    fm.show(rvFragment2);
                }
                rb_water.setChecked(true);
                break;
            case 2:
                if (null == rvFragment3) {
                    rvFragment3 = RVFragment3.newInstance(yhycListBean);
                    fm.add(R.id.fragment_contacts, rvFragment3);
                } else {
                    fm.show(rvFragment3);
                }
                rb_one.setChecked(true);
                break;
            case 3:
                if (null == rvFragment4) {
                    rvFragment4 = RVFragment4.newInstance(xcjhListBean, reach_code);
                    fm.add(R.id.fragment_contacts, rvFragment4);
                } else {
                    fm.show(rvFragment4);
                }
                rb_record.setChecked(true);
                break;
        }
        fm.commit();
    }

    private void hideAllFragment(FragmentTransaction transaction) {
        if (rvFragment1 != null) {
            transaction.hide(rvFragment1);
        }
        if (rvFragment3 != null) {
            transaction.hide(rvFragment3);
        }
        if (rvFragment2 != null) {
            transaction.hide(rvFragment2);
        }
        if (rvFragment4 != null) {
            transaction.hide(rvFragment4);
        }
    }
}
