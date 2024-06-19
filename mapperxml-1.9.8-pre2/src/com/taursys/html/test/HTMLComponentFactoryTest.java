/**
 * HTMLComponentFactoryTest - Tests for HTMLComponentFactory
 *
 * Copyright (c) 2000-2004
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
package com.taursys.html.test;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;

import junit.framework.TestCase;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.taursys.debug.Debug;
import com.taursys.debug.SimpleLogger;
import com.taursys.dom.DOM_1_20000929_DocumentAdapter;
import com.taursys.dom.DocumentAdapter;
import com.taursys.html.HTMLAnchorURL;
import com.taursys.html.HTMLCheckBox;
import com.taursys.html.HTMLComponentFactory;
import com.taursys.html.HTMLInputText;
import com.taursys.model.CollectionValueHolder;
import com.taursys.model.VOCollectionValueHolder;
import com.taursys.model.VOListValueHolder;
import com.taursys.model.VOValueHolder;
import com.taursys.model.ValueHolder;
import com.taursys.util.EmptyMessageFormat;
import com.taursys.xml.AbstractField;
import com.taursys.xml.Attribute;
import com.taursys.xml.BoundDocumentElement;
import com.taursys.xml.Button;
import com.taursys.xml.CheckboxField;
import com.taursys.xml.Component;
import com.taursys.xml.Container;
import com.taursys.xml.DocumentElement;
import com.taursys.xml.Template;
import com.taursys.xml.TextField;
import com.taursys.xml.Trigger;

/**
 * JUnitTest case for class: com.taursys.html.HTMLComponentFactory
 * 
 * @author marty
 */
public class HTMLComponentFactoryTest extends TestCase {
  private TestContainer container;
  private TestFactory factory = null;
  private VOValueHolder personHolder;
  private VOCollectionValueHolder invoiceHolder;
  private VOValueHolder unnamedHolder;
  private ValueHolder[] holders;
  private VOListValueHolder level1;
  private VOListValueHolder level2;
  private VOListValueHolder level3;
  private ValueHolder[] levelHolders;

  private static final boolean WITH_BUTTON = true;
  private static final boolean WITHOUT_BUTTON = false;

  public HTMLComponentFactoryTest(String _name) {
    super(_name);
  }

  // ==========================================================================
  //                     Test Setup Methods
  // ==========================================================================

  /* setUp method for test case */
  protected void setUp() {
    // Set default logging
    Debug.setLoggerAdapter(new SimpleLogger(Debug.INFO));
    container = new TestContainer();
    factory = new TestFactory();
    personHolder = new VOValueHolder();
    personHolder.setAlias("Person");
    invoiceHolder = new VOCollectionValueHolder();
    invoiceHolder.setAlias("Invoice");
    unnamedHolder = new VOValueHolder();
    unnamedHolder.setAlias(null);
    holders = new ValueHolder[] { personHolder, invoiceHolder, unnamedHolder };
    level1 = new VOListValueHolder();
    level2 = new VOListValueHolder();
    level3 = new VOListValueHolder();
    level1.setAlias("Level1");
    level2.setAlias("Level2");
    level3.setAlias("Level3");
    levelHolders = new ValueHolder[] { level1, level2, level3 };

  }

  protected void setupTestPage(String fileName) throws Exception {
    DOMParser parser = new DOMParser();
    InputSource is = new InputSource(getClass().getResourceAsStream(fileName));
    parser.parse(is);
    container.setDocument(parser.getDocument());
  }

  protected void setupTestPage1WithButton() throws Exception {
    setupTestPage("TestPage1.html");
    Button button = new Button();
    button.setId("SaveButton");
    button.setParameter("action");
    button.setText("Save");
    container.add(button);
  }

  protected void setupTestPage1WithButtonAndIssueDate() throws Exception {
    setupTestPage1WithButton();
    TextField field = new TextField();
    field.setId("Invoice__issueDate");
    field.setValueHolder(invoiceHolder);
    field.setPropertyName("issueDate");
    field.setFormat(new SimpleDateFormat());
    field.setFormatPattern("MM/dd/yyyy");
    container.add(field);
  }

