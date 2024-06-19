/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: BarPlot.java,v 1.1 2007/10/10 18:54:39 vauchers Exp $
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 * (C) Copyright 2000, 2001, Simba Management Limited;
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
 * 21-Aug-2001 : Added standard header. Fixed DOS encoding problem (DG);
 * 18-Sep-2001 : Updated e-mail address in header (DG);
 * 15-Oct-2001 : Data source classes moved to com.jrefinery.data.* (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 23-Oct-2001 : Changed intro and trail gaps on bar plots to use percentage of available space
 *               rather than a fixed number of units (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import com.jrefinery.data.*;
import com.jrefinery.chart.event.*;

/**
 * A general plotting class that uses data from a CategoryDataset, and presents that data in the
 * form of bars.
 * @see Plot
 * @see CategoryDataSource
 */
public abstract class BarPlot extends Plot implements CategoryPlot {

    /** Default value for the gap before the first bar in the plot. */
    protected static final double DEFAULT_INTRO_GAP_PERCENT = 0.05;  // 5 percent

    /** Default value for the gap after the last bar in the plot. */
    protected static final double DEFAULT_TRAIL_GAP_PERCENT = 0.05;  // 5 percent

    /** Default value for the total gap to be distributed between categories. */
    protected static final double DEFAULT_CATEGORY_GAPS_PERCENT = 0.20;  // 20 percent

    /** Default value for the total gap to be distributed between items within a category. */
    protected static final double DEFAULT_ITEM_GAPS_PERCENT = 0.15;  // 15 percent

    /** The maximum gap before the first bar in the plot. */
    protected static final double MAX_INTRO_GAP_PERCENT = 0.10;  // 10 percent

    /** The maximum gap after the last bar in the plot. */
    protected static final double MAX_TRAIL_GAP_PERCENT = 0.10;  // 10 percent

    /** The maximum gap to be distributed between categories. */
    protected static final double MAX_CATEGORY_GAPS_PERCENT = 0.30;  // 30 percent

    /** The maximum gap to be distributed between items within categories. */
    protected static final double MAX_ITEM_GAPS_PERCENT = 0.30;  // 30 percent

    /** The gap before the first bar in the plot. */
    protected double introGapPercent;

    /** The gap after the last bar in the plot. */
    protected double trailGapPercent;

    /**
     * The percentage of the overall drawing space allocated to providing gaps between the last
     * bar in one category and the first bar in the next category.
     */
    protected double categoryGapsPercent;

    /** The gap between bars within the same category. */
    protected double itemGapsPercent;

    /**
     * Standard constructor: returns a BarPlot with attributes specified by the caller.
     * @param horizontal The horizontal axis;
     * @param vertical The vertical axis;
     * @param introGapPercent The gap before the first bar in the plot, as a percentage of the
     *                        available drawing space;
     * @param trailGapPercent The gap after the last bar in the plot, as a percentage of the
     *                        available drawing space;
     * @param categoryGapsPercent The percentage of drawing space allocated to the gap between the
     *                           last bar in one category and the first bar in the next category;
     * @param itemGapsPercent The gap between bars within the same category;
     */
    public BarPlot(Axis horizontal, Axis vertical, Insets insets,
		   double introGapPercent, double trailGapPercent,
                   double categoryGapsPercent, double itemGapsPercent)
            throws AxisNotCompatibleException, PlotNotCompatibleException {

	super(horizontal, vertical);
	this.insets = insets;
	this.introGapPercent = introGapPercent;
	this.trailGapPercent = trailGapPercent;
	this.categoryGapsPercent = categoryGapsPercent;
	this.itemGapsPercent = itemGapsPercent;
    }

    /**
     * Constructs a bar plot with the specified axes...other attributes take default values.
     */
    public BarPlot(Axis horizontalAxis, Axis verticalAxis) throws AxisNotCompatibleException,
                                                                  PlotNotCompatibleException
    {
	this(horizontalAxis, verticalAxis,
             new Insets(2, 2, 2, 2),
             DEFAULT_INTRO_GAP_PERCENT,
             DEFAULT_TRAIL_GAP_PERCENT,
             DEFAULT_CATEGORY_GAPS_PERCENT,
             DEFAULT_ITEM_GAPS_PERCENT);
    }

    /**
     * A convenience method that returns the dataset for the plot, cast as a
     * CategoryDataset.
     */
    public CategoryDataset getDataset() {
	return (CategoryDataset)chart.getDataset();
    }

    /**
     * Sets the vertical axis for the plot.
     * <P>
     * This method should throw an exception if the axis doesn't implement the required interfaces.
     * @param axis The new vertical axis.
     */
    public void setVerticalAxis(Axis axis) throws AxisNotCompatibleException {
	super.setVerticalAxis(axis);
    }

    /**
     * Sets the horizontal axis for the plot.
     * <P>
     * This method should throw an exception if the axis doesn't implement the required interfaces.
     * @param axis The new horizontal axis.
     */
    public void setHorizontalAxis(Axis axis) throws AxisNotCompatibleException {
	super.setHorizontalAxis(axis);
    }

    /**
     * A convenience method that returns a list of the categories in the data source.
     */
    public java.util.List getCategories() {
	return getDataset().getCategories();
    }

    /**
     * Returns the gap before the first bar on the chart, as a percentage of the available drawing
     * space (0.05 = 5 percent).
     */
    public double getIntroGapPercent() {
	return introGapPercent;
    }

    /**
     * Sets the gap before the first bar on the chart, and notifies registered listeners that the
     * plot has been modified.
     * @param percent The new gap value.
     */
    public void setIntroGapPercent(double percent) {
	if (this.introGapPercent!=percent) {
            this.introGapPercent = Math.min(percent, MAX_INTRO_GAP_PERCENT);
	    notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Returns the gap following the last bar on the chart, as a percentage of the available
     * drawing space.
     */
    public double getTrailGapPercent() {
	return trailGapPercent;
    }

    /**
     * Sets the gap after the last bar on the chart, and notifies registered listeners that the plot
     * has been modified.
     * @param percent The new gap value.
     */
    public void setTrailGapPercent(double percent) {
	if (this.trailGapPercent!=percent) {
            trailGapPercent = Math.min(percent, MAX_TRAIL_GAP_PERCENT);
	    notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Returns the percentage of the drawing space that is allocated to providing gaps between the
     * categories.
     */
    public double getCategoryGapsPercent() {
	return categoryGapsPercent;
    }

    /**
     * Sets the gap between the last bar in one category and the first bar in the
     * next category, and notifies registered listeners that the plot has been modified.
     * @param gap The new gap value.
     */
    public void setCategoryGapsPercent(double percent) {
	if (this.categoryGapsPercent!=percent) {
            this.categoryGapsPercent=Math.min(percent, MAX_CATEGORY_GAPS_PERCENT);
	    notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Returns the gap between one bar and the next within the same category, measured in Java 2D
     * User Space units.
     */
    public double getItemGapsPercent() {
	return itemGapsPercent;
    }

    /**
     * Sets the gap between one bar and the next within the same category, and notifies registered
     * listeners that the plot has been modified.
     * @param percent The new gap value.
     */
    public void setItemGapsPercent(double percent) {
	if (percent!=this.itemGapsPercent) {
            this.itemGapsPercent = Math.min(percent, MAX_ITEM_GAPS_PERCENT);
	    notifyListeners(new PlotChangeEvent(this));
        }
    }

}