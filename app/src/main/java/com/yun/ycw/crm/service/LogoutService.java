package com.yun.ycw.crm.service;

import android.content.Context;
import com.yun.ycw.crm.API.RetrofitInterface;
import com.yun.ycw.crm.comparator.StringConverter;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.utils.SharedUtils;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wdyan on 2016/3/23.
 */
public class LogoutService {
    private static LogoutService retrofitService = new LogoutService();
    private RetrofitInterface retrofitInterface;
    private String methodName;
    private static String token;

    private LogoutService() {
        initRetrofit();
    }

    public static LogoutService getInstance(Context context) {
        if (retrofitService == null) {
            retrofitService = new LogoutService();
        }
        token = SharedUtils.getToken(context, "info");
        return retrofitService;
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

    public void userLogout(String userId) {
        Map<String, String> params = new HashMap<>();
        params.put("umeng_token", "");
        retrofitInterface.deleteDevice_token(userId, params, new Callback<String>() {
            @Override
            public void success(String s, Response response) {

            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }

}
