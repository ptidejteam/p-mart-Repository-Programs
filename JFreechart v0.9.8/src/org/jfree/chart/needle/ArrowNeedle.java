/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
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
 * ----------------
 * ArrowNeedle.java
 * ----------------
 * (C) Copyright 2002, 2003, by the Australian Antarctic Division and Contributors.
 *
 * Original Author:  Bryan Scott (for the Australian Antarctic Division);
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: ArrowNeedle.java,v 1.1 2007/10/10 20:03:25 vauchers Exp $
 *
 * Changes:
 * --------
 * 25-Sep-2002 : Version 1, contributed by Bryan Scott (DG);
 * 27-Mar-2003 : Implemented Serializable (DG);
 *
 */

package org.jfree.chart.needle;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

/**
 * A needle in the shape of an arrow.
 *
 * @author Bryan Scott
 */
public class ArrowNeedle extends MeterNeedle implements Serializable {

    /** A flag controlling whether or not there is an arrow at the top of the needle. */
    private boolean isArrowAtTop = true;

    /**
     * Constructs a new arrow needle.
     *
     * @param isArrowAtTop  a flag that controls whether or not there is an arrow at the top of
     *                      the needle.
     */
    public ArrowNeedle(boolean isArrowAtTop) {
        this.isArrowAtTop = isArrowAtTop;
    }

    /**
     * Draws the needle.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param rotate  the rotation point.
     * @param angle  the angle.
     */
    protected void drawNeedle(Graphics2D g2, Rectangle2D plotArea, Point2D rotate, double angle) {

        Line2D shape = new Line2D.Float();
        Shape d = null;

        float x = (float) (plotArea.getMinX() +  (plotArea.getWidth() / 2));
        float minY = (float) plotArea.getMinY();
        float maxY = (float) plotArea.getMaxY();
        shape.setLine(x, minY, x, maxY);

        GeneralPath shape1 = new GeneralPath();
        if (isArrowAtTop) {
            shape1.moveTo(x, minY);
            minY += 4 * getSize();
        }
        else {
            shape1.moveTo(x, maxY);
            minY = maxY - 4 * getSize();
        }
        shape1.lineTo(x + getSize(), minY);
        shape1.lineTo(x - getSize(), minY);
        shape1.closePath();

        if ((rotate != null) && (angle != 0)) {
            getTransform().setToRotation(angle, rotate.getX(), rotate.getY());
            d = getTransform().createTransformedShape(shape);
        }
        else {
            d = shape;
        }
        defaultDisplay(g2, d);

        if ((rotate != null) && (angle != 0)) {
            d = getTransform().createTransformedShape(shape1);
        }
        else {
            d = shape1;
        }
        defaultDisplay(g2, d);

    }

}
