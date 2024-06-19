package net.suberic.util.gui.propedit;
import javax.swing.*;
import net.suberic.util.*;
import java.awt.CardLayout;
import javax.swing.event.*;
import java.util.*;
import javax.swing.*;

/**
 * This class will make an editor for a list of elements, where each
 * element will be displayed on the left and, by selecting one of these
 * elements, the editor for that item will appear in the panel to the
 * right.
 *
 * Configuration is as follows:
 *
 * Foo.propertyType=Sectioned  --  shows this is a property editor that
 *                                 uses a SectionedEditorPane
 *
 * Foo.editableFields=Foo.bar:Foo.baz -- shows which subfields are to be edited
 *
 * Foo._default=Foo.bar -- shows that by default, the editor for Foo.bar
 *                         is shown.  If this is not included or blank,
 *                         then no editor is displayed by default.
 *
 * The value for Foo itself is not used.
 *
 * If your Foo.editableFields=Foo.bar:.baz:Frotz.zork, then the values
 * edited will be defined by Foo.bar, Foo.baz, and Frotz.zork.
 */

public class SectionedEditorPane extends CompositeSwingPropertyEditor implements ListSelectionListener {

  JList optionList;
  JPanel entryPanel;
  boolean changed = false;
  DefaultListModel optionListModel;
  List templates;

  Hashtable<String, SwingPropertyEditor> currentPanels = new Hashtable<String, SwingPropertyEditor>();

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
    configureBasic(propertyName, template, propertyBaseName, newManager);

    // create the editors list.
    editors = new Vector();

    // create the list of properties to be edited.

    List propertyList = manager.getPropertyAsList(propertyName + ".editableFields", "");

    optionList = createOptionList(propertyList);

    JScrollPane optionScrollPane = new JScrollPane(optionList);
    SpringLayout layout = new SpringLayout();
    this.setLayout(layout);

    this.add(optionScrollPane);
    layout.putConstraint(SpringLayout.WEST, optionScrollPane, 5, SpringLayout.WEST, this);
    layout.putConstraint(SpringLayout.NORTH, optionScrollPane, 5, SpringLayout.NORTH, this);
    //layout.putConstraint(SpringLayout.SOUTH, this, 5, SpringLayout.SOUTH, optionScrollPane);
    layout.putConstraint(SpringLayout.SOUTH, optionScrollPane, -5, SpringLayout.SOUTH, this);

    // create entryPanels (the panels which show the editors for each
    // property in the optionList) for each option.

    entryPanel = createEntryPanel(propertyList);

    java.awt.Component entryComponent = entryPanel;
    if (manager.getProperty(template + "._useScrollPane", "false").equalsIgnoreCase("true")) {
      JScrollPane jsp = new JScrollPane(entryPanel);
      /*
      java.awt.Dimension size = jsp.getPreferredSize();
      size.height = Math.min(size.height, 400);
      size.width = Math.min(size.width, 475);
      jsp.setPreferredSize(size);
      */
      entryComponent = jsp;
    }

    this.add(entryComponent);

    // the entry components should handle their offsets themselves, so
    // put them on the very edges.
    layout.putConstraint(SpringLayout.WEST, entryComponent, 0 ,SpringLayout.EAST, optionScrollPane);

    layout.putConstraint(SpringLayout.NORTH, entryComponent, 0 ,SpringLayout.NORTH, this);
    layout.putConstraint(SpringLayout.SOUTH, this, 0 ,SpringLayout.SOUTH, entryComponent);
    layout.putConstraint(SpringLayout.EAST, this, 0 ,SpringLayout.EAST, entryComponent);

    updateEditorEnabled();

    manager.registerPropertyEditor(property, this);

