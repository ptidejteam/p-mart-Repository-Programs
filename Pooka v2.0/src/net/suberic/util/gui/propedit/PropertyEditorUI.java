package net.suberic.util.gui.propedit;
import net.suberic.util.VariableBundle;

/**
 * An interface which defines a way of editing a property.
 */
public interface PropertyEditorUI {

  /**
   * This configures an editor for the given propertyName using the
   * PropertyManager mgr.
   *
   * This version usees the template property to definte all things about
   * the editor for propertyName.  This is useful if you want to be able
   * to edit, for instace, the properties of a particular user:
   *
   * UserProfile.userOne.showHeaders
   * UserProfile.userTwo.showHeaders
   *
   * UserProfile.showHeaders.propertyType=boolean
   *
   * So you can use this just to call configureEditor(factory,
   * "UserProfile.userOne.showHeaders", "UserProfile.showHeaders", mgr,
   * true)
   *
   * @param propertyName The property to be edited.
   * @param template The property that will define the layout of the
   *                 editor.
   * @param propertyBase The property that is used for any relative
   *                 property resolutions.
   * @param manager The PropertyEditorManager that will manage the
   *                   changes.
   */
  public void configureEditor(String propertyName, String template, String propertyBase, PropertyEditorManager manager);

  /**
   * This configures an editor for the given propertyName using the
   * PropertyManager mgr.
   *
   * This version usees the template property to definte all things about
   * the editor for propertyName.  This is useful if you want to be able
   * to edit, for instace, the properties of a particular user:
   *
   * UserProfile.userOne.showHeaders
   * UserProfile.userTwo.showHeaders
   *
   * UserProfile.showHeaders.propertyType=boolean
   *
   * So you can use this just to call configureEditor(factory,
   * "UserProfile.userOne.showHeaders", "UserProfile.showHeaders", mgr,
   * true)
   *
   * @param propertyName The property to be edited.
   * @param template The property that will define the layout of the
   *                 editor.
   * @param manager The PropertyEditorManager that will manage the
   *                   changes.
   */
  public void configureEditor(String propertyName, String template, PropertyEditorManager manager);

  /**
   * This configures an editor for the given propertyName in the
   * PropertyEditorManager mgr.
   * @param propertyName The property to be edited.  This will also be
   *                     used for the editor layout.
   * @param manager The PropertyEditorManager that will manage the
   *                   changes.
   */
  public void configureEditor(String propertyName, PropertyEditorManager mgr);

  /**
   * Returns the currently edited property.
   */
  public String getProperty();

  /**
   * Returns the template for the current property.
   */
  public String getEditorTemplate();

  /**
   * This writes the currently configured value in the PropertyEditorUI
   * to the source VariableBundle.
   */
  public void setValue() throws PropertyValueVetoException;

  /**
   * This checks that the currently configured value is valid.
   */
  public void validateProperty() throws PropertyValueVetoException;

  /**
   * This resets the editor to the original (or latest set, if setValue()
   * has been called) value of the edited property.
   */
  public void resetDefaultValue() throws PropertyValueVetoException;

  /**
   * Sets the original value of the editor.
   */
  public void setOriginalValue(String pOriginalValue);

  /**
   * Returns the current values of the edited properties as a
   * java.util.Properties object.
   */
  public java.util.Properties getValue();

  /**
   * Adds a disable mask to the PropertyEditorUI.
   */
  public void addDisableMask(Object key);

  /**
   * Removes the disable mask keyed by this Object.
   */
  public void removeDisableMask(Object key);

  /**
   * Returns whether or not this Editor is currently enabled.
   */
  public boolean isEditorEnabled();

  /**
   * Returns the PropertyEditorManager for this PropertyEditorUI.
   */
  public PropertyEditorManager getManager();

  /**
   * Adds a PropertyEditorListener to the ListenerList.
   */
  public void addPropertyEditorListener(PropertyEditorListener pel);

  /**
   * Removes a PropertyEditorListener from the ListenerList.
   */
  public void removePropertyEditorListener(PropertyEditorListener pel);

  /**
   * Returns the helpId for this editor.
   */
  public String getHelpID();

  /**
   * Returns the display value for this property.
   */
  public String getDisplayValue();

  /**
   * Removes the PropertyEditor.
   */
  public void remove();
}
