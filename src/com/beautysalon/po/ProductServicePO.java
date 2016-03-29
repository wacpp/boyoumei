package com.beautysalon.po;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.beautysalon.spiner.widget.CustemObject;

public class ProductServicePO extends CustemObject implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 100010101001L;

  public ProductServicePO() {
    super();
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public int getPrice() {
    return price;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  public String getServiceDec() {
    return serviceDec;
  }

  public void setServiceDec(String serviceDec) {
    this.serviceDec = serviceDec;
  }

  public String getProductId() {
    return productId;
  }

  public void setProductId(String produceId) {
    this.productId = produceId;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public List<String> getTypeId() {
    return typeId;
  }

  public void setTypeId(List<String> typeId) {
    this.typeId = typeId;
  }

  public List<String> getTypeName() {
    return typeName;
  }

  public void setTypeName(List<String> typeName) {
    this.typeName = typeName;
  }

  public String getCreateDateTime() {
    return createDateTime;
  }

  public void setCreateDateTime(String createDateTime) {
    this.createDateTime = createDateTime;
  }

  public String getFile() {
    return file;
  }

  public void setFile(String file) {
    this.file = file;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public String getServiceId() {
    return serviceId;
  }

  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }

  private String shopName;

  private String serviceId;

  private String serviceName = "";
  private int price;
  private String serviceDec = "";
  private String productId = "";// 大类
  private String productName = "";// 大类名称
  private List<String> typeId = new ArrayList<String>();// 小类id
  private List<String> typeName = new ArrayList<String>();// 小类name
  private String createDateTime;
  private String file = "";// 服务相关图片

  @Override
  public String toString() {
    return "shopName=" + shopName + "serviceId=" + serviceId + "servicename="
        + serviceName + "price=" + price + "serviceDec=" + serviceDec
        + "productName=" + productName + "productId=" + productId + "typeName="
        + typeName.toString();
  }
}
