/**
 * ElementDelegateTest - Unit tests for ElementDelegate
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
package com.taursys.xml.test;

import com.taursys.model.ModelException;
import com.taursys.model.VOCollectionValueHolder;
import com.taursys.model.VOValueHolder;
import com.taursys.util.DataTypes;
import com.taursys.xml.Attribute;
import com.taursys.xml.Button;
import com.taursys.xml.Component;
import com.taursys.xml.DocumentElement;
import com.taursys.xml.ElementDelegate;
import com.taursys.xml.Template;
import com.taursys.xml.TextField;

import junit.framework.TestCase;

/**
 * Unit tests for ElementDelegate
 * @author Marty
 */
public class ElementDelegateTest extends TestCase {

  /*
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
  }

  /**
   * Constructor for ElementDelegateTest.
   * @param arg0
   */
  public ElementDelegateTest(String arg0) {
    super(arg0);
  }

  public void testGetParentForDocumentElement() {
    DocumentElement element = new DocumentElement();
    ElementDelegate delegate = element.getElementDelegate();
    assertEquals("Parent", element, delegate.getParent());
  }

  public void testGetParentForTextField() {
    DocumentElement container = new DocumentElement();
    TextField field = new TextField();
    container.add(field);
    ElementDelegate delegate = field.getElementDelegate();
    assertEquals("Parent", container, delegate.getParent());
  }

  public void testGetId() {
    DocumentElement element = new DocumentElement();
    element.setId("testId");
    ElementDelegate delegate = element.getElementDelegate();
    assertEquals("Id", "testId", delegate.getId());
  }

  public void testSetId() {
    DocumentElement element = new DocumentElement();
    element.setId("testId");
    ElementDelegate delegate = element.getElementDelegate();
    delegate.setId("badId");
    assertEquals("Id", "testId", delegate.getId());
  }

  public void testAddAttribute() {
    DocumentElement element = new DocumentElement();
    Attribute attribute = new Attribute();
    element.addAttribute(attribute);
    ElementDelegate delegate = element.getElementDelegate();
    Component[] components = delegate.getComponents();
    assertEquals(attribute, components[0]);
  }

  public void testAddAttributeToButton() {
    Button button = new Button();
    Attribute attribute = new Attribute();
    button.addAttribute(attribute);
    ElementDelegate delegate = button.getElementDelegate();
    Component[] components = delegate.getComponents();
    assertEquals(attribute, components[0]);
  }

  public void testGetAttribute() {
    DocumentElement element = new DocumentElement();
    Attribute a = new Attribute();
    element.addAttribute(a);
    a.setAttributeName("style");
    assertNotNull(element.getAttribute("style"));
    assertEquals(a, element.getAttribute("style"));
  }

  public void testGetAttributeText() throws ModelException {
    DocumentElement container = new DocumentElement();
    TextField field = new TextField();
    container.add(field);
    Attribute attribute = field.createAttribute("style");
    assertNotNull(attribute);
    attribute.setText("whatever");
    assertEquals("whatever", field.getAttributeText("style"));
  }

  public void testSetAttributeText() throws ModelException {
    DocumentElement container = new DocumentElement();
    TextField field = new TextField();
    container.add(field);
    Attribute attribute = field.createAttribute("style");
    assertNotNull(attribute);
    field.setAttributeText("style","whatever");
    assertEquals("whatever", attribute.getText());
  }

  public void testGetAttributeValue() throws ModelException {
    DocumentElement container = new DocumentElement();
    TextField field = new TextField();
    container.add(field);
    Attribute attribute = field.createAttribute("style");
    assertNotNull(attribute);
    attribute.setText("whatever");
    assertEquals("whatever", field.getAttributeValue("style"));
  }

  public void testSetAttributeValue() throws ModelException {
    DocumentElement container = new DocumentElement();
    TextField field = new TextField();
    container.add(field);
    Attribute attribute = field.createAttribute("style");
    assertNotNull(attribute);
    field.setAttributeValue("style","whatever");
    assertEquals("whatever", attribute.getText());
  }

  /*
   * Class under test for Attribute createAttribute(String)
   */
  public void testCreateAttribute() throws ModelException {
    DocumentElement container = new DocumentElement();
    TextField field = new TextField();
    container.add(field);
    Attribute attribute = field.createAttribute("style");
    assertNotNull(attribute);
    ElementDelegate delegate = field.getElementDelegate();
    Component[] components = delegate.getComponents();
    assertEquals("style", attribute.getAttributeName());
    assertEquals(attribute, components[1]);
  }

  /*
   * Class under test for Attribute createAttribute(String, int)
   */
  public void testCreateAttributeDate() throws ModelException {
    DocumentElement container = new DocumentElement();
    TextField field = new TextField();
    container.add(field);
    Attribute attribute = field.createAttribute("style", DataTypes.TYPE_DATE);
    assertNotNull(attribute);
    assertEquals(DataTypes.TYPE_DATE, attribute.getValueHolder().getJavaDataType("value"));
    ElementDelegate delegate = field.getElementDelegate();
    Component[] components = delegate.getComponents();
    assertEquals("style", attribute.getAttributeName());
    assertEquals(attribute, components[1]);
  }

