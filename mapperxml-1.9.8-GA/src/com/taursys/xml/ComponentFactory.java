/**
 * ComponentFactory - Factory which creates XMLComponents from an XML Doc.
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
package com.taursys.xml;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.taursys.debug.Debug;
import com.taursys.dom.DOM_1_20000929_DocumentAdapter;
import com.taursys.html.NodeDescriptor;
import com.taursys.model.CollectionValueHolder;
import com.taursys.model.ValueHolder;
import com.taursys.model.VariantValueHolder;
import com.taursys.util.EmptyMessageFormat;

/**
 * Abstract class used to automate the creation of Components based on
 * the XML Document and its Elements. It determines the Component type based
 * on its element tag and "id" attribute). Concrete subclasses must override
 * the <code>initTagTable</code> method and populate the tagTable with
 * suggested components for the elements in the XML document.
 * <p>
 * This class contains two primary methods:
 * <ul>
 * <li><code>getSuggestedComponents</code> - for use by design tools.</li>
 * <li><code>createComponents</code> - to automatically create and bind
 * <code>Components</code> at runtime.</li>
 * </ul>
 */
public abstract class ComponentFactory {
  public static final String ID_DELIMITER = "__";
  public static final String TEMPLATE_NODE = "TEMPLATE_NODE";
  protected Hashtable tagTable = new Hashtable();

  // ***********************************************************************
  // *                    CONSTRUCTORS AND INITIALIZERS
  // ***********************************************************************

  /**
   * Default constructor which initializes tag table by calling initTagTable
   */
  public ComponentFactory() {
    initTagTable();
  }

  // ***********************************************************************
  // *                        GENERAL METHODS
  // ***********************************************************************

  /**
   * Returns a Vector of suggested Component class names for given Element.
   * This method will choose the appropriate Components based on the type of
   * Element given.  The default Component type will be the first in the list.
   * <p>
   * If the Element has an ID, then the DocumentElement Component will be added
   * to the end of the suggestion list.
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
  public abstract Vector getSuggestedComponents(Element element);

  /**
   * <p>Creates components based on document, set their properties (including
   * valueHolder) and adds them to the container. As it moves through the
   * document, it first checks to see if the component is already in the
   * container (by matching id's). If it is, the existing component is moved
   * to the proper place in the heirarchy. Otherwise it will create a component.
   * </p>
   * <p>This method builds a component heirarchy which matches the document order
   * and heirarchy. If any newly created component is itself a Container type,
   * then all children of that component are added to it rather than its parent
   * container.
   * </p>
   * <p>Only bound components which are bound to one of the given value holders
   * are created.</p>
   * @param container the Container to add components to
   * @param holders an array of valueholders.
   */
  public void createComponents(Container container, ValueHolder[] holders) {
    // invoke private method to recursively create components
    createComponents(
        container.getDocumentAdapter().getDocument(),
        container,
        holders,
        container, false);
//    printComponentTree(container,"");
  }

  /**
   * Prints the component tree contents to the console
   * @param parent starting parent container
   * @param level spaces to indent initially
   */
  public void printComponentTree(Container parent, String level) {
    Component[] children = parent.getComponents();
    for (int i = 0; i < children.length; i++) {
      System.out.println(level + children[i]);
      if (children[i] instanceof Container) {
        printComponentTree((Container) children[i], level + "  ");
      }
    }
  }

  /**
   * <p>Creates components based on document, set their properties (including
   * valueHolder) and adds them to the container. As it moves through the
   * document, it first checks to see if the component is already in the
   * container (by matching id's). If it is, the existing component is moved
   * to the proper place in the heirarchy. Otherwise it will create a component.
   * </p>
   * <p>This method builds a component heirarchy which matches the document order
   * and heirarchy. If any newly created component is itself a Container type,
   * then all children of that component are added to it rather than its parent
   * container.
   * </p>
   * <p>Only bound components which are bound to one of the given value holders
   * are created.</p>
   * @param container the Container to add components to
   * @param holders an array of valueholders.
   */
  public void createComponents(Container container, ValueHolder[] holders,
      boolean bindExisting) {
    // invoke private method to recursively create components
    createComponents(
        container.getDocumentAdapter().getDocument(),
        container,
        holders,
        container, bindExisting);
  }

