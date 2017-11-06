package com.yun.ycw.crm.entity;

import java.io.Serializable;

/**
 * 客户类
 * Created by wdyan on 2016/1/30.
 */
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;//客户ID
    private int source;//来源
    private String customer_name;//名字或工长名
    private String phonenumber;//电话
    private String birthplace;//籍贯
    private String sex;//性别
    private String level;//级别
    private String company;//公司
    private String firstrecord;//首次拜访记录
    private String firsttime;//首次拜访时间

    private String notes;//备注

    private String company_icon;//头像
    private String waitorder_num;//待回款订单数
    private String allorder_num;//总订单数
    private String all_money;//总金额
    private String wait_money;//待回款金额

    private String sortLetters; // 显示数据拼音的首字母
    private String namePinyin;//名字的拼音
    private boolean isChoice;//复选框是否被选中
    public Customer() {
    }

    public Customer(String customer_name, String company_icon, String waitorder_num) {
        this.customer_name = customer_name;
        this.company_icon = company_icon;
        this.waitorder_num = waitorder_num;
    }
    //新增客户
    public Customer(int source, String phonenumber, String birthplace, String company, String firsttime, String firstrecord, String notes, String customer_name) {
        this.source = source;
        this.phonenumber = phonenumber;
        this.birthplace = birthplace;
        this.company = company;
        this.firsttime = firsttime;
        this.firstrecord = firstrecord;
        this.notes = notes;
        this.customer_name = customer_name;
    }
    //客户订单详情
    public Customer(String customer_name, String waitorder_num, String allorder_num, String all_money, String wait_money) {
        this.customer_name = customer_name;
        this.waitorder_num = waitorder_num;
        this.allorder_num = allorder_num;
        this.all_money = all_money;
        this.wait_money = wait_money;
    }

    public boolean isChoice() {
        return isChoice;
    }

    public void setIsChoice(boolean isChoice) {
        this.isChoice = isChoice;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNamePinyin() {
        return namePinyin;
    }

    public void setNamePinyin(String namePinyin) {
        this.namePinyin = namePinyin;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCompany_icon() {
        return company_icon;
    }

    public void setCompany_icon(String company_icon) {
        this.company_icon = company_icon;
    }

    public String getTotal_num() {
        return waitorder_num;
    }

    public void setTotal_num(String waitorder_num) {
        this.waitorder_num = waitorder_num;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public void setBirthplace(String birthplace) {
        this.birthplace = birthplace;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getFirsttime() {
        return firsttime;
    }

    public void setFirsttime(String firsttime) {
        this.firsttime = firsttime;
    }

    public String getFirstrecord() {
        return firstrecord;
    }

    public void setFirstrecord(String firstrecord) {
        this.firstrecord = firstrecord;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getWaitorder_num() {
        return waitorder_num;
    }

    public void setWaitorder_num(String waitorder_num) {
        this.waitorder_num = waitorder_num;
    }

    public String getAllorder_num() {
        return allorder_num;
    }

    public void setAllorder_num(String allorder_num) {
        this.allorder_num = allorder_num;
    }

    public String getAll_money() {
        return all_money;
    }

    public void setAll_money(String all_money) {
        this.all_money = all_money;
    }

    public String getWait_money() {
        return wait_money;
    }

    public void setWait_money(String wait_money) {
        this.wait_money = wait_money;
    }
}
