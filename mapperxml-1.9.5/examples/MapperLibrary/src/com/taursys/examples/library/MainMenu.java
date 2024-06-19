package com.taursys.examples.library;

import com.taursys.servlet.*;
import com.taursys.util.DataTypes;
import org.xml.sax.InputSource;
import org.apache.xerces.parsers.DOMParser;
import com.taursys.model.*;
import com.taursys.xml.*;
import com.taursys.html.*;


/**
 * Servlet Form
 * @author ${author}
 * @version 1.0
 */
public class MainMenu extends ServletForm {

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
        getClass().getResourceAsStream("MainMenu.html"));
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
  }

  /**
   * Send the appropriate response. It is invoked by doGet following the
   * dispatchActions method.  The default behavior is to dispatch a render
   * message to all components, set the response type to text/html and
   * invoke the xmlWriter to write to the response object.
   */
  protected void sendResponse() throws java.lang.Exception {
    super.sendResponse();
  }

  /**
   * Closes the form and any resources it may have opened.  This method is
   * the final method invoked by the doGet method (even if an Exception
   * occurred).  The method currently does nothing, so there is no need to
   * invoke super.closeForm().
   */
  protected void closeForm() throws java.lang.Exception {
  }

  /**
   * This method is invoked whenever an exception occurs within doGet.
   * Override this method to provide custom exception handling behavior.
   * Throwing an exception will delegate the exception handling to the
   * caller of the doGet method.
   * The default behavior of this method is to simply re-throw the exception.
   */
  protected void handleException(Exception ex) throws java.lang.Exception {
    throw ex;
  }

  /**
   * Returns true to indicate that this form can be re-used.
   * If the form cannot be reused, override this method and return false.
   * Override this method to provide custom behavior to recycle this form
   * for future re-use.  This is invoked by the ServletFormFactory before
   * the form is added to the pool of available forms.
   */
  public boolean recycle() {
    return super.recycle();
  }
}
