/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: LineAndShapeRenderer.java,v 1.1 2007/10/10 18:54:39 vauchers Exp $
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
 * Changes
 * -------
 * 23-Oct-2001 : Initial implementation (DG);
 * 15-Nov-2001 : Modified to allow for null data values (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;
import com.jrefinery.data.*;

public class LineAndShapeRenderer implements HorizontalCategoryItemRenderer {

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
     * Constructs a renderer that draws shapes only.
     */
    public LineAndShapeRenderer() {
        this.plotShapes = true;
        this.plotLines = false;
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

    /**
     * Draws the horizontal category item.
     */
    public void drawHorizontalCategoryItem(Graphics2D g2, Rectangle2D plotArea, CategoryPlot plot,
                                           ValueAxis axis, CategoryDataset data, int series,
                                           Object category, int categoryIndex,
                                           Object previousCategory) {

        // first check the number we are plotting...
        Number value = data.getValue(series, category);
        if (value!=null) {
            // Current X
            double x1 = plot.getCategoryCoordinate(categoryIndex, plotArea);

            // Current Y
            double y1 = axis.translatedValue(value, plotArea);

            g2.setPaint(((Plot)plot).getSeriesPaint(series));
            g2.setStroke(((Plot)plot).getSeriesStroke(series));

            if (this.plotShapes) {
                Shape shape = ((Plot)plot).getShape(series, category, x1, y1, shapeScale);
                g2.fill(shape);
                //g2.draw(shape);
            }

            if (this.plotLines) {
                if (previousCategory!=null) {

                    Number previousValue = data.getValue(series, previousCategory);
                    if (previousValue!=null) {
                        // get the previous data point...
                        double x0 = plot.getCategoryCoordinate(categoryIndex-1, plotArea);
                        double y0 = axis.translatedValue(previousValue, plotArea);

                        g2.setPaint(((Plot)plot).getSeriesPaint(series));
                        g2.setStroke(((Plot)plot).getSeriesStroke(series));
                        Line2D line = new Line2D.Double(x0, y0, x1, y1);
                        g2.draw(line);
                    }

                }
            }
        }

    }

}