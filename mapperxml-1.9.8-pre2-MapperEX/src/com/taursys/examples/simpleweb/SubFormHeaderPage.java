package com.taursys.examples.simpleweb;

import com.taursys.servlet.ServletForm;

/**
 * Servlet Form
 * @author ${_author}
 * @version 2.0
 */
public class SubFormHeaderPage extends ServletForm {

  /**
   * Constructs a new SubFormHeaderPage and initializes component properties.
   */
  public SubFormHeaderPage() {
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
    this.setDocumentURI("resource:///forms/SubFormHeaderPage.html");
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
  }

  /**
   * Send the appropriate response. It is invoked by doGet following the
   * dispatchActions method.  This method invokes the current Responder's
   * respond method to provide the appropriate response.
   * Change the Responder to provide custom response.
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
