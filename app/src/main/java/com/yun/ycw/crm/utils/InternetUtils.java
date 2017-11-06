package com.yun.ycw.crm.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by scb on 2016/2/16.
 */
public class InternetUtils {
    public static boolean internet(Context context) {
        //检查当前网络连接
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return true;
        }
        Toast.makeText(context, "网络连接失败", Toast.LENGTH_LONG).show();
        return false;

    }
}