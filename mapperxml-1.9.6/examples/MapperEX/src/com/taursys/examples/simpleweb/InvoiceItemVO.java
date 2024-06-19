package com.taursys.examples.simpleweb;

import java.math.BigDecimal;

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
    this.quantityShipped = quantityShipped;
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

}
