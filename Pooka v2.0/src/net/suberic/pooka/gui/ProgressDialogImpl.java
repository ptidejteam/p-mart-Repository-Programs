package net.suberic.pooka.gui;
import javax.swing.*;
import java.awt.event.*;
import java.util.List;

import net.suberic.util.swing.*;


/**
 * A simple implementation of ProgressDialog.  This creates and shows a
 * dialog with a JProgressBar and a cancel button.
 */
public class ProgressDialogImpl implements ProgressDialog {

  JProgressBar progressBar;
  JDialog dialog;
  boolean cancelled = false;
  int mCurrentValue;
  String mTitle;
  String mMessage;

  JLabel nameLabel;
  JPanel buttonPanel;
  List listenerList = new java.util.ArrayList();

  /**
   * Creates a ProgressDialogImpl with the given minimum, maximum, and
   * current values.
   */
  public ProgressDialogImpl(int min, int max, int current, String title, String message) {
    initDialog(min, max, current,title, message);
  }
  
  /**
   * For subclasses.
   */
  protected ProgressDialogImpl() {

  }

  protected void initDialog(int min, int max, int current, String title, String message) {
    progressBar = new JProgressBar(min, max);
    
    mTitle = title;
    mMessage = message;
    
    nameLabel = new JLabel(mTitle);
    buttonPanel = new JPanel();
    JButton cancelButton = new JButton(net.suberic.pooka.Pooka.getProperty("button.cancel", "Cancel"));
    cancelButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  cancelAction();
	}
      });
    buttonPanel.add(cancelButton);
    
    createDialog();
    
    setValue(current);
  }

  /**
   * Creates the Dialog in which the ProgressBar will be shown.
   */
  protected void createDialog() {
    dialog = new JDialog();
    dialog.getContentPane().setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));

    dialog.getContentPane().add(nameLabel);
    dialog.getContentPane().add(progressBar);
    dialog.getContentPane().add(buttonPanel);
    
    dialog.pack();
    
  }

  /**
   * Sets the minimum value for the progress dialog.
   */
  public void setMinimumValue(int minimum) {
    progressBar.setMinimum(minimum);
  }

  /**
   * Gets the minimum value for the progress dialog.
   */
  public int getMinimumValue() {
    return progressBar.getMinimum();
  }

  /**
   * Sets the maximum value for the progress dialog.
   */
  public void setMaximumValue(int maximum) {
    progressBar.setMaximum(maximum);
  }

  /**
   * Gets the maximum value for the progress dialog.
   */
  public int getMaximumValue() {
    return progressBar.getMaximum();
  }

  /**
   * Sets the current value for the progress dialog.
   */
  public void setValue(int current) {
    mCurrentValue = current;
    if (SwingUtilities.isEventDispatchThread()) {
      if (mCurrentValue != getBarValue())
	progressBar.setValue(mCurrentValue);
    } else
      SwingUtilities.invokeLater(new Runnable() {
	  public void run() {
	    if (mCurrentValue != getBarValue())
	      progressBar.setValue(mCurrentValue);
	  }
	});   
  }

  /**
   * Gets the configured current value for the progress dialog.
   */
  public int getValue() {
    return mCurrentValue;
  }

  /**
   * Gets the actual value for the progress dialog.
   */
  public int getBarValue() {
    return progressBar.getValue();
  }

  /**
   * Gets the title for the progress dialog.
   */
  public String getTitle() {
    return mTitle;
  }

  /**
   * Gets the display message for the progress dialog.
   */
  public String getMessage() {
    return mMessage;
  }
  
  /**
   * Cancels the current action.
   */
  public void cancelAction() {
    cancelled = true;
    for (int i = 0; i < listenerList.size(); i++) {
      ProgressDialogListener current = (ProgressDialogListener) listenerList.get(i);
      current.dialogCancelled();
    }
  }

  /**
   * Adds a ProgressDialogListener.
   */
  public void addCancelListener(ProgressDialogListener listener) {
    listenerList.add(listener);
  }

  /**
   * Returns whether or not this action has been cancelled.
   */
  public boolean isCancelled() {
    return cancelled;
  }

  /**
   * Shows the dialog.
   */
  public void show() {
    if (SwingUtilities.isEventDispatchThread())
      dialog.setVisible(true);
    else
      SwingUtilities.invokeLater(new Runnable() {
	  public void run() {
	    dialog.setVisible(true);
	  }
	});
  }

  /**
   * Disposes of the dialog.
   */
  public void dispose() {
    if (SwingUtilities.isEventDispatchThread())
      dialog.dispose();
    else
      SwingUtilities.invokeLater(new Runnable() {
	  public void run() {
	    dialog.dispose();
	  }
	});
  }
}
