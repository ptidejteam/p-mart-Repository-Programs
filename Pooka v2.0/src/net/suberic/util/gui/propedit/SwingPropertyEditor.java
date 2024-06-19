package net.suberic.util.gui.propedit;
import net.suberic.util.*;
import javax.swing.*;
import java.awt.Dimension;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.Set;

/**
 * A Swing implementation of the PropertyEditorUI.
 */
public abstract class SwingPropertyEditor extends JPanel implements PropertyEditorUI {
  // debug flag
  protected boolean debug = false;

  // a set of disable flags
  protected Set disableMaskSet = new HashSet();

  // the property being edited.
  protected String property;

  // the template to use
  protected String editorTemplate;

  // the property base to use
  protected String propertyBase;

  // the original value of the property.
  protected String originalValue;

  // the PorpertyEditorManager for this instance.
  protected PropertyEditorManager manager;

  // the logger
  protected static Logger sLogger =  Logger.getLogger("editors.debug");

  /**
   * Creates a new SwingPropertyEditor, in this case a JPanel with a
   * SpringLayout.  Note that configureEditor() will need to get called
   * on this component in order to make it useful.
   */
  public SwingPropertyEditor() {
    super();
    this.setLayout(new java.awt.GridBagLayout());
  }

  /**
   * Creates a SwingPropertyEditor using the given property and manager.
   *
   * @param propertyName The property to be edited.  This property will
   *        also be used to define the layout of this Editor.
   * @param template The template to be used for this property
   * @param baseProperty The base property to be used for other scoped
   *                     properties.
   * @param newManager The PropertyEditorManager that will manage the
   *                   changes.
   */
  public SwingPropertyEditor(String propertyName, String template, String baseProperty, PropertyEditorManager newManager) {
    configureEditor(propertyName, template, baseProperty,  newManager);
  }

  /**
   * Creates a SwingPropertyEditor using the given property and manager.
   *
   * @param propertyName The property to be edited.  This property will
   *        also be used to define the layout of this Editor.
   * @param newManager The PropertyEditorManager that will manage the
   *                   changes.
   */
  public SwingPropertyEditor(String propertyName, PropertyEditorManager newManager) {
    configureEditor(propertyName, propertyName, newManager);
  }

  /**
   * Creates a SwingPropertyEditor using the given property and manager.
   *
   * @param propertyName The property to be edited.  This property will
   *        also be used to define the layout of this Editor.
   * @param newManager The PropertyEditorManager that will manage the
   *                   changes.
   */
  public void configureEditor(String propertyName, PropertyEditorManager newManager) {
    configureEditor(propertyName, propertyName, newManager);
  }

  /**
   * Creates a SwingPropertyEditor using the given property and manager.
   *
   * @param propertyName The property to be edited.  This property will
   *        also be used to define the layout of this Editor.
   * @param propertyName The template to use for this Property.
   * @param newManager The PropertyEditorManager that will manage the
   *                   changes.
   */
  public void configureEditor(String propertyName, String template, PropertyEditorManager manager) {
    configureEditor(propertyName, template, propertyName, manager);
  }

  /**
   * Loads the basic properties for all SwingPropertyEditors.
   */
  public void configureBasic(String propertyName, String template, String propertyBaseName, PropertyEditorManager newManager) {
    manager=newManager;
    propertyBase=propertyBaseName;
    editorTemplate = template;
    /*
    if (propertyBaseName == null || propertyBaseName.length() == 0 || propertyBaseName.equals(propertyName)) {
      property = propertyName;
    } else {
      property = propertyBaseName + "." + propertyName;
    }
    */
    property = propertyName;
    addDefaultListeners();
    originalValue = manager.getProperty(property, manager.getProperty(editorTemplate, ""));
    manager.registerPropertyEditor(property, this);
    firePropertyInitializedEvent(originalValue);

  }

  /**
   * Adds a disable mask to the PropertyEditorUI.
   */
  public void addDisableMask(Object key) {
    disableMaskSet.add(key);
    updateEditorEnabled();
  }

