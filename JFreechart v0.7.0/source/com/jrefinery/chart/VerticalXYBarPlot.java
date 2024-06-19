/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: VerticalXYBarPlot.java,v 1.1 2007/10/10 18:54:39 vauchers Exp $
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Bill Kelemen;
 *
 * (C) Copyright 2001 Simba Management Limited;
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
 * Changes
 * -------
 * 18-Oct-2001 : First version (DG);
 * 19-Oct-2001 : Moved series paint and stroke methods from JFreeChart.java to Plot.java (DG);
 *               Added a new constructor with more defaults (DG);
 * 20-Nov-2001 : Fixed clipping bug that shows up when chart is displayed inside JScrollPane (DG);
 * 06-Dec-2001 : Small change to drawBar(...) method (BK);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;
import com.jrefinery.data.*;

public class VerticalXYBarPlot extends Plot implements HorizontalValuePlot, VerticalValuePlot {

    /**
     * Standard constructor.
     * @param horizontalAxis The horizontal axis for the plot;
     * @param verticalAxis The vertical axis for the plot;
     * @param insets Amount of blank space around the plot area;
     * @param background The Paint used to fill the plot background;
     * @param outlineStroke The Stroke used to draw an outline around the plot;
     * @param outlinePaint Storage for registered change listeners.
     */
    public VerticalXYBarPlot(Axis horizontalAxis, Axis verticalAxis, Insets insets,
            Paint background, Stroke outlineStroke, Paint outlinePaint)
            throws AxisNotCompatibleException, PlotNotCompatibleException
    {

        super(horizontalAxis, verticalAxis, insets, background, outlineStroke, outlinePaint);

    }

    /**
     * Standard constructor.
     * @param horizontalAxis The horizontal axis for the plot;
     * @param verticalAxis The vertical axis for the plot;
     */
    public VerticalXYBarPlot(Axis horizontalAxis, Axis verticalAxis)
        throws AxisNotCompatibleException, PlotNotCompatibleException
    {

       super(horizontalAxis, verticalAxis);

    }

    /**
     * A convenience method that returns the horizontal axis cast as a ValueAxis.
     */
    public ValueAxis getDomainAxis() {
        return (ValueAxis)horizontalAxis;
    }

    /**
     * A convenience method that returns the vertical axis cast as a VerticalNumberAxis.
     */
    public VerticalNumberAxis getRangeAxis() {
        return (VerticalNumberAxis)verticalAxis;
    }

    /**
     * Returns true if the specified axis is compatible with the plot with regard to operating as
     * the horizontal axis.
     * <P>
     * This plot requires the horizontal axis to be a subclass of HorizontalNumberAxis or
     * HorizontalDateAxis.
     * @param axis The axis.
     */
    public boolean isCompatibleHorizontalAxis(Axis axis) {
        if (axis instanceof HorizontalNumberAxis) return true;
        else if (axis instanceof HorizontalDateAxis) return true;
        else return false;
    }

