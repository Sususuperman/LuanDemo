package com.hywy.luanhzt.adapter.item;

import android.animation.Animator;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs.common.utils.ImageLoaderUtils;
import com.cs.common.utils.PhoneUtil;
import com.cs.common.utils.StringUtils;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.entity.HzContact;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.helpers.AnimatorHelper;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by weifei on 17/1/11.
 */

public class ContactSubItem extends AbstractSectionableItem<ContactSubItem.ChildViewHolder, EvaluatedFormsExpandableItem> {

    private HzContact.DealsBean contact;

    public ContactSubItem(HzContact.DealsBean data, EvaluatedFormsExpandableItem header) {
        super(header);

        this.contact = data;
        setSwipeable(true);
    }

    public HzContact.DealsBean getData() {
        return contact;
    }

    @Override
    public boolean equals(Object inObject) {
        if (inObject instanceof ContactSubItem) {
            ContactSubItem inItem = (ContactSubItem) inObject;
            return contact.getUSER_ID() == inItem.getData().getUSER_ID();
        }
        return false;
    }


    @Override
    public int getLayoutRes() {
        return R.layout.item_contact;
    }

    @Override
    public ContactSubItem.ChildViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ContactSubItem.ChildViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ContactSubItem.ChildViewHolder holder, int position, List payloads) {
        if (contact != null) {
            if (StringUtils.hasLength(contact.getPER_NAME())) {
                holder.tv_name.setText(contact.getPER_NAME());
            }
            if (StringUtils.hasLength(contact.getPER_D())) {
                holder.role_name.setText(contact.getPER_D());
            }
            if (StringUtils.hasLength(contact.getIMAGE_URL())) {
                ImageLoaderUtils.display(holder.iv_head.getContext(), holder.iv_head, contact.getIMAGE_URL());
            }
        }


    }

    /**
     * Provide a reference to the views for each data item.
     * Complex data labels may need more than one view per item, and
     * you provide access to all the views for a data item in a view holder.
     */
    static final class ChildViewHolder extends FlexibleViewHolder {
        TextView tv_name;
        TextView role_name;
        ImageView iv_head;

        public ChildViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            tv_name = (TextView) view.findViewById(R.id.name);
            role_name = (TextView) view.findViewById(R.id.tv_role);
            iv_head = (ImageView) view.findViewById(R.id.iv_head);
        }

        @Override
        public float getActivationElevation() {
            return PhoneUtil.dp2px(itemView.getContext(), 4);
        }

        @Override
        public void scrollAnimators(@NonNull List<Animator> animators, int position, boolean isForward) {
            if (isForward) {
                AnimatorHelper.alphaAnimator(animators, itemView, 0f);
            } else {
                AnimatorHelper.alphaAnimator(animators, itemView, 1f);
            }
        }
    }

    @Override
    public String toString() {
        return "SubItem[" + super.toString() + "]";
    }


    private String getStringText(String content) {
        if (TextUtils.isEmpty(content)) {
            return "";
        } else {
            return content;
        }
    }
}