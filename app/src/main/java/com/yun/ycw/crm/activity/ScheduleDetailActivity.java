package com.yun.ycw.crm.activity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.entity.Schedule;
import com.yun.ycw.crm.entity.User;
import com.yun.ycw.crm.utils.InternetUtils;
import com.yun.ycw.crm.utils.LoadingUtils;
import com.yun.ycw.crm.utils.SharedUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 计划详情
 * Created by wdyan on 2016/3/25.
 */
public class ScheduleDetailActivity extends Activity implements View.OnClickListener {
    @ViewInject(R.id.target_money_detail)
    private TextView target_money_detail;//目标业绩
    @ViewInject(R.id.target_chief_detail)
    private TextView target_chief_detail;//目标开发工长数
    @ViewInject(R.id.target_visitChief_detail)
    private TextView target_visitChief_detail;//目标回访工长数
    @ViewInject(R.id.schedule_note_detail)
    private TextView schedule_note_detail;//计划备注
    @ViewInject(R.id.schedule_time_show)
    private TextView schedule_time_show;//计划时间显示
    @ViewInject(R.id.scheduleBarChart)
    private BarChart barChart;//柱状图
    @ViewInject(R.id.schedule_detail_back)
    private ImageButton schedule_detail_back;//返回button

    private User user;
    private Dialog dialog;
    private RequestQueue requestQueue;

    private BarData data;
    private BarDataSet dataSet;
    private String order_prices;
    private String order_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_detail);
        ViewUtils.inject(this);
        initView();
        init();
    }

    private void initView() {
        schedule_detail_back.setOnClickListener(this);
        dialog = LoadingUtils.createLoadingDialog(this, "正在加载");
        dialog.show();
    }

    private void init() {
        final Schedule s = (Schedule) getIntent().getSerializableExtra("Schedule");
        target_money_detail.setText(s.getAchievement() + "元");//计划业绩
        target_chief_detail.setText(s.getJoin_headman() + "个");//计划开发工长数
        target_visitChief_detail.setText(s.getVisit_headman() + "个");//计划回访工长数
        schedule_time_show.setText(s.getStartTime() + "~" + s.getEndTime());//计划时间显示
        if (s.getRemarks().equals("")) {
            schedule_note_detail.setText("无");
        } else
            schedule_note_detail.setText(s.getRemarks());

        user = SharedUtils.getUser("user", this);
        final String token = SharedUtils.getToken(this, "info");
        if (InternetUtils.internet(this)) {
            requestQueue = Volley.newRequestQueue(this);
            Log.i("getScheduleUrl   ", Constants.GENERAL_URL + s.getUser_id() + "/stat?start_time=" + s.getStartTime() + "&end_time=" + s.getEndTime());
            StringRequest request = new StringRequest(Request.Method.GET, Constants.GENERAL_URL + s.getUser_id() + "/stat?start_time=" + s.getStartTime() + "&end_time=" + s.getEndTime(), new Response.Listener<String>() {
                @Override
                public void onResponse(String json) {
                    try {
                        //获取某一个周的实际业绩，用于和计划做对比
                        JSONObject trueObj = new JSONObject(json);
                        order_prices = trueObj.getString("order_prices");
                        order_num = trueObj.getString("order_num");

                        float percentage_prices = Float.parseFloat(order_prices) / Float.parseFloat(s.getAchievement());
                        float percentage_num = Float.parseFloat(order_num) / Float.parseFloat(s.getJoin_headman());
                        //配置图表
                        ArrayList<BarEntry> entries = new ArrayList<>();
                        ArrayList<String> xVals = new ArrayList<String>();

                        entries.add(new BarEntry(percentage_prices * 100, 2));
                        entries.add(new BarEntry(percentage_num * 100, 6));
                        for (int i = 0; i < 9; i++) {
                            if (i == 2)
                                xVals.add("业绩");
                            if (i == 6)
                                xVals.add("开发工长");
                            if (i != 2 && i != 6)
                                xVals.add("");
                        }
                        Log.i("Xsize", xVals.size() + "");
                        dataSet = new BarDataSet(entries, "实际/目标百分比(%)");
                        dataSet.setColors(new int[]{Color.rgb(23, 155, 72)});
                        data = new BarData(xVals, dataSet);
                        barChart.setData(data);
                        barChart.setDescription(" ");
                        barChart.animateY(1000);

                        //图表的点击事件
                        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                            @Override
                            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                                if (entry.getXIndex() == 2) {
                                    Toast.makeText(ScheduleDetailActivity.this, "实际业绩: " + order_prices, Toast.LENGTH_SHORT).show();
                                } else if (entry.getXIndex() == 6) {
                                    Toast.makeText(ScheduleDetailActivity.this, "实际开发工长数: " + order_num, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onNothingSelected() {

                            }
                        });
                        dialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    dialog.dismiss();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer" + " " + token);
                    return headers;
                }
            };
            requestQueue.add(request);
        }
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
