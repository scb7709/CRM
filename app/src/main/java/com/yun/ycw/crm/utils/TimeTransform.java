package com.yun.ycw.crm.utils;

/**
 * Created by scb on 2016/5/4.
 */
public class TimeTransform {
    public static String getLastMouthTime(String time) {
        int year = Integer.parseInt(time.substring(0, 4));
        int mouth = Integer.parseInt(time.substring(6, 7));
        int day = Integer.parseInt(time.substring(8, 10));
        if (mouth > 1) {
            if (day > getMonthDay(year, mouth - 1)) {
                return year + "-" + (mouth - 1) + "-" + getMonthDay(year, mouth - 1);
            } else {
                return year + "-" + (mouth - 1) + "-" + day;
            }
        } else {
            year=year-1;
            return year + "-" + 12+ "-" + day;
        }

    }

    private static int getMonthDay(int year, int month) {
        int result = 0;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                result = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                result = 30;
                break;
            case 2:
                result = 28;
                if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) {
                    result = 29;
                }
                break;
        }

        return result;
    }
}
