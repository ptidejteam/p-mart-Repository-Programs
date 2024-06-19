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
 * ---------------------------
 * AbstractXYItemRenderer.java
 * ---------------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: AbstractCategoryItemRenderer.java,v 1.1 2007/10/10 19:41:59 vauchers Exp $
 *
 * Changes:
 * --------
 * 29-May-2002 : Version 1 (DG);
 * 06-Jun-2002 : Added accessor methods for the tool tip generator (DG);
 * 11-Jun-2002 : Made constructors protected (DG);
 * 26-Jun-2002 : Added axis to initialise method (DG);
 *
 */

package com.jrefinery.chart;

import com.jrefinery.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.chart.tooltips.StandardCategoryToolTipGenerator;
import com.jrefinery.data.CategoryDataset;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * Abstract base class for category item renderers.
 */
public abstract class AbstractCategoryItemRenderer implements CategoryItemRenderer {

    /** Local copy of the info reference. */
    protected ChartRenderingInfo info;

    /** The tooltip generator. */
    protected CategoryToolTipGenerator toolTipGenerator;

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
        this.toolTipGenerator = toolTipGenerator;
        this.info = null;
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
        this.toolTipGenerator = toolTipGenerator;
    }

    /**
     * Initialises the renderer.
     * <P>
     * This method gets called once each time the chart is drawn, and provides the renderer an
     * opportunity to initialise itself.  The default implementation just stores a reference to the
     * ChartRenderingInfo object (which might be null).
     *
     * @param g2 The graphics device.
     * @param dataArea The data area.
     * @param plot The plot.
     * @param data The data.
     * @param info An object for returning information about the structure of the chart.
     *
     */
    public void initialise(Graphics2D g2,
                           Rectangle2D dataArea,
                           CategoryPlot plot,
                           ValueAxis axis,
                           CategoryDataset data,
                           ChartRenderingInfo info) {
        this.info = info;
    }

    /**
     * Returns a flag indicating whether the items within one category are stacked up when
     * represented by the renderer.
     *
     * @return The flag.
     */
    public boolean isStacked() {
        return false;
    }

    /**
     * Returns the area that the axes (and date) must fit into.
     * <P>
     * Often this is the same as the plotArea, but sometimes a smaller region should be used
     * (for example, the 3D charts require the axes to use less space in order to leave room
     * for the 'depth' part of the chart).
     *
     * @param plotArea The plot area.
     */
    public Rectangle2D getAxisArea(Rectangle2D plotArea) {
        return plotArea;
    }

    /**
     * Returns the clip region...usually returns the dataArea, but some charts (e.g. 3D) have
     * non rectangular clip regions.
     *
     * @param dataArea The data area.
     */
    public Shape getDataClipRegion(Rectangle2D dataArea) {
        return dataArea;
    }

    /**
     * Draws the background for the plot.
     * <P>
     * For most charts, the axisDataArea and the dataClipArea are the same.
     *
     * @param g2 The graphics device.
     * @param plot The plot.
     * @param axisDataArea The area inside the axes.
     * @param dataClipArea The data clip area.
     */
    public void drawPlotBackground(Graphics2D g2,
                                   CategoryPlot plot,
                                   Rectangle2D axisDataArea, Shape dataClipRegion) {

        if (plot.getBackgroundPaint()!=null) {
            g2.setPaint(plot.getBackgroundPaint());
            g2.fill(dataClipRegion);
        }

        if ((plot.getOutlineStroke()!=null) && (plot.getOutlinePaint()!=null)) {
            g2.setStroke(plot.getOutlineStroke());
            g2.setPaint(plot.getOutlinePaint());
            g2.draw(dataClipRegion);
        }

    }

}