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
package com.taursys.dom.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import junit.framework.TestCase;

import com.taursys.dom.DOM_2_20001113_DocumentAdapter;
import com.taursys.dom.DocumentAdapter;
import com.taursys.dom.DocumentAdapterBuilder;
import com.taursys.dom.DocumentAdapterBuilderFactory;
import com.taursys.dom.Tidy_DOM_2_20001113_DocumentAdapter;

/**
 * DOMDocumentAdapterFactoryTest is unit test for DOMDocumentAdapterFactory
 * @author marty
 */
public class DOMDocumentAdapterFactoryTest extends TestCase {

  /*
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
  }

  /**
   * Constructor for DOMDocumentAdapterFactoryTest.
   * @param arg0
   */
  public DOMDocumentAdapterFactoryTest(String arg0) {
    super(arg0);
  }

  protected byte[] getHTMLDoc1Bytes() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(bos);
    writer.println("<html>");
    writer.println("  <head>");
    writer.println("    <title>Test</title>");
    writer.println("  </head>");
    writer.println("  <body>");
    writer.println("    <h1>Test</h1>");
    writer.println("    <p id=\"p1\">Test</p>");
    writer.println("    <p id=\"p2\">Test</p>");
    writer.println("  </body>");
    writer.println("</html>");
    writer.flush();
    writer.close();
    return bos.toByteArray();
  }

  protected DocumentAdapter getDocumentAdapterUsingXerces(byte[] bytes) throws Exception {
    System.setProperty(DocumentAdapterBuilderFactory.SYSTEM_PROPERTY_BUILDER_NAME,
        DocumentAdapterBuilderFactory.XERCES_BUILDER);
    DocumentAdapterBuilderFactory factory = DocumentAdapterBuilderFactory.newInstance();
    DocumentAdapterBuilder builder = factory.newDocumentAdapterBuilder();
    DocumentAdapter adapt = builder.build(new ByteArrayInputStream(bytes));
    return adapt;
  }

  protected DocumentAdapter getDocumentAdapterUsingTidy(byte[] bytes) throws Exception {
    System.setProperty(DocumentAdapterBuilderFactory.SYSTEM_PROPERTY_BUILDER_NAME,
        DocumentAdapterBuilderFactory.TIDY_BUILDER);
    DocumentAdapterBuilderFactory factory = DocumentAdapterBuilderFactory.newInstance();
    DocumentAdapterBuilder builder = factory.newDocumentAdapterBuilder();
    DocumentAdapter adapt = builder.build(new ByteArrayInputStream(bytes));
    return adapt;
  }

  public void testNewDOMDocumentAdapterUsingXerces() throws Exception {
    DocumentAdapter da = getDocumentAdapterUsingXerces(getHTMLDoc1Bytes());
    assertEquals("DocumentAdapterClass", DOM_2_20001113_DocumentAdapter.class,
        da.getClass());
  }

  public void testNewDOMDocumentAdapterUsingTidy() throws Exception {
    DocumentAdapter da = getDocumentAdapterUsingTidy(getHTMLDoc1Bytes());
    assertEquals("DocumentAdapterClass", Tidy_DOM_2_20001113_DocumentAdapter.class,
        da.getClass());
  }

}
