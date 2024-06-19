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

import com.taursys.html.HTMLCheckBox;
import com.taursys.html.HTMLInputText;
import com.taursys.model.ObjectArrayValueHolder;
import com.taursys.servlet.ServletForm;
import com.taursys.xml.Button;
import com.taursys.xml.DocumentElement;
import com.taursys.xml.Template;
import com.taursys.xml.TextField;


/**
 * This form shows how to show/hide parts of the XML/HTML document
 */
public class ShowHidePage extends ServletForm {
  HTMLCheckBox showOptional = new HTMLCheckBox();
  HTMLCheckBox showField = new HTMLCheckBox();
  HTMLCheckBox showMessage = new HTMLCheckBox();
  HTMLCheckBox showTemplate = new HTMLCheckBox();
  HTMLCheckBox showButton = new HTMLCheckBox();
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
    this.setDocumentURI("resource:///forms/ShowHidePage.html");
    showField.setParameter("showField");
    showField.setId("showField");
    showMessage.setParameter("showMessage");
    showMessage.setId("showMessage");
    showTemplate.setParameter("showTemplate");
    showTemplate.setId("showTemplate");
    showButton.setParameter("showButton");
    showButton.setId("showButton");
    showOptional.setParameter("showOptional");
    showOptional.setId("showOptional");
    message.setId("message");
    item.setValueHolder(holder);
    item.setId("item");
    lastName.setId("lastName");
    reportTemplate.setId("reportTemplate");
    reportTemplate.setCollectionValueHolder(holder);
    button.setId("button");
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
  }

  /**
   * This method is invoked by doGet to open the form.
   * It is invoked after any parameters have been read, but before input
   * values have been read.  Override this method to provide custom
   * behavior such as opening data sources.  There is no need to invoke
   * super.openForm().
   */
  protected void openForm() throws java.lang.Exception {
    // Set defaults
    message.setText("Message: The sky is blue");
  }

  /**
   * Send the appropriate response. It is invoked by doGet following the
   * dispatchActions method.  The default behavior is to dispatch a render
   * message to all components, set the response type to text/html and
   * invoke the xmlWriter to write to the response object.
   */
  protected void sendResponse() throws java.lang.Exception {
    // To cope with behavior of html checkboxes (only sends value if checked),
    // test if this is POST or GET
    if (getRequest().getMethod().equalsIgnoreCase("get")) {
      // Preset to checked on GET
      showMessage.setSelected(true);
      showTemplate.setSelected(true);
      showField.setSelected(true);
      showButton.setSelected(true);
      showOptional.setSelected(true);
    }
    message.setVisible(showMessage.isSelected());
    reportTemplate.setVisible(showTemplate.isSelected());
    lastName.setVisible(showField.isSelected());
    button.setVisible(showButton.isSelected());
    optional.setVisible(showOptional.isSelected());
    // Invoke normal processing
    super.sendResponse();
  }

}
