/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: StandardXYItemRenderer.java,v 1.1 2007/10/10 18:54:39 vauchers Exp $
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Mark Watson (www.markwatson.com);
 *
 * (C) Copyright 2001 Simba Management Limited;
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
 * Change History:
 * ---------------
 * 19-Oct-2001 : Initial implementation, base on code by Mark Watson (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
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

    /**
     * Default constructor.
     */
    public StandardXYItemRenderer() {
        this.plotShapes = true;
        this.plotLines = false;
    }

    /**
     * Standard constructor.
     * @param The type of renderer.  Use one of the constants SHAPES, LINES or SHAPES_AND_LINES.
     */
    public StandardXYItemRenderer(int type) {
        if (type==SHAPES) this.plotShapes=true;
        if (type==LINES) this.plotLines=true;
        if (type==SHAPES_AND_LINES) {
            this.plotShapes = true;
            this.plotLines = true;
        }
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
     * @param plotArea The area within which the plot is being drawn.
     * @param plot The plot (can be used to obtain standard color information etc).
     * @param horizontalAxis The horizontal axis.
     * @param verticalAxis The vertical axis.
     * @param data The dataset.
     * @param series The series index.
     * @param item The item index.
     * @param translatedRangeZero Zero on the range axis (supplied so that, if it is required, it
     *        doesn't have to be calculated repeatedly).
     */
    public void drawItem(Graphics2D g2, Rectangle2D plotArea,
                         Plot plot, ValueAxis horizontalAxis, ValueAxis verticalAxis,
                         XYDataset data, int series, int item,
                         double translatedRangeZero) {

        Paint seriesPaint = plot.getSeriesPaint(series);
        Stroke seriesStroke = plot.getSeriesStroke(series);
        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);

        // get the data point...
        Number x1 = data.getXValue(series, item);
        Number y1 = data.getYValue(series, item);
        double transX1 = horizontalAxis.translatedValue(x1, plotArea);
        double transY1 = verticalAxis.translatedValue(y1, plotArea);

        if (this.plotShapes) {
            Shape shape = plot.getShape(series, item, transX1, transY1, shapeScale);
            g2.draw(shape);
        }

        if (this.plotLines) {
            if (item>0) {

                // get the previous data point...
                Number x0 = data.getXValue(series, item-1);
                Number y0 = data.getYValue(series, item-1);
                double transX0 = horizontalAxis.translatedValue(x0, plotArea);
                double transY0 = verticalAxis.translatedValue(y0, plotArea);

                Line2D line = new Line2D.Double(transX0, transY0, transX1, transY1);
                g2.draw(line);
            }
        }

    }

}