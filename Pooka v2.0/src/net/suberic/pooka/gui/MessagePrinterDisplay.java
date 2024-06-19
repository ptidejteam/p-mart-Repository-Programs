package net.suberic.pooka.gui;

import javax.swing.*;
import javax.print.*;
import javax.print.event.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import net.suberic.pooka.*;

/**
 * Displays the current status of a print job in Pooka.
 */
public class MessagePrinterDisplay implements PrintJobListener {

  public static int RENDERING = 0;
  public static int PAGINATING = 5;
  public static int PRINTING = 10;

  public static int CANCELED = -5;
  public static int FAILED = -10;

  MessagePrinter mPrinter = null;
  DocPrintJob mJob = null;

  int mStatus = RENDERING;

  int mCurrentPage = 0;

  int mPageCount = 0;

  String mCurrentDoc = "";

  String mTitle;

  JTextPane mDisplayPane = null;
  JButton mOkButton = null;
  JButton mCancelButton = null;

  boolean mInternal = false;

  JDialog mDialog = null;
  JInternalFrame mDialogFrame = null;

  Object mSource;

  /**
   * Creates a new MessagePrinterDisplay using the given MessagePrinter
   * and the given DocPrintJob.
   */
  public MessagePrinterDisplay(MessagePrinter pPrinter, DocPrintJob pJob, Object pSource) {
    mPrinter = pPrinter;
    mJob = pJob;
    mSource = pSource;
    mInternal = checkInternal();
    mPrinter.setDisplay(this);
  }

  /**
   * Refreshes the display pane to show the current printing status.
   */
  public void updateDisplayPane() {
    StringBuffer displayMessage = new StringBuffer();
    displayMessage.append(getStatusString() + ":  ");
    displayMessage.append(mCurrentDoc);
    displayMessage.append("\r\n\r\n");
    if (getStatus() > RENDERING) {
      displayMessage.append(Pooka.getProperty("PrinterDisplay.page", "Page"));
      displayMessage.append("  ");
      displayMessage.append(mCurrentPage);
      if (getStatus() > PAGINATING) {
	displayMessage.append(" ");
	displayMessage.append(Pooka.getProperty("PrinterDisplay.of", "of"));
	displayMessage.append(" ");
	displayMessage.append(mPageCount);
      }
      displayMessage.append("\r\n");
    }

    final String msg = displayMessage.toString();
    if (SwingUtilities.isEventDispatchThread()) {
      mDisplayPane.setText(msg);
      mDisplayPane.repaint();
    } else {
      SwingUtilities.invokeLater(new Runnable() {
	  public void run() {
	    mDisplayPane.setText(msg);
	    mDisplayPane.repaint();
	  }
	});
    }
  }

  /**
   * Sets the current page.
   */
  public void setCurrentPage(int pCurrentPage) {
    mCurrentPage = pCurrentPage;
    updateDisplayPane();
  }
  
  /**
   * Sets the page count.
   */
  public void setPageCount(int pPageCount) {
    mPageCount = pPageCount;
    updateDisplayPane();
  }
  
  /**
   * Gets the current status.
   */
  public int getStatus() {
    return mStatus;
  }

  /**
   * Sets the current status.
   */
  public void setStatus(int pStatus) {
    mStatus = pStatus;
    updateDisplayPane();
  }

  /**
   * Checks to see if this is an internal dialog or not.
   */
  private boolean checkInternal() {
    if (mSource instanceof JComponent) {
      PookaUIFactory uiFactory = Pooka.getUIFactory();
      if (uiFactory instanceof PookaDesktopPaneUIFactory) {
	JComponent sourceComponent = (JComponent) mSource;
	if (((PookaDesktopPaneUIFactory) uiFactory).isInMainFrame(sourceComponent))
	  return true;
      }
    }

    return false;
    
  }

