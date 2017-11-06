package com.yun.ycw.crm.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.squareup.picasso.Picasso;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.activity.CheckInListActivity;
import com.yun.ycw.crm.customview.CircleImageView;
import com.yun.ycw.crm.customview.RoundImageView;
import com.yun.ycw.crm.entity.CheckIn;

import java.util.List;

/**
 * 签到列表的adapter
 * Created by wdyan on 2016/3/28.
 */
public class CheckInAdapter extends BaseAdapter {
    private Context context;
    private List<CheckIn> list;
    private BitmapUtils bitmapUtils;
    private LinearLayout layout;
    private ImageView tempImg;

    public CheckInAdapter() {
    }

    public CheckInAdapter(Context context, List<CheckIn> list, BitmapUtils bitmapUtils, LinearLayout layout, ImageView tempImg) {
        this.context = context;
        this.list = list;
        this.bitmapUtils = bitmapUtils;
        this.layout = layout;
        this.tempImg = tempImg;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        CheckInViewHolder checkInViewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_checkin_list, null);
            checkInViewHolder = new CheckInViewHolder();
            checkInViewHolder.vAvatarImg = (CircleImageView) convertView.findViewById(R.id.item_checkIn_avatar);
            checkInViewHolder.vName = (TextView) convertView.findViewById(R.id.item_checkIn_name);
            checkInViewHolder.vTime = (TextView) convertView.findViewById(R.id.item_checkIn_time);
            checkInViewHolder.vLocation = (TextView) convertView.findViewById(R.id.item_checkIn_location);
            checkInViewHolder.vContent = (TextView) convertView.findViewById(R.id.item_checkIn_content);
            checkInViewHolder.vCamImg = (ImageView) convertView.findViewById(R.id.item_checkIn_cam);
            convertView.setTag(checkInViewHolder);
        } else
            checkInViewHolder = (CheckInViewHolder) convertView.getTag();
        checkInViewHolder.vName.setText(list.get(position).getName());
        checkInViewHolder.vTime.setText(list.get(position).getTime());
        checkInViewHolder.vLocation.setText(list.get(position).getLocation());
        if (list.get(position).getContent().equals("")) {
            checkInViewHolder.vContent.setVisibility(View.GONE);
        } else
            checkInViewHolder.vContent.setText(list.get(position).getContent());


        //签到图片
        bitmapUtils.display(checkInViewHolder.vCamImg, list.get(position).getCamUrl(), new BitmapLoadCallBack<ImageView>() {
            @Override
            public void onLoadCompleted(ImageView imageView, String s, final Bitmap bp, BitmapDisplayConfig bitmapDisplayConfig, BitmapLoadFrom bitmapLoadFrom) {
                Log.i("vCamImg", list.get(position).getCamUrl());
                imageView.setImageBitmap(bp);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layout.setVisibility(View.VISIBLE);
                        int height = bp.getHeight();
                        int width = bp.getWidth();
                        Log.i("height-width", height + "  " + width);//256*144
                        if (height > width) {
                            tempImg.setPadding(60, 30, 60, 30);
                        } else
                            tempImg.setPadding(60, 300, 60, 300);
                        tempImg.setImageBitmap(bp);
                        tempImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                layout.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }

            @Override
            public void onLoadFailed(ImageView imageView, String s, Drawable drawable) {
                imageView.setImageResource(R.mipmap.yuncaiwang);
            }
        });
        //头像图片
        bitmapUtils.display(checkInViewHolder.vAvatarImg, list.get(position).getAvatarUrl(), new BitmapLoadCallBack<ImageView>() {
            @Override
            public void onLoadCompleted(ImageView imageView, String s, Bitmap bitmap, BitmapDisplayConfig bitmapDisplayConfig, BitmapLoadFrom bitmapLoadFrom) {
                imageView.setImageBitmap(bitmap);

            }

            @Override
            public void onLoadFailed(ImageView imageView, String s, Drawable drawable) {
                imageView.setImageResource(R.mipmap.checkin_default);
                Log.i("avatar", list.get(position).getAvatarUrl());
            }
        });

        return convertView;
    }
}

class CheckInViewHolder {
    CircleImageView vAvatarImg;
    TextView vName;
    TextView vTime;
    TextView vLocation;
    TextView vContent;
    ImageView vCamImg;
}

