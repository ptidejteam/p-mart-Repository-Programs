package net.suberic.util;
import java.util.*;
import java.io.*;

/**
 * VariableBundle is a combination of a Properties object, a ResourceBundle
 * object, and (optionally) a second Properties object to act as the 'parent'
 * properties.  This allows both for a single point of reference for
 * variables, as well as the ability to do hierarchical lookups with the
 * parent (see getProperty() for an example).
 *
 * The order of lookup is as follows:  Local properties are checked first,
 * then parent properties, and then finally (if the value is not found in
 * any properties) the ResourceBundle is checked.
 */

public abstract class VariableBundle extends Object {
  protected Properties properties;
  protected Properties writableProperties;
  protected Properties temporaryProperties = new Properties();
  protected ResourceBundle resources;
  protected VariableBundle parentProperties;
  protected Set removeSet = new HashSet();
  protected Hashtable VCListeners = new Hashtable();
  protected Hashtable VCGlobListeners = new Hashtable();

  public VariableBundle() {
  }

  public VariableBundle(Properties editableProperties, VariableBundle newParentProperties) {
    writableProperties = editableProperties;
    parentProperties = newParentProperties;
    properties = new Properties();
    resources = null;
  }

  /**
   * Returns the value for the given property, or the provided
   * defaultValue if the property does not exist.
   */
  public String getProperty(String key, String defaultValue) {
    String returnValue = "";

    if (! propertyIsRemoved(key)) {
      returnValue = temporaryProperties.getProperty(key, "");
    }
    if (returnValue == "") {
      if (! propertyIsRemoved(key)) {
        returnValue = writableProperties.getProperty(key, "");
      }
      if (returnValue == "") {
        if (! propertyIsRemoved(key)) {
          returnValue = properties.getProperty(key, "");
        }
        if (returnValue == "") {
          returnValue=getParentProperty(key, "");
          if (returnValue == "") {
            if (resources != null)
              try {
                returnValue = resources.getString(key);
              } catch (MissingResourceException mre) {
                returnValue=defaultValue;
              }
            else
              returnValue = defaultValue;
          }
        }
      }
    }
    return returnValue;
  }

  public String getProperty(String key) throws MissingResourceException {
    String returnValue;

    returnValue = getProperty(key, "");
    if (returnValue == "") {
      throw new MissingResourceException(key, "", key);
    }
    return returnValue;
  }



  private String getParentProperty(String key, String defaultValue) {
    if (parentProperties==null) {
      return defaultValue;
    } else {
      return parentProperties.getProperty(key, defaultValue);
    }
  }


  public ResourceBundle getResources() {
    return resources;
  }

  public void setResourceBundle (ResourceBundle newResources) {
    resources = newResources;
  }

  public Properties getProperties() {
    return properties;
  }

  /**
   * Sets the Properties object for this VariableBundle.
   */
  public void setProperties(Properties newProperties) {
    properties = newProperties;
  }

  public VariableBundle getParentProperties() {
    return parentProperties;
  }

  public Properties getWritableProperties() {
    return writableProperties;
  }

  public void setProperty(String propertyName, String propertyValue) {
    internSetProperty(propertyName, propertyValue, true);
  }

  /**
   * Sets a group of properties at once, not firing any valueChanged events
   * until all properties are set.
   */
  public void setAllProperties(Properties properties) {
    for (String propertyName: properties.stringPropertyNames()) {
      String propertyValue = properties.getProperty(propertyName);
      internSetProperty(propertyName, propertyValue, false);
    }
    for (String propertyName: properties.stringPropertyNames()) {
      fireValueChanged(propertyName);
    }
  }

  private void internSetProperty(String propertyName, String propertyValue, boolean notify) {

    temporaryProperties.remove(propertyName);
    if (propertyValue == null || propertyValue.equalsIgnoreCase("")) {
      removeProperty(propertyName);
      writableProperties.remove(propertyName);
    } else {
      unRemoveProperty(propertyName);
      writableProperties.setProperty(propertyName, propertyValue);
    }
    if (notify) {
      fireValueChanged(propertyName);
    }
  }

  /**
   * sets a property as temporary (so it won't be saved).
   */
  public void setProperty(String propertyName, String propertyValue, boolean temporary) {
    if (temporary) {
      temporaryProperties.setProperty(propertyName, propertyValue);
      fireValueChanged(propertyName);
    } else {
      setProperty(propertyName, propertyValue);
    }
  }

  /**
   * Returns a property which has multiple values separated by a ':' (colon)
   * as a java.util.Enumeration.
   */

