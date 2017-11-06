package com.yun.ycw.crm.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.yun.ycw.crm.API.RetrofitInterface;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.comparator.StringConverter;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.entity.User;
import com.yun.ycw.crm.service.LogoutService;
import com.yun.ycw.crm.utils.InternetUtils;
import com.yun.ycw.crm.utils.LoadingUtils;
import com.yun.ycw.crm.utils.SharedUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by scb on 2016/2/2.
 */
public class PersonalCenterActivity extends Activity {
    @ViewInject(R.id.updatemy_outlogin_bu)
    private Button outlogin;
    @ViewInject(R.id.updatemy_icon_iv)
    private ImageView icon;//头像图片
    @ViewInject(R.id.updatemy_phonenumber_tv)
    private TextView phonenumber;
    @ViewInject(R.id.updatemy_name_tv)
    private TextView name;
    @ViewInject(R.id.updatemy_company_tv)
    private TextView company;
    @ViewInject(R.id.updatemy_department_tv)
    private TextView department;
    @ViewInject(R.id.updatemy_team_tv)
    private TextView team;
    @ViewInject(R.id.updatemy_team_view)
    private View updatemy_team_view;

    @ViewInject(R.id.updatemy_position_tv)
    private TextView position;
    @ViewInject(R.id.updatemy_email_tv)
    private TextView email;

    @ViewInject(R.id.updatemy_back)
    private ImageButton updatemy_back;

