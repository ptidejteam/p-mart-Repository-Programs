package net.suberic.pooka.gui.propedit;

import net.suberic.util.gui.propedit.*;
import javax.swing.*;
import net.suberic.util.*;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.util.*;

/**
 * A property editor that gives a choice for each available look and feel.
 */
public class LookAndFeelEditor extends ListEditorPane {

  /**
   * Creates the JComboBox with the appropriate options.
   */
  protected JComboBox createComboBox() {
    originalValue = manager.getProperty(property, "");

    Vector items = loadLnF();

    JComboBox jcb = new JComboBox(items);

    if (debug)
      System.out.println("setting to original index " + originalIndex);

    jcb.setSelectedIndex(originalIndex);

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

  /**
   * Creates the list of available Look and Feels.
   *
   * This goes through and adds each registered look and feel, and then 
   * goes through again and adds any in our custom list that might have
   * been missed, if available.
   */
  public Vector loadLnF() {
    HashMap foundLnfs = new HashMap();
    Vector items = new Vector();

    // first add registered look-and-feels
    UIManager.LookAndFeelInfo[] feels = UIManager.getInstalledLookAndFeels();

    for (int i = 0; i < feels.length; i++) {
      String itemLabel = feels[i].getName();
      String itemValue = feels[i].getClassName();
      
      try {

	LookAndFeel currentLnf = (LookAndFeel) Class.forName(itemValue).newInstance();
	if (currentLnf.isSupportedLookAndFeel()) {
	  if (debug)
	    System.out.println("instantiated " + itemValue + "; adding " + itemLabel);

	  if (itemValue.equals(originalValue)) {
	    if (debug)
	      System.out.println("matching " + itemValue + "; settingin originalIndex to " + i);
	    originalIndex=items.size();
	    currentIndex=items.size();
	  }
	  items.add(itemLabel);
	  labelToValueMap.put(itemLabel, itemValue);
	  
	  foundLnfs.put(itemValue, null);
	} else {
	  if (debug)
	    System.out.println("not adding " + itemLabel + "; not supported look and feel.");
	}
      } catch (Exception e) {
	// assume it couldn't be instantiated.
	if (debug)
	  System.out.println("error instantiating " + itemLabel + "; not adding.");
      }
    }
    
    // now add configured ones.
    StringTokenizer tokens;
    
    String allowedValuesString = manager.getProperty(editorTemplate + ".allowedValues", "");
    if (manager.getProperty(allowedValuesString, "") != "") {
      tokens = new StringTokenizer(manager.getProperty(allowedValuesString, ""), ":");
      manager.addPropertyEditorListener(allowedValuesString, new ListEditorListener());
    } else {
      tokens = new StringTokenizer(manager.getProperty(editorTemplate + ".allowedValues", ""), ":");
    }
    
    while (tokens.hasMoreTokens()) {
      String currentItem = tokens.nextToken();
      
      String itemLabel = manager.getProperty(editorTemplate + ".listMapping." + currentItem.toString() + ".label", "");
      if (itemLabel.equals(""))
	itemLabel = currentItem.toString();
      
      String itemValue = manager.getProperty(editorTemplate + ".listMapping." + currentItem.toString() + ".value", "");
      if (itemValue.equals(""))
	itemValue = currentItem.toString();

      if (! foundLnfs.containsKey(itemValue)) {
	// try instantiating this.
	try {
	  LookAndFeel currentLnf = (LookAndFeel) Class.forName(itemValue).newInstance();
	  if (currentLnf.isSupportedLookAndFeel()) {
	    if (debug)
	      System.out.println("instantiated; adding " + itemLabel);
	    if (itemValue.equals(originalValue)) {
	      if (debug)
		System.out.println("setting originalIndex to " + items.size());
	      originalIndex=items.size();
	      currentIndex=items.size();
	    }
	    items.add(itemLabel);
	    labelToValueMap.put(itemLabel, itemValue);
	  } else {
	    if (debug)
	      System.out.println("not adding " + itemLabel + "; not supported look and feel.");
	  }
	} catch (Exception e) {
	  // assume it didn't work.
	  if (debug)
	    System.out.println("error instantiating " + itemLabel + "; not adding.");

	}
      }
    }
    
    if (originalIndex == -1) {
      items.add(originalValue);
      labelToValueMap.put(originalValue, originalValue);
      originalIndex = items.size() - 1;
    }

    return items;
  }
}
