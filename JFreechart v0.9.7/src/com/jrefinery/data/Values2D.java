/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
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
 * -------------
 * Values2D.java
 * -------------
 * (C) Copyright 2002, 2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: Values2D.java,v 1.1 2007/10/10 20:00:04 vauchers Exp $
 *
 * Changes:
 * --------
 * 28-Oct-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.data;

/**
 * A general purpose interface that can be used to access a table of values.
 *
 * @author David Gilbert
 */
public interface Values2D {

    /**
     * Returns the number of rows in the table.
     *
     * @return the row count.
     */
    public int getRowCount();

    /**
     * Returns the number of columns in the table.
     *
     * @return the column count.
     */
    public int getColumnCount();

    /**
     * Returns a value from the table.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return the value (possibly null).
     */
    public Number getValue(int row, int column);

}
