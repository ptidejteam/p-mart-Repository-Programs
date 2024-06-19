/**
 * Container - Container for Mapper XML Components
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

import java.util.ArrayList;
import java.util.EventObject;
import com.taursys.dom.DocumentAdapter;
import java.util.Iterator;
import com.taursys.xml.event.Dispatcher;
import com.taursys.xml.event.RenderDispatcher;
import com.taursys.xml.event.RecycleDispatcher;

/**
 * <p>This class is a foundation container for MapperXML components. This class
 * defines all required properties and methods for a container and provides
 * basic implementation for many of them. The <code>Container</code> provides
 * 3 primary functions:</p>
 * <ul>
 * <li><code>Component</code> management - provides methods to add, remove and
 * retrieve <code>Components</code>.
 * </li>
 * <li>Provide access to a <code>DocumentAdapter</code> for the
 * <code>Components</code> to render themselves to. This implementation simply
 * looks to its parent <code>Container</code> to obtain the
 * <code>DocumentAdapter</code>. Bottom level or master <code>Containers</code>
 * should override this method and provide access to a
 * <code>DocumentAdapter</code>.
 * </li>
 * <li>Provide access to <code>Dispatchers</code> for the various event types
 * to the <code>Components</code>. There are separate
 * <code>getXXXDispatcher</code> methods for each event type.
 * <code>Components</code> need to access these whenever they are added or
 * removed from this <code>Container</code> so they can add or remove themselves
 * from the various event notification lists. This implementation simply
 * looks to its parent <code>Container</code> to obtain the appropriate
 * <code>Dispatcher</code>. Bottom level or master <code>Containers</code>
 * should override this method and provide access to an appropriate
 * <code>Dispatcher</code>. Intermediate level <code>Containers</code> can
 * also override this method and provide access to their own
 * <code>Dispatchers</code>.
 * </li>
 * </ul>
 */
public abstract class Container extends Component {
  private ArrayList components = new ArrayList();

  /**
   * Constructs a new container
   */
  public Container() {
  }

  // =========================================================================
  //            Methods to Manage Components for this Container
  // =========================================================================

  /**
   * Add a component to this container.  No action is taken if the component
   * already belongs to this container.  If the component belongs to another
   * container as indicated by its parent, it is first removed from the old
   * container before it is added to this container.  Finally, it is added to
   * this container and its parent is set to this container.  The component's
   * addNotify method is also invoked so it will be notified of events it is
   * interested in.
   */
  public void add(Component c) {
    if (!components.contains(c)) {
      // remove from old parent
      if (c.parent != null)
        c.parent.remove(c);
      // Add to this container, set parent and setup component to be notified
      components.add(c);
      c.parent = this;
      c.addNotify();
    }
  }

  /**
   * Removes the given component from this container and sets its parent to null.
   * Also invokes removeNotify on the given component so it will un-register
   * itself with any dispatchers.
   */
  public void remove(Component c) {
    c.removeNotify();
    components.remove(c);
    c.parent = null;
  }

  /**
   * Recursively searches for given Component and returns true if found.
   * @param c Component to look for
   * @return true if found in this container or a child container
   */
  public boolean contains(Component c) {
    // Check our children first
    if (components.contains(c))
      return true;
    // recurse child containers
    Iterator iter = components.iterator();
    while (iter.hasNext()) {
      Object item = iter.next();
      if (item instanceof Container) {
        if (((Container)item).contains(c))
          return true;
      }
    }
    return false;
  }

  /**
   * Recursively searches for a DocumentComponent with the given ID.
   * Returns DocumentComponent if found else null.
   * Returns null if given id is null
   * @param id the ID to search for
   * @return the DocumentComponent if found else null
   */
  public DocumentComponent get(String id) {
    if (id == null)
      return null;
    Iterator iter = components.iterator();
    while (iter.hasNext()) {
      Object item = iter.next();
      if (item instanceof DocumentComponent)
        if (id.equals(((DocumentComponent)item).getId()))
          return (DocumentComponent)item;
      if (item instanceof Container) {
        DocumentComponent dc = ((Container)item).get(id);
        if (dc != null)
          return dc;
      }
    }
    return null;
  }

  /**
   * Returns an array of all the components for this container.
   */
  public Component[] getComponents() {
    return (Component[])components.toArray(new Component[]{});
  }

  // =========================================================================
  //                 Methods to Access Document Adapter
  // =========================================================================

  /**
   * Returns the DocumentAdapter from the parent else null if no parent.
   * The master container should override this method to return the shared
   * DocumentAdapter for all components.
   */
  public DocumentAdapter getDocumentAdapter() {
    if (this.parent != null)
      return parent.getDocumentAdapter();
    else
      return null;
  }

  // =========================================================================
  //                    Methods to Access Dispatchers
  // =========================================================================

  /**
   * Returns the Dispatcher for ParameterEvents from the parent else null if no parent.
   * The master container should override this method to return the shared
   * Dispatcher for all components.
   */
  public Dispatcher getParameterDispatcher() {
    if (this.parent != null)
      return parent.getParameterDispatcher();
    else
      return null;
  }

  /**
   * Returns the Dispatcher for InputEvents from the parent else null if no parent.
   * The master container should override this method to return the shared
   * Dispatcher for all components.
   */
  public Dispatcher getInputDispatcher() {
    if (this.parent != null)
      return parent.getInputDispatcher();
    else
      return null;
  }

  /**
   * Returns the Dispatcher for TriggerEvents from the parent else null if no parent.
   * The master container should override this method to return the shared
   * Dispatcher for all components.
   */
  public Dispatcher getTriggerDispatcher() {
    if (this.parent != null)
      return parent.getTriggerDispatcher();
    else
      return null;
  }

  /**
   * Returns the Dispatcher for RenderEvents from the parent else null if no parent.
   * The master container should override this method to return the shared
   * Dispatcher for all components.
   */
  public RenderDispatcher getRenderDispatcher() {
    if (this.parent != null)
      return parent.getRenderDispatcher();
    else
      return null;
  }

  /**
   * Returns the Dispatcher for RecycleEvents from the parent else null if no parent.
   * The master container should override this method to return the shared
   * Dispatcher for all components.
   */
  public RecycleDispatcher getRecycleDispatcher() {
    if (this.parent != null)
      return parent.getRecycleDispatcher();
    else
      return null;
  }
}
