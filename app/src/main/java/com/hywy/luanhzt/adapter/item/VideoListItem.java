package com.hywy.luanhzt.adapter.item;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs.common.utils.ImageLoaderUtils;
import com.cs.common.utils.StringUtils;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.activity.VideoInfoActivity;
import com.hywy.luanhzt.adapter.VideoGridAdapter;
import com.hywy.luanhzt.adapter.WaterClassifyAdapter;
import com.hywy.luanhzt.entity.Contact;
import com.hywy.luanhzt.entity.Video;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.flexibleadapter.items.ISectionable;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * 视频监控item
 *
 * @author Superman
 */

public class VideoListItem extends AbstractFlexibleItem<VideoListItem.EntityViewHolder> implements ISectionable<VideoListItem.EntityViewHolder, MapClassifyHeaderItem>, IFilterable {
    private List<Video> videos;
    private MapClassifyHeaderItem headerItem;
    private VideoGridAdapter mAdapter;

    public List<Video> getData() {
        return videos;
    }

    public VideoListItem(List<Video> videos) {
        this.videos = videos;
    }

    public VideoListItem(List<Video> videos, MapClassifyHeaderItem header) {
        this.videos = videos;
        this.headerItem = header;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_video_list;
    }

    @Override
    public VideoListItem.EntityViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new VideoListItem.EntityViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public boolean filter(String constraint) {
        return false;
    }

    @Override
    public MapClassifyHeaderItem getHeader() {
        return headerItem;
    }

    @Override
    public void setHeader(MapClassifyHeaderItem header) {
        this.headerItem = header;
    }

    static class EntityViewHolder extends FlexibleViewHolder {
        GridView gridView;

        public EntityViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            gridView = (GridView) view.findViewById(R.id.gridview);
        }
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, EntityViewHolder holder, int position, List payloads) {
        mAdapter = new VideoGridAdapter(holder.gridView.getContext());
        if (StringUtils.isNotNullList(videos)) {
            mAdapter.setList(videos);
            holder.gridView.setAdapter(mAdapter);
            holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    VideoInfoActivity.startAction(holder.gridView.getContext(), videos.get(i));
                }
            });
//            //解决recycleview和gridview的点击冲突问题
//            holder.gridView.setClickable(false);
//            holder.gridView.setEnabled(false);
//            holder.gridView.setPressed(false);


        }
    }

    /**
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof VideoListItem) {
            VideoListItem odata = (VideoListItem) o;
            return videos.get(0).getVIDCD().equals(odata.getData().get(0).getVIDCD());
        }
        return false;
    }
}
