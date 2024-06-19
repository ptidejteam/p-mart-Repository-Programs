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
 * -----------------
 * CategoryAxis.java
 * -----------------
 * (C) Copyright 2000-2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 * $Id: CategoryAxis.java,v 1.1 2007/10/10 20:03:22 vauchers Exp $
 *
 * Changes (from 21-Aug-2001)
 * --------------------------
 * 21-Aug-2001 : Added standard header. Fixed DOS encoding problem (DG);
 * 18-Sep-2001 : Updated header (DG);
 * 04-Dec-2001 : Changed constructors to protected, and tidied up default values (DG);
 * 19-Apr-2002 : Updated import statements (DG);
 * 05-Sep-2002 : Updated constructor for changes in Axis class (DG);
 * 06-Nov-2002 : Moved margins from the CategoryPlot class (DG);
 * 08-Nov-2002 : Moved to new package com.jrefinery.chart.axis (DG);
 * 22-Jan-2002 : Removed monolithic constructor (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 *
 */

package org.jfree.chart.axis;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.event.AxisChangeEvent;

/**
 * The base class for axes that display categories.
 *
 * @author David Gilbert
 */
public abstract class CategoryAxis extends Axis implements Serializable {

    /** The default margin for the axis (used for both lower and upper margins). */
    public static final double DEFAULT_AXIS_MARGIN = 0.05;

    /** The default margin between categories (a percentage of the overall axis length). */
    public static final double DEFAULT_CATEGORY_MARGIN = 0.20;
    
    /** The amount of space reserved at the start of the axis. */
    private double lowerMargin;

    /** The amount of space reserved at the end of the axis. */
    private double upperMargin;

    /** The amount of space reserved between categories. */
    private double categoryMargin;

    /**
     * Constructs a category axis, using default values where necessary.
     *
     * @param label  the axis label.
     */
    protected CategoryAxis(String label) {

        super(label);

        this.lowerMargin = DEFAULT_AXIS_MARGIN;
        this.upperMargin = DEFAULT_AXIS_MARGIN;
        this.categoryMargin = DEFAULT_CATEGORY_MARGIN;

        this.setTickMarksVisible(false);  // not supported by this axis type yet

    }

    /**
     * Returns the lower margin for the axis.
     *
     * @return the margin.
     */
    public double getLowerMargin() {
        return this.lowerMargin;
    }

    /**
     * Sets the lower margin for the axis.  An {@link AxisChangeEvent} is sent to all registered
     * listeners.
     *
     * @param margin  the new margin.
     */
    public void setLowerMargin(double margin) {
        this.lowerMargin = margin;
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Returns the upper margin for the axis.
     *
     * @return the margin.
     */
    public double getUpperMargin() {
        return this.upperMargin;
    }

    /**
     * Sets the upper margin for the axis.  An {@link AxisChangeEvent} is sent to all registered
     * listeners.
     *
     * @param margin  the new margin.
     */
    public void setUpperMargin(double margin) {
        this.upperMargin = margin;
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Returns the category margin.
     *
     * @return the margin.
     */
    public double getCategoryMargin() {
        return this.categoryMargin;
    }

    /**
     * Sets the category margin.  An {@link AxisChangeEvent} is sent to all registered
     * listeners.
     *
     * @param margin  the new margin.
     */
    public void setCategoryMargin(double margin) {
        this.categoryMargin = margin;
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Returns the starting coordinate for the specified category.
     *
     * @param category  the category.
     * @param categoryCount  the number of categories.
     * @param area  the data area.
     *
     * @return The coordinate.
     */
    public abstract double getCategoryStart(int category, int categoryCount, Rectangle2D area);

    /**
     * Returns the middle coordinate for the specified category.
     *
     * @param category  the category.
     * @param categoryCount  the number of categories.
     * @param area  the data area.
     *
     * @return The coordinate.
     */
    public abstract double getCategoryMiddle(int category, int categoryCount, Rectangle2D area);

    /**
     * Returns the end coordinate for the specified category.
     *
     * @param category  the category.
     * @param categoryCount  the number of categories.
     * @param area  the data area.
     *
     * @return The coordinate.
     */
    public abstract double getCategoryEnd(int category, int categoryCount, Rectangle2D area);

    /**
     * Tests this axis for equality with another object.
     * 
     * @param obj  the object.
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
 
        if (obj instanceof CategoryAxis) {
            CategoryAxis ca = (CategoryAxis) obj;
            if (super.equals(obj)) {
                boolean b0 = (this.lowerMargin == ca.lowerMargin);
                boolean b1 = (this.upperMargin == ca.upperMargin);
                boolean b2 = (this.categoryMargin == ca.categoryMargin);
                return b0 && b1 && b2;
            }
        }
        
        return false;
               
    }
 
}
