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
 * -------------------------------
 * CombinedDomainCategoryPlot.java
 * -------------------------------
 * (C) Copyright 2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: CombinedDomainCategoryPlot.java,v 1.1 2007/10/10 19:12:29 vauchers Exp $
 *
 * Changes:
 * --------
 * 16-May-2003 : Version 1 (DG);
 * 08-Aug-2003 : Adjusted totalWeight in remove(...) method (DG);
 * 19-Aug-2003 : Added equals(...) method, implemented Cloneable and Serializable (DG);
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
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ObjectUtils;

/**
 * A combined category plot where the domain axis is shared.
 *
 * @author David Gilbert
 */
public class CombinedDomainCategoryPlot extends CategoryPlot
                                        implements Cloneable, Serializable {

    /** Storage for the subplot references. */
    private List subplots;

    /** Total weight of all charts. */
    private int totalWeight;

    /** The gap between subplots. */
    private double gap;

    /** Temporary storage for the subplot areas. */
    private transient Rectangle2D[] subplotAreas;
    
    /**
     * Default constructor.
     */
    public CombinedDomainCategoryPlot() {
        this(null);
    }
    
    /**
     * Creates a new plot.
     *
     * @param domainAxis  the shared domain axis.
     */
    public CombinedDomainCategoryPlot(CategoryAxis domainAxis) {

        super(null, domainAxis, null, null);
        this.subplots = new java.util.ArrayList();
        this.totalWeight = 0;
        this.gap = 5.0;

    }

    /**
     * Adds a subplot to the combined chart.
     *
     * @param subplot  the subplot.
     * @param weight  the weight.
     */
    public void add(CategoryPlot subplot, int weight) {
        subplot.setParent(this);
        subplot.setWeight(weight);
        subplot.setInsets(new Insets(0, 0, 0, 0));
        subplot.setDomainAxis(null);
        subplot.setOrientation(getOrientation());
        this.subplots.add(subplot);
        this.totalWeight += weight;
        getDomainAxis().configure();
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Removes a subplot from the combined chart.  Potentially, this removes some unique categories
     * from the overall union of the datasets...so the domain axis is reconfigured, then a 
     * {@link PlotChangeEvent} is sent to all registered listeners.
     *
     * @param subplot  the subplot.
     */
    public void remove(CategoryPlot subplot) {

        subplots.remove(subplot);
        subplot.setParent(null);
        this.totalWeight -= subplot.getWeight();

        CategoryAxis domain = getDomainAxis();
        if (domain != null) {
            domain.configure();
        }

        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Returns the list of subplots.
     *
     * @return An unmodifiable list of subplots .
     */
    public List getSubplots() {
        return Collections.unmodifiableList(this.subplots);
    }

    /**
     * Calculates the space required for the axes.
     * 
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * 
     * @return The space required for the axes.
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
            CategoryAxis categoryAxis = getDomainAxis();
            RectangleEdge categoryEdge = Plot.resolveDomainAxisLocation(
                getDomainAxisLocation(), orientation
            );
            if (categoryAxis != null) {
                space = categoryAxis.reserveSpace(g2, this, plotArea, categoryEdge, space);
            }
            else {
                if (getDrawSharedDomainAxis()) {
                    space = getDomainAxis().reserveSpace(g2, this, plotArea,
                                                         categoryEdge, space);
                }
            }
        }
        
        Rectangle2D adjustedPlotArea = space.shrink(plotArea, null);
        
        // work out the maximum height or width of the non-shared axes...
        int n = subplots.size();
        this.subplotAreas = new Rectangle2D[n];
        double x = adjustedPlotArea.getX();
        double y = adjustedPlotArea.getY();
        double usableSize = 0.0;
        if (orientation == PlotOrientation.HORIZONTAL) {
            usableSize = adjustedPlotArea.getWidth() - gap * (n - 1);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            usableSize = adjustedPlotArea.getHeight() - gap * (n - 1);
        }

        for (int i = 0; i < n; i++) {
            CategoryPlot plot = (CategoryPlot) subplots.get(i);

            // calculate sub-plot area
            if (orientation == PlotOrientation.HORIZONTAL) {
                double w = usableSize * plot.getWeight() / totalWeight;
                this.subplotAreas[i] = new Rectangle2D.Double(x, y, w, 
                                                              adjustedPlotArea.getHeight());
                x = x + w + gap;
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                double h = usableSize * plot.getWeight() / totalWeight;
                this.subplotAreas[i] = new Rectangle2D.Double(x, y, adjustedPlotArea.getWidth(), h);
                y = y + h + gap;
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

        PlotOrientation orientation = getOrientation();

        // calculate the data area...
        AxisSpace space = calculateAxisSpace(g2, plotArea);
        Rectangle2D dataArea = space.shrink(plotArea, null);

        // set the width and height of non-shared axis of all sub-plots
        setFixedRangeAxisSpaceForSubplots(space);

        // draw all the charts
        for (int i = 0; i < this.subplots.size(); i++) {
            CategoryPlot plot = (CategoryPlot) subplots.get(i);
            plot.draw(g2, subplotAreas[i], info);
        }

        if (info != null) {
            info.setDataArea(dataArea);
        }

        // draw the shared axis
        RectangleEdge domainEdge = getDomainAxisEdge();
        double cursor = RectangleEdge.coordinate(dataArea, domainEdge);
        cursor = getDomainAxis().draw(g2, cursor, plotArea, dataArea, domainEdge);

    }

    /**
     * Sets the size (width or height, depending on the orientation of the plot) for the range
     * axis of each subplot.
     *
     * @param space  the space.
     */
    protected void setFixedRangeAxisSpaceForSubplots(AxisSpace space) {

        Iterator iterator = subplots.iterator();
        while (iterator.hasNext()) {
            CategoryPlot plot = (CategoryPlot) iterator.next();
            plot.setFixedRangeAxisSpace(space);
        }

    }

    /**
     * Sets the orientation of the plot (and all subplots).
     * 
     * @param orientation  the orientation.
     */
    public void setOrientation(PlotOrientation orientation) {

        super.setOrientation(orientation);

        Iterator iterator = subplots.iterator();
        while (iterator.hasNext()) {
            CategoryPlot plot = (CategoryPlot) iterator.next();
            plot.setOrientation(orientation);
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
     * Returns an unmodifiable list of the categories contained in all the subplots.
     * 
     * @return The list.
     */
    public List getCategories() {
        
        List result = new java.util.ArrayList();

        if (this.subplots != null) {
            Iterator iterator = subplots.iterator();
            while (iterator.hasNext()) {
                CategoryPlot plot = (CategoryPlot) iterator.next();
                List more = plot.getCategories();
                Iterator moreIterator = more.iterator();
                while (moreIterator.hasNext()) {
                    Comparable category = (Comparable) moreIterator.next();
                    if (!result.contains(category)) {
                        result.add(category);
                    }
                }
            }
        }

        return Collections.unmodifiableList(result);
    }
    
    /** 
     * Tests the plot for equality with an arbitrary object.
     * 
     * @param object  the object to test against.
     * 
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean equals(Object object) {
    
        if (object == null) {
            return false;
        }
        
        if (object == this) {
            return true;
        }
        
        if (object instanceof CombinedDomainCategoryPlot) {
            CombinedDomainCategoryPlot plot = (CombinedDomainCategoryPlot) object;
            if (super.equals(object)) {
                boolean b0 = ObjectUtils.equal(this.subplots, plot.subplots);
                boolean b1 = (this.totalWeight == plot.totalWeight);
                boolean b2 = (this.gap == plot.gap);

                return b0 && b1 && b2;
                          
            } 
        }
        
        return false;
        
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
        return super.clone();    
    }
    
}
