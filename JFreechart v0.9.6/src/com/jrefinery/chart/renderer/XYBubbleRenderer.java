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
 * ---------------------
 * XYBubbleRenderer.java
 * ---------------------
 * (C) Copyright 2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: XYBubbleRenderer.java,v 1.1 2007/10/10 19:57:50 vauchers Exp $
 *
 * Changes
 * -------
 * 28-Jan-2003 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import com.jrefinery.chart.ChartRenderingInfo;
import com.jrefinery.chart.CrosshairInfo;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.entity.XYItemEntity;
import com.jrefinery.chart.plot.XYPlot;
import com.jrefinery.chart.tooltips.XYZToolTipGenerator;
import com.jrefinery.chart.urls.XYZURLGenerator;
import com.jrefinery.data.XYDataset;
import com.jrefinery.data.XYZDataset;

/**
 * A renderer that draws a circle at each data point.  The renderer expects the dataset to be an
 * {@link XYZDataset}.
 *
 * @author David Gilbert
 */
public class XYBubbleRenderer extends AbstractXYItemRenderer
                              implements XYItemRenderer {

    /** A useful constant. */
    public static final int SCALE_ON_BOTH_AXES = 0;
    
    /** A useful constant. */
    public static final int SCALE_ON_DOMAIN_AXIS = 1;
    
    /** A useful constant. */
    public static final int SCALE_ON_RANGE_AXIS = 2;

    /** Controls how the width and height of the bubble are scaled. */    
    private int scaleType;

    /**
     * Constructs a new renderer.
     */
    public XYBubbleRenderer() {
        this(SCALE_ON_BOTH_AXES, null, null);
    }
    
    /**
     * Constructs a new renderer.
     * 
     * @param scaleType  the type of scaling.
     * @param toolTipGenerator  the tool-tip generator (<code>null</code> permitted).
     * @param urlGenerator  the URL generator (<code>null</code> permitted).
     */
    public XYBubbleRenderer(int scaleType,
                            XYZToolTipGenerator toolTipGenerator, XYZURLGenerator urlGenerator) {
        super(toolTipGenerator, urlGenerator);
        this.scaleType = scaleType;
    }

    /**
     * Returns the scale type.
     * 
     * @return the scale type.
     */
    public int getScaleType() {
        return this.scaleType;
    }
    
    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area within which the data is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param domainAxis  the domain (horizontal) axis.
     * @param rangeAxis  the range (vertical) axis.
     * @param dataset  the dataset.
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairInfo  information about crosshairs on a plot.
     */
    public void drawItem(Graphics2D g2,
                         Rectangle2D dataArea,
                         ChartRenderingInfo info,
                         XYPlot plot,
                         ValueAxis domainAxis,
                         ValueAxis rangeAxis,
                         XYDataset dataset,
                         int datasetIndex,
                         int series,
                         int item,
                         CrosshairInfo crosshairInfo) {

        // get the data point...
        Number xn = dataset.getXValue(series, item);
        Number yn = dataset.getYValue(series, item);
        Number zn = null;
        if (dataset instanceof XYZDataset) {
            XYZDataset xyzData = (XYZDataset) dataset;
            zn = xyzData.getZValue(series, item);
        }
        if (zn != null) {
            double x = xn.doubleValue();
            double y = yn.doubleValue();
            double z = zn.doubleValue();
            double transX = domainAxis.translateValueToJava2D(x, dataArea);
            double transY = rangeAxis.translateValueToJava2D(y, dataArea);

            double transWidth = 0.0;
            double transHeight = 0.0;
            double zero;
            
            switch(this.scaleType) {
                case SCALE_ON_DOMAIN_AXIS:
                    zero = domainAxis.translateValueToJava2D(0.0, dataArea);
                    transWidth = domainAxis.translateValueToJava2D(z, dataArea) - zero;
                    transHeight = transWidth;
                    break;
                case SCALE_ON_RANGE_AXIS:
                    zero = rangeAxis.translateValueToJava2D(0.0, dataArea);
                    transWidth = zero - rangeAxis.translateValueToJava2D(z, dataArea);
                    transHeight = transWidth;
                    break;
                default:
                    double zero1 = domainAxis.translateValueToJava2D(0.0, dataArea);
                    double zero2 = rangeAxis.translateValueToJava2D(0.0, dataArea);
                    transWidth = domainAxis.translateValueToJava2D(z, dataArea) - zero1;
                    transHeight = zero2 - rangeAxis.translateValueToJava2D(z, dataArea);
            }
            double transZ = -rangeAxis.translateValueToJava2D(z, dataArea) 
                          + rangeAxis.translateValueToJava2D(0.0, dataArea);
            Ellipse2D circle = new Ellipse2D.Double(transX - transZ / 2.0, 
                                                    transY - transZ / 2.0, 
                                                    transWidth, transHeight);
            g2.setPaint(getItemPaint(datasetIndex, series, item));
            g2.fill(circle);
            g2.setStroke(new BasicStroke(1.0f));
            g2.setPaint(Color.lightGray);
            g2.draw(circle);

            // setup for collecting optional entity info...
            EntityCollection entities = null;
            if (info != null) {
                entities = info.getEntityCollection();
            }
        
            // add an entity for the item...
            if (entities != null) {
                String tip = null;
                if (getToolTipGenerator() != null) {
                    tip = getToolTipGenerator().generateToolTip(dataset, series, item);
                }
                String url = null;
                if (getURLGenerator() != null) {
                    url = getURLGenerator().generateURL(dataset, series, item);
                }
                XYItemEntity entity = new XYItemEntity(circle, tip, url, series, item);
                entities.addEntity(entity);
            }

            // do we need to update the crosshair values?
            if (plot.isDomainCrosshairLockedOnData()) {
                if (plot.isRangeCrosshairLockedOnData()) {
                    // both axes
                    crosshairInfo.updateCrosshairPoint(x, y);
                }
                else {
                    // just the horizontal axis...
                    crosshairInfo.updateCrosshairX(x);
                }
            }
            else {
                if (plot.isRangeCrosshairLockedOnData()) {
                    // just the vertical axis...
                    crosshairInfo.updateCrosshairY(y);
                }
            }
        }

    }

}
