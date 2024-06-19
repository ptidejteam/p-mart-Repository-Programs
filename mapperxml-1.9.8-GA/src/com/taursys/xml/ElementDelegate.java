/**
 * ElementDelegate - A delegate that implements the Element interface
 *
 * Copyright (c) 2005
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

import java.util.ArrayList;
import java.util.Iterator;

import com.taursys.model.ModelException;
import com.taursys.model.ValueHolder;

/**
 * ElementDelegate serves as a delegate for Elements by providing
 * a full implementation of the Element interface.
 * @author marty
 */
public class ElementDelegate extends DispatchingContainer implements Element {
  private Component parentComponent;
  private ValueHolder valueHolder = null;
  private ArrayList boundAttributes = new ArrayList();
  private ArrayList boundTextNodes = new ArrayList();
  private TextNode textNode;

  /**
   * Constructs a new ElementDelegate for the given Element
   */
  public ElementDelegate(Element c) {
    parentComponent = (Component)c;
  }
  
  // =======================================================================
  //                     PROPERTY ACCESSOR METHODS
  // =======================================================================

  /**
   * Returns the parent container of this component else null.
   */
  public com.taursys.xml.Container getParent() {
    if (parentComponent instanceof Container) {
      return (Container)parentComponent;
    } else {
      return parentComponent.getParent();
    }
  }

  // =========================================================================
  //              Element Interface Implementation
  // =========================================================================

  /**
   * Get the Id of the this container's parent
   * return the Id of the this container's parent
   */
  public String getId() {
    return ((DocumentComponent)parentComponent).getId();
  }

  /**
   * Un-implemented method - do not call - use parent's method
   * instead.
   * @param newId unused parameter
   */
  public void setId(String newId) {
  }

  // =====================================================================
  //            Attribute Support 
  // =====================================================================
  
  /* (non-Javadoc)
   * @see com.taursys.xml.Element#addAttribute(com.taursys.xml.Attribute)
   */
  public void addAttribute(Attribute attribute) {
    if (getAttribute(attribute.getAttributeName()) == null) {
      super.add(attribute);
    }
  }

  /* (non-Javadoc)
   * @see com.taursys.xml.Element#removeAttribute(java.lang.String)
   */
  public void removeAttribute(String attributeName) {
    Attribute a = getAttribute(attributeName);
    if (a != null) {
      removeAttribute(a);
    }
  }
  
  /* (non-Javadoc)
   * @see com.taursys.xml.Element#removeAttribute(com.taursys.xml.Attribute)
   */
  public void removeAttribute(Attribute attribute) {
    super.remove(attribute);
    boundAttributes.remove(attribute);
  }

  /* (non-Javadoc)
   * @see com.taursys.xml.Element#getAttribute(java.lang.String)
   */
  public Attribute getAttribute(String attributeName) {
    if (attributeName != null) {
      Component[] cx = getComponents();
      for (int i = 0; i < cx.length; i++) {
        if (cx[i] instanceof Attribute
            && attributeName.equals(((Attribute) cx[i]).getAttributeName())) {
          return (Attribute) cx[i];
        }
      }
    }
    return null;
  }

  /* (non-Javadoc)
   * @see com.taursys.xml.Element#getAttributeText(java.lang.String)
   */
  public String getAttributeText(String attributeName) throws ModelException {
    Attribute a = getAttribute(attributeName);
    if (a != null) {
      return a.getText();
    } else {
      return null;
    }
  }

  /* (non-Javadoc)
   * @see com.taursys.xml.Element#setAttributeText(java.lang.String, java.lang.String)
   */
  public void setAttributeText(String attributeName, String value) throws ModelException {
    Attribute a = getAttribute(attributeName);
    if (a == null) {
      a = createAttribute(attributeName);
    }
    a.setText(value);
  }

  /* (non-Javadoc)
   * @see com.taursys.xml.Element#getAttributeValue(java.lang.String)
   */
  public Object getAttributeValue(String attributeName) throws ModelException {
    Attribute a = getAttribute(attributeName);
    if (a != null) {
      return a.getValue();
    } else {
      return null;
    }
  }

  /* (non-Javadoc)
   * @see com.taursys.xml.Element#setAttributeValue(java.lang.String, java.lang.Object)
   */
  public void setAttributeValue(String attributeName, Object value) throws ModelException {
    Attribute a = getAttribute(attributeName);
    if (a == null) {
      a = createAttribute(attributeName);
    }
    a.setValue(value);
  }

  /* (non-Javadoc)
   * @see com.taursys.xml.Element#createAttribute(java.lang.String)
   */
  public Attribute createAttribute(String attributeName) {
    Attribute a = getAttribute(attributeName);
    if (a == null) {
      a = new Attribute();
      a.setAttributeName(attributeName);
      addAttribute(a);
    }
    return a;
  }

  /* (non-Javadoc)
   * @see com.taursys.xml.Element#createAttribute(java.lang.String, int)
   */
  public Attribute createAttribute(String attributeName, int dataType) {
    Attribute a = getAttribute(attributeName);
    if (a == null) {
      a = new Attribute(dataType);
      a.setAttributeName(attributeName);
      addAttribute(a);
    }
    return a;
  }

