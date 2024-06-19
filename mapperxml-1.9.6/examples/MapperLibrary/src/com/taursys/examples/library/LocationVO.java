package com.taursys.examples.library;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class LocationVO {

  public LocationVO() {
  }
  private int locationId;
  private String description;
  public int getLocationId() {
    return locationId;
  }
  public void setLocationId(int newLocationId) {
    locationId = newLocationId;
  }
  public void setDescription(String newDescription) {
    description = newDescription;
  }
  public String getDescription() {
    return description;
  }
}