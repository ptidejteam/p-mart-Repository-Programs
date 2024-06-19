/**
 * JAXPDocumentAdapterBuilder
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

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.taursys.debug.Debug;

/**
 * JAXPDocumentAdapterBuilder is used to create a
 * DOM_2_20001113_DocumentAdapter. It provides a variety of build methods with
 * options to validate the document source. It uses the Xerces 2.x.x DOMParser
 * to create the DocumentAdapter.
 * 
 * @author marty
 * @version $Revision: 1.3 $
 */
public class JAXPDocumentAdapterBuilder extends DocumentAdapterBuilder
    implements ErrorHandler {

  public static final String FEATURE_COALESCING = "javax.xml.parsers.DocumentBuilderFactory.coalescing";
  public static final String FEATURE_EXPAND_ENTITY_REFERENCES = "javax.xml.parsers.DocumentBuilderFactory.expandEntityReferences";
  public static final String FEATURE_IGNORE_COMMENTS = "javax.xml.parsers.DocumentBuilderFactory.ignoreComments";
  public static final String FEATURE_IGNORE_ELEMENT_CONTENT_WHITESPACE = "javax.xml.parsers.DocumentBuilderFactory.ignoreElementContentWhitespace";
  public static final String FEATURE_NAMESPACE_AWARE = "javax.xml.parsers.DocumentBuilderFactory.namespaceAware";
  public static final String FEATURE_VALIDATING = "javax.xml.parsers.DocumentBuilderFactory.validating";

  private static final String FEATURE_VALIDATION = "http://xml.org/sax/features/validation";
  private static final String FEATURE_LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";

  private DocumentBuilderFactory factory;

  /**
   * Constructs new builder with FEATURE_VALIDATING, FEATURE_VALIDATION and
   * FEATURE_LOAD_EXTERNAL_DTD features disabled.
   */
  public JAXPDocumentAdapterBuilder() {
    factory = DocumentBuilderFactory.newInstance();
    setFeature(FEATURE_VALIDATING, false);
    setFeature(FEATURE_VALIDATION, false);
    setFeature(FEATURE_LOAD_EXTERNAL_DTD, false);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.taursys.dom.DocumentAdapterBuilder#setFeature(java.lang.String,
   *      boolean)
   */
  public void setFeature(String featureName, boolean value) {
    if (featureName.equals(FEATURE_COALESCING)) {
      factory.setCoalescing(value);
    } else if (featureName.equals(FEATURE_EXPAND_ENTITY_REFERENCES)) {
      factory.setExpandEntityReferences(value);
    } else if (featureName.equals(FEATURE_IGNORE_COMMENTS)) {
      factory.setIgnoringComments(value);
    } else if (featureName.equals(FEATURE_IGNORE_ELEMENT_CONTENT_WHITESPACE)) {
      factory.setIgnoringElementContentWhitespace(value);
    } else if (featureName.equals(FEATURE_NAMESPACE_AWARE)) {
      factory.setNamespaceAware(value);
    } else if (featureName.equals(FEATURE_VALIDATING)) {
      factory.setValidating(value);
    } else {
      try {
        factory.setAttribute(featureName, new Boolean(value));
      } catch (IllegalArgumentException e) {
        Debug.debug("Feature " + featureName + " not recognized", e);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.taursys.dom.DocumentAdapterBuilder#getFeature(java.lang.String)
   */
  public boolean getFeature(String featureName) {
    if (featureName.equals(FEATURE_COALESCING)) {
      return factory.isCoalescing();
    } else if (featureName.equals(FEATURE_EXPAND_ENTITY_REFERENCES)) {
      return factory.isExpandEntityReferences();
    } else if (featureName.equals(FEATURE_IGNORE_COMMENTS)) {
      return factory.isIgnoringComments();
    } else if (featureName.equals(FEATURE_IGNORE_ELEMENT_CONTENT_WHITESPACE)) {
      return factory.isIgnoringElementContentWhitespace();
    } else if (featureName.equals(FEATURE_NAMESPACE_AWARE)) {
      return factory.isNamespaceAware();
    } else if (featureName.equals(FEATURE_VALIDATING)) {
      return factory.isValidating();
    } else {
      try {
        Object value = factory.getAttribute(featureName);
        if (value instanceof Boolean) {
          return ((Boolean) value).booleanValue();
        } else {
          Debug.debug("Feature " + featureName + " exists, but is not Boolean");
          return false;
        }
      } catch (IllegalArgumentException e) {
        Debug.debug("Feature " + featureName + " not recognized", e);
      }
    }
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.taursys.dom.DocumentAdapterBuilder#setProperty(java.lang.String,
   *      java.lang.Object)
   */
  public void setProperty(String propertyName, Object value) {
    try {
      factory.setAttribute(propertyName, value);
    } catch (IllegalArgumentException e) {
      Debug.debug("Property " + propertyName + " not recognized", e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.taursys.dom.DocumentAdapterBuilder#getProperty(java.lang.String)
   */
  public Object getProperty(String propertyName) {
    try {
      return factory.getAttribute(propertyName);
    } catch (IllegalArgumentException e) {
      Debug.debug("Property " + propertyName + " not recognized", e);
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.taursys.dom.DocumentAdapterBuilder#build(java.io.InputStream,
   *      boolean)
   */
  public DocumentAdapter build(InputStream is)
      throws DocumentAdapterBuilderException {
    if (is == null) {
      throw new DocumentAdapterBuilderException(MESSAGE_NULL_INPUT_STREAM);
    }
    Document doc = null;
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      builder.setErrorHandler(this);
      doc = builder.parse(new InputSource(is));
    } catch (Exception e) {
      throw new DocumentAdapterBuilderException(e.getMessage());
    }
    return DOMDocumentAdapterFactory.getInstance().newDOMDocumentAdapter(doc);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
   */
  public void error(SAXParseException e) throws SAXException {
    Debug.error(e.getMessage());
    throw e;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
   */
  public void fatalError(SAXParseException e) throws SAXException {
    Debug.fatal(e.getMessage());
    throw e;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
   */
  public void warning(SAXParseException e) throws SAXException {
    Debug.warn(e.getMessage());
  }

}