  // ==========================================================================
  //                     Generic Measurement Methods
  // ==========================================================================

  private String measureAbstractField(Class clazz, Component component,
      Container parent, int i, String id, ValueHolder holder, String name,
      String parameter, Class formatClazz, String formatPattern) {
    String identity = "Component=" + id + " index=" + i + " property=";
    assertNotNull(identity + "self (expected NOT null)", component);
    assertEquals(identity + "class", clazz, component.getClass());
    assertEquals(identity + "id", id, ((AbstractField) component).getId());
    if (holder != null) {
      assertEquals(identity + "valueHolder", holder,
          ((AbstractField) component).getValueHolder());
    }
    assertEquals(identity + "propertyName", name, ((AbstractField) component)
        .getPropertyName());
    assertEquals(identity + "parameter", parameter, ((AbstractField) component)
        .getParameter());
    assertEquals(identity + "parent", parent, component.getParent());
    if (formatClazz == null) {
      assertNull(identity + "format (expected NULL)",
          ((AbstractField) component).getFormat());
    } else {
      assertNotNull(identity + "format (expected NOT null)",
          ((AbstractField) component).getFormat());
      assertEquals(identity + "formatClass", formatClazz,
          ((AbstractField) component).getFormat().getClass());
    }
    assertEquals(identity + "formatPattern", formatPattern,
        ((AbstractField) component).getFormatPattern());
    return identity;
  }

  private String measureBoundDocumentElement(Class clazz, Component component,
      Container parent, int i, String id, ValueHolder holder) {
    String identity = "Component=" + id + " index=" + i + " ";
    assertNotNull(identity + "(expected NOT null)", component);
    assertEquals(identity + "class", clazz, component.getClass());
    BoundDocumentElement bde = ((BoundDocumentElement) component);
    assertEquals(identity + "id", id, bde.getId());
    if (holder != null) {
      assertEquals(identity + "valueHolder", holder, bde.getValueHolder());
    }
    assertEquals(identity + "parent", parent, component.getParent());
    return identity;
  }

  private String measureTemplate(Component component, Container parent, int i,
      String id, CollectionValueHolder holder) {
    String identity = "Component=" + id + " index=" + i + " ";
    assertNotNull(identity + "(expected NOT null)", component);
    assertEquals(identity + "class", Template.class, component.getClass());
    Template bde = ((Template) component);
    assertEquals(identity + "id", id, bde.getId());
    if (holder != null) {
      assertEquals(identity + "valueHolder", holder, bde
          .getCollectionValueHolder());
    }
    assertEquals(identity + "parent", parent, component.getParent());
    return identity;
  }

  private String measureAttribute(Attribute attribute, ValueHolder holder,
      String attributeName, String propertyName) {
    String identity = "Attribute=" + attributeName + " ";
    assertNotNull(identity + "(expected NOT null)", attribute);
    if (holder != null) {
      assertEquals(identity + "valueHolder", holder, attribute.getValueHolder());
    }
    assertEquals(identity + "propertyName", propertyName, attribute
        .getPropertyName());
    return identity;
  }

  private String measureCheckboxField(Class clazz, Component component,
      Container parent, int i, String id, ValueHolder holder, String name,
      String parameter, Class formatClazz, String formatPattern,
      String selectedValue, String unselectedValue) {
    String identity = measureAbstractField(clazz, component, parent, i, id,
        holder, name, parameter, formatClazz, formatPattern);
    assertEquals(identity + "selectedValue", selectedValue,
        ((CheckboxField) component).getSelectedValue());
    assertEquals(identity + "unselectedValue", unselectedValue,
        ((CheckboxField) component).getUnselectedValue());
    return identity;
  }

  // ==========================================================================
  //              Specific Component Measurement Methods
  // ==========================================================================

