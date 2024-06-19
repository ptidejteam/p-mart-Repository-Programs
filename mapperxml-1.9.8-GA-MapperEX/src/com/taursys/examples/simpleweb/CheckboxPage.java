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

import com.taursys.html.HTMLCheckBox;
import com.taursys.servlet.ServletForm;
import com.taursys.util.DataTypes;
import com.taursys.xml.TextField;

/**
 * CheckboxPage Servlet Form Example demonstrating the use of an HTMLCheckbox.
 * @author
 * @version 1.0
 */
public class CheckboxPage extends ServletForm {
  HTMLCheckBox housing = new HTMLCheckBox(DataTypes.TYPE_BOOLEAN);
  HTMLCheckBox attendence = new HTMLCheckBox();
  HTMLCheckBox prepaid = new HTMLCheckBox();
  HTMLCheckBox materials = new HTMLCheckBox(DataTypes.TYPE_BIGDECIMAL);
  TextField results = new TextField();

  /**
   * Constructs a new CheckboxPage and initializes component properties.
   */
  public CheckboxPage() {
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
    this.setDocumentURI("resource:///forms/CheckboxPage.html");
    housing.setParameter("housing");
    housing.setId("housing");
    housing.setSelectedValue("true");
    housing.setUnselectedValue("false");
    attendence.setParameter("attendence");
    attendence.setId("attendence");
    attendence.setSelectedValue("F");
    attendence.setUnselectedValue("P");
    prepaid.setParameter("prepaid");
    prepaid.setId("prepaid");
    prepaid.setSelectedValue("Y");
    prepaid.setUnselectedValue("N");
    materials.setParameter("materials");
    materials.setId("materials");
    materials.setSelectedValue("55.00");
    materials.setUnselectedValue("0.00");
    results.setId("results");
    this.add(housing);
    this.add(attendence);
    this.add(prepaid);
    this.add(materials);
    this.add(results);
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
    // Clear out checkboxes
    housing.setSelected(true);
    prepaid.setSelected(false);
    materials.setSelected(false);
    attendence.setSelected(false);
    // Enable input only if this is a POST request (user input present)
    setEnableInput(getRequest().getMethod().equals("POST"));
  }

  /**
   * Send the appropriate response. It is invoked by doGet following the
   * dispatchActions method.  The default behavior is to dispatch a render
   * message to all components, set the response type to text/html and
   * invoke the xmlWriter to write to the response object.
   */
  protected void sendResponse() throws java.lang.Exception {
    StringBuffer r = new StringBuffer("");
    if (housing.isSelected())
      r.append("Housing is required. ");
    r.append("Attendence is code " + attendence.getText() + ". ");
    if (prepaid.isSelected())
      r.append("Fees are prepaid. ");
    else
      r.append("Pay at the door. ");
    r.append("Total due for materials = " + materials.getText());
    results.setText(r.toString());
    // Invoke normal processing
    super.sendResponse();
  }
}