    private User user;//账户信息
    private AlertDialog dialog;//对话框对象
    private String photopath;
    private Context mContext;
    private String mPageName = "PersonalCenterActivity";
    private SimpleDateFormat sDateFormat;
    private String initStartDateTime;
    private PushAgent mPushAgent;
    private RetrofitInterface retrofitInterface;
    private RequestInterceptor requestInterceptor;
    private String avatarUrl = null;
    private Dialog dialog1;
    private boolean OUT_FLAG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatemy);
        ViewUtils.inject(this);
        mContext = this;
        setdate();
    }

    @OnClick({R.id.updatemy_outlogin_bu, R.id.updatemy_icon_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.updatemy_outlogin_bu:
                mPushAgent = PushAgent.getInstance(PersonalCenterActivity.this);
                mPushAgent.disable();
                if (InternetUtils.internet(PersonalCenterActivity.this)) {
                    OUT_FLAG = true;
                    LogoutService.getInstance(PersonalCenterActivity.this).userLogout(SharedUtils.getUser("user", PersonalCenterActivity.this).getId());
                }
                new SharedUtils(PersonalCenterActivity.this).loginOut("user");
//                Intent intent = new Intent(PersonalCenterActivity.this, LoginActivity.class);
//                startActivity(intent);
                Intent i = getIntent();
                setResult(RESULT_OK, i);
                finish();
                break;
            case R.id.updatemy_icon_iv:
                iconChoose();
                break;
        }

    }

    public void setdate() {
        updatemy_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        user = SharedUtils.getUser("user", PersonalCenterActivity.this);
        requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Authorization", "Bearer" + " " + SharedUtils.getToken(PersonalCenterActivity.this, "info"));
            }
        };
        if (user.getIcon().length() != 0) {
            Picasso.with(PersonalCenterActivity.this)
                    .load(user.getIcon())//图片网址
                    .placeholder(R.mipmap.hand)//默认图标
                    .into(icon);//控件
        } else {
            String camPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CRMicon/iconn.png";
            String photoPath = SharedUtils.getPhotoPath("iconpath", PersonalCenterActivity.this);
            user = SharedUtils.getUser("user", PersonalCenterActivity.this);
            icon.setClickable(true);
            try {
                if (SharedUtils.getIconFlag("flagg", PersonalCenterActivity.this).equals("takePhoto")) {
                    File file = new File(camPath);
                    if (file.exists()) {
                        FileInputStream fis = null;
                        try {
                            fis = new FileInputStream(camPath);
                            Bitmap bitmap = BitmapFactory.decodeStream(fis);
                            icon.setImageBitmap(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    File file = new File(photoPath);
                    if (file.exists()) {
                        FileInputStream fis = null;
                        try {
                            fis = new FileInputStream(photoPath);
                            Bitmap bitmap = BitmapFactory.decodeStream(fis);
                            icon.setImageBitmap(bitmap);

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                }

            } catch (NullPointerException n) {
                icon.setImageResource(R.mipmap.hand);
            }
        }
        phonenumber.setText(user.getPhoneNumber().equals("null")?"":user.getPhoneNumber());
        name.setText(user.getName());
        company.setText("公司  " + user.getCompany());
        department.setText("部门  " + user.getDepartment());
        if (user.getSuperior_id().equals("0")) {
            updatemy_team_view.setVisibility(View.GONE);
            team.setVisibility(View.GONE);
        } else {
            team.setText("团队  " + user.getTeam());
        }
        position.setText("职位  " + user.getPosition());
        email.setText("邮箱  " + user.getEmail());
    }

    //自定义头像 选择
    private void iconChoose() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PersonalCenterActivity.this);
        dialog = builder.create();
        View view = LayoutInflater.from(PersonalCenterActivity.this).inflate(R.layout.dialog_iconchoose, null);
        Button takePhoto = (Button) view.findViewById(R.id.dialogchoses_takephoto_bu);
        Button choosePhoto = (Button) view.findViewById(R.id.dialogchoses_chosesphoto_bu);

        takePhoto.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });
        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
        dialog.setView(view, 20, 20, 20, 50);
        dialog.setCanceledOnTouchOutside(true);//点击边界取消
        dialog.show();
    }

    //处理选图后的结果
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //拍照获取图片
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            icon.setImageBitmap(bitmap);
            SharedUtils.putIconFlag("flagg", "takePhoto", PersonalCenterActivity.this);
            saveMyBitmap(bitmap);
            dialog.dismiss();
        }
        // 读取相册图片
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Cursor cursor = PersonalCenterActivity.this.getContentResolver().query(data.getData(), null, null, null, null);
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            SharedUtils.putIconFlag("flagg", "choosePhoto", PersonalCenterActivity.this);
            SharedUtils.putPhotoPath("iconpath", cursor.getString(idx), PersonalCenterActivity.this);
            File file = new File(cursor.getString(idx));
            Bitmap bitmap = BitmapFactory.decodeFile(file.toString());
            icon.setImageBitmap(bitmap);
            RequestParams params = new RequestParams();
            params.addBodyParameter("file", file);
            uploadMethod(params, Constants.BASE_URL + "/file-upload");
            cursor.close();//cursor查询完之后要关掉

        } else {
            Toast.makeText(PersonalCenterActivity.this, "没有选中图片", Toast.LENGTH_LONG).show();
        }
        dialog.dismiss();
    }

    //保存图片
    protected void saveMyBitmap(Bitmap bitmap) {

        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "sdcard不存在!", Toast.LENGTH_SHORT).show();
            return;
        }
        deleteIcon();
        // 文件在sdcard的路径
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/CRMicon");
        if (!file.exists()) {
            file.mkdir();
        }
        File filepic = new File(file.getPath(), "iconn.png");
        try {
            if (!filepic.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filepic);
            BufferedOutputStream out = new BufferedOutputStream(fos);
            bitmap.compress(Bitmap.CompressFormat.PNG, 20, out);
            out.flush();
            RequestParams params = new RequestParams();
            params.addBodyParameter("file", filepic);
            uploadMethod(params, Constants.BASE_URL + "/file-upload");
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteIcon() {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/CRMicon/iconn.png");
        if (dir.exists())
            dir.delete(); //
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageStart(mPageName);
        MobclickAgent.onResume(mContext);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName);
        MobclickAgent.onResume(mContext);
    }

    //上传照片
    public void uploadMethod(final RequestParams params, final String uploadHost) {
        dialog1 = LoadingUtils.createLoadingDialog(PersonalCenterActivity.this, "正在上传");
        dialog1.show();
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, uploadHost, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.i("XXXXXXXXXX", responseInfo.result.toString());
                try {
                    avatarUrl = new JSONObject(responseInfo.result.toString()).getString("filename");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Map<String, String> params = new HashMap<>();
                params.put("avatar", avatarUrl);
                initRetrofit();
                retrofitInterface.deleteDevice_token(user.getId(), params, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        Log.i("XXXXXXXXXXSSSS", s);
                        User user = new User();
                        user.setIcon(Constants.FILE_BASE_URL + avatarUrl);
                        SharedUtils.putUser("user", user, PersonalCenterActivity.this);
                        dialog1.dismiss();
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        dialog1.dismiss();
                    }
                });


            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Log.i("XXXXXFFFF", error.toString());
                dialog1.dismiss();
            }
        });
    }

    private void initRetrofit() {
        RestAdapter retrofit = new RestAdapter.Builder()
                .setEndpoint(Constants.BASE_URL)
                .setConverter(new StringConverter())//设置数据转换器，默认是使用GsonConvert，但是因为测试接口是在新浪云上面布置，并且没有通过实名认证，返回数据不规范，所以一直使用自定义的convert
                .setLogLevel(RestAdapter.LogLevel.FULL)//设置log参数，设置了之后可以看到一些log参数，比如会在logcat中打印出来heads
                .setRequestInterceptor(requestInterceptor)//Headers that need to be added to every request，如果对每个请求都需要添加特定的heads，可以这样来配置
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (OUT_FLAG) {
            MobclickAgent.onKillProcess(PersonalCenterActivity.this);
            android.os.Process.killProcess(android.os.Process.myPid());
        }

    }
}
