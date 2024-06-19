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

import com.taursys.examples.simpleweb.delegate.BusinessDelegate;
import com.taursys.examples.simpleweb.delegate.Person;
import com.taursys.html.HTMLInputText;
import com.taursys.model.VOValueHolder;
import com.taursys.servlet.ServletForm;
import com.taursys.util.DataTypes;
import com.taursys.xml.TextField;


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
  private static final String MESSAGE =
      "Enter a person ID to lookup. (Examples include: 0,1, or 2)";

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
    this.setDocumentURI("resource:///forms/LookupPage.html");
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
    // Get the Business delegate
    BusinessDelegate bd = BusinessDelegate.getInstance();
    if (pid != null) {
      // Create the value object and store
      try {
        personVO = bd.getPerson(pid.intValue());
        errorMessage.setText(null);
      } catch (IndexOutOfBoundsException ex) {
        errorMessage.setText("Person not found. " + MESSAGE);
      }
    } else {
        errorMessage.setText(MESSAGE);
    }
    // Bind value object to person ValueHolder
    person.setValueObject(personVO);
  }

  protected void handleException(Exception ex) throws Exception {
    if (ex instanceof com.taursys.model.ModelParseException) {
      errorMessage.setText(ex.getMessage() + ". " + MESSAGE);
      sendResponse();
    } else {
      super.handleException(ex);
    }
  }
}
