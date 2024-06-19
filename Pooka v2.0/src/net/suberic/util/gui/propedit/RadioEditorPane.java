package net.suberic.util.gui.propedit;
import javax.swing.*;
import net.suberic.util.*;
import java.awt.event.*;
import java.util.*;

/**
 * An EditorPane which allows a user to select from a set of choices.
 *
 */
public class RadioEditorPane extends SwingPropertyEditor implements ItemListener {
  String labelString = "";
  protected ButtonGroup buttonGroup = new ButtonGroup();
  protected ButtonModel lastSelected = null;

  /**
   * @param propertyName The property to be edited.
   * @param template The property that will define the layout of the
   *                 editor.
   * @param manager The PropertyEditorManager that will manage the
   *                   changes.
   */
  public void configureEditor(String propertyName, String template, String propertyBaseName, PropertyEditorManager newManager) {
    configureBasic(propertyName, template, propertyBaseName, newManager);

    SpringLayout layout = new SpringLayout();
    this.setLayout(layout);

    String defaultLabel;
    int dotIndex = editorTemplate.lastIndexOf(".");
    if (dotIndex == -1)
      defaultLabel = new String(editorTemplate);
    else
      defaultLabel = property.substring(dotIndex+1);

    if (originalValue == null || originalValue.length() < 1) {
      originalValue = manager.getProperty(property, manager.getProperty(editorTemplate, ""));
    }

    //JLabel mainLabel = new JLabel(manager.getProperty(editorTemplate + ".label", defaultLabel));

    //this.add(mainLabel);
    labelString = manager.getProperty(editorTemplate + ".label", defaultLabel);

    if (manager.getProperty(editorTemplate + ".showBorder", "true").equalsIgnoreCase("false")) {
      this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), labelString));
    } else {
      this.setBorder(BorderFactory.createTitledBorder(labelString));
    }

    //System.err.println("radioeditorpane:  mainLabel = " + mainLabel.getText());
    //layout.putConstraint(SpringLayout.WEST, mainLabel, 0, SpringLayout.WEST, this);
    //layout.putConstraint(SpringLayout.NORTH, mainLabel, 0, SpringLayout.NORTH, this);

    List<String> allowedValues = manager.getPropertyAsList(editorTemplate + ".allowedValues", "");

    //JComponent previous = mainLabel;
    JComponent previous = null;
    JComponent widest = null;
    //Spring widthSpring = layout.getConstraints(mainLabel).getWidth();
    for(String allowedValue: allowedValues) {
      String label = manager.getProperty(editorTemplate + ".listMapping." + allowedValue + ".label", allowedValue);
      JRadioButton button = new JRadioButton(label);
      button.setActionCommand(allowedValue);
      button.addItemListener(this);

      buttonGroup.add(button);
      this.add(button);
      if (allowedValue.equals(originalValue)) {
        button.setSelected(true);
      }
      layout.putConstraint(SpringLayout.WEST, button, 15, SpringLayout.WEST, this);
      if (previous != null)
        layout.putConstraint(SpringLayout.NORTH, button, 0, SpringLayout.SOUTH, previous);
      else
        layout.putConstraint(SpringLayout.NORTH, button, 0, SpringLayout.NORTH, this);

      if (widest == null || widest.getPreferredSize().width < button.getPreferredSize().width) {
        widest = button;
      }
      previous = button;
    }

    layout.putConstraint(SpringLayout.SOUTH, this, 0, SpringLayout.SOUTH, previous);
    layout.putConstraint(SpringLayout.EAST, this, Spring.constant(5, 5, Integer.MAX_VALUE), SpringLayout.EAST, widest);

  }

  /**
   * Called when the button value changes.
   */
  public void itemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      JRadioButton button = (JRadioButton) e.getSource();
      String currentValue = button.getActionCommand();
      try {
        //System.err.println("firing propertyChangedEvent for " + property);
        firePropertyChangingEvent(currentValue);
        firePropertyChangedEvent(currentValue);

        lastSelected = button.getModel();
      } catch (PropertyValueVetoException pvve) {
        manager.getFactory().showError(this, "Error changing value " + labelString + " to " + currentValue + ":  " + pvve.getReason());
        if (lastSelected != null) {
          lastSelected.setSelected(true);
        }
      }
    }
  }

  /**
   * This writes the currently configured value in the PropertyEditorUI
   * to the source VariableBundle.
   */
  public void setValue() throws PropertyValueVetoException {
    validateProperty();
    ButtonModel selectedModel = buttonGroup.getSelection();
    String currentValue = "";
    if (selectedModel != null) {
      currentValue = selectedModel.getActionCommand();
    }

    firePropertyCommittingEvent(currentValue);

    if (isEditorEnabled() && isChanged()) {
      manager.setProperty(property, currentValue);
      originalValue = currentValue;
    }
    lastSelected = selectedModel;
  }

  /**
   * This checks that the currently configured value is valid.
   */
  public void validateProperty() throws PropertyValueVetoException {
    ButtonModel selectedModel = buttonGroup.getSelection();
    String currentValue = "";
    if (selectedModel != null) {
      currentValue = selectedModel.getActionCommand();
    }
    if (! currentValue.equals(originalValue)) {
      firePropertyChangingEvent(currentValue);
      //System.err.println("firing propertyChangedEvent for " + property);
      firePropertyChangedEvent(currentValue);
    }

    firePropertyCommittingEvent(currentValue);
  }

  /**
   * Returns the current values of the edited properties as a
   * java.util.Properties object.
   */
  public java.util.Properties getValue() {
    java.util.Properties retProps = new java.util.Properties();

    ButtonModel selectedModel = buttonGroup.getSelection();
    String value = "";
    if (selectedModel != null) {
      value = buttonGroup.getSelection().getActionCommand();
    }
    retProps.setProperty(property, value);

    return retProps;
  }

  /**
   * This resets the editor to the original (or latest set, if setValue()
   * has been called) value of the edited property.
   */
  public void resetDefaultValue() {
    setSelectedValue(originalValue);
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
    ButtonModel selectedModel = buttonGroup.getSelection();
    String currentValue = "";
    if (selectedModel != null) {
      currentValue = buttonGroup.getSelection().getActionCommand();
    }
    return (! currentValue.equals(originalValue));
  }

  /**
   * Run when the PropertyEditor may have changed enabled states.
   *
   * This is a default implementation of updateEnabledState. If the
   * labelComponent and valueComponent attributes are set, it will call
   * setEnabled on those.
   *
   * Subclasses which do not use the default labelComponent and
   * valueComponent attributes, or which require additional functionality,
   * should override this method.
   */
  protected void updateEditorEnabled() {
    Enumeration<AbstractButton> buttons = buttonGroup.getElements();
    while (buttons.hasMoreElements()) {
      buttons.nextElement().setEnabled(isEditorEnabled());
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
    return labelString;
  }

  /**
   * Accepts or rejects the initial focus for this component.
   */
  public boolean acceptDefaultFocus() {
    if (isEditorEnabled()) {
      Enumeration<AbstractButton> buttonEnum = buttonGroup.getElements();
      // for some reason this returns false on dialogs.
      /*
        while (buttonEnum.hasMoreElements()) {
        AbstractButton button = buttonEnum.nextElement();
        if (button.requestFocusInWindow())
        return true;
        }
        return false;
      */
      AbstractButton button = buttonEnum.nextElement();
      button.requestFocusInWindow();
      return true;
    } else {
      return false;
    }
  }

}
