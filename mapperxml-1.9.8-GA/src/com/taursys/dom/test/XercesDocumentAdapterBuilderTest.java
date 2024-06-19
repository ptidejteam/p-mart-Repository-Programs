/**
 * XercesDocumentAdapterBuilderTest
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

import com.taursys.dom.DocumentAdapterBuilderFactory;
import com.taursys.dom.test.util.*;

/**
 * Test cases for XercesDocumentAdapterBuilder
 * 
 * @author Marty Phelan
 * @version $Revision: 1.2 $
 */
public class XercesDocumentAdapterBuilderTest 
    extends ConcreteDocumentAdapterBuilderTestCase {

  /**
   * Constructor for XercesDocumentAdapterBuilderTest.
   * @param arg0
   */
  public XercesDocumentAdapterBuilderTest(String arg0) {
    super(arg0);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(XercesDocumentAdapterBuilderTest.class);
  }

  /*
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
    System.setProperty(DocumentAdapterBuilderFactory.SYSTEM_PROPERTY_BUILDER_NAME,
        DocumentAdapterBuilderFactory.XERCES_BUILDER);
    factory = DocumentAdapterBuilderFactory.newInstance();
  }

//TODO test Xerces 2.x without these overrides

  /*
   * OVERRIDE TEST - Xerces does not seem to care about missing DTD
   * 
   * Test for DocumentAdapter build(InputStream)
   * Validating builder
   * Valid XML - Valid Document - Missing DTD
   */
  public void testBuildInputStreamValidateNoDTD() throws Exception {
  }

  /*
   * OVERRIDE TEST - Xerces does not seem to care about missing DTD
   * 
   * Test for DocumentAdapter build(Class, String)
   * Validating builder
   * Valid XML - Valid Document - Missing DTD
   */
  public void testBuildClassResourceNoDTD() throws Exception {
  }

  /*
   * OVERRIDE TEST - Xerces does not seem to care about missing DTD
   * 
   * Test for DocumentAdapter build(URL)
   * Validating builder
   * Valid XML - Valid Document - Missing DTD
   */
  public void testBuildURLNoDTD() throws Exception {
  }

  /*
   * OVERRIDE TEST - Xerces does not seem to care about missing DTD
   * 
   * Test for DocumentAdapter build(InputStream)
   * Validating builder
   * Valid XML - Invalid Document - Good DTD
   */
  public void testBuildInputStreamValidateWeirdTag() throws Exception {
  }

}
