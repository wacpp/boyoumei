package com.beautysalon.po;

import java.io.Serializable;
import java.util.Date;

public class UserPO implements Serializable {
  public UserPO() {
    super();
  }

  private static final long serialVersionUID = -7060210544600464481L;
  private String userName;
  private String realName;
  private String passWord;
  private int sex;// 0.男性1.女性
  private Date brithDay;
  private String profession;
  private String phone;
  private String address;
  private int type;// 0店家1普通用户
  private String email;// 用于商家端的注册

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getRealName() {
    return realName;
  }

  public void setRealName(String realName) {
    this.realName = realName;
  }

  public String getPassWord() {
    return passWord;
  }

  public void setPassWord(String passWord) {
    this.passWord = passWord;
  }

  public int getSex() {
    return sex;
  }

  public void setSex(int sex) {
    this.sex = sex;
  }

  public Date getBrithDay() {
    return brithDay;
  }

  public void setBrithDay(Date brithDay) {
    this.brithDay = brithDay;
  }

  public String getProfession() {
    return profession;
  }

  public void setProfession(String profession) {
    this.profession = profession;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
