package net.suberic.pooka.gui.dnd;

import net.suberic.pooka.*;
import net.suberic.pooka.gui.*;

import java.awt.datatransfer.*;
import java.util.*;

import java.io.*;

/**
 * A Transferable version of a MessageProxy.
 *
 * Note that, since this class requires that the object not get deleted until
 * a paste is done, we implement a system which delays the removal until 
 * after the full transfer is completed.  See <code>importDone</code> and
 * <code>actionType</code>.
 */
public class MessageProxyTransferable implements Transferable {

  // DataFlavor information
  public static DataFlavor sMessageProxyDataFlavor = null;
  static {
    try {
      sMessageProxyDataFlavor = new DataFlavor(Class.forName("net.suberic.pooka.gui.MessageProxy"), "MessageProxy");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  MessageProxy mMessageProxy = null;
  int mActionType = javax.swing.TransferHandler.COPY;
  boolean mImportDone = false;
  
  public MessageProxyTransferable(MessageProxy pMessageProxy) {
    setMessageProxy(pMessageProxy);
  }
  
  public DataFlavor[] getTransferDataFlavors() {
    return new DataFlavor[] {
      sMessageProxyDataFlavor
    };
  }

  public boolean isDataFlavorSupported(DataFlavor flavor) {
    if (flavor == sMessageProxyDataFlavor)
      return true;
    else 
      return false;
  }

  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    if (flavor == sMessageProxyDataFlavor) {
      return mMessageProxy;
      /*
    } else if (flavor != null && flavor.isFlavorJavaFileListType()) {
      ArrayList returnValue = new ArrayList();
      MessageInfo info = mMessageProxy.getMessageInfo();
      if (info instanceof MultiMessageInfo) {
	MultiMessageInfo multi = (MultiMessageInfo) info;
	for (int i = 0; i < multi.getMessageCount(); i++) {
	  returnValue.add(extractMessageInfo(multi.getMessageInfo(i)));
	}
      } else {
	returnValue.add(extractMessageInfo(info));
      }

      return returnValue;
      */
    } else {
      throw new UnsupportedFlavorException(flavor);
    }
  }

  /**
   * Extracts the given MessageInfo into a file.
   */
  private File extractMessageInfo(MessageInfo info) throws java.io.IOException {
    try {
      String name = "pooka_message";
      try {
	javax.mail.Message m = info.getMessage();
	if (m instanceof net.suberic.pooka.UIDMimeMessage) {
	  name = "message_" + ((net.suberic.pooka.UIDMimeMessage) m).getUID() ;
	}  else {
	  name = "message_" + m.getMessageNumber();
	}
      } catch (Exception me) {
	// ignore.
      }
      File f = DndUtils.createTemporaryFile(name);
      info.saveMessageAs(f);
      return f;
    } catch (javax.mail.MessagingException me) {
      IOException ioe = new IOException("Error saving file");
      ioe.initCause(me);
      throw ioe;
    }
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
