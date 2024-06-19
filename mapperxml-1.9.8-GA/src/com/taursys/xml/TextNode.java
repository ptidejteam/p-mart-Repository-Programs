/**
 * TextNode - A Component is used as part of an Element component.
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

import com.taursys.xml.event.RenderEvent;
import com.taursys.xml.event.RenderException;
import com.taursys.xml.render.TextNodeRenderer;

/**
 * An <code>TextNode</code> is used as a sub-component of an Element. It is
 * rendered within the Element tag as the first child Text node. 
 * <code>TextNode</code> extends <code>Parameter</code> and thus can 
 * carry a value or be bound to a value holder. It can also receive its 
 * value from the request.
 * <p>
 * A <code>TextNode</code> renders itself to its parent's element
 * only if the parent has its <code>id</code> property set.
 */
public class TextNode extends Parameter {
  private TextNodeRenderer renderer;

  /**
   * Constructs a new <code>TextNode</code> with a <code>DefaultTextModel</code>
   * and a <code>VariantValueHolder</code> for a <code>String</code> data type.
   * The default model, a <code>DefaultTextModel</code>, is created via the
   * <code>createDefaultModel</code> method.  By default, the
   * <code>DefaultTextModel</code> creates and uses a <code>VariantValueHolder</code>
   * of type <code>String</code>.
   * The default renderer, an TextNodeRenderer, is created via the
   * createDefaultRenderer method.
   */
  public TextNode() {
    super();
    addEventType(RenderEvent.class.getName());
    renderer = createDefaultRenderer();
  }

  /**
   * Constructs a new <code>TextNode</code> with a <code>DefaultTextModel</code>
   * and a <code>VariantValueHolder</code> for the given data type.
   * To specify the data type, use one of the TYPE_xxx constants defined in
   * <code>DataTypes</code>.
   * @param javaDataType data type for new model
   * @see com.taursys.util.DataTypes
   */
  public TextNode(int javaDataType) {
    super(javaDataType);
    addEventType(RenderEvent.class.getName());
    renderer = createDefaultRenderer();
  }

  // ************************************************************************
  //                       Renderer Methods
  // ************************************************************************

  /**
   * Creates the default TextNodeRenderer for this component.
   * By Default this methos returns a new TextNodeRenderer.
   * Override this method to define your own TextNodeRenderer.
   */
  protected TextNodeRenderer createDefaultRenderer() {
    return new TextNodeRenderer(this);
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

  /**
   * Sets the renderer subcomponent used to render the value to the Document.
   */
  public void setRenderer(TextNodeRenderer newRenderer) {
    renderer = newRenderer;
  }

  /**
   * Returns the renderer subcomponent used to render the value to the Document.
   */
  public TextNodeRenderer getRenderer() {
    return renderer;
  }

  // ************************************************************************
  //                       Property Accessors
  // ************************************************************************

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
    return  result; 
  }

}
