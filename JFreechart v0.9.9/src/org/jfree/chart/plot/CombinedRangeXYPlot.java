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
 * -----------------------
 * SharedDomainXYPlot.java
 * -----------------------
 * (C) Copyright 2001-2003, by Bill Kelemen and Contributors.
 *
 * Original Author:  Bill Kelemen;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *                   Anthony Boulestreau;
 *                   David Basten;
 *                   Kevin Frechette (for ISTI);
 *
 * $Id: CombinedRangeXYPlot.java,v 1.1 2007/10/10 20:07:31 vauchers Exp $
 *
 * Changes:
 * --------
 * 06-Dec-2001 : Version 1 (BK);
 * 12-Dec-2001 : Removed unnecessary 'throws' clause from constructor (DG);
 * 18-Dec-2001 : Added plotArea attribute and get/set methods (BK);
 * 22-Dec-2001 : Fixed bug in chartChanged with multiple combinations of CombinedPlots (BK);
 * 08-Jan-2002 : Moved to new package com.jrefinery.chart.combination (DG);
 * 25-Feb-2002 : Updated import statements (DG);
 * 28-Feb-2002 : Readded "this.plotArea = plotArea" that was deleted from draw() method (BK);
 * 26-Mar-2002 : Added an empty zoom method (this method needs to be written so that combined
 *               plots will support zooming (DG);
 * 29-Mar-2002 : Changed the method createCombinedAxis adding the creation of OverlaidSymbolicAxis
 *               and CombinedSymbolicAxis(AB);
 * 23-Apr-2002 : Renamed CombinedPlot-->MultiXYPlot, and simplified the structure (DG);
 * 23-May-2002 : Renamed (again) MultiXYPlot-->CombinedXYPlot (DG);
 * 19-Jun-2002 : Added get/setGap() methods suggested by David Basten (DG);
 * 25-Jun-2002 : Removed redundant imports (DG);
 * 16-Jul-2002 : Draws shared axis after subplots (to fix missing gridlines),
 *               added overrides of 'setSeriesPaint()' and 'setXYItemRenderer()'
 *               that pass changes down to subplots (KF);
 * 09-Oct-2002 : Added add(XYPlot) method (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 16-May-2003 : Renamed CombinedXYPlot --> CombinedRangeXYPlot (DG);
 * 26-Jun-2003 : Fixed bug 755547 (DG);
 *
 */

package org.jfree.chart.plot;

import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.renderer.XYItemRenderer;
import org.jfree.data.Range;

/**
 * An extension of {@link XYPlot} that contains multiple subplots that share a common range axis.
 *
 * @author Bill Kelemen (bill@kelemen-usa.com).
 * @author David Gilbert.
 */
public class CombinedRangeXYPlot extends XYPlot implements Serializable {

    /** Storage for the subplot references. */
    private List subplots;

    /** Total weight of all charts. */
    private int totalWeight = 0;

    /** The gap between subplots. */
    private double gap = 5.0;

    /**
     * Creates a new plot.
     *
     * @param rangeAxis  the shared axis.
     */
    public CombinedRangeXYPlot(ValueAxis rangeAxis) {

        super(null, // no data in the parent plot
              null,
              rangeAxis);

        this.subplots = new java.util.ArrayList();

    }

    /**
     * Returns a string describing the type of plot.
     *
     * @return the type of plot.
     */
    public String getPlotType() {

        return "Combined Range XYPlot";

    }

    /**
     * Returns the list of subplots.
     *
     * @return the list of subplots.
     */
    public List getSubPlots() {
        return Collections.unmodifiableList(this.subplots);
    }

    /**
     * Returns the space between subplots.
     *
     * @return the gap
     */
    public double getGap() {
        return gap;
    }

    /**
     * Sets the amount of space between subplots.
     *
     * @param gap  the gap between subplots
     */
    public void setGap(double gap) {
        this.gap = gap;
    }

    /**
     * Adds a subplot, with a default 'weight' of 1.
     * <P>
     * The subplot should have a null horizontal axis (for VERTICAL layout) or a null vertical
     * axis (for HORIZONTAL layout).
     *
     * @param subplot  the subplot.
     */
    public void add(XYPlot subplot) {
        add(subplot, 1);
    }

    /**
     * Adds a subplot with a particular weight (greater than or equal to one).  The weight
     * determines how much space is allocated to the subplot relative to all the other subplots.
     * <P>
     * The subplot should have a null horizontal axis (for VERTICAL layout) or a
     * null vertical axis (for HORIZONTAL layout).
     *
     * @param subplot  the subplot.
     * @param weight  the weight.
     *
     * @throws IllegalArgumentException if weight <code>&lt; 1</code>
     */
    public void add(XYPlot subplot, int weight) throws IllegalArgumentException {

        // verify valid weight
        if (weight <= 0) {
            String msg = "SharedRangeXYPlot.add(...): weight must be positive.";
            throw new IllegalArgumentException(msg);
        }

        // store the plot and its weight
        subplot.setParent(this);
        subplot.setWeight(weight);
        subplot.setInsets(new Insets(0, 0, 0, 0));
        subplot.setRangeAxis(null);
        this.subplots.add(subplot);

        // keep track of total weights
        this.totalWeight += weight;

        // configure the range axis...
        ValueAxis axis = getRangeAxis();
        if (axis != null) {
            axis.configure();
        }

    }

    /**
     * Removes a subplot from the combined chart.
     *
     * @param subplot  the subplot.
     */
    public void remove(XYPlot subplot) {

        subplots.remove(subplot);
        subplot.setParent(null);

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
     * Returns the list of subplots.
     *
     * @return the list of subplots.
     */
    public List getSubplots() {
        return Collections.unmodifiableList(this.subplots);
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     * Will perform all the placement calculations for each sub-plots and then tell these to draw
     * themselves.
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the plot (including axis labels) should be drawn.
     * @param info  collects information about the drawing (null permitted).
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, ChartRenderingInfo info) {

        // set up info collection...
        if (info != null) {
            info.setPlotArea(plotArea);
        }

        // adjust the drawing area for plot insets (if any)...
        Insets insets = getInsets();
        if (insets != null) {
            plotArea.setRect(plotArea.getX() + insets.left,
                             plotArea.getY() + insets.top,
                             plotArea.getWidth() - insets.left - insets.right,
                             plotArea.getHeight() - insets.top - insets.bottom);
        }

        // get the data area (but without accounting for the domain axes yet)
        Rectangle2D dataArea = getDataArea(g2, plotArea);

        PlotOrientation orientation = getOrientation();

        // work out the maximum height or width of the non-shared axes...
        int n = subplots.size();

        // calculate plotAreas of all sub-plots, maximum vertical/horizontal axis width/height
        Rectangle2D[] subplotArea = new Rectangle2D[n];
        double x = dataArea.getX();
        double y = dataArea.getY();
        double usableSize = 0.0;
        if (orientation == PlotOrientation.HORIZONTAL) {
            usableSize = dataArea.getHeight() - gap * (n - 1);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            usableSize = dataArea.getWidth() - gap * (n - 1);
        }

        AxisSpace space = new AxisSpace();
        for (int i = 0; i < n; i++) {
            XYPlot plot = (XYPlot) subplots.get(i);

            // calculate sub-plot height
            double subplotSize = usableSize * plot.getWeight() / totalWeight;

            // calculate sub-plot area
            if (orientation == PlotOrientation.HORIZONTAL) {
                subplotArea[i] = new Rectangle2D.Double(x, y, dataArea.getWidth(), subplotSize);
                y += subplotSize + gap;
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                subplotArea[i] = new Rectangle2D.Double(x, y, subplotSize, dataArea.getHeight());
                x += subplotSize + gap;
            }

            // calculate sub-plot max axis width and height if needed
            final ValueAxis domainAxis = plot.getDomainAxis();
            final AxisLocation location = plot.getDomainAxisLocation();
            space = domainAxis.reserveSpace(g2, plot, subplotArea[i], location, space);

        }

        // set the width and height of non-shared axis of all sub-plots
        setFixedDomainAxisSpaceForSubplots(space);

        if (orientation == PlotOrientation.HORIZONTAL) {
            dataArea = space.shrinkLeftAndRight(dataArea, dataArea);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            dataArea = space.shrinkTopAndBottom(dataArea, dataArea);
        }

        if (info != null) {
            info.setDataArea(dataArea);
        }

        // draw all the charts
        for (int i = 0; i < n; i++) {
            XYPlot plot = (XYPlot) subplots.get(i);
            plot.draw(g2, subplotArea[i], info);
        }

        // draw the shared axis
        getRangeAxis().draw(g2, plotArea, dataArea, getRangeAxisLocation());

    }

//    /**
//     * Sets the size of the domain axis for each subplot.
//     *
//     * @param size  the size.
//     */
//    protected void setDomainAxisSize(double size) {
//
//        Iterator iterator = subplots.iterator();
//        while (iterator.hasNext()) {
//            XYPlot plot = (XYPlot) iterator.next();
//            ValueAxis axis = plot.getDomainAxis();
//            axis.setFixedDimension(size);
//        }
//
//    }

    /**
     * Returns a collection of legend items for the plot.
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
     * A zoom method that (currently) does nothing.
     *
     * @param percent  the zoom percentage.
     */
    public void zoom(double percent) {
        // need to decide how to handle zooming...
    }

    /**
     * Sets the item renderer FOR ALL SUBPLOTS.  Registered listeners are notified that the plot
     * has been modified.
     * <P>
     * Note: usually you will want to set the renderer independently for each subplot, which is
     * NOT what this method does.
     *
     * @param renderer the new renderer.
     */
    public void setRenderer(XYItemRenderer renderer) {

        super.setRenderer(renderer);  // not strictly necessary, since the renderer set for the
                                      // parent plot is not used

        Iterator iterator = subplots.iterator();
        while (iterator.hasNext()) {
            XYPlot plot = (XYPlot) iterator.next();
            plot.setRenderer(renderer);
        }

    }

    public void setOrientation(PlotOrientation orientation) {

        super.setOrientation(orientation);

        Iterator iterator = subplots.iterator();
        while (iterator.hasNext()) {
            XYPlot plot = (XYPlot) iterator.next();
            plot.setOrientation(orientation);
        }

    }

   /**
     * Returns the range for the axis.  This is the combined range of all the subplots.
     *
     * @param axis  the axis.
     *
     * @return the range.
     */
    public Range getDataRange(ValueAxis axis) {

        Range result = null;

        if (subplots != null) {
            Iterator iterator = subplots.iterator();
            while (iterator.hasNext()) {
                XYPlot subplot = (XYPlot) iterator.next();
                result = Range.combine(result, subplot.getDataRange(axis));
            }
        }

        return result;

    }


    /**
     * Tests this plot for equality with another object.
     *
     * @param obj  the other object.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (obj instanceof CombinedRangeXYPlot) {
            CombinedRangeXYPlot p = (CombinedRangeXYPlot) obj;
            if (super.equals(obj)) {
                boolean b0 = true;
                boolean b1 = true;
                // ObjectUtils.equalOrBothNull(this.subplots, p.subplots);
                boolean b2 = (this.totalWeight == p.totalWeight);
                boolean b3 = (this.gap == p.gap);
                return b0 && b1 && b2 && b3;
            }
        }

        return false;
    }
    
    /**
     * Sets the size (width or height, depending on the orientation of the plot) for the domain
     * axis of each subplot.
     *
     * @param size  the size.
     */
    protected void setFixedDomainAxisSpaceForSubplots(AxisSpace space) {

        Iterator iterator = subplots.iterator();
        while (iterator.hasNext()) {
            XYPlot plot = (XYPlot) iterator.next();
            plot.setFixedDomainAxisSpace(space);
        }

    }


}
