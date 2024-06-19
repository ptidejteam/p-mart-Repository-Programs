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
 * -------------------------
 * LineAndShapeRenderer.java
 * -------------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Mark Watson (www.markwatson.com);
 *
 * $Id: LineAndShapeRenderer.java,v 1.1 2007/10/10 19:02:36 vauchers Exp $
 *
 * Changes
 * -------
 * 23-Oct-2001 : Version 1 (DG);
 * 15-Nov-2001 : Modified to allow for null data values (DG);
 * 16-Jan-2002 : Renamed HorizontalCategoryItemRenderer.java --> CategoryItemRenderer.java (DG);
 * 05-Feb-2002 : Changed return type of the drawCategoryItem method from void to Shape, as part
 *               of the tooltips implementation (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import com.jrefinery.data.CategoryDataset;

/**
 * A renderer for a CategoryPlot that draws shapes for each data item, and lines between data items.
 * The renderer is immutable so that the only way to change the renderer for a plot is to call the
 * setRenderer() method.
 */
public class LineAndShapeRenderer implements CategoryItemRenderer {

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
    protected double shapeScale = 6;

    /**
     * Constructs a default renderer (draws shapes and lines).
     */
    public LineAndShapeRenderer() {
        this(SHAPES_AND_LINES);
    }

    /**
     * Constructs a renderer of the specified type.
     * @param The type of renderer.  Use one of the constants SHAPES, LINES or SHAPES_AND_LINES.
     */
    public LineAndShapeRenderer(int type) {
        if (type==SHAPES) this.plotShapes=true;
        if (type==LINES) this.plotLines=true;
        if (type==SHAPES_AND_LINES) {
            this.plotShapes = true;
            this.plotLines = true;
        }
    }

    public boolean isStacked() {
        return false;
    }

    public void initialise(Graphics2D g2,
                           Rectangle2D dataArea,
                           CategoryPlot plot,
                           CategoryDataset data) {
    }

    /**
     * Draw a single data item.
     * @param g2 The graphics device.
     * @param dataArea The area in which the data is drawn.
     * @param plot The plot.
     * @param axis The range axis.
     * @param data The data.
     * @param series The series number (zero-based index).
     * @param category The category.
     * @param categoryIndex The category number (zero-based index).
     * @param previousCategory The previous category (will be null when the first category is
     *                         drawn).
     */
    public Shape drawCategoryItem(Graphics2D g2, Rectangle2D dataArea,
                                  CategoryPlot plot,
                                  ValueAxis axis,
                                  CategoryDataset data,
                                  int series,
                                  Object category, int categoryIndex, Object previousCategory) {

        Shape result = null;

        // first check the number we are plotting...
        Number value = data.getValue(series, category);
        if (value!=null) {
            // Current X
            double x1 = plot.getCategoryCoordinate(categoryIndex, dataArea);

            // Current Y
            double y1 = axis.translateValueToJava2D(value.doubleValue(), dataArea);

            g2.setPaint(plot.getSeriesPaint(series));
            g2.setStroke(plot.getSeriesStroke(series));

            if (this.plotShapes) {
                Shape shape = ((Plot)plot).getShape(series, category, x1, y1, shapeScale);
                g2.fill(shape);
                result = shape;
                //g2.draw(shape);
            }

            if (this.plotLines) {
                if (previousCategory!=null) {

                    Number previousValue = data.getValue(series, previousCategory);
                    if (previousValue!=null) {
                        // get the previous data point...
                        double x0 = plot.getCategoryCoordinate(categoryIndex-1, dataArea);
                        double y0 = axis.translateValueToJava2D(previousValue.doubleValue(), dataArea);

                        g2.setPaint(plot.getSeriesPaint(series));
                        g2.setStroke(plot.getSeriesStroke(series));
                        Line2D line = new Line2D.Double(x0, y0, x1, y1);
                        g2.draw(line);
                    }

                }
            }
        }
        return result;

    }

}