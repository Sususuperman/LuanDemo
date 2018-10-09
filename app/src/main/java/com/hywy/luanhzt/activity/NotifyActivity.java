package com.hywy.luanhzt.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cs.android.task.Task;
import com.cs.common.base.BaseToolbarActivity;
import com.cs.common.utils.StringUtils;
import com.cs.common.view.SwipeRefreshview;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.adapter.item.NotifyItem;
import com.hywy.luanhzt.app.App;
import com.hywy.luanhzt.entity.AppMenu;
import com.hywy.luanhzt.entity.Notify;
import com.hywy.luanhzt.task.GetNotifyListTask;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import eu.davidea.flexibleadapter.FlexibleAdapter;

public class NotifyActivity extends BaseToolbarActivity implements FlexibleAdapter.OnItemClickListener {
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.swipeRefresh)
    SwipeRefreshview swipeRefresh;

    public static void startAction(Context activity) {
        Intent intent = new Intent(activity, NotifyActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        init();
        initData();
    }

    private void init() {
        this.setTitleBulider(new Bulider().title(getString(R.string.title_notify)).backicon(R.drawable.ic_arrow_back_white_24dp));
        swipeRefresh.setAdapter(recyclerview);
        swipeRefresh.getAdapter().initializeListeners(this);
    }

    private void initData() {
        Map<String, Object> params = new HashMap<>();
        Task task = new GetNotifyListTask(this);
        swipeRefresh.builder(new SwipeRefreshview.Builder()
                .task(task).params(params));
        swipeRefresh.isToast(false);
        swipeRefresh.refresh();
    }

    @Override
    public boolean onItemClick(int position) {
        NotifyItem item = (NotifyItem) swipeRefresh.getAdapter().getItem(position);
        Notify notify = item.getData();
        NotifyDetailsActivity.startAction(this, notify);
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        AppMenu appMenu = App.getInstance().getMenu1();
        if (appMenu != null) {
            if (StringUtils.isNotNullList(appMenu.getSubMenu())) {
                for (AppMenu m : appMenu.getSubMenu()) {
                    if (m.getMENU_KEY().equals("tongzhigonggaofabu")) {
                        getMenuInflater().inflate(R.menu.send, menu);
                    }
                }
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.send) {
            CreateNotifyActivity.startAction(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        initData();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
