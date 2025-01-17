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
 * ---------------
 * XYDataPair.java
 * ---------------
 * (C) Copyright 2001-2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: XYDataPair.java,v 1.1 2007/10/10 19:09:12 vauchers Exp $
 *
 * Changes
 * -------
 * 15-Nov-2001 : Version 1 (DG);
 * 24-Jun-2002 : Removed unnecessary import (DG);
 * 27-Aug-2002 : Implemented cloneable (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 05-Aug-2003 : Renamed XYDataPair --> XYDataItem (DG);
 *
 */

package org.jfree.data;


/**
 * Represents one (x, y) data item for an xy-series.
 * 
 * @deprecated Use {@link XYDataItem}.
 *
 * @author David Gilbert
 */
public class XYDataPair extends XYDataItem {
    /**
     * Constructs a new data pair.
     *
     * @param x  the x-value.
     * @param y  the y-value.
     */
    public XYDataPair(Number x, Number y) {
        super(x, y);
    }

    /**
     * Constructs a new data pair.
     *
     * @param x  the x-value.
     * @param y  the y-value.
     */
    public XYDataPair(double x, double y) {
        super(x, y);
    }

}
