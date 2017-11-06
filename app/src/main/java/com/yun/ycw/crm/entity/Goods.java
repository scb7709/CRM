package com.yun.ycw.crm.entity;

import java.io.Serializable;

/**
 * Created by scb on 2016/2/1.
 */
public class Goods implements Serializable {
    private static final long serialVersionUID = 1L;
    private int catid;//类别
    private int isthird;//是否是第三方
    private String name;//名字
    private String price;//单价
    private String total;//数量
    private String specifications2;//规格
    private String logurl;//商品图标


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getCatid() {
        return catid;
    }

    public void setCatid(int catid) {
        this.catid = catid;
    }

    public int getIsthird() {
        return isthird;
    }

    public void setIsthird(int isthird) {
        this.isthird = isthird;
    }

    public String getSpecifications2() {
        return specifications2;
    }

    public void setSpecifications2(String specifications2) {
        this.specifications2 = specifications2;
    }

    public String getLogurl() {
        return logurl;
    }

    public void setLogurl(String logurl) {
        this.logurl = logurl;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public Goods() {
    }

    public Goods(int catid, int isthird, String name, String price, String total, String specifications2, String logurl) {
        this.catid = catid;
        this.isthird = isthird;
        this.name = name;
        this.price = price;
        this.total = total;
        this.specifications2 = specifications2;
        this.logurl = logurl;
    }
}
