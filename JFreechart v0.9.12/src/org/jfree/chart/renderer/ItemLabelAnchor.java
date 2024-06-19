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
 * --------------------
 * ItemLabelAnchor.java
 * --------------------
 * (C) Copyright 2003 by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: ItemLabelAnchor.java,v 1.1 2007/10/10 19:12:28 vauchers Exp $
 *
 * Changes
 * -------
 * 29-Apr-2003 : Version 1 (DG);
 *
 */
package org.jfree.chart.renderer;

import java.io.Serializable;

/**
 * An enumeration of the positions that a value label can take, relative to an item
 * in a {@link org.jfree.chart.plot.CategoryPlot}.
 *
 * @author David Gilbert
 */
public class ItemLabelAnchor implements Serializable {

    /** Center. */
    public static final ItemLabelAnchor CENTER = new ItemLabelAnchor("ItemLabelAnchor.CENTER");

    /** INSIDE1. */
    public static final ItemLabelAnchor INSIDE1 = new ItemLabelAnchor("ItemLabelAnchor.INSIDE1");

    /** INSIDE2. */
    public static final ItemLabelAnchor INSIDE2 = new ItemLabelAnchor("ItemLabelAnchor.INSIDE2");

    /** INSIDE3. */
    public static final ItemLabelAnchor INSIDE3 = new ItemLabelAnchor("ItemLabelAnchor.INSIDE3");

    /** INSIDE4. */
    public static final ItemLabelAnchor INSIDE4 = new ItemLabelAnchor("ItemLabelAnchor.INSIDE4");

    /** INSIDE5. */
    public static final ItemLabelAnchor INSIDE5 = new ItemLabelAnchor("ItemLabelAnchor.INSIDE5");

    /** INSIDE6. */
    public static final ItemLabelAnchor INSIDE6 = new ItemLabelAnchor("ItemLabelAnchor.INSIDE6");

    /** INSIDE7. */
    public static final ItemLabelAnchor INSIDE7 = new ItemLabelAnchor("ItemLabelAnchor.INSIDE7");

    /** INSIDE8. */
    public static final ItemLabelAnchor INSIDE8 = new ItemLabelAnchor("ItemLabelAnchor.INSIDE8");

    /** INSIDE9. */
    public static final ItemLabelAnchor INSIDE9 = new ItemLabelAnchor("ItemLabelAnchor.INSIDE9");

    /** INSIDE10. */
    public static final ItemLabelAnchor INSIDE10 = new ItemLabelAnchor("ItemLabelAnchor.INSIDE10");

    /** INSIDE11. */
    public static final ItemLabelAnchor INSIDE11 = new ItemLabelAnchor("ItemLabelAnchor.INSIDE11");

    /** INSIDE12. */
    public static final ItemLabelAnchor INSIDE12 = new ItemLabelAnchor("ItemLabelAnchor.INSIDE12");

    /** OUTSIDE1. */
    public static final ItemLabelAnchor OUTSIDE1 = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE1");

    /** OUTSIDE2. */
    public static final ItemLabelAnchor OUTSIDE2 = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE2");

    /** OUTSIDE3. */
    public static final ItemLabelAnchor OUTSIDE3 = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE3");

    /** OUTSIDE4. */
    public static final ItemLabelAnchor OUTSIDE4 = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE4");

    /** OUTSIDE5. */
    public static final ItemLabelAnchor OUTSIDE5 = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE5");

    /** OUTSIDE6. */
    public static final ItemLabelAnchor OUTSIDE6 = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE6");

    /** OUTSIDE7. */
    public static final ItemLabelAnchor OUTSIDE7 = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE7");

    /** OUTSIDE8. */
    public static final ItemLabelAnchor OUTSIDE8 = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE8");

    /** OUTSIDE9. */
    public static final ItemLabelAnchor OUTSIDE9 = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE9");

    /** OUTSIDE10. */
    public static final ItemLabelAnchor OUTSIDE10 
        = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE10");

    /** OUTSIDE11. */
    public static final ItemLabelAnchor OUTSIDE11 
        = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE11");

    /** OUTSIDE12. */
    public static final ItemLabelAnchor OUTSIDE12 
        = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE12");

    /** The name. */
    private String name;

    /**
     * Private constructor.
     *
     * @param name  the name.
     */
    private ItemLabelAnchor(String name) {
        this.name = name;
    }

    /**
     * Returns a string representing the object.
     *
     * @return The string.
     */
    public String toString() {
        return this.name;
    }

