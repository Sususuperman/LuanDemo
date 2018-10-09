package com.hywy.luanhzt.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cs.android.task.Task;
import com.cs.common.adapter.BaseListFlexAdapter;
import com.cs.common.base.BaseToolbarActivity;
import com.cs.common.handler.SpringViewHandler;
import com.cs.common.listener.OnPostListenter;
import com.cs.common.utils.IToast;
import com.cs.common.utils.PhoneUtil;
import com.cs.common.view.SwipeRefreshview;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.adapter.SpinnerListAdapter;
import com.hywy.luanhzt.adapter.item.RiverItem;
import com.hywy.luanhzt.adapter.item.SpinnerItem;
import com.hywy.luanhzt.entity.Adnm;
import com.hywy.luanhzt.entity.River;
import com.hywy.luanhzt.key.Key;
import com.hywy.luanhzt.task.GetRiver9ListTask;
import com.hywy.luanhzt.task.GetRiverListTask;
import com.hywy.luanhzt.task.PostCuibanEventTask;
import com.hywy.luanhzt.view.dialog.dialogplus.DialogPlus;
import com.hywy.luanhzt.view.dialog.dialogplus.ListHolder;
import com.hywy.luanhzt.view.dialog.dialogplus.OnCancelListener;
import com.hywy.luanhzt.view.dialog.dialogplus.OnClickListener;
import com.hywy.luanhzt.view.dialog.dialogplus.OnItemClickListener;
import com.hywy.luanhzt.view.dialog.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import eu.davidea.flexibleadapter.FlexibleAdapter;

/**
 * 我的河道
 *
 * @author Superman
 */
