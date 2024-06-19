package net.suberic.util.swing;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.KeyEvent;
import java.awt.AWTKeyStroke;
import java.awt.KeyboardFocusManager;
import java.util.*;

/**
 * This is a subclass of JTextArea which allows for tab field navigation.
 */
public class EntryTextArea extends JTextArea {

  boolean keysUpdated = false;
  /**
   * Constructor; calls super.
   */
  public EntryTextArea() {
    super();
  }
  
  /**
   * Constructor; calls super.
   */
  public EntryTextArea(Document doc) {
    super(doc);
    updateFocusTraversalKeys();
  }

  /**
   * Constructor; calls super.
   */
  public EntryTextArea(Document doc, String text, int rows, int columns) {
    super(doc, text, rows, columns);
    updateFocusTraversalKeys();
  }
  
  /**
   * Constructor; calls super.
   */
  public EntryTextArea(int rows, int columns) {
    super(rows, columns);
    updateFocusTraversalKeys();
  }
  
  /**
   * Constructor; calls super.
   */
  public EntryTextArea(String text) {
    super(text);
    updateFocusTraversalKeys();
  }
  
  /**
   * Constructor; calls super.
   */
  public EntryTextArea(String text, int rows, int columns) {
    super(text, rows, columns);
    updateFocusTraversalKeys();
  }

  /**
   * updates the focus traversal keys to include tab and shift-tab.
   */
  protected void updateFocusTraversalKeys() {
    if (! keysUpdated) {
      keysUpdated=true;
      Set forward = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
      
      Set newForward = new HashSet(forward);
      newForward.add(AWTKeyStroke.getAWTKeyStroke("pressed TAB"));

      setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
			    newForward);

      Set backward = getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
      
      Set newBackward = new HashSet(backward);
      newBackward.add(AWTKeyStroke.getAWTKeyStroke("shift pressed TAB"));

      setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
			    newBackward);
    }
  }

}
