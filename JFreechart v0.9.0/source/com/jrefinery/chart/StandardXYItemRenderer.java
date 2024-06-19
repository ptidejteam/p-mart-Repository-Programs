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
 * ---------------------------
 * StandardXYItemRenderer.java
 * ---------------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Mark Watson (www.markwatson.com);
 *                   Jonathan Nash;
 *                   Andreas Schneider;
 *
 * $Id: StandardXYItemRenderer.java,v 1.1 2007/10/10 19:01:18 vauchers Exp $
 *
 * Changes:
 * --------
 * 19-Oct-2001 : Version 1, based on code by Mark Watson (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 21-Dec-2001 : Added working line instance to improve performance (DG);
 * 22-Jan-2002 : Added code to lock crosshairs to data points.  Based on code by Jonathan Nash (DG);
 * 23-Jan-2002 : Added DrawInfo parameter to drawItem(...) method (DG);
 * 28-Mar-2002 : Added a property change listener mechanism so that the renderer no longer needs to
 *               be immutable (DG);
 * 02-Apr-2002 : Modified to handle null values (DG);
 * 09-Apr-2002 : Modified draw method to return void.  Removed the translated zero from the
 *               drawItem method.  Override the initialise() method to calculate it (DG);
 * 13-May-2002 : Added code from Andreas Schneider to allow changing shapes/colors per item (DG);
 * 24-May-2002 : Incorporated tooltips into chart entities (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.ImageObserver;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import com.jrefinery.data.XYDataset;
import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.entity.XYItemEntity;
import com.jrefinery.chart.tooltips.XYToolTipGenerator;
import com.jrefinery.chart.tooltips.StandardXYToolTipGenerator;

/**
 * Standard item renderer for an XYPlot.  This class can draw (a) shapes at each point, or (b) lines
 * between points, or (c) both shapes and lines.
 */
public class StandardXYItemRenderer extends AbstractXYItemRenderer implements XYItemRenderer {

    /** Useful constant for specifying the type of rendering (shapes only). */
    public static final int SHAPES = 1;

    /** Useful constant for specifying the type of rendering (lines only). */
    public static final int LINES = 2;

    /** Useful constant for specifying the type of rendering (shapes and lines).
      * @deprecated Use (SHAPES | LINES) instead
      */
    public static final int SHAPES_AND_LINES = 3;

    /** Useful constant for specifying the type of rendering (images only). */
    public static final int IMAGES = 4;

    /** A flag indicating whether or not shapes are drawn at each XY point. */
    protected boolean plotShapes;

    /** A flag indicating whether or not lines are drawn between XY points. */
    protected boolean plotLines;

    /** A flag indicating whether or not images are drawn between XY points. */
    protected boolean plotImages;

    /** Scale factor for standard shapes.
      * @deprecated Use getShapeScale() instead
      */
    protected double shapeScale  = 6;

    /** A working line (to save creating thousands of instances). */
    protected Line2D line;

    /**
     * Constructs a new renderer.
     */
    public StandardXYItemRenderer() {

        this(LINES, new StandardXYToolTipGenerator());

    }

    /**
     * Constructs a new renderer.
     * <p>
     * To specify the type of renderer, use one of the constants: SHAPES, LINES or SHAPES_AND_LINES.
     *
     * @param type The type of renderer.
     * @param toolTipGenerator The tooltip generator.
     */
    public StandardXYItemRenderer(int type, XYToolTipGenerator toolTipGenerator) {

        super(toolTipGenerator);
        if ((type & SHAPES) != 0) this.plotShapes=true;
        if ((type & LINES) != 0) this.plotLines=true;
        if ((type & IMAGES) != 0) this.plotImages=true;
        if (type==SHAPES_AND_LINES) {
            this.plotShapes = true;
            this.plotLines = true;
        }
        this.line = new Line2D.Double(0.0, 0.0, 0.0, 0.0);

    }

    /**
     * Returns true if shapes are being plotted by the renderer.
     */
    public boolean getPlotShapes() {
        return this.plotShapes;
    }

    /**
     * Returns true if lines are being plotted by the renderer.
     */
    public boolean getPlotLines() {
        return this.plotLines;
    }

    /**
     * Returns true if images are being plotted by the renderer.
     */
    public boolean getPlotImages() {
        return this.plotImages;
    }

    /**
     * Returns the shape scale of a single data item.
     *
     * @param plot The plot (can be used to obtain standard color information etc).
     * @param series The series index
     * @param item The item index
     * @param x The x value of the item
     * @param y The y value of the item
     *
     * @ return The scale used to draw the shape used for the data item
     */
    protected double getShapeScale(Plot plot,int series,int item, double x, double y) {
      return 6.0;
    }

    /**
     * Returns the shape used to draw a single data item.
     *
     * @param plot The plot (can be used to obtain standard color information etc).
     * @param series The series index
     * @param item The item index
     * @param x The x value of the item
     * @param y The y value of the item
     * @param scale The scale used to draw the shape
     *
     * @return The shape used to draw the data item
     */
    protected Shape getShape(Plot plot, int series, int item, double x, double y, double scale) {
      return plot.getShape(series, item, x, y, scale);
    }

    /**
     * Returns the image used to draw a single data item.
     *
     * @param plot The plot (can be used to obtain standard color information etc).
     * @param series The series index
     * @param item The item index
     * @param x The x value of the item
     * @param y The y value of the item
     *
     * @return The image used to draw the data item
     */
    protected Image getImage(Plot plot, int series, int item, double x, double y) {
      // should this be added to the plot as well ?
      // return plot.getShape(series, item, x, y, scale);
      // or should this be left to the user - like this:
      return null;
    }

    /**
     * Returns the hotspot of the image used to draw a single data item.
     * The hotspot is the point relative to the top left of the image
     * that should indicate the data item. The default is the center of the image
     *
     * @param plot The plot (can be used to obtain standard color information etc).
     * @param image The image (can be used to get size information about the image)
     * @param series The series index
     * @param item The item index
     * @param x The x value of the item
     * @param y The y value of the item
     *
     * @return The hotspot used to draw the data item
     */
    protected Point getImageHotspot(Plot plot, int series, int item, double x, double y, Image image) {
      int height = image.getHeight(null);
      int width = image.getWidth(null);
      return new Point(width/2, height/2);
    }

    /**
     * Is used to determine if a shape is filled when drawn or not
     *
     * @param plot The plot (can be used to obtain standard color information etc).
     * @param series The series index
     * @param item The item index
     * @param x The x value of the item
     * @param y The y value of the item
     *
     * @return True if the shape used to draw the data item should be filled, false otherwise.
     */
    protected boolean isShapeFilled(Plot plot, int series, int item, double x, double y) {
      return false;
    }

    /**
     * Returns the Paint used to draw a single data item
     *
     * @param plot The plot (can be used to obtain standard color information etc).
     * @param series The series index
     * @param item The item index
     * @param x The x value of the item
     * @param y The y value of the item
     *
     * @return The Paint used to draw the item. If this method returns null the Paint used will remain unchanged
     */
    protected Paint getPaint(Plot plot, int series, int item, double x, double y) {
      // return plot.getSeriesPaint(series);
      return null;
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2 The graphics device.
     * @param dataArea The area within which the data is being drawn.
     * @param info Collects information about the drawing.
     * @param plot The plot (can be used to obtain standard color information etc).
     * @param horizontalAxis The horizontal axis.
     * @param verticalAxis The vertical axis.
     * @param data The dataset.
     * @param series The series index.
     * @param item The item index.
     */
    public void drawItem(Graphics2D g2, Rectangle2D dataArea, ChartRenderingInfo info,
                         XYPlot plot, ValueAxis horizontalAxis, ValueAxis verticalAxis,
                         XYDataset data, int series, int item,
                         CrosshairInfo crosshairInfo) {

        // setup for collecting optional entity info...
        Shape entityArea = null;
        EntityCollection entities = null;
        if (info!=null) {
            entities = info.getEntityCollection();
        }

        Paint seriesPaint = plot.getSeriesPaint(series);
        Stroke seriesStroke = plot.getSeriesStroke(series);
        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);

        // get the data point...
        Number x1 = data.getXValue(series, item);
        Number y1 = data.getYValue(series, item);
        if (y1!=null) {
            double transX1 = horizontalAxis.translateValueToJava2D(x1.doubleValue(), dataArea);
            double transY1 = verticalAxis.translateValueToJava2D(y1.doubleValue(), dataArea);

            Paint paint = getPaint(plot, series, item, transX1, transY1);
            if (paint != null) {
              g2.setPaint(paint);
            }

            if (this.plotLines) {

                if (item>0) {
                    // get the previous data point...
                    Number x0 = data.getXValue(series, item-1);
                    Number y0 = data.getYValue(series, item-1);
                    if (y0!=null) {
                        double transX0 = horizontalAxis.translateValueToJava2D(x0.doubleValue(), dataArea);
                        double transY0 = verticalAxis.translateValueToJava2D(y0.doubleValue(), dataArea);

                        line.setLine(transX0, transY0, transX1, transY1);
                        if (line.intersects(dataArea)) {
                            g2.draw(line);
                        }
                    }
                }
            }

            if (this.plotShapes) {

                shapeScale = getShapeScale(plot, series, item, transX1, transY1);
                Shape shape = getShape(plot, series, item, transX1, transY1, shapeScale);
                if (isShapeFilled(plot, series, item, transX1, transY1)) {
                    if (shape.intersects(dataArea)) g2.fill(shape);
                } else {
                    if (shape.intersects(dataArea)) g2.draw(shape);
                }
                entityArea = shape;

            }

            if (this.plotImages) {
                // use shape scale with transform??
                shapeScale = getShapeScale(plot, series, item, transX1, transY1);
                Image image = getImage(plot, series, item, transX1, transY1);
                if (image != null) {
                    Point hotspot = getImageHotspot(plot, series, item, transX1, transY1, image);
                    g2.drawImage(image,(int)(transX1-hotspot.getX()),(int)(transY1-hotspot.getY()),(ImageObserver)null);
                }
                // tooltipArea = image; not sure how to handle this yet
            }

            // add an entity for the item...
            if (entities!=null) {
                if (entityArea==null) {
                    entityArea = new Rectangle2D.Double(transX1-2, transY1-2, 4, 4);
                }
                String tip = "";
                if (this.toolTipGenerator!=null) {
                    tip = this.toolTipGenerator.generateToolTip(data, series, item);
                }
                XYItemEntity entity = new XYItemEntity(entityArea, tip, series, item);
                entities.addEntity(entity);
            }

            // do we need to update the crosshair values?
            double distance = 0.0;
            if (horizontalAxis.isCrosshairLockedOnData()) {
                if (verticalAxis.isCrosshairLockedOnData()) {
                    // both axes
                    crosshairInfo.updateCrosshairPoint(x1.doubleValue(), y1.doubleValue());
                }
                else {
                    // just the horizontal axis...
                    crosshairInfo.updateCrosshairX(x1.doubleValue());
                }
            }
            else {
                if (verticalAxis.isCrosshairLockedOnData()) {
                    // just the vertical axis...
                    crosshairInfo.updateCrosshairY(y1.doubleValue());
                }
            }
        }

    }

}
