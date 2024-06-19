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

import java.sql.SQLException;

/**
 * An exception arising from a JDBC/SQL exception during data access
 * @author Marty Phelan
 * @version 1.0
 */
public class DAOSQLException extends DAOException {
  public static final int REASON_OFFSET                     = 100;
  public static final int SQL_EXCEPTION_DURING_QUERY        = 0 + REASON_OFFSET;
  public static final int SQL_EXCEPTION_DURING_CLOSE        = 1 + REASON_OFFSET;
  public static final int SQL_EXCEPTION_DURING_INSERT       = 2 + REASON_OFFSET;
  public static final int SQL_EXCEPTION_DURING_UPDATE       = 3 + REASON_OFFSET;
  public static final int SQL_EXCEPTION_DURING_DELETE       = 4 + REASON_OFFSET;
  private static final String[] messages = new String[] {
    "SQLException occurred during query",
    "SQLException occurred during close",
    "SQLException occurred during insert",
    "SQLException occurred during update",
    "SQLException occurred during delete",
  };
  private String sql;
  private String operationName;

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
  public DAOSQLException(int reason, String operationName, String sql,
      SQLException ex) {
    super(getReasonMessage(reason), reason, ex);
    this.operationName = operationName;
    this.sql = sql;
    setDiagnosticsInfo();
  }

  /**
   * Creates a DAOSQLException with a reason code (which will display its message).
   */
  public DAOSQLException(int reason, String operationName, SQLException ex) {
    super(getReasonMessage(reason), reason, ex);
    this.operationName = operationName;
    this.sql = "n/a";
    setDiagnosticsInfo();
  }

  // ************************************************************************
  //                   Prepare Diagnostics Info Methods
  // ************************************************************************

  /**
   * Builds the diagnosticsInfo String from the current property values.
   */
  protected void setDiagnosticsInfo() {
    diagnosticInfo =
        "operationName=" + operationName + "\n" +
        "SQL=" + sql + "\n";
  }
}