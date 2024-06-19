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

import com.taursys.servlet.*;
import com.taursys.util.DataTypes;
import org.xml.sax.InputSource;
import org.apache.xerces.parsers.DOMParser;
import com.taursys.model.VOValueHolder;
import com.taursys.model.ObjectArrayValueHolder;
import com.taursys.xml.SelectField;
import com.taursys.html.*;

/**
 * This form demonstrates the HTMLSelect component's basic use.
 */
public class SelectPage extends ServletForm {
  HTMLSelect color = new HTMLSelect();
  SelectField selectedColor = new SelectField();
  VOValueHolder person = new VOValueHolder();

  /**
   * Constructs a new SelectPage and initializes component properties.
   */
  public SelectPage() {
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
    color.setParameter("color");
    color.setId("color");
    color.setValueHolder(person);
    color.setPropertyName("favoriteColor");
    color.setNullDisplay("--choose a color--");
    selectedColor.setId("selectedColor");
    // Link selectedColor to same model as color
    selectedColor.setModel(color.getModel());
    this.add(selectedColor);
    this.add(color);
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
        getClass().getResourceAsStream("SelectPage.html"));
    parser.parse(is);
    this.setDocument(parser.getDocument());
    // Setup the list of Colors to choose from the Business delegate
    BusinessDelegate bd =
        (BusinessDelegate)getRequest().getAttribute(MainServlet.BUSINESS_DELEGATE);
    color.setList(new ObjectArrayValueHolder(bd.getColorCodes()));
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
