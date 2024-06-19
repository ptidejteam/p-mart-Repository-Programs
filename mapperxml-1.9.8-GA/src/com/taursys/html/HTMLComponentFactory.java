/**
 * HTMLComponentFactory - Factory which creates XMLComponents from an HTML Doc.
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
package com.taursys.html;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.taursys.debug.Debug;
import com.taursys.dom.DOM_1_20000929_DocumentAdapter;
import com.taursys.model.CollectionValueHolder;
import com.taursys.model.ValueHolder;
import com.taursys.util.EmptyMessageFormat;
import com.taursys.xml.AbstractField;
import com.taursys.xml.Attribute;
import com.taursys.xml.BoundDocumentElement;
import com.taursys.xml.Button;
import com.taursys.xml.Component;
import com.taursys.xml.ComponentFactory;
import com.taursys.xml.Parameter;
import com.taursys.xml.Template;
import com.taursys.xml.TextField;
import com.taursys.xml.TextNode;
import com.taursys.xml.Trigger;

/**
 * HTMLComponentFactory is used to automate the creation of Components based on
 * the HTML Document and its Elements. It determines the Component type based
 * on its element tag "type" attribute and "id" attribute. This class
 * initializes the tagTable with suggested components for HTML tags in its
 * constructor by calling the <code>initTagTable</code> method.
 * <p>
 * This class provides a Singleton via the <code>getInstance</code> method. This
 * is recommended over constructing a new instance.
 * <p>
 * This class contains two primary methods:
 * <ul>
 * <li><code>getSuggestedComponents</code> - for use by design tools.</li>
 * <li><code>createComponents</code> - to automatically create and bind
 * <code>Components</code> at runtime.</li>
 * </ul>
 */
public class HTMLComponentFactory extends ComponentFactory {
  private static HTMLComponentFactory factory;

  /**
   * Default constructor for HTMLComponentFactory.
   * @see #getInstance
   */
  public HTMLComponentFactory() {
    super();
  }

  /**
   * Get the singleton instance of the HTMLComponentFactory.
   */
  public static HTMLComponentFactory getInstance() {
    if (factory == null) {
      factory = new HTMLComponentFactory();
    }
    return factory;
  }

  // ***********************************************************************
  // *                       PROTECTED METHODS
  // ***********************************************************************

