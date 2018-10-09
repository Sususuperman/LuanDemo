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
import android.widget.TextView;

import com.cs.android.task.Task;
import com.cs.common.baserx.RxManager;
import com.cs.common.view.SwipeRefreshview;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.action.RxAction;
import com.hywy.luanhzt.activity.SiteDetailsActivity;
import com.hywy.luanhzt.adapter.item.ReservoirItem;
import com.hywy.luanhzt.adapter.item.RiverCourseItem;
import com.hywy.luanhzt.adapter.item.TakeWaterItem;
import com.hywy.luanhzt.app.App;
import com.hywy.luanhzt.task.GetReservoirTask;
import com.hywy.luanhzt.task.GetRiverCourseTask;
import com.hywy.luanhzt.task.GetTakeWaterTask;

import java.util.HashMap;
import java.util.Map;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import rx.functions.Action1;

/**
 * 取用水监测
 */

public class TakeWaterFragment extends Fragment implements FlexibleAdapter.OnItemClickListener {
    public Activity mActivity;
    TextView emptyText;
    LinearLayout emptyView;
    public RxManager rxManager;
    private int current_position;//记录点击的item
    private RecyclerView recyclerview;
    private SwipeRefreshview swipeRefresh;

    public static TakeWaterFragment newInstance() {
        TakeWaterFragment fragment = new TakeWaterFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservoir, null);
        swipeRefresh = (SwipeRefreshview) view.findViewById(R.id.swipeRefresh);
        recyclerview = (RecyclerView) view.findViewById(R.id.recyclerview);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        rxManager = new RxManager();
        init();
        initData();
        createSelectListener();
    }

    /**
     * 初始化数据
     */
    private void init() {
        swipeRefresh.setMode(SwipeRefreshview.Mode.BOTH);
        swipeRefresh.setAdapter(recyclerview);
        swipeRefresh.getAdapter().initializeListeners(this);
    }

    public void initData() {
        Map<String, Object> params = new HashMap<>();
        params.put("ADCD", App.getInstance().getAccount().getADCD());
        Task task;
        task = new GetTakeWaterTask(getActivity());
        swipeRefresh.getAdapter().clear();
        swipeRefresh.builder(new SwipeRefreshview.Builder().params(params).task(task)).isToast(false);
        swipeRefresh.request();
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public boolean onItemClick(int position) {
        current_position = position;
            TakeWaterItem item = (TakeWaterItem) swipeRefresh.getAdapter().getItem(position);
            SiteDetailsActivity.startAction(getActivity(), item.getData());
        return false;
    }

    private void createSelectListener() {

    }
}
