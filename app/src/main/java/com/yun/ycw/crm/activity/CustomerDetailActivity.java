package com.yun.ycw.crm.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.adapter.ViewpagerAdapter;
import com.yun.ycw.crm.customview.CustomPopmenu;
import com.yun.ycw.crm.entity.Customer;
import com.yun.ycw.crm.fragment.CusInfoFragment;
import com.yun.ycw.crm.fragment.CusOrderFragment;
import com.yun.ycw.crm.utils.SharedUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 这是客户详情的页面
 * Created by wdyan on 2016/2/1.
 */
public class CustomerDetailActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    @ViewInject(R.id.detail_rgs)
    private RadioGroup detail_rgs; //客户详情页面的 radiogroup （详细资料和订单）
    private List<Fragment> fragmentList = new ArrayList<>(); //存放 客户资料fragment和订单fragment的list
    @ViewInject(R.id.viewpager)
    private ViewPager viewPager;//滑动切换fragment的viewpager
    @ViewInject(R.id.customer_detail_info)
    private RadioButton info; //详细资料 rbtn
    @ViewInject(R.id.customer_detail_order)
    private RadioButton order; //订单 rbtn
    @ViewInject(R.id.edit_img)
    private ImageView edit_img; //右上角的“三个点”
    @ViewInject(R.id.customer_detail_name)
    private TextView name;
    @ViewInject(R.id.cus_detail_back)
    private ImageButton cus_detail_back;
    private CustomPopmenu popmenu; //自定义布局的菜单
    private Customer c;
    private Context mContext;
    private String  isPublicOcean;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cus_detail);
        ViewUtils.inject(this);
        mContext = this;
        //获取传过来的customer对象
        Customer customer = (Customer) getIntent().getSerializableExtra("customer");
        init(customer);
    }

    private void init(Customer customer) {
        isPublicOcean=getIntent().getStringExtra("isPublicOcean");
        if(isPublicOcean.equals("3")){//主管从激活页面过来的或者何总打开客户详情
            edit_img.setVisibility(View.GONE);
        }
        popmenu = new CustomPopmenu(CustomerDetailActivity.this, customer, R.layout.popmenu, isPublicOcean);
        detail_rgs.setOnCheckedChangeListener(this);
        edit_img.setOnClickListener(this);

        cus_detail_back.setOnClickListener(this);
        name.setText(customer.getCustomer_name() + "");
        fragmentList.add(new CusOrderFragment());
        fragmentList.add(new CusInfoFragment());
        ViewpagerAdapter adapter = new ViewpagerAdapter(getSupportFragmentManager(), this, fragmentList);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    //viewpager的滑动监听
    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                info.setChecked(false);
                order.setChecked(true);
                break;
            case 1:
                info.setChecked(true);
                order.setChecked(false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    //radiobutton的点击监听
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.customer_detail_info:
                viewPager.setCurrentItem(1);
                break;
            case R.id.customer_detail_order:
                viewPager.setCurrentItem(0);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_img://点击“三个点”按钮
                if (popmenu != null) {
                    if (popmenu.isShowing()) {
                        popmenu.dismiss();
                    } else {
                        popmenu.showAsDropDown(edit_img);
                    }
                }
                break;
            case R.id.cus_detail_back://点击back按钮
                setResult(2, CustomerDetailActivity.this.getIntent().putExtra("REFRESH_FLAG", "NOT_REFRESH"));
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(2, CustomerDetailActivity.this.getIntent().putExtra("REFRESH_FLAG", "NOT_REFRESH"));
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
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
