package com.yun.ycw.crm.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Config;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MyLocationStyle;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.annotation.Check;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ta.utdid2.device.DeviceInfo;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.utils.InternetUtils;
import com.yun.ycw.crm.utils.LoadingUtils;
import com.yun.ycw.crm.utils.SharedUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.CacheRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 签到页面
 * Created by wdyan on 2016/3/28.
 */
public class CheckInActivity extends Activity implements LocationSource, AMapLocationListener, View.OnClickListener {
    @ViewInject(R.id.checkIn_location)
    private TextView checkIn_location;//签到地址显示
    @ViewInject(R.id.checkIn_time)
    private TextView checkIn_time;//签到时间显示
    @ViewInject(R.id.checkIn_content)
    private EditText checkIn_content;//签到备注显示
    @ViewInject(R.id.checkIn_confirm)
    private Button checkIn_confirm;//签到确定button
    @ViewInject(R.id.checkIn_back)
    private ImageButton checkIn_back;//页面返回button
    @ViewInject(R.id.cameraImg)
    private ImageView cameraImg;//点击拍照img
    @ViewInject(R.id.camImg)
    private ImageView camImg;//拍照图片显示
    @ViewInject(R.id.camContainer)
    private FrameLayout camContainer;
    @ViewInject(R.id.checkIn_cam_layout)
    private RelativeLayout checkIn_cam_layout;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private MapView mMapView;
    private AMap mAMap;
    private Dialog dialog;
    private double latitude;
    private double longitude;
    private List<String> images = new ArrayList<>();
    private int camFlag = 0;//判断是否已拍照

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);
        ViewUtils.inject(this);
        mMapView = (MapView) findViewById(R.id.locationMap);
        mMapView.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        //设置地图相关
        if (InternetUtils.internet(this)) {

            String token = SharedUtils.getToken(this, "info");
            Log.i("TOKEN", token);

            dialog = LoadingUtils.createLoadingDialog(this, "正在定位");
            Log.i("dia1", dialog + "");
            dialog.show();
            if (mAMap == null) {
                mAMap = mMapView.getMap();
                setUpMap();
            }
        }
        //设置时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        checkIn_time.setText(sdf.format(System.currentTimeMillis()));
        checkIn_back.setOnClickListener(this);
        checkIn_confirm.setOnClickListener(this);
        cameraImg.setOnClickListener(this);
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {

        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.mipmap.location));// 设置“我的位置”图标
        myLocationStyle.strokeColor(Color.GRAY);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 50));// 设置圆形的填充颜色
        //myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        mAMap.setMyLocationStyle(myLocationStyle);
        mAMap.setLocationSource(this);// 设置定位监听
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // aMap.setMyLocationType()
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }


    @Override
    public void activate(LocationSource.OnLocationChangedListener listener) {

        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            // 设置定位监听
            mlocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置只定位一次
            mLocationOption.setOnceLocation(true);

            // 设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除

            mlocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                checkIn_location.setText(amapLocation.getAddress() + "");
                latitude = amapLocation.getLatitude();
                longitude = amapLocation.getLongitude();
                dialog.dismiss();
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": "
                        + amapLocation.getErrorInfo();
                dialog.dismiss();
                Log.e("AmapErr", errText);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkIn_back://点击返回
                finish();
                break;
            case R.id.checkIn_confirm://点击确定
                dialog = LoadingUtils.createLoadingDialog(this, "正在上传，请稍后...");
                dialog.show();
                //上传过程
                if (InternetUtils.internet(this)) {
                    if (camFlag == 1) {
                        HttpUtils httpUtils = new HttpUtils();
                        httpUtils.configCurrentHttpCacheExpiry(0);
                        final RequestParams params = new RequestParams();
                        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Cam/pic.png");
                        params.addBodyParameter("file", f, "image/png");
                        httpUtils.send(HttpRequest.HttpMethod.POST, Constants.UPLOAD_CHECKIN_IMG, params, new RequestCallBack<String>() {
                            @Override
                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                Log.i("Success", responseInfo.result);
                                String jsonString = responseInfo.result;
                                try {
                                    JSONObject Obj = new JSONObject(jsonString);
                                    HttpUtils httpUtils1 = new HttpUtils();
                                    httpUtils1.configCurrentHttpCacheExpiry(0);
                                    RequestParams params1 = new RequestParams();
                                    params1.addHeader("Authorization", "Bearer " + SharedUtils.getToken(CheckInActivity.this, "info"));
                                    params1.addBodyParameter("content", checkIn_content.getText().toString());
                                    params1.addBodyParameter("lat", latitude + "");
                                    params1.addBodyParameter("lng", longitude + "");
                                    params1.addBodyParameter("address", checkIn_location.getText().toString());
                                    images.add(Obj.getString("filename"));
                                    params1.addBodyParameter("images[]", images.get(0) + "");
                                    Log.i("INFO", checkIn_content.getText() + " " + latitude + " " + longitude + " " + checkIn_location.getText() + " " + images.get(0));
                                    httpUtils1.send(HttpRequest.HttpMethod.POST, Constants.CHECKIN_URL, params1, new RequestCallBack<String>() {
                                        @Override
                                        public void onSuccess(ResponseInfo<String> responseInfo) {
                                            Log.i("Success222", responseInfo.result);
                                            Toast.makeText(CheckInActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                            finish();
                                        }

                                        @Override
                                        public void onFailure(HttpException e, String s) {

                                            Log.i("ERROR222", s);
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(HttpException e, String s) {
                                dialog.dismiss();
                                Log.i("ERROR", s);
                            }
                        });
                    } else {
                        dialog.dismiss();
                        Toast.makeText(CheckInActivity.this, "请上传拍照图片", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.cameraImg://点击拍照
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
                break;
        }

    }

    //处理选图后的结果
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //拍照获取图片
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            camContainer.setVisibility(View.VISIBLE);
            camImg.setImageBitmap(bitmap);
            camFlag = 1;
            saveMyBitmap(bitmap);
        }
    }

    //保存图片
    protected void saveMyBitmap(Bitmap bitmap) {

        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "sdcard不存在!", Toast.LENGTH_SHORT).show();
            return;
        }
        // 文件在sdcard的路径
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Cam");
        if (!file.exists()) {
            file.mkdir();
        }
        File filepic = new File(file.getPath(), "pic.png");
        try {
            if (!filepic.exists()) {
                file.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(filepic);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
