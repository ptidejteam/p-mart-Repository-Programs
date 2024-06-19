package net.suberic.util.gui.propedit;
import java.util.*;

/**
 * A PropertyEditorListener which enables or disables this editor if
 * certain values are set.
 */
public class DisableFilter extends PropertyEditorAdapter implements ConfigurablePropertyEditorListener {
  Map<String,Set<String>> disableValues = new HashMap<String, Set<String>>();
  Map<String,Set<String>> enableValues = new HashMap<String, Set<String>>();
  PropertyEditorManager manager;
  String propertyBase;
  String property;

  /**
   * Configures this filter from the given key.
   */
  public void configureListener(String key, String pProperty, String pPropertyBase, String editorTemplate, PropertyEditorManager pManager) {
    //System.err.println("");
    //System.err.println("init for " + key);
    manager = pManager;
    propertyBase = pPropertyBase;
    property = pProperty;
    List<String> disableKeys = manager.getPropertyAsList(key + ".disableValues", "");
    for (String keyString: disableKeys) {
      String[] pair = keyString.split("=");
      //System.err.println("split '" + keyString + "'; pair.length = " + pair.length);
      if (pair != null && pair.length == 1) {
        String[] newPair = new String[2];
        newPair[0] = pair[0];
        newPair[1] = "";
        pair = newPair;
      }

      if (pair != null && pair.length == 2) {
        if (pair[0].startsWith(".")) {
          pair[0] = propertyBase + pair[0];
        }
        Set<String> valueSet = disableValues.get(pair[0]);
        if (valueSet == null) {
          manager.addPropertyEditorListener(pair[0], this);
          valueSet = new HashSet<String>();
          disableValues.put(pair[0], valueSet);
        }
        valueSet.add(pair[1]);
      }
    }

    List<String> enableKeys = manager.getPropertyAsList(key + ".enableValues", "");
    for (String keyString: enableKeys) {
      String[] pair = keyString.split("=");
      //System.err.println("split '" + keyString + "'; pair.length = " + pair.length);
      if (pair != null && pair.length == 1) {
        String[] newPair = new String[2];
        newPair[0] = pair[0];
        newPair[1] = "";
        pair = newPair;
      }
      if (pair != null && pair.length == 2) {
        if (pair[0].startsWith(".")) {
          pair[0] = propertyBase + pair[0];
        }
        Set<String> valueSet = enableValues.get(pair[0]);
        if (valueSet == null) {
          valueSet = new HashSet<String>();
          enableValues.put(pair[0], valueSet);
          manager.addPropertyEditorListener(pair[0], this);
        }
        valueSet.add(pair[1]);
      }
    }
  }

  /**
   * On initialization, if any of the source properties are set to
   * values to be disabled, disable the editor.
   */
  public void propertyInitialized(PropertyEditorUI source, String pProperty, String newValue) {
    //System.err.println("property " + pProperty + " initializing.");
    if (property.equals(pProperty)) {
      checkEnabledStatus(source);
    }
  }

  /**
   * On a property change, if any of the source properties are set to
   * values to be disabled, disable the editor.
   */
  public void propertyChanged(PropertyEditorUI source, String pProperty, String newValue) {
    //System.err.println("property " + pProperty + " changed.");
    if (enableValues.keySet().contains(pProperty) || disableValues.keySet().contains(pProperty)) {
      checkEnabledStatus(null);
    }
  }

  /**
   * Checks the enabled status of the affected editor.
   */
  public void checkEnabledStatus(PropertyEditorUI source) {
    //System.err.println("checking enabled status for property " + property );
    boolean enable = true;
    for (String key: enableValues.keySet()) {
      boolean enableFound = false;
      String fullProperty = key;
      String propValue = manager.getCurrentProperty(fullProperty, "");
      //System.err.println("fullProperty to check is " + fullProperty + ", value = '" + propValue + "'");
      Set<String> valueSet = enableValues.get(key);
      for (String value: valueSet) {
        //System.err.println("checking value " + value);
        if (propValue.equals(value)) {
          //System.err.println("found enable value for " + key);
          enableFound=true;
        }
      }
      if (! enableFound)
        enable = false;
    }

    for (String key: disableValues.keySet()) {
      String fullProperty = key;
      String propValue = manager.getCurrentProperty(fullProperty, "");
      //System.err.println("fullProperty to check is " + fullProperty + ", value = '" + propValue + "'");
      Set<String> valueSet = disableValues.get(key);
      for (String value: valueSet) {
        //System.err.println("checking value " + value);
        if (propValue.equals(value)) {
          //System.err.println("match found; setting enabled to false.");
          enable = false;
        }
      }
    }
    if (source != null) {
      //System.err.println("setting source editor to " + enable);
      if (enable) {
        source.removeDisableMask(this);
      } else {
        source.addDisableMask(this);
      }
    } else {
      //System.err.println("getPropertyEditor(" + property + ")=" + manager.getPropertyEditor(property));
      if (manager.getPropertyEditor(property) != null)
        if (enable) {
          manager.getPropertyEditor(property).removeDisableMask(this);
        } else {
          manager.getPropertyEditor(property).addDisableMask(this);
        }
    }
  }
}
