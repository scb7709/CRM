package com.yun.ycw.crm.utils;

import java.math.BigDecimal;

/**
 * Created by scb on 2016/5/18.
 */
public class MyBigDecimal {
    public static String getBigDecimal(double num ){
        BigDecimal b = new BigDecimal(num);
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()+"";
    }
}
