/**
 * XercesDocumentAdapterBuilder
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

import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import com.taursys.debug.Debug;

/**
 * XercesDocumentAdapterBuilder is used to create a
 * DOM_2_20001113_DocumentAdapter. It provides a variety of build methods with
 * options to validate the document source. It uses the Xerces 1.4.4 DOMParser
 * to create the DocumentAdapter.
 * 
 * @author marty
 * @version $Revision: 1.3 $
 */
public class XercesDocumentAdapterBuilder extends DocumentAdapterBuilder {
  public static final String FEATURE_VALIDATION =
    "http://xml.org/sax/features/validation";
  public static final String FEATURE_LOAD_EXTERNAL_DTD =
    "http://apache.org/xml/features/nonvalidating/load-external-dtd";
  private DOMParser parser;

  /**
   * Constructs new builder with FEATURE_VALIDATION and FEATURE_LOAD_EXTERNAL_DTD features disabled.
   */
  public XercesDocumentAdapterBuilder() {
    parser = new DOMParser();
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
    try {
      if (parser.isFeatureRecognized(featureName)) {
        parser.setFeature(featureName, value);
      }
    } catch (SAXNotRecognizedException e) {
      Debug.debug("Feature " + featureName + " not recognized", e);
    } catch (SAXNotSupportedException e) {
      Debug.debug("Feature  " + featureName + "not supported", e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.taursys.dom.DocumentAdapterBuilder#getFeature(java.lang.String)
   */
  public boolean getFeature(String featureName) {
    try {
      if (parser.isFeatureRecognized(featureName)) {
        return parser.getFeature(featureName);
      }
    } catch (SAXNotRecognizedException e) {
      Debug.debug("Feature " + featureName + " not recognized", e);
    } catch (SAXNotSupportedException e) {
      Debug.debug("Feature  " + featureName + "not supported", e);
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
      if (parser.isFeatureRecognized(propertyName)) {
        parser.setProperty(propertyName, value);
      }
    } catch (SAXNotRecognizedException e) {
      Debug.debug("Property " + propertyName + " not recognized", e);
    } catch (SAXNotSupportedException e) {
      Debug.debug("Property " + propertyName + "not supported", e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.taursys.dom.DocumentAdapterBuilder#getProperty(java.lang.String)
   */
  public Object getProperty(String propertyName) {
    try {
      if (parser.isPropertyRecognized(propertyName)) {
        return parser.getProperty(propertyName);
      }
    } catch (SAXNotRecognizedException e) {
      Debug.debug("Property " + propertyName + " not recognized", e);
    } catch (SAXNotSupportedException e) {
      Debug.debug("Property  " + propertyName + "not supported", e);
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.taursys.dom.DocumentAdapterBuilder#build(java.io.InputStream)
   */
  public DocumentAdapter build(InputStream is)
      throws DocumentAdapterBuilderException {
    if (is == null) {
      throw new DocumentAdapterBuilderException(MESSAGE_NULL_INPUT_STREAM);
    }
    try {
      parser.parse(new InputSource(is));
    } catch (Exception e) {
      throw new DocumentAdapterBuilderException(e.getMessage());
    }
    return DOMDocumentAdapterFactory.getInstance().newDOMDocumentAdapter(
        parser.getDocument());
  }

}