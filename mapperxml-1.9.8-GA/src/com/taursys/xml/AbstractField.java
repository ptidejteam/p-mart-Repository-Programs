/**
 * AbstractField - A Component which receives InputEvents and/or renders value in a Document
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
import com.taursys.util.StringUtil;
import com.taursys.xml.event.InputEvent;
import com.taursys.xml.event.ParameterEvent;
import com.taursys.xml.event.RecycleEvent;
import com.taursys.xml.event.RenderEvent;
import com.taursys.xml.event.RenderException;

/**
 * A Component which receives input and/or renders value to a Document. It
 * responds to InputEvents and holds the given value.  By default, this
 * component uses a VariantTextModel. You can change this by overriding the
 * createDefaultModel method or explicitly setting the model property.
 * <p>
 * This component can render its value to an xml Document (thru the
 * DocumentAdapter). The id property indicates which element to render in
 * the Document.  The attributeName property is only used with an
 * AttributeTextFieldRenderer to indicate which attribute to store the value
 * in.  Subclasses must override the processRenderEvent method
 * and should delegate the work to a rendering subcomponent.
 */
public abstract class AbstractField extends Parameter
    implements Element {
  private String id;
  private String attributeName = "value";
  private boolean earlyInputNotify;
  private ElementDelegate elementDelegate = new ElementDelegate(this);

  // *************************************************************************
  //                               Constructors
  // *************************************************************************

  /**
   * Constructs a new AbstractField with a default model.
   * The default model, a VariantTextModel, is created via the
   * createDefaultModel method in the Parameter superclass.
   */
  public AbstractField() {
    super();
    init();
  }

  /**
   * Creates a new AbstractField with a DefaultTextModel and VariantValueHolder of the given type.
   * See com.taursys.util.DataTypes for defined data type constants TYPE_XXXXXX.
   */
  public AbstractField(int javaDataType) {
    super(javaDataType);
    init();
  }
  
  private void init() {
    removeEventType(ParameterEvent.class.getName());
    addEventType(InputEvent.class.getName());
    addEventType(RenderEvent.class.getName());
    addEventType(RecycleEvent.class.getName());
    elementDelegate.setValueHolder(getValueHolder());
  }
 
  // ************************************************************************
  //                       Event Support Methods
  // ************************************************************************

  /**
   * Store value and fires input event if event has correct input name.
   */
  protected void processInputEvent(InputEvent e) throws Exception {
    if (getParameter() != null && getParameter().equals(e.getName())) {
      getModel().setText(e.getValue());
      fireInputReceived(e);
    }
  }

  /**
   * Responds to a render event for this component. Subclasses must override
   * the processRenderEvent method and should delegate the work to a
   * rendering subcomponent.
   */
  public abstract void processRenderEvent(RenderEvent e) throws RenderException;

  /**
   * Set flag for early input notification.  Normally this field registers to
   * receive InputEvents.  If earlyInputNofity is true, it will register to
   * receive ParameterEvents instead.  It will then be notified of input at the
   * same time as Parameter components. This method will also force new
   * registration if isNotifySet is true.
   * @param earlyInputNotify flag for early input notification.
   */
  public void setEarlyInputNotify(boolean earlyInputNotify) {
    boolean b = isNotifySet(); // save old state
    if (isNotifySet())
      removeNotify();
    this.earlyInputNotify = earlyInputNotify;
    if (earlyInputNotify) {
      removeEventType(InputEvent.class.getName());
      addEventType(ParameterEvent.class.getName());
    } else {
      removeEventType(ParameterEvent.class.getName());
      addEventType(InputEvent.class.getName());
    }
    if (b)
      addNotify(); // only if was originally set
  }

  /**
   * Get flag for early input notification.  Normally this field registers to
   * receive InputEvents.  If earlyInputNofity is true, it will register to
   * receive ParameterEvents instead.  It will then be notified of input at the
   * same time as Parameter components. This method will also force new
   * registration if isNotifySet is true.
   * @return earlyInputNotify flag for early input notification.
   */
  public boolean isEarlyInputNotify() {
    return earlyInputNotify;
  }

  // ************************************************************************
  //                       Property Accessors
  // ************************************************************************

  /**
   * Sets the id of the Element this component is bound to.  This is the Element
   * where the component will render its value.
   */
  public void setId(String newId) {
    id = newId;
  }

  /**
   * Returns the id of the Element this component is bound to.  This is the Element
   * where the component will render its value.
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the name of the Element's attribute where the value should be rendered.
   * This property is only used when a AttributeTextFieldRenderer is used.  The
   * default is "value".
   */
  public void setAttributeName(String newAttributeName) {
    attributeName = newAttributeName;
  }

  /**
   * Returns the name of the Element's attribute where the value should be rendered.
   * This property is only used when a AttributeTextFieldRenderer is used.  The
   * default is "value".
   */
  public String getAttributeName() {
    return attributeName;
  }

  /**
   * Get the ElementDelegate for this component.
   * @return the ElementDelegate for this component.
   */
  public ElementDelegate getElementDelegate() {
    return elementDelegate;
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
    String result = StringUtil.identityString(this);
    result += " id=" + id;
    result += " parent=" + StringUtil.identityString(parent);
    result += " visible=" + isVisible();
    result += " attributeName=" + attributeName;
    result += " earlyInputNotify=" + earlyInputNotify;
    result += " parameter=" + getParameter();
    result += " defaultValue=" + getDefaultValue();
    result += " model=[" + getModel() + "]";
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
  //           Override Methods to Support Attributes
  // =======================================================================

  public void addNotify() {
    super.addNotify();
    elementDelegate.addNotify();
  }
  
  public void removeNotify() {
    super.removeNotify();
    elementDelegate.removeNotify();
  }

  /* (non-Javadoc)
   * @see com.taursys.xml.Parameter#setValueHolder(com.taursys.model.ValueHolder)
   */
  public void setValueHolder(ValueHolder newValueHolder) {
    super.setValueHolder(newValueHolder);
    elementDelegate.setValueHolder(newValueHolder);
  }
}
