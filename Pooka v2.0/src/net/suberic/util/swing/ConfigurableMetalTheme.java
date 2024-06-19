package net.suberic.util.swing;

import net.suberic.util.*;
import java.util.*;
import javax.swing.*;
import javax.swing.plaf.metal.*;
import javax.swing.plaf.*;


/**
 * A Theme which allows you to set all of your colors for a Metal
 * application.
 */
public class ConfigurableMetalTheme extends DefaultMetalTheme implements Item, ValueChangeListener {

  private String itemId;
  private String resourceString;  

  private VariableBundle bundle = null;

  private WeakHashMap themeListenerList = new WeakHashMap();

  protected ColorUIResource subPrimary1 = null;
  protected ColorUIResource subPrimary2 = null;
  protected ColorUIResource subPrimary3 = null;
    

  protected ColorUIResource subSecondary1 = null;
  protected ColorUIResource subSecondary2 = null;
  protected ColorUIResource subSecondary3 = null;

  protected ColorUIResource subBlack = null;
  protected ColorUIResource subWhite = null;
  
  protected FontUIResource subControlFont = null;
  protected FontUIResource subSystemFont = null;
  protected FontUIResource subUserFont = null;
  protected FontUIResource subSmallFont = null;
  protected FontUIResource subMonospacedFont = null;
  
  
  /**
   * Creates a new ConfigurableMetalTheme from the given property.
   */
  public ConfigurableMetalTheme(VariableBundle sourceBundle, String newResourceString, String newItemId) {
    itemId = newItemId;
    resourceString = newResourceString;
    
    bundle = sourceBundle;

    loadTheme(getItemProperty(), sourceBundle);

    sourceBundle.addValueChangeListener(this, getItemProperty() + ".*");
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
    loadTheme(getItemProperty(), bundle);
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
    Iterator iter = themeListenerList.keySet().iterator();
    while (iter.hasNext()) {
      ThemeListener current = (ThemeListener) iter.next();
      current.themeChanged(this);
    }
  }

  /**
   * This loads the theme from the given property and bundle.
   */
  protected void loadTheme(String property, VariableBundle sourceBundle) {
    subPrimary1 = createColorUIResource(property + ".primary1", sourceBundle);
    subPrimary2 = createColorUIResource(property + ".primary2", sourceBundle);
    subPrimary3 = createColorUIResource(property + ".primary3", sourceBundle);

    subSecondary1 = createColorUIResource(property + ".secondary1", sourceBundle);
    subSecondary2 = createColorUIResource(property + ".secondary2", sourceBundle);
    subSecondary3 = createColorUIResource(property + ".secondary3", sourceBundle);
    subWhite = createColorUIResource(property + ".white", sourceBundle);
    subBlack = createColorUIResource(property + ".black", sourceBundle);

    subControlFont = createFontUIResource(property + ".controlFont", sourceBundle);
    subSystemFont = createFontUIResource(property + ".systemFont", sourceBundle);
    subUserFont = createFontUIResource(property + ".userFont", sourceBundle);
    subSmallFont = createFontUIResource(property + ".smallFont", sourceBundle);
    subMonospacedFont = createFontUIResource(property + ".monospacedFont", sourceBundle);
    
  }
  
  protected ColorUIResource createColorUIResource(String property, VariableBundle sourceBundle) {
    String enabled = sourceBundle.getProperty(property + "._enabled", "false");
    if (enabled.equalsIgnoreCase("true")) {
      String rgbString = sourceBundle.getProperty(property + ".rgb", "");
      if (rgbString != null && ! rgbString.equals("")) {
	try {
	  int rgbValue = Integer.parseInt(rgbString);
	  ColorUIResource returnValue = new ColorUIResource(rgbValue);
	  return returnValue;
	} catch (Exception e) {
	  return null;
	}
      }
    }

    return null;
  }

  protected FontUIResource createFontUIResource(String property, VariableBundle sourceBundle) {
    String enabled = sourceBundle.getProperty(property + "._enabled", "false");
    if (enabled.equalsIgnoreCase("true")) {
      String fontString = sourceBundle.getProperty(property, "");
      if (fontString != null && ! fontString.equals("")) {
	try {
	  FontUIResource returnValue = new FontUIResource(java.awt.Font.decode(fontString));
	  return returnValue;
	} catch (Exception e) {
	return null;
	}
      }
    }

    return null;
  }

  public String getName() { return getItemID(); }
  
  
  protected ColorUIResource getPrimary1() { 
    if (subPrimary1 != null)
      return subPrimary1;
    else
      return super.getPrimary1(); 
  } 
  protected ColorUIResource getPrimary2() {
    if (subPrimary2 != null)
      return subPrimary2;
    else
      return super.getPrimary2();
  } 
  protected ColorUIResource getPrimary3() {
    if (subPrimary3 != null)
      return subPrimary3;
    else
      return super.getPrimary3();
  } 
  
  protected ColorUIResource getSecondary1() {
    if (subSecondary1 != null)
      return subSecondary1;
    else
      return super.getSecondary1(); 
  }
    protected ColorUIResource getSecondary2() {
    if (subSecondary2 != null)
      return subSecondary2;
    else
      return super.getSecondary2(); 
    }
    protected ColorUIResource getSecondary3() {
    if (subSecondary3 != null)
      return subSecondary3;
    else
      return super.getSecondary3(); 
    }
    protected ColorUIResource getWhite() {
    if (subWhite != null)
      return subWhite;
    else
      return super.getWhite(); 
    }
    protected ColorUIResource getBlack() {
      if (subBlack != null) {
      
	return subBlack;
      } else {
	return super.getBlack(); 
      }
    }

    public FontUIResource getControlTextFont() { 
    if (subControlFont != null)
      return subControlFont;
    else
      return super.getControlTextFont(); 
    }
    public FontUIResource getSystemTextFont() {
    if (subSystemFont != null)
      return subSystemFont;
    else
      return super.getSystemTextFont(); 
    }
    public FontUIResource getUserTextFont() {
    if (subUserFont != null)
      return subUserFont;
    else
      return super.getUserTextFont(); 
    }
    public FontUIResource getMenuTextFont() {
    if (subControlFont != null)
      return subControlFont;
    else
      return super.getMenuTextFont(); 
    }
    public FontUIResource getWindowTitleFont() {
    if (subControlFont != null)
      return subControlFont;
    else
      return super.getWindowTitleFont(); 
    }
    public FontUIResource getSubTextFont() { 
      if (subSmallFont != null)
	return subSmallFont;
      else
	return super.getSubTextFont(); 
    }
    public FontUIResource getMonospacedFont() { 
      return subMonospacedFont;
    }
}
