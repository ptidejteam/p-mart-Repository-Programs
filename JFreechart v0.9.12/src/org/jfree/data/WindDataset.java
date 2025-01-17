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
 * ----------------
 * WindDataset.java
 * ----------------
 * (C) Copyright 2001-2003, by Achilleus Mantzios and Contributors.
 *
 * Original Author:  Achilleus Mantzios;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: WindDataset.java,v 1.1 2007/10/10 19:12:18 vauchers Exp $
 *
 * Changes
 * -------
 * 06-Feb-2002 : Version 1, based on code contributed by Achilleus Mantzios (DG);
 *
 */

package org.jfree.data;

/**
 * Interface for a dataset that supplies wind intensity and direction values
 * observed at various points in time.
 *
 * @author Achilleus Mantzios
 */
public interface WindDataset extends XYDataset {

    /**
     * Returns the wind direction (should be in the range 0 to 12).
     * @param series    The series (zero-based index).
     * @param item      The item (zero-based index).
     * @return The wind direction.
     */
    public Number getWindDirection(int series, int item);

    /**
     * Returns the wind force on the Beaufort scale (0 to 12).
     * @param   series The series (zero-based index).
     * @param   item The item (zero-based index).
     * @return The wind force.
     */
    public Number getWindForce(int series, int item);

}
