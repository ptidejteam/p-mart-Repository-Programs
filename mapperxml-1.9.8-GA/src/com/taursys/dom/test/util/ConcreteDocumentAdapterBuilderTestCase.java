/**
 * ConcreteDocumentAdapterBuilderTestCase
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
package com.taursys.dom.test.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;

import junit.framework.TestCase;

import com.taursys.dom.DocumentAdapter;
import com.taursys.dom.DocumentAdapterBuilder;
import com.taursys.dom.DocumentAdapterBuilderException;
import com.taursys.dom.DocumentAdapterBuilderFactory;

/**
 * ConcreteDocumentAdapterBuilderTestCase is superclass for all
 * test cases for DocumentAdapterBuilders.
 * 
 * @author Marty Phelan
 * @version $Revision: 1.3 $
 */
public class ConcreteDocumentAdapterBuilderTestCase extends TestCase {
  private DocumentAdapterBuilder builder;
  protected DocumentAdapterBuilderFactory factory;

  /**
   * Constructs new test case
   * @param arg0
   */
  public ConcreteDocumentAdapterBuilderTestCase(String arg0) {
    super(arg0);
  }
  
  private void copyReplace(String srcFile, String dstFile, String token, String value) throws IOException {
    BufferedReader r = new BufferedReader(new FileReader(srcFile));
    PrintWriter w = new PrintWriter(new FileWriter(dstFile));
    for(String line = r.readLine(); line != null; line = r.readLine()) {
      w.println(line.replaceAll(token, value));
    }
    w.close();
    r.close();
  }

  private void createValidatingBuilder() throws Exception {
    factory.setValidating(true);
    builder = factory.newDocumentAdapterBuilder();
  }

  private void createNonValidatingBuilder() throws Exception {
    factory.setValidating(false);
    builder = factory.newDocumentAdapterBuilder();
  }

  private String getProjectBuildTestDataPath(String doc) {
    String testdir = System.getProperty("com.taursys.test.dir");
    assertNotNull(
        "You must set system property: com.taursys.test.dir to path of MapperXML project directory",
        testdir);
    testdir = testdir.replaceAll("\\\\","/");
    return testdir + "/build/classes/com/taursys/dom/test/data/" + doc;
  }

  private String getProjectSourceTestDataPath(String doc) {
    String testdir = System.getProperty("com.taursys.test.dir");
    assertNotNull(
        "You must set system property: com.taursys.test.dir to path of MapperXML project directory",
        testdir);
    testdir = testdir.replaceAll("\\\\","/");
    return testdir + "/src/com/taursys/dom/test/data/" + doc;
  }
  /*
   * Test for DocumentAdapter build(InputStream)
   * Validating builder
   * Valid XML - Valid Document - Good DTD
   */
  public void testBuildInputStreamValidateGoodDTD() throws Exception {
    createValidatingBuilder();
    String tmpPath = System.getProperty("user.home") + "/testdoc1.xml";
    copyReplace(getProjectSourceTestDataPath("testdoc1.xml"), tmpPath, "testdoc1.dtd",
        "file:///" + getProjectSourceTestDataPath("testdoc1.dtd"));
    InputStream is = new FileInputStream(tmpPath);
    DocumentAdapter da = builder.build(is);
    assertNotNull("DocumentAdapter was null", da);
  }

  /*
   * Test for DocumentAdapter build(InputStream)
   * Validating builder
   * Valid XML - Valid Document - Bad DTD
   */
  public void testBuildInputStreamValidateBadDTD() throws Exception {
    createValidatingBuilder();
    InputStream is = new FileInputStream(getProjectSourceTestDataPath("testdoc2.xml"));
    try {
      DocumentAdapter da = builder.build(is);
      fail("Expected DocumentAdapterBuilderException");      
    } catch (DocumentAdapterBuilderException e) {
    }
  }

  /*
   * Test for DocumentAdapter build(InputStream)
   * Validating builder
   * Valid XML - Valid Document - Missing DTD
   */
  public void testBuildInputStreamValidateNoDTD() throws Exception {
    createValidatingBuilder();
    InputStream is = new FileInputStream(getProjectSourceTestDataPath("testdoc3.xml"));
    try {
      DocumentAdapter da = builder.build(is);
      fail("Expected DocumentAdapterBuilderException");
    } catch (DocumentAdapterBuilderException e) {
    }
  }

  /*
   * Test for DocumentAdapter build(InputStream)
   * Validating builder
   * Valid XML - Valid Document - Good DTD
   */
  public void testBuildInputStreamNoValidateGoodDTD() throws Exception {
    createNonValidatingBuilder();
    InputStream is = new FileInputStream(getProjectSourceTestDataPath("testdoc1.xml"));
    DocumentAdapter da = builder.build(is);
    assertNotNull("DocumentAdapter was null", da);
  }

