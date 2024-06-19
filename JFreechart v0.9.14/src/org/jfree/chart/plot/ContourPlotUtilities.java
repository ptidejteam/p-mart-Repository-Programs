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
 * -------------------------
 * ContourPlotUtilities.java
 * -------------------------
 * (C) Copyright 2002, 2003 by David M. O'Donnell and Contributors.
 *
 * Original Author:  David M. O'Donnell;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: ContourPlotUtilities.java,v 1.1 2007/10/10 19:19:05 vauchers Exp $
 *
 * Changes
 * -------
 * 26-Nov-2002 : Version 1 contributed by David M. O'Donnell (DG);
 * 22-Jul-2003 : Made this class abstract as there is no need to instantiate it (DG);
 *
 */

package org.jfree.chart.plot;

import org.jfree.data.ContourDataset;
import org.jfree.data.DefaultContourDataset;
import org.jfree.data.Range;

/**
 * Some utility methods for the {@link ContourPlot} class.
 *
 * @author David M. O'Donnell
 */
public abstract class ContourPlotUtilities {

    /**
     * Returns the visible z-range.
     *
     * @param data  the dataset.
     * @param x  the x range.
     * @param y  the y range.
     *
     * @return The range.
     */
    public static Range visibleRange(ContourDataset data, Range x, Range y) {
        Range range = null;
        range = ((DefaultContourDataset) data).getZValueRange(x, y);
        return range;
    }

}
