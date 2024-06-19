/**
 * AbstractSubcomponentRenderer - Renderer for Subcomponents
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

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.taursys.debug.Debug;
import com.taursys.dom.DocumentAdapter;
import com.taursys.xml.Component;
import com.taursys.xml.Container;
import com.taursys.xml.DocumentComponent;
import com.taursys.xml.event.RenderException;

/**
 * Abstract renderer for subcomponents
 * @author marty
 */
public class AbstractSubcomponentRenderer {
  protected Component component;
  private Node componentNode;
  private Document doc;
  protected DocumentAdapter da;
  protected String id;

  /**
   * Constructs new AbstractSubcomponentRenderer
   * @param component which this renderer belongs to
   */
  public AbstractSubcomponentRenderer(Component component) {
    this.component = component;
  }

  /**
   * Initializes reference to component node.
   * Only acts if componentNode is null and document has not changed
   * @throws RenderException if parent, DocumentAdapter, or Document is null.
   */
  protected void init() throws RenderException {
    Container parent = component.getParent();
    if (parent == null)
      throw new RenderException(RenderException.REASON_PARENT_CONTAINER_NULL);
    da = parent.getDocumentAdapter();
    if (da == null)
      throw new RenderException(RenderException.REASON_DOCUMENT_IS_NULL);
    Document currentDoc = da.getDocument();
    if (currentDoc == null)
      throw new RenderException(RenderException.REASON_DOCUMENT_IS_NULL);
    if (doc != currentDoc) {
      doc = currentDoc;
      componentNode = null;
    }
    if (componentNode == null) {
      id = ((DocumentComponent)parent).getId();
      if (id != null) {
        componentNode = da.getElementById(id);
        if (componentNode == null) {
          Debug.warn("AttributeRenderer.init: Attempt to render an component for an element which is not in document. ID="
            + id);
        }
      } else {
          Debug.warn("AttributeRenderer.init: Attempt to render an component for an element whose ID is null. Class="
            + parent.getClass().getName());
      }
    }
  }

}
