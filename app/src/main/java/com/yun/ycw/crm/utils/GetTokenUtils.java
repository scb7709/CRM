package com.yun.ycw.crm.utils;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.yun.ycw.crm.activity.HomeActivity;
import com.yun.ycw.crm.activity.LoginActivity;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 获取token并保存，用于用户的验证和请求数据时的header
 * Created by wdyan on 2016/2/16.
 */
public class GetTokenUtils {

    private static String tokenString;
    private static RequestParams params;

    public static void getTokenJson(final Context context, String cellphone, String passward, final Dialog dialog) {


        if (InternetUtils.internet(context)) {

            HttpUtils httpUtils = new HttpUtils();
            params = new RequestParams();
            params.addBodyParameter("user_login", cellphone);
            params.addBodyParameter("password", passward);
            httpUtils.send(HttpRequest.HttpMethod.POST, "http://api.dev.yuncaiwang.net/crm/login", params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    String jsonString = responseInfo.result;
                    try {
                        JSONObject Obj = new JSONObject(jsonString);
                        String token = Obj.getString("token");//获取token的值
                        SharedUtils.putToken("info", token, context);
                        System.out.println(token);
                        dialog.dismiss();
                        context.startActivity(new Intent(context, HomeActivity.class));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    System.out.println(s);
                }
            });
        }
    }
}
