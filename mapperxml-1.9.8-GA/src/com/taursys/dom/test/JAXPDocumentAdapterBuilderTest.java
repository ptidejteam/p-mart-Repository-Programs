/**
 * JAXPDocumentAdapterBuilderTest
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

import com.taursys.dom.DocumentAdapterBuilderFactory;
import com.taursys.dom.test.util.ConcreteDocumentAdapterBuilderTestCase;

/**
 * JAXPDocumentAdapterBuilder Tests
 * 
 * @author marty
 * @version $Revision: 1.3 $
 */
public class JAXPDocumentAdapterBuilderTest 
    extends ConcreteDocumentAdapterBuilderTestCase {

  /**
   * Constructor for JAXPDocumentAdapterBuilderTest.
   * @param arg0
   */
  public JAXPDocumentAdapterBuilderTest(String arg0) {
    super(arg0);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(JAXPDocumentAdapterBuilderTest.class);
  }

  /*
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
    System.setProperty(DocumentAdapterBuilderFactory.SYSTEM_PROPERTY_BUILDER_NAME,
        DocumentAdapterBuilderFactory.JAXP_BUILDER);
    factory = DocumentAdapterBuilderFactory.newInstance();
  }
  
  /**
   * Dummy test so eclipse JUnit will include in suite for package
   * @throws Exception
   */
  public void testDummy() throws Exception {
  }
}
