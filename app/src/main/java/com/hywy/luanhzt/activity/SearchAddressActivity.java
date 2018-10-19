package com.hywy.luanhzt.activity;

import android.app.Activity;
import android.content.Intent;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.cs.android.task.Task;
import com.cs.common.adapter.item.ProgressItem;
import com.cs.common.utils.StringUtils;
import com.hywy.luanhzt.adapter.item.AddressAroundItem;
import com.hywy.luanhzt.key.Key;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.davidea.flexibleadapter.FlexibleAdapter;

/**
 * @author Superman
 */
public class SearchAddressActivity extends BaseMapSearchActivity implements PoiSearch.OnPoiSearchListener, FlexibleAdapter.OnItemClickListener {
    String status;
    public static final int request_code_map_search = 15256;
    private int pageNum = 1;

    public static void startAction(Activity context) {
        Intent intent = new Intent(context, SearchAddressActivity.class);
        context.startActivityForResult(intent, request_code_map_search);
    }

    @Override
    protected void init() {
        super.init();
        initListeners();
    }

    private void initListeners() {
        mAdapter.setEndlessScrollListener(new FlexibleAdapter.EndlessScrollListener() {
            @Override
            public void onLoadMore() {
                if (!mAdapter.isNomore()) {
                    mAdapter.setClear(false);
                    mAdapter.setPage(mAdapter.getPage() + 1);
                    pageNum = mAdapter.getPage() + 1;
                    searchPoi();
                } else {
                    mAdapter.onLoadMoreComplete(null);
                }
            }
        }, new ProgressItem());

    }


    @Override
    public void search() {
        String key = search.getText().toString();
        mAdapter.clear();
        if (StringUtils.hasLength(key)) {
            pageNum = 1;
            searchPoi();
        } else {
            mAdapter.notifyDataSetChanged();
        }

    }

    private void searchPoi() {
        PoiSearch.Query query = new PoiSearch.Query(search.getText().toString(), "", "");
        query.setPageSize(20);
        query.setPageNum(pageNum);
        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
        if (pageNum == 1) {
            showDialog("请稍后...");
        }
    }


    @Override
    protected String getTitleName() {
        return "";
    }

    @Override
    protected String getSearchHint() {
        return "搜索地点";
    }

    @Override
    protected String getkey() {
        return Key.ITEMS;
    }

    @Override
    protected Task getTask() {
        return null;
    }

    @Override
    protected Map<String, Object> getParams() {
        Map<String, Object> params = new HashMap<>();
        return params;
    }

    @Override
    protected void backData(List list, List list2) {

    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        dismissDialog();
        List<AddressAroundItem> items = new ArrayList<>();
        ArrayList<PoiItem> list = poiResult.getPois();
        showSearchListView(true);
        for (int j = 0; j < list.size(); j++) {
            com.amap.api.services.core.PoiItem poiItem = list.get(j);
            AddressAroundItem addressAroundItem = new AddressAroundItem(poiItem, search.getText().toString());
//            if (StringUtils.hasLength(poiItem.getTitle())) {
            items.add(addressAroundItem);
//            }
        }
        mAdapter.addItems(mAdapter.getItemCount() - 1, items);
        mAdapter.notifyDataSetChanged();
        mAdapter.setNomore(list.size() < mAdapter.getCount());
        mAdapter.onLoadMoreComplete(items);
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public boolean onItemClick(int position) {
        AddressAroundItem item = (AddressAroundItem) mAdapter.getItem(position);
        PoiItem poiItem = item.getData();
        Intent intent = new Intent();
        intent.putExtra("poiitem", poiItem);
        setResult(RESULT_OK, intent);
        finish();
        return false;
    }

}
