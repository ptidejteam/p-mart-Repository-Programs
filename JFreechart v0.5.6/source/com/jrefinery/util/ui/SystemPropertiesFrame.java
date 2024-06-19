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
 * $Id: SystemPropertiesFrame.java,v 1.1 2007/10/10 18:52:17 vauchers Exp $
 */

package com.jrefinery.util.ui;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.*;

public class SystemPropertiesFrame extends JFrame implements ActionListener {

  protected JTable table;

  public SystemPropertiesFrame(boolean menu) {

    super("System Properties");

    if (menu) {
      this.setJMenuBar(createMenuBar());
    }

    JPanel content = new JPanel(new BorderLayout());
    content.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    table = Swing.createSystemPropertiesTable();
    content.add(new JScrollPane(table));

    JPanel buttonPanel = new JPanel(new BorderLayout());
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
    JButton closeButton = new JButton("Close");
    closeButton.setMnemonic('C');
    closeButton.setActionCommand("Close");
    closeButton.addActionListener(this);
    buttonPanel.add(closeButton, BorderLayout.EAST);
    content.add(buttonPanel, BorderLayout.SOUTH);
    this.setContentPane(content);

  }

  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if (command.equals("Close")) {
      dispose();
    }
    else if (command.equals("Copy")) {
      doCopy();
    }
  }

  /** Copies the selected cells in the table to the clipboard, tab-delimited. */
  public void doCopy() {
    StringBuffer buffer = new StringBuffer();
    ListSelectionModel selection = table.getSelectionModel();
    int firstRow = selection.getMinSelectionIndex();
    int lastRow = selection.getMaxSelectionIndex();
    if ((firstRow!=-1) && (lastRow!=-1)) {
      for (int r=firstRow; r<=lastRow; r++) {
        for (int c=0; c<table.getColumnCount(); c++) {
          buffer.append(table.getValueAt(r, c));
          if (c!=2) buffer.append("\t");
        }
        buffer.append("\n");
      }
    }
    StringSelection ss = new StringSelection(buffer.toString());
    Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
    cb.setContents(ss, ss);

  }

  /** Creates and returns a menu-bar for the frame. */
  private JMenuBar createMenuBar() {

    // create the menus
    JMenuBar menuBar = new JMenuBar();

    // first the file menu
    JMenu fileMenu = new JMenu("File", true);
    fileMenu.setMnemonic('F');

    JMenuItem exitItem = new JMenuItem("Close", 'C');
    exitItem.setActionCommand("Close");
    exitItem.addActionListener(this);
    fileMenu.add(exitItem);

    // then the edit menu
    JMenu editMenu = new JMenu("Edit");
    editMenu.setMnemonic('E');

    JMenuItem copyItem = new JMenuItem("Copy", 'C');
    copyItem.setActionCommand("Copy");
    copyItem.addActionListener(this);
    editMenu.add(copyItem);

    // finally, glue together the menu and return it
    menuBar.add(fileMenu);
    menuBar.add(editMenu);
    return menuBar;

  }

}