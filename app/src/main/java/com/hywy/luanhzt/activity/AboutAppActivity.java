package com.hywy.luanhzt.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.cs.common.base.BaseToolbarActivity;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.utils.UpgradeUtils;
import com.hywy.luanhzt.view.customer.MoreItemView;

import butterknife.Bind;
import butterknife.OnClick;

public class AboutAppActivity extends BaseToolbarActivity {
    @Bind(R.id.app_name)
    TextView name;
    @Bind(R.id.app_version)
    TextView version;
    @Bind(R.id.feedback)
    MoreItemView tvFeed;

    public static void startAction(Context activity) {
        Intent intent = new Intent(activity, AboutAppActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        init();
    }

    private void init() {
        setTitleBulider(new Bulider()
                .backicon(R.drawable.ic_arrow_back_white_24dp)
                .title(getString(R.string.copyright_info)));
        version.setText(UpgradeUtils.getVersionName(getPackageName(), this));
        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), 0);
            name.setText(applicationInfo.loadLabel(getPackageManager()).toString());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.feedback)
    public void feedBack() {
        FeedBackActivity.startAction(this);
    }
}
