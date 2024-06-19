package net.suberic.util.gui.propedit;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.FlowLayout;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import net.suberic.util.*;

/**
 * This is a Swing implemenation of a boolean PropertyEditorUI.
 */
public class BooleanEditorPane extends SwingPropertyEditor {
  JCheckBox inputField;
  String label;
  boolean originalBoolean = false;

  /**
   * @param propertyName The property to be edited.
   * @param template The property that will define the layout of the
   *                 editor.
   * @param manager The PropertyEditorManager that will manage the
   *                   changes.
   */
  public void configureEditor(String propertyName, String template, String propertyBaseName, PropertyEditorManager newManager) {
    configureBasic(propertyName, template, propertyBaseName, newManager);
    debug = newManager.getProperty("editors.debug", "false").equalsIgnoreCase("true");

    //this.setLayout(new FlowLayout(FlowLayout.LEFT));
    SpringLayout layout = new SpringLayout();
    this.setLayout(layout);

    originalBoolean = originalValue.equalsIgnoreCase("true");

    String defaultLabel;
    int dotIndex = property.lastIndexOf(".");
    if (dotIndex == -1)
      defaultLabel = new String(property);
    else
      defaultLabel = property.substring(dotIndex+1);

    label = manager.getProperty(editorTemplate + ".label", defaultLabel);
    inputField = new JCheckBox(label);

    inputField.setSelected(originalBoolean);

    inputField.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          String newValue = null;
          if (e.getStateChange() == ItemEvent.SELECTED) {
            newValue = "true";
          } else if (e.getStateChange() == ItemEvent.DESELECTED) {
            newValue = "false";
          }

          try {
            if (newValue != null) {
              firePropertyChangingEvent(newValue);
              firePropertyChangedEvent(newValue);
            }
          } catch (PropertyValueVetoException pvve) {
            manager.getFactory().showError(inputField, "Error changing value " + label + " to " + newValue+ ":  " + pvve.getReason());
            inputField.setSelected(! inputField.isSelected());
          }
        }
      });

    inputField.getInsets().set(0,0,0,0);
    inputField.setMargin(new java.awt.Insets(0,0,0,5));

    this.add(inputField);

    //inputField.setBackground(java.awt.Color.RED);
    //this.setBackground(java.awt.Color.BLUE);

    layout.putConstraint(SpringLayout.WEST, inputField, 0, SpringLayout.WEST, this);
    layout.putConstraint(SpringLayout.NORTH, inputField, 0, SpringLayout.NORTH, this);
    layout.putConstraint(SpringLayout.SOUTH, this, 0, SpringLayout.SOUTH, inputField);
    layout.putConstraint(SpringLayout.EAST, this, Spring.constant(0, 0, Integer.MAX_VALUE), SpringLayout.EAST, inputField);
    //layout.putConstraint(SpringLayout.BASELINE, label, 0, SpringLayout.SOUTH, inputField);

    this.getInsets().set(0,0,0,0);

    //this.setBackground(java.awt.Color.BLACK);

    this.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, this.getPreferredSize().height));
    manager.registerPropertyEditor(property, this);
    updateEditorEnabled();
  }

  /**
   * as defined in net.suberic.util.gui.PropertyEditorUI
   */
  public void setValue() throws PropertyValueVetoException {
    if (isEditorEnabled()) {
      validateProperty();
      if (inputField.isSelected() != originalBoolean || manager.getProperty(property, "unset").equals("unset")) {
        String newValue;
        if (inputField.isSelected())
          newValue = "true";
        else
          newValue = "false";

        manager.setProperty(property, newValue);

        originalBoolean = inputField.isSelected();
      }

    }
  }

  /**
   * as defined in net.suberic.util.gui.PropertyEditorUI
   */
  public void validateProperty() throws PropertyValueVetoException {
    if (isEditorEnabled()) {
      String newValue;
      if (inputField.isSelected())
        newValue = "true";
      else
        newValue = "false";

      firePropertyCommittingEvent(newValue);
    }
  }

  /**
   * Returns the current values of the edited properties as a
   * java.util.Properties object.
   */
  public java.util.Properties getValue() {
    java.util.Properties retProps = new java.util.Properties();

    if (! isEditorEnabled()) {
      return retProps;
    } else {
      if (inputField.isSelected())
        retProps.setProperty(property, "true");
      else
        retProps.setProperty(property, "false");
    }
    return retProps;
  }

  /**
   * This resets the editor to the original (or latest set, if setValue()
   * has been called) value of the edited property.
   */
  public void resetDefaultValue() {
    // this will be handled by the listener on the inputField, so we don't
    // have to send any events here.
    inputField.setSelected(originalBoolean);
  }

  /**
   * Run when the PropertyEditor may have changed enabled states.
   */
  protected void updateEditorEnabled() {
    //System.err.println("setting enabled for " + getProperty() + " to " + newValue);
    if (inputField != null) {
      //System.err.println("setting enabled for " + getProperty() + "; setting enabled on input field to " + newValue);
      inputField.setEnabled(isEditorEnabled());
    }
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
    return label;
  }

  /**
   * Accepts or rejects the initial focus for this component.
   */
  public boolean acceptDefaultFocus() {
    if (isEditorEnabled() && inputField.isRequestFocusEnabled()) {
      return inputField.requestFocusInWindow();
    } else {
      return false;
    }
  }

}

