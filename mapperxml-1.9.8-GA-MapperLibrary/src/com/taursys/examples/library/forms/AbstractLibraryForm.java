package com.taursys.examples.library.forms;

import java.util.Hashtable;

import com.taursys.examples.library.delegate.ApplicationException;
import com.taursys.examples.library.delegate.LibraryServicesBD;
import com.taursys.html.HTMLComponentFactory;
import com.taursys.model.ModelException;
import com.taursys.model.ModelParseException;
import com.taursys.model.ValueHolder;
import com.taursys.servlet.ServletForm;
import com.taursys.servlet.respond.HTMLResponder;
import com.taursys.xml.TextField;

public abstract class AbstractLibraryForm extends ServletForm {
  public static final String RESOURCE_PREFIX = "resource:///forms/";
  public static final String TEST_OUTPUT_FOLDER = "output/forms/";
  private TextField msg = new TextField();
  private HTMLResponder normalResponder = new HTMLResponder();
  private Hashtable testSessionAttributes = new Hashtable();
  protected LibraryServicesBD bd;

  /**
   * Default constructor
   */
  public AbstractLibraryForm() {
    super();
    try {
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Set component properties here. It is not recommended to put anything other
   * than property settings in here. Any Exception will cause the constructor to
   * fail.
   */
  private void jbInit() throws Exception {
    msg.setId("msg");
    this.add(msg);
  }

  /**
   * One time initialization of the ServletForm. This method is invoked ONCE. It
   * is the first method invoked by the doGet method. If the form is recycled,
   * it will NOT be invoked again by doGet.
   */
  protected void initForm() throws java.lang.Exception {
    super.initForm();
    // Get reference to SHARED/Stateless Business Delegate
    bd = LibraryServicesBD.getInstance();
    // Use HTMLComponentFactory to create components
    HTMLComponentFactory factory = new HTMLComponentFactory();
    factory.createComponents(this, (ValueHolder[]) holders.values().toArray(
        new ValueHolder[] {}), true);
  }

  /**
   * This method is invoked by doGet to open the form. It is invoked after any
   * parameters have been read, but before input values have been read. Override
   * this method to provide custom behavior such as opening data sources. There
   * is no need to invoke super.openForm().
   */
  protected void openForm() throws java.lang.Exception {
    super.openForm();
    msg.setText("");
    setResponder(normalResponder);
  }

  /**
   * Global exception handler for all forms
   * 
   * @param ex
   *          Exception that occurred
   */
  protected void handleException(Exception ex) throws java.lang.Exception {
    if (ex instanceof ModelParseException) {
      setErrorMessage(((ModelParseException) ex).getUserFriendlyMessage());
      sendResponse();
    } else if (ex instanceof ApplicationException) {
      setErrorMessage(ex.getMessage());
      sendResponse();
    } else {
      throw ex;
    }
  }

  /**
   * Set the form message to the given message with normal font/color
   * 
   * @param message
   *          to display on form
   * @throws ModelException
   *           if problem occurs
   */
  public void setMessage(String message) throws ModelException {
    msg.setText(message);
    msg.setAttributeText("class", Style.MSG_NORMAL);
  }

  /**
   * Set the form message to the given message with normal font/color
   * 
   * @param message
   *          to display on form
   * @throws ModelException
   *           if problem occurs
   */
  public void setErrorMessage(String message) throws ModelException {
    msg.setText(message);
    msg.setAttributeText("class", Style.MSG_ERROR);
  }

  /**
   * Store value in session if running in container, else store in Hashtable for
   * testing.
   * 
   * @param key
   * @param value
   */
  protected void setSessionAttribute(String key, Object value) {
    if (getRequest() != null) {
      getRequest().getSession().setAttribute(key, value);
    } else {
      testSessionAttributes.put(key, value);
    }
  }

  /**
   * Fetch value from session if running in container, else fetch from Hashtable
   * for testing.
   * 
   * @param key
   * @return
   */
  protected Object getSessionAttribute(String key) {
    if (getRequest() != null) {
      return getRequest().getSession().getAttribute(key);
    } else {
      return testSessionAttributes.get(key);
    }
  }

  /**
   * Returns true if user has given role
   * 
   * @param roleName to check
   * @return true if user has given role
   */
  public boolean isUserInRole(String roleName) {
    if (getRequest() != null && getRequest().getUserPrincipal() != null ) {
      return getRequest().isUserInRole(roleName);
    } else {
      return true;
    }
  }
  
  // =========================================================================
  // MapperXML overrides - may no longer be needed after future enhancements
  // =========================================================================

  private Hashtable holders = new Hashtable();

  public void add(ValueHolder holder) {
    if (holder.getAlias() != null && holder.getAlias().length() > 0) {
      holders.put(holder.getAlias(), holder);
    } else {
      System.err.println("ValueHolder does not have an alias set. "
          + "Not added to container");
    }
  }

}
