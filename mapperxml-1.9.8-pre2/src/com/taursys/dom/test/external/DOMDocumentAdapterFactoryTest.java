/**
 * DOMDocumentAdapterFactoryTest
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
package com.taursys.dom.test.external;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.TestCase;

import com.taursys.dom.DOMDocumentAdapterFactory;

/**
 * DOMDocumentAdapterFactoryTest tests DOMDocumentAdapterFactory.
 * 
 * NOTE: THis test must be run in a VM without org.w3c.dom in path
 * (ie below Java 1.4) 
 * 
 * @author Marty Phelan
 * @version $Revision: 1.1 $
 */
public class DOMDocumentAdapterFactoryTest extends TestCase {

  private ClassLoader loader;

  /**
   * Constructor for DOMDocumentAdapterFactoryTest.
   * @param arg0
   */
  public DOMDocumentAdapterFactoryTest(String arg0) {
    super(arg0);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(DOMDocumentAdapterFactoryTest.class);
  }

  /*
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
    loader = rootLoader(getClass().getClassLoader().getParent());
  }

  /*
   * Support method
   */
  private ClassLoader rootLoader(ClassLoader currentLoader) {
    if (currentLoader.getParent() == null) {
      return currentLoader;
    } else {
      return rootLoader(currentLoader.getParent());
    }
  }

  /*
   * Support method
   */
  private int getDOMLevel(String jarName)
    throws ClassNotFoundException, IOException {
    URL url = new URL("file:///home/marty/jbproject/DOMTests/lib/" +
        jarName);
    url.openStream().close();
    URL[] urls = new URL[] {
      url,
    };
    loader = new URLClassLoader(urls, loader);
    Class clazz = loader.loadClass("org.w3c.dom.Document");
    return DOMDocumentAdapterFactory.getInstance().determineDOMLevel(clazz);
  }

  /**
   * Test for DOM level
   * @throws Exception
   */
  public void testDetermineDOMLevel_1_19981001() 
      throws Exception {
    int level = getDOMLevel("dom-1-19981001.jar");
    assertEquals("Level",DOMDocumentAdapterFactory.DOM_1_20000929, level);
  }

  /**
   * Test for DOM level
   * @throws Exception
   */
  public void testDetermineDOMLevel_1_20000929() 
      throws Exception {
    int level = getDOMLevel("dom-1-20000929.jar");
    assertEquals("Level",DOMDocumentAdapterFactory.DOM_1_20000929, level);
  }
  
  /**
   * Test for DOM level
   * @throws Exception
   */
  public void testDetermineDOMLevel_2_20000510() 
      throws Exception {
    int level = getDOMLevel("dom-2-20000510.jar");
    assertEquals("Level",DOMDocumentAdapterFactory.DOM_2_20000510, level);
  }
  
  /**
   * Test for DOM level
   * @throws Exception
   */
  public void testDetermineDOMLevel_2_20000927() 
      throws Exception {
    int level = getDOMLevel("dom-2-20000927.jar");
    assertEquals("Level",DOMDocumentAdapterFactory.DOM_2_20000927, level);
  }
  
//Nothing to differentiate DOM_2_20001113 from DOM_2_20000927
//  /**
//   * Test for DOM level
//   * @throws Exception
//   */
//  public void testDetermineDOMLevel_2_20001113() 
//      throws Exception {
//    int level = getDOMLevel("dom-2-20001113.jar");
//    assertEquals("Level",DOMDocumentAdapterFactory.DOM_2_20001113, level);
//  }
  
  /**
   * Test for DOM level
   * @throws Exception
   */
  public void testDetermineDOMLevel_3_20010913() 
      throws Exception {
    int level = getDOMLevel("dom-3-20010913.jar");
    assertEquals("Level",DOMDocumentAdapterFactory.DOM_3_20010913, level);
  }
  
  /**
   * Test for DOM level
   * @throws Exception
   */
  public void testDetermineDOMLevel_3_20020114() 
      throws Exception {
    int level = getDOMLevel("dom-3-20020114.jar");
    assertEquals("Level",DOMDocumentAdapterFactory.DOM_3_20020114, level);
  }
  
}
