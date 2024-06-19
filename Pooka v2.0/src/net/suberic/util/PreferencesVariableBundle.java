package net.suberic.util;
import java.util.*;
import java.util.prefs.*;


/**
 * An implementation of VariableBundle that uses a java.util.pref.Preferences
 * package backend.
 */

public class PreferencesVariableBundle extends VariableBundle {
  private Preferences mPreferences;

  public PreferencesVariableBundle(Class pClass, String resourceFile, VariableBundle newParentProperties) {
    configure(pClass, resourceFile, newParentProperties);
  }

  public PreferencesVariableBundle(Class pClass, VariableBundle newParentProperties) {
    this(pClass, null, newParentProperties);
  }

  /**
   * Configures the VariableBundle.
   */
  protected void configure(Class pClass, String resourceFile, VariableBundle newParentProperties) {

    writableProperties = new Properties();

    if (resourceFile != null)
      try {
        resources = ResourceBundle.getBundle(resourceFile, Locale.getDefault());
      } catch (MissingResourceException mre) {
        System.err.println("Error loading resource " + mre.getClassName() + mre.getKey() + ":  trying default locale.");
        try {
          resources = ResourceBundle.getBundle(resourceFile, Locale.US);
        } catch (MissingResourceException mreTwo){
          System.err.println("Unable to load default (US) resource bundle; exiting.");
          System.exit(1);
        }
      }
    else
      resources=null;

    parentProperties = newParentProperties;

    mPreferences = Preferences.userNodeForPackage(pClass);
    properties = new Properties();

    loadPreferencesIntoProperties(mPreferences, properties);

  }

  private void loadPreferencesIntoProperties(Preferences prefs, Properties props) {
    try {
      String[] keys = prefs.keys();
      for (int i = 0; i < keys.length; i++) {
        props.setProperty(keys[i], prefs.get(keys[i], ""));
      }
    } catch (BackingStoreException bse) {
      bse.printStackTrace();
    }
  }

  /**
   * Saves the current properties in the VariableBundle to a file.  Note
   * that this only saves the writableProperties of this particular
   * VariableBundle--underlying defaults are not written.
   */
  public void saveProperties() {
    synchronized(this) {
      try {
        // first go through each of the existing propreties and keep the
        // ones that we want to keep.
        String[] keys = mPreferences.keys();
        for (int i = 0; i < keys.length; i++) {
          String key = keys[i];

          if (!propertyIsRemoved(key)) {
            if (writableProperties.getProperty(key, "").equals("")) {
              // if it's not in writableProperties, leave it as it is.
            } else {
              // write it to the backing preferences and remove it from
              // writableProperties.
              mPreferences.put(key, writableProperties.getProperty(key, ""));

              properties.setProperty(key, writableProperties.getProperty(key, ""));
              writableProperties.remove(key);
            }
          } else {
            mPreferences.remove(key);
          }
        }
        // write out the rest of the writableProperties

        Set<String> propsLeft = writableProperties.stringPropertyNames();
        List<String> propsLeftList = new ArrayList<String>(propsLeft);
        Collections.sort(propsLeftList);
        for (String nextKey: propsLeftList) {
          mPreferences.put(nextKey, writableProperties.getProperty(nextKey, ""));

          properties.setProperty(nextKey, writableProperties.getProperty(nextKey, ""));
          writableProperties.remove(nextKey);
        }

        clearRemoveList();

        mPreferences.flush();
      } catch (BackingStoreException bse) {
        System.err.println(getProperty("VariableBundle.saveError", "Error saving properties: " + bse.getMessage()));
        bse.printStackTrace(System.err);
      }
    }
  }
}


