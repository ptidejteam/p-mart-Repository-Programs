package com.taursys.examples.library;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class DAONotFoundException extends DAOException {
  public static final int REASON_OFFSET                     = 200;
  public static final int NOT_FOUND_EXCEPTION               = 0 + REASON_OFFSET;
  private static final String[] messages = new String[] {
    "Record not found",
  };
  private String sql;
  private String operationName;
  private String keyValue;

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
   * Creates a DAONotFoundException with a reason code (which will display its message).
   */
  public DAONotFoundException(int reason, String operationName, String sql,
    String keyValue) {
    super(getReasonMessage(reason), reason);
    this.operationName = operationName;
    this.sql = sql;
    this.keyValue = keyValue;
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
        "keyValue=" + keyValue + "\n" +
        "SQL=" + sql + "\n";
  }
}