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
 * -------------------
 * OverlaidXYPlot.java
 * -------------------
 * (C) Copyright 2001-2003, by Bill Kelemen and Contributors.
 *
 * Original Author:  Bill Kelemen;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *                   Xavier Poinsard;
 *
 * $Id: OverlaidXYPlot.java,v 1.1 2007/10/10 20:07:31 vauchers Exp $
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
 * 18-Sep-2002 : Overided the setSeriesPaint, setSeriesStroke, setSeriesOutlinePaint,
 *               setSeriesOutlineStroke methods to ensure better functionality and to keep
 *               the legend colors consistent with the plot colors.
 * 24-Sep-2002 : Added getLegendItems() method (DG);
 * 27-Sep-2002 : Removed obsolete methods (AS);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 02-Jul-2003 : Applied patch in bug report 698646 (secondary axes for overlaid plots) (DG)
 * 04-Jul-2003 : Added a method to return an unmodifiable list of subplots (DG);
 * 
 */

package org.jfree.chart.plot;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.CrosshairInfo;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.data.DatasetGroup;
import org.jfree.data.Range;

/**
 * An extension of {@link XYPlot} that allows multiple plots to be overlaid in one
 * space, using common axes.
 *
 * @author Bill Kelemen (bill@kelemen-usa.com)
 */
public class OverlaidXYPlot extends XYPlot implements Serializable {

    /** Storage for the subplot references. */
    private List subplots;

    /**
     * Constructs a new overlaid XY plot.  Number axes are created for the X
     * and Y axes, using the supplied labels.
     * <P>
     * After creating a new OverlaidXYPlot, you need to add some subplots.
     * <P>
     * No dataset is required, because each of the subplots maintains its own dataset.
     * <P>
     * This constructor is provided for convenience.  If you need greater
     * control over the axes, use another constructor.
     *
     * @param domainAxisLabel  the label for the domain axis.
     * @param rangeAxisLabel  the label for the range axis.
     */
    public OverlaidXYPlot(String domainAxisLabel, String rangeAxisLabel) {

        this(new NumberAxis(domainAxisLabel),
             new NumberAxis(rangeAxisLabel));

    }

    /**
     * Constructs a new overlaid plot (with no subplots initially).
     *
     * @param domain  the shared domain axis.
     * @param range  the shared range axis.
     */
    public OverlaidXYPlot(ValueAxis domain, ValueAxis range) {

        super(null, // dataset not required for parent plot
              domain, range);

        this.subplots = new java.util.ArrayList();

    }

    /**
     * Returns a string representing the plot type.
     *
     * @return the plot type.
     */
    public String getPlotType() {
        return "Overlaid XY Plot";
    }

