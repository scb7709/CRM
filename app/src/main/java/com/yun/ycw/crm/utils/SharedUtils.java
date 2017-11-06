package com.yun.ycw.crm.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.util.Log;

import com.yun.ycw.crm.entity.User;


/*
 Created by scb on 2016/2/1.
* 此为保存数据的工具类
*
* */
public class SharedUtils {
    private static Context context;

    public SharedUtils(Context context) {
        this.context = context;
    }

    //保存用户登录成功后 返回的token
    public static void putToken(String fileName, String token, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString("token", token);

        editor.commit();
    }

    //取出token信息
    public static String getToken(Context context, String fileName) {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return preferences.getString("token", "");

    }

    /**
     * 保存登录信息
     */
    public static void putUser(String fileName, User user, Context context) {
        SharedPreferences preferences =
                context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        if (user.getName() == null && user.getIcon() != null) {
            editor.putString("icon", user.getIcon());
        } else {
            editor.putString("phoneNumber", user.getPhoneNumber());
            editor.putString("name", user.getName());
            editor.putString("password", user.getLoginPwd());
            editor.putString("level", user.getLevel());//String 类型
            editor.putString("is_superior", user.getIs_superior());
            editor.putString("superior_id", user.getSuperior_id());
            editor.putString("id", user.getId());
            editor.putString("team", user.getTeam());
            editor.putString("icon", user.getIcon());
            editor.putString("department", "销售部");
       /*  editor.putString("company", user.getCompany());
        editor.putString("position", user.getPosition());*/
            editor.putString("email", user.getEmail());
        }
        editor.commit();
    }

    /**
     * 获取登录信息
     */
    public static User getUser(String fileName, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String name = preferences.getString("name", null);
        if (name != null) {
            User user = new User();
            user.setPhoneNumber(preferences.getString("phoneNumber", ""));
            user.setName(preferences.getString("name", ""));
            user.setLoginPwd(preferences.getString("password", ""));
            user.setId(preferences.getString("id", "0"));
            user.setIs_superior(preferences.getString("is_superior", "0"));
            user.setSuperior_id(preferences.getString("superior_id", "0"));
            user.setLevel(preferences.getString("level", ""));
            user.setIcon(preferences.getString("icon", ""));
            user.setDepartment(preferences.getString("department", ""));
            user.setCompany(preferences.getString("company", "北京辅力达信息技术有限公司"));
            switch (user.getSuperior_id()) {
                case "0":
                    user.setPosition(preferences.getString("position", "销售总经理"));
                    break;
                case "28":
                    user.setPosition(preferences.getString("position", "销售主管"));
                    break;
                default:
                    user.setPosition(preferences.getString("position", "销售专员"));
                    break;
            }
            if (user.getSuperior_id().equals("24") || user.getId().equals("24")) {
                user.setTeam(preferences.getString("team", "云家军二队"));
            } else if (user.getSuperior_id().equals("25") || user.getId().equals("25")) {
                user.setTeam(preferences.getString("team", "云家军三队"));
            } else if (user.getSuperior_id().equals("27") || user.getId().equals("27")) {
                user.setTeam(preferences.getString("team", "云家军一队"));
            }  else {
                user.setTeam(preferences.getString("team", "云家军"));
            }
            user.setEmail(preferences.getString("email", ""));
            return user;
        }
        return null;
    }

    /**
     * 退出登录状态
     */
    public static void loginOut(String fileName) {
        SharedPreferences preferences =
                context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public static boolean getBoolean(String fileName, String key) {
        SharedPreferences preferences =
                context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }


    public static String getPhotoPath(String fileName, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);

        return preferences.getString("path", null);
    }

    public static void putPhotoPath(String fileName, String path, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString("path", path);
        editor.commit();
    }

    public static String getIconFlag(String fileName, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return preferences.getString("flag", "");
    }

    public static void putIconFlag(String fileName, String flag, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString("flag", flag);
        editor.commit();
    }

    public static String getStaffHierarchy(String fileName, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return preferences.getString("staffhierarchy", "");
    }

    public static void putStaffHierarchy(String fileName, String staffhierarchy, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.clear();
        Log.i("ZZZZZ",staffhierarchy);
        editor.putString("staffhierarchy", staffhierarchy);
        editor.commit();
    }

    public static String getGoodsCatgory(String fileName, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return preferences.getString("goodscatgory", "");
    }

    public static void putGoodsCatgory(String fileName, String goodscatgory, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString("goodscatgory", goodscatgory);
        editor.commit();
    }

}
