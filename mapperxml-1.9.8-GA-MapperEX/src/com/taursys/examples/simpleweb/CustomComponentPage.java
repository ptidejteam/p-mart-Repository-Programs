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

import java.text.SimpleDateFormat;
import java.util.Date;

import com.taursys.servlet.ServletForm;
import com.taursys.util.DataTypes;
import com.taursys.xml.TextField;

/**
 * Example of creating and using a custom compound component for a
 * Date field which is broken into 3 parts: month, day and year. The custom
 * component is actually a container that contains three components
 * which actually input the parts of the whole value (month, day and year).
 * They are combined whenever they are all received (year is last to get
 * input notification).
 *
 * If the model value changes (the actual Date), the changes are propagated
 * to the individual components (month, day, and year).
 *
 * @author M Phelan
 * @version 2.0
 */
public class CustomComponentPage extends ServletForm {
  private CustomDateComponent customDate = new CustomDateComponent();
  private TextField date = new TextField(DataTypes.TYPE_DATE);

  /**
   * Constructs a new CustomComponentPage and initializes component properties.
   */
  public CustomComponentPage() {
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
    this.setDocumentURI("resource:///forms/CustomComponentPage.html");
    customDate.setId("theDate");
    date.setFormat(new SimpleDateFormat());
    date.setFormatPattern("MM/dd/yyyy");
    date.setId("date");
    // We will have the Date field and the CustomComponent share the same holder
    date.setValueHolder(customDate.getValueHolder());
    this.add(customDate);
    this.add(date);
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
    date.setValue(new Date());
  }

}
