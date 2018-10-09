package com.hywy.luanhzt.adapter.item;

import android.animation.Animator;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.hywy.luanhzt.R;
import com.hywy.luanhzt.activity.UserTree2Activity;
import com.hywy.luanhzt.view.customer.MoreItemView;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.helpers.AnimatorHelper;
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem;
import eu.davidea.viewholders.ExpandableViewHolder;

/**
 * Created by weifei on 17/1/11.
 */

public class EvaluatedFormsExpandableItem extends AbstractExpandableHeaderItem<EvaluatedFormsExpandableItem.AnimatorExpandableViewHolder, ContactSubItem> {

    private String title;

    public EvaluatedFormsExpandableItem(String title) {
        super();
        this.title = title;
        setExpanded(false);//Start collapsed
        setSwipeable(true);
    }

    @Override
    public boolean equals(Object inObject) {
        if (inObject instanceof EvaluatedFormsExpandableItem) {
            EvaluatedFormsExpandableItem inItem = (EvaluatedFormsExpandableItem) inObject;
            return this.title.equals(inItem.title);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return title.hashCode();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.layout_contact_header;
    }

    @Override
    public AnimatorExpandableViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new AnimatorExpandableViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, AnimatorExpandableViewHolder holder, int position, List payloads) {

        holder.mTitle.setItemText(title + "");
        holder.mTitle.setRightIcon(R.drawable.bt_arrow_down_gray);
    }

    static class AnimatorExpandableViewHolder extends ExpandableViewHolder {

        public MoreItemView mTitle;

        public AnimatorExpandableViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter, true);//True for sticky
            mTitle = (MoreItemView) view.findViewById(R.id.title);

        }

        @Override
        protected void expandView(int position) {
            super.expandView(position);
            Log.e("expandView", "position:" + position);
//            if (mAdapter.isExpanded(position)) {
//                mAdapter.notifyItemChanged(position, true);
//                ViewCompat.animate(mTitle.getItemRightIcon())
//                        .rotation(90)
//                        .setDuration(300)
//                        .setInterpolator(new DecelerateInterpolator())
//                        .start();
//            }

        }

        @Override
        protected void collapseView(int position) {
            super.collapseView(position);
//            if (!mAdapter.isExpanded(position)) {
//                mAdapter.notifyItemChanged(position, true);
//                ViewCompat.animate(mTitle.getItemRightIcon())
//                        .rotation(0)
//                        .setDuration(300)
//                        .setInterpolator(new DecelerateInterpolator())
//                        .start();
//            }
        }


        @Override
        public void scrollAnimators(@NonNull List<Animator> animators, int position, boolean isForward) {
            if (isForward) {
                AnimatorHelper.alphaAnimator(animators, itemView, 0f);
            } else {
                AnimatorHelper.alphaAnimator(animators, itemView, 1);
            }
        }

    }

}