  /* (non-Javadoc)
   * @see com.taursys.xml.Element#createAttribute(java.lang.String, java.lang.String)
   */
  public Attribute createBoundAttribute(String attributeName, String propertyName) {
    Attribute a = getAttribute(attributeName);
    if (a == null) {
      a = new Attribute();
      a.setAttributeName(attributeName);
      addAttribute(a);
    }
    a.setPropertyName(propertyName);
    boundAttributes.add(a);
    if (valueHolder != null) {
      a.setValueHolder(valueHolder);
    }
    return a;
  }

  /* (non-Javadoc)
   * @see com.taursys.xml.Element#createAttribute(java.lang.String, java.lang.String, com.taursys.model.ValueHolder)
   */
  public Attribute createAttribute(String attributeName, String propertyName, ValueHolder holder) {
    Attribute a = getAttribute(attributeName);
    if (a == null) {
      a = new Attribute();
      a.setAttributeName(attributeName);
      addAttribute(a);
    }
    a.setPropertyName(propertyName);
    a.setValueHolder(holder);
    boundAttributes.remove(a);
    return a;
  }

  // =====================================================================
  //            TextNode Support 
  // =====================================================================
  
  /* (non-Javadoc)
   * @see com.taursys.xml.Element#addTextNode(com.taursys.xml.TextNode)
   */
  public void addTextNode(TextNode t) {
    if (this.textNode == null) {
      super.add(t);
      this.textNode = t;
    }
  }

  /* (non-Javadoc)
   * @see com.taursys.xml.Element#removeTextNode(com.taursys.xml.TextNode)
   */
  public void removeTextNode(TextNode t) {
    if (t == this.textNode) {
      super.remove(t);
      boundTextNodes.remove(t);
      this.textNode = null;
    }
  }
  
  /* (non-Javadoc)
   * @see com.taursys.xml.Element#removeTextNode(com.taursys.xml.TextNode)
   */
  public void removeTextNode() {
    if (this.textNode != null) {
      super.remove(textNode);
      boundTextNodes.remove(textNode);
      this.textNode = null;
    }
  }
  
  /* (non-Javadoc)
   * @see com.taursys.xml.Element#getTextNode()
   */
  public TextNode getTextNode() {
    return this.textNode;
  }

  /* (non-Javadoc)
   * @see com.taursys.xml.Element#getTextNodeText()
   */
  public String getTextNodeText() throws ModelException {
    if (this.textNode != null) {
      return textNode.getText();
    } else {
      return null;
    }
  }

  /* (non-Javadoc)
   * @see com.taursys.xml.Element#setTextNodeText(java.lang.String)
   */
  public void setTextNodeText(String value) throws ModelException {
    createTextNode().setText(value);
  }

  /* (non-Javadoc)
   * @see com.taursys.xml.Element#getTextNodeValue()
   */
  public Object getTextNodeValue() throws ModelException {
    if (this.textNode != null) {
      return textNode.getValue();
    } else {
      return null;
    }
  }

  /* (non-Javadoc)
   * @see com.taursys.xml.Element#setTextNodeValue(java.lang.Object)
   */
  public void setTextNodeValue(Object value) throws ModelException {
    createTextNode().setValue(value);
  }

  /* (non-Javadoc)
   * @see com.taursys.xml.Element#createTextNode()
   */
  public TextNode createTextNode() {
    TextNode t = this.textNode;
    if (t == null) {
      t = new TextNode();
      addTextNode(t);
    }
    return t;
  }

  /* (non-Javadoc)
   * @see com.taursys.xml.Element#createTextNode(int)
   */
  public TextNode createTextNode(int dataType) {
    TextNode t = this.textNode;
    if (t == null) {
      t = new TextNode(dataType);
      addTextNode(t);
    }
    return t;
  }

  /* (non-Javadoc)
   * @see com.taursys.xml.Element#createTextNode(java.lang.String)
   */
  public TextNode createBoundTextNode(String propertyName) {
    TextNode t = this.textNode;
    if (t == null) {
      t = new TextNode();
      addTextNode(t);
    }
    t.setPropertyName(propertyName);
    boundTextNodes.add(t);
    if (valueHolder != null) {
      t.setValueHolder(valueHolder);
    }
    return t;
  }

  /* (non-Javadoc)
   * @see com.taursys.xml.Element#createTextNode(java.lang.String, com.taursys.model.ValueHolder)
   */
  public TextNode createTextNode(String propertyName, ValueHolder holder) {
    TextNode t = this.textNode;
    if (t == null) {
      t = new TextNode();
      addTextNode(t);
    }
    t.setPropertyName(propertyName);
    t.setValueHolder(holder);
    boundTextNodes.remove(t);
    return t;
  }

  // =====================================================================
  //            Bound Value Holder Support 
  // =====================================================================
  
  /**
   * Sets the given ValueHolder for this ElementDelegate and sets the
   * ValueHolder of each of its bound Attributes and TextNodes. 
   */
  public void setValueHolder(ValueHolder valueHolder) {
    this.valueHolder = valueHolder;
    for (Iterator iter = boundAttributes.iterator(); iter.hasNext();) {
      Attribute a = (Attribute) iter.next();
      a.setValueHolder(valueHolder);
    }
    for (Iterator iter = boundTextNodes.iterator(); iter.hasNext();) {
      TextNode a = (TextNode) iter.next();
      a.setValueHolder(valueHolder);
    }
  }

}
