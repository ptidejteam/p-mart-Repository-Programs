package net.suberic.util.swing;

import net.suberic.util.*;
import java.util.*;
import javax.swing.*;
import javax.swing.plaf.metal.*;
import javax.swing.plaf.*;


/**
 * 
 */
public class ThemeWrapperItem implements Item, ValueChangeListener {

  private String itemId;
  private String resourceString;  

  private VariableBundle bundle = null;

  private WeakHashMap themeListenerList = new WeakHashMap();
  
  private MetalTheme mWrappedTheme = null;

  /**
   * Creates a new ItemOcreanTheme from the given property.
   */
  public ThemeWrapperItem(VariableBundle sourceBundle, String newResourceString, String newItemId) {
    itemId = newItemId;
    resourceString = newResourceString;
    
    bundle = sourceBundle;

    sourceBundle.addValueChangeListener(this, getItemProperty() + ".*");
  }

  /**
   * Sets the wrapped theme.
   */
  public void setWrappedTheme(MetalTheme pWrappedTheme) {
    mWrappedTheme = pWrappedTheme;
  }

  /**
   * Gets the wrapped theme.
   */
  public MetalTheme getWrappedTheme() {
    return mWrappedTheme;
  }

  /**
   * The Item ID.  For example, if you were to have a list of users, a
   * given user's itemID may be "defaultUser".
   */
  public String getItemID() {
    return itemId;
  }
  
  /**
   * The Item property.  For example, if you were to have a list of users, a
   * given user's itemProperty may be "Users.defaultUser".
   */
  public String getItemProperty() {
    return  resourceString + "." + itemId;
  }

  /**
   * Called when a ui value changes.
   */
  public void valueChanged(String changedValue) {
    fireThemeChangedEvent();
  }

  /**
   * Adds a ThemeListener to the ListenerList.
   */
  public void addThemeListener(ThemeListener tl) {
    if (! themeListenerList.containsKey(tl))
      themeListenerList.put(tl, null);
  }
  
  /**
   * Removes a ThemeListener from the ListenerList.
   */
  public void removeThemeListener(ThemeListener tl) {
    themeListenerList.remove(tl);
  }

  /**
   * Notifies all registered ThemeListeners that this Theme has changed.
   */
  public void fireThemeChangedEvent() {
    // no-op.  this theme doesn't change.
  }

  public String getName() { return getItemID(); }
  
}
