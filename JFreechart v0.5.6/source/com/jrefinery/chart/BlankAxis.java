/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            BlankAxis.java
 * Author:          Andrzej Porebski;
 * Contributor(s):  David Gilbert;
 *
 * (C) Copyright 2000, by Andrzej Porebski;
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
 * $Id: BlankAxis.java,v 1.1 2007/10/10 18:52:16 vauchers Exp $
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;

/**
 * A blank axis that can be used as either vertical or horizontal axis.
 */
public class BlankAxis extends Axis implements HorizontalAxis, VerticalAxis {

  /**
   * Default constructor
   */
  public BlankAxis() {
    super("");
  }

  /**
   * Returns the area required to draw the axis in the specified draw area.  This implementation
   * returns 0.
   * @param g2 The graphics device;
   * @param plot The plot that the axis belongs to;
   * @param drawArea The area within which the plot should be drawn;
   * @param reservedHeight The height reserved by the horizontal axis.
   */

  public Rectangle2D reserveAxisArea(Graphics2D g2, Plot plot, Rectangle2D drawArea,
                                     double reservedWidth) {

    return new Rectangle(0, 0, 0, 0);
  }

  /**
   * Returns the height required to draw the axis in the specified draw area.  This implementation
   * returns 0.
   * @param g2 The graphics device;
   * @param plot The plot that the axis belongs to;
   * @param drawArea The area within which the plot should be drawn.
   */
  public double reserveHeight(Graphics2D g2, Plot plot, Rectangle2D drawArea) {
    return 0;
  }

  /**
   * Returns the width required to draw the axis in the specified draw area.  This implementation
   * returns 0.
   * @param g2 The graphics device;
   * @param drawArea The area within which the plot should be drawn;
   */
  public double reserveWidth(Graphics2D g2, Plot plot, Rectangle2D drawArea) {
    return 0;
  }

  /**
   * This implementation does nothing.
   */
  public void draw(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

  }

  /**
   * Calculates the positions of the ticks for the axis, storing the results in the tick list (ready
   * for drawing).  This implementation does nothing.
   */
  public void refreshTicks(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {
  }

}