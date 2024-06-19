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

import com.taursys.debug.Debug;
import com.taursys.html.HTMLInputText;
import com.taursys.model.ModelException;
import com.taursys.servlet.ServletForm;
import com.taursys.xml.TextField;
import com.taursys.xml.Trigger;
import com.taursys.xml.event.InputEvent;
import com.taursys.xml.event.TriggerEvent;

/**
 * This form shows how to respond to events from Triggers and other components.
 */
public class ActionPage extends ServletForm {
  HTMLInputText lastName = new HTMLInputText();
  Trigger button1 = new Trigger();
  Trigger button2 = new Trigger();
  TextField happened = new TextField();

  /**
   * Constructs a new ActionPage and initializes component properties.
   */
  public ActionPage() {
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
    this.setDocumentURI("resource:///forms/ActionPage.html");
    lastName.setParameter("lastName");
    lastName.setId("lastName");
    lastName.addInputListener(new com.taursys.xml.event.InputListener() {
      public void inputReceived(InputEvent e) {
        lastName_inputReceived(e);
      }
    });
    button1.setParameter("action");
    button1.setText("High");
    button1.addTriggerListener(new com.taursys.xml.event.TriggerListener() {
      public void actionPerformed(TriggerEvent e) throws Exception {
        button1_actionPerformed(e);
      }
    });
    button2.setParameter("action");
    button2.setText("Low");
    button2.addTriggerListener(new com.taursys.xml.event.TriggerListener() {
      public void actionPerformed(TriggerEvent e) throws Exception {
        button2_actionPerformed(e);
      }
    });
    happened.setId("happened");
    this.add(lastName);
    this.add(button1);
    this.add(button2);
    this.add(happened);
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
    happened.setText("absolutely nothing");
  }

  /**
   * This method is invoked whenever the user presses the High button
   */
  void button1_actionPerformed(TriggerEvent e) throws Exception {
    happened.setText(happened.getText() + " - High button pressed");
  }

  /**
   * This method is invoked whenever the user presses the Low button
   */
  void button2_actionPerformed(TriggerEvent e) throws Exception {
    happened.setText(happened.getText() + " - Low button pressed");
  }

  /**
   * This method is whenever input is received for the lastName field
   */
  void lastName_inputReceived(InputEvent e) {
    try {
      happened.setText("lastName submitted=" + lastName.getText());
    } catch (ModelException ex) {
      Debug.error("Error during inputReceived for lastName", ex);
    }
  }
}
