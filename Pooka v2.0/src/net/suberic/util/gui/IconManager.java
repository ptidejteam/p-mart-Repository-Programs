package net.suberic.util.gui;
import java.awt.Component;
import javax.swing.*;
import java.util.*;

import net.suberic.util.VariableBundle;

/**
 * This manages a set of icons.
 *
 */
public class IconManager {

  // the source VariableBundle
  VariableBundle mResources = null;

  // the source property.
  String mProperty = null;

  // the default location for icons
  String mIconDirectory = null;

  // the default extension for icons
  String mIconExtension = null;;

  // the icon manager map
  static HashMap sManagers = new HashMap();

  // the image map
  static HashMap sImageMap = new HashMap();

  /**
   * Creates a new IconManager from the given property.
   *
   * @param pResources the VariableBundle used to access the icons
   * @param pResourceBase a property in the given VariableBundle that will resolve to provide the correct property base
   */
  protected IconManager(VariableBundle pResources, String pResourceBase) {
    mResources = pResources;
    mProperty = pResources.getProperty(pResourceBase, "");
    mIconDirectory = mResources.getProperty(mProperty + ".defaultDirectory", "images");
    mIconExtension = mResources.getProperty(mProperty + ".defaultExtension", "images");
  }

  /**
   * Gets the IconManager for the given resources and resource base.  Will
   * return a cached copy if available.
   */
  public static IconManager getIconManager(VariableBundle pResources, String pResourceBase) {
    IconManager returnValue = (IconManager) sManagers.get(pResourceBase + pResources.getProperty(pResourceBase, "") + System.identityHashCode(pResources));
    if (returnValue == null) {
      synchronized(sManagers)  {
        returnValue = (IconManager) sManagers.get(pResourceBase + pResources.getProperty(pResourceBase, "") + System.identityHashCode(pResources));
        if (returnValue == null) {
          returnValue = new IconManager(pResources, pResourceBase);
          sManagers.put(pResourceBase + pResources.getProperty(pResourceBase, "") + System.identityHashCode(pResources), returnValue);
        }
      }
    }

    return returnValue;
  }

  /**
   * Gets the ImageIcon specified by the resource given.
   */
  public ImageIcon getIcon(String pIconString) {
    java.net.URL imageURL = this.getClass().getResource(mResources.getProperty(mProperty + ".icon." + pIconString, mIconDirectory + "/" + pIconString + "." + mIconExtension));
    if (imageURL != null) {
      ImageIcon returnValue = (ImageIcon) sImageMap.get(imageURL);
      if (returnValue == null) {
        synchronized(sImageMap) {
          returnValue = (ImageIcon) sImageMap.get(imageURL);
          if (returnValue == null) {
            returnValue = new ImageIcon(imageURL);
            sImageMap.put(imageURL, returnValue);
          }
        }
      }
      return returnValue;
    } else {
      System.err.println("got null for iconString " + pIconString + ", file " + mResources.getProperty(mProperty + ".icon." + pIconString, mIconDirectory + "/" + pIconString + "." + mIconExtension));
      return null;
    }
  }


}

