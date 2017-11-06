package com.yun.ycw.crm.entity;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;//员工工号
    private String is_superior;//是否是管理层
    private String superior_id; //上级ID
    private String phoneNumber;//电话号码
    private String loginPwd;//登录密码
    private String name;//用户名
    private String company;//公司
    private String position;//职位
    private String department;//部门
    private String team;//团队
    private String email;//邮箱
    private String icon;//用户头像
    private String token; //验证登录成功后返回的token 后续请求中作为header来使用
    private String level;//用于级别，one two three依次降低
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLoginPwd() {
        return loginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getIcon() {
        return icon;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }


    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public User(String id, String phoneNumber, String loginPwd, String name, String company, String position, String email, String icon, String token) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.loginPwd = loginPwd;
        this.name = name;
        this.company = company;
        this.position = position;
        this.email = email;
        this.icon = icon;
        this.token = token;
    }

    public User(String id, String phoneNumber, String loginPwd, String name, String company, String position, String email, String icon) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.loginPwd = loginPwd;
        this.name = name;
        this.company = company;
        this.position = position;
        this.email = email;
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "User{" +
                "accountNumber='" + id + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", loginPwd='" + loginPwd + '\'' +
                ", name='" + name + '\'' +
                ", company='" + company + '\'' +
                ", position='" + position + '\'' +
                ", email='" + email + '\'' +
                ", icon='" + icon + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

    public String getIs_superior() {
        return is_superior;
    }

    public void setIs_superior(String is_superior) {
        this.is_superior = is_superior;
    }

    public String getSuperior_id() {
        return superior_id;
    }

    public void setSuperior_id(String superior_id) {
        this.superior_id = superior_id;
    }

    public User() {
    }
}
