package com.hywy.luanhzt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs.common.adapter.BaseListAdapter;
import com.cs.common.utils.FileUtils;
import com.cs.common.utils.StringUtils;
import com.cs.common.utils.ViewHolder;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.entity.Video;


/**
 * 视频点gridview adapter
 *
 * @author Superman
 */
public class VideoGridAdapter extends BaseListAdapter<Video> {
    private Context context;

    public VideoGridAdapter(Context context) {
        super(context);
        this.context = context;
    }


    @Override
    protected View getView(LayoutInflater layoutInflater, View convertView, ViewGroup parent, final int position) {
        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.item_video_grid_layout, parent, false);

        TextView status = ViewHolder.get(convertView, R.id.status);
        ImageView imageView = ViewHolder.get(convertView, R.id.image);
        TextView address = ViewHolder.get(convertView, R.id.address);
        Video video = getItem(position);

        if (StringUtils.hasLength(video.getVIDNM())) {
            address.setText(video.getVIDNM());
        }
        if (video.getVDSTATE().equals("0")) {
            status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_red_point, 0, 0, 0);
        } else {
            status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_green_point, 0, 0, 0);
        }
//        String videoPath = "http://demo-videos.qnsdk.com/movies/Sunset.mp4";
//        imageView.setImageBitmap(FileUtils.getNetVideoBitmap(videoPath));
        status.setText(video.getVDSTATE().equals("0") ? "设备异常" : "设备正常");
        return convertView;
    }


}
