package com.yun.ycw.crm.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yun.ycw.crm.R;

/**
 * 用于显示自定义对话框的工具类
 * Created by wdyan on 2016/2/16.
 */
public class LoadingUtils {
    private static ImageView img;
    private static Dialog dialog;
    private static TextView text;

    public static Dialog createLoadingDialog(Context context, String msg) {
        View view = LayoutInflater.from(context).inflate(R.layout.customdialog, null);
        img = (ImageView) view.findViewById(R.id.img);
        text = (TextView) view.findViewById(R.id.txt);
        LinearLayout loading_layout = (LinearLayout) view.findViewById(R.id.loading_layout);
        LinearInterpolator lin = new LinearInterpolator();
        img.setBackgroundResource(R.drawable.frameanim);
        AnimationDrawable animationDrawable = (AnimationDrawable) img.getBackground();
        animationDrawable.start();

        text.setText(msg);
        dialog = new Dialog(context, R.style.loading_dialog);
        dialog.setContentView(loading_layout);
//        设置只能点返回键才能取消掉对话框
//        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}
