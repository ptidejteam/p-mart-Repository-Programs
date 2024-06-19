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
package com.taursys.examples.simpleweb;

/**
 * ValueObject for a Deduction.
 */
public class Deduction {
  private java.math.BigDecimal amount;

  /**
   * Constructs a new empty Deduction
   */
  public Deduction() {
  }

  /**
   * Get the amount of this deduction
   */
  public java.math.BigDecimal getAmount() {
    return amount;
  }

  /**
   * Set the amount of this deduction
   */
  public void setAmount(java.math.BigDecimal newAmount) {
    amount = newAmount;
  }
}