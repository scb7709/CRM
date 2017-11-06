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
import com.yun.ycw.crm.entity.Leader;
import com.yun.ycw.crm.utils.InternetUtils;
import com.yun.ycw.crm.utils.LoadingUtils;
import com.yun.ycw.crm.utils.SharedUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 可供主管查看的下属列表(用于计划)
 * Created by Administrator on 2016/3/25.
 */
public class StaffScheduleListActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    @ViewInject(R.id.staff_listView_forSchedule)
    private ListView staff_listView_forSchedule;//下属listview
    @ViewInject(R.id.staff_schedule_back)
    private ImageButton staff_schedule_back;//返回button
    @ViewInject(R.id.staff_schedule_title)
    private TextView staff_schedule_title;//标题
    private List<Leader> staffScheduleList = new ArrayList<>();
    private RequestQueue requestQueue;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_schedule_list);
        ViewUtils.inject(this);
        initView();
        init();
    }

    private void initView() {
        staff_listView_forSchedule.setOnItemClickListener(this);
        staff_schedule_back.setOnClickListener(this);
        dialog = LoadingUtils.createLoadingDialog(this, "正在加载");
        dialog.show();
    }

    private void init() {
//        user = SharedUtils.getUser("user", this);
        String id = getIntent().getStringExtra("id");
        String level = getIntent().getStringExtra("level");
        String name = getIntent().getStringExtra("name");
        if (level.equals("one")) {
            staff_schedule_title.setText("销售主管");
        } else if (level.equals("two")) {
            if (name.equals("")) {
                staff_schedule_title.setText("我的销售专员");
            } else
                staff_schedule_title.setText(name + "的销售专员");
        }

        final String token = SharedUtils.getToken(this, "info");
        Log.i("url__", Constants.STAFF_URL + id + "/sub-list");
        if (InternetUtils.internet(this)) {
            requestQueue = Volley.newRequestQueue(this);
            StringRequest request = new StringRequest(Request.Method.GET, Constants.STAFF_URL + id + "/sub-list", new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    try {
                        JSONArray scheduleArray = new JSONArray(s);
                        for (int i = 0; i < scheduleArray.length(); i++) {
                            JSONObject scheduleObj = scheduleArray.getJSONObject(i);
                            Leader leader = new Leader();
                            leader.setId(scheduleObj.getString("id"));
                            leader.setName(scheduleObj.getString("user_login"));

                            int is_superior = scheduleObj.getInt("is_superior");
                            int superior_id = scheduleObj.getInt("superior_id");

                            if (is_superior == 1 && superior_id == 0) {
                                leader.setLevel("one");//设置销售主管的级别
                            }
                            if (is_superior == 1 && superior_id != 0) {
                                leader.setLevel("two");
                            }
                            if (is_superior == 0) {
                                leader.setLevel("three");
                            }
                            staffScheduleList.add(leader);
                        }
                        StaffScheduleListAdapter adapter = new StaffScheduleListAdapter(StaffScheduleListActivity.this);
                        staff_listView_forSchedule.setAdapter(adapter);
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
        String level = staffScheduleList.get(position).getLevel();
        //点击的是销售专员
        if (level.equals("three")) {
            Intent intent = new Intent(this, ScheduleListActivity.class);
            intent.putExtra("id", staffScheduleList.get(position).getId());//把专员的id传过去
            intent.putExtra("name", staffScheduleList.get(position).getName());//把专员的name传过去
            startActivity(intent);
        } else if (level.equals("two")) {//点击的是销售主管
            Intent intent = new Intent(this, StaffScheduleListActivity.class);
            intent.putExtra("id", staffScheduleList.get(position).getId());//把主管的id传过去
            intent.putExtra("level", staffScheduleList.get(position).getLevel());//把主管的level传过去
            intent.putExtra("name", staffScheduleList.get(position).getName());//把主管的name传过去
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    class StaffScheduleListAdapter extends BaseAdapter {

        private Context context;

        public StaffScheduleListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return staffScheduleList.size();
        }

        @Override
        public Object getItem(int position) {
            return staffScheduleList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            StaffScheduleViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_schedule, null);
                viewHolder = new StaffScheduleViewHolder();
                viewHolder.staff_schedule_name = (TextView) convertView.findViewById(R.id.schedule_date);
                convertView.setTag(viewHolder);
            } else
                viewHolder = (StaffScheduleViewHolder) convertView.getTag();
            viewHolder.staff_schedule_name.setText(staffScheduleList.get(position).getName());
            return convertView;
        }
    }

    class StaffScheduleViewHolder {
        private TextView staff_schedule_name;
    }

}
