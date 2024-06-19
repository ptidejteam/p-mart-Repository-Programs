/**
 * TextField - A Component which receives InputEvents and/or renders text value in a Document
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

import com.taursys.model.TextModel;
import com.taursys.xml.event.RenderEvent;
import com.taursys.xml.event.RenderException;
import com.taursys.xml.render.VisibleRenderer;

/**
 * A component which can receive input and/or render a value to an XML document
 * element. This component can function in three different ways, depending on the
 * properties you set:
 *
 * <ul>
 *  <li>input only - set the <code>parameter</code> property.</li>
 *  <li>output only - set the <code>id</code> property.</li>
 *  <li>input and output - set both the <code>parameter</code> and
 *  <code>id</code> properties.</li>
 * </ul>
 *
 * <p>When used for output, the value is rendered in the XML document element as a
 * text node by default. You can also render values to the element's attributes.
 * This can be done by invoking the <code>setAttributeValue</code> method. The
 * following example illustrates this:</p>
 * 
 * <pre>
 * private TextField projectStatus = new TextField();
 *
 * private void jbInit() throws Exception {
 *   projectStatus.setId("projectStatus");
 *   ...
 *   this.add(projectStatus);
 * }
 *
 * protected void openForm() throws Exception {
 *   ...
 *   projectStatus.setValue("on-schedule");
 *   projectStatus.setAttributeValue("bgcolor","green");
 *   ...
 * }
 * </pre>
 *
 * <p>After rendering the HTML document would appear as follows:</p>
 *
 * <pre>
 * ...
 * &lt;table&gt;
 *   &lt;tr&gt;
 *     &lt;td id="projectStatus" bgcolor="green"&gt;on-schedule&lt;/td&gt;
 *     ...
 * </pre>
 *
 * <p>When used for input, this component receives its value from the
 * <code>InputDispatcher</code> AFTER the <code>openForm</code> method of the
 * <code>ServletForm</code> by default.  If you want this component to receive
 * its input earlier (at the same time as <code>Parameters</code>), set the
 * <code>earlyInputNotify</code> property to <code>true</code>.</p>
 *
 * <p>By default, this component uses an internal <code>DefaultTextModel</code>
 * with a <code>VariantValueHolder</code> and a default data type of
 * <code>String</code>. You can specify a different data type when you invoke
 * the constructor of this component. Below is an example:</p>
 *
 * <pre>
 *   TextField salary = new TextField(DataTypes.TYPE_BIGDECIMAL);
 * </pre>
 *
 * <p>This component can also be bound to an external <code>ValueHolder</code>.
 * An external <code>ValueHolder</code> can be shared by multiple components.
 * The <code>propertyName</code> specifies which property in the
 * <code>ValueHolder</code> will be bound to this component. To bind this
 * component, set the <code>valueHolder</code> and <code>propertyName</code>
 * properties. You do not need to specify a data type when you bind to a
 * <code>ValueHolder</code>. The following is an example of binding:</p>
 *
 * <pre>
 *   TextField personId = new TextField(DataTypes.TYPE_INT);
 *   TextField lastName = new TextField();
 *   TextField city = new TextField();
 *   VOValueHolder holder = new VOValueHolder();
 *
 *   private void jbInit() throws Exception {
 *     holder.setValueObjectClass(PersonVO.class);
 *     ...
 *     personId.setParameter("personId");
 *     personId.setId("personId");
 *     personId.setEarlyInputNotify(true);
 *     ...
 *     lastName.setId("lastName");
 *     lastName.setValueHolder(holder);
 *     lastName.setPropertyName("lastName");
 *     ...
 *     city.setId("city");
 *     city.setValueHolder(holder);
 *     city.setPropertyName("address.city");
 *     ...
 *     this.add(lastName);
 *     this.add(city);
 *   }
 *
 *   protected void openForm() throws java.lang.Exception {
 *     holder.setValueObject(
 *         delegate.getPerson((Integer)personId.getValue());
 *     ...
 *   }
 * </pre>
 * <p>Notes: In the above example, the personId field is used as a parameter
 * and display field. By setting its <code>earlyInputNotify</code> property
 * to true, it will have its value available when the <code>openForm</code>
 * method is invoked. It is not bound to the holder. The lastName field
 * functions as a display only field. It is not configured as an input field.
 * The same is true of the city field. Note the city field's property name:
 * "address.city". The PersonVO has an "address" property of type AddressVO.
 * The AddressVO in turn has a "city" property. The dot notation supports
 * multiple levels of indirection.</p>
 *
 * @see com.taursys.model.ValueHolder
 * @see Parameter
 */
public class TextField extends AbstractField {
  private VisibleRenderer renderer;

  /**
   * Constructs a new TextField with a default model and renderer.
   * The default model, a VariantTextModel, is created via the
   * createDefaultModel method in the Parameter superclass.
   */
  public TextField() {
    super();
    renderer = createDefaultRenderer();
    createTextNode().setModel(this.getModel());
  }

  /**
   * Creates a new TextField with a DefaultTextModel and VariantValueHolder of the given type.
   * See com.taursys.util.DataTypes for defined data type constants TYPE_XXXXXX.
   */
  public TextField(int javaDataType) {
    super(javaDataType);
    renderer = createDefaultRenderer();
    createTextNode(javaDataType).setModel(this.getModel());
  }

  /**
   * Creates the default VisibleRenderer for this component.
   * By Default this methos returns a new VisibleRenderer.
   * Override this method to define your own VisibleRenderer.
   * @deprecated The TextFieldRender is no longer used. A TextField
   * now contains a TextNode which uses a TextNodeRenderer.
   */
  protected VisibleRenderer createDefaultRenderer() {
    return new VisibleRenderer(this);
  }

  /**
   * Responds to a render event for this component.  This method simply notifies any
   * RenderListeners of the event.
   * @param e the current render event message
   * @throws RenderException if problem rendering value to document
   */
  public void processRenderEvent(RenderEvent e) throws RenderException {
    fireRender(e);
    renderer.render();
  }

  /**
   * Sets the renderer subcomponent used to render the value to the Document.
   * @deprecated The TextFieldRender is no longer used. A TextField
   * now contains a TextNode which uses a TextNodeRenderer.
   */
  public void setRenderer(VisibleRenderer newRenderer) {
    renderer = newRenderer;
  }

  /**
   * Returns the renderer subcomponent used to render the value to the Document.
   * @deprecated The TextFieldRender is no longer used. A TextField
   * now contains a TextNode which uses a TextNodeRenderer.
   */
  public VisibleRenderer getRenderer() {
    return renderer;
  }


  public void setModel(TextModel newModel) {
    super.setModel(newModel);
    getTextNode().setModel(newModel);
  }
}
