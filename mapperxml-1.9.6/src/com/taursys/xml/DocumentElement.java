/**
 * DocumentElement - is a simple container element for an XML document.
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
import com.taursys.xml.render.DocumentElementRenderer;
import com.taursys.xml.event.RecycleEvent;
import com.taursys.xml.event.RecycleDispatcher;
import com.taursys.xml.event.RecycleException;

/**
 * DocumentElement is a simple container element for an XML document.
 * It currently has no specific behavior.
 * @author Marty Phelan
 * @version 1.0
 */
public class DocumentElement extends Container implements DocumentComponent {
  private RenderDispatcher renderDispatcher = new RenderDispatcher(this);
  private RecycleDispatcher recycleDispatcher = new RecycleDispatcher(this);
  private boolean notifySet = false;
  private String id;
  private DocumentElementRenderer renderer;

  /**
   * Constructs a new DocumentElement
   */
  public DocumentElement() {
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
   * Creates the default TextFieldRenderer for this component.
   * By Default this methos returns a new TextFieldRenderer.
   * Override this method to define your own TextFieldRenderer.
   */
  protected DocumentElementRenderer createDefaultRenderer() {
    return new DocumentElementRenderer(this);
  }

  /**
   * Get the Renderer for this component.
   * @return the Renderer for this component.
   */
  public DocumentElementRenderer getRenderer() {
    return renderer;
  }

  /**
   * Returns the RenderDispatcher used by this container.
   * This is invoked by renderable components.  It overrides the
   * method defined in the Container class.
   */
  public RenderDispatcher getRenderDispatcher() {
    return renderDispatcher;
  }

  /**
   * Returns the RecycleDispatcher used by this container.
   * This is invoked by recyclable components.  It overrides the
   * method defined in the Container class.
   */
  public RecycleDispatcher getRecycleDispatcher() {
    return recycleDispatcher;
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
   * Processes a RecycleEvent recycling self and then by propagating event to listeners.
   */
  public void processRecycleEvent(RecycleEvent e) throws RecycleException {
    renderer.recycle();
    super.processRecycleEvent( e);
  }

  /**
   * Registers this component with dispatcher to be notified of ParameterEvents
   * This method invokes the lazyAddNotify method to perform the work
   */
  public void addNotify() {
    lazyAddNotify();
  }

  /**
   * Un-Registers this component with dispatcher.
   * This method invokes the lazyRemoveNotify method to perform the work
   */
  public void removeNotify() {
    lazyRemoveNotify();
  }

  /**
   * Conditionally registers this component with dispatcher to be notified of ParameterEvents
   * In order to be notified, this component must have a parent.
   */
  protected void lazyAddNotify() {
    if (!notifySet && parent != null) {
      notifySet = true;
      // Register with RenderDispatcher
      Dispatcher dispatcher = parent.getRenderDispatcher();
      if (dispatcher != null)
        dispatcher.addNotify(this);
      // Register with RecycleDispatcher
      dispatcher = parent.getRecycleDispatcher();
      if (dispatcher != null)
        dispatcher.addNotify(this);
    }
  }

  /**
   * Conditionally un-registers this component from dispatcher.
   * Only un-registers if it WAS registered and parent is not null
   */
  protected void lazyRemoveNotify() {
    if (notifySet && parent != null) {
      notifySet = false;
      // Un-register with RenderDispatcher
      Dispatcher dispatcher = parent.getRenderDispatcher();
      if (dispatcher != null)
        dispatcher.removeNotify(this);
      // Un-register with RecycleDispatcher
      dispatcher = parent.getRecycleDispatcher();
      if (dispatcher != null)
        dispatcher.removeNotify(this);
    }
  }
}
