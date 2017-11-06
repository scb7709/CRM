package com.yun.ycw.crm.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.yun.ycw.crm.API.RetrofitInterface;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.comparator.StringConverter;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.entity.User;
import com.yun.ycw.crm.service.LoginService;
import com.yun.ycw.crm.utils.GetStaffHierarchyUtils;
import com.yun.ycw.crm.utils.GoodsCatgoryUtils;
import com.yun.ycw.crm.utils.InternetUtils;
import com.yun.ycw.crm.utils.SharedUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by scb on 2016/1/30.
 */

public class MainActivity extends Activity {
    private String mVersionName;
    private int mVersionCode;
    private String mDescription;
    private String mDownloadUrl;
    private User user;//登录的信息
    private Context mContext;
    private String mPageName = "MainActivity";
    private Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Toast.makeText(MainActivity.this, "请先登录", Toast.LENGTH_LONG).show();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            } else if (msg.what == 2) {
                //  Log.i("VVVVVV",user.toString());
                LoginService.getInstance().userLogin(user.getName(), user.getLoginPwd(), mPushAgent.getRegistrationId(), MainActivity.this, null, true);
                finish();
            }
        }
    };

    private PushAgent mPushAgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("1111111111", getVerisonName());
        mContext = this;
        mPushAgent = PushAgent.getInstance(this);
        //应用程序启动统计
        mPushAgent.onAppStart();
        //开启推送并设置注册的回调处理
        mPushAgent.enable();
        //设置保护进程间隔时间
        PushAgent.sendSoTimeout(this, 600);

        GetStaffHierarchyUtils.saveStaffHierarchy(MainActivity.this);
        // GoodsCatgoryUtils.downCatgory(MainActivity.this);
        ///////////////////////////// umeng相关 统计/////////////////////////////////////////////
        MobclickAgent.setDebugMode(true);
        // SDK在统计Fragment时，需要关闭Activity自带的页面统计，
        // 然后在每个页面中重新集成页面统计的代码(包括调用了 onResume 和 onPause 的Activity)。
        //在程序入口处，调用 MobclickAgent.openActivityDurationTrack(false)  禁止默认的页面统计方式，这样将不会再自动统计Activity。
        MobclickAgent.openActivityDurationTrack(false);
        // MobclickAgent.setAutoLocation(true);
        // MobclickAgent.setSessionContinueMillis(1000);

        //集成测试时需要的设备信息
        System.out.println(getDeviceInfo(MainActivity.this));
    }


    /**
     * @param context
     * @return 返回的是设备信息
     * 集成测试时使用
     */
    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            String device_id = tm.getDeviceId();

            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);

            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }

            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            }

            json.put("device_id", device_id);

            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //检查登录
    public void login() {
        user = SharedUtils.getUser("user", MainActivity.this);
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                Message message = Message.obtain();
                if (user == null) {
                    //跳转登录登录注册
                    message.what = 1;
                    handle.sendMessage(message);
                } else {
                    //跳转首页
                    message.what = 2;
                    handle.sendMessage(message);

                }
            }
        }, 2000);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName);
        MobclickAgent.onResume(mContext);
        login();


    /*    if (InternetUtils.internet(this)) {
         checkVersion();
        } else {
            login();
        }*/
        Log.i("!!!!!!!!!!", getVerisonCode() + "");//本地版本号
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
        MobclickAgent.onPause(mContext);
    }

    /**
     * 获取版本名称
     */
    private String getVerisonName() {
        PackageManager pm = this.getPackageManager();// 获取包管理器
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);// 获取当前应用信息
            int versionCode = packageInfo.versionCode;// 版本号
            String versionName = packageInfo.versionName;// 版本名

