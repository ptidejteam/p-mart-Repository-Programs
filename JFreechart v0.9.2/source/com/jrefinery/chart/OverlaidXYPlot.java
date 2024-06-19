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
 * -------------------
 * OverlaidXYPlot.java
 * -------------------
 * (C) Copyright 2001, 2002, by Bill Kelemen and Contributors.
 *
 * Original Author:  Bill Kelemen;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: OverlaidXYPlot.java,v 1.1 2007/10/10 19:41:58 vauchers Exp $
 *
 * Changes:
 * --------
 * 06-Dec-2001 : Version 1 (BK);
 * 12-Dec-2001 : Removed unnecessary 'throws' clause in constructor (DG);
 * 08-Jan-2002 : Moved to new package com.jrefinery.chart.combination (DG);
 * 25-Feb-2002 : Removed redundant import statements (DG);
 * 22-Apr-2002 : Renamed OverlaidPlot --> OverlaidXYPlot (DG);
 * 30-Apr-2002 : Deleted redundant zoom() method (DG);
 * 13-May-2002 : A small modification to the draw(...) method in XYPlot means that it can just be
 *               inherited now, as suggested by Jeremy Bowman (DG);
 * 13-Jun-2002 : Updated Javadoc comments (DG);
 * 25-Jun-2002 : Removed redundant imports (DG);
 *
 */

package com.jrefinery.chart;

import com.jrefinery.data.Range;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Iterator;

/**
 * An extension of XYPlot that allows multiple XYPlots to be overlaid in one space, using common
 * axes.
 *
 * @author Bill Kelemen (bill@kelemen-usa.com)
 */
public class OverlaidXYPlot extends XYPlot {

    /** Storage for the subplot references. */
    protected List subplots;

    /** The total number of series. */
    protected int seriesCount = 0;

    /**
     * Constructs a new overlaid XY plot.  Number axes are created for the X and Y axes, using
     * the supplied labels.
     * <P>
     * After creating a new OverlaidXYPlot, you need to add some subplots.
     * <P>
     * No dataset is required, because each of the subplots maintains its own dataset.
     * <P>
     * This constructor is provided for convenience.  If you need greater control over the axes,
     * use another constructor.
     *
     * @param domainAxisLabel The label for the domain axis.
     * @param rangeAxisLabel The label for the range axis.
     */
    public OverlaidXYPlot(String domainAxisLabel, String rangeAxisLabel) {

        this(new HorizontalNumberAxis(domainAxisLabel),
             new VerticalNumberAxis(rangeAxisLabel));

    }

    /**
     * Constructs an OverlaidXYPlot.
     *
     * @param domain Horizontal axis to use for all sub-plots.
     * @param range Vertical axis to use for all sub-plots.
     */
    public OverlaidXYPlot(ValueAxis domain, ValueAxis range) {
        super(null, domain, range);
        this.subplots = new java.util.ArrayList();
    }

    /**
     * Adds a subplot.
     * <P>
     * This method sets the axes of the subplot to null.
     *
     * @param subplot The subplot.
     */
    public void add(XYPlot subplot) {

        subplot.setParent(this);
        subplot.setDomainAxis(null);
        subplot.setRangeAxis(null);
        subplot.setFirstSeriesIndex(seriesCount);
        seriesCount = seriesCount + subplot.getSeriesCount();
        subplots.add(subplot);
        ValueAxis domain = this.getDomainAxis();
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
                XYPlot plot = (XYPlot)iterator.next();
                List more = plot.getLegendItemLabels();
                result.addAll(more);
            }
        }

        return result;

    }

    /**
     * Renders the subplots.
     * <P>
     * The draw(...) method inherited from XYPlot takes care of all the setup (background and
     * axes) then calls the render(...) method.
     *
     * @param g2 The graphics device.
     * @param dataArea The area inside the axes.
     * @param info Optional information collection.
     * @param crosshairInfo Collects information about crosshairs.
     */
    public void render(Graphics2D g2, Rectangle2D dataArea, ChartRenderingInfo info,
                       CrosshairInfo crosshairInfo) {
        Iterator iterator = subplots.iterator();
        while (iterator.hasNext()) {
            XYPlot subplot = (XYPlot)iterator.next();
            subplot.render(g2, dataArea, info, crosshairInfo);
        }
    }

    /**
     * Returns a string representing the plot type.
     *
     * @return The plot type.
     */
    public String getPlotType() {
        return "Overlaid XY Plot";
    }

    /**
     * Returns the horizontal (x-axis) data range.  This is the combined range of all the
     * subplots.
     *
     * @return The horizontal data range.
     *
     */
    public Range getHorizontalDataRange() {

        Range result = null;

        if (subplots!=null) {
            Iterator iterator = subplots.iterator();
            while (iterator.hasNext()) {
                XYPlot plot = (XYPlot)iterator.next();
                result = Range.combine(result, plot.getHorizontalDataRange());
            }
        }

        return result;

    }

    /**
     * Returns the vertical (y-axis) data range.  This is the combined range of all the
     * subplots.
     *
     * @return The vertical data range.
     */
    public Range getVerticalDataRange() {

        Range result = null;

        if (subplots!=null) {
            Iterator iterator = subplots.iterator();
            while (iterator.hasNext()) {
                XYPlot plot = (XYPlot)iterator.next();
                result = Range.combine(result, plot.getVerticalDataRange());
            }
        }

        return result;



    }

    /**
     * Returns the number of series in this plot.  In this case, this is the sum of the number
     * of series in all the subplots.
     *
     * @return The series count.
     */
    public int getSeriesCount() {

        int result = 0;

        Iterator iterator = subplots.iterator();
        while (iterator.hasNext()) {
            XYPlot subplot = (XYPlot)iterator.next();
            result = result + subplot.getSeriesCount();
        }

        return result;

    }

    /**
     * Sets the first series index.  This is used to give all series in the plot (including
     * subplots) a unique number for identification purposes.  Need to find a more robust
     * method.
     *
     * @param index The index.
     */
    public void setFirstSeriesIndex(int index) {

        this.firstSeriesIndex = index;
        int seriesCount = index;
        Iterator iterator = subplots.iterator();
        while (iterator.hasNext()) {
            XYPlot subplot = (XYPlot)iterator.next();
            subplot.setFirstSeriesIndex(seriesCount);
            seriesCount = seriesCount + subplot.getSeriesCount();
        }

    }

}