    /**
     * Adds a subplot.
     * <P>
     * This method sets the axes of the subplot to <code>null</code> since the subplot will
     * use the axes from this (parent) plot.
     *
     * @param subplot  the subplot.
     */
    public void add(XYPlot subplot) {

        DatasetGroup group = getDatasetGroup();
        if (group != null) {
            subplot.setDatasetGroup(group);
        }
        else {
            setDatasetGroup(subplot.getDatasetGroup());
        }

        subplot.setParent(this);
        subplot.setDomainAxis(null);  // subplot uses parent domain axis
        subplot.setRangeAxis(null);   // subplot uses parent range axis
        subplot.setSecondaryRangeAxis(null);  // subplot uses parent axis
        subplots.add(subplot);

        ValueAxis domain = getDomainAxis();
        if (domain != null) {
            domain.configure();
        }

        ValueAxis range = getRangeAxis();
        if (range != null) {
            range.configure();
        }
        
        ValueAxis range2 = getSecondaryRangeAxis();
        if (range2 != null) {
            range2.configure();
        }

        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Removes a subplot from the overlaid chart.
     *
     * @param subplot  the subplot.
     */
    public void remove(XYPlot subplot) {

        subplots.remove(subplot);
        subplot.setParent(null);

        ValueAxis domain = getDomainAxis();
        if (domain != null) {
            domain.configure();
        }

        ValueAxis range = getRangeAxis();
        if (range != null) {
            range.configure();
        }

        ValueAxis range2 = getSecondaryRangeAxis();
        if (range2 != null) {
            range2.configure();
        }

        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Returns an unmodifiable list of the subplots.
     * 
     * @return An unmodifiable list of the subplots.
     */
    public List getSubplots() {
        return Collections.unmodifiableList(this.subplots);
    }
    
    /**
     * Sets the orientation for the plot (also changes the orientation for all the subplots
     * to match).
     * 
     * @param orientation  the orientation.
     */
    public void setOrientation(PlotOrientation orientation) {

        super.setOrientation(orientation);

        Iterator iterator = subplots.iterator();
        while (iterator.hasNext()) {
            XYPlot plot = (XYPlot) iterator.next();
            plot.setOrientation(orientation);
        }

    }

    /**
     * Returns the data range.  This is the combined range of all the subplots.
     *
     * @param axis  the axis.
     *
     * @return The data range.
     *
     */
    public Range getDataRange(ValueAxis axis) {

        Range result = null;

        if (subplots != null) {
            Iterator iterator = subplots.iterator();
            while (iterator.hasNext()) {
                XYPlot plot = (XYPlot) iterator.next();
                result = Range.combine(result, plot.getDataRange(axis));
            }
        }

        return result;

    }

    /**
     * Returns a collection of legend items for the overlaid plot.
     *
     * @return the legend items.
     */
    public LegendItemCollection getLegendItems() {

        LegendItemCollection result = new LegendItemCollection();

        if (subplots != null) {
            Iterator iterator = subplots.iterator();
            while (iterator.hasNext()) {
                XYPlot plot = (XYPlot) iterator.next();
                LegendItemCollection more = plot.getLegendItems();
                result.addAll(more);
            }
        }

        return result;

    }

    /**
     * Renders the subplots.
     * <P>
     * The draw(...) method inherited from {@link XYPlot} takes care of all the setup
     * (background and axes) then calls the render(...) method.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area inside the axes.
     * @param info  optional information collection.
     * @param crosshairInfo  collects information about crosshairs.
     */
    public void render(Graphics2D g2, Rectangle2D dataArea,
                       ChartRenderingInfo info, CrosshairInfo crosshairInfo) {

        Iterator iterator = subplots.iterator();
        while (iterator.hasNext()) {
            XYPlot subplot = (XYPlot) iterator.next();
            subplot.render(g2, dataArea, info, crosshairInfo);
        }

    }
    
    /**
     * Renders the subplots (secondary).
     * <P>
     * The draw(...) method inherited from XYPlot takes care of all the setup
     * (background and axes) then calls the render(...) method.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area inside the axes.
     * @param info  optional information collection.
     * @param crosshairInfo  collects information about crosshairs.
     */
    public void render2(Graphics2D g2, Rectangle2D dataArea,
                        ChartRenderingInfo info, CrosshairInfo crosshairInfo) {
    
        Iterator iterator = subplots.iterator();
        while (iterator.hasNext()) {
            XYPlot subplot = (XYPlot) iterator.next();
            subplot.render2(g2, dataArea, info, crosshairInfo);
        }
    
    }

    /**
     * Draws the annotations for the subplots, then the parent plot.
     *
     * @param g2  the graphics device.
     * @param dataArea
     * @param info
     */
    public void drawAnnotations(Graphics2D g2,
                                Rectangle2D dataArea,
                                ChartRenderingInfo info) {


        Iterator iterator = subplots.iterator();
        while (iterator.hasNext()) {
            XYPlot subplot = (XYPlot) iterator.next();
            subplot.drawAnnotations(g2, dataArea, info);
        }
        super.drawAnnotations(g2, dataArea, info);

    }
    
}
