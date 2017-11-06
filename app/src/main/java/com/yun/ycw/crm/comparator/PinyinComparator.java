package com.yun.ycw.crm.comparator;

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
            //按照拼音首字母排序
            //return o1.getSortLetters().compareTo(o2.getSortLetters());
            //按照全拼排序
            return o1.getNamePinyin().compareTo(o2.getNamePinyin());
        }
    }

}
