package com.yun.ycw.crm.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.yun.ycw.crm.API.RetrofitInterface;
import com.yun.ycw.crm.comparator.StringConverter;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.utils.SharedUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于更新后台的客户信息
 * Created by wdyan on 2016/2/18.
 */
public class UpdateCustomerService {
    private static UpdateCustomerService updateCustomerService = new UpdateCustomerService();
    private RetrofitInterface retrofitInterface;
    private static String token;
    private Context ctext;

    private UpdateCustomerService() {
        initRetrofit();
    }

    public static UpdateCustomerService getInstance(Context context) {
        if (updateCustomerService == null) {
            updateCustomerService = new UpdateCustomerService();
        }
        token = SharedUtils.getToken(context, "info");
        return updateCustomerService;
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

    public void updateCustomer(Context context, String headman_id, final Map<String, String> para) {
        ctext = context;
        Map<String, String> params = new HashMap<String, String>();
        params = para;
        retrofitInterface.updateCustomer(headman_id, params, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                    Toast.makeText(ctext, "更新成功", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                String error = (String) retrofitError.getBody();
                Log.i("ERROR", error);
                String phontMsg = "";
                String fMsg = "";
                try {
                    JSONObject Obj = new JSONObject(error);
                    JSONObject obj = Obj.getJSONObject("errors");
                    JSONArray array = obj.getJSONArray("phone");
                    JSONArray array1 = obj.getJSONArray("follow_up_advice");

                    for (int i = 0; i < array.length(); i++) {
                        phontMsg = array.getString(i) + " " + phontMsg;
                    }
                    for (int i = 0; i < array1.length(); i++) {
                        fMsg = array1.getString(i) + " " + fMsg;
                    }
                    Toast.makeText(ctext, "错误:" + phontMsg + '\n' + fMsg, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    public void transferCustomer(Context context, String headman_id, final Map<String, String> para) {
        ctext = context;
        retrofitInterface.transferCustomer(headman_id, para, new Callback<String>() {
            @Override
            public void success(String s, Response response) {

                    Toast.makeText(ctext, "转移成功", Toast.LENGTH_LONG).show();

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                String error = (String) retrofitError.getBody();
                Log.i("ERROR", error);
                String phontMsg = "";
                String fMsg = "";
                try {
                    JSONObject Obj = new JSONObject(error);
                    JSONObject obj = Obj.getJSONObject("errors");
                    JSONArray array = obj.getJSONArray("phone");
                    JSONArray array1 = obj.getJSONArray("follow_up_advice");

                    for (int i = 0; i < array.length(); i++) {
                        phontMsg = array.getString(i) + " " + phontMsg;
                    }
                    for (int i = 0; i < array1.length(); i++) {
                        fMsg = array1.getString(i) + " " + fMsg;
                    }
                    Toast.makeText(ctext, "错误:" + phontMsg + '\n' + fMsg, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

}
