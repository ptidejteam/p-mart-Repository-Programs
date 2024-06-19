/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
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
 * -------------------------------------
 * StandardCategoryToolTipGenerator.java
 * -------------------------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: StandardCategoryToolTipGenerator.java,v 1.1 2007/10/10 18:56:25 vauchers Exp $
 *
 * Changes
 * -------
 * 13-Dec-2001 : Version 1 (DG);
 * 16-Jan-2002 : Completed Javadocs (DG);
 *
 */

package com.jrefinery.chart.tooltips;

import com.jrefinery.data.CategoryDataset;

/**
 * A standard tooltip generator for plots that use data from a CategoryDataset.
 */
public class StandardCategoryToolTipGenerator implements CategoryToolTipGenerator {

    /**
     * Generates a tooltip text item for a particular category within a series.
     * @param data The dataset.
     * @param series The series number (zero-based index).
     * @param category The category.
     */
    public String generateToolTip(CategoryDataset data, int series, Object category) {

        String result = null;
        Number value = data.getValue(series, category);
        if (value!=null) {
            String seriesName = data.getSeriesName(series);
            String categoryName = category.toString();
            result = seriesName+", "+categoryName+" = "+value.toString();
        }

        return result;

    }

}