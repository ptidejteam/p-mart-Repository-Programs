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
 * $Id: SignalRenderer.java,v 1.1 2007/10/10 19:41:58 vauchers Exp $
 *
 * Changes
 * -------
 * 08-Jan-2002 : Version 1.  Based on code in the SignalsPlot class, written by Sylvain
 *               Vieujot (DG);
 * 23-Jan-2002 : Added DrawInfo parameter to drawItem(...) method (DG);
 * 14-Feb-2002 : Added small fix from Sylvain (DG);
 * 28-Mar-2002 : Added a property change listener mechanism so that renderers no longer need to be
 *               immutable (DG);
 * 09-Apr-2002 : Removed translatedRangeZero from the drawItem(...) method, and changed the return
 *               type of the drawItem method to void, reflecting a change in the XYItemRenderer
 *               interface.  Added tooltip code to drawItem(...) method (DG);
 * 25-Jun-2002 : Removed redundant code (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import com.jrefinery.data.XYDataset;
import com.jrefinery.data.SignalsDataset;
import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.entity.XYItemEntity;

/**
 * A renderer that draws signals on an XY plot (requires a SignalsDataset).
 */
public class SignalRenderer extends AbstractXYItemRenderer implements XYItemRenderer {

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
     *
     * @param g2 The graphics device.
     * @param dataArea The area within which the plot is being drawn.
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

        // setup for collecting optional entity info...
        EntityCollection entities = null;
        if (info!=null) {
            entities = info.getEntityCollection();
        }

        SignalsDataset signalData = (SignalsDataset)data;

        Number x = signalData.getXValue(series, item);
        Number y = signalData.getYValue(series, item);
        int type = signalData.getType(series, item);
        //double level = signalData.getLevel(series, item);

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

        // add an entity for the item...
        if (entities!=null) {
            String tip = "";
            if (this.toolTipGenerator!=null) {
                tip = this.toolTipGenerator.generateToolTip(data, series, item);
            }
            XYItemEntity entity = new XYItemEntity(path, tip, series, item);
            entities.addEntity(entity);
        }

    }

}