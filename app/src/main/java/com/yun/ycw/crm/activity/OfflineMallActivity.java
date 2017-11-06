package com.yun.ycw.crm.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnRadioGroupCheckedChange;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.adapter.GoodsMallAdapter;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.entity.GoodsModel;
import com.yun.ycw.crm.utils.GoodsCatgoryUtils;
import com.yun.ycw.crm.utils.MySQLiteDataDao;
import com.yun.ycw.crm.utils.InternetUtils;
import com.yun.ycw.crm.utils.LoadingUtils;
import com.yun.ycw.crm.utils.SharedUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by scb on 2016/3/3.
 */
public class OfflineMallActivity extends Activity {
    @ViewInject(R.id.offlinemal_back)
    private ImageButton offlinemal_back;//返回
    @ViewInject(R.id.offlinemal_picdown)
    private TextView offlinemal_picdown;//下载更新
    @ViewInject(R.id.offlinemal_query_ed)
    private EditText offlinemal_query_ed;//查询

    @ViewInject(R.id.offlinemal_rb)
    private RadioButton offlinemal_rb;//默认
    @ViewInject(R.id.offlinemal_water_tv)
    private RadioButton offlinemal_water_tv;//水工
    @ViewInject(R.id.offlinemal_electricity_tv)
    private RadioButton offlinemal_electricity_tv;//电工
    @ViewInject(R.id.offlinemal_wood_tv)
    private RadioButton offlinemal_wood_tv;//木工
    @ViewInject(R.id.offlinemal_tile_tv)
    private RadioButton offlinemal_tile_tv;//瓦工
    @ViewInject(R.id.offlinemal_oil_tv)
    private RadioButton offlinemal_oil_tv;//油工
    @ViewInject(R.id.offlinemal_tool_tv)
    private RadioButton offlinemal_tool_tv;//工具

    @ViewInject(R.id.offlinemal_catgroy_ly)
    private LinearLayout offlinemal_catgroy_ly;//子类跟布局
    @ViewInject(R.id.offlinemal_catgroy_vp)
    private HorizontalScrollView offlinemal_catgroy_vp;//子类
    @ViewInject(R.id.offlinemal_catgroy_layout)
    private RadioGroup offlinemal_catgroy_layout;//子类

    @ViewInject(R.id.offlinemal_brand_vp)
    private HorizontalScrollView offlinemal_brand_vp;//品牌
    @ViewInject(R.id.offlinemal_brand_ly)
    private RadioGroup offlinemal_brand_ly;//品牌
    @ViewInject(R.id.offlinemal_brand_layout)
    private LinearLayout offlinemal_brand_layout;//品牌
    @ViewInject(R.id.offlinemal_brand_view)
    private View offlinemal_brand_view;//品牌下边的直线

    @ViewInject(R.id.offlinemal_img1)
    private LinearLayout offlinemal_img1;//根类横线
    @ViewInject(R.id.lin11)
    private ImageView lin11;
    @ViewInject(R.id.lin12)
    private ImageView lin12;
    @ViewInject(R.id.lin13)
    private ImageView lin13;
    @ViewInject(R.id.lin14)
    private ImageView lin14;
    @ViewInject(R.id.lin15)
    private ImageView lin15;
    @ViewInject(R.id.lin16)
    private ImageView lin16;
    @ViewInject(R.id.offlinemal_img2)
    private LinearLayout offlinemal_img2;//根类小三角
    @ViewInject(R.id.lin21)
    private ImageView lin21;
    @ViewInject(R.id.lin22)
    private ImageView lin22;
    @ViewInject(R.id.lin23)
    private ImageView lin23;
    @ViewInject(R.id.lin24)
    private ImageView lin24;
    @ViewInject(R.id.lin25)
    private ImageView lin25;
    @ViewInject(R.id.lin26)
    private ImageView lin26;

