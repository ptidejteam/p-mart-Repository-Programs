/**
 * Button - A visible Trigger
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
import com.taursys.xml.render.AbstractRenderer;
import com.taursys.xml.render.VisibleRenderer;

/**
 * Button is a visible Trigger
 * @author Marty Phelan
 * @version 1.0
 */
public class Button extends Trigger implements DocumentComponent {
  private boolean notifySet = false;
  private String id;
  private VisibleRenderer renderer;

  /**
   * Constructs a new Button
   */
  public Button() {
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
  protected VisibleRenderer createDefaultRenderer() {
    return new VisibleRenderer(this);
  }

  /**
   * Get the Renderer for this component.
   * @return the Renderer for this component.
   */
  public VisibleRenderer getRenderer() {
    return renderer;
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
   * Registers this component with dispatcher to be notified of ParameterEvents
   * This method invokes the lazyAddNotify method to perform the work
   */
  public void addNotify() {
    lazyAddNotify();
    super.addNotify();
  }

  /**
   * Un-Registers this component with dispatcher.
   * This method invokes the lazyRemoveNotify method to perform the work
   */
  public void removeNotify() {
    lazyRemoveNotify();
    super.removeNotify();
  }

  /**
   * Conditionally registers this component with dispatcher to be notified of ParameterEvents
   * In order to be notified, this component must have a parent.
   */
  protected void lazyAddNotify() {
    if (!notifySet && parent != null) {
      notifySet = true;
      Dispatcher dispatcher = parent.getRenderDispatcher();
      if (dispatcher != null)
        dispatcher.addNotify(this);
      dispatcher = parent.getTriggerDispatcher();
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
      Dispatcher dispatcher = parent.getRenderDispatcher();
      if (dispatcher != null)
        dispatcher.removeNotify(this);
      dispatcher = parent.getTriggerDispatcher();
      if (dispatcher != null)
        dispatcher.removeNotify(this);
    }
  }
}
