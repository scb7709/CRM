package com.yun.ycw.crm.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnRadioGroupCheckedChange;
import com.umeng.analytics.MobclickAgent;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.fragment.HomeFragment;
import com.yun.ycw.crm.fragment.MyFragment;
import com.yun.ycw.crm.fragment.WorkFragment;
import com.yun.ycw.crm.utils.SharedUtils;

/**
 * Created by scb on 2016/1/30.
 */
public class HomeActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener {
    @ViewInject(R.id.home_text_tv)
    private TextView text;
    @ViewInject(R.id.homecustomer_add_iv)
    private ImageView homecustomer_add;
    @ViewInject(R.id.tab_home)
    private RadioButton homeTab;
    @ViewInject(R.id.tab_customer)
    private RadioButton tab_customer;
    @ViewInject(R.id.home_tabs)
    private RadioGroup tabs;
    private FragmentManager fragmentManager;
    private String whoFragment;//记录当前Fragment
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ViewUtils.inject(this);
        mContext = this;
        //获取fragment的管理对象
        fragmentManager = getSupportFragmentManager();
        //设置默认选择第一个
        tabs.setOnCheckedChangeListener(this);
        homeTab.setChecked(true);
        //切换页面
        changeFragment(new HomeFragment(), "HomeFragment");
    }

    /**
     * 切换fragment对象
     */
    private void changeFragment(Fragment fragment, String tag) {
        //1.获取事务对象
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //2.切换内容的显示
        transaction.replace(R.id.home_frame, fragment, tag);
//		//3.进站
//		transaction.addToBackStack(null);
        //4.提交事务
        transaction.commit();
    }

    @OnRadioGroupCheckedChange({R.id.home_tabs})
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.tab_home:
                //首页
                text.setText("业绩");
                homecustomer_add.setVisibility(View.INVISIBLE);
                changeFragment(new HomeFragment(), "HomeFragment");
                break;
            case R.id.tab_customer:
                text.setText("工作");
                homecustomer_add.setVisibility(View.INVISIBLE);
                changeFragment(new WorkFragment(), "WorkFragment");

                break;
            case R.id.tab_my:
                //个人中心
                whoFragment = "个人中心";
                text.setText("个人中心");
                homecustomer_add.setVisibility(View.INVISIBLE);
                changeFragment(new MyFragment(), "MyFragment");
                break;
        }
    }

    @OnClick({R.id.homecustomer_add_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.homecustomer_add_iv:
                //如果是添加新客户，就想AddCustomerActivity传递一个"addCustomer"
                Intent intent = new Intent(this, AddCustomerActivity.class);
                intent.putExtra("flag", "addcustomer");
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       // Log.i("QQQQQQQQQQQQQQ", resultCode + "aa" + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == RESULT_OK) {
                finish();
            } else if (requestCode == 67758) {
                text.setText("工作");
                homecustomer_add.setVisibility(View.INVISIBLE);
                changeFragment(new WorkFragment(), "WorkFragment");
            }
        }

        try {
            Fragment cusFragment = getSupportFragmentManager().findFragmentByTag("CustomerFragment");
            cusFragment.onActivityResult(requestCode, resultCode, data);
        } catch (NullPointerException n) {

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(mContext);
    }


    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(mContext);
    }
}
