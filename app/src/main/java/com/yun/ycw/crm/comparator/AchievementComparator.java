package com.yun.ycw.crm.comparator;

import android.content.Context;

import com.yun.ycw.crm.entity.Achievement;
import com.yun.ycw.crm.entity.UndoneOrder;
import com.yun.ycw.crm.utils.SharedUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by wdyan on 2016/3/9.
 */
public class AchievementComparator implements Comparator<Achievement> {

    private String info;
    private int result;
    public AchievementComparator(String info) {
        this.info = info;
    }

    @Override
    public int compare(Achievement lhs, Achievement rhs) {

        switch (info) {
            case "ORDER":
                double order1 = Double.parseDouble(lhs.getAllorder());
                double order2 = Double.parseDouble(rhs.getAllorder());
                if (order1 > order2) {
                    result = -1;
                } else if (order1 < order2) {
                    result = 1;
                } else
                    result = 0;
                break;

            case "CUSTOMER":
                int customer1 = Integer.parseInt(lhs.getAllcustomers());
                int customer2 = Integer.parseInt(rhs.getAllcustomers());
                if (customer1 > customer2) {
                    result = -1;
                } else if (customer1 < customer2) {
                    result = 1;
                } else
                    result = 0;
                break;
            case "MONEY":
                double money1 = Double.parseDouble(lhs.getAllmoney());
                double money2 = Double.parseDouble(rhs.getAllmoney());
                if (money1 > money2) {
                    result = -1;
                } else if (money1 < money2) {
                    result = 1;
                } else
                    result = 0;
                break;
            case "COMMISSION":
                double commission1 = Double.parseDouble(lhs.getAllcommission());
                double commission2 = Double.parseDouble(rhs.getAllcommission());
                if (commission1 > commission2) {
                    result = -1;
                } else if (commission1 < commission2) {
                    result = 1;
                } else
                    result = 0;
                break;



        }
        return result;
    }
}
