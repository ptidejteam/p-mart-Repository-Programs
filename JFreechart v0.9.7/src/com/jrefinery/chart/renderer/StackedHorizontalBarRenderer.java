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
 * ---------------------------------
 * StackedHorizontalBarRenderer.java
 * ---------------------------------
 * (C) Copyright 2001-2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Richard Atkinson;
 *
 * $Id: StackedHorizontalBarRenderer.java,v 1.1 2007/10/10 20:00:08 vauchers Exp $
 *
 * Changes
 * -------
 * 22-Oct-2001 : Version 1 (DG);
 *               Renamed DataSource.java --> Dataset.java etc. (DG);
 * 23-Oct-2001 : Changed intro and trail gaps on bar plots to use percentage of available space
 *               rather than a fixed number of units (DG);
 * 15-Nov-2001 : Modified to allow for null data values (DG);
 * 13-Dec-2001 : Initial tooltip implementation (DG);
 * 15-Feb-2002 : Added isStacked() method (DG);
 * 14-Mar-2002 : Modified to implement the CategoryItemRenderer interface (DG);
 * 24-May-2002 : Incorporated tooltips into chart entities (DG);
 * 11-Jun-2002 : Added check for (permitted) null info object, bug and fix reported by David
 *               Basten.  Also updated Javadocs. (DG);
 * 25-Jun-2002 : Removed redundant import (DG);
 * 26-Jun-2002 : Small change to entity (DG);
 * 05-Aug-2002 : Small modification to drawCategoryItem method to support URLs for HTML
 *               image maps (RA);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 24-Oct-2002 : Amendments for changes in CategoryDataset interface and CategoryToolTipGenerator
 *               interface (DG);
 * 05-Nov-2002 : Replaced references to CategoryDataset with TableDataset (DG);
 * 26-Nov-2002 : Replaced isStacked() method with getRangeType() method (DG);
 * 17-Jan-2003 : Moved plot classes to a separate package (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 *
 */

package com.jrefinery.chart.renderer;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import com.jrefinery.chart.ChartRenderingInfo;
import com.jrefinery.chart.axis.CategoryAxis;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.entity.CategoryItemEntity;
import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.plot.CategoryPlot;
import com.jrefinery.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.chart.urls.CategoryURLGenerator;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.KeyedValues2DDataset;

/**
 * A renderer that handles the drawing of "stacked" bars for a horizontal bar plot.
 * <p>
 * For use with the {@link com.jrefinery.chart.plot.HorizontalCategoryPlot} class.
 *
 * @author David Gilbert
 */
public class StackedHorizontalBarRenderer extends HorizontalBarRenderer implements Serializable {

    /**
     * Creates a new renderer with no tool tip generator and no URL generator.
     * <P>
     * The defaults (no tool tip or URL generators) have been chosen to minimise the processing
     * required to generate a default chart.  If you require tool tips or URLs, then you can
     * easily add the required generators.
     */
    public StackedHorizontalBarRenderer() {
        this(null, null);
    }

    /**
     * Creates a new renderer with the specified tooltip generator but no URL generator.
     *
     * @param toolTipGenerator  the tool tip generator.
     */
    public StackedHorizontalBarRenderer(CategoryToolTipGenerator toolTipGenerator) {
        this(toolTipGenerator, null);
    }
    
    /**
     * Creates a new renderer with the specified URL generator but no tooltip generator.
     *
     * @param urlGenerator  the URL generator.
     */
    public StackedHorizontalBarRenderer(CategoryURLGenerator urlGenerator) {
        this(null, urlGenerator);
    }

    /**
     * Creates a new renderer with the specified tooltip generator and URL generator.
     *
     * @param toolTipGenerator  the tool tip generator.
     * @param urlGenerator  the URL generator.
     */
    public StackedHorizontalBarRenderer(CategoryToolTipGenerator toolTipGenerator,
                                           CategoryURLGenerator urlGenerator) {

        super(toolTipGenerator, urlGenerator);

    }

    /**
     * Returns the range type.
     *
     * @return the range type.
     */
    public int getRangeType() {
        return CategoryItemRenderer.STACKED;
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
            double cm = 0.0;
            if (columns > 1) {
                cm = domainAxis.getCategoryMargin();
            }

            double used = dataArea.getHeight() * (1 - domainAxis.getLowerMargin()
                                                   - domainAxis.getUpperMargin()
                                                   - cm);
            if (columns > 0) {
                setBarWidth(used / columns);
            }
            else {
                setBarWidth(used);
            }
        }

    }

    /**
     * Draws a stacked bar for a specific item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the plot area.
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

        // RECT X
        double positiveBase = 0.0;
        double negativeBase = 0.0;
        for (int i = 0; i < row; i++) {
            Number v = data.getValue(i, column);
            if (v != null) {
                double d = v.doubleValue();
                if (d > 0) {
                    positiveBase = positiveBase + d;
                }
                else {
                    negativeBase = negativeBase + d;
                }
            }
        }


        Number value = data.getValue(row, column);
        if (value != null) {
            double xx = value.doubleValue();
            double translatedBase;
            double translatedValue;
            double rectX;

            if (xx > 0) {
                translatedBase = rangeAxis.translateValueToJava2D(positiveBase, dataArea);
                translatedValue = rangeAxis.translateValueToJava2D(positiveBase + xx, dataArea);
                rectX = Math.min(translatedBase, translatedValue);
            }
            else {
                translatedBase = rangeAxis.translateValueToJava2D(negativeBase, dataArea);
                translatedValue = rangeAxis.translateValueToJava2D(negativeBase + xx, dataArea);
                rectX = Math.min(translatedBase, translatedValue);
            }

            // Y
            double rectY = domainAxis.getCategoryStart(column, getColumnCount(), dataArea);

            // RECT WIDTH
            double rectWidth = Math.abs(translatedValue - translatedBase);
            // Supplied as a parameter as it is constant

            // rect HEIGHT
            double rectHeight = getBarWidth();

            Rectangle2D bar = new Rectangle2D.Double(rectX, rectY, rectWidth, rectHeight);
            Paint seriesPaint = getItemPaint(dataset, row, column);
            g2.setPaint(seriesPaint);
            g2.fill(bar);
            if (getBarWidth() > 3) {
                g2.setStroke(getItemStroke(dataset, row, column));
                g2.setPaint(getItemOutlinePaint(dataset, row, column));
                g2.draw(bar);
            }

            // collect entity and tool tip information...
            if (getInfo() != null) {
                EntityCollection entities = getInfo().getEntityCollection();
                if (entities != null) {
                    String tip = "";
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
