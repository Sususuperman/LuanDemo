package com.hywy.luanhzt.activity.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.cs.common.adapter.BaseListFlexAdapter;
import com.cs.common.adapter.layoutmanager.FullyLinearLayoutManager;
import com.cs.common.base.BaseFragment;
import com.cs.common.utils.StringUtils;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.adapter.RiverFragmentAdapter;
import com.hywy.luanhzt.adapter.item.XunChaRecordItem;
import com.hywy.luanhzt.entity.RiverDetails;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;


/**
 * 河道~巡查记录
 *
 * @author Superman
 */
public class RVFragment4 extends BaseFragment {
    @Bind(R.id.tablayout)
    TabLayout tabLayout;
    @Bind(R.id.viewpager)
    ViewPager viewPager;
    private ArrayList<RiverDetails.XCJHListBean> xcjhListBean;

    private BaseListFlexAdapter adapter;
    private List<Fragment> list;
    private RiverFragmentAdapter mAdapter;
    private String[] titles = new String[]{"巡查日志", "事件列表", "投诉建议"};
    private String reach_code;

    public static RVFragment4 newInstance(ArrayList<RiverDetails.XCJHListBean> xcjhListBean, String reach_code) {
        Bundle args = new Bundle();
        args.putParcelableArrayList("xcjhListBean", xcjhListBean);
        args.putString("reach_code", reach_code);
        RVFragment4 fragment = new RVFragment4();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_xcjl;
    }

    @Override
    protected void initView() {
        adapter = new BaseListFlexAdapter(getActivity());
//        recyclerView.setLayoutManager(new FullyLinearLayoutManager(getActivity()));
//        recyclerView.setAdapter(adapter);

        if (getArguments() != null) {
            xcjhListBean = getArguments().getParcelableArrayList("xcjhListBean");
            reach_code = getArguments().getString("reach_code");
        }

        if (StringUtils.isNotNullList(xcjhListBean)) {
            for (RiverDetails.XCJHListBean xj : xcjhListBean) {
                XunChaRecordItem item = new XunChaRecordItem(xj);
                adapter.addItem(item);
            }
            adapter.notifyDataSetChanged();
        }

        list = new ArrayList<>();
        list.add(RiverManageFragment.newInstance(RiverManageFragment.LOG_LIST,reach_code));
        list.add(RiverManageFragment.newInstance(RiverManageFragment.EVENT_LIST,reach_code));
        list.add(RiverManageFragment.newInstance(RiverManageFragment.COMPLAIN_LIST,reach_code));

        mAdapter = new RiverFragmentAdapter(getChildFragmentManager(), list, titles);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(mAdapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).select();//设置默认选中项

    }

    private void setTextView(TextView tv, String str) {
        if (StringUtils.hasLength(str)) {
            tv.setText(str);
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
    }


}
