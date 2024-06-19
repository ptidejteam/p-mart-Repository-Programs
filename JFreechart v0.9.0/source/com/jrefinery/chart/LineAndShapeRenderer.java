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
 * -------------------------
 * LineAndShapeRenderer.java
 * -------------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Mark Watson (www.markwatson.com);
 *                   Jeremy Bowman;
 *
 * $Id: LineAndShapeRenderer.java,v 1.1 2007/10/10 19:01:18 vauchers Exp $
 *
 * Changes
 * -------
 * 23-Oct-2001 : Version 1 (DG);
 * 15-Nov-2001 : Modified to allow for null data values (DG);
 * 16-Jan-2002 : Renamed HorizontalCategoryItemRenderer.java --> CategoryItemRenderer.java (DG);
 * 05-Feb-2002 : Changed return type of the drawCategoryItem method from void to Shape, as part
 *               of the tooltips implementation (DG);
 * 11-May-2002 : Support for value label drawing (JB);
 * 29-May-2002 : Now extends AbstractCategoryItemRenderer (DG);
 *
 */

package com.jrefinery.chart;

import com.jrefinery.chart.ChartRenderingInfo;
import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.entity.CategoryItemEntity;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.ui.RefineryUtilities;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;

/**
 * A renderer for a CategoryPlot that draws shapes for each data item, and lines between data items.
 * The renderer is immutable so that the only way to change the renderer for a plot is to call the
 * setRenderer() method.
 */
public class LineAndShapeRenderer extends AbstractCategoryItemRenderer {

    /** Useful constant for specifying the type of rendering (shapes only). */
    public static final int SHAPES = 1;

    /** Useful constant for specifying the type of rendering (lines only). */
    public static final int LINES = 2;

    /** Useful constant for specifying the type of rendering (shapes and lines). */
    public static final int SHAPES_AND_LINES = 3;

    /** Constant indicating that labels are to be shown above data points */
    public static final int TOP = 1;

    /** Constant indicating that labels are to be shown below data points */
    public static final int BOTTOM = 2;

    /** Constant indicating that labels are to be shown left of data points */
    public static final int LEFT = 3;

    /** Constant indicating that labels are to be shown right of data points */
    public static final int RIGHT = 4;

    /** A flag indicating whether or not shapes are drawn at each XY point. */
    protected boolean plotShapes;

    /** A flag indicating whether or not lines are drawn between XY points. */
    protected boolean plotLines;

    /** Scale factor for standard shapes. */
    protected double shapeScale = 6;

    /** Location of labels (if shown) relative to the data points. */
    protected int labelPosition;

    /**
     * Constructs a default renderer (draws shapes and lines).
     */
    public LineAndShapeRenderer() {
        this(SHAPES_AND_LINES, TOP);
    }

    /**
     * Constructs a renderer of the specified type.
     * <P>
     * Use one of the constants SHAPES, LINES or SHAPES_AND_LINES.
     *
     * @param type The type of renderer.
     */
    public LineAndShapeRenderer(int type) {
        this(type, TOP);
    }

    /**
     * Constructs a renderer of the specified type.
     * <P>
     * Use one of the constants SHAPES, LINES or SHAPES_AND_LINES.
     *
     * @param type The type of renderer.
     * @param labelPosition Location of labels (if shown) relative to the data points
     *                      (TOP, BOTTOM, LEFT, or RIGHT).
     */
    public LineAndShapeRenderer(int type, int labelPosition) {
        if (type==SHAPES) this.plotShapes=true;
        if (type==LINES) this.plotLines=true;
        if (type==SHAPES_AND_LINES) {
            this.plotShapes = true;
            this.plotLines = true;
        }
        this.labelPosition = labelPosition;
    }

    /**
     * Returns the area that the axes must fit into.  Often this is the same as the plotArea, but
     * sometimes a smaller region should be used (for example, the 3D charts require the axes to
     * use less space in order to leave room for the 'depth' part of the chart).
     */
    public Rectangle2D getAxisArea(Rectangle2D plotArea) {
        return plotArea;
    }

    /**
     * Draws a horizontal line across the chart to represent the marker.
     *
     * @param g2 The graphics device.
     * @param plot The plot.
     * @param axis The value axis.
     * @param marker The marker line.
     * @param axisDataArea The axis data area.
     * @param dataClipRegion The data clip region.
     */
    public void drawRangeMarker(Graphics2D g2,
                                CategoryPlot plot, ValueAxis axis, Marker marker,
                                Rectangle2D axisDataArea, Shape dataClipRegion) {

        double y = axis.translateValueToJava2D(marker.getValue(), axisDataArea);
        Line2D line = new Line2D.Double(axisDataArea.getMinX(), y,
                                        axisDataArea.getMaxX(), y);
        g2.setPaint(marker.getOutlinePaint());
        g2.draw(line);

    }

