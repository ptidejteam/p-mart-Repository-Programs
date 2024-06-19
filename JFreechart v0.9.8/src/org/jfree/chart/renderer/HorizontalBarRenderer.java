/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * HorizontalBarRenderer.java
 * --------------------------
 * (C) Copyright 2001-2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Tin Luu;
 *                   Richard Atkinson;
 *                   Rich Unger
 *
 * $Id: HorizontalBarRenderer.java,v 1.1 2007/10/10 20:03:12 vauchers Exp $
 *
 * Changes
 * -------
 * 22-Oct-2001 : Version 1 (DG);
 *               Renamed DataSource.java --> Dataset.java etc. (DG);
 * 23-Oct-2001 : Changed intro and trail gaps on bar plots to use percentage of available space
 *               rather than a fixed number of units (DG);
 * 31-Oct-2001 : Debug for gaps (DG);
 * 15-Nov-2001 : Modified to allow for null values (DG);
 * 13-Dec-2001 : Changed drawBar(...) method to return a Shape (that can be used for tooltips) (DG);
 * 16-Jan-2002 : Updated Javadoc comments (DG);
 * 15-Feb-2002 : Added isStacked() method to allow the plot to alter the method of finding the
 *               minimum and maximum data values (DG);
 * 14-Mar-2002 : Modified this class to implement the CategoryItemRenderer interface (DG);
 * 24-May-2002 : Incorporated tooltips into chart entities (DG);
 * 13-Jun-2002 : Added check to make sure marker is visible before drawing it (DG);
 * 19-Jun-2002 : Added code to draw labels on bars (TL);
 * 26-Jun-2002 : Added axis to initialise method, and implemented bar clipping to avoid
 *               PRExceptions (DG);
 * 05-Aug-2002 : Small modification to drawCategoryItem method to support URLs for HTML image
 *               maps (RA);
 * 20-Aug-2002 : Updated drawRangeMarker method (DG);
 * 20-Sep-2002 : Added fix by Rich Unger for categoryPaint, and fixed errors reported by
 *               Checkstyle (DG);
 * 24-Oct-2002 : Amendments for changes in CategoryDataset interface and CategoryToolTipGenerator
 *               interface (DG);
 * 05-Nov-2002 : Base dataset is now TableDataset not CategoryDataset (DG);
 * 20-Nov-2002 : Changed signature of drawItem(...) method to reflect use of TableDataset (DG);
 * 10-Jan-2003 : Moved get/setItemMargin methods to BarRenderer class (DG);
 * 17-Jan-2003 : Moved plot classes to a separate package (DG);
 * 22-Jan-2003 : Fixed bug 672713, thanks to Gareth Cronin (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 10-Apr-2003 : Removed category paint usage (DG);
 * 10-Apr-2003 : Changed CategoryDataset to KeyedValues2DDataset in drawItem(...) method (DG);
 */

package org.jfree.chart.renderer;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.text.NumberFormat;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.Marker;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.tooltips.CategoryToolTipGenerator;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.data.CategoryDataset;
import org.jfree.data.Range;

/**
 * A renderer that handles the drawing of bars for a horizontal bar plot.
 * <p>
 * For use with the {@link org.jfree.chart.plot.HorizontalCategoryPlot} class.
 *
 * @author David Gilbert
 */
public class HorizontalBarRenderer extends BarRenderer 
                                   implements CategoryItemRenderer, Serializable {

    /**
     * Creates a new renderer with no tool tip generator and no URL generator.
     * <P>
     * The defaults (no tool tip or URL generators) have been chosen to minimise the processing
     * required to generate a default chart.  If you require tool tips or URLs, then you can
     * easily add the required generators.
     */
    public HorizontalBarRenderer() {
        this(null, null);
    }

    /**
     * Creates a new renderer with the specified tooltip generator but no URL generator.
     *
     * @param toolTipGenerator  the tool tip generator.
     */
    public HorizontalBarRenderer(CategoryToolTipGenerator toolTipGenerator) {
        this(toolTipGenerator, null);
    }

    /**
     * Creates a new renderer with the specified URL generator but no tooltip generator.
     *
     * @param urlGenerator  the URL generator.
     */
    public HorizontalBarRenderer(CategoryURLGenerator urlGenerator) {
        this(null, urlGenerator);
    }

    /**
     * Creates a new renderer with the specified tooltip generator and URL generator.
     *
     * @param toolTipGenerator  the tool tip generator.
     * @param urlGenerator  the URL generator.
     */
    public HorizontalBarRenderer(CategoryToolTipGenerator toolTipGenerator,
                                 CategoryURLGenerator urlGenerator) {

        super(toolTipGenerator, urlGenerator);

    }

    /**
     * Initialises the renderer.
     * <p>
     * This method gets called once at the start of the process of drawing a chart.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param plot  the plot.
     * @param info  optional information collection.
     */
    public void initialise(Graphics2D g2,
                           Rectangle2D dataArea,
                           CategoryPlot plot,
                           ChartRenderingInfo info) {

        super.initialise(g2, dataArea, plot, info);

        // calculate the bar width
        CategoryAxis domainAxis = plot.getDomainAxis();
		CategoryDataset data = plot.getCategoryDataset();
        if (data != null) {
            int columns = data.getColumnCount();
            int rows = data.getRowCount();
            double cm = 0.0;
            double im = 0.0;
            if (columns > 1) {
                cm = domainAxis.getCategoryMargin();
            }
            if (rows > 1) {
                im = getItemMargin();
            }
            double used = dataArea.getHeight() * (1 - domainAxis.getLowerMargin()
                                                    - domainAxis.getUpperMargin()
                                                    - cm - im);
            if ((rows * columns) > 0) {
                setBarWidth(used / (data.getColumnCount() * data.getRowCount()));
            }
            else {
                setBarWidth(used);
            }
        }

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
     * Draws a vertical line across the chart to represent the marker.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param marker  the marker line.
     * @param dataArea  the axis data area.
     */
    public void drawRangeMarker(Graphics2D g2,
                                CategoryPlot plot,
                                ValueAxis axis,
                                Marker marker,
                                Rectangle2D dataArea) {

        double value = marker.getValue();
        Range range = axis.getRange();

        if (!range.contains(value)) {
            return;
        }

        double x = axis.translateValueToJava2D(marker.getValue(), dataArea);
        Line2D line = new Line2D.Double(x, dataArea.getMinY(),
                                        x, dataArea.getMaxY());
        Paint paint = marker.getOutlinePaint();
        Stroke stroke = marker.getOutlineStroke();
        g2.setPaint(paint != null ? paint : Plot.DEFAULT_OUTLINE_PAINT);
        g2.setStroke(stroke != null ? stroke : Plot.DEFAULT_OUTLINE_STROKE);
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

        // plot non-null values...
        Number dataValue = data.getValue(row, column);
        if (dataValue != null) {

            // X
            double value = dataValue.doubleValue();
            double base = 0.0;
            double lclip = getLowerClip();
            double uclip = getUpperClip();
            if (uclip <= 0.0) {  // cases 1, 2, 3 and 4
                if (value >= uclip) {
                    return; // bar is not visible
                }
                base = uclip;
                if (value <= lclip) {
                    value = lclip;
                }
            }
            else if (lclip <= 0.0) { // cases 5, 6, 7 and 8
                if (value >= uclip) {
                    value = uclip;
                }
                else {
                    if (value <= lclip) {
                        value = lclip;
                    }
                }
            }
            else { // cases 9, 10, 11 and 12
                if (value <= lclip) {
                    return; // bar is not visible
                }
                base = lclip;
                if (value >= uclip) {
                    value = uclip;
                }
            }

            double transX1 = rangeAxis.translateValueToJava2D(base, dataArea);
            double transX2 = rangeAxis.translateValueToJava2D(value, dataArea);
            double rectX = Math.min(transX1, transX2);
            double rectWidth = Math.abs(transX2 - transX1);

            // Y
            double rectY = domainAxis.getCategoryStart(column, getColumnCount(), dataArea);

            int seriesCount = getRowCount();
            int categoryCount = getColumnCount();
            if (seriesCount > 1) {
                double seriesGap = dataArea.getHeight() * getItemMargin()
                                   / (categoryCount * (seriesCount - 1));
                rectY = rectY + row * (getBarWidth() + seriesGap);
            }
            else {
                rectY = rectY + row * getBarWidth();
            }

            // draw the bar...
            double rectHeight = getBarWidth();
            Rectangle2D bar = new Rectangle2D.Double(rectX, rectY, rectWidth, rectHeight);

            // choose the color...
//            Paint itemPaint = null;
//            if (!getUseCategoriesPaint()) {
//                itemPaint = getItemPaint(dataset, row, column);
//            }
//            else {
//                itemPaint = getCategoryPaint(column);
//            }
            g2.setPaint(getItemPaint(dataset, row, column));
            g2.fill(bar);

            // draw the outline...
            if (getBarWidth() > BAR_OUTLINE_WIDTH_THRESHOLD) {
                g2.setStroke(getItemStroke(dataset, row, column));
                g2.setPaint(getItemOutlinePaint(dataset, row, column));
                g2.draw(bar);
                if (plot.getValueLabelsVisible()) {
                    Font labelFont = plot.getValueLabelFont();
                    g2.setFont(labelFont);
                    Paint paint = plot.getValueLabelPaint();
                    g2.setPaint(paint);
                    NumberFormat formatter = plot.getValueLabelFormatter();
                    g2.drawString(formatter.format(dataValue),
                                  (int) (rectX + rectWidth * 0.90), (int) rectY - 5);
                }
            }

            // collect entity and tool tip information...
            if (getInfo() != null) {
                EntityCollection entities = getInfo().getEntityCollection();
                if (entities != null) {
                    String tip = null;
                    if (getToolTipGenerator() != null) {
                        tip = getToolTipGenerator().generateToolTip(data, row, column);
                    }
                    String url = null;
                    if (getURLGenerator() != null) {
                        url = getURLGenerator().generateURL(data, row, column);
                    }
                    CategoryItemEntity entity
                        = new CategoryItemEntity(bar, tip, url, row,
                                                 data.getColumnKey(column), column);
                    entities.addEntity(entity);
                }
            }

        }

    }

}
