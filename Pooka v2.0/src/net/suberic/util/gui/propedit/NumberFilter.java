package net.suberic.util.gui.propedit;

/**
 * A PropertyEditorListener which disallows non-integers, and can also be
 * used for min/max settings.
 */
public class NumberFilter extends PropertyEditorAdapter implements ConfigurablePropertyEditorListener {
  String originalValue;

  boolean checkMin = false;
  int min;
  boolean checkMax = false;
  int max;

  /**
   * Configures this filter from the given key.
   */
  public void configureListener(String key, String property, String propertyBase, String editorTemplate, PropertyEditorManager manager) {
    originalValue = manager.getProperty(property, "");

    String minProp = manager.getProperty(key + ".min", "");
    if (minProp != "") {
      try {
        min = Integer.parseInt(minProp);
        checkMin = true;
      } catch (NumberFormatException nfe) {
      }
    }

    String maxProp = manager.getProperty(key + ".max", "");
    if (maxProp != "") {
      try {
        max = Integer.parseInt(maxProp);
        checkMax = true;
      } catch (NumberFormatException nfe) {
      }
    }
  }

  /**
   * Called when a property is about to change.  If the value is not ok
   * with the listener, a PropertyValueVetoException should be thrown.
   *
   * In this case, if the entry is either not a number, or is not within
   * the min/max range, an error is thrown.
   */
  public void propertyChanging(PropertyEditorUI source, String property, String newValue) throws PropertyValueVetoException {
    try {
      if (newValue != null && newValue.length() > 0 && ! newValue.equals(originalValue)) {
        int currentValue = Integer.parseInt(newValue);
        if (checkMin) {
          if (currentValue < min) {
            throw new PropertyValueVetoException(property, newValue, "value must be at least " + min, this);
          }
        }

        if (checkMax) {
          if (currentValue > max) {
            throw new PropertyValueVetoException(property, newValue, "value must be at most " + max, this);
          }
        }
      }
    } catch (NumberFormatException nfe) {
      throw new PropertyValueVetoException(property, newValue, "entry must be a number.", this);
    }
  }

}
