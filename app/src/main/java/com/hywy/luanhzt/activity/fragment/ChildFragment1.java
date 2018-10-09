package com.hywy.luanhzt.activity.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cs.android.task.Task;
import com.cs.common.adapter.BaseListFlexAdapter;
import com.cs.common.adapter.layoutmanager.FullyLinearLayoutManager;
import com.cs.common.base.BaseFragment;
import com.cs.common.handler.BaseHandler_;
import com.cs.common.handler.SpringViewHandler;
import com.cs.common.listener.OnPostListenter;
import com.cs.common.utils.IToast;
import com.cs.common.utils.ImageLoaderUtils;
import com.cs.common.utils.Logger;
import com.cs.common.utils.StringUtils;
import com.cs.common.view.SwipeRefreshview;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hywy.luanhzt.AppConfig;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.activity.AllRiverActivity;
import com.hywy.luanhzt.activity.AllXunchaActivity;
import com.hywy.luanhzt.activity.MenuManageActivity;
import com.hywy.luanhzt.activity.MyRiverActivity;
import com.hywy.luanhzt.activity.NewsInfoActivity;
import com.hywy.luanhzt.activity.NotifyActivity;
import com.hywy.luanhzt.activity.RiverDetailsActivity;
import com.hywy.luanhzt.activity.XcDetailsActivity;
import com.hywy.luanhzt.adapter.RiverGridAdapter;
import com.hywy.luanhzt.adapter.item.InspectionItem;
import com.hywy.luanhzt.adapter.item.RiverGridItem;
import com.hywy.luanhzt.adapter.item.RiverItem;
import com.hywy.luanhzt.adapter.menu.IndexDataAdapter;
import com.hywy.luanhzt.app.App;
import com.hywy.luanhzt.configure.AppConfigure;
import com.hywy.luanhzt.entity.AppMenu;
import com.hywy.luanhzt.entity.CompanyContact;
import com.hywy.luanhzt.entity.Inspection;
import com.hywy.luanhzt.entity.MenuEntity;
import com.hywy.luanhzt.entity.News;
import com.hywy.luanhzt.entity.Notify;
import com.hywy.luanhzt.entity.River;
import com.hywy.luanhzt.key.Key;
import com.hywy.luanhzt.task.GetMyRiverListTask;
import com.hywy.luanhzt.task.GetMyXcTask;
import com.hywy.luanhzt.task.GetNewsListTask;
import com.hywy.luanhzt.task.GetNotifyListTask;
import com.hywy.luanhzt.task.GetRiverListTask;
import com.hywy.luanhzt.view.GridViewForScrollView;
import com.hywy.luanhzt.view.MyScrollview;
import com.hywy.luanhzt.view.customer.BannerView;
import com.paradoxie.autoscrolltextview.VerticalTextview;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import rx.functions.Action1;

import static com.hywy.luanhzt.action.RxAction.MAIN_FRAGMENT_1_REFRESH;


/**
 * Created by weifei on 17/1/3.
 */

public class ChildFragment1 extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, FlexibleAdapter.OnItemClickListener, ViewTreeObserver.OnGlobalLayoutListener {
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    //    @Bind(R.id.river_recy)
//    RecyclerView riverRecy;
    @Bind(R.id.main_title_layout)
    LinearLayout mainTitleLayout;
    @Bind(R.id.swipeRefresh)
    SwipeRefreshview swipeRefresh;
    @Bind(R.id.scrollview)
    MyScrollview scrollView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.all_xuncha)
    TextView allXuncha;

    @Bind(R.id.bannerview)
    BannerView bannerView;

    @Bind(R.id.gv_lanuch_start)
    GridViewForScrollView gridView;

    @Bind(R.id.gridview)
    GridViewForScrollView gridViewRiver;

    @Bind(R.id.river_all)
    TextView allRiver;
    @Bind(R.id.tv_notify)
    VerticalTextview tvNotify;
    @Bind(R.id.tv_myriver)
    TextView tvMyriver;

    private static App appContext;
    ;
    private List<MenuEntity> indexDataAll = new ArrayList<MenuEntity>();
    private List<MenuEntity> indexDataList = new ArrayList<MenuEntity>();
    private IndexDataAdapter adapter;
    private final static String fileName = "menulist";

    List<View> mItems;

    ImageView[] mBottomImgs;


    private String[] appkes;
    Integer menuImages1[] = {R.drawable.ic_image_loading, R.drawable.ic_image_loading, R.drawable.ic_image_loading, R.drawable.ic_image_loading, R.drawable.ic_image_loading, R.drawable.ic_image_loading, R.drawable.ic_image_loading};
    private int currentViewPagerItem;

    private BaseListFlexAdapter mAdapter;
    private RiverGridAdapter riverGridAdapter;

    private ArrayList<String> titleList = new ArrayList<String>();

    public static ChildFragment1 newInstance(String title) {
        Bundle args = new Bundle();
        ChildFragment1 fragment = new ChildFragment1();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main1;
    }

    @Override
    protected void initView() {
        init();
        initMenu();
        initNews();
        initNotify();
        refreshListener();
        createTaskListener();
    }

    private void initNews() {
        SpringViewHandler handler = new SpringViewHandler(getActivity());
        Map<String, Object> params = new HashMap<>();
        handler.setBuilder(new BaseHandler_.Builder().isShowToast(false).isWait(false));
        handler.request(params, new GetNewsListTask(getActivity()));
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                List<News> list = (List<News>) result.get(Key.RESULT);
                if (StringUtils.isNotNullList(list)) {
                    for (News news : list) {
                        addImageView(news);
                    }
                    mBottomImgs = new ImageView[mItems.size()];
                    bannerView.startLoop(true);
                    bannerView.setViewList(mItems);
                }
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {

            }
        });
    }

    private void initNotify() {
        tvNotify.setText(13, 0, ContextCompat.getColor(getActivity(), R.color.font_3));//设置属性
        tvNotify.setTextStillTime(3000);//设置停留时长间隔
        tvNotify.setAnimTime(300);//设置进入和退出的时间间隔
        SpringViewHandler handler = new SpringViewHandler(getActivity());
        Map<String, Object> params = new HashMap<>();
        handler.setBuilder(new BaseHandler_.Builder().isShowToast(false).isWait(false));
        handler.request(params, new GetNotifyListTask(getActivity()));
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                List<Notify> list = (List<Notify>) result.get(Key.RESULT);
                if (StringUtils.isNotNullList(list)) {
                    for (Notify notify : list) {
                        titleList.add(notify.getINFO());
                    }
//                    tvNotify.setText(titleList.get(0));
                    tvNotify.setTextList(titleList);

                }
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {

            }
        });
    }


    private void init() {
        appContext = (App) getActivity().getApplication();

//        toolbar.setTitle(this.getArguments().getString("title"));
        toolbar.setTitleTextColor(ContextCompat.getColor(getContext(), R.color.white));//需要设置下字体颜色，style文件已经设置为白色一直弄不上
        title.setText(getArguments().getString("title"));

        swipeRefresh.setMode(SwipeRefreshview.Mode.DISABLED);
        swipeRefresh.setAdapter(recyclerview);
        swipeRefresh.getAdapter().initializeListeners(this);

        mItems = new ArrayList<>();

        riverGridAdapter = new RiverGridAdapter(getActivity());
        gridViewRiver.setAdapter(riverGridAdapter);

//        riverRecy.setLayoutManager(new FullyLinearLayoutManager(getActivity()));
//        mAdapter = new BaseListFlexAdapter(getActivity());
//        riverRecy.setAdapter(mAdapter);
//        mAdapter.initializeListeners(new FlexibleAdapter.OnItemClickListener() {
//            @Override
//            public boolean onItemClick(int position) {
//                startActivity(new Intent(getActivity(), MyRiverActivity.class));
//                return false;
//            }
//        });

        initListVisibility();

        if(StringUtils.hasLength(App.getInstance().getAccount().getPARENT_ID())){
            if (App.getInstance().getAccount().getPARENT_ID().equals("2")) {
                tvMyriver.setText("我的河道");
            } else {
                tvMyriver.setText("辖区河道");
            }
        }

    }

    /**
     * 数据展示区
     */
    private void initListVisibility() {
        if (appContext.getMenu1() != null) {
            AppMenu menu = appContext.getMenu1();
            if (StringUtils.isNotNullList(menu.getSubMenu())) {
                for (AppMenu m : menu.getSubMenu()) {
                    if (m.getMENU_KEY().equals("shujuzhanshiqu")) {
                        if (StringUtils.isNotNullList(m.getSubMenu())) {
                            for (AppMenu am : m.getSubMenu()) {
                                if (am.getMENU_KEY().equals("xuncharizhi")) {
                                    getActivity().findViewById(R.id.xc_layout).setVisibility(View.VISIBLE);
                                    initData();
                                } else if (am.getMENU_KEY().equals("wodehedao")) {
                                    getActivity().findViewById(R.id.river_layout).setVisibility(View.VISIBLE);
                                    initRiver();
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private void initData() {
        Map<String, Object> params = new HashMap<>();
        Task task = new GetMyXcTask(getContext(), 1);

        swipeRefresh.builder(new SwipeRefreshview.Builder()
                .task(task).params(params));
        swipeRefresh.isToast(false);
        swipeRefresh.refresh();

    }

    private void initRiver() {
//        IToast.toast(App.getInstance().getAccount().getUserId()+"");

        SpringViewHandler handler = new SpringViewHandler(getActivity());
        Map<String, Object> params = new HashMap<>();
        handler.setBuilder(new BaseHandler_.Builder().isShowToast(false).isWait(false));
        handler.request(params, new GetMyRiverListTask(getActivity()));
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
//                mAdapter.clear();
                List<River> list = (List<River>) result.get(Key.RESULT);
                if (StringUtils.isNotNullList(list)) {
//                    for (River river : list) {
//                        RiverGridItem item = new RiverGridItem(river);
//                        mAdapter.addItem(item);
//                    }
//                    mAdapter.notifyDataSetChanged();
                    riverGridAdapter.setList(list);
                    riverGridAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {

            }
        });
    }

    /**
     * 添加图片
     *
     * @param news
     */
    private void addImageView(News news) {
        ImageView view0 = new ImageView(getContext());
        view0.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (StringUtils.hasLength(news.getIMG_URL())) {
            ImageLoaderUtils.display(getActivity(), view0, news.getIMG_URL(), R.drawable.icon_empty_image);
            view0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NewsInfoActivity.startAction(getActivity(), news);
                }
            });
        }

        mItems.add(view0);
    }

    /********查看所有巡查日志******/
    @OnClick(R.id.all_xuncha)
    public void toXuncha() {
        startActivity(new Intent(getActivity(), AllXunchaActivity.class));
    }

    @OnClick({R.id.river_all})
    public void toRiver() {
        startActivity(new Intent(getActivity(), MyRiverActivity.class));
    }

    @OnClick(R.id.notify_layout)
    public void toNotify() {
        NotifyActivity.startAction(getContext());
    }


    /**
     * 刷新功能监听
     */
    private void refreshListener() {
        mRxManager.on(MAIN_FRAGMENT_1_REFRESH, new Action1<String>() {
            @Override
            public void call(String s) {
                initData();
                initRiver();
            }
        });
    }

    /**
     * 初始化菜单
     */
    private void initMenu() {
        gridView.setFocusable(false);
        String strByJson = getJson(getContext(), fileName);

        indexDataAll = AppConfigure.saveMenuEntities(App.getInstance().getMenu1().getSubMenu(), strByJson);
        appContext.saveObject((Serializable) indexDataAll, AppConfig.KEY_All);
        List<MenuEntity> indexDataUser = (List<MenuEntity>) appContext.readObject(AppConfig.KEY_USER);
        //如果indexDataUser为null或者size为0则说明app是第一次进入，key_user为首页菜单集合。第一次进去默认拿menulist文件里的前七个
        List<MenuEntity> mainList = new ArrayList<>();
        if (indexDataUser == null || indexDataUser.size() == 0) {
            for (int i = 0; i < indexDataAll.size(); i++) {
                if (i < 7) {
                    mainList.add(indexDataAll.get(i));
                }
                ;
            }
            boolean b = appContext.saveObject((Serializable) mainList, AppConfig.KEY_USER);
        }
        indexDataList = (List<MenuEntity>) appContext.readObject(AppConfig.KEY_USER);

        MenuEntity allMenuEntity = new MenuEntity();
        allMenuEntity.setIco("");
        allMenuEntity.setId("all");
        allMenuEntity.setTitle("更多");
        indexDataList.add(allMenuEntity);
        adapter = new IndexDataAdapter(getContext(), indexDataList);
        gridView.setAdapter(adapter);
        Logger.e(strByJson);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MenuEntity menuEntity = indexDataList.get(position);
                AppConfigure.startAction(getContext(), menuEntity);
            }
        });
    }

    public String getJson(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Logger.e(e.toString());
        }
        return stringBuilder.toString();
    }

    @Override
    public void onResume() {
        super.onResume();
        tvNotify.startAutoScroll();
        indexDataList.clear();
        indexDataList = (List<MenuEntity>) appContext.readObject(AppConfig.KEY_USER);
        MenuEntity allMenuEntity = new MenuEntity();
        allMenuEntity.setIco("all_big_ico");
        allMenuEntity.setId("all");
        allMenuEntity.setTitle("更多");
        indexDataList.add(allMenuEntity);
        adapter = new IndexDataAdapter(getContext(), indexDataList);
        gridView.setAdapter(adapter);

    }

    @Override
    public void onPause() {
        super.onPause();
        tvNotify.stopAutoScroll();
    }

    /**
     * 监听fragment布局的变化，布局画完成之后，添加蒙层。
     */
    private void addGlobalListener() {
        getActivity().getWindow()
                .getDecorView()
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        removeListener();
    }

    private void removeListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getActivity().getWindow()
                    .getDecorView()
                    .getViewTreeObserver()
                    .removeOnGlobalLayoutListener(this);
        } else {
            getActivity().getWindow()
                    .getDecorView()
                    .getViewTreeObserver()
                    .removeGlobalOnLayoutListener(this);
        }
    }

    @Override
    public void onRefresh() {
        initData();
        initRiver();
    }

    private void createTaskListener() {
        tvNotify.setOnItemClickListener(new VerticalTextview.OnItemClickListener() {
            @Override
            public void onItemClick(int i) {
                NotifyActivity.startAction(getContext());
            }
        });

        gridViewRiver.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                River river = (River) gridViewRiver.getItemAtPosition(i);
                RiverDetailsActivity.startAction(getActivity(), river);
            }
        });
    }


    @Override
    public boolean onItemClick(int position) {
        InspectionItem item = (InspectionItem) swipeRefresh.getAdapter().getItem(position);
        Inspection inspection = item.getData();
        XcDetailsActivity.startAction(getActivity(), inspection);
        return false;
    }

}
