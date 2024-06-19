/**
 * Template - A Component which replicates itself and its children for each object in its model.
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

import com.taursys.xml.event.Dispatcher;
import com.taursys.xml.event.RenderEvent;
import com.taursys.xml.event.RenderDispatcher;
import com.taursys.xml.event.RenderException;
import com.taursys.xml.render.TemplateRenderer;
import com.taursys.xml.render.DocumentElementRenderer;
import com.taursys.model.CollectionValueHolder;

/**
 * A Template is a component used to display multiple items.  A Template is a
 * Container which contains other components (eg TextFields).  It uses a
 * TemplateRenderer to do the actual rendering.  Typically, this involves
 * dispatching a RenderEvent to its children, then cloning itself for each item
 * in its collectionValueHolder.
 * <p>
 * You can override the createDefaultRender method to use your own custom
 * TemplateRenderer subcomponent if desired.
 */
public class Template extends DocumentElement {
  private CollectionValueHolder collectionValueHolder;

  /**
   * Constructs a new template
   */
  public Template() {
  }

  /**
   * Creates the default Renderer for this component.
   * By Default this methos returns a new TemplateRenderer.
   * Override this method to define your own TemplateRenderer.
   */
  protected DocumentElementRenderer createDefaultRenderer() {
    return new TemplateRenderer(this);
  }

  /**
   * Set the CollectionValueHolder that this template will iterate for rendering.
   * @param holder the CollectionValueHolder that this template will iterate for rendering.
   */
  public void setCollectionValueHolder(CollectionValueHolder holder) {
    collectionValueHolder = holder;
  }

  /**
   * Get the CollectionValueHolder that this template will iterate for rendering.
   * @return the CollectionValueHolder that this template will iterate for rendering.
   */
  public CollectionValueHolder getCollectionValueHolder() {
    return collectionValueHolder;
  }
}
