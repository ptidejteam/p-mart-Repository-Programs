/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
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
 * -----------------
 * CategoryPlot.java
 * -----------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: CategoryPlot.java,v 1.1 2007/10/10 19:02:36 vauchers Exp $
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
 * 06-Mar-2002 : Updated import statements (DG);
 * 14-Mar-2002 : Renamed BarPlot.java --> CategoryPlot.java, and changed code to use the
 *               CategoryItemRenderer interface (DG);
 * 22-Mar-2002 : Dropped the getCategories() method (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.Insets;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.util.List;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.chart.event.PlotChangeEvent;
import com.jrefinery.chart.tooltips.CategoryToolTipGenerator;

/**
 * A general plotting class that uses data from a CategoryDataset, and uses a plug-in renderer
 * to draw individual data items.
 *
 * @see Plot
 * @see CategoryItemRenderer
 *
 */
public abstract class CategoryPlot extends Plot {

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

    /** The renderer for the data items. */
    protected CategoryItemRenderer renderer;

    /** The gap before the first item in the plot. */
    protected double introGapPercent;

    /** The gap after the last item in the plot. */
    protected double trailGapPercent;

    /**
     * The percentage of the overall drawing space allocated to providing gaps between the last
     * item in one category and the first item in the next category.
     */
    protected double categoryGapsPercent;

    /** The gap between items within the same category. */
    protected double itemGapsPercent;

    /** The tool tip generator. */
    protected CategoryToolTipGenerator toolTipGenerator;

    /**
     * Constructs a category plot, using default values where necessary.
     *
     * @param horizontalAxis The horizontal axis.
     * @param verticalAxis The vertical axis.
     * @param renderer The item renderer.
     *
     */
    protected CategoryPlot(Axis horizontalAxis, Axis verticalAxis, CategoryItemRenderer renderer) {

	this(horizontalAxis, verticalAxis, renderer,
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
     * Constructs a category plot.
     *
     * @param horizontalAxis The horizontal axis.
     * @param verticalAxis The vertical axis.
     * @param renderer The item renderer.
     * @param insets The insets for the plot.
     * @param backgroundPaint An optional color for the plot's background.
     * @param backgroundImage An optional image for the plot's background.
     * @param backgroundAlpha Alpha-transparency for the plot's background.
     * @param outlineStroke The stroke used to draw the plot outline.
     * @param outlinePaint The paint used to draw the plot outline.
     * @param foregroundAlpha The alpha transparency.
     * @param introGapPercent The gap before the first item in the plot, as a percentage of the
     *                        available drawing space.
     * @param trailGapPercent The gap after the last item in the plot, as a percentage of the
     *                        available drawing space.
     * @param categoryGapsPercent The percentage of drawing space allocated to the gap between the
     *                            last item in one category and the first item in the next category.
     * @param itemGapsPercent The gap between items within the same category.
     * @param toolTipGenerator The tool tip generator.
     *
     */
    protected CategoryPlot(Axis horizontalAxis, Axis verticalAxis, CategoryItemRenderer renderer,
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

        this.renderer = renderer;
        this.insets = insets;
	this.introGapPercent = introGapPercent;
	this.trailGapPercent = trailGapPercent;
	this.categoryGapsPercent = categoryGapsPercent;
	this.itemGapsPercent = itemGapsPercent;
        this.toolTipGenerator = toolTipGenerator;

    }

    /**
     * Returns a reference to the renderer for the plot.
     *
     * @return The renderer.
     */
    public CategoryItemRenderer getRenderer() {
        return this.renderer;
    }

    /**
     * Sets the renderer for the plot.
     * <p>
     * If you set the renderer to null, no data will be plotted on the chart.
     *
     * @param renderer The renderer (null permitted).
     */
    public void setRenderer(CategoryItemRenderer renderer) {

        this.renderer = renderer;
        this.notifyListeners(new PlotChangeEvent(this));

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
     *
     * @return The tooltip generator.
     */
    public CategoryToolTipGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }

    /**
     * Sets the tooltip generator for the plot.
     *
     * @param generator The new generator.
     */
    public void setToolTipGenerator(CategoryToolTipGenerator generator) {
        this.toolTipGenerator = generator;
    }

    /**
     * A convenience method that returns the dataset for the plot, cast as a
     * CategoryDataset.
     */
    public CategoryDataset getDataset() {
	return (CategoryDataset)chart.getDataset();
    }

    /**
     * Returns the domain axis for the category plot.  That is, the axis that displays the
     * categories.
     *
     * @return The domain axis.
     */
    public abstract CategoryAxis getDomainAxis();

    /**
     * Returns the range axis for the category plot.  That is, the axis that displays the values.
     *
     * @return The range axis.
     */
    public abstract ValueAxis getRangeAxis();

    /**
     * Returns the x or y coordinate (depending on the orientation of the plot) in Java 2D User
     * Space of the center of the specified category.
     *
     * @param category The category (zero-based index).
     * @param area The region within which the plot will be drawn.
     */
    public abstract double getCategoryCoordinate(int category, Rectangle2D area);

    /**
     * Zooms (in or out) on the plot's value axis.
     * <p>
     * If the value 0.0 is passed in as the zoom percent, the auto-range calculation for the axis
     * is restored (which sets the range to include the minimum and maximum data values, thus
     * displaying all the data).
     *
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