package com.yun.ycw.crm.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.yun.ycw.crm.API.RetrofitInterface;
import com.yun.ycw.crm.comparator.StringConverter;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.utils.SharedUtils;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于添加周计划
 * Created by wdyan on 2016/3/24.
 */
public class AddScheduleService {
    private static AddScheduleService addScheduleService = new AddScheduleService();
    private RetrofitInterface retrofitInterface;
    private static String token;
    private Date d;
    private static Context ctext;

    private AddScheduleService() {
        initRetrofit();
    }

    public static AddScheduleService getInstance(Context context) {
        if (addScheduleService == null) {
            addScheduleService = new AddScheduleService();
        }
        token = SharedUtils.getToken(context, "info");
        return addScheduleService;
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

    public void addSchedule(Context context, String target_money, String target_chief, String target_visitChief, String schedule_start, String schedule_end, String target_note) {
        ctext = context;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("achievement", target_money);//目标业绩
        params.put("join_headman", target_chief);//目标开发工长数
        params.put("visit_headman", target_visitChief);//目标回访工长数

        params.put("start_time", schedule_start);//开始日期
        params.put("end_time", schedule_end);//结束日期
        params.put("remarks", target_note);//备注
        Log.i("token", token);
        retrofitInterface.addSchedule(params, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Toast.makeText(ctext, "计划创建成功", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.i("Error:计划已存在",retrofitError.getMessage()+"");
                Toast.makeText(ctext, "ERROR", Toast.LENGTH_LONG).show();
            }
        });
    }
}
