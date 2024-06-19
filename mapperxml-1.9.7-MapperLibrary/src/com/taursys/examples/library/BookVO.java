/**
 * Example Mapper Application
 * by: Marty Phelan
 *
 * This example is free software; you can redistribute it and/or
 * modify it as you wish.  It is released to the public domain.
 *
 * This example is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package com.taursys.examples.library;

/**
 * A object representing a book.
 * @author Marty Phelan
 * @version 1.0
 */
public class BookVO {
  private String catalogNo;
  private String title;
  private String keywords;
  private java.util.Date dateAdded;
  private java.math.BigDecimal cost;
  private int locationId;

  public BookVO() {
  }
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