    @ViewInject(R.id.offlinemal_lv)
    private ListView offlinemal_lv;//
    @ViewInject(R.id.offlinemal_rg)
    private RadioGroup offlinemal_rg;//
    private int parentid;
    private Dialog dialog;
    private List<GoodsModel> list;
    private GoodsMallAdapter goodsMallAdapter;
    private boolean isExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offlinemall);
        ViewUtils.inject(this);

        isExist = MySQLiteDataDao.getInstance(OfflineMallActivity.this).tabIsExist("goods");
        offlinemal_water_tv.setChecked(true);
        setListener();
        if (!isExist) {
            addCatButton(MySQLiteDataDao.getInstance(OfflineMallActivity.this).queryCatgory(parentid));
            setAdapter(1);
            if (InternetUtils.internet(this)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("温馨提示");
                builder.setMessage("当前无任何数据，您是否要下载数据包？");
                builder.setPositiveButton("立即更新",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // 开始更新
                                downdata();
                            }
                        });
                builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        }
    }

    protected void setListener() {
        offlinemal_query_ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (offlinemal_query_ed.getText().toString().length() != 0) {
                    offlinemal_brand_layout.setVisibility(View.GONE);
                    offlinemal_catgroy_ly.setVisibility(View.GONE);
                    offlinemal_rb.setChecked(true);
                    setLineImg(0,0);
                    try {
                        if (list.size() != 0) {
                            list.clear();
                        }
                    } catch (NullPointerException n) {
                    }
                    list = MySQLiteDataDao.getInstance(OfflineMallActivity.this).queryName(offlinemal_query_ed.getText().toString());
                    goodsMallAdapter = new GoodsMallAdapter(list, OfflineMallActivity.this);
                    offlinemal_lv.setAdapter(goodsMallAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnRadioGroupCheckedChange({R.id.offlinemal_rg})
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        offlinemal_query_ed.setText("");
        offlinemal_catgroy_ly.setVisibility(View.VISIBLE);
        switch (checkedId) {
            case R.id.offlinemal_water_tv:
                parentid = 1;
                setLineImg(R.id.lin11,R.id.lin21);
                break;
            case R.id.offlinemal_electricity_tv:
                parentid = 2;
                setLineImg(R.id.lin12,R.id.lin22);
                break;
            case R.id.offlinemal_wood_tv:
                setLineImg(R.id.lin13,R.id.lin23);
                parentid = 3;
                break;
            case R.id.offlinemal_tile_tv:
                setLineImg(R.id.lin14,R.id.lin24);
                parentid = 4;
                break;
            case R.id.offlinemal_oil_tv:
                setLineImg(R.id.lin15,R.id.lin25);
                parentid = 5;
                break;
            case R.id.offlinemal_tool_tv:
                setLineImg(R.id.lin16,R.id.lin26);
                parentid = 7;
                break;
        }
        if (isExist) {
            offlinemal_brand_layout.setVisibility(View.GONE);
            addCatButton(MySQLiteDataDao.getInstance(OfflineMallActivity.this).queryCatgory(parentid));
            setAdapter(parentid);
        }
    }

    @OnClick({R.id.offlinemal_back
            , R.id.offlinemal_picdown
            , R.id.offlinemal_query_ed
            , R.id.offlinemal_water_tv

    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.offlinemal_water_tv:
                parentid = 1;
                if (isExist) {
                    offlinemal_brand_layout.setVisibility(View.GONE);
                    addCatButton(MySQLiteDataDao.getInstance(OfflineMallActivity.this).queryCatgory(parentid));
                    setAdapter(parentid);
                }
                break;
            case R.id.offlinemal_back:
                finish();
                break;
            case R.id.offlinemal_picdown:
                downDialog();
                break;
            case R.id.offlinemal_query_ed:
                break;
        }
    }

    //添加分类按钮
    private List<Button> buttoncatlist = new ArrayList<Button>();
    private String flagcat;

    private void addCatButton(List<String> cat) {
        for (Button but : buttoncatlist) {
            offlinemal_catgroy_layout.removeAllViews();
        }
        if (cat.size() == 0) {

            offlinemal_catgroy_layout.setVisibility(View.GONE);
            return;
        } else {
            offlinemal_catgroy_layout.setVisibility(View.VISIBLE);

        }
        for (final String bra : cat) {
            final Button button = new Button(this);
            button.setText(bra);
            button.setTextSize(12);
            button.setGravity(Gravity.CENTER);
            button.setBackgroundResource(R.drawable.rectangularframe);
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (button.getText().toString() != null) {
                        flagcat = button.getText().toString();
                        for (Button but : buttoncatlist) {
                            if (but.getText().toString().equals(flagcat)) {
                                but.setTextColor(Color.BLUE);
                            } else {
                                but.setTextColor(Color.BLACK);
                            }
                        }
                        if (parentid != 0) {
                            addButton(MySQLiteDataDao.getInstance(OfflineMallActivity.this).queryBrand(parentid, getCatid(parentid + "", flagcat)));
                            setAdapter(flagcat);

                        }
                    }
                }
            });
            buttoncatlist.add(button);

            offlinemal_catgroy_layout.addView(button);
        }
    }

    //添加品牌按钮
    private List<Button> buttonlist = new ArrayList<Button>();
    private String flag;

    private void addButton(List<String> brand) {
        for (Button but : buttonlist) {
            offlinemal_brand_ly.removeAllViews();
        }
        if (brand.size() == 0) {
            offlinemal_brand_view.setVisibility(View.GONE);
            offlinemal_brand_layout.setVisibility(View.GONE);
            return;
        } else {
            offlinemal_brand_layout.setVisibility(View.VISIBLE);
            offlinemal_brand_view.setVisibility(View.VISIBLE);
        }
        for (final String bra : brand) {
            final Button button = new Button(this);
            button.setText(bra);
            button.setTextSize(12);
            button.setGravity(Gravity.CENTER);
            button.setBackgroundResource(R.drawable.rectangularframe);
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (button.getText().toString() != null) {
                        flag = button.getText().toString();
                        for (Button but : buttonlist) {
                            if (but.getText().toString().equals(flag)) {
                                but.setTextColor(Color.BLUE);
                            } else {
                                but.setTextColor(Color.BLACK);
                            }
                        }
                        setAdapter(flagcat, bra);
                    }
                }
            });
            buttonlist.add(button);
            offlinemal_brand_ly.addView(button);
        }
    }

    //获取类别的ID
    private int getCatid(String parentid, String j) {
        try {
            String s = GoodsCatgoryUtils.getCatgory();
            JSONArray array = new JSONArray(s);
            for (int i = 0; i < array.length(); i++) {
                JSONObject arrobj = array.getJSONObject(i);
                if (j.equals(arrobj.getString("name")) && parentid.equals(arrobj.getString("parentid"))) {
                    return arrobj.getInt("id");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //获取根分类ID
    private int getParentid(int j) {
        try {
            String s = GoodsCatgoryUtils.getCatgory();
            JSONArray array = new JSONArray(s);
            for (int i = 0; i < array.length(); i++) {
                JSONObject arrobj = array.getJSONObject(i);
                if (j == arrobj.getInt("id")) {
                    return arrobj.getInt("parentid");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //设置适配器 根id+分类id
    private void setAdapter(String name) {
        try {
            if (list.size() != 0) {
                list.clear();
            }
        } catch (NullPointerException n) {

        }
        list = MySQLiteDataDao.getInstance(OfflineMallActivity.this).queryAll(parentid, getCatid(parentid + "", name));
        goodsMallAdapter = new GoodsMallAdapter(list, OfflineMallActivity.this);
        offlinemal_lv.setAdapter(goodsMallAdapter);
    }

    //设置适配器 根id+分类id+品牌
    private void setAdapter(String name, String brand) {
        try {
            if (list.size() != 0) {
                list.clear();
            }
        } catch (NullPointerException n) {

        }
        list = MySQLiteDataDao.getInstance(OfflineMallActivity.this).queryAll(parentid, getCatid(parentid + "", name), brand);
        goodsMallAdapter = new GoodsMallAdapter(list, OfflineMallActivity.this);
        offlinemal_lv.setAdapter(goodsMallAdapter);
    }

    //设置适配器 根id
    private void setAdapter(int parentid) {
        try {
            if (list.size() != 0) {
                list.clear();
            }
        } catch (NullPointerException n) {
        }
        list = MySQLiteDataDao.getInstance(OfflineMallActivity.this).queryAll(parentid);
        goodsMallAdapter = new GoodsMallAdapter(list, OfflineMallActivity.this);
        offlinemal_lv.setAdapter(goodsMallAdapter);
    }

    //网络加载数据源
    private void downdata() {

        dialog = LoadingUtils.createLoadingDialog(OfflineMallActivity.this, "正在更新，请稍等...");
        dialog.show();
        if (MySQLiteDataDao.getInstance(OfflineMallActivity.this).tabIsExist("goods")) {
            MySQLiteDataDao.getInstance(OfflineMallActivity.this).delete();//删除数据库
        }
        final List<GoodsModel> goodslist = new ArrayList<GoodsModel>();
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addHeader("Authorization", "Bearer" + " " + SharedUtils.getToken(this, "info"));
        httpUtils.send(HttpRequest.HttpMethod.GET, Constants.GOODS_MALL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String s = responseInfo.result;
                JSONArray array = null;
                JSONObject obj = null;
                try {
                    array = new JSONArray(s);
                    for (int i = 0; i < array.length(); i++) {
                        obj = array.getJSONObject(i);
                        String name = obj.getString("name");
                        int catid = obj.getInt("catid");
                        int goodsid = obj.getInt("id");
                        int parentid = getParentid(obj.getInt("catid"));
                        String logurl = obj.getString("logourl");
                        String specifications2 = obj.getString("specifications2");
                        String brand = obj.getString("brand");
                        String price = obj.getString("price");
                        String unit = obj.getString("unit");
                        goodslist.add(new GoodsModel(name, catid, goodsid, parentid, logurl, specifications2, brand, price, unit));
                    }
                    MySQLiteDataDao.getInstance(OfflineMallActivity.this).insertBySql(goodslist, OfflineMallActivity.this, dialog);//数据库插入数据
                } catch (JSONException e) {
                    e.printStackTrace();
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                dialog.dismiss();
            }
        });

    }

    //提示对话款
    private void downDialog() {
        if (InternetUtils.internet(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("数据更新");
            builder.setMessage("资源更新会占用一定时间，你确定这样做吗？");
            builder.setPositiveButton("立即更新",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // 开始更新
                            downdata();
                        }
                    });
            builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }
    }

    private void setLineImg(int i,int k) {
        ImageView img1[] = {lin11, lin12, lin13, lin14, lin15, lin16};
        ImageView img2[] = {lin21, lin22, lin23, lin24, lin25, lin26};
        if(i==0||k==0){
            for(int j=0;j<6;j++){
                img1[j].setImageResource(R.mipmap.linewhite);
                img2[j].setImageResource(R.mipmap.grayline);
            }
        }
        else {
            for (int j = 0; j < 6; j++) {
                if (img1[j].getId() == i && img2[j].getId() == k) {
                    img1[j].setImageResource(R.mipmap.linegreen);
                    img2[j].setImageResource(R.mipmap.triangle);

                } else {
                    img1[j].setImageResource(R.mipmap.linewhite);
                    img2[j].setImageResource(R.mipmap.grayline);
                }
            }
        }
    }
}