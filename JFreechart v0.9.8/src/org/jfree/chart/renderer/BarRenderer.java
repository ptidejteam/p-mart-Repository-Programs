/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
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
 * (C) Copyright 2002, 2003 by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: BarRenderer.java,v 1.1 2007/10/10 20:03:12 vauchers Exp $
 *
 * Changes
 * -------
 * 14-Mar-2002 : Version 1 (DG);
 * 23-May-2002 : Added tooltip generator to renderer (DG);
 * 29-May-2002 : Moved tooltip generator to abstract super-class (DG);
 * 25-Jun-2002 : Changed constructor to protected and removed redundant code (DG);
 * 26-Jun-2002 : Added axis to initialise method, and record upper and lower clip values (DG);
 * 24-Sep-2002 : Added getLegendItem(...) method (DG);
 * 09-Oct-2002 : Modified constructor to include URL generator (DG);
 * 05-Nov-2002 : Base dataset is now TableDataset not CategoryDataset (DG);
 * 10-Jan-2003 : Moved get/setItemMargin() method up from subclasses (DG); 
 * 17-Jan-2003 : Moved plot classes into a separate package (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 *
 */

package org.jfree.chart.renderer;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.tooltips.CategoryToolTipGenerator;
import org.jfree.chart.urls.CategoryURLGenerator;

/**
 * A base class for category item renderers that draw bars.
 *
 * @author David Gilbert
 */
public abstract class BarRenderer extends AbstractCategoryItemRenderer
                                  implements Serializable {

    /** The default item margin percentage. */
    public static final double DEFAULT_ITEM_MARGIN = 0.20;

    /** Constant that controls the minimum width before a bar has an outline drawn. */
    public static final double BAR_OUTLINE_WIDTH_THRESHOLD = 3.0;

    /** The margin between items (bars) within a category. */
    private double itemMargin;

    /** The bar width. */
    private double barWidth;

    /** The data value ZERO translated to Java2D user space. */
    private double zeroInJava2D;

    /** The upper clip (axis) value. */
    private double upperClip;

    /** The lower clip (axis) value. */
    private double lowerClip;

    /**
     * Constructs a bar renderer.
     *
     * @param toolTipGenerator  the tool tip generator (<code>null</code> permitted).
     * @param urlGenerator  the URL generator (<code>null</code> permitted).
     */
    protected BarRenderer(CategoryToolTipGenerator toolTipGenerator,
                          CategoryURLGenerator urlGenerator) {

        super(toolTipGenerator, urlGenerator);
        this.itemMargin = DEFAULT_ITEM_MARGIN;

    }

    /**
     * Returns the item margin.
     *
     * @return the margin.
     */
    public double getItemMargin() {
        return this.itemMargin;
    }

    /**
     * Sets the item margin.  The value is expressed as a percentage of the available width for 
     * plotting all the bars, with the resulting amount to be distributed between all the bars 
     * evenly.
     *
     * @param percent  the new margin.
     */
    public void setItemMargin(double percent) {
        this.itemMargin = percent;
        this.firePropertyChanged("ItemMargin", null, null);
    }

    /**
     * Returns the bar width.
     *
     * @return the bar width.
     */
    public double getBarWidth() {
        return this.barWidth;
    }

    /**
     * Updates the calculated bar width.
     *
     * @param width  the new width.
     */
    protected void setBarWidth(double width) {
        this.barWidth = width;
    }

    /**
     * Returns the zero value in Java2D coordinates.
     * <P>
     * This value is recalculated in the initialise() method.
     *
     * @return the value.
     */
    public double getZeroInJava2D() {
        return this.lowerClip;
    }

    /**
     * Returns the lower clip value.
     * <P>
     * This value is recalculated in the initialise() method.
     *
     * @return the value.
     */
    public double getLowerClip() {
        return this.lowerClip;
    }

    /**
     * Returns the upper clip value.
     * <P>
     * This value is recalculated in the initialise() method.
     *
     * @return the value.
     */
    public double getUpperClip() {
        return this.upperClip;
    }

    /**
     * Initialises the renderer.
     * <p>
     * This method gets called once at the start of the process of drawing a chart.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area in which the data is to be plotted.
     * @param plot  the plot.
     * @param info  collects chart rendering information for return to caller.
     *
     */
    public void initialise(Graphics2D g2,
                           Rectangle2D dataArea,
                           CategoryPlot plot,
                           ChartRenderingInfo info) {

        super.initialise(g2, dataArea, plot, info);

        ValueAxis rangeAxis = plot.getRangeAxis();
        this.lowerClip = rangeAxis.getRange().getLowerBound();
        this.upperClip = rangeAxis.getRange().getUpperBound();

    }

}
