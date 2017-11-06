package com.yun.ycw.crm.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.entity.Achievement;
import com.yun.ycw.crm.entity.Schedule;
import com.yun.ycw.crm.entity.User;
import com.yun.ycw.crm.utils.InternetUtils;
import com.yun.ycw.crm.utils.LoadingUtils;
import com.yun.ycw.crm.utils.SharedUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 周计划列表
 * Created by wdyan on 2016/3/25.
 */
public class ScheduleListActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    @ViewInject(R.id.schedule_listview)
    private ListView schedule_listView;
    @ViewInject(R.id.schedule_back)
    private ImageButton schedule_back;//返回button
    @ViewInject(R.id.schedule_title)
    private TextView schedule_title;//标题
    @ViewInject(R.id.add_schedule_img)
    private ImageView add_schedule_img;//添加新的计划
    private RequestQueue requestQueue;
    private User user;
    private Dialog dialog;
    private List<Schedule> scheduleList = new ArrayList<>();
    private List<Schedule> orderedScheduleList = new ArrayList<>();//倒序排列，最新创建的计划在最上面
    private String token;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        ViewUtils.inject(this);
        initView();
    }

    private void initView() {
        user = SharedUtils.getUser("user", this);
        id = getIntent().getStringExtra("id");
        String name = getIntent().getStringExtra("name");
        if (user.getLevel().equals("three")) {
            add_schedule_img.setVisibility(View.VISIBLE);
            add_schedule_img.setOnClickListener(this);
        }
        schedule_listView.setOnItemClickListener(this);
        schedule_back.setOnClickListener(this);

        if (user.getLevel().equals("three")) {
            schedule_title.setText("我的计划");
        } else
            schedule_title.setText(name + "的计划");
        token = SharedUtils.getToken(this, "info");


    }


    private void init(final String id, final String token) {

        Log.i("id_token", id + "   " + token);
        dialog = LoadingUtils.createLoadingDialog(this, "正在加载");
        dialog.show();
        if (InternetUtils.internet(this)) {
            requestQueue = Volley.newRequestQueue(this);
            StringRequest request = new StringRequest(Request.Method.GET, Constants.SCHEDULE_URL + id, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    try {
                        scheduleList.clear();
                        orderedScheduleList.clear();
                        Log.i("aaa", s);
                        JSONArray scheduleArray = new JSONArray(s);

                        for (int i = 0; i < scheduleArray.length(); i++) {
                            JSONObject scheduleObj = scheduleArray.getJSONObject(i);
                            Schedule schedule = new Schedule();
                            schedule.setId(scheduleObj.getString("id"));
                            schedule.setUser_id(scheduleObj.getString("user_id"));
                            schedule.setSuperior_id(scheduleObj.getString("superior_id"));
                            schedule.setAchievement(scheduleObj.getString("achievement"));
                            schedule.setJoin_headman(scheduleObj.getString("join_headman"));
                            schedule.setVisit_headman(scheduleObj.getString("visit_headman"));
                            schedule.setStartTime(scheduleObj.getString("start_time").substring(0, 10));
                            schedule.setEndTime(scheduleObj.getString("end_time").substring(0, 10));
                            schedule.setRemarks(scheduleObj.getString("remarks"));
                            scheduleList.add(schedule);
                        }
                        //计划列表倒置
                        for (int i = scheduleList.size() - 1; i > -1; i--) {
                            orderedScheduleList.add(scheduleList.get(i));
                        }

                        ScheduleListAdapter adapter = new ScheduleListAdapter(ScheduleListActivity.this);
                        schedule_listView.setAdapter(adapter);
                        if (orderedScheduleList.size() == 0) {
                            schedule_listView.setVisibility(View.INVISIBLE);
                        } else
                            schedule_listView.setVisibility(View.VISIBLE);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ScheduleDetailActivity.class);
        intent.putExtra("Schedule", orderedScheduleList.get(position));
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.schedule_back:
                finish();
                break;
            case R.id.add_schedule_img:
                startActivity(new Intent(ScheduleListActivity.this, AddScheduleActivity.class));
                break;
        }
    }

    class ScheduleListAdapter extends BaseAdapter {

        private Context context;

        public ScheduleListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return scheduleList.size();
        }

        @Override
        public Object getItem(int position) {
            return scheduleList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ScheduleViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_schedule, null);
                viewHolder = new ScheduleViewHolder();
                viewHolder.schedule_date = (TextView) convertView.findViewById(R.id.schedule_date);
                convertView.setTag(viewHolder);
            } else
                viewHolder = (ScheduleViewHolder) convertView.getTag();
            viewHolder.schedule_date.setText(orderedScheduleList.get(position).getStartTime() + "——" + orderedScheduleList.get(position).getEndTime());
            return convertView;
        }
    }

    class ScheduleViewHolder {
        private TextView schedule_date;
    }


    @Override
    protected void onResume() {
        super.onResume();
        init(id, token);
    }
}
