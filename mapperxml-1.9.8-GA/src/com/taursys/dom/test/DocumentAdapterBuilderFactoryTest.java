/**
 * DocumentAdapterBuilderFactoryTest
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
package com.taursys.dom.test;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.TestCase;

import com.taursys.dom.DocumentAdapterBuilder;
import com.taursys.dom.DocumentAdapterBuilderException;
import com.taursys.dom.DocumentAdapterBuilderFactory;
import com.taursys.dom.JAXPDocumentAdapterBuilder;
import com.taursys.dom.TidyDocumentAdapterBuilder;
import com.taursys.dom.XercesDocumentAdapterBuilder;
import com.taursys.dom.test.util.TestDocumentAdapterBuilder;

/**
 * Tests for the DocumentAdapterBuilderFactory
 * 
 * @author marty
 * @version $Revision: 1.3 $
 */
public class DocumentAdapterBuilderFactoryTest extends TestCase {

  /**
   * Constructor for DocumentAdapterBuilderFactoryTest.
   * 
   * @param arg0
   */
  public DocumentAdapterBuilderFactoryTest(String arg0) {
    super(arg0);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(DocumentAdapterBuilderFactoryTest.class);
  }

  /*
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
  }

  public void testDocumentAdapterBuilderFactoryDefaultWithTidyAndXerces()
      throws Exception {
    DocumentAdapterBuilderFactory factory = DocumentAdapterBuilderFactory
        .getInstance();
    DocumentAdapterBuilder builder = factory.newDocumentAdapterBuilder();
    assertEquals("DocumentAdapterBuilderClass",
        TidyDocumentAdapterBuilder.class, builder.getClass());
  }

  private void defaultUsingClassPathTest(String[] jars, String expectedClassName)
      throws Exception {
    String testdir = System.getProperty("com.taursys.test.dir");
    assertNotNull(
        "You must set system property: com.taursys.test.dir to path of MapperXML project directory",
        testdir);
    testdir = testdir.replaceAll("\\\\", "/");
    URL[] urls = new URL[jars.length + 1];
    for (int i = 0; i < urls.length - 1; i++) {
      urls[i] = new URL("file:///" + testdir + "/lib/" + jars[i]);
    }
    urls[urls.length -1] = new URL("file:///" + testdir + "/lib/mapperxml.jar");
    ClassLoader loader = rootLoader(getClass().getClassLoader().getParent());
    loader = new URLClassLoader(urls, loader);
    Class clazz = loader
        .loadClass("com.taursys.dom.DocumentAdapterBuilderFactory");
    Method method = clazz.getMethod("newInstance", new Class[] {});
    Object factory = method.invoke(clazz, new Object[] {});
    method = clazz.getMethod("newDocumentAdapterBuilder", new Class[] {});
    Object builder = method.invoke(factory, new Object[] {});
    assertEquals("Builder class", expectedClassName, builder.getClass()
        .getName());
  }

  public void testDocumentAdapterBuilderFactoryDefaultWithXercesButWithoutTidy()
      throws Exception {
    defaultUsingClassPathTest(new String[]{"xerces.jar"}, XercesDocumentAdapterBuilder.class.getName());

//    String testdir = System.getProperty("com.taursys.test.dir");
//    assertNotNull(
//        "You must set system property: com.taursys.test.dir to path of MapperXML project directory",
//        testdir);
//    testdir = testdir.replaceAll("\\\\", "/");
//    URL[] urls = new URL[] { new URL("file:///" + testdir + "/lib/xerces.jar"),
//        new URL("file:///" + testdir + "/lib/mapperxml.jar"), };
//    ClassLoader loader = rootLoader(getClass().getClassLoader().getParent());
//    loader = new URLClassLoader(urls, loader);
//    Class clazz = loader
//        .loadClass("com.taursys.dom.DocumentAdapterBuilderFactory");
//    Method method = clazz.getMethod("newInstance", new Class[] {});
//    Object factory = method.invoke(clazz, new Object[] {});
//    method = clazz.getMethod("newDocumentAdapterBuilder", new Class[] {});
//    Object builder = method.invoke(factory, new Object[] {});
//    assertEquals("Builder class", XercesDocumentAdapterBuilder.class.getName(),
//        builder.getClass().getName());
  }

  public void testDocumentAdapterBuilderFactoryDefaultWithoutXercesOrTidy()
      throws Exception {
    defaultUsingClassPathTest(new String[]{}, JAXPDocumentAdapterBuilder.class.getName());

//    String testdir = System.getProperty("com.taursys.test.dir");
//    assertNotNull(
//        "You must set system property: com.taursys.test.dir to path of MapperXML project directory",
//        testdir);
//    testdir = testdir.replaceAll("\\\\", "/");
//    String path = "file:///" + testdir + "/lib/mapperxml.jar";
//    ClassLoader loader = rootLoader(getClass().getClassLoader().getParent());
//    URL[] urls = new URL[] { new URL(path), };
//    loader = new URLClassLoader(urls, loader);
//    Class clazz = loader
//        .loadClass("com.taursys.dom.DocumentAdapterBuilderFactory");
//    Method method = clazz.getMethod("newInstance", new Class[] {});
//    DocumentAdapterBuilderFactory factory = (DocumentAdapterBuilderFactory) method
//        .invoke(clazz, new Object[] {});
//    DocumentAdapterBuilder builder = factory.newDocumentAdapterBuilder();
//    assertEquals("Builder class", JAXPDocumentAdapterBuilder.class.getName(),
//        builder.getClass().getName());
  }

  private ClassLoader rootLoader(ClassLoader currentLoader) {
    if (currentLoader.getParent() == null) {
      return currentLoader;
    } else {
      return rootLoader(currentLoader.getParent());
    }
  }

  private void buildUsingSystemProperty(String builderClassName,
      Class builderClass) throws Exception {
    System.setProperty(
        DocumentAdapterBuilderFactory.SYSTEM_PROPERTY_BUILDER_NAME,
        builderClassName);
    DocumentAdapterBuilderFactory factory = DocumentAdapterBuilderFactory
        .newInstance();
    DocumentAdapterBuilder builder = factory.newDocumentAdapterBuilder();
    assertEquals("Builder class", builderClass, builder.getClass());
  }

  public void testDocumentAdapterBuilderFactoryWithProperty() throws Exception {
    buildUsingSystemProperty(TestDocumentAdapterBuilder.class.getName(),
        TestDocumentAdapterBuilder.class);
  }

  public void testDocumentAdapterBuilderFactoryWithPropertyInvalidClass()
      throws Exception {
    try {
      buildUsingSystemProperty("com.taursys.dom.BogusDocumentAdapterBuilder",
          null);
      fail("Expected DocumentAdapterBuilderException");
    } catch (DocumentAdapterBuilderException e) {
    }
  }

  public void testDocumentAdapterBuilderFactoryFactoryTypeXERCES()
      throws Exception {
    buildUsingSystemProperty(DocumentAdapterBuilderFactory.XERCES_BUILDER,
        XercesDocumentAdapterBuilder.class);
  }

  public void testDocumentAdapterBuilderFactoryFactoryTypeJAXP()
      throws Exception {
    buildUsingSystemProperty(DocumentAdapterBuilderFactory.JAXP_BUILDER,
        JAXPDocumentAdapterBuilder.class);
  }

}