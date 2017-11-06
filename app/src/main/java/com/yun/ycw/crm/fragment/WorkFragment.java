package com.yun.ycw.crm.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.activity.*;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.entity.Achievement;
import com.yun.ycw.crm.entity.User;
import com.yun.ycw.crm.utils.InternetUtils;
import com.yun.ycw.crm.utils.SharedUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by scb on 2016/3/24.
 */
public class WorkFragment extends Fragment {
    @ViewInject(R.id.fragment_work_company_message)
    private LinearLayout fragment_work_company_message;//
    @ViewInject(R.id.fragment_work_staff)
    private LinearLayout fragment_work_staff;//
    @ViewInject(R.id.fragment_work_public_ocean)
    private LinearLayout fragment_work_public_ocean;//
    @ViewInject(R.id.fragment_work_sign_in)
    private LinearLayout fragment_work_sign_in;//
    @ViewInject(R.id.fragment_work_week_plan)
    private LinearLayout fragment_work_week_plan;//
    @ViewInject(R.id.fragment_work_activity)
    private LinearLayout fragment_work_activity;//
    @ViewInject(R.id.fragment_work_activity_number)
    private TextView fragment_work_activity_number;
    @ViewInject(R.id.fragment_work_customer_img)
    private ImageView fragment_work_customer_img;

    private List<String[]> list;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_work, null);
        ViewUtils.inject(this, view);
        list = new ArrayList<String[]>();
        user = SharedUtils.getUser("user", getActivity());
        if (user.getSuperior_id().equals("28")) {
            fragment_work_customer_img.setImageResource(R.mipmap.subordinate0);
            downdata();
        } else if (user.getSuperior_id().equals("0")) {
            fragment_work_customer_img.setImageResource(R.mipmap.subordinate0);
        }
        return view;
    }

    @OnClick({R.id.fragment_work_week_plan, R.id.fragment_work_sign_in, R.id.fragment_work_public_ocean, R.id.fragment_work_staff, R.id.fragment_work_company_message, R.id.fragment_work_activity})
    public void onClick(View view) {
        User user = SharedUtils.getUser("user", getActivity());
        Achievement achievement = new Achievement(user.getName(), user.getId(),"2015-01-01","2100-01-01");
        switch (view.getId()) {
            case R.id.fragment_work_week_plan:
                switch (user.getLevel()) {
                    case "one":
                        Intent one = new Intent(getActivity(), StaffScheduleListActivity.class);
                        one.putExtra("id", user.getId());
                        one.putExtra("level", "one");
                        startActivity(one);
                        break;
                    case "two":
                        Intent two = new Intent(getActivity(), StaffScheduleListActivity.class);
                        two.putExtra("id", user.getId());
                        two.putExtra("level", "two");
                        two.putExtra("name", "");
                        startActivity(two);
                        break;
                    case "three":
                        Intent three = new Intent(getActivity(), ScheduleListActivity.class);
                        three.putExtra("id", user.getId());
                        three.putExtra("level", "three");
                        three.putExtra("name", user.getName());
                        startActivity(three);
                        break;
                }
                break;
            case R.id.fragment_work_sign_in://点击小区签到
                Intent checkIn = new Intent(getActivity(), CheckInListActivity.class);
                checkIn.putExtra("level", user.getLevel());//级别
                checkIn.putExtra("id", user.getId());//id
                checkIn.putExtra("name", user.getName());//name
                startActivity(checkIn);
                break;
            case R.id.fragment_work_public_ocean:
                Intent i = new Intent(getActivity(), CustomerActivity.class);
                i.putExtra("achievement", achievement);
                i.putExtra("isPublicOcean", "1");
                startActivity(i);
                break;
            case R.id.fragment_work_staff:
                Intent intent = new Intent();
                intent.putExtra("achievement", achievement);
                switch (SharedUtils.getUser("user", getActivity()).getSuperior_id()) {
                    case "0":
                        intent.setClass(getActivity(), StaffTeamActivity.class);

                        startActivity(intent);
                        break;
                    case "28":
                        intent.setClass(getActivity(), StaffStaffAcitivity.class);
                        startActivity(intent);
                        break;
                    default:
                        intent.setClass(getActivity(), CustomerActivity.class);
                        intent.putExtra("isPublicOcean", "0");
                        startActivity(intent);
                        break;
                }
                break;
            case R.id.fragment_work_company_message:
                startActivity(new Intent(getActivity(), CompanyMessageActivity.class));
                break;
            case R.id.fragment_work_activity:
                Intent intentt = new Intent(getActivity(), ActiviteApplyActivity.class);
                intentt.putExtra("apply", (Serializable) list);
                startActivityForResult(intentt, Constants.ACTIVITE_APPLY);
                break;
        }
    }

    private void downdata() {

        if (InternetUtils.internet(getActivity())) {
            if (list.size() != 0) {
                list.clear();
            }
            HttpUtils httpUtils = new HttpUtils();
            httpUtils.configCurrentHttpCacheExpiry(3000);
            RequestParams params = new RequestParams();
            params.addHeader("Authorization", "Bearer" + " " + SharedUtils.getToken(getActivity(), "info"));
            httpUtils.send(HttpRequest.HttpMethod.GET, Constants.STAFFAPPLY_URL, params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    JSONArray array = null;
                    try {
                        array = new JSONArray(responseInfo.result);
                        for (int i = 0; i < array.length(); i++) {
                            String[] apply = new String[15];
                            String string = array.getJSONObject(i).getString("created_at");

                            String year = string.substring(0, 10);
                            String time = string.substring(11, 16);
                            apply[0] = year + " " + time;
                            apply[1] = array.getJSONObject(i).getJSONObject("staff").getString("user_login");
                            apply[2] = array.getJSONObject(i).getJSONObject("headman").getString("headman_name");
                            apply[3] = array.getJSONObject(i).getString("user_id");
                            apply[12] = array.getJSONObject(i).getString("headman_id");
                            apply[4] = array.getJSONObject(i).getJSONObject("headman").getString("sources");
                            apply[5] = array.getJSONObject(i).getJSONObject("headman").getString("phone");
                            apply[6] = array.getJSONObject(i).getJSONObject("headman").getString("origin");
                            apply[7] = array.getJSONObject(i).getJSONObject("headman").getString("corporation");
                            apply[8] = array.getJSONObject(i).getJSONObject("headman").getString("recent_visit_record");
                            apply[9] = array.getJSONObject(i).getJSONObject("headman").getString("last_visit_time");
                            apply[10] = array.getJSONObject(i).getJSONObject("headman").getString("sex");
                            apply[11] = array.getJSONObject(i).getJSONObject("headman").getString("level");
                            apply[13] = array.getJSONObject(i).getString("id");
                            apply[14] = array.getJSONObject(i).getJSONObject("headman").getString("remark");

                            list.add(apply);
                        }
                        if (list.size() > 0) {
                            fragment_work_activity.setVisibility(View.VISIBLE);
                            fragment_work_activity_number.setText("激活申请（" + list.size() + "）");

                        } else {
                            fragment_work_activity_number.setText("激活申请");
                            fragment_work_activity.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        fragment_work_activity.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    fragment_work_activity.setVisibility(View.GONE);
                }
            });
        }
    }

}