  public void measureAreaInfo_NODE(Component component, Container parent, int i) {
    measureBoundDocumentElement(BoundDocumentElement.class, component, parent,
        i, "Person__NODE", personHolder);
    BoundDocumentElement bde = (BoundDocumentElement) component;
    measureAttribute(bde.getAttribute("src"), personHolder, "src",
        "cameraMapUrl");
    measureAttribute(bde.getAttribute("width"), personHolder, "width",
        "mapWidth");
    measureAttribute(bde.getAttribute("height"), personHolder, "height",
        "mapHeight");
    measureAttribute(bde.getAttribute("alt"), personHolder, "alt", "fullName");
  }

  private void measureInvoice__TEMPLATE_NODE(Component component,
      Container parent, int i) {
    measureTemplate(component, parent, i, "Invoice__TEMPLATE_NODE",
        invoiceHolder);
  }

  // ==========================================================================
  //                    Full Page Measurement Methods
  // ==========================================================================

  private void measureTestPage1(boolean withButton) throws Exception {
    Component[] components = container.getComponents();
    int i = 0;
    int count = withButton ? 9 : 8;
    int templateCount = 3;
    assertEquals("Component count in master container", count,
        components.length);
    measureAbstractField(TextField.class, components[i], container, i,
        "Person__fullName", personHolder, "fullName", null, null, null);
    i++;
    measureAbstractField(TextField.class, components[i], container, i,
        "Person__fullName__2", personHolder, "fullName", null, null, null);
    i++;
    measureInvoice__TEMPLATE_NODE(components[i], container, i);
    int templateField = i;
    i++;
    measureAbstractField(HTMLInputText.class, components[i], container, i,
        "Person__lastName", personHolder, "lastName", "lastName",
        EmptyMessageFormat.class, "{0}");
    i++;
    measureAbstractField(HTMLInputText.class, components[i], container, i,
        "Person__firstName", personHolder, "firstName", "firstName", null, null);
    i++;
    measureCheckboxField(HTMLCheckBox.class, components[i], container, i,
        "Person__active", personHolder, "active", "active", null, null, "true",
        "");
    i++;
    if (withButton) {
      assertEquals("Component " + i, Button.class, components[i].getClass());
      i++;
    }
    measureAbstractField(HTMLAnchorURL.class, components[i], container, i,
        "Person__personID", personHolder, "personID", null,
        EmptyMessageFormat.class, "http://localhost/mypage.mxform?pid={0}");
    i++;
    measureAreaInfo_NODE(components[i], container, i);

    i = 0;
    Template template = (Template) components[templateField];
    components = template.getComponents();
    assertEquals("Component count in template container", templateCount,
        components.length);
    measureAbstractField(TextField.class, components[i], template, i,
        "Invoice__invoiceNumber", invoiceHolder, "invoiceNumber", null,
        DecimalFormat.class, "0000000");
    i++;
    measureAbstractField(TextField.class, components[i], template, i,
        "Invoice__issueDate", invoiceHolder, "issueDate", null,
        SimpleDateFormat.class, "MM/dd/yyyy");
    i++;
    measureAbstractField(TextField.class, components[i], template, i,
        "Invoice__customerID", invoiceHolder, "customerID", null,
        EmptyMessageFormat.class, "CustNo={0}");
  }

  private void measureTestPage2() throws Exception {
    Component[] components = container.getComponents();
    assertEquals("Component count in master container", 1, components.length);
    assertEquals("Component 0", Template.class, components[0].getClass());

    components = ((Template) components[0]).getComponents();
    assertEquals("Component count in level 1 template container", 2,
        components.length);
    assertEquals("Component 0", TextField.class, components[0].getClass());
    assertEquals("Component 1", Template.class, components[1].getClass());

    components = ((Template) components[1]).getComponents();
    assertEquals("Component count in level 2 template container", 2,
        components.length);
    assertEquals("Component 0", TextField.class, components[0].getClass());
    assertEquals("Component 1", Template.class, components[1].getClass());

    components = ((Template) components[1]).getComponents();
    assertEquals("Component count in level 3 template container", 1,
        components.length);
    assertEquals("Component 0", TextField.class, components[0].getClass());
  }

