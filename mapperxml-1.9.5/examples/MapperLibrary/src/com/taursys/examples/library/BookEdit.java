package com.taursys.examples.library;

import com.taursys.servlet.*;
import com.taursys.util.DataTypes;
import org.xml.sax.InputSource;
import org.apache.xerces.parsers.DOMParser;
import com.taursys.model.*;
import com.taursys.xml.*;
import com.taursys.html.*;
import com.taursys.servlet.respond.*;
import javax.sql.DataSource;
import com.taursys.xml.event.*;
import java.sql.*;

/**
 * Servlet Form
 * @author ${author}
 * @version 1.0
 */
public class BookEdit extends ServletForm {
  VOValueHolder bookHolder = new VOValueHolder();
  HTMLResponder htmlResponder = new HTMLResponder();
  ClientRedirectResponder clientRedirectResponder = new ClientRedirectResponder();
  TextField catalogNo = new TextField();
  HTMLInputText hiddenCatalogNo = new HTMLInputText();
  HTMLInputText title = new HTMLInputText();
  HTMLInputText keywords = new HTMLInputText();
  HTMLInputText dateAdded = new HTMLInputText();
  HTMLInputText cost = new HTMLInputText();
  HTMLSelect locationId = new HTMLSelect();
  Trigger saveButton = new Trigger();
  TextField errorMessage = new TextField();
  BookDAO dao = new BookDAO();
  private static String NULL_TITLE_MSG =
    "ORA-01407: cannot update (\"LIBRARIAN\".\"BOOK\".\"TITLE\") to NULL\n";
  private static String NULL_DATE_ADDED_MSG =
    "ORA-01407: cannot update (\"LIBRARIAN\".\"BOOK\".\"DATE_ADDED\") to NULL\n";
  private static String NULL_COST_MSG =
    "ORA-01407: cannot update (\"LIBRARIAN\".\"BOOK\".\"COST\") to NULL\n";
  Trigger revertButton = new Trigger();

  /**
   * Constructs a new EditForm and initializes component properties.
   */
  public BookEdit() {
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
    catalogNo.setPropertyName("catalogNo");
    catalogNo.setValueHolder(bookHolder);
    catalogNo.setId("catalogNo");
    hiddenCatalogNo.setParameter("catalogNo");
    hiddenCatalogNo.setId("hiddenCatalogNo");
    hiddenCatalogNo.setEarlyInputNotify(true);
    title.setParameter("title");
    title.setPropertyName("title");
    title.setValueHolder(bookHolder);
    title.setId("title");
    keywords.setParameter("keywords");
    keywords.setPropertyName("keywords");
    keywords.setValueHolder(bookHolder);
    keywords.setId("keywords");
    dateAdded.setFormat(java.text.SimpleDateFormat.getInstance());
    dateAdded.setFormatPattern("MM/dd/yyyy");
    dateAdded.setParameter("dateAdded");
    dateAdded.setPropertyName("dateAdded");
    dateAdded.setValueHolder(bookHolder);
    dateAdded.setId("dateAdded");
    cost.setParameter("cost");
    cost.setPropertyName("cost");
    cost.setValueHolder(bookHolder);
    cost.setId("cost");
    locationId.setDisplayPropertyName("description");
    locationId.setListPropertyNames(new String[] {"locationId"});
    locationId.setNullDisplay("--choose location--");
    locationId.setPropertyName("locationId");
    locationId.setId("locationId");
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
    revertButton.setParameter("action");
    revertButton.setText("Revert");
    revertButton.setDefaultTrigger(true);
    revertButton.addTriggerListener(new com.taursys.xml.event.TriggerListener() {
      public void actionPerformed(TriggerEvent e) throws Exception {
        revertButton_actionPerformed(e);
      }
    });
    this.add(catalogNo);
    this.add(hiddenCatalogNo);
    this.add(title);
    this.add(keywords);
    this.add(dateAdded);
    this.add(cost);
    this.add(locationId);
    this.add(saveButton);
    this.add(errorMessage);
    this.add(revertButton);
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
        getClass().getResourceAsStream("BookEdit.html"));
    parser.parse(is);
    this.setDocument(parser.getDocument());
    // Setup list of locations - this list does not change so we will do it here
    // rather than in openForm
    LocationDAO dao = new LocationDAO(
        (DataSource)getRequest().getAttribute("dataSource"));
    VOCollectionValueHolder listHolder = new VOCollectionValueHolder();
    listHolder.setCollection(dao.getAll());
    locationId.setList(listHolder);
  }

  /**
   * This method is invoked by doGet to open the form.
   * It is invoked after any parameters have been read, but before input
   * values have been read.  Override this method to provide custom
   * behavior such as opening data sources.  There is no need to invoke
   * super.openForm().
   */
  protected void openForm() throws java.lang.Exception {
    // Get datasource for DAO
    dao.setDataSource(
        (DataSource)getRequest().getAttribute("dataSource"));
    // Default the response to just sending back the page
    setResponder(htmlResponder);
    // Clear error message
    errorMessage.setText("");
  }

  /**
   * This method is invoked when NO action is specified or the Revert button is pressed.
   * It will retrieve the indicated book from the database.
   */
  void revertButton_actionPerformed(TriggerEvent e) throws Exception {
    // Retrieve book from database
    bookHolder.setValueObject(dao.getByPrimaryKey(hiddenCatalogNo.getText()));
  }

  /**
   * This method is invoked when the Save button is pressed (action=Save)
   * It will save the value object to the database.
   */
  void saveButton_actionPerformed(TriggerEvent e) throws Exception {
    try {
      dao.updateByPrimaryKey((BookVO)bookHolder.getValueObject());
      setResponder(clientRedirectResponder);
    } catch (DAOSQLException ex) {
      Throwable cause = ex.getCause();
      String msg = cause.getMessage();
      // See if this is a problem the user can fix
      // if so, give a user-friendly message
      if (msg.equals(NULL_TITLE_MSG)) {
        errorMessage.setText("You must provide a Title for the Book");
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
    if (ex instanceof java.lang.NumberFormatException) {
      errorMessage.setText(ex.getMessage());
      sendResponse();
    } else if (ex instanceof com.taursys.model.ModelParseException) {
      errorMessage.setText(ex.getMessage());
      sendResponse();
    } else {
      // If user cannot fix problem, rethrow the Exception
      throw ex;
    }
  }
}
