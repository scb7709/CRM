package com.yun.ycw.crm.comparator;

import com.yun.ycw.crm.entity.User;

import java.util.Comparator;

/**
 * 对User按照id排序,用于签到列表上方的spinner下拉列表
 * Created by wdyan on 2016/4/7.
 */
public class IdComparator implements Comparator<User> {

    @Override
    public int compare(User u1, User u2) {
        return Integer.parseInt(u1.getId()) - Integer.parseInt(u2.getId());
    }
}
