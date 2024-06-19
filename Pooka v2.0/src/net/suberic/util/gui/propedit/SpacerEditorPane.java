package net.suberic.util.gui.propedit;
import javax.swing.*;
import java.awt.Component;
import java.awt.Dimension;
import net.suberic.util.*;

/**
 * An EditorPane which actually just acts as a strut.
 *
 */
public class SpacerEditorPane extends SwingPropertyEditor {
  protected Component strut = null;

  /**
   * @param propertyName The property to be edited.
   * @param template The property that will define the layout of the
   *                 editor.
   * @param manager The PropertyEditorManager that will manage the
   *                   changes.
   */
  public void configureEditor(String propertyName, String template, String propertyBaseName, PropertyEditorManager newManager) {
    configureBasic(propertyName, template, propertyBaseName, newManager);
    int height = 10;
    try {
      height = Integer.parseInt(manager.getProperty(template + ".height", "10"));
    } catch (Exception e) {

    }
    strut = Box.createVerticalStrut(height);
    this.add(strut);
    this.setMinimumSize(new Dimension(1, height));
    this.setPreferredSize(new Dimension(10, height));
    this.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));

  }

  /**
   * This writes the currently configured value in the PropertyEditorUI
   * to the source VariableBundle.
   *
   * A no-op in this case.
   */
  public void setValue() {

  }
  public void validateProperty() {

  }

  /**
   * Returns the current values of the edited properties as a
   * java.util.Properties object.
   *
   * This implementation returns an empty array.
   */
  public java.util.Properties getValue() {
    java.util.Properties retProps = new java.util.Properties();

    return retProps;
  }

  /**
   * This resets the editor to the original (or latest set, if setValue()
   * has been called) value of the edited property.
   */
  public void resetDefaultValue() {
  }

  /**
   * Selects the given value.
   */
  public void setSelectedValue(String newValue) {
  }

  /**
   * Returns whether or not the current list selection has changed from
   * the last save.
   */
  public boolean isChanged() {
    return false;
  }

  /**
   * Gets the parent PropertyEditorPane for the given component.
   */
  public PropertyEditorPane getPropertyEditorPane() {
    return getPropertyEditorPane(this);
  }

  /**
   * Returns the display value for this property.
   */
  public String getDisplayValue() {
    return getProperty();
  }

  /**
   * Updates the editorEnabled value.  A no-op in this case.
   */
  protected void updateEditorEnabled() {

  }

}
