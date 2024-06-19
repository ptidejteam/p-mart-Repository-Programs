package net.suberic.pooka.gui.filter;
import java.util.Properties;

/**
 * This is a class that lets you choose your filter actions.
 */
public class DeleteFilterEditor extends FilterEditor {
  String originalFolderName;
  
  public static String FILTER_CLASS = "net.suberic.pooka.filter.DeleteFilterAction";
  
  /**
   * Configures the given FilterEditor from the given VariableBundle and
   * property.
   */
  public void configureEditor(net.suberic.util.gui.propedit.PropertyEditorManager newManager, String propertyName) {
    manager = newManager;
    property = propertyName;
    // there really isn't anything to do here, is there?

  }
  
  /**
   * Gets the values that would be set by this FilterEditor.
   */
  public java.util.Properties getValue() {
    Properties props = new Properties();
    
    String oldClassName = manager.getProperty(property + ".class", "");
    if (!oldClassName.equals(FILTER_CLASS))
      props.setProperty(property + ".class", FILTER_CLASS);
    
    return props;
  }
  
  /**
   * Sets the values represented by this FilterEditor in the manager.
   */
  public void setValue() {
    String oldClassName = manager.getProperty(property + ".class", "");
    if (!oldClassName.equals(FILTER_CLASS))
      manager.setProperty(property + ".class", FILTER_CLASS);
  }
  
  /**
   * Returns the class that will be set for this FilterEditor.
   */
  public String getFilterClassValue() {
    return FILTER_CLASS;
  }
}
