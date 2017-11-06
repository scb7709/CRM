package com.yun.ycw.crm.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.yun.ycw.crm.R;

/**
 * Created by scb on 2016/5/11.
 */
public class CompanyMessageActivity extends Activity{
    @ViewInject(R.id.companymessage_favorable)
    private LinearLayout favorable;//

    @ViewInject(R.id.companymessage_distribution)
    private LinearLayout distribution;//

    @ViewInject(R.id.companymessage_back)
    private ImageButton back;//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companymessage);
        ViewUtils.inject(this);
    }
    @OnClick({R.id.companymessage_favorable,R.id.companymessage_distribution,R.id.companymessage_back})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.companymessage_back:
                finish();
                break;
            case R.id.companymessage_favorable:
             startActivity(new Intent(this, FavorableActivity.class));
                break;
            case R.id.companymessage_distribution:
               startActivity(new Intent(this,DistributionActivity.class));
                break;

        }
    }

}
