/**
 * DOMDocumentAdapterFactory
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

import java.lang.reflect.Method;

/**
 * DOMDocumentAdapterFactory is concrete factory that creates DOM type
 * DocumentAdapters to wrap DOM documents.
 * 
 * @author Marty Phelan
 * @version $Revision: 1.2 $
 */
public class DOMDocumentAdapterFactory {
  public static final int DOM_1_20000929 = 120000929;
  public static final int DOM_2_20000510 = 220000510;
  public static final int DOM_2_20000927 = 220000927;
  public static final int DOM_2_20001113 = 220001113;
  public static final int DOM_3_20010913 = 320010913;
  public static final int DOM_3_20020114 = 320020114;

  private static DOMDocumentAdapterFactory _factory;

  /**
   * Default constructor 
   */
  private DOMDocumentAdapterFactory() {
  }

  /**
   * Get singleton instance of DOMDocumentAdapterFactory
   * @return singleton instance of DOMDocumentAdapterFactory
   */
  public static DOMDocumentAdapterFactory getInstance() {
    if (_factory == null) {
      _factory = new DOMDocumentAdapterFactory();
    }
    return _factory;
  }

  /**
   * Create new DOM type DocumentAdapter which conforms to the
   * DOM implementation level of the given Document object.
   * 
   * @param doc
   * @throws DocumentAdapterBuilderException if cannot build adapter
   */
  public DocumentAdapter newDOMDocumentAdapter(Object doc)
    throws DocumentAdapterBuilderException {
    if (doc == null) {
      throw new DocumentAdapterBuilderException("Given Document cannot be null");
    }
    // Special Check for Tidy
    if (doc.getClass().getName().equals("org.w3c.tidy.DOMDocumentImpl")) {
      return new Tidy_DOM_2_20001113_DocumentAdapter(doc);
    }
    int level = determineDOMLevel(doc.getClass());
    if (level == 0) {
      throw new DocumentAdapterBuilderException(
        "Cannot determine DOM Level for given Document");
    }
    switch (level) {
      case DOM_1_20000929 :
        return new DOM_1_20000929_DocumentAdapter(doc);
      case DOM_2_20000510 :
        return new DOM_1_20000929_DocumentAdapter(doc);
      case DOM_2_20000927 :
        return new DOM_2_20001113_DocumentAdapter(doc);
      case DOM_2_20001113 :
        return new DOM_2_20001113_DocumentAdapter(doc);
      case DOM_3_20010913 :
        return new DOM_2_20001113_DocumentAdapter(doc);
      case DOM_3_20020114 :
        return new DOM_2_20001113_DocumentAdapter(doc);
    }
    throw new DocumentAdapterBuilderException(
      "No suitable DocumentAdapter found for document with level=" + level);
  }

  /**
   * Return true if given Class is, extends or implements org.w3c.dom.Document
   * @param docClazz to test
   * @return true if given Class implements org.w3c.dom.Document
   */  
  private boolean implementsDocument(Class docClazz) {
    if (docClazz.getName().equals("org.w3c.dom.Document")) {
      return true;
    }
    Class[] interfaces = docClazz.getInterfaces();
    for (int i = 0; i < interfaces.length; i++) {
      if (interfaces[i].getName().equals("org.w3c.dom.Document")) {
        return true;
      }
    }
    // recurse for superclasses
    if (docClazz.getSuperclass() == null) {
      return false;
    } else {
      return implementsDocument(docClazz.getSuperclass());
    }
  }

  /**
   * Determines a DOM level for the given org.w3c.dom.Document class
   * @param docClazz the class of the Document
   * @return a int constant for the DOM Level or 0 if cannot determine
   */
  public int determineDOMLevel(Class docClazz) {
    if (!implementsDocument(docClazz)) {
      return 0;
    }
    Method[] methods = docClazz.getMethods();
    // Preset to lowest supported level
    int level = DOM_1_20000929;
    for (int i = 0; i < methods.length; i++) {
      String methodName = methods[i].getName();
      if (methodName.equals("canSetNormalizationFeature")) {
        level = Math.max(level, DOM_3_20020114);
      }
      if (methodName.equals("getActualEncoding")) {
        level = Math.max(level, DOM_3_20010913);
      }
      // No method to differentiate DOM_2_20001113 from DOM_2_20000927
      //      if (methodName.equals("???")) {
      //        level = Math.max(level, DOM_2_20001113);
      //      }
      if (methodName.equals("isSupported")) {
        level = Math.max(level, DOM_2_20000927);
      }
      if (methodName.equals("getElementById")) {
        level = Math.max(level, DOM_2_20000510);
      }
    }
    return level;
  }

}
