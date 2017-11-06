package com.yun.ycw.crm.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.yun.ycw.crm.API.RetrofitInterface;
import com.yun.ycw.crm.comparator.StringConverter;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.utils.SharedUtils;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户添加客户
 * Created by wdyan on 2016/2/17.
 */
public class AddCustomerService {
    private static AddCustomerService addCustomerService = new AddCustomerService();
    private RetrofitInterface retrofitInterface;
    private static String token;
    private Date d;
    private static Context ctext;

    private AddCustomerService() {
        initRetrofit();
    }

    public static AddCustomerService getInstance(Context context) {
        if (addCustomerService == null) {
            addCustomerService = new AddCustomerService();
        }
        token = SharedUtils.getToken(context, "info");
        return addCustomerService;
    }

    private void initRetrofit() {
        RestAdapter retrofit = new RestAdapter.Builder()
                .setEndpoint(Constants.BASE_URL)
                .setConverter(new StringConverter())//设置数据转换器，默认是使用GsonConvert，但是因为测试接口是在新浪云上面布置，并且没有通过实名认证，返回数据不规范，所以一直使用自定义的convert
                .setLogLevel(RestAdapter.LogLevel.FULL)//设置log参数，设置了之后可以看到一些log参数，比如会在logcat中打印出来heads
                .setRequestInterceptor(requestInterceptor)//Headers that need to be added to every request，如果对每个请求都需要添加特定的heads，可以这样来配置
//                .setErrorHandler(new MyErrorHandler())//f you need custom error handling for requests在异常抛出框架层之前（比如到达你的回调函数之前），给你一个自定义的机会
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
    }


    RequestInterceptor requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {

            request.addHeader("Authorization", "Bearer" + " " + token);
        }
    };

    public void addCustomer(Context context, String sex, String sources, String headman_name, String phone, String origin, String corporation, String firstrecord, String firsttime, String remark) {
        ctext = context;
      setAddCustomer(sex, sources, headman_name, phone, origin, corporation, firstrecord, firsttime, remark);
    }

    private void setAddCustomer(String sex, String sources, String headman_name, String phone, String origin, String corporation, String firstrecord, String firsttime, String remark) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sources", sources);
        params.put("headman_name", headman_name);
        params.put("phone", phone);
        params.put("sex", sex);
        params.put("origin", origin);
        params.put("corporation", corporation);
        params.put("remark", remark);
        params.put("recent_visit_record", firstrecord);
        params.put("last_visit_time", firsttime);
        retrofitInterface.addCustomer(params, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Log.i("XXXXXXXXX", " SSS");
                Toast.makeText(ctext, "增加成功", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.i("XXXXXXXXX", "" + retrofitError.getBody());
                String error = (String) retrofitError.getBody();
                Log.i("ERROR----", error);
                String msg = "";

                try {
                    JSONObject Obj = new JSONObject(error);
                    String errorInfo = new JSONObject(Obj.getString("errors")).getString("phone").substring(2, 11);
                    Toast.makeText(ctext, errorInfo, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void addCustomer(Context context, String userid, String sex, String sources, String headman_name, String phone, String origin, String corporation, String firstrecord, String firsttime, String remark) {
        ctext = context;
        setDataForAddCustomer(userid, sex, sources, headman_name, phone, origin, corporation, firstrecord, firsttime, remark);

    }

    private void setDataForAddCustomer(String userid, String sex, String sources, String headman_name, String phone, String origin, String corporation, String firstrecord, String firsttime, String remark) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sources", sources);
        params.put("headman_name", headman_name);
        params.put("phone", phone);
        params.put("origin", origin);
        params.put("corporation", corporation);
        params.put("remark", remark);
        params.put("sex", sex);
        params.put("recent_visit_record", firstrecord);
        params.put("user_id", userid);
        params.put("last_visit_time", firsttime);
        retrofitInterface.addCustomer(params, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Toast.makeText(ctext, "增加成功", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                String error = (String) retrofitError.getBody();
                String msg = "";
                try {
                    JSONObject Obj = new JSONObject(error);
                    msg = Obj.getString("message");
                    Toast.makeText(ctext, "错误:" + msg, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}

