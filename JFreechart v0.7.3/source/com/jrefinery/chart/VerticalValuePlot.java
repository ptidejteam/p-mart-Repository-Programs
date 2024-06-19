/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
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
 * ----------------------
 * VerticalValuePlot.java
 * ----------------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: VerticalValuePlot.java,v 1.1 2007/10/10 18:57:06 vauchers Exp $
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
 *
 */

package com.jrefinery.chart;

/**
 * An interface defining methods for interrogating a plot that displays values along the
 * vertical axis.
 * <P>
 * Used by vertical axes (when auto-adjusting the axis range) to determine the minimum and
 * maximum data values.
 */
public interface VerticalValuePlot {

    /**
     * Returns the minimum value in either the domain or the range, whichever is displayed against
     * the vertical axis for the particular type of plot implementing this interface.
     */
    public Number getMinimumVerticalDataValue();

    /**
     * Returns the maximum value in either the domain or the range, whichever is displayed against
     * the vertical axis for the particular type of plot implementing this interface.
     */
    public Number getMaximumVerticalDataValue();

}
