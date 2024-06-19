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

import java.math.BigDecimal;
import java.util.Date;

import com.taursys.html.HTMLInputText;
import com.taursys.model.ModelParseException;
import com.taursys.servlet.ServletForm;
import com.taursys.util.DataTypes;
import com.taursys.xml.TextField;

/**
 * This form shows the basics of using formatting for fields.
 */
public class FormatPage extends ServletForm {
  HTMLInputText unformattedDate = new HTMLInputText(DataTypes.TYPE_DATE);
  HTMLInputText formattedDate = new HTMLInputText(DataTypes.TYPE_DATE);
  HTMLInputText unformattedNumber = new HTMLInputText(DataTypes.TYPE_BIGDECIMAL);
  HTMLInputText formattedNumber = new HTMLInputText(DataTypes.TYPE_BIGDECIMAL);
  HTMLInputText messageNumber = new HTMLInputText(DataTypes.TYPE_INT);
  TextField messageNumberOut = new TextField();
  TextField errorMessage = new TextField();
  TextField friendlyMessage = new TextField();
  HTMLInputText vehicleLength = new HTMLInputText(DataTypes.TYPE_INT);

  /**
   * Constructs a new FormatPage and initializes component properties.
   */
  public FormatPage() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Set component properties here.  It is not recommended to put anything
   * other than property settings in here.  Any Exception will cause the
   * constructor to fail.
   */
  private void jbInit() throws Exception {
    this.setDocumentURI("resource:///forms/FormatPage.html");
    unformattedDate.setParameter("unformattedDate");
    unformattedDate.setId("unformattedDate");
    formattedDate.setFormat(java.text.SimpleDateFormat.getInstance());
    formattedDate.setFormatPattern("MM/dd/yyyy");
    formattedDate.setParameter("formattedDate");
    formattedDate.setId("formattedDate");
    unformattedNumber.setParameter("unformattedNumber");
    unformattedNumber.setId("unformattedNumber");
    formattedNumber.setFormat(java.text.DecimalFormat.getInstance());
    formattedNumber.setFormatPattern("###,###,##0.00");
    formattedNumber.setParameter("formattedNumber");
    formattedNumber.setId("formattedNumber");
    messageNumber.setParameter("messageNumber");
    messageNumber.setPropertyName("");
    messageNumber.setId("messageNumber");
    messageNumberOut.setFormat(new java.text.MessageFormat(""));
    messageNumberOut.setFormatPattern("http://www.somewhere.com/servlet/MyApp?productID={0,number,######}");
    messageNumberOut.setId("messageNumberOut");
    messageNumberOut.setValueHolder(messageNumber.getValueHolder());
    errorMessage.setId("errorMessage");
    friendlyMessage.setId("friendlyMessage");
    vehicleLength.setId("vehicleLength");
    vehicleLength.setFormat(java.text.DecimalFormat.getInstance());
    vehicleLength.setFormatPattern("#0");
    vehicleLength.setParameter("vehicleLength");
    this.add(vehicleLength);
    this.add(unformattedDate);
    this.add(formattedDate);
    this.add(unformattedNumber);
    this.add(formattedNumber);
    this.add(messageNumber);
    this.add(messageNumberOut);
    this.add(errorMessage);
    this.add(friendlyMessage);
  }

  /**
   * One time initialization of the ServletForm.  This method is invoked ONCE.
   * It is the first method invoked by the doGet method.  If the form is
   * recycled, it will NOT be invoked again by doGet.
   */
  protected void initForm() throws java.lang.Exception {
    super.initForm();
  }

  /**
   * This method is invoked by doGet to open the form.
   * It is invoked after any parameters have been read, but before input
   * values have been read.  Override this method to provide custom
   * behavior such as opening data sources.  There is no need to invoke
   * super.openForm().
   */
  protected void openForm() throws java.lang.Exception {
    // Preset some values to show formats
    unformattedDate.setValue(new Date());
    formattedDate.setValue(new Date());
    unformattedNumber.setValue(new BigDecimal("123456.78"));
    formattedNumber.setValue(new BigDecimal("123456.78"));
    messageNumber.setValue(new Integer("2101"));
    // clear error message
    errorMessage.setText("");
    friendlyMessage.setText("");
  }

  /**
   * This method is invoked whenever an exception occurs within doGet.
   * Override this method to provide custom exception handling behavior.
   * Throwing an exception will delegate the exception handling to the
   * caller of the doGet method.
   * The default behavior of this method is to simply re-throw the exception.
   */
  protected void handleException(Exception ex) throws java.lang.Exception {
    if (ex instanceof ModelParseException) {
      errorMessage.setText(ex.getMessage());
      friendlyMessage.setText(((ModelParseException)ex).getUserFriendlyMessage());
      sendResponse();
    } else {
      throw ex;
    }
  }

  /**
   * Send the appropriate response. It is invoked by doGet following the
   * dispatchActions method.  This method invokes the current Responder's
   * respond method to provide the appropriate response.
   * Change the Responder to provide custom response.
   */
  protected void sendResponse() throws java.lang.Exception {
    // Check vehicle length
    Integer vlen = (Integer)vehicleLength.getValue();
    if (vlen != null && (vlen.intValue() < 1 || vlen.intValue() > 70)) {
      errorMessage.setText("VehicleLength error");
      friendlyMessage.setText("Vehicle length must be between 1 and 70 feet");
    }
    super.sendResponse();
  }
}
