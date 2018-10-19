package com.hywy.luanhzt.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs.android.task.Task;
import com.cs.common.adapter.BaseListFlexAdapter;
import com.cs.common.base.BaseActivity;
import com.cs.common.handler.BaseHandler_;
import com.cs.common.handler.SpringViewHandler;
import com.cs.common.listener.OnPostListenter;
import com.cs.common.listener.OnTextChangedListener;
import com.cs.common.utils.IToast;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.key.Key;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;

public abstract class BaseMapSearchActivity<T, Item extends AbstractFlexibleItem> extends BaseActivity {

    @Bind(R.id.search)
    EditText search;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.delete)
    ImageView delete;
    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;
    TextView title;
    BaseListFlexAdapter<Item> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_map_search);
        init();
    }

    protected void init() {
        final OnTextChangedListener listener = new OnTextChangedListener(delete, search);
        this.search.addTextChangedListener(listener);
        String hint = getSearchHint() == null ? "搜索" : getSearchHint();
        this.search.setHint(hint);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.mAdapter = new BaseListFlexAdapter<>(this);
        this.mRecyclerView.setAdapter(mAdapter);
        setTransparentStatusBar(R.color.alpha);

        this.search.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                }
                return true;
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("");
            }
        });

    }


    /**
     * 设置statusBar
     *
     * @param color
     */
    public void setTransparentStatusBar(int color) {
        //5.0及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = this.getWindow().getDecorView();
            decorView.setFitsSystemWindows(false);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, color));
            //4.4到5.0
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
    }




    public void request(String q) {
        Map<String, Object> params = getParams();
        if (!q.equals("")) {
            if (getQ_Search() != null) {
                params.put(getQ_Search(), q);
            } else {
                params.put("q", q);
            }
        }

        SpringViewHandler handler = new SpringViewHandler(this);
        handler.setBuilder(new BaseHandler_.Builder().isWait(true));
        handler.request(params, getTask());
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                List<T> list = (List<T>) result.get(getkey());

                List<Item> items = new ArrayList<>();
                if (result.containsKey(Key.ITEMS)) {
                    items = (ArrayList<Item>) result.get(Key.ITEMS);
                    mAdapter.clear();
                    mAdapter.addItems(0, items);
                    mAdapter.notifyDataSetChanged();
                }

                showSearchListView(true);
                backData(list, items);
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {

            }
        });
    }


    public void showSearchListView(boolean isShow) {
        mRecyclerView.setVisibility(!isShow ? View.GONE : View.VISIBLE);
    }

    @OnClick(R.id.search_btn)
    public void search() {
        String q = search.getText().toString();
        if (!q.equals("")) {
            request(q);
            hideSoftInput(search);
        } else {
            IToast.toast("请输入关键字");
        }
    }


    @OnClick(R.id.search_back)
    public void back() {
        finish();
    }


    protected String getTitleName() {
        return null;
    }

    protected String getSearchHint() {
        return null;
    }


    protected abstract String getkey();

    protected abstract Task getTask();

    protected abstract Map<String, Object> getParams();

//    protected abstract void backData(List<T> list);

    protected abstract void backData(List<T> list, List<Item> items);

    /**
     * 关键字Q
     *
     * @return
     */
    public String getQ_Search() {
        return null;
    }

}
