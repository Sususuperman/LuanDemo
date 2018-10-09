package com.hywy.luanhzt.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.cs.android.task.Task;
import com.cs.common.adapter.BaseListFlexAdapter;
import com.cs.common.adapter.layoutmanager.FullyLinearLayoutManager;
import com.cs.common.base.BaseToolbarActivity;
import com.cs.common.handler.BaseHandler_;
import com.cs.common.handler.SpringViewHandler;
import com.cs.common.listener.OnPostListenter;
import com.cs.common.utils.DateUtils;
import com.cs.common.utils.DialogTools;
import com.cs.common.utils.FileUtils;
import com.cs.common.utils.IToast;
import com.cs.common.utils.StringUtils;
import com.cs.common.view.ImagePagerActivity;
import com.cs.common.view.MyProgressDialog;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.action.RxAction;
import com.hywy.luanhzt.adapter.ImagesAdapter;
import com.hywy.luanhzt.adapter.SpinnerListAdapter;
import com.hywy.luanhzt.adapter.item.OptionsItem;
import com.hywy.luanhzt.app.App;
import com.hywy.luanhzt.entity.Adnm;
import com.hywy.luanhzt.entity.AttachMent;
import com.hywy.luanhzt.entity.EventSupervise;
import com.hywy.luanhzt.entity.Plan;
import com.hywy.luanhzt.entity.River;
import com.hywy.luanhzt.entity.RiverCourse;
import com.hywy.luanhzt.entity.User;
import com.hywy.luanhzt.entity.UserTree;
import com.hywy.luanhzt.entity.WaterQuality;
import com.hywy.luanhzt.key.Key;
import com.hywy.luanhzt.task.GetAddressListTask;
import com.hywy.luanhzt.task.GetEventShuiwenTask;
import com.hywy.luanhzt.task.GetEventShuizhiTask;
import com.hywy.luanhzt.task.GetEventTypeTask;
import com.hywy.luanhzt.task.GetRiverListInMobileTask;
import com.hywy.luanhzt.task.GetRiverNameListTask;
import com.hywy.luanhzt.task.GetUserListTask;
import com.hywy.luanhzt.task.PostCreateEventProgramTask;
import com.hywy.luanhzt.task.PostCreateLogTask;
import com.hywy.luanhzt.treeview.bean.BranchNode;
import com.hywy.luanhzt.treeview.bean.LeafNode;
import com.hywy.luanhzt.treeview.bean.RootNode;
import com.hywy.luanhzt.view.dialog.dialogplus.DialogPlus;
import com.hywy.luanhzt.view.dialog.dialogplus.ListHolder;
import com.hywy.luanhzt.view.dialog.dialogplus.OnItemClickListener;
import com.superman.treeview.bean.LayoutItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.entity.Photo;
import rx.functions.Action1;

public class CreateEventActivity extends BaseToolbarActivity implements ImagesAdapter.onDeleteListener, AdapterView.OnItemClickListener, OnItemClickListener {

    @Bind(R.id.event_name)
    TextView eventName;
    @Bind(R.id.et_content)
    EditText etContent;
    @Bind(R.id.image_grid)
    GridView imageGrid;
    @Bind(R.id.btn)
    Button btn;
    private ImagesAdapter imageAdapter;

    @Bind(R.id.tv_type)
    TextView tv_type;
    @Bind(R.id.tv_river)
    TextView tv_river;
    @Bind(R.id.tv_shuizhi)
    TextView tv_shuizhi;
    @Bind(R.id.tv_shuiwen)
    TextView tv_shuiwen;
    @Bind(R.id.tv_user)
    TextView tv_user;
    @Bind(R.id.tv_limit)
    TextView tv_limit;

    private Plan plan;
    private String jsonArray;
    private BaseListFlexAdapter mAdapter;

