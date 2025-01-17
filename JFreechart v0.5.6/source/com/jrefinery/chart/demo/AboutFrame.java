/* ===============
 * JFreeChart Demo
 * ===============
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            AboutFrame.java
 * Author:          David Gilbert;
 * Contributor(s):  -;
 *
 * (C) Copyright 2000, Simba Management Limited;
 *
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307, USA.
 *
 * $Id: AboutFrame.java,v 1.1 2007/10/10 18:52:17 vauchers Exp $
 */

package com.jrefinery.chart.demo;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * A frame that displays information about the application.
 */
public class AboutFrame extends JFrame implements ActionListener {

  /**
   * Standard constructor for the About frame.
   */
  public AboutFrame(String title) {
    super(title);

    JPanel contentPanel;
    JButton b;

    contentPanel = new JPanel(new BorderLayout());
    contentPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    b = new JButton(new ImageIcon(AboutFrame.class.getResource("About0-5-6.gif")));
    b.addActionListener(this);
    contentPanel.add(b, BorderLayout.CENTER);
    setContentPane(contentPanel);
    pack();
  }

  /**
   * Closes the frame (this method is triggered when the button in the frame is clicked).
   */
  public void actionPerformed(ActionEvent e) {
    dispose();
  }

}
