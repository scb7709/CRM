package com.yun.ycw.crm.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by scb
 */
public class DateCheckUtils {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
    private static SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    public static int checkDate(String mFirsttime) {
        //某一时间转换为毫秒数

        //SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        long cTime = System.currentTimeMillis();
        try {
            Date d = simpleDateFormat.parse(mFirsttime);
//Date d = simpleDateFormat1.parse("2015-12-25 9:00");
            if (d.getTime() > cTime) {
                return 0;//选择的时间大于当前时间
            } else if (d.getTime() <= cTime) {
                return 1;//选择的时间小于等于当前时间
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 2;
    }

    public static int checkDate2(String mFirsttime) {
        //某一时间转换为毫秒数

        //SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        long cTime = System.currentTimeMillis();
        try {
            Date d = simpleDateFormat2.parse(mFirsttime);
//Date d = simpleDateFormat1.parse("2015-12-25 9:00");
            if (d.getTime() > cTime) {
                return 0;//选择的时间大于当前时间
            } else if (d.getTime() <= cTime) {
                return 1;//选择的时间小于等于当前时间
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 2;
    }

    public static int isEndAfterStartTime(String startTime, String endTime) {
        try {
            Date d = simpleDateFormat2.parse(startTime);
            Date date = simpleDateFormat2.parse(endTime);
            if (d.getTime() > date.getTime()) {
                return 0;//如果结束时间小于开始时间 返回0
            } else if (d.getTime() < date.getTime()) {
                return 1;//如果结束时间大于开始时间 返回1
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 2;
    }

}
