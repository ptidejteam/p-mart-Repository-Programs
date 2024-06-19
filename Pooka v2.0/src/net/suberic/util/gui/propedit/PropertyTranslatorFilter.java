package net.suberic.util.gui.propedit;
import java.util.*;

/**
 * A PropertyEditorListener which translates one value to another.  Useful
 * for version migrations
 */
public class PropertyTranslatorFilter extends PropertyEditorAdapter implements ConfigurablePropertyEditorListener {

  Map<String,String> translator = new HashMap<String,String>();
  String sourceProperty = null;
  PropertyEditorManager manager = null;

  /**
   * Configures this filter from the given key.
   */
  public void configureListener(String key, String property, String propertyBase, String editorTemplate, PropertyEditorManager pManager) {
    manager = pManager;
    //System.err.println("configuring translator filter for " + property);
    List<String> translatorKeys = manager.getPropertyAsList(key + ".map", "");
    for (String translatorKey: translatorKeys) {
      String[] pair = translatorKey.split("=");
      if (pair != null && pair.length == 2) {
        //System.err.println("adding map " + pair[0] + ", " + pair[1]);
        translator.put(pair[0], pair[1]);
      }
    }

    sourceProperty = manager.getProperty(key + ".sourceProperty", "");
    if (sourceProperty.startsWith(".")) {
      sourceProperty = propertyBase + sourceProperty;
    }
  }

  /**
   * On initialization, if the property is set to one of the values to
   * be translated, we set the new value to the translated value.
   */
  public void propertyInitialized(PropertyEditorUI source, String property, String newValue) {
    //System.err.println("property " + property + " initialized; newValue = " + newValue);
    Properties currentProperties = source.getValue();
    String currentValue = currentProperties.getProperty(property);
    //System.err.println("currentValue = '" + currentValue + "'");
    if (newValue.equals(currentValue) || currentValue == null || currentValue.equals("")) {
      if (sourceProperty != null && ! sourceProperty.equals("")) {
        //System.err.println("have sourceProperty(" + sourceProperty + ")");
        if (newValue == "") {
          //System.err.println("newValue is blank.");
          String testValue = manager.getProperty(sourceProperty, "");
          //System.err.println("testValue is " + testValue);
          String translatedValue = translator.get(testValue);
          if (translatedValue != null) {
            //System.err.println("setting value to " + translatedValue);
            source.setOriginalValue(translatedValue);
          }
        }
      } else {
        String translatedValue = translator.get(newValue);
        //System.err.println("checking translation; value for value " + newValue + " = " + translatedValue);
        if (translatedValue != null) {
          source.setOriginalValue(translatedValue);
          //System.err.println("setting value to " + translatedValue);
        }
      }
    } else {
      //System.err.println("value changed; not checking.");
    }
  }

}
