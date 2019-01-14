package com.hywy.luanhzt.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.amap.api.location.AMapLocation;
import com.cs.android.task.Task;
import com.cs.common.adapter.BaseListFlexAdapter;
import com.cs.common.base.BaseToolbarActivity;
import com.cs.common.handler.BaseHandler_;
import com.cs.common.handler.SpringViewHandler;
import com.cs.common.handler.WaitDialog;
import com.cs.common.listener.OnPostListenter;
import com.cs.common.utils.DateUtils;
import com.cs.common.utils.DialogTools;
import com.cs.common.utils.FileUtils;
import com.cs.common.utils.IToast;
import com.cs.common.utils.StringUtils;
import com.cs.common.view.ImagePagerActivity;
import com.cs.upgrade.entity.Config;
import com.cs.upgrade.entity.VersionUpdateConfig;
import com.cs.upgrade.utils.UpgradeUtils;
import com.hywy.amap.Locate;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.adapter.ImagesAdapter;
import com.hywy.luanhzt.adapter.SpinnerListAdapter;
import com.hywy.luanhzt.app.App;
import com.hywy.luanhzt.dao.ProblemDao;
import com.hywy.luanhzt.entity.Adnm;
import com.hywy.luanhzt.entity.AttachMent;
import com.hywy.luanhzt.entity.EventSupervise;
import com.hywy.luanhzt.entity.ProblemReport;
import com.hywy.luanhzt.entity.River;
import com.hywy.luanhzt.entity.User;
import com.hywy.luanhzt.key.Key;
import com.hywy.luanhzt.task.GetAddressListTask;
import com.hywy.luanhzt.task.GetEventTypeTask;
import com.hywy.luanhzt.task.GetRiverNameListTask;
import com.hywy.luanhzt.task.GetUserListProblemTask;
import com.hywy.luanhzt.task.PostProblemReportTask;
import com.hywy.luanhzt.view.dialog.dialogplus.DialogPlus;
import com.hywy.luanhzt.view.dialog.dialogplus.ListHolder;
import com.hywy.luanhzt.view.dialog.dialogplus.OnItemClickListener;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

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
import me.drakeet.materialdialog.MaterialDialog;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.entity.Photo;

public class ProblemReportActivity extends BaseToolbarActivity implements ImagesAdapter.onDeleteListener, AdapterView.OnItemClickListener, OnItemClickListener {

