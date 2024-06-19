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
import com.taursys.model.VOCollectionValueHolder;
import com.taursys.xml.render.AttributeTextFieldRenderer;
import com.taursys.html.*;
import java.util.Collection;
import java.util.ArrayList;
import com.taursys.xml.*;

/**
 * This form demonstrates the HTMLSelect component's basic use.
 */
public class SelectPage2 extends ServletForm {
  HTMLSelect color = new HTMLSelect();
  SelectField selectedColor = new SelectField();
  VOValueHolder person = new VOValueHolder();
  TextField fullName = new TextField();
  TextField favoriteColor = new TextField();
  TextField colorExample = new TextField();

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
    colorExample.setModel(favoriteColor.getModel());
    colorExample.setRenderer(new AttributeTextFieldRenderer(colorExample));
    colorExample.setAttributeName("bgcolor");

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
    // Use Xerces to Parse document to a DOM and store as this form's document
    // You can use any method you like to create the DOM
    DOMParser parser = new DOMParser();
    InputSource is = new InputSource(
        getClass().getResourceAsStream("SelectPage2.html"));
    parser.parse(is);
    this.setDocument(parser.getDocument());
    // Setup list of colors - this list does not change so we will do it here
    // rather than in openForm
    VOCollectionValueHolder listHolder = new VOCollectionValueHolder();
    listHolder.setCollection(fetchColorList());
    color.setList(listHolder);
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

  /**
   * Create a list of colors for testing.  These colors could come from
   * a database or Enterprise bean.  If the list does NOT change, then
   * this should be done in the initForm method.  If the list DOES change,
   * then it should be done in the openForm method.
   */
  private Collection fetchColorList() {
    ArrayList list = new ArrayList();
    list.add(new RBGColor("Bright White", "#FFFFFF"));
    list.add(new RBGColor("Solid Black", "#000000"));
    list.add(new RBGColor("Bright Red", "#FF0000"));
    list.add(new RBGColor("Bright Green", "#00FF00"));
    list.add(new RBGColor("Bright Blue", "#0000FF"));
    list.add(new RBGColor("Bright Orange", "#FF9900"));
    list.add(new RBGColor("BrightY ellow", "#FFFF00"));
    list.add(new RBGColor("Bright Cyan", "#00FFFF"));
    list.add(new RBGColor("Light Magenta", "#FF00FF"));
    return list;
  }
}
