package com.beautysalon.po;

import java.io.Serializable;
import java.util.List;

public class ShopInfoPO implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 100010101001L;

  public ShopInfoPO() {
    super();
  }

  private String shopId;

  public String getShopId() {
    return shopId;
  }

  public void setShopId(String shopId) {
    this.shopId = shopId;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public String getTelephone() {
    return telephone;
  }

  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  public String getDec() {
    return dec;
  }

  public void setDec(String dec) {
    this.dec = dec;
  }

  public List<String> getImageName() {
    return imageName;
  }

  public void setImageName(List<String> imageName) {
    this.imageName = imageName;
  }

  public List<String> getIamgeUrl() {
    return iamgeUrl;
  }

  public void setIamgeUrl(List<String> iamgeUrl) {
    this.iamgeUrl = iamgeUrl;
  }

  public List<String> getIamgeDec() {
    return iamgeDec;
  }

  public void setIamgeDec(List<String> iamgeDec) {
    this.iamgeDec = iamgeDec;
  }

  @Override
  public String toString() {
    return "ShopInfoPO [toString()=" + "shopName=" + shopName + "city=" + city
        + "area=" + "telephone=" + telephone + "dec=" + dec;
  }

  public String getBussinessTime() {
    return bussinessTime;
  }

  public void setBussinessTime(String bussinessTime) {
    this.bussinessTime = bussinessTime;
  }

  private String shopName;
  private String city;
  private String area;
  private String telephone;
  private String dec;
  private String bussinessTime;
  private List<String> imageName;
  private List<String> iamgeUrl;
  private List<String> iamgeDec;
}
