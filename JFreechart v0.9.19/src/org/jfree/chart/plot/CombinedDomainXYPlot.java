/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * -------------------------
 * CombinedDomainXYPlot.java
 * -------------------------
 * (C) Copyright 2001-2004, by Bill Kelemen and Contributors.
 *
 * Original Author:  Bill Kelemen;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *                   Anthony Boulestreau;
 *                   David Basten;
 *                   Kevin Frechette (for ISTI);
 *                   Nicolas Brodu;
 *
 * $Id: CombinedDomainXYPlot.java,v 1.1 2007/10/10 19:34:47 vauchers Exp $
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
 * 16-May-2003 : Renamed CombinedXYPlot --> CombinedDomainXYPlot (DG);
 * 04-Aug-2003 : Removed leftover code that was causing domain axis drawing problem (DG);
 * 08-Aug-2003 : Adjusted totalWeight in remove(...) method (DG);
 * 21-Aug-2003 : Implemented Cloneable (DG);
 * 11-Sep-2003 : Fix cloning support (subplots) (NB);
 * 15-Sep-2003 : Fixed error in cloning (DG);
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 17-Sep-2003 : Updated handling of 'clicks' (DG);
 *
 */

package org.jfree.chart.plot;

import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.renderer.XYItemRenderer;
import org.jfree.data.Range;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ObjectUtils;
import org.jfree.util.PublicCloneable;

/**
 * An extension of {@link XYPlot} that contains multiple subplots that share a common domain axis.
 *
 */
