package com.yun.ycw.crm.comparator;

import com.yun.ycw.crm.entity.Customer;

import java.util.Comparator;

/**
 * Created by wdyan on 2016/3/8.
 */
public class OrderNumComparator implements Comparator<Customer> {
    private String info;
    private int result;

    public OrderNumComparator(String info) {
        this.info = info;
    }

    @Override
    public int compare(Customer lhs, Customer rhs) {
        switch (info) {
            case "ALL_ORDER_NUM":
                int lhs_order_num = Integer.parseInt(lhs.getAllorder_num());
                int rhs_order_num = Integer.parseInt(rhs.getAllorder_num());
                if (lhs_order_num < rhs_order_num) {
                    result = 1;
                } else if (lhs_order_num > rhs_order_num) {
                    result = -1;
                } else {
                    result = 0;
                }
                break;
            case "WAITING_ORDER_NUM":
                int lhs_waiting_num = Integer.parseInt(lhs.getWaitorder_num());
                int rhs_waiting_num = Integer.parseInt(rhs.getWaitorder_num());
                if (lhs_waiting_num < rhs_waiting_num) {
                    result = 1;
                } else if (lhs_waiting_num > rhs_waiting_num) {
                    result = -1;
                } else {
                    result = 0;
                }
                break;
            case "CUSTOMER_NAME":
                if (lhs.getSortLetters().equals("@")
                        || rhs.getSortLetters().equals("#")) {
                    result = -1;
                } else if (lhs.getSortLetters().equals("#")
                        || rhs.getSortLetters().equals("@")) {
                    result = 1;
                } else {
                    //按照拼音首字母排序
                    //return o1.getSortLetters().compareTo(o2.getSortLetters());
                    //按照全拼排序
                    result = lhs.getNamePinyin().compareTo(rhs.getNamePinyin());
                }
                break;
        }

        return result;
    }
}
