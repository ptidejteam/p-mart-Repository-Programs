package net.suberic.util.gui.propedit;
import javax.swing.*;
import java.awt.event.*;
import net.suberic.util.*;
import java.awt.FlowLayout;

/**
 * The default EditorPane.  Just shows a text field in which a user
 * can enter a String.
 */
public class StringEditorPane extends LabelValuePropertyEditor {

  String currentValue = "";
  JLabel label;
  JTextField inputField;

  /**
   * @param propertyName The property to be edited.
   * @param template The property that will define the layout of the
   *                 editor.
   * @param manager The PropertyEditorManager that will manage the
   *                   changes.
   */
  public void configureEditor(String propertyName, String template, String propertyBaseName, PropertyEditorManager newManager) {
    configureBasic(propertyName, template, propertyBaseName, newManager);

    getLogger().fine("configuring StringEditorPane.  property is " + property + "; editorTemplate is " + editorTemplate);

    label = createLabel();
    currentValue = originalValue == null ? "" : originalValue;

    inputField = new JTextField(originalValue);
    inputField.setPreferredSize(new java.awt.Dimension(150, inputField.getMinimumSize().height));
    inputField.setMinimumSize(new java.awt.Dimension(150, inputField.getMinimumSize().height));
    inputField.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, inputField.getMinimumSize().height));
    inputField.addFocusListener(new FocusAdapter() {
        public void focusLost(FocusEvent e) {
          if (! inputField.getText().equals(currentValue)) {
            try {
              firePropertyChangingEvent(inputField.getText());
              currentValue = inputField.getText();
              firePropertyChangedEvent(currentValue);
            } catch (PropertyValueVetoException pvve) {
              inputField.setText(currentValue);
              manager.getFactory().showError(inputField, "Error changing value " + label.getText() + " to " + pvve.getRejectedValue() + ":  " + pvve.getReason());
              inputField.requestFocusInWindow();
            }
          }
        }

        public void focusGained(FocusEvent e) {
          if (inputField.getText() != null && inputField.getText().length() > 0) {
            inputField.setSelectionStart(0);
            inputField.setSelectionEnd(inputField.getText().length());
          }
        }

      });
    this.add(label);
    this.add(inputField);

    labelComponent = label;
    valueComponent = inputField;

    // have to set enabled now, with the current enabled value, since it
    // may have changed.
    updateEditorEnabled();

    manager.registerPropertyEditor(property, this);
  }

  /**
   * This writes the currently configured value in the PropertyEditorUI
   * to the source PropertyEditorManager.
   */
  public void setValue() throws PropertyValueVetoException {
    validateProperty();
    if (isEnabled() && !(inputField.getText().equals(originalValue))) {
      manager.setProperty(property, inputField.getText());
      originalValue = inputField.getText();
    }
  }

  /**
   * This checks that the currently configured value is valid.
   */
  public void validateProperty() throws PropertyValueVetoException {
    if (isEnabled() && !(inputField.getText().equals(currentValue))) {
      firePropertyChangingEvent(inputField.getText());
      firePropertyChangedEvent(inputField.getText());
    }

    firePropertyCommittingEvent(inputField.getText());
  }


  /**
   * Returns the current values of the edited properties as a
   * java.util.Properties object.
   */
  public java.util.Properties getValue() {
    java.util.Properties retProps = new java.util.Properties();
    retProps.setProperty(property, inputField.getText());
    return retProps;
  }


  /**
   * This resets the editor to the original (or latest set, if setValue()
   * has been called) value of the edited property.
   */
  public void resetDefaultValue() throws PropertyValueVetoException {
    String fieldValue = inputField.getText();
    if (! (fieldValue.equals(currentValue) && fieldValue.equals(originalValue))) {
      // something has changed, so we'll have to deal with it.
      if (! currentValue.equals(originalValue)) {
        firePropertyChangingEvent(originalValue);
        currentValue = originalValue;
        firePropertyChangedEvent(originalValue);
      }
      inputField.setText(originalValue);
    }
  }

  /**
   * Run when the PropertyEditor may have changed enabled states.
   */
  protected void updateEditorEnabled() {
    if (inputField != null) {
      inputField.setEnabled(isEditorEnabled());
    }
    if (label != null) {
      label.setEnabled(isEditorEnabled());
    }
  }
}
