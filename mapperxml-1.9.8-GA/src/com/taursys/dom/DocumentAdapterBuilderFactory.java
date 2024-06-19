/**
 * DocumentAdapterBuilderFactory
 *
 * Copyright (c) 2004
 *      Marty Phelan, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package com.taursys.dom;

import java.util.Hashtable;
import java.util.Iterator;

import com.taursys.debug.Debug;

/**
 * DocumentAdapterBuilderFactory is used to create DocumentAdapterBuilders based
 * on the factory configuration. 
 * 
 * @author marty
 * @version $Revision: 1.4 $
 */
public class DocumentAdapterBuilderFactory {
  public static final String TIDY_BUILDER = "com.taursys.dom.TidyDocumentAdapterBuilder";
  public static final String JAXP_BUILDER = "com.taursys.dom.JAXPDocumentAdapterBuilder";
  public static final String XERCES_BUILDER = "com.taursys.dom.XercesDocumentAdapterBuilder";
  public static final String SYSTEM_PROPERTY_BUILDER_NAME = "com.taursys.dom.DocumentAdapterBuilder";
  private static final Object[][] builderOptions = new Object[][] {
      new Object[] { "org.w3c.tidy.Tidy", TIDY_BUILDER },
      new Object[] { "org.apache.xerces.parsers.DOMParser", XERCES_BUILDER },
      new Object[] { "javax.xml.parsers.DocumentBuilder", JAXP_BUILDER } };

  private static DocumentAdapterBuilderFactory _factory;

  private Hashtable properties = new Hashtable();
  private Hashtable features = new Hashtable();

  // =============================================================================
  // Factory Instance Methods
  // =============================================================================

  /**
   * Default constructor
   */
  private DocumentAdapterBuilderFactory() {
  }

  /**
   * Gets a new, private instance of DocumentAdapterBuilderFactory
   * 
   * @return a new, private instance of DocumentAdapterBuilderFactory
   */
  public static DocumentAdapterBuilderFactory newInstance() {
    return new DocumentAdapterBuilderFactory();
  }

  /**
   * Gets singleton shared instance of DocumentAdapterBuilderFactory
   * 
   * @return singleton shared instance of DocumentAdapterBuilderFactory
   */
  public static DocumentAdapterBuilderFactory getInstance() {
    if (_factory == null) {
      _factory = new DocumentAdapterBuilderFactory();
    }
    return _factory;
  }

  // =============================================================================
  // Factory Methods
  // =============================================================================

  /**
   * Creates a new DocumentAdapterBuilder and initializes it based on this
   * factory's settings. The specific builder class returned is determined by
   * the following rules:
   * <ul>
   * <li>If the com.taursys.dom.DocumentAdapterBuilder system property is set,
   * the class name it specifies will be created and returned. A
   * DocumentAdapterBuilderException will be thrown if the specified class
   * does not extend com.taursys.dom.DocumentAdapterBuilder.</li>
   * <li>The classpath is searched for org.w3c.tidy.Tidy. If found, a
   * TidyDocumentAdapterBuilder is returned.</li>
   * <li>The classpath is searched for org.apache.xerces.parsers.DOMParser. If
   * found, a XercesDocumentAdapterBuilder is returned.</li>
   * <li>The classpath is searched for javax.xml.parsers.DocumentBuilder. If
   * found, a JAXPDocumentAdapterBuilder is returned.</li>
   * <li>If none of the above are successful, a DocumentAdapterBuilderException
   * is thrown.</li>
   * </ul> 
   * 
   * @return a new DocumentAdapterBuilder
   * @throws DocumentAdapterBuilderException
   *           if cannot create and initialize a new DocumentAdapterBuilder
   */
  public DocumentAdapterBuilder newDocumentAdapterBuilder()
      throws DocumentAdapterBuilderException {
    Class clazz = defaultDocumentAdatperBuilderClass();
    if (clazz == null) {
      String msg = "Cannot determine appropriate "
          + "DocumentAdapterBuilder to use. Need to ensure that "
          + "Tidy, Xerces or JAXB is in classpath";
      Debug.debug(msg);
      throw new DocumentAdapterBuilderException(msg);
    }
    DocumentAdapterBuilder builder = createInstance(clazz);
    // Set properties and features
    for (Iterator iter = properties.keySet().iterator(); iter.hasNext();) {
      String key = (String) iter.next();
      builder.setProperty(key, properties.get(key));
    }
    for (Iterator iter = features.keySet().iterator(); iter.hasNext();) {
      String key = (String) iter.next();
      builder.setFeature(key, ((Boolean) features.get(key)).booleanValue());
    }
    return builder;
  }

