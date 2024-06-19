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
package com.taursys.examples.library;

import javax.sql.DataSource;

import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.InputSource;

import com.taursys.html.HTMLComponentFactory;
import com.taursys.html.HTMLSelect;
import com.taursys.model.ModelException;
import com.taursys.model.VOCollectionValueHolder;
import com.taursys.model.VOValueHolder;
import com.taursys.model.ValueHolder;
import com.taursys.servlet.ServletForm;
import com.taursys.servlet.respond.ClientRedirectResponder;
import com.taursys.servlet.respond.HTMLResponder;
import com.taursys.xml.TextField;
import com.taursys.xml.Trigger;
import com.taursys.xml.event.TriggerEvent;

/**
 * Servlet Form for adding books. This for uses the HTMLComponentFactory to
 * create most of the components and bind them.  The HTMLSelect component is
 * created manually since we need to customize its properties (the list).
 * @author
 * @version 1.0
 */
public class BookAdd extends ServletForm {
  VOValueHolder bookHolder = new VOValueHolder();
  BookDAO dao = new BookDAO();
  HTMLResponder htmlResponder = new HTMLResponder();
  ClientRedirectResponder clientRedirectResponder = new ClientRedirectResponder();
  HTMLSelect locationId = new HTMLSelect();
  Trigger saveButton = new Trigger();
  TextField errorMessage = new TextField();
  private static String NULL_TITLE_MSG =
    "ORA-01400: cannot insert NULL into (\"LIBRARIAN\".\"BOOK\".\"TITLE\")\n";
  private static String NULL_CATALOG_NO_MSG =
    "ORA-01400: cannot insert NULL into (\"LIBRARIAN\".\"BOOK\".\"CATALOG_NO\")\n";
  private static String NULL_DATE_ADDED_MSG =
    "ORA-01400: cannot insert NULL into (\"LIBRARIAN\".\"BOOK\".\"DATE_ADDED\")\n";
  private static String NULL_COST_MSG =
    "ORA-01400: cannot insert NULL into (\"LIBRARIAN\".\"BOOK\".\"COST\")\n";

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
    bookHolder.setAlias("Book");

    locationId.setDisplayPropertyName("description");
    locationId.setListPropertyNames(new String[] {"locationId"});
    locationId.setNullDisplay("--choose location--");
    locationId.setPropertyName("locationId");
    locationId.setId("Book__locationId");
    locationId.setValueHolder(bookHolder);
    locationId.setParameter("locationId");

    saveButton.setParameter("action");
    saveButton.setText("Save");
    saveButton.addTriggerListener(new com.taursys.xml.event.TriggerListener() {
      public void actionPerformed(TriggerEvent e) throws Exception {
        saveButton_actionPerformed(e);
      }
    });

    errorMessage.setId("errorMessage");

    clientRedirectResponder.setServletForm(this);
    clientRedirectResponder.setRedirectURL("MainMenu.sf");
    htmlResponder.setServletForm(this);

    this.add(locationId);
    this.add(saveButton);
    this.add(errorMessage);
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
        getClass().getResourceAsStream("BookAdd.html"));
    parser.parse(is);
    this.setDocument(parser.getDocument());
    // Set the BookDAO datasource
    dao.setDataSource(
        (DataSource)getRequest().getAttribute("dataSource"));
    // Setup list of locations - this list does not change
    // so we will do it here once, rather than in openForm
    LocationDAO dao = new LocationDAO(
        (DataSource)getRequest().getAttribute("dataSource"));
    VOCollectionValueHolder listHolder = new VOCollectionValueHolder();
    listHolder.setCollection(dao.getAll());
    locationId.setList(listHolder);
    // Auto-create remaining display components
    HTMLComponentFactory.getInstance().createComponents(this,
        new ValueHolder[] {bookHolder});
  }

  /**
   * This method is invoked by doGet to open the form.
   * It is invoked after any parameters have been read, but before input
   * values have been read.  Override this method to provide custom
   * behavior such as opening data sources.  There is no need to invoke
   * super.openForm().
   */
  protected void openForm() throws java.lang.Exception {
    // Start with a blank value object and set default values
    BookVO vo = new BookVO();
    vo.setLocationId(1);
    bookHolder.setValueObject(vo);
    // Default the response to just sending back the page
    setResponder(htmlResponder);
    // Clear error message
    errorMessage.setText("");
  }

  /**
   * This method is invoked when the Save button is pressed (action=Save)
   * It will save the value object to the database.
   */
  void saveButton_actionPerformed(TriggerEvent e) throws Exception {
    try {
      dao.create((BookVO)bookHolder.getValueObject());
      setResponder(clientRedirectResponder);
    } catch (DAOSQLException ex) {
      Throwable cause = ex.getCause();
      String msg = cause.getMessage();
      // See if this is a problem the user can fix
      // if so, give a user-friendly message
      if (msg.equals(NULL_TITLE_MSG)) {
        errorMessage.setText("You must provide a Title for the Book");
      } else if (msg.equals(NULL_CATALOG_NO_MSG)) {
        errorMessage.setText("You must provide a Catalog No for the Book");
      } else if (msg.equals(NULL_DATE_ADDED_MSG)) {
        errorMessage.setText("You must provide a Date Added for the Book");
      } else if (msg.equals(NULL_COST_MSG)) {
        errorMessage.setText("You must provide a Cost for the Book");
      } else {
        // If user cannot fix, rethrow the exception
        throw ex;
      }
    }
  }

  /**
   * This method is invoked whenever an exception occurs within doGet.
   * Override this method to provide custom exception handling behavior.
   * Throwing an exception will delegate the exception handling to the
   * caller of the doGet method.
   * The default behavior of this method is to simply re-throw the exception.
   */
  protected void handleException(Exception ex) throws Exception {
    // Check to see if user can deal with problem
    if (ex instanceof com.taursys.model.ModelException) {
      errorMessage.setText(((ModelException)ex).getUserFriendlyMessage());
      sendResponse();
    } else {
      // If user cannot fix problem, rethrow the Exception
      throw ex;
    }
  }
}