  /*
   * Class under test for Attribute createBoundAttribute(String, String)
   */
  public void testCreateBoundAttributeForTextField() throws ModelException {
    DocumentElement container = new DocumentElement();
    TextField field = new TextField();
    container.add(field);
    Attribute attribute = field.createBoundAttribute("style","value");
    field.setText("something");
    ElementDelegate delegate = field.getElementDelegate();
    Component[] components = delegate.getComponents();
    assertEquals("style", attribute.getAttributeName());
    assertEquals(attribute, components[1]);
    assertEquals("something", attribute.getText());
  }

  /*
   * Class under test for Attribute createBoundAttribute(String, String)
   */
  public void testCreateBoundAttributeForTemplate() throws ModelException {
    DocumentElement container = new DocumentElement();
    Template field = new Template();
    container.add(field);
    VOCollectionValueHolder holder = new VOCollectionValueHolder();
    holder.add(new TestVO("Phelan", "Marty"));
    field.setCollectionValueHolder(holder);
    Attribute attribute = field.createBoundAttribute("style","lastName");
    holder.next();
    ElementDelegate delegate = field.getElementDelegate();
    Component[] components = delegate.getComponents();
    assertEquals("style", attribute.getAttributeName());
    assertEquals(attribute, components[0]);
    assertEquals(holder, attribute.getValueHolder());
    assertEquals("lastName", attribute.getPropertyName());
    assertEquals("Phelan", attribute.getText());
  }

  /*
   * Class under test for Attribute createAttribute(String, String, ValueHolder)
   */
  public void testCreateAttributeExternalValueHolder() throws ModelException {
    DocumentElement container = new DocumentElement();
    Template field = new Template();
    container.add(field);
    VOCollectionValueHolder holder = new VOCollectionValueHolder();
    holder.add(new TestVO("Phelan", "Marty"));
    Attribute attribute = field.createAttribute("style","lastName", holder);
    assertNotNull(attribute);
    holder.next();
    ElementDelegate delegate = field.getElementDelegate();
    Component[] components = delegate.getComponents();
    assertEquals("style", attribute.getAttributeName());
    assertEquals(attribute, components[0]);
    assertEquals(holder, attribute.getValueHolder());
    assertEquals("lastName", attribute.getPropertyName());
    assertEquals("Phelan", attribute.getText());
  }

  /*
   * Class under test for void removeAttribute(String)
   */
  public void testRemoveAttributeBound() throws ModelException {
    DocumentElement container = new DocumentElement();
    TextField field = new TextField();
    container.add(field);
    Attribute attribute = field.createBoundAttribute("style","lastName");
    field.setText("something");
    VOValueHolder holder = new VOValueHolder();
    holder.setValueObject(new TestVO("Phelan", "Marty"));
    field.setValueHolder(holder);

    field.removeAttribute("style");
    
    ElementDelegate delegate = field.getElementDelegate();
    Component[] components = delegate.getComponents();
    assertEquals(1, components.length);
    assertEquals(attribute.getValueHolder(), holder);
  }
  
  /*
   * Class under test for void removeAttribute(Attribute)
   */
  public void testRemoveAttributeAttribute() throws ModelException {
    DocumentElement container = new DocumentElement();
    TextField field = new TextField();
    container.add(field);
    Attribute attribute = field.createAttribute("style");
    field.removeAttribute(attribute);
    ElementDelegate delegate = field.getElementDelegate();
    Component[] components = delegate.getComponents();
    assertEquals(1, components.length);
  }


  public void testSetValueHolder() throws ModelException {
    DocumentElement container = new DocumentElement();
    TextField field = new TextField();
    container.add(field);
    Attribute attribute = field.createBoundAttribute("style","lastName");
    field.setText("something");
    VOValueHolder holder = new VOValueHolder();
    holder.setValueObject(new TestVO("Phelan", "Marty"));
    field.setValueHolder(holder);
    ElementDelegate delegate = field.getElementDelegate();
    Component[] components = delegate.getComponents();
    assertEquals("style", attribute.getAttributeName());
    assertEquals(attribute, components[1]);
    assertEquals("Phelan", attribute.getText());
  }
  
  public void testAddDuplicateAttribute() {
    DocumentElement container = new DocumentElement();
    TextField field = new TextField();
    container.add(field);
    Attribute first = new Attribute();
    first.setAttributeName("style");
    Attribute duplicate = new Attribute();
    duplicate.setAttributeName("style");
    field.addAttribute(first);
    field.addAttribute(duplicate);
    ElementDelegate delegate = field.getElementDelegate();
    Component[] components = delegate.getComponents();
    assertEquals(2, components.length);
    assertEquals("Attribute", first, components[1]);
  }

  public void testCreateDuplicateAttribute() {
    DocumentElement container = new DocumentElement();
    TextField field = new TextField();
    container.add(field);
    Attribute first = new Attribute();
    first.setAttributeName("style");
    field.addAttribute(first);
    field.createAttribute("style");
    ElementDelegate delegate = field.getElementDelegate();
    Component[] components = delegate.getComponents();
    assertEquals(2, components.length);
    assertEquals("Attribute", first, components[1]);
  }

  public void testCreateBoundDuplicateAttribute() {
    DocumentElement container = new DocumentElement();
    TextField field = new TextField();
    container.add(field);
    Attribute first = new Attribute();
    first.setAttributeName("style");
    field.addAttribute(first);
    field.createBoundAttribute("style","style");
    ElementDelegate delegate = field.getElementDelegate();
    Component[] components = delegate.getComponents();
    assertEquals(2, components.length);
    assertEquals("Attribute", first, components[1]);
  }

}
