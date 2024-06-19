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
 * ------------------------
 * VerticalBarRenderer.java
 * ------------------------
 * (C) Copyright 2000-2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Tin Luu;
 *                   Richard Atkinson;
 *
 * $Id: VerticalBarRenderer.java,v 1.1 2007/10/10 20:00:08 vauchers Exp $
 *
 * Changes
 * -------
 * 19-Oct-2001 : Version 1 (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 23-Oct-2001 : Changed intro and trail gaps on bar plots to use percentage of available space
 *               rather than a fixed number of units (DG);
 * 31-Oct-2001 : Debug for gaps (DG);
 * 15-Nov-2001 : Modified to allow for null data values (DG);
 * 13-Dec-2001 : Changed drawBar(...) method to return a Shape (that can be used for tooltips) (DG);
 * 15-Feb-2002 : Added isStacked() method to allow the plot to alter the method of finding the
 *               minimum and maximum data values (DG);
 * 14-Mar-2002 : Modified to implement the CategoryItemRenderer interface (DG);
 * 24-May-2002 : Incorporated tooltips into chart entities (DG);
 * 11-Jun-2002 : Added check for (permitted) null info object, bug and fix reported by David
 *               Basten.  Also updated Javadocs. (DG);
 * 19-Jun-2002 : Added code to draw labels on bars (TL);
 * 26-Jun-2002 : Added range axis to initialise method, and implemented our own bar clipping to
 *               avoid PRExceptions (DG);
 * 05-Aug-2002 : Small modification to drawCategoryItem method to support URLs for HTML image
 *               maps (RA);
 * 08-Aug-2002 : Applied fixed in bug id 592218 (DG);
 * 20-Aug-2002 : Updated drawRangeMarker method (DG);
 * 20-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 09-Oct-2002 : Changed default to NO tool tip generator (DG);
 * 24-Oct-2002 : Amendments for changes in CategoryDataset interface and CategoryToolTipGenerator
 *               interface (DG);
 * 05-Nov-2002 : Replaced references to CategoryDataset with TableDataset (DG);
 * 06-Nov-2002 : Modified CategoryAxis classes.  Moved this class to the
 *               com.jrefinery.chart.renderer package (DG);
 * 20-Nov-2002 : Changed signature of drawItem(...) method to reflect use of TableDataset (DG);
 * 10-Jan-2003 : Moved get/setItemMargin methods to BarRenderer class (DG);
 * 17-Jan-2003 : Moved plot classes to a separate package (DG); 
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 10-Apr-2003 : Removed category paint usage (DG);
 * 10-Apr-2003 : Changed CategoryDataset to KeyedValues2DDataset in drawItem(...) method (DG);
 *
 */

package com.jrefinery.chart.renderer;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.text.NumberFormat;

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
 * A renderer that handles the drawing of bars for a vertical bar plot.
 * <p>
 * For use with the {@link com.jrefinery.chart.plot.VerticalCategoryPlot} class.
 *
 * @author David Gilbert
 */
public class VerticalBarRenderer extends BarRenderer 
                                 implements CategoryItemRenderer, Serializable {

    /**
     * Creates a new renderer with no tool tip generator and no URL generator.
     * <P>
     * The defaults (no tool tip or URL generators) have been chosen to minimise the processing
     * required to generate a default chart.  If you require tool tips or URLs, then you can
     * easily add the required generators.
     */
    public VerticalBarRenderer() {
        this(null, null);
    }

    /**
     * Creates a new renderer with the specified tooltip generator but no URL generator.
     *
     * @param toolTipGenerator  the tool tip generator.
     */
    public VerticalBarRenderer(CategoryToolTipGenerator toolTipGenerator) {
        this(toolTipGenerator, null);
    }

    /**
     * Creates a new renderer with the specified URL generator but no tooltip generator.
     *
     * @param urlGenerator  the URL generator.
     */
    public VerticalBarRenderer(CategoryURLGenerator urlGenerator) {
        this(null, urlGenerator);
    }

    /**
     * Creates a new renderer with the specified tooltip generator and URL generator.
     *
     * @param toolTipGenerator  the tool tip generator.
     * @param urlGenerator  the URL generator.
     */
    public VerticalBarRenderer(CategoryToolTipGenerator toolTipGenerator,
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
            double used = dataArea.getWidth() * (1 - domainAxis.getLowerMargin()
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
                         KeyedValues2DDataset data,
                         int dataset,
                         int row,
                         int column) {

        // plot non-null values...
        Number dataValue = data.getValue(row, column);
        if (dataValue != null) {

            // BAR X
            double rectX = domainAxis.getCategoryStart(column, getColumnCount(), dataArea);

            int seriesCount = getRowCount();
            int categoryCount = getColumnCount();
            if (seriesCount > 1) {
                double seriesGap = dataArea.getWidth() * getItemMargin()
                                   / (categoryCount * (seriesCount - 1));
                rectX = rectX + row * (getBarWidth() + seriesGap);
            }
            else {
                rectX = rectX + row * getBarWidth();
            }

            // BAR Y
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
                base = getLowerClip();
                if (value >= uclip) {
                   value = uclip;
                }
            }

            double transY1 = rangeAxis.translateValueToJava2D(base, dataArea);
            double transY2 = rangeAxis.translateValueToJava2D(value, dataArea);
            double rectY = Math.min(transY2, transY1);

            double rectWidth = getBarWidth();
            double rectHeight = Math.abs(transY2 - transY1);

            Rectangle2D bar = new Rectangle2D.Double(rectX, rectY, rectWidth, rectHeight);
//            Paint itemPaint = null;
//            if (!getUseCategoriesPaint()) {
//                itemPaint = getItemPaint(dataset, row, column);
//            }
//            else {
//                itemPaint = getCategoryPaint(column);
//            }
            g2.setPaint(getItemPaint(dataset, row, column));
            g2.fill(bar);
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
                    String s = formatter.format(dataValue);
                    java.awt.FontMetrics fm = g2.getFontMetrics();
                    int ix = (int) ((getBarWidth() - fm.stringWidth(s)) / 2);
                    g2.drawString(s, (int) (rectX + ix), (int) (rectY - 5));
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
                    CategoryItemEntity entity = new CategoryItemEntity(bar, tip, url, row, 
                                                        data.getColumnKey(column), column);
                    entities.addEntity(entity);
                }
            }

        }

    }

}
