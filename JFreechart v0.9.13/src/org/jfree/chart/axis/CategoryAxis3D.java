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
 * -------------------
 * CategoryAxis3D.java
 * -------------------
 * (C) Copyright 2003, by Klaus Rheinwald and Contributors.
 *
 * Original Author:  Klaus Rheinwald;
 * Contributor(s):   Tin Luu,
 *                   David Gilbert (for Object Refinery Limited);
 *
 * Changes
 * -------
 * 19-Feb-2003 : File creation;
 * 21-Mar-2003 : Added to JFreeChart CVS, see bug id 685501 for code contribution from KR (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 13-May-2003 : Renamed HorizontalCategoryAxis3D --> CategoryAxis3D, and modified to take into
 *               account the plot orientation (DG);
 * 14-Aug-2003 : Implemented Cloneable (DG);
 * 21-Aug-2003 : Fixed draw(...) method bugs (DG);
 *
 */

package org.jfree.chart.axis;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.Effect3D;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.ui.RectangleEdge;

/**
 * An axis that displays categories and has a 3D effect.
 * Used for bar charts and line charts.
 *
 * @author Klaus Rheinwald
 */
public class CategoryAxis3D extends CategoryAxis implements Cloneable, Serializable {

    /**
     * Creates a new axis.
     */
    public CategoryAxis3D() {
        this(null);
    }
    
    /**
     * Creates a new axis using default attribute values.
     *
     * @param label  the axis label (<code>null</code> permitted).
     */
    public CategoryAxis3D(String label) {
        super(label);
    }

    /**
     * Draws the axis on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param cursor  the cursor location.
     * @param plotArea  the area within which the axis should be drawn.
     * @param dataArea  the area within which the plot is being drawn.
     * @param edge  the location of the axis.
     * 
     * @return The updated cursor value.
     */
    public double draw(Graphics2D g2, double cursor,
                       Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge) {

        // if the axis is not visible, don't draw it...
        if (!isVisible()) {
            return 0.0;
        }

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
        if (edge == RectangleEdge.LEFT || edge == RectangleEdge.BOTTOM) {
            adjustedY += e3D.getYOffset();
        }
        else if (edge == RectangleEdge.RIGHT || edge == RectangleEdge.TOP) {
            adjustedX += e3D.getXOffset();
        }
        Rectangle2D adjustedDataArea2 = new Rectangle2D.Double(adjustedX, adjustedY,
                                                               adjustedW, adjustedH);

        // draw the category labels
        double used1 = 0.0;
        if (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM) {
            used1 = drawHorizontalCategoryLabels(g2, cursor, plotArea, adjustedDataArea2, edge);
        }
        else if (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT) {
            used1 = drawVerticalCategoryLabels(g2, cursor, plotArea, adjustedDataArea2, edge);
        }
        if (edge == RectangleEdge.TOP || edge == RectangleEdge.LEFT) {
            cursor = cursor - used1;
        }
        else if (edge == RectangleEdge.BOTTOM || edge == RectangleEdge.RIGHT) {
            cursor = cursor + used1;
        }

        // draw the axis label...
        double used2 = drawLabel(getLabel(), g2, cursor, plotArea, dataArea, edge);

        return used1 + used2;
        
    }

    /**
     * Returns a clone of the axis.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException If the axis is not cloneable for some reason.
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
