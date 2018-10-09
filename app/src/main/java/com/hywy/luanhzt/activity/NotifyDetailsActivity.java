package com.hywy.luanhzt.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import com.cs.common.base.BaseActivity;
import com.cs.common.base.BaseToolbarActivity;
import com.cs.common.utils.StringUtils;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.entity.Notify;

import butterknife.Bind;

public class NotifyDetailsActivity extends BaseToolbarActivity {
    private Notify notify;
    @Bind(R.id.tv_info)
    TextView tvInfo;
    @Bind(R.id.webview)
    WebView webView;

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.time)
    TextView time;
    @Bind(R.id.type)
    TextView type;

    public static void startAction(Context activity, Notify notify) {
        Intent intent = new Intent(activity, NotifyDetailsActivity.class);
        intent.putExtra("notify", notify);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_details);
        init();
    }

    private void init() {
        notify = getIntent().getParcelableExtra("notify");
        this.setTitleBulider(new BaseToolbarActivity.Bulider().title(notify.getINFOTYPE()).backicon(R.drawable.ic_arrow_back_white_24dp));
        if (StringUtils.hasLength(notify.getINFOCONTENT())) {
            tvInfo.setText(notify.getINFOCONTENT());
//            webView.loadData(notify.getINFOCONTENT(),"text/html" , "utf-8");
        }
        setTextview(title,notify.getINFO());
        setTextview(name,notify.getINFOMAN());
        setTextview(time,notify.getTM());
    }

    private void setTextview(TextView tv, String txt) {
        if (StringUtils.hasLength(txt)) {
            tv.setText(txt);
        }
    }
}
