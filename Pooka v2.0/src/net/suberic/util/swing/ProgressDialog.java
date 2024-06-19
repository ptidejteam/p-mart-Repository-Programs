package net.suberic.util.swing;

/**
 * An interface for any UI component which displays the progress for a
 * particular action, along with an optional way to cancel the action.
 */
public interface ProgressDialog {
  
  /**
   * Sets the minimum value for the progress dialog.
   */
  public void setMinimumValue(int minimum);

  /**
   * Gets the minimum value for the progress dialog.
   */
  public int getMinimumValue();

  /**
   * Sets the maximum value for the progress dialog.
   */
  public void setMaximumValue(int maximum);

  /**
   * Gets the maximum value for the progress dialog.
   */
  public int getMaximumValue();

  /**
   * Sets the current value for the progress dialog.
   */
  public void setValue(int value);

  /**
   * Gets the current value for the progress dialog.
   */
  public int getValue();

  /**
   * Gets the title for the progress dialog.
   */
  public String getTitle();

  /**
   * Gets the display message for the progress dialog.
   */
  public String getMessage();
  
  /**
   * Cancels the current action.
   */
  public void cancelAction();

  /**
   * Adds a cancel action listener.
   */
  public void addCancelListener(ProgressDialogListener listener);

  /**
   * Returns whether or not this action has been cancelled.
   */
  public boolean isCancelled();

  /**
   * Shows the dialog.
   */
  public void show();

  /**
   * Disposed of the dialog.
   */
  public void dispose();

}