public class AllRiverActivity extends BaseToolbarActivity implements FlexibleAdapter.OnItemClickListener, OnItemClickListener {
    @Bind(R.id.swipeRefresh)
    SwipeRefreshview swipeRefresh;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.title_name)
    TextView title;
    @Bind(R.id.rgp)
    RadioGroup radioGroup;

    private DialogPlus dialogPlus;

    private RecyclerView spinnerRecycle;

    private BaseListFlexAdapter mAdapter1, mAdapter2;
    private boolean isShow;
    private List<River> rivers;
    private List<Adnm> admins;
    private SpinnerListAdapter spinnerAdapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_river);
        init();
        initListener();
        initRiversAndAdmins();
        initData();
    }

    private void init() {
        setTitleBulider(new Bulider().title("一河一档").backicon(R.drawable.ic_arrow_back_white_24dp));

        spinnerAdapter1 = new SpinnerListAdapter(this);

        swipeRefresh.setAdapter(recyclerview);
        swipeRefresh.getAdapter().initializeListeners(this);
        swipeRefresh.setMode(SwipeRefreshview.Mode.DISABLED);
//        mAdapter1 = new BaseListFlexAdapter(this);
//        recyclerview.setLayoutManager(new LinearLayoutManager(this));
//        recyclerview.setAdapter(mAdapter1);

        mAdapter2 = new BaseListFlexAdapter(this);
        spinnerRecycle = new RecyclerView(this);
        spinnerRecycle.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        spinnerRecycle.setLayoutManager(new LinearLayoutManager(this));
        spinnerRecycle.setAdapter(mAdapter2);
    }

    private void initSpinner(List<River> list) {
        //先清一下
        mAdapter2.clear();
        List<SpinnerItem> items = new ArrayList<>();
        River river = new River();
        river.setRV_NAME("全部");
        items.add(new SpinnerItem(river));
        for (int i = 0; i < list.size(); i++) {
            SpinnerItem spinnerItem = new SpinnerItem(list.get(i));
            items.add(spinnerItem);
        }
        mAdapter2.addItems(0, items);
    }

    private void initRiversAndAdmins() {
        initRivers();
        initAdmins();
    }

    private void initRivers() {
        SpringViewHandler handler = new SpringViewHandler(this);
        Map<String, Object> map = new HashMap<>();
        handler.request(map, new GetRiver9ListTask(this));
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                rivers = (List<River>) result.get(Key.RESULT);
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {

            }
        });
    }


    private void initAdmins() {
        SpringViewHandler handler = new SpringViewHandler(this);
        Map<String, Object> map = new HashMap<>();
        handler.request(map, new GetRiver9ListTask(this));
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                rivers = (List<River>) result.get(Key.RESULT);
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {

            }
        });
    }

    private void showRiverList(List<River> list) {
        spinnerAdapter1.setList(list);
        showRadioDialog();
    }

    private void showAddressList(List<Adnm> list) {
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
        if (object instanceof Adnm) {
            Adnm adnm = (Adnm) object;
            title.setTag(adnm);
            title.setText(adnm.getADNM());
        } else if (object instanceof River) {
            River river = (River) object;
            title.setTag(river);
            title.setText(river.getREACH_NAME());
        }
        dialog.dismiss();
    }

    private void initData() {
        Map<String, Object> params = new HashMap<>();
        Task task = new GetRiverListTask(this);

        swipeRefresh.builder(new SwipeRefreshview.Builder()
                .task(task).params(params));
        swipeRefresh.refresh();
        swipeRefresh.isToast(false);

    }


    private void initListener() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (radioGroup.getCheckedRadioButtonId() == R.id.rb1) {
                    title.setText("请选择河道");
                } else if (radioGroup.getCheckedRadioButtonId() == R.id.rb2) {
                    title.setText("请选择行政区划");
                }
            }
        });

        mAdapter2.initializeListeners(new FlexibleAdapter.OnItemClickListener() {
            @Override
            public boolean onItemClick(int position) {
                SpinnerItem item = (SpinnerItem) mAdapter2.getItem(position);
                River r = item.getData();
                initData();//选择之后根据rv_code筛选数据进行显示
                if (isShow) {
                    cancleData();
                } else {
                    showData();
                }
                title.setText(item.getData().getRV_NAME());
                return false;
            }
        });

        swipeRefresh.setCompleteListener(new SwipeRefreshview.onCompleteListener() {
            @Override
            public void onComplete(Map<String, Object> result, boolean isLoadmore) {
                if (result.get(Key.RESULT) != null) {
                    List<River> list = (List<River>) result.get(Key.RESULT);
                    initSpinner(list);
                }
            }
        });
    }

    @OnClick(R.id.layout_river)
    public void choose() {
//        if (isShow) {
//            cancleData();
//        } else {
//            showData();
//        }
        if (radioGroup.getCheckedRadioButtonId() == R.id.rb1) {
            showRiverList(rivers);
        } else {
            showAddressList(admins);
        }
    }

    private void showData() {
        isShow = true;
        //弹窗显示
        if (dialogPlus == null) {
            createDialogPlus();
        }
        dialogPlus.show();
    }

    private void cancleData() {
        isShow = false;
        if (dialogPlus != null && dialogPlus.isShowing()) {
            dialogPlus.dismiss();
        }
    }

    private void createDialogPlus() {
        dialogPlus = new DialogPlus.Builder(this)
                .setContentHolder(new ViewHolder(spinnerRecycle))
                .setGravity(DialogPlus.Gravity.CENTER)
                .setBackgroundColorResourceId(R.color.white)
                .setCancelable(true)
                .setOutMostMargin(0, PhoneUtil.dp2px(this, 0), 0, 0)
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogPlus dialog) {
                        cancleData();
                    }
                })
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {

                    }
                })
                .create();
    }


    @Override
    public boolean onItemClick(int position) {
        if (swipeRefresh.getAdapter().getItem(position) instanceof RiverItem) {
            RiverItem item = (RiverItem) swipeRefresh.getAdapter().getItem(position);
            RiverDetailsActivity.startAction(this, item.getData());
        }
        return false;
    }
}
