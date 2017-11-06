package com.yun.ycw.crm.entity;

import android.widget.ImageView;

import java.io.Serializable;

/**
 * 签到的实体类
 * Created by wdyan on 2016/3/28.
 */
public class CheckIn implements Serializable {
    private String avatarUrl;//签到人头像的uri
    private String name;//签到人姓名
    private String time;//签到时间
    private String location;//签到地点
    private String content;//签到备注
    private String camUrl;//签到时拍照url

    public CheckIn() {
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCamUrl() {
        return camUrl;
    }

    public void setCamUrl(String camUrl) {
        this.camUrl = camUrl;
    }
}
