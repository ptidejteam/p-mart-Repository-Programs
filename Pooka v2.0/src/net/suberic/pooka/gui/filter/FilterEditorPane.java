package net.suberic.pooka.gui.filter;
import net.suberic.pooka.gui.search.*;
import net.suberic.pooka.*;
import net.suberic.util.gui.propedit.*;
import net.suberic.util.VariableBundle;
import javax.swing.*;
import java.util.Vector;

/**
 * This is a class that lets you choose your filter actions.
 */
public class FilterEditorPane extends LabelValuePropertyEditor implements java.awt.event.ItemListener {

  JLabel label;
  JComboBox typeCombo;
  JPanel filterConfigPanel;
  java.awt.CardLayout layout;

  java.util.HashMap editorTable;

  /**
   * Configures the FilterEditorPane.
   */
  public void configureEditor(String propertyName, String template, String propertyBaseName, PropertyEditorManager newManager) {
    configureBasic(propertyName, template, propertyBaseName, newManager);

    getLogger().fine("property is " + property + "; editorTemplate is " + editorTemplate);

    editorTable = new java.util.HashMap();

    // create the label
    label = new JLabel(manager.getProperty(editorTemplate + ".label", "Action"));

    // find out if we're a display filter or a backend filter
    String filterType = manager.getProperty(editorTemplate + ".filterType", "display");

    // create the combo
    Vector filterLabels = null;
    if (filterType.equalsIgnoreCase("display"))
      filterLabels = Pooka.getSearchManager().getDisplayFilterLabels();
    else
      filterLabels = Pooka.getSearchManager().getBackendFilterLabels();

    typeCombo = new JComboBox(filterLabels);
    typeCombo.addItemListener(this);


    // create the filterConfigPanel.

    String currentClassValue = manager.getProperty(property + ".class", "");
    String selectedLabel = null;

    filterConfigPanel = new JPanel();
    layout = new java.awt.CardLayout();
    filterConfigPanel.setLayout(layout);
    for (int i = 0; i < filterLabels.size(); i++) {
      String label = (String) filterLabels.elementAt(i);
      FilterEditor currentEditor = Pooka.getSearchManager().getEditorForFilterLabel(label);
      currentEditor.configureEditor(manager, property);

      filterConfigPanel.add(label, currentEditor);
      editorTable.put(label, currentEditor);

      if (selectedLabel == null && currentClassValue != null &&  currentClassValue.equalsIgnoreCase(currentEditor.getFilterClassValue()))
        selectedLabel = label;
    }

    if (selectedLabel != null)
      typeCombo.setSelectedItem(selectedLabel);

    JPanel tmpPanel = new JPanel();
    SpringLayout layout = new SpringLayout();
    tmpPanel.setLayout(layout);

    tmpPanel.add(typeCombo);
    tmpPanel.add(filterConfigPanel);

    layout.putConstraint(SpringLayout.NORTH, typeCombo, 0, SpringLayout.NORTH, tmpPanel);
    layout.putConstraint(SpringLayout.WEST, typeCombo, 0, SpringLayout.WEST, tmpPanel);
    layout.putConstraint(SpringLayout.SOUTH, tmpPanel, 0, SpringLayout.SOUTH, typeCombo);
    layout.putConstraint(SpringLayout.WEST, filterConfigPanel, 5, SpringLayout.EAST, typeCombo);
    layout.putConstraint(SpringLayout.EAST, tmpPanel, 5, SpringLayout.EAST, filterConfigPanel);

    tmpPanel.setPreferredSize(new java.awt.Dimension(tmpPanel.getPreferredSize().width, typeCombo.getMinimumSize().height));
    tmpPanel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, typeCombo.getMinimumSize().height));

    this.add(label);
    this.add(tmpPanel);

    labelComponent = label;
    valueComponent = tmpPanel;

    resetDefaultValue();
  }

  /**
   * Sets the value for this PropertyEditor.
   */
  public void setValue() throws PropertyValueVetoException {
    validateProperty();
    getFilterEditor().setValue();
  }

  /**
   * Validates this PropertyEditor.
   */
  public void validateProperty() throws PropertyValueVetoException {
    getFilterEditor().validate();
  }

  /**
   * Returns the currently selected FilterEditor.
   */
  public FilterEditor getFilterEditor() {
    return (FilterEditor) editorTable.get(typeCombo.getSelectedItem());
  }

  /**
   * Gets the value that would be set by this PropertyEditor.
   */
  public java.util.Properties getValue() {
    return getFilterEditor().getValue();
  }

  /**
   * Resets the current Editor to its original value.
   */
  public void resetDefaultValue() {
    // get the current value, if any
    String currentLabel = Pooka.getSearchManager().getLabelForFilterClass(manager.getProperty(property + ".class", ""));
    if (currentLabel != null) {
      typeCombo.setSelectedItem(currentLabel);
    } else {
      typeCombo.setSelectedIndex(0);
    }

    FilterEditor currentEditor = getFilterEditor();

  }

  /**
   * This handles the switch of the filterConfigPanel when the typeCombo
   * value changes.
   */
  public void itemStateChanged(java.awt.event.ItemEvent e) {
    String selectedString = (String) typeCombo.getSelectedItem();

    layout.show(filterConfigPanel, selectedString);
  }

  /**
   * Run when the PropertyEditor may have changed enabled states.
   */
  protected void updateEditorEnabled() {
    typeCombo.setEnabled(isEditorEnabled());
  }

}
