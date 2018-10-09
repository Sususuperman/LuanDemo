package com.hywy.luanhzt.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.cs.common.base.BaseToolbarActivity;
import com.cs.common.handler.BaseHandler_;
import com.cs.common.handler.SpringViewHandler;
import com.cs.common.listener.OnPostListenter;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.action.RxAction;
import com.hywy.luanhzt.activity.fragment.EventListFragment;
import com.hywy.luanhzt.adapter.RiverFragmentAdapter;
import com.hywy.luanhzt.adapter.SpinnerListAdapter;
import com.hywy.luanhzt.entity.Adnm;
import com.hywy.luanhzt.entity.EventSupervise;
import com.hywy.luanhzt.entity.River;
import com.hywy.luanhzt.entity.User;
import com.hywy.luanhzt.key.Key;
import com.hywy.luanhzt.task.GetEventTypeTask;
import com.hywy.luanhzt.view.dialog.dialogplus.DialogPlus;
import com.hywy.luanhzt.view.dialog.dialogplus.ListHolder;
import com.hywy.luanhzt.view.dialog.dialogplus.OnItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * 事件督办
 **/
public class EventSuperviseActivity extends BaseToolbarActivity implements OnItemClickListener {
    private List<Fragment> list;
    private RiverFragmentAdapter adapter;

    @Bind(R.id.viewpager)
    ViewPager viewPager;
    @Bind(R.id.tablayout)
    TabLayout tabLayout;
    @Bind(R.id.title)
    TextView title;

    private String[] titles = new String[]{"未处理", "处理中", "已处理"};
    private List<EventSupervise> events;
    private SpinnerListAdapter spinnerAdapter1;

    public static void startAction(Context context) {
        Intent intent = new Intent(context, EventSuperviseActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_supervise);
        init();
        initEventList();
        initListeners();
    }

    private void initEventList() {
        SpringViewHandler handler = new SpringViewHandler(this);
        handler.setBuilder(new BaseHandler_.Builder().isShowToast(false).isWait(false));
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


    private void init() {
        this.setTitleBulider(new Bulider().backicon(R.drawable.ic_arrow_back_white_24dp).title(getString(R.string.event_list)));
        list = new ArrayList<>();
        list.add(EventListFragment.newInstance(3));//未处理
        list.add(EventListFragment.newInstance(1));//处理中
        list.add(EventListFragment.newInstance(4));//已办结

        adapter = new RiverFragmentAdapter(getSupportFragmentManager(), list, titles);
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).select();//设置默认选中项

        spinnerAdapter1 = new SpinnerListAdapter(this);
    }

    private void initListeners() {
        findViewById(R.id.layout_river).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEventList(events);
            }
        });

        findViewById(R.id.iv_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateEventActivity.startAction(EventSuperviseActivity.this);
            }
        });
    }

    private void showEventList(List<EventSupervise> list) {
        spinnerAdapter1.setList(list);
        showRadioDialog();
    }

    /**
     * 显示单选dialog
     */
    private void showRadioDialog() {
        DialogPlus dialogPlus = new DialogPlus.Builder(this)
                .setContentHolder(new ListHolder())
                .setAdapter(spinnerAdapter1)
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
        Object object = spinnerAdapter1.getItem(position);
        if (object instanceof EventSupervise) {
            EventSupervise eventSupervise = (EventSupervise) object;
            title.setTag(eventSupervise);
            title.setText(eventSupervise.getEVENT_TYPE_NAME());
            mRxManager.post(RxAction.ACTION_EVENT_CHOOSE_TYPE, eventSupervise.getEVENT_TYPE_ID());
        }
        dialog.dismiss();
    }

}
