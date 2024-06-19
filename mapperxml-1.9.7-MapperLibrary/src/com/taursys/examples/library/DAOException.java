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
 * A general data access exception.
 * @author Marty Phelan
 * @version 1.0
 */
public class DAOException extends com.taursys.util.ChainedException {

  // ************************************************************************
  //                       Protected Constructors
  // ************************************************************************

  /**
   * Creates a DAOException with a message.
   * This constructor is only available to subclasses
   */
  protected DAOException(String message, int reason) {
    super(message, reason);
  }

  /**
   * Creates a DAOException with a message.
   * This constructor is only available to subclasses.
   * This constructor appends the cause message to given message separated
   * by a ": ".  It then stores the message, reason code and cause.
   */
  protected DAOException(String message, int reason, Throwable cause) {
    super(message, reason, cause);
  }
}