  public Enumeration getPropertyAsEnumeration(String propertyName, String defaultValue) {
    StringTokenizer tokens = new StringTokenizer(getProperty(propertyName, defaultValue), ":");
    return tokens;
  }

  /**
   * Converts a value which has multiple values separated by a ':' (colon)
   * to a java.util.Vector.
   */
  public static Vector<String> convertToVector(String value) {
    Vector<String> returnValue = new Vector<String>();
    StringTokenizer tokens = new StringTokenizer(value, ":");
    while (tokens.hasMoreElements())
      returnValue.add((String)tokens.nextElement());
    return returnValue;
  }

  /**
   * Converts the given property value to a Vector using the convertToVector
   * call.
   */
  public Vector<String> getPropertyAsVector(String propertyName, String defaultValue) {
    return convertToVector(getProperty(propertyName, defaultValue));
  }

  /**
   * Converts a value which has multiple values separated by a ':' (colon)
   * to a java.util.List.
   */
  public static List<String> convertToList(String value) {
/*    List<String> returnValue = new ArrayList<String>();
    StringTokenizer tokens = new StringTokenizer(value, ":");
    while (tokens.hasMoreElements())
      returnValue.add((String)tokens.nextElement());
    return returnValue;
*/
	    //Liao-
		List tokens = new LinkedList();
		int sQuotePos = -1, eQuotePos = -1, sPos = 0, ePos= 0;
		while(sPos < value.length()){
			if(value.charAt(sPos) == '"')
				sQuotePos = sPos;
			if(sQuotePos != -1){
				eQuotePos = value.indexOf('"',sQuotePos+1);
				ePos = value.indexOf(':', eQuotePos+1);
			}else{
				ePos = value.indexOf(':', sPos+1);
			}
			
			if(ePos == -1){
				ePos = value.length();
			}
			if(sPos <= ePos){
				String token = value.substring(sPos, ePos);
				if(token.charAt(0) == '"' && token.charAt(token.length()-1) == '"' ){
					token = token.substring(1, token.length()-1);
				}
				tokens.add(token);
				
				sPos = ePos + 1;
				
				eQuotePos = -1; sQuotePos = -1;
			}
			else{
				break;
			}
		}

		return tokens;
  }

  /**
   * Converts the given property value to a List using the convertToList
   * call.
   */
  public List<String> getPropertyAsList(String propertyName, String defaultValue) {
    return convertToList(getProperty(propertyName, defaultValue));
  }

  /**
   * Converts a List of Strings to a colon-delimited String.
   */
  public static String convertToString(List pValue) {
    if (pValue == null || pValue.size() == 0)
      return "";
    else {
      StringBuffer returnBuffer = new StringBuffer();
      Iterator it = pValue.iterator();
      while (it.hasNext()) {
        returnBuffer.append((String) it.next());
        if (it.hasNext()) {
          returnBuffer.append(":");
        }
      }

      return returnBuffer.toString();
    }
  }

  /**
   * Returns all property keys in this VariableBundle.
   */
  public Set<String> getPropertyNames() {
    HashSet<String> returnValue = new HashSet<String>();
    returnValue.addAll(temporaryProperties.stringPropertyNames());
    returnValue.addAll(writableProperties.stringPropertyNames());
    returnValue.addAll(properties.stringPropertyNames());
    if (parentProperties != null)
      returnValue.addAll(parentProperties.getPropertyNames());
    if (resources != null)
      returnValue.addAll(resources.keySet());

    return returnValue;
  }

  /**
   * Returns all property keys in this VariableBundle that start with
   * the given string.
   */
  public Set<String> getPropertyNamesStartingWith(String startsWith) {
    Set<String> returnValue = new HashSet<String>();
    Set<String> allProps = getPropertyNames();
    for (String prop: allProps) {
      if (prop.startsWith(startsWith))
        returnValue.add(prop);
    }

    return returnValue;
  }

  /**
   * Clears the removeList.  This should generally be called after
   * you do a writeProperties();
   */
  public void clearRemoveList() {
    removeSet.clear();
  }

  /**
   * This removes the property from the current VariableBundle.
   */
  private void removeProperty(String remProp) {
    if (remProp != null) {
      removeSet.add(remProp.intern());
    }
  }

  /**
   * Removes a property from the removeList.  Only necessary if a property
   * had been removed since the last save, and now has been set to a new
   * value.  It's probably a good idea, though, to call this method any
   * time a property has its value set.
   */
  private void unRemoveProperty(String unRemProp) {
    if (unRemProp != null) {
      removeSet.remove(unRemProp.intern());
    }
  }

