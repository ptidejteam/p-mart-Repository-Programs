/* ============================================
 * JFreeChart : a free Java chart class library
 * ============================================
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
 * ---------------
 * XYZDataset.java
 * ---------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: XYZDataset.java,v 1.1 2007/10/10 19:52:14 vauchers Exp $
 *
 * Changes
 * -------
 * 31-Oct-2001 : Initial version (DG);
 *
 */
package com.jrefinery.data;

/**
 * The interface through which JFreeChart obtains data in the form of (x, y, z)
 * items - used for XY and XYZ plots.
 */
public interface XYZDataset extends XYDataset {

    /**
     * Returns the z-value for the specified series and item.
     * @param series    The series (zero-based index).
     * @param item      The item (zero-based index).
     *
     * @return the z-value for the specified series and item.
     */
    public Number getZValue(int series, int item);

}
