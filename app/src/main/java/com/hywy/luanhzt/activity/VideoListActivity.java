package com.hywy.luanhzt.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.cs.android.task.Task;
import com.cs.common.base.BaseToolbarActivity;
import com.cs.common.view.SwipeRefreshview;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.task.GetMessageListTask;
import com.hywy.luanhzt.task.GetVideoListTask;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

public class VideoListActivity extends BaseToolbarActivity {

    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.swipeRefresh)
    SwipeRefreshview swipeRefresh;

    public static void startAction(Context activity) {
        Intent intent = new Intent(activity, VideoListActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        init();
        initData();
    }

    private void init() {
        setTitleBulider(new Bulider().backicon(R.drawable.ic_arrow_back_white_24dp).title("视频监控"));
        swipeRefresh.setAdapter(recyclerview);
        swipeRefresh.getAdapter().initializeListeners(this);

    }

    private void initData() {
        Map<String, Object> params = new HashMap<>();
        Task task = new GetVideoListTask(this);
        swipeRefresh.builder(new SwipeRefreshview.Builder()
                .task(task).params(params));
        swipeRefresh.isToast(false);
        swipeRefresh.refresh();
    }

}
