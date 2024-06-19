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
 * -----------------------------
 * HorizontalCategoryAxis3D.java
 * -----------------------------
 * (C) Copyright 2003, by Klaus Rheinwald and Contributors.
 *
 * Original Author:  Klaus Rheinwald
 * Contributor(s):   Tin Luu, 
 *                   David Gilbert (for Simba Management Limited);
 *
 * Changes
 * -------
 * 19-Feb-2003 : File creation;
 * 21-Mar-2003 : Added to JFreeChart CVS, see bug id 685501 for code contribution from KR (DG);
 * 26-Mar-2003 : Implemented Serializable (DG); 
 * 
 */

package org.jfree.chart.axis;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Iterator;

import org.jfree.chart.Effect3D;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.ui.RefineryUtilities;

/**
 * A horizontal axis that displays categories and has a 3D effect.
 * Used for bar charts and line charts.
 *
 * @author Klaus Rheinwald
 */
public class HorizontalCategoryAxis3D extends HorizontalCategoryAxis implements Serializable {

    /**
     * Creates a new axis using default attribute values.
     *
     * @param label  the axis label (<code>null</code> permitted).
     */
    public HorizontalCategoryAxis3D(String label) {
        super(label);
    }

    /**
     * Draws the axis on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the axis should be drawn.
     * @param dataArea  the area within which the plot is being drawn.
     * @param location  the location of the axis (TOP or BOTTOM).
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, int location) {

        // if the axis is not visible, don't draw it...
        if (!isVisible()) {
            return;
        }

        // use a cursor to track the vertical level of the items that need drawing...
        double cursorY = 0.0;
        if (location == TOP) {
            cursorY = plotArea.getMinY();
        }
        else {
            cursorY = plotArea.getMaxY();
        }

        // draw the axis label...
        cursorY = drawHorizontalLabel(getLabel(), g2, plotArea, dataArea, location, cursorY);

        // draw the category labels
        if (isTickLabelsVisible()) {
            Font tickLabelFont = getTickLabelFont();
            g2.setFont(tickLabelFont);
            g2.setPaint(getTickLabelPaint());
            
            // draw the tick labels and marks
            // calculate the adjusted data area taking into account the 3D effect...
            CategoryPlot plot = (CategoryPlot) getPlot();
            Effect3D e3D = (Effect3D) plot.getRenderer();
            Rectangle2D adjustedDataArea = new Rectangle2D.Double(dataArea.getMinX(),
                dataArea.getMinY() + e3D.getYOffset(), dataArea.getWidth() - e3D.getXOffset(),
                dataArea.getHeight() - e3D.getYOffset());
            refreshTicks(g2, plotArea, adjustedDataArea, location);
            
            Iterator iterator = getTicks().iterator();
            while (iterator.hasNext()) {
                Object obj = iterator.next();
                if (obj instanceof Tick) {
                    Tick tick = (Tick) obj;
                    if (this.getVerticalCategoryLabels()) {   //KR
                        RefineryUtilities.drawRotatedString(tick.getText(), g2,
                                                            tick.getX(), tick.getY(),
                                                            -Math.PI / 2);
                    }
                    else {
                        g2.drawString(tick.getText(), tick.getX(), tick.getY());
                    }
                }
                else {
                    Tick[] ts = (Tick[]) obj;
                    for (int i = 0; i < ts.length; i++) {
                        g2.drawString(ts[i].getText(), ts[i].getX(), ts[i].getY());
                    }
                }
            }
        }

    }
}
