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
 * -------------------------
 * LineAndShapeRenderer.java
 * -------------------------
 * (C) Copyright 2001-2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Mark Watson (www.markwatson.com);
 *                   Jeremy Bowman;
 *                   Richard Atkinson;
 *
 * $Id: LineAndShapeRenderer.java,v 1.1 2007/10/10 19:57:50 vauchers Exp $
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
 * 25-Jun-2002 : Removed redundant import (DG);
 * 05-Aug-2002 : Small modification to drawCategoryItem method to support URLs for
 *               HTML image maps (RA);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 11-Oct-2002 : Added new constructor to incorporate tool tip and URL generators (DG);
 * 24-Oct-2002 : Amendments for changes in CategoryDataset interface and CategoryToolTipGenerator
 *               interface (DG);
 * 05-Nov-2002 : Base dataset is now TableDataset not CategoryDataset (DG);
 * 06-Nov-2002 : Renamed drawCategoryItem(...) --> drawItem(...) and now using axis for
 *               category spacing (DG);
 * 17-Jan-2003 : Moved plot classes to a separate package (DG);
 *
 */

package com.jrefinery.chart.renderer;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import com.jrefinery.chart.axis.CategoryAxis;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.entity.CategoryItemEntity;
import com.jrefinery.chart.plot.CategoryPlot;
import com.jrefinery.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.chart.urls.CategoryURLGenerator;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A renderer that draws shapes for each data item, and lines between data items.
 * <p>
 * For use with the {@link com.jrefinery.chart.plot.VerticalCategoryPlot} class.
 *
 * @author David Gilbert
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
    private boolean drawShapes;

    /** A flag indicating whether or not lines are drawn between XY points. */
    private boolean drawLines;

    /** Scale factor for standard shapes. */
    private double shapeScale = 6;

    /** Location of labels (if shown) relative to the data points. */
    private int labelPosition;

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
     * @param type  the type of renderer.
     */
    public LineAndShapeRenderer(int type) {
        this(type, TOP);
    }

    /**
     * Constructs a renderer of the specified type.
     * <P>
     * Use one of the constants SHAPES, LINES or SHAPES_AND_LINES.
     *
     * @param type  the type of renderer.
     * @param labelPosition  location of labels (if shown) relative to the data  points
     *                       (TOP, BOTTOM, LEFT, or RIGHT).
     */
    public LineAndShapeRenderer(int type, int labelPosition) {
        this(type, labelPosition, null, null);
    }

    /**
     * Constructs a renderer of the specified type.
     * <P>
     * Use one of the constants SHAPES, LINES or SHAPES_AND_LINES.
     *
     * @param type  the type of renderer.
     * @param labelPosition  location of labels (if shown) relative to the data  points
     *                       (TOP, BOTTOM, LEFT, or RIGHT).
     * @param toolTipGenerator  the tool tip generator (null permitted).
     * @param urlGenerator the URL generator (null permitted).
     */
    public LineAndShapeRenderer(int type, int labelPosition,
                                CategoryToolTipGenerator toolTipGenerator,
                                CategoryURLGenerator urlGenerator) {

        super(toolTipGenerator, urlGenerator);

        if (type == SHAPES) {
            this.drawShapes = true;
        }
        if (type == LINES) {
            this.drawLines = true;
        }
        if (type == SHAPES_AND_LINES) {
            this.drawShapes = true;
            this.drawLines = true;
        }
        this.labelPosition = labelPosition;

    }

    /**
     * Returns <code>true</code> if a shape should be drawn to represent each data point, and
     * <code>false</code> otherwise.
     * 
     * @return A boolean flag.
     */
    public boolean isDrawShapes() {
        return this.drawShapes;
    }

    /**
     * Sets the flag that controls whether or not a shape should be drawn to represent each data
     * point.
     * 
     * @param draw  the new value of the flag.
     */
    public void setDrawShapes(boolean draw) {
        if (draw != this.drawShapes) {
            this.drawShapes = draw;
            this.firePropertyChanged("Shapes", new Boolean(!draw), new Boolean(draw));
        }
    }

    /**
     * Returns <code>true</code> if a line should be drawn from the previous to the current data
     * point, and <code>false</code> otherwise.
     * 
     * @return A boolean flag.
     */
    public boolean isDrawLines() {
        return this.drawLines;
    }

    /**
     * Sets the flag that controls whether or not lines are drawn between consecutive data points.
     * 
     * @param draw  the new value of the flag.
     */
    public void setDrawLines(boolean draw) {
        if (draw != this.drawLines) {
            this.drawLines = draw;
            this.firePropertyChanged("Lines", new Boolean(!draw), new Boolean(draw));
        }
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
    public void drawItem(Graphics2D g2,
                         Rectangle2D dataArea,
                         CategoryPlot plot,
                         CategoryAxis domainAxis,
                         ValueAxis rangeAxis,
                         CategoryDataset data,
                         int dataset,
                         int row,
                         int column) {

        // first check the number we are plotting...
        Number value = data.getValue(row, column);
        if (value != null) {

            // current data point...
            double x1 = domainAxis.getCategoryMiddle(column, getColumnCount(), dataArea);
            double y1 = rangeAxis.translateValueToJava2D(value.doubleValue(), dataArea);

            g2.setPaint(getItemPaint(dataset, row, column));
            g2.setStroke(getItemStroke(dataset, row, column));

            Shape shape = getItemShape(dataset, row, column);
            shape = createTransformedShape(shape, x1, y1);
            if (this.drawShapes) {
                g2.fill(shape);
            }

            if (this.drawLines) {
                if (column != 0) {

                    Number previousValue = data.getValue(row, column - 1);
                    if (previousValue != null) {

                        // previous data point...
                        double previous = previousValue.doubleValue();
                        double x0 = domainAxis.getCategoryMiddle(column - 1,
                                                                 getColumnCount(), dataArea);
                        double y0 = rangeAxis.translateValueToJava2D(previous, dataArea);

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
                    CategoryItemEntity entity
                        = new CategoryItemEntity(shape, tip, url, row,
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
     * @param rotate  if <code>true</code> the label is will be rotated 90 degrees.
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
             RefineryUtilities.drawRotatedString(label, g2, labelx, labely,
                                                 -Math.PI / 2);
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
