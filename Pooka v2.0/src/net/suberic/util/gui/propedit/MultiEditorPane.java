package net.suberic.util.gui.propedit;
import javax.swing.*;
import net.suberic.util.*;
import java.util.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.Container;
import java.awt.Component;
import java.awt.event.*;

/**
 * This class will make an editor for a list of elements, where each of
 * the elements has a set of subproperties.
 *
 * Configuration is as follows:
 *
 * Foo.propertyType=Multi  --  shows this is a property editor for an
 *                             attribute with multiple values.
 *
 * Foo.editableFields=.bar:.baz -- shows which subfields are to be edited
 *
 *
 * So if your Foo property equals "fooOne:fooTwo", then you'll end up with
 * a MultiPropertyEditor that has an entry for fooOne and fooTwo, along with
 * ways to add and remove items.
 *
 * If your Foo.editableFields=bar:baz, then your editor screen for, say,
 * fooOne will have two entries, one for Foo.fooOne.bar, and the other for
 * Foo.fooOne.baz.  These editors will use Foo.editableFields.bar and
 * Foo.editableFields.baz for templates.
 *
 */

public class MultiEditorPane extends CompositeSwingPropertyEditor implements ListSelectionListener {
  protected JTable optionTable;
  JPanel entryPanel;
  protected JPanel buttonPanel;

  List<JButton> buttonList;
  boolean changed = false;
  List<String> removeValues = new ArrayList<String>();
  String propertyTemplate;
  List<String> displayProperties;

  protected Action[] mDefaultActions;

  /**
   * This configures this editor with the following values.
   *
   * @param propertyName The property to be edited.
   * @param template The property that will define the layout of the
   *                 editor.
   * @param manager The PropertyEditorManager that will manage the
   *                   changes.
   */
  public void configureEditor(String propertyName, String template, String propertyBaseName, PropertyEditorManager newManager) {
    getLogger().fine("creating MultiEditorPane for property " + propertyName + ", template " + template);
    configureBasic(propertyName, template, propertyBaseName, newManager);

    // create the current list of edited items.  so if this is a User list,
    // these values might be 'allen', 'deborah', 'marc', 'jessica', etc.

    List<String> optionList = manager.getPropertyAsList(property, "");

    displayProperties = manager.getPropertyAsList(editorTemplate + "._displayProperties", "");

    optionTable = createOptionTable(optionList, displayProperties);
    JScrollPane optionScrollPane = new JScrollPane(optionTable);

    buttonPanel = createButtonPanel();

    getLogger().fine("MultiEditorPane for property " + propertyName + ", template " + template);

    doEditorPaneLayout(optionScrollPane, buttonPanel);

    updateEditorEnabled();

    manager.registerPropertyEditor(property, this);

  }

  /**
   * Lays out the MultiEditorPane.
   */
  public void doEditorPaneLayout(Component listPanel, Component buttonPanel) {
    SpringLayout layout = new SpringLayout();
    this.setLayout(layout);

    this.add(listPanel);
    this.add(buttonPanel);

    SpringLayout.Constraints ospConstraints = layout.getConstraints(listPanel);
    SpringLayout.Constraints buttonConstraints = layout.getConstraints(buttonPanel);

    Spring panelHeight = Spring.constant(0);
    panelHeight = Spring.max(panelHeight, ospConstraints.getHeight());
    panelHeight = Spring.max(panelHeight, buttonConstraints.getHeight());

    ospConstraints.setHeight(panelHeight);
    buttonConstraints.setHeight(panelHeight);

    layout.putConstraint(SpringLayout.WEST, listPanel, 5, SpringLayout.WEST, this);
    layout.putConstraint(SpringLayout.NORTH, listPanel, 5, SpringLayout.NORTH, this);
    layout.putConstraint(SpringLayout.SOUTH, this, 5, SpringLayout.SOUTH, listPanel);

    layout.putConstraint(SpringLayout.WEST, buttonPanel, 5, SpringLayout.EAST, listPanel);
    layout.putConstraint(SpringLayout.NORTH, buttonPanel, 5, SpringLayout.NORTH, this);
    layout.putConstraint(SpringLayout.EAST, this, 5 ,SpringLayout.EAST, buttonPanel);


  }

