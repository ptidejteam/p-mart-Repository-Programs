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
 * ---------------------
 * PeriodMarkerPlot.java
 * ---------------------
 * (C) Copyright 2002, by Sylvain Vieujot and Contributors.
 *
 * Original Author:  Sylvain Vieujot;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: PeriodMarkerPlot.java,v 1.1 2007/10/10 19:05:14 vauchers Exp $
 *
 * Changes
 * -------
 * 08-Jan-2002 : Version 1, thanks to SV.  Added parameter for tooltips so that the code will
 *               compile in the current development version - tooltips ignored at this point (DG);
 * 09-Apr-2002 : Removed empty version tag to eliminate Javadoc warning, and updated import
 *               statements (DG);
 * 23-Apr-2002 : Moved dataset from JFreeChart to Plot (DG);
 * 13-Jun-2002 : Removed commented out code (DG);
 * 25-Jun-2002 : Removed redundant import (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.data.XYDataset;

/**
 * A plot that marks time periods, for use in overlaid plots.
 *
 * @author  sylvain
 */
public class PeriodMarkerPlot extends XYPlot implements ValueAxisPlot {

    /**
     * Creates a new period marker plot.
     *
     * @param data  the data series.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     */
    public PeriodMarkerPlot(XYDataset data, ValueAxis domainAxis, ValueAxis rangeAxis) {
        super(data, domainAxis, rangeAxis, null);
    }

    /**
     * Returns the legend items (null for this plot).
     *
     * @return the legend items.
     */
    public LegendItemCollection getLegendItems() {
        return null;
    }

    /**
     * Returns the plot type as a string.
     *
     * @return <i>Period Marker Plot</i>.
     */
    public String getPlotType() {
          return "Period Marker Plot";
    }

    /**
     * A convenience method that returns the dataset for the plot, cast as an XYDataset.
     *
     * @return a dataset.
     */
    public XYDataset getTempXYDataset() {
        // Usefull until SignalsDataset is included in jcommon.SubSeriesDataset
        return (XYDataset) getDataset();
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the plot should be drawn.
     * @param info  an optional info collection object to return data back to the caller.
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, ChartRenderingInfo info) {

        Insets insets = getInsets();
        if (insets != null) {
            plotArea = new Rectangle2D.Double(plotArea.getX() + insets.left,
                plotArea.getY() + insets.top,
                plotArea.getWidth() - insets.left - insets.right,
                plotArea.getHeight() - insets.top - insets.bottom);
        }

        AxisSpace space = new AxisSpace();
        ValueAxis domainAxis = getDomainAxis();
        ValueAxis rangeAxis = getRangeAxis();
        space = domainAxis.reserveSpace(g2, this, plotArea, getDomainAxisEdge(), space);
        space = rangeAxis.reserveSpace(g2, this, plotArea, getRangeAxisEdge(), space);
        Rectangle2D dataArea = space.shrink(plotArea, null);

        drawBackground(g2, dataArea);

        // draw the axes
        double cursor = 0.0;
        cursor = getDomainAxis().draw(g2, cursor, plotArea, dataArea, getDomainAxisEdge());
        cursor = getRangeAxis().draw(g2, cursor, plotArea, dataArea, getRangeAxisEdge());

        Shape originalClip = g2.getClip();
        g2.clip(dataArea);

        //SignalsDataset data = getDataset();
        XYDataset data = getTempXYDataset();
        if (data != null) {
            int seriesCount = data.getSeriesCount();
            for (int serie = 0; serie < seriesCount; serie++) {
                // area should be remaining area only
                drawMarkedPeriods(data, serie, g2, dataArea);
            }
        }

        drawOutline(g2, dataArea);
        g2.setClip(originalClip);
    }

    /**
     * Draws the marked periods.
     *
     * @param data  the dataset.
     * @param serie  the series.
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     */
    private void drawMarkedPeriods(XYDataset data, int serie, Graphics2D g2, Rectangle2D plotArea) {

        Paint thisSeriePaint = Color.gray;  // fix later //getFillPaint(0, serie);
        g2.setPaint(thisSeriePaint);
        g2.setStroke(AbstractRenderer.DEFAULT_STROKE);  // fix later getSeriesStroke(serie));

        float opacity = 0.1f;
        if (thisSeriePaint instanceof Color) {
            Color thisSerieColor = (Color) thisSeriePaint;
            int colorSaturation = thisSerieColor.getRed()
                                  + thisSerieColor.getGreen() + thisSerieColor.getBlue();
            if (colorSaturation > 255) {
                opacity = opacity * colorSaturation / 255.0f;
            }
        }
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

        double minY = plotArea.getMinY();
        double maxY = plotArea.getMaxY();

        int itemCount = data.getItemCount(serie);
        for (int itemIndex = 0; itemIndex < itemCount; itemIndex++) {
            if (data.getYValue(serie, itemIndex).doubleValue() == 0) {
                // un -marked period
                continue;
            }
            Number xStart;
            if (itemIndex > 0) {
                xStart = new Long((data.getXValue(serie, itemIndex).longValue()
                                   + data.getXValue(serie, itemIndex - 1).longValue()) / 2);
            }
            else {
                xStart = data.getXValue(serie, itemIndex);
            }
            int j = itemIndex + 1;
            while (j < itemCount) {
                if (data.getYValue(serie, j).doubleValue() == 0) {
                    break;
                }
                j++;
            }
            itemIndex = j;
            Number xEnd;
            if (j < itemCount) {
                xEnd = new Long((data.getXValue(serie, j - 1).longValue()
                                + data.getXValue(serie, j).longValue()) / 2);
            }
            else {
                xEnd = data.getXValue(serie, j - 1);
            }

            double xxStart = getDomainAxis().translateValueToJava2D(xStart.doubleValue(), plotArea,
                                     getDomainAxisEdge());
            double xxEnd = getDomainAxis().translateValueToJava2D(xEnd.doubleValue(), plotArea,
                                    getDomainAxisEdge());

            markPeriod(xxStart, xxEnd, minY, maxY, g2);
        }

        g2.setComposite(originalComposite);

    }

    /**
     * Marks a single period.
     *
     * @param xStart  the start.
     * @param xEnd  the end.
     * @param minY  the minimum y-value.
     * @param maxY  the maximum y-value.
     * @param g2  the graphics device.
     */
    private void markPeriod(double xStart, double xEnd, double minY, double maxY, Graphics2D g2) {
        g2.fill(new Rectangle2D.Double(xStart, minY, xEnd - xStart, maxY - minY));
    }

    /**
     * A zoom method that does nothing.  TO BE DONE.
     *
     * @param percent  the zoom percentage.
     */
    public void zoom(double percent) {
    }

}
