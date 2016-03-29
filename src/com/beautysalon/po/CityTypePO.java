package com.beautysalon.po;

import java.io.Serializable;
import java.util.List;

public class CityTypePO implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 100010101001L;

  public CityTypePO() {
    super();

  }

  public String getCityId() {
    return cityId;
  }

  public void setCityId(String cityId) {
    this.cityId = cityId;
  }

  public String getCityName() {
    return CityName;
  }

  public void setCityName(String cityName) {
    CityName = cityName;
  }

  public List<String> getAreaId() {
    return areaId;
  }

  public void setAreaId(List<String> areaId) {
    this.areaId = areaId;
  }

  public List<String> getAreaName() {
    return areaName;
  }

  public void setAreaName(List<String> areaName) {
    this.areaName = areaName;
  }

  private String cityId;// cityid
  private String CityName;// cityname
  private List<String> areaId;// 区域id
  private List<String> areaName;// 区域Name

  public static long getSerialversionuid() {
    return serialVersionUID;
  }
}
