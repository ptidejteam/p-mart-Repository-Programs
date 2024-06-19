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

import java.util.HashMap;

import com.taursys.model.VOCollectionValueHolder;
import com.taursys.xml.Parameter;
import com.taursys.xml.Trigger;
import com.taursys.xml.event.TriggerEvent;

/**
 * Servlet Form for listing library books. This uses the HTMLComponentFactory
 * to create most of the components on the form.
 * @author ${author}
 * @version 1.0
 */
public class BookList extends AbstractLibraryForm {
  public static final String FORM_NAME = "BookList.html";
  private VOCollectionValueHolder bookHolder = new VOCollectionValueHolder();
  private Trigger defaultButton = new Trigger();
  private Trigger deleteButton = new Trigger();
  private Trigger searchButton = new Trigger();
  private Parameter catalogNoParm = new Parameter();
  private Parameter searchKey = new Parameter();

  /**
   * Get default URL for this form
   * 
   * @return
   */
  public static String getUrl() {
    return FORM_NAME;
  }


  /**
   * Constructs a new BookList and initializes component properties.
   */
  public BookList() {
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

    defaultButton.setParameter("action");
    defaultButton.setText("Default");
    defaultButton.setDefaultTrigger(true);
    defaultButton.addTriggerListener(new com.taursys.xml.event.TriggerListener() {
      public void actionPerformed(TriggerEvent e) throws Exception {
        defaultButton_actionPerformed(e);
      }
    });
    this.add(defaultButton);

    deleteButton.setParameter("action");
    deleteButton.setText("Delete");
    deleteButton.addTriggerListener(new com.taursys.xml.event.TriggerListener() {
      public void actionPerformed(TriggerEvent e) throws Exception {
        deleteButton_actionPerformed(e);
      }
    });
    this.add(deleteButton);

    catalogNoParm.setParameter("catalogNo");
    this.add(catalogNoParm);
    
    searchKey.setParameter("searchKey");
    this.add(searchKey);
    
    searchButton.setParameter("action");
    searchButton.setText("Search");
    searchButton.addTriggerListener(new com.taursys.xml.event.TriggerListener() {
      public void actionPerformed(TriggerEvent e) throws Exception {
        searchButton_actionPerformed(e);
      }
    });
    this.add(searchButton);
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
    super.openForm();
    // clear out current book list
    bookHolder.clear();
  }

  /**
   * The default button is "pressed" when no action is specified
   * This will list all books
   */
  void defaultButton_actionPerformed(TriggerEvent e) throws Exception {
    bookHolder.setCollection(bd.getAllBooks());
  }

  /**
   * The search button will list books matching the search key
   */
  void searchButton_actionPerformed(TriggerEvent e) throws Exception {
    bookHolder.setCollection(bd.getAllMatchingBooks(searchKey.getText()));
  }

  /**
   * The delete button will delete the selected book.
   */
  void deleteButton_actionPerformed(TriggerEvent e) throws Exception {
    bd.deleteBook(catalogNoParm.getText());
    bookHolder.setCollection(bd.getAllBooks());
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
    BookList form = new BookList();
    HashMap map = new HashMap();
    map.put("searchKey", new String[]{"programming"});
    map.put("action", new String[]{"Search"});
    form.setParameterMap(map);
    form.testFullCycle(TEST_OUTPUT_FOLDER + getUrl());
  }

}
