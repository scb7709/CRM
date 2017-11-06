package com.yun.ycw.crm.entity;

import java.io.Serializable;

/**
 * 管理者对象
 * Created by wdyan on 2016/2/24.
 */
public class Leader implements Serializable {
    private String id;//id
    private String name;//姓名
    private String undoneNum;//待回款单数
    private String undoneMoney;//待回款金额
    private String level;//人员的级别

    public Leader() {
    }


    public Leader(String name, String undoneNum, String undoneMoney) {
        this.name = name;
        this.undoneNum = undoneNum;
        this.undoneMoney = undoneMoney;
    }

    public Leader(String name, String undoneNum, String undoneMoney, String level) {
        this.name = name;
        this.undoneNum = undoneNum;
        this.undoneMoney = undoneMoney;
        this.level = level;
    }

    public Leader(String id, String name, String undoneNum, String undoneMoney, String level) {
        this.id = id;
        this.name = name;
        this.undoneNum = undoneNum;
        this.undoneMoney = undoneMoney;
        this.level = level;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getLevel() {
        return level;
    }
    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUndoneNum() {
        return undoneNum;
    }

    public void setUndoneNum(String undoneNum) {
        this.undoneNum = undoneNum;
    }

    public String getUndoneMoney() {
        return undoneMoney;
    }

    public void setUndoneMoney(String undoneMoney) {
        this.undoneMoney = undoneMoney;
    }
}
