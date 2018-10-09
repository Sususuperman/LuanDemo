package com.hywy.luanhzt.activity.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cs.android.task.Task;
import com.cs.common.baserx.RxManager;
import com.cs.common.view.SwipeRefreshview;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.action.RxAction;
import com.hywy.luanhzt.activity.EventDetailsActivity;
import com.hywy.luanhzt.adapter.item.EventListItem;
import com.hywy.luanhzt.app.App;
import com.hywy.luanhzt.task.GetEventListTask;
import com.hywy.luanhzt.task.GetEventProcessListTask;

import java.util.HashMap;
import java.util.Map;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import rx.functions.Action1;

/**
 * 事件列表fragment
 */

public class EventListFragment extends Fragment implements FlexibleAdapter.OnItemClickListener {
    public Activity mActivity;
    public RxManager rxManager;
    private RecyclerView recyclerview;
    private SwipeRefreshview swipeRefresh;
    private int type;
    private RxManager mRxManager = new RxManager();
    private int event_type_id;

    public static EventListFragment newInstance(int type) {

        Bundle args = new Bundle();
        args.putInt("type", type);
        EventListFragment fragment = new EventListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, null);
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
        createRecordListener();
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
        }
    }

    private void initData() {
        Map<String, Object> params = new HashMap<>();
        params.put("KEY", type);
        if (event_type_id != 0) {
            params.put("EVENT_TYPE_ID", event_type_id);
        }
        Task task;
        if (type == 3) {
            params.put("DEAL_PER_ID", App.getInstance().getAccount().getUserId());
            params.put("DEAL_STATE", 0);
            task = new GetEventProcessListTask(getActivity(), type);
        } else
            task = new GetEventListTask(getActivity(), type);
        swipeRefresh.builder(new SwipeRefreshview.Builder().params(params).task(task)).isToast(false);
        swipeRefresh.request();
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public boolean onItemClick(int position) {
        EventListItem eventListItem = (EventListItem) swipeRefresh.getAdapter().getItem(position);
        EventDetailsActivity.startAction(getActivity(), eventListItem.getData());
        return false;
    }

    private void createRecordListener() {
        mRxManager.on(RxAction.ACTION_EVENT_CHOOSE_TYPE, new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                event_type_id = integer;
                initData();
            }
        });
        mRxManager.on(RxAction.ACTION_EVENT_CREATE, new Action1<String>() {
            @Override
            public void call(String s) {
                initData();
            }
        });

        mRxManager.on(RxAction.ACTION_EVENT_SUBMIT, new Action1<String>() {
            @Override
            public void call(String s) {
                initData();
            }
        });
    }
}
