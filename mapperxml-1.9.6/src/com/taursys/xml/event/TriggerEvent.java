/**
 * TriggerEvent - Event descriptor used when specific input value is received.
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

import java.util.EventObject;

/**
 * Event descriptor used when specific trigger(key/value) is received.  It contains
 * information about the source of the event, the input name and
 * the value received.
 */
public class TriggerEvent extends EventObject {
  private String name;
  private String value;

  /**
   * Constructs a new TriggerEvent with all the given properties.
   */
  public TriggerEvent(Object source, String name, String value) {
    super(source);
    this.name = name;
    this.value = value;
  }

  /**
   * Returns the trigger key name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the trigger key name
   */
  public void setName(String newName) {
    name = newName;
  }

  /**
   * Sets the trigger value
   */
  public void setValue(String newValue) {
    value = newValue;
  }

  /**
   * Returns the trigger value
   */
  public String getValue() {
    return value;
  }
}