  /**
   * Initialize the factory's tagTable with suggested components for HTML
   * documents. During the automated Component creation, only the first
   * suggestion is used. The other suggestions are intended for use by design
   * tools. The following are the suggestions created by this class:
   * <table>
   *  <tr>
   *    <td>Element Tag</td>
   *    <td>Type Attribute</td>
   *    <td>Suggested Component(s)</td>
   *  </tr>
   *  <tr>
   *    <td>center</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>input</td>
   *    <td>password</td>
   *    <td>com.taursys.html.HTMLInputText</td>
   *  </tr>
   *  <tr>
   *    <td>th</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>td</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>code</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>dt</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>textarea</td>
   *    <td>n/a</td>
   *    <td>com.taursys.html.HTMLTextArea</td>
   *  </tr>
   *  <tr>
   *    <td>link</td>
   *    <td>n/a</td>
   *    <td>com.taursys.html.HTMLAnchorURL</td>
   *  </tr>
   *  <tr>
   *    <td>input</td>
   *    <td>hidden</td>
   *    <td>com.taursys.html.HTMLInputText</td>
   *  </tr>
   *  <tr>
   *    <td>samp</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>strike</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>abbr</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>big</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>dd</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>sup</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>del</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>acronym</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>input</td>
   *    <td>submit</td>
   *    <td>com.taursys.xml.Button, com.taursys.xml.Trigger</td>
   *  </tr>
   *  <tr>
   *    <td>span</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>sub</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>var</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>h6</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>h5</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>h4</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>input</td>
   *    <td>checkbox</td>
   *    <td>com.taursys.html.HTMLCheckBox</td>
   *  </tr>
   *  <tr>
   *    <td>h3</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>h2</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>thead</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>h1</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>strong</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>cite</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>kbd</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>title</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>li</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>small</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>option</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>ins</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>legend</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>caption</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>u</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>pre</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>s</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>q</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>select</td>
   *    <td>n/a</td>
   *    <td>com.taursys.html.HTMLSelect</td>
   *  </tr>
   *  <tr>
   *    <td>p</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>label</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>input</td>
   *    <td>text</td>
   *    <td>com.taursys.html.HTMLInputText</td>
   *  </tr>
   *  <tr>
   *    <td>blockquote</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>i</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>em</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>font</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>tt</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>b</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   *  <tr>
   *    <td>a</td>
   *    <td>n/a</td>
   *    <td>com.taursys.html.HTMLAnchorURL</td>
   *  </tr>
   *  <tr>
   *    <td>dfn</td>
   *    <td>n/a</td>
   *    <td>com.taursys.xml.TextField</td>
   *  </tr>
   * </table>
   * <p>
   */
  protected void initTagTable() {
    //  A - Anchor
    putTagSuggestion("a", HTMLAnchorURL.class);
    // ABBR - Abbreviation
    putTagSuggestion("abbr", TextField.class);
    // ACRONYM - Acronym
    putTagSuggestion("acronym", TextField.class);
    // ADDRESS - Address
    // APPLET - Java applet
    // AREA - Image map region
    putTagSuggestion("area", Template.class);
    // B - Bold text
    putTagSuggestion("b", TextField.class);
    // BASE - Document base URI
    // BASEFONT - Base font change
    // BDO - BiDi override
    // BIG - Large text
    putTagSuggestion("big", TextField.class);
    // BLOCKQUOTE - Block quotation
    putTagSuggestion("blockquote", TextField.class);
    // BODY - Document body
    // BR - Line break
    // BUTTON - Button
    // CAPTION - Table caption
    putTagSuggestion("caption", TextField.class);
    // CENTER - Centered block
    putTagSuggestion("center", TextField.class);
    // CITE - Citation
    putTagSuggestion("cite", TextField.class);
    // CODE - Computer code
    putTagSuggestion("code", TextField.class);
    // COL - Table column
    // COLGROUP - Table column group
    // DD - Definition description
    putTagSuggestion("dd", TextField.class);
    // DEL - Deleted text
    putTagSuggestion("del", TextField.class);
    // DFN - Defined term
    putTagSuggestion("dfn", TextField.class);
    // DIR - Directory list
    // DIV - Generic block-level container
    // DL - Definition list
    // DT - Definition term
    putTagSuggestion("dt", TextField.class);
    // EM - Emphasis
    putTagSuggestion("em", TextField.class);
    // FIELDSET - Form control group
    // FONT - Font change
    putTagSuggestion("font", TextField.class);
    // FORM - Interactive form
    // FRAME - Frame
    // FRAMESET - Frameset
    // H1 - Level-one heading
    putTagSuggestion("h1", TextField.class);
    // H2 - Level-two heading
    putTagSuggestion("h2", TextField.class);
    // H3 - Level-three heading
    putTagSuggestion("h3", TextField.class);
    // H4 - Level-four heading
    putTagSuggestion("h4", TextField.class);
    // H5 - Level-five heading
    putTagSuggestion("h5", TextField.class);
    // H6 - Level-six heading
    putTagSuggestion("h6", TextField.class);
    // HEAD - Document head
    // HR - Horizontal rule
    // HTML - HTML document
    // I - Italic text
    putTagSuggestion("i", TextField.class);
    // IFRAME - Inline frame
    // IMG - Inline image
    putTagSuggestion("img", BoundDocumentElement.class);
    // INPUT - Form input
    putTagSuggestion("input-hidden", HTMLInputText.class);
    putTagSuggestion("input-password", HTMLInputText.class);
    putTagSuggestion("input-text", HTMLInputText.class);
    putTagSuggestion("input-checkbox", HTMLCheckBox.class);
    putTagSuggestion("input-submit", new Class[] {
      Button.class,
      Trigger.class
    });
    // INS - Inserted text
    putTagSuggestion("ins", TextField.class);
    // ISINDEX - Input prompt
    // KBD - Text to be input
    putTagSuggestion("kbd", TextField.class);
    // LABEL - Form field label
    putTagSuggestion("label", TextField.class);
    // LEGEND - Fieldset caption
    putTagSuggestion("legend", TextField.class);
    // LI - List item
    putTagSuggestion("li", TextField.class);
    // LINK - Document relationship
    putTagSuggestion("link", HTMLAnchorURL.class);
    // MAP - Image map
    // MENU - Menu list
    // META - Metadata
    // NOFRAMES - Frames alternate content
    // NOSCRIPT - Alternate script content
    // OBJECT - Object
    // OL - Ordered list
    // OPTGROUP - Option group
    // OPTION - Menu option
    putTagSuggestion("option", TextField.class);
    // P - Paragraph
    putTagSuggestion("p", TextField.class);
    // PARAM - Object parameter
    // PRE - Preformatted text
    putTagSuggestion("pre", TextField.class);
    // Q - Short quotation
    putTagSuggestion("q", TextField.class);
    // S - Strike-through text
    putTagSuggestion("s", TextField.class);
    // SAMP - Sample output
    putTagSuggestion("samp", TextField.class);
    // SCRIPT - Client-side script
    // SELECT - Option selector
    putTagSuggestion("select", HTMLSelect.class);
    // SMALL - Small text
    putTagSuggestion("small", TextField.class);
    // SPAN - Generic inline container
    putTagSuggestion("span", TextField.class);
    // STRIKE - Strike-through text
    putTagSuggestion("strike", TextField.class);
    // STRONG - Strong emphasis
    putTagSuggestion("strong", TextField.class);
    // STYLE - Embedded style sheet
    // SUB - Subscript
    putTagSuggestion("sub", TextField.class);
    // SUP - Superscript
    putTagSuggestion("sup", TextField.class);
    // TABLE - Table
    // TBODY - Table body
    // TD - Table data cell
    putTagSuggestion("td", TextField.class);
    // TEXTAREA - Multi-line text input
    putTagSuggestion("textarea", HTMLTextArea.class);
    // TFOOT - Table foot
    // TH - Table header cell
    putTagSuggestion("th", TextField.class);
    // THEAD - Table head
    putTagSuggestion("thead", TextField.class);
    // TITLE - Document title
    putTagSuggestion("title", TextField.class);
    // TR - Table row
    // TT - Teletype text
    putTagSuggestion("tt", TextField.class);
    // U - Underlined text
    putTagSuggestion("u", TextField.class);
    // UL - Unordered list
    // VAR - Variable
    putTagSuggestion("var", TextField.class);
  }
  
