package com.yun.ycw.crm.entity;

import java.io.Serializable;

/**
 * 待回款订单 的实体类
 * Created by wdyan on 2016/1/30.
 */
public class UndoneOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;//订单ID
    private String laststep;//订单状态
    private String voucher;//签收凭证
    private String orderid;//订单号
    private String time;//下单时间
    private String address;//收货地址
    private String truename;//收货人
    private String tel;//收货电话
    private String custonmer;//下单客户
    private String customerphone;//下单客户电话
    private String paytype;//支付方式
    private int paid;//支付状态（1支付，0未支付 ）
    private int delivery_type;//配送方式（1云材配送，2自提  ）
    private String deliver_time;//配送时间
    private String delivery;//运费
    private String remote_price;//远程费
    private String upstairsfee;//上楼费
    private String total_price;//订单总额
    private String giftprice;//优惠金额
    private String payprice;//支付金额
    private String commission;//订单提成
    private String sortLetters; // 显示数据拼音的首字母
    private String com_for_leader;//销售主管能看的提成
    private String namePinyin;//全拼
    private String originTime;//json数据中的原始时间

    public String getCustonmer() {
        return custonmer;
    }

    public void setCustonmer(String custonmer) {
        this.custonmer = custonmer;
    }

    public String getCustomerphone() {
        return customerphone;
    }

    public void setCustomerphone(String customerphone) {
        this.customerphone = customerphone;
    }

    public UndoneOrder(String time,String laststep, String total_price, String commission) {
        this.time = time;
        this.laststep = laststep;
        this.total_price = total_price;
        this.commission = commission;
    }
//    public UndoneOrder(String time, String orderid, String laststep, String total_price, String commission, String com_for_leader) {
//        this.orderid = orderid;
//        this.time = time;
//        this.laststep = laststep;
//        this.total_price = total_price;
//        this.commission = commission;
//        this.com_for_leader = com_for_leader;
//    }

    public String getVoucher() {
        return voucher;
    }

    public void setVoucher(String voucher) {
        this.voucher = voucher;
    }

    public UndoneOrder(String truename, String time, String laststep, String total_price, String commission) {
        this.truename = truename;
        this.time = time;
        this.laststep = laststep;
        this.total_price = total_price;
        this.commission = commission;
    }

    public String getOriginTime() {
        return originTime;
    }

    public void setOriginTime(String originTime) {
        this.originTime = originTime;
    }

    public String getNamePinyin() {
        return namePinyin;
    }

    public void setNamePinyin(String namePinyin) {
        this.namePinyin = namePinyin;
    }

    public String getRemote_price() {
        return remote_price;
    }

    public void setRemote_price(String remote_price) {
        this.remote_price = remote_price;
    }

    public UndoneOrder() {

    }

    public String getCom_for_leader() {
        return com_for_leader;
    }

    public void setCom_for_leader(String com_for_leader) {
        this.com_for_leader = com_for_leader;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public String getDelivery() {
        return delivery;
    }

    public String getUpstairsfee() {
        return upstairsfee;
    }

    public void setUpstairsfee(String upstairsfee) {
        this.upstairsfee = upstairsfee;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLaststep() {
        return laststep;
    }

    public void setLaststep(String laststep) {
        this.laststep = laststep;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTruename() {
        return truename;
    }

    public void setTruename(String truename) {
        this.truename = truename;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPaytype() {
        return paytype;
    }

    public void setPaytype(String paytype) {
        this.paytype = paytype;
    }

    public int getPaid() {
        return paid;
    }

    public void setPaid(int paid) {
        this.paid = paid;
    }

    public int getDelivery_type() {
        return delivery_type;
    }

    public void setDelivery_type(int delivery_type) {
        this.delivery_type = delivery_type;
    }

    public String getDeliver_time() {
        return deliver_time;
    }

    public void setDeliver_time(String deliver_time) {
        this.deliver_time = deliver_time;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getGiftprice() {
        return giftprice;
    }

    public void setGiftprice(String giftprice) {
        this.giftprice = giftprice;
    }

    public String getPayprice() {
        return payprice;
    }

    public void setPayprice(String payprice) {
        this.payprice = payprice;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }


}
