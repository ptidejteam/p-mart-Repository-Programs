/**
 * File   : $Source: /cvsroot/mapper/MapperXML/src/test/ProjectTestSuite.java,v $
 * Date   : $Date: 2004/08/19 15:33:15 $
 *
 * Copyright (c) 2002
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
package test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This test suite has been automatically generated
 * to execute all JUnitTest tests found in the project.
 * To update the list of tests re-run the UnitTest Batch TestRunner again.
 * @author Marty Phelan
 * @version $Revision: 1.13 $
 */
public class ProjectTestSuite extends TestCase {

  public ProjectTestSuite(String _name) {
    super(_name);
  }

  public static Test suite() {
    TestSuite theSuite = new TestSuite();
    theSuite.addTestSuite(com.taursys.dom.test.DOM_1_20000929_DocumentAdapterTest.class);
    theSuite.addTestSuite(com.taursys.html.test.HTMLComponentFactoryTest.class);
    theSuite.addTestSuite(com.taursys.model.test.DefaultSelectModelTest.class);
    theSuite.addTestSuite(com.taursys.model.test.DefaultTextModelTest.class);
    theSuite.addTestSuite(com.taursys.model.test.ObjectArrayValueHolderTest.class);
    theSuite.addTestSuite(com.taursys.model.test.PropertyAccessorTest.class);
    theSuite.addTestSuite(com.taursys.model.test.VOCollectionValueHolderTest.class);
    theSuite.addTestSuite(com.taursys.model.test.VOValueHolderTest.class);
    theSuite.addTestSuite(com.taursys.model.test.VariantValueHolderTest.class);
    theSuite.addTestSuite(com.taursys.model.test.DefaultCheckboxModelTest.class);
    theSuite.addTestSuite(com.taursys.model.test.VOComparatorTest.class);
    theSuite.addTestSuite(com.taursys.servlet.test.ServletFormFactoryTest.class);
    theSuite.addTestSuite(com.taursys.servlet.test.HttpMultiPartServletRequestTest.class);
    theSuite.addTestSuite(com.taursys.servlet.test.ServletFormTest.class);
    theSuite.addTestSuite(com.taursys.tools.util.test.ClassPathTest.class);
    theSuite.addTestSuite(com.taursys.xml.event.test.ParameterDispatcherTest.class);
    theSuite.addTestSuite(com.taursys.xml.event.test.InputDispatcherTest.class);
    theSuite.addTestSuite(com.taursys.xml.event.test.TriggerDispatcherTest.class);
    theSuite.addTestSuite(com.taursys.xml.test.ParameterTest.class);
    theSuite.addTestSuite(com.taursys.xml.test.SelectFieldTest.class);
    theSuite.addTestSuite(com.taursys.xml.test.CheckboxFieldTest.class);
    return theSuite;
  }

  /* Executes the test case */
  public static void main(String[] argv) {
    String[] testCaseList = {ProjectTestSuite.class.getName()};
    junit.swingui.TestRunner.main(testCaseList);
  }
}