    /**
     * Draw a single data item.
     *
     * @param g2 The graphics device.
     * @param dataArea The area in which the data is drawn.
     * @param plot The plot.
     * @param axis The range axis.
     * @param data The data.
     * @param series The series number (zero-based index).
     * @param category The category.
     * @param categoryIndex The category number (zero-based index).
     * @param previousCategory The previous category (will be null when the first category is
     *                         drawn).
     */
    public void drawCategoryItem(Graphics2D g2, Rectangle2D dataArea,
                                 CategoryPlot plot,
                                 ValueAxis axis,
                                 CategoryDataset data,
                                 int series,
                                 Object category, int categoryIndex, Object previousCategory) {

        // first check the number we are plotting...
        Number value = data.getValue(series, category);
        if (value!=null) {

            // current data point...
            double x1 = plot.getCategoryCoordinate(categoryIndex, dataArea);
            double y1 = axis.translateValueToJava2D(value.doubleValue(), dataArea);

            g2.setPaint(plot.getSeriesPaint(series));
            g2.setStroke(plot.getSeriesStroke(series));

            Shape shape = null;
            if (this.plotShapes) {
                shape = plot.getShape(series, category, x1, y1, shapeScale);
                g2.fill(shape);
            }
            else {
                shape = new Rectangle2D.Double(x1-2, y1-2, 4.0, 4.0);
            }

            if (this.plotLines) {
                if (previousCategory!=null) {

                    Number previousValue = data.getValue(series, previousCategory);
                    if (previousValue!=null) {

                        // previous data point...
                        double previous = previousValue.doubleValue();
                        double x0 = plot.getCategoryCoordinate(categoryIndex-1, dataArea);
                        double y0 = axis.translateValueToJava2D(previous, dataArea);

                        g2.setPaint(plot.getSeriesPaint(series));
                        g2.setStroke(plot.getSeriesStroke(series));
                        Line2D line = new Line2D.Double(x0, y0, x1, y1);
                        g2.draw(line);
                    }

                }
            }

            if (plot.getLabelsVisible()) {
                NumberFormat formatter = plot.getLabelFormatter();
                Font labelFont = plot.getLabelFont();
                g2.setFont(labelFont);
                Paint paint = plot.getLabelPaint();
                g2.setPaint(paint);
                boolean rotate = plot.getVerticalLabels();
                String label = formatter.format(value);
                drawLabel(g2, label, x1, y1, labelFont, rotate);
            }

            // collect entity and tool tip information...
            if (this.info!=null) {
                EntityCollection entities = this.info.getEntityCollection();
                if (entities!=null && shape!=null) {
                    String tip="";
                    if (this.toolTipGenerator!=null) {
                        tip = this.toolTipGenerator.generateToolTip(data, series, category);
                    }
                    CategoryItemEntity entity = new CategoryItemEntity(shape, tip, series, category);
                    entities.addEntity(entity);
                }
            }

        }

    }

    /**
     * Draws a value label on the plot.
     *
     * @param g2 The graphics device.
     * @param label The label text.
     * @param x The x position of the data point.
     * @param x The y position of the data point.
     * @param font The font to draw the label with.
     * @param rotate True if the label is to be rotated 90 degrees, false otherwise
     */
    private void drawLabel(Graphics2D g2, String label, double x, double y,
                           Font labelFont, boolean rotate) {

         FontRenderContext frc = g2.getFontRenderContext();
         Rectangle2D labelBounds = labelFont.getStringBounds(label, frc);
         LineMetrics lm = labelFont.getLineMetrics(label, frc);
         float lead = lm.getLeading();
         double width = labelBounds.getWidth();
         double height = labelBounds.getHeight();
         float labelx;
         float labely;
         int position = labelPosition;
         if (rotate) {
             if (position==TOP) {
                 labelx = (float)(x+height/2-lm.getDescent());
                 labely = (float)(y-shapeScale);
             }
             else if (position==BOTTOM) {
                 labelx = (float)(x+height/2-lm.getDescent());
                 labely = (float)(y+shapeScale+width);
             }
             else if (position==LEFT) {
                 labelx = (float)(x-shapeScale/2-lead-lm.getDescent());
                 labely = (float)(y+width/2);
             }
             else {
                 labelx = (float)(x+shapeScale/2+lead+lm.getAscent());
                 labely = (float)(y+width/2);
             }
             RefineryUtilities.drawRotatedString(label, g2, labelx, labely,
                                                 -Math.PI/2);
         }
         else {
             if (position==TOP) {
                 labelx = (float)(x-width/2);
                 labely = (float)(y-shapeScale/2-lm.getDescent()-lead);
             }
             else if (position==BOTTOM) {
                 labelx = (float)(x-width/2);
                 labely = (float)(y+shapeScale/2+lm.getAscent()+lead);
             }
             else if (position==LEFT) {
                 labelx = (float)(x-shapeScale-width);
                 labely = (float)(y+height/2-lm.getDescent());
             }
             else {
                 labelx = (float)(x+shapeScale);
                 labely = (float)(y+height/2-lm.getDescent());
             }
             g2.drawString(label, labelx, labely);
         }

    }

}
