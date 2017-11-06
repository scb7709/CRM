package com.yun.ycw.crm.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.yun.ycw.crm.API.RetrofitInterface;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.adapter.ActhievementTeamAdapter;
import com.yun.ycw.crm.adapter.StaffStaffAdapter;
import com.yun.ycw.crm.adapter.StaffTeamAdapter;
import com.yun.ycw.crm.comparator.StringConverter;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.entity.Achievement;
import com.yun.ycw.crm.entity.User;
import com.yun.ycw.crm.utils.InternetUtils;
import com.yun.ycw.crm.utils.LoadingUtils;
import com.yun.ycw.crm.utils.SharedUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by scb on 2016/2/24.
 */
public class StaffStaffAcitivity extends Activity {
    @ViewInject(R.id.staffsalesman_man)
    private TextView man;//当前是谁
    @ViewInject(R.id.staffsalesman_salesman)
    private TextView salesman_num;//销售员数
    @ViewInject(R.id.staffsalesman_customer)
    private TextView customer_num;//客户数
    @ViewInject(R.id.staffsalesman_lv)
    private ListView listView;//
    @ViewInject(R.id.staffsalesman_re)
    private RelativeLayout relativeLayout;//
    @ViewInject(R.id.staffsalesman_back)
    private ImageButton back;//

    private StaffStaffAdapter ataffStaffAdapter;
    private List<Achievement> list;
    private Achievement achievement;
    private Context mContext;
    private String mPageName = "StaffStaffAcitivity";
    private Dialog dialog;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staffsalesman);
        ViewUtils.inject(this);
        mContext = this;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        list = new ArrayList<Achievement>();
        setdate();
        downdate();

    }

    @OnItemClick({R.id.staffsalesman_lv})
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Achievement achievement = ataffStaffAdapter.getItem((int) id);
        String str="&start_time="+achievement.getStarttime()+"&end_time="+achievement.getEndtime();
        Log.i("VVVVVVVVVVVV", str);
        Intent intent = new Intent(StaffStaffAcitivity.this, CustomerActivity.class);
        intent.putExtra("achievement", achievement);
        if(SharedUtils.getUser("user",StaffStaffAcitivity.this).getSuperior_id().equals("28")){

            intent.putExtra("isPublicOcean", "2");
        }else {
            intent.putExtra("isPublicOcean", "3");
        }
        startActivity(intent);
    }

    public void setdate() {
        achievement = (Achievement) getIntent().getSerializableExtra("achievement");
        man.setText(achievement.getUsername());
    }
    public void downdate() {
        if (InternetUtils.internet(StaffStaffAcitivity.this)) {
            dialog = LoadingUtils.createLoadingDialog(StaffStaffAcitivity.this, "正在加载");
            dialog.show();
            HttpUtils httpUtils = new HttpUtils();
            RequestParams params = new RequestParams();
            params.addQueryStringParameter("start_time", achievement.getStarttime());
            params.addQueryStringParameter("end_time", achievement.getEndtime());
            params.addHeader("Authorization", "Bearer" + " " + SharedUtils.getToken(StaffStaffAcitivity.this, "info"));
            httpUtils.send(HttpRequest.HttpMethod.GET, Constants.STAFF_URL + achievement.getUserid() + "/sub-list", params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    String s = responseInfo.result;
                    JSONArray array = null;

                    try {
                        array = new JSONArray(s);

                        for (int i = 0; i < array.length(); i++) {
                            Achievement ach = new Achievement();
                            ach.setUsername(array.getJSONObject(i).getString("user_login"));
                            ach.setUserid(array.getJSONObject(i).getString("id"));
                            ach.setAllcustomers(array.getJSONObject(i).getJSONObject("stats").getInt("customer_num") + array.getJSONObject(i).getJSONObject("stats").getInt("company_num") + "");
                            ach.setSalesman(array.getJSONObject(i).getJSONObject("stats").getString("subordinate_num"));
                            ach.setSalesmanage(array.getJSONObject(i).getJSONObject("stats").getString("superior_num"));
                            ach.setStarttime(achievement.getStarttime());
                            ach.setEndtime(achievement.getEndtime());
                            list.add(ach);

                        }

                        getSum(list);
                        ataffStaffAdapter = new StaffStaffAdapter(list);
                        listView.setAdapter(ataffStaffAdapter);
                        dialog.dismiss();
                    } catch (JSONException e) {
                        dialog.dismiss();
                        e.printStackTrace();

                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    dialog.dismiss();
                }
            });
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
        int customer = 0;
        for (Achievement achievement : list) {
            customer += Integer.parseInt(achievement.getAllcustomers());
        }
        customer_num.setText(customer + "");
        salesman_num.setText(list.size()+"");
    }
}
