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
 * $Id: SortButtonRenderer.java,v 1.1 2007/10/10 18:52:17 vauchers Exp $
 */

package com.jrefinery.util.ui;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/** A table cell renderer for table headings - uses one of three JButton instances to indicate the
    sort order for the table column.
    <P>
    This class (and also BevelArrowIcon and BlankIcon) is adapted from original code by Nobuo
    Tamemasa (version 1.0, 26-Feb-1999) posted on www.codeguru.com. */
public class SortButtonRenderer implements TableCellRenderer {

  /** A useful constant indicating NO sorting. */
  public static final int NONE = 0;

  /** A useful constant indicating ASCENDING (that is, arrow pointing down) sorting in the table. */
  public static final int DOWN = 1;

  /** A useful constant indicating DESCENDING (that is, arrow pointing up) sorting in the table. */
  public static final int UP   = 2;

  /** The current pressed column (-1 for no column). */
  protected int pressedColumn = -1;

  /** The three buttons that are used to render the table header cells. */
  JButton normalButton, ascendingButton, descendingButton;

  /** Default constructor - builds a SortButtonRenderer. */
  public SortButtonRenderer() {

    pressedColumn   = -1;

    normalButton = new JButton();
    normalButton.setMargin(new Insets(0,0,0,0));
    normalButton.setHorizontalAlignment(JButton.LEADING);

    ascendingButton = new JButton();
    ascendingButton.setMargin(new Insets(0,0,0,0));
    ascendingButton.setHorizontalAlignment(JButton.LEADING);
    ascendingButton.setHorizontalTextPosition(JButton.LEFT);
    ascendingButton.setIcon(new BevelArrowIcon(BevelArrowIcon.DOWN, false, false));
    ascendingButton.setPressedIcon(new BevelArrowIcon(BevelArrowIcon.DOWN, false, true));

    descendingButton = new JButton();
    descendingButton.setMargin(new Insets(0,0,0,0));
    descendingButton.setHorizontalAlignment(JButton.LEADING);
    descendingButton.setHorizontalTextPosition(JButton.LEFT);
    descendingButton.setIcon(new BevelArrowIcon(BevelArrowIcon.UP, false, false));
    descendingButton.setPressedIcon(new BevelArrowIcon(BevelArrowIcon.UP, false, true));

    Border border = UIManager.getBorder("TableHeader.cellBorder");
    normalButton.setBorder(border);
    ascendingButton.setBorder(border);
    descendingButton.setBorder(border);

  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                 boolean hasFocus, int row, int column) {

    JButton button = normalButton;
    int cc = table.convertColumnIndexToModel(column);
    if (table!=null) {
      SortableTableModel model = (SortableTableModel)table.getModel();
      if (model.getSortingColumn()==cc) {
        if (model.getAscending()) {
          button = ascendingButton;
        }
        else {
          button = descendingButton;
        }
      }
    }

    JTableHeader header = table.getTableHeader();
    if (header != null) {
      button.setForeground(header.getForeground());
      button.setBackground(header.getBackground());
      button.setFont(header.getFont());
    }

    button.setText((value==null) ? "" : value.toString());
    boolean isPressed = (cc == pressedColumn);
    button.getModel().setPressed(isPressed);
    button.getModel().setArmed(isPressed);
    return button;
  }

  public void setPressedColumn(int column) {
    this.pressedColumn = column;
  }

}



