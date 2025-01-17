/**
 * DocumentElementRenderer - Renders a DocumentElement
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

import com.taursys.debug.Debug;
import com.taursys.xml.event.RenderException;
import com.taursys.xml.event.RecycleException;
import com.taursys.xml.DocumentElement;

/**
 * DocumentElementRenderer renders a DocumentElement.  It simply shows or
 * hides the Element and, if visible, dispatches a render event to the
 * DocumentElement's children.
 * @author Marty Phelan
 * @version 1.0
 */
public class DocumentElementRenderer extends AbstractRenderer {

  /**
   * Constructs a new DocumentElementRenderer
   */
  public DocumentElementRenderer(DocumentElement de) {
    super(de);
  }

  /**
   * Recycles this component by restoring the Document element to a default state.
   * This implementation simply makes it visible, and dispatches a recycle
   * event to any children.
   * <p>
   * If a Document element was left invisible, and the Document changed, then
   * the invisible component would become dereferenced.
   * @throws RecycleException if problem occurs during recycling.
   */
  public void recycle() throws RecycleException {
    try {
      init();
    } catch (RenderException ex) {
      throw new RecycleException(ex.getReason(), ex);
    }
    restoreSelf();
    ((DocumentElement)getComponent()).getRecycleDispatcher().dispatch();
  }

  /**
   * Renders the DocumentElement by showing or hiding it.  If it is visible,
   * it invokes the DocumentElement's renderDispatcher to dispatch to the
   * children.
   * @throws RenderException if any problem occurs during rendering
   */
  public void render() throws RenderException {
    init();
    if (getComponent().isVisible()) {
      restoreSelf();
      ((DocumentElement)getComponent()).getRenderDispatcher().dispatch();
    } else {
      removeSelf();
    }
  }
}
