package com.taursys.examples.library.delegate;

/**
 * ApplicationException is an exception which the user can remedy
 * @author marty
 */
public class ApplicationException extends Exception {

  /**
   * 
   */
  public ApplicationException() {
  }

  /**
   * @param message
   */
  public ApplicationException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public ApplicationException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public ApplicationException(String message, Throwable cause) {
    super(message, cause);
  }

}