  // ==========================================================================
  //                   Test Cases for getSuggestedComponents
  // ==========================================================================

  /**
   * Test method: getSuggestedComponents Case: SPAN tag
   */
  public void testGetSuggestedComponentsForSpan() throws Exception {
    setupTestPage("TestPage1.html");
    Vector suggestions = factory.getSuggestedComponents(container
        .getDocumentAdapter().getElementById("Person__fullName"));
    assertEquals("size", 2, suggestions.size());
    assertEquals("Suggestion 1", TextField.class.getName(), suggestions
        .elementAt(0));
    assertEquals("Suggestion 2", DocumentElement.class.getName(), suggestions
        .elementAt(1));
  }

  /**
   * Test method: getSuggestedComponents Case: TR tag with id TEMPLATE_NODE
   */
  public void testGetSuggestedComponentsForTemplate() throws Exception {
    setupTestPage("TestPage1.html");
    Vector suggestions = factory.getSuggestedComponents(container
        .getDocumentAdapter().getElementById("Invoice__TEMPLATE_NODE"));
    assertEquals("size", 2, suggestions.size());
    assertEquals("Suggestion 1", Template.class.getName(), suggestions
        .elementAt(0));
    assertEquals("Suggestion 2", DocumentElement.class.getName(), suggestions
        .elementAt(1));
  }

  /**
   * Test method: getSuggestedComponents Case: TD tag
   */
  public void testGetSuggestedComponentsForTD() throws Exception {
    setupTestPage("TestPage1.html");
    Vector suggestions = factory.getSuggestedComponents(container
        .getDocumentAdapter().getElementById("Invoice__invoiceNumber"));
    assertEquals("size", 2, suggestions.size());
    assertEquals("Suggestion 1", TextField.class.getName(), suggestions
        .elementAt(0));
    assertEquals("Suggestion 2", DocumentElement.class.getName(), suggestions
        .elementAt(1));
  }

  /**
   * Test method: getSuggestedComponents Case: INPUT tag type TEXT
   */
  public void testGetSuggestedComponentsForInputText() throws Exception {
    setupTestPage("TestPage1.html");
    Vector suggestions = factory.getSuggestedComponents(container
        .getDocumentAdapter().getElementById("Person__lastName"));
    assertEquals("size", 2, suggestions.size());
    assertEquals("Suggestion 1", HTMLInputText.class.getName(), suggestions
        .elementAt(0));
    assertEquals("Suggestion 2", DocumentElement.class.getName(), suggestions
        .elementAt(1));
  }

  /**
   * Test method: getSuggestedComponents Case: INPUT tag type SUBMIT
   */
  public void testGetSuggestedComponentsForSubmitButton() throws Exception {
    setupTestPage("TestPage1.html");
    Vector suggestions = factory.getSuggestedComponents(container
        .getDocumentAdapter().getElementById("SaveButton"));
    assertEquals("size", 3, suggestions.size());
    assertEquals("Suggestion 1", Button.class.getName(), suggestions
        .elementAt(0));
    assertEquals("Suggestion 2", Trigger.class.getName(), suggestions
        .elementAt(1));
    assertEquals("Suggestion 3", DocumentElement.class.getName(), suggestions
        .elementAt(2));
  }

  /**
   * Test method: getSuggestedComponents
   * Case: SPAN nested in SPAN
   */
  public void testGetSuggestedComponentsForSpanInSpan() throws Exception {
    setupTestPage("TestPage3.html");
    Vector suggestions = factory.getSuggestedComponents(container
        .getDocumentAdapter().getElementById("Invoice__TEMPLATE_NODE"));
    assertEquals("size", 3, suggestions.size());
    assertEquals("Suggestion 1", Template.class.getName(), suggestions
        .elementAt(0));
    assertEquals("Suggestion 2", TextField.class.getName(), suggestions
        .elementAt(1));
    assertEquals("Suggestion 3", DocumentElement.class.getName(), suggestions
        .elementAt(2));
    suggestions = factory.getSuggestedComponents(container
        .getDocumentAdapter().getElementById("Invoice__invoiceDate"));
    assertEquals("size", 2, suggestions.size());
    assertEquals("Suggestion 1", TextField.class.getName(), suggestions
        .elementAt(0));
    assertEquals("Suggestion 2", DocumentElement.class.getName(), suggestions
        .elementAt(1));
  }

