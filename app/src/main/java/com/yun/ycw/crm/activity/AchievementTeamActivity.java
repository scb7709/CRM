package com.yun.ycw.crm.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.lidroid.xutils.view.annotation.event.OnRadioGroupCheckedChange;
import com.umeng.analytics.MobclickAgent;
import com.yun.ycw.crm.API.RetrofitInterface;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.adapter.ActhievementTeamAdapter;
import com.yun.ycw.crm.adapter.NewOrderAdapter;
import com.yun.ycw.crm.comparator.AchievementComparator;
import com.yun.ycw.crm.comparator.StringConverter;
import com.yun.ycw.crm.comparator.UndoneOrderComparator;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.entity.Achievement;
import com.yun.ycw.crm.utils.DataChooseUtils;
import com.yun.ycw.crm.utils.InternetUtils;
import com.yun.ycw.crm.utils.LoadingUtils;
import com.yun.ycw.crm.utils.MyBigDecimal;
import com.yun.ycw.crm.utils.SharedUtils;
import com.yun.ycw.crm.utils.TimeTransform;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by scb on 2016/2/24.
 */
public class AchievementTeamActivity extends Activity {

    @ViewInject(R.id.achievementteam_man)
    private TextView man;//当前是谁
    @ViewInject(R.id.achievementteam_time)
    private TextView time;//时间

    @ViewInject(R.id.achievementteam_rg)
    private RadioGroup achievementteam_rg;//
    @ViewInject(R.id.achievementteam_commission)
    private RadioButton achievementteam_commission;//提成排序
    @ViewInject(R.id.achievementteam_customer)
    private RadioButton achievementteam_customer;//客户排序
    @ViewInject(R.id.achievementteam_money)
    private RadioButton achievementteam_money;//业绩排序
    @ViewInject(R.id.achievementteam_order)
    private RadioButton achievementteam_order;//订单排序

