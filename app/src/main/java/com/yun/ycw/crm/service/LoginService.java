package com.yun.ycw.crm.service;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.yun.ycw.crm.API.RetrofitInterface;
import com.yun.ycw.crm.activity.HomeActivity;
import com.yun.ycw.crm.activity.LoginActivity;
import com.yun.ycw.crm.comparator.StringConverter;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.entity.User;
import com.yun.ycw.crm.utils.SharedUtils;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于用户的登录
 * Created by wdyan on 2016/2/18
 */
public class LoginService {
    private static LoginService retrofitService = new LoginService();
    private RetrofitInterface retrofitInterface;
    private String methodName;

    private LoginService() {
        initRetrofit();
    }

    public static LoginService getInstance() {
        if (retrofitService == null) {
            retrofitService = new LoginService();
        }
        return retrofitService;
    }

    private void initRetrofit() {
        RestAdapter retrofit = new RestAdapter.Builder()
                .setEndpoint(Constants.BASE_URL)
                .setConverter(new StringConverter())//设置数据转换器，默认是使用GsonConvert，但是因为测试接口是在新浪云上面布置，并且没有通过实名认证，返回数据不规范，所以一直使用自定义的convert
                .setLogLevel(RestAdapter.LogLevel.FULL)//设置log参数，设置了之后可以看到一些log参数，比如会在logcat中打印出来heads
//                .setRequestInterceptor(requestInterceptor)//Headers that need to be added to every request，如果对每个请求都需要添加特定的heads，可以这样来配置
//                .setErrorHandler(new MyErrorHandler())//f you need custom error handling for requests在异常抛出框架层之前（比如到达你的回调函数之前），给你一个自定义的机会
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
    }

    /**
     * 访问接口http://1.mirror3.sinaapp.com/gavin/postParams.php
     * POST方式，会返回你传递的post参数
     */
    public void userLogin(String username, final String password, String device_token, final Activity context, final Dialog dialog, final boolean isMain) {

        User user = new User();
        user.setName("迪丽热巴");
        user.setLoginPwd(password);
        user.setEmail("770967819@qq.COM");
        user.setIcon("https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=%E8%BF%AA%E4%B8%BD%E7%83%AD%E5%B7%B4&step_word=&hs=0&pn=52&spn=0&di=46296448050&pi=0&rn=1&tn=baiduimagedetail&is=4003608120%2C132105293&istype=0&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=undefined&cs=912604644%2C2959897590&os=2616442789%2C3480155319&simid=0%2C0&adpicid=0&lpn=0&ln=1970&fr=&fmq=1507723455237_R&fm=&ic=undefined&s=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&ist=&jit=&cg=&bdtype=17&oriquery=&objurl=http%3A%2F%2Fb.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2Fae51f3deb48f8c54a59fa83230292df5e0fe7f5a.jpg&fromurl=ippr_z2C%24qAzdH3FAzdH3Fooo_z%26e3Bcml9_z%26e3Bv54AzdH3FgjofAzdH3Fd8dcc&gsm=&rpstart=0&rpnum=0");
        user.setSuperior_id("1122");
        user.setId("1122");
        user.setPhoneNumber("18611347385");
        user.setIs_superior("1120");

        int is_superior = 1;
        int superior_id = 1;
        if (is_superior == 1 && superior_id == 0) {
            user.setLevel("one");
        }
        if (is_superior == 1 && superior_id != 0) {
            user.setLevel("two");
        }
        if (is_superior == 0) {
            user.setLevel("three");
        }
        SharedUtils.putUser("user", user, context);
        SharedPreferences preferences = context.getSharedPreferences("info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", "");
        editor.apply();

        context.startActivity(new Intent(context, HomeActivity.class));
        context.finish();




      /*  Map<String, String> params = new HashMap<String, String>();
        params.put("user_login", username);
        params.put("password", password);
        params.put("device_token", device_token);
        retrofitInterface.callPostParamsMap(params, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
//                Toast.makeText(context, "登录成功", Toast.LENGTH_LONG).show();

                try {

                    JSONObject Obj = new JSONObject(s);
                    String token = Obj.getString("token");//获取token的值
                    Log.i("TTTTTTT", token);
                    Log.i("AAAA", s);
                    String userjson = Obj.getString("user");//获取用户信息
                    JSONObject json = new JSONObject(userjson);

                    User user = new User();
                    user.setName(json.getString("user_login"));
                    user.setLoginPwd(password);
                    user.setEmail(json.getString("user_email"));
                    user.setIcon(json.getString("avatar"));
                    user.setSuperior_id(json.getString("superior_id"));
                    user.setId(String.valueOf(json.getInt("id")));
                    user.setPhoneNumber(json.getString("user_phone"));
                    user.setIs_superior(json.getString("is_superior"));

                    int is_superior = json.getInt("is_superior");
                    int superior_id = json.getInt("superior_id");
                    if (is_superior == 1 && superior_id == 0) {
                        user.setLevel("one");
                    }
                    if (is_superior == 1 && superior_id != 0) {
                        user.setLevel("two");
                    }
                    if (is_superior == 0) {
                        user.setLevel("three");
                    }
                    SharedUtils.putUser("user", user, context);
                    SharedPreferences preferences = context.getSharedPreferences("info", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("token", token);
                    editor.apply();

                    context.startActivity(new Intent(context, HomeActivity.class));
                    context.finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (dialog != null) {
                    dialog.dismiss();
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {

                try {
                    JSONObject errorObject = new JSONObject(retrofitError.getBody().toString());
                    String errorMsg = errorObject.getString("message");
                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
                    if (isMain) {
                        context.startActivity(new Intent(context, LoginActivity.class));
                    }
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException n) {
                    context.startActivity(new Intent(context, LoginActivity.class));
                }
            }
        });*/
    }

    Callback<String> callback = new Callback<String>() {
        @Override
        public void success(String s, Response response) {
            Log.e(methodName, response.getUrl() + ":success:" + s);
        }

        @Override
        public void failure(RetrofitError error) {
            Log.e(methodName, error.getUrl() + ":error:" + error.toString());
        }
    };
}
