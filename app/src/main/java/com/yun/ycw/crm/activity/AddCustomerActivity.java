package com.yun.ycw.crm.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnRadioGroupCheckedChange;
import com.umeng.analytics.MobclickAgent;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.adapter.ActhievementSalesAdapter;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.customview.DateTimePickDialog;
import com.yun.ycw.crm.entity.Achievement;
import com.yun.ycw.crm.entity.Customer;
import com.yun.ycw.crm.service.AddCustomerService;
import com.yun.ycw.crm.service.UpdateCustomerService;
import com.yun.ycw.crm.utils.DateCheckUtils;
import com.yun.ycw.crm.utils.InternetUtils;
import com.yun.ycw.crm.utils.SharedUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by scb on 2016/1/30.
 */
public class AddCustomerActivity extends Activity implements RadioGroup.OnCheckedChangeListener {

    @ViewInject(R.id.addcustomer_addorupdate_bu)
    private TextView addorupdate;
    @ViewInject(R.id.addcustomer_return_bu)
    private ImageButton returnn;
    @ViewInject(R.id.addcustomer_save_bu)
    private TextView save;
    @ViewInject(R.id.addcustomer_source_rg)
    private RadioGroup source;//客户来源
    @ViewInject(R.id.addcustomer_sex_rg)
    private RadioGroup Sex;//客户性别
    @ViewInject(R.id.addcustomer_man)
    private RadioButton addcustomer_man;//男
    @ViewInject(R.id.addcustomer_woman)
    private RadioButton addcustomer_woman;//女
    @ViewInject(R.id.addcustomer_source_tv)
    private TextView sourcet;//客户来源
    @ViewInject(R.id.addcustomer_boos_ed)
    private EditText boosname;//工长名字
    @ViewInject(R.id.addcustomer_phonenumber_ed)
    private EditText phonenumber;//工长电话
    @ViewInject(R.id.addcustomer_birthplace_ed)
    private EditText birthplace;//工长籍贯
    @ViewInject(R.id.addcustomer_company_ed)
    private EditText company;//所属公司
    @ViewInject(R.id.addcustomer_firsttime_ed)
    private TextView firsttime;
    @ViewInject(R.id.addcustomer_firstrecord_ed)
    private EditText firstrecord;
    @ViewInject(R.id.addcustomer_notes_ed)
    private EditText notes;
    private String initStartDateTime; // 初始化开始时间
    private Intent intent;
    private Customer customer;
    private String flag;
    private Dialog dialog;//用于上传新增客户信息时的对话框
    private String sources;//来源的序号
    private String sex = "0";//性别
    private String origin_name;
    private String origin_phone;
    private String origin_birthplace;
    private String origin_company;
    private String origin_firstrecord;
    private String origin_firsttime;
    private String origin_notes;
    private String origin_sex;
    private String format_time;
    private String mPageName = "AddCustomerActivity";
    private Context mContext;
    private List<Achievement> list;
    private String name;//被指派客户的 专员名字
    private String id;//被指派客户的 专员ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcustomer);
        ViewUtils.inject(this);
        mContext = this;
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
        initStartDateTime = sDateFormat.format(new Date());


        flag = getIntent().getStringExtra("flag");

        Sex.setOnCheckedChangeListener(this);
        source.setOnCheckedChangeListener(this);
        if (flag.equals("editcustomer")) {
            //如果是编辑用户
            customer = (Customer) getIntent().getExtras().get("customer");
            setdata();
        } else {
            source.setVisibility(View.VISIBLE);
            sourcet.setVisibility(View.INVISIBLE);
        }
        if (flag.equals("allocation_customer")) {
            //如果是主管指派
            addorupdate.setText("指派客户");
            name = getIntent().getStringExtra("name");
            id = getIntent().getStringExtra("id");
        }

