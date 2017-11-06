package com.yun.ycw.crm.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
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
import com.umeng.analytics.MobclickAgent;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.adapter.StaffTeamAdapter;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.entity.Achievement;
import com.yun.ycw.crm.entity.User;
import com.yun.ycw.crm.utils.InternetUtils;
import com.yun.ycw.crm.utils.LoadingUtils;
import com.yun.ycw.crm.utils.SharedUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by scb on 2016/3/24.
 */
public class StaffTeamActivity extends Activity {
    @ViewInject(R.id.fragmentstaffteam_team)
    private TextView team;//销售主管数
    @ViewInject(R.id.activity_staffteam_back)
    private ImageButton activity_staffteam_back;//销售主管数


    @ViewInject(R.id.fragmentstaffteam_customer)
    private TextView allcustomer;//总客户
    @ViewInject(R.id.fragmentstaffteam__lv)
    private ListView listView;//
    @ViewInject(R.id.fragmentstaffteam_sales)
    private TextView allsales;//
    private StaffTeamAdapter staffTeamAdapter;
    private List<Achievement> list;
    private Achievement achievement;
    private User user;
    private View topView;
    private String mPageName = "StaffTeamFragment";
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staffteam);
        ViewUtils.inject(this);
        user = SharedUtils.getUser("user", StaffTeamActivity.this);

        list = new ArrayList<Achievement>();
        activity_staffteam_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        downdata();


    }

    @OnItemClick({R.id.fragmentstaffteam__lv})
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Achievement achievement = staffTeamAdapter.getItem((int) id);
        String str="&start_time="+achievement.getStarttime()+"&end_time="+achievement.getEndtime();
        Log.i("VVVVVVVVVVVV", str);
        if (!achievement.getSalesman().equals("0")) {
            Intent intent = new Intent(StaffTeamActivity.this, StaffStaffAcitivity.class);
            intent.putExtra("achievement", achievement);
            startActivity(intent);
        }
    }

    private void downdata() {

          final   Achievement   ach = (Achievement) getIntent().getSerializableExtra("achievement");

        if (InternetUtils.internet(StaffTeamActivity.this)) {
            dialog = LoadingUtils.createLoadingDialog(StaffTeamActivity.this, "正在加载");
            dialog.show();
            HttpUtils httpUtils = new HttpUtils();
            RequestParams params = new RequestParams();
            params.addQueryStringParameter("start_time", ach.getStarttime());
            params.addQueryStringParameter("end_time", ach.getEndtime());
            params.addHeader("Authorization", "Bearer" + " " + SharedUtils.getToken(StaffTeamActivity.this, "info"));
            httpUtils.send(HttpRequest.HttpMethod.GET, Constants.STAFF_URL + user.getId() + "/sub-list", params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    String s = responseInfo.result;
                    Log.i("22222222", s);
                    JSONArray array = null;
                    try {
                        array = new JSONArray(s);
                        for (int i = 0; i < array.length(); i++) {
                            Achievement achievement = new Achievement();
                            achievement.setUsername(array.getJSONObject(i).getString("user_login"));
                            achievement.setUserid(array.getJSONObject(i).getString("id"));
                            achievement.setAllcustomers(array.getJSONObject(i).getJSONObject("stats").getInt("customer_num") + array.getJSONObject(i).getJSONObject("stats").getInt("company_num") + "");
                            achievement.setSalesman(array.getJSONObject(i).getJSONObject("stats").getString("subordinate_num"));
                            achievement.setSalesmanage(array.getJSONObject(i).getJSONObject("stats").getString("superior_num"));
                            achievement.setStarttime(ach.getStarttime());
                            achievement.setEndtime(ach.getEndtime());
                            list.add(achievement);
                        }
                        getSum(list);
                        staffTeamAdapter = new StaffTeamAdapter(list);
                        listView.setAdapter(staffTeamAdapter);
                        dialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    dialog.dismiss();
                    try {
                        Log.i("22222222", achievement.getUserid());
                    } catch (NullPointerException n) {
                    }
                }
            });
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

    private void getSum(List<Achievement> list) {
        int sales = 0;
        int customer = 0;
        for (Achievement achievement : list) {
            sales += Integer.parseInt(achievement.getSalesman());
            customer += Integer.parseInt(achievement.getAllcustomers());
        }
        team.setText(list.size() + "");
        allsales.setText(sales + "");
        allcustomer.setText(customer + "");
    }
}
