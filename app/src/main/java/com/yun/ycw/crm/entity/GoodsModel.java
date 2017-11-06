package com.yun.ycw.crm.entity;

/**
 * Created by scb on 2016/3/3.
 */
public class GoodsModel {
    private int id;//商品ID
    private String name;//商品名字
    private int catid;//分类ID
    private int goodsid;//分类ID
    private int parentid;//根分类ID
    private String logurl;//图片地址
    private String specifications2;//规格
    private String brand;//品牌
    private String price;//价格
    private String unit;//价格

    public GoodsModel(String name, int catid, int goodsid, int parentid, String logurl, String specifications2, String brand, String price, String unit) {
        this.name = name;
        this.catid = catid;
        this.goodsid = goodsid;
        this.parentid = parentid;
        this.logurl = logurl;
        this.specifications2 = specifications2;
        this.brand = brand;
        this.price = price;
        this.unit = unit;
    }

    public GoodsModel(int id, String name, int catid, int goodsid, int parentid, String logurl, String specifications2, String brand, String price, String unit) {

        this(name, catid, goodsid, parentid, logurl, specifications2, brand, price, unit);
        this.id = id;
    }

    @Override
    public String toString() {
        return "GoodsModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", catid=" + catid +
                ", goodsid=" + goodsid +
                ", parentid=" + parentid +
                ", logurl='" + logurl + '\'' +
                ", specifications2='" + specifications2 + '\'' +
                ", brand='" + brand + '\'' +
                ", price='" + price + '\'' +
                ", unit='" + unit + '\'' +
                '}';
    }

    public int getGoodsid() {
        return goodsid;
    }

    public void setGoodsid(int goodsid) {
        this.goodsid = goodsid;
    }

    public GoodsModel(String logurl, int goodsid, int id) {
        this.logurl = logurl;
        this.goodsid = goodsid;
        this.id = id;
    }


    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCatid() {
        return catid;
    }

    public void setCatid(int catid) {
        this.catid = catid;
    }

    public int getParentid() {
        return parentid;
    }

    public void setParentid(int parentid) {
        this.parentid = parentid;
    }

    public String getLogurl() {
        return logurl;
    }

    public void setLogurl(String logurl) {
        this.logurl = logurl;
    }

    public String getSpecifications2() {
        return specifications2;
    }

    public void setSpecifications2(String specifications2) {
        this.specifications2 = specifications2;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