  // ***********************************************************************
  // *                       PROTECTED AND PRIVATE METHODS
  // ***********************************************************************

  /**
   * Initialize values in the tag table
   */
  protected abstract void initTagTable();

  /**
   * Returns a Vector of suggested Component class names for given Element.
   * This method will choose the appropriate Components based on the type of
   * tag given.  The default Component type will be the first in the list.
   * <p>
   * If an ID is given, then the DocumentElement Component will be added to the
   * end of the suggestion list.
   * <p>
   * If the id contains the TEMPLATE_NODE keyword, then a Template will be added
   * to the top of the suggestion list.
   * <p>
   * If an ID is given and the id does not contain the TEMPLATE_NODE keyword,
   * but the given element has child nodes, then a Template will be added before
   * the DocumentElement suggestion.
   * <p>
   * If there are no suggested types of Component for the given tag, then
   * an empty Vector will be returned.
   * @param tagName to return default Component type for
   * @param id of the tag or null
   * @param element the Element to get suggestions for
   * @return a Vector containing any suggested Component class names
   */
  protected Vector getSuggestedComponents(
      String tagName, String id, Element element) {
    Vector suggestions = (Vector)tagTable.get(tagName);
    if (suggestions == null) {
      suggestions = new Vector();
    } else {
      suggestions = new Vector(suggestions);
    }
    if (id != null && id.length() > 0) {
      // add Template at beginning or end of suggestions
      if (id.indexOf(TEMPLATE_NODE) > -1) {
        suggestions.add(0,Template.class.getName());
      } else {
        if (DOM_1_20000929_DocumentAdapter.hasChildElements(element))
          suggestions.add(Template.class.getName());
      }
      suggestions.add(DocumentElement.class.getName());
    }
    return suggestions;
  }

  /**
   * Create a component for given element and set its properties.
   * @param id the id of the Element to create the Component for.
   * @param element the Element to create the Component for.
   * @param holders the array of ValueHolders for binding
   */
  protected abstract Component createComponentForElement(
      String id, Element element, ValueHolder[] holders);

  protected abstract Component createComponentForElement(Element element, 
      NodeDescriptor nd);


  /**
   * <p>Creates components based on document, set their properties (including
   * valueHolder) and adds them to the container. As it moves through the
   * document, it first checks to see if the component is already in the
   * container (by matching id's). If it is, the existing component is moved
   * to the proper place in the heirarchy. Otherwise it will create a component.
   * </p>
   * <p>This method builds a component heirarchy which matches the document order
   * and heirarchy. If any newly created component is itself a Container type,
   * then all children of that component are added to it rather than its parent
   * container.
   * </p>
   * <p>Only bound components which are bound to one of the given value holders
   * are created.</p>
   * @param parentNode the parent node of the children to process and recurse
   * @param parentContainer the current Container to add components to
   * @param holders an array of valueholders.
   * @param rootContainer the top Container which may contain existing
   *    Components
   */
//  private void createComponents(Node parentNode, Container parentContainer,
//      ValueHolder[] holders, Container rootContainer) {
//    createComponents(parentNode, parentContainer, holders, rootContainer, false);
//    Node childNode = parentNode.getFirstChild();
//    Component childComponent = null;
//    while (childNode != null) {
//      if (childNode.getNodeType() == Node.ELEMENT_NODE) {
//        String id = ((Element)childNode).getAttribute("id");
//        if (id != null && id.length() > 0) {
//          // See if already exists
//          childComponent = (Component)rootContainer.get(id);
//          if (childComponent != null) {
//            // remove from existing position
//            childComponent.getParent().remove(childComponent);
//            traceCreateComponents("Moving component id=" + id + 
//                " element=" + childNode);
//          } else {
//            // create component
//            childComponent = createComponentForElement(
//                id, (Element)childNode, holders);
//            traceCreateComponents("Creating component id=" + id
//                + " element=" + childNode + " componentType=" + childComponent);
//          }
//          // Add component to parent (if not null)
//          if (childComponent != null)
//            parentContainer.add(childComponent);
//        } else {
//          childComponent = null;
//        }
//        if (childComponent instanceof Container)
//          createComponents(
//              childNode, (Container)childComponent, holders, rootContainer);
//        else
//          createComponents(childNode, parentContainer, holders, rootContainer);
//      }
//      childNode = childNode.getNextSibling();
//    }
//  }