//            System.out.println("版本号:" + versionCode + ";版本名:" + versionName);

            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // 没有找到包名的异常
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 获取版本号
     *
     * @return
     */
    private int getVerisonCode() {
        PackageManager pm = this.getPackageManager();// 获取包管理器
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);// 获取当前应用信息
            int versionCode = packageInfo.versionCode;// 版本号
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // 没有找到包名的异常
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * 弹出升级对话框
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final AlertDialog dialog = builder.create();
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_download, null);
        progressBar = (SeekBar) view.findViewById(R.id.dialog_download_progress);
        downtext = (TextView) view.findViewById(R.id.dialog_download_tv);
        Button cancel = (Button) view.findViewById(R.id.dialog_download__cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.cancel();
                dialog.dismiss();
                login();
            }
        });
        dialog.setView(view, 20, 20, 20, 50);
        dialog.setCanceledOnTouchOutside(false);//点击边界取消
        dialog.show();

       /* AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("最新版本:" + mVersionName);
        builder.setMessage(mDescription);
        builder.setPositiveButton("立即更新",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 开始更新
//                        System.out.println("开始下载apk");
                        downloaddialog();
                        download();
                    }
                });

        builder.setNegativeButton("以后再说",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new Timer().schedule(new TimerTask() {

                            @Override
                            public void run() {

                                login();
                            }
                        }, 2000);
                    }
                }
        );

        builder.show();*/
    }

    /**
     * 下载apk
     */
    private AlertDialog dialog;//下载的对话框
    private SeekBar progressBar;//下载进度条
    private TextView downtext;//下载进度条

    protected void downloaddialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        dialog = builder.create();
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_download, null);
        progressBar = (SeekBar) view.findViewById(R.id.dialog_download_progress);
        downtext = (TextView) view.findViewById(R.id.dialog_download_tv);
        Button cancel = (Button) view.findViewById(R.id.dialog_download__cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.cancel();
                dialog.dismiss();
                login();
            }
        });
        dialog.setView(view, 20, 20, 20, 50);
        dialog.setCanceledOnTouchOutside(false);//点击边界取消
        dialog.show();


    }

    private HttpUtils utils;
    private HttpHandler handler;

    protected void download() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "sdcard不存在!", Toast.LENGTH_SHORT).show();
            return;
        }

        utils = new HttpUtils();
        // 文件在sdcard的路径
        String path = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/" + new Date().getTime() +
                "crm").getPath();


        handler = utils.download(mDownloadUrl, path, new RequestCallBack<File>() {

            /**
             * 下载进度 total: 总文件大小 current: 已下载大小, 在主线程运行
             */
            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                int percent = (int) (current * 100 / total);
//                System.out.println("下载进度:" + percent + "%");
                downtext.setText("正在下载，已完成:" + percent + "%");// 更新下载进度
                progressBar.setMax((int) (total / 1000));
                progressBar.setProgress((int) (current / 1000));
            }

            /**
             * 下载成功, 在主线程运行
             */
            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                String filePath = responseInfo.result.getPath();
//                System.out.println("下载成功!:" + filePath);

                installApk(responseInfo.result);// 安装apk
            }

            /**
             * 下载失败, 在主线程运行
             */
            @Override
            public void onFailure(HttpException error, String msg) {
                error.printStackTrace();
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    /**
     * 安装apk
     */
    private void installApk(File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivityForResult(intent, 0);
        //安转完成后提示打开
        android.os.Process.killProcess(android.os.Process.myPid());


    }

    public void checkVersion() {
        RestAdapter retrofit = new RestAdapter.Builder()
                .setEndpoint(Constants.BASE_URL)
                .setConverter(new StringConverter())//设置数据转换器，默认是使用GsonConvert，但是因为测试接口是在新浪云上面布置，并且没有通过实名认证，返回数据不规范，所以一直使用自定义的convert
                .setLogLevel(RestAdapter.LogLevel.FULL)//设置log参数，设置了之后可以看到一些log参数，比如会在logcat中打印出来heads
                .build();
        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        retrofitInterface.getCrmVersion(new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                JSONObject jo = null;
                try {
                    jo = new JSONObject(s);
                    mVersionName = jo.getString("versionname");// 版本名
                    mVersionCode = jo.getInt("versioncode");// 版本号
                    mDescription = jo.getString("description");// 版本描述
                    mDownloadUrl = jo.getString("downloadurl");// 下载地址

//                    System.out.print(mDownloadUrl + "ZZZZZZZZZZZZZ");
                    if (mVersionCode > getVerisonCode()) {
                        showUpdateDialog();
                    } else {
                        login();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    login();
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                login();
            }
        });
    }

}

