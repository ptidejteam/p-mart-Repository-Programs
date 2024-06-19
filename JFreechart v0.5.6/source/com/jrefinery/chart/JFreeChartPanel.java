/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            JFreeChartPanel.java
 * Author:          David Gilbert;
 * Contributor(s):  Andrzej Porebski;
 *
 * (C) Copyright 2000, by Simba Management Limited;
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
 * $Id: JFreeChartPanel.java,v 1.1 2007/10/10 18:52:16 vauchers Exp $
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import com.jrefinery.chart.event.*;

/**
 * A GUI component for displaying a JFreeChart.  The panel is registered to receive notification
 * of changes to the chart, so that the chart can be redrawn automatically as required.
 */
public class JFreeChartPanel extends JComponent
                             implements ChartChangeListener {

  /** The chart that is contained within the panel. */
  protected JFreeChart chart;

  /**
   * Full constructor: returns a panel containing the specified chart.
   * @param chart The chart to display in the panel;
   */
  public JFreeChartPanel(JFreeChart chart) {
    this.chart = chart;
    this.chart.addChangeListener(this);
    setPreferredSize(new Dimension(480, 320));
  }

  /**
   * Returns a reference to the chart displayed in the panel.
   */
  public JFreeChart getChart() {
    return chart;
  }

  /**
   * Paints the component - this means drawing the chart to fill the entire component, but
   * allowing for the insets (which will be non-zero if a border has been set for this
   * component);
   */
  public void paintComponent(Graphics g) {

    Graphics2D g2 = (Graphics2D)g;
    Dimension size = getSize();
    Insets insets = getInsets();
    Rectangle2D chartArea = new Rectangle2D.Double(insets.left, insets.top,
                                                   size.getWidth()-insets.left-insets.right,
                                                   size.getHeight()-insets.top-insets.bottom);
    chart.draw(g2, chartArea);

  }

  /**
   * Receives notification of changes to the chart, and redraws the chart.
   */
  public void chartChanged(ChartChangeEvent event) {
    this.repaint();
  }

}
