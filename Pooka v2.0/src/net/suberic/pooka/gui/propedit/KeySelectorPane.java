package net.suberic.pooka.gui.propedit;
import java.awt.Dimension;
import java.util.Set;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.suberic.crypto.EncryptionManager;
import net.suberic.pooka.Pooka;
import net.suberic.util.gui.propedit.LabelValuePropertyEditor;
import net.suberic.util.gui.propedit.PropertyEditorManager;
import net.suberic.util.gui.propedit.PropertyValueVetoException;

/**
 * This displays the currently selected key (if any).
 */

public class KeySelectorPane extends LabelValuePropertyEditor {

  JLabel label;
  JComboBox valueDisplay;

  /**
   * @param propertyName The property to be edited.
   * @param template The property that will define the layout of the
   *                 editor.
   * @param manager The PropertyEditorManager that will manage the
   *                   changes.
   */
  public void configureEditor(String propertyName, String template, String propertyBaseName, PropertyEditorManager newManager) {
    configureBasic(propertyName, template, propertyBaseName, newManager);

    if (debug) {
      System.out.println("property is " + property + "; editorTemplate is " + editorTemplate);
    }

    label = createLabel();

    valueDisplay = createKeyList(originalValue);

    //valueDisplay.setPreferredSize(new java.awt.Dimension(150 - inputButton.getPreferredSize().width, valueDisplay.getMinimumSize().height));

    this.add(label);
    labelComponent = label;
    //JPanel tmpPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0,0));
    //tmpPanel.add(valueDisplay);
    //tmpPanel.setPreferredSize(new java.awt.Dimension(Math.max(150, tmpPanel.getMinimumSize().width), valueDisplay.getMinimumSize().height));
    //valueComponent = tmpPanel;
    //this.add(tmpPanel);
    valueComponent = valueDisplay;
    this.add(valueComponent);

    updateEditorEnabled();

  }

  /**
   * Creates a button that will bring up a way to select a folder.
   */
  public JComboBox createKeyList(String defaultValue) {
    if (Pooka.isDebug())
      System.out.println("creating keylist.");

    String sEncryptionType = manager.getProperty(editorTemplate + ".encryptionType", "All");

    String encryptionType;    
    if(sEncryptionType.equalsIgnoreCase("S/MIME"))
      encryptionType = EncryptionManager.SMIME;
    else if(sEncryptionType.equalsIgnoreCase("SMIME"))
        encryptionType = EncryptionManager.SMIME;
    else if(sEncryptionType.equalsIgnoreCase("PGP"))
        encryptionType = EncryptionManager.PGP;
    else
        encryptionType = null;

    String keyType = manager.getProperty(editorTemplate + ".keyType", "private");
    String keyPurpose = manager.getProperty(editorTemplate + ".keyPurpose", "signature");
    Set keySet = null;

    Set tmpSet = null;
    try {
      if (keyType.equalsIgnoreCase("private")){
    	  if("signature".equalsIgnoreCase(keyPurpose)){
    		  keySet = Pooka.getCryptoManager().privateKeyAliases(encryptionType, true);
    	  }else if("encryption".equalsIgnoreCase(keyPurpose)){
    		  keySet = Pooka.getCryptoManager().privateKeyAliases(encryptionType, false);
    	  }else {
    		  keySet = Pooka.getCryptoManager().privateKeyAliases(encryptionType, true);
    		  tmpSet = Pooka.getCryptoManager().privateKeyAliases(encryptionType, true);
    	  }
      }
      else{
    	  if("signature".equalsIgnoreCase(keyPurpose)){
    		  keySet = Pooka.getCryptoManager().publicKeyAliases(encryptionType, true);
    	  }else if("encryption".equalsIgnoreCase(keyPurpose)){
    		  keySet = Pooka.getCryptoManager().publicKeyAliases(encryptionType, false);
    	  }else {
    		  keySet = Pooka.getCryptoManager().publicKeyAliases(encryptionType, true);
    		  tmpSet = Pooka.getCryptoManager().publicKeyAliases(encryptionType, true);
    	  }
      }
    } catch (java.security.KeyStoreException kse) {
      keySet = null;
    }

    if(tmpSet != null){
	    java.util.Iterator it = tmpSet.iterator();
	    while(it.hasNext()){
	    	Object obj = it.next();
	    	if(!keySet.contains(obj))
	    		keySet.add(obj);
	    }
    }

    Vector listModel = null;

    if (keySet != null) {
      listModel = new Vector(keySet);
    } else {
      listModel = new Vector();
    }

    /*if (originalValue != null && originalValue != "") {
      if (! listModel.contains(originalValue))
        listModel.add(originalValue);
      JComboBox returnValue = new JComboBox(listModel);
      returnValue.setSelectedItem(originalValue);

      return returnValue;
    } else {*/
      JComboBox returnValue = new JComboBox(listModel);

      return returnValue;
//    }
  }

  //  as defined in net.suberic.util.gui.PropertyEditorUI

  public void setValue() throws PropertyValueVetoException {
    validateProperty();
    if (Pooka.isDebug())
      System.out.println("calling ksp.setValue.  isEditorEnabled() = " + isEditorEnabled() + "; isChanged() = " + isChanged());

    String newValue = (String) valueDisplay.getSelectedItem();
    if (newValue == null)
      newValue = "";

    if (isEditorEnabled() && isChanged()) {
      manager.setProperty(property, newValue);
    }
  }

  public void validateProperty() throws PropertyValueVetoException {
    String newValue = (String) valueDisplay.getSelectedItem();
    if (newValue == null)
      newValue = "";

    if (isEditorEnabled()) {
      firePropertyCommittingEvent(newValue);
    }
  }

  public java.util.Properties getValue() {
    java.util.Properties retProps = new java.util.Properties();

    String newValue = (String) valueDisplay.getSelectedItem();
    if (newValue == null)
      newValue = "";

    retProps.setProperty(property, newValue);

    return retProps;
  }

  public void resetDefaultValue() {
    valueDisplay.setSelectedItem(originalValue);
  }

  public boolean isChanged() {
    return (!(originalValue.equals(valueDisplay.getSelectedItem())));
  }

  /**
   * Run when the PropertyEditor may have changed enabled states.
   */
  protected void updateEditorEnabled() {
    if (Pooka.isDebug())
      System.out.println("calling ksp.updateEditorEnabled().  isEditorEnabled() = " + isEditorEnabled());

    if (valueDisplay != null) {
      valueDisplay.setEnabled(isEditorEnabled());
    }
  }

}
