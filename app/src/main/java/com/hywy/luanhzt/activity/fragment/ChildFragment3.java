package com.hywy.luanhzt.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs.common.base.BaseFragment;
import com.cs.common.handler.WaitDialog;
import com.cs.common.utils.DialogTools;
import com.cs.common.utils.ImageLoaderUtils;
import com.cs.common.utils.StringUtils;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.action.RxAction;
import com.hywy.luanhzt.activity.AboutAppActivity;
import com.hywy.luanhzt.activity.AllXunchaActivity;
import com.hywy.luanhzt.activity.EventListActivity;
import com.hywy.luanhzt.activity.EventSuperviseActivity;
import com.hywy.luanhzt.activity.FeedBackActivity;
import com.hywy.luanhzt.activity.InstructionActivity;
import com.hywy.luanhzt.activity.PersonInfoActivity;
import com.hywy.luanhzt.activity.ProblemReportListActivity;
import com.hywy.luanhzt.activity.YuJingListActivity;
import com.hywy.luanhzt.app.App;
import com.hywy.luanhzt.entity.Account;
import com.hywy.luanhzt.entity.AppMenu;
import com.hywy.luanhzt.utils.StartIntent;
import com.hywy.luanhzt.view.customer.MoreItemView;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by weifei on 17/1/19.
 */

public class ChildFragment3 extends BaseFragment {
    @Bind(R.id.iv_head)
    ImageView ivHead;
    @Bind(R.id.person_info)
    TextView tvPerson;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.my_xuncha)
    MoreItemView tvXuncha;
    @Bind(R.id.my_duban)
    MoreItemView tvDuban;
    @Bind(R.id.my_yujing)
    MoreItemView tvYujing;
    @Bind(R.id.my_notice)
    MoreItemView tvNotice;
    @Bind(R.id.my_save)
    MoreItemView tvSave;
//    @Bind(R.id.feedback)
//    MoreItemView tvFeed;
    @Bind(R.id.about_us)
    MoreItemView tvAbout;
    @Bind(R.id.instructions)
    MoreItemView tvInstructions;

    @Bind(R.id.btn_exit)
    Button btnExit;

    private WaitDialog waitDialog;

    private App app;
    private AppMenu menu;

    public static ChildFragment3 newInstance(String title) {
        Bundle args = new Bundle();
        ChildFragment3 fragment = new ChildFragment3();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main3;
    }

    @Override
    protected void initView() {
        app = App.getInstance();
        Account account = app.getAccount();
        tvName.setText(account.getNAME());
        if (StringUtils.hasLength(account.getUrlData()))
            ImageLoaderUtils.displayRound(getActivity(), ivHead, account.getUrlData(), R.drawable.ic_head_default);
        menu = app.getMenu3();
        if (StringUtils.isNotNullList(menu.getSubMenu())) {
            for (AppMenu m : menu.getSubMenu()) {
                if (m.getMENU_KEY().equals("wodexuncha")) {
                    getActivity().findViewById(R.id.my_xuncha).setVisibility(View.VISIBLE);
                } else if (m.getMENU_KEY().equals("wodeduban")) {
                    getActivity().findViewById(R.id.my_duban).setVisibility(View.VISIBLE);
                } else if (m.getMENU_KEY().equals("wodeshangbao")) {
                    getActivity().findViewById(R.id.my_notice).setVisibility(View.VISIBLE);
                } else if (m.getMENU_KEY().equals("yujingchakan")) {
                    getActivity().findViewById(R.id.my_yujing).setVisibility(View.VISIBLE);
                }
            }
        }

        mRxManager.on(RxAction.ACTION_REPLACE_HEAD, new Action1<String>() {
            @Override
            public void call(String s) {
                if (StringUtils.hasLength(account.getUrlData()))
                    ImageLoaderUtils.displayRound(getActivity(), ivHead, account.getUrlData(), R.drawable.ic_head_default);
            }
        });
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
    }

    @OnClick({
            R.id.iv_head, R.id.person_info, R.id.my_xuncha, R.id.my_duban, R.id.my_yujing, R.id.my_notice, R.id.my_save, R.id.about_us, R.id.instructions, R.id.btn_exit
    })
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_head:
                break;
            case R.id.person_info:
                PersonInfoActivity.startAction(getActivity());
                break;
            case R.id.my_xuncha:
                startActivity(new Intent(getActivity(), AllXunchaActivity.class));
                break;
            case R.id.my_duban:
                EventListActivity.startAction(getActivity());
                break;
            case R.id.my_yujing:
                YuJingListActivity.startAction(getActivity());
                break;
            case R.id.my_notice:
                ProblemReportListActivity.startAction(getActivity());
                break;
            case R.id.my_save:
                break;
//            case R.id.feedback:
//                FeedBackActivity.startAction(getActivity());
//                break;
            case R.id.about_us:
                AboutAppActivity.startAction(getActivity());
                break;
            case R.id.instructions:
                InstructionActivity.startAction(getActivity());
                break;
            case R.id.btn_exit:
                DialogTools.showConfirmDialog(getContext(), getString(R.string.exit_define), getString(R.string.exit_txt), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StartIntent.startExit(getContext());
                    }
                });
                break;

            default:
                break;
        }

    }

}
