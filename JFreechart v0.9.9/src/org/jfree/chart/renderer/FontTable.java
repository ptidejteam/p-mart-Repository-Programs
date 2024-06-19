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
 * --------------
 * FontTable.java
 * --------------
 * (C) Copyright 2003 by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: FontTable.java,v 1.1 2007/10/10 20:07:35 vauchers Exp $
 *
 * Changes
 * -------
 * 11-Jun-2003 : Version 1 (DG);
 */

package org.jfree.chart.renderer;

import java.awt.Font;
import java.io.Serializable;

/**
 * A table of <code>Font</code> objects.
 *
 * @author David Gilbert
 */
public class FontTable extends ObjectTable implements Serializable {

    /**
     * Creates a new font table.
     */
    public FontTable() {
    }

    /**
     * Returns the Font object from a particular cell in the table.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The font.
     */
    public Font getFont(int row, int column) {

        return (Font) getObject(row, column);

    }

    /**
     * Sets the font for a cell in the table.  The table is expanded if necessary.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param font  the font.
     */
    public void setFont(int row, int column, Font font) {

        setObject(row, column, font);

    }

    /**
     * Tests this font table for equality with another object (typically also a font table).
     *
     * @param o  the other object.
     *
     * @return A font.
     */
    public boolean equals(Object o) {

        if (o instanceof FontTable) {
            return super.equals(o);
        }

        return false;

    }

}
