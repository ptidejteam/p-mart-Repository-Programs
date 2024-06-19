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
 * --------------------------
 * VerticalXYBarRenderer.java
 * --------------------------
 * (C) Copyright 2001-2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Richard Atkinson;
 *
 * $Id: VerticalXYBarRenderer.java,v 1.1 2007/10/10 20:00:08 vauchers Exp $
 *
 * Changes
 * -------
 * 13-Dec-2001 : Version 1, makes VerticalXYBarPlot class redundant (DG);
 * 23-Jan-2002 : Added DrawInfo parameter to drawItem(...) method (DG);
 * 09-Apr-2002 : Removed the translated zero from the drawItem method.  Override the initialise()
 *               method to calculate it (DG);
 * 24-May-2002 : Incorporated tooltips into chart entities (DG);
 * 25-Jun-2002 : Removed redundant import (DG);
 * 05-Aug-2002 : Small modification to drawItem method to support URLs for HTML image maps (RA);
 * 25-Mar-2003 : Implemented Serializable (DG);
 *
 */

package com.jrefinery.chart.renderer;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import com.jrefinery.chart.ChartRenderingInfo;
import com.jrefinery.chart.CrosshairInfo;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.entity.XYItemEntity;
import com.jrefinery.chart.plot.XYPlot;
import com.jrefinery.chart.tooltips.XYToolTipGenerator;
import com.jrefinery.chart.urls.XYURLGenerator;
import com.jrefinery.data.IntervalXYDataset;
import com.jrefinery.data.XYDataset;

/**
 * A renderer that draws bars on an {@link XYPlot} (requires an {@link IntervalXYDataset}).
 *
 * @author David Gilbert
 */
public class VerticalXYBarRenderer extends AbstractXYItemRenderer 
                                   implements XYItemRenderer, Serializable {

    /** Percentage margin (to reduce the width of bars). */
    private double margin;

    /** A data value of zero translated to a Java2D value. */
    private double translatedRangeZero;

    /**
     * The default constructor.
     */
    public VerticalXYBarRenderer() {
        this(0.0);
    }

    /**
     * Constructs a new renderer.
     *
     * @param margin  the percentage amount to trim from the width of each bar.
     */
    public VerticalXYBarRenderer(double margin) {
        this(margin, null, null);
    }

    /**
     * Constructs a new renderer.
     *
     * @param margin  the percentage amount to trim from the width of each bar.
     * @param toolTipGenerator  the tool tip generator (<code>null</code> permitted).
     * @param urlGenerator  the URL generator (<code>null</code> permitted).
     */
    public VerticalXYBarRenderer(double margin, 
                                 XYToolTipGenerator toolTipGenerator,
                                 XYURLGenerator urlGenerator) {

        super(toolTipGenerator, urlGenerator);
        this.margin = margin;

    }

    /**
     * Sets the percentage amount by which the bars are trimmed.
     * <P>
     * Fires a property change event.
     *
     * @param margin  the new margin.
     */
    public void setMargin(double margin) {

        Double old = new Double(this.margin);
        this.margin = margin;
        this.firePropertyChanged("VerticalXYBarRenderer.margin", old, new Double(margin));

    }

    /**
     * Initialises the renderer.  Here we calculate the Java2D y-coordinate for zero, since all
     * the bars have their bases fixed at zero.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area inside the axes.
     * @param plot  the plot.
     * @param data  the data.
     * @param info  an optional info collection object to return data back to the caller.
     */
    public void initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset data,
                           ChartRenderingInfo info) {

        super.initialise(g2, dataArea, plot, data, info);
        ValueAxis rangeAxis = plot.getRangeAxis();
        this.translatedRangeZero = rangeAxis.translateValueToJava2D(0.0, dataArea);

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
     * @param crosshairInfo  collects information about crosshairs.
     */
    public void drawItem(Graphics2D g2,
                         Rectangle2D dataArea,
                         ChartRenderingInfo info,
                         XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
                         XYDataset dataset, int datasetIndex, int series, int item,
                         CrosshairInfo crosshairInfo) {

        IntervalXYDataset intervalData = (IntervalXYDataset) dataset;

        Paint seriesPaint = getItemPaint(datasetIndex, series, item);
        Paint seriesOutlinePaint = getSeriesOutlinePaint(datasetIndex, series);

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

        if (margin > 0.0) {
            double cut = translatedWidth * margin;
            translatedWidth = translatedWidth - cut;
            translatedStartX = translatedStartX + cut / 2;
        }

        Rectangle2D bar
            = new Rectangle2D.Double(translatedStartX,
                                     Math.min(this.translatedRangeZero, translatedValue),
                                     translatedWidth, translatedHeight);

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
                String tip = "";
                if (getToolTipGenerator() != null) {
                    tip = getToolTipGenerator().generateToolTip(dataset, series, item);
                }
                String url = null;
                if (getURLGenerator() != null) {
                    url = getURLGenerator().generateURL(dataset, series, item);
                }
                XYItemEntity entity = new XYItemEntity(bar, tip, url, series, item);
                entities.addEntity(entity);
            }
        }

    }

}
