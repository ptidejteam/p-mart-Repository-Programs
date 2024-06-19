/**
 * AttributeRenderer - Subcomponent to render an attribute value to an Attribute of a DOM Element.
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
package com.taursys.xml.render;

import com.taursys.model.ModelException;
import com.taursys.xml.Attribute;
import com.taursys.xml.event.RenderException;

/**
 * Subcomponent to render an attribute value to an Attribute of a DOM Element.
 * @author marty
 */
public class StandAloneAttributeRenderer extends AbstractSubcomponentRenderer {

  /**
   * Constructs a new AttributeRender for the given Attribute.
   * @param attribute which this renderer belongs to
   */
  public StandAloneAttributeRenderer(Attribute attribute) {
    super(attribute);
  }

  /**
   * Initializes reference to component node.
   * Only acts if componentNode is null and document has not changed
   * @throws RenderException if parent, DocumentAdapter, or Document is null.
   */
  protected void init() throws RenderException {
  }

  /**
   * Renders by setting the attributeName="attributeName" in the Element
   * is visible and its value is not null and not FALSE.
   * @throws RenderException if any problem occurs during rendering
   */
  public void render() throws RenderException {
    init();
    try {
      String attributeName = ((Attribute) component).getAttributeName();
      Object value = ((Attribute) component).getValue();
      // If not null and not FALSE
      if (component.isVisible() && value != null && value != Boolean.FALSE) {
        da.setAttributeText(id, attributeName, attributeName);
      } else {
        da.removeAttribute(id, attributeName);
      }
    } catch (ModelException ex) {
      throw new RenderException(RenderException.REASON_MODEL_EXCEPTION, ex);
    }
  }
}
