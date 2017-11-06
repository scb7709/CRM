package com.yun.ycw.crm.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.yun.ycw.crm.R;

/**
 * Created by scb on 2016/5/11.
 */
public class DistributionActivity extends Activity {
    @ViewInject(R.id.companymessagee_webview)
    private WebView distribution;//
    @ViewInject(R.id.companymessagee_back)
    private ImageButton back;//
    @ViewInject(R.id.companymessagee_title)
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companymessagee);
        ViewUtils.inject(this);
        title.setText("配送政策");
        distribution.getSettings().setJavaScriptEnabled(true);
        // distribution.loadUrl("https://www.taobao.com");
        distribution.loadUrl("http://mp.weixin.qq.com/s?__biz=MzIxMjA3Mjc3Mg==&mid=407130590&idx=1&sn=ba9b41263b61c4badf7857482e81e300");
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        distribution.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
    }

    @OnClick({R.id.companymessagee_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.companymessagee_back:
                finish();
                break;

        }
    }
}
