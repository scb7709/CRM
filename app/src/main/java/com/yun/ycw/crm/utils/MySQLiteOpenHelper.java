package com.yun.ycw.crm.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**

 * Created by scb on 2016/3/3.
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    public MySQLiteOpenHelper(Context context) {
        super(context, "goodsdata.db", null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table goods(id integer primary key autoincrement ," +
                "name varchar(100)," +
                "catid integer," +
                "parentid integer," +
                "logurl varchar(100)," +
                "specifications2 varchar(50)," +
                "brand varchar(50)," +
                "price varchar(10)," +
                "unit varchar(5)," +
                "goodsid integer)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
