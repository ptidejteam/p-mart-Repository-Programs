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
 * YisSymbolic.java
 * ----------------
 *
 * Original Author:  Anthony Boulestreau;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: YisSymbolic.java,v 1.1 2007/10/10 19:09:12 vauchers Exp $
 *
 * Changes (from 21-Aug-2001)
 * --------------------------
 * 29-Mar-2002 : First version (AB);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package org.jfree.data;

/**
 * Represent a data set where Y is a symbolic values. Each symbolic value is
 * linked with an Integer.
 *
 * @author Anthony Boulestreau
 */
public interface YisSymbolic {

    /**
     * Returns the list of symbolic values.
     *
     * @return array of symbolic value.
     */
    public String[] getYSymbolicValues();

    /**
     * Returns the symbolic value of the data set specified by
     * <CODE>series</CODE> and <CODE>item</CODE> parameters.
     *
     * @param series  value of the serie.
     * @param item  value of the item.
     *
     * @return the symbolic value.
     */
    public String getYSymbolicValue(int series, int item);

    /**
     * Returns the symbolic value linked with the specified
     * <CODE>Integer</CODE>.
     *
     * @param val value of the integer linked with the symbolic value.
     *
     * @return the symbolic value.
     */
    public String getYSymbolicValue(Integer val);
}