    optionList.addListSelectionListener(this);
  }

  /**
   * Creates the list of edited items.
   */
  private JList createOptionList(List editedProperties) {

    optionListModel = new DefaultListModel();

    Iterator iter = editedProperties.iterator();
    while (iter.hasNext()) {
      String key = (String) iter.next();
      String iconString = manager.getProperty(key + ".Icon", "");
      Icon icon = null;
      if (iconString != "") {
        icon = manager.getIconManager().getIcon(iconString);
      }
      SEPListEntry listEntry = new SEPListEntry(manager.getProperty(key + ".Label", key), icon, key);
      optionListModel.addElement(listEntry);
    }

    JList returnValue =  new JList(optionListModel);
    returnValue.setSelectedIndex(0);
    returnValue.setCellRenderer(new SEPCellRenderer());
    return returnValue;
  }

  /**
   * This creates a panel for each option.  It uses a CardLayout.
   *
   * Note that this is also the section of code which determines which
   * subproperties are to be edited.
   */
  private JPanel createEntryPanel (List itemList) {
    CardLayout entryLayout = new CardLayout();
    JPanel panel = new JPanel(entryLayout);

    for (Object o: itemList) {
      String rootProp = (String) o;
      SwingPropertyEditor sep = createEditorPane(rootProp, rootProp);

      getLogger().fine("creating editor for " + rootProp);
      // save reference to new pane in hash table
      currentPanels.put(rootProp, sep);
      editors.add(sep);

      panel.add(rootProp, sep);
    }
    String defaultProperty = manager.getProperty(property + "._default", "");

    if (defaultProperty != "")
      entryLayout.show(panel, defaultProperty);

    return panel;
  }

  /**
   * Called when the selected value changed.  Should result in the
   * entryPane changing.
   */
  public void valueChanged(ListSelectionEvent e) {

    CardLayout entryLayout = (CardLayout)entryPanel.getLayout();

    String selectedId = ((SEPListEntry)((JList)e.getSource()).getSelectedValue()).getKey();

    getLogger().fine("selectedId = " + selectedId);
    if (selectedId != null) {
      SwingPropertyEditor newSelected = currentPanels.get(selectedId);
      getLogger().fine("newSelected = " + newSelected);
      entryLayout.show(entryPanel, selectedId);
    }

  }

  /**
   * Edits the currently selected value.
   */
  public void editSelectedValue() {
  }

  /**
   * Sets the value for this SectionedEditorPane.
   */
  public void setValue() throws PropertyValueVetoException {
    if (isEnabled()) {
      super.setValue();
    }
  }

  /**
   * Resets the default values.
   */
  public void resetDefaultValue() throws PropertyValueVetoException {

    if (isChanged()) {
      firePropertyChangingEvent(originalValue);
      optionListModel.removeAllElements();
      entryPanel.removeAll();

      firePropertyChangedEvent(originalValue);
    }

    java.awt.Component[] components = entryPanel.getComponents();
    for (int i = 0; i < components.length; i++) {
      ((CompositeEditorPane)components[i]).resetDefaultValue();
    }
  }

  /**
   * Returns the currently edited values as a Properties object.
   */
  public java.util.Properties getValue() {
    java.util.Properties currentRetValue = super.getValue();
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
   * Returns the optionList.
   */
  public JList getOptionList() {
    return optionList;
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
    getLogger().fine("creating editor for " + subProperty + ", template " + subTemplate);
    return (SwingPropertyEditor) manager.getFactory().createEditor(subProperty, subTemplate, manager);
  }

  /**
   * Run when the PropertyEditor may have changed enabled states.
   */
  protected void updateEditorEnabled() {

    optionList.setEnabled(isEditorEnabled());

    for (int i = 0; i < editors.size() ; i++) {
      PropertyEditorUI current = (PropertyEditorUI) editors.get(i);
      if (isEditorEnabled()) {
        current.removeDisableMask(this);
      } else {
        current.addDisableMask(this);
      }
    }
  }

  /**
   * Returns the helpId for this editor.
   */
  public String getHelpID() {
    CardLayout entryLayout = (CardLayout)entryPanel.getLayout();
    SEPListEntry selectedValue = (SEPListEntry) optionList.getSelectedValue();
    if (selectedValue != null) {
      String selectedId = selectedValue.getKey();

      if (selectedId != null) {
        SwingPropertyEditor newSelected = currentPanels.get(selectedId);
        return newSelected.getHelpID();
      }
    }
    return getEditorTemplate();
  }

  class SEPCellRenderer extends JLabel implements ListCellRenderer {

    public java.awt.Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      SEPListEntry sepValue = (SEPListEntry) value;
      String label = sepValue.getLabel();
      this.setText(label);
      this.setIcon(sepValue.getIcon());
      if (isSelected) {
        setBackground(list.getSelectionBackground());
        setForeground(list.getSelectionForeground());
      } else {
        setBackground(list.getBackground());
        setForeground(list.getForeground());
      }
      setEnabled(list.isEnabled());
      setFont(list.getFont());
      setOpaque(true);
      return this;
    }
  }

  class SEPListEntry {
    String label;
    Icon icon;
    String key;

    public SEPListEntry(String pLabel, Icon pIcon, String pKey) {
      label = pLabel;
      icon = pIcon;
      key = pKey;
    }

    public String getLabel() {
      return label;
    }

    public Icon getIcon() {
      return icon;
    }

    public String getKey() {
      return key;
    }
  }

}
