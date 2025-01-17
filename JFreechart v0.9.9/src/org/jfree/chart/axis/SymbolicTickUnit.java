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
 * ---------------------
 * SymbolicTickUnit.java
 * ---------------------
 * (C) Copyright 2002, 2003, by Anthony Boulestreau.
 *
 * Original Author:  Anthony Boulestreau;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: SymbolicTickUnit.java,v 1.1 2007/10/10 20:07:40 vauchers Exp $
 *
 * Changes
 * -------
 * 01-May-2002 : Version 1, creation of SymbolicTickUnit to work with VerticalSymbolicAxis and
 *               HorizontalSymbolicAxis (AB);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG):
 * 08-Nov-2002 : Moved to new package com.jrefinery.chart.axis (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 *
 */

package org.jfree.chart.axis;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * A symbolic tick unit.
 *
 * @author Anthony Boulestreau
 */
public class SymbolicTickUnit extends NumberTickUnit implements Serializable {

    /** The list of symbolic value to display instead of the numeric values */
    private List symbolicValue;

    /**
     * Creates a new symbolic tick unit.
     *
     * @param size  the size of the tick unit.
     * @param sv  the list of symbolic value to display instead of the numeric value.
     */
    public SymbolicTickUnit(double size, String[] sv) {
        super(size, null);
        this.symbolicValue = Arrays.asList(sv);
    }

    /**
     * Converts a value to a string, using the list of symbolic values.
     * ex: if the symbolic value list is ["up", "down"] then 0 is convert to
     * "up" and 1 to "down".
     *
     * @param value  value to convert.
     *
     * @return the symbolic value.
     */
    public String valueToString(double value) {

        String strToReturn;
        try {
            strToReturn = (String) this.symbolicValue.get((int) value);
        }
        catch (IndexOutOfBoundsException  ex) {
            throw new IllegalArgumentException (
                "The value " + value + " does not have a corresponding symbolic value");
        }
        return strToReturn;
    }

}
