package com.hywy.luanhzt.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cs.android.task.Task;
import com.cs.common.base.BaseToolbarActivity;
import com.cs.common.utils.DialogTools;
import com.cs.common.view.SwipeRefreshview;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.adapter.item.InspectionItem;
import com.hywy.luanhzt.adapter.item.ProblemReportItem;
import com.hywy.luanhzt.dao.LogDao;
import com.hywy.luanhzt.dao.ProblemDao;
import com.hywy.luanhzt.entity.Inspection;
import com.hywy.luanhzt.entity.ProblemReport;
import com.hywy.luanhzt.task.GetMyXcTask;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import eu.davidea.flexibleadapter.FlexibleAdapter;

/**
 * @author Superman
 */
public class AllXunchaActivity extends BaseToolbarActivity implements FlexibleAdapter.OnItemClickListener, FlexibleAdapter.OnItemLongClickListener {
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.swipeRefresh)
    SwipeRefreshview swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        init();
        initData();
    }

    private void init() {
        setTitleBulider(new Bulider().backicon(R.drawable.ic_arrow_back_white_24dp).title(getString(R.string.log_xuncha)));
        swipeRefresh.setAdapter(recyclerview);
        swipeRefresh.getAdapter().initializeListeners(this);
    }

    private void initData() {
        Map<String, Object> params = new HashMap<>();

//        params.put("USER_ID",1);

        Task task = new GetMyXcTask(this);

        swipeRefresh.builder(new SwipeRefreshview.Builder()
                .task(task).params(params));
        swipeRefresh.isToast(false);
        swipeRefresh.refresh();

    }

    @Override
    public boolean onItemClick(int position) {
        InspectionItem item = (InspectionItem) swipeRefresh.getAdapter().getItem(position);
        Inspection inspection = item.getData();
        if (inspection.getDATA_TYPE().equals(Inspection.DATA_LOCAL)) {
            CreateLogActivity.startAction(this, inspection.getPlan(), inspection.getJsonArray(), inspection.getLOG_ID(),inspection);
        } else
            XcDetailsActivity.startAction(this, inspection);
        return false;
    }

    @Override
    public void onItemLongClick(int position) {
        InspectionItem item = (InspectionItem) swipeRefresh.getAdapter().getItem(position);
        Inspection inspection = item.getData();
        if (inspection.getDATA_TYPE().equals(ProblemReport.DATA_LOCAL)) {
            DialogTools.showConfirmDialog(this, "提示", "确定删除该条数据？", "确定", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    LogDao dao = new LogDao(AllXunchaActivity.this);
                    dao.delete(inspection);
                    swipeRefresh.getAdapter().removeItem(position);
                }
            });
        }
    }
}
