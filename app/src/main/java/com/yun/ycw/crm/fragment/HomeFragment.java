package com.yun.ycw.crm.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.analytics.MobclickAgent;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.activity.AchievementSalesActivity;
import com.yun.ycw.crm.activity.AchievementTeamActivity;
import com.yun.ycw.crm.activity.CustomerActivity;
import com.yun.ycw.crm.activity.NewOrdersActivity;
import com.yun.ycw.crm.activity.StaffStaffAcitivity;
import com.yun.ycw.crm.activity.StaffTeamActivity;
import com.yun.ycw.crm.activity.SubUndoneActivity;
import com.yun.ycw.crm.activity.UndoneActivity;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.entity.Achievement;
import com.yun.ycw.crm.entity.User;
import com.yun.ycw.crm.utils.DataChooseUtils;
import com.yun.ycw.crm.utils.InternetUtils;
import com.yun.ycw.crm.utils.LoadingUtils;
import com.yun.ycw.crm.utils.SharedUtils;
import com.yun.ycw.crm.utils.TimeTransform;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 首页的fragment
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    @ViewInject(R.id.achievement_time)
    private TextView achievement_time;//所选时间
    @ViewInject(R.id.month_order_num)
    private TextView month_order_num;//当月单数


    @ViewInject(R.id.month_income)
    private TextView month_income;//当月收入
    @ViewInject(R.id.month_percentage)
    private TextView month_percentage;//当月提成
    @ViewInject(R.id.all_commisssion)
    private TextView all_commisssion;//总提成
    @ViewInject(R.id.deduct_commission)
    private TextView deduct_commission;//扣除提成
    @ViewInject(R.id.leader_num)
    private TextView leader_num;//注册工长数
    @ViewInject(R.id.company_num)
    private TextView company_num;//注册的公司数
    @ViewInject(R.id.undone_order_num)
    private TextView undone_order_num;//待回款单数
    @ViewInject(R.id.undone_money)
    private TextView undone_money;//待回款金额
    @ViewInject(R.id.home_fragment_commssiom2)
    private RelativeLayout home_fragment_commission2;
    @ViewInject(R.id.home_fragment_commssiom3)
    private LinearLayout home_fragment_commission3;
    @ViewInject(R.id.undone_layout)
    private LinearLayout undone_layout;
    @ViewInject(R.id.achievement_neworder)
    private LinearLayout achievement_neworder;//新增订单
    @ViewInject(R.id.achievement_layout)
    private LinearLayout achievement_layout;

    @ViewInject(R.id.fragment_home_customer1)
    private RelativeLayout fragment_home_customer1;//客户
    @ViewInject(R.id.fragment_home_customer2)
    private LinearLayout fragment_home_customer2;

    @ViewInject(R.id.homefragment_scheduleBarChart)
    private BarChart barChart;//柱状图
    private BarData data;
    private BarDataSet dataSet;

    private Achievement achievement, achievement2;
    private User user;
    private String token;
    private String mPageName = "HomeFragment";
    private Dialog dialog;//网络加载的对话框
    private TextView starttime;
    private TextView endtime;
    private AlertDialog timeDialog;//时间选择的对话框
    private String startTime;//业绩查询的开始时间
    private String endTime;//业绩查询的结束时间
    private String initStartDateTime; // 初始化开始时间
    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home, null);
        ViewUtils.inject(this, view);
        setTime();
       // dialog = LoadingUtils.createLoadingDialog(getActivity(), "正在加载");
       // dialog.show();
        user = SharedUtils.getUser("user", getActivity());
        setVisibility();
        token = SharedUtils.getToken(getActivity(), "info");
        downdata(startTime, endTime);
        return view;
    }

    private void setVisibility() {
        if (user.getSuperior_id().equals("0")) {
            home_fragment_commission2.setVisibility(View.GONE);
            home_fragment_commission3.setVisibility(View.GONE);
        }
    }

    private void downdata(final String startTime, final String endTime) {
        achievement = new Achievement();
        achievement2 = new Achievement();

        leader_num.setText("5000");//工长数
        company_num.setText("6000");//客户数
        undone_order_num.setText("200");//待回款单数
        undone_money.setText("600000");//待回款金额
        month_order_num.setText("1000");//当月单数
        month_income.setText("700000");//当月金额


            all_commisssion.setText("80000");//总提成
            achievement.setAllcommission("4000");

        achievement.setAllcustomers("6000");
        achievement.setAllmoney("700000");
        achievement.setAllorder("200");
        achievement.setSalesman("50");
        achievement.setSalesmanage("20");

        achievement.setUserid(user.getId());
        achievement.setUsername(user.getName());


        achievement.setEndtime(endTime);
        achievement.setStarttime(startTime);




            achievement2.setAllcommission("8888");

        achievement2.setAllcustomers( "");
        achievement2.setAllmoney( "50000");
        achievement2.setAllorder("500");
        achievement2.setSalesman("100");
        achievement2.setSalesmanage("20");

        achievement2.setUserid(user.getId());
        achievement2.setUsername(user.getName());


        achievement2.setEndtime(endTime);
        achievement2.setStarttime(startTime);
//上月业绩

        //配置图表
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<String>();
        float customer = 500;//(Float.parseFloat(achievement.getAllcustomers()) / Float.parseFloat(achievement2.getAllcustomers()) - 1);
        float order = 200;//(Float.parseFloat(achievement.getAllorder()) / Float.parseFloat(achievement2.getAllorder()) - 1);
        float money = 100000;//(Float.parseFloat(achievement.getAllmoney()) / Float.parseFloat(achievement2.getAllmoney()) - 1);
        float commission = 50000;//(Float.parseFloat(achievement.getAllcommission()) / Float.parseFloat(achievement2.getAllcommission()) - 1);

        entries.add(new BarEntry(customer * 100, 1));


            entries.add(new BarEntry(order * 100, 3));


            entries.add(new BarEntry(money * 100, 5));


            entries.add(new BarEntry(commission * 100, 7));

        for (int i = 0; i < 8; i++) {
            if (i == 1)
                xVals.add("客户");
            if (i == 3)
                xVals.add("订单");
            if (i == 5)
                xVals.add("业绩");
            if (i == 7)
                xVals.add("提成");
            if (i != 1 && i != 3 && i != 5 && i != 7)
                xVals.add("");
        }
        Log.i("Xsize", xVals.size() + "");
        dataSet = new BarDataSet(entries, "较上月同期增长率（%）");
        dataSet.setColors(new int[]{Color.rgb(23, 155, 72)});
        data = new BarData(xVals, dataSet);
        barChart.setData(data);
        barChart.setDescription(" ");
        barChart.animateY(1000);
        //图表的点击事件
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                if (entry.getXIndex() == 1) {
                    Toast.makeText(getActivity(), "上月同期录入客户: " +  "200个", Toast.LENGTH_SHORT).show();
                } else if (entry.getXIndex() == 3) {
                    Toast.makeText(getActivity(), "上月同期订单数: " + "100单", Toast.LENGTH_SHORT).show();
                } else if (entry.getXIndex() == 5) {
                    Toast.makeText(getActivity(), "上月同期业绩: "  + "55555元", Toast.LENGTH_SHORT).show();
                } else if (entry.getXIndex() == 7) {
                    Toast.makeText(getActivity(), "上月同期提成: " + "000000元", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });




       /*
        if (timeDialog != null) {
            timeDialog.dismiss();
        }
        if (InternetUtils.internet(getActivity())) {
            final HttpUtils httpUtils = new HttpUtils();
            final RequestParams params = new RequestParams();
            params.addQueryStringParameter("start_time", startTime);
            params.addQueryStringParameter("end_time", endTime);
            params.addHeader("Authorization", "Bearer" + " " + token);
            achievement = new Achievement();
            achievement2 = new Achievement();

            httpUtils.send(HttpRequest.HttpMethod.GET, Constants.GENERAL_URL + user.getId() + "/stat", params, new RequestCallBack<String>() {

                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    achievement_time.setText(startTime + "——" + endTime);
                    String jsonString = responseInfo.result;
                    try {
                        JSONObject Obj = new JSONObject(jsonString);

                        leader_num.setText(String.valueOf(Obj.getInt("customer_num")));//工长数
                        company_num.setText(String.valueOf(Obj.getInt("company_num")));//客户数
                        undone_order_num.setText(String.valueOf(Obj.getInt("waiting_collection_order")));//待回款单数
                        undone_money.setText(String.valueOf(Obj.getDouble("waiting_collection_prices")));//待回款金额
                        month_order_num.setText(String.valueOf(Obj.getInt("order_num")));//当月单数
                        month_income.setText(new BigDecimal(Obj.getDouble("order_prices")).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "");//当月金额

                        JSONObject obj = Obj.getJSONObject("month_stats");
                      //  all_commisssion.setText(new BigDecimal(obj.getDouble("accrued")).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + ""); //待提成
                      //  month_done_per.setText(new BigDecimal(obj.getDouble("income")).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "");//已提成
                        DecimalFormat df = new DecimalFormat("#.00");
                        double commission = obj.getDouble("accrued") + obj.getDouble("income");
                        new BigDecimal(commission).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        if (commission == 0) {
                            all_commisssion.setText("0.0");//总提成
                            achievement.setAllcommission("0.00");
                        } else {
                            all_commisssion.setText(df.format(commission));//总提成
                            achievement.setAllcommission(df.format(commission));
                        }
                        achievement.setAllcustomers(Obj.getInt("customer_num") + Obj.getInt("company_num") + "");
                        achievement.setAllmoney(new BigDecimal(Obj.getDouble("order_prices")).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "");
                        achievement.setAllorder(String.valueOf(Obj.getInt("order_num")));
                        achievement.setSalesman(Obj.getString("subordinate_num"));
                        achievement.setSalesmanage(Obj.getString("superior_num"));

                        achievement.setUserid(user.getId());
                        achievement.setUsername(user.getName());


                        achievement.setEndtime(endTime);
                        achievement.setStarttime(startTime);
//上月业绩

                        params.addQueryStringParameter("start_time", TimeTransform.getLastMouthTime(startTime));
                        params.addQueryStringParameter("end_time", TimeTransform.getLastMouthTime(endTime));

                        httpUtils.send(HttpRequest.HttpMethod.GET, Constants.GENERAL_URL + user.getId() + "/stat", params, new RequestCallBack<String>() {
                            @Override
                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                String jsonString = responseInfo.result;
                                try {
                                    JSONObject Obj = new JSONObject(jsonString);


                                    JSONObject obj = Obj.getJSONObject("month_stats");
                                    DecimalFormat df = new DecimalFormat("#.00");
                                    double commissionn = obj.getDouble("accrued") + obj.getDouble("income");
                                    new BigDecimal(commissionn).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                    if (commissionn == 0) {
                                        achievement2.setAllcommission("0.00");
                                    } else {
                                        achievement2.setAllcommission(df.format(commissionn));
                                    }
                                    achievement2.setAllcustomers(Obj.getInt("customer_num") + Obj.getInt("company_num") + "");
                                    achievement2.setAllmoney(new BigDecimal(Obj.getDouble("order_prices")).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "");
                                    achievement2.setAllorder(String.valueOf(Obj.getInt("order_num")));
                                    achievement2.setSalesman(Obj.getString("subordinate_num"));
                                    achievement2.setSalesmanage(Obj.getString("superior_num"));

                                    achievement2.setUserid(user.getId());
                                    achievement2.setUsername(user.getName());


                                    achievement2.setEndtime(endTime);
                                    achievement2.setStarttime(startTime);
//上月业绩

                                    //配置图表
                                    ArrayList<BarEntry> entries = new ArrayList<>();
                                    ArrayList<String> xVals = new ArrayList<String>();
                                    float customer = (Float.parseFloat(achievement.getAllcustomers()) / Float.parseFloat(achievement2.getAllcustomers()) - 1);
                                    float order = (Float.parseFloat(achievement.getAllorder()) / Float.parseFloat(achievement2.getAllorder()) - 1);
                                    float money = (Float.parseFloat(achievement.getAllmoney()) / Float.parseFloat(achievement2.getAllmoney()) - 1);
                                    float commission = (Float.parseFloat(achievement.getAllcommission()) / Float.parseFloat(achievement2.getAllcommission()) - 1);
                                    if (Float.parseFloat(achievement2.getAllcustomers()) != 0) {

                                        entries.add(new BarEntry(customer * 100, 1));
                                    } else {
                                        entries.add(new BarEntry(0, 1));
                                    }
                                    if (Float.parseFloat(achievement2.getAllorder()) != 0) {

                                        entries.add(new BarEntry(order * 100, 3));
                                    } else {
                                        entries.add(new BarEntry(0, 3));
                                    }
                                    if (Float.parseFloat(achievement2.getAllmoney()) != 0) {

                                        entries.add(new BarEntry(money * 100, 5));
                                    } else {
                                        entries.add(new BarEntry(0, 5));
                                    }
                                    if (Float.parseFloat(achievement2.getAllcommission()) != 0) {

                                        entries.add(new BarEntry(commission * 100, 7));
                                    } else {
                                        entries.add(new BarEntry(0, 7));
                                    }
                                    for (int i = 0; i < 8; i++) {
                                        if (i == 1)
                                            xVals.add("客户");
                                        if (i == 3)
                                            xVals.add("订单");
                                        if (i == 5)
                                            xVals.add("业绩");
                                        if (i == 7)
                                            xVals.add("提成");
                                        if (i != 1 && i != 3 && i != 5 && i != 7)
                                            xVals.add("");
                                    }
                                    Log.i("Xsize", xVals.size() + "");
                                    dataSet = new BarDataSet(entries, "较上月同期增长率（%）");
                                    dataSet.setColors(new int[]{Color.rgb(23, 155, 72)});
                                    data = new BarData(xVals, dataSet);
                                    barChart.setData(data);
                                    barChart.setDescription(" ");
                                    barChart.animateY(1000);
                                    //图表的点击事件
                                    barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                                        @Override
                                        public void onValueSelected(Entry entry, int i, Highlight highlight) {
                                            if (entry.getXIndex() == 1) {
                                                Toast.makeText(getActivity(), "上月同期录入客户: " + achievement2.getAllcustomers() + "个", Toast.LENGTH_SHORT).show();
                                            } else if (entry.getXIndex() == 3) {
                                                Toast.makeText(getActivity(), "上月同期订单数: " + achievement2.getAllorder() + "单", Toast.LENGTH_SHORT).show();
                                            } else if (entry.getXIndex() == 5) {
                                                Toast.makeText(getActivity(), "上月同期业绩: " + achievement2.getAllmoney() + "元", Toast.LENGTH_SHORT).show();
                                            } else if (entry.getXIndex() == 7) {
                                                Toast.makeText(getActivity(), "上月同期提成: " + achievement2.getAllcommission() + "元", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected() {

                                        }
                                    });


                                    dialog.dismiss();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    achievement.setEndtime(endTime);
                                    achievement.setStarttime(startTime);
                                }
                            }

                            @Override
                            public void onFailure(HttpException e, String s) {

                                dialog.dismiss();
                            }
                        });


                        dialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        achievement.setEndtime(endTime);
                        achievement.setStarttime(startTime);
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    achievement.setEndtime(endTime);
                    achievement.setStarttime(startTime);
                    dialog.dismiss();
                }
            });
        }*/
    }

    private void setTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);//将月至第一天
        calendar.set(Calendar.HOUR_OF_DAY, 0);//将小时至0
        calendar.set(Calendar.MINUTE, 0);//将分钟至0
        startTime = sDateFormat.format(calendar.getTime());
        endTime = sDateFormat.format(new Date().getTime());
    }

    @OnClick({R.id.undone_layout, R.id.achievement_layout, R.id.achievement_time, R.id.achievement_neworder, R.id.fragment_home_customer1, R.id.fragment_home_customer2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.undone_layout://点击待回款的部分，如果待回款订单数不为空就点击进去。
                if (!undone_order_num.getText().equals("0")) {
                    String loginUserId = SharedUtils.getUser("user", getActivity()).getId();//登录时的用户的id
                    String loginLevel = SharedUtils.getUser("user", getActivity()).getLevel();//登录时的用户级别
                    if (loginLevel.equals("three")) {
                        Intent intent = new Intent(getActivity(), UndoneActivity.class);
                        intent.putExtra("personId", loginUserId);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), SubUndoneActivity.class);
                        intent.putExtra("id", loginUserId);
                        intent.putExtra("level", loginLevel);
                        intent.putExtra("name", "");
                        startActivity(intent);
                    }
                }
                break;
            case R.id.achievement_layout:

                if (user.getSuperior_id().equals("0")) {
                    Intent i = new Intent(getActivity(), AchievementTeamActivity.class);
                    i.putExtra("achievement", achievement);
                    startActivity(i);
                } else if (user.getSuperior_id().equals("28")) {
                    Intent i = new Intent(getActivity(), AchievementSalesActivity.class);
                    i.putExtra("achievement", achievement);
                    startActivity(i);

                } else {
                    Intent i = new Intent(getActivity(), NewOrdersActivity.class);
                    i.putExtra("achievement", achievement);
                    startActivity(i);
                }

                break;
            case R.id.achievement_time:
                new DataChooseUtils(getActivity()).chooseTimeDialog(new DataChooseUtils.ChooseData() {
                    @Override
                    public void ChooseData(String StartTime, String EndTime) {
                        downdata(StartTime, EndTime);

                    }
                });
                break;
            case R.id.achievement_neworder:
                if (user.getSuperior_id().equals("0")) {
                    Intent i = new Intent(getActivity(), AchievementTeamActivity.class);
                    i.putExtra("achievement", achievement);
                    startActivity(i);
                } else if (user.getSuperior_id().equals("28")) {
                    Intent i = new Intent(getActivity(), AchievementSalesActivity.class);
                    i.putExtra("achievement", achievement);
                    startActivity(i);

                } else {
                    Intent i = new Intent(getActivity(), NewOrdersActivity.class);
                    i.putExtra("achievement", achievement);
                    startActivity(i);
                }

                break;
            case R.id.fragment_home_customer1:
            case R.id.fragment_home_customer2:
                Intent i = new Intent() {};
                i.putExtra("achievement", achievement);
                if (user.getSuperior_id().equals("0")) {
                    i.setClass(getActivity(), StaffTeamActivity.class);
                    startActivity(i);
                } else if (user.getSuperior_id().equals("28")) {
                    i.setClass(getActivity(), StaffStaffAcitivity.class);
                    startActivity(i);

                } else {
                    i.setClass(getActivity(), CustomerActivity.class);
                    i.putExtra("isPublicOcean", "0");
                    startActivity(i);
                }

                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
    }


}
