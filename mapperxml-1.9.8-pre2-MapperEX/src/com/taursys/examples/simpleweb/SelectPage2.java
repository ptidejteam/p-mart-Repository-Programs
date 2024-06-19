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
import com.taursys.examples.simpleweb.delegate.RBGColor;
import com.taursys.html.HTMLSelect;
import com.taursys.model.VOCollectionValueHolder;
import com.taursys.model.VOValueHolder;
import com.taursys.servlet.ServletForm;
import com.taursys.xml.BoundDocumentElement;
import com.taursys.xml.SelectField;
import com.taursys.xml.TextField;

/**
 * This form demonstrates the HTMLSelect component's basic use.
 */
public class SelectPage2 extends ServletForm {
  HTMLSelect color = new HTMLSelect();
  SelectField selectedColor = new SelectField();
  VOValueHolder person = new VOValueHolder();
  TextField fullName = new TextField();
  TextField favoriteColor = new TextField();
  BoundDocumentElement colorExample = new BoundDocumentElement();

  /**
   * Constructs a new SelectPage2 and initializes component properties.
   */
  public SelectPage2() {
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
    this.setDocumentURI("resource:///forms/SelectPage2.html");
    color.setParameter("color");
    color.setId("color");
    color.setValueHolder(person);
    color.setPropertyName(Person.FAVORITE_COLOR);
    color.setDisplayPropertyName(RBGColor.TITLE);
    color.setListPropertyNames(new String[] {RBGColor.CODE});
    color.setNullDisplay("--choose a color--");

    // Link selectedColor to same model as color
    selectedColor.setModel(color.getModel());
    selectedColor.setId("selectedColor");

    // Setup Person components
    fullName.setId("fullName");
    fullName.setValueHolder(person);
    fullName.setPropertyName(Person.FULL_NAME);
    //
    favoriteColor.setId("favoriteColor");
    favoriteColor.setValueHolder(person);
    favoriteColor.setPropertyName(Person.FAVORITE_COLOR);
    //
    colorExample.setId("colorExample");
    colorExample.setValueHolder(person);
    colorExample.createBoundAttribute("bgcolor","favoriteColor");

    this.add(selectedColor);
    this.add(color);
    this.add(fullName);
    this.add(favoriteColor);
    this.add(colorExample);
  }

  /**
   * One time initialization of the ServletForm.  This method is invoked ONCE.
   * It is the first method invoked by the doGet method.  If the form is
   * recycled, it will NOT be invoked again by doGet.
   */
  protected void initForm() throws java.lang.Exception {
    super.initForm();
    // Setup list of colors - this list does not change so we will do it here
    // rather than in openForm
    // Setup the list of Colors to choose from the Business delegate
    BusinessDelegate bd = BusinessDelegate.getInstance();
    color.setList(new VOCollectionValueHolder(bd.getColorList()));
  }

  /**
   * This method is invoked by doGet to open the form.
   * It is invoked after any parameters have been read, but before input
   * values have been read.  Override this method to provide custom
   * behavior such as opening data sources.  There is no need to invoke
   * super.openForm().
   */
  protected void openForm() throws java.lang.Exception {
    // Try to retrieve from session else create
    Person personVO = (Person)getRequest().getSession().getAttribute("ThePerson");
    if (personVO == null) {
      // Create the value object and store
      personVO = new Person(1629, "Pulaski", "Katherine", null);
      getRequest().getSession().setAttribute("ThePerson", personVO);
    }
    // Bind value object to person ValueHolder
    person.setValueObject(personVO);
  }
}