  // ==========================================================================
  //                 Test Cases for createComponentForElement
  // ==========================================================================

  /**
   * Test method: createComponentForElement Case: SPAN bound field
   */
  public void testCreateComponentForSpan() throws Exception {
    setupTestPage("TestPage1.html");
    Component component = factory.createComponentForElement("Person__fullName",
        container.getDocumentAdapter().getElementById("Person__fullName"),
        holders);
    measureAbstractField(TextField.class, component, null, 0,
        "Person__fullName", personHolder, "fullName", null, null, null);
  }

  /**
   * Test method: createComponentForElement Case: SPAN bound field but NO
   * matching value holder
   */
  public void testCreateComponentForSpanNoHolder() throws Exception {
    setupTestPage("TestPage1.html");
    Component component = factory.createComponentForElement(
        "PersonX__fullName", container.getDocumentAdapter().getElementById(
            "Person__fullName"), holders);
    assertNull("Component sh/b null", component);
  }

  /**
   * Test method: createComponentForElement Case: SPAN bound field 2nd
   * occurrance
   */
  public void testCreateComponentForSpan2() throws Exception {
    setupTestPage("TestPage1.html");
    Component component = factory.createComponentForElement(
        "Person__fullName__2", container.getDocumentAdapter().getElementById(
            "Person__fullName__2"), holders);
    measureAbstractField(TextField.class, component, null, 0,
        "Person__fullName__2", personHolder, "fullName", null, null, null);
  }

  /**
   * Test method: createComponentForElement Case: TD bound TEMPLATE_NODE
   */
  public void testCreateComponentForTemplate() throws Exception {
    setupTestPage("TestPage1.html");
    Component component = factory.createComponentForElement(
        "Invoice__TEMPLATE_NODE", container.getDocumentAdapter()
            .getElementById("Invoice__TEMPLATE_NODE"), holders);
    measureInvoice__TEMPLATE_NODE(component, null, 0);
  }

  /**
   * Test method: createComponentForElement Case: P invalid ID
   */
  public void testCreateComponentForBad1() throws Exception {
    setupTestPage("TestPage1.html");
    Component component = factory.createComponentForElement("__", container
        .getDocumentAdapter().getElementById("__"), holders);
    assertNull("Expected null", component);
  }

  /**
   * Test method: createComponentForElement Case: P invalid ID
   */
  public void testCreateComponentForBad2() throws Exception {
    setupTestPage("TestPage1.html");
    Component component = factory.createComponentForElement("Person__",
        container.getDocumentAdapter().getElementById("Person__"), holders);
    assertNull("Expected null", component);
  }

  /**
   * Test method: createComponentForElement Case: INPUT-TEXT bound field
   */
  public void testCreateComponentForInputText() throws Exception {
    setupTestPage("TestPage1.html");
    Component component = factory.createComponentForElement("Person__lastName",
        container.getDocumentAdapter().getElementById("Person__lastName"),
        holders);
    measureAbstractField(HTMLInputText.class, component, null, 0,
        "Person__lastName", personHolder, "lastName", "lastName",
        EmptyMessageFormat.class, "{0}");
  }

  /**
   * Test method: createComponentForElement Case: INPUT-CHECKBOX bound field
   */
  public void testCreateComponentForInputCheckbox() throws Exception {
    setupTestPage("TestPage1.html");
    Component component = factory.createComponentForElement("Person__active",
        container.getDocumentAdapter().getElementById("Person__active"),
        holders);
    measureCheckboxField(HTMLCheckBox.class, component, null, 0,
        "Person__active", personHolder, "active", "active", null, null, "true",
        "");
  }

