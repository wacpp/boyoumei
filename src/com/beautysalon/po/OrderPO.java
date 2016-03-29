package com.beautysalon.po;

import java.io.Serializable;

public class OrderPO implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 100010122001L;

  public OrderPO() {
    super();
  }

  private String orderId;
  private int sumPrice;
  private String createTime;
  private String serviceId;
  private String serviceName;
  private String userName;
  private String userContact;
  private int state;// 1 预约中, 2 已完成, 3 已关闭, 4 已退订

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public int getSumPrice() {
    return sumPrice;
  }

  public void setSumPrice(int sumPrice) {
    this.sumPrice = sumPrice;
  }

  public String getCreateTime() {
    return createTime;
  }

  public void setCreateTime(String createTime) {
    this.createTime = createTime;
  }

  public String getServiceId() {
    return serviceId;
  }

  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserContact() {
    return userContact;
  }

  public void setUserContact(String userContact) {
    this.userContact = userContact;
  }

  public int getState() {
    return state;
  }

  public void setState(int state) {
    this.state = state;
  }

  @Override
  public String toString() {
    return "orderId=" + orderId + "sumPrice=" + sumPrice + "createTime="
        + createTime + "serviceId=" + serviceId + "serviceName=" + serviceName
        + "userName=" + userName + "userContact=" + userContact + "state="
        + state;

  }
}