  /**
   * Shows the MessagePrinterDisplay.
   */
  public void show() {
    mDisplayPane = new JTextPane();
    mDisplayPane.setBorder(BorderFactory.createEtchedBorder());
    JLabel jl = new JLabel();
    mDisplayPane.setBackground(jl.getBackground());
    mDisplayPane.setFont(jl.getFont());
    java.awt.Insets newMargin = new java.awt.Insets(10,10,10,10);
    mDisplayPane.setMargin(newMargin);

    if (mInternal) {
      mDialogFrame = new JInternalFrame("Printing", true, false, false, true);
      mDialogFrame.getContentPane().setLayout(new BoxLayout(mDialogFrame.getContentPane(), BoxLayout.Y_AXIS));
      
      mDialogFrame.getContentPane().add(mDisplayPane);
      if (mJob instanceof CancelablePrintJob) {
	Box buttonBox = createButtonBox();
	mDialogFrame.getContentPane().add(buttonBox);
      }
      updateDisplayPane();
      mDialogFrame.pack();

      mDialogFrame.setSize(Math.max(mDialogFrame.getPreferredSize().width, 300), Math.max(mDialogFrame.getPreferredSize().height, 200));

      MessagePanel mp = ((PookaDesktopPaneUIFactory)Pooka.getUIFactory()).getMessagePanel();
      mp.add(mDialogFrame);
      mDialogFrame.setLocation(mp.getNewWindowLocation(mDialogFrame, true));
      mDialogFrame.setVisible(true);
      
    } else {
      if (mSource instanceof JComponent && SwingUtilities.getWindowAncestor((JComponent) mSource) instanceof java.awt.Frame) {
	mDialog = new JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor((JComponent) mSource));
      } else {
	mDialog = new JDialog();
      }
      mDialog.getContentPane().setLayout(new BoxLayout(mDialog.getContentPane(), BoxLayout.Y_AXIS));
      
      mDialog.getContentPane().add(mDisplayPane);

      if (mJob instanceof CancelablePrintJob) {
	Box buttonBox = createButtonBox();
	mDialog.getContentPane().add(buttonBox);
      }
      updateDisplayPane();
      mDialog.pack();
      mDialog.setSize(Math.max(mDialog.getPreferredSize().width, 300), Math.max(mDialog.getPreferredSize().height, 200));
      mDialog.setVisible(true);
    }

  }

  /**
   * Cancels the printjob.
   */
  public void cancel() {
    if (mJob instanceof CancelablePrintJob) {
      try {
	((CancelablePrintJob) mJob).cancel();
      } catch (PrintException e) {
	showError(Pooka.getProperty("PrintDisplay.message.errorCanceling", "Error canceling job:  "), e);
      }
    }
  }

  /**
   * Creates the buttonBox.
   */
  Box createButtonBox() {
    Box returnValue = new Box(BoxLayout.X_AXIS);
    JButton cancelButton = new JButton(Pooka.getProperty("button.cancel", "Cancel"));
    returnValue.add(Box.createHorizontalGlue());
    returnValue.add(cancelButton);
    returnValue.add(Box.createHorizontalGlue());
    cancelButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  cancel();
	}
      });
    
    return returnValue;
    
  }

  /**
   * Closes the MessageDisplay.
   */
  public void dispose() {
    if (SwingUtilities.isEventDispatchThread()) {
      if (mInternal) {
	try {
	  mDialogFrame.setClosed(true);
	} catch (java.beans.PropertyVetoException e) {
	}
      } else {
	mDialog.dispose();
      }
    } else {
      SwingUtilities.invokeLater(new Runnable() {
	  public void run() {
	    if (mInternal) {
	      try {
		mDialogFrame.setClosed(true);
	      } catch (java.beans.PropertyVetoException e) {
	      }
	    } else {
	      mDialog.dispose();
	    }
	  }
	});
    }
  }

  /**
   * Gets the string representation of the current status.
   */
  public String getStatusString() {
    if (mStatus == RENDERING) {
      return Pooka.getProperty("PrintDisplay.status.rendering", "Rendering");
    } else if (mStatus == PAGINATING) {
      return Pooka.getProperty("PrintDisplay.status.paginating", "Paginating");
    } else {
      return Pooka.getProperty("PrintDisplay.status.printing", "Printing");
    }
  }

  /**
   * Shows an error.
   */
  public void showError(String text) {
    Pooka.getUIFactory().showError(text);
  }

  /**
   * Shows an error.
   */
  public void showError(String text, Exception e) {
    Pooka.getUIFactory().showError(text, e);
  }

  // PrintJobListener

  public void printDataTransferCompleted(PrintJobEvent pje) {
    // do nothing.
  }
  
  public void printJobCompleted(PrintJobEvent pje) {
    // do nothing.
  }
  
  public void printJobCanceled(PrintJobEvent pje) {
    setStatus(CANCELED);
    showError(Pooka.getProperty("PrintDisplay.message.canceled", "Canceled."));
    dispose();
  }

  public void printJobFailed(PrintJobEvent pje) {
    if (getStatus() > CANCELED) {
      setStatus(FAILED);
      showError(Pooka.getProperty("PrintDisplay.message.failed", "Failed."));
      dispose();
    }
  }
  
  public void printJobNoMoreEvents(PrintJobEvent pje) {
    dispose();
  }
  
  public void printJobRequiresAttention(PrintJobEvent pje) {
    showError(Pooka.getProperty("PrintDisplay.message.needsAttention", "Needs attention."));
  }

}