  /**
   * Creates the Option Table.  This is a JTable that lists the various
   * items that have been created.
   */
  private JTable createOptionTable(List<String> optionList, List<String> pDisplayProperties) {
    // first get the display properties and their labels.
    Vector columnLabels = new Vector();
    // first one is always the id.
    columnLabels.add(manager.getProperty(editorTemplate + "._label", editorTemplate));
    for (String subProperty: pDisplayProperties) {
      getLogger().fine("adding label for " + subProperty);

      String label = manager.getProperty(editorTemplate + "._displayProperties." + subProperty + ".label", subProperty);
      columnLabels.add(label);
    }

    DefaultTableModel dtm = new DefaultTableModel(columnLabels, 0);

    // now add the properties.

    for (String option: optionList) {
      Vector optionValues = createTableEntry(option, pDisplayProperties);
      dtm.addRow(optionValues);
    }

    JTable returnValue = new JTable(dtm);
    returnValue.setCellSelectionEnabled(false);
    returnValue.setColumnSelectionAllowed(false);
    returnValue.setRowSelectionAllowed(true);
    returnValue.setShowGrid(false);

    returnValue.getSelectionModel().addListSelectionListener(this);
    if (returnValue.getRowCount() > 0) {
      returnValue.setRowSelectionInterval(0,0);
    }
    return returnValue;
  }

  /**
   * Adds the given property to the property table.
   */
  public Vector createTableEntry(String option, List<String> pDisplayProperties) {
    Vector optionValues = new Vector();
    // first one is always the id, at least for now.
    optionValues.add(option);
    for (String subProperty: pDisplayProperties) {
      getLogger().fine("adding display property for " + option + "." + subProperty);
      optionValues.add(manager.getProperty(property + "." + option + "." + subProperty, subProperty));
    }

    return optionValues;
  }

  /**
   * Creates the box which holds the "Add", "Edit", and "Remove" buttons.
   */
  protected JPanel createButtonPanel() {
    getLogger().fine("creating buttons.");

    createActions();

    buttonList = new ArrayList<JButton>();
    JPanel returnValue = new JPanel();
    SpringLayout layout = new SpringLayout();
    returnValue.setLayout(layout);

    // FIXME i18n
    JButton addButton = createButton("Add", getAction("editor-add"), true);

    JButton editButton = createButton("Edit", getAction("editor-edit"), true);

    JButton removeButton = createButton("Remove", getAction("editor-delete"), false);

    returnValue.add(addButton);
    returnValue.add(editButton);
    returnValue.add(removeButton);

    layout.putConstraint(SpringLayout.NORTH, addButton, 0, SpringLayout.NORTH, returnValue);
    layout.putConstraint(SpringLayout.WEST, addButton, 5 ,SpringLayout.WEST, returnValue);
    layout.putConstraint(SpringLayout.EAST, returnValue, 5 ,SpringLayout.EAST, addButton);

    layout.putConstraint(SpringLayout.NORTH, editButton, 5, SpringLayout.SOUTH, addButton);
    layout.putConstraint(SpringLayout.WEST, editButton, 5 ,SpringLayout.WEST, returnValue);

    layout.putConstraint(SpringLayout.NORTH, removeButton, 5, SpringLayout.SOUTH, editButton);
    layout.putConstraint(SpringLayout.WEST, removeButton, 5 ,SpringLayout.WEST, returnValue);

    Spring buttonWidth = Spring.constant(0);

    SpringLayout.Constraints addConstraints = layout.getConstraints(addButton);
    SpringLayout.Constraints editConstraints = layout.getConstraints(editButton);
    SpringLayout.Constraints removeConstraints = layout.getConstraints(removeButton);

    buttonWidth = Spring.max(buttonWidth, addConstraints.getWidth());
    buttonWidth = Spring.max(buttonWidth, editConstraints.getWidth());
    buttonWidth = Spring.max(buttonWidth, removeConstraints.getWidth());

    addConstraints.setWidth(buttonWidth);
    editConstraints.setWidth(buttonWidth);
    removeConstraints.setWidth(buttonWidth);

    SpringLayout.Constraints panelConstraints = layout.getConstraints(returnValue);
    //panelConstraints.setWidth(Spring.sum(buttonWidth, Spring.constant(10)));

    return returnValue;
  }

