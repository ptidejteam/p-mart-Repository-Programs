/**
 * Element - A type of Component which binds to the Document with Attributes
 *
 * Copyright (c) 2002-2005
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

import com.taursys.model.ModelException;
import com.taursys.model.ValueHolder;

/**
 * An Element is a type of Component which binds to the Document and 
 * can have Attributes.
 * @author Marty Phelan
 * @version 2.0
 */
public interface Element extends DocumentComponent {
  
  /**
   * Add the given Attribute to this Element.  No action is taken if the 
   * attribute already belongs to this Element.  If the Attribute belongs to 
   * another Element as indicated by its parent, it is first removed from 
   * the old Element before it is added to this Element.  Finally, it is 
   * added to this Element and its parent is set to this Element.  The 
   * Attribute's addNotify method is also invoked so it will be notified 
   * of events it is interested in.
   * @param attribute the Attribute to add
   */
  public void addAttribute(Attribute attribute);
  
  /**
   * Removes the given Attribute from this Element and sets its parent to null.
   * Also invokes removeNotify on the given Attribute so it will un-register
   * itself with any dispatchers.
   * @param attribute the Attribute to remove
   */
  public void removeAttribute(Attribute attribute);

  /**
   * Gets an Attribute by name or null if it does not exist
   * @param attributeName the name of the Attribute
   * @return an Attribute by name or null if it does not exist
   */
  public Attribute getAttribute(String attributeName);

  /**
   * Gets an Attribute's text value by name or null if it does not exist
   * @param attributeName the name of the Attribute
   * @return an Attribute's text value by name or null if it does not exist
   */
  public String getAttributeText(String attributeName) throws ModelException;

  /**
   * Sets an Attribute's text value by name or does nothing if it does not exist.
   * Logs a warning if attribute does not exist.
   * @param attributeName the name of the Attribute
   * @param value the new String value for the Attribute
   */
  public void setAttributeText(String attributeName, String value) throws ModelException;

  /**
   * Gets an Attribute's Object value by name or null if it does not exist
   * @param attributeName
   * @return
   */
  public Object getAttributeValue(String attributeName) throws ModelException;
  
  /**
   * Sets an Attribute's Object value by name or does nothing if it does not exist.
   * Logs a warning if attribute does not exist.
   * @param attributeName
   * @param value
   */
  public void setAttributeValue(String attributeName, Object value) throws ModelException;

  /**
   * Creates and binds an Attribute with the given name to this Element.
   * If the Attribute already exists, the existing one is returned.
   * @param attributeName name of the new Attribute
   * @return the newly created and bound Attribute
   */
  public Attribute createAttribute(String attributeName);

  /**
   * Creates and binds an Attribute of the given type with the given 
   * name to this Element.
   * If the Attribute already exists, the existing one is returned.
   * @param attributeName name of the new Attribute
   * @param dataType the data type for the new Attribute
   * @return the newly created and bound Attribute
   * @see com.taursys.util.DataTypes
   */
  public Attribute createAttribute(String attributeName, int dataType);

  /**
   * Creates and binds an Attribute with the given name to this Element.
   * The Attribute is bound to this Element's valueHolder with the
   * given propertyName.
   * If the Attribute already exists, the propertyName is changed
   * and the existing Attribute is returned.
   * @param attributeName name of the new Attribute
   * @param propertyName for the new Attribute
   * @return the newly created and bound Attribute
   */
  public Attribute createBoundAttribute(String attributeName, String propertyName);

  /**
   * Creates and binds an Attribute with the given name to this Element.
   * The Attribute is bound to the given valueHolder with the
   * given propertyName.
   * If the Attribute already exists, the valueHolder and propertyName 
   * are changed and the existing Attribute is returned.
   * The Attribute is also un-bound from this Element's valueHolder.
   * @param attributeName name of the new Attribute
   * @param propertyName for the new Attribute
   * @param holder ValueHolder for the new Attribute
   * @return the newly created and bound Attribute
   */
  public Attribute createAttribute(String attributeName, String propertyName, ValueHolder holder);

  /**
   * Removes the Attribute with the given name from this Container if found.
   * It does NOT alter the properties of the Attribute in any way.
   * @param attributeName to remove
   */
  public void removeAttribute(String attributeName);
  
  /**
   * Add the given TextNode to this Element.  No action is taken if a 
   * textNode already belongs to this Element.  If the TextNode belongs to 
   * another Element as indicated by its parent, it is first removed from 
   * the old Element before it is added to this Element.  Finally, it is 
   * added to this Element and its parent is set to this Element.  The 
   * TextNode's addNotify method is also invoked so it will be notified 
   * of events it is interested in.
   * @param textNode the TextNode to add
   */
  public void addTextNode(TextNode t);

  /**
   * Removes the given TextNode from this Element and sets its parent to null.
   * Also invokes removeNotify on the given TextNode so it will un-register
   * itself with any dispatchers.
   * @param textNode the TextNode to remove
   */
  public void removeTextNode(TextNode t);
  
  /**
   * Removes the TextNode from this Container if exists.
   * It does NOT alter the properties of the TextNode in any way.
   */
  public void removeTextNode();
  
  /**
   * Gets the TextNode or null if it does not exist
   * @return the TextNode or null if it does not exist
   */
  public TextNode getTextNode();

  /**
   * Gets the TextNode's text value or null if it does not exist
   * @return the TextNode's text value or null if it does not exist
   */
  public String getTextNodeText() throws ModelException;
  
  /**
   * Sets the TextNode's text value or does nothing if it does not exist.
   * @param value the new String value for the TextNode
   */
  public void setTextNodeText(String value) throws ModelException;
  
  /**
   * Gets the TextNode's Object value or null if it does not exist
   * @return the TextNode's Object value or null if it does not exist
   */
  public Object getTextNodeValue() throws ModelException;

  /**
   * Sets the TextNode's Object value or does nothing if it does not exist.
   * @param value the new Object value for the TextNode
   */
  public void setTextNodeValue(Object value) throws ModelException;

  /**
   * Creates and binds a TextNode to this Element.
   * If the TextNode already exists, the existing one is returned.
   * @return the newly created and bound TextNode
   */
  public TextNode createTextNode();

  /**
   * Creates and binds a TextNode of the given type to this Element.
   * If the TextNode already exists, the existing one is returned.
   * @param dataType the data type for the new TextNode
   * @return the newly created and bound TextNode
   * @see com.taursys.util.DataTypes
   */
  public TextNode createTextNode(int dataType);

  /**
   * Creates and binds a TextNode with the given name to this Element.
   * The TextNode is bound to this Element's valueHolder with the
   * given propertyName.
   * If the TextNode already exists, the propertyName is changed
   * and the existing TextNode is returned.
   * @param textNodeName name of the new TextNode
   * @param propertyName for the new TextNode
   * @return the newly created and bound TextNode
   */
  public TextNode createBoundTextNode(String propertyName);

  /**
   * Creates and binds a TextNode with the given name to this Element.
   * The TextNode is bound to the given valueHolder with the
   * given propertyName.
   * If the TextNode already exists, the valueHolder and propertyName 
   * are changed and the existing TextNode is returned.
   * The TextNode is also un-bound from this Element's valueHolder.
   * @param textNodeName name of the new TextNode
   * @param propertyName for the new TextNode
   * @param holder ValueHolder for the new TextNode
   * @return the newly created and bound TextNode
   */
  public TextNode createTextNode(String propertyName, ValueHolder holder);

}
