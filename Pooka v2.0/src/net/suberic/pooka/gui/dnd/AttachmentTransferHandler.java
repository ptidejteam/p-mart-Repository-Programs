package net.suberic.pooka.gui.dnd;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;

import net.suberic.pooka.*;
import net.suberic.pooka.gui.*;

/**
 * A TransferHandler for an attachment window.  This is for ReadMessages,
 * so all that's really supported is exporting the attachment(s).
 */
public class AttachmentTransferHandler extends TransferHandler {

  protected Transferable createTransferable(JComponent c) {
    Attachment attachment = null;
    MessageProxy proxy = null;
    if (c instanceof net.suberic.pooka.gui.AttachmentPane) {
      attachment = ((AttachmentPane) c).getSelectedAttachment();
      proxy = ((AttachmentPane) c).getMessageProxy();
    } else if (c instanceof JTable) {
      try {
	Object o = SwingUtilities.getAncestorOfClass(Class.forName("net.suberic.pooka.gui.AttachmentPane"), c);
	if (o != null ) {
	  attachment = ((AttachmentPane) o).getSelectedAttachment();
	  proxy = ((AttachmentPane) o).getMessageProxy();
	} else {
	  return null;
	}
      } catch ( Exception e) {
	return null;
      }
    } 
    
    if (attachment != null && proxy != null) {
      try {
	return new AttachmentTransferable(attachment, proxy);
      } catch (java.io.IOException ioe) {
	return null;
      }
    } else {
      return null;
    }
    
  }

  public int getSourceActions(JComponent c) {
    return COPY;
  }

  public boolean canImport(JComponent c, DataFlavor[] flavors) {
    return false;
  }

  protected void exportDone(JComponent source, Transferable data, int action) {
    /*
    if (data instanceof AttachmentTransferable) {
      ((AttachmentTransferable) data).completeWrite();
    }
    */
  }
  
}
