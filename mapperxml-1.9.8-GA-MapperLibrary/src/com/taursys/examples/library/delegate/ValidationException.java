package com.taursys.examples.library.delegate;

/**
 * Indicates that required information that the user supplied is
 * missing or not valid.
 * 
 * @author marty
 */
public class ValidationException extends ApplicationException {
	
  public ValidationException() {
    super();
  }

  public ValidationException(String message) {
    super(message);
  }

  public ValidationException(Throwable cause) {
    super(cause);
  }

  public ValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}
