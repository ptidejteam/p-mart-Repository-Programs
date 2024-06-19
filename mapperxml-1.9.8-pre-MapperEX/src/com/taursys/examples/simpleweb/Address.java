package com.taursys.examples.simpleweb;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class Address {

  public Address() {
  }
  private String addressLine1;
  private String addressLine2;
  private String city;
  private String stateProvince;
  private String zipCode;
  private String country;
  public String getAddressLine1() {
    return addressLine1;
  }
  public void setAddressLine1(String newAddressLine1) {
    addressLine1 = newAddressLine1;
  }
  public void setAddressLine2(String newAddressLine2) {
    addressLine2 = newAddressLine2;
  }
  public String getAddressLine2() {
    return addressLine2;
  }
  public void setCity(String newCity) {
    city = newCity;
  }
  public String getCity() {
    return city;
  }
  public void setStateProvince(String newStateProvince) {
    stateProvince = newStateProvince;
  }
  public String getStateProvince() {
    return stateProvince;
  }
  public void setZipCode(String newZipCode) {
    zipCode = newZipCode;
  }
  public String getZipCode() {
    return zipCode;
  }
  public void setCountry(String newCountry) {
    country = newCountry;
  }
  public String getCountry() {
    return country;
  }
}