package com.yun.ycw.crm.comparator;

import android.content.Context;

import com.yun.ycw.crm.entity.UndoneOrder;
import com.yun.ycw.crm.utils.SharedUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by wdyan on 2016/3/9.
 */
public class UndoneOrderComparator implements Comparator<UndoneOrder> {

    private String info;
    private int result;
    private Context context;
    private boolean flagTime,flagCommission;

    public UndoneOrderComparator(String info, Context context, boolean flagTime, boolean flagCommission) {
        this.info = info;
        this.context = context;
        this.flagTime = flagTime;
        this.flagCommission = flagCommission;
    }

    public UndoneOrderComparator(String info) {
        this.info = info;
    }

    @Override
    public int compare(UndoneOrder lhs, UndoneOrder rhs) {
        switch (info) {
            case "UNDONE_TIME":
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String originTime_lhs = lhs.getOriginTime();
                String originTime_rhs = rhs.getOriginTime();
                try {
                    Date date_lhs = sdf.parse(originTime_lhs);
                    Date date_rhs = sdf.parse(originTime_rhs);
                    if (date_lhs.before(date_rhs)) {
                        if (!flagTime)
                            result = 1;
                        else
                            result = -1;
                    } else if (date_lhs.after(date_rhs)) {
                        if (!flagTime)
                            result = -1;
                        else
                            result = -1;
                    } else
                        result = 0;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case "UNDONE_MONEY":
                double price_lhs = Double.parseDouble(lhs.getTotal_price());
                double price_rhs = Double.parseDouble(rhs.getTotal_price());
                if (price_lhs > price_rhs) {
                    result = -1;
                } else if (price_lhs < price_rhs) {
                    result = 1;
                } else
                    result = 0;
                break;
            case "UNDONE_COMMISSION":
                double commssion1 = 0;
                double commssion2 = 0;
                if (SharedUtils.getUser("user", context).getLevel().equals("three")) {
                    commssion1 = Double.parseDouble(lhs.getCommission());
                    commssion2 = Double.parseDouble(rhs.getCommission());
                } else {
                    if (flagCommission) {
                        commssion1 = Double.parseDouble(lhs.getCommission().substring(0, lhs.getCommission().indexOf("/")));
                        commssion2 = Double.parseDouble(rhs.getCommission().substring(0, rhs.getCommission().indexOf("/")));
                    } else {
                        commssion1 = Double.parseDouble(lhs.getCommission().substring(lhs.getCommission().indexOf("/") + 1, lhs.getCommission().length()));
                        commssion2 = Double.parseDouble(rhs.getCommission().substring(rhs.getCommission().indexOf("/") + 1, rhs.getCommission().length()));
                    }
                }
                if (commssion1 > commssion2) {
                    result = -1;
                } else if (commssion1 < commssion2) {
                    result = 1;
                } else
                    result = 0;
                break;

            case "UNDONE_NAME":
                if (lhs.getSortLetters().equals("@")
                        || rhs.getSortLetters().equals("#")) {
                    result = -1;
                } else if (lhs.getSortLetters().equals("#")
                        || rhs.getSortLetters().equals("@")) {
                    result = 1;
                } else {
                    //按照全拼排序
                    result = lhs.getNamePinyin().compareTo(rhs.getNamePinyin());
                }
                break;
            default:
                break;
        }
        return result;
    }
}
