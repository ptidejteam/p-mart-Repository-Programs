/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart
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
 * -------------------
 * XYItemRenderer.java
 * -------------------
 * (C) Copyright 2001, 2002, by Achilleus Mantzios and Contributors.
 *
 * Original Author:  Achilleus Mantzios;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: WindItemRenderer.java,v 1.1 2007/10/10 18:57:06 vauchers Exp $
 *
 * Changes
 * -------
 * 06-Feb-2002 : Version 1, based on code contributed by Achilleus Mantzios (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;
import com.jrefinery.data.*;
import java.lang.StrictMath;

/**
 * A specialised renderer for displaying wind intensity/direction data.
 */
public class WindItemRenderer implements XYItemRenderer {

    /**
     * Default constructor.
     */
    public WindItemRenderer() {
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
    public Shape drawItem(Graphics2D g2, Rectangle2D plotArea, DrawInfo info,
                          XYPlot plot, ValueAxis horizontalAxis, ValueAxis verticalAxis,
                          XYDataset data,
                          int series, int item, double translatedRangeZero,
                          CrosshairInfo crosshairs) {

        WindDataset windData = (WindDataset)data;

        Paint seriesPaint = plot.getSeriesPaint(series);
        Stroke seriesStroke = plot.getSeriesStroke(series);
        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);

        // get the data point...

        Number x = windData.getXValue(series, item);
        Number windDir = windData.getWindDirection(series, item);
        Number wforce = windData.getWindForce(series, item);
        double windForce = wforce.doubleValue();

        double wdir_t = StrictMath.toRadians(windDir.doubleValue()*(-30.0)-90.0);

        double ax1, ax2, ay1, ay2, rax1, rax2, ray1, ray2;

        rax1 = x.doubleValue();
        ray1 = 0.0;

        ax1= horizontalAxis.translateValueToJava2D(x.doubleValue(), plotArea);
        ay1= verticalAxis.translateValueToJava2D(0.0, plotArea);

        rax2 = x.doubleValue() + (windForce * StrictMath.cos(wdir_t)*8000000.0);
        ray2 = windForce * StrictMath.sin(wdir_t);

        ax2= horizontalAxis.translateValueToJava2D(rax2, plotArea);
        ay2= verticalAxis.translateValueToJava2D(ray2, plotArea);

        int diri = windDir.intValue();
        int forcei = wforce.intValue();
        String dirforce = diri + "-" +  forcei;
        Line2D line = new Line2D.Double(ax1, ay1, ax2, ay2);

        g2.draw(line);
        g2.setPaint(Color.blue);
        g2.setFont(new Font("foo",1,9));

        g2.drawString(dirforce,(float)ax1,(float)ay1);

        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);

        double alx2, aly2, arx2, ary2;
        double ralx2, raly2, rarx2, rary2;

        double aldir = StrictMath.toRadians(windDir.doubleValue() * (-30.0) -90.0 - 5.0);
        ralx2 = wforce.doubleValue() * StrictMath.cos(aldir)*(double) 8000000 * 0.8+ x.doubleValue();
        raly2= wforce.doubleValue() * StrictMath.sin(aldir) * 0.8 ;

        double fac= (wforce.doubleValue()>1.0)?wforce.doubleValue()-2.0:0;

        alx2= horizontalAxis.translateValueToJava2D(ralx2, plotArea);
        aly2= verticalAxis.translateValueToJava2D(raly2, plotArea);

        line = new Line2D.Double(alx2, aly2, ax2, ay2);
        g2.draw(line);


        double ardir = StrictMath.toRadians(windDir.doubleValue() * (-30.0) -90.0 + 5.0);
        rarx2 = wforce.doubleValue() * StrictMath.cos(ardir)*(double) 8000000 * 0.8 + x.doubleValue();
        rary2= wforce.doubleValue() * StrictMath.sin(ardir) * 0.8;

        arx2= horizontalAxis.translateValueToJava2D(rarx2, plotArea);
        ary2= verticalAxis.translateValueToJava2D(rary2, plotArea);

        line = new Line2D.Double(arx2, ary2, ax2, ay2);
        g2.draw(line);

        return null;

    }

}