package net.suberic.util.thread;
import net.suberic.util.DynamicAbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.AbstractAction;
import java.beans.PropertyChangeListener;
import java.awt.event.ActionEvent;

/**
 * This class associates an Action with a particular ActionThread.  It
 * then can be used in place of the wrapped Action.  When the Action is
 * performed, the work is done by the ActionThread, rather than the 
 * original thread.
 */
public class ActionWrapper extends net.suberic.util.DynamicAbstractAction {
  Action wrappedAction;
  ActionThread thread;

  /**
   * This creates a new ActionWrapper from an Action and a Thread.
   */
  public ActionWrapper(Action newWrappedAction, ActionThread newThread) {
    super();
    wrappedAction=newWrappedAction;
    thread=newThread;
  }
  
  /**
   * This performs the wrapped Action on the configured Thread.
   */
  public void actionPerformed(ActionEvent e) {
    thread.addToQueue(wrappedAction, e);
  }
	
  /**
   * This passes the call on to the wrapped Action.
   */
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    wrappedAction.addPropertyChangeListener(listener);
  }
  
  /**
   * This passes the call on to the wrapped Action.
   */
  public Object getValue(String key) {
    return wrappedAction.getValue(key);
  }
  
  /**
   * This passes the call on to the wrapped Action.
   */
  public boolean isEnabled() {
    return wrappedAction.isEnabled();
  }
  
  /**
   * This passes the call on to the wrapped Action.
   */
  public void putValue(String key, Object newValue) {
    wrappedAction.putValue(key, newValue);
    // have to call super.putValue() in order to work around a bug in
    // javax.swing.AbstractAction.clone() later
    super.putValue(key, newValue);
  }
  
  /**
   * This passes the call on to the wrapped Action.
   */
  public void removePropertyChangeListener(PropertyChangeListener listener) {
    wrappedAction.removePropertyChangeListener(listener);
  }
  
  /**
   * This passes the call on to the wrapped Action.
   */
  public void setEnabled(boolean newValue) {
    wrappedAction.setEnabled(newValue);
  }
	
  public Action getWrappedAction() {
    return wrappedAction;
  }
  
  public void setWrappedAction(Action newValue) {
    wrappedAction = newValue;
  }
  
  public ActionThread getThread() {
    return thread;
  }
  
  public void setThread(ActionThread newValue) {
    thread=newValue;
  }
  
  public Action cloneDynamicAction() throws CloneNotSupportedException {
    if (wrappedAction instanceof DynamicAbstractAction) {
      this.putValue("bugWorkaround", "true");
      ActionWrapper newAction = (ActionWrapper)this.clone();
      newAction.setWrappedAction(((DynamicAbstractAction) wrappedAction).cloneDynamicAction());
      return newAction;
    } else {
      throw new CloneNotSupportedException();
    }
  }
  
}
