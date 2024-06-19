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
 * A object representing a library location.
 * @author Marty Phelan
 * @version 1.0
 */
public class LocationVO {
  private int locationId;
  private String description;

  public LocationVO() {
  }
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