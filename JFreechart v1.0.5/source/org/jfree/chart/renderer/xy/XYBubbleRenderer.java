/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2007, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ---------------------
 * XYBubbleRenderer.java
 * ---------------------
 * (C) Copyright 2003-2007, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Christian W. Zuckschwerdt;
 *
 * $Id: XYBubbleRenderer.java,v 1.1 2007/10/10 20:38:20 vauchers Exp $
 *
 * Changes
 * -------
 * 28-Jan-2003 : Version 1 (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 01-May-2003 : Modified drawItem() method signature (DG);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 20-Aug-2003 : Implemented Cloneable and PublicCloneable (DG);
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 10-Feb-2004 : Small change to drawItem() method to make cut-and-paste 
 *               overriding easier (DG);
 * 15-Jul-2004 : Switched getZ() and getZValue() methods (DG);
 * 19-Jan-2005 : Now accesses only primitives from dataset (DG);
 * 28-Feb-2005 : Modify renderer to use circles in legend (DG);
 * 17-Mar-2005 : Fixed bug in bubble bounds calculation (DG);
 * 20-Apr-2005 : Use generators for legend tooltips and URLs (DG);
 * ------------- JFREECHART 1.0.x ---------------------------------------------
 * 13-Dec-2005 : Added support for item labels (bug 1373371) (DG);
 * 20-Jan-2006 : Check flag for drawing item labels (DG);
 * 21-Sep-2006 : Respect the outline paint and stroke settings (DG);
 * 24-Jan-2007 : Added new equals() override (DG);
 * 06-Feb-2007 : Fixed bug 1086307, crosshairs with multiple axes (DG);
 *
 */

package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

/**
 * A renderer that draws a circle at each data point with a diameter that is
 * determined by the z-value in the dataset (the renderer requires the dataset 
 * to be an instance of {@link XYZDataset}.
 */
public class XYBubbleRenderer extends AbstractXYItemRenderer 
                              implements XYItemRenderer, 
                                         Cloneable,
                                         PublicCloneable,
                                         Serializable {

    /** For serialization. */
    public static final long serialVersionUID = -5221991598674249125L;
    
    /** 
     * A constant to specify that the bubbles drawn by this renderer should be 
     * scaled on both axes (see {@link #XYBubbleRenderer(int)}). 
     */
    public static final int SCALE_ON_BOTH_AXES = 0;

    /** 
     * A constant to specify that the bubbles drawn by this renderer should be 
     * scaled on the domain axis (see {@link #XYBubbleRenderer(int)}). 
     */
    public static final int SCALE_ON_DOMAIN_AXIS = 1;

    /** 
     * A constant to specify that the bubbles drawn by this renderer should be 
     * scaled on the range axis (see {@link #XYBubbleRenderer(int)}). 
     */
    public static final int SCALE_ON_RANGE_AXIS = 2;

    /** Controls how the width and height of the bubble are scaled. */
    private int scaleType;

    /**
     * Constructs a new renderer.
     */
    public XYBubbleRenderer() {
        this(SCALE_ON_BOTH_AXES); 
    }

    /**
     * Constructs a new renderer with the specified type of scaling. 
     *
     * @param scaleType  the type of scaling (must be one of: 
     *        {@link #SCALE_ON_BOTH_AXES}, {@link #SCALE_ON_DOMAIN_AXIS}, 
     *        {@link #SCALE_ON_RANGE_AXIS}).
     */
    public XYBubbleRenderer(int scaleType) {
        super();
        if (scaleType < 0 || scaleType > 2) {
            throw new IllegalArgumentException("Invalid 'scaleType'.");
        }
        this.scaleType = scaleType;
    }

    /**
     * Returns the scale type that was set when the renderer was constructed.
     *
     * @return The scale type (one of: {@link #SCALE_ON_BOTH_AXES}, 
     *         {@link #SCALE_ON_DOMAIN_AXIS}, {@link #SCALE_ON_RANGE_AXIS}).
     */
    public int getScaleType() {
        return this.scaleType;
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the data is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color 
     *              information etc).
     * @param domainAxis  the domain (horizontal) axis.
     * @param rangeAxis  the range (vertical) axis.
     * @param dataset  the dataset (an {@link XYZDataset} is expected).
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  crosshair information for the plot 
     *                        (<code>null</code> permitted).
     * @param pass  the pass index.
     */
    public void drawItem(Graphics2D g2, XYItemRendererState state,
            Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
            ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, 
            int series, int item, CrosshairState crosshairState, int pass) {

        PlotOrientation orientation = plot.getOrientation();
        
        // get the data point...
        double x = dataset.getXValue(series, item);
        double y = dataset.getYValue(series, item);
        double z = Double.NaN;
        if (dataset instanceof XYZDataset) {
            XYZDataset xyzData = (XYZDataset) dataset;
            z = xyzData.getZValue(series, item);
        }
        if (!Double.isNaN(z)) {
            RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
            RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
            double transX = domainAxis.valueToJava2D(x, dataArea, 
                    domainAxisLocation);
            double transY = rangeAxis.valueToJava2D(y, dataArea, 
                    rangeAxisLocation);

            double transDomain = 0.0;
            double transRange = 0.0;
            double zero;

            switch(getScaleType()) {
                case SCALE_ON_DOMAIN_AXIS:
                    zero = domainAxis.valueToJava2D(0.0, dataArea, 
                            domainAxisLocation);
                    transDomain = domainAxis.valueToJava2D(z, dataArea, 
                            domainAxisLocation) - zero;
                    transRange = transDomain;
                    break;
                case SCALE_ON_RANGE_AXIS:
                    zero = rangeAxis.valueToJava2D(0.0, dataArea, 
                            rangeAxisLocation);
                    transRange = zero - rangeAxis.valueToJava2D(z, dataArea, 
                            rangeAxisLocation);
                    transDomain = transRange;
                    break;
                default:
                    double zero1 = domainAxis.valueToJava2D(0.0, dataArea, 
                            domainAxisLocation);
                    double zero2 = rangeAxis.valueToJava2D(0.0, dataArea, 
                            rangeAxisLocation);
                    transDomain = domainAxis.valueToJava2D(z, dataArea, 
                            domainAxisLocation) - zero1;
                    transRange = zero2 - rangeAxis.valueToJava2D(z, dataArea, 
                            rangeAxisLocation);
            }
            transDomain = Math.abs(transDomain);
            transRange = Math.abs(transRange);
            Ellipse2D circle = null;
            if (orientation == PlotOrientation.VERTICAL) {
                circle = new Ellipse2D.Double(transX - transDomain / 2.0, 
                        transY - transRange / 2.0, transDomain, transRange);
            }
            else if (orientation == PlotOrientation.HORIZONTAL) {
                circle = new Ellipse2D.Double(transY - transRange / 2.0, 
                        transX - transDomain / 2.0, transRange, transDomain);
            }
            g2.setPaint(getItemPaint(series, item));
            g2.fill(circle);
            g2.setStroke(getItemOutlineStroke(series, item));
            g2.setPaint(getItemOutlinePaint(series, item));
            g2.draw(circle);

            if (isItemLabelVisible(series, item)) {
                if (orientation == PlotOrientation.VERTICAL) {
                    drawItemLabel(g2, orientation, dataset, series, item, 
                            transX, transY, false);
                }
                else if (orientation == PlotOrientation.HORIZONTAL) {
                    drawItemLabel(g2, orientation, dataset, series, item, 
                            transY, transX, false);                
                }
            }
            
            // setup for collecting optional entity info...
            EntityCollection entities = null;
            if (info != null) {
                entities = info.getOwner().getEntityCollection();
            }

            // add an entity for the item...
            if (entities != null) {
                String tip = null;
                XYToolTipGenerator generator 
                    = getToolTipGenerator(series, item);
                if (generator != null) {
                    tip = generator.generateToolTip(dataset, series, item);
                }
                String url = null;
                if (getURLGenerator() != null) {
                    url = getURLGenerator().generateURL(dataset, series, item);
                }
                XYItemEntity entity = new XYItemEntity(circle, dataset, series,
                        item, tip, url);
                entities.add(entity);
            }

            int domainAxisIndex = plot.getDomainAxisIndex(domainAxis);
            int rangeAxisIndex = plot.getRangeAxisIndex(rangeAxis);
            updateCrosshairValues(crosshairState, x, y, domainAxisIndex, 
                    rangeAxisIndex, transX, transY, orientation);
        }

    }

    /**
     * Returns a legend item for the specified series.  The default method
     * is overridden so that the legend displays circles for all series.
     *
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     *
     * @return A legend item for the series.
     */
    public LegendItem getLegendItem(int datasetIndex, int series) {
        LegendItem result = null;
        XYPlot xyplot = getPlot();
        if (xyplot != null) {
            XYDataset dataset = xyplot.getDataset(datasetIndex);
            if (dataset != null) {
                if (getItemVisible(series, 0)) {
                    String label = getLegendItemLabelGenerator().generateLabel(
                        dataset, series);
                    String description = label;
                    String toolTipText = null;
                    if (getLegendItemToolTipGenerator() != null) {
                        toolTipText 
                            = getLegendItemToolTipGenerator().generateLabel(
                                dataset, series);
                    }
                    String urlText = null;
                    if (getLegendItemURLGenerator() != null) {
                        urlText = getLegendItemURLGenerator().generateLabel(
                            dataset, series);
                    }
                    Shape shape = new Ellipse2D.Double(-4.0, -4.0, 8.0, 8.0);
                    Paint paint = getSeriesPaint(series);
                    Paint outlinePaint = getSeriesOutlinePaint(series);
                    Stroke outlineStroke = getSeriesOutlineStroke(series);
                    result = new LegendItem(label, description, 
                            toolTipText, urlText, shape, paint, 
                            outlineStroke, outlinePaint);
                }
            }

        }
        return result;
    }
    
    /**
     * Tests this renderer for equality with an arbitrary object.
     * 
     * @param obj  the object (<code>null</code> permitted).
     * 
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYBubbleRenderer)) {
            return false;
        }
        XYBubbleRenderer that = (XYBubbleRenderer) obj;
        if (this.scaleType != that.scaleType) {
            return false;
        }
        return super.equals(obj);
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
