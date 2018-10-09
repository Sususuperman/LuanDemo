package com.hywy.luanhzt.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs.common.base.BaseActivity;
import com.cs.common.utils.DialogTools;
import com.cs.common.utils.ImageLoaderUtils;
import com.cs.common.utils.StringUtils;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.app.App;
import com.hywy.luanhzt.entity.CompanyContact;
import com.hywy.luanhzt.entity.Contact;
import com.hywy.luanhzt.entity.HzContact;
import com.hywy.luanhzt.entity.Message;
import com.hywy.luanhzt.utils.SystemUtils;
import com.hywy.luanhzt.view.customer.MoreItemView;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class ContactDetailsActivity extends BaseActivity {
    @Bind(R.id.iv_head)
    ImageView iv_head;
    @Bind(R.id.river)
    MoreItemView tv_river;
    @Bind(R.id.duty)
    MoreItemView tv_duty;
    @Bind(R.id.phone)
    MoreItemView tv_phone;
    @Bind(R.id.name)
    TextView tv_name;
    @Bind(R.id.role)
    TextView tv_role;
    @Bind(R.id.iv_top_back)
    ImageView iv_back;

    private Parcelable parcelable;
    private HzContact.DealsBean hzContact;
    private Contact.DealsBean contact;
    private CompanyContact.DealsBean companyContact;
    private String phone, per_name, per_id;


    public static void startAction(Context activity, Parcelable parcelable) {
        Intent intent = new Intent(activity, ContactDetailsActivity.class);
        intent.putExtra("parcelable", parcelable);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        init();
    }

    private void init() {
        parcelable = getIntent().getParcelableExtra("parcelable");

        if (parcelable instanceof HzContact.DealsBean) {
            hzContact = (HzContact.DealsBean) parcelable;
            if (StringUtils.hasLength(hzContact.getIMAGE_URL())) {
                ImageLoaderUtils.displayRound(this, iv_head, hzContact.getIMAGE_URL());
            }
            setMoreItemView(tv_river, hzContact.getREACH_NAME());
            setMoreItemView(tv_duty, hzContact.getPER_D());
            setMoreItemView(tv_phone, hzContact.getPHONE());
            phone = hzContact.getPHONE();

            per_id = hzContact.getUSER_ID();
            per_name = hzContact.getPER_NAME();

            setTextView(tv_name, hzContact.getPER_NAME());
            setTextView(tv_role, hzContact.getPER_TYPE());
        } else if (parcelable instanceof Contact.DealsBean) {
            contact = (Contact.DealsBean) parcelable;
            if (StringUtils.hasLength(contact.getIMAGE_URL())) {
                ImageLoaderUtils.displayRound(this, iv_head, contact.getIMAGE_URL());
            }
            tv_river.setItemText("行政区划");
            setMoreItemView(tv_river, contact.getADMN());
            setMoreItemView(tv_duty, contact.getPER_D());
            setMoreItemView(tv_phone, contact.getPHONE());
            phone = contact.getPHONE();

            per_id = contact.getUSER_ID();
            per_name = contact.getNAME();

            setTextView(tv_name, contact.getNAME());
            setTextView(tv_role, contact.getUSERNAME());
        } else if (parcelable instanceof CompanyContact.DealsBean) {
            companyContact = (CompanyContact.DealsBean) parcelable;
            tv_river.setItemText("行政区划");
            tv_duty.setVisibility(View.GONE);
            setMoreItemView(tv_river, companyContact.getADNM());
//            setMoreItemView(tv_duty, companyContact.getDUTY());
            setMoreItemView(tv_phone, companyContact.getPHONE());
            phone = companyContact.getPHONE();

            per_id = companyContact.getID() + "";
            per_name = companyContact.getNAME();

            setTextView(tv_name, companyContact.getNAME());
        }
    }

    private void setMoreItemView(MoreItemView miv, String str) {
        if (StringUtils.hasLength(str)) {
            miv.setItemRightText(str);
        }
    }

    private void setTextView(TextView miv, String str) {
        if (StringUtils.hasLength(str)) {
            miv.setText(str);
        }
    }

    @OnClick({R.id.ll_top_back, R.id.iv_contact_phone, R.id.iv_contact_message, R.id.btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_top_back:

                finish();
                break;
            case R.id.iv_contact_phone:
                callPhone(phone, this);
                break;
            case R.id.iv_contact_message:
                break;
            case R.id.btn:
                Message message = new Message();
                message.setPER_ID(per_id);
                message.setPER_NAME(per_name);
                message.setUSER_ID(App.getInstance().getAccount().getUserId());
                MessageInfoActivity.startAction(this, message);
                break;
        }
    }

    private void requestPermmisons(final Context context, final String phone) {
        Acp.getInstance(context).request(new AcpOptions.Builder()
                        .setPermissions(Manifest.permission.CALL_PHONE)
                        .build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {
                        SystemUtils.call(context, phone);
                    }

                    @Override
                    public void onDenied(List<String> permissions) {

                    }
                });
    }

    private void callPhone(final String phone, final Context context) {
        if (phone == null) return;

        DialogTools.showConfirmDialog(this, "", "确认要拨打 '" + phone + "'号码？\n", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermmisons(context, phone);
            }
        });
    }

}
