/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
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
 * ------------------------
 * HorizontalValuePlot.java
 * ------------------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: HorizontalValuePlot.java,v 1.1 2007/10/10 19:02:26 vauchers Exp $
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
 * 22-Apr-2002 : Replaced existing methods with getHorizontalRange() method (DG);
 * 29-Apr-2002 : Added getHorizontalAxis() method (DG);
 *
 */

package com.jrefinery.chart;

import com.jrefinery.data.Range;

/**
 * An interface defining methods for interrogating a plot that displays values along the
 * horizontal axis.
 * <P>
 * Used by horizontal axes (when auto-adjusting the axis range) to determine the minimum and
 * maximum data values.
 */
public interface HorizontalValuePlot {

    /**
     * Returns the range of data values to be plotted along the horizontal axis.
     *
     * @return The range.
     */
    public Range getHorizontalDataRange();

    public ValueAxis getHorizontalValueAxis();

}
