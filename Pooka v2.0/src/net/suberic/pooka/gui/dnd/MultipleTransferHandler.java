//The contents of this file are subject to the Mozilla Public License Version 1.1
//(the "License"); you may not use this file except in compliance with the
//License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
//
//Software distributed under the License is distributed on an "AS IS" basis,
//WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
//for the specific language governing rights and
//limitations under the License.
//
//The Original Code is "The Columba Project"
//
//The Initial Developers of the Original Code are Frederik Dietz and Timo Stich.
//Portions created by Frederik Dietz and Timo Stich are Copyright (C) 2003.
//
//All Rights Reserved.
package net.suberic.pooka.gui.dnd;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.TransferHandler;


/**
 * A transfer handler that is composed from many transfer handlers.
 * When setting a TransferHandler on a component, the new transfer handler
 * is the only one used in a Drag and Drop action. Most of the times, this is
 * fine but sometimes you want to preserve the behaviour of the component's
 * default transfer handler. You only want to implement one specific data flavor
 * to the component, not redoing those feature already implemented.
 * This class lets a component to have several
 * transfer handlers instead of one.
 * <p>
 * For example, the JTextArea has a transfer handler that supports draging and dropping
 * strings into it by default. Say that you want to add support of DnD of a file into the
 * text. Instead of mimicing behaviour of DnD of strings, you will only implement a
 * transfer handler that supports the file list data flavor. Add these two transfer handler
 * to this class and set this as the component transferhandler and everything is solved.
 * <p>
 * This chain of resposibility object is only good for adding new data flavors when importing, it will
 * not give extra features when dragging from the component to another component. When starting
 * a drag, the first transfer handler is used as the source handler. This is important to have
 * the transfer handler with the most export support implemented, otherwise the component will
 * lose its DnD export features. The default handler is always the first in the list, but can also
 * be set by the corresponding method.
 * <p>
 * The behind the scenes implementation tries to cohere with the design pattern "Chain of responsibility".
 * The actual implementation is internally a list that is ordered, ie TransferHandlers that
 * are added first, is check first. If there are two transfer handlers that supports the same
 * type of data flavors, the first one in the list is the one used.
 * <p>
 * Note that this transfer handler will put an overhead for each transferhandler.
 * @author redsolo
 */
public class MultipleTransferHandler extends TransferHandler {

  private List transferHandlers = new ArrayList();
  
  private TransferHandler dragSourceHandler;

  /**
   * Adds the transfer handler to this handler.
   * @param handler the new handler.
   */
  public void addTransferHandler(TransferHandler handler) {
    if (!transferHandlers.contains(handler)) {
      transferHandlers.add(handler);
    }
    if (dragSourceHandler == null) {
      dragSourceHandler = handler;
    }
  }

  /**
   * Removes the transfer handler from this handler.
   * @param handler the handler.
   */
  public void removeTransferHandler(TransferHandler handler) {
    transferHandlers.remove(handler);
    if (handler == dragSourceHandler) {
      if (transferHandlers.size() > 0) {
	dragSourceHandler = (TransferHandler) transferHandlers.get(0);
      } else {
	dragSourceHandler = null;
      }
    }
  }

  /**
   * Returns a list with all transfer handlers.
   * @return a list with all transfer handlers.
   */
  public List getTransferHandlers() {
    return (List) ((ArrayList) transferHandlers).clone();
  }
  
  /**
   * Sets the transfer handler to use as the drag source.
   * The default implementation is to use the first transfer handler in the list.
   * @param newDragHandler the new drag source transfer handler.
   */
  public void setDragSourceTransferHandler(TransferHandler newDragHandler) {
    dragSourceHandler = newDragHandler;
  }
  
  /**
   * Returns the transfer handler when dragging from the component.
   * @return the transfer handler when dragging from the component.
   */
  public TransferHandler getDragSourceTransferHandler() {
    if (dragSourceHandler == null) {
      if (transferHandlers.size() == 0) {
	throw new IllegalStateException("The multiple transfer handler must have at least one TransferHandler.");
      }
      dragSourceHandler = (TransferHandler) transferHandlers.get(0);
    }
    return dragSourceHandler;
  }

  // ---------- Methods that handles importing of objects. ----------
  
  /** {@inheritDoc} */
  public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
    boolean canImport = false;
    for (Iterator iterator = transferHandlers.iterator(); (iterator.hasNext()) && (!canImport);) {
      TransferHandler handler = (TransferHandler) iterator.next();
      canImport = handler.canImport(comp, transferFlavors);
    }
    return canImport;
  }
  
  /** {@inheritDoc} */
  public boolean importData(JComponent comp, Transferable t) {
    boolean wasImported = false;
    
    for (Iterator iterator = transferHandlers.iterator(); (iterator.hasNext()) && (!wasImported);) {
      TransferHandler handler = (TransferHandler) iterator.next();
      if (handler.canImport(comp, t.getTransferDataFlavors())) {
	wasImported = handler.importData(comp, t);
      }
    }
    return wasImported;
  }
  
  // ---------- Methods that handles exporting of objects. ----------
  
  /** {@inheritDoc} */
  public int getSourceActions(JComponent c) {
    return getDragSourceTransferHandler().getSourceActions(c);
  }
  
  /** {@inheritDoc} */
  public void exportAsDrag(JComponent comp, InputEvent e, int action) {
    getDragSourceTransferHandler().exportAsDrag(comp, e, action);
  }
  
  /** {@inheritDoc} */
  public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
    getDragSourceTransferHandler().exportToClipboard(comp, clip, action);
  }
  
  /** {@inheritDoc} */
  public Icon getVisualRepresentation(Transferable t) {
    return getDragSourceTransferHandler().getVisualRepresentation(t);
  }

  /** {@inheritDoc} */
  protected Transferable createTransferable(JComponent c) {
    return (Transferable) ignoreProtectedAccess("createTransferable",
						new Class[] {JComponent.class},
						new Object[]{c});
  }
  
  /** {@inheritDoc} */
  protected void exportDone(JComponent source, Transferable data, int action) {
    ignoreProtectedAccess("exportDone",
			  new Class[] {JComponent.class, Transferable.class, int.class},
			  new Object[]{source, data, new Integer(action)});
  }
  
  /**
   * Calls the method and ignores the protected access modifier on the TransferHandler class.
   * This is necessary due to the fact the needed methods have protected access in the TransferHandler
   * class. The Swing framework bypass this "feature" by having the cooperating classes in the same package.
   * And since package access gives access before the protected modifier, then they dont have any
   * problem doing this. But for other developers, there is no other way than to cheat using
   * the reflection API.
   * <p>
   * The method calls the specified methodname on the default drag source TransferHandler, bypassing
   * the protected access modifier.
   * @param methodName the name of the method to call.
   * @param methodArguments the type of arguments that the method has.
   * @param arguments the arguments to send when calling the method.
   * @return an Object if the method returns one. If the method doesnt return an object, null is returned.
   */
  private Object ignoreProtectedAccess(String methodName, Class[] methodArguments, Object[] arguments) {
    Object object = null;
    try {
      Method method = TransferHandler.class.getDeclaredMethod(methodName, methodArguments);
      method.setAccessible(true);
      object = (Transferable) method.invoke(getDragSourceTransferHandler(), arguments);
    } catch (SecurityException se) {
    } catch (NoSuchMethodException nsme) {
    } catch (IllegalArgumentException iae) {
    } catch (IllegalAccessException iace) {
    } catch (InvocationTargetException ite) {
    }
    return object;
  }
}
