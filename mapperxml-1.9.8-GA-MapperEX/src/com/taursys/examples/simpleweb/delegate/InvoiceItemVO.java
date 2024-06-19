package com.taursys.examples.simpleweb.delegate;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Value Object
 * @author Marty Phelan
 * @version 1.0
 */
public class InvoiceItemVO {
  private int quantity;
  private String productID;
  private BigDecimal unitPrice;
  private int quantityShipped;
  private int warehouseID;
  private java.util.ArrayList warehouses;

  /**
   * Constructs a new InvoiceItemVO with default values.
   */
  public InvoiceItemVO() {
  }

  /**
   * Constructs a new InvoiceItemVO with given values.
   * @param quantity the quantity for this invoice item.
   * @param productID the unique product identifier for this invoice item.
   * @param unitPrice the unit price for this invoice item.
   * @param quantityShipped the number of items that where shipped for this line item.
   */
  public InvoiceItemVO(
      int quantity
      ,String productID
      ,BigDecimal unitPrice
      ,int quantityShipped
      ) {
    this.quantity = quantity;
    this.productID = productID;
    this.unitPrice = unitPrice;
    this.quantityShipped = quantityShipped;
  }

  /**
   * Constructs a new InvoiceItemVO with given values.
   * @param quantity the quantity for this invoice item.
   * @param productID the unique product identifier for this invoice item.
   * @param unitPrice the unit price for this invoice item.
   */
  public InvoiceItemVO(
      int quantity
      ,String productID
      ,BigDecimal unitPrice
      ) {
    this.quantity = quantity;
    this.productID = productID;
    this.unitPrice = unitPrice;
  }

  /**
   * Constructs a new InvoiceItemVO with given values.
   * @param quantity the quantity for this invoice item.
   * @param warehouseID the warehouse ID where the item is stored.
   * @param productID the unique product identifier for this invoice item.
   * @param unitPrice the unit price for this invoice item.
   */
  public InvoiceItemVO(
      int quantity
      ,int warehouseID
      ,String productID
      ,BigDecimal unitPrice
      ,ArrayList warehouses
      ) {
    this.quantity = quantity;
    this.warehouseID = warehouseID;
    this.productID = productID;
    this.unitPrice = unitPrice;
    this.warehouses = warehouses;
  }

  /**
   * Set the quantity for this invoice item.
   * @param quantity the quantity for this invoice item.
   */
  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  /**
   * Get the quantity for this invoice item.
   * @return the quantity for this invoice item.
   */
  public int getQuantity() {
    return quantity;
  }

  /**
   * Set the unique product identifier for this invoice item.
   * @param productID the unique product identifier for this invoice item.
   */
  public void setProductID(String productID) {
    this.productID = productID;
  }

  /**
   * Get the unique product identifier for this invoice item.
   * @return the unique product identifier for this invoice item.
   */
  public String getProductID() {
    return productID;
  }

  /**
   * Set the unit price for this invoice item.
   * @param unitPrice the unit price for this invoice item.
   */
  public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
  }

  /**
   * Get the unit price for this invoice item.
   * @return the unit price for this invoice item.
   */
  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  /**
   * Get the extended price for this invoice item (quantity x unitPrice).
   * @return the extended price for this invoice item (quantity x unitPrice).
   */
  public BigDecimal getExtension() {
    if (unitPrice != null)
      return unitPrice.multiply(new BigDecimal(quantity));
    else
      return new BigDecimal(0);
  }

  /**
   * Set the number of items that where shipped for this line item.
   * @param quantityShipped the number of items that where shipped for this line item.
   */
  public void setQuantityShipped(int quantityShipped) {
    this.quantityShipped = quantityShipped;
  }

  /**
   * Get the number of items that where shipped for this line item.
   * @return the number of items that where shipped for this line item.
   */
  public int getQuantityShipped() {
    return quantityShipped;
  }

  /**
   * Set the warehouse ID where the item is stored.
   * @param warehouseID the warehouse ID where the item is stored.
   */
  public void setWarehouseID(int warehouseID) {
    this.warehouseID = warehouseID;
  }

  /**
   * Get the warehouse ID where the item is stored.
   * @return the warehouse ID where the item is stored.
   */
  public int getWarehouseID() {
    return warehouseID;
  }

  /**
   * Set the list of available warehouses where this product can be stored.
   * @param warehouses the list of available warehouses where this product can
   * be stored.
   */
  public void setWarehouses(ArrayList warehouses) {
    this.warehouses = warehouses;
  }

  /**
   * Get the list of available warehouses where this product can be stored.
   * @return the list of available warehouses where this product can
   * be stored.
   */
  public ArrayList getWarehouses() {
    return warehouses;
  }

}
