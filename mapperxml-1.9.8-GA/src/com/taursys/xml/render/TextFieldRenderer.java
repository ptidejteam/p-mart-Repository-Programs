/**
 * TextFieldRenderer - Subcomponent which can render a text field value to a DOM Element.
 *
 * Copyright (c) 2002-2006
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

import com.taursys.xml.TextField;

/**
 * Subcomponent which can render a value from a TextField to a DOM Element as a TextNode.
 * @deprecated The TextFieldRender is no longer used. A TextField
 * now contains a TextNode which uses a TextNodeRenderer.
 */
public class TextFieldRenderer extends VisibleRenderer {

  /**
   * Constructs a new TextFieldRender for the given TextField.
   * @param textField which this renderer belongs to
   */
  public TextFieldRenderer(TextField textField) {
    super(textField);
  }

}
