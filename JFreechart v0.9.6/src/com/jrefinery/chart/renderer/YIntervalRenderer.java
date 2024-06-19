/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
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
 * ----------------------
 * YIntervalRenderer.java
 * ----------------------
 * (C) Copyright 2002, 2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: YIntervalRenderer.java,v 1.1 2007/10/10 19:57:50 vauchers Exp $
 *
 * Changes
 * -------
 * 05-Nov-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.renderer;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import com.jrefinery.chart.ChartRenderingInfo;
import com.jrefinery.chart.CrosshairInfo;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.entity.XYItemEntity;
import com.jrefinery.chart.plot.XYPlot;
import com.jrefinery.chart.tooltips.XYToolTipGenerator;
import com.jrefinery.data.XYDataset;
import com.jrefinery.data.IntervalXYDataset;

/**
 * A renderer that draws a vertical line connecting the start and end Y values.
 *
 * @author David Gilbert
 */
public class YIntervalRenderer extends AbstractXYItemRenderer implements XYItemRenderer {

    /**
     * The default constructor.
     */
    public YIntervalRenderer() {
        this(null);
    }

    /**
     * Creates a new renderer with the specified tool tip generator.
     *
     * @param toolTipGenerator  the tool tip generator.
     */
    public YIntervalRenderer(XYToolTipGenerator toolTipGenerator) {
        super(toolTipGenerator);
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area within which the plot is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairInfo  information about crosshairs on a plot.
     */
    public void drawItem(Graphics2D g2, Rectangle2D dataArea,
                         ChartRenderingInfo info,
                         XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
                         XYDataset dataset, int datasetIndex, int series, int item,
                         CrosshairInfo crosshairInfo) {

        // setup for collecting optional entity info...
        Shape entityArea = null;
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getEntityCollection();
        }

        IntervalXYDataset intervalData = (IntervalXYDataset) dataset;

        Number x = intervalData.getXValue(series, item);
        Number yLow   = intervalData.getStartYValue(series, item);
        Number yHigh  = intervalData.getEndYValue(series, item);

        double xx = domainAxis.translateValueToJava2D(x.doubleValue(), dataArea);
        double yyLow = rangeAxis.translateValueToJava2D(yLow.doubleValue(), dataArea);
        double yyHigh = rangeAxis.translateValueToJava2D(yHigh.doubleValue(), dataArea);

        Paint p = getItemPaint(datasetIndex, series, item);
        Stroke s = getItemStroke(datasetIndex, series, item);

        Line2D line = new Line2D.Double(xx, yyLow, xx, yyHigh);

        g2.setPaint(p);
        g2.setStroke(s);
        g2.draw(line);

        Shape shape = getItemShape(datasetIndex, series, item);
        Shape top = createTransformedShape(shape, xx, yyHigh);
        g2.fill(top);
        Shape bottom = createTransformedShape(shape, xx, yyLow);
        g2.fill(bottom);

        // add an entity for the item...
        if (entities != null) {
            if (entityArea == null) {
                entityArea = line.getBounds();
            }
            String tip = "";
            if (getToolTipGenerator() != null) {
                tip = getToolTipGenerator().generateToolTip(dataset, series, item);
            }
            String url = null;
            if (getURLGenerator() != null) {
                url = getURLGenerator().generateURL(dataset, series, item);
            }
            XYItemEntity entity = new XYItemEntity(entityArea, tip, url, series, item);
            entities.addEntity(entity);
        }

    }

}
