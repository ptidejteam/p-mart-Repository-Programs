package net.suberic.pooka.gui;

/**
 * This defines a set of methods that can be used to display errors.  Mainly
 * it's used so that we can have a consistent interface to error handling.
 *
 * Note that most implementations of this class should try to make these
 * methods thread safe; that is, if the implemenation of showError requires
 * a dialog window to be shown on the AWTEventThread, the implementation of
 * the ErrorHandler should be expected to handle that.
 */
public interface ErrorHandler {

  public void showError(String errorMessage);
  
  public void showError(String errorMessage, String title);
  
  public void showError(String errorMessage, String title, Exception e);
  
  public void showError(String errorMessage, Exception e);

  public String formatMessage(String message);
}