    /**
     * Returns true if the specified axis is compatible with the plot with regard to operating as
     * the vertical axis.
     * <P>
     * This plot requires the vertical axis to be a subclass of VerticalNumberAxis.
     * @param axis The axis.
     */
    public boolean isCompatibleVerticalAxis(Axis axis) {
        if (axis instanceof VerticalNumberAxis) return true;
        else return false;
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     * @param g2 The graphics device;
     * @param drawArea The area within which the plot should be drawn;
     */
    public void draw(Graphics2D g2, Rectangle2D drawArea) {

        // adjust the drawing area for plot insets (if any)...
        if (insets!=null) {
            drawArea = new Rectangle2D.Double(drawArea.getX()+insets.left,
                                              drawArea.getY()+insets.top,
                                              drawArea.getWidth()-insets.left-insets.right,
                                              drawArea.getHeight()-insets.top-insets.bottom);
        }

        // estimate the area required for drawing the axes...
        HorizontalAxis ha = getHorizontalAxis();
        VerticalAxis va = getVerticalAxis();
        double hAxisAreaHeight = ha.reserveHeight(g2, this, drawArea);
        Rectangle2D vAxisArea = va.reserveAxisArea(g2, this, drawArea, hAxisAreaHeight);
        Rectangle2D plotArea = new Rectangle2D.Double(drawArea.getX()+vAxisArea.getWidth(),
                                                      drawArea.getY(),
                                                      drawArea.getWidth()-vAxisArea.getWidth(),
                                                      drawArea.getHeight()-hAxisAreaHeight);

        // draw the background and axes...
        drawOutlineAndBackground(g2, plotArea);
        getDomainAxis().draw(g2, drawArea, plotArea);
        getRangeAxis().draw(g2, drawArea, plotArea);

        // now get the data and plot the bars...
        IntervalXYDataset data = (IntervalXYDataset)chart.getDataset();
        if (data!=null) {
            Shape savedClip = g2.getClip();
            g2.clip(plotArea);
            double translatedVerticalZero = getRangeAxis().translatedValue(Plot.ZERO, plotArea);
            int seriesCount = data.getSeriesCount();
            for (int series = 0; series<seriesCount; series++) {
                int itemCount = data.getItemCount(series);
                for (int item = 0; item<itemCount; item++) {
                    drawBar(g2, plotArea, data, series, item, getDomainAxis(), getRangeAxis(),
                            translatedVerticalZero);
                }
            }
            g2.setClip(savedClip);
        }

    }

    protected void drawBar(Graphics2D g2, Rectangle2D plotArea, IntervalXYDataset data,
                           int series, int item, ValueAxis horizontalAxis, ValueAxis verticalAxis,
                           double translatedRangeZero) {

        Paint seriesPaint = this.getSeriesPaint(series);
        Paint seriesOutlinePaint = this.getSeriesOutlinePaint(series);

        Number valueNumber = data.getYValue(series, item);
        double translatedValue = verticalAxis.translatedValue(valueNumber, plotArea);

        Number startXNumber = data.getStartXValue(series, item);
        double translatedStartX = horizontalAxis.translatedValue(startXNumber, plotArea);

        Number endXNumber = data.getEndXValue(series, item);
        double translatedEndX = horizontalAxis.translatedValue(endXNumber, plotArea);

        double translatedWidth = Math.max(1, translatedEndX-translatedStartX);
        double translatedHeight = Math.abs(translatedValue-translatedRangeZero);
        Rectangle2D bar = new Rectangle2D.Double(translatedStartX,
                                                 Math.min(translatedRangeZero, translatedValue),
                                                 translatedWidth, translatedHeight);
        g2.setPaint(seriesPaint);
        g2.fill(bar);
        if ((translatedEndX-translatedStartX)>3) {
            g2.setStroke(this.getSeriesOutlineStroke(series));
            g2.setPaint(seriesOutlinePaint);
            g2.draw(bar);
        }

    }

    /**
     * Returns the minimum value in either the domain or the range, whichever is displayed against
     * the horizontal axis for the particular type of plot implementing this interface.
     */
    public Number getMinimumHorizontalDataValue() {

        Dataset data = this.getChart().getDataset();
	if (data!=null) {
	    return Datasets.getMinimumDomainValue(data);
	}
	else return null;

    }

    /**
     * Returns the maximum value in either the domain or the range, whichever is displayed against
     * the horizontal axis for the particular type of plot implementing this interface.
     */
    public Number getMaximumHorizontalDataValue() {

	Dataset data = this.getChart().getDataset();
	if (data!=null) {
	    return Datasets.getMaximumDomainValue(data);
	}
	else return null;

    }
    /**
     * Returns the minimum Y value from the datasource.
     * <P>
     * This method can return null if the data source is null.
     */
    public Number getMinimumVerticalDataValue()	{

        Dataset data = this.getChart().getDataset();
	if (data!=null) {
	    return Datasets.getMinimumRangeValue(data);
	}
	else return null;

    }

    /**
     * Returns the maximum Y value from the datasource.
     * <P>
     * This method can return null if the data source is null.
     */
    public Number getMaximumVerticalDataValue() {

	Dataset data = this.getChart().getDataset();
	if (data!=null) {
	    return Datasets.getMaximumRangeValue(data);
	}
	else return null;

    }

    /**
     * Returns a short string describing the plot type.
     */
    public String getPlotType() {
        return "Vertical XY Bar Plot";
    }

}