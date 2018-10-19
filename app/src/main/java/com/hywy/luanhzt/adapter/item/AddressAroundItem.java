package com.hywy.luanhzt.adapter.item;

import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.cs.common.utils.StringUtils;
import com.hywy.luanhzt.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;


/**
 * @author Superman
 */
public class AddressAroundItem extends AbstractFlexibleItem<AddressAroundItem.EntityViewHolder> {
    private PoiItem poiItem;
    private boolean isChecked;
    private String keyWord;//搜索关键字匹配

    public AddressAroundItem(PoiItem poiItem) {
        this.poiItem = poiItem;
    }

    public AddressAroundItem(PoiItem poiItem, String keyWord) {
        this.poiItem = poiItem;
        this.keyWord = keyWord;
    }

    public PoiItem getData() {
        return poiItem;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AddressAroundItem) {
            AddressAroundItem oItem = (AddressAroundItem) o;
            return poiItem.getPoiId() == oItem.getData().getPoiId();
        }
        return false;
    }


    @Override
    public int getLayoutRes() {
        return R.layout.item_address_arround;
    }

    @Override
    public EntityViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        AddressAroundItem.EntityViewHolder viewHolder = new EntityViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
        return viewHolder;
    }

    static class EntityViewHolder extends FlexibleViewHolder {
        TextView address;
        ImageView imageView;
        TextView title;

        public EntityViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            address = (TextView) view.findViewById(R.id.address);
            imageView = (ImageView) view.findViewById(R.id.iv);
            title = (TextView) view.findViewById(R.id.title);
        }
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, AddressAroundItem.EntityViewHolder holder, int position, List payloads) {
        if (StringUtils.hasLength(keyWord)) {
            matchKeyword(holder.title, poiItem.getTitle());
        } else {
            holder.title.setText(poiItem.getTitle());
        }
        if (StringUtils.hasLength(poiItem.getSnippet())) {
            if (StringUtils.hasLength(keyWord)) {
                matchKeyword(holder.address, poiItem.getSnippet());
            } else {
                holder.address.setText(poiItem.getSnippet());
            }
            holder.address.setVisibility(View.VISIBLE);
        } else {
            holder.address.setVisibility(View.GONE);
        }
        if (isChecked) {
            holder.imageView.setVisibility(View.VISIBLE);
        } else {
            holder.imageView.setVisibility(View.INVISIBLE);
        }
    }

    public void setChecked(boolean b) {
        isChecked = b;
    }

    /**
     * 匹配搜索关键字，显示颜色
     *
     * @param textView
     * @param str
     */
    private void matchKeyword(TextView textView, String str) {
        if (StringUtils.hasLength(str) && str.contains(keyWord)) {
            int index = str.indexOf(keyWord);
            int len = keyWord.length();
            Spanned temp = Html.fromHtml(str.substring(0, index)
                    + "<font color=#FF0000>"
                    + str.substring(index, index + len) + "</font>"
                    + str.substring(index + len, str.length()));

            textView.setText(temp);
        } else {
            textView.setText(str);
        }
    }

}
