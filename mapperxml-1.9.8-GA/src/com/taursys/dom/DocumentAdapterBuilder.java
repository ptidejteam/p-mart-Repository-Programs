/**
 * DocumentAdapterBuilder
 *
 * Copyright (c) 2004-2006
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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Hashtable;

/**
 * DocumentAdapterBuilder is used to create DocumentAdapters. It provides a
 * variety of build methods.
 * 
 * @author marty
 * @version $Revision: 1.3 $
 */
public abstract class DocumentAdapterBuilder {

  protected static final String MESSAGE_NULL_INPUT_STREAM = "Document not found: InputStream is null";
  private static final String RESOURCE_QUALIFIED = "resource://";
  private static final String RESOURCE_RELATIVE = "resource:///";
  
  private Hashtable properties = new Hashtable();
  private HashSet features = new HashSet();

  /**
   * Sets the given property to the given value.
   * See the Specific DocumentAdapterBuilder, DocumentBuilder and/or Parser for
   * the specific Features and Properties.
   * @param propertyName name of the property to set
   * @param value for the property
   */
  public abstract void setProperty(String propertyName, Object value);

  /**
   * Gets the given property.
   * See the Specific DocumentAdapterBuilder, DocumentBuilder and/or Parser for
   * the specific Features and Properties.
   * @param propertyName to retrieve
   * @return value of property
   */
  public abstract Object getProperty(String propertyName);

  /**
   * Enables the given featureName if value is true, otherwise disables feature.
   * See the Specific DocumentAdapterBuilder, DocumentBuilder and/or Parser for
   * the specific Features and Properties.
   * @param featureName to enable/disable
   * @param value true to enable, false to disable
   */
  public abstract void setFeature(String featureName, boolean value);

  /**
   * Returns true if given featureName is enables, otherwise false
   * See the Specific DocumentAdapterBuilder, DocumentBuilder and/or Parser for
   * the specific Features and Properties.
   * @param featureName to query
   * @return true if enabled, otherwise false
   */
  public abstract boolean getFeature(String featureName);
  
  /**
   * Build a new DocumentAdapter from the XML document contained in the
   * InputStream.
   * 
   * @param is
   *          InputStream containing the XML source document
   * @return a new DocumentAdapter
   * @throws DocumentAdapterBuilderException
   *           if invalid parameters or problems parsing the document.
   */
  public abstract DocumentAdapter build(InputStream is)
      throws DocumentAdapterBuilderException;

  /**
   * Build a new DocumentAdapter from the XML document contained in the
   * indicated resource in the jar of the given class.
   * 
   * @param clazz
   *          use jar file (or class directory) which contains this class
   * @param resourceName
   *          name of file containing the XML source document
   * @return a new DocumentAdapter
   * @throws DocumentAdapterBuilderException
   *           if invalid parameters or problems parsing the document.
   */
  public DocumentAdapter build(Class clazz, String resourceName)
      throws DocumentAdapterBuilderException {
    if (clazz == null) {
      throw new DocumentAdapterBuilderException("Class for resource is null");
    }
    return build(clazz.getResourceAsStream(resourceName));
  }

  /**
   * Build a new DocumentAdapter from the XML document read as a resource using
   * the given ClassLoader.
   * 
   * @param loader
   *          ClassLoader to read the resource
   * @param resourceName
   *          name of file containing the XML source document
   * @return a new DocumentAdapter
   * @throws DocumentAdapterBuilderException
   *           if invalid parameters or problems parsing the document.
   */
  public DocumentAdapter build(ClassLoader loader, String resourceName)
      throws DocumentAdapterBuilderException {
    if (loader == null) {
      throw new DocumentAdapterBuilderException(
          "ClassLoader for resource is null");
    }
    InputStream is = loader.getResourceAsStream(resourceName);
    return build(is);
  }

  /**
   * Build a new DocumentAdapter from the XML document found at the URL.
   * 
   * @param url
   *          URL pointing to the XML source document
   * @return a new DocumentAdapter
   * @throws DocumentAdapterBuilderException
   *           if invalid parameters or problems parsing the document.
   */
  public DocumentAdapter build(URL url) throws DocumentAdapterBuilderException {
    if (url == null) {
      throw new DocumentAdapterBuilderException("URL is null");
    }
    InputStream is = null;
    try {
      is = url.openStream();
    } catch (IOException e) {
      throw new DocumentAdapterBuilderException(e.getMessage());
    }
    return build(is);
  }

  /**
   * Build a new DocumentAdapter from the XML document found at the given String
   * URI. If the protocol specified is resource:// then the Document will be
   * read as a Resource from the class loader. If the authority (eg
   * resource://[AUTHORITY]/path/doc.xml) is not specified, then the Document
   * will be loaded from the ClassLoader of the current Thread, otherwise it
   * will be loaded from the class specified in the authority section of the URL
   * (eg resource://com.company.MyClass/path/doc.xml).
   * 
   * @param uri
   *          URI pointing to the XML source document
   * @return a new DocumentAdapter
   * @throws DocumentAdapterBuilderException
   *           if invalid parameters or problems parsing or validating the
   *           document.
   */
  public DocumentAdapter build(String uri)
      throws DocumentAdapterBuilderException {
    if (uri.toLowerCase().startsWith(RESOURCE_RELATIVE)) {
      return build(Thread.currentThread().getContextClassLoader(), uri
          .substring(RESOURCE_RELATIVE.length()));
    } else if (uri.toLowerCase().startsWith(RESOURCE_QUALIFIED)) {
      int pos = uri.indexOf('/', RESOURCE_QUALIFIED.length());
      if (pos == -1) {
        throw new DocumentAdapterBuilderException(
            "Missing authority classname in URI. " +
            "Syntax: resource://com.mypkg.MyClass/Doc.html or " +
            "resource:///Doc.html.");
      }
      try {
        return build(Class.forName(uri.substring(RESOURCE_QUALIFIED.length(),
            pos)), uri.substring(pos));
      } catch (ClassNotFoundException e) {
        throw new DocumentAdapterBuilderException(
            "Class referenced in URI cannot be found");
      }
    } else {
      try {
        return build(new URL(uri));
      } catch (MalformedURLException e) {
        throw new DocumentAdapterBuilderException("URL in URI is malformed:"
            + e.getMessage());
      }
    }
  }
}