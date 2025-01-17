/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ----------------
 * YisSymbolic.java
 * ----------------
 *
 * Original Author:  Anthony Boulestreau;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: YisSymbolic.java,v 1.1 2007/10/10 20:29:35 vauchers Exp $
 *
 * Changes (from 21-Aug-2001)
 * --------------------------
 * 29-Mar-2002 : First version (AB);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 18-Aug-2004 : Moved from org.jfree.data --> org.jfree.data.xy (DG);
 *
 */

package org.jfree.data.xy;

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
     * @return The symbolic values.
     */
    public String[] getYSymbolicValues();

    /**
     * Returns the symbolic value of the data set specified by
     * <CODE>series</CODE> and <CODE>item</CODE> parameters.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The symbolic value.
     */
    public String getYSymbolicValue(int series, int item);

    /**
     * Returns the symbolic value linked with the specified
     * <CODE>Integer</CODE>.
     *
     * @param val  value of the integer linked with the symbolic value.
     *
     * @return The symbolic value.
     */
    public String getYSymbolicValue(Integer val);
    
}
