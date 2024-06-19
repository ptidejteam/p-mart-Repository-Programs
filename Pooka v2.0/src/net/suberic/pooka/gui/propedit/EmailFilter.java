package net.suberic.pooka.gui.propedit;
import net.suberic.util.gui.propedit.*;
import java.util.*;
import javax.mail.internet.InternetAddress;

/**
 * A PropertyEditorListener which sets a property as required.
 */
public class EmailFilter extends PropertyEditorAdapter implements ConfigurablePropertyEditorListener {

  /**
   * Configures this filter from the given key.
   */
  public void configureListener(String key, String property, String pPropertyBase, String editorTemplate, PropertyEditorManager pManager) {

  }

  /**
   * Checks to make sure that the value is a valid email address.
   */
  public void propertyCommitting(PropertyEditorUI source, String property, String newValue) throws PropertyValueVetoException {
    validateAddress(property, newValue);
  }

  /**
   * Checks to make sure that the value is a valid email address.
   */
  public void propertyChanging(PropertyEditorUI source, String property, String newValue) throws PropertyValueVetoException {
    validateAddress(property, newValue);
  }

  /**
   * Checks to make sure that the value is a valid email address.
   */
  public void validateAddress(String property, String newValue) throws PropertyValueVetoException {
    try {
      if (newValue == null || newValue.length() == 0) {
        return;
      }
      InternetAddress.parse(newValue);
    } catch (Exception e) {
        throw new PropertyValueVetoException(property, newValue, "must be a valid email address", this);
    }
  }
}
