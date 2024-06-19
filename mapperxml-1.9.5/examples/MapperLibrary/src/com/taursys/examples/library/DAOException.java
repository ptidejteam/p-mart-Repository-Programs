package com.taursys.examples.library;

import com.taursys.util.ChainedException;
import java.sql.SQLException;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
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