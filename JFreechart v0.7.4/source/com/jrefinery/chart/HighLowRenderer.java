/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart
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
 * --------------------
 * HighLowRenderer.java
 * --------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: HighLowRenderer.java,v 1.1 2007/10/10 18:57:57 vauchers Exp $
 *
 * Changes
 * -------
 * 13-Dec-2001 : Version 1 (DG);
 * 23-Jan-2002 : Added DrawInfo parameter to drawItem(...) method (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;
import com.jrefinery.data.*;

/**
 * A renderer that draws bars on an XY plot (requires an IntervalXYDataset).
 */
public class HighLowRenderer implements XYItemRenderer {

    public HighLowRenderer() {
    }

    /**
     * Draws the visual representation of a single data item.
     * @param g2 The graphics device.
     * @param dataArea The area within which the plot is being drawn.
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

        HighLowDataset highLowData = (HighLowDataset)data;

	Number x = highLowData.getXValue(series, item);
        Number yHigh  = highLowData.getHighValue(series, item);
        Number yLow   = highLowData.getLowValue(series, item);
        Number yOpen  = highLowData.getOpenValue(series, item);
        Number yClose = highLowData.getCloseValue(series, item);

        double xx = horizontalAxis.translateValueToJava2D(x.doubleValue(), dataArea);
        double yyHigh = verticalAxis.translateValueToJava2D(yHigh.doubleValue(), dataArea);
        double yyLow = verticalAxis.translateValueToJava2D(yLow.doubleValue(), dataArea);
        double yyOpen = verticalAxis.translateValueToJava2D(yOpen.doubleValue(), dataArea);
        double yyClose = verticalAxis.translateValueToJava2D(yClose.doubleValue(), dataArea);

        Paint p = plot.getSeriesPaint(series);
        Stroke s = plot.getSeriesStroke(series);

        HighLow hl = new HighLow(xx, yyHigh, yyLow, yyOpen, yyClose, s, p);
        Line2D l1 = hl.getOpenTickLine();
        Line2D l2 = hl.getLine();
        Line2D l3 = hl.getCloseTickLine();

        g2.setPaint(p);
        g2.setStroke(s);
        g2.draw(l1);
        g2.draw(l2);
        g2.draw(l3);

        result = l2.getBounds();
        return result;

    }

}