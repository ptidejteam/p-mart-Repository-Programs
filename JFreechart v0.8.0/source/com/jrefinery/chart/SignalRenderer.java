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
 * SignalRenderer.java
 * -------------------
 * (C) Copyright 2001, 2002, by Sylvain Viuejot and Contributors.
 *
 * Original Author:  Sylvain Vieujot;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: SignalRenderer.java,v 1.1 2007/10/10 18:59:09 vauchers Exp $
 *
 * Changes
 * -------
 * 08-Jan-2002 : Version 1.  Based on code in the SignalsPlot class, written by Sylvain
 *               Vieujot (DG);
 * 23-Jan-2002 : Added DrawInfo parameter to drawItem(...) method (DG);
 * 14-Feb-2002 : Added small fix from Sylvain (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;
import com.jrefinery.data.SignalsDataset;
import com.jrefinery.data.XYDataset;

/**
 * A renderer that draws signals on an XY plot (requires a HighLowDataset).
 */
public class SignalRenderer implements XYItemRenderer {

    public double markOffset = 5;
    public double shapeWidth = 15;
    public double shapeHeight = 25;

    /**
     * Creates a new renderer.
     */
    public SignalRenderer() {

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

        SignalsDataset signalData = (SignalsDataset)data;

	Number x = signalData.getXValue(series, item);
        Number y = signalData.getYValue(series, item);
        int type = signalData.getType(series, item);
        double level = signalData.getLevel(series, item);

        double xx = horizontalAxis.translateValueToJava2D(x.doubleValue(), dataArea);
        double yy = verticalAxis.translateValueToJava2D(y.doubleValue(), dataArea);

        Paint p = plot.getSeriesPaint(series);
        Stroke s = plot.getSeriesStroke(series);
        g2.setPaint(p);
        g2.setStroke(s);

        int direction = 1;
        if ((type==SignalsDataset.ENTER_LONG) || (type==SignalsDataset.EXIT_SHORT)) {
            yy=yy+markOffset;
            direction = -1;
        }
        else {
            yy=yy-markOffset;
        }

        GeneralPath path = new GeneralPath();
        if ((type==SignalsDataset.ENTER_LONG) || (type==SignalsDataset.ENTER_SHORT)) {
            path.moveTo((float)xx, (float)yy);
            path.lineTo((float)(xx+shapeWidth/2), (float)(yy-direction*shapeHeight/3));
            path.lineTo((float)(xx+shapeWidth/6), (float)(yy-direction*shapeHeight/3));
            path.lineTo((float)(xx+shapeWidth/6), (float)(yy-direction*shapeHeight));
            path.lineTo((float)(xx-shapeWidth/6), (float)(yy-direction*shapeHeight));
            path.lineTo((float)(xx-shapeWidth/6), (float)(yy-direction*shapeHeight/3));
            path.lineTo((float)(xx-shapeWidth/2), (float)(yy-direction*shapeHeight/3));
            path.lineTo((float)xx, (float)yy);
        }
        else {
            path.moveTo((float)xx, (float)yy);
            path.lineTo((float)xx, (float)(yy-direction*shapeHeight));
            Ellipse2D.Double ellipse = new Ellipse2D.Double(xx-shapeWidth/2,
                yy+(direction==1?-shapeHeight:shapeHeight-shapeWidth), shapeWidth, shapeWidth);
            path.append(ellipse, false);
        }

        g2.fill(path);
        g2.setPaint(Color.black);
        g2.draw(path);
        return null;

    }

}