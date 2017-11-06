package com.yun.ycw.crm.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.activity.OfflineMallActivity;
import com.yun.ycw.crm.activity.SellCheatsActivity;
import com.yun.ycw.crm.activity.PersonalCenterActivity;
import com.yun.ycw.crm.entity.User;
import com.yun.ycw.crm.utils.SharedUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class MyFragment extends Fragment {

    @ViewInject(R.id.my_icon_iv)
    private ImageView icon;//头像图片
    @ViewInject(R.id.my_layout)
    private LinearLayout layout;//
    @ViewInject(R.id.my_goods)
    private LinearLayout goods;//离线商城

    @ViewInject(R.id.sell_cheats)
    private LinearLayout sell_cheats;//销售秘籍

    @ViewInject(R.id.my_phonenumber_tv)
    private TextView phonenumber;


    @ViewInject(R.id.my_name_tv)
    private TextView name;
    private String mPageName = "MyFragment";

    private User user;//账户信息

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_my, null);
        ViewUtils.inject(this, view);
        setdate();
        return view;
    }


    public void setdate() {
        user = SharedUtils.getUser("user", getActivity());
        phonenumber.setText(user.getPhoneNumber().equals("null")?"":user.getPhoneNumber());
        name.setText(user.getName());
        try {
            setAvator();

        } catch (NullPointerException n) {
            icon.setImageResource(R.mipmap.hand);
        }
    }

    private void setAvator() {

        if (user.getIcon().length() != 0) {
            Picasso.with(getActivity())
                    .load(user.getIcon())//图片网址
                    .placeholder(R.mipmap.hand)//默认图标
                    .into(icon);//控件
        } else {
            if (SharedUtils.getIconFlag("flagg", getActivity()).length() != 0) {
                if (SharedUtils.getIconFlag("flagg", getActivity()).equals("takePhoto")) {
                    File file = new File((Environment.getExternalStorageDirectory().getAbsolutePath() + "/CRMicon/iconn.png"));
                    if (file.exists()) {
                        FileInputStream fis = null;
                        try {
                            fis = new FileInputStream((Environment.getExternalStorageDirectory().getAbsolutePath() + "/CRMicon/iconn.png"));
                            Bitmap bitmap = BitmapFactory.decodeStream(fis);
                            icon.setImageBitmap(bitmap);

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    File file = new File(SharedUtils.getPhotoPath("iconpath", getActivity()));
                    if (file.exists()) {
                        FileInputStream fis = null;
                        try {
                            fis = new FileInputStream(SharedUtils.getPhotoPath("iconpath", getActivity()));
                            Bitmap bitmap = BitmapFactory.decodeStream(fis);
                            icon.setImageBitmap(bitmap);

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
            else {
                icon.setImageResource(R.mipmap.hand);
            }
        }
    }

    @OnClick({R.id.my_layout, R.id.my_goods,R.id.sell_cheats})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_layout:
                startActivityForResult(new Intent(getActivity(), PersonalCenterActivity.class), 0);
                break;
            case R.id.my_goods:
                startActivity(new Intent(getActivity(), OfflineMallActivity.class));
                break;
            case  R.id.sell_cheats:
                startActivity(new Intent(getActivity(), SellCheatsActivity.class));
                break;

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        setAvator();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName);


    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
    }
}