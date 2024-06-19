/**
 * BoundDocumentElement - DocumentElement which is bound to a ValueHolder
 *
 * Copyright (c) 2005
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

import com.taursys.model.ValueHolder;

/**
 * BoundDocumentElement is a DocumentElement which is bound to a ValueHolder.
 * @author marty
 */
public class BoundDocumentElement extends DocumentElement {
  private ValueHolder valueHolder = null;
  
  /**
   * Default constructor 
   */
  public BoundDocumentElement() {
    super();
  }

  // =======================================================================
  //                          Property Accessors
  // =======================================================================

  /**
   * Get the ValueHolder for this component
   * @return Returns the valueHolder.
   */
  public ValueHolder getValueHolder() {
    return valueHolder;
  }

  /**
   * Set the ValueHolder for this component
   * @param valueHolder The valueHolder to set.
   */
  public void setValueHolder(ValueHolder valueHolder) {
    this.valueHolder = valueHolder;
    getElementDelegate().setValueHolder(valueHolder);
  }

  // =======================================================================
  //                       DIAGNOSTIC METHODS
  // =======================================================================

  /**
   * Returns a string representation of this object. This contains
   * the object identity and state information.
   * @return a string representation of this object
   */
  public String toString() {
    String result = super.toString();
    result += " holder=";
    if (valueHolder == null) {
      result += "null";
    } else {
      result += "[" + valueHolder +"]";
    }
    return  result; 
  }

  
}