  /**
   * Creates the actions for this editor.
   */
  protected void createActions() {
    mDefaultActions = new Action[] {
      new AddAction(),
      new EditAction(),
      new DeleteAction()
    };
  }

  /**
   * Creates a Button for the ButtonBox with the appropriate label and
   * Action.
   */
  private JButton createButton(String label, Action e, boolean isDefault) {
    JButton thisButton;

    thisButton = new JButton(manager.getProperty("label." + label, label));
    String mnemonic = manager.getProperty("label." + label + ".mnemonic", "");
    if (!mnemonic.equals(""))
      thisButton.setMnemonic(mnemonic.charAt(0));

    thisButton.setSelected(isDefault);

    thisButton.addActionListener(e);

    buttonList.add(thisButton);
    return thisButton;
  }


  /**
   * Called when the selected value changed.  Should result in the
   * entryPane changing.
   */
  public void valueChanged(ListSelectionEvent e) {

  }

  /**
   * Adds a new value to the edited List.
   */
  public void addNewValue(String newValueName) {
    try {
      List<String> newValueList = new ArrayList<String>();
      for (int i = 0; i < optionTable.getRowCount(); i++) {
        newValueList.add((String) optionTable.getValueAt(i, 0));
      }
      newValueList.add(newValueName);
      String newValue = VariableBundle.convertToString(newValueList);
      firePropertyChangingEvent(newValue) ;
      Vector newValueVector = createTableEntry(newValueName, displayProperties);
      ((DefaultTableModel)optionTable.getModel()).addRow(newValueVector);
      firePropertyChangedEvent(newValue);
      this.setChanged(true);

      optionTable.getSelectionModel().setSelectionInterval(optionTable.getModel().getRowCount(), optionTable.getModel().getRowCount() -1);
    } catch (PropertyValueVetoException pvve) {
      manager.getFactory().showError(getPropertyEditorPane().getContainer(), "Error adding value " + newValueName + " to " + property + ":  " + pvve.getReason());
    }
  }

  /**
   * Adds a new value to the edited List.
   */
  protected void addNewValue(String newValueName, Container container) {
    if (newValueName == null || newValueName.length() == 0)
      return;

    try {
      List<String> newValueList = new ArrayList<String>();
      for (int i = 0; i < optionTable.getRowCount(); i++) {
        newValueList.add((String) optionTable.getValueAt(i, 0));
      }
      newValueList.add(newValueName);
      String newValue = VariableBundle.convertToString(newValueList);
      firePropertyChangingEvent(newValue) ;
      Vector newValueVector = new Vector();
      newValueVector.add(newValueName);
      ((DefaultTableModel)optionTable.getModel()).addRow(newValueVector);
      firePropertyChangedEvent(newValue);
      this.setChanged(true);

      optionTable.getSelectionModel().setSelectionInterval(optionTable.getModel().getRowCount(), optionTable.getModel().getRowCount() -1);
      editSelectedValue(container);
    } catch (PropertyValueVetoException pvve) {
      manager.getFactory().showError(container, "Error adding value " + newValueName + " to " + property + ":  " + pvve.getReason());
    }
  }

