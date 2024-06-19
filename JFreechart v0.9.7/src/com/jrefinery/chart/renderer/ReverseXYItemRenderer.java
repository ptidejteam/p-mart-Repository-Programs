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
 * ReverseXYItemRenderer.java
 * --------------------------
 * (C) Copyright 2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Mark Watson (www.markwatson.com);
 *                   Jonathan Nash;
 *                   Andreas Schneider;
 *                   Norbert Kiesel (for TBD Networks);
 *
 * $Id: ReverseXYItemRenderer.java,v 1.1 2007/10/10 20:00:08 vauchers Exp $
 *
 * Changes:
 * --------
 * 17-Jan-2003 : Version 1 (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 *
 */

package com.jrefinery.chart.renderer;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.io.Serializable;

import com.jrefinery.chart.ChartRenderingInfo;
import com.jrefinery.chart.CrosshairInfo;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.entity.XYItemEntity;
import com.jrefinery.chart.plot.XYPlot;
import com.jrefinery.chart.tooltips.StandardXYToolTipGenerator;
import com.jrefinery.chart.tooltips.XYToolTipGenerator;
import com.jrefinery.chart.urls.XYURLGenerator;
import com.jrefinery.data.XYDataset;

/**
 * This renderer performs exactly the same function as StandardXYItemRenderer, except that it
 * switches the X and Y values (effectively rotating the plot by 90 degrees).
 * 
 * @author David Gilbert
 */
public class ReverseXYItemRenderer extends StandardXYItemRenderer implements Serializable {

    /** A working line (to save creating thousands of instances). */
    private transient Line2D line;

    /**
     * Constructs a new renderer.
     */
    public ReverseXYItemRenderer() {

        this(LINES, new StandardXYToolTipGenerator());

    }

    /**
     * Constructs a new renderer.
     * <p>
     * To specify the type of renderer, use one of the constants: SHAPES, LINES
     * or SHAPES_AND_LINES.
     *
     * @param  type the type.
     */
    public ReverseXYItemRenderer(int type) {
        this(type, new StandardXYToolTipGenerator());
    }

    /**
     * Constructs a new renderer.
     * <p>
     * To specify the type of renderer, use one of the constants: SHAPES, LINES
     * or SHAPES_AND_LINES.
     *
     * @param type  the type of renderer.
     * @param toolTipGenerator  the tooltip generator.
     */
    public ReverseXYItemRenderer(int type, XYToolTipGenerator toolTipGenerator) {

        this(type, toolTipGenerator, null);

    }

    /**
     * Constructs a new renderer.
     * <p>
     * To specify the type of renderer, use one of the constants: SHAPES, LINES or SHAPES_AND_LINES.
     *
     * @param type  the type of renderer.
     * @param toolTipGenerator  the tooltip generator.
     * @param urlGenerator  the URL generator.
     */
    public ReverseXYItemRenderer(int type,
                                  XYToolTipGenerator toolTipGenerator,
                                  XYURLGenerator urlGenerator) {

        super(type, toolTipGenerator, urlGenerator);
        this.line = new Line2D.Double(0.0, 0.0, 0.0, 0.0);

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

        // setup for collecting optional entity info...
        Shape entityArea = null;
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getEntityCollection();
        }

        Paint seriesPaint = getItemPaint(datasetIndex, series, item);
        Stroke seriesStroke = getItemStroke(datasetIndex, series, item);
        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);

        // get the data point...
        Number x1n = dataset.getYValue(series, item);
        Number y1n = dataset.getXValue(series, item);
        if (y1n != null) {
            double x1 = x1n.doubleValue();
            double y1 = y1n.doubleValue();
            double transX1 = domainAxis.translateValueToJava2D(x1, dataArea);
            double transY1 = rangeAxis.translateValueToJava2D(y1, dataArea);

            Paint paint = getPaint(plot, series, item, transX1, transY1);
            if (paint != null) {
                g2.setPaint(paint);
            }

            if (getPlotLines()) {

                if (item > 0) {
                    // get the previous data point...
                    Number x0n = dataset.getYValue(series, item - 1);
                    Number y0n = dataset.getXValue(series, item - 1);
                    if (y0n != null) {
                        double x0 = x0n.doubleValue();
                        double y0 = y0n.doubleValue();
                        boolean drawLine = true;
                        if (getPlotDiscontinuous()) {
                            // only draw a line if the gap between the current and previous data
                            // point is within the threshold
                            int numX = dataset.getItemCount(series);
                            double minX = dataset.getXValue(series, 0).doubleValue();
                            double maxX = dataset.getXValue(series, numX - 1).doubleValue();
                            drawLine = (x1 - x0) <= ((maxX - minX) / numX * getGapThreshold());
                        }
                        if (drawLine) {
                            double transX0 = domainAxis.translateValueToJava2D(x0, dataArea);
                            double transY0 = rangeAxis.translateValueToJava2D(y0, dataArea);

                            line.setLine(transX0, transY0, transX1, transY1);
                            if (line.intersects(dataArea)) {
                                g2.draw(line);
                            }
                        }
                    }
                }
            }

            if (getPlotShapes()) {

                //double scale = getShapeScale(plot, series, item, transX1, transY1);
                Shape shape = getItemShape(datasetIndex, series, item);
                if (shape.intersects(dataArea)) {
                    if (isShapeFilled(plot, series, item, transX1, transY1)) {
                        g2.fill(shape);
                    }
                    else {
                        g2.draw(shape);
                    }
                }
                entityArea = shape;

            }

            if (getPlotImages()) {
                // use shape scale with transform??
                //double scale = getShapeScale(plot, series, item, transX1, transY1);
                Image image = getImage(plot, series, item, transX1, transY1);
                if (image != null) {
                    Point hotspot = getImageHotspot(plot, series, item, transX1, transY1, image);
                    g2.drawImage(image,
                                 (int) (transX1 - hotspot.getX()),
                                 (int) (transY1 - hotspot.getY()), (ImageObserver) null);
                }
                // tooltipArea = image; not sure how to handle this yet
            }

            // add an entity for the item...
            if (entities != null) {
                if (entityArea == null) {
                    entityArea = new Rectangle2D.Double(transX1 - 2, transY1 - 2, 4, 4);
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

            // do we need to update the crosshair values?
            if (plot.isDomainCrosshairLockedOnData()) {
                if (plot.isRangeCrosshairLockedOnData()) {
                    // both axes
                    crosshairInfo.updateCrosshairPoint(x1, y1);
                }
                else {
                    // just the horizontal axis...
                    crosshairInfo.updateCrosshairX(x1);
                }
            }
            else {
                if (plot.isRangeCrosshairLockedOnData()) {
                    // just the vertical axis...
                    crosshairInfo.updateCrosshairY(y1);
                }
            }
        }

    }

}
