package com.yun.ycw.crm.entity;

import java.io.Serializable;

/**
 * Created by wdyan on 2016/3/25.
 */
public class Schedule implements Serializable {
    private String id;//计划id
    private String week;//计划时间段
    private String user_id;//计划创建者id
    private String superior_id;//计划创建者的上级id
    private String achievement;//计划目标业绩
    private String join_headman;//计划开发工长数
    private String visit_headman;//计划回访工长数
    private String remarks;//计划的备注
    private String startTime;//计划的开始时间
    private String endTime;//计划的结束时间

    public Schedule() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSuperior_id() {
        return superior_id;
    }

    public void setSuperior_id(String superior_id) {
        this.superior_id = superior_id;
    }

    public String getAchievement() {
        return achievement;
    }

    public void setAchievement(String achievement) {
        this.achievement = achievement;
    }

    public String getJoin_headman() {
        return join_headman;
    }

    public void setJoin_headman(String join_headman) {
        this.join_headman = join_headman;
    }

    public String getVisit_headman() {
        return visit_headman;
    }

    public void setVisit_headman(String visit_headman) {
        this.visit_headman = visit_headman;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
