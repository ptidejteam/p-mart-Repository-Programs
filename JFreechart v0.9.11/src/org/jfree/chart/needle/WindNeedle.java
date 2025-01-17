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
 * ---------------
 * WindNeedle.java
 * ---------------
 * (C) Copyright 2002, 2003, by the Australian Antarctic Division and Contributors.
 *
 * Original Author:  Bryan Scott (for the Australian Antarctic Division);
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: WindNeedle.java,v 1.1 2007/10/10 19:09:25 vauchers Exp $
 *
 * Changes:
 * --------
 * 25-Sep-2002 : Version 1, contributed by Bryan Scott (DG);
 *
 */

package org.jfree.chart.needle;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

/**
 * A needle that indicates wind direction, for use with the
 * {@link org.jfree.chart.plot.CompassPlot} class.
 *
 * @author Bryan Scott
 */
public class WindNeedle extends ArrowNeedle implements Serializable {

    /**
     * Default constructor.
     */
    public WindNeedle() {
        super(false);  // isArrowAtTop
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

        super.drawNeedle(g2, plotArea, rotate, angle);
        if ((rotate != null) && (plotArea != null)) {

            int spacing = getSize() * 3;
            Rectangle2D newArea = new Rectangle2D.Double();

            Point2D newRotate = rotate;
            //Point2D newRotate = new Point2D.Double(rotate.getX()-spacing, rotate.getY());
            newArea.setRect(plotArea.getMinX() - spacing, plotArea.getMinY(),
                            plotArea.getWidth(), plotArea.getHeight());
            super.drawNeedle(g2, newArea, newRotate, angle);

            //newRotate.setLocation(rotate.getX()+spacing, rotate.getY());
            newArea.setRect(plotArea.getMinX() + spacing, plotArea.getMinY(),
                            plotArea.getWidth(), plotArea.getHeight());
            super.drawNeedle(g2, newArea, newRotate, angle);

        }
    }

}