  /**
   * Used for generating JavaDoc for supported tags
   */
  private void generateInitTagTableJavaDoc() {
    System.out.println("   * <table>");
    System.out.println("   *  <tr>");
    System.out.println("   *    <td>Element Tag</td>");
    System.out.println("   *    <td>Type Attribute</td>");
    System.out.println("   *    <td>Suggested Component(s)</td>");
    System.out.println("   *  </tr>");
    Enumeration tags = tagTable.keys();
    while (tags.hasMoreElements()) {
      String key = (String) tags.nextElement();
      String typelist = "";
      String separator = "";
      Enumeration types = ((Vector)tagTable.get(key)).elements();
      while (types.hasMoreElements()) {
        String type = (String) types.nextElement();
        typelist += separator + type;
        separator = ", ";
      }
      StringTokenizer tokens = new StringTokenizer(key, "-");
      int count = tokens.countTokens();
      String element = tokens.nextToken().trim();
      String attribute = "n/a";
      if (tokens.hasMoreTokens()) {
        attribute = tokens.nextToken().trim();
      }
      System.out.println("   *  <tr>");
      System.out.println("   *    <td>" + element + "</td>");
      System.out.println("   *    <td>" + attribute + "</td>");
      System.out.println("   *    <td>" + typelist + "</td>");
      System.out.println("   *  </tr>");
    }
    System.out.println("   * </table>");
  }

//  /**
//   * Generates portion of the JavaDoc for initTagTable to list the
//   * supported HTML tags.
//   * @param args not used
//   */  
//  public static void main(String[] args) {
//    HTMLComponentFactory factory = HTMLComponentFactory.getInstance();
//    factory.generateInitTagTableJavaDoc();
//  }

  /**
   * Adds tag with single suggested component to tagTable
   * @param tagName the tag name
   * @param clazz the suggested component
   */
  private void putTagSuggestion(String tagName, Class clazz) {
    Vector suggestions = new Vector();
    suggestions.add(clazz.getName());
    tagTable.put(tagName, suggestions);
  }
  
  /**
   * Adds tag with multiple suggested components to tagTable
   * @param tagName the tag name
   * @param clazzes the suggested components
   */
  public void putTagSuggestion(String tagName, Class[] clazzes) {
    Vector suggestions = new Vector();
    for (int i = 0; i < clazzes.length; i++) {
      suggestions.add(clazzes[i].getName());
    }
    tagTable.put(tagName, suggestions);
  }

