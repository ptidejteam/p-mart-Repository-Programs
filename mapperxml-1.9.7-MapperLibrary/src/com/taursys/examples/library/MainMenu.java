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

import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.InputSource;

import com.taursys.servlet.ServletForm;
import com.taursys.xml.TextField;

/**
 * Servlet Form for the main menu.
 * @author Marty Phelan
 * @version 1.0
 */
public class MainMenu extends ServletForm {
  private TextField messageField = new TextField();

  /**
   * Constructs a new MainMenu and initializes component properties.
   */
  public MainMenu() {
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
    messageField.setId("message");
    this.add(messageField);
  }

  /**
   * One time initialization of the ServletForm.  This method is invoked ONCE.
   * It is the first method invoked by the doGet method.  If the form is
   * recycled, it will NOT be invoked again by doGet.
   */
  protected void initForm() throws java.lang.Exception {
    super.initForm();
    // Use Xerces to Parse document to a DOM and store as this form's document
    // You can use any method you like to create the DOM
    DOMParser parser = new DOMParser();
    InputSource is = new InputSource(
        getClass().getResourceAsStream("MainMenu.html"));
    parser.parse(is);
    this.setDocument(parser.getDocument());
  }

  protected void openForm() throws java.lang.Exception {
    String visitTime = (String)getRequest().getSession().getAttribute("visitTime");
    if (visitTime == null) {
      visitTime = new java.util.Date().toString();
      getRequest().getSession().setAttribute("visitTime", visitTime);
    }
    messageField.setText(visitTime);
  }
}
