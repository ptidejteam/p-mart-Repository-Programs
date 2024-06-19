/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
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
 * OverlaidVerticalCategoryPlot.java
 * ---------------------------------
 * (C) Copyright 2002, 2003, by Jeremy Bowman and Contributors.
 *
 * Original Author:  Jeremy Bowman;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: OverlaidCategoryPlot.java,v 1.1 2007/10/10 20:07:31 vauchers Exp $
 *
 * Changes
 * -------
 * 13-May-2002 : Version 1 (JB);
 * 25-Jun-2002 : Removed redundant imports (DG);
 * 18-Sep-2002 : Overided the setSeriesPaint, setSeriesStroke, setSeriesOutlinePaint,
 *               setSeriesOutlineStroke methods to ensure better functionality and to keep
 *               the legend colors consistent with the plot colors.
 * 20-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 27-Sep-2002 : Removed obsolete methods (AS);
 * 26-Nov-2002 : Removed getMin/getMaximumVerticalDataValue(...) methods (DG);
 * 11-Feb-2002 : Fixed bug where category axis labels were not showing (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 12-May-2003 : Renamed OverlaidVerticalCategoryPlot --> OverlaidCategoryPlot (DG);
 *
 */

package org.jfree.chart.plot;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.renderer.LineAndShapeRenderer;
import org.jfree.data.CategoryDataset;
import org.jfree.data.Range;

/**
 * An extension of {@link CategoryPlot} that allows multiple plots to be overlaid in one
 * space, using common axes.
 * <p>
 * The code assumes that all the subplots use datasets with the SAME categories.
 *
 * @author Jeremy Bowman
 */
public class OverlaidCategoryPlot extends CategoryPlot implements Serializable {

    /** Storage for the subplot references. */
    private List subplots;

    /**
     * Constructs a new overlaid vertical category plot.
     *
     * @param domainAxisLabel  the label for the domain axis.
     * @param rangeAxisLabel  the label for the range axis.
     */
    public OverlaidCategoryPlot(String domainAxisLabel, String rangeAxisLabel) {

        this(new CategoryAxis(domainAxisLabel),
             new NumberAxis(rangeAxisLabel));

    }

    /**
     * Constructs a new overlaid vertical category plot.
     *
     * @param domain  horizontal axis to use for all sub-plots.
     * @param range  vertical axis to use for all sub-plots.
     */
    public OverlaidCategoryPlot(CategoryAxis domain, ValueAxis range) {

        // the renderer here is only used for drawing gridlines, subplots have their own
        // renderers installed...
        super(null, domain, range, new LineAndShapeRenderer());
        this.subplots = new java.util.ArrayList();

    }

    /**
     * Adds a subplot.
     * <P>
     * This method sets the axes of the subplot to null.
     *
     * @param subplot  the subplot.
     */
    public void add(CategoryPlot subplot) {

        subplot.setParent(this);
        subplot.setDomainAxis(null);
        subplot.setRangeAxis(null);
        subplots.add(subplot);
        CategoryAxis domain = getDomainAxis();
        if (domain != null) {
            domain.configure();
        }
        ValueAxis range = getRangeAxis();
        if (range != null) {
            range.configure();
        }

    }

    /**
     * Returns the category dataset for the plot.  A trick is employed here - the overlaid plot
     * does not have a dataset, only the subplots do.  We return the dataset for the first subplot,
     * this will only be used to get the categories for the domain axis.
     *
     * @return The dataset.
     */
    public CategoryDataset getCategoryDataset() {

        CategoryDataset result = null;
        if (this.subplots != null) {
            if (this.subplots.size() > 0) {
                CategoryPlot subplot1 = (CategoryPlot) this.subplots.get(0);
                result = subplot1.getCategoryDataset();
            }
        }
        return result;

    }

    /**
     * Returns the legend items.
     *
     * @return the legend items.
     */
    public LegendItemCollection getLegendItems() {

        LegendItemCollection result = new LegendItemCollection();

        if (subplots != null) {
            Iterator iterator = subplots.iterator();
            while (iterator.hasNext()) {
                CategoryPlot plot = (CategoryPlot) iterator.next();
                LegendItemCollection more = plot.getLegendItems();
                result.addAll(more);
            }
        }

        return result;

    }

    /**
     * Performs the actual drawing of the  data.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param info  the chart rendering info.
     */
    public void render(Graphics2D g2, Rectangle2D dataArea, ChartRenderingInfo info) {

        Iterator iterator = subplots.iterator();
        while (iterator.hasNext()) {
            CategoryPlot subplot = (CategoryPlot) iterator.next();
            subplot.render(g2, dataArea, info);
        }
    }

    /**
     * Returns a string for the plot type.
     *
     * @return a string for the plot type.
     */
    public String getPlotType() {
        return "Overlaid Category Plot";
    }

    /**
     * Returns the range of data values that will be plotted against the range axis.
     * <P>
     * If the dataset is <code>null</code>, this method returns <code>null</code>.
     *
     * @param axis  the axis.
     *
     * @return The data range.
     */
    public Range getDataRange(ValueAxis axis) {

        Range result = null;

        if (subplots != null) {
            Iterator iterator = subplots.iterator();
            while (iterator.hasNext()) {
                CategoryPlot plot = (CategoryPlot) iterator.next();
                result = Range.combine(result, plot.getDataRange(axis));
            }
        }

        return result;

    }

    /**
     * Sets the orientation for the plot.
     *
     * @param orientation  the orientation.
     */
    public void setOrientation(PlotOrientation orientation) {

        if (subplots != null) {
            Iterator iterator = subplots.iterator();
            while (iterator.hasNext()) {
                CategoryPlot plot = (CategoryPlot) iterator.next();
                plot.setOrientation(orientation);
            }
        }
        super.setOrientation(orientation);

    }

}
