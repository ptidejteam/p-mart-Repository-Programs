/**
 * ContentValueChangeEvent - Indicates a change to a property or value of the contents of a ValueHolder.
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
package com.taursys.model.event;

import com.taursys.model.ValueHolder;

/**
 * ContentValueChangeEvent indicates a change to a property or value of the contents of a ValueHolder.
 * @author Marty Phelan
 * @version 1.0
 */
public class ContentValueChangeEvent extends ContentChangeEvent {
  private String propertyName;
  private Object oldValue;
  private Object newValue;

  /**
   * Constructs a new ContentValueChangeEvent with the given information.
   * @param source the source value holder which holds the object
   * @param propertyName the property name which has changed
   * @param oldValue the value before the change occurred (or null if not available)
   * @param newValue the value after the change occured
   */
  public ContentValueChangeEvent(ValueHolder source, String propertyName,
      Object oldValue, Object newValue) {
    super(source);
    this.propertyName = propertyName;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  /**
   * Gets the property name which has changed.
   * @return property name which has changed
   */
  public String getPropertyName() {
    return propertyName;
  }

  /**
   * Gets the value before the change occurred (or null if not available).
   * @return the value before the change occurred (or null if not available)
   */
  public Object getOldValue() {
    return oldValue;
  }

  /**
   * Gets the value after the change occured.
   * @return the value after the change occured
   */
  public Object getNewValue() {
    return newValue;
  }
}
