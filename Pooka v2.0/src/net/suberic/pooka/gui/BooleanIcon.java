package net.suberic.pooka.gui;
import net.suberic.pooka.Pooka;
import net.suberic.util.gui.IconManager;
import javax.swing.*;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.awt.Component;

public class BooleanIcon implements TableCellIcon {
  public boolean bool;
  public String iconProperty;
  public static HashMap labelTable = new HashMap();
  protected static Component blankImage = new JLabel();
  String mIconId = "";

  public BooleanIcon(boolean boolValue, String newIconFile, String pId) {
    bool=boolValue;
    iconProperty = newIconFile;
    ((JLabel)blankImage).setOpaque(true);
    mIconId = pId;
  }

    /**
     * This returns a JLabel.  If the value of this BooleanIcon is true,
     * then it returns the configued image.  If it's false, then it just
     * returns a blank JLabel.
     */
    public Component getIcon() {
	if (bool) {
	    return getIcon(iconProperty);
	} else
	    return blankImage;
    }

    public Component getIcon(String imageKey) {
	
	if (labelTable.containsKey(imageKey))
	    return (Component)labelTable.get(imageKey);
	else
	    return loadImage(imageKey);
    }

  /**
   * This attempts to load an image for the given imageKey.
   */
  public Component loadImage(String imageKey) {
    Component returnValue = null;
    IconManager iconManager = Pooka.getUIFactory().getIconManager();
    ImageIcon icon = iconManager.getIcon(imageKey);
    if (icon != null) {
      returnValue = new JLabel(icon);
      ((JLabel)returnValue).setOpaque(true);
      labelTable.put(imageKey, returnValue);
    } else {
      returnValue = null;
    }
    
    return returnValue;
  }
  
  public int compareTo(Object o) {
    if (o instanceof BooleanIcon) {
      boolean oValue = ((BooleanIcon)o).bool;
      if (bool == oValue)
	return 0;
      else if (bool == true)
	return 1;
      else
	return -1;
    }
    throw new ClassCastException("object is not a BooleanIcon.");
  }

  public String toString() {
    return "";
  }
  
  /**
   * Returns the icon id.
   */
  public String getIconId() {
    return mIconId;
  }

  /**
   * Returns the icon value.
   */
  public boolean iconValue() {
    return bool;
  }

}
