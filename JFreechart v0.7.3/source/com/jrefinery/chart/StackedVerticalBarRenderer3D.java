/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
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
 * ---------------------------------
 * StackedVerticalBarRenderer3D.java
 * ---------------------------------
 * (C) Copyright 2000-2002, by Serge V. Grachov and Contributors.
 *
 * Original Author:  Serge V. Grachov;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: StackedVerticalBarRenderer3D.java,v 1.1 2007/10/10 18:57:06 vauchers Exp $
 *
 * Changes
 * -------
 * 31-Oct-2001 : Version 1, contributed by Serge V. Grachov (DG);
 * 15-Nov-2001 : Modified to allow for null data values (DG);
 * 13-Dec-2001 : Added tooltips (DG);
 * 15-Feb-2002 : Added isStacked() method (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.GeneralPath;
import com.jrefinery.data.CategoryDataset;

/**
 * Renders vertical stacked bars with 3D-effect.
 */
public class StackedVerticalBarRenderer3D extends VerticalBarRenderer3D {

    /**
     * Default constructor.
     */
    public StackedVerticalBarRenderer3D() {
    }

    /**
     * Returns true, to indicate that this renderer stacks values.  This affects the axis range
     * required to display all values.
     */
    public boolean isStacked() {

        return true;

    }

    /**
     * This will be a method in the renderer that tells whether there is one bar width per category
     * or onebarwidth per series per category.
     */
    public int barWidthsPerCategory(CategoryDataset data) {
        return 1;
    }

    /**
     * Returns false, since the items in each category are stacked on top of one another.
     */
    public boolean hasItemGaps() {
        return false;
    }

    /**
     * Draws an individual item.  There are some bug-fixes in the StackedVerticalBarRenderer class
     * that need to get applied here...
     */
    public Shape drawBar(Graphics2D g2, Rectangle2D dataArea, BarPlot plot, ValueAxis valueAxis,
                         CategoryDataset data, int series, Object category, int categoryIndex,
                         double translatedZero, double itemWidth,
                         double categorySpan, double categoryGapSpan,
                         double itemSpan, double itemGapSpan) {

        Shape result = null;

        Paint seriesPaint = plot.getSeriesPaint(series);
        Paint seriesOutlinePaint = plot.getSeriesOutlinePaint(series);

        // BAR X
        double rectX = dataArea.getX()
                           // intro gap
                           + dataArea.getWidth()*plot.getIntroGapPercent()
                           // bars in completed categories
                           + categoryIndex*categorySpan/data.getCategoryCount();
        if (data.getCategoryCount()>1) {
            // gaps between completed categories
            rectX = rectX+categoryIndex*categoryGapSpan/(data.getCategoryCount()-1);
        }

        // BAR Y
        double positiveBase = 0.0;
        double negativeBase = 0.0;

        for (int i=0; i<series; i++) {
            Number v = data.getValue(i, category);
            if (v!=null) {
                double d = v.doubleValue();
                if (d>0) positiveBase = positiveBase+d;
                else negativeBase = negativeBase+d;
            }
        }

        Number value = data.getValue(series, category);
        if (value!=null) {

            double xx = value.doubleValue();
            double translatedBase;
            double translatedValue;
            double barY;
            if (xx>0) {
                translatedBase = valueAxis.translateValueToJava2D(positiveBase, dataArea);
                translatedValue = valueAxis.translateValueToJava2D(positiveBase+xx, dataArea);
                barY = Math.min(translatedBase, translatedValue);
            }
            else {
                translatedBase = valueAxis.translateValueToJava2D(negativeBase, dataArea);
                translatedValue = valueAxis.translateValueToJava2D(negativeBase+xx, dataArea);
                barY = Math.min(translatedBase, translatedValue);
            }

            // BAR WIDTH
            double rectWidth = itemWidth;

            // BAR HEIGHT
            double barHeight = Math.abs(translatedValue-translatedBase);

            Rectangle2D bar = new Rectangle2D.Double(rectX, barY, rectWidth, barHeight);
            g2.setPaint(seriesPaint);
            g2.fill(bar);
            result = bar;

            GeneralPath barR3d = null;
            GeneralPath barT3d = null;
            double effect3d = 0.00;
            VerticalAxis vAxis = plot.getVerticalAxis();
            if (barHeight != 0 && vAxis instanceof VerticalNumberAxis3D) {
                effect3d = ((VerticalNumberAxis3D) vAxis).getEffect3d();
                barR3d = new GeneralPath();
                barR3d.moveTo( (float) (rectX+rectWidth), (float) barY);
                barR3d.lineTo((float) (rectX+rectWidth), (float) (barY+barHeight));
                barR3d.lineTo((float) (rectX+rectWidth+effect3d), (float) (barY+barHeight-effect3d));
                barR3d.lineTo((float) (rectX+rectWidth+effect3d), (float) (barY-effect3d));
                if (seriesPaint instanceof Color) {
                    g2.setPaint( ((Color) seriesPaint).darker());
                }
                g2.fill(barR3d);

                if (xx>0) {
                    barT3d = new GeneralPath();
                    barT3d.moveTo( (float) rectX, (float) barY);
                    barT3d.lineTo((float) (rectX+effect3d), (float) (barY-effect3d));
                    barT3d.lineTo((float) (rectX+rectWidth+effect3d), (float) (barY-effect3d));
                    barT3d.lineTo((float) (rectX+rectWidth), (float) (barY) );
                    if (seriesPaint instanceof Color) {
                        g2.setPaint( ((Color) seriesPaint).brighter());
                    }
                    g2.fill(barT3d);
                }
            }

            if (rectWidth>3) {
                g2.setStroke(plot.getSeriesOutlineStroke(series));
                g2.setPaint(seriesOutlinePaint);
                g2.draw(bar);
                if (barR3d != null) {
                  g2.draw(barR3d);
                }
                if (barT3d != null) {
                  g2.draw(barT3d);
                }
            }
        }

        return result;
    }

}