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
 * ------------------
 * PlotException.java
 * ------------------
 * (C) Copyright 2000-2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: PlotException.java,v 1.1 2007/10/10 19:09:07 vauchers Exp $
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
 * 15-Nov-2001 : Changed name from DataSourceNotCompatibleException to PlotException (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package org.jfree.chart.plot;

/**
 * A general purpose exception class for plots.
 *
 * @author David Gilbert
 */
public class PlotException extends Exception {

    /**
     * Constructs a new plot exception.
     *
     * @param message  a message describing the exception.
     */
    public PlotException(String message) {
        super(message);
    }

}
