package com.yun.ycw.crm.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import com.umeng.analytics.MobclickAgent;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.adapter.SubUndoneAdapter;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.entity.Leader;
import com.yun.ycw.crm.entity.UndoneOrder;
import com.yun.ycw.crm.entity.User;
import com.yun.ycw.crm.utils.LoadingUtils;
import com.yun.ycw.crm.utils.SharedUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 下属的待回款页面
 * Created by wdyan on 2016/2/24.
 */
public class SubUndoneActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    @ViewInject(R.id.sub_undone_listview)
    private ListView listview;
    @ViewInject(R.id.sub_undone_back)
    private ImageButton iButton;
    @ViewInject(R.id.sub_undone_title)
    private TextView title;
    @ViewInject(R.id.sub_undone_num)//待回款单数
    private TextView sub_undone_num;
    @ViewInject(R.id.sub_undone_money)//待回款金额
    private TextView sub_undone_money;
    @ViewInject(R.id.sub_undone_leader)//销售主管
    private TextView sub_undone_leader;
    @ViewInject(R.id.sub_undone_leader_name)//销售的名字，是主管还是专员
    private TextView sub_undone_leader_name;
    private List<Leader> list = new ArrayList<>();

    private SubUndoneAdapter adapter;
    private String userId;
    private Context mContext;
    private String mPageName = "SubUndoneActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subundone);
        ViewUtils.inject(this);
        mContext = this;
        //level是登录人员的级别，级别从高到低依次是one,two,three
        String level = getIntent().getStringExtra("level");
        String topTitle = getIntent().getStringExtra("name");
        userId = getIntent().getStringExtra("id");
        if (topTitle.equals("") && level.equals("one")) {
            title.setText("销售主管");
        } else if (topTitle.equals("") && level.equals("two")) {
            title.setText("销售专员");
        } else {
            title.setText(topTitle);
        }

        switch (level) {
            case "one":
                initView("销售主管");
                break;
            case "two":
                initView("销售专员");
                break;
            default:
                break;
        }
    }


    private void initView(String leader) {
        final Dialog dialog = LoadingUtils.createLoadingDialog(SubUndoneActivity.this, "正在加载");
        dialog.show();
        list.clear();
        String token = SharedUtils.getToken(SubUndoneActivity.this, "info");
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.configCurrentHttpCacheExpiry(5000);
        RequestParams params = new RequestParams();
        params.addHeader("Authorization", "Bearer" + " " + token);

        sub_undone_leader_name.setText(leader);
        list.add(new Leader(leader, "待回款单数", "待回款金额"));
        httpUtils.send(HttpRequest.HttpMethod.GET, Constants.GENERAL_URL + userId + "/sub-list", params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                int subUndoneNum = 0;//销售主管——>待回款单数
                double subUndoneMoney = 0.00;//销售主管——>待回款金额
                int subUndoneLeader = 0;//销售主管的个数
                String leaderJsonString = responseInfo.result;//获取销售主管的jsonString
                try {
                    JSONArray leaderArray = new JSONArray(leaderJsonString);
                    for (int i = 0; i < leaderArray.length(); i++) {
                        Leader leader = new Leader();
                        JSONObject leaderObj = leaderArray.getJSONObject(i);
                        leader.setId(leaderObj.getString("id"));//设置销售主管的id
                        leader.setName(leaderObj.getString("user_login"));//设置销售主管的名字
                        int is_superior = leaderObj.getInt("is_superior");
                        int superior_id = leaderObj.getInt("superior_id");

                        if (is_superior == 1 && superior_id == 0) {
                            leader.setLevel("one");//设置销售主管的级别
                        }
                        if (is_superior == 1 && superior_id != 0) {
                            leader.setLevel("two");
                        }
                        if (is_superior == 0) {
                            leader.setLevel("three");
                        }

                        //这是获取销售主管的待回款订单
                        JSONObject undoneObject = leaderObj.getJSONObject("stats");
                        leader.setUndoneNum(undoneObject.getString("waiting_collection_order"));//设置销售主管的待回款单数
                        leader.setUndoneMoney(undoneObject.getString("waiting_collection_prices"));//设置销售主管的待回款金额
                        list.add(leader);
                    }
                    for (int i = 0; i < list.size(); i++) {

                        if (i == 0) {
                        } else {
                            subUndoneNum += Integer.parseInt(list.get(i).getUndoneNum());
                            subUndoneMoney += Double.parseDouble(list.get(i).getUndoneMoney());
                        }
                    }
                    subUndoneLeader = list.size();
                    sub_undone_num.setText(String.valueOf(subUndoneNum));
                    DecimalFormat df = new DecimalFormat("#.00");
                    String formatedMoney = df.format(subUndoneMoney);
                    sub_undone_money.setText(formatedMoney);

                    sub_undone_leader.setText(String.valueOf(subUndoneLeader - 1));
                    adapter = new SubUndoneAdapter(SubUndoneActivity.this, list);
                    listview.setAdapter(adapter);
                    setListener();
                    dialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i("error", s);
            }
        });
    }

    private void setListener() {
        listview.setOnItemClickListener(this);
        iButton.setOnClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (position != 0) {
            if (!list.get(position).getUndoneNum().equals("0")) {
                if (list.get(position).getLevel().equals("three")) {
                    Intent intent = new Intent(SubUndoneActivity.this, UndoneActivity.class);
                    intent.putExtra("personId", list.get(position).getId());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, SubUndoneActivity.class);
                    intent.putExtra("id", list.get(position).getId());
                    intent.putExtra("name", list.get(position).getName() + "的下属");
                    intent.putExtra("level", list.get(position).getLevel());
                    startActivity(intent);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sub_undone_back:
                finish();
                break;
            default:
                break;
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