  /**
   * Test method: createComponentForElement Case: TD bound field with DATE
   * FORMAT
   */
  public void testCreateComponentWithDateFormat() throws Exception {
    setupTestPage("TestPage1.html");
    Component component = factory.createComponentForElement(
        "Invoice__issueDate", container.getDocumentAdapter().getElementById(
            "Invoice__issueDate"), holders);
    measureAbstractField(TextField.class, component, null, 0,
        "Invoice__issueDate", invoiceHolder, "issueDate", null,
        SimpleDateFormat.class, "MM/dd/yyyy");
  }

  /**
   * Test method: createComponentForElement Case: TD bound field with NUMBER
   * FORMAT
   */
  public void testCreateComponentWithNumberFormat() throws Exception {
    setupTestPage("TestPage1.html");
    Component component = factory.createComponentForElement(
        "Invoice__invoiceNumber", container.getDocumentAdapter()
            .getElementById("Invoice__invoiceNumber"), holders);
    measureAbstractField(TextField.class, component, null, 0,
        "Invoice__invoiceNumber", invoiceHolder, "invoiceNumber", null,
        DecimalFormat.class, "0000000");
  }

  /**
   * Test method: createComponentForElement Case: TD bound field with MESSAGE
   * FORMAT
   */
  public void testCreateComponentWithMessageFormat() throws Exception {
    setupTestPage("TestPage1.html");
    Component component = factory.createComponentForElement(
        "Invoice__customerID", container.getDocumentAdapter().getElementById(
            "Invoice__customerID"), holders);
    measureAbstractField(TextField.class, component, null, 0,
        "Invoice__customerID", invoiceHolder, "customerID", null,
        EmptyMessageFormat.class, "CustNo={0}");
  }

  /**
   * Test method: createComponentForElement Case: INPUT-TEXT bound field with
   * MESSAGE FORMAT in value attribute
   */
  public void testCreateComponentWithMessageFormatInValueAttribute()
      throws Exception {
    setupTestPage("TestPage1.html");
    Component component = factory.createComponentForElement("Person__lastName",
        container.getDocumentAdapter().getElementById("Person__lastName"),
        holders);
    measureAbstractField(HTMLInputText.class, component, null, 0,
        "Person__lastName", personHolder, "lastName", "lastName",
        EmptyMessageFormat.class, "{0}");
  }

  /**
   * Test method: createComponentForElement Case: SPAN bound field without
   * FORMAT
   */
  public void testCreateComponentWithoutFormat() throws Exception {
    setupTestPage("TestPage1.html");
    Component component = factory.createComponentForElement("Person__fullName",
        container.getDocumentAdapter().getElementById("Person__fullName"),
        holders);
    measureAbstractField(TextField.class, component, null, 0,
        "Person__fullName", personHolder, "fullName", null, null, null);
  }

  /**
   * Test method: createComponentForElement Case: A bound field with MESSSAGE
   * FORMAT in attribute
   */
  public void testCreateComponentWithMessageFormatInHrefAttribute()
      throws Exception {
    setupTestPage("TestPage1.html");
    Component component = factory.createComponentForElement("Person__personID",
        container.getDocumentAdapter().getElementById("Person__personID"),
        holders);
    measureAbstractField(HTMLAnchorURL.class, component, null, 0,
        "Person__personID", personHolder, "personID", null,
        EmptyMessageFormat.class, "http://localhost/mypage.mxform?pid={0}");
  }

  /**
   * Test method: createComponentForElement Case: IMG bound element with bound
   * attributes
   */
  public void testCreateComponentWithBoundAttributes() throws Exception {
    setupTestPage("TestPage1.html");
    Component component = factory.createComponentForElement("Person__NODE",
        container.getDocumentAdapter().getElementById("Person__NODE"), holders);
    measureAreaInfo_NODE(component, null, 0);
  }

