/**
 * Dispatcher - Component responsible for receiving and dispatching Events
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

import java.util.ArrayList;
import com.taursys.xml.Component;

/**
 * Component responsible for receiving and dispatching Events.
 * Components should register with this component to be notified whenever
 * their events occur.
 */
public abstract class Dispatcher {
  protected ArrayList components = new ArrayList();

  /**
   * Adds a component to the notification list unless it is already registered
   */
  public void addNotify(Component c) {
    if (!components.contains(c))
      components.add(c);
  }

  /**
   * Removes a component from the notification list if it is registered
   */
  public void removeNotify(Component c) {
    if (components.contains(c))
      components.remove(c);
  }
}
