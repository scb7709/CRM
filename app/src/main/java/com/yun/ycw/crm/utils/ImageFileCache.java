package com.yun.ycw.crm.utils;

import android.graphics.Bitmap;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by scb on 2016/3/8.
 */
public class ImageFileCache implements ImageLoader.ImageCache {

    @Override
    public Bitmap getBitmap(String url) {
        return ImageFileCacheUtils.getInstance().getImage(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        ImageFileCacheUtils.getInstance().saveBitmap(bitmap, url);
    }


}
