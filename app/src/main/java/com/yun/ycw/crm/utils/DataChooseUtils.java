package com.yun.ycw.crm.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yun.ycw.crm.R;
import com.yun.ycw.crm.customview.DatePickDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by scb on 2016/3/23.
 */
public class DataChooseUtils {

    private TextView starttime;
    private TextView endtime;
    private AlertDialog timeDialog;//时间选择的对话框
    private String startTime;//业绩查询的开始时间
    private String endTime;//业绩查询的结束时间
    private String initStartDateTime; // 初始化开始时间
    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Activity context;

    public DataChooseUtils(Activity context) {
        this.context = context;
    }

    public interface ChooseData {
        public void ChooseData(String StartTime, String EndTime);
    }

    public void chooseTimeDialog(final ChooseData chooseData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        timeDialog = builder.create();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_choosetime, null);
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.dialog_choosetime_group);
        RadioButton day = (RadioButton) view.findViewById(R.id.dialog_choosetime_day);

        RadioButton week = (RadioButton) view.findViewById(R.id.dialog_choosetime_week);
        RadioButton month = (RadioButton) view.findViewById(R.id.dialog_choosetime_month);
        starttime = (TextView) view.findViewById(R.id.dialog_choosetime_starttime);
        endtime = (TextView) view.findViewById(R.id.dialog_choosetime_endtime);
        final Calendar calendarday = Calendar.getInstance();
        starttime.setText(sDateFormat.format(calendarday.getTime()));
        endtime.setText(sDateFormat.format(new Date().getTime()));
        day.setChecked(true);
        Button ok = (Button) view.findViewById(R.id.dialog_choosetime_ok);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.dialog_choosetime_day:
                        starttime.setText(sDateFormat.format(calendarday.getTime()));
                        endtime.setText(sDateFormat.format(new Date().getTime()));
                        break;
                    case R.id.dialog_choosetime_week:
                        Calendar calendarweek = Calendar.getInstance();
                        calendarweek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                        starttime.setText(sDateFormat.format(calendarweek.getTime()));
                        endtime.setText(sDateFormat.format(new Date().getTime()));
                        break;
                    case R.id.dialog_choosetime_month:
                        Calendar calendarmonth = Calendar.getInstance();
                        calendarmonth.set(Calendar.DAY_OF_MONTH, 1);//将月至第一天
                        starttime.setText(sDateFormat.format(calendarmonth.getTime()));
                        endtime.setText(sDateFormat.format(new Date().getTime()));
                        break;
                }
            }
        });
        starttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                initStartDateTime = sDateFormat.format(new Date());
                DatePickDialog dateTimePicKDialog = new DatePickDialog(context, initStartDateTime);
                dateTimePicKDialog.dateTimePicKDialog(starttime);
            }
        });
        endtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sDateFormat2 = new SimpleDateFormat("yyyy年MM月dd日");
                initStartDateTime = sDateFormat2.format(new Date());
                DatePickDialog dateTimePicKDialog2 = new DatePickDialog(context, initStartDateTime);
                dateTimePicKDialog2.dateTimePicKDialog(endtime);
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTime = starttime.getText().toString();
                endTime = endtime.getText().toString();
                Date d1 = null;
                Date d2 = null;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                if (startTime.length() != 0 && endTime.length() != 0) {
                    try {
                        d1 = simpleDateFormat.parse(startTime);
                        d2 = simpleDateFormat.parse(endTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (DateCheckUtils.checkDate2(startTime) == 1) {
                        if (d1.getTime() <= d2.getTime()) {
                            chooseData.ChooseData(startTime, endTime);
                            timeDialog.dismiss();
                        } else {
                            Toast.makeText(context, "开始时间不能大于结束时间", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(context, "开始时间不能大于当前日期", Toast.LENGTH_LONG).show();
                    }
                } else {
                    timeDialog.dismiss();
                    Toast.makeText(context, "没有选中时间", Toast.LENGTH_LONG).show();
                }
            }
        });


        timeDialog.setView(view, 20, 20, 20, 50);
        timeDialog.setCanceledOnTouchOutside(true);//点击边界取消
        timeDialog.show();

    }

}
