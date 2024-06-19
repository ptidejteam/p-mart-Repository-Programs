/**
 * MultiModelParseException - List of multiple ModelParseExceptions
 *
 * Copyright (c) 2006
 *      Marty Phelan, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package com.taursys.model;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * List of multiple ModelParseExceptions.
 * 
 * @author Marty
 */
public class MultiModelParseException extends ModelParseException {
  private List exceptionList = new ArrayList();

  /**
   * Constructs MultiModelParseException with given list of ModelParseExceptions
   * 
   * @param exceptionList
   */
  public MultiModelParseException(List exceptionList) {
    this.exceptionList = exceptionList;
  }

  /**
   * Constructs MultiModelParseException with given ModelParseExceptions
   * 
   * @param ex
   */
  public MultiModelParseException(ModelParseException ex) {
    exceptionList.add(ex);
  }

  /**
   * Adds a ModelParseException to the list
   * 
   * @param ex
   */
  public void add(ModelParseException ex) {
    exceptionList.add(ex);
  }

  /**
   * Adds all the ModelParseExceptions from the given MultiModelParseException
   * 
   * @param ex
   */
  public void add(MultiModelParseException ex) {
    exceptionList.addAll(ex.getExceptionList());
  }

  /**
   * Gets the list of all the ModelParseExceptions
   * 
   * @return
   */
  public List getExceptionList() {
    return exceptionList;
  }

  // ************************************************************************
  //            Overrides that getXXX from first ModelParseException
  // ************************************************************************

  private ModelParseException getFirst() {
    return (ModelParseException) exceptionList.get(0);
  }

  /**
   * Get the formatClass of the first ModelParseException
   * 
   * @see com.taursys.model.ModelParseException#getFormatClass()
   */
  public String getFormatClass() {
    return getFirst().getFormatClass();
  }

  /**
   * Get the formatPattern of the first ModelParseException
   * 
   * @see com.taursys.model.ModelParseException#getFormatPattern()
   */
  public String getFormatPattern() {
    return getFirst().getFormatPattern();
  }

  /**
   * Get the givenValue of the first ModelParseException
   * 
   * @see com.taursys.model.ModelParseException#getGivenValue()
   */
  public String getGivenValue() {
    return getFirst().getGivenValue();
  }

  /**
   * Get the propertyDataType of the first ModelParseException
   * 
   * @see com.taursys.model.ModelParseException#getPropertyDataType()
   */
  public String getPropertyDataType() {
    return getFirst().getPropertyDataType();
  }

  /**
   * Get the propertyName of the first ModelParseException
   * 
   * @see com.taursys.model.ModelParseException#getPropertyName()
   */
  public String getPropertyName() {
    return getFirst().getPropertyName();
  }

  /**
   * Get the cause of the first ModelParseException
   * 
   * @see java.lang.Throwable#getCause()
   */
  public Throwable getCause() {
    return getFirst().getCause();
  }

  /**
   * Get the reason of the first ModelParseException
   * 
   * @see com.taursys.util.ChainedException#getReason()
   */
  public int getReason() {
    return getFirst().getReason();
  }

  // ************************************************************************
  //      Overrides that include information from ALL ModelParseExceptions
  // ************************************************************************

  /**
   * Get the diagnosticInfo from all the ModelParseExceptions
   * 
   * @see com.taursys.util.ChainedException#getDiagnosticInfo()
   */
  public String getDiagnosticInfo() {
    StringBuffer msgs = new StringBuffer();
    msgs.append("DiagnosticInfo: ==========================================\n");
    for (Iterator iter = exceptionList.iterator(); iter.hasNext();) {
      ModelParseException ex = (ModelParseException) iter.next();
      msgs.append(ex.getDiagnosticInfo());
      msgs
          .append("\n========================================================\n");
    }
    return msgs.toString();
  }

  /**
   * Get the localizedMessage from all the ModelParseExceptions
   * 
   * @see java.lang.Throwable#getLocalizedMessage()
   */
  public String getLocalizedMessage() {
    StringBuffer msgs = new StringBuffer();
    for (Iterator iter = exceptionList.iterator(); iter.hasNext();) {
      ModelParseException ex = (ModelParseException) iter.next();
      msgs.append(ex.getLocalizedMessage());
      msgs.append("; ");
    }
    return msgs.toString();
  }

  /**
   * Get the message from all the ModelParseExceptions
   * 
   * @see java.lang.Throwable#getMessage()
   */
  public String getMessage() {
    StringBuffer msgs = new StringBuffer();
    for (Iterator iter = exceptionList.iterator(); iter.hasNext();) {
      ModelParseException ex = (ModelParseException) iter.next();
      msgs.append(ex.getMessage());
      msgs.append("; ");
    }
    return msgs.toString();
  }

  /**
   * Get the userFriendlyMessage from all the ModelParseExceptions
   * 
   * @see com.taursys.util.ChainedException#getUserFriendlyMessage()
   */
  public String getUserFriendlyMessage() {
    StringBuffer msgs = new StringBuffer();
    for (Iterator iter = exceptionList.iterator(); iter.hasNext();) {
      ModelParseException ex = (ModelParseException) iter.next();
      msgs.append(ex.getUserFriendlyMessage());
      msgs.append("; ");
    }
    return msgs.toString();
  }

  /**
   * Print the stack trace from all the ModelParseExceptions
   * 
   * @see java.lang.Throwable#printStackTrace()
   */
  public void printStackTrace() {
    for (Iterator iter = exceptionList.iterator(); iter.hasNext();) {
      ModelParseException ex = (ModelParseException) iter.next();
      ex.printStackTrace();
    }
  }

  /**
   * Print the stack trace from all the ModelParseExceptions
   * 
   * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
   */
  public void printStackTrace(PrintStream printStream) {
    for (Iterator iter = exceptionList.iterator(); iter.hasNext();) {
      ModelParseException ex = (ModelParseException) iter.next();
      ex.printStackTrace(printStream);
    }
  }

  /**
   * Print the stack trace from all the ModelParseExceptions
   * 
   * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
   */
  public void printStackTrace(PrintWriter printWriter) {
    for (Iterator iter = exceptionList.iterator(); iter.hasNext();) {
      ModelParseException ex = (ModelParseException) iter.next();
      ex.printStackTrace(printWriter);
    }
  }
}