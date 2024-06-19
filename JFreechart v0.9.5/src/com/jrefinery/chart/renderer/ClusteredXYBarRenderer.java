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
 * ---------------------------
 * ClusteredXYBarRenderer.java
 * ---------------------------
 * (C) Copyright 2003, by Paolo Cova and Contributors.
 *
 * Original Author:  Paolo Cova;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: ClusteredXYBarRenderer.java,v 1.1 2007/10/10 19:54:20 vauchers Exp $
 *
 * Changes
 * -------
 * 24-Jan-2003 : Version 1, contributed by Paolo Cova (DG);
 *
 */

package com.jrefinery.chart.renderer;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import com.jrefinery.chart.ChartRenderingInfo;
import com.jrefinery.chart.CrosshairInfo;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.entity.XYItemEntity;
import com.jrefinery.chart.plot.XYPlot;
import com.jrefinery.data.IntervalXYDataset;
import com.jrefinery.data.XYDataset;

/**
 * An extension of VerticalXYBarRenderer that displays bars for different
 * series values at the same x next to each other. The assumption here is
 * that for each x (time or else) there is a y value for each series. If
 * this is not the case, there will be spaces between bars for a given x.
 * 
 * @author Paolo Cova
 */
public class ClusteredXYBarRenderer extends VerticalXYBarRenderer {
 
    /** Percentage margin (to reduce the width of bars). */
    private double margin;
 
    /** A data value of zero translated to a Java2D value. */
    private double translatedRangeZero;
 
    /** Determines whether bar center should be interval start. */
    private boolean centerBarAtStartValue;
 
    /**
     * Default constructor. Bar margin is set to 0.0.
    */
    public ClusteredXYBarRenderer() {
        this(0.0, false);
    }
 
    /**
    * Constructs a new XY clustered bar renderer.
    *
    * @param margin the percentage amount to trim from the width of each bar.
    * @param centerBarAtStartValue If true, bars will be centered on the start of the time period.
    */
    public ClusteredXYBarRenderer(double margin, boolean centerBarAtStartValue) {
        super(margin);
        this.margin = margin;
        this.centerBarAtStartValue = centerBarAtStartValue;
    }
 
    /**
    * Initialises the renderer. Here we calculate the Java2D y-coordinate for zero, since all
    * the bars have their bases fixed at zero. Copied from superclass to
    * initialize local variables.
    *
    * @param g2 the graphics device.
    * @param dataArea the area inside the axes.
    * @param plot the plot.
    * @param data the data.
    * @param info an optional info collection object to return data back to the caller.
    */
    public void initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset data,
                           ChartRenderingInfo info) {
 
        super.initialise(g2, dataArea, plot, data, info);
        ValueAxis rangeAxis = plot.getRangeAxis();
        this.translatedRangeZero = rangeAxis.translateValueToJava2D(0.0, dataArea);
 
    }
 
    /**
     * Sets the margin.
     * 
     * @param margin  the margin.
     */
    public void setMargin(double margin) {
        this.margin = margin;
        super.setMargin(margin);
    }
 
    /**
     * Draws the visual representation of a single data item. This method
     * is mostly copied from the superclass, the change is that in the
     * calculated space for a singe bar we draw bars for each series next to
     * each other. The width of each bar is the available width divided by
     * the number of series. Bars for each series are drawn in order left to
     * right.
     *
     * @param g2 the graphics device.
     * @param dataArea the area within which the plot is being drawn.
     * @param info collects information about the drawing.
     * @param plot the plot (can be used to obtain standard color information etc).
     * @param domainAxis the domain axis.
     * @param rangeAxis the range axis.
     * @param data the dataset.
     * @param datasetIndex  the dataset index.
     * @param series the series index.
     * @param item the item index.
     * @param crosshairInfo collects information about crosshairs.
     */
    public void drawItem(Graphics2D g2,
                         Rectangle2D dataArea,
                         ChartRenderingInfo info,
                         XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
                         XYDataset data, int datasetIndex, int series, int item,
                         CrosshairInfo crosshairInfo) {
 
        IntervalXYDataset intervalData = (IntervalXYDataset) data;
 
        Paint seriesPaint = getItemPaint(datasetIndex, series, item);
        Paint seriesOutlinePaint = getItemOutlinePaint(datasetIndex, series, item);
 
        Number valueNumber = intervalData.getYValue(series, item);
        double translatedValue = rangeAxis.translateValueToJava2D(valueNumber.doubleValue(),
                                                                  dataArea);
 
        Number startXNumber = intervalData.getStartXValue(series, item);
        double translatedStartX = domainAxis.translateValueToJava2D(startXNumber.doubleValue(),
                                                                    dataArea);
 
        Number endXNumber = intervalData.getEndXValue(series, item);
        double translatedEndX = domainAxis.translateValueToJava2D(endXNumber.doubleValue(),
                                                                  dataArea);
 
        double translatedWidth = Math.max(1, translatedEndX - translatedStartX);
        double translatedHeight = Math.abs(translatedValue - translatedRangeZero);
 
        if (centerBarAtStartValue) {
            translatedStartX -= translatedWidth / 2;
        }
 
        if (margin > 0.0) {
            double cut = translatedWidth * margin;
            translatedWidth = translatedWidth - cut;
            translatedStartX = translatedStartX + cut / 2;
        }
 
        int numSeries = data.getSeriesCount();
        double seriesBarWidth = translatedWidth / numSeries;
 
        Rectangle2D bar = new Rectangle2D.Double(translatedStartX + seriesBarWidth * series,
                                            Math.min(this.translatedRangeZero, translatedValue),
                                            seriesBarWidth, translatedHeight);
 
        g2.setPaint(seriesPaint);
        g2.fill(bar);
        if ((translatedEndX - translatedStartX) > 3) {
            g2.setStroke(getItemStroke(datasetIndex, series, item));
            g2.setPaint(seriesOutlinePaint);
            g2.draw(bar);
        }
 
        // add an entity for the item...
        if (info != null) {
            EntityCollection entities = info.getEntityCollection();
            if (entities != null) {
                String tip = null;
                if (getToolTipGenerator() != null) {
                    tip = getToolTipGenerator().generateToolTip(data, series, item);
                }
                String url = null;
                if (getURLGenerator() != null) {
                    url = getURLGenerator().generateURL(data, series, item);
                }
                XYItemEntity entity = new XYItemEntity(bar, tip, url, series, item);
                entities.addEntity(entity);
            }
        }
 
     }
}

 
