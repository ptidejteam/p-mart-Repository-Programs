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
 * ---------------------------------
 * OverlaidVerticalCategoryPlot.java
 * ---------------------------------
 * (C) Copyright 2002, by Jeremy Bowman.
 *
 * Original Author:  Jeremy Bowman;
 * Contributor(s):   -;
 *
 * $Id: OverlaidVerticalCategoryPlot.java,v 1.1 2007/10/10 19:02:26 vauchers Exp $
 *
 * Changes
 * -------
 * 13-May-2002 : Version 1 (JB);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Iterator;
import com.jrefinery.data.Dataset;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.DatasetUtilities;
import com.jrefinery.data.DefaultCategoryDataset;
import com.jrefinery.data.Range;
import com.jrefinery.chart.HorizontalAxis;
import com.jrefinery.chart.HorizontalCategoryAxis;
import com.jrefinery.chart.VerticalAxis;
import com.jrefinery.chart.VerticalNumberAxis;
import com.jrefinery.chart.CrosshairInfo;
import com.jrefinery.chart.CategoryPlot;
import com.jrefinery.chart.ChartRenderingInfo;
import com.jrefinery.chart.ValueAxis;
import com.jrefinery.chart.tooltips.ToolTipsCollection;

/**
 * An extension of VerticalCategoryPlot that allows multiple
 * VerticalCategoryPlots to be overlaid in one space, using common axes.
 *
 * @author Jeremy Bowman
 */
public class OverlaidVerticalCategoryPlot extends VerticalCategoryPlot {

    /** Storage for the subplot references. */
    protected List subplots;

    /** The total number of series. */
    protected int seriesCount = 0;

    /**
     * Constructs a new overlaid vertical category plot.
     *
     * @param domainAxisLabel The label for the domain axis.
     * @param rangeAxisLabel The label for the range axis.
     * @param categories The categories to be shown on the domain axis.
     */
    public OverlaidVerticalCategoryPlot(String domainAxisLabel,
                                        String rangeAxisLabel,
                                        Object[] categories) {

        this(new HorizontalCategoryAxis(domainAxisLabel),
             new VerticalNumberAxis(rangeAxisLabel),
             categories);

    }

    /**
     * Constructs an OverlaidVerticalCategoryPlot.
     *
     * @param domain Horizontal axis to use for all sub-plots.
     * @param range Vertical axis to use for all sub-plots.
     * @param categories The categories to be shown on the domain axis.
     */
    public OverlaidVerticalCategoryPlot(CategoryAxis domain, ValueAxis range,
                                        Object categories[]) {
        super(null, domain, range, null);
        // Create an empty dataset to hold the category labels
        double[][] emptyArray = new double[1][categories.length];
        DefaultCategoryDataset empty = new DefaultCategoryDataset(emptyArray);
        empty.setCategories(categories);
        setDataset(empty);
        this.subplots = new java.util.ArrayList();
    }

    /**
     * Adds a subplot.
     * <P>
     * This method sets the axes of the subplot to null.
     *
     * @param subplot The subplot.
     */
    public void add(VerticalCategoryPlot subplot) {

        subplot.setParent(this);
        subplot.setDomainAxis(null);
        subplot.setRangeAxis(null);
        subplot.setFirstSeriesIndex(seriesCount);
        seriesCount = seriesCount + subplot.getSeriesCount();
        subplots.add(subplot);
        CategoryAxis domain = this.getDomainAxis();
        if (domain!=null) domain.configure();
        ValueAxis range = this.getRangeAxis();
        if (range!=null) range.configure();

    }

    /**
     * Returns an array of labels to be displayed by the legend.
     *
     * @return An array of legend item labels (or null).
     */
    public List getLegendItemLabels() {

        List result = new java.util.ArrayList();

        if (subplots!=null) {
            Iterator iterator = subplots.iterator();
            while (iterator.hasNext()) {
                VerticalCategoryPlot plot = (VerticalCategoryPlot)iterator.next();
                List more = plot.getLegendItemLabels();
                result.addAll(more);
            }
        }

        return result;

    }

    public void render(Graphics2D g2, Rectangle2D dataArea, ChartRenderingInfo info,
                       Shape backgroundPlotArea) {
        Iterator iterator = subplots.iterator();
        while (iterator.hasNext()) {
            VerticalCategoryPlot subplot = (VerticalCategoryPlot)iterator.next();
            subplot.render(g2, dataArea, info, backgroundPlotArea);
        }
    }

    public String getPlotType() {
        return "Overlaid Vertical Category Plot";
    }

    public int getSeriesCount() {

        int result = 0;

        Iterator iterator = subplots.iterator();
        while (iterator.hasNext()) {
            VerticalCategoryPlot subplot = (VerticalCategoryPlot)iterator.next();
            result = result + subplot.getSeriesCount();
        }

        return result;

    }

    public void setFirstSeriesIndex(int index) {

        this.firstSeriesIndex = index;
        int seriesCount = index;
        Iterator iterator = subplots.iterator();
        while (iterator.hasNext()) {
            VerticalCategoryPlot subplot = (VerticalCategoryPlot)iterator.next();
            subplot.setFirstSeriesIndex(seriesCount);
            seriesCount = seriesCount + subplot.getSeriesCount();
        }

    }

    /**
     * Returns the range of data values that will be plotted against the range axis.
     * <P>
     * If the dataset is null, this method returns null.
     *
     * @return The data range.
     */
    public Range getVerticalDataRange() {

        Range result = null;

        if (subplots!=null) {
            Iterator iterator = subplots.iterator();
            while (iterator.hasNext()) {
                VerticalCategoryPlot plot = (VerticalCategoryPlot)iterator.next();
                result = Range.combine(result, plot.getVerticalDataRange());
            }
        }

        return result;

    }

    /**
     * Returns the minimum value in the range (since this is plotted against the vertical axis by
     * VerticalBarPlot).
     * <P>
     * This method will return null if the dataset is null.
     *
     * @return The minimum value.
     */
    public Number getMinimumVerticalDataValue() {

        Number result = null;

        if (subplots!=null) {
            Iterator iterator = subplots.iterator();
            while (iterator.hasNext()) {
                VerticalCategoryPlot plot = (VerticalCategoryPlot)iterator.next();
                Number subMin = plot.getMinimumVerticalDataValue();
                if (result == null)
                    result = subMin;
                else if (subMin != null) {
                    result = new Double(Math.min(result.doubleValue(),
                                                 subMin.doubleValue()));
                }
            }
        }

        return result;

    }

    /**
     * Returns the maximum value in the range (since the range values are plotted against the
     * vertical axis by this plot).
     * <P>
     * This method will return null if the dataset is null.
     *
     * @return The maximum value.
     */
    public Number getMaximumVerticalDataValue() {

        Number result = null;

        if (subplots!=null) {
            Iterator iterator = subplots.iterator();
            while (iterator.hasNext()) {
                VerticalCategoryPlot plot = (VerticalCategoryPlot)iterator.next();
                Number subMax = plot.getMaximumVerticalDataValue();
                if (result == null)
                    result = subMax;
                else if (subMax != null)
                    result = new Double(Math.max(result.doubleValue(),
                                                 subMax.doubleValue()));
            }
        }

        return result;

    }

}
