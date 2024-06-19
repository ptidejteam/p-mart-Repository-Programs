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
 * ----------------------------
 * HorizontalShapeRenderer.java
 * ----------------------------
 * (C) Copyright 2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: HorizontalShapeRenderer.java,v 1.1 2007/10/10 20:00:08 vauchers Exp $
 *
 * Changes
 * -------
 * 15-Jul-2002 : Version 1, based on LineAndShapeRenderer (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 10-Oct-2002 : Added URL generator (DG);
 * 24-Oct-2002 : Amendments for changes in CategoryDataset interface and CategoryToolTipGenerator
 *               interface (DG);
 * 05-Nov-2002 : Base dataset is now TableDataset not CategoryDataset (DG);
 * 18-Nov-2002 : Added grid line methods (DG);
 * 17-Jan-2003 : Moved plot classes to a separate package (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 10-Apr-2003 : Changed CategoryDataset to KeyedValues2DDataset in drawItem(...) method (DG);
 *
 */

package com.jrefinery.chart.renderer;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.text.NumberFormat;

import com.jrefinery.chart.Marker;
import com.jrefinery.chart.axis.CategoryAxis;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.entity.CategoryItemEntity;
import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.plot.CategoryPlot;
import com.jrefinery.chart.plot.Plot;
import com.jrefinery.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.chart.urls.CategoryURLGenerator;
import com.jrefinery.data.KeyedValues2DDataset;
import com.jrefinery.data.Range;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A renderer that draws shapes for each data item, and lines between data items.
 * <p>
 * For use with the {@link com.jrefinery.chart.plot.HorizontalCategoryPlot} class.
 *
 * @author David Gilbert
 */
public class HorizontalShapeRenderer extends AbstractCategoryItemRenderer 
                                     implements Serializable {

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
    private boolean plotShapes;

    /** A flag indicating whether or not lines are drawn between XY points. */
    private boolean plotLines;

    /** Scale factor for standard shapes. */
    private double shapeScale = 10;

    /** Location of labels (if shown) relative to the data points. */
    private int labelPosition;

    /**
     * Constructs a default renderer (draws shapes and lines).
     */
    public HorizontalShapeRenderer() {
        this(SHAPES, RIGHT);
    }

    /**
     * Constructs a renderer of the specified type.
     * <P>
     * Use one of the constants SHAPES, LINES or SHAPES_AND_LINES.
     *
     * @param type  the type of renderer.
     */
    public HorizontalShapeRenderer(int type) {
        this(type, RIGHT);
    }

    /**
     * Constructs a renderer of the specified type.
     * <P>
     * Use one of the constants SHAPES, LINES or SHAPES_AND_LINES.
     *
     * @param type  the type of renderer.
     * @param labelPosition  location of labels (if shown) relative to the data points
     *                       (TOP, BOTTOM, LEFT, or RIGHT).
     */
    public HorizontalShapeRenderer(int type, int labelPosition) {
        this(type, labelPosition, null, null);
    }

    /**
     * Constructs a renderer of the specified type.
     * <P>
     * Use one of the constants SHAPES, LINES or SHAPES_AND_LINES.
     *
     * @param type  the type of renderer.
     * @param labelPosition  location of labels (if shown) relative to the data points
     *                       (TOP, BOTTOM, LEFT, or RIGHT).
     * @param toolTipGenerator  the tool tip generator (null permitted).
     * @param urlGenerator  the URL generator (null permitted).
     */
    public HorizontalShapeRenderer(int type, int labelPosition,
                                   CategoryToolTipGenerator toolTipGenerator,
                                   CategoryURLGenerator urlGenerator) {

        super(toolTipGenerator, urlGenerator);

        if (type == SHAPES) {
            this.plotShapes = true;
        }

        if (type == LINES) {
            this.plotLines = true;
        }

        if (type == SHAPES_AND_LINES) {
            this.plotShapes = true;
            this.plotLines = true;
        }

        this.labelPosition = labelPosition;
    }

    /**
     * Draws a grid line against the domain axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the area for plotting data (not yet adjusted for any 3D effect).
     * @param value  the Java2D value at which the grid line should be drawn.
     *
     */
    public void drawDomainGridline(Graphics2D g2,
                                   CategoryPlot plot,
                                   Rectangle2D dataArea,
                                   double value) {

        Line2D line = new Line2D.Double(dataArea.getMinX(), value,
                                        dataArea.getMaxX(), value);
        Paint paint = plot.getDomainGridlinePaint();
        Stroke stroke = plot.getDomainGridlineStroke();
        g2.setPaint(paint != null ? paint : Plot.DEFAULT_OUTLINE_PAINT);
        g2.setStroke(stroke != null ? stroke : Plot.DEFAULT_OUTLINE_STROKE);
        g2.draw(line);

    }

    /**
     * Draws a grid line against the range axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param dataArea  the area for plotting data (not yet adjusted for any 3D effect).
     * @param value  the value at which the grid line should be drawn.
     *
     */
    public void drawRangeGridline(Graphics2D g2,
                                  CategoryPlot plot,
                                  ValueAxis axis,
                                  Rectangle2D dataArea,
                                  double value) {

        Range range = axis.getRange();

        if (!range.contains(value)) {
            return;
        }

        double x = axis.translateValueToJava2D(value, dataArea);
        Line2D line = new Line2D.Double(x, dataArea.getMinY(),
                                        x, dataArea.getMaxY());
        Paint paint = plot.getRangeGridlinePaint();
        Stroke stroke = plot.getRangeGridlineStroke();
        g2.setPaint(paint != null ? paint : Plot.DEFAULT_OUTLINE_PAINT);
        g2.setStroke(stroke != null ? stroke : Plot.DEFAULT_OUTLINE_STROKE);
        g2.draw(line);

    }

    /**
     * Draws a vertical line on the chart to represent the marker.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param marker  the marker line.
     * @param axisDataArea  the axis data area.
     * @param dataClipRegion  the data clip region.
     */
    public void drawRangeMarker(Graphics2D g2,
                                CategoryPlot plot, ValueAxis axis, Marker marker,
                                Rectangle2D axisDataArea, Shape dataClipRegion) {

        double value = marker.getValue();
        Range range = axis.getRange();
        if (!range.contains(value)) {
            return;
        }

        double x = axis.translateValueToJava2D(marker.getValue(), axisDataArea);
        Line2D line = new Line2D.Double(x, axisDataArea.getMinY(),
                                        x, axisDataArea.getMaxY());
        g2.setPaint(marker.getOutlinePaint());
        g2.draw(line);

    }

    /**
     * Draw a single data item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area in which the data is drawn.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param data  the data.
     * @param dataset  the dataset index (zero-based).
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    public void drawItem(Graphics2D g2, Rectangle2D dataArea,
                         CategoryPlot plot,
                         CategoryAxis domainAxis,
                         ValueAxis rangeAxis,
                         KeyedValues2DDataset data,
                         int dataset,
                         int row,
                         int column) {

        // first check the number we are plotting...
        Number value = data.getValue(row, column);
        if (value != null) {

            // current data point...
            double x1 = rangeAxis.translateValueToJava2D(value.doubleValue(), dataArea);
            double y1 = domainAxis.getCategoryMiddle(column, getColumnCount(), dataArea);

            g2.setPaint(getItemPaint(dataset, row, column));
            g2.setStroke(getItemStroke(dataset, row, column));

            Shape shape = null;
            if (this.plotShapes) {
                shape = getItemShape(dataset, row, column);
                shape = createTransformedShape(shape, x1, y1);
                g2.fill(shape);
            }
            else {
                shape = new Rectangle2D.Double(x1 - 2, y1 - 2, 4.0, 4.0);
            }

            if (this.plotLines) {
                if (column != 0) {

                    Number previousValue = data.getValue(row, column - 1);
                    if (previousValue != null) {

                        // previous data point...
                        double previous = previousValue.doubleValue();
                        double x0 = rangeAxis.translateValueToJava2D(previous, dataArea);
                        double y0 = domainAxis.getCategoryStart(column - 1, getColumnCount(),
                                                                dataArea);

                        g2.setPaint(getItemPaint(dataset, row, column));
                        g2.setStroke(getItemStroke(dataset, row, column));
                        Line2D line = new Line2D.Double(x0, y0, x1, y1);
                        g2.draw(line);
                    }

                }
            }

            if (plot.getValueLabelsVisible()) {
                NumberFormat formatter = plot.getValueLabelFormatter();
                Font labelFont = plot.getValueLabelFont();
                g2.setFont(labelFont);
                Paint paint = plot.getValueLabelPaint();
                g2.setPaint(paint);
                boolean rotate = plot.getVerticalValueLabels();
                String label = formatter.format(value);
                drawLabel(g2, label, x1, y1, labelFont, rotate);
            }

            // collect entity and tool tip information...
            if (getInfo() != null) {
                EntityCollection entities = getInfo().getEntityCollection();
                if (entities != null && shape != null) {
                    String tip = null;
                    if (getToolTipGenerator() != null) {
                        tip = getToolTipGenerator().generateToolTip(data, row, column);
                    }
                    String url = null;
                    if (getURLGenerator() != null) {
                        url = getURLGenerator().generateURL(data, row, column);
                    }

                    CategoryItemEntity entity = new CategoryItemEntity(shape, tip, row,
                                                           data.getColumnKey(column), column);
                    entities.addEntity(entity);
                }
            }

        }

    }

    /**
     * Draws a value label on the plot.
     *
     * @param g2  the graphics device.
     * @param label  the label text.
     * @param x  the x position of the data point.
     * @param y  the y position of the data point.
     * @param labelFont  the font to draw the label with.
     * @param rotate  true if the label is to be rotated 90 degrees, false otherwise.
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
             if (position == TOP) {
                 labelx = (float) (x + height / 2 - lm.getDescent());
                 labely = (float) (y - shapeScale);
             }
             else if (position == BOTTOM) {
                 labelx = (float) (x + height / 2 - lm.getDescent());
                 labely = (float) (y + shapeScale + width);
             }
             else if (position == LEFT) {
                 labelx = (float) (x - shapeScale / 2 - lead - lm.getDescent());
                 labely = (float) (y + width / 2);
             }
             else {
                 labelx = (float) (x + shapeScale / 2 + lead + lm.getAscent());
                 labely = (float) (y + width / 2);
             }
             RefineryUtilities.drawRotatedString(label, g2, labelx, labely, -Math.PI / 2);
         }
         else {
             if (position == TOP) {
                 labelx = (float) (x - width / 2);
                 labely = (float) (y - shapeScale / 2 - lm.getDescent() - lead);
             }
             else if (position == BOTTOM) {
                 labelx = (float) (x - width / 2);
                 labely = (float) (y + shapeScale / 2 + lm.getAscent() + lead);
             }
             else if (position == LEFT) {
                 labelx = (float) (x - shapeScale - width);
                 labely = (float) (y + height / 2 - lm.getDescent());
             }
             else {
                 labelx = (float) (x + shapeScale);
                 labely = (float) (y + height / 2 - lm.getDescent());
             }
             g2.drawString(label, labelx, labely);
         }

    }

}
