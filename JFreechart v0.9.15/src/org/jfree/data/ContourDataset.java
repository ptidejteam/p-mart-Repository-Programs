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
 * ContourDataset.java
 * -------------------
 * (C) Copyright 2002, 2003, by David M. O'Donnell.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: ContourDataset.java,v 1.1 2007/10/10 19:21:49 vauchers Exp $
 *
 * Changes (from 23-Jan-2003)
 * --------------------------
 * 23-Jan-2003 : Added standard header (DG);
 *
 */

package org.jfree.data;
/**
 * The interface through which JFreeChart obtains data in the form of (x, y, z) items - used for
 * XY and XYZ plots.
 *
 * @author David M. O'Donnell
 */
public interface ContourDataset extends XYZDataset {

    /**
     * Returns the smallest Z data value.
     *
     * @return The minimum Z value.
     */
    public double getMinZValue();

    /**
     * Returns the largest Z data value.
     *
     * @return The maximum Z value.
     */
    public double getMaxZValue();

    /**
     * Returns the array of Numbers representing the x data values.
     *
     * @return The array of x values.
     */
    public Number[] getXValues();

    /**
     * Returns the array of Numbers representing the y data values.
     *
     * @return The array of y values.
     */
    public Number[] getYValues();

    /**
     * Returns the array of Numbers representing the z data values.
     *
     * @return The array of z values.
     */
    public Number[] getZValues();

    /**
     * Returns the maximum z-value within visible region of plot.
     *
     * @param x  the x-value.
     * @param y  the y-value.
     *
     * @return The maximum z-value.
     */
    public Range getZValueRange(Range x, Range y);

    /**
     * Returns true if axis are dates.
     *
     * @param axisNumber  the axis where 0-x, 1-y, and 2-z.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean isDateAxis(int axisNumber);

}