    /**
     * Returns <code>true</code> if this object is equal to the specified object, and
     * <code>false</code> otherwise.
     *
     * @param o  the other object.
     *
     * @return A boolean.
     */
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemLabelAnchor)) {
            return false;
        }

        final ItemLabelAnchor order = (ItemLabelAnchor) o;
        if (!this.name.equals(order.toString())) {
            return false;
        }

        return true;

    }

    /**
     * Returns the anchor point that is horizontally opposite the given anchor point.
     * 
     * @param anchor  an anchor point.
     * 
     * @return The opposite anchor point.
     */
    public static ItemLabelAnchor getHorizontalOpposite(ItemLabelAnchor anchor) {

        if (anchor == ItemLabelAnchor.CENTER) {
            return ItemLabelAnchor.CENTER;
        }
        else if (anchor == ItemLabelAnchor.INSIDE1) {
            return ItemLabelAnchor.INSIDE11;
        }
        else if (anchor == ItemLabelAnchor.INSIDE2) {
            return ItemLabelAnchor.INSIDE10;
        }
        else if (anchor == ItemLabelAnchor.INSIDE3) {
            return ItemLabelAnchor.INSIDE9;
        }
        else if (anchor == ItemLabelAnchor.INSIDE4) {
            return ItemLabelAnchor.INSIDE8;
        }
        else if (anchor == ItemLabelAnchor.INSIDE5) {
            return ItemLabelAnchor.INSIDE7;
        }
        else if (anchor == ItemLabelAnchor.INSIDE6) {
            return ItemLabelAnchor.INSIDE6;
        }
        else if (anchor == ItemLabelAnchor.INSIDE7) {
            return ItemLabelAnchor.INSIDE5;
        }
        else if (anchor == ItemLabelAnchor.INSIDE8) {
            return ItemLabelAnchor.INSIDE4;
        }
        else if (anchor == ItemLabelAnchor.INSIDE9) {
            return ItemLabelAnchor.INSIDE3;
        }
        else if (anchor == ItemLabelAnchor.INSIDE10) {
            return ItemLabelAnchor.INSIDE2;
        }
        else if (anchor == ItemLabelAnchor.INSIDE11) {
            return ItemLabelAnchor.INSIDE1;
        }
        else if (anchor == ItemLabelAnchor.INSIDE12) {
            return ItemLabelAnchor.INSIDE12;
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE1) {
            return ItemLabelAnchor.OUTSIDE11;
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE2) {
            return ItemLabelAnchor.OUTSIDE10;
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE3) {
            return ItemLabelAnchor.OUTSIDE9;
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE4) {
            return ItemLabelAnchor.OUTSIDE8;
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE5) {
            return ItemLabelAnchor.OUTSIDE7;
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE6) {
            return ItemLabelAnchor.OUTSIDE6;
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE7) {
            return ItemLabelAnchor.OUTSIDE5;
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE8) {
            return ItemLabelAnchor.OUTSIDE4;
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE9) {
            return ItemLabelAnchor.OUTSIDE3;
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE10) {
            return ItemLabelAnchor.OUTSIDE2;
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE11) {
            return ItemLabelAnchor.OUTSIDE1;
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE12) {
            return ItemLabelAnchor.OUTSIDE12;
        }
        return null;
    }

    /**
     * Returns the anchor point that is vertically opposite the given anchor point.
     * 
     * @param anchor  an anchor point.
     * 
     * @return The opposite anchor point.
     */
    public static ItemLabelAnchor getVerticalOpposite(ItemLabelAnchor anchor) {

        if (anchor == ItemLabelAnchor.CENTER) {
            return ItemLabelAnchor.CENTER;
        }
        else if (anchor == ItemLabelAnchor.INSIDE1) {
            return ItemLabelAnchor.INSIDE5;
        }
        else if (anchor == ItemLabelAnchor.INSIDE2) {
            return ItemLabelAnchor.INSIDE4;
        }
        else if (anchor == ItemLabelAnchor.INSIDE3) {
            return ItemLabelAnchor.INSIDE3;
        }
        else if (anchor == ItemLabelAnchor.INSIDE4) {
            return ItemLabelAnchor.INSIDE2;
        }
        else if (anchor == ItemLabelAnchor.INSIDE5) {
            return ItemLabelAnchor.INSIDE1;
        }
        else if (anchor == ItemLabelAnchor.INSIDE6) {
            return ItemLabelAnchor.INSIDE12;
        }
        else if (anchor == ItemLabelAnchor.INSIDE7) {
            return ItemLabelAnchor.INSIDE11;
        }
        else if (anchor == ItemLabelAnchor.INSIDE8) {
            return ItemLabelAnchor.INSIDE10;
        }
        else if (anchor == ItemLabelAnchor.INSIDE9) {
            return ItemLabelAnchor.INSIDE9;
        }
        else if (anchor == ItemLabelAnchor.INSIDE10) {
            return ItemLabelAnchor.INSIDE8;
        }
        else if (anchor == ItemLabelAnchor.INSIDE11) {
            return ItemLabelAnchor.INSIDE7;
        }
        else if (anchor == ItemLabelAnchor.INSIDE12) {
            return ItemLabelAnchor.INSIDE6;
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE1) {
            return ItemLabelAnchor.OUTSIDE5;
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE2) {
            return ItemLabelAnchor.OUTSIDE4;
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE3) {
            return ItemLabelAnchor.OUTSIDE3;
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE4) {
            return ItemLabelAnchor.OUTSIDE2;
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE5) {
            return ItemLabelAnchor.OUTSIDE1;
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE6) {
            return ItemLabelAnchor.OUTSIDE12;
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE7) {
            return ItemLabelAnchor.OUTSIDE11;
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE8) {
            return ItemLabelAnchor.OUTSIDE10;
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE9) {
            return ItemLabelAnchor.OUTSIDE9;
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE10) {
            return ItemLabelAnchor.OUTSIDE8;
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE11) {
            return ItemLabelAnchor.OUTSIDE7;
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE12) {
            return ItemLabelAnchor.OUTSIDE6;
        }
        return null;
    }

}
