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


/**
 * Servlet Form for the main menu.
 * @author Marty Phelan
 * @version 1.0
 */
public class MainMenu extends AbstractLibraryForm {
  public static final String FORM_NAME = "MainMenu.html";

  /**
   * Get default URL for this form
   * 
   * @return
   */
  public static String getUrl() {
    return FORM_NAME;
  }

  /**
   * Constructs a new MainMenu and initializes component properties.
   */
  public MainMenu() {
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
  }

  /**
   * One time initialization of the ServletForm.  This method is invoked ONCE.
   * It is the first method invoked by the doGet method.  If the form is
   * recycled, it will NOT be invoked again by doGet.
   */
  protected void initForm() throws java.lang.Exception {
    super.initForm();
  }

  protected void openForm() throws java.lang.Exception {
    super.openForm();
    String visitTime = (String)getSessionAttribute("visitTime");
    if (visitTime == null) {
      visitTime = new java.util.Date().toString();
      setSessionAttribute("visitTime", visitTime);
    }
    setErrorMessage("You logged in at " + visitTime);
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
    MainMenu form = new MainMenu();
    form.testFullCycle(TEST_OUTPUT_FOLDER + getUrl());
  }

}
