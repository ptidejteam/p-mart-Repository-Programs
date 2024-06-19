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
 * ------------------------------
 * CategoryURLGeneratorTable.java
 * ------------------------------
 * (C) Copyright 2003 by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: CategoryURLGeneratorTable.java,v 1.1 2007/10/10 19:21:54 vauchers Exp $
 *
 * Changes
 * -------
 * 15-Jul-2003 : Version 1 (DG);
 * 28-Oct-2003 : Deprecated this class, it is no longer used by JFreeChart (DG);
 *
 */

package org.jfree.chart.renderer;

import java.io.Serializable;

import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.util.ObjectTable;

/**
 * A lookup table for URL generators. Now based on the object table.
 *
 * @author David Gilbert
 * 
 * @deprecated This class is no longer being used in JFreeChart and will be removed before 
 *             version 1.0.0 is released. 
 */
public class CategoryURLGeneratorTable extends ObjectTable implements Serializable {

    /**
     * Creates a new URL generator table.
     */
    public CategoryURLGeneratorTable() {
    }

    /**
     * Returns the URL generator object from a particular cell in the table.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The URL generator.
     */
    public CategoryURLGenerator getURLGenerator(int row, int column) {

        return (CategoryURLGenerator) getObject(row, column);

    }

    /**
     * Sets the URL generator for a cell in the table.  The table is expanded if necessary.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param generator  the URL generator.
     */
    public void setURLGenerator(int row, int column, CategoryURLGenerator generator) {

        setObject(row, column, generator);

    }

    /**
     * Tests this paint table for equality with another object (typically another label generator
     * table).
     *
     * @param o  the other object.
     *
     * @return A boolean.
     */
    public boolean equals(Object o) {

        if (o instanceof CategoryURLGeneratorTable) {
            return super.equals(o);
        }

        return false;

    }
}

