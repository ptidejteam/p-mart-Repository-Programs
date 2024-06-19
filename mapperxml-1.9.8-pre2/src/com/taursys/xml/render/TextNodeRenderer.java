/**
 * TextNodeRenderer - Subcomponent to render an TextNode value to a Text node of a DOM Element.
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
package com.taursys.xml.render;

import com.taursys.model.ModelException;
import com.taursys.xml.TextNode;
import com.taursys.xml.event.RenderException;

/**
 * TextNodeRenderer is a Subcomponent to render an TextNode value to a Text node
 * of a DOM Element.
 * 
 * @author marty
 */
public class TextNodeRenderer extends AbstractSubcomponentRenderer {

  /**
   * Constructs a new TextNodeRender for the given TextNode.
   * @param component which this renderer belongs to
   */
  public TextNodeRenderer(TextNode component) {
    super(component);
  }

  /**
   * Renders the Attribute to an attribute of an Element in a Document.
   * This component accesses the given attributes's parent to obtain the
   * DocumentAdapter.  It also accesses the parents's id, its attributeName 
   * and model. It uses the DocumentAdapter's setElementText to render the 
   * model's text value to the Element indicated by the parent's id.
   * @throws RenderException if any problem occurs during rendering
   */
  public void render() throws RenderException {
    init();
    if (component.isVisible()) {
      try {
        da.setElementText(id, ((TextNode) component).getModel().getText());
      } catch (ModelException ex) {
        throw new RenderException(RenderException.REASON_MODEL_EXCEPTION, ex);
      }
    } else {
      da.setElementText(id, "");
    }
  }
}