  /**
   * Removes the currently selected value from the edited List.
   */
  public void removeSelectedValue() {
    int selectedRow = optionTable.getSelectedRow();
    String selValue = (String) optionTable.getValueAt(selectedRow, 0);
    if (selValue == null)
      return;

    try {
      List<String> newValueList = new ArrayList<String>();
      for (int i = 0; i < optionTable.getRowCount(); i++) {
        if (i != selectedRow) {
          newValueList.add((String) optionTable.getValueAt(i, 0));
        }
      }
      String newValue = VariableBundle.convertToString(newValueList);
      firePropertyChangingEvent(newValue) ;
      ((DefaultTableModel)optionTable.getModel()).removeRow(selectedRow);
      firePropertyChangedEvent(newValue);

      removeValues.add(property + "." + selValue);

      this.setChanged(true);
    } catch (PropertyValueVetoException pvve) {
      manager.getFactory().showError(this, "Error removing value " + selValue + " from " + property + ":  " + pvve.getReason());
    }

  }

  /**
   * Edits the currently selected value.
   */
  public void editSelectedValue() {
    editSelectedValue(this.getPropertyEditorPane().getContainer());
  }

  /**
   * Edits the currently selected value, using the given Container as an
   * editor source.
   */
  protected void editSelectedValue(Container container) {
    getLogger().fine("calling editSelectedValue().");
    int selectedRow = optionTable.getSelectedRow();
    if (selectedRow != -1) {
      String valueToEdit = (String) optionTable.getValueAt(selectedRow, 0);
      String editProperty = property + "." + valueToEdit;
      getLogger().fine("editing " + editProperty);

      manager.getFactory().showNewEditorWindow(manager.getProperty(editorTemplate + ".label", editProperty), manager.getFactory().createEditor(editProperty, editorTemplate + ".editableFields", editProperty, "Composite", manager), container);
    } else {
      getLogger().fine("editSelectedValue():  no selected value.");
    }

  }

  /**
   * Puts up a dialog to get a name for the new value.
   */
  public String getNewValueName() {
    boolean goodValue = false;
    boolean matchFound = false;

    String newName = null;
    newName = manager.getFactory().showInputDialog(this, manager.getProperty("MultiEditorPane.renameProperty", "Enter new name."));

    while (goodValue == false) {
      matchFound = false;
      if (newName != null) {

        for (int i = 0; i < optionTable.getRowCount() && matchFound == false; i++) {
          if (((String)optionTable.getValueAt(i, 0)).equals(newName))
            matchFound = true;

        }

        if (matchFound == false)
          goodValue = true;
        else
          newName = manager.getFactory().showInputDialog(this, manager.getProperty("MultiEditorPane.error.duplicateName", "Name already exists:") + "  " + newName + "\n" + manager.getProperty("MultiEditorPane.renameProperty", "Enter new name."));
      } else {
        goodValue = true;
      }
    }

    return newName;
  }

  /**
   * This produces a string for the given JList.
   */
  public String getStringFromList(DefaultListModel dlm) {

    String retVal;
    if (dlm.getSize() < 1)
      return "";
    else
      retVal = new String((String)dlm.getElementAt(0));

    for (int i = 1; i < dlm.getSize(); i++) {
      retVal = retVal.concat(":" + (String)dlm.getElementAt(i));
    }

    return retVal;
  }

  /**
   * Sets the value for this MultiEditorPane.
   */
  public void setValue() throws PropertyValueVetoException {
    if (isEditorEnabled()) {

      if (isChanged()) {
        getLogger().fine("setting property.  property is " + property + "; value is " + getCurrentValue());
        manager.setProperty(property, getCurrentValue());

        for (String removeProp: removeValues) {
          //manager.removeProperty(removeValues.get(i));
          Set<String> subProperties = manager.getPropertyNamesStartingWith(removeProp + ".");
          for (String subProp: subProperties) {
            manager.removeProperty(subProp);
          }
        }
        removeValues = new ArrayList<String>();

      }
    }
  }

