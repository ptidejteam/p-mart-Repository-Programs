package net.suberic.pooka.gui.filter;
import javax.swing.*;
import java.awt.*;

/**
 * This class allows you to choose colors for a ColorFilter.
 */
public class ColorFilterEditor extends FilterEditor implements java.awt.event.ActionListener {
  JButton colorButton;
  Color currentColor;
  int originalRgb = NO_VALUE;
  
  public static int NO_VALUE = -1;
  
  public static String FILTER_CLASS = "net.suberic.pooka.gui.filter.ColorDisplayFilter";
  
  /**
   * Configures the given FilterEditor from the given VariableBundle and
   * property.
   */
  public void configureEditor(net.suberic.util.gui.propedit.PropertyEditorManager newManager, String propertyName) {
    property = propertyName;
    manager = newManager;
    
    String origRgbString = manager.getProperty(propertyName + ".rgb", Integer.toString(NO_VALUE));
    originalRgb = Integer.parseInt(origRgbString);
    
    colorButton = new JButton();
    if (originalRgb != NO_VALUE) {
      setCurrentColor(new Color(originalRgb));
    } else {
      setCurrentColor(Color.blue);
    }
    colorButton.addActionListener(this);
    this.add(colorButton);
  }
  
  public void setCurrentColor(Color newColor) {
    currentColor = newColor;
    colorButton.setBackground(currentColor);
  }
  
  public Color getCurrentColor() {
    return currentColor;
  }
  
  /**
   * Shows a dialog for choosing a new color when this is selected.
   */
  public void actionPerformed(java.awt.event.ActionEvent e) {
    Color newColor = JColorChooser.showDialog(this, "title", currentColor);
    if (newColor != null)
      setCurrentColor(newColor);
  }
  
  /**
   * Gets the values that would be set by this FilterEditor.
   */
  public java.util.Properties getValue() {
    java.util.Properties props = new java.util.Properties();
    props.setProperty(property + ".rgb", Integer.toString(currentColor.getRGB()));
    props.setProperty(property + ".class", FILTER_CLASS);
    return props;
  }
  
  /**
   * Sets the values represented by this FilterEditor in the manager.
   */
  public void setValue() {
    int newValue = currentColor.getRGB();
    if (newValue != originalRgb)
      manager.setProperty(property + ".rgb", Integer.toString(newValue));
    
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









