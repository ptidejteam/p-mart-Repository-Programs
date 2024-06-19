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
 * -------------------------
 * XYDifferenceRenderer.java
 * -------------------------
 * (C) Copyright 2003 by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: XYDifferenceRenderer.java,v 1.1 2007/10/10 20:07:35 vauchers Exp $
 *
 * Changes:
 * --------
 * 30-Apr-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.renderer;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.CrosshairInfo;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.XYDataset;

/**
 * A renderer for an {@link XYPlot} that highlights the differences between two
 * series.  The renderer expects a dataset that:
 * <ul>
 * <li>has exactly two series;</li>
 * <li>each series has the same x-values;</li>
 * <li>no <code>null</code> values;
 * </ul>
 *
 * @author David Gilbert
 */
public class XYDifferenceRenderer extends AbstractXYItemRenderer
                                  implements XYItemRenderer, Serializable {

    /** The paint used to highlight positive differences (y(0) > y(1)). */
    private Paint positivePaint;

    /** The paint used to highlight negative differences (y(0) < y(1)). */
    private Paint negativePaint;

    /** Display shapes at each point? */
    private boolean plotShapes = true;

    /**
     * Creates a new renderer.
     *
     * @param positivePaint  the highlight color for positive differences;
     * @param negativePaint  the highlight color for negative differences;
     * @param shapes  draw shapes?
     */
    public XYDifferenceRenderer(Paint positivePaint, Paint negativePaint, boolean shapes) {
        this.positivePaint = positivePaint;
        this.negativePaint = negativePaint;
        this.plotShapes = shapes;
    }

    /**
     * Initialises the renderer then returns the number of 'passes' through the data that the
     * renderer will require (usually just one).  This method will be called before the first
     * item is rendered, giving the renderer an opportunity to initialise any
     * state information it wants to maintain.  The renderer can do nothing if it chooses.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area inside the axes.
     * @param plot  the plot.
     * @param data  the data.
     * @param info  an optional info collection object to return data back to the caller.
     *
     * @return The number of passes the renderer requires.
     */
    public int initialise(Graphics2D g2,
                          Rectangle2D dataArea,
                          XYPlot plot,
                          XYDataset data,
                          ChartRenderingInfo info) {

        super.initialise(g2, dataArea, plot, data, info);
        return 2;

    }

    /**
     * Returns the paint used to highlight positive differences.
     *
     * @return The paint.
     */
    public Paint getPositivePaint() {
        return this.positivePaint;
    }

    /**
     * Returns the paint used to highlight negative differences.
     *
     * @return The paint.
     */
    public Paint getNegativePaint() {
        return this.positivePaint;
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

        if (pass == 0) {
            drawItemPass0(g2, dataArea, info, plot, domainAxis, rangeAxis, dataset,
                          series, item, crosshairInfo);
        }
        else if (pass == 1) {
            drawItemPass1(g2, dataArea, info, plot, domainAxis, rangeAxis, dataset,
                          series, item, crosshairInfo);
        }

    }

    /**
     * Draws the visual representation of a single data item, first pass.
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
     */
    private void drawItemPass0(Graphics2D g2,
                              Rectangle2D dataArea,
                              ChartRenderingInfo info,
                              XYPlot plot,
                              ValueAxis domainAxis,
                              ValueAxis rangeAxis,
                              XYDataset dataset,
                              int series,
                              int item,
                              CrosshairInfo crosshairInfo) {

        if (series == 0) {

            // get the data points...
            Number y0n = dataset.getYValue(0, item);
            Number x1n = dataset.getXValue(1, item);
            Number y1n = dataset.getYValue(1, item);

            AxisLocation domainAxisLocation = plot.getDomainAxisLocation();
            AxisLocation rangeAxisLocation = plot.getRangeAxisLocation();
            double y0 = y0n.doubleValue();
            double transY0 = rangeAxis.translateValueToJava2D(y0, dataArea, rangeAxisLocation);

            double x1 = x1n.doubleValue();
            double y1 = y1n.doubleValue();
            double transX1 = domainAxis.translateValueToJava2D(x1, dataArea, domainAxisLocation);
            double transY1 = rangeAxis.translateValueToJava2D(y1, dataArea, rangeAxisLocation);

            if (item > 0) {
                // get the previous data points...
                // get the data points...
                Number prevx0n = dataset.getXValue(0, item - 1);
                Number prevy0n = dataset.getYValue(0, item - 1);
                Number prevy1n = dataset.getYValue(1, item - 1);

                double prevx0 = prevx0n.doubleValue();
                double prevy0 = prevy0n.doubleValue();
                double prevtransX0 = domainAxis.translateValueToJava2D(prevx0, dataArea, 
                                                                       domainAxisLocation);
                double prevtransY0 = rangeAxis.translateValueToJava2D(prevy0, dataArea, 
                                                                      rangeAxisLocation);

                double prevy1 = prevy1n.doubleValue();
                double prevtransY1 = rangeAxis.translateValueToJava2D(prevy1, dataArea, 
                                                                      rangeAxisLocation);

                Shape positive = getPositiveArea((float) prevtransX0,
                                                 (float) prevtransY0, (float) prevtransY1,
                                                 (float) transX1,
                                                 (float) transY0, (float) transY1);
                if (positive != null) {
                    g2.setPaint(this.positivePaint);
                    g2.fill(positive);
                }

                Shape negative = getNegativeArea((float) prevtransX0,
                                                 (float) prevtransY0, (float) prevtransY1,
                                                 (float) transX1,
                                                 (float) transY0, (float) transY1);

                if (negative != null) {
                    g2.setPaint(this.negativePaint);
                    g2.fill(negative);
                }
            }
        }

    }

    /**
     * Draws the visual representation of a single data item, second pass.
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
     */
    private void drawItemPass1(Graphics2D g2,
                               Rectangle2D dataArea,
                               ChartRenderingInfo info,
                               XYPlot plot,
                               ValueAxis domainAxis,
                               ValueAxis rangeAxis,
                               XYDataset dataset,
                               int series,
                               int item,
                               CrosshairInfo crosshairInfo) {

        Shape entityArea = null;
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getEntityCollection();
        }

        Paint seriesPaint = getItemPaint(series, item);
        Stroke seriesStroke = getItemStroke(series, item);
        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);

        if (series == 0) {

            // get the data points...
            Number x0n = dataset.getXValue(0, item);
            Number y0n = dataset.getYValue(0, item);
            Number x1n = dataset.getXValue(1, item);
            Number y1n = dataset.getYValue(1, item);

            AxisLocation domainAxisLocation = plot.getDomainAxisLocation();
            AxisLocation rangeAxisLocation = plot.getRangeAxisLocation();

            double x0 = x0n.doubleValue();
            double y0 = y0n.doubleValue();
            double transX0 = domainAxis.translateValueToJava2D(x0, dataArea, domainAxisLocation);
            double transY0 = rangeAxis.translateValueToJava2D(y0, dataArea, rangeAxisLocation);

            double x1 = x1n.doubleValue();
            double y1 = y1n.doubleValue();
            double transX1 = domainAxis.translateValueToJava2D(x1, dataArea, domainAxisLocation);
            double transY1 = rangeAxis.translateValueToJava2D(y1, dataArea, rangeAxisLocation);

            if (item > 0) {
                // get the previous data points...
                // get the data points...
                Number prevx0n = dataset.getXValue(0, item - 1);
                Number prevy0n = dataset.getYValue(0, item - 1);
                Number prevx1n = dataset.getXValue(1, item - 1);
                Number prevy1n = dataset.getYValue(1, item - 1);

                double prevx0 = prevx0n.doubleValue();
                double prevy0 = prevy0n.doubleValue();
                double prevtransX0 = domainAxis.translateValueToJava2D(prevx0, dataArea, 
                                                                       domainAxisLocation);
                double prevtransY0 = rangeAxis.translateValueToJava2D(prevy0, dataArea, 
                                                                      rangeAxisLocation);

                double prevx1 = prevx1n.doubleValue();
                double prevy1 = prevy1n.doubleValue();
                double prevtransX1 = domainAxis.translateValueToJava2D(prevx1, dataArea, 
                                                                       domainAxisLocation);
                double prevtransY1 = rangeAxis.translateValueToJava2D(prevy1, dataArea, 
                                                                      rangeAxisLocation);

                Line2D line0 = new Line2D.Double(transX0, transY0, prevtransX0, prevtransY0);
                if (line0.intersects(dataArea)) {
                    g2.setPaint(getItemPaint(series, item));
                    g2.draw(line0);
                }
                Line2D line1 = new Line2D.Double(transX1, transY1, prevtransX1, prevtransY1);
                if (line1.intersects(dataArea)) {
                    g2.setPaint(getItemPaint(1, item));
                    g2.draw(line1);
                }
            }

            if (this.plotShapes) {
                Shape shape0 = getItemShape(series, item);
                shape0 = createTransformedShape(shape0, transX0, transY0);
                if (shape0.intersects(dataArea)) {
                    g2.setPaint(getItemPaint(series, item));
                    g2.fill(shape0);
                }
                entityArea = shape0;

                // add an entity for the item...
                if (entities != null) {
                    if (entityArea == null) {
                        entityArea = new Rectangle2D.Double(transX0 - 2, transY0 - 2, 4, 4);
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

                Shape shape1 = getItemShape(series + 1, item);
                shape1 = createTransformedShape(shape1, transX1, transY1);
                if (shape1.intersects(dataArea)) {
                    g2.setPaint(getItemPaint(series + 1, item));
                    g2.fill(shape1);
                }
                entityArea = shape1;

                // add an entity for the item...
                if (entities != null) {
                    if (entityArea == null) {
                        entityArea = new Rectangle2D.Double(transX1 - 2, transY1 - 2, 4, 4);
                    }
                    String tip = null;
                    if (getToolTipGenerator() != null) {
                        tip = getToolTipGenerator().generateToolTip(dataset, series + 1, item);
                    }
                    String url = null;
                    if (getURLGenerator() != null) {
                        url = getURLGenerator().generateURL(dataset, series + 1, item);
                    }
                    XYItemEntity entity = new XYItemEntity(entityArea, tip, url, series + 1, item);
                    entities.addEntity(entity);
                }
            }

        }

    }

    /**
     * Returns the positive area for a crossover point.
     * 
     * @param x0  x coordinate.
     * @param y0A  y coordinate A.
     * @param y0B  y coordinate B.
     * @param x1  x coordinate.
     * @param y1A  y coordinate A.
     * @param y1B  y coordinate B.
     * 
     * @return The positive area.
     */
    private Shape getPositiveArea(float x0, float y0A, float y0B, float x1, float y1A, float y1B) {

        Shape result = null;

        if (y0A >= y0B) {  // negative
            if (y1A >= y1B) {
                // all negative - return null
            }
            else {
                // changed from negative to positive
                //this.positivePaint = Color.yellow;
                float[] p = getIntersection(x0, y0A, x1, y1A, x0, y0B, x1, y1B);
                GeneralPath area = new GeneralPath();
                area.moveTo(x1, y1A);
                area.lineTo(p[0], p[1]);
                area.lineTo(x1, y1B);
                area.closePath();
                result = area;
            }
        }
        else {
            if (y1A >= y1B) {
                // changed from positive to negative
                //this.positivePaint = Color.green;
                float[] p = getIntersection(x0, y0A, x1, y1A, x0, y0B, x1, y1B);
                GeneralPath area = new GeneralPath();
                area.moveTo(x0, y0A);
                area.lineTo(p[0], p[1]);
                area.lineTo(x0, y0B);
                area.closePath();
                result = area;

            }
            else {
                //this.positivePaint = Color.blue;
                GeneralPath area = new GeneralPath();
                area.moveTo(x0, y0A);
                area.lineTo(x1, y1A);
                area.lineTo(x1, y1B);
                area.lineTo(x0, y0B);
                area.closePath();
                result = area;
            }

        }

        return result;

    }

    /**
     * Returns the negative area for a cross-over section.
     * 
     * @param x0  x coordinate.
     * @param y0A  y coordinate A.
     * @param y0B  y coordinate B.
     * @param x1  x coordinate.
     * @param y1A  y coordinate A.
     * @param y1B  y coordinate B.
     * 
     * @return The negative area.
     */
    private Shape getNegativeArea(float x0, float y0A, float y0B, float x1, float y1A, float y1B) {

        Shape result = null;

        if (y0A >= y0B) {  // negative
            if (y1A >= y1B) {  // negative
                //this.negativePaint = Color.red;
                GeneralPath area = new GeneralPath();
                area.moveTo(x0, y0A);
                area.lineTo(x1, y1A);
                area.lineTo(x1, y1B);
                area.lineTo(x0, y0B);
                area.closePath();
                result = area;
            }
            else {  // changed from negative to positive

                //this.negativePaint = Color.pink;
                float[] p = getIntersection(x0, y0A, x1, y1A, x0, y0B, x1, y1B);
                GeneralPath area = new GeneralPath();
                area.moveTo(x0, y0A);
                area.lineTo(p[0], p[1]);
                area.lineTo(x0, y0B);
                area.closePath();
                result = area;
            }
        }
        else {
            if (y1A >= y1B) {
                // changed from positive to negative
                //this.negativePaint = Color.gray;
                float[] p = getIntersection(x0, y0A, x1, y1A, x0, y0B, x1, y1B);
                GeneralPath area = new GeneralPath();
                area.moveTo(x1, y1A);
                area.lineTo(p[0], p[1]);
                area.lineTo(x1, y1B);
                area.closePath();
                result = area;
            }
            else {
                // all negative - return null
            }

        }

        return result;

    }

    /**
     * Returns the intersection point of two lines.
     * 
     * @param x1  x1
     * @param y1  y1
     * @param x2  x2
     * @param y2  y2
     * @param x3  x3
     * @param y3  y3
     * @param x4  x4
     * @param y4  y4
     * 
     * @return The intersection point.
     */
    private float[] getIntersection(float x1, float y1, float x2, float y2,
                                    float x3, float y3, float x4, float y4) {

        float n = (x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3);
        float d = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        float u = n / d;

        float[] result = new float[2];
        result[0] = x1 + u * (x2 - x1);
        result[1] = y1 + u * (y2 - y1);
        return result;

    }

}
