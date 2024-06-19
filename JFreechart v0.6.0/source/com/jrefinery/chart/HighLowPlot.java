/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: HighLowPlot.java,v 1.1 2007/10/10 18:53:20 vauchers Exp $
 *
 * Original Author:  Andrzej Porebski;
 * Contributor(s):   David Gilbert;
 *
 * (C) Copyright 2000, 2001 by Andrzej Porebski;
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
 * Changes (from 21-Jun-2001)
 * --------------------------
 * 21-Jun-2001 : Removed redundant JFreeChart parameter from constructor (DG);
 * 18-Sep-2001 : Updated e-mail address in header (DG);
 * 15-Oct-2001 : Data source classes moved to com.jrefinery.data.* (DG);
 * 19-Oct-2001 : Moved some methods [getSeriesPaint(...) etc.] from JFreeChart to Plot (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 *               Renamed HiLowPlot.java --> HighLowPlot.java (DG);
 * 17-Nov-2001 : Renamed HiLow.java --> HighLow.java (DG);
 * 20-Nov-2001 : Fixed clipping bug that shows up when chart is displayed inside JScrollPane (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import com.jrefinery.data.*;
import com.jrefinery.chart.event.PlotChangeEvent;

/**
 * A Plot that displays data in the form of an high-low-open-close plot, using data from any class
 * that implements the HighLowDataSource interface.
 *
 * @see Plot
 * @see HighLowDataSource
 */
public class HighLowPlot extends Plot implements HorizontalValuePlot, VerticalValuePlot {

    /**
     * If true, open and close marks are drawn.
     */
    private boolean drawOpenClose = true;

    /**
     * Standard constructor: returns an HiLowPlot with attributes specified by the caller.
     *
     * @param horizontalAxis The horizontal axis.
     * @param verticalAxis The vertical axis.
     */
    public HighLowPlot(Axis horizontalAxis, Axis verticalAxis) throws AxisNotCompatibleException,
                                                                      PlotNotCompatibleException
    {
	super(horizontalAxis, verticalAxis);
    }

    /**
     * A convenience method that returns the dataset for the plot, cast as an HighLowDataset.
     */
    public HighLowDataset getDataset() {
	return (HighLowDataset)chart.getDataset();
    }

    /**
     * Returns true if this plot draws open/close marks.
     */
    public boolean getDrawOpenCloseTicks() {
	return drawOpenClose;
    }

    /**
     * Sets the property that tells this plot to draw open/close marks.
     * Once the value of this property is set, all listeners are notified
     * of the change.
     */
    public void setDrawOpenCloseTicks(boolean draw) {
	if (drawOpenClose != draw) {
	    drawOpenClose = draw;
            notifyListeners(new PlotChangeEvent(this));
	}
    }

    /**
     * A convenience method that returns a reference to the horizontal axis cast as a
     * HorizontalValueAxis.
     */
    public ValueAxis getHorizontalValueAxis() {
	return (ValueAxis)horizontalAxis;
    }

    /**
     * A convenience method that returns a reference to the vertical axis cast as a
     * VerticalNumberAxis.
     */
    public ValueAxis getVerticalValueAxis() {
	return (ValueAxis)verticalAxis;
    }

    /**
     * Returns a list of HighLow elements that will fit inside the specified area.
     */
    private java.util.List getLines(Rectangle2D plotArea) {
	ArrayList elements = new ArrayList();
	HighLowDataset data = getDataset();
	if (data != null) {
	    int seriesCount = data.getSeriesCount();

	    for (int series=0; series<seriesCount; series++) {
	    	int itemCount = data.getItemCount(series);
	    	//if (itemCount % 4 != 0)
	    	//    continue;

	    	for(int itemIndex = 0; itemIndex < itemCount; itemIndex++) {
	    	    Number x = data.getXValue(series, itemIndex);
	    	    Number yHigh  = data.getHighValue(series,itemIndex);
	    	    Number yLow   = data.getLowValue(series,itemIndex);
	    	    Number yOpen  = data.getOpenValue(series,itemIndex);
	    	    Number yClose = data.getCloseValue(series,itemIndex);

	    	    double xx = getHorizontalValueAxis().translatedValue(x, plotArea);
	    	    double yyHigh = getVerticalValueAxis().translatedValue(yHigh, plotArea);
	    	    double yyLow = getVerticalValueAxis().translatedValue(yLow, plotArea);
	    	    double yyOpen = getVerticalValueAxis().translatedValue(yOpen, plotArea);
	    	    double yyClose = getVerticalValueAxis().translatedValue(yClose, plotArea);

	    	    Paint p = this.getSeriesPaint(series);
	    	    Stroke s = this.getSeriesStroke(series);

	    	    elements.add(new HighLow(xx, yyHigh, yyLow, yyOpen, yyClose, s, p));
	    	}
	    }
	}
	return elements;
    }

    /**
     * Checks the compatibility of a horizontal axis, returning true if the axis is compatible with
     * the plot, and false otherwise.
     * @param axis The horizontal axis.
     */
    public boolean isCompatibleHorizontalAxis(Axis axis) {
	if (axis instanceof HorizontalNumberAxis) {
	    return true;
	}
	else if (axis instanceof HorizontalDateAxis) {
	    return true;
	}
        else return false;
    }

    /**
     * Checks the compatibility of a vertical axis, returning true if the axis is compatible with
     * the plot, and false otherwise.  The vertical axis for this plot must be an instance of
     * VerticalNumberAxis.
     * @param axis The vertical axis.
     */
    public boolean isCompatibleVerticalAxis(Axis axis)
    {
	if (axis instanceof VerticalNumberAxis)
	    return true;
	else
	    return false;
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     * @param g2 The graphics device;
     * @param drawArea The area within which the plot should be drawn.
     */
    public void draw(Graphics2D g2, Rectangle2D drawArea) {

	if (insets!=null) {
	    drawArea = new Rectangle2D.Double(drawArea.getX()+insets.left,
		        		      drawArea.getY()+insets.top,
					      drawArea.getWidth()-insets.left-insets.right,
					      drawArea.getHeight()-insets.top-insets.bottom);
	}

	// we can cast the axes because HiLowPlot enforces support of these interfaces
	HorizontalAxis ha = getHorizontalAxis();
	VerticalAxis va = getVerticalAxis();

	double h = ha.reserveHeight(g2, this, drawArea);
	Rectangle2D vAxisArea = va.reserveAxisArea(g2, this, drawArea, h);

	// compute the plot area
	Rectangle2D plotArea = new Rectangle2D.Double(drawArea.getX()+vAxisArea.getWidth(),
						      drawArea.getY(),
						      drawArea.getWidth()-vAxisArea.getWidth(),
						      drawArea.getHeight()-h);

	drawOutlineAndBackground(g2, plotArea);

	// draw the axes

	this.horizontalAxis.draw(g2, drawArea, plotArea);
	this.verticalAxis.draw(g2, drawArea, plotArea);

	Shape originalClip = g2.getClip();
	g2.clip(plotArea);

	java.util.List lines = getLines(plotArea);   // area should be remaining area only
	for (int i=0; i<lines.size(); i++) {
            HighLow l = (HighLow)lines.get(i);
	    g2.setPaint(l.getPaint());
	    g2.setStroke(l.getStroke());
	    g2.draw(l.getLine());
	    if (getDrawOpenCloseTicks()) {
	        g2.draw(l.getOpenTickLine());
	        g2.draw(l.getCloseTickLine());
	    }
	}

	g2.setClip(originalClip);
    }

    /**
     * Returns the plot type as a string. This implementation returns "HiLow Plot".
     */
    public String getPlotType() {
	return "HiLow Plot";
    }

    /**
     * Returns the minimum value in the domain, since this is plotted against the horizontal axis
     * for a HighLowPlot.
     */
    public Number getMinimumHorizontalDataValue() {

	Dataset data = this.getChart().getDataset();
	if (data!=null) {
	    return Datasets.getMinimumDomainValue(data);
	}
	else return null;

    }

    /**
     * Returns the maximum value in the domain, since this is plotted against the horizontal axis
     * for a HighLowPlot.
     */
    public Number getMaximumHorizontalDataValue() {
	Dataset data = this.getChart().getDataset();
	if (data!=null) {
	    return Datasets.getMaximumDomainValue(data);
	}
	else return null;
    }

    /**
     * Returns the minimum value in the range, since this is plotted against the vertical axis for
     * a HighLowPlot.
     */
    public Number getMinimumVerticalDataValue() {
	Dataset data = this.getChart().getDataset();
	if (data!=null) {
	    return Datasets.getMinimumRangeValue(data);
	}
	else return null;
    }

    /**
     * Returns the maximum value in the range, since this is plotted against the vertical axis for
     * a HighLowPlot.
     */
    public Number getMaximumVerticalDataValue() {
	Dataset data = this.getChart().getDataset();
	if (data!=null) {
	    return Datasets.getMaximumRangeValue(data);
	}
	else
	    return null;
    }

}
