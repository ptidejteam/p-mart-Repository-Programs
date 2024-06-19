/**
 * RecycleDispatcher - Subcomponent responsible for receiving and dispatching RecycleEvents
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
package com.taursys.xml.event;

import java.util.Iterator;
import com.taursys.xml.Container;
import com.taursys.xml.Component;

/**
 * RecycleDispatcher is a subcomponent responsible for receiving and
 * dispatching RecycleEvents.
 * Components should register with this component to be notified whenever
 * a recycle event occurs.
 * @author Marty Phelan
 * @version 1.0
 */
public class RecycleDispatcher extends Dispatcher {
  private Container parent;

  /**
   * Constructs a new RecycleDispatcher with given Container as parent.
   */
  public RecycleDispatcher(Container c) {
    parent = c;
  }

  /**
   * Dispatches a RecycleEvent to all registered components.
   */
  public void dispatch() throws RecycleException {
    RecycleEvent e = new RecycleEvent(parent);
    Iterator iter = components.iterator();
    while (iter.hasNext()) {
      ((Component)iter.next()).processRecycleEvent(e);
    }
  }
}
