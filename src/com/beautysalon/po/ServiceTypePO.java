package com.beautysalon.po;

import java.io.Serializable;
import java.util.List;

public class ServiceTypePO implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 100010101001L;

  public ServiceTypePO() {
    super();

  }

  private String productId;// 小类Id
  private String productName;// 小类Name
  private List<String> prodTypeId;// 大类Id
  private List<String> prodTypeName;// 大类Name

  public String getproductId() {
    return productId;
  }

  public void setproductId(String productId) {
    this.productId = productId;
  }

  public String  getproductName() {
    return productName;
  }

  public void setproductName(String productName) {
    this.productName = productName;
  }

  public List<String> getProdTypeId() {
    return prodTypeId;
  }

  public void setProdTypeId(List<String> prodTypeId) {
    this.prodTypeId = prodTypeId;
  }

  public List<String> getProdTypeName() {
    return prodTypeName;
  }

  public void setProdTypeName(List<String> prodTypeName) {
    this.prodTypeName = prodTypeName;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }
}
