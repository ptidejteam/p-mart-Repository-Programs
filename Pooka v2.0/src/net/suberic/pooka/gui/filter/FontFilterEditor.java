package net.suberic.pooka.gui.filter;
import javax.swing.*;
import java.awt.*;

/**
 * This class allows you to choose colors for a FontFilter.
 */
public class FontFilterEditor extends FilterEditor {
  JComboBox fontCombo;
  String origFontString;
  
  public static String FILTER_CLASS = "net.suberic.pooka.gui.filter.FontDisplayFilter";
  
  /**
   * Configures the given FilterEditor from the given VariableBundle and
   * property.
   */
  public void configureEditor(net.suberic.util.gui.propedit.PropertyEditorManager newManager, String propertyName) {
    property = propertyName;
    manager = newManager;
    
    origFontString = manager.getProperty(propertyName + ".style", "");
    
    fontCombo = createFontCombo();
    
    if (origFontString.equals("")) 
      fontCombo.setSelectedIndex(0);
    else
      fontCombo.setSelectedItem(getFontLabel(origFontString));
    
    this.add(fontCombo);
    
  }
  
  /**
   * creates the font combo.
   */
  public JComboBox createFontCombo() {
    java.util.Vector labels = new java.util.Vector();
    labels.add(manager.getProperty("Font.PLAIN.label", "PLAIN"));
    labels.add(manager.getProperty("Font.BOLD.label", "BOLD"));
    labels.add(manager.getProperty("Font.ITALIC.label", "ITALIC"));
    
    return new JComboBox(labels);
  }
  
  /**
   * Returns the font label for this font.
   */
  public String getFontLabel(String fontType) {
    return manager.getProperty("Font." + fontType + ".label", "");
  }
  
  /**
   * Returns the selected font type.
   */
  public String getSelectedFontType() {
    String selectedString = (String) fontCombo.getSelectedItem();
    if (selectedString.equalsIgnoreCase(manager.getProperty("Font.PLAIN.label", "PLAIN")))
      return "PLAIN";
    else if (selectedString.equalsIgnoreCase(manager.getProperty("Font.BOLD.label", "BOLD")))
      return "BOLD";
    else if (selectedString.equalsIgnoreCase(manager.getProperty("Font.ITALIC.label", "ITALIC")))
      return "ITALIC";
    else
      return "";
  }
  
  /**
   * Gets the values that would be set by this FilterEditor.
   */
  public java.util.Properties getValue() {
    java.util.Properties props = new java.util.Properties();
    props.setProperty(property + ".style", getSelectedFontType());
    props.setProperty(property + ".class", FILTER_CLASS);
    return props;
  }
  
  /**
   * Sets the values represented by this FilterEditor in the manager.
   */
  public void setValue() {
    String newValue = getSelectedFontType();
    if (newValue != origFontString)
      manager.setProperty(property + ".style", newValue);
    
    String oldClassName = manager.getProperty(property + ".class", "");
    if (!oldClassName.equals(FILTER_CLASS))
      manager.setProperty(property + ".class", FILTER_CLASS);
  }
  
  /**
   * Returns the class that will be set for this FilterEditor.
   */
  public String getFilterClassValue() {
    return FILTER_CLASS;
  }

}









