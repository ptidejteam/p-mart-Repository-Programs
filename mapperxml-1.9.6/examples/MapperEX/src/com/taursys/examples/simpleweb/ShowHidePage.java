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
import com.taursys.html.*;
import com.taursys.xml.*;
import com.taursys.model.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.apache.xerces.parsers.DOMParser;
import java.util.*;


/**
 * This form shows how to show/hide parts of the XML/HTML document
 */
public class ShowHidePage extends ServletForm {
  HTMLInputText showField = new HTMLInputText();
  HTMLInputText showMessage = new HTMLInputText();
  HTMLInputText showTemplate = new HTMLInputText();
  HTMLInputText showButton = new HTMLInputText();
  TextField message = new TextField();
  TextField item = new TextField();
  HTMLInputText lastName = new HTMLInputText();
  Button button = new Button();
  Template reportTemplate = new Template();
  ObjectArrayValueHolder holder = new ObjectArrayValueHolder(new String[] {
    "Item 1",
    "Item 2",
    "Item 3",
    "Item 4",
  });
  HTMLInputText showOptional = new HTMLInputText();
  DocumentElement optional = new DocumentElement();

  /**
   * Constructs a new ShowHidePage and initializes component properties.
   */
  public ShowHidePage() {
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
    showField.setParameter("showField");
    showField.setId("showField");
    showField.setEarlyInputNotify(true);
    showField.setText("y");
    showMessage.setParameter("showMessage");
    showMessage.setId("showMessage");
    showMessage.setEarlyInputNotify(true);
    showMessage.setText("y");
    showTemplate.setParameter("showTemplate");
    showTemplate.setId("showTemplate");
    showTemplate.setEarlyInputNotify(true);
    showTemplate.setText("y");
    showButton.setParameter("showButton");
    showButton.setId("showButton");
    showButton.setEarlyInputNotify(true);
    showButton.setText("y");
    message.setId("message");
    item.setValueHolder(holder);
    item.setId("item");
    lastName.setId("lastName");
    reportTemplate.setId("reportTemplate");
    reportTemplate.setCollectionValueHolder(holder);
    button.setId("button");
    showOptional.setParameter("showOptional");
    showOptional.setId("showOptional");
    showOptional.setEarlyInputNotify(true);
    showOptional.setText("y");
    optional.setId("optional");
    this.add(showField);
    this.add(showMessage);
    this.add(showTemplate);
    this.add(showButton);
    this.add(message);
    this.add(lastName);
    this.add(button);
    reportTemplate.add(item);
    this.add(reportTemplate);
    this.add(showOptional);
    this.add(optional);
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
        getClass().getResourceAsStream("ShowHidePage.html"));
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
    message.setText("Message: The sky is blue");
    // Set visibility
    message.setVisible(showMessage.getText().equalsIgnoreCase("y"));
    reportTemplate.setVisible(showTemplate.getText().equalsIgnoreCase("y"));
    lastName.setVisible(showField.getText().equalsIgnoreCase("y"));
    button.setVisible(showButton.getText().equalsIgnoreCase("y"));
    optional.setVisible(showOptional.getText().equalsIgnoreCase("y"));
  }
}
