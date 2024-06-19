package net.suberic.pooka.gui.search;
import net.suberic.util.VariableBundle;
import net.suberic.util.gui.propedit.*;
import javax.swing.*;
import java.util.*;

/**
 * This is a class which lets you choose SearchTerms as properties.
 */
public class SearchEditorPane extends LabelValuePropertyEditor {

  Properties originalProperties;

  SearchEntryPanel searchEntryPanel;

  /**
   * @param propertyName The property to be edited.
   * @param template The property that will define the layout of the
   *                 editor.
   * @param manager The PropertyEditorManager that will manage the
   *                   changes.
   */
  public void configureEditor(String propertyName, String template, String propertyBaseName, PropertyEditorManager newManager) {
    configureBasic(propertyName, template, propertyBaseName, newManager);

    searchEntryPanel = new SearchEntryPanel(net.suberic.pooka.Pooka.getSearchManager(), property, manager.getFactory().getSourceBundle());
    originalProperties = searchEntryPanel.generateSearchTermProperties(property);

    //this.add(searchEntryPanel);
    labelComponent = new JLabel(manager.getProperty("title.search.where", "Where"));
    valueComponent = searchEntryPanel;

    updateEditorEnabled();
  }

  /**
   * Sets the value for this PropertyEditor.
   */
  public void setValue() throws PropertyValueVetoException {
    // we need to go through all of the new properties any only set
    // the ones that have changed.  we also need to remove any that
    // no longer exist.
    Properties newValues = searchEntryPanel.generateSearchTermProperties(property);
    Enumeration newKeys = newValues.keys();

    Set originalKeys = originalProperties.keySet();

    while (newKeys.hasMoreElements()) {
      Object currentKey = newKeys.nextElement();
      if (originalKeys.contains(currentKey)) {
        originalKeys.remove(currentKey);
        String originalValue = originalProperties.getProperty((String) currentKey);
        String newValue = newValues.getProperty((String) currentKey);
        if (originalValue == null ||  ! originalValue.equals(newValue))
          manager.setProperty((String) currentKey, newValue);
      } else {
        manager.setProperty((String) currentKey, newValues.getProperty((String) currentKey));
      }
    }

    Iterator iter = originalKeys.iterator();
    while (iter.hasNext()) {
      manager.removeProperty((String) iter.next());
    }
  }

  public void validateProperty() throws PropertyValueVetoException {

  }

  /**
   * Returns the values that would be set by this SearchEditorPane.
   */
  public Properties getValue() {
    return searchEntryPanel.generateSearchTermProperties(property);
  }

  /**
   * Resets the current Editor to its original value.
   */
  public void resetDefaultValue() {
    searchEntryPanel.setSearchTerm(property, manager.getFactory().getSourceBundle());
  }

  /**
   * Run when the PropertyEditor may have changed enabled states.
   */
  protected void updateEditorEnabled() {
    searchEntryPanel.setEnabled(isEditorEnabled());
  }
}
