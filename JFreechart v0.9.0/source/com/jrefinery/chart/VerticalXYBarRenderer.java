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
 * --------------------------
 * VerticalXYBarRenderer.java
 * --------------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: VerticalXYBarRenderer.java,v 1.1 2007/10/10 19:01:18 vauchers Exp $
 *
 * Changes
 * -------
 * 13-Dec-2001 : Version 1, makes VerticalXYBarPlot class redundant (DG);
 * 23-Jan-2002 : Added DrawInfo parameter to drawItem(...) method (DG);
 * 09-Apr-2002 : Removed the translated zero from the drawItem method.  Override the initialise()
 *               method to calculate it (DG);
 * 24-May-2002 : Incorporated tooltips into chart entities (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import com.jrefinery.data.XYDataset;
import com.jrefinery.data.IntervalXYDataset;
import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.entity.XYItemEntity;
import com.jrefinery.chart.tooltips.XYToolTipGenerator;
import com.jrefinery.chart.tooltips.StandardXYToolTipGenerator;

/**
 * A renderer that draws bars on an XY plot (requires an IntervalXYDataset).
 */
public class VerticalXYBarRenderer extends AbstractXYItemRenderer implements XYItemRenderer {

    /** Percentage margin (to reduce the width of bars). */
    protected double margin;

    /** A data value of zero translated to a Java2D value. */
    protected double translatedRangeZero;

    /**
     * The default constructor.
     */
    public VerticalXYBarRenderer() {
        this(0.0, new StandardXYToolTipGenerator());
    }

    /**
     * Constructs a new renderer.
     *
     * @param margin The percentage amount to trim from the width of each bar.
     */
    public VerticalXYBarRenderer(double margin) {
        this(margin, new StandardXYToolTipGenerator());
    }

    /**
     * Constructs a new renderer.
     *
     * @param margin The percentage amount to trim from the width of each bar.
     * @param toolTipGenerator The tool tip generator (null permitted).
     */
    public VerticalXYBarRenderer(double margin, XYToolTipGenerator toolTipGenerator) {

        super(toolTipGenerator);
        this.margin = margin;

    }

    /**
     * Sets the percentage amount by which the bars are trimmed.
     * <P>
     * Fires a property change event.
     *
     * @param margin The new margin.
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
     * @param g2 The graphics device.
     * @param dataArea The area inside the axes.
     * @param plot The plot.
     * @param data The data.
     * @param info An optional info collection object to return data back to the caller.
     */
    public void initialise(Graphics2D g2,
                           Rectangle2D dataArea,
                           XYPlot plot,
                           XYDataset data,
                           ChartRenderingInfo info) {

        super.initialise(g2, dataArea, plot, data, info);
        ValueAxis rangeAxis = plot.getRangeAxis();
        this.translatedRangeZero = rangeAxis.translateValueToJava2D(0.0, dataArea);

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

        IntervalXYDataset intervalData = (IntervalXYDataset)data;

        Paint seriesPaint = plot.getSeriesPaint(series);
        Paint seriesOutlinePaint = plot.getSeriesOutlinePaint(series);

        Number valueNumber = intervalData.getYValue(series, item);
        double translatedValue = rangeAxis.translateValueToJava2D(valueNumber.doubleValue(), dataArea);

        Number startXNumber = intervalData.getStartXValue(series, item);
        double translatedStartX = domainAxis.translateValueToJava2D(startXNumber.doubleValue(), dataArea);

        Number endXNumber = intervalData.getEndXValue(series, item);
        double translatedEndX = domainAxis.translateValueToJava2D(endXNumber.doubleValue(), dataArea);

        double translatedWidth = Math.max(1, translatedEndX-translatedStartX);
        double translatedHeight = Math.abs(translatedValue-translatedRangeZero);

        if (margin>0.0) {
            double cut = translatedWidth * margin;
            translatedWidth = translatedWidth - cut;
            translatedStartX = translatedStartX + cut/2;
        }

        Rectangle2D bar = new Rectangle2D.Double(translatedStartX,
                                                 Math.min(this.translatedRangeZero, translatedValue),
                                                 translatedWidth, translatedHeight);

        g2.setPaint(seriesPaint);
        g2.fill(bar);
        if ((translatedEndX-translatedStartX)>3) {
            g2.setStroke(plot.getSeriesOutlineStroke(series));
            g2.setPaint(seriesOutlinePaint);
            g2.draw(bar);
        }

        // add an entity for the item...
        if (info!=null) {
            EntityCollection entities = info.getEntityCollection();
            if (entities!=null) {
                String tip = "";
                if (this.toolTipGenerator!=null) {
                    tip = this.toolTipGenerator.generateToolTip(data, series, item);
                }
                XYItemEntity entity = new XYItemEntity(bar, tip, series, item);
                entities.addEntity(entity);
            }
        }

    }

}