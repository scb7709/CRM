package com.yun.ycw.crm.utils;

import android.content.Context;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.yun.ycw.crm.adapter.ActhievementTeamAdapter;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.entity.Achievement;
import com.yun.ycw.crm.entity.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by scb on 2016/3/29.
 */
public class GetStaffHierarchyUtils {
    public static void saveStaffHierarchy(final Context context) {
        if (InternetUtils.internet(context)) {
            HttpUtils httpUtils = new HttpUtils();
            RequestParams params = new RequestParams();
            httpUtils.send(HttpRequest.HttpMethod.GET, Constants.STAFF_HIERARCHY, params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    String s = responseInfo.result;
                    Log.i("QQQQQ", s);
                    SharedUtils.putStaffHierarchy("StaffHierarchy", s, context);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                }
            });
        }
    }

    public static List<User> getSubordinate(String id, final Context context) {
        List<User> list = new ArrayList<User>();

        String staffHierarchy = SharedUtils.getStaffHierarchy("StaffHierarchy", context);
        if (staffHierarchy.length() != 0) {
            try {
                JSONObject jsonObject = new JSONObject(staffHierarchy);
                if (id.equals(jsonObject.getString("id"))) {
                    JSONArray jsonArray1 = new JSONArray(jsonObject.getString("subordinates"));
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        User user = new User();
                        user.setId(jsonArray1.getJSONObject(i).getString("id"));
                        user.setName(jsonArray1.getJSONObject(i).getString("user_login"));
                        if(getSubordinate(user.getId(),context).size()>0){
                            list.add(user);
                        }
                    }

                    return list;
                } else {
                    JSONArray jsonArray1 = new JSONArray(jsonObject.getString("subordinates"));
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        if (id.equals(jsonArray1.getJSONObject(i).getString("id"))) {
                            JSONArray jsonArray2 = new JSONArray(jsonArray1.getJSONObject(i).getString("subordinates"));
                            for (int j = 0; j < jsonArray2.length(); j++) {
                                User user = new User();
                                user.setId(jsonArray2.getJSONObject(j).getString("id"));
                                user.setName(jsonArray2.getJSONObject(j).getString("user_login"));
                                list.add(user);
                            }
                            return list;
                        }

                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        return list;
    }

    public static String getId(String name, final Context context) {
        String staffHierarchy = SharedUtils.getStaffHierarchy("StaffHierarchy", context);
        Log.i("QQQQQ", staffHierarchy);
        if (staffHierarchy.length() != 0) {
            try {
                JSONObject jsonObject = new JSONObject(staffHierarchy);
                if (name.equals(jsonObject.getString("user_login"))) {
                    return jsonObject.getString("id");
                } else {
                    JSONArray jsonArray1 = new JSONArray(jsonObject.getString("subordinates"));
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        if (name.equals(jsonArray1.getJSONObject(i).getString("user_login"))) {
                            return jsonArray1.getJSONObject(i).getString("id");
                        } else {
                            JSONArray jsonArray2 = new JSONArray(jsonArray1.getJSONObject(i).getString("subordinates"));
                            for (int j = 0; j < jsonArray2.length(); j++) {
                                if (name.equals(jsonArray2.getJSONObject(j).getString("user_login"))) {
                                    return jsonArray2.getJSONObject(j).getString("id");
                                }

                            }
                        }
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        return "";
    }

}
