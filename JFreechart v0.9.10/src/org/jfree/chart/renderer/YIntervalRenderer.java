/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
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
 * (C) Copyright 2002, 2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: YIntervalRenderer.java,v 1.1 2007/10/10 19:05:08 vauchers Exp $
 *
 * Changes
 * -------
 * 05-Nov-2002 : Version 1 (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 01-May-2003 : Modified drawItem(...) method signature (DG);
 *
 */

package org.jfree.chart.renderer;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.CrosshairInfo;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.IntervalXYDataset;
import org.jfree.data.XYDataset;
import org.jfree.ui.RectangleEdge;

/**
 * A renderer that draws a vertical line connecting the start and end Y values for an
 * {@link XYPlot}.
 *
 * @author David Gilbert
 */
public class YIntervalRenderer extends AbstractXYItemRenderer
                               implements XYItemRenderer, Serializable {

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
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairInfo  information about crosshairs on a plot.
     * @param pass  the pass index (ignored here).
     */
    public void drawItem(Graphics2D g2, Rectangle2D dataArea,
                         ChartRenderingInfo info,
                         XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
                         XYDataset dataset, int series, int item,
                         CrosshairInfo crosshairInfo, int pass) {

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

        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        
        double xx = domainAxis.translateValueToJava2D(x.doubleValue(), dataArea, xAxisLocation);
        double yyLow 
            = rangeAxis.translateValueToJava2D(yLow.doubleValue(), dataArea, yAxisLocation);
        double yyHigh 
            = rangeAxis.translateValueToJava2D(yHigh.doubleValue(), dataArea, yAxisLocation);

        Paint p = getItemPaint(series, item);
        Stroke s = getItemStroke(series, item);
        
        Line2D line = null;
        Shape shape = getItemShape(series, item);
        Shape top = null;
        Shape bottom = null;
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(yyLow, xx, yyHigh, xx);
            top = createTransformedShape(shape, yyHigh, xx);
            bottom = createTransformedShape(shape, yyLow, xx);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(xx, yyLow, xx, yyHigh);
            top = createTransformedShape(shape, xx, yyHigh);
            bottom = createTransformedShape(shape, xx, yyLow);
        }
        g2.setPaint(p);
        g2.setStroke(s);
        g2.draw(line);

        g2.fill(top);
        g2.fill(bottom);

        // add an entity for the item...
        if (entities != null) {
            if (entityArea == null) {
                entityArea = line.getBounds();
            }
            String tip = null;
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
