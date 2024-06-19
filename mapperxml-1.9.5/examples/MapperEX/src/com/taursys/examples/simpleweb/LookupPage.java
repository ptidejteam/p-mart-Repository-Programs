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
import com.taursys.html.HTMLInputText;
import com.taursys.util.DataTypes;
import org.xml.sax.InputSource;
import org.apache.xerces.parsers.DOMParser;
import com.taursys.model.VOValueHolder;
import com.taursys.xml.*;


/**
 * This form demonstrates how to use Fields as parameters to lookup and display data.
 * @author
 * @version 1.0
 */
public class LookupPage extends ServletForm {
  HTMLInputText personID = new HTMLInputText(DataTypes.TYPE_INT);
  HTMLInputText firstName = new HTMLInputText();
  HTMLInputText lastName = new HTMLInputText();
  VOValueHolder person = new VOValueHolder();
  TextField errorMessage = new TextField();

  /**
   * Constructs a new LookupPage and initializes component properties.
   */
  public LookupPage() {
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
    firstName.setParameter("firstName");
    firstName.setPropertyName("firstName");
    firstName.setValueHolder(person);
    firstName.setId("firstName");
    lastName.setParameter("lastName");
    lastName.setPropertyName("lastName");
    lastName.setValueHolder(person);
    lastName.setId("lastName");
    personID.setParameter("personID");
    personID.setId("personID");
    personID.setEarlyInputNotify(true);
    errorMessage.setId("errorMessage");
    this.add(personID);
    this.add(firstName);
    this.add(lastName);
    this.add(errorMessage);
    // Define the class that will be held in case its null at runtime
    person.setValueObjectClass(Person.class);
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
        getClass().getResourceAsStream("LookupPage.html"));
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
    // Try to lookup person if given a search key
    Integer pid = (Integer)personID.getValue();
    Person personVO = null;
    if (pid != null) {
      // Create the value object and store
      try {
        personVO = new BusinessDelegate().getPerson(pid.intValue());
        errorMessage.setText(null);
      } catch (IndexOutOfBoundsException ex) {
        errorMessage.setText("Person not found");
      }
    } else {
        errorMessage.setText("Enter a person ID to lookup");
    }
    // Bind value object to person ValueHolder
    person.setValueObject(personVO);
  }
}
