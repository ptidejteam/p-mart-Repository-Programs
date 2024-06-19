package net.suberic.pooka.gui;

import javax.swing.*;
import java.util.Vector;
import java.util.List;
import javax.mail.MessagingException;

import net.suberic.pooka.*;

/**
 * This is a popup menu that allows access to the Message's attachments
 * directly from the FolderPanel.
 */
public class AttachmentPopupMenu extends JPopupMenu {
  
  // the root MessageProxy
  MessageProxy mProxy = null;

  // the Attachments
  List mAttachments = null;

  public static int OPEN = 0;
  public static int OPEN_WITH = 5;
  public static int SAVE = 10;

  int mActionType = OPEN;

  /**
   * Creates a new AttachmentPopupMenu from the given MessageProxy.
   */
  public AttachmentPopupMenu(MessageProxy pProxy, int pActionType) {
    mProxy = pProxy;
    mActionType = pActionType;
  }

  /**
   * Loads the attachments for this MessageProxy.
   */
  void loadAttachments(java.awt.event.ActionEvent e) throws MessagingException {
    final MessageInfo mInfo = mProxy.getMessageInfo();
    if (! mInfo.hasLoadedAttachments()) {
      this.setLabel(Pooka.getProperty("AttachmentPopupMenu.notloaded.title", "Loading attachments..."));
      FolderInfo fi = mInfo.getFolderInfo();
      if (fi != null) {
	fi.getFolderThread().addToQueue(new javax.swing.AbstractAction() {
	    public void actionPerformed(java.awt.event.ActionEvent pActionEvent) {
	      try {
		mInfo.loadAttachmentInfo();
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		      try {
			displayAttachments();
		      } catch (MessagingException me) {
			showLoadError(me);
		      }
		    }
		  });
	      } catch (MessagingException me) {
		showLoadError(me);
	      }
	    }
	  }, e, net.suberic.util.thread.ActionThread.PRIORITY_HIGH);
      } else {
	// just do it here.
	mInfo.loadAttachmentInfo();
	displayAttachments();
      }
    } else {
      displayAttachments();
    }
  }

  /**
   * Actually populates the PopupMenu with attachments and actions.
   */
  void displayAttachments() throws MessagingException {
    mAttachments = mProxy.getAttachments();

    for (int i = 0; i < mAttachments.size(); i++) {
      Attachment current = (Attachment) mAttachments.get(i);
      JMenuItem item = new JMenuItem();
      item.setAction(new AttachmentAction(current));
      String label = current.getName();
      if (label == null || label.length() == 0) {
	label = Pooka.getProperty("AttachmentPane.error.FileNameUnavailable", "Unavailable");
      }
      javax.mail.internet.ContentType mimeType = current.getMimeType();
      if (mimeType != null) {
	item.setText(label + " (" + mimeType.getBaseType() + ")");
      } else {
	item.setText(label);
      }
      this.add(item);
    }

    JMenuItem saveAllItem = new JMenuItem();
    saveAllItem.setAction(new SaveAllAction());
    saveAllItem.setText(Pooka.getProperty("AttachmentPane.Actions.SaveAll.Label", "Save All"));
    this.add(saveAllItem);

    if (mActionType == SAVE) {
      this.setLabel(Pooka.getProperty("AttachmentPopupMenu.save.title", "Save Attachment"));
    } else if (mActionType == OPEN_WITH) {
      this.setLabel(Pooka.getProperty("AttachmentPopupMenu.openWith.title", "Open Attachment With..."));
    } else {
      this.setLabel(Pooka.getProperty("AttachmentPopupMenu.open.title", "Open Attachment"));
    }
 
    this.pack();
    this.setSize(this.getMinimumSize());
    if (this.isVisible())
      this.revalidate();
  }

  /**
   * Shows a load error for this PopupMenu.
   */
  public void showLoadError(MessagingException me) {
    final MessagingException fme = me;
    Runnable runMe = new Runnable() {
	public void run() {
	  setLabel(Pooka.getProperty("AttachmentPopupMenu.errorloading.title", "Error loading attachments"));
	  fme.printStackTrace();
	}
      };

    if (SwingUtilities.isEventDispatchThread())
      runMe.run();
    else
      SwingUtilities.invokeLater(runMe);
  }
  
  /**
   * An Action that will do the appropriate action for an Attachment.
   */
  class AttachmentAction extends AbstractAction {
    // the Attachment that will be acted upon
    Attachment mAttachment;

    /**
     * Creates a new AttachmentAction for this Attachment.
     */
    public AttachmentAction(Attachment pAttachment) {
      mAttachment = pAttachment;
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
      AttachmentHandler ah = new AttachmentHandler(mProxy);
      if (mActionType == SAVE) {
	ah.saveAttachment(mAttachment, AttachmentPopupMenu.this);
      } else if (mActionType == OPEN_WITH) {
	ah.openWith(mAttachment);
      } else {
	ah.openAttachment(mAttachment);
      }
    }
  }

  /**
   * An Action that will save all the attachments on a message.
   */
  class SaveAllAction extends AbstractAction {
    /**
     * Creates a new SaveAllAction for this Attachment.
     */
    public SaveAllAction() {
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
      AttachmentHandler ah = new AttachmentHandler(mProxy);
      ah.saveAllAttachments(AttachmentPopupMenu.this);
    }
  }
}
