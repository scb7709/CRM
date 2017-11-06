package com.yun.ycw.crm.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.yun.ycw.crm.activity.OfflineMallActivity;
import com.yun.ycw.crm.entity.GoodsModel;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by scb on 2016/3/3.
 */
public class MySQLiteDataDao {

    private static volatile MySQLiteDataDao sInstance = null;
    private MySQLiteOpenHelper helper;
    private String queryString = "";
private Context context;
    private MySQLiteDataDao(Context ct) {

        this.context=ct;
        helper = new MySQLiteOpenHelper(ct);
    }

    public static MySQLiteDataDao getInstance(Context ct) {
        if (sInstance == null) {
            synchronized (MySQLiteDataDao.class) {
                if (sInstance == null) {
                    sInstance = new MySQLiteDataDao(ct);
                }
            }
        }
        return sInstance;
    }

    public boolean insertBySql(List<GoodsModel> list, final Activity context, final Dialog dialog) {
        if (list.size() <= 0) {
            return false;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            String sql = "insert into goods (name,catid,parentid,logurl,specifications2,brand,price,unit,goodsid) values (?,?,?,?,?,?,?,?,?)";
            SQLiteStatement stat = db.compileStatement(sql);
            db.beginTransaction();
            for (GoodsModel goodsModel : list) {
                stat.bindString(1, goodsModel.getName());
                stat.bindLong(2, goodsModel.getCatid());
                stat.bindLong(3, goodsModel.getParentid());
                stat.bindString(4, goodsModel.getLogurl());
                stat.bindString(5, goodsModel.getSpecifications2());
                stat.bindString(6, goodsModel.getBrand());
                stat.bindString(7, goodsModel.getPrice());
                stat.bindString(8, goodsModel.getUnit());
                stat.bindLong(9, goodsModel.getGoodsid());
                long result = stat.executeInsert();
                if (result < 0) {
                    context.startActivity(new Intent(context, OfflineMallActivity.class));
                    dialog.dismiss();
                    context.finish();
                    return false;
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (null != db) {
                    db.endTransaction();
                    db.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        context.startActivity(new Intent(context, OfflineMallActivity.class));
        dialog.dismiss();
        context.finish();
        return true;
    }

    public void delete() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from goods");
        db.close();
    }

    public boolean tabIsExist(String tabName) {
        boolean result = false;
        if (tabName == null) {
            return false;
        }
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = helper.getReadableDatabase();//
            cursor = db.rawQuery("select * from goods", null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }

        } catch (Exception e) {
        }
        db.close();
        return result;
    }


    public List<String> queryBrand(int parentid, int catid) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from goods where parentid=" + parentid + " and catid=" + catid, null);
        List<String> brand = new ArrayList<>();
        while (cursor.moveToNext()) {
            String brandd = cursor.getString(6);
            if (!brand.contains(brandd) && !brandd.contains(";") && !brandd.equals("")) {
                brand.add(brandd);
            }
        }
        cursor.close();
        db.close();
        return brand;
    }

    public List<String> queryCatgory(int parentid) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select catid from goods where parentid=" + parentid, null);
        List<String> brand = new ArrayList<>();
        while (cursor.moveToNext()) {
            String catid = cursor.getInt(0) + "";
            if (!brand.contains(catid)) {
                brand.add(catid);
            }
        }
        List<String> cat = new ArrayList<>();
        for (String s : brand) {
            cat.add(getCatgory(Integer.parseInt(s)));
        }
        Log.i("分类" + parentid + ":", cat.size() + "AAAA" + cat.toString());
        cursor.close();
        db.close();
        return cat;
    }

    private String getCatgory(int j) {
        try {
            String s = GoodsCatgoryUtils.getCatgory();
            JSONArray array = new JSONArray(s);
            for (int i = 0; i < array.length(); i++) {
                JSONObject arrobj = array.getJSONObject(i);
                if (j == arrobj.getInt("id")) {
                    return arrobj.getString("name");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return " ";
    }

    public List<String> queryBrand() {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from goods", null);
        List<String> brand = new ArrayList<>();
        while (cursor.moveToNext()) {
            String brandd = cursor.getString(6);
            if (brandd.contains(";")) {
                brand.add(brandd);
            }
        }
        cursor.close();
        db.close();
        return brand;
    }

    public ArrayList<GoodsModel> queryAll(int parentid, int catid) {
        queryString = "select * from goods where parentid=" + parentid + " and catid=" + catid;
        return qurey(queryString);
    }

    public ArrayList<GoodsModel> queryAll(int parentid, int catid, String brand) {
        queryString = "select * from goods where parentid=" + parentid + " and catid=" + catid + " and brand like '%" + brand + "%'";
        return qurey(queryString);
    }

    public ArrayList<GoodsModel> queryAll(int parentid) {
        queryString = "select * from goods where parentid=" + parentid;
        return qurey(queryString);
    }

    public ArrayList<GoodsModel> queryName(String name) {
        queryString = "select * from goods where name like '%" + name + "%'";
        return qurey(queryString);
    }

    private ArrayList<GoodsModel> qurey(String queryString) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        db = helper.getWritableDatabase();
        ArrayList<GoodsModel> list = new ArrayList<GoodsModel>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String namee = cursor.getString(1);
            int catidd = cursor.getInt(2);
            int parentidd = cursor.getInt(3);
            String logurl = cursor.getString(4);
            String specifications2 = cursor.getString(5);
            String brandd = cursor.getString(6);
            String price = cursor.getString(7);
            String unit = cursor.getString(8);
            int goodsid = cursor.getInt(9);
            list.add(new GoodsModel(id, namee, catidd, goodsid, parentidd, logurl, specifications2, brandd, price, unit));
        }
        cursor.close();
        db.close();
        return list;
    }


}
