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
package com.taursys.examples.simpleweb.delegate;

/**
 * NotFoundException is generated when a requested item is not found.
 * @author Marty Phelan
 * @version 1.0
 */
public class NotFoundException extends java.lang.Exception {

  /**
   * Constructs a new NotFoundException
   */
  public NotFoundException(String message) {
    super(message);
  }
}
