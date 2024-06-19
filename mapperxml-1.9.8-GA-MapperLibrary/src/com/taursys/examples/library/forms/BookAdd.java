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
package com.taursys.examples.library.forms;

import com.taursys.examples.library.delegate.BookVO;
import com.taursys.html.HTMLSelect;
import com.taursys.model.VOCollectionValueHolder;
import com.taursys.model.VOValueHolder;
import com.taursys.servlet.respond.ClientRedirectResponder;
import com.taursys.xml.Trigger;
import com.taursys.xml.event.TriggerEvent;

/**
 * Servlet Form for adding books. This for uses the HTMLComponentFactory to
 * create most of the components and bind them.  The HTMLSelect component is
 * created manually since we need to customize its properties (the list).
 * @author
 * @version 1.0
 */
public class BookAdd extends AbstractLibraryForm {
  public static final String FORM_NAME = "BookAdd.html";
  private VOValueHolder bookHolder = new VOValueHolder();
  private ClientRedirectResponder clientRedirectResponder = new ClientRedirectResponder();
  private HTMLSelect locationId = new HTMLSelect();
  private Trigger saveButton = new Trigger();

  /**
   * Get default URL for this form
   * 
   * @return
   */
  public static String getUrl() {
    return FORM_NAME;
  }

  /**
   * Constructs a new BookAdd and initializes component properties.
   */
  public BookAdd() {
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
    setDocumentURI(RESOURCE_PREFIX + FORM_NAME);
    bookHolder.setAlias("Book");
    this.add(bookHolder);

    locationId.setDisplayPropertyName("description");
    locationId.setListPropertyNames(new String[] {"locationId"});
    locationId.setNullAllowed(false);
    locationId.setPropertyName("locationId");
    locationId.setId("Book__locationId");
    locationId.setValueHolder(bookHolder);
    locationId.setParameter("locationId");
    this.add(locationId);

    saveButton.setParameter("action");
    saveButton.setText("Save");
    saveButton.addTriggerListener(new com.taursys.xml.event.TriggerListener() {
      public void actionPerformed(TriggerEvent e) throws Exception {
        saveButton_actionPerformed(e);
      }
    });
    this.add(saveButton);
    
    clientRedirectResponder.setRedirectURL(MainMenu.getUrl());
  }

  /**
   * One time initialization of the ServletForm.  This method is invoked ONCE.
   * It is the first method invoked by the doGet method.  If the form is
   * recycled, it will NOT be invoked again by doGet.
   */
  protected void initForm() throws java.lang.Exception {
    super.initForm();
    // Setup list of locations - this list does not change
    // so we will do it here once, rather than in openForm
    locationId.setList(new VOCollectionValueHolder(bd.getAllLocations()));
  }

  /**
   * This method is invoked by doGet to open the form.
   * It is invoked after any parameters have been read, but before input
   * values have been read.  Override this method to provide custom
   * behavior such as opening data sources.  There is no need to invoke
   * super.openForm().
   */
  protected void openForm() throws java.lang.Exception {
    super.openForm();
    // Start with a blank value object and set default values
    bookHolder.setValueObject(bd.createDefaultBook());
  }

  /**
   * This method is invoked when the Save button is pressed (action=Save)
   * It will save the value object to the database.
   */
  void saveButton_actionPerformed(TriggerEvent e) throws Exception {
    bd.addBook((BookVO)bookHolder.getValueObject());
    setResponder(clientRedirectResponder);
  }

  // =================================================================================
  // Testing Methods
  // =================================================================================

  /**
   * Testing method to run form. Resulting form is stored in test output folder.
   * 
   * IMPORTANT: Run the Ant target "copy-resources" before running this method
   * the first time, and after any changes to any files in the "web" folder.
   * 
   * @param args
   */
  public static void main(String[] args) throws Exception {
    BookAdd form = new BookAdd();
    form.testFullCycle(TEST_OUTPUT_FOLDER + getUrl());
  }

}
