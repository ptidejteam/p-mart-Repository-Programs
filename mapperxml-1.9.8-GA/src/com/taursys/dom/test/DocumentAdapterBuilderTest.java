/**
 * DocumentAdapterBuilderTest
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
package com.taursys.dom.test;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

import com.taursys.dom.DocumentAdapter;
import com.taursys.dom.DocumentAdapterBuilder;
import com.taursys.dom.DocumentAdapterBuilderException;
import com.taursys.dom.DocumentAdapterBuilderFactory;
import com.taursys.dom.test.util.*;

/**
 * DocumentAdapterBuilderTest is test cases for DocumentAdapterBuilder
 * 
 * @author Marty Phelan
 * @version $Revision: 1.3 $
 */
public class DocumentAdapterBuilderTest extends TestCase {
  private DocumentAdapterBuilderFactory factory;
  private DocumentAdapterBuilder builder;

  /**
   * Constructor for DocumentAdapterBuilderTest.
   * @param arg0
   */
  public DocumentAdapterBuilderTest(String arg0) {
    super(arg0);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(DocumentAdapterBuilderTest.class);
  }

  /*
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
    System.setProperty(DocumentAdapterBuilderFactory.SYSTEM_PROPERTY_BUILDER_NAME,
        TestDocumentAdapterBuilder.class.getName());
    factory = DocumentAdapterBuilderFactory.newInstance();
    builder = factory.newDocumentAdapterBuilder();
  }

  /*
   * Test for DocumentAdapter build(Class, String)
   */
  public void testBuildClassString() {
    try {
      builder.build(getClass(), "data/testdoc3.xml");
    } catch (DocumentAdapterBuilderException e) {
      fail(e.getMessage());
    }
  }

  /*
   * Test for DocumentAdapter build(URL)
   */
  public void testBuildURL() throws MalformedURLException {
    try {
      String testdir = System.getProperty("com.taursys.test.dir");
      assertNotNull(
          "You must set system property: com.taursys.test.dir to path of MapperXML project directory",
          testdir);
      testdir = testdir.replaceAll("\\\\","/");
      String path = "file:///" + testdir 
          + "/src/com/taursys/dom/test/data/testdoc3.xml";
      DocumentAdapter da = builder.build(new URL(path));
    } catch (DocumentAdapterBuilderException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /*
   * Test for DocumentAdapter build(String uri)
   */
  public void testBuildStringURIResourceQualified() {
    try {
      String uri = "resource://" + this.getClass().getName() + 
          "/com/taursys/dom/test/data/testdoc3.xml";
      builder.build(uri);
    } catch (DocumentAdapterBuilderException e) {
      fail(e.getMessage());
    }
  }

  /*
   * Test for DocumentAdapter build(String uri)
   */
  public void testBuildStringURIResourceRelative() {
    try {
      String uri = "resource://" + 
          "/com/taursys/dom/test/data/testdoc3.xml";
      builder.build(uri);
    } catch (DocumentAdapterBuilderException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

}
