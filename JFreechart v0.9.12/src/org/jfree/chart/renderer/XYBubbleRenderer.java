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
 * ---------------------
 * XYBubbleRenderer.java
 * ---------------------
 * (C) Copyright 2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Christian W. Zuckschwerdt;
 *
 * $Id: XYBubbleRenderer.java,v 1.1 2007/10/10 19:12:28 vauchers Exp $
 *
 * Changes
 * -------
 * 28-Jan-2003 : Version 1 (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 01-May-2003 : Modified drawItem(...) method signature (DG);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 20-Aug-2003 : Implemented Cloneable and PublicCloneable (DG);
 * 
 */

package org.jfree.chart.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.CrosshairInfo;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.XYZToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.urls.XYZURLGenerator;
import org.jfree.data.XYDataset;
import org.jfree.data.XYZDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

/**
 * A renderer that draws a circle at each data point.  The renderer expects the dataset to be an
 * {@link XYZDataset}.
 *
 * @author David Gilbert
 */
public class XYBubbleRenderer extends AbstractXYItemRenderer implements XYItemRenderer, 
                                                                        Cloneable,
                                                                        PublicCloneable,
                                                                        Serializable {

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
        this(SCALE_ON_BOTH_AXES); 
    }

    /**
     * Constructs a new renderer.
     *
     * @param scaleType  the type of scaling.
     */
    public XYBubbleRenderer(int scaleType) {
        super(); 
        this.scaleType = scaleType;
    }

    /**
     * Constructs a new renderer.
     *
     * @param scaleType  the type of scaling.
     * @param toolTipGenerator  the tool-tip generator (<code>null</code> permitted).
     * @param urlGenerator  the URL generator (<code>null</code> permitted).
     * 
     * @deprecated Use default constructor.
     */
    public XYBubbleRenderer(int scaleType,
                            XYZToolTipGenerator toolTipGenerator, XYZURLGenerator urlGenerator) {
        super();
        this.scaleType = scaleType;
        setToolTipGenerator(toolTipGenerator);
        setURLGenerator(urlGenerator);
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
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairInfo  information about crosshairs on a plot.
     * @param pass  the pass index.
     */
    public void drawItem(Graphics2D g2,
                         Rectangle2D dataArea,
                         ChartRenderingInfo info,
                         XYPlot plot,
                         ValueAxis domainAxis,
                         ValueAxis rangeAxis,
                         XYDataset dataset,
                         int series,
                         int item,
                         CrosshairInfo crosshairInfo,
                         int pass) {

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

            RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
            RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
            double transX = domainAxis.translateValueToJava2D(x, dataArea, domainAxisLocation);
            double transY = rangeAxis.translateValueToJava2D(y, dataArea, rangeAxisLocation);

            double transDomain = 0.0;
            double transRange = 0.0;
            double zero;

            switch(this.scaleType) {
                case SCALE_ON_DOMAIN_AXIS:
                    zero = domainAxis.translateValueToJava2D(0.0, dataArea, domainAxisLocation);
                    transDomain = domainAxis.translateValueToJava2D(z, dataArea, rangeAxisLocation) 
                                  - zero;
                    transRange = transDomain;
                    break;
                case SCALE_ON_RANGE_AXIS:
                    zero = rangeAxis.translateValueToJava2D(0.0, dataArea, rangeAxisLocation);
                    transRange = zero 
                                 - rangeAxis.translateValueToJava2D(z, dataArea, rangeAxisLocation);
                    transDomain = transRange;
                    break;
                default:
                    double zero1 = domainAxis.translateValueToJava2D(0.0, dataArea, 
                                                                     domainAxisLocation);
                    double zero2 = rangeAxis.translateValueToJava2D(0.0, dataArea, 
                                                                    rangeAxisLocation);
                    transDomain = domainAxis.translateValueToJava2D(z, dataArea, domainAxisLocation)
                                                                    - zero1;
                    transRange = zero2 - rangeAxis.translateValueToJava2D(z, dataArea, 
                                                                          rangeAxisLocation);
            }
            double transZ = -rangeAxis.translateValueToJava2D(z, dataArea, rangeAxisLocation)
                            + rangeAxis.translateValueToJava2D(0.0, dataArea, rangeAxisLocation);
            transZ = Math.abs(transZ);
            transDomain = Math.abs(transDomain);
            transRange = Math.abs(transRange);
            Ellipse2D circle = null;
            if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                circle = new Ellipse2D.Double(transX - transZ / 2.0,
                                              transY - transZ / 2.0,
                                              transDomain, transRange);
            }
            else if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                circle = new Ellipse2D.Double(transY - transZ / 2.0,
                                              transX - transZ / 2.0,
                                              transRange, transDomain);
            }
            g2.setPaint(getItemPaint(series, item));
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
                XYItemEntity entity = new XYItemEntity(circle, dataset, series, item, tip, url);
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
