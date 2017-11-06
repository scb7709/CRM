package com.yun.ycw.crm.entity;

import java.io.Serializable;

/**
 * Created by scb on 2016/2/25.
 */
public class Achievement implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;//当前用户
    private String userid;//当前用户id
    private String allorder;//总下单
    private String allmoney;//总金额
    private String allcommission;//总提成
    private String salesmanage;//下属销售主管数
    private String salesman;//下属销售人员
    private String allcustomers;//总客户数
    private String starttime;
    private String endtime;
    public String getAllorder() {
        return allorder;
    }

    public void setAllorder(String allorder) {
        this.allorder = allorder;
    }

    public String getAllmoney() {
        return allmoney;
    }

    public Achievement(String username, String userid, String allorder, String allmoney, String allcommission, String salesmanage, String salesman, String allcustomers) {
        this.username = username;
        this.userid = userid;
        this.allorder = allorder;
        this.allmoney = allmoney;
        this.allcommission = allcommission;
        this.salesmanage = salesmanage;
        this.salesman = salesman;
        this.allcustomers = allcustomers;
    }

    public void setAllmoney(String allmoney) {

        this.allmoney = allmoney;
    }

    public Achievement() {
    }

    public Achievement(String username, String userid, String starttime, String endtime) {
        this.username = username;
        this.userid = userid;
        this.starttime = starttime;
        this.endtime = endtime;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getAllcommission() {
        return allcommission;
    }

    public void setAllcommission(String allcommission) {
        this.allcommission = allcommission;
    }

    public String getAllcustomers() {
        return allcustomers;
    }

    public void setAllcustomers(String allcustomers) {
        this.allcustomers = allcustomers;
    }

    public String getSalesmanage() {
        return salesmanage;
    }

    public void setSalesmanage(String salesmanage) {
        this.salesmanage = salesmanage;
    }

    public String getSalesman() {
        return salesman;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setSalesman(String salesman) {
        this.salesman = salesman;
    }


}