  /**
   * Returns true if the property has been removed.
   */
  public boolean propertyIsRemoved(String prop) {
    if (prop != null) {
      return removeSet.contains(prop.intern());
    }
    return false;
  }

  /**
   * Saves the current properties in the VariableBundle to a file.  Note
   * that this only saves the writableProperties of this particular
   * VariableBundle--underlying defaults are not written.
   */
  public abstract void saveProperties();

  /**
   * This notifies all registered listeners for changedValue that its
   * value has changed.
   */
  public void fireValueChanged(String changedValue) {
    // only notify each listener once.
    Set notified = new HashSet();

    Vector listeners = (Vector)VCListeners.get(changedValue);
    if (listeners != null) {
      Iterator iter = listeners.iterator();
      while (iter.hasNext()) {
        ValueChangeListener vcl = (ValueChangeListener) iter.next();
        vcl.valueChanged(changedValue);
        notified.add(vcl);
      }
    }

    // now add the glob listeners.

    Enumeration keys = VCGlobListeners.keys();
    while (keys.hasMoreElements()) {
      String currentPattern = (String) keys.nextElement();
      if (changedValue.startsWith(currentPattern)) {
        Vector globListeners = (Vector) VCGlobListeners.get(currentPattern);
        if (globListeners != null && globListeners.size() > 0) {
          for (int i = 0; i < globListeners.size(); i++) {
            ValueChangeListener currentListener = ((ValueChangeListener)globListeners.elementAt(i));
            if (!notified.contains(currentListener)) {
              currentListener.valueChanged(changedValue);
              notified.add(currentListener);
            }
          }
        }
      }
    }

  }

  /**
   * This adds the ValueChangeListener to listen for changes in the
   * given property.
   */
  public void addValueChangeListener(ValueChangeListener vcl, String property) {
    if (property.endsWith("*")) {
      String startProperty = property.substring(0, property.length() - 1);
      Vector listeners = (Vector)VCGlobListeners.get(startProperty);
      if (listeners == null) {
        listeners = new Vector();
        listeners.add(vcl);
        VCGlobListeners.put(startProperty, listeners);
      } else {
        if (!listeners.contains(vcl))
          listeners.add(vcl);
      }

    } else {
      Vector listeners = (Vector)VCListeners.get(property);
      if (listeners == null) {
        listeners = new Vector();
        listeners.add(vcl);
        VCListeners.put(property, listeners);
      } else {
        if (!listeners.contains(vcl))
          listeners.add(vcl);
      }
    }
  }

  /**
   * This removes the given ValueChangeListener for all the values that
   * it's listening to.
   */
  public void removeValueChangeListener(ValueChangeListener vcl) {
    Enumeration keys = VCListeners.keys();
    Vector currentListenerList;
    while (keys.hasMoreElements()) {
      currentListenerList = (Vector)VCListeners.get(keys.nextElement());
      while (currentListenerList != null && currentListenerList.contains(vcl))
        currentListenerList.remove(vcl);
    }

    keys = VCGlobListeners.keys();
    while (keys.hasMoreElements()) {
      currentListenerList = (Vector)VCGlobListeners.get(keys.nextElement());
      while (currentListenerList != null && currentListenerList.contains(vcl))
        currentListenerList.remove(vcl);
    }
  }

  /**
   * This removes the given ValueChangeListener from listening on the
   * given property.
   */
  public void removeValueChangeListener(ValueChangeListener vcl, String property) {
    Vector currentListenerList;
    currentListenerList = (Vector)VCListeners.get(property);
    while (currentListenerList != null && currentListenerList.contains(vcl))
      currentListenerList.remove(vcl);

    currentListenerList = (Vector)VCGlobListeners.get(property);
    while (currentListenerList != null && currentListenerList.contains(vcl))
      currentListenerList.remove(vcl);
  }

  /**
   * Returns all of the ValueChangeListeners registered.
   */
  public Map getAllListeners() {
    HashMap returnValue =  new HashMap(VCListeners);
    returnValue.putAll(VCGlobListeners);
    return returnValue;
  }

  /**
   * Returns a formatted message using the given key and the appropriate
   * objects.  If no message corresponding to the given key exists, uses
   * the key string as the pattern instead.
   */
  public String formatMessage(String key, Object... arguments) {
    String pattern = getProperty(key, key);
    return java.text.MessageFormat.format(pattern, arguments);
  }
}


