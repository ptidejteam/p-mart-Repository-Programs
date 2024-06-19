package net.suberic.util.gui.propedit;
import java.util.*;

/**
 * A PropertyEditorListener which sets a property as required.
 */
public class RequiredFilter extends PropertyEditorAdapter implements ConfigurablePropertyEditorListener {

  PropertyEditorManager manager;
  String propertyBase;

  Map<String,String> requiredIfMap = new HashMap<String,String>();
  boolean always = false;

  /**
   * Configures this filter from the given key.
   */
  public void configureListener(String key, String property, String pPropertyBase, String editorTemplate, PropertyEditorManager pManager) {
    //System.err.println("configuring requiredfilter for property " + property);
    manager = pManager;
    propertyBase = pPropertyBase;
    List<String> requiredKeys = manager.getPropertyAsList(key + ".map", "");
    if (requiredKeys.size() < 1) {
      always = true;
      //System.err.println("always.");
    } else {
      for (String requiredKey: requiredKeys) {
        String[] pair = requiredKey.split("=");
        if (pair != null && pair.length == 2) {
          //System.err.println("adding requirement for " + pair[0] + " = '" + pair[1] + "'");
          requiredIfMap.put(pair[0], pair[1]);
        }
      }
    }
  }

  /**
   * Checks to make sure that this value is set.
   */
  public void propertyCommitting(PropertyEditorUI source, String property, String newValue) throws PropertyValueVetoException{
    if (newValue == null || newValue.trim().length() == 0) {
      if (always) {
        throw new PropertyValueVetoException(source.getDisplayValue(), newValue, "property is required", this);
      }
      Iterator<String> keys = requiredIfMap.keySet().iterator();
      while ( keys.hasNext()) {
        String affectedProperty = keys.next();
        String fullProperty = affectedProperty;
        if (affectedProperty != null && affectedProperty.startsWith(".")) {
          fullProperty = propertyBase + affectedProperty;
        }
        PropertyEditorUI ui = manager.getPropertyEditor(fullProperty);
        if (ui != null) {
          Properties uiValue = ui.getValue();
          String propertyValue = uiValue.getProperty(fullProperty);
          if (requiredIfMap.get(affectedProperty).equals(propertyValue)) {
            //System.err.println("throwing exception.");
            throw new PropertyValueVetoException(source.getDisplayValue(), newValue, "property is required", this);
          }
        }
      }
    }

  }
}