    private JSONArray array3;
    private List<River> rivers;
    private List<Adnm> adnms;
    private List<EventSupervise> events;
    private SpinnerListAdapter spinnerAdapter1;
    private long logId;
    private List<WaterQuality> waterQualitys;
    private List<RiverCourse> riverCourses;
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        init();
        initData();
        initListeners();
    }

    private void initListeners() {
        mRxManager.on(RxAction.EVENT_CHOOSE_USER, new Action1<LayoutItem>() {
            @Override
            public void call(LayoutItem item) {
                String name = "";
                String id = "";
                if (item instanceof RootNode) {
                    name = ((RootNode) item).getName();
                    id = ((RootNode) item).getId();
                } else if (item instanceof BranchNode) {
                    name = ((BranchNode) item).getName();
                    id = ((BranchNode) item).getId();
                } else if (item instanceof LeafNode) {
                    name = ((LeafNode) item).getName();
                    id = ((LeafNode) item).getId();
                }
                tv_user.setText(name);
                tv_user.setTag(id);
            }
        });
    }


    public static void startAction(Activity activity) {
        Intent intent = new Intent(activity, CreateEventActivity.class);
        activity.startActivity(intent);
    }

    private void init() {
        setTitleBulider(new Bulider().backicon(R.drawable.ic_arrow_back_white_24dp).title("事务立项"));
        imageAdapter = new ImagesAdapter(this);
        imageGrid.setAdapter(imageAdapter);
        imageAdapter.setOnDeleteListener(this);
        imageGrid.setOnItemClickListener(this);
        spinnerAdapter1 = new SpinnerListAdapter(this);
        addDefaultImage();

        mAdapter = new BaseListFlexAdapter(this);

    }

    private void initData() {
        initRiverList();
        initEventList();
        initShuizhiList();
        initShuiwenList();
    }

    private void initShuizhiList() {
        SpringViewHandler handler = new SpringViewHandler(this);
        handler.setBuilder(new BaseHandler_.Builder().isShowToast(false));
        Map<String, Object> params = new HashMap<>();
        handler.request(params, new GetEventShuizhiTask(this));
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                waterQualitys = (List<WaterQuality>) result.get(Key.RESULT);
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {

            }
        });
    }

    private void initShuiwenList() {
        SpringViewHandler handler = new SpringViewHandler(this);
        handler.setBuilder(new BaseHandler_.Builder().isShowToast(false));
        Map<String, Object> params = new HashMap<>();
        handler.request(params, new GetEventShuiwenTask(this));
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                riverCourses = (List<RiverCourse>) result.get(Key.RESULT);
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {

            }
        });
    }


    private void initRiverList() {
        SpringViewHandler handler = new SpringViewHandler(this);
        handler.setBuilder(new BaseHandler_.Builder().isShowToast(false));
        Map<String, Object> params = new HashMap<>();
        handler.request(params, new GetRiverNameListTask(this));
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                rivers = (List<River>) result.get(Key.RESULT);
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {

            }
        });
    }

    private void initEventList() {
        SpringViewHandler handler = new SpringViewHandler(this);
        handler.setBuilder(new BaseHandler_.Builder().isShowToast(false));
        Map<String, Object> params = new HashMap<>();
        handler.request(params, new GetEventTypeTask(this));
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                events = (List<EventSupervise>) result.get(Key.RESULT);
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {

            }
        });
    }

    private void initUserList() {
        SpringViewHandler handler = new SpringViewHandler(this);
        handler.setBuilder(new BaseHandler_.Builder().isShowToast(false));
        Map<String, Object> params = new HashMap<>();
        handler.request(params, new GetUserListTask(this));
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                users = (List<User>) result.get(com.hywy.luanhzt.key.Key.RESULT);
                showUserList(users);
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {

            }
        });
    }


    @OnClick({R.id.river_layout, R.id.type_layout, R.id.shuizhi_layout, R.id.shuiwen_layout, R.id.user_layout, R.id.limit_layout})
    public void click(View v) {
        switch (v.getId()) {
            case R.id.river_layout:
                showRiverList(rivers);
                break;
            case R.id.type_layout:
                showEventList(events);
                break;
            case R.id.shuizhi_layout:
                showShuizhiList(waterQualitys);
                break;
            case R.id.shuiwen_layout:
                showShuiwenList(riverCourses);
                break;
            case R.id.user_layout:
                UserTreeActivity.startAction(this);
//                initUserList();
                break;
            case R.id.limit_layout:
                showTimeDialog(tv_limit);
                break;
        }
    }

    /**
     * 选择时间和日期
     *
     * @param tv
     */
    private void showTimeDialog(final TextView tv) {
        final MyProgressDialog progressDialog = DialogTools.showDateDialog(this, System.currentTimeMillis(), MyProgressDialog.DIALOG_DATEPICKER, new MyProgressDialog.OnDatePickerClickListener() {
            @Override
            public void datePickerConfirmClick(long dateTime) {
                tv.setText(DateUtils.transforMillToDateInfo(dateTime / 1000));
            }

            @Override
            public void datePickerCancelClick() {

            }
        });
    }

    private void showRiverList(List<River> list) {
        spinnerAdapter1.setList(list);
        showRadioDialog();
    }

    private void showEventList(List<EventSupervise> list) {
        spinnerAdapter1.setList(list);
        showRadioDialog();
    }

    private void showShuizhiList(List<WaterQuality> list) {
        spinnerAdapter1.setList(list);
        showRadioDialog();
    }

    private void showShuiwenList(List<RiverCourse> list) {
        spinnerAdapter1.setList(list);
        showRadioDialog();
    }

    private void showUserList(List<User> list) {
        spinnerAdapter1.setList(list);
        showRadioDialog();
    }

    /**
     * 显示单选dialog
     */
    private void showRadioDialog() {
        DialogPlus dialogPlus = new DialogPlus.Builder(this)
                .setContentHolder(new ListHolder())
                .setAdapter(spinnerAdapter1)
                .setGravity(DialogPlus.Gravity.CENTER)
                .setCancelable(true)
                .setInAnimation(0)
                .setOutAnimation(0)
                .setOnItemClickListener(this)
                .create();
        View view = dialogPlus.getHolderView();
        view.setBackgroundResource(R.drawable.bg_btn_default);
        dialogPlus.show();
    }

    @Override
    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
        Object object = spinnerAdapter1.getItem(position);
        if (object instanceof EventSupervise) {
            EventSupervise eventSupervise = (EventSupervise) object;
            tv_type.setTag(eventSupervise);
            tv_type.setText(eventSupervise.getEVENT_TYPE_NAME());
        } else if (object instanceof River) {
            River river = (River) object;
            tv_river.setTag(river);
            tv_river.setText(river.getREACH_NAME());
        } else if (object instanceof WaterQuality) {
            WaterQuality waterQuality = (WaterQuality) object;
            tv_shuizhi.setTag(waterQuality);
            tv_shuizhi.setText(waterQuality.getSTNM());
        } else if (object instanceof RiverCourse) {
            RiverCourse riverCourse = (RiverCourse) object;
            tv_shuiwen.setTag(riverCourse);
            tv_shuiwen.setText(riverCourse.getSTNM());
        } else if (object instanceof User) {
            User user = (User) object;
            tv_user.setTag(user);
            tv_user.setText(user.getNAME());
        }
        dialog.dismiss();
    }

    private void addDefaultImage() {
        AttachMent attachMent = createNewAttach(null);
        imageAdapter.add(imageAdapter.getCount(), attachMent);
        imageAdapter.notifyDataSetChanged();
    }

    /**
     * 创建附件
     *
     * @param path
     * @return
     */
    private AttachMent createNewAttach(String path) {
        AttachMent attachMent = new AttachMent();
        attachMent.setATTACH_URL(path);
        attachMent.setStatus(AttachMent.UPLOAD_UN);
        attachMent.setATTACH_NAME(StringUtils.getFilename(path));
        return attachMent;
    }

    @OnClick(R.id.btn)
    public void submit() {
        if ("".equals(eventName.getText().toString().trim())) {
            IToast.toast("请输入事物名称");
            return;
        }
        if ("".equals(etContent.getText().toString().trim())) {
            IToast.toast("请输入事物内容");
            return;
        }
        if (tv_type.getTag() == null) {
            IToast.toast("请选择事物来源");
            return;
        }
        if (tv_river.getTag() == null) {
            IToast.toast("请选择所属河段");
            return;
        }
//        if (tv_shuiwen.getTag() == null) {
//            IToast.toast("请选择相关水文站");
//            return;
//        }
//        if (tv_shuizhi.getTag() == null) {
//            IToast.toast("请选择相关水质站");
//            return;
//        }
        if (tv_user.getTag() == null) {
            IToast.toast("请选择接收人员");
            return;
        }
        if (!StringUtils.hasLength(tv_limit.getText().toString())) {
            IToast.toast("请选择完成时限");
            return;
        }
        SpringViewHandler handler = new SpringViewHandler(this);
        Task task = new PostCreateEventProgramTask(this);
        try {
            handler.request(getParams(), task);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                IToast.toast("成功");
                mRxManager.post(RxAction.ACTION_EVENT_CREATE, "");
                finish();
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {
//                IToast.toast(R.string.submit_fail);
            }
        });

    }

    /**
     * 请求参数
     */
    private Map<String, Object> getParams() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("EVENT_NAME", eventName.getText().toString());
        obj.put("PER_ID", App.getInstance().getAccount().getUserId());
        obj.put("USER_ID", App.getInstance().getAccount().getUserId());
        EventSupervise eventSupervise = (EventSupervise) tv_type.getTag();
        obj.put("EVENT_TYPE_ID", eventSupervise.getEVENT_TYPE_ID());
        River r = (River) tv_river.getTag();
        obj.put("REACH_CODE", r.getREACH_CODE());
        WaterQuality waterQuality = (WaterQuality) tv_shuizhi.getTag();
        obj.put("STBPRP_B", waterQuality.getSTCD());
        RiverCourse riverCourse = (RiverCourse) tv_shuiwen.getTag();
        obj.put("STBPRP_R", riverCourse.getSTCD());

        obj.put("STARTTIME", DateUtils.transforMillToDateInfo(System.currentTimeMillis() / 1000));
        obj.put("EVENT_CONT", etContent.getText().toString());
