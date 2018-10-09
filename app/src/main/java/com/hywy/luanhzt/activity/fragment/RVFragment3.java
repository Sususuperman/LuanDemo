package com.hywy.luanhzt.activity.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.cs.android.async.AsyncDownLoad;
import com.cs.android.task.Task;
import com.cs.common.adapter.BaseListFlexAdapter;
import com.cs.common.adapter.layoutmanager.FullyLinearLayoutManager;
import com.cs.common.base.BaseFragment;
import com.cs.common.baserx.RxManager;
import com.cs.common.listener.OnDownLoadListenter;
import com.cs.common.utils.FileUtils;
import com.cs.common.utils.StringUtils;
import com.cs.common.view.SwipeRefreshview;
import com.hywy.luanhzt.Const;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.activity.MyRiverActivity;
import com.hywy.luanhzt.adapter.item.RiverFileItem;
import com.hywy.luanhzt.adapter.item.RiverItem;
import com.hywy.luanhzt.entity.River;
import com.hywy.luanhzt.entity.RiverDetails;
import com.hywy.luanhzt.entity.RiverFile;
import com.hywy.luanhzt.task.GetEventListTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import eu.davidea.flexibleadapter.FlexibleAdapter;


/**
 * 河道基本信息
 *
 * @author Superman
 */
public class RVFragment3 extends BaseFragment {
    //    @Bind(R.id.webview)
//    WebView webView;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    private ArrayList<RiverFile> yhycListBean;
    private BaseListFlexAdapter mAdapter;

    public static RVFragment3 newInstance(ArrayList<RiverFile> yhycListBean) {
        Bundle args = new Bundle();
        args.putParcelableArrayList("yhycListBean", yhycListBean);
        RVFragment3 fragment = new RVFragment3();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_yhyc;
    }

    @Override
    protected void initView() {
        if (getArguments() != null) {
            yhycListBean = getArguments().getParcelableArrayList("yhycListBean");
        }
        recyclerview.setLayoutManager(new FullyLinearLayoutManager(getActivity()));
        mAdapter = new BaseListFlexAdapter(getActivity());
        recyclerview.setAdapter(mAdapter);
        mAdapter.initializeListeners(new FlexibleAdapter.OnItemClickListener() {
            @Override
            public boolean onItemClick(int position) {
//                startActivity(new Intent(getActivity(), MyRiverActivity.class));
                RiverFileItem item = (RiverFileItem) mAdapter.getItem(position);
                RiverFile riverFile = item.getData();
                if (StringUtils.hasLength(riverFile.getURL())) {
                    infoAttachment(riverFile);
                }
                return false;
            }
        });
//        WebSettings webSettings = webView.getSettings();
//        //设置WebView属性，能够执行Javascript脚本
//        webSettings.setJavaScriptEnabled(true);
//        //设置可以访问文件
//        webSettings.setAllowFileAccess(true);
//        //设置支持缩放
//        webSettings.setSupportZoom(true);
//        if (yhycListBean != null) {
//            if (StringUtils.hasLength(yhycListBean.getHTML_Url())) {
//                webView.loadUrl(yhycListBean.getHTML_Url());
//            }
//        }
//        webView.setWebViewClient(new WebViewClient());
        initData();
    }

    private void initData() {
        if (StringUtils.isNotNullList(yhycListBean)) {
            for (RiverFile riverFile : yhycListBean) {
                RiverFileItem item = new RiverFileItem(riverFile);
                mAdapter.addItem(item);
            }
            mAdapter.notifyDataSetChanged();
        }

    }

    public void infoAttachment(final RiverFile attachMent) {
        final String[] pathUrl = StringUtils.delimitedListToStringArray(attachMent.getURL(), "|");
        AsyncDownLoad downLoad = new AsyncDownLoad(getActivity());
        String downUrl = pathUrl[0];
        String path = downLoad.getPath(downUrl);
        String url = downUrl;

        File file = new File(path);
        if (!file.exists()) {
            showDialog(getString(R.string.down_office_downing));
            downLoad.loadFile(url, new OnDownLoadListenter() {
                @Override
                public void onLoadSuccess(String path) {
                    Toast.makeText(getActivity(), R.string.down_office_success, Toast.LENGTH_SHORT).show();
                    dismissDialog();
                }

                @Override
                public void onLoadFail(String url) {
                    dismissDialog();
                    Toast.makeText(getActivity(), R.string.down_error, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLoadProgress(int total, int progress) {

                }
            });
        } else {
            selectOffice(path);
        }

    }

    /**
     * 显示可支持的打开的软件
     *
     * @param path
     */
    public void selectOffice(String path) {
        FileUtils.openFile(getActivity(), Const.AUTHORITY, path);
    }
}
