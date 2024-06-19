package net.suberic.util.gui.propedit;
import net.suberic.util.VariableBundle;

import java.util.List;

/**
 * A PropertyEditorListener which disallows entries which already exist
 * for a particular property.
 *
 */
public class UniqueFilter extends PropertyEditorAdapter implements ConfigurablePropertyEditorListener {
  String parentProperty;
  PropertyEditorManager manager;

  /**
   * Configures this filter from the given key.
   */
  public void configureListener(String key, String property, String propertyBase, String editorTemplate, PropertyEditorManager pManager) {

    String parentProp = pManager.getProperty(key + ".listProperty", "");
    if (parentProp.length() > 0) {
      if (parentProp.startsWith(".")) {
        parentProperty=propertyBase + parentProp;
      } else {
        parentProperty=parentProp;
      }
    }

    manager = pManager;

  }

  /**
   * Called when a property is about to change.  If the value is not ok
   * with the listener, a PropertyValueVetoException should be thrown.
   *
   * In this case, if the entry already exists in the parentProperty we
   * throw an Exception.
   */
  public void propertyChanging(PropertyEditorUI source, String property, String newValue) throws PropertyValueVetoException {
    String parentValue = manager.getCurrentProperty(parentProperty, "");
    List<String> parentValueList = VariableBundle.convertToList(parentValue);
    if (parentValueList.contains(newValue)) {
      throw new PropertyValueVetoException(property, newValue, manager.formatMessage("Message.uniquFilter.notUnique", newValue, parentProperty), this);
    }
  }

}
