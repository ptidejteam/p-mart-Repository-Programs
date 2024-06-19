package net.suberic.util;
import javax.swing.AbstractAction;
import javax.swing.Action;

public abstract class DynamicAbstractAction extends AbstractAction {
  
  public DynamicAbstractAction() {
    super();
  }
  
  public DynamicAbstractAction(String cmd) {
    super(cmd);
  }
  
  public Action cloneDynamicAction() throws CloneNotSupportedException {
    this.putValue("foo", "bar");
    return (Action)this.clone();
  }
  
}