  /**
   * Returns a Vector of suggested Component class names for given Element.
   * This method will choose the appropriate Components based on the type of
   * Element given.  The default Component type will be the first in the list.
   * <p>
   * If the component is an input component, then the TYPE attribute is also
   * used in selecting the right component.
   * <p>
   * If the Element has an ID, then the com.taursys.xml.DocumentElement Component
   * will be added to the end of the suggestion list.
   * <p>
   * If the id contains the TEMPLATE_NODE keyword, then a Template will be added
   * to the top of the suggestion list.
   * <p>
   * If there are no suggested types of Component for the given Element, then
   * an empty Vector will be returned.
   * <p>
   * Subclasses should override this method if more than the Element tag name
   * is needed to determine the suggested components.
   * @param element to return default Component type for
   * @return a Vector containing any suggested Component class names
   */
  public Vector getSuggestedComponents(Element element) {
    String tagName = element.getTagName();
    if (tagName.equals("input"))
      tagName += "-" + element.getAttribute("type");
    String id = element.getAttribute("id");
    return getSuggestedComponents(tagName, id, element);
  }

  /**
   * <p>Create a component for given element and set its properties.
   * Only bound components with id's following a strict id naming convention
   * will be created.
   * </p>
   * <p>The id must begin with a ValueHolder's alias. It must then be followed
   * by a double-underscore ("__"). Next the propertyName must appear or the
   * keyword "TEMPLATE_NODE".  An optional suffix can be added to ensure
   * unique id's (as required by spec). The optional suffix must be separated
   * from the property name by a double-underscore("__"). The following are
   * examples of valid id format:</p>
   * <ul>
   * <li>Person__lastName</li>
   * <li>Person__lastName__2</li>
   * <li>Invoices__TEMPLATE_NODE</li>
   * <li>Invoices__TEMPLATE_NODE__2</li>
   * </ul>
   * <p>The alias of the id (first part), must match an alias of a ValueHolder
   * in the given array of ValueHolders, otherwise no Component will be
   * created. The ValueHolder with a matching alias will be set as the new
   * Component's valueHolder.
   * </p>
   * <p>If the new Component is an AbstractField subclass, then its
   * propertyName will be set to the propertyName of the id (second part). If
   * the given Element has a "name" attribute, the Component's parameter will
   * be set to the value of the "name" attribute.
   * <p>If the new Component is a Template (or subclass), then only its
   * collectionValueHolder property will be set. The associated ValueHolder
   * for this Component must be a CollectionValueHolder, otherwise no
   * Component will be created.
   * </p>
   * <p>This method will also attempt to setup the formatting properties
   * for the new Component. This only applies to AbstractField subclasses.
   * The format is extracted from the document within the element's "value"
   * attribute, "href" attribute, or text node. The format must be specified
   * as TYPE:pattern, where TYPE is one of: DATE NUMBER or MSG. The pattern
   * should be a valid pattern for the format type. The following are examples
   * of use:
   * <ul>
   * <li>&lt;span id="Person__birthdate"&gt;DATE:MM/dd/yyyy&lt;/span&gt;</li>
   * <li>&lt;input type="text" id="InvoiceItem__unitPrice" 
   *    value="NUMBER:###,##0.00" /&gt;</li>
   * <li>&lt;a id="Person__personID" 
   *    href="MSG:/PersonProfile.mxform?personID={0}"&gt;Link to 
   *    Person&lt;/a&gt;</li>
   * </ul>
   * </p>
   * @param id of Element to create component for
   * @param element to create component for
   * @param holders the array of ValueHolders for binding
   * @return new Component with properties set or null
   */
//  protected Component createComponentForElement1(String id, Element element,
//      ValueHolder[] holders) {
//    if (id == null)
//      throw new IllegalArgumentException(
//          "Null id passed to createComponentForElement");
//
//    // Extract holder alias
//    int pos = id.indexOf(ID_DELIMITER);
//    if (pos < 1)
//      return null;
//    String alias = id.substring(0, pos);
//
//    // Extract property name (remove suffix)
//    pos += 2;
//    if (id.length() <= pos)
//      return null;
//    String propertyName = id.substring(pos);
//    pos = propertyName.indexOf(ID_DELIMITER);
//    if (pos != -1)
//      propertyName = propertyName.substring(0, pos);
//
//    // Find holder for alias
//    ValueHolder holder = findValueHolder(holders, alias);
//    if (holder == null)
//      return null;
//
//    // Create appropriate component
//    Vector suggestions = getSuggestedComponents(element);
//    if (suggestions.size() == 0)
//      return null;
//    Component component = null;
//    try {
//      component = (Component)
//          Class.forName((String)suggestions.get(0)).newInstance();
//    } catch (Exception ex) {
//      Debug.error("Error during create component: " + ex.getMessage(), ex);
//      return null;
//    }
//
//    // Set properties as appropriate:
//    // id, valueHolder, propertyName, parameter, format, formatPattern)
//    if (component instanceof AbstractField) {
//      ((AbstractField)component).setId(id);
//      ((AbstractField)component).setValueHolder(holder);
//      ((AbstractField)component).setPropertyName(propertyName);
//      String parameter = element.getAttribute("name");
//      if (parameter != null && parameter.length() == 0)
//        parameter = null;
//      ((AbstractField)component).setParameter(parameter);
//      setupFormat((AbstractField)component, element);
//      bindAttributes1(component, element, holders);
//      return component;
//    } else if (component instanceof Template) {
//      ((Template)component).setId(id);
//      if (holder instanceof CollectionValueHolder) {
//        ((Template)component).setCollectionValueHolder(
//            (CollectionValueHolder)holder);
//        bindAttributes1(component, element, holders);
//        return component;
//      } else {
//        return null;
//      }
//    } else if (component instanceof BoundDocumentElement) {
//      ((BoundDocumentElement)component).setId(id);
//      ((BoundDocumentElement)component).setValueHolder(holder);
//      bindAttributes1(component, element, holders);
//      return component;
//    } else {
//      return null;
//    }
//  }
  
