/**
 * InputEvent - Event descriptor used when input is received
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

/**
 * Event descriptor used when input is received.  It contains
 * information about the source of the event, the input name and
 * the value received.
 */
public class InputEvent extends ParameterEvent {

  /**
   * Constructs a new InputEvent with the given Object as the source.
   */
  public InputEvent(Object source) {
    super(source);
  }

  /**
   * Constructs a new InputEvent with all the given properties.
   */
  public InputEvent(Object source, String name, String value) {
    super(source, name, value);
  }
}
