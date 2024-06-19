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

import java.util.ArrayList;

import com.taursys.examples.simpleweb.delegate.Person;
import com.taursys.model.VOCollectionValueHolder;
import com.taursys.servlet.ServletForm;
import com.taursys.xml.Template;
import com.taursys.xml.TextField;

/**
 * This example demonstrates how to use a Template and a VOCollectionValueHolder.
 */
public class TemplatePage extends ServletForm {
  TextField firstName = new TextField();
  TextField lastName = new TextField();
  Template report = new Template();
  VOCollectionValueHolder people = new VOCollectionValueHolder();
  Template formLetter = new Template();
  TextField firstName2 = new TextField();
  TextField lastName2 = new TextField();

  /**
   * Constructs a new TemplatePage and initializes component properties.
   */
  public TemplatePage() {
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
    this.setDocumentURI("resource:///forms/TemplatePage.html");
    people.setAlias("People");
    people.setValueObjectClass(Person.class);
    firstName.setPropertyName(Person.FIRST_NAME);
    firstName.setValueHolder(people);
    firstName.setId("firstName");
    lastName.setPropertyName(Person.LAST_NAME);
    lastName.setValueHolder(people);
    lastName.setId("lastName");
    report.setId("report");
    report.setCollectionValueHolder(people);
    formLetter.setId("formLetter");
    formLetter.setCollectionValueHolder(people);
    firstName2.setPropertyName("firstName");
    firstName2.setValueHolder(people);
    firstName2.setId("firstName2");
    lastName2.setPropertyName("lastName");
    lastName2.setValueHolder(people);
    lastName2.setId("lastName2");
    report.add(firstName);
    report.add(lastName);
    this.add(report);
    this.add(formLetter);
    formLetter.add(firstName2);
    formLetter.add(lastName2);
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
    // Retrieve or create the value objects
    ArrayList array = new ArrayList();
    array.add(new Person(1629, "Picard", "Jean Luc", null));
    array.add(new Person(2044, "Riker", "William", null));
    array.add(new Person(1326, "Crusher", "Beverly", null));
    people.setCollection(array);
  }
}