  /**
   * Removes the disable mask keyed by this Object.
   */
  public void removeDisableMask(Object key) {
    disableMaskSet.remove(key);
    updateEditorEnabled();
  }

  /**
   * Returns whether or not this Editor is currently enabled.
   */
  public boolean isEditorEnabled() {
    return disableMaskSet.isEmpty();
  }

  /**
   * Run when the PropertyEditor may have changed enabled states.
   */
  protected abstract void updateEditorEnabled();

  /**
   * Gets the PropertyEditorManager
   */
  public PropertyEditorManager getManager() {
    return manager;
  }

  /**
   * Adds a PropertyEditorListener to the ListenerList.
   */
  public void addPropertyEditorListener(PropertyEditorListener pel) {
    manager.addPropertyEditorListener(getProperty(), pel);
  }

  /**
   * Removes a PropertyEditorListener from the ListenerList.
   */
  public void removePropertyEditorListener(PropertyEditorListener pel) {
    manager.removePropertyEditorListener(getProperty(), pel);
  }


  /**
   * Fires a propertyChanging event to all of the PropertyEditorListeners.
   * If any of the listeners veto the new value, then this returns false.
   * Otherwise, returns true.
   */
  public void firePropertyChangingEvent(String newValue) throws PropertyValueVetoException {
    manager.firePropertyChangingEvent(this, newValue);
  }

  /**
   * Fires a propertyChanged event to all of the PropertyEditorListeners.
   */
  public void firePropertyChangedEvent(String newValue) {
    manager.firePropertyChangedEvent(this, newValue);
  }

  /**
   * Fires a propertyCommitting event to all of the PropertyEditorListeners.
   */
  public void firePropertyCommittingEvent(String newValue) throws PropertyValueVetoException {
    manager.firePropertyCommittingEvent(this, newValue);
  }

  /**
   * Fires a propertyInitialized event to all of the PropertyEditorListeners.
   */
  public void firePropertyInitializedEvent(String newValue) {
    manager.firePropertyInitializedEvent(this, newValue);
  }

  /**
   * Gets the parent PropertyEditorPane for the given component.
   */
  public abstract PropertyEditorPane getPropertyEditorPane();

  /**
   * Gets the parent PropertyEditorPane for the given component.
   */
  protected PropertyEditorPane getPropertyEditorPane(java.awt.Component component) {
    try {
      Class pepClass = Class.forName("net.suberic.util.gui.propedit.PropertyEditorPane");
      if (pepClass != null) {
        PropertyEditorPane pep = (PropertyEditorPane) SwingUtilities.getAncestorOfClass(pepClass, component);
        return pep;
      }
    } catch (Exception e) {
    }

    return null;
  }

  /**
   * Adds the appropriate listeners.
   */
  public void addDefaultListeners() {
    List propertyListenerList = manager.getPropertyAsList(editorTemplate + "._listeners", "");
    java.util.Iterator it = propertyListenerList.iterator();
    while (it.hasNext()) {
      String current = (String)it.next();
      PropertyEditorListener pel = manager.createListener(current, property, propertyBase, editorTemplate);
      if (pel != null) {
        addPropertyEditorListener(pel);
      }
    }
  }

  /**
   * Returns the currently edited property.
   */
  public String getProperty() {
    return property;
  }

  /**
   * Sets the original value.
   */
  public void setOriginalValue(String pOriginalValue) {
    originalValue = pOriginalValue;
  }

  /**
   * Returns the template for the current property.
   */
  public String getEditorTemplate() {
    return editorTemplate;
  }

  /**
   * Returns the helpId for this editor.
   */
  public String getHelpID() {
    return getEditorTemplate();
  }

  /**
   * Gets the Logger for this Editor.
   *
   */
  public Logger getLogger() {
    return sLogger;
  }

  /**
   * Removes the PropertyEditor.
   */
  public void remove() {
    manager.removePropertyEditorListeners(getProperty());
  }

  /**
   * Accepts or rejects the initial focus for this component.
   */
  public boolean acceptDefaultFocus() {
    return false;
  }
}
