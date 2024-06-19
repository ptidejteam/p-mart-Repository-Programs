package net.suberic.util.gui.propedit;
import javax.swing.*;
import net.suberic.util.*;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.util.*;

/**
 * An EditorPane which allows a user to select from a choice of three:
 * true, false, or default.
 */
public class DefaultBooleanEditorPane extends ListEditorPane {

  /**
   * Creates the JComboBox with the appropriate options.
   */
  protected JComboBox createComboBox() {
    String originalValue = manager.getProperty(property, "");
    originalIndex=-1;
    currentIndex = -1;
    Vector items = new Vector();
    
    HashMap valueMap = new HashMap();
    valueMap.put("False", "False");
    valueMap.put("True", "True");
    valueMap.put("Default", "");
    
    Set keys = valueMap.keySet();
    Iterator keyIter = keys.iterator();
    for (int i = 0; keyIter.hasNext(); i++) {
      String currentLabel = (String) keyIter.next();
      String currentValue = (String) valueMap.get(currentLabel);
      if (currentValue.equals(originalValue)) {
	originalIndex=i;
	currentIndex=i;
      }
      items.add(currentLabel);
    }
    
    if (originalIndex == -1) {
      items.add(originalValue);
      labelToValueMap.put(originalValue, originalValue);
      originalIndex = items.size() - 1;
    }
      
    JComboBox jcb = new JComboBox(items);
    jcb.setSelectedIndex(originalIndex);

    labelToValueMap = valueMap;

    jcb.addItemListener(new ItemListener() {
	public void itemStateChanged(ItemEvent e) {
	  int newIndex = inputField.getSelectedIndex();
	  if (newIndex != currentIndex) {
	    String newValue = (String)labelToValueMap.get(inputField.getSelectedItem());
	    try {
	      firePropertyChangingEvent(newValue);
	      firePropertyChangedEvent(newValue);
	      currentIndex = newIndex;
	    } catch (PropertyValueVetoException pvve) {
	      manager.getFactory().showError(inputField, "Error changing value " + label.getText() + " to " + newValue + ":  " + pvve.getReason());
	      inputField.setSelectedIndex(currentIndex);
	    } 
	  }
	}
      });

    return jcb;
  }
  
}
