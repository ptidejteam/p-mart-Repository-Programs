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
 * $Id: FontChooserPanel.java,v 1.1 2007/10/10 18:52:17 vauchers Exp $
 */

package com.jrefinery.util.ui;

import java.awt.*;
import javax.swing.*;

/** A panel for choosing a font from the available system fonts. */
public class FontChooserPanel extends JPanel {

  String[] sizes = { "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "28",
                     "36", "48", "72" };

  private JList fontlist;
  private JList sizelist;
  private JCheckBox bold;
  private JCheckBox italic;

  /** Standard constructor. */
  public FontChooserPanel(Font font) {

    GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
    String[] fonts = g.getAvailableFontFamilyNames();

    setLayout(new BorderLayout());
    JPanel right = new JPanel(new BorderLayout());

    JPanel fontPanel = new JPanel(new BorderLayout());
    fontPanel.setBorder(BorderFactory.createTitledBorder(
                          BorderFactory.createEtchedBorder(), "Font:"));
    fontlist = new JList(fonts);
    JScrollPane fontpane = new JScrollPane(fontlist);
    fontpane.setBorder(BorderFactory.createEtchedBorder());
    fontPanel.add(fontpane);
    add(fontPanel);

    JPanel sizePanel = new JPanel(new BorderLayout());
    sizePanel.setBorder(BorderFactory.createTitledBorder(
                          BorderFactory.createEtchedBorder(), "Size:"));
    sizelist = new JList(sizes);
    JScrollPane sizepane = new JScrollPane(sizelist);
    sizepane.setBorder(BorderFactory.createEtchedBorder());
    sizePanel.add(sizepane);


    JPanel attributes = new JPanel(new GridLayout(1, 2));
    bold = new JCheckBox("Bold");
    italic = new JCheckBox("Italic");
    attributes.add(bold);
    attributes.add(italic);
    attributes.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                         "Attributes:"));

    right.add(sizePanel, BorderLayout.CENTER);
    right.add(attributes, BorderLayout.SOUTH);

    add(right, BorderLayout.EAST);
  }

  /** Returns a Font object representing the selection in the panel. */
  public Font getSelectedFont() {
    return new Font(getSelectedName(), getSelectedStyle(), getSelectedSize());
  }

  /** Returns the selected name. */
  public String getSelectedName() {
    return (String)fontlist.getSelectedValue();
  }

  /** Returns the selected style. */
  public int getSelectedStyle() {
    if (bold.isSelected() && italic.isSelected()) {
      return Font.BOLD+Font.ITALIC;
    }
    if (bold.isSelected()) {
      return Font.BOLD;
    }
    if (italic.isSelected()) {
      return Font.ITALIC;
    }
    else return Font.PLAIN;
  }

  /** Returns the selected size. */
  public int getSelectedSize() {
    String selected = (String)sizelist.getSelectedValue();
    if (selected!=null) {
      return Integer.parseInt(selected);
    }
    else return 10;
  }

}