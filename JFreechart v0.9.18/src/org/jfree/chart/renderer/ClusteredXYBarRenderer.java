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
 * ---------------------------
 * ClusteredXYBarRenderer.java
 * ---------------------------
 * (C) Copyright 2003, 2004, by Paolo Cova and Contributors.
 *
 * Original Author:  Paolo Cova;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *                   Christian W. Zuckschwerdt;
 *                   Matthias Rose;
 *
 * $Id: ClusteredXYBarRenderer.java,v 1.1 2007/10/10 19:39:14 vauchers Exp $
 *
 * Changes
 * -------
 * 24-Jan-2003 : Version 1, contributed by Paolo Cova (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 01-May-2003 : Modified drawItem(...) method signature (DG);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 20-Aug-2003 : Implemented Cloneable and PublicCloneable (DG);
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 07-Oct-2003 : Added renderer state (DG);
 * 03-Nov-2003 : In draw method added state parameter and y==null value handling (MR);
 * 25-Feb-2004 : Replaced CrosshairInfo with CrosshairState (DG);
 * 
 */

package org.jfree.chart.renderer;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.IntervalXYDataset;
import org.jfree.data.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

/**
 * An extension of {@link XYBarRenderer} that displays bars for different
 * series values at the same x next to each other. The assumption here is
 * that for each x (time or else) there is a y value for each series. If
 * this is not the case, there will be spaces between bars for a given x.
 * <P>
 * This renderer does not include code to calculate the crosshair point for the plot.
 *
 * @author Paolo Cova
 */
public class ClusteredXYBarRenderer extends XYBarRenderer implements Cloneable,
                                                                     PublicCloneable,
                                                                     Serializable {

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
    *
    * @return The number of passes required by the renderer.
    */
    public XYItemRendererState initialise(Graphics2D g2, 
                                          Rectangle2D dataArea, 
                                          XYPlot plot, 
                                          XYDataset data,
                                          PlotRenderingInfo info) {

        XYItemRendererState state = super.initialise(g2, dataArea, plot, data, info);
        ValueAxis rangeAxis = plot.getRangeAxis();
        this.translatedRangeZero = rangeAxis.valueToJava2D(0.0, dataArea, plot.getRangeAxisEdge());
        return state;

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
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the plot is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param series  the series index.
     * @param item  the item index.
     * @param crosshairState  crosshair information for the plot (<code>null</code> permitted).
     * @param pass  the pass index.
     */
    public void drawItem(Graphics2D g2,
                         XYItemRendererState state,
                         Rectangle2D dataArea,
                         PlotRenderingInfo info,
                         XYPlot plot, 
                         ValueAxis domainAxis, 
                         ValueAxis rangeAxis,
                         XYDataset dataset, int series, int item,
                         CrosshairState crosshairState,
                         int pass) {

        IntervalXYDataset intervalData = (IntervalXYDataset) dataset;

        Paint seriesPaint = getItemPaint(series, item);
        Paint seriesOutlinePaint = getItemOutlinePaint(series, item);

        Number y = intervalData.getYValue(series, item);
        if (y == null) {
            return;
        }
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        double translatedY = rangeAxis.valueToJava2D(y.doubleValue(), dataArea, yAxisLocation);

        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        double x1 = intervalData.getStartXValue(series, item).doubleValue();
        double translatedX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);

        double x2 = intervalData.getEndXValue(series, item).doubleValue();
        double translatedX2 = domainAxis.valueToJava2D(x2, dataArea, xAxisLocation);

        double translatedWidth = Math.max(1, Math.abs(translatedX2 - translatedX1));
        double translatedHeight = Math.abs(translatedY - this.translatedRangeZero);

        if (this.centerBarAtStartValue) {
            translatedX1 -= translatedWidth / 2;
        }

        if (this.margin > 0.0) {
            double cut = translatedWidth * this.margin;
            translatedWidth = translatedWidth - cut;
            translatedX1 = translatedX1 + cut / 2;
        }

        int numSeries = dataset.getSeriesCount();
        double seriesBarWidth = translatedWidth / numSeries;

        Rectangle2D bar = null;
        PlotOrientation orientation = plot.getOrientation();        
        if (orientation == PlotOrientation.HORIZONTAL) {
            bar = new Rectangle2D.Double(Math.min(this.translatedRangeZero, translatedY),
                                         translatedX1 - seriesBarWidth * (numSeries - series),
                                         translatedHeight, seriesBarWidth);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
        
            bar = new Rectangle2D.Double(translatedX1 + seriesBarWidth * series,
                                         Math.min(this.translatedRangeZero, translatedY),
                                         seriesBarWidth, translatedHeight);

        }
        g2.setPaint(seriesPaint);
        g2.fill(bar);
        if (Math.abs(translatedX2 - translatedX1) > 3) {
            g2.setStroke(getItemStroke(series, item));
            g2.setPaint(seriesOutlinePaint);
            g2.draw(bar);
        }

        // add an entity for the item...
        if (info != null) {
            EntityCollection entities = info.getOwner().getEntityCollection();
            if (entities != null) {
                String tip = null;
                XYToolTipGenerator generator = getToolTipGenerator(series, item);
                if (generator != null) {
                    tip = generator.generateToolTip(dataset, series, item);
                }
                String url = null;
                if (getURLGenerator() != null) {
                    url = getURLGenerator().generateURL(dataset, series, item);
                }
                XYItemEntity entity = new XYItemEntity(bar, dataset, series, item, tip, url);
                entities.addEntity(entity);
            }
        }

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

