/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: XYPlot.java,v 1.1 2007/10/10 18:53:20 vauchers Exp $
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Craig MacFarlane;
 *                   Mark Watson (www.markwatson.com);
 *
 * (C) Copyright 2000, 2001 Simba Management Limited;
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
 * 21-Jun-2001 : Removed redundant JFreeChart parameter from constructors (DG);
 * 18-Sep-2001 : Updated e-mail address and fixed DOS encoding problem (DG);
 * 15-Oct-2001 : Data source classes moved to com.jrefinery.data.* (DG);
 * 19-Oct-2001 : Removed the code for drawing the visual representation of each data point into
 *               a separate class StandardXYItemRenderer.  This will make it easier to add
 *               variations to the way the charts are drawn.  Based on code contributed by
 *               Mark Watson (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 20-Nov-2001 : Fixed clipping bug that shows up when chart is displayed inside JScrollPane (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import com.jrefinery.data.*;
import com.jrefinery.chart.event.*;

/**
 * A general class for plotting data in the form of (x, y) pairs.  XYPlot can use data from any
 * class that implements the XYDataset interface (in the com.jrefinery.data package).
 * <P>
 * XYPlot makes use of a renderer to draw each point on the plot.  By using different renderers,
 * various chart types can be produced.  The ChartFactory class contains static methods for
 * creating pre-configured charts.
 * @see ChartFactory
 * @see Plot
 * @see XYDataset
 */
public class XYPlot extends Plot implements HorizontalValuePlot, VerticalValuePlot {

    /** Object responsible for drawing the visual representation of each point on the plot. */
    XYItemRenderer renderer;

    /** A list of vertical lines that will be overlaid on the plot. */
    ArrayList verticalLines = null;

    /** The colors for the vertical lines. */
    ArrayList verticalColors = null;

    /** A list of horizontal lines that will be overlaid on the plot. */
    ArrayList horizontalLines = null;

    /** The colors for the horizontal lines. */
    ArrayList horizontalColors = null;

    /**
     * Constructs an XYPlot with the specified axes (other attributes take default values).
     * @param horizontalAxis The horizontal axis.
     * @param verticalAxis The vertical axis.
     */
    public XYPlot(Axis horizontalAxis, Axis verticalAxis) throws AxisNotCompatibleException,
                                                                 PlotNotCompatibleException
    {
	super(horizontalAxis, verticalAxis);
        this.renderer = new StandardXYItemRenderer();
    }

    /**
     * Returns a reference to the current item renderer.
     * @return A reference to the current item renderer.
     */
    public XYItemRenderer getItemRenderer() {
        return this.renderer;
    }

    /**
     * Sets the item renderer, and notifies all listeners of a change to the plot.
     * @param renderer The new renderer.
     */
    public void setXYItemRenderer(XYItemRenderer renderer) {
        this.renderer = renderer;
        this.notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * A convenience method that returns the dataset for the plot, cast as an XYDataset.
     * @return The dataset for the plot, cast as an XYDataset.
     */
    public XYDataset getDataset() {
	return (XYDataset)chart.getDataset();
    }

    /**
     * Adds a vertical line at location with default color blue.
     * @return void
     */
    public void addVerticalLine(Number location) {
        addVerticalLine(location, Color.blue);
    }

    /**
     * Adds a vertical of the given color at location with the given color.
     * @return void
     */
    public void addVerticalLine(Number location, Paint color) {
        if (verticalLines == null) {
            verticalLines = new ArrayList();
            verticalColors = new ArrayList();
        }

        verticalColors.add(color);
        verticalLines.add(location);
    }

    /**
     * Adds a horizontal line at location with default color red.
     * @return void
     */
    public void addHorizontalLine(Number location) {
        addHorizontalLine(location, Color.red);
    }

    /**
     * Adds a horizontal line at location with given color.
     * @return void
     */
    public void addHorizontalLine(Number location, Paint color) {
        if (horizontalLines == null) {
            horizontalLines = new ArrayList();
            horizontalColors = new ArrayList();
        }

        horizontalColors.add(color);
        horizontalLines.add(location);
    }

    /**
     * A convenience method that returns a reference to the horizontal axis cast as a
     * ValueAxis.
     * @return The horizontal axis cast as a ValueAxis.
     */
    public ValueAxis getHorizontalValueAxis() {
	return (ValueAxis)horizontalAxis;
    }

    /**
     * A convenience method that returns a reference to the vertical axis cast as a
     * ValueAxis.
     * @return The vertical axis cast as a ValueAxis.
     */
    public ValueAxis getVerticalValueAxis() {
	return (ValueAxis)verticalAxis;
    }

    /**
     * Checks the compatibility of a horizontal axis, returning true if the axis is compatible with
     * the plot, and false otherwise.
     * @param axis The horizontal axis;
     * @return True if the axis is compatible with the plot, and false otherwise.
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
     * the plot, and false otherwise.
     * @param axis The vertical axis;
     * @return True if the axis is compatible with the plot, and false otherwise.
     */
    public boolean isCompatibleVerticalAxis(Axis axis) {
	if (axis instanceof VerticalNumberAxis) {
	    return true;
	}
	else return false;
    }

    /**
     * Draws the XY plot on a Java 2D graphics device (such as the screen or a printer).
     * <P>
     * XYPlot now relies on an XYItemRenderer to draw each item in the plot.  This allows the
     * visual representation of the plot to be changed easily.
     * @param g2 The graphics device;
     * @param drawArea The area within which the plot (including axis labels) should be drawn;
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
	HorizontalAxis hAxis = getHorizontalAxis();
	VerticalAxis vAxis = getVerticalAxis();
	double hAxisAreaHeight = hAxis.reserveHeight(g2, this, drawArea);
	Rectangle2D vAxisArea = vAxis.reserveAxisArea(g2, this, drawArea, hAxisAreaHeight);

        // ...and therefore what is left for the plot itself...
	Rectangle2D plotArea = new Rectangle2D.Double(drawArea.getX()+vAxisArea.getWidth(),
						      drawArea.getY(),
						      drawArea.getWidth()-vAxisArea.getWidth(),
						      drawArea.getHeight()-hAxisAreaHeight);

        // draw the plot background and axes...
	drawOutlineAndBackground(g2, plotArea);
	this.horizontalAxis.draw(g2, drawArea, plotArea);
	this.verticalAxis.draw(g2, drawArea, plotArea);

        // now get the data and plot it (the visual representation will depend on the renderer
        // that has been set)...
        XYDataset data = this.getDataset();
        if (data!=null) {
	    Shape originalClip = g2.getClip();
	    g2.clip(plotArea);

            drawVerticalLines(g2, plotArea);
            drawHorizontalLines(g2, plotArea);

            double transRangeZero = this.getVerticalValueAxis().translatedValue(Plot.ZERO, plotArea);
            int seriesCount = data.getSeriesCount();
            for (int series=0; series<seriesCount; series++) {
                int itemCount = data.getItemCount(series);
                for (int item=0; item<itemCount; item++) {
                    renderer.drawItem(g2, plotArea, this, (ValueAxis)hAxis, (ValueAxis)vAxis,
                                      data, series, item, transRangeZero);
                }
            }

            g2.setClip(originalClip);
        }
    }

    /**
     * Support method for the draw(...) method.
     */
    private void drawVerticalLines(Graphics2D g2, Rectangle2D plotArea) {

        // Draw any vertical lines
        if (verticalLines != null) {
            for (int i=0; i<verticalLines.size(); i++) {
                g2.setPaint((Paint)verticalColors.get(i));
                g2.setStroke(new BasicStroke(1));
                g2.drawLine((int)getHorizontalValueAxis().translatedValue((Number)verticalLines.get(i), plotArea),
                            0,
                            (int)getHorizontalValueAxis().translatedValue((Number)verticalLines.get(i), plotArea),
                            (int)(plotArea.getHeight()));
            }
        }

    }

    /**
     * Support method for the draw(...) method.
     */
    private void drawHorizontalLines(Graphics2D g2, Rectangle2D plotArea) {

        // Draw any horizontal lines
        if (horizontalLines != null) {
            for (int i=0; i<horizontalLines.size(); i++) {
                g2.setPaint((Paint)horizontalColors.get(i));
                g2.setStroke(new BasicStroke(1));
                g2.drawLine(0,
                            (int)getVerticalValueAxis().translatedValue((Number)horizontalLines.get(i), plotArea),
                            (int)(plotArea.getWidth()),
                            (int)getVerticalValueAxis().translatedValue((Number)horizontalLines.get(i), plotArea));
            }
        }

    }

    /**
     * Returns the plot type as a string.
     * @return A short string describing the type of plot.
     */
    public String getPlotType() {
	return "XY Plot";
    }

    /**
     * Returns the minimum value in the domain, since this is plotted against the horizontal axis
     * for an XYPlot.
     * @return The minimum value to be plotted against the horizontal axis.
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
     * for an XYPlot.
     * @return The maximum value to be plotted against the horizontal axis.
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
     * an XYPlot.
     * @return The minimum value to be plotted against the vertical axis.
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
     * an XYPlot.
     * @return The maximum value to be plotted against the vertical axis.
     */
    public Number getMaximumVerticalDataValue() {

	Dataset data = this.getChart().getDataset();
	if (data!=null) {
	    return Datasets.getMaximumRangeValue(data);
	}
	else return null;
    }

}