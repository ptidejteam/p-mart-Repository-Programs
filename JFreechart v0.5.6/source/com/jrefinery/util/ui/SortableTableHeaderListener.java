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
 * $Id: SortableTableHeaderListener.java,v 1.1 2007/10/10 18:52:17 vauchers Exp $
 */

package com.jrefinery.util.ui;

import java.awt.event.*;
import javax.swing.table.*;

/** Captures mouse clicks on a table header, with the intention of triggering a sort.  Adapted from
    code by Nabuo Tamemasa posted on http://www.codeguru.com. */
public class SortableTableHeaderListener implements MouseListener, MouseMotionListener {

  /** A reference to the table model. */
  private SortableTableModel model;

  /** The header renderer. */
  private SortButtonRenderer renderer;

  /** The index of the column that is sorted - used to determine the state of the renderer. */
  private int sortColumnIndex;

  public SortableTableHeaderListener(SortableTableModel model, SortButtonRenderer renderer) {
    this.model = model;
    this.renderer = renderer;
  }

  /** Handle a mouse press event - if the user is NOT resizing a column and NOT dragging a column
      then give visual feedback that the column header has been pressed. */
  public void mousePressed(MouseEvent e) {

    JTableHeader header = (JTableHeader)e.getComponent();

    if (header.getResizingColumn()==null) {  // resizing takes precedence over sorting
      if (header.getDraggedDistance()<1) {   // dragging also takes precedence over sorting
        int columnIndex = header.columnAtPoint(e.getPoint());
        int modelColumnIndex = header.getTable().convertColumnIndexToModel(columnIndex);
        if (model.isSortable(modelColumnIndex)) {
          sortColumnIndex = header.getTable().convertColumnIndexToModel(columnIndex);
          renderer.setPressedColumn(sortColumnIndex);
          header.repaint();
          if (header.getTable().isEditing()) {
            header.getTable().getCellEditor().stopCellEditing();
          }
        }
        else sortColumnIndex = -1;
      }
    }

  }

  /** If the user is dragging or resizing, then we clear the sort column. */
  public void mouseDragged(MouseEvent e) {

    JTableHeader header = (JTableHeader)e.getComponent();

    if ((header.getDraggedDistance()>0) || (header.getResizingColumn()!=null)) {
      renderer.setPressedColumn(-1);
      sortColumnIndex=-1;
    }
  }

  /** This event is ignored (not required). */
  public void mouseEntered(MouseEvent e) {
  }
  /** This event is ignored (not required). */
  public void mouseClicked(MouseEvent e) {
  }
  /** This event is ignored (not required). */
  public void mouseMoved(MouseEvent e) {
  }
  /** This event is ignored (not required). */
  public void mouseExited(MouseEvent e) {
  }

  /** When the user releases the mouse button, we attempt to sort the table. */
  public void mouseReleased(MouseEvent e) {

    JTableHeader header = (JTableHeader)e.getComponent();

    if (header.getResizingColumn()==null) {  // resizing the column takes precedence over sorting
      if (sortColumnIndex!=-1) {
        SortableTableModel model = (SortableTableModel)(header.getTable().getModel());
        boolean ascending = !model.getAscending();
        model.setAscending(ascending);
        model.sortByColumn(sortColumnIndex, ascending);

        renderer.setPressedColumn(-1);       // clear
        header.repaint();
      }
    }
  }

}

