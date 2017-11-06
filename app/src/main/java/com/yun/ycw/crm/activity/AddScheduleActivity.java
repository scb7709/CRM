package com.yun.ycw.crm.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.customview.DatePickDialog;
import com.yun.ycw.crm.service.AddScheduleService;
import com.yun.ycw.crm.utils.DateCheckUtils;
import com.yun.ycw.crm.utils.InternetUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 新建工作计划
 * Created by wdyan on 2016/3/24.
 */
public class AddScheduleActivity extends Activity implements View.OnClickListener {
    @ViewInject(R.id.add_schedule_back)
    private ImageButton add_schedule_back;//返回button
    @ViewInject(R.id.confirm_add_schedule)
    private Button confirm_add_schedule;//确定新建button
    @ViewInject(R.id.target_money)
    private EditText target_money;//本周目标业绩
    @ViewInject(R.id.target_chief)
    private EditText target_chief;//本周目标开发工长数
    @ViewInject(R.id.target_visitChief)
    private EditText target_visitChief;//本周目标回访工长数
    @ViewInject(R.id.select_date_layout)
    private LinearLayout select_date_layout;//日期选择linearlayout
    @ViewInject(R.id.schedule_startTime)
    private TextView schedule_startTime;//日期选址-->开始时间
    @ViewInject(R.id.schedule_endTime)
    private TextView schedule_endTime;//日期选择-->结束时间
    @ViewInject(R.id.schedule_note)
    private EditText schedule_note;//计划备注
    private String money;
    private String chief;
    private String visitChief;
    private String startTime;
    private String note;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);
        ViewUtils.inject(this);
        initViews();
        init();
    }

    private void initViews() {
        add_schedule_back.setOnClickListener(this);
        confirm_add_schedule.setOnClickListener(this);
    }

    private void init() {

        /*
        //获取下周的第一天和第五天
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance(Locale.CHINA);
        c.set(Calendar.DAY_OF_WEEK, 2);
        //获取下周的第一天
        schedule_startTime.setText(sdf.format(c.getTime().getTime() + (7 * 24 * 60 * 60 * 1000)));
        //获取下周的第五天
        schedule_endTime.setText(sdf.format(c.getTime().getTime() + (11 * 24 * 60 * 60 * 1000)));
        */
        //开始时间选择
        schedule_startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //初始时间
                String origin_start = sdf.format(new Date());
                DatePickDialog dialog = new DatePickDialog(AddScheduleActivity.this, origin_start);
                dialog.dateTimePicKDialog(schedule_startTime);
            }
        });

        //结束时间选择
        schedule_endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				//结束时间
                String origin_end = sdf.format(new Date());
                DatePickDialog dialog = new DatePickDialog(AddScheduleActivity.this, origin_end);
                dialog.dateTimePicKDialog(schedule_endTime);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击返回button
            case R.id.add_schedule_back:
                finish();
                break;
            //点击确定button
            case R.id.confirm_add_schedule:
                if (InternetUtils.internet(this)) {
                    if (inputComplete()) {
                        //开始时间和结束时间作对比，结束时间不能小于开始时间
                        //获取开始时间
                        String schedule_start = schedule_startTime.getText() + "";
                        String schedule_end = schedule_endTime.getText() + "";
                        if (DateCheckUtils.isEndAfterStartTime(schedule_start, schedule_end) == 1) {
                            AddScheduleService.getInstance(this).addSchedule(this, money, chief, visitChief, schedule_start,schedule_end, note);
                            finish();
                        } else {
                            toast("结束日期要大于开始日期");
                        }
//                        toast("成功");
                    } else toast("输入不完整");
                }

                break;
        }
    }

    private boolean inputComplete() {
        //本周目标业绩
        money = target_money.getText().toString();
        //本周开发工长数
        chief = target_chief.getText().toString();
        //回访工长数
        visitChief = target_visitChief.getText().toString();
        //下周的某一天
        startTime = schedule_startTime.getText().toString();
        //备注
        note = schedule_note.getText().toString();
        return (money.length() != 0 && chief.length() != 0 && visitChief.length() != 0);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