  /**
   * Returns the current value from the table.
   */
  public String getCurrentValue() {
    List<String> values = new ArrayList<String>();
    for (int i = 0; i < optionTable.getRowCount(); i++) {
      values.add((String) optionTable.getValueAt(i, 0));
    }
    return VariableBundle.convertToString(values);
  }

  /**
   * Resets the default values.
   */
  public void resetDefaultValue() throws PropertyValueVetoException {

    //FIXME
    throw new UnsupportedOperationException("reset not yet implemented for MultiEditorPane.");

    /*
    removeValues = new Vector();

    if (isChanged()) {
      firePropertyChangedEvent(originalValue);
    }
    */
  }

  /**
   * Returns the currently edited values as a Properties object.
   */
  public java.util.Properties getValue() {
    java.util.Properties currentRetValue = new java.util.Properties();
    currentRetValue.setProperty(property, getCurrentValue());
    return currentRetValue;
  }

  /**
   * Returns whether or not the top-level edited values of this EditorPane
   * have changed.
   */
  public boolean isChanged() {
    return changed;
  }

  /**
   * Sets whether or not the top-level edited values of this EditorPane
   * have changed.
   */
  public void setChanged(boolean newChanged) {
    changed=newChanged;
  }

  /**
   * Returns the entryPanel.
   */
  public JPanel getEntryPanel() {
    return entryPanel;
  }

  /**
   * Creates an editor.
   */
  public SwingPropertyEditor createEditorPane(String subProperty, String subTemplate) {
    return (SwingPropertyEditor) manager.getFactory().createEditor(subProperty, subTemplate, "Composite", manager);
  }

  /**
   * Run when the PropertyEditor may have changed enabled states.
   */
  protected void updateEditorEnabled() {
    for (int i = 0; i < buttonList.size(); i++) {
      buttonList.get(i).setEnabled(isEditorEnabled());
    }
  }

  /**
   * Removes the PropertyEditor.
   */
  public void remove() {
    manager.removePropertyEditorListeners(getProperty());
  }

  /**
   * Returns the actions associated with this editor.
   */
  public Action[] getActions() {
    return mDefaultActions;
  }

  /**
   * Returns the action for the given identifier.
   */
  public Action getAction(String name) {
    for (Action action: mDefaultActions) {
      if (action.getValue(Action.NAME) != null && action.getValue(Action.NAME).equals(name))
        return action;
    }

    return null;
  }

  public class AddAction extends AbstractAction {
    public AddAction() {
      //super("address-add");
      super("editor-add");
    }

    public void actionPerformed(ActionEvent e) {
      /*
      setBusy(true);
      performAdd();
      setBusy(false);
      */
      // check to see if we want to add a new value using a
      // wizard
      String newValueTemplate = manager.getProperty(editorTemplate + "._addValueTemplate", "");
      if (newValueTemplate.length() > 0) {
        manager.getFactory().showNewEditorWindow(manager.getProperty(newValueTemplate + ".label", newValueTemplate), manager.getFactory().createEditor(newValueTemplate, newValueTemplate, manager), getPropertyEditorPane().getContainer());

      } else {
        addNewValue(getNewValueName(), getPropertyEditorPane().getContainer());
      }

    }
  }

  public class EditAction extends AbstractAction {
    public EditAction() {
      super("editor-edit");
    }

    public void actionPerformed(ActionEvent e) {
      /*
      setBusy(true);
      editSelectedValue();
      setBusy(false);
      */
      editSelectedValue();
    }
  }

  public class DeleteAction extends AbstractAction {
    public DeleteAction() {
      super("editor-delete");
    }

    public void actionPerformed(ActionEvent e) {
      removeSelectedValue();
      /*
      setBusy(true);
      performDelete();
      setBusy(false);
      */
    }
  }


}
