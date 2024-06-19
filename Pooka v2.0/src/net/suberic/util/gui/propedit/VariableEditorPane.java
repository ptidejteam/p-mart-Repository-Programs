package net.suberic.util.gui.propedit;
import javax.swing.*;
import java.util.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.CardLayout;

/**
 * This will made a panel which can change depending on
 * exact properties which are then edited will depend on the value of
 * another propery.
 *
 * Special settings:  keyProperty is the property that will be used
 * to define this editor.
 */
public class VariableEditorPane extends CompositeSwingPropertyEditor {

  HashMap<String, PropertyEditorUI> idToEditorMap = new HashMap<String, PropertyEditorUI>();
  String keyProperty;
  String currentKeyValue = null;

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
    debug = manager.getProperty("editors.debug", "false").equalsIgnoreCase("true");

    getLogger().fine("VEP: editorTemplate=" + editorTemplate + ", getProperty template.keyProperty = '" + manager.getProperty(editorTemplate + ".keyProperty", "") + "'");
    keyProperty = createSubProperty(manager.getProperty(editorTemplate + ".keyProperty", ""));
    getLogger().fine("VEP:  keying off property " + keyProperty);

    editors = new Vector();

    manager.addPropertyEditorListener(keyProperty, new PropertyEditorAdapter() {
        public void propertyChanged(PropertyEditorUI ui, String prop, String newValue) {
          showPanel(newValue);
        }
      });

    this.setLayout(new java.awt.CardLayout());

    String currentValue = manager.getProperty(keyProperty, "");
    getLogger().fine("VEP:  currentValue for " + keyProperty + " = " + currentValue);
    if (currentValue == "") {
      // check the editor for this, if any.
      PropertyEditorUI keyEditor = manager.getPropertyEditor(keyProperty);
      if (keyEditor != null) {
        currentValue = keyEditor.getValue().getProperty(keyProperty, "");
      }
    }

    showPanel(currentValue);

    manager.registerPropertyEditor(property, this);
  }

  /**
   * This shows the editor window for the configured value.
   */
  public void showPanel(String selectedId) {
    boolean enableMe = true;
    if (selectedId == null || selectedId.equals("")) {
      enableMe = false;
    }

    CardLayout layout = (CardLayout) getLayout();

    PropertyEditorUI newSelected = idToEditorMap.get(selectedId);
    if (newSelected == null) {
      // we'll have to make a new window.
      if (selectedId == null || selectedId.equals("")) {
        JPanel jp = new JPanel();
        this.add(selectedId, jp);
      } else {
        SwingPropertyEditor spe = createEditorPane(selectedId);

        // save reference to new pane in hash table
        idToEditorMap.put(selectedId, spe);
        editors.add(spe);

        if (enableMe && isEditorEnabled()) {
          spe.removeDisableMask(this);
        } else {
          spe.addDisableMask(this);
        }
        this.add(selectedId, spe);
      }
    }
    layout.show(this, selectedId);
    currentKeyValue = selectedId;
  }

  /**
   * Creates a SwingPropertyEditor for the given subproperty.
   */
  public SwingPropertyEditor createEditorPane(String selectedId) {

    String editValue = createSubTemplate("." + selectedId);

    SwingPropertyEditor returnValue = (SwingPropertyEditor)manager.getFactory().createEditor(property, editValue, propertyBase, manager);
    return returnValue;
  }


  /**
   * Returns the helpId for this editor.
   */
  public String getHelpID() {
    String subProperty = manager.getProperty(editorTemplate + ".helpController", "");
    if (subProperty.length() == 0) {
      if (currentKeyValue != null) {
        PropertyEditorUI selectedEditor = idToEditorMap.get(currentKeyValue);
        if (selectedEditor == null) {
          return super.getHelpID();
        } else {
          return selectedEditor.getHelpID();
        }
      } else {
        return super.getHelpID();
      }
    } else {
      return super.getHelpID();
    }
  }

  /**
   * This writes the currently configured values in the PropertyEditorUI
   * to the source VariableBundle.
   */
  public void setValue() throws PropertyValueVetoException {
    validateProperty();
    if (currentKeyValue != null) {
      PropertyEditorUI selectedEditor = idToEditorMap.get(currentKeyValue);
      selectedEditor.setValue();
    }
  }

  /**
   * Validates the currently selected editor.
   */
  public void validateProperty() throws PropertyValueVetoException {
    if (currentKeyValue != null) {
      PropertyEditorUI selectedEditor = idToEditorMap.get(currentKeyValue);
      selectedEditor.validateProperty();
    }
  }

  /**
   * Returns the current values of the edited properties as a
   * java.util.Properties object.
   */
  public java.util.Properties getValue() {
    java.util.Properties currentRetValue = new java.util.Properties();
    if (currentKeyValue != null) {
      PropertyEditorUI selectedEditor = idToEditorMap.get(currentKeyValue);
      currentRetValue.putAll(selectedEditor.getValue());
    }
    return currentRetValue;
  }

}



