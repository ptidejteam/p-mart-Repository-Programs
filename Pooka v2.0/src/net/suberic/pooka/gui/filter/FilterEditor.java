package net.suberic.pooka.gui.filter;
import net.suberic.util.gui.propedit.PropertyValueVetoException;

/**
 * This is a class that lets you choose your filter actions.
 */
public abstract class FilterEditor extends javax.swing.JPanel {

  protected net.suberic.util.gui.propedit.PropertyEditorManager manager;

  protected String property;

  /**
   * Configures the given FilterEditor from the given VariableBundle and
   * property.
   */
  public abstract void configureEditor(net.suberic.util.gui.propedit.PropertyEditorManager manager, String propertyName);

  /**
   * Gets the values that would be set by this FilterEditor.
   */
  public abstract java.util.Properties getValue();

  /**
   * Sets the values represented by this FilterEditor in the sourceBundle.
   */
  public abstract void setValue() throws PropertyValueVetoException;

  /**
   * Returns the class that will be set for this FilterEditor.
   */
  public abstract String getFilterClassValue();
}