    @Bind(R.id.pro_name)
    TextView proName;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.tv_river)
    TextView tvRiver;
    @Bind(R.id.tv_event)
    TextView tvEvent;
    @Bind(R.id.tv_touser)
    TextView tvTouser;
    @Bind(R.id.pro_content)
    TextView tvContent;
    @Bind(R.id.image_grid)
    GridView imageGrid;
    @Bind(R.id.btn)
    Button btn;
    @Bind(R.id.toggle)
    ToggleButton toggle;
    @Bind(R.id.spinner)
    Spinner spinner;
    private ImagesAdapter imageAdapter;

    private BaseListFlexAdapter mAdapter;
    private List<Adnm> adnms;
    private List<River> rivers;
    private List<EventSupervise> events;
    private List<User> userList;

    private SpinnerListAdapter spinnerAdapter1;
    private JSONArray array;

    public static final int request_activity_mobilework = 10012;
    private ProblemReport problemReport;

    private long logId;
    private String latitude, longtitud;
    private WaitDialog waitDialog;
    private Locate locale;
    private AMapLocation loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_report);
        setHomeEnable(true);
        init();
        initData();
        addDefaultImage();
    }

    @Override
    public void onClickBack() {
        final MaterialDialog dialog1 = new MaterialDialog(this);
        dialog1.setTitle("提示");
//            "大小:" + StringUtils.formatSize(upgrade.getSize()) +"\n" +
        dialog1.setMessage("问题尚未提交，是否保存？");
        dialog1.setPositiveButton("是", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
                //保存本地
                ProblemDao dao = new ProblemDao(ProblemReportActivity.this);
                ProblemReport p = createProblem();
                if (problemReport == null) {
                    dao.insert(p);
                } else {
                    dao.update(p);
                }
                setResult(RESULT_OK);
                finish();
            }
        });
        dialog1.setNegativeButton("否", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
                finish();
            }
        });

        dialog1.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            onClickBack();
        }
        return super.onOptionsItemSelected(item);
    }

    private ProblemReport createProblem() {
//        obj.put("EVENT_NAME", proName.getText().toString());
        EventSupervise e = (EventSupervise) tvEvent.getTag();
//        obj.put("EVENT_TYPE_ID", e.getEVENT_TYPE_ID());
//        if (StringUtils.hasLength(tvContent.getText().toString())) {
//            obj.put("EVENT_CONT", tvContent.getText().toString());
//        }
        River r = (River) tvRiver.getTag();
//        obj.put("REACH_CODE", r.getREACH_CODE());
        Adnm a = (Adnm) tvAddress.getTag();
//        obj.put("ADCD", a.getADCD());
//        obj.put("PER_ID", App.getInstance().getAccount().getUserId());
//        obj.put("STARTTIME", DateUtils.transforMillToDateInfo(System.currentTimeMillis() / 1000));
        array = getUploadImage();
//        obj.put("EVENT_NOTE", array);
        ProblemReport p = new ProblemReport();
        if (problemReport == null) {
            p.setEVENT_ID(System.currentTimeMillis());
        } else p.setEVENT_ID(problemReport.getEVENT_ID());
        p.setEVENT_NAME(proName.getText().toString());
        p.setEVENT_CONT(tvContent.getText().toString());
        if (r != null) {
            p.setREACH_CODE(r.getREACH_CODE());
            p.setREACH_NAME(r.getREACH_NAME());
        }
        if (a != null) {
            p.setADCD(a.getADCD());
            p.setADNM(a.getADNM());
        }
        if (e != null) {
            p.setEVENT_TYPE_ID(e.getEVENT_TYPE_ID());
            p.setTYPENAME(e.getEVENT_TYPE_NAME());
        }
        p.setPATROL_NOTE(imageAdapter.getImageList());
        p.setDATA_TYPE(ProblemReport.DATA_LOCAL);
        p.setLOG_ID(logId);
        p.setEVENT_LAT(latitude);
        p.setEVENT_LOGN(longtitud);
        return p;
    }

    public static void startAction(Activity activity) {
        startAction(activity, 0, null, null);
    }

    /**
     * @param activity
     * @param logId
     * @param latitude
     * @param longtitude
     */
    public static void startAction(Activity activity, long logId, String latitude, String longtitude) {
        Intent intent = new Intent(activity, ProblemReportActivity.class);
        intent.putExtra("logId", logId);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longtitude", longtitude);
        activity.startActivityForResult(intent, request_activity_mobilework);
    }

    public static void startActionForResult(Activity activity, ProblemReport problemReport) {
        Intent intent = new Intent(activity, ProblemReportActivity.class);
        intent.putExtra("problemReport", problemReport);
        activity.startActivityForResult(intent, request_activity_mobilework);
    }

    private void init() {
        setTitleBulider(new Bulider().backicon(R.drawable.ic_arrow_back_white_24dp).title("事件上报"));
        problemReport = getIntent().getParcelableExtra("problemReport");

        imageAdapter = new ImagesAdapter(this);
        imageGrid.setAdapter(imageAdapter);
        imageAdapter.setOnDeleteListener(this);
        imageGrid.setOnItemClickListener(this);

        mAdapter = new BaseListFlexAdapter(this);
//        recyclerView.setLayoutManager(new FullyLinearLayoutManager(this));
//        recyclerView.setAdapter(mAdapter);
        spinnerAdapter1 = new SpinnerListAdapter(this);
//        spinner.setAdapter(spinnerAdapter1);

        logId = getIntent().getLongExtra("logId", 0);
        latitude = getIntent().getStringExtra(  "latitude");
        longtitud = getIntent().getStringExtra("longtitude");
        waitDialog = new WaitDialog(this, "定位中...", false, false);

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b) {
                    ChooseAddressActivity.startAction(ProblemReportActivity.this, 0, 0, "");
//                    RequestPermmisons();
//                }
            }
        });
    }

    private void RequestPermmisons() {
        Acp.getInstance(this).request(new AcpOptions.Builder()
                        .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                        .build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {
                        startLocation(new Locate.OnLocationListener() {
                            @Override
                            public void onReceiveLocation(AMapLocation location) {
                                loc = location;
                                IToast.toast("获取成功！");

                                if (waitDialog != null)
                                    waitDialog.dismiss();
                            }

                            @Override
                            public void onReceiveFail() {
                                if (waitDialog != null)
                                    waitDialog.dismiss();
                            }
                        });
                    }

                    @Override
                    public void onDenied(List<String> permissions) {

                    }
                });
    }

    private void startLocation(Locate.OnLocationListener listener) {
        if (waitDialog != null)
            waitDialog.show();
        //启动定位
        locale = new Locate(this);
        locale.start();
        locale.setOnLocationListener(listener);
    }

    private void initData() {
//        tvName.setText(plan.getREACH_NAME());
//        tvUser.setText("超级管理员");//当前登录人
//        tvTime.setText(DateUtils.transforMillToDate(System.currentTimeMillis()));
        initLocal();
        initAddressList();
        initRiverList();
        initEventList();
        initTouserList();
    }

    private void initLocal() {
        if (problemReport != null) {
            List<AttachMent> attachMents = problemReport.getPATROL_NOTE();
            if (attachMents != null && attachMents.size() > 0) {
                imageAdapter.setList(attachMents);
            }


            proName.setText(problemReport.getEVENT_NAME());
            tvContent.setText(problemReport.getEVENT_CONT());
            EventSupervise e = new EventSupervise();
            e.setEVENT_TYPE_ID(problemReport.getEVENT_TYPE_ID());
            e.setEVENT_TYPE_NAME(problemReport.getTYPENAME());
            tvEvent.setTag(e);
            tvEvent.setText(problemReport.getTYPENAME());

            Adnm adnm = new Adnm();
            adnm.setADCD(problemReport.getADCD());
            adnm.setADNM(problemReport.getADNM());
            tvAddress.setTag(adnm);
            tvAddress.setText(problemReport.getADNM());

            River r = new River();
            r.setREACH_CODE(problemReport.getREACH_CODE());
            r.setREACH_NAME(problemReport.getREACH_NAME());
            tvRiver.setTag(r);
            tvRiver.setText(problemReport.getREACH_NAME());

            logId = problemReport.getLOG_ID();
            longtitud = problemReport.getEVENT_LOGN();
            latitude = problemReport.getEVENT_LAT();
        }
    }


    private void initAddressList() {
        SpringViewHandler handler = new SpringViewHandler(this);
        handler.setBuilder(new BaseHandler_.Builder().isShowToast(false));
        Map<String, Object> params = new HashMap<>();
        handler.request(params, new GetAddressListTask(this));
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                adnms = (List<Adnm>) result.get(Key.RESULT);
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

    private void initTouserList() {
        SpringViewHandler handler = new SpringViewHandler(this);
        handler.setBuilder(new BaseHandler_.Builder().isShowToast(false));
        Map<String, Object> params = new HashMap<>();
        handler.request(params, new GetUserListProblemTask(this));
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                userList = (List<User>) result.get(Key.RESULT);
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {

            }
        });
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

    @OnClick({R.id.layout_address, R.id.layout_river, R.id.layout_event, R.id.layout_touser})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_address:
                showAddressList(adnms);
                break;
            case R.id.layout_river:
                showRiverList(rivers);
                break;
            case R.id.layout_event:
                showEventList(events);
                break;
            case R.id.layout_touser:
                showUserList(userList);
                break;
        }
    }

    private void showEventList(List<EventSupervise> list) {
        spinnerAdapter1.setList(list);
        showRadioDialog();
    }

    private void showRiverList(List<River> list) {
        spinnerAdapter1.setList(list);
        showRadioDialog();
    }


    private void showAddressList(List<Adnm> list) {
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
        if (object instanceof Adnm) {
            Adnm adnm = (Adnm) object;
            tvAddress.setTag(adnm);
            tvAddress.setText(adnm.getADNM());
        } else if (object instanceof River) {
            River river = (River) object;
            tvRiver.setTag(river);
            tvRiver.setText(river.getREACH_NAME());
        } else if (object instanceof User) {
            User user = (User) object;
            tvTouser.setTag(user);
            tvTouser.setText(user.getNAME());
        } else {
            EventSupervise event = (EventSupervise) object;
            tvEvent.setTag(event);
            tvEvent.setText(event.getEVENT_TYPE_NAME());
        }
        dialog.dismiss();
    }

    @OnClick(R.id.btn)
    public void submit() {
        if ("".equals(proName.getText().toString().trim())) {
            IToast.toast("请输入问题名称");
            return;
        }

        if ("".equals(tvEvent.getText().toString().trim())) {
            IToast.toast("请选择事件类型");
            return;
        }

        if ("".equals(tvAddress.getText().toString().trim())) {
            IToast.toast("请选择所属区域");
            return;
        }

        if ("".equals(tvRiver.getText().toString().trim())) {
            IToast.toast("请选择所属河段");
            return;
        }

        SpringViewHandler handler = new SpringViewHandler(this);
        Task task = new PostProblemReportTask(this);
        try {
            handler.request(getParams(), task);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                IToast.toast("上报成功");
                if (problemReport != null) {
                    ProblemDao dao = new ProblemDao(ProblemReportActivity.this);
                    dao.delete(problemReport);
                    setResult(RESULT_OK);
                }
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
        obj.put("EVENT_NAME", proName.getText().toString());
        EventSupervise e = (EventSupervise) tvEvent.getTag();
        obj.put("EVENT_TYPE_ID", e.getEVENT_TYPE_ID());
        if (StringUtils.hasLength(tvContent.getText().toString())) {
            obj.put("EVENT_CONT", tvContent.getText().toString());
        }
        River r = (River) tvRiver.getTag();
        obj.put("REACH_CODE", r.getREACH_CODE());
        Adnm a = (Adnm) tvAddress.getTag();
        obj.put("ADCD", a.getADCD());
        obj.put("PER_ID", App.getInstance().getAccount().getUserId());
        obj.put("STARTTIME", DateUtils.transforMillToDateInfo(System.currentTimeMillis() / 1000));
        if (logId != 0) {
            obj.put("LOG_ID", logId);
            obj.put("EVENT_LOGN", longtitud);
            obj.put("EVENT_LAT", latitude);
        }
        array = getUploadImage();
        obj.put("EVENT_NOTE", array);
        Map<String, Object> params = new HashMap<>();
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
