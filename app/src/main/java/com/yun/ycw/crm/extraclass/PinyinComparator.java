package com.yun.ycw.crm.extraclass;

import com.yun.ycw.crm.entity.Customer;

import java.util.Comparator;

/**
 * 比较拼音的顺序
 */
public class PinyinComparator implements Comparator<Customer> {

    public int compare(Customer o1, Customer o2) {
        if (o1.getSortLetters().equals("@")
                || o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#")
                || o2.getSortLetters().equals("@")) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }

}
