package net.suberic.pooka.gui;

import javax.mail.*;

/**
 * A class that gives a UI for doing interactive authentication.  Behavior
 * is to show a dialog asking for username and password, then to wait while
 * the login is done.  If the login fails, then the application should set
 * an error and redisplay.  If it works, it is the application's job to
 * remove the AuthenticatorUI.
 */
public abstract class AuthenticatorUI extends Authenticator {
  boolean mCancelled = false;
  boolean mShowing = false;
  Exception mException;
  String mErrorMessage;

  /**
   * Shows.
   */
  public abstract void showAuthenticator();

 /**
   * Dispose of this dialog.
   */
  public abstract void disposeAuthenticator();

  /**
   * Sets an error message to display.
   */
  public abstract void setErrorMessage(String pMessage, Exception pException);

  /**
   * Returns whether or not this process was cancelled.
   */
  public boolean isCancelled() {
    return mCancelled;
  }

  /**
   * Sets whether or not this process was cancelled.
   */
  public void setCancelled(boolean pCancelled) {
    mCancelled = pCancelled;
  }

  /**
   * Returns if this Authenticator is showing.
   */
  public boolean isShowing() {
    return mShowing;
  }
}
