package com.yun.ycw.crm.comparator;


import com.yun.ycw.crm.entity.UndoneOrder;

import java.util.Comparator;

/**
 * 用于回款订单
 * Created by wdyan on 2016/2/29.
 */
public class PinyinOrderComparator implements Comparator<UndoneOrder> {
    public int compare(UndoneOrder o1, UndoneOrder o2) {
        if (o1.getSortLetters().equals("@")
                || o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#")
                || o2.getSortLetters().equals("@")) {
            return 1;
        } else {
            return o1.getNamePinyin().compareTo(o2.getNamePinyin());
        }
    }
}
