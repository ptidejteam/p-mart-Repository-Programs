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

import com.taursys.servlet.ServletForm;
import com.taursys.xml.TextField;
import com.taursys.util.DataTypes;
import org.xml.sax.InputSource;
import org.apache.xerces.parsers.DOMParser;
import com.taursys.model.VOValueHolder;

/**
 * VOValueHolderPage Servlet Form Example demonstrating the use of a ValueHolder.
 */
public class VOValueHolderPage extends ServletForm {
  TextField firstName = new TextField();
  TextField lastName = new TextField();
  VOValueHolder person = new VOValueHolder();

  /**
   * Constructs a new VOValueHolderPage and initializes component properties.
   */
  public VOValueHolderPage() {
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
    firstName.setPropertyName("firstName");
    firstName.setValueHolder(person);
    firstName.setId("firstName");
    lastName.setPropertyName("lastName");
    lastName.setValueHolder(person);
    lastName.setId("lastName");
    this.add(firstName);
    this.add(lastName);
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
        getClass().getResourceAsStream("VOValueHolderPage.html"));
    parser.parse(is);
    this.setDocument(parser.getDocument());
  }

  /**
   * This method is invoked by doGet to open the form.
   * It is invoked after any parameters have been read, but before input
   * values have been read.  Override this method to provide custom
   * behavior such as opening data sources.  There is no need to invoke
   * super.openForm().
   */
  protected void openForm() throws java.lang.Exception {
    // Retrieve or create the value object
    Person personVO = new Person(1629, "Pulaski", "Katherine", null);
    // Bind value object to person ValueHolder
    person.setValueObject(personVO);
  }
}
