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
 * -----------------------
 * AreaXYItemRenderer.java
 * -----------------------
 * (C) Copyright 2002, by Hari.
 *
 * Original Author:  Hari (ourhari@hotmail.com);
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: AreaXYItemRenderer.java,v 1.1 2007/10/10 19:01:18 vauchers Exp $
 *
 * Changes:
 * --------
 * 03-Apr-2002 : Version 1, contributed by Hari.  This class is based on the StandardXYItemRenderer
 *               class (DG);
 * 09-Apr-2002 : Removed the translated zero from the drawItem method - overridden the initialise()
 *               method to calculate it (DG);
 * 30-May-2002 : Added tool tip generator to constructor to match super class (DG);
 *
 */

package com.jrefinery.chart;

import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.entity.XYItemEntity;
import com.jrefinery.chart.tooltips.XYToolTipGenerator;
import com.jrefinery.data.XYDataset;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.Shape;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * Area item renderer for an XYPlot.  This class can draw (a) shapes at each point, or (b) lines
 * between points, or (c) both shapes and lines, or (d) filled areas, or (e) filled areas and
 * shapes.
 */
public class AreaXYItemRenderer extends AbstractXYItemRenderer implements XYItemRenderer {

    /** Useful constant for specifying the type of rendering (shapes only). */
    public static final int SHAPES = 1;

    /** Useful constant for specifying the type of rendering (lines only). */
    public static final int LINES = 2;

    /** Useful constant for specifying the type of rendering (shapes and lines). */
    public static final int SHAPES_AND_LINES = 3;

    /** Useful constant for specifying the type of rendering (area only). */
    public static final int AREA = 4;

    /** Useful constant for specifying the type of rendering (area and shapes). */
    public static final int AREA_AND_SHAPES = 5;

    /** A flag indicating whether or not shapes are drawn at each XY point. */
    protected boolean plotShapes;

    /** A flag indicating whether or not lines are drawn between XY points. */
    protected boolean plotLines;

    /** A flag indicating whether or not Area are drawn at each XY point. */
    protected boolean plotArea;

    /** Scale factor for standard shapes. */
    protected double shapeScale  = 6;

    protected boolean showOutline;

    protected double zeroToJava2D;

    /** A working line (to save creating thousands of instances). */
    protected Line2D line;

    /** Area of the complete series */
    protected Polygon pArea = null;

    /**
     * Constructs a new renderer.
     */
    public AreaXYItemRenderer() {

        this(AREA);

    }

    /**
     * Constructs a new renderer.
     *
     */
    public AreaXYItemRenderer(int type) {
        this(type, null);
    }

    /**
     * Constructs a new renderer.
     * <p>
     * To specify the type of renderer, use one of the constants: SHAPES, LINES, SHAPES_AND_LINES,
     *                                                            AREA or AREA_AND_SHAPES.
     *
     * @param type The type of renderer.
     */
    public AreaXYItemRenderer(int type, XYToolTipGenerator toolTipGenerator) {

        super(toolTipGenerator);

        if (type==SHAPES) this.plotShapes=true;
        if (type==LINES) this.plotLines=true;
        if (type==SHAPES_AND_LINES) {
            this.plotShapes = true;
            this.plotLines = true;
        }
        if (type==AREA) {
            this.plotArea=true;
        }
        if (type==AREA_AND_SHAPES) {
            this.plotArea=true;
            this.plotShapes=true;
        }
        this.line = new Line2D.Double(0.0, 0.0, 0.0, 0.0);
        showOutline = false;

    }

    public boolean isOutline() {
        return showOutline;
    }

    public void setOutline( boolean show) {
        showOutline = show;
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
     * Returns true if Area is being plotted by the renderer.
     */
    public boolean getPlotArea() {
        return this.plotArea;
    }

    /**
     * Initialises the renderer.  Here we calculate the Java2D y-coordinate for zero, since all
     * the bars have their bases fixed at zero.
     *
     * @param g2 The graphics device.
     * @param dataArea The area inside the axes.
     * @param plot The plot.
     * @param data The data.
     */
    public void initialise(Graphics2D g2,
                           Rectangle2D dataArea,
                           XYPlot plot,
                           XYDataset data,
                           ChartRenderingInfo info) {

        super.initialise(g2, dataArea, plot, data, info);
        ValueAxis rangeAxis = plot.getRangeAxis();
        this.zeroToJava2D = rangeAxis.translateValueToJava2D(0.0, dataArea);

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

        // Get the item count for the series, so that we can know which is the end of the series.
        int itemCount = data.getItemCount(series);

        Paint seriesPaint = plot.getSeriesPaint(series);
        Stroke seriesStroke = plot.getSeriesStroke(series);
        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);

        // get the data point...
        Number x1 = data.getXValue(series, item);
        Number y1 = data.getYValue(series, item);
        double transX1 = horizontalAxis.translateValueToJava2D(x1.doubleValue(), dataArea);
        double transY1 = verticalAxis.translateValueToJava2D(y1.doubleValue(), dataArea);

        if( item == 0) {
            // Create a new Area for the series
            pArea = new Polygon();

            // start from Y = 0
            double transY2 = verticalAxis.translateValueToJava2D(0.0, dataArea);

            // The first point is (x, 0)
            pArea.addPoint((int)transX1, (int)transY2);
        }

        // Add each point to Area (x, y)
        pArea.addPoint((int)transX1, (int)transY1);

        Shape shape = null;
        if (this.plotShapes) {
            shape = plot.getShape(series, item, transX1, transY1, shapeScale);
            g2.draw(shape);
        }
        else {
            shape = new Rectangle2D.Double(transX1-2, transY1-2, 4.0, 4.0);
        }

        if (this.plotLines) {
            if (item>0) {
                // get the previous data point...
                Number x0 = data.getXValue(series, item-1);
                Number y0 = data.getYValue(series, item-1);
                double transX0 = horizontalAxis.translateValueToJava2D(x0.doubleValue(), dataArea);
                double transY0 = verticalAxis.translateValueToJava2D(y0.doubleValue(), dataArea);

                line.setLine(transX0, transY0, transX1, transY1);
                g2.draw(line);
            }
        }

        // Check if the item is the last item for the series.
        // and number of items > 0.  We can't draw an area for a single point.
        if (this.plotArea && item>0 && item==(itemCount-1)) {

            double transY2 = verticalAxis.translateValueToJava2D(0.0, dataArea);

            // Add the last point (x,0)
            pArea.addPoint((int)transX1, (int)transY2);

            // fill the polygon
            g2.fill(pArea);

            // draw an outline around the Area.
            if (showOutline) {
                g2.setStroke(plot.outlineStroke);
                g2.setPaint(plot.outlinePaint);
                g2.draw(pArea);
            }
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

        // collect entity and tool tip information...
        if (this.info!=null) {
            EntityCollection entities = this.info.getEntityCollection();
            if (entities!=null && shape!=null) {
                String tip="";
                if (this.toolTipGenerator!=null) {
                    tip = this.toolTipGenerator.generateToolTip(data, series, item);
                }
                XYItemEntity entity = new XYItemEntity(shape, tip, series, item);
                entities.addEntity(entity);
            }
        }

    }

}
