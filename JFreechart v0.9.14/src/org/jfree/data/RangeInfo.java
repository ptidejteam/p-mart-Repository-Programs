/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, Object Refinery Limited and Contributors.
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
 * --------------
 * RangeInfo.java
 * --------------
 * (C) Copyright 2000-2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: RangeInfo.java,v 1.1 2007/10/10 19:19:00 vauchers Exp $
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
 * 15-Nov-2001 : Moved to package com.jrefinery.data.* (DG);
 *               Updated Javadoc comments (DG);
 * 22-Apr-2002 : Added getValueRange() method (DG);
 *
 */

package org.jfree.data;

/**
 * An interface (optional) that can be implemented by a dataset to assist in determining the
 * minimum and maximum values.
 *
 * @author David Gilbert
 */
public interface RangeInfo {

    /**
     * Returns the minimum value in the dataset's range (or null if all the
     * values in the range are null).
     *
     * @return the minimum value.
     */
    public Number getMinimumRangeValue();

    /**
     * Returns the maximum value in the dataset's range (or null if all the
     * values in the range are null).
     *
     * @return the maximum value.
     */
    public Number getMaximumRangeValue();

    /**
     * Returns the range of the values in this dataset's range.
     *
     * @return the range.
     */
    public Range getValueRange();

}
