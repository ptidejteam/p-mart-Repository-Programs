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
 * $Id: RightTwoButtonPanel.java,v 1.1 2007/10/10 18:52:17 vauchers Exp $
 */

package com.jrefinery.util.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** A 'ready-made' panel that has two buttons on the right - nested
    panels and layout managers take care of resizing. */
public class RightTwoButtonPanel extends JPanel {

  protected JButton right1;
  protected JButton right2;

  /** Standard constructor - creates a three button panel with the specified button labels. */
  public RightTwoButtonPanel(String label1, String label2) {

    setLayout(new BorderLayout());

    // create the pieces...
    JPanel panel=new JPanel(new BorderLayout());
    right1 = new JButton(label1);
    right2 = new JButton(label2);

    // ...and put them together
    panel.add(right1, BorderLayout.EAST);
    add(panel, BorderLayout.CENTER);
    add(right2, BorderLayout.EAST);

  }

  /** Returns a reference to button 2, allowing the caller to set labels, action-listeners etc. */
  public JButton getRightButton1() {
    return right1;
  }

  /** Returns a reference to button 3, allowing the caller to set labels, action-listeners etc. */
  public JButton getRightButton2() {
    return right2;
  }

}
