/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
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
 * XYStepRenderer.java
 * -------------------
 * (C) Copyright 2002, by Roger Studner and Contributors.
 *
 * Original Author:  Roger Studner;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: XYStepRenderer.java,v 1.1 2007/10/10 19:01:18 vauchers Exp $
 *
 * Changes
 * -------
 * 13-May-2002 : Version 1, contributed by Roger Studner (DG);
 *
 */
package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;
import com.jrefinery.data.*;

/**
 * Line/Step item renderer for an XYPlot.  This class draws lines between data points,
 * only allowing horizontal or vertical lines (steps).
 *
 */
public class XYStepRenderer extends AbstractXYItemRenderer implements XYItemRenderer {

    /** A working line (to save creating many instances). */
    protected Line2D line;

    /**
     * Constructs a new renderer.
     */
    public XYStepRenderer() {

        this.line = new Line2D.Double(0.0, 0.0, 0.0, 0.0);

    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2 The graphics device.
     * @param dataArea The area within which the data is being drawn.
     * @param info Collects information about the drawing.
     * @param plot The plot (can be used to obtain standard color information etc).
     * @param horizontalAxis The horizontal axis.
     * @param verticalAxis The vertical axis.
     * @param data The dataset.
     * @param series The series index.
     * @param item The item index.
     */
    public void drawItem(Graphics2D g2, Rectangle2D dataArea, ChartRenderingInfo info,
                          XYPlot plot, ValueAxis horizontalAxis, ValueAxis verticalAxis,
                          XYDataset data, int series, int item,
                          CrosshairInfo crosshairInfo) {

        Paint seriesPaint = plot.getSeriesPaint(series);
        Stroke seriesStroke = plot.getSeriesStroke(series);
        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);

        // get the data point...
        Number x1 = data.getXValue(series, item);
        Number y1 = data.getYValue(series, item);
        double transX1 = horizontalAxis.translateValueToJava2D(x1.doubleValue(), dataArea);
        double transY1 = verticalAxis.translateValueToJava2D(y1.doubleValue(), dataArea);

         if (item>0) {
             // get the previous data point...
             Number x0 = data.getXValue(series, item-1);
             Number y0 = data.getYValue(series, item-1);
             double transX0 = horizontalAxis.translateValueToJava2D(x0.doubleValue(), dataArea);
             double transY0 = verticalAxis.translateValueToJava2D(y0.doubleValue(), dataArea);

             if (transY0 == transY1) { //this represents the situation for drawing a
                                       //horizontal bar.
                line.setLine(transX0, transY0, transX1, transY1);
                g2.draw(line);
             }
             else {  //this handles the need to perform a 'step'.
                line.setLine(transX0, transY0, transX1, transY0);
                g2.draw(line);
                line.setLine(transX1, transY0, transX1, transY1);
                g2.draw(line);
             }
         }

        // do we need to update the crosshair values?
        double distance = 0.0;
        if (horizontalAxis.isCrosshairLockedOnData()) {
            if (verticalAxis.isCrosshairLockedOnData()) {
                // both axes
                crosshairInfo.updateCrosshairPoint(x1.doubleValue(), y1.doubleValue());
            }
            else {
                // just the horizontal axis...
                crosshairInfo.updateCrosshairX(x1.doubleValue());

            }
        }
        else {
            if (verticalAxis.isCrosshairLockedOnData()) {
                // just the vertical axis...
                crosshairInfo.updateCrosshairY(y1.doubleValue());
            }
        }

    }

}