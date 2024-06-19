package com.taursys.examples.library;

import java.sql.SQLException;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class DAODuplicateKeyException extends DAOSQLException {
  public static final int REASON_OFFSET                     = 400;
  public static final int DUPLICATE_KEY                     = 0 + REASON_OFFSET;
  private static final String[] messages = new String[] {
    "SQLException - Duplicate Key occurred during insert",
  };

  // ************************************************************************
  //                       Static Class Methods
  // ************************************************************************

  /**
   * Returns String for given reason code else String for REASON_INVALID_REASON_CODE.
   */
  public static String getReasonMessage(int reason) {
    if (reason >=  + REASON_OFFSET && reason < messages.length + REASON_OFFSET)
      return messages[reason - REASON_OFFSET];
    else
      return getInvalidMessage();
  }

  // ************************************************************************
  //                        Public Constructors
  // ************************************************************************

  /**
   * Creates a DAOSQLException with a reason code (which will display its message).
   */
  public DAODuplicateKeyException(int reason, String operationName, String sql,
      SQLException ex) {
    super(reason, operationName, sql, ex);
  }
}