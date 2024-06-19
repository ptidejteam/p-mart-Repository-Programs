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
 * $Id: LeftRightButtonPanel.java,v 1.1 2007/10/10 18:52:17 vauchers Exp $
 */

package com.jrefinery.util.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** A 'ready-made' panel that has one button on the left and another button on the right - a
    layout manager takes care of resizing. */
public class LeftRightButtonPanel extends JPanel {

  private JButton left;
  private JButton right;

  /** Standard constructor - creates a two-button panel with the specified labels. */
  public LeftRightButtonPanel(String leftLabel, String rightLabel) {

    setLayout(new BorderLayout());
    left = new JButton(leftLabel);
    right = new JButton(rightLabel);
    add(left, BorderLayout.WEST);
    add(right, BorderLayout.EAST);

  }

  /** Returns a reference to button 1, allowing the caller to set labels, action-listeners etc. */
  public JButton getLeftButton() {
    return left;
  }

  /** Returns a reference to button 2, allowing the caller to set labels, action-listeners etc. */
  public JButton getRightButton() {
    return right;
  }

}
