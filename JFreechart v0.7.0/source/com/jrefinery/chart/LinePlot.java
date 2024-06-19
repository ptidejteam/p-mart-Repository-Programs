/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: LinePlot.java,v 1.1 2007/10/10 18:54:39 vauchers Exp $
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
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
 * 21-Jun-2001 : Removed redundant JFreeChart parameter from constructor (DG);
 * 18-Sep-2001 : Updated e-mail address and fixed DOS encoding (DG);
 * 15-Oct-2001 : Data source classes moved to com.jrefinery.data.* (DG);
 * 19-Oct-2001 : Moved series paint and stroke methods from JFreeChart.java to Plot.java (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 23-Oct-2001 : Added HorizontalCategoryItemRenderer interface for drawing the data (DG);
 * 20-Nov-2001 : Fixed clipping bug that shows up when chart is displayed inside JScrollPane (DG);
 *               Added properties to control gaps at each end of the plot (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import com.jrefinery.chart.event.PlotChangeEvent;
import com.jrefinery.data.*;

/**
 * A Plot that displays data in the form of a line chart, using data from any class that
 * implements the CategoryDataSource interface.
 * @see Plot
 * @see CategoryDataSource
 */
public class LinePlot extends Plot implements CategoryPlot, VerticalValuePlot {

    /** The default gap before the first category (currently 10%). */
    public static final double DEFAULT_INTRO_GAP = 0.10;

    /** The maximum gap before the first category (currently 25%). */
    public static final double MAX_INTRO_GAP = 0.25;

    /** The default gap after the last category (currently 10%). */
    public static final double DEFAULT_TRAIL_GAP = 0.10;

    /** The maximum gap after the last category (currently 25%). */
    public static final double MAX_TRAIL_GAP = 0.25;

    /** The renderer that draws the lines. */
    protected HorizontalCategoryItemRenderer renderer;

    /** The gap before the first category, as a percentage of the total space. */
    protected double introGapPercent=0.10;

    /** The gap after the last category, as a percentage of the total space. */
    protected double trailGapPercent=0.10;

    /**
     * Constructs a line plot.
     * @param chart The chart that the plot belongs to.
     * @param horizontal The horizontal axis.
     * @param vertical The vertical axis.
     */
    public LinePlot(Axis horizontal, Axis vertical) throws AxisNotCompatibleException,
                                                           PlotNotCompatibleException {

	super(horizontal, vertical);
        this.renderer = new LineAndShapeRenderer(LineAndShapeRenderer.SHAPES_AND_LINES);
        this.introGapPercent = DEFAULT_INTRO_GAP;
        this.trailGapPercent = DEFAULT_TRAIL_GAP;

    }

    /**
     * Returns the intro gap.
     * @return The intro gap as a percentage of the available width.
     */
    public double getIntroGapPercent() {
        return this.introGapPercent;
    }

    /**
     * Sets the intro gap.
     * @param The gap as a percentage of the total width.
     */
    public void setIntroGapPercent(double percent) {

        // check arguments...
        if ((percent<=0.0) || (percent>MAX_INTRO_GAP)) {
            throw new IllegalArgumentException("LinePlot.setIntroGapPercent(double): "
                                               +"gap percent outside valid range.");
        }

        // make the change...
        if (introGapPercent!=percent) {
            introGapPercent = percent;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the trail gap.
     * @return The trail gap as a percentage of the available width.
     */
    public double getTrailGapPercent() {
        return this.introGapPercent;
    }

    /**
     * Sets the trail gap.
     * @param The gap as a percentage of the total width.
     */
    public void setTrailGapPercent(double percent) {

        // check arguments...
        if ((percent<=0.0) || (percent>MAX_TRAIL_GAP)) {
            throw new IllegalArgumentException("LinePlot.setTrailGapPercent(double): "
                                               +"gap percent outside valid range.");
        }

        // make the change...
        if (trailGapPercent!=percent) {
            trailGapPercent = percent;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * A convenience method that returns the dataset for the plot, cast as a CategoryDataset.
     */
    public CategoryDataset getDataset() {
	return (CategoryDataset)chart.getDataset();
    }

    /**
     * A convenience method that returns a reference to the horizontal axis cast as a
     * CategoryAxis.
     */
    public CategoryAxis getCategoryAxis() {
	return (CategoryAxis)horizontalAxis;
    }

    /**
     * A convenience method that returns a reference to the vertical axis cast as a
     * VerticalNumberAxis.
     */
    public VerticalNumberAxis getValueAxis() {
	return (VerticalNumberAxis)verticalAxis;
    }

    /**
     * A convenience method that returns a list of the categories in the data source.
     */
    public java.util.List getCategories() {
	return getDataset().getCategories();
    }

    /**
     * Returns the x-coordinate (in Java 2D User Space) of the center of the specified category.
     * @param category The category (zero-based index).
     * @param area The region within which the plot will be drawn.
     */
    public double getCategoryCoordinate(int category, Rectangle2D area) {

        // check arguments...
	int count = getDataset().getCategoryCount();
        if ((category<0) || (category>=count)) {
            throw new IllegalArgumentException("LinePlot.getCategoryCoordinate(...): "
                                               +"category outside valid range.");
        }
        if (area==null) {
            throw new IllegalArgumentException("LinePlot.getCategoryCoordinate(...): "
                                               +"null area not permitted.");
        }

        // calculate result...
        double result = area.getX() + area.getWidth()/2;
        if (count>1) {
            double available = area.getWidth() * (1-introGapPercent-trailGapPercent);
	    result = area.getX()+(introGapPercent*area.getWidth())
                                +(category*1.0/(count-1.0))*available;
        }

        return result;

    }

    /**
     * Checks the compatibility of a horizontal axis, returning true if the axis is compatible with
     * the plot, and false otherwise.
     * @param axis The horizontal axis.
     */
    public boolean isCompatibleHorizontalAxis(Axis axis) {
	if (axis instanceof CategoryAxis) {
	    return true;
	}
	else return false;
    }

    /**
     * Checks the compatibility of a vertical axis, returning true if the axis is compatible with
     * the plot, and false otherwise.
     * @param axis The vertical axis;
     */
    public boolean isCompatibleVerticalAxis(Axis axis) {
	if (axis instanceof VerticalNumberAxis) {
	    return true;
	}
	else return false;
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     * @param g2 The graphics device;
     * @param drawArea The area within which the plot should be drawn;
     */
    public void draw(Graphics2D g2, Rectangle2D drawArea) {

        // adjust the drawing area for the plot insets (if any)...
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

	// and thus the area available for plotting...
	Rectangle2D plotArea = new Rectangle2D.Double(drawArea.getX()+vAxisArea.getWidth(),
						      drawArea.getY(),
						      drawArea.getWidth()-vAxisArea.getWidth(),
						      drawArea.getHeight()-hAxisAreaHeight);

        // draw the background and axes...
	drawOutlineAndBackground(g2, plotArea);
	getCategoryAxis().draw(g2, drawArea, plotArea);
	getValueAxis().draw(g2, drawArea, plotArea);

        // now get the data and plot the lines (or shapes, or lines and shapes)...
        CategoryDataset data = this.getDataset();
        if (data!=null) {
            Shape originalClip=g2.getClip();
	    g2.clip(plotArea);

	    int seriesCount = data.getSeriesCount();
            int categoryCount = data.getCategoryCount();
            int categoryIndex = 0;
            Object previousCategory = null;
            Iterator iterator = data.getCategories().iterator();
            while (iterator.hasNext()) {

                Object category = iterator.next();
                for (int series=0; series<seriesCount; series++) {
                    renderer.drawHorizontalCategoryItem(g2, plotArea, this, getValueAxis(), data,
                                                        series, category, categoryIndex,
                                                        previousCategory);


                }
                previousCategory = category;
                categoryIndex++;

            }

	    g2.setClip(originalClip);
        }

    }

    /**
     * Returns a short string describing the plot type;
     */
    public String getPlotType() {
	return "Line Plot";
    }

    /**
     * Returns the minimum value in the range, since this is plotted against the vertical axis for
     * LinePlot.
     */
    public Number getMinimumVerticalDataValue() {

	Dataset data = this.getChart().getDataset();
	if (data!=null) {
	    return Datasets.getMinimumRangeValue(data);
	}
	else return null;

    }

    /**
     * Returns the maximum value in either the domain or the range, whichever is displayed against
     * the vertical axis for the particular type of plot implementing this interface.
     */
    public Number getMaximumVerticalDataValue() {

	Dataset data = this.getChart().getDataset();
	if (data!=null) {
	    return Datasets.getMaximumRangeValue(data);
	}
	else return null;
    }

}