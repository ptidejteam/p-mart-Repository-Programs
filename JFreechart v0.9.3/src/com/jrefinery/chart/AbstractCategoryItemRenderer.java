/* ============================================
 * JFreeChart : a free Java chart class library
 * ============================================
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
 * ---------------------------------
 * AbstractCategoryItemRenderer.java
 * ---------------------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Richard Atkinson;
 *
 * $Id: AbstractCategoryItemRenderer.java,v 1.1 2007/10/10 19:52:14 vauchers Exp $
 *
 * Changes:
 * --------
 * 29-May-2002 : Version 1 (DG);
 * 06-Jun-2002 : Added accessor methods for the tool tip generator (DG);
 * 11-Jun-2002 : Made constructors protected (DG);
 * 26-Jun-2002 : Added axis to initialise method (DG);
 * 05-Aug-2002 : Added urlGenerator member variable plus accessors (RA);
 * 22-Aug-2002 : Added categoriesPaint attribute, based on code submitted by Janet Banks.
 *               This can be used when there is only one series, and you want each category
 *               item to have a different color (DG);
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.chart.tooltips.StandardCategoryToolTipGenerator;
import com.jrefinery.chart.urls.CategoryURLGenerator;

/**
 * Abstract base class for category item renderers.
 */
public abstract class AbstractCategoryItemRenderer extends AbstractRenderer
                                                   implements CategoryItemRenderer {

    /** A flag to indicate whether or not to use the categoriesPaint array. */
    protected boolean useCategoriesPaint;

    /** Paint objects for categories (optional). */
    protected Paint[] categoriesPaint;

    /** The tooltip generator. */
    protected CategoryToolTipGenerator toolTipGenerator;

    /** The URL generator. */
    protected CategoryURLGenerator urlGenerator;

    /**
     * Default constructor, creates a renderer with a standard tool tip generator.
     */
    protected AbstractCategoryItemRenderer() {
        this(new StandardCategoryToolTipGenerator());
    }

    /**
     * Constructs a new renderer with the specified tooltip generator.
     *
     * @param toolTipGenerator The tool tip generator.
     */
    protected AbstractCategoryItemRenderer(CategoryToolTipGenerator toolTipGenerator) {
        this(toolTipGenerator, null);
    }

    /**
     * Constructs a new renderer with the specified tooltip generator.
     *
     * @param toolTipGenerator The tool tip generator.
     * @param urlGenerator The URL generator.
     */
    protected AbstractCategoryItemRenderer(CategoryToolTipGenerator toolTipGenerator,
                                           CategoryURLGenerator urlGenerator) {

        this.categoriesPaint = null;
        this.toolTipGenerator = toolTipGenerator;
        this.urlGenerator = urlGenerator;

    }

    /**
     * Returns the paint to use for the categories when there is just one series.
     * <P>
     * If this is null, the categories will all have the same color (that of the series).
     *
     * @return The paint for the categories.
     */
    public Paint[] getCategoriesPaint() {
        return this.categoriesPaint;
    }

    /**
     * Sets the paint to be used for categories under special circumstances.
     * <P>
     * This attribute is provided for the situation where there is just one series, and you want
     * each category item to be plotted using a different color (ordinarily, the series color is
     * used for all the items in the series).
     * <P>
     * May not be observed by all subclasses yet.
     *
     * @param paint The colors.
     */
    public void setCategoriesPaint(Paint[] paint) {

        Object oldValue = this.categoriesPaint;
        this.categoriesPaint = paint;
        firePropertyChanged("renderer.CategoriesPaint", oldValue, paint);

    }

    /**
     * Returns the paint for a specific category (possibly null).
     *
     * @param index The category index.
     *
     * @return The paint for the category.
     */
    public Paint getCategoryPaint(int index) {

        Paint result = null;
        if (this.categoriesPaint!=null) {
            result = this.categoriesPaint[index % categoriesPaint.length];
        }
        return result;
    }

    /**
     * Returns the tool tip generator.
     *
     * @return The tool tip generator.
     */
    public CategoryToolTipGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }

    /**
     * Sets the tool tip generator.
     *
     * @param toolTipGenerator The tool tip generator.
     */
    public void setToolTipGenerator(CategoryToolTipGenerator toolTipGenerator) {

        Object oldValue = this.toolTipGenerator;
        this.toolTipGenerator = toolTipGenerator;
        firePropertyChanged("renderer.ToolTipGenerator", oldValue, toolTipGenerator);

    }

    /**
     * Returns the URL generator for HTML image maps.
     *
     * @return The URL generator.
     */
    public CategoryURLGenerator getURLGenerator() {
        return this.urlGenerator;
    }

    /**
     * Sets the URL generator for HTML image maps.
     *
     * @param urlGenerator The URL generator.
     */
    public void setURLGenerator(CategoryURLGenerator urlGenerator) {

        Object oldValue = this.urlGenerator;
        this.urlGenerator = urlGenerator;
        firePropertyChanged("renderer.URLGenerator", oldValue, urlGenerator);

    }

    /**
     * Initialises the renderer.
     * <P>
     * Stores a reference to the ChartRenderingInfo object (which might be null), and then
     * sets the useCategoriesPaint flag according to the special case conditions a) there is
     * only one series and b) the categoriesPaint array is not null.
     *
     * @param g2  The graphics device.
     * @param dataArea  The data area.
     * @param plot  The plot.
     * @param axis  The axis.
     * @param data  The data.
     * @param info  An object for returning information about the structure of the chart.
     *
     */
    public void initialise(Graphics2D g2, Rectangle2D dataArea, CategoryPlot plot,
                           ValueAxis axis, CategoryDataset data, ChartRenderingInfo info) {

        this.info = info;

        // the renderer can use different colors for the categories if there is one series and
        // the categoriesPaint array has been populated...
        this.useCategoriesPaint = (data.getSeriesCount()==1) && (this.categoriesPaint!=null);

    }

    /**
     * Returns a flag indicating whether the items within one category are
     * stacked up when represented by the renderer.
     *
     * @return The flag.
     */
    public boolean isStacked() {
        return false;
    }

    /**
     * Returns the area that the axes (and data) must fit into.
     * <P>
     * Often this is the same as the plotArea, but sometimes a smaller region
     * should be used (for example, the 3D charts require the axes to use less
     * space in order to leave room for the 'depth' part of the chart).
     *
     * @param plotArea The plot area.
     *
     * @return the area that the axes (and date) must fit into.
     */
    public Rectangle2D getAxisArea(Rectangle2D plotArea) {
        return plotArea;
    }

    /**
     * Returns the clip region... usually returns the dataArea, but some charts
     * (e.g. 3D) have non rectangular clip regions.
     *
     * @param dataArea The data area.
     *
     * @return  the clip region.
     */
    public Shape getDataClipRegion(Rectangle2D dataArea) {
        return dataArea;
    }

    /**
     * Draws the background for the plot.
     * <P>
     * For most charts, the axisDataArea and the dataClipArea are the same.
     *
     * @param g2  The graphics device.
     * @param plot  The plot.
     * @param axisDataArea  The area inside the axes.
     * @param dataClipArea  The data clip area.
     */
    public void drawPlotBackground(Graphics2D g2, CategoryPlot plot,
                                   Rectangle2D axisDataArea, Shape dataClipArea) {

        if (plot.getBackgroundPaint()!=null) {
            g2.setPaint(plot.getBackgroundPaint());
            g2.fill(dataClipArea);
        }

        if ((plot.getOutlineStroke()!=null) && (plot.getOutlinePaint()!=null)) {
            g2.setStroke(plot.getOutlineStroke());
            g2.setPaint(plot.getOutlinePaint());
            g2.draw(dataClipArea);
        }

    }

}
