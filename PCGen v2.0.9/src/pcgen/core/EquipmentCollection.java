/*
 * EquipmentCollection.java
 * Copyright 2001 (C) Thomas G. W. Epperly
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on April 7, 2001, 12:14 PM
 */

package pcgen.core;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * An interface for collections of equipment.
 */
public interface EquipmentCollection
{
  /**
   * Return the name of the equipment collection.
   *
   * @return the name of the equipment collection.
   */
  String getName();

  /**
   * Change the name of the equipment collection.
   *
   * @param name the new name for the collection.  This name will
   * replace the previous name.
   */
  void setName(String name);

  /**
   * Get the number of children.
   */
  int getChildCount();

  /**
   * Return the i'th child.
   */
  Object getChild(int i);

  /**
   * Remove the i'th child.
   */
  void removeChild(int i);

  /**
   * Return the index of this child or -1.
   */
  int indexOfChild(Object child);

  /**
   * Set the parent of this collection.
   */
  void setParent(EquipmentCollection parent);

  /**
   * Get the parent of this collection.
   */
  EquipmentCollection getParent();

  /**
   * Insert a child in the i'th position.
   */
  void insertChild(int i, Object child);

  /**
   * Return <code>true</code> if this collection ever can accept
   * children.
   */
  boolean acceptsChildren();

  /**
   * Return <code>true</code> if this collection can take this object.
   */
  boolean canContain(Object obj);

  /**
   * Check if the container can hold an item or collection with a
   * certain set of properties.  For example, you can't add a castle
   * to a backpack.
   */
  boolean canHold(HashMap properties);

  /**
   * Update this collection's properties.
   *
   * @param properties a set of (Name, Number) properties
   * @param additive   <code>true</code> means the change is additive;
   *                   <code>false</code> means the change is negative.
   */
  void updateProperties(HashMap properties, boolean additive);
}
