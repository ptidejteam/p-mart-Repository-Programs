/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
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
 * ------------
 * BarPlot.java
 * ------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: BarPlot.java,v 1.1 2007/10/10 18:57:06 vauchers Exp $
 *
 * Changes (from 21-Jun-2001)
 * --------------------------
 * 21-Jun-2001 : Removed redundant JFreeChart parameter from constructors (DG);
 * 21-Aug-2001 : Added standard header. Fixed DOS encoding problem (DG);
 * 18-Sep-2001 : Updated header (DG);
 * 15-Oct-2001 : Data source classes moved to com.jrefinery.data.* (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 23-Oct-2001 : Changed intro and trail gaps on bar plots to use percentage of available space
 *               rather than a fixed number of units (DG);
 * 12-Dec-2001 : Changed constructors to protected (DG);
 * 13-Dec-2001 : Added tooltips (DG);
 * 16-Jan-2002 : Increased maximum intro and trail gap percents, plus added some argument checking
 *               code.  Thanks to Taoufik Romdhane for suggesting this (DG);
 * 05-Feb-2002 : Added accessor methods for the tooltip generator, incorporated alpha-transparency
 *               for Plot and subclasses (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import com.jrefinery.data.*;
import com.jrefinery.chart.event.*;
import com.jrefinery.chart.tooltips.*;

/**
 * A general plotting class that uses data from a CategoryDataset, and presents that data in the
 * form of bars.
 * @see Plot
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
    protected static final double MAX_INTRO_GAP_PERCENT = 0.20;  // 20 percent

    /** The maximum gap after the last bar in the plot. */
    protected static final double MAX_TRAIL_GAP_PERCENT = 0.20;  // 20 percent

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

    /** The tool tip generator. */
    protected CategoryToolTipGenerator toolTipGenerator;

    /**
     * Constructs a bar plot, using default values where necessary.
     * @param horizontalAxis The horizontal axis.
     * @param verticalAxis The vertical axis.
     */
    protected BarPlot(Axis horizontalAxis, Axis verticalAxis) {

	this(horizontalAxis, verticalAxis,
             Plot.DEFAULT_INSETS,
             Plot.DEFAULT_BACKGROUND_PAINT,
             null, // background image
             Plot.DEFAULT_BACKGROUND_ALPHA,
             Plot.DEFAULT_OUTLINE_STROKE,
             Plot.DEFAULT_OUTLINE_PAINT,
             Plot.DEFAULT_FOREGROUND_ALPHA,
             DEFAULT_INTRO_GAP_PERCENT,
             DEFAULT_TRAIL_GAP_PERCENT,
             DEFAULT_CATEGORY_GAPS_PERCENT,
             DEFAULT_ITEM_GAPS_PERCENT,
             null);  // tool tip generator

    }

    /**
     * Constructs a bar plot.
     * @param horizontalAxis The horizontal axis.
     * @param verticalAxis The vertical axis.
     * @param insets The insets for the plot.
     * @param backgroundPaint An optional color for the plot's background.
     * @param backgroundImage An optional image for the plot's background.
     * @param backgroundAlpha Alpha-transparency for the plot's background.
     * @param outlineStroke The stroke used to draw the plot outline.
     * @param outlinePaint The paint used to draw the plot outline.
     * @param foregroundAlpha The alpha transparency.
     * @param introGapPercent The gap before the first bar in the plot, as a percentage of the
     *                        available drawing space.
     * @param trailGapPercent The gap after the last bar in the plot, as a percentage of the
     *                        available drawing space.
     * @param categoryGapsPercent The percentage of drawing space allocated to the gap between the
     *                            last bar in one category and the first bar in the next category.
     * @param itemGapsPercent The gap between bars within the same category.
     * @param toolTipGenerator The tool tip generator.
     */
    protected BarPlot(Axis horizontalAxis, Axis verticalAxis,
                      Insets insets,
                      Paint backgroundPaint, Image backgroundImage, float backgroundAlpha,
                      Stroke outlineStroke, Paint outlinePaint,
                      float foregroundAlpha,
		      double introGapPercent, double trailGapPercent,
                      double categoryGapsPercent, double itemGapsPercent,
                      CategoryToolTipGenerator toolTipGenerator) {

	super(horizontalAxis, verticalAxis,
              insets,
              backgroundPaint, backgroundImage, backgroundAlpha,
              outlineStroke, outlinePaint,
              foregroundAlpha);

        this.insets = insets;
	this.introGapPercent = introGapPercent;
	this.trailGapPercent = trailGapPercent;
	this.categoryGapsPercent = categoryGapsPercent;
	this.itemGapsPercent = itemGapsPercent;
        this.toolTipGenerator = toolTipGenerator;

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
     * @param axis The new axis.
     */
    public void setVerticalAxis(Axis axis) {
	super.setVerticalAxis(axis);
    }

    /**
     * Sets the horizontal axis for the plot.
     * @param axis The new axis.
     */
    public void setHorizontalAxis(Axis axis) {
	super.setHorizontalAxis(axis);
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
     * @param percent The new gap value, expressed as a percentage of the width of the plot area
     *                (0.05 = 5 percent).
     */
    public void setIntroGapPercent(double percent) {

        // check argument...
        if ((percent<0.0) || (percent>MAX_INTRO_GAP_PERCENT)) {
            throw new IllegalArgumentException("BarPlot.setIntroGapPercent(double): argument "
                                              +"outside valid range.");
        }

        // make the change...
	if (this.introGapPercent!=percent) {
            this.introGapPercent = percent;
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
     * @param percent The new gap value, expressed as a percentage of the width of the plot area
     *                (0.05 = 5 percent).
     */
    public void setTrailGapPercent(double percent) {

        // check argument...
        if ((percent<0.0) || (percent>MAX_TRAIL_GAP_PERCENT)) {
            throw new IllegalArgumentException("BarPlot.setTrailGapPercent(double): argument "
                                              +"outside valid range.");
        }

        // make the change...
	if (this.trailGapPercent!=percent) {
            trailGapPercent = percent;
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
     * @param percent The new gap value, expressed as a percentage of the width of the plot area
     *                (0.05 = 5 percent).
     */
    public void setCategoryGapsPercent(double percent) {

        // check argument...
        if ((percent<0.0) || (percent>MAX_CATEGORY_GAPS_PERCENT)) {
            throw new IllegalArgumentException("BarPlot.setCategoryGapsPercent(double): argument "
                                              +"outside valid range.");
        }

        // make the change...
	if (this.categoryGapsPercent!=percent) {
            this.categoryGapsPercent=percent;
	    notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the percentage of the drawing space that is allocated to providing gaps between the
     * items in a category.
     */
    public double getItemGapsPercent() {
	return itemGapsPercent;
    }

    /**
     * Sets the gap between one bar and the next within the same category, and notifies registered
     * listeners that the plot has been modified.
     * @param percent The new gap value, expressed as a percentage of the width of the plot area
     *                (0.05 = 5 percent).
     */
    public void setItemGapsPercent(double percent) {

        // check argument...
        if ((percent<0.0) || (percent>MAX_ITEM_GAPS_PERCENT)) {
            throw new IllegalArgumentException("BarPlot.setItemGapsPercent(double): argument "
                                              +"outside valid range.");
        }

        // make the change...
	if (percent!=this.itemGapsPercent) {
            this.itemGapsPercent = percent;
	    notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the tooltip generator for the plot.
     * @return The tooltip generator for the plot.
     */
    public CategoryToolTipGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }

    /**
     * Sets the tooltip generator for the plot.
     * @param generator The new generator.
     */
    public void setToolTipGenerator(CategoryToolTipGenerator generator) {
        this.toolTipGenerator = generator;
    }

    /**
     * A convenience method that returns a list of the categories in the data source.
     */
    public java.util.List getCategories() {
	return getDataset().getCategories();
    }

    /**
     * Returns the range (value) axis for the plot.
     * @return The range (value) axis for the plot.
     */
    public abstract ValueAxis getRangeAxis();

    /**
     * Zooms (in or out) on the plot's value axis.
     * @param percent The zoom amount.
     */
    public void zoom(double percent) {

        ValueAxis rangeAxis = this.getRangeAxis();
        if (percent>0.0) {
            double range = rangeAxis.getMaximumAxisValue()-rangeAxis.getMinimumAxisValue();
            double scaledRange = range * percent;
            rangeAxis.setAnchoredRange(scaledRange);
        }
        else {
            rangeAxis.setAutoRange(true);
        }

    }

}