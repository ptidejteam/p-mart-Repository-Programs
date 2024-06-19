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
 * -----------------------------------
 * VerticalStatisticalBarRenderer.java
 * -----------------------------------
 * (C) Copyright 2002, 2003 by Pascal Collet and Contributors.
 *
 * Original Author:  Pascal Collet;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: VerticalStatisticalBarRenderer.java,v 1.1 2007/10/10 19:57:50 vauchers Exp $
 *
 * Changes
 * -------
 * 21-Aug-2002 : Version 1, contributed by Pascal Collet (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 24-Oct-2002 : Changes to dataset interface (DG);
 * 05-Nov-2002 : Base dataset is now TableDataset not CategoryDataset (DG);
 * 05-Feb-2003 : Updates for new DefaultStatisticalCategoryDataset (DG);
 *
 */

package com.jrefinery.chart.renderer;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import com.jrefinery.chart.axis.CategoryAxis;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.plot.CategoryPlot;
import com.jrefinery.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.chart.urls.CategoryURLGenerator;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.StatisticalCategoryDataset;

/**
 * A renderer that handles the drawing of bars for a vertical bar plot where
 * each bar has a mean value and a standard deviation vertical line.
 *
 * @author Pascal Collet
 */
public class VerticalStatisticalBarRenderer extends VerticalBarRenderer
                                            implements CategoryItemRenderer {

    /**
     * Creates a new renderer with no tool tip or URL generator.
     */
    public VerticalStatisticalBarRenderer() {
        this(null, null);
    }

    /**
     * Creates a new renderer with the specified tool tip generator.
     *
     * @param toolTipGenerator  the tooltip generator
     */
    public VerticalStatisticalBarRenderer(CategoryToolTipGenerator toolTipGenerator) {
        this(toolTipGenerator, null);
    }

    /**
     * Creates a new renderer with the specified URL generator.
     *
     * @param urlGenerator  the URL generator
     */
    public VerticalStatisticalBarRenderer(CategoryURLGenerator urlGenerator) {
        this(null, urlGenerator);
    }

    /**
     * Creates a new renderer with the specified tool tip generator.
     *
     * @param toolTipGenerator  the tooltip generator
     * @param urlGenerator  the URL generator
     */
    public VerticalStatisticalBarRenderer(CategoryToolTipGenerator toolTipGenerator,
                                          CategoryURLGenerator urlGenerator) {

        super(toolTipGenerator, urlGenerator);

    }

    /**
     * Draws the bar with its standard deviation line range for a single (series, category) data
     * item.
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


        // defensive check
        if (!(data instanceof StatisticalCategoryDataset)) {
            throw new IllegalArgumentException("VerticalStatisticalBarRenderer.drawCategoryItem()"
                + " : the data should be of type StatisticalCategoryDataSet only.");
        }
        StatisticalCategoryDataset statData = (StatisticalCategoryDataset) data;

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
        Number meanValue = statData.getMeanValue(row, column);
        
        double value = meanValue.doubleValue();
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
        Paint seriesPaint = getItemPaint(0, row, column);
        g2.setPaint(seriesPaint);
        g2.fill(bar);
        if (getBarWidth() > 3) {
            g2.setStroke(getItemStroke(dataset, row, column));
            g2.setPaint(getItemOutlinePaint(dataset, row, column));
            g2.draw(bar);
        }

        // standard deviation lines
        double valueDelta = statData.getStdDevValue(row, column).doubleValue();
        double highVal = rangeAxis.translateValueToJava2D(meanValue.doubleValue() + valueDelta,
                                                     dataArea);
        double lowVal = rangeAxis.translateValueToJava2D(meanValue.doubleValue() - valueDelta,
                                                         dataArea);

        Line2D line = null;
        line = new Line2D.Double(rectX + rectWidth / 2.0d, lowVal,
                                 rectX + rectWidth / 2.0d, highVal);
        g2.draw(line);
        line = new Line2D.Double(rectX + rectWidth / 2.0d - 5.0d, highVal,
                                 rectX + rectWidth / 2.0d + 5.0d, highVal);
        g2.draw(line);
        line = new Line2D.Double(rectX + rectWidth / 2.0d - 5.0d, lowVal,
                                 rectX + rectWidth / 2.0d + 5.0d, lowVal);
        g2.draw(line);
    }

}
