package com.hywy.luanhzt.activity.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cs.android.task.Task;
import com.cs.common.baserx.RxManager;
import com.cs.common.utils.DateUtils;
import com.cs.common.utils.DialogTools;
import com.cs.common.utils.StringUtils;
import com.cs.common.view.MyProgressDialog;
import com.cs.common.view.SwipeRefreshview;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.action.RxAction;
import com.hywy.luanhzt.activity.ComplainInfoActivity;
import com.hywy.luanhzt.activity.ProblemReportDetailsActivity;
import com.hywy.luanhzt.adapter.item.ComplainItem;
import com.hywy.luanhzt.adapter.item.InspectionItem;
import com.hywy.luanhzt.adapter.item.ProblemReportItem;
import com.hywy.luanhzt.app.App;
import com.hywy.luanhzt.task.GetComplainListTask;
import com.hywy.luanhzt.task.GetProblemListRiverTask;
import com.hywy.luanhzt.task.GetXcInRiverTask;

import java.util.HashMap;
import java.util.Map;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import rx.functions.Action1;

/**
 * 河道记录fragment
 */

public class RiverManageFragment extends Fragment implements FlexibleAdapter.OnItemClickListener {
    public Activity mActivity;
    public RxManager rxManager;
    private RecyclerView recyclerview;
    private SwipeRefreshview swipeRefresh;
    private TextView tvDate;
    private LinearLayout layout;
    private RelativeLayout layout_date;
    private int type;
    public static final int LOG_LIST = 0;//巡查日志
    public static final int EVENT_LIST = 1;//事件列表
    public static final int COMPLAIN_LIST = 2;//投诉建议

    private Task task;
    private String reach_code;

    public static RiverManageFragment newInstance(int type, String reach_code) {

        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putString("reach_code", reach_code);
        RiverManageFragment fragment = new RiverManageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_river_manage, null);
        swipeRefresh = (SwipeRefreshview) view.findViewById(R.id.swipeRefresh);
        recyclerview = (RecyclerView) view.findViewById(R.id.recyclerview);
        tvDate = (TextView) view.findViewById(R.id.tv_date);
        layout = (LinearLayout) view.findViewById(R.id.log_layout);
        layout_date = (RelativeLayout) view.findViewById(R.id.layout_date);
        layout_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseTime();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        rxManager = new RxManager();
        init();
        initData("");
        createSearchListener();
    }

    /**
     * 初始化数据
     */
    private void init() {
        swipeRefresh.setMode(SwipeRefreshview.Mode.DISABLED);
        swipeRefresh.setAdapter(recyclerview);
        swipeRefresh.getAdapter().initializeListeners(this);

        if (getArguments() != null) {
            type = getArguments().getInt("type");
            reach_code = getArguments().getString("reach_code");
        }

        if (type == 0) {
            layout.setVisibility(View.VISIBLE);
            task = new GetXcInRiverTask(getActivity());
        } else if (type == 1) {
            layout.setVisibility(View.GONE);
            task = new GetProblemListRiverTask(getActivity());
        } else if (type == 2) {
            layout.setVisibility(View.GONE);
            task = new GetComplainListTask(getActivity());
        }
    }

    private void initData(String s) {
        Map<String, Object> params = new HashMap<>();
        if (type == 0) {
            if (StringUtils.hasLength(tvDate.getText().toString())) {
                params.put("PATROL_TM", tvDate.getText().toString());
            }
            params.put("REACH_CODE", reach_code);
        } else if (type == 1) {
            if (StringUtils.hasLength(tvDate.getText().toString())) {
                params.put("STARTTIME", tvDate.getText().toString());
            }
//            params.put("ROLE_ID", App.getInstance().getAccount().getROLE_ID());
            params.put("ADCD", App.getInstance().getAccount().getADCD());
            params.put("REACH_CODE", reach_code);
            task = new GetProblemListRiverTask(getActivity());
        } else if (type == 2) {
            if (StringUtils.hasLength(tvDate.getText().toString())) {
                params.put("COMPLAINTIME", tvDate.getText().toString());
            }
            params.put("ADCD", App.getInstance().getAccount().getADCD());
            params.put("COMPLAINRIVER", reach_code);
            task = new GetComplainListTask(getActivity());
        }
        swipeRefresh.builder(new SwipeRefreshview.Builder().params(params).task(task)).isToast(false);
        swipeRefresh.request();
    }

    public void chooseTime() {
//        Calendar calendar = Calendar.getInstance();
//        new YearMonthDatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                calendar.set(Calendar.YEAR, year);
//                calendar.set(Calendar.MONTH, monthOfYear);
//                tvDate.setText(DateUtils.transforMillToMoth(calendar.getTimeInMillis()));
//                initData("");
//
//            }
//        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show();


        DialogTools.showDateDialog(getActivity(), System.currentTimeMillis(), MyProgressDialog.DIALOG_DATEPICKER_YM, new MyProgressDialog.OnDatePickerClickListener() {
            @Override
            public void datePickerConfirmClick(long dateTime) {
                tvDate.setText(DateUtils.transforMillToMoth(dateTime));
                initData("");
            }

            @Override
            public void datePickerCancelClick() {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public boolean onItemClick(int position) {
        AbstractFlexibleItem adapter = (AbstractFlexibleItem) swipeRefresh.getAdapter().getItem(position);
        if (adapter instanceof InspectionItem) {
//            InspectionItem item = (InspectionItem) adapter;
//            ContactDetailsActivity.startAction(getActivity(), item.getData());
        } else if (adapter instanceof ProblemReportItem) {
            ProblemReportItem item = (ProblemReportItem) adapter;
            ProblemReportDetailsActivity.startAction(getActivity(), item.getData());
        } else if (adapter instanceof ComplainItem) {
            ComplainItem item = (ComplainItem) adapter;
            ComplainInfoActivity.startAction(getActivity(), item.getData());
        }
        return false;
    }

    private void createSearchListener() {
        rxManager.on(RxAction.ACTION_CONTACT_SEARCH, new Action1<String>() {
            @Override
            public void call(String s) {
                initData(s);
            }
        });
    }
}
