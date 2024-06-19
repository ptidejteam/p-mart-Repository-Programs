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
 * ------------------------
 * IntervalBarRenderer.java
 * ------------------------
 * (C) Copyright 2002, 2003 by Jeremy Bowman.
 *
 * Original Author:  Jeremy Bowman;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: IntervalBarRenderer.java,v 1.1 2007/10/10 20:07:35 vauchers Exp $
 *
 * Changes
 * -------
 * 29-Apr-2002 : Version 1, contributed by Jeremy Bowman (DG);
 * 11-May-2002 : Use CategoryPlot.getLabelsVisible() (JB);
 * 29-May-2002 : Added constructors (DG);
 * 26-Jun-2002 : Added axis to initialise method (DG);
 * 20-Sep-2002 : Added basic support for chart entities (DG);
 * 24-Oct-2002 : Amendments for changes in CategoryDataset interface and CategoryToolTipGenerator
 *               interface (DG);
 * 05-Nov-2002 : Base dataset is now TableDataset not CategoryDataset (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 *
 */

package org.jfree.chart.renderer;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.Marker;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.data.CategoryDataset;
import org.jfree.data.IntervalCategoryDataset;
import org.jfree.data.Range;

/**
 * A renderer that handles the drawing of bars for a vertical bar plot where
 * each bar has a high and low value.
 * <p>
 * For use with the {@link org.jfree.chart.plot.CategoryPlot} class.
 *
 * @author Jeremy Bowman
 */
public class IntervalBarRenderer extends BarRenderer
                                         implements CategoryItemRenderer, Serializable {

    /** Constant indicating a low value label */
    private static final int LOW_LABEL = 0;

    /** Constant indicating a high value label */
    private static final int HIGH_LABEL = 1;

    /**
     * Constructs a new renderer.
     */
    public IntervalBarRenderer() {
        this(null, null);
    }

    /**
     * Constructs a new renderer.
     *
     * @param toolTipGenerator  the tool tip generator.
     */
    public IntervalBarRenderer(CategoryItemLabelGenerator toolTipGenerator) {
        this(toolTipGenerator, null);
    }

    /**
     * Constructs a new renderer.
     *
     * @param urlGenerator  the url generator.
     */
    public IntervalBarRenderer(CategoryURLGenerator urlGenerator) {
        this(null, urlGenerator);
    }

    /**
     * Constructs a new renderer.
     *
     * @param toolTipGenerator  the tool tip generator.
     * @param urlGenerator  the url generator.
     */
    public IntervalBarRenderer(CategoryItemLabelGenerator toolTipGenerator,
                                       CategoryURLGenerator urlGenerator) {

        super(toolTipGenerator, urlGenerator);

    }

    /**
     * Draws a marker for the range axis.
     * <P>
     * A marker is a constant value, usually represented by a line.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the range axis.
     * @param marker  the marker to be drawn.
     * @param axisDataArea  the area inside the axes.
     * @param dataClipRegion  the data clip area.
     */
    public void drawRangeMarker(Graphics2D g2,
                                CategoryPlot plot, ValueAxis axis, Marker marker,
                                Rectangle2D axisDataArea, Shape dataClipRegion) {

        double value = marker.getValue();
        Range range = axis.getRange();
        if (!range.contains(value)) {
            return;
        }

        double y = axis.translateValueToJava2D(marker.getValue(), axisDataArea,
                                               plot.getRangeAxisLocation());
        Line2D line = new Line2D.Double(axisDataArea.getMinX(), y,
                                        axisDataArea.getMaxX(), y);
        g2.setPaint(marker.getOutlinePaint());
        g2.draw(line);

    }

    /**
     * Draws the bar for a single (series, category) data item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param data  the data.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    public void drawItem(Graphics2D g2,
                         Rectangle2D dataArea,
                         CategoryPlot plot,
                         CategoryAxis domainAxis,
                         ValueAxis rangeAxis,
                         CategoryDataset data,
                         int row,
                         int column) {

        IntervalCategoryDataset intervalData = (IntervalCategoryDataset) data;

        int seriesCount = getRowCount();
        int categoryCount = getColumnCount();

        PlotOrientation orientation = plot.getOrientation();
        
        double rectX = 0.0;
        double rectY = 0.0;

        AxisLocation domainAxisLocation = plot.getDomainAxisLocation();
        AxisLocation rangeAxisLocation = plot.getRangeAxisLocation();
        
        // Y0
        Number value0 = intervalData.getEndValue(row, column);
        double java2dValue0 = rangeAxis.translateValueToJava2D(value0.doubleValue(), dataArea, 
                                                               rangeAxisLocation);

        // Y1
        Number value1 = intervalData.getStartValue(row, column);
        double java2dValue1 = rangeAxis.translateValueToJava2D(value1.doubleValue(), dataArea, 
                                                               rangeAxisLocation);

        if (java2dValue1 < java2dValue0) {
            double temp = java2dValue1;
            java2dValue1 = java2dValue0;
            java2dValue0 = temp;
            Number tempNum = value1;
            value1 = value0;
            value0 = tempNum;
        }

        // BAR WIDTH
        double rectWidth = getBarWidth();

        // BAR HEIGHT
        double rectHeight = Math.abs(java2dValue1 - java2dValue0);

        if (orientation == PlotOrientation.HORIZONTAL) {
            // BAR Y
            rectY = domainAxis.getCategoryStart(column, getColumnCount(), dataArea, 
                                                domainAxisLocation);
            if (seriesCount > 1) {
                double seriesGap = dataArea.getHeight() * getItemMargin()
                                   / (categoryCount * (seriesCount - 1));
                rectY = rectY + row * (getBarWidth() + seriesGap);
            }
            else {
                rectY = rectY + row * getBarWidth();
            }
            
            rectX = java2dValue0;

            rectHeight = getBarWidth();
            rectWidth = Math.abs(java2dValue1 - java2dValue0);

        }
        else if (orientation == PlotOrientation.VERTICAL) {
            // BAR X
            rectX = domainAxis.getCategoryStart(column, getColumnCount(), dataArea,
                                                domainAxisLocation);

            if (seriesCount > 1) {
                double seriesGap = dataArea.getWidth() * getItemMargin()
                                   / (categoryCount * (seriesCount - 1));
                rectX = rectX + row * (getBarWidth() + seriesGap);
            }
            else {
                rectX = rectX + row * getBarWidth();
            }

            rectY = java2dValue0;

        }
        Rectangle2D bar = new Rectangle2D.Double(rectX, rectY, rectWidth, rectHeight);
        Paint seriesPaint = getItemPaint(row, column);
        g2.setPaint(seriesPaint);
        g2.fill(bar);
        
        // draw the outline...
        if (getBarWidth() > BAR_OUTLINE_WIDTH_THRESHOLD) {
            Stroke stroke = getItemOutlineStroke(row, column);
            Paint paint = getItemOutlinePaint(row, column);
            if (stroke != null && paint != null) {
                g2.setStroke(stroke);
                g2.setPaint(paint);
                g2.draw(bar);
            }
        }
        
        CategoryItemLabelGenerator generator = getItemLabelGenerator(row, column);
        if (generator != null && isItemLabelVisible(row, column)) {
            drawItemLabel(g2, data, row, column, plot, generator, bar, false);
        }        

        // collect entity and tool tip information...
        if (getInfo() != null) {
            EntityCollection entities = getInfo().getEntityCollection();
            if (entities != null) {
                String tip = null;
                if (generator != null) {
                    tip = generator.generateToolTip(data, row, column);
                }
                String url = null;
                if (getURLGenerator() != null) {
                    url = getURLGenerator().generateURL(data, row, column);
                }
                CategoryItemEntity entity
                    = new CategoryItemEntity(bar, tip, url, row, data.getColumnKey(column), column);
                entities.addEntity(entity);
            }
        }

    }

//    /**
//     * Draws a value label on the plot.
//     *
//     * @param g2  the graphics device.
//     * @param label  the label text.
//     * @param labelArea  the area in which to draw the label (it may extend beyond the sides of
//     *                   this rectangle).
//     * @param labelFont  the font to draw the label with.
//     * @param labelType  HIGH_LABEL or LOW_LABEL; determines how to position the label in the
//     *                   provided area.
//     * @param rotate  if <code>true</code> rotate the label 90 degrees.
//     */
//    private void drawLabel(Graphics2D g2, String label, Rectangle2D labelArea,
//                           Font labelFont, int labelType, boolean rotate) {
//
//         FontRenderContext frc = g2.getFontRenderContext();
//         Rectangle2D labelBounds = labelFont.getStringBounds(label, frc);
//         LineMetrics lm = labelFont.getLineMetrics(label, frc);
//         float labelx = (float) labelArea.getCenterX();
//         float labely;
//         if (rotate) {
//             labelx += (float) (labelBounds.getHeight() / 2 - lm.getDescent());
//             if (labelType == HIGH_LABEL) {
//                 labely = (float) (labelArea.getMaxY() - lm.getLeading());
//             }
//             else {
//                 labely = (float) (labelArea.getY() + labelBounds.getWidth()
//                                                    + lm.getLeading());
//             }
//             RefineryUtilities.drawRotatedString(label, g2, labelx, labely, -Math.PI / 2);
//         }
//         else {
//             labelx -= (float) (labelBounds.getWidth() / 2);
//             if (labelType == HIGH_LABEL) {
//                 labely = (float) (labelArea.getMaxY() - lm.getDescent()
//                                                       - lm.getLeading());
//             }
//             else {
//                 labely = (float) (labelArea.getY() + lm.getAscent()
//                                                    + lm.getLeading());
//             }
//             g2.drawString(label, labelx, labely);
//         }
//
//    }

}
