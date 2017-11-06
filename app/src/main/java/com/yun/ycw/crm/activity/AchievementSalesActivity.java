package com.yun.ycw.crm.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.adapter.ActhievementSalesAdapter;
import com.yun.ycw.crm.comparator.AchievementComparator;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * Created by scb on 2016/2/24.
 */
public class AchievementSalesActivity extends Activity {
    @ViewInject(R.id.achievementsaleamen_man)
    private TextView man;//当前是谁
    @ViewInject(R.id.achievementsaleamen_time)
    private TextView time;//时间

    @ViewInject(R.id.achievementsaleamen_rg)
    private RadioGroup achievementsaleamen_rg;//
    @ViewInject(R.id.achievementsaleamen_commission)
    private RadioButton achievementsaleamen_commission;//提成排序
    @ViewInject(R.id.achievementsaleamen_customer)
    private RadioButton achievementsaleamen_customer;//客户排序
    @ViewInject(R.id.achievementsaleamen_money)
    private RadioButton achievementsaleamen_money;//业绩排序
    @ViewInject(R.id.achievementsaleamen_order)
    private RadioButton achievementsaleamen_order;//订单排序



    @ViewInject(R.id.achievementsaleamen_allcommission)
    private TextView commission;//总提成
    @ViewInject(R.id.achievementsaleamen_salesman)
    private TextView salesman;//销售员数
    @ViewInject(R.id.achievementasleamen_allorder)
    private TextView allorder;//总下单
    @ViewInject(R.id.achievementsaleamen_allcustomer)
    private TextView allcustomer;//总客户
    @ViewInject(R.id.achievementsaleamen_allmoney)
    private TextView allmoney;//总金额
    @ViewInject(R.id.achievementsaleamen_back)
    private ImageButton back;//
    @ViewInject(R.id.achievementsaleamen_lv)
    private ListView listView;//
    private ActhievementSalesAdapter acthievementSalesAdapter;
    private List<Achievement> list,list2;
    private Achievement ach;
    private Context mContext;
    private String mPageName = "AchievementSalesActivity";
    private Dialog dialog;
    private AchievementComparator achievementComparator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievementsalesman);
        ViewUtils.inject(this);
        mContext = this;
        list = new ArrayList<Achievement>();
        list2 = new ArrayList<Achievement>();
        setdata();//
        downdata(ach.getStarttime(), ach.getEndtime());
    }

    private void setdata() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ach = (Achievement) getIntent().getSerializableExtra("achievement");
        time.setText(ach.getStarttime() + "——" + ach.getEndtime());
        man.setText(ach.getUsername());
        commission.setText(ach.getAllcommission());
        salesman.setText(ach.getSalesman());
        allorder.setText(ach.getAllorder());
        allcustomer.setText(ach.getAllcustomers());
        allmoney.setText(ach.getAllmoney());
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DataChooseUtils(AchievementSalesActivity.this).chooseTimeDialog(new DataChooseUtils.ChooseData() {
                    @Override
                    public void ChooseData(String StartTime, String EndTime) {
                        downdata(StartTime, EndTime);

                    }
                });
            }
        });
    }

    private void downdata(final String startTime, final String endTime) {

        if (InternetUtils.internet(this)) {
            if (list.size() != 0) {
                list.clear();
            }
            dialog = LoadingUtils.createLoadingDialog(AchievementSalesActivity.this, "正在加载");
            dialog.show();
           final HttpUtils httpUtils = new HttpUtils();
            //设置lru缓存时间是0s，如果不设置，默认的是60s
            httpUtils.configCurrentHttpCacheExpiry(0);
           final RequestParams params = new RequestParams();
            params.addHeader("Authorization", "Bearer" + " " + SharedUtils.getToken(this, "info"));
            params.addQueryStringParameter("start_time", startTime);
            params.addQueryStringParameter("end_time", endTime);
            httpUtils.send(HttpRequest.HttpMethod.GET, Constants.STAFF_URL + ach.getUserid() + "/sub-list", params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    JSONArray array = null;
                    try {
                        array = new JSONArray(responseInfo.result);

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

                                JSONArray array = null;
                                try {
                                    array = new JSONArray(responseInfo.result);

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
                                    acthievementSalesAdapter = new ActhievementSalesAdapter(list, list2, AchievementSalesActivity.this);
                                    listView.setAdapter(acthievementSalesAdapter);
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
    @OnRadioGroupCheckedChange({R.id.achievementsaleamen_rg})
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.achievementsaleamen_commission:
                typeSort("COMMISSION");
                break;
            case R.id.achievementsaleamen_customer:
                typeSort("CUSTOMER");
                break;
            case R.id.achievementsaleamen_money:
                typeSort("MONEY");
                break;
            case R.id.achievementsaleamen_order:
                typeSort("ORDER");
                break;
        }
    }
    public void typeSort(String info) {
        achievementComparator = new AchievementComparator(info);
        Collections.sort(list, achievementComparator);
        acthievementSalesAdapter = new ActhievementSalesAdapter(list, list2, AchievementSalesActivity.this);
        listView.setAdapter(acthievementSalesAdapter);
        acthievementSalesAdapter.notifyDataSetChanged();
    }
    @OnItemClick({R.id.achievementsaleamen_lv})
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Achievement achievement = acthievementSalesAdapter.getItem((int) id);
        Intent intent = new Intent(AchievementSalesActivity.this, NewOrdersActivity.class);
        intent.putExtra("achievement", achievement);
        startActivity(intent);
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
        int customer = 0;
        int order = 0;
        double money = 0;
        double commissionn = 0;
        for (Achievement achievement : list) {
            customer += Integer.parseInt(achievement.getAllcustomers());
            order += Integer.parseInt(achievement.getAllorder());
            money += Double.parseDouble(achievement.getAllmoney());
            commissionn += Double.parseDouble(achievement.getAllcommission());
        }
        commission.setText(MyBigDecimal.getBigDecimal(commissionn));
        allmoney.setText(MyBigDecimal.getBigDecimal(money));
        salesman.setText(list.size() + "");
        allorder.setText(order + "");
        allcustomer.setText(customer + "");
    }
}
