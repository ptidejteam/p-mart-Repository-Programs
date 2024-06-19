/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
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
 * ---------------------------------
 * OverlaidVerticalCategoryPlot.java
 * ---------------------------------
 * (C) Copyright 2002, 2003, by Jeremy Bowman and Contributors.
 *
 * Original Author:  Jeremy Bowman;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: OverlaidVerticalCategoryPlot.java,v 1.1 2007/10/10 19:54:14 vauchers Exp $
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
 *
 */

package com.jrefinery.chart.plot;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Iterator;

import com.jrefinery.chart.ChartRenderingInfo;
import com.jrefinery.chart.LegendItemCollection;
import com.jrefinery.chart.axis.CategoryAxis;
import com.jrefinery.chart.axis.HorizontalCategoryAxis;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.axis.VerticalNumberAxis;
import com.jrefinery.data.Range;

/**
 * An extension of VerticalCategoryPlot that allows multiple
 * VerticalCategoryPlots to be overlaid in one space, using common axes.
 *
 * @author Jeremy Bowman
 */
public class OverlaidVerticalCategoryPlot extends VerticalCategoryPlot {

    /** Storage for the subplot references. */
    private List subplots;

    /**
     * Constructs a new overlaid vertical category plot.
     *
     * @param domainAxisLabel  the label for the domain axis.
     * @param rangeAxisLabel  the label for the range axis.
     * @param categories  the categories to be shown on the domain axis.
     */
    public OverlaidVerticalCategoryPlot(String domainAxisLabel, String rangeAxisLabel,
                                        Comparable[] categories) {

        this(new HorizontalCategoryAxis(domainAxisLabel),
             new VerticalNumberAxis(rangeAxisLabel),
             categories);

    }

    /**
     * Constructs a new overlaid vertical category plot.
     *
     * @param domain  horizontal axis to use for all sub-plots.
     * @param range  vertical axis to use for all sub-plots.
     * @param categories  the categories to be shown on the domain axis.
     */
    public OverlaidVerticalCategoryPlot(CategoryAxis domain, ValueAxis range,
                                        Comparable categories[]) {

        super(null, domain, range, null);
        //DefaultCategoryDataset empty = new DefaultCategoryDataset();
        //empty.setColumnKeys(categories);
        //setDataset(empty);
        this.subplots = new java.util.ArrayList();
                
    }

    /**
     * Adds a subplot.
     * <P>
     * This method sets the axes of the subplot to null.
     *
     * @param subplot  the subplot.
     */
    public void add(VerticalCategoryPlot subplot) {

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
            VerticalCategoryPlot subplot = (VerticalCategoryPlot) iterator.next();
            subplot.render(g2, dataArea, info);
        }
    }

    /**
     * Returns a string for the plot type.
     *
     * @return a string for the plot type.
     */
    public String getPlotType() {
        return "Overlaid Vertical Category Plot";
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
    public Range getVerticalDataRange(ValueAxis axis) {

        Range result = null;

        if (subplots != null) {
            Iterator iterator = subplots.iterator();
            while (iterator.hasNext()) {
                VerticalCategoryPlot plot = (VerticalCategoryPlot) iterator.next();
                result = Range.combine(result, plot.getVerticalDataRange(axis));
            }
        }

        return result;

    }

}
