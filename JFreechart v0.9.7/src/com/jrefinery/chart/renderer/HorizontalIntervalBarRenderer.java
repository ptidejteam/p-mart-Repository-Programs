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
 * ----------------------------------
 * HorizontalIntervalBarRenderer.java
 * ----------------------------------
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: HorizontalIntervalBarRenderer.java,v 1.1 2007/10/10 20:00:08 vauchers Exp $
 *
 * Changes
 * -------
 * 21-Mar-2002 : Version 1 (DG);
 * 29-May-2002 : Added constructors (DG);
 * 13-Jun-2002 : Added check to make sure marker is visible before drawing it (DG);
 * 18-Jun-2002 : Fixed bug in drawCategoryItem (occurs when there is just one category) (DG);
 * 26-Jun-2002 : Added axis to initialise method (DG);
 * 20-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 10-Oct-2002 : Added chart entity support (DG);
 * 24-Oct-2002 : Amendments for changes in CategoryDataset interface and CategoryToolTipGenerator
 *               interface (DG);
 * 05-Nov-2002 : The base dataset is now TableDataset not CategoryDataset (DG);
 * 10-Jan-2003 : Now handles both IntervalTableDataset and MultiIntervalTableDataset (DG);
 * 17-Jan-2003 : Moved plot classes to a separate package (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 *
 */

package com.jrefinery.chart.renderer;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import com.jrefinery.chart.Marker;
import com.jrefinery.chart.axis.CategoryAxis;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.entity.CategoryItemEntity;
import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.plot.CategoryPlot;
import com.jrefinery.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.chart.urls.CategoryURLGenerator;
import com.jrefinery.data.IntervalCategoryDataset;
import com.jrefinery.data.KeyedValues2DDataset;
import com.jrefinery.data.MultiIntervalCategoryDataset;
import com.jrefinery.data.Range;

/**
 * A renderer that draws horizontal bars representing a data range on a category plot.
 * <p>
 * For use with the {@link com.jrefinery.chart.plot.HorizontalCategoryPlot} class.
 *
 * @author David Gilbert
 */
public class HorizontalIntervalBarRenderer extends HorizontalBarRenderer
                                           implements CategoryItemRenderer, Serializable {

    /**
     * Creates a new renderer with no tool tip generator and no URL generator.
     * <P>
     * The defaults (no tool tip or URL generators) have been chosen to minimise the processing
     * required to generate a default chart.  If you require tool tips or URLs, then you can
     * easily add the required generators.
     */
    public HorizontalIntervalBarRenderer() {
        this(null, null);
    }

    /**
     * Creates a new renderer with the specified tooltip generator but no URL generator.
     *
     * @param toolTipGenerator  the tool tip generator (<code>null</code> permitted).
     */
    public HorizontalIntervalBarRenderer(CategoryToolTipGenerator toolTipGenerator) {
        this(toolTipGenerator, null);
    }

    /**
     * Creates a new renderer with the specified URL generator but no tooltip generator.
     *
     * @param urlGenerator  the URL generator (<code>null</code> permitted).
     */
    public HorizontalIntervalBarRenderer(CategoryURLGenerator urlGenerator) {
        this(null, urlGenerator);
    }

    /**
     * Creates a new renderer with the specified tooltip generator and URL generator.
     *
     * @param toolTipGenerator  the tool tip generator (<code>null</code> permitted).
     * @param urlGenerator  the URL generator (<code>null</code> permitted).
     */
    public HorizontalIntervalBarRenderer(CategoryToolTipGenerator toolTipGenerator,
                                         CategoryURLGenerator urlGenerator) {

        super(toolTipGenerator, urlGenerator);

    }

    /**
     * Draws a vertical line across the chart to represent the marker.
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
     * Draws an item from the dataset.  The {@link com.jrefinery.chart.plot.HorizontalCategoryPlot}
     * class will call this method once for each item in the dataset.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data plot area.
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
                         KeyedValues2DDataset data,
                         int dataset,
                         int row,
                         int column) {
     
        if (data instanceof MultiIntervalCategoryDataset) {
            MultiIntervalCategoryDataset d = (MultiIntervalCategoryDataset) data;
            drawSubIntervals(g2, dataArea, plot, domainAxis, rangeAxis, d, dataset, row, column);
        }
        else if (data instanceof IntervalCategoryDataset) {
            IntervalCategoryDataset d = (IntervalCategoryDataset) data;
            drawInterval(g2, dataArea, plot, domainAxis, rangeAxis, d, dataset, row, column);
        }
        else {
            // how do we want to handle a standard TableDataset
        } 
    }
                          
    /**
     * Draws a single interval.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data plot area.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param intervalData  the data.
     * @param dataset  the dataset index (zero-based).
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    private void drawInterval(Graphics2D g2,
                              Rectangle2D dataArea,
                              CategoryPlot plot,
                              CategoryAxis domainAxis,
                              ValueAxis rangeAxis,
                              IntervalCategoryDataset intervalData,
                              int dataset,
                              int row,
                              int column) {

        // X0
        Number value0 = intervalData.getStartValue(row, column);
        double translatedValue0 = rangeAxis.translateValueToJava2D(value0.doubleValue(), dataArea);

        // X1
        Number value1 = intervalData.getEndValue(row, column);
        double translatedValue1 = rangeAxis.translateValueToJava2D(value1.doubleValue(), dataArea);

        if (translatedValue1 < translatedValue0) {
            double temp = translatedValue1;
            translatedValue1 = translatedValue0;
            translatedValue0 = temp;
        }

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

        // WIDTH
        double rectWidth = Math.abs(translatedValue1 - translatedValue0);

        // HEIGHT
        double rectHeight = getBarWidth();

        // DRAW THE BAR...
        Rectangle2D bar = new Rectangle2D.Double(translatedValue0, rectY, rectWidth, rectHeight);

        Paint seriesPaint = getItemPaint(dataset, row, column);
        g2.setPaint(seriesPaint);
        g2.fill(bar);
        if (getBarWidth() > BAR_OUTLINE_WIDTH_THRESHOLD) {
            g2.setStroke(getItemStroke(dataset, row, column));
            g2.setPaint(getItemOutlinePaint(dataset, row, column));
            g2.draw(bar);
        }

        // collect entity and tool tip information...
        if (getInfo() != null) {
            EntityCollection entities = getInfo().getEntityCollection();
            if (entities != null) {
                String tip = null;
                if (getToolTipGenerator() != null) {
                    tip = getToolTipGenerator().generateToolTip(intervalData, row, column);
                }
                String url = null;
                if (getURLGenerator() != null) {
                    url = getURLGenerator().generateURL(intervalData, row, column);
                }
                CategoryItemEntity entity = new CategoryItemEntity(bar, tip, url, row, 
                                                    intervalData.getColumnKey(column), column);
                entities.addEntity(entity);
            }
        }
    }

    /**
     * Draws the sub-intervals for one item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data plot area.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param data  the data.
     * @param dataset  the dataset index (zero-based).
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    private void drawSubIntervals(Graphics2D g2,
                                  Rectangle2D dataArea,
                                  CategoryPlot plot,
                                  CategoryAxis domainAxis,
                                  ValueAxis rangeAxis,
                                  MultiIntervalCategoryDataset data,
                                  int dataset,
                                  int row,
                                  int column) {

        int count = data.getSubIntervalCount(row, column);
        if (count == 0) {
            drawInterval(g2, dataArea, plot, domainAxis, rangeAxis, data, dataset, row, column);
        }

        for (int subinterval = 0; subinterval < count; subinterval++) {
            
            // X0
            Number value0 = data.getStartValue(row, column, subinterval);
            double translatedValue0 = rangeAxis.translateValueToJava2D(value0.doubleValue(), 
                                                                       dataArea);
    
            // X1
            Number value1 = data.getEndValue(row, column, subinterval);
            double translatedValue1 = rangeAxis.translateValueToJava2D(value1.doubleValue(), 
                                                                       dataArea);
    
            if (translatedValue1 < translatedValue0) {
                double temp = translatedValue1;
                translatedValue1 = translatedValue0;
                translatedValue0 = temp;
            }
    
            // Y
            double rectY = domainAxis.getCategoryStart(column, getColumnCount(), dataArea);
    
            int rows = getRowCount();
            int columns = getColumnCount();
            if (rows > 1) {
                double seriesGap = dataArea.getHeight() * getItemMargin()
                                   / (columns * (rows - 1));
                rectY = rectY + row * (getBarWidth() + seriesGap);
            }
            else {
                rectY = rectY + row * getBarWidth();
            }
    
            // WIDTH
            double rectWidth = Math.abs(translatedValue1 - translatedValue0);
    
            // HEIGHT
            double rectHeight = getBarWidth();
    
            // DRAW THE BAR...
            Rectangle2D bar = new Rectangle2D.Double(translatedValue0, rectY, 
                                                     rectWidth, rectHeight);
    
            Paint seriesPaint = getItemPaint(dataset, row, column);
            g2.setPaint(seriesPaint);
            g2.fill(bar);
            if (getBarWidth() > BAR_OUTLINE_WIDTH_THRESHOLD) {
                g2.setStroke(getItemStroke(dataset, row, column));
                g2.setPaint(getItemOutlinePaint(dataset, row, column));
                g2.draw(bar);
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
                    CategoryItemEntity entity = new CategoryItemEntity(bar, tip, url, row, 
                                                        data.getColumnKey(column), column);
                    entities.addEntity(entity);
                }
            }
        }
    }

}