  /**
   * <p>Creates components based on document, set their properties (including
   * valueHolder) and adds them to the container. As it moves through the
   * document, it first checks to see if the component is already in the
   * container (by matching id's). If it is, the existing component is moved
   * to the proper place in the heirarchy. Otherwise it will create a component.
   * </p>
   * <p>This method builds a component heirarchy which matches the document order
   * and heirarchy. If any newly created component is itself a Container type,
   * then all children of that component are added to it rather than its parent
   * container.
   * </p>
   * <p>Only bound components which are bound to one of the given value holders
   * are created.</p>
   * @param parentNode the parent node of the children to process and recurse
   * @param parentContainer the current Container to add components to
   * @param holders an array of valueholders.
   * @param rootContainer the top Container which may contain existing
   *    Components
   */
  private void createComponents(Node parentNode, Container parentContainer,
      ValueHolder[] holders, Container rootContainer, boolean bindExisting) {
    Node childNode = parentNode.getFirstChild();
    Component childComponent = null;
    while (childNode != null) {
      childComponent = null;
      if (childNode.getNodeType() == Node.ELEMENT_NODE) {
        String id = ((Element)childNode).getAttribute("id");
        NodeDescriptor nd = parseBindSyntax(id);
        if (id != null && id.length() > 0) {
          // See if already exists
          childComponent = (Component)rootContainer.get(id);
          if (childComponent != null) {
            // move from existing position
            childComponent.getParent().remove(childComponent);
            traceCreateComponents("Moving " +
                (bindExisting ? "and binding " : "") +
                "existing component id=" + id + 
                " element=" + childNode);
            if (bindExisting) {
              bindComponent(nd, (Element) childNode, holders, childComponent);
            }
          } else {
            // create component
            childComponent = createComponentForElement(
                (Element)childNode, nd);
            if (childComponent != null && 
                bindComponent(nd, (Element) childNode, holders, childComponent)) {
              traceCreateComponents("Created component id=" + id
                  + " element=" + childNode + " componentType=" + childComponent);
            } else {
              traceCreateComponents("Did NOT create component id=" + id
                  + " element=" + childNode + " componentType=" + childComponent);
              childComponent = null;
            }
          }
          if (childComponent != null) {
            parentContainer.add(childComponent);
          }
        } else {
          childComponent = null;
        }
        if (childComponent instanceof Container)
          createComponents(
              childNode, (Container)childComponent, holders, rootContainer, bindExisting);
        else
          createComponents(childNode, parentContainer, holders, rootContainer, bindExisting);
      }
      childNode = childNode.getNextSibling();
    }
  }

