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
 * -------------------------------
 * AxisNotCompatibleException.java
 * -------------------------------
 * (C) Copyright 2000-2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: AxisNotCompatibleException.java,v 1.1 2007/10/10 19:12:30 vauchers Exp $
 *
 * Changes (from 21-Aug-2001)
 * --------------------------
 * 21-Aug-2001 : Added standard header. Fixed DOS encoding problem (DG);
 * 18-Sep-2001 : Updated header (DG);
 * 30-Nov-2001 : Now extends RuntimeException rather than Exception, as suggested by Joao Guilherme
 *               Del Valle (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 08-Nov-2002 : Moved to new package com.jrefinery.chart.axis (DG);
 *
 */

package org.jfree.chart.axis;

/**
 * An exception that is generated when assigning an axis to a plot *if* the
 * axis is not compatible with the plot type.  For example, a CategoryAxis is
 * not compatible with an XYPlot.
 *
 * @author David Gilbert
 */
public class AxisNotCompatibleException extends RuntimeException {

    /**
     * Constructs a new exception.
     *
     * @param message  a message describing the exception.
     */
    public AxisNotCompatibleException(String message) {
        super(message);
    }

}
