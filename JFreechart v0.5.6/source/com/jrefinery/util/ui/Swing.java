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
 * $Id: Swing.java,v 1.1 2007/10/10 18:52:17 vauchers Exp $
 */

package com.jrefinery.util.ui;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

/** A collection of utility methods that can be used with (or on or for) Swing components. */
public class Swing {

  /** Positions the specified frame in the middle of the screen. */
  public static void centerFrameOnScreen(JFrame frame) {

    Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
    Dimension f=frame.getSize();
    frame.setBounds(Math.max(0, (d.width - f.width) / 2),
                    Math.max(0, (d.height - f.height) / 2),
                    f.width, f.height);

  }

  /** Positions the specified frame at a random location on the screen while ensuring that the
      entire frame is visible (provided that the frame is smaller than the screen). */
  public static void positionFrameRandomly(JFrame frame) {

    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    int maxX=Math.max(d.width-frame.getWidth(), 0);
    int maxY=Math.max(d.height-frame.getHeight(), 0);

    int x=(int)(maxX*Math.random());
    int y=(int)(maxY*Math.random());
    frame.setLocation(x, y);

  }

  /** Creates a panel that contains a table based on the specified table model. */
  public static JPanel createTablePanel(TableModel model) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    JTable table = new JTable(model);
    for (int columnIndex=0; columnIndex<model.getColumnCount(); columnIndex++) {
      TableColumn column = table.getColumnModel().getColumn(columnIndex);
      Class c = model.getColumnClass(columnIndex);
      if (c.equals(Number.class)) {
        column.setCellRenderer(new NumberCellRenderer());
      }
    }
    panel.add(new JScrollPane(table));
    return panel;
  }

  /** Creates and returns a JTable containing all the system properties.  This method returns a
      table that is configured so that the user can sort the properties by clicking on the
      table header. */
  public static JTable createSystemPropertiesTable() {
    SystemPropertiesTableModel properties = new SystemPropertiesTableModel();
    JTable table = new JTable(properties);

    SortButtonRenderer renderer = new SortButtonRenderer();
    TableColumnModel model = table.getColumnModel();
    for (int i=0; i<model.getColumnCount(); i++) {
      model.getColumn(i).setHeaderRenderer(renderer);
    }

    TableColumn column = model.getColumn(0);
    column.setPreferredWidth(200);
    column = model.getColumn(1);
    column.setPreferredWidth(350);

    JTableHeader header = table.getTableHeader();
    SortableTableHeaderListener listener = new SortableTableHeaderListener(properties, renderer);
    header.addMouseListener(listener);
    header.addMouseMotionListener(listener);

    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    return table;
  }

}