  protected NodeDescriptor parseBindSyntax(String value) {
    NodeDescriptor nd = new NodeDescriptor();
    // Exit if null or blank
    if (value == null || value.length() == 0) {
      return nd;
    }
    nd.setId(value);
    List tokens = tokenize(value);
    Iterator iter = tokens.iterator();
    String token = (String) iter.next();
    if (tokens.size() == 1) {
      // can only be format
      if (isFormatDescriptor(token)) {
        nd.setFormatDescriptor(token);
      }
    } else if (token.equals(ID_DELIMITER)){
      // No value holder - must be property name
      token = (String) iter.next(); // property name
      nd.setPropertyName(token);
      if (iter.hasNext()) {
        token = (String) iter.next(); // delimiter
        if (iter.hasNext()) {
          token = (String) iter.next(); // format
          if (isFormatDescriptor(token)) {
            nd.setFormatDescriptor(token);
          }
        }
      }
    } else {
      // Must be value holder alias
      nd.setValueHolderAlias(token);
      token = (String) iter.next(); // skip delimiter
      if (iter.hasNext()) {
        // get property
        token = (String) iter.next();
        if (!isTieBreaker(token)) {
          nd.setPropertyName(token);
          if (iter.hasNext()) {
            token = (String) iter.next(); // skip delimiter
            if (iter.hasNext()) {
              token = (String) iter.next(); // format or tie-breaker
              if (isFormatDescriptor(token)) {
                nd.setFormatDescriptor(token);
              }
            }
          }
        }
      }
    }
    if (nd.getFormatDescriptor() != null) {
      if (nd.getFormatDescriptor().startsWith("DATE:")) {
        nd.setFormat(new java.text.SimpleDateFormat());
        nd.setFormatPattern(nd.getFormatDescriptor().substring(5));
      } else if (nd.getFormatDescriptor().startsWith("NUMBER:")) {
        nd.setFormat(new DecimalFormat());
        nd.setFormatPattern(nd.getFormatDescriptor().substring(7));
      } else if (nd.getFormatDescriptor().startsWith("MSG:")) {
        nd.setFormat(new EmptyMessageFormat());
        nd.setFormatPattern(nd.getFormatDescriptor().substring(4));
      }
    }
    return nd;
  }
  
  private boolean isTieBreaker(String value) {
    return value.charAt(0) >= '0' && value.charAt(0) <= '9';
  }
  
  private boolean isFormatDescriptor(String value) {
    return value.startsWith("DATE:") ||
        value.startsWith("NUMBER:") || value.startsWith("MSG:");
  }
  
  private List tokenize(String value) {
    ArrayList list = new ArrayList();
    int pos = value.indexOf(ID_DELIMITER);
    while (pos > -1) {
      if (pos > 0) {
        list.add(value.substring(0, pos));
        list.add(ID_DELIMITER);
      } else {
        list.add(ID_DELIMITER);
      }
      value = value.substring(pos + 2);
      pos = value.indexOf(ID_DELIMITER);
    }
    if (value.length() > 0) {
      list.add(value);
    }
    return list;
  }

  /**
   * @param nd
   * @param element
   * @param holders
   * @param component
   * @return
   */
  protected boolean bindComponent(NodeDescriptor nd, Element element, ValueHolder[] holders, Component component) {
    boolean bound = false;
    if (component instanceof AbstractField) {
      bound = bindAbstractField(nd, element, holders,
          (AbstractField) component);
    } else if (component instanceof Template) {
      bound = bindTemplate(nd, element, holders, (Template) component);
    } else if (component instanceof BoundDocumentElement) {
      bound = bindDocumentElement(nd, element, holders,
          (BoundDocumentElement) component);
    }
    return bound;
  }

  /**
   * @param nd
   *          TODO
   * @param element
   * @param holders
   * @param component
   * @return TODO
   */
  private boolean bindTemplate(NodeDescriptor nd, Element element, ValueHolder[] holders, Template component) {
    component.setId(nd.getId());
    ValueHolder holder = findValueHolder(holders, nd.getValueHolderAlias(), nd.getId());
    boolean bound = holder != null;
    if (bound) {
      if (holder instanceof CollectionValueHolder) {
        component.setCollectionValueHolder((CollectionValueHolder) holder);
      } else {
        Debug.warn("ValueHolder for Template id=" + nd.getId() + 
            " is wrong class. Must implement CollectionValueHolder interface. " +
            "Given ValueHolder: " + holder);
        bound = false;
      }
    }
    bound = bindAttributes(nd.getId(), component, element, holders) || bound;
    bound = bindTextNode(nd.getId(), component, element, holders) || bound;
    return bound;
  }

