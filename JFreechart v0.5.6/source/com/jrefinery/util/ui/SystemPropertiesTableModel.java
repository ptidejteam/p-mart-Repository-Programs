/* ================================
 * JRefinery Utility Class Library;
 * ================================
 * Version 0.20;
 * (C) Copyright 2000, Simba Management Limited;
 * Contact: David Gilbert (david.gilbert@bigfoot.com);
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307, USA.
 *
 * $Id: SystemPropertiesTableModel.java,v 1.1 2007/10/10 18:52:17 vauchers Exp $
 */

package com.jrefinery.util.ui;

import java.util.*;

/** A sortable table model containing the system properties. */
public class SystemPropertiesTableModel extends SortableTableModel {

  /** Storage for the system properties. */
  protected java.util.List propertyList;

  /** Standard constructor. */
  public SystemPropertiesTableModel() {

    propertyList = new java.util.ArrayList();
    Properties p = System.getProperties();
    for (Enumeration e = p.propertyNames(); e.hasMoreElements();) {
      String name = (String)e.nextElement();
      String value = System.getProperty(name);
      SystemProperty sp = new SystemProperty(name, value);
      propertyList.add(sp);
    }

    Collections.sort(propertyList, new SystemPropertyComparator(true));

  }

  /** Returns true for the first column, and false otherwise - sorting is only allowed on the first
      column. */
  public boolean isSortable(int columnIndex) {
    if (columnIndex==0) return true;
    else return false;
  }

  /** Returns the number of rows in the table model (that is, the number of system properties). */
  public int getRowCount() {
    return propertyList.size();
  }

  /** Returns the number of columns in the table model.  In this case, there are two columns: one
      for the property name, and one for the property value. */
  public int getColumnCount() {
    return 2;
  }

  public String getColumnName(int columnIndex) {
    if (columnIndex==0) return "Property Name:";
    else return "Value:";
  }

  /** Returns the value at the specified row and column.  This method supports the TableModel
      interface. */
  public Object getValueAt(int rowIndex, int columnIndex) {
    SystemProperty sp = (SystemProperty)propertyList.get(rowIndex);
    if (columnIndex==0) return sp.getName();
    else if (columnIndex==1) return sp.getValue();
    else return null;
  }

  public void sortByColumn(int column, boolean ascending) {
    if (isSortable(column)) {
      this.sortingColumn = column;
      Collections.sort(propertyList, new SystemPropertyComparator(ascending));
    }
  }


}

/** Useful class for holding the name and value of a system property. */
class SystemProperty {

  /** The property name; */
  private String name;

  /** The property value; */
  private String value;

  /* Standard constructor - builds a new SystemProperty. */
  public SystemProperty(String name, String value) {
    this.name = name;
    this.value = value;
  }

  /** Returns the property name. */
  public String getName() {
    return this.name;
  }

  /** Returns the property value. */
  public String getValue() {
    return this.value;
  }

}

class SystemPropertyComparator implements Comparator {

  private boolean ascending;

  public SystemPropertyComparator(boolean ascending) {
    this.ascending = ascending;
  }

  public int compare(Object o1, Object o2) {
    if ((o1 instanceof SystemProperty) && (o2 instanceof SystemProperty)) {
      SystemProperty sp1 = (SystemProperty)o1;
      SystemProperty sp2 = (SystemProperty)o2;
      if (ascending) return sp1.getName().compareTo(sp2.getName());
      else return sp2.getName().compareTo(sp1.getName());
    }
    else return 0;
  }

  public boolean equals(Object object) {
    return false;
  }

}
