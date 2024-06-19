/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart
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
 * -------------------
 * CombinableAxis.java
 * -------------------
 * (C) Copyright 2001, 2002, by Bill Kelemen.
 *
 * Original Author:  Bill Kelemen;
 * Contributor(s):   -;
 *
 * $Id: CombinableAxis.java,v 1.1 2007/10/10 18:57:57 vauchers Exp $
 *
 * Changes:
 * --------
 * 06-Dec-2001 : Version 1 (BK);
 * 08-Jan-2002 : Moved to new package com.jrefinery.chart.combination (DG);
 *
 */

package com.jrefinery.chart.combination;

import com.jrefinery.chart.Axis;

/**
 * Interface implemented by all CombinedXXXXXAxis classes. These classes are
 * used by CombinedPlots to display/hide common axes used by inner charts.
 *
 * @author Bill Kelemen (bill@kelemen-usa.com)
 */
public interface CombinableAxis {

    /**
     * Returns the parent axis.
     */
    public Axis getParentAxis();

    /**
     * Returns the range (minimum and maximum) for the axis.
     */
     public AxisRange getRange();

    /**
     * Sets the range (minimum and maximum) recursively for all sub-plots.  This is done after a
     * CombinedPlot has calculated the overall range of all CombinedAxis that share the same Axis
     * for all Plots. This makes all plots display the complete range of their Datasets.
     *
     * @param range The range.
     */
    public void setRange(AxisRange range);

    /**
     * Sets the visible flag on or off for this combined axis. A visible axis will
     * display the axis title, ticks and legend depending on the parent's
     * attributes. An invisible axis will only display gridLines if needed.
     *
     * @param flag New value of flag
     */
    public void setVisible(boolean flag);

    /**
     * Is this axis visible? Is is drawn?
     */
    public boolean isVisible();

    /**
     * The CombinedPlot will calculate the maximim of all reserveWidth or reserveHeight
     * depending on the type of CombinedPlot and inform all CombinedXXXXXAxis to store
     * this value.
     *
     * @param dimension If the axis is vertical, this is width. If axis is
     *        horizontal, then this is height
     */
    public void setReserveDimension(double dimension);

}