  /**
   * @param nd TODO
   * @param element
   * @param holders
   * @param component
   */
  private boolean bindDocumentElement(NodeDescriptor nd, Element element, ValueHolder[] holders, BoundDocumentElement component) {
    component.setId(nd.getId());
    ValueHolder holder = findValueHolder(holders, nd.getValueHolderAlias(), nd.getId());
    boolean bound = holder != null;
    if (bound) {
      setupValueHolder(component, holder);
    }
    bound = bindAttributes(nd.getId(), component, element, holders) || bound;
    bound = bindTextNode(nd.getId(), component, element, holders) || bound;
    return bound;
  }

  /**
   * @param nd
   * @param element
   * @param holders
   * @param component
   */
  private boolean bindAbstractField(NodeDescriptor nd, Element element, ValueHolder[] holders, AbstractField component) {
    component.setId(nd.getId());
    setupPropertyName(component, nd.getPropertyName());
    setupParameterName(component, trim(element.getAttribute("name")));
    setupFormat(component, nd);
    ValueHolder holder = findValueHolder(holders, nd.getValueHolderAlias(), nd.getId());
    boolean bound = holder != null;
    if (bound) {
      setupValueHolder(component, holder);
    }
    bound = bindAttributes(nd.getId(), component, element, holders) || bound;
    bound = bindTextNode(nd.getId(), component, element, holders) || bound;
    // Backwards compatibility format
    if (component.getFormat() == null) {
      setupOldFormat(component, element);
    }
    return bound;
  }
  
  private boolean bindTextNode(String id, com.taursys.xml.Element element, Element node, ValueHolder[] holders) {
    String value = trim(DOM_1_20000929_DocumentAdapter.getElementText(node));
    NodeDescriptor nd = parseBindSyntax(value);
    ValueHolder holder = findValueHolder(holders, nd.getValueHolderAlias(), nd.getId());
    boolean bound = holder != null;
    if (bound) {      
      TextNode text = element.getTextNode();
      if (text == null) {
        element.createTextNode(nd.getPropertyName(), holder);
      } else {
        setupValueHolder(text, holder);
        setupPropertyName(text, nd.getPropertyName());
      }
      setupFormat(text, nd);
    } else if(nd.getPropertyName() != null) {
      TextNode text = element.getTextNode();
      if (text == null) {
        text = element.createBoundTextNode(nd.getPropertyName());
      } else {
        setupPropertyName(text, nd.getPropertyName());
      }
      setupFormat(text, nd);
    }
    return bound;
  }  
  
  private boolean bindAttributes(String id, com.taursys.xml.Element element, Node node, ValueHolder[] holders) {
    boolean anyBound = false;
    // Get all attributes for node
    NamedNodeMap attribs = node.getAttributes();
    for (int i = 0; i < attribs.getLength(); i++) {
      Node attribNode = attribs.item(i);
      String attribName = attribNode.getNodeName();
      String attribValue = attribNode.getNodeValue();
      if (!attribName.equals("id") && attribValue != null && attribValue.indexOf(ID_DELIMITER) > -1) {
        NodeDescriptor nd = parseBindSyntax(attribValue);
        ValueHolder holder = findValueHolder(holders, nd.getValueHolderAlias(), nd.getId());
        boolean bound = holder != null;
        anyBound = anyBound || bound;
        if (bound) {
          Attribute attribute = element.getAttribute(attribName);
          if (attribute == null) {
            attribute = 
              element.createAttribute(attribName, nd.getPropertyName(), holder);
          } else {
            setupValueHolder(attribute, holder);
            setupPropertyName(attribute, nd.getPropertyName());
          }
          setupFormat(attribute, nd);
        } else if(nd.getPropertyName() != null) {
          Attribute attribute = element.getAttribute(attribName);
          if (attribute == null) {
            attribute = 
              element.createBoundAttribute(attribName, nd.getPropertyName());
          } else {
            setupPropertyName(attribute, nd.getPropertyName());
          }
          setupFormat(attribute, nd);
        }
      }
    }
    return anyBound;
  }
  