  /**
   * Test method: createComponentForElement 
   * Case: SPAN bound element - bound to CollectionValueHolder
   */
  public void testCreateComponentTextFieldBoundToCollection() throws Exception {
    setupTestPage("TestPage3.html");
    Component component = factory.createComponentForElement("Invoice__invoiceDate",
        container.getDocumentAdapter().getElementById("Invoice__invoiceDate"), holders);
    measureAbstractField(TextField.class, component, null, 0,
        "Invoice__invoiceDate", invoiceHolder, "invoiceDate", null, null, null);
  }

  // ==========================================================================
  //                 Test Cases for createComponents
  // ==========================================================================

  /**
   * Test method: createComponents Case: no predefined components
   */
  public void testCreateComponentsWithoutExisting() throws Exception {
    setupTestPage("TestPage1.html");
    factory.createComponents(container, holders);
    measureTestPage1(WITHOUT_BUTTON);
  }

  /**
   * Test method: createComponents Case: with predefined components in top level
   * container
   */
  public void testCreateComponentsWithExistingInTopContainer() throws Exception {
    setupTestPage1WithButton();
    factory.createComponents(container, holders);
    measureTestPage1(WITH_BUTTON);
  }

  /**
   * Test method: createComponents Case: with predefined components in nested
   * container
   */
  public void testCreateComponentsWithExistingInSubContainer() throws Exception {
    setupTestPage1WithButtonAndIssueDate();
    factory.createComponents(container, holders);
    measureTestPage1(WITH_BUTTON);
  }

  /**
   * Test method: createComponents Case: calling method multiple times without
   * predefined components
   */
  public void testCreateComponentsCalledTwiceWithoutExisting() throws Exception {
    setupTestPage("TestPage1.html");
    factory.createComponents(container, holders);
    factory.createComponents(container, holders);
    measureTestPage1(WITHOUT_BUTTON);
  }

  /**
   * Test method: createComponents Case: calling method multiple times with
   * predefined components in top container
   */
  public void testCreateComponentsCalledTwiceWithExistingInTopContainer()
      throws Exception {
    setupTestPage1WithButton();
    factory.createComponents(container, holders);
    factory.createComponents(container, holders);
    measureTestPage1(WITH_BUTTON);
  }

  /**
   * Test method: createComponents Case: calling method multiple times with
   * predefined components in nested container
   */
  public void testCreateComponentsCalledTwiceWithExistingInSubContainer()
      throws Exception {
    setupTestPage1WithButtonAndIssueDate();
    factory.createComponents(container, holders);
    factory.createComponents(container, holders);
    measureTestPage1(WITH_BUTTON);
  }

  /**
   * Test method: createComponents Case: with nested templates - none predefined
   */
  public void testCreateComponentsNestedTemplates() throws Exception {
    setupTestPage("TestPage2.html");
    factory.createComponents(container, levelHolders);
    measureTestPage2();
  }

  /**
   * Test method: createComponents Case: with nested templates - some predefined
   */
  public void testCreateComponentsNestedTemplatesWithSomeDeclared()
      throws Exception {
    setupTestPage("TestPage2.html");

    Template t3 = new Template();
    t3.setCollectionValueHolder(level3);
    t3.setId("Level3__TEMPLATE_NODE");

    Template t2 = new Template();
    t2.setCollectionValueHolder(level2);
    t2.setId("Level2__TEMPLATE_NODE");
    t2.add(t3);

    Template t1 = new Template();
    t1.setCollectionValueHolder(level1);
    t1.setId("Level1__TEMPLATE_NODE");
    t1.add(t2);

    container.add(t1);

    factory.createComponents(container, levelHolders);
    measureTestPage2();
  }

  /**
   * Test method: createComponents 
   * Case: SPAN TEMPLATE_NODE tag with nested SPAN tag - both bound
   * to same CollectionValueHolder.
   * Also contains nested SPAN bound to another value holder
   * @throws Exception
   */
  public void testCreateComponentsNestedElements() throws Exception {
    setupTestPage("TestPage3.html");
    factory.createComponents(container, holders);
    // Measure top level components
    Component[] components = container.getComponents();
    assertEquals("Component count in master container", 1, components.length);
    measureTemplate(components[0], container, 0, "Invoice__TEMPLATE_NODE",
        invoiceHolder);
    // Measure children of Template
    Container parent = (Container) components[0];
    Component[] children = parent.getComponents();
    assertEquals("Component count in nested container", 2, children.length);
    measureAbstractField(TextField.class, children[0], parent, 0,
        "Invoice__invoiceDate", invoiceHolder, "invoiceDate", null, null, null);
    measureAbstractField(TextField.class, children[1], parent, 1,
        "Person__fullName", personHolder, "fullName", null, null, null);
  }

//  /** FUTURE FEATURE
//   * Test method: createComponents 
//   * Case: A tag with nested SPAN tag - both bound
//   * 
//   * @throws Exception
//   */
//  public void testCreateComponentsFullyNestedElements() throws Exception {
//    setupTestPage("TestPage4.html");
//    factory.createComponents(container, holders);
//    System.out.println("======== TEST 4 =============");
//    printContainer(container, "");
//    // Measure top level components
//    Component[] components = container.getComponents();
//    assertEquals("Component count in master container", 3, components.length);
//    measureAbstractField(HTMLAnchorURL.class, components[0], container, 0,
//        "Person__personID", personHolder, "personID", null, null, null);
//    measureTemplate(components[1], container, 1, "Invoice__TEMPLATE_NODE",
//        invoiceHolder);
//    measureAbstractField(TextField.class, components[2], container, 2,
//        "Person__lastName", personHolder, "lastName", null, null, null);
//    // Measure children of Anchor 1
//    Container parent = (Container) components[0];
//    Component[] children = parent.getComponents();
//    assertEquals("Component count in nested container", 1, children.length);
//    measureAbstractField(TextField.class, children[0], parent, 0,
//        "Person__fullName", personHolder, "fullName", null, null, null);
//    // Measure children of Template  
//    parent = (Container) components[1];
//    children = parent.getComponents();
//    assertEquals("Component count in nested container", 1, children.length);
//    measureAbstractField(HTMLAnchorURL.class, children[0], parent, 0,
//        "Invoice__invoiceID", invoiceHolder, "invoiceID", null, null, null);
//    // Measure children of nested Anchor
//    parent = (Container) children[0];
//    children = parent.getComponents();
//    assertEquals("Component count in nested container", 2, children.length);
//    measureAbstractField(TextField.class, children[0], parent, 0,
//        "Invoice__invoiceDate", invoiceHolder, "invoiceDate", null, null, null);
//    measureAbstractField(TextField.class, children[1], parent, 1,
//        "Person__firstName", personHolder, "firstName", null, null, null);
//  }

  // ==========================================================================
  //                     Test Support Methods and Classes
  // ==========================================================================

  /**
   * Diagnostics display method
   */
  private void printContainer(Container c, String level) {
    Component[] components = c.getComponents();
    for (int i = 0; i < components.length; i++) {
      Component component = components[i];
      System.out.println(level + component);
      if (component instanceof Container)
        printContainer((Container) component, level + "  ");
    }
  }

  /**
   * Test Factory Class
   */
  class TestFactory extends HTMLComponentFactory {
    protected Component createComponentForElement(String id, Element element,
        ValueHolder[] holders) {
      return super.createComponentForElement(id, element, holders);
    }
  }

  /**
   * Test Container Class
   */
  class TestContainer extends Container {
    private DocumentAdapter documentAdapter;

    public DocumentAdapter getDocumentAdapter() {
      return documentAdapter;
    }

    public void setDocument(org.w3c.dom.Document newDocument) {
      documentAdapter = new DOM_1_20000929_DocumentAdapter(newDocument);
    }

    public void removeNotify() {
    }

    public void addNotify() {
    }

    public org.w3c.dom.Document getDocument() {
      if (documentAdapter == null)
        return null;
      else
        return documentAdapter.getDocument();
    }
  }

  /* Executes the test case */
  public static void main(String[] argv) {
    String[] testCaseList = { HTMLComponentFactoryTest.class.getName() };
    junit.swingui.TestRunner.main(testCaseList);
  }
}