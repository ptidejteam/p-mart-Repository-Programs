/*
 * Created on 9 juil. 2003
 *
 */
package org.gudy.azureus2.ui.swt.config;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.gudy.azureus2.core3.config.*;

/**
 * @author Olivier
 * 
 */
public class BooleanParameter implements IParameter{

  String name;
  Button checkBox;
  IAdditionalActionPerformer performer;
  
  public BooleanParameter(Composite composite, final String name, boolean defaultValue) {
    this(composite,name,defaultValue,null);
  }
  
  public BooleanParameter(Composite composite, final String name, boolean defaultValue,IAdditionalActionPerformer actionPerformer) {
    this.performer = actionPerformer;
    boolean value = COConfigurationManager.getBooleanParameter(name,defaultValue);
    checkBox = new Button(composite,SWT.CHECK);
    checkBox.setSelection(value);
    checkBox.addListener(SWT.Selection,new Listener() {
    /* (non-Javadoc)
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    public void handleEvent(Event event) {
		boolean selected  = checkBox.getSelection();
    COConfigurationManager.setParameter(name,selected);
    if(performer != null) {
      performer.setSelected(selected);
      performer.performAction();
    }    
    }
  });
  }

  public void setLayoutData(Object layoutData) {
    checkBox.setLayoutData(layoutData);
  }
  
  public void setAdditionalActionPerformer(IAdditionalActionPerformer actionPerformer) {
    this.performer = actionPerformer;
    boolean selected  = checkBox.getSelection();
    performer.setSelected(selected);
    performer.performAction();
  }
  /* (non-Javadoc)
   * @see org.gudy.azureus2.ui.swt.IParameter#getControl()
   */
  public Control getControl() {
    return checkBox;
  }

  public boolean
  isSelected()
  {
  	return( checkBox.getSelection());
  }
}
