package com.yun.ycw.crm.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.customview.EditTextWithDel;
import com.yun.ycw.crm.entity.User;
import com.yun.ycw.crm.service.LoginService;
import com.yun.ycw.crm.utils.InternetUtils;
import com.yun.ycw.crm.utils.LoadingUtils;

public class LoginActivity extends Activity {
    @ViewInject(R.id.login_phonenumber_iv)
    protected ImageView phoneicon;
    @ViewInject(R.id.login_password_iv)
    private ImageView pwdicon;
    @ViewInject(R.id.login_login_bu1)
    private Button login1;
    @ViewInject(R.id.login_login_bu)
    private Button login;
    @ViewInject(R.id.login_phonenumber_et)
    private EditTextWithDel phoneNumber;
    @ViewInject(R.id.login_password_et)
    private EditText password;

    @ViewInject(R.id.login_forget_bu)
    private TextView forget;
    private String phone, pwd;
    private User user;
    private Dialog dialog;//登录时显示的对话框
    private RequestParams params;
    private String str;
    private String mPageName = "LoginActivity";
    private Context mContext;
    private PushAgent mPushAgent;
    private String device_token;
    private int OUT_FLAG;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewUtils.inject(this);
        ///////////////////////////// umeng相关 推送/////////////////////////////////////////////

        mPushAgent = PushAgent.getInstance(this);
        //应用程序启动统计
        mPushAgent.onAppStart();
        //开启推送并设置注册的回调处理
        mPushAgent.enable();
        //设置保护进程间隔时间
        PushAgent.sendSoTimeout(this, 600);

//        device_token = mPushAgent.getRegistrationId();
        Log.i("mPushAgent", mPushAgent.isEnabled() + "");
        Log.i("mPushAgent token ____", mPushAgent.getRegistrationId() + mPushAgent.getMessageAppkey());

        setEdittextListener();
    }

    public void setEdittextListener() {
        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (phoneNumber.getText().toString().length() != 0) {
                    phoneicon.setImageResource(R.mipmap.user_name);
                    login1.setVisibility(View.INVISIBLE);
                    login.setVisibility(View.VISIBLE);


                } else {
                    phoneicon.setImageResource(R.mipmap.user_name_gray);
                    login.setVisibility(View.INVISIBLE);
                    login1.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (password.getText().toString().length() != 0) {
                    pwdicon.setImageResource(R.mipmap.password);
                } else {
                    pwdicon.setImageResource(R.mipmap.password_gray);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    @OnClick({R.id.login_login_bu, R.id.login_forget_bu})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_login_bu:  //登录按钮点击
                phone = phoneNumber.getText().toString();
                pwd = password.getText().toString();

                //点击登录时显示登录对话框
                dialog = LoadingUtils.createLoadingDialog(LoginActivity.this, "正在登录");
                dialog.show();
                if (InternetUtils.internet(LoginActivity.this)) {
                    Log.i("mPushAgent", mPushAgent.isEnabled() + "");
                    Log.i("mPushAgent token ____", mPushAgent.getRegistrationId());
                    LoginService.getInstance().userLogin(phone, pwd, mPushAgent.getRegistrationId(), LoginActivity.this, dialog,false);
                    //添加alias
//                    new AddAliasTask("dywu", "BAIDU").execute();

                }

                //checklogin(phoneNumber.getText().toString(), password.getText().toString(),dialog);
//                checklogin("唐李浩", "111111", dialog);
//                if (phone.equals("") || pwd.equals("")) {
//                    Toast.makeText(LoginActivity.this, "输入不能为空", Toast.LENGTH_LONG).show();
//                } else {
//                    if (!phone.matches("^[1][3578][0-9]{9}$")) {
//
//                        Toast.makeText(LoginActivity.this, "手机号格式不正确", Toast.LENGTH_LONG).show();
//                    } else {
//                        if (!pwd.matches("^[0-9a-zA-Z]{6,10}$")) {
//                            Toast.makeText(LoginActivity.this, "密码格式不对", Toast.LENGTH_LONG).show();
//                        } else {
//                            //点击登录时显示登录对话框
//                            dialog = LoadingUtils.createLoadingDialog(LoginActivity.this, "正在登录");
//                            dialog.show();
//                            checklogin(phoneNumber.getText().toString(), password.getText().toString());
//
//                        }
//                    }
//                }
                //  startActivity(new Intent(LoginRegisterActivity.this,LoginActivity.class));
                break;
            case R.id.login_forget_bu:
                break;
            default:
                break;
        }
    }

    class AddAliasTask extends AsyncTask<Void, Void, Boolean> {

        String alias;
        String aliasType;

        public AddAliasTask(String aliasString, String aliasTypeString) {
            // TODO Auto-generated constructor stub
            this.alias = aliasString;
            this.aliasType = aliasTypeString;
        }

        protected Boolean doInBackground(Void... params) {
            try {
                return mPushAgent.addAlias(alias, aliasType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (Boolean.TRUE.equals(result))
                Log.i("TAG", "alias was set successfully.");

        }

    }


//    public void showDialog() {
//        if (progDialog == null) {
//            progDialog = new ProgressDialog(this);
//            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            progDialog.setMessage("正在登录");
//        }
//        progDialog.show();
//    }
//
//    public void dissmiss() {
//        if (progDialog != null && progDialog.isShowing())
//            progDialog.dismiss();
//    }

//    public void checklogin(String username, String password, Dialog dialog) {
//        GetTokenUtils.getTokenJson(this, username, password, dialog);
//
//    }


}
