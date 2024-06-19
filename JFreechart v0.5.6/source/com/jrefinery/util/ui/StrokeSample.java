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
 * $Id: StrokeSample.java,v 1.1 2007/10/10 18:52:17 vauchers Exp $
 */

package com.jrefinery.util.ui;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

/** A panel that displays a stroke sample. */
public class StrokeSample extends JComponent implements ListCellRenderer {

  protected Stroke stroke;
  protected Dimension preferredSize;

  /** Standard constructor. */
  public StrokeSample(Stroke stroke) {
    this.stroke = stroke;
    this.preferredSize = new Dimension(80, 18);
  }

  /** Returns the current Paint object being displayed in the panel. */
  public Stroke getStroke() {
    return stroke;
  }

  /** Sets the Paint object being displayed in the panel. */
  public void setStroke(Stroke stroke) {
    this.stroke = stroke;
    this.repaint();
  }

  /** Returns the preferred size of the component. */
  public Dimension getPreferredSize() {
    return preferredSize;
  }

  /** Draws a line using the sample stroke. */
  public void paintComponent(Graphics g) {

    Graphics2D g2 = (Graphics2D)g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    Dimension size = getSize();
    Insets insets = getInsets();
    double xx = insets.left;
    double yy = insets.top;
    double ww = size.getWidth()-insets.left-insets.right;
    double hh = size.getHeight()-insets.top-insets.bottom;

    // calculate point one
    Point2D one =  new Point2D.Double(xx+6, yy+hh/2);
    // calculate point two
    Point2D two =  new Point2D.Double(xx+ww-6, yy+hh/2);
    // draw a circle at point one
    Ellipse2D circle1 = new Ellipse2D.Double(one.getX()-5, one.getY()-5, 10, 10);
    Ellipse2D circle2 = new Ellipse2D.Double(two.getX()-6, two.getY()-5, 10, 10);

    // draw a circle at point two
    g2.draw(circle1);
    g2.fill(circle1);
    g2.draw(circle2);
    g2.fill(circle2);

    // draw a line connecting the points
    Line2D line = new Line2D.Double(one, two);
    g2.setStroke(stroke);
    g2.draw(line);

  }

  public Component getListCellRendererComponent(JList list, Object value,
                                                int index, boolean isSelected, boolean cellHasFocus) {
    if (value instanceof StrokeSample) {
      StrokeSample in = (StrokeSample)value;
      this.setStroke(in.getStroke());
    }
    return this;
  }

}
