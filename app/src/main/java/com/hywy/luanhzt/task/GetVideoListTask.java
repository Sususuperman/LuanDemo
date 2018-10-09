package com.hywy.luanhzt.task;

import android.content.Context;

import com.cs.common.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.adapter.item.MapClassifyHeaderItem;
import com.hywy.luanhzt.adapter.item.VideoListItem;
import com.hywy.luanhzt.entity.Video;
import com.hywy.luanhzt.entity.YuJing;
import com.hywy.luanhzt.key.Key;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 获取视频点列表接口
 *
 * @author Superman
 */

public class GetVideoListTask extends BaseRequestTask {

    public GetVideoListTask(Context context) {
        super(context);
    }

    @Override
    public String url() {
        return HttpUrl.getUrl(HttpUrl.RMS_APP_VIDEO_VIDEOLIST);
    }

    @Override
    public Object request(String json) throws Exception {
        Type type = new TypeToken<List<Video>>() {
        }.getType();
        List<Video> list = new Gson().fromJson(json, type);
        Map<String, List<Video>> map = new TreeMap<>();//map用于存放list中含有相同id的元素
        if (StringUtils.isNotNullList(list)) {
            for (Video video : list) {
                if (map.containsKey(video.getREACH_CODE())) {//map中存有id相同的元素，就添加，没有就创建
                    List<Video> videos = map.get(video.getREACH_CODE());
                    videos.add(video);
                    map.put(video.getREACH_CODE(), videos);
                } else {
                    List<Video> videos = new ArrayList<>();
                    videos.add(video);
                    map.put(video.getREACH_CODE(), videos);
                }
            }

            List<VideoListItem> items = new ArrayList<>();
            //遍历map取到每一个list然后创建gridview以及recycleview 子item的头部
            for (Map.Entry<String, List<Video>> entry : map.entrySet()) {
                VideoListItem item = new VideoListItem(entry.getValue(), new MapClassifyHeaderItem(entry.getKey()));
                items.add(item);
            }
            result.put(Key.ITEMS, items);
        }

        return list;
    }

    @Override
    public boolean isPost() {
        return false;
    }
}