  private void setupValueHolder(Parameter p, ValueHolder holder) {
    if (p.getValueHolder() == null || 
        p.getValueHolder() instanceof VariantValueHolder) {
      p.setValueHolder(holder);
    }
  }
  
  private void setupValueHolder(BoundDocumentElement p, ValueHolder holder) {
    if (p.getValueHolder() == null || 
        p.getValueHolder() instanceof VariantValueHolder) {
      p.setValueHolder(holder);
    }
  }

  private void setupPropertyName(Parameter p, String propertyName) {
    if (p.getPropertyName() == null) {
      p.setPropertyName(propertyName);
    }
  }
  
  private void setupParameterName(Parameter p, String parameterName) {
    if (p.getParameter() == null) {
      p.setParameter(parameterName);
    }
  }
  
  private void setupFormat(Parameter component, NodeDescriptor nd) {
    if (component.getFormat() == null) {
      component.setFormat(nd.getFormat());
    }
    if (component.getFormatPattern() == null) {
      component.setFormatPattern(nd.getFormatPattern());
    }
  }
    
  private String trim(String value) {
    return value == null || value.trim().length() == 0 ? null : value.trim();
  }
  
  private ValueHolder findValueHolder(ValueHolder[] holders, String alias, 
      String id) {
    if (alias != null) {
      for (int i = 0; i < holders.length; i++) {
        if (alias.equals(holders[i].getAlias())) {
          return holders[i];
        }
      }
      // Not found
      Debug.warn("ValueHolder not found for alias: " + alias + 
          " for Component id=" + id);
    }
    return null;
  }

  /**
   * Setup the Format and FormatPattern from contents in the
   * HTML document. Looks for the formatting information in
   * the value or href attributes first, then in the text node.
   * The following formats are supported:
   * 
   * DATE: java.text.SimpleDateFormat
   * NUMBER: java.text.DecimalFormat
   * MSG: java.text.MessageFormat
   * 
   * The patterns are as specified in the JavaDoc for the above
   * formats. This method supports the following syntax to 
   * specify the format: [ DATE: | NUMBER: | MSG: ] formatPattern.
   * 
   * Example: "DATE:MM/dd/yyyy"
   * 
   * @param field the field being formatted
   * @param element the HTML element containing the format info
   */
  private void setupOldFormat(AbstractField field, Element element) {
    // Try to get format from value attribute or text node
    String formatInfo = element.getAttribute("value");
    if (formatInfo == null || formatInfo.length() == 0)
      formatInfo = element.getAttribute("href");
    if (formatInfo == null || formatInfo.length() == 0)
      formatInfo = DOM_1_20000929_DocumentAdapter.getElementText(element);
    // Determine what kind of format this is
    if (formatInfo != null && formatInfo.length() > 0) {
      if (formatInfo.startsWith("DATE:")) {
        field.setFormat(new java.text.SimpleDateFormat());
        field.setFormatPattern(formatInfo.substring(5));
      } else if (formatInfo.startsWith("NUMBER:")) {
        field.setFormat(new DecimalFormat());
        field.setFormatPattern(formatInfo.substring(7));
      } else if (formatInfo.startsWith("MSG:")) {
        field.setFormat(new EmptyMessageFormat());
        field.setFormatPattern(formatInfo.substring(4));
      }
    }
  }
  
  private void traceCreateComponents(String msg) {
    Debug.debug("ComponentFactory.createComponents: " + msg);
  }
}
