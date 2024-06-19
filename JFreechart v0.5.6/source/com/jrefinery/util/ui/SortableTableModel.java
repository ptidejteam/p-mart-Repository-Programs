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
 * $Id: SortableTableModel.java,v 1.1 2007/10/10 18:52:17 vauchers Exp $
 */

package com.jrefinery.util.ui;

import javax.swing.table.*;

/** The base class for a sortable table model. */
public abstract class SortableTableModel extends AbstractTableModel {

  /** The column on which the data is sorted (-1 for no sorting). */
  protected int sortingColumn;

  protected boolean ascending;

  /** Standard constructor. */
  public SortableTableModel() {
    this.sortingColumn = -1;
    this.ascending = true;
  }

  /** Returns the index of the sorting column, or -1 if the data is not sorted on any column. */
  public int getSortingColumn() {
    return sortingColumn;
  }

  /** Returns true if the data is sorted in ascending order, and false otherwise. */
  public boolean getAscending() {
    return this.ascending;
  }

  /** Sets the flag that determines whether the sort order is ascending or descending. */
  public void setAscending(boolean flag) {
    this.ascending = flag;
  }

  /** Sorts the table by the specified column. */
  public void sortByColumn(int column, boolean ascending) {
    if (isSortable(column)) {
      this.sortingColumn = column;
    }
  }

  /** Returns true if the specified column is sortable, and false otherwise. */
  public boolean isSortable(int column) {
    return false;
  }

}