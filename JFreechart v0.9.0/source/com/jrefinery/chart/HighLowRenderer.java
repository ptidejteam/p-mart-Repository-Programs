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
 * --------------------
 * HighLowRenderer.java
 * --------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: HighLowRenderer.java,v 1.1 2007/10/10 19:01:18 vauchers Exp $
 *
 * Changes
 * -------
 * 13-Dec-2001 : Version 1 (DG);
 * 23-Jan-2002 : Added DrawInfo parameter to drawItem(...) method (DG);
 * 28-Mar-2002 : Added a property change listener mechanism so that renderers no longer need to be
 *               immutable (DG);
 * 09-Apr-2002 : Removed translatedRangeZero from the drawItem(...) method, and changed the return
 *               type of the drawItem method to void, reflecting a change in the XYItemRenderer
 *               interface.  Added tooltip code to drawItem(...) method (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import com.jrefinery.data.XYDataset;
import com.jrefinery.data.HighLowDataset;
import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.entity.XYItemEntity;
import com.jrefinery.chart.tooltips.XYToolTipGenerator;

/**
 * A renderer that draws high/low/open/close markers on an XY plot (requires an IntervalXYDataset).
 */
public class HighLowRenderer extends AbstractXYItemRenderer implements XYItemRenderer {

    /**
     * The default constructor.
     */
    public HighLowRenderer() {
        this(null);
    }

    public HighLowRenderer(XYToolTipGenerator tooltipGenerator) {
        super(tooltipGenerator);
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2 The graphics device.
     * @param dataArea The area within which the plot is being drawn.
     * @param info Collects information about the drawing.
     * @param plot The plot (can be used to obtain standard color information etc).
     * @param domainAxis The domain axis.
     * @param rangeAxis The range axis.
     * @param data The dataset.
     * @param series The series index.
     * @param item The item index.
     */
    public void drawItem(Graphics2D g2, Rectangle2D dataArea, ChartRenderingInfo info,
                         XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
                         XYDataset data, int series, int item,
                         CrosshairInfo crosshairInfo) {

        // setup for collecting optional entity info...
        Shape entityArea = null;
        EntityCollection entities = null;
        if (info!=null) {
            entities = info.getEntityCollection();
        }

        HighLowDataset highLowData = (HighLowDataset)data;

        Number x = highLowData.getXValue(series, item);
        Number yHigh  = highLowData.getHighValue(series, item);
        Number yLow   = highLowData.getLowValue(series, item);
        Number yOpen  = highLowData.getOpenValue(series, item);
        Number yClose = highLowData.getCloseValue(series, item);

        double xx = domainAxis.translateValueToJava2D(x.doubleValue(), dataArea);
        double yyHigh = rangeAxis.translateValueToJava2D(yHigh.doubleValue(), dataArea);
        double yyLow = rangeAxis.translateValueToJava2D(yLow.doubleValue(), dataArea);
        double yyOpen = rangeAxis.translateValueToJava2D(yOpen.doubleValue(), dataArea);
        double yyClose = rangeAxis.translateValueToJava2D(yClose.doubleValue(), dataArea);

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

        // add an entity for the item...
        if (entities!=null) {
            if (entityArea==null) {
                entityArea = hl.getBounds();
            }
            String tip = "";
            if (this.toolTipGenerator!=null) {
                tip = this.toolTipGenerator.generateToolTip(data, series, item);
            }
            XYItemEntity entity = new XYItemEntity(entityArea, tip, series, item);
            entities.addEntity(entity);
        }

    }

}