package net.suberic.pooka.gui.dnd;

import net.suberic.pooka.*;
import net.suberic.pooka.gui.*;

import java.awt.datatransfer.*;
import java.io.IOException;
import java.io.File;

public class AttachmentTransferable implements Transferable {
  
  Attachment mAttachment = null;
  MessageProxy mMessageProxy = null;
  File mTmpFile = null;
  boolean mFileWritten = false;

  public AttachmentTransferable(Attachment pAttachment, MessageProxy pMessageProxy) throws java.io.IOException {
    setAttachment(pAttachment);
    setMessageProxy(pMessageProxy);
    // create a temp file in the tmp directory.

    String filename = pAttachment.getName();
    if (filename == null || filename.length() == 0) {
      filename = "Attachment";
    }
    mTmpFile = DndUtils.createTemporaryFile(filename);
  }

  public DataFlavor[] getTransferDataFlavors() {
    return new DataFlavor[] {
      DataFlavor.javaFileListFlavor
    };
  }

  public boolean isDataFlavorSupported(DataFlavor flavor) {
    if (flavor == DataFlavor.javaFileListFlavor)
      return true;
    else 
      return false;
  }

  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    if (isDataFlavorSupported(flavor)) {
      
      java.util.LinkedList list = new java.util.LinkedList();

      list.add(mTmpFile);

      if (! mFileWritten) {
	writeFile();
	mFileWritten = true;
      }
      return list;
    } else {
      throw new UnsupportedFlavorException(flavor);
    }
  }

  /**
   * Writes the File object for this attachment.
   */
  public void writeFile() {
    AttachmentHandler handler = new AttachmentHandler(mMessageProxy);
    try {
      handler.saveFileAs(mAttachment, mTmpFile);
    } catch (IOException exc) {
      handler.showError(Pooka.getProperty("error.SaveFile", "Error saving file") + ":\n", Pooka.getProperty("error.SaveFile", "Error saving file"), exc);
    }
  }

  /**
   * Returns the Attachment.
   */
  public Attachment getAttachment() {
    return mAttachment;
  }
 
  /**
   * Sets the Attachment.
   */
  public void setAttachment(Attachment pAttachment) {
    mAttachment = pAttachment;
  }

  /**
   * Returns the MessageProxy.
   */
  public MessageProxy getMessageProxy() {
    return mMessageProxy;
  }
  
  /**
   * Sets the MessageProxy.
   */
  public void setMessageProxy(MessageProxy pMessageProxy) {
    mMessageProxy = pMessageProxy;
  }
  
  
}
