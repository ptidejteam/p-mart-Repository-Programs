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
 * ----------------
 * BarRenderer.java
 * ----------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: BarRenderer.java,v 1.1 2007/10/10 19:02:26 vauchers Exp $
 *
 * Changes
 * -------
 * 14-Mar-2002 : Version 1 (DG);
 * 23-May-2002 : Added tooltip generator to renderer (DG);
 * 29-May-2002 : Moved tooltip generator to abstract super-class (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.chart.tooltips.StandardCategoryToolTipGenerator;

/**
 * A base class for category item renderers that draw bars.
 */
public abstract class BarRenderer extends AbstractCategoryItemRenderer {

    /** Constant that controls the minimum width before a bar has an outline drawn. */
    protected static final double BAR_OUTLINE_WIDTH_THRESHOLD = 3.0;

    /** The total width of the categories. */
    protected double categorySpan;

    /** The total width of the category gaps. */
    protected double categoryGapSpan;

    /** The total width of the items within a category. */
    protected double itemSpan;

    /** The total width of the item gaps. */
    protected double itemGapSpan;

    /** The width of a single item. */
    protected double itemWidth;

    /** The data value ZERO translated to Java2D user space. */
    protected double zeroInJava2D;

    public BarRenderer(CategoryToolTipGenerator toolTipGenerator) {
        super(toolTipGenerator);
    }

    /**
     * Initialises the renderer.
     * <p>
     * This method gets called once at the start of the process of drawing a chart.
     *
     * @param g2 The graphics device.
     * @param dataArea The area in which the data is to be plotted.
     * @param plot The plot.
     * @param data The data.
     * @param info Collects chart rendering information for return to caller.
     *
     */
    public void initialise(Graphics2D g2,
                           Rectangle2D dataArea,
                           CategoryPlot plot,
                           CategoryDataset data,
                           ChartRenderingInfo info) {
        this.info = info;
    }

    /**
     * Returns the number of bar widths per category, which depends on whether or not the renderer
     * stacks bars.
     *
     * @param data The dataset.
     */
    public abstract int barWidthsPerCategory(CategoryDataset data);

    /**
     * Returns true if there are gaps between items within a category, and false otherwise.  Again,
     * this depends on whether or not the bars are stacked.
     */
    public abstract boolean hasItemGaps();

    /**
     * Calculates some dimensions required for plotting the bars.
     *
     * @param g2 The graphics device.
     * @param dataArea The area within the axes.
     * @param plot The plot.
     * @param data The data.
     * @param span ???
     */
    protected void calculateCategoryAndItemSpans(Graphics2D g2,
                                                 Rectangle2D dataArea,
                                                 CategoryPlot plot,
                                                 CategoryDataset data,
                                                 double span) {

        // work out the span dimensions for the categories...
        int seriesCount = data.getSeriesCount();
        int categoryCount = data.getCategoryCount();
        int barCount = this.barWidthsPerCategory(data);

        categorySpan = 0.0;
        categoryGapSpan = 0.0;
        if (categoryCount>1) {
            double used = (1-plot.getIntroGapPercent()
                            -plot.getTrailGapPercent()
                            -plot.getCategoryGapsPercent());
            categorySpan = span*used;
            categoryGapSpan = span*plot.getCategoryGapsPercent();
        }
        else {
            double used = (1-plot.getIntroGapPercent()-plot.getTrailGapPercent());
            categorySpan = span*used;
        }

        // work out the item span...
        itemSpan = categorySpan;
        itemGapSpan = 0.0;
        if (seriesCount>1) {
            if (this.hasItemGaps()) {
                itemGapSpan = span*plot.getItemGapsPercent();
                itemSpan = itemSpan - itemGapSpan;
            }
        }
        itemWidth = itemSpan/(categoryCount*this.barWidthsPerCategory(data));

        zeroInJava2D = plot.getRangeAxis().translateValueToJava2D(0.0, dataArea);

    }

    /**
     * Returns a flag indicating whether or not the renderer stacks values within each category.
     * This has an effect on the minimum and maximum values required for the axis to show all the
     * data values.
     * <P>
     * Subclasses should override this method as necessary.
     */
    public boolean isStacked() {
        return false;
    }

}