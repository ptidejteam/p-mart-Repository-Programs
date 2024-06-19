package net.suberic.util.gui.propedit;
import javax.swing.*;
import net.suberic.util.*;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.util.*;

/**
 * An EditorPane which allows a user to select from a list of choices.
 */
public class ListEditorPane extends LabelValuePropertyEditor {
  protected int originalIndex;
  protected String mOriginalValue;
  protected JLabel label;
  protected JComboBox inputField;
  JButton addButton;
  protected HashMap labelToValueMap = new HashMap();
  protected int currentIndex = -1;

  // configuration settings
  static String INCLUDE_ADD_BUTTON = "_includeAddButton";
  static String ALLOWED_VALUES = "allowedValues";
  static String LIST_MAPPING = "listMapping";
  static String INCLUDE_DEFAULT_OPTION = "_includeDefault";
  static String INCLUDE_NEW_OPTION = "_includeNew";
  public static String SELECTION_DEFAULT = "__default";
  public static String SELECTION_NEW = "__new";

  /**
   * @param propertyName The property to be edited.
   * @param template The property that will define the layout of the
   *                 editor.
   * @param manager The PropertyEditorManager that will manage the
   *                   changes.
   */
  public void configureEditor(String propertyName, String template, String propertyBaseName, PropertyEditorManager newManager) {
    configureBasic(propertyName, template, propertyBaseName, newManager);

    label = createLabel();

    inputField = createComboBox();

    Box inputBox = new Box(BoxLayout.X_AXIS);
    inputField.setPreferredSize(inputField.getMinimumSize());
    inputField.setMaximumSize(inputField.getMinimumSize());
    inputBox.add(inputField);
    inputBox.add(Box.createGlue());

    if (manager.getProperty(editorTemplate + "." + INCLUDE_ADD_BUTTON, "false").equalsIgnoreCase("true")) {
      addButton = createAddButton();
      inputBox.add(addButton);
    }

    inputBox.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, inputBox.getMinimumSize().height));

    this.add(label);
    this.add(inputBox);
    updateEditorEnabled();

    labelComponent = label;
    valueComponent = inputBox;

    manager.registerPropertyEditor(property, this);

    // if we're showing something other than the actually configured
    // value, fire an event.
    if (isChanged()) {
      String newValue = (String)labelToValueMap.get(inputField.getSelectedItem());
      try {
        firePropertyChangingEvent(newValue);
        firePropertyChangedEvent(newValue);
      } catch (PropertyValueVetoException pvve) {
        manager.getFactory().showError(inputField, "Error changing value " + label.getText() + " to " + newValue + ":  " + pvve.getReason());
        inputField.setSelectedIndex(currentIndex);
      }
    }
  }

  /**
   * Creates the JComboBox with the appropriate options.
   */
  protected JComboBox createComboBox() {
    String originalValue = manager.getProperty(property, "");
    // we set this to the real original value, rather than the default
    // value if none is set.
    mOriginalValue = originalValue;
    // now we get the default value.
    if (originalValue.equalsIgnoreCase(""))
      originalValue = manager.getProperty(editorTemplate, "");
    String currentItem;
    originalIndex=-1;
    Vector items = new Vector();
    List<String> tokens;

    String allowedValuesString = manager.getProperty(editorTemplate + "." + ALLOWED_VALUES, "");
    if (manager.getProperty(allowedValuesString, "") != "") {
      tokens = manager.getPropertyAsList(allowedValuesString, "");
      manager.addPropertyEditorListener(allowedValuesString, new ListEditorListener());
      // if we're loading this from a property list, check for options for
      // default values and adding new values.
      if (manager.getProperty(editorTemplate + "." + INCLUDE_NEW_OPTION, "false").equalsIgnoreCase("true")) {
        tokens.add(0, SELECTION_NEW);
      }
      if (tokens != null && tokens.size() > 0) {
        if (manager.getProperty(editorTemplate + "." + INCLUDE_DEFAULT_OPTION, "false").equalsIgnoreCase("true")) {
          tokens.add(0, SELECTION_DEFAULT);
        }
      }
    } else {
      if (manager.getProperty(editorTemplate + "." + INCLUDE_NEW_OPTION, "false").equalsIgnoreCase("true")) {
        tokens = new ArrayList<String>();
        tokens.add(0, SELECTION_NEW);
      } else {
        tokens = manager.getPropertyAsList(editorTemplate + "." + ALLOWED_VALUES, "");
      }
    }

    for (int i = 0; i < tokens.size(); i++) {
      currentItem = tokens.get(i);

      String itemLabel = manager.getProperty(editorTemplate + "." + LIST_MAPPING + "." + currentItem.toString() + ".label", "");
      String itemValue = manager.getProperty(editorTemplate + "." + LIST_MAPPING + "." + currentItem.toString() + ".value", "");

      // special cases
      if (currentItem.equals(SELECTION_DEFAULT)) {
        if (itemLabel.length() < 1)
          itemLabel = manager.getProperty("ListEditorPane.button.default", "< Default Value >");

        if (itemValue.length() < 1)
          itemValue = currentItem;

        // and set a blank entry as equal to the default option.
        if (originalValue.length() < 1) {
          originalValue = itemValue;
        }
      } else if (currentItem.equals(SELECTION_NEW)) {
        if (itemLabel.length() < 1)
          itemLabel = manager.getProperty("ListEditorPane.button.new", "< Create New Value >");

        if (itemValue.length() < 1)
          itemValue = currentItem;
      } else {
        // default case
        if (itemLabel.length() < 1)
          itemLabel = currentItem;

        if (itemValue.length() < 1)
          itemValue = currentItem;

      }
      if (itemValue.equals(originalValue)) {
        originalIndex=i;
        currentIndex=i;
      }
      items.add(itemLabel);
      labelToValueMap.put(itemLabel, itemValue);
    }

    if (originalIndex == -1) {
      // if there are no valid values and we have a new value option, then
      // don't add the current value if it's the default option.
      if (! (manager.getProperty(editorTemplate + "." + INCLUDE_NEW_OPTION, "false").equalsIgnoreCase("true") && SELECTION_DEFAULT.equalsIgnoreCase(originalValue))) {
        items.add(originalValue);
        labelToValueMap.put(originalValue, originalValue);
        originalIndex = items.size() - 1;
      } else {
        originalIndex = 0;
      }
    }

    JComboBox jcb = new JComboBox(items);
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
   * Updates the combo box with the new value(s).
   */
  private void updateComboBox(String newValue) {
    Vector items = new Vector();
    StringTokenizer tokens = new StringTokenizer(newValue, ":");
    String currentValue = (String) inputField.getSelectedItem();

    String currentItem;
    for (int i=0; tokens.hasMoreTokens(); i++) {
      currentItem = tokens.nextToken();

      String itemLabel = manager.getProperty(editorTemplate + "." + LIST_MAPPING + "." + currentItem.toString() + ".label", "");
      if (itemLabel.length() < 1)
        itemLabel = currentItem.toString();

      String itemValue = manager.getProperty(editorTemplate + "." + LIST_MAPPING + "." + currentItem.toString() + ".value", "");
      if (itemValue.length() < 1)
        itemValue = currentItem.toString();

      if (itemValue.equals(originalValue)) {
        originalIndex=i;
      }
      if (itemValue.equals(currentValue)) {
        currentIndex=i;
      }
      items.add(itemLabel);
      labelToValueMap.put(itemLabel, itemValue);
    }

    ComboBoxModel newModel = new DefaultComboBoxModel(items);
    newModel.setSelectedItem(currentValue);
    inputField.setModel(newModel);

  }

  /**
   * Creates a button to add a new value to the List.
   */
  public JButton createAddButton() {
    JButton returnValue = new JButton(manager.getProperty("button.add", "Add"));
    returnValue.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          addNewEntry();
        }
      });

    return returnValue;
  }

  /**
   * Opens up an editor to add a new Item to the List.
   */
  public void addNewEntry() {
    String editedProperty = manager.getProperty(editorTemplate + "." + ALLOWED_VALUES, "");
    //
    //manager.getFactory().showNewEditorWindow("Add property", editedProperty, editedProperty, manager);
    PropertyEditorUI sourceEditor = manager.getPropertyEditor(editedProperty);
    if (sourceEditor == null) {
      sourceEditor = manager.getFactory().createEditor(editedProperty, editedProperty, manager);
    }
    if (sourceEditor instanceof MultiEditorPane) {
      MultiEditorPane multiEditor = (MultiEditorPane) sourceEditor;
      multiEditor.addNewValue(multiEditor.getNewValueName(), this.getPropertyEditorPane().getContainer());
    }
  }

  //  as defined in net.suberic.util.gui.PropertyEditorUI

  /**
   * This writes the currently configured value in the PropertyEditorUI
   * to the source VariableBundle.
   */
  public void setValue() throws PropertyValueVetoException {
    int newIndex = inputField.getSelectedIndex();
    String currentValue = (String)labelToValueMap.get(inputField.getSelectedItem());

    if (isEditorEnabled() && isChanged()) {
      manager.setProperty(property, currentValue);
      mOriginalValue = (String)labelToValueMap.get(inputField.getSelectedItem());
    }
  }


  /**
   * This checks that the currently configured value is valid.
   */
  public void validateProperty() throws PropertyValueVetoException {
    int newIndex = inputField.getSelectedIndex();
    String currentValue = (String)labelToValueMap.get(inputField.getSelectedItem());
    if (newIndex != currentIndex) {
      firePropertyChangingEvent(currentValue);
      firePropertyChangedEvent(currentValue);
      currentIndex = newIndex;
    }
    firePropertyCommittingEvent(currentValue);
  }

  /**
   * Returns the current values of the edited properties as a
   * java.util.Properties object.
   */
  public java.util.Properties getValue() {
    java.util.Properties retProps = new java.util.Properties();

    retProps.setProperty(property, (String)labelToValueMap.get(inputField.getSelectedItem()));

    return retProps;
  }

  /**
   * This resets the editor to the original (or latest set, if setValue()
   * has been called) value of the edited property.
   */
  public void resetDefaultValue() {
    // this will be handled by the ItemListener we have on the inputField,
    // so we don't have to notify listeners here.

    inputField.setSelectedIndex(originalIndex);
  }

  /**
   * Returns whether or not the current list selection has changed from
   * the last save.
   */
  public boolean isChanged() {
    if (mOriginalValue == null)
      return (mOriginalValue != (String)labelToValueMap.get(inputField.getSelectedItem()));
    else
      return (! mOriginalValue.equals((String)labelToValueMap.get(inputField.getSelectedItem())));

  }

  /**
   * Run when the PropertyEditor may have changed enabled states.
   */
  protected void updateEditorEnabled() {
    if (inputField != null) {
      inputField.setEnabled(isEditorEnabled());
    }
    if (addButton != null) {
      addButton.setEnabled(isEditorEnabled());
    }
  }

  /**
   * This listens to the property that it currently providing the list
   * of allowed values for this List.  If it changes, then the allowed
   * values list also is updated.
   */
  public class ListEditorListener extends PropertyEditorAdapter {

    /**
     * Called after a property changes.
     */
    public void propertyChanged(PropertyEditorUI ui, String property, String newValue) {
      updateComboBox(newValue);
    }

  }
}