  /*
   * Test for DocumentAdapter build(InputStream)
   * Non-validating builder
   * Valid XML - Valid Document - Bad DTD
   */
  public void testBuildInputStreamNoValidateBadDTD() throws Exception {
    createNonValidatingBuilder();
    InputStream is = new FileInputStream(getProjectSourceTestDataPath("testdoc2.xml"));
    DocumentAdapter da = builder.build(is);
    assertNotNull("DocumentAdapter was null", da);
  }

  /*
   * Test for DocumentAdapter build(InputStream)
   * Non-validating builder
   * Valid XML - Valid Document - Missing DTD
   */
  public void testBuildInputStreamNoValidateNoDTD() throws Exception {
    createNonValidatingBuilder();
    InputStream is = new FileInputStream(getProjectSourceTestDataPath("testdoc3.xml"));
    DocumentAdapter da = builder.build(is);
    assertNotNull("DocumentAdapter was null", da);
  }

  /*
   * Test for DocumentAdapter build(InputStream)
   * Non-validating builder
   * Invalid XML - Invalid Document - Missing DTD
   */
  public void testBuildInputStreamNoValidateMalformed() throws Exception {
    createNonValidatingBuilder();
    InputStream is = new FileInputStream(getProjectSourceTestDataPath("testdoc4.xml"));
    try {
      DocumentAdapter da = builder.build(is);
      fail("Expected DocumentAdapterBuilderException");      
    } catch (DocumentAdapterBuilderException e) {
    }
  }

  /*
   * Test for DocumentAdapter build(InputStream) Validating builder Valid XML -
   * Invalid Document - Good DTD
   */
  public void testBuildInputStreamValidateWeirdTag() throws Exception {
    createValidatingBuilder();
    String tmpPath = System.getProperty("user.home") + "/testdoc6.xml";
    copyReplace(getProjectSourceTestDataPath("testdoc6.xml"), tmpPath, "testdoc1.dtd",
        "file:///" + getProjectSourceTestDataPath("testdoc1.dtd"));
    InputStream is = new FileInputStream(tmpPath);
    try {
      DocumentAdapter da = builder.build(is);
      fail("Expected DocumentAdapterBuilderException");
    } catch (DocumentAdapterBuilderException e) {
    }
  }

  /*
   * Test for DocumentAdapter build(Class, String)
   * Validating builder
   * Valid XML - Valid Document - Missing DTD
   */
  public void testBuildClassResourceNoDTD() throws Exception {
    createValidatingBuilder();
    try {
      DocumentAdapter da = builder.build(getClass(), "data/testdoc3.xml");
      fail("Expected DocumentAdapterBuilderException");
    } catch (DocumentAdapterBuilderException e) {
    }
  }

  /*
   * Test for DocumentAdapter build(Class, String)
   * Validating builder
   * Valid XML - Valid Document - Good DTD
   */
  public void testBuildClassResourceDTD() throws Exception {
    createValidatingBuilder();
    copyReplace(getProjectSourceTestDataPath("testdoc1.xml"), getProjectBuildTestDataPath("testdoc1.xml"), "testdoc1.dtd",
        "file:///" + getProjectSourceTestDataPath("testdoc1.dtd"));
    DocumentAdapter da = builder.build(getClass(), "data/testdoc1.xml");
    assertNotNull("DocumentAdapter was null", da);
  }

  /*
   * Test for DocumentAdapter build(URL)
   * Validating builder
   * Valid XML - Valid Document - Missing DTD
   */
  public void testBuildURLNoDTD() throws Exception {
    createValidatingBuilder();
    try {
      DocumentAdapter da = builder.build(new URL("file:///" + getProjectSourceTestDataPath("testdoc3.xml")));
      fail("Expected DocumentAdapterBuilderException");
    } catch (DocumentAdapterBuilderException e) {
    }
  }

  /*
   * Test for DocumentAdapter build(URL)
   * Validating builder
   * Valid XML - Valid Document - Good DTD
   */
  public void testBuildURLDTD() throws Exception {
    createValidatingBuilder();
    String tmpPath = System.getProperty("user.home") + "/testdoc1.xml";
    copyReplace(getProjectSourceTestDataPath("testdoc1.xml"), tmpPath, "testdoc1.dtd",
        "file:///" + getProjectSourceTestDataPath("testdoc1.dtd"));
    DocumentAdapter da = builder.build(new URL("file:///" + tmpPath));
    assertNotNull("DocumentAdapter was null", da);
  }

  /*
   * Test for DocumentAdapter build(URL)
   * Validating builder
   * Valid XML - Valid XHTML Document - Good DTD
   */
  public void testBuildURLXHtmlDTD() throws Exception {
    createValidatingBuilder();
    DocumentAdapter da = builder.build(new URL("file:///" + getProjectSourceTestDataPath("testdoc5.xhtml")));
    assertNotNull("DocumentAdapter was null", da);
  }

}
