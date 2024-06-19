/**
 * DOMDocumentAdapterAcceptanceTest
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

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

import com.taursys.debug.Debug;
import com.taursys.dom.DocumentAdapter;
import com.taursys.dom.DocumentAdapterBuilder;
import com.taursys.dom.DocumentAdapterBuilderException;
import com.taursys.dom.DocumentAdapterBuilderFactory;

/**
 * DOMDocumentAdapterAcceptanceTest is 
 * 
 * @author Marty Phelan
 * @version $Revision: 1.1 $
 */
public class DOMDocumentAdapterAcceptanceTest extends TestCase {

  /**
   * Constructor for DOMDocumentAdapterAcceptanceTest.
   * @param arg0
   */
  public DOMDocumentAdapterAcceptanceTest(String arg0) {
    super(arg0);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(DOMDocumentAdapterAcceptanceTest.class);
  }

  /*
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
  }

  /**
   * Test that a DocumentAdapter can be successfully built
   */
  public void testBuildADocumentAdapter() {
    try {
      DocumentAdapterBuilderFactory factory =
        DocumentAdapterBuilderFactory.newInstance();
      DocumentAdapterBuilder builder = factory.newDocumentAdapterBuilder();
      String url =
        "file://"
          + System.getProperty("user.home")
          + "/eclipse/workspace/MapperXML/src"
          + "/com/taursys/dom/test/data/testdoc3.xml";
      DocumentAdapter da = builder.build(new URL(url));
      Debug.debug("DA=" + da.getClass());
    } catch (DocumentAdapterBuilderException e) {
      Debug.debug("Failure", e);
      fail(e.getMessage());
    } catch (MalformedURLException e) {
      Debug.debug("Failure", e);
      fail(e.getMessage());
    }
  }

}