public class CombinedDomainXYPlot extends XYPlot 
                                  implements Cloneable, PublicCloneable, Serializable,
                                             PlotChangeListener {

    /** Storage for the subplot references. */
    private List subplots;

    /** Total weight of all charts. */
    private int totalWeight = 0;

    /** The gap between subplots. */
    private double gap = 5.0;

    /** Temporary storage for the subplot areas. */
    private transient Rectangle2D[] subplotAreas;
    // TODO:  the subplot areas needs to be moved out of the plot into the plot state
    
    /**
     * Default constructor.
     */
    public CombinedDomainXYPlot() {
        this(new NumberAxis());      
    }
    
    /**
     * Creates a new combined plot that shares a domain axis among multiple subplots.
     *
     * @param domainAxis  the shared axis.
     */
    public CombinedDomainXYPlot(ValueAxis domainAxis) {

        super(
            null,        // no data in the parent plot
            domainAxis,
            null,        // no range axis
            null         // no rendereer
        );  

        this.subplots = new java.util.ArrayList();

    }

    /**
     * Returns a string describing the type of plot.
     *
     * @return The type of plot.
     */
    public String getPlotType() {
        return "Combined_Domain_XYPlot";
    }

    /**
     * Sets the orientation for the plot (also changes the orientation for all the subplots
     * to match).
     * 
     * @param orientation  the orientation.
     */
    public void setOrientation(PlotOrientation orientation) {

        super.setOrientation(orientation);

        Iterator iterator = this.subplots.iterator();
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
     * @return The range.
     */
    public Range getDataRange(ValueAxis axis) {

        Range result = null;
        if (this.subplots != null) {
            Iterator iterator = this.subplots.iterator();
            while (iterator.hasNext()) {
                XYPlot subplot = (XYPlot) iterator.next();
                result = Range.combine(result, subplot.getDataRange(axis));
            }
        }
        return result;

    }

    /**
     * Returns the space between subplots.
     *
     * @return The gap (in Java2D units).
     */
    public double getGap() {
        return this.gap;
    }

    /**
     * Sets the amount of space between subplots and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param gap  the gap between subplots (in Java2D units).
     */
    public void setGap(double gap) {
        this.gap = gap;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Adds a subplot, with a default 'weight' of 1.
     * <P>
     * The subplot should have a null domain axis.
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
     * The domain axis for the subplot will be set to <code>null</code>.
     *
     * @param subplot  the subplot.
     * @param weight  the weight (must be 1 or greater).
     */
    public void add(XYPlot subplot, int weight) {

        // verify valid weight
        if (weight <= 0) {
            String msg = "SharedDomainXYPlot.add(...): weight must be positive.";
            throw new IllegalArgumentException(msg);
        }

        // store the plot and its weight
        subplot.setParent(this);
        subplot.setWeight(weight);
        subplot.setInsets(new Insets(0, 0, 0, 0));
        subplot.setDomainAxis(null);
        subplot.addChangeListener(this);
        this.subplots.add(subplot);

        // keep track of total weights
        this.totalWeight += weight;

        ValueAxis axis = getDomainAxis();
        if (axis != null) {
            axis.configure();
        }
        
        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Removes a subplot from the combined chart and sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param subplot  the subplot (<code>null</code> not permitted).
     */
    public void remove(XYPlot subplot) {
        if (subplot == null) {
            throw new IllegalArgumentException(" Null 'subplot' argument.");   
        }
        this.subplots.remove(subplot);
        subplot.setParent(null);
        subplot.removeChangeListener(this);
        this.totalWeight -= subplot.getWeight();

        ValueAxis domain = getDomainAxis();
        if (domain != null) {
            domain.configure();
        }
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the list of subplots.
     *
     * @return An unmodifiable list of subplots.
     */
    public List getSubplots() {
        return Collections.unmodifiableList(this.subplots);
    }

    /**
     * Calculates the axis space required.
     * 
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * 
     * @return The space.
     */
    protected AxisSpace calculateAxisSpace(Graphics2D g2, Rectangle2D plotArea) {
        
        AxisSpace space = new AxisSpace();
        PlotOrientation orientation = getOrientation();
        
        // work out the space required by the domain axis...
        AxisSpace fixed = getFixedDomainAxisSpace();
        if (fixed != null) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                space.setLeft(fixed.getLeft());
                space.setRight(fixed.getRight());
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                space.setTop(fixed.getTop());
                space.setBottom(fixed.getBottom());                
            }
        }
        else {
            ValueAxis xAxis = getDomainAxis();
            RectangleEdge xEdge = Plot.resolveDomainAxisLocation(
                getDomainAxisLocation(), orientation
            );
            if (xAxis != null) {
                space = xAxis.reserveSpace(g2, this, plotArea, xEdge, space);
            }
        }
        
        Rectangle2D adjustedPlotArea = space.shrink(plotArea, null);
        
        // work out the maximum height or width of the non-shared axes...
        int n = this.subplots.size();
        this.subplotAreas = new Rectangle2D[n];
        double x = adjustedPlotArea.getX();
        double y = adjustedPlotArea.getY();
        double usableSize = 0.0;
        if (orientation == PlotOrientation.HORIZONTAL) {
            usableSize = adjustedPlotArea.getWidth() - this.gap * (n - 1);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            usableSize = adjustedPlotArea.getHeight() - this.gap * (n - 1);
        }

        for (int i = 0; i < n; i++) {
            XYPlot plot = (XYPlot) this.subplots.get(i);

            // calculate sub-plot area
            if (orientation == PlotOrientation.HORIZONTAL) {
                double w = usableSize * plot.getWeight() / this.totalWeight;
                this.subplotAreas[i] = new Rectangle2D.Double(
                    x, y, w, adjustedPlotArea.getHeight()
                );
                x = x + w + this.gap;
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                double h = usableSize * plot.getWeight() / this.totalWeight;
                this.subplotAreas[i] = new Rectangle2D.Double(x, y, adjustedPlotArea.getWidth(), h);
                y = y + h + this.gap;
            }

            AxisSpace subSpace = plot.calculateRangeAxisSpace(g2, this.subplotAreas[i], null);
            space.ensureAtLeast(subSpace);

        }

        return space;
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     * Will perform all the placement calculations for each sub-plots and then tell these to draw
     * themselves.
     *
     * @param g2  the graphics device.
     * @param area  the area within which the plot (including axis labels) should be drawn.
     * @param parentState  the parent state.
     * @param info  collects information about the drawing (null permitted).
     */
    public void draw(Graphics2D g2, 
                     Rectangle2D area, 
                     PlotState parentState,
                     PlotRenderingInfo info) {
        draw(g2, area, null, parentState, info);                          
    }
    
    /**
     * Draws the plot within the specified area on a graphics device.
     * 
     * @param g2  the graphics device.
     * @param area  the plot area (in Java2D space).
     * @param anchor  an anchor point in Java2D space (<code>null</code> permitted).
     * @param parentState  the state from the parent plot, if there is one (<code>null</code> 
     *                     permitted).
     * @param info  collects chart drawing information (<code>null</code> permitted).
     */
    public void draw(Graphics2D g2,
                     Rectangle2D area,
                     Point2D anchor,
                     PlotState parentState,
                     PlotRenderingInfo info) {
        
        // set up info collection...
        if (info != null) {
            info.setPlotArea(area);
        }

        // adjust the drawing area for plot insets (if any)...
        Insets insets = getInsets();
        if (insets != null) {
            area.setRect(
                area.getX() + insets.left, area.getY() + insets.top,
                area.getWidth() - insets.left - insets.right,
                area.getHeight() - insets.top - insets.bottom
            );
        }

        AxisSpace space = calculateAxisSpace(g2, area);
        Rectangle2D dataArea = space.shrink(area, null);

        // set the width and height of non-shared axis of all sub-plots
        setFixedRangeAxisSpaceForSubplots(space);

        // draw the shared axis
        ValueAxis axis = getDomainAxis();
        RectangleEdge edge = getDomainAxisEdge();
        double cursor = RectangleEdge.coordinate(dataArea, edge);
        AxisState axisState = axis.draw(g2, cursor, area, dataArea, edge, info);
        if (parentState == null) {
            parentState = new PlotState();
        }
        parentState.getSharedAxisStates().put(axis, axisState);
        
        // draw all the subplots
        for (int i = 0; i < this.subplots.size(); i++) {
            XYPlot plot = (XYPlot) this.subplots.get(i);
            PlotRenderingInfo subplotInfo = null;
            if (info != null) {
                subplotInfo = new PlotRenderingInfo(info.getOwner());
                info.addSubplotInfo(subplotInfo);
            }
            plot.draw(g2, this.subplotAreas[i], anchor, parentState, subplotInfo);
        }

        if (info != null) {
            info.setDataArea(dataArea);
        }
        
    }

    /**
     * Returns a collection of legend items for the plot.
     *
     * @return the legend items.
     */
    public LegendItemCollection getLegendItems() {

        LegendItemCollection result = new LegendItemCollection();
        if (this.subplots != null) {
            Iterator iterator = this.subplots.iterator();
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

        Iterator iterator = this.subplots.iterator();
        while (iterator.hasNext()) {
            XYPlot plot = (XYPlot) iterator.next();
            plot.setRenderer(renderer);
        }

    }

    /**
     * Sets the size (width or height, depending on the orientation of the plot) for the domain
     * axis of each subplot.
     *
     * @param space  the space.
     */
    protected void setFixedRangeAxisSpaceForSubplots(AxisSpace space) {

        Iterator iterator = this.subplots.iterator();
        while (iterator.hasNext()) {
            XYPlot plot = (XYPlot) iterator.next();
            plot.setFixedRangeAxisSpace(space);
        }

    }

    /**
     * Handles a 'click' on the plot by updating the anchor values...
     *
     * @param x  x-coordinate, where the click occured.
     * @param y  y-coordinate, where the click occured.
     * @param info  object containing information about the plot dimensions.
     */
    public void handleClick(int x, int y, PlotRenderingInfo info) {

        Rectangle2D dataArea = info.getDataArea();
        if (dataArea.contains(x, y)) {
            for (int i = 0; i < this.subplots.size(); i++) {
                XYPlot subplot = (XYPlot) this.subplots.get(i);
                PlotRenderingInfo subplotInfo = info.getSubplotInfo(i);
                subplot.handleClick(x, y, subplotInfo);
            }
        }

    }
    
    /**
     * Receives a {@link PlotChangeEvent} and responds by notifying all listeners.
     * 
     * @param event  the event.
     */
    public void plotChanged(PlotChangeEvent event) {
        notifyListeners(event);
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

        if (!(obj instanceof CombinedDomainXYPlot)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }

        CombinedDomainXYPlot p = (CombinedDomainXYPlot) obj;
        if (this.totalWeight != p.totalWeight) {
            return false;
        }
        if (this.gap != p.gap) {
            return false;
        }
        if (!ObjectUtils.equal(this.subplots, p.subplots)) {
            return false;
        }

        return true;
    }
    
    /**
     * Returns a clone of the annotation.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException  this class will not throw this exception, but subclasses
     *         (if any) might.
     */
    public Object clone() throws CloneNotSupportedException {
        
        CombinedDomainXYPlot result = (CombinedDomainXYPlot) super.clone(); 
        result.subplots = ObjectUtils.clone(this.subplots);
        for (Iterator it = result.subplots.iterator(); it.hasNext();) {
            Plot child = (Plot) it.next();
            child.setParent(result);
        }
        
        // after setting up all the subplots, the shared domain axis may need reconfiguring
        ValueAxis domainAxis = result.getDomainAxis();
        if (domainAxis != null) {
            domainAxis.configure();
        }
        
        return result;
        
    }

}
