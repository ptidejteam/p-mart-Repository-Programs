package net.suberic.pooka.gui;

import javax.swing.*;

/**
 * A simple implementation of ProgressDialog.  This creates and shows a
 * JInternalFramewith a JProgressBar and a cancel button.
 */
public class ProgressInternalDialog extends ProgressDialogImpl {
  
  JInternalFrame dialogFrame = null;
  JDesktopPane desktop = null;

  /**
   * Creates a ProgressDialogImpl with the given minimum, maximum, and
   * current values.
   */
  public ProgressInternalDialog(int min, int max, int current, String title, String message, JDesktopPane desktopPane) {
    initDialog(min, max, current, title, message);
    desktop = desktopPane;
  }

  /**
   * Creates the Dialog in which the ProgressBar will be shown.
   */
  protected void createDialog() {
    dialogFrame = new JInternalFrame(nameLabel.getText(), true, false, false, true);
    dialogFrame.getContentPane().setLayout(new BoxLayout(dialogFrame.getContentPane(), BoxLayout.Y_AXIS));

    dialogFrame.getContentPane().add(nameLabel);
    dialogFrame.getContentPane().add(progressBar);
    dialogFrame.getContentPane().add(buttonPanel);
    
    dialogFrame.pack();
    
  }

  /**
   * Shows the dialog.
   */
  public void show() {
    Runnable runMe = new Runnable() {
	public void run() {
	  desktop.add(dialogFrame);
	  if (desktop instanceof MessagePanel) {
	    dialogFrame.setLocation(((MessagePanel) desktop).getNewWindowLocation(dialogFrame, true));
	  }
	  dialogFrame.setVisible(true);
	}
      };
    if (SwingUtilities.isEventDispatchThread()) 
      runMe.run();
    else
      SwingUtilities.invokeLater(runMe);
  }

  /**
   * Disposes of the dialog.
   */
  public void dispose() {
    Runnable runMe = new Runnable() {
	public void run() {
	  try {
	    dialogFrame.setClosed(true);
	  } catch (java.beans.PropertyVetoException e) {
	  }
	}
      };
    
    if (SwingUtilities.isEventDispatchThread())
      runMe.run();
    else
      SwingUtilities.invokeLater(runMe);
  }

}
