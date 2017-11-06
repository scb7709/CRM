package com.yun.ycw.crm.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.yun.ycw.crm.API.RetrofitInterface;
import com.yun.ycw.crm.comparator.StringConverter;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.utils.SharedUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 用于删除客户
 * Created by wdyan on 2016/2/18.
 */
public class DelCustomerService {
    private static DelCustomerService delCustomerService = new DelCustomerService();
    private RetrofitInterface retrofitInterface;
    private static String token;
    private DelCustomerService() {
        initRetrofit();
    }
    public static DelCustomerService getInstance(Context context) {
        if (delCustomerService == null) {
            delCustomerService = new DelCustomerService();
        }
        token = SharedUtils.getToken(context, "info");
        return delCustomerService;
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

    public void delCustomer(String customer_id,final Context context) {
        retrofitInterface.deleteCustomer(customer_id, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                System.out.println(retrofitError.getMessage());
            }
        });
    }

    public void giveUpCustomer(String customer_id,final Context context) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", "0");
        retrofitInterface.giveUpCustomer(customer_id, map, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Toast.makeText(context, "放弃成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
                System.out.println("VVVVVVVVVV" + retrofitError.getMessage());
            }
        });
    }
    public void activiteCustomer(Map<String, String> map,final Context context) {
        retrofitInterface.activiteCustomer(map, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
                System.out.println(retrofitError.getMessage());
            }
        });
    }

    public void agreeactiviteCustomer(String id,Map<String, String> map,final Context context) {
        retrofitInterface.agreeactiviteCustomer(id,map, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
                System.out.println("FFFFFFFFFFFFFFFF" + retrofitError.getMessage());

            }
        });
    }

}