  /**
   * Returns a default DocumentAdapterBuilder class. It checks for the presence
   * of various document builders to determine the appropriate
   * DocumentAdapterBuilder to use. It first checks for a specified
   * DocumentAdapterBuilder based on the System property
   * com.taursys.dom.DocumentAdapterBuilder.
   * 
   * @return a default DocumentAdapterBuilder class
   * @throws DocumentAdapterBuilderException
   */
  private Class defaultDocumentAdatperBuilderClass()
      throws DocumentAdapterBuilderException {
    String clazzName = System.getProperty(SYSTEM_PROPERTY_BUILDER_NAME);
    if (clazzName != null) {
      try {
        return Class.forName(clazzName);
      } catch (ClassNotFoundException e) {
        String msg = "DocumentAdapterBuilder class=" + clazzName + " not found";
        Debug.error(msg);
        throw new DocumentAdapterBuilderException(msg);
      }
    }
    // Try other builderOptions
    for (int i = 0; i < builderOptions.length; i++) {
      try {
        Class.forName((String) builderOptions[i][0]);
        return Class.forName((String) builderOptions[i][1]);
      } catch (ClassNotFoundException e) {
      }
    }
    return null;
  }

  /**
   * Obtain a new concrete instance of a DocumentAdapterBuilder of the given
   * class.
   * 
   * @param type
   *          fully qualified name of a DocumentAdapterBuilder class to use
   * @return a new concrete builder
   * @throws DocumentAdapterBuilderException
   *           if type is null or builder cannot be created.
   */
  private DocumentAdapterBuilder createInstance(Class clazz)
      throws DocumentAdapterBuilderException {
    String msg = "DocumentAdapterBuilder class=" + clazz.getName()
        + " cannot be instantiated.";
    try {
      return (DocumentAdapterBuilder) clazz.newInstance();
    } catch (InstantiationException e) {
      Debug.debug(msg, e);
      throw new DocumentAdapterBuilderException(msg);
    } catch (IllegalAccessException e) {
      Debug.debug(msg, e);
      throw new DocumentAdapterBuilderException(msg);
    } catch (ClassCastException e) {
      msg += " Not instance of com.taursys.dom.DocumentAdapterBuilder";
      Debug.debug(msg, e);
      throw new DocumentAdapterBuilderException(msg);
    }
  }

  // =============================================================================
  // Property Accessor Methods
  // =============================================================================

  /**
   * Sets the given property to the given value. A null value removes the given
   * property (un-sets the property). See the Specific DocumentAdapterBuilder,
   * DocumentBuilder and/or Parser for the specific Features and Properties.
   * 
   * @param propertyName
   *          name of the property to set
   * @param value
   *          for the property or null to un-set
   */
  public void setProperty(String propertyName, Object value) {
    if (value != null) {
      properties.put(propertyName, value);
    } else {
      properties.remove(propertyName);
    }
  }

  /**
   * Gets the given property. Returns null if the property has not been set. See
   * the Specific DocumentAdapterBuilder, DocumentBuilder and/or Parser for the
   * specific Features and Properties.
   * 
   * @param propertyName
   *          to retrieve
   * @return value of property or null if not set.
   */
  public Object getProperty(String propertyName) {
    return properties.get(propertyName);
  }

  /**
   * Enables the given featureName if value is true, otherwise disables feature.
   * See the Specific DocumentAdapterBuilder, DocumentBuilder and/or Parser for
   * the specific Features and Properties.
   * 
   * @param featureName
   *          to enable/disable
   * @param value
   *          true to enable, false to disable
   */
  public void setFeature(String featureName, boolean value) {
    features.put(featureName, new Boolean(value));
  }

  /**
   * Returns true if given featureName is enabled, otherwise false See the
   * Specific DocumentAdapterBuilder, DocumentBuilder and/or Parser for the
   * specific Features and Properties.
   * 
   * @param featureName
   *          to query
   * @return true if enabled, otherwise false
   */
  public boolean getFeature(String featureName) {
    Boolean value = (Boolean) features.get(featureName);
    if (value != null) {
      return value.booleanValue();
    } else {
      return false;
    }
  }

  // =============================================================================
  // Property to fix or deprecate
  // =============================================================================

  //TODO consider whether or not to deprecate validating property
  private boolean validating;

  /**
   * Indicates whether or not the factory is configured to produce parsers which
   * validate the XML content during parse. Validating also controls whether or
   * not the parser will load the external dtd for the document.
   * 
   * @return true if the factory is configured to produce parsers which validate
   *         the XML content during parse; false otherwise.
   */
  public boolean isValidating() {
    return validating;
  }

  /**
   * Specifies that the parser produced by this code will validate documents as
   * they are parsed. By default the value of this is set to false. Validating
   * also controls whether or not the parser will load the external dtd for the
   * document.
   * 
   * @param b
   *          true if the parser produced will validate documents as they are
   *          parsed; false otherwise.
   */
  public void setValidating(boolean b) {
    validating = b;
  }

}