    @ViewInject(R.id.achievimentteam_commission)
    private TextView commission;//总提成
    @ViewInject(R.id.achievimentteam_team)
    private TextView team;//销售主管数
    @ViewInject(R.id.achievimentteam_allcustomer)
    private TextView allcustomer;//总客户
    @ViewInject(R.id.achievimentteam_allorder)
    private TextView allorder;//总下单
    @ViewInject(R.id.achievimentteam_allmoney)
    private TextView allmoney;//总金额
    @ViewInject(R.id.achievementteam_back)
    private ImageButton back;//
    @ViewInject(R.id.achievimentteam_lv)
    private ListView listView;//
    private ActhievementTeamAdapter achievementTeamAdapter;
    private List<Achievement> list;
    private List<Achievement> list2;
    private Achievement ach;
    private Context mContext;
    private String mPageName = "AchievementTeamActivity";
    private Dialog dialog;
    private AchievementComparator achievementComparator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievementteam);
        ViewUtils.inject(this);
        mContext = this;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        list = new ArrayList<Achievement>();
        list2 = new ArrayList<Achievement>();
        setdata();
        downdata(ach.getStarttime(), ach.getEndtime());
    }

    private void setdata() {
        ach = (Achievement) getIntent().getSerializableExtra("achievement");
        time.setText(ach.getStarttime() + "——" + ach.getEndtime());
        man.setText(ach.getUsername());
        team.setText(ach.getSalesmanage());
        commission.setText(ach.getAllcommission());
        allorder.setText(ach.getAllorder());
        allcustomer.setText(ach.getAllcustomers());
        allmoney.setText(ach.getAllmoney());
        listView.setCacheColorHint(0x00FF00);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DataChooseUtils(AchievementTeamActivity.this).chooseTimeDialog(new DataChooseUtils.ChooseData() {
                    @Override
                    public void ChooseData(String StartTime, String EndTime) {
                        downdata(StartTime, EndTime);

                    }
                });
            }
        });
    }

    @OnRadioGroupCheckedChange({R.id.achievementteam_rg})
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.achievementteam_commission:
                typeSort("COMMISSION");
                break;
            case R.id.achievementteam_customer:

                typeSort("CUSTOMER");
                break;
            case R.id.achievementteam_money:
                typeSort("MONEY");
                break;
            case R.id.achievementteam_order:
                typeSort("ORDER");
                break;
        }
    }
    public void typeSort(String info) {
        achievementComparator = new AchievementComparator(info);
        Collections.sort(list, achievementComparator);
        achievementTeamAdapter = new ActhievementTeamAdapter(list, list2, AchievementTeamActivity.this);
        listView.setAdapter(achievementTeamAdapter);
        achievementTeamAdapter.notifyDataSetChanged();
    }
    private void downdata(final String startTime, final String endTime) {

        if (InternetUtils.internet(this)) {
            if (list.size() != 0) {
                list.clear();
            }
            dialog = LoadingUtils.createLoadingDialog(AchievementTeamActivity.this, "正在加载");
            dialog.show();
            final  HttpUtils httpUtils = new HttpUtils();
            httpUtils.configCurrentHttpCacheExpiry(0);
           final RequestParams params = new RequestParams();
            params.addHeader("Authorization", "Bearer" + " " + SharedUtils.getToken(this, "info"));
            params.addQueryStringParameter("start_time", startTime);
            params.addQueryStringParameter("end_time", endTime);
            httpUtils.send(HttpRequest.HttpMethod.GET, Constants.STAFF_URL + ach.getUserid() + "/sub-list", params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    String s = responseInfo.result;
                    JSONArray array = null;


                    try {
                        array = new JSONArray(s);
                        for (int i = 0; i < array.length(); i++) {
                            Achievement achievement = new Achievement();
                            achievement.setUsername(array.getJSONObject(i).getString("user_login"));
                            achievement.setUserid(array.getJSONObject(i).getString("id"));
                            achievement.setAllcommission(MyBigDecimal.getBigDecimal(array.getJSONObject(i).getJSONObject("stats").getJSONObject("month_stats").getDouble("accrued") + array.getJSONObject(i).getJSONObject("stats").getJSONObject("month_stats").getDouble("income")));
                            achievement.setAllcustomers(array.getJSONObject(i).getJSONObject("stats").getInt("customer_num") + array.getJSONObject(i).getJSONObject("stats").getInt("company_num") + "");
                            achievement.setAllmoney(MyBigDecimal.getBigDecimal(array.getJSONObject(i).getJSONObject("stats").getDouble("order_prices")));
                            achievement.setAllorder(String.valueOf(array.getJSONObject(i).getJSONObject("stats").getInt("order_num")));
                            achievement.setSalesman(array.getJSONObject(i).getJSONObject("stats").getString("subordinate_num"));
                            achievement.setSalesmanage(array.getJSONObject(i).getJSONObject("stats").getString("superior_num"));
                            achievement.setStarttime(startTime);
                            achievement.setEndtime(endTime);
                            list.add(achievement);
                        }
                        if (list2.size() != 0) {
                            list2.clear();
                        }

                        params.addQueryStringParameter("start_time", TimeTransform.getLastMouthTime(startTime));
                        params.addQueryStringParameter("end_time", TimeTransform.getLastMouthTime(endTime));
                        httpUtils.send(HttpRequest.HttpMethod.GET, Constants.STAFF_URL + ach.getUserid() + "/sub-list", params, new RequestCallBack<String>() {
                            @Override
                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                time.setText(startTime + "——" + endTime);
                                String s = responseInfo.result;
                                JSONArray array = null;
                                try {
                                    array = new JSONArray(s);
                                    for (int i = 0; i < array.length(); i++) {
                                        Achievement achievement = new Achievement();
                                        achievement.setUsername(array.getJSONObject(i).getString("user_login"));
                                        achievement.setUserid(array.getJSONObject(i).getString("id"));
                                        achievement.setAllcommission(MyBigDecimal.getBigDecimal(array.getJSONObject(i).getJSONObject("stats").getJSONObject("month_stats").getDouble("accrued") + array.getJSONObject(i).getJSONObject("stats").getJSONObject("month_stats").getDouble("income")));
                                        achievement.setAllcustomers(array.getJSONObject(i).getJSONObject("stats").getInt("customer_num") + array.getJSONObject(i).getJSONObject("stats").getInt("company_num") + "");
                                        achievement.setAllmoney(MyBigDecimal.getBigDecimal(array.getJSONObject(i).getJSONObject("stats").getDouble("order_prices")));
                                        achievement.setAllorder(String.valueOf(array.getJSONObject(i).getJSONObject("stats").getInt("order_num")));
                                        achievement.setSalesman(array.getJSONObject(i).getJSONObject("stats").getString("subordinate_num"));
                                        achievement.setSalesmanage(array.getJSONObject(i).getJSONObject("stats").getString("superior_num"));
                                        achievement.setStarttime(startTime);
                                        achievement.setEndtime(endTime);
                                        list2.add(achievement);
                                    }
                                    getSum(list);
                                    achievementTeamAdapter = new ActhievementTeamAdapter(list, list2, AchievementTeamActivity.this);
                                    listView.setAdapter(achievementTeamAdapter);
                                    dialog.dismiss();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onFailure(HttpException e, String s) {
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(HttpException e, String s) {
                }
            });
        }
    }


    @OnItemClick({R.id.achievimentteam_lv})
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Achievement achievement = achievementTeamAdapter.getItem((int) id);
        //Achievement achievement = list.get(position - 1);

        if (!achievement.getSalesman().equals("0")) {
            Intent intent = new Intent(AchievementTeamActivity.this, AchievementSalesActivity.class);
            intent.putExtra("achievement", achievement);
            startActivity(intent);
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

    private void getSum(List<Achievement> list) {
        int sales = 0;
        int customer = 0;
        int order = 0;
        double money = 0;
        double commissionn = 0;

        for (Achievement achievement : list) {
            sales += Integer.parseInt(achievement.getSalesman());
            customer += Integer.parseInt(achievement.getAllcustomers());
            order += Integer.parseInt(achievement.getAllorder());
            money += Double.parseDouble(achievement.getAllmoney());
            commissionn += Double.parseDouble(achievement.getAllcommission());
        }

        team.setText(list.size() + "");
        commission.setText(MyBigDecimal.getBigDecimal(commissionn));
        allmoney.setText(MyBigDecimal.getBigDecimal(money));
        allorder.setText(order + "");
        allcustomer.setText(customer + "");
    }

}
