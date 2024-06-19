package com.taursys.examples.library;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class BookVO {

  public BookVO() {
  }
  private String catalogNo;
  private String title;
  private String keywords;
  private java.util.Date dateAdded;
  private java.math.BigDecimal cost;
  private int locationId;
  public String getCatalogNo() {
    return catalogNo;
  }
  public void setCatalogNo(String newCatalogNo) {
    catalogNo = newCatalogNo;
  }
  public void setTitle(String newTitle) {
    title = newTitle;
  }
  public String getTitle() {
    return title;
  }
  public void setKeywords(String newKeywords) {
    keywords = newKeywords;
  }
  public String getKeywords() {
    return keywords;
  }
  public void setDateAdded(java.util.Date newDateAdded) {
    dateAdded = newDateAdded;
  }
  public java.util.Date getDateAdded() {
    return dateAdded;
  }
  public void setCost(java.math.BigDecimal newCost) {
    cost = newCost;
  }
  public java.math.BigDecimal getCost() {
    return cost;
  }
  public void setLocationId(int newLocationId) {
    locationId = newLocationId;
  }
  public int getLocationId() {
    return locationId;
  }
}