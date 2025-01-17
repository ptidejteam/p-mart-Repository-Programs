/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ---------------------
 * WindItemRenderer.java
 * ---------------------
 * (C) Copyright 2001-2004, by Achilleus Mantzios and Contributors.
 *
 * Original Author:  Achilleus Mantzios;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: WindItemRenderer.java,v 1.1 2007/10/10 19:46:15 vauchers Exp $
 *
 * Changes
 * -------
 * 06-Feb-2002 : Version 1, based on code contributed by Achilleus Mantzios (DG);
 * 28-Mar-2002 : Added a property change listener mechanism so that renderers no longer need to be
 *               immutable.  Changed StrictMath-->Math to retain JDK1.2 compatibility (DG);
 * 09-Apr-2002 : Changed return type of the drawItem method to void, reflecting the change in the
 *               XYItemRenderer method (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 21-Jan-2003 : Added new constructor (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 01-May-2003 : Modified drawItem(...) method signature (DG);
 * 20-Aug-2003 : Implemented Cloneable and PublicCloneable (DG);
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 25-Feb-2004 : Replaced CrosshairInfo with CrosshairState (DG);
 *
 */

package org.jfree.chart.renderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.WindDataset;
import org.jfree.data.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

/**
 * A specialised renderer for displaying wind intensity/direction data.
 *
 * @author Achilleus Mantzios
 */
public class WindItemRenderer extends AbstractXYItemRenderer 
                              implements XYItemRenderer, 
                                         Cloneable,
                                         PublicCloneable,
                                         Serializable {

    /**
     * Creates a new renderer.
     */
    public WindItemRenderer() {
        super();
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param plotArea  the area within which the plot is being drawn.
     * @param info  optional information collection.
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param domainAxis  the horizontal axis.
     * @param rangeAxis  the vertical axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  crosshair information for the plot (<code>null</code> permitted).
     * @param pass  the pass index.
     */
    public void drawItem(Graphics2D g2,
                         XYItemRendererState state,
                         Rectangle2D plotArea,
                         PlotRenderingInfo info,
                         XYPlot plot,
                         ValueAxis domainAxis,
                         ValueAxis rangeAxis,
                         XYDataset dataset,
                         int series,
                         int item,
                         CrosshairState crosshairState,
                         int pass) {

        WindDataset windData = (WindDataset) dataset;

        Paint seriesPaint = getItemPaint(series, item);
        Stroke seriesStroke = getItemStroke(series, item);
        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);

        // get the data point...

        Number x = windData.getXValue(series, item);
        Number windDir = windData.getWindDirection(series, item);
        Number wforce = windData.getWindForce(series, item);
        double windForce = wforce.doubleValue();

        double wdirt = Math.toRadians(windDir.doubleValue() * (-30.0) - 90.0);

        double ax1, ax2, ay1, ay2, rax2, ray2;

        RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
        ax1 = domainAxis.valueToJava2D(x.doubleValue(), plotArea, domainAxisLocation);
        ay1 = rangeAxis.valueToJava2D(0.0, plotArea, rangeAxisLocation);

        rax2 = x.doubleValue() + (windForce * Math.cos(wdirt) * 8000000.0);
        ray2 = windForce * Math.sin(wdirt);

        ax2 = domainAxis.valueToJava2D(rax2, plotArea, domainAxisLocation);
        ay2 = rangeAxis.valueToJava2D(ray2, plotArea, rangeAxisLocation);

        int diri = windDir.intValue();
        int forcei = wforce.intValue();
        String dirforce = diri + "-" + forcei;
        Line2D line = new Line2D.Double(ax1, ay1, ax2, ay2);

        g2.draw(line);
        g2.setPaint(Color.blue);
        g2.setFont(new Font("foo", 1, 9));

        g2.drawString(dirforce, (float) ax1, (float) ay1);

        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);

        double alx2, aly2, arx2, ary2;
        double ralx2, raly2, rarx2, rary2;

        double aldir = Math.toRadians(windDir.doubleValue() * (-30.0) - 90.0 - 5.0);
        ralx2 = wforce.doubleValue() * Math.cos(aldir) * 8000000 * 0.8 + x.doubleValue();
        raly2 = wforce.doubleValue() * Math.sin(aldir) * 0.8;

        alx2 = domainAxis.valueToJava2D(ralx2, plotArea, domainAxisLocation);
        aly2 = rangeAxis.valueToJava2D(raly2, plotArea, rangeAxisLocation);

        line = new Line2D.Double(alx2, aly2, ax2, ay2);
        g2.draw(line);

        double ardir = Math.toRadians(windDir.doubleValue() * (-30.0) - 90.0 + 5.0);
        rarx2 = wforce.doubleValue() * Math.cos(ardir) * 8000000 * 0.8 + x.doubleValue();
        rary2 = wforce.doubleValue() * Math.sin(ardir) * 0.8;

        arx2 = domainAxis.valueToJava2D(rarx2, plotArea, domainAxisLocation);
        ary2 = rangeAxis.valueToJava2D(rary2, plotArea, rangeAxisLocation);

        line = new Line2D.Double(arx2, ary2, ax2, ay2);
        g2.draw(line);

    }

    /**
     * Returns a clone of the renderer.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException  if the renderer cannot be cloned.
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
