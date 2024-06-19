package com.taursys.examples.simpleweb;

import java.io.Serializable;

/**
 * Value Object
 * @author Marty Phelan
 * @version 1.0
 */
public class WarehouseVO implements Serializable {
  private int warehouseID;
  private String description;

  /**
   * Constructs a new WarehouseVO with default values.
   */
  public WarehouseVO() {
  }

  /**
   * Constructs a new WarehouseVO with given values.
   * @param warehouseID the unique identifier for this warehouse.
   * @param description the description for this warehouse.
   */
  public WarehouseVO(
      int warehouseID
      ,String description
      ) {
    this.warehouseID = warehouseID;
    this.description = description;
  }

  /**
   * Set the unique identifier for this warehouse.
   * @param warehouseID the unique identifier for this warehouse.
   */
  public void setWarehouseID(int warehouseID) {
    this.warehouseID = warehouseID;
  }

  /**
   * Get the unique identifier for this warehouse.
   * @return the unique identifier for this warehouse.
   */
  public int getWarehouseID() {
    return warehouseID;
  }

  /**
   * Set the description for this warehouse.
   * @param description the description for this warehouse.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Get the description for this warehouse.
   * @return the description for this warehouse.
   */
  public String getDescription() {
    return description;
  }

}
