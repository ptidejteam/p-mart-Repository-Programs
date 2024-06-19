package net.suberic.pooka.gui;
import javax.mail.*;
import javax.swing.*;
import java.util.Hashtable;
import java.util.Vector;
import java.awt.event.MouseEvent;
import java.awt.print.*;
import net.suberic.pooka.*;
import net.suberic.util.gui.ConfigurablePopupMenu;
import net.suberic.util.thread.*;

/**
 * This class represents a group of MessageInfo all selected.
 */

public class MultiMessageProxy extends MessageProxy {

  int[] rowNumbers;
  FolderInfo folderInfo;
  
  /**
   * This creates a new MultiMessageProxy from the MessageProxys in 
   * the newMessageInfo array.  These should be the MessageProxy objects
   * which correspond with rows newRowNumbers on FolderWindow
   * newFolderWindow.
   */
  public MultiMessageProxy(int[] newRowNumbers, MessageProxy[] newMessageProxy, FolderInfo newFolderInfo) {
    rowNumbers=newRowNumbers;
    folderInfo = newFolderInfo;
    
    MessageInfo[] newMessageInfo = new MessageInfo[newMessageProxy.length];
    for (int i = 0; i < newMessageProxy.length; i++)
      newMessageInfo[i] = newMessageProxy[i].getMessageInfo();
    
    messageInfo=new MultiMessageInfo(newMessageInfo, folderInfo);
    
    if (folderInfo != null) {
      ActionThread storeThread = folderInfo.getFolderThread();
      
      defaultActions = new Action[] {
        new ActionWrapper(new OpenAction(), storeThread),
        new ActionWrapper(new DeleteAction(), storeThread),
        new ActionWrapper(new MoveAction(), storeThread),
        new ActionWrapper(new CopyAction(), storeThread),
        new ActionWrapper(new PrintAction(), storeThread),
        new ActionWrapper(new CacheMessageAction(), storeThread),
        new ActionWrapper(new MessageFilterAction(), storeThread),
        new ActionWrapper(new SpamAction(), storeThread)
      };
    } else {
      defaultActions = new Action[] {
        new OpenAction(),
        new DeleteAction(),
        new MoveAction(),
        new CopyAction(),
        new PrintAction(),
        new MessageFilterAction(),
        new SpamAction()
      };
    }	    
    
    commands = new Hashtable();
    
    Action[] actions = getActions();
    if (actions != null) {
      for (int i = 0; i < actions.length; i++) {
        Action a = actions[i];
        commands.put(a.getValue(Action.NAME), a);
      }
    }
    
  }
  
  /**
   * This opens up new windows for all of the selected messageInfo.
   */
  public void openWindow() {
    MultiMessageInfo multi = getMulti();
    for (int i = 0; i < multi.getMessageCount(); i++) {
      multi.getMessageInfo(i).getMessageProxy().openWindow();
    }
  }
  
  /**
   * Shows the popupMenu for the MultiMessageProxy.  The definition of this
   * menu comes from the MessageProxy.popupMenu property.
   */
  public void showPopupMenu(JComponent component, MouseEvent e) {
    ConfigurablePopupMenu popupMenu = new ConfigurablePopupMenu();
    if (folderInfo instanceof net.suberic.pooka.cache.CachingFolderInfo  && ! ((net.suberic.pooka.cache.CachingFolderInfo) folderInfo).getCacheHeadersOnly()) {
      popupMenu.configureComponent("MessageProxy.cachingPopupMenu", Pooka.getResources());
    } else {
      popupMenu.configureComponent("MessageProxy.popupMenu", Pooka.getResources());
      
    }
    popupMenu.setActive(getActions());
    popupMenu.show(component, e.getX(), e.getY());
    
  }
  
  /**
   * This sends the message to the printer, first creating an appropriate
   * print dialog, etc.
   */
  
  public void printMessage() {
    PrinterJob job = PrinterJob.getPrinterJob ();
    Book book = new Book ();
    PageFormat pf = job.pageDialog (job.defaultPage ());
    MultiMessageInfo multi = getMulti();
    for (int i = 0; i < getMulti().getMessageCount(); i++) {
      MessagePrinter printer = new MessagePrinter(multi.getMessageInfo(i), book.getNumberOfPages());
      book.append (printer, pf);
    }
    job.setPageable (book);
    final PrinterJob externalJob = job;
    if (job.printDialog ()) {
      Thread printThread = new Thread(new Runnable() {
          public void run() {
            try {
              externalJob.print ();
            }
            catch (PrinterException ex) {
              ex.printStackTrace ();
            }
          }
        }, "printing thread");
      printThread.start();
      
    }
  }
  
  /**
   * Sets the deleteInProgress flag.
   */
  public void setDeleteInProgress(boolean newValue) {
    boolean orig = mDeleteInProgress;
    mDeleteInProgress = newValue;
    if (orig != mDeleteInProgress) {
      MultiMessageInfo multi = getMulti();
      for (int i = 0; i < multi.getMessageCount(); i++) {
        MessageProxy current = multi.getMessageInfo(i).getMessageProxy();
        current.setDeleteInProgress(newValue);
      }
    }
  }

  /**
   * Returns the messageInfo attribute as a MultiMessageInfo.
   */
  public MultiMessageInfo getMulti() {
    return (MultiMessageInfo)messageInfo;
  }
  
}

