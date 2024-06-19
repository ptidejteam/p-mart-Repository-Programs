/**
 * Button - A visible Trigger
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
package com.taursys.xml;

import com.taursys.model.ModelException;
import com.taursys.model.ValueHolder;
import com.taursys.xml.event.RenderEvent;
import com.taursys.xml.event.RenderException;
import com.taursys.xml.render.VisibleRenderer;

/**
 * Button is a visible Trigger
 * @author Marty Phelan
 * @version 1.0
 */
public class Button extends Trigger implements Element {
  private String id;
  private VisibleRenderer renderer;
  private ElementDelegate elementDelegate = new ElementDelegate(this);

  /**
   * Constructs a new Button
   */
  public Button() {
    super();
    addEventType(RenderEvent.class.getName());
    renderer = createDefaultRenderer();
  }

  /**
   * Returns the id of the node this component is bound to.  This is the node
   * which this component will replicate.
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id of the node this component is bound to.  This is the node
   * which this component will replicate.
   */
  public void setId(String newId) {
    id = newId;
  }

  /**
   * Get the ElementDelegate for this component.
   * @return the ElementDelegate for this component.
   */
  public ElementDelegate getElementDelegate() {
    return elementDelegate;
  }

  /**
   * Creates the default TextFieldRenderer for this component.
   * By Default this methos returns a new TextFieldRenderer.
   * Override this method to define your own TextFieldRenderer.
   */
  protected VisibleRenderer createDefaultRenderer() {
    return new VisibleRenderer(this);
  }

  /**
   * Get the Renderer for this component.
   * @return the Renderer for this component.
   */
  public VisibleRenderer getRenderer() {
    return renderer;
  }

  /**
   * Responds to a render event for this component.  This uses the renderer
   * subcomponent to actually render the value. It first notifies any
   * RenderListeners of the event. It then invokes the renderer subcomponent
   * to render the value to the document.
   * @param e the current render event message
   * @throws RenderException if problem rendering value to document
   */
  public void processRenderEvent(RenderEvent e) throws RenderException {
    fireRender(e);
    renderer.render();
  }

  // =======================================================================
  //                       DIAGNOSTIC METHODS
  // =======================================================================

  /**
   * Returns a string representation of this object. This contains
   * the object identity and state information.
   * @return a string representation of this object
   */
  public String toString() {
    String result = super.toString();
    result += " id=" + id;
    return  result; 
  }

  // =======================================================================
  //                Implementation of Element Interface
  //                     Attribute Related Methods
  //                  Delegate to ElementDelegate
  // =======================================================================

  public void addAttribute(Attribute attribute) {
    elementDelegate.addAttribute(attribute);
  }

  public void removeAttribute(Attribute attribute) {
    elementDelegate.removeAttribute(attribute);
  }
  
  public Attribute createAttribute(String attributeName) {
    return elementDelegate.createAttribute(attributeName);
  }

  public Attribute createAttribute(String attributeName, int dataType) {
    return elementDelegate.createAttribute(attributeName, dataType);
  }

  public Attribute createBoundAttribute(String attributeName, String propertyName) {
    return elementDelegate.createBoundAttribute(attributeName, propertyName);
  }

  public Attribute createAttribute(String attributeName, String propertyName,
      ValueHolder holder) {
    return elementDelegate
        .createAttribute(attributeName, propertyName, holder);
  }

  public Attribute getAttribute(String attributeName) {
    return elementDelegate.getAttribute(attributeName);
  }

  public String getAttributeText(String attributeName) throws ModelException {
    return elementDelegate.getAttributeText(attributeName);
  }

  public Object getAttributeValue(String attributeName) throws ModelException {
    return elementDelegate.getAttributeValue(attributeName);
  }

  public void removeAttribute(String attributeName) {
    elementDelegate.removeAttribute(attributeName);
  }

  public void setAttributeText(String attributeName, String value) throws ModelException {
    elementDelegate.setAttributeText(attributeName, value);
  }

  public void setAttributeValue(String attributeName, Object value) throws ModelException {
    elementDelegate.setAttributeValue(attributeName, value);
  }
  
  public void addTextNode(TextNode t) {
    elementDelegate.addTextNode(t);
  }

  public TextNode createBoundTextNode(String propertyName) {
    return elementDelegate.createBoundTextNode(propertyName);
  }

  public TextNode createTextNode() {
    return elementDelegate.createTextNode();
  }

  public TextNode createTextNode(int dataType) {
    return elementDelegate.createTextNode(dataType);
  }

  public TextNode createTextNode(String propertyName, ValueHolder holder) {
    return elementDelegate.createTextNode(propertyName, holder);
  }

  public TextNode getTextNode() {
    return elementDelegate.getTextNode();
  }

  public String getTextNodeText() throws ModelException {
    return elementDelegate.getTextNodeText();
  }

  public Object getTextNodeValue() throws ModelException {
    return elementDelegate.getTextNodeValue();
  }

  public void removeTextNode() {
    elementDelegate.removeTextNode();
  }

  public void removeTextNode(TextNode t) {
    elementDelegate.removeTextNode(t);
  }

  public void setTextNodeText(String value) throws ModelException {
    elementDelegate.setTextNodeText(value);
  }

  public void setTextNodeValue(Object value) throws ModelException {
    elementDelegate.setTextNodeValue(value);
  }

  // =======================================================================
  //           Override Notify Methods to Support Attributes
  // =======================================================================

  public void addNotify() {
    super.addNotify();
    elementDelegate.addNotify();
  }
  
  public void removeNotify() {
    super.removeNotify();
    elementDelegate.removeNotify();
  }

}
