package net.suberic.util.swing;

import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;
import javax.swing.undo.*;
import javax.swing.text.*;
import java.beans.*;

/**
 * An extension of JTextPane with some additional actions.
 *
 * Most of the Undo and Redo action code was taken from the sample
 * Notepad and Stylepad demos included in the Sun JDK's.
 */
public class ExtendedEditorPane extends JTextPane {
  
  private UndoManager undoManager = new UndoManager();

  private UndoableEditListener undoHandler = new UndoHandler();
  
  private UndoAction undoAction = new UndoAction();

  private RedoAction redoAction = new RedoAction();

  public ExtendedEditorPane() {
    super();
    this.addPropertyChangeListener("document", new PropertyChangeListener() {
	public void propertyChange(PropertyChangeEvent evt) {
	  if (evt.getPropertyName().equalsIgnoreCase("document")) {
	    Document oldDoc = (Document) evt.getOldValue();
	    Document doc = (Document) evt.getNewValue();

	    if (oldDoc != null)
	      oldDoc.removeUndoableEditListener(undoHandler);
	    if (doc != null) 
	      doc.addUndoableEditListener(undoHandler);
	    
	    if (doc != oldDoc) {
	      undoManager.discardAllEdits();
	      undoAction.update();
	      redoAction.update();
	    }
	    
	  }
	}
      });
    
  }

  public Action[] getActions() {
    Action[] defaultActions = super.getActions();
    if (defaultActions == null)
      return defaultActions;

    if (undoAction != null) {
      Action[] additionalActions = new Action[] {
	undoAction,
	redoAction
      };
      return TextAction.augmentList(defaultActions, additionalActions);
    } else {
      return defaultActions;
    }
  }

  class UndoHandler implements UndoableEditListener {
    
    /**
     * Messaged when the Document has created an edit, the edit is
     * added to <code>undo</code>, an instance of UndoManager.
     */
    public void undoableEditHappened(UndoableEditEvent e) {
      undoManager.addEdit(e.getEdit());
      undoAction.update();
      redoAction.update();
    }
  }

  class UndoAction extends AbstractAction {
    public UndoAction() {
      super("edit-undo");
      setEnabled(false);
    }
    
    public void actionPerformed(ActionEvent e) {
      try {
	undoManager.undo();
      } catch (CannotUndoException ex) {

      }
      update();
      redoAction.update();
    }
    
    protected void update() {
      if(undoManager.canUndo()) {
	setEnabled(true);
	//putValue(Action.NAME, undo.getUndoPresentationName());
      }
      else {
	setEnabled(false);
	//putValue(Action.NAME, "Undo");
      }
    }
  }
  
  class RedoAction extends AbstractAction {
    public RedoAction() {
      super("edit-redo");
      setEnabled(false);
    }
    
    public void actionPerformed(ActionEvent e) {
      try {
	undoManager.redo();
      } catch (CannotRedoException ex) {

      }
      update();
      undoAction.update();
    }
    
    protected void update() {
      if(undoManager.canRedo()) {
	setEnabled(true);
	//putValue(Action.NAME, undo.getRedoPresentationName());
      }
      else {
	setEnabled(false);
	//putValue(Action.NAME, "Redo");
      }
    }
  }

}