        if (flag.equals("add_customer")) {
            addorupdate.setText("新增客户");
        }

    }


    @OnClick({R.id.addcustomer_return_bu, R.id.addcustomer_save_bu, R.id.addcustomer_firsttime_ed})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addcustomer_return_bu:
                finish();
                break;
            case R.id.addcustomer_save_bu:
                save();
                break;
            case R.id.addcustomer_firsttime_ed:
                DateTimePickDialog dateTimePicKDialog = new DateTimePickDialog(AddCustomerActivity.this, initStartDateTime);
                dateTimePicKDialog.dateTimePicKDialog(firsttime);
                break;
        }
    }

    protected void setdata() {
        addorupdate.setText("编辑客户");
        source.setVisibility(View.INVISIBLE);
        sourcet.setVisibility(View.VISIBLE);
        sourcet.setText(Constants.getSource(customer.getSource()));


        //传过来的原始数据，用于和修改过的数据的比较
        origin_name = customer.getCustomer_name();
        origin_phone = customer.getPhonenumber();
        origin_birthplace = customer.getBirthplace();
        origin_company = customer.getCompany();
        origin_firstrecord = customer.getFirstrecord();
        if (customer.getSex().equals("男")) {
            addcustomer_man.setChecked(true);
        } else if (customer.getSex().equals("女")) {
            addcustomer_woman.setChecked(true);
        }

        //这是传过来的customer对象，里面的拜访时间是“2016-01-19T15:20:00+08:00”，但也可以为“ ”
        if (customer.getFirsttime().length() != 0) {
            origin_firsttime = customer.getFirsttime();
        } else {
            origin_firsttime = "";
        }
        origin_notes = customer.getNotes();

        boosname.setText(customer.getCustomer_name());
        phonenumber.setText(customer.getPhonenumber());
        birthplace.setText(customer.getBirthplace());
        company.setText(customer.getCompany());
        firstrecord.setText(customer.getFirstrecord());

        if (origin_firsttime.length() != 0) {
            String originTime = customer.getFirsttime();
            String year = originTime.substring(0, 10);
            String time = originTime.substring(11, 16);
            String formated_time = year + " " + time;
            firsttime.setText(formated_time);//显示原始的时间
        } else {
            firsttime.setText("");
        }
        notes.setText(customer.getNotes());
    }

    @OnRadioGroupCheckedChange({R.id.addcustomer_source_rg, R.id.addcustomer_sex_rg})
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.addcustomer_radio1:
                sources = "1";
                break;
            case R.id.addcustomer_radio2:
                sources = "2";
                break;
            case R.id.addcustomer_radio3:
                sources = "3";
                break;
            case R.id.addcustomer_radio4:
                sources = "4";
                break;
            case R.id.addcustomer_radio5:
                sources = "5";
                break;
            case R.id.addcustomer_man:
                sex = "1";
                break;
            case R.id.addcustomer_woman:
                sex = "2";
                break;
        }
    }

    //编辑后的信息
    String mBoosname;
    String mPhonenumber;
    String mBirthplace;
    String mCompany;
    String mFirstrecord;
    String mFirsttime;//编辑后的时间
    String mNotes;
    String mSex;

    public void save() {
        mBoosname = boosname.getText().toString();
        mPhonenumber = phonenumber.getText().toString();
        mBirthplace = birthplace.getText().toString();
        mCompany = company.getText().toString();
        mFirstrecord = firstrecord.getText().toString();
        mFirsttime = firsttime.getText().toString();//编辑后的时间
        mNotes = notes.getText().toString();
        mSex = sex.equals("1") ? "男" : "女";
        if (addorupdate.getText().toString().equals("新增客户") || addorupdate.getText().toString().equals("指派客户")) {
            if (mPhonenumber.length() != 0 && mBoosname.length() != 0 && sources.length() != 0 && sex.length() != 0) {
                if (mPhonenumber.matches("^[1][3578][0-9]{9}$")) {
                    //如果时间和记录都为空，可以向下走
                    if (mFirstrecord.length() == 0 && mFirsttime.length() == 0) {
                        if (InternetUtils.internet(AddCustomerActivity.this)) {
                            addCustomer();
                        }
                    } else if (mFirsttime.length() != 0 && mFirstrecord.length() == 0) {//时间不空，记录为空，可以往下走，需要判断时间是否合理
                        if (DateCheckUtils.checkDate(mFirsttime) == 1) {
                            if (InternetUtils.internet(AddCustomerActivity.this)) {
                                addCustomer();
                            }
                        } else {
                            Toast.makeText(AddCustomerActivity.this, "选择日期不能大于当前日期", Toast.LENGTH_LONG).show();
                        }
                    } else if (mFirstrecord.length() != 0 && mFirsttime.length() == 0) {//时间为空，记录不空
                        Toast.makeText(AddCustomerActivity.this, "请输入最近拜访时间", Toast.LENGTH_LONG).show();
                    } else if (mFirstrecord.length() != 0 && mFirsttime.length() != 0) {//时间和记录都不空
                        if (DateCheckUtils.checkDate(mFirsttime) == 1) {
                            if (InternetUtils.internet(AddCustomerActivity.this)) {
                                addCustomer();
                            }
                        } else {
                            Toast.makeText(AddCustomerActivity.this, "选择日期不能大于当前日期", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(AddCustomerActivity.this, "手机号格式不正确", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(AddCustomerActivity.this, "来源、电话、工长、性别不能为空", Toast.LENGTH_LONG).show();
            }
        } else if (addorupdate.getText().toString().equals("编辑客户")) {
            //如果是编辑用户
            String headman_id = customer.getId();
            //更新客户信息
            Map<String, String> params = new HashMap<String, String>();
            int flag = 0;
            if (origin_firsttime.length() != 0) {
                format_time = origin_firsttime.substring(0, 10) + " " + origin_firsttime.substring(11, 16);
            } else {
                format_time = "";
            }

            if (!mBoosname.equals(origin_name)) {
                params.put("headman_name", mBoosname);
            }
            if (!mPhonenumber.equals(origin_phone)) {
                if (mPhonenumber.matches("^[1][3578][0-9]{9}$")) {
                    params.put("phone", mPhonenumber);
                } else {
                    Toast.makeText(AddCustomerActivity.this, "手机号格式不正确", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            if (!mBirthplace.equals(origin_birthplace)) {
                params.put("origin", mBirthplace);
            }
            if (!mCompany.equals(origin_company)) {
                params.put("corporation", mCompany);
            }
            if (!mNotes.equals(origin_notes)) {
                params.put("remark", mNotes);
            }
            if (!mFirstrecord.equals(origin_firstrecord)) {
                params.put("recent_visit_record", mFirstrecord + " ");
            }
            if (!mSex.equals(origin_sex)) {
                if (mSex.equals("男")) {
                    params.put("sex", "1");
                } else
                    params.put("sex", "2");
            }
            params.put("follow_up_advice", "无建议");
            if (!mFirsttime.equals("")) {
                if (DateCheckUtils.checkDate(mFirsttime) == 1) {
                    params.put("last_visit_time", mFirsttime);
                } else {
                    Toast.makeText(AddCustomerActivity.this, "修改日期不能大于当前日期", Toast.LENGTH_LONG).show();
                    flag = 1;
                }
            }

            if (InternetUtils.internet(this)) {
                if (flag == 0) {
                    UpdateCustomerService.getInstance(this).updateCustomer(AddCustomerActivity.this, headman_id, params);
                    finish();
                }
            }
        }
    }

    private void addCustomer() {
        if (SharedUtils.getUser("user", AddCustomerActivity.this).getSuperior_id().equals("28") || SharedUtils.getUser("user", AddCustomerActivity.this).getSuperior_id().equals("0")) {
            if (id != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddCustomerActivity.this);
                final AlertDialog dialog = builder.create();
                View view = LayoutInflater.from(AddCustomerActivity.this).inflate(R.layout.dialog_allocationcustomer, null);
                final TextView textView = (TextView) view.findViewById(R.id.dialog_allocation_sp);
                Button ok = (Button) view.findViewById(R.id.dialog_allocation_ok);
                textView.setText("你确定要把客户指派给 " + name + "  吗？");
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddCustomerService.getInstance(AddCustomerActivity.this).addCustomer(AddCustomerActivity.this, id, sex, sources, mBoosname, mPhonenumber, mBirthplace, mCompany, mFirstrecord, mFirsttime, mNotes);
                        dialog.dismiss();
                        setResult(2, getIntent().putExtra("REFRESH_FLAG", "REFRESH"));
                        finish();
                    }
                });
                dialog.setView(view, 20, 20, 20, 50);
                dialog.setCanceledOnTouchOutside(true);//点击边界取消
                dialog.show();

            }
        } else {
            AddCustomerService.getInstance(this).addCustomer(AddCustomerActivity.this, sex, sources, mBoosname, mPhonenumber, mBirthplace, mCompany, mFirstrecord, mFirsttime, mNotes);
            setResult(2, getIntent().putExtra("REFRESH_FLAG", "REFRESH"));
            finish();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName);
        MobclickAgent.onResume(mContext);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
        MobclickAgent.onPause(mContext);
    }
}
