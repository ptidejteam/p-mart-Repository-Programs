/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * -----------------
 * NumberAxis3D.java
 * -----------------
 * (C) Copyright 2001-2003, by Serge V. Grachov and Contributors.
 *
 * Original Author:  Serge V. Grachov;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *                   Jonathan Nash;
 *                   Richard Atkinson;
 *                   Tin Luu;
 *
 * $Id: NumberAxis3D.java,v 1.1 2007/10/10 20:07:40 vauchers Exp $
 *
 * Changes
 * -------
 * 31-Oct-2001 : Version 1 contributed by Serge V. Grachov (DG);
 * 23-Nov-2001 : Overhauled auto tick unit code for all axes (DG);
 * 12-Dec-2001 : Minor change due to grid lines bug fix (DG);
 * 08-Jan-2002 : Added flag allowing the axis to be 'inverted'.  That is, run from positive to
 *               negative.  Added default values to constructors (DG);
 * 16-Jan-2002 : Added an optional crosshair, based on the implementation by Jonathan Nash (DG);
 * 25-Feb-2002 : Updated constructors for new autoRangeStickyZero flag (DG);
 * 19-Apr-2002 : drawVerticalString(...) is now drawRotatedString(...) in RefineryUtilities (DG);
 * 25-Jun-2002 : Removed redundant import (DG);
 * 25-Jul-2002 : Changed order of parameters in ValueAxis constructor (DG);
 * 06-Aug-2002 : Modified draw method to not draw axis label if label is empty String (RA);
 * 05-Sep-2002 : Updated constructor for changes in the Axis class, and changed draw method to
 *               observe tickMarkPaint (DG);
 * 22-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 08-Nov-2002 : Moved to new package com.jrefinery.chart.axis (DG);
 * 20-Jan-2003 : Removed unnecessary constructors (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 13-May-2003 : Merged HorizontalNumberAxis3D and VerticalNumberAxis3D (DG);
 *
 */

package org.jfree.chart.axis;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Iterator;

import org.jfree.chart.Effect3D;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.ui.RefineryUtilities;

/**
 * A standard linear value axis, for values displayed vertically.
 *
 * @author Serge V. Grachov
 */
public class NumberAxis3D extends NumberAxis implements Serializable {

    /**
     * Constructs a new axis.
     *
     * @param label  the axis label (<code>null</code> permitted).
     */
    public NumberAxis3D(String label) {
        super(label);
    }

    /**
     * Draws the axis on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param plotArea  the area for drawing the axes and data.
     * @param dataArea  the area for drawing the data (a subset of the plotArea).
     * @param location  the axis location.
     */
    public void draw(Graphics2D g2,
                     Rectangle2D plotArea, Rectangle2D dataArea, AxisLocation location) {

        // if the axis is not visible, don't draw it...
        if (!isVisible()) {
            return;
        }

        // use a cursor to track the (horizontal or vertical) coordinate of the items that need
        // drawing...
        double cursor = 0.0;
        if (location == AxisLocation.TOP) {
            cursor = plotArea.getMinY();
        }
        else if (location == AxisLocation.BOTTOM) {
            cursor = plotArea.getMaxY();
        }
        else if (location == AxisLocation.LEFT) {
            cursor = plotArea.getMinX();
        }
        else if (location == AxisLocation.RIGHT) {
            cursor = plotArea.getMaxX();
        }

        // draw the axis label...
        cursor = drawLabel(getLabel(), g2, plotArea, dataArea, location, cursor);

        // draw the tick labels and marks and gridlines

        // calculate the adjusted data area taking into account the 3D effect...
        CategoryPlot plot = (CategoryPlot) getPlot();

        Effect3D e3D = (Effect3D) plot.getRenderer();
        double adjustedX = dataArea.getMinX();
        double adjustedY = dataArea.getMinY();
        double adjustedW = dataArea.getWidth() - e3D.getXOffset();
        double adjustedH = dataArea.getHeight() - e3D.getYOffset();

        Rectangle2D adjustedDataArea1 = new Rectangle2D.Double(adjustedX, 
                                                               adjustedY + e3D.getYOffset(),
                                                               adjustedW, adjustedH);
        if (location == AxisLocation.LEFT || location == AxisLocation.BOTTOM) {
            adjustedY += e3D.getYOffset();
        }
        else if (location == AxisLocation.RIGHT || location == AxisLocation.TOP) {
            adjustedX += e3D.getXOffset();
        }
        Rectangle2D adjustedDataArea2 = new Rectangle2D.Double(adjustedX, adjustedY,
                                                               adjustedW, adjustedH);

        // draw the tick labels and marks and gridlines
        refreshTicks(g2, plotArea, adjustedDataArea2, location);

        double on = 0.0;
        double ol = getTickMarkOutsideLength();
        double il = getTickMarkInsideLength();
        if (location == AxisLocation.LEFT) {
            on = dataArea.getMinX();
        }
        else if (location == AxisLocation.RIGHT) {
            on = dataArea.getMaxX();
        }
        else if (location == AxisLocation.TOP) {
            on = dataArea.getMinY();
        }
        else if (location == AxisLocation.BOTTOM) {
            on = dataArea.getMaxY();
        }
        g2.setFont(getTickLabelFont());

        Iterator iterator = getTicks().iterator();
        while (iterator.hasNext()) {
            Tick tick = (Tick) iterator.next();
            float xx = (float) translateValueToJava2D(tick.getNumericalValue(), adjustedDataArea2,
                                                      location);
            if (isTickLabelsVisible()) {
                g2.setPaint(getTickLabelPaint());
                if (isVerticalTickLabels()) {
                    RefineryUtilities.drawRotatedString(tick.getText(), g2,
                                                        tick.getX(), tick.getY(), -Math.PI / 2);
                }
                else {
                    g2.drawString(tick.getText(), tick.getX(), tick.getY());
                }
            }

            if (isTickMarksVisible()) {
                Line2D mark = null;
                g2.setStroke(getTickMarkStroke());
                g2.setPaint(getTickMarkPaint());
                if (location == AxisLocation.LEFT) {
                    mark = new Line2D.Double(on - ol, xx, on + il, xx);
                }
                else if (location == AxisLocation.RIGHT) {
                    mark = new Line2D.Double(on + ol, xx, on - il, xx);
                }
                else if (location == AxisLocation.TOP) {
                    mark = new Line2D.Double(xx, on - ol, xx, on + il);
                }
                else if (location == AxisLocation.BOTTOM) {
                    mark = new Line2D.Double(xx, on + ol, xx, on - il);
                }
                g2.draw(mark);
            }
        }

    }

}