  protected Component createComponentForElement(String id, Element element,
      ValueHolder[] holders) {
    if (id == null)
      throw new IllegalArgumentException(
          "Null id passed to createComponentForElement");
    NodeDescriptor nd = parseBindSyntax(id);
    Component component = createComponentForElement(element, nd);
    if (component == null)
      return null;
    // Binding component
    boolean results = bindComponent(nd, element, holders, component);
    return results ? component : null;
  }

  /**
   * @param element
   * @param nd
   * @return
   */
  protected Component createComponentForElement(Element element, NodeDescriptor nd) {
    Component component = null;
    // Create component
    if (nd.isTemplateNode()) {
      component = new Template();
    } else {
      Vector suggestions = getSuggestedComponents(element);
      if (suggestions.size() > 0
          && !suggestions.get(0).equals(BoundDocumentElement.class.getName())) {
        try {
          component = (Component) Class.forName((String) suggestions.get(0))
              .newInstance();
        } catch (Exception ex) {
          Debug.error("Error creating component. class="
              + suggestions.get(0) + " - " + ex.getMessage(), ex);
//          component = null;
        }
      } else {
        component = new BoundDocumentElement();
      }
    }
    return component;
  }

//  /**
//   * @param holders
//   * @param alias
//   * @return
//   */
//  private ValueHolder findValueHolder(ValueHolder[] holders, String alias) {
//    if (alias != null) {
//      for (int i = 0; i < holders.length; i++) {
//        if (alias.equals(holders[i].getAlias())) {
//          return holders[i];
//        }
//      }
//    }
//    return null;
//  }

//  private void bindAttributes1(Component component, Node node, ValueHolder[] holders) {
//    com.taursys.xml.Element element = (com.taursys.xml.Element)component;
//    // Get all attributes for node
//    NamedNodeMap attribs = node.getAttributes();
//    // Continue only if there are attributes
//    for (int i = 0; i < attribs.getLength(); i++) {
//      Node attribNode = attribs.item(i);
//      String attribName = attribNode.getNodeName();
//      String attribValue = attribNode.getNodeValue();
//      if (!attribName.equals("id") && attribValue != null && attribValue.indexOf(ID_DELIMITER) > -1) {
//        // Extract holder alias
//        int pos = attribValue.indexOf(ID_DELIMITER);
//        String alias = attribValue.substring(0, pos);
//        // Extract property name
//        String propName = attribValue.substring(pos + 2);
//        if (propName.length() > 0) {
//          if (alias.length() > 0) {
//            // Lookup valueholder
//            ValueHolder holder = findValueHolder(holders, alias);
//            if (holder != null) {
//              element.createAttribute(attribName, propName, holder);
//            }
//          } else {
//            element.createBoundAttribute(attribName, propName);
//          }
//        }
//      }
//    }
//  }
  
  public static void main(String[] args) {
    HTMLComponentFactory factory = HTMLComponentFactory.getInstance();
    //    List results = factory.tokenize("holder");
//    for (Iterator iter = results.iterator(); iter.hasNext();) {
//      String token = (String) iter.next();
//      System.out.println(token);
//    }

    //    String attribValue = "areaInfo__lastName";
//    int pos = attribValue.indexOf(ID_DELIMITER);
//    String alias = attribValue.substring(0, pos);
//    System.out.println(">>" + alias + "<<" + " len:" + alias.length());
//    String propName = attribValue.substring(pos + 2);
//    System.out.println(">>" + propName + "<<" + " len:" + propName.length());
  }

}
