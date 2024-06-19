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
 * ---------------------------
 * StandardXYItemRenderer.java
 * ---------------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Mark Watson (www.markwatson.com);
 *                   Jonathan Nash;
 *
 * $Id: StandardXYItemRenderer.java,v 1.1 2007/10/10 18:59:09 vauchers Exp $
 *
 * Changes:
 * --------
 * 19-Oct-2001 : Version 1, based on code by Mark Watson (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 21-Dec-2001 : Added working line instance to improve performance (DG);
 * 22-Jan-2002 : Added code to lock crosshairs to data points.  Based on code by Jonathan Nash (DG);
 * 23-Jan-2002 : Added DrawInfo parameter to drawItem(...) method (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;
import com.jrefinery.data.*;

/**
 * Standard item renderer for an XYPlot.  This class can draw (a) shapes at each point, or (b) lines
 * between points, or (c) both shapes and lines.
 */
public class StandardXYItemRenderer implements XYItemRenderer {

    /** Useful constant for specifying the type of rendering (shapes only). */
    public static final int SHAPES = 1;

    /** Useful constant for specifying the type of rendering (lines only). */
    public static final int LINES = 2;

    /** Useful constant for specifying the type of rendering (shapes and lines). */
    public static final int SHAPES_AND_LINES = 3;

    /** A flag indicating whether or not shapes are drawn at each XY point. */
    protected boolean plotShapes;

    /** A flag indicating whether or not lines are drawn between XY points. */
    protected boolean plotLines;

    /** Scale factor for standard shapes. */
    protected double shapeScale  = 6;

    /** A working line (to save creating thousands of instances). */
    protected Line2D line;

    /**
     * Constructs a new renderer.
     */
    public StandardXYItemRenderer() {

        this(SHAPES);

    }

    /**
     * Constructs a new renderer.
     * @param The type of renderer.  Use one of the constants SHAPES, LINES or SHAPES_AND_LINES.
     */
    public StandardXYItemRenderer(int type) {

        if (type==SHAPES) this.plotShapes=true;
        if (type==LINES) this.plotLines=true;
        if (type==SHAPES_AND_LINES) {
            this.plotShapes = true;
            this.plotLines = true;
        }
        this.line = new Line2D.Double(0.0, 0.0, 0.0, 0.0);

    }

    /**
     * Returns true if shapes are being plotted by the renderer.
     */
    public boolean getPlotShapes() {
        return this.plotShapes;
    }

    /**
     * Returns true if lines are being plotted by the renderer.
     */
    public boolean getPlotLines() {
        return this.plotLines;
    }

    /**
     * Draws the visual representation of a single data item.
     * @param g2 The graphics device.
     * @param dataArea The area within which the data is being drawn.
     * @param info Collects information about the drawing.
     * @param plot The plot (can be used to obtain standard color information etc).
     * @param horizontalAxis The horizontal axis.
     * @param verticalAxis The vertical axis.
     * @param data The dataset.
     * @param series The series index.
     * @param item The item index.
     * @param translatedRangeZero Zero on the range axis (supplied so that, if it is required, it
     *        doesn't have to be calculated repeatedly).
     */
    public Shape drawItem(Graphics2D g2, Rectangle2D dataArea, DrawInfo info,
                          XYPlot plot, ValueAxis horizontalAxis, ValueAxis verticalAxis,
                          XYDataset data, int series, int item,
                          double translatedRangeZero, CrosshairInfo crosshairInfo) {

        Shape result = null;

        Paint seriesPaint = plot.getSeriesPaint(series);
        Stroke seriesStroke = plot.getSeriesStroke(series);
        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);

        // get the data point...
        Number x1 = data.getXValue(series, item);
        Number y1 = data.getYValue(series, item);
        double transX1 = horizontalAxis.translateValueToJava2D(x1.doubleValue(), dataArea);
        double transY1 = verticalAxis.translateValueToJava2D(y1.doubleValue(), dataArea);

        if (this.plotShapes) {

            Shape shape = plot.getShape(series, item, transX1, transY1, shapeScale);
            g2.draw(shape);
            result = shape;

        }

        if (this.plotLines) {

            if (item>0) {
                // get the previous data point...
                Number x0 = data.getXValue(series, item-1);
                Number y0 = data.getYValue(series, item-1);
                double transX0 = horizontalAxis.translateValueToJava2D(x0.doubleValue(), dataArea);
                double transY0 = verticalAxis.translateValueToJava2D(y0.doubleValue(), dataArea);

                line.setLine(transX0, transY0, transX1, transY1);
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

        return result;

    }

}