//        User user = (User) tv_user.getTag();
        String id = (String) tv_user.getTag();
        obj.put("DEAL_PER_ID", id);
        obj.put("LIMITTIME", tv_limit.getText().toString());
        Map<String, Object> params = new HashMap<>();

        array3 = getUploadImage();
        obj.put("EVENT_NOTE", array3);
        params.put("data", obj.toString());
        return params;
    }

    @Override
    public void onDelete(final int position, AttachMent photos) {
        DialogTools.showConfirmDialog(this, "", getString(R.string.dialog_delete_image), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageAdapter.remove(position);
                imageAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        AttachMent attachMent = imageAdapter.getItem(i);
        if (attachMent.getID() == 0) {
            chooseMedia(imageAdapter.getImageList());
        } else {
            ImagePagerActivity.startShowImages(view.getContext(), imageAdapter.getImagePaths(), i);
        }
    }

    private void chooseMedia(List<AttachMent> arrayList) {
        PhotoPicker.builder().setPhotoCount(8)
                .setSelected(getPhotos(arrayList))
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(false)
                .start(this, PhotoPicker.REQUEST_CODE);
    }

    /**
     * AttachMent转换成Photo进入相册选择
     *
     * @param arrayList
     * @return
     */
    private ArrayList<Photo> getPhotos(List<AttachMent> arrayList) {
        ArrayList<Photo> photos = new ArrayList<>();
        if (StringUtils.isNotNullList(arrayList)) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                AttachMent att = arrayList.get(i);
                Photo photo = new Photo();
                photo.setPath(att.getATTACH_URL());
                photo.setStatus(att.getID() != 0 ? Photo.Enabled : Photo.UnEnabled);
                photos.add(photo);
            }
        }
        return photos;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (isCancel(resultCode)) {
            switch (requestCode) {
                case PhotoPicker.REQUEST_CODE:
                    if (data != null) {
                        ArrayList<Photo> listPhotos = data.getParcelableArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                        for (Photo photo : listPhotos) {
                            //id为0的话说明已经选择过了
                            if (photo.getId() != 0) {
                                AttachMent newAtt = createNewAttach(photo.getPath());
                                newAtt.setID(photo.getId());
                                imageAdapter.add(0, newAtt);
                            }
                        }
                        imageAdapter.notifyDataSetChanged();
                    }
                    break;
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /********转化为jsonarray********/
    private JSONArray getUploadImage() {
        JSONArray jsonArray = new JSONArray();

        if (StringUtils.isNotNullList(imageAdapter.getImageList())) {
            int size = imageAdapter.getImageList().size();
            for (int i = 0; i < size; i++) {
                AttachMent attachMent = imageAdapter.getImageList().get(i);
                File file = new File(attachMent.getATTACH_URL());
                if (file.exists()) {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("fileName", StringUtils.getFilename(attachMent.getATTACH_URL()));
                        object.put("fileExt", StringUtils.getFilenameExtension(attachMent.getATTACH_URL()));
                        Bitmap bitmap = BitmapFactory.decodeFile(attachMent.getATTACH_URL());
                        object.put("fileStr", FileUtils.getByteByBitmap(bitmap));
                        jsonArray.put(object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return jsonArray;
    }

}
