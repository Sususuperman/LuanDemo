package com.hywy.luanhzt.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.cs.common.adapter.BaseListFlexAdapter;
import com.cs.common.adapter.layoutmanager.FullyLinearLayoutManager;
import com.cs.common.base.BaseToolbarActivity;
import com.cs.common.handler.BaseHandler_;
import com.cs.common.handler.SpringViewHandler;
import com.cs.common.listener.OnPostListenter;
import com.cs.common.utils.StringUtils;
import com.cs.common.view.ImagePagerActivity;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.adapter.ImagesAdapter;
import com.hywy.luanhzt.adapter.item.DealItem;
import com.hywy.luanhzt.entity.AttachMent;
import com.hywy.luanhzt.entity.Deal;
import com.hywy.luanhzt.entity.EventSupervise;
import com.hywy.luanhzt.entity.ProblemReport;
import com.hywy.luanhzt.key.Key;
import com.hywy.luanhzt.task.GetEventCreateListTask;
import com.hywy.luanhzt.task.GetEventTypeTask;
import com.hywy.luanhzt.view.MyRecycleView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

public class ProblemReportDetailsActivity extends BaseToolbarActivity implements AdapterView.OnItemClickListener {
    @Bind(R.id.pro_name)
    TextView proName;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.tv_river)
    TextView tvRiver;
    @Bind(R.id.tv_event)
    TextView tvEvent;
    @Bind(R.id.pro_content)
    TextView tvContent;
    @Bind(R.id.image_grid)
    GridView imageGrid;

    @Bind(R.id.recyclerview)
    MyRecycleView recycleView;
    private BaseListFlexAdapter adapter;

    private ImagesAdapter imageAdapter;
    private ProblemReport problemReport;


    public static void startAction(Activity activity, ProblemReport problemReport) {
        Intent intent = new Intent(activity, ProblemReportDetailsActivity.class);
        intent.putExtra("problemReport", problemReport);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_report_details);
        init();
    }

    private void init() {
        setTitleBulider(new Bulider().backicon(R.drawable.ic_arrow_back_white_24dp).title("问题详情"));
        problemReport = getIntent().getParcelableExtra("problemReport");
        imageAdapter = new ImagesAdapter(this);
        imageGrid.setAdapter(imageAdapter);
        imageGrid.setOnItemClickListener(this);
        List<AttachMent> attachMents = problemReport.getPATROL_NOTE();
        if (attachMents != null && attachMents.size() > 0) {
            imageAdapter.setList(attachMents);
        }

        proName.setText(problemReport.getEVENT_NAME());
        tvContent.setText(problemReport.getEVENT_CONT());
        tvAddress.setText(problemReport.getADNM());
        tvRiver.setText(problemReport.getREACH_NAME());
        tvEvent.setText(problemReport.getTYPENAME());

        adapter = new BaseListFlexAdapter(this);
        recycleView.setAdapter(adapter);
        recycleView.setLayoutManager(new FullyLinearLayoutManager(this));
        recycleView.setAdapter(adapter);
        initData();
    }

    private void initData() {
        SpringViewHandler handler = new SpringViewHandler(this);
        handler.setBuilder(new BaseHandler_.Builder().isShowToast(false).isWait(true));
        Map<String, Object> params = new HashMap<>();
        params.put("EVENT_ID", problemReport.getEVENT_ID());
        handler.request(params, new GetEventCreateListTask(this));
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                List<EventSupervise> list = (List<EventSupervise>) result.get(Key.RESULT);
                if (StringUtils.isNotNullList(list)) {
                    EventSupervise eventSupervise = list.get(0);
                    if (StringUtils.isNotNullList(eventSupervise.getDeals())) {
                        List<Deal> deals = eventSupervise.getDeals();
                        int size = deals.size();
                        for (int i = 0; i < size; i++) {
                            Deal deal = deals.get(i);
                            DealItem item = new DealItem(deal);
                            adapter.addItem(item);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }


            @Override
            public void OnPostFail(Map<String, Object> result) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ImagePagerActivity.startShowImages(view.getContext(), imageAdapter.getImagePaths(), i);
    }
}
