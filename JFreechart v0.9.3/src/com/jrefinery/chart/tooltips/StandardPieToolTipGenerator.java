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
 * --------------------------------
 * StandardPieToolTipGenerator.java
 * --------------------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Richard Atkinson;
 *
 * $Id: StandardPieToolTipGenerator.java,v 1.1 2007/10/10 19:52:22 vauchers Exp $
 *
 * Changes
 * -------
 * 13-Dec-2001 : Version 1 (DG);
 * 16-Jan-2002 : Completed Javadocs (DG);
 * 29-Aug-2002 : Changed to format numbers using default locale (RA);
 */

package com.jrefinery.chart.tooltips;

import com.jrefinery.data.PieDataset;
import java.text.NumberFormat;

/**
 * A standard tooltip generator for plots that use data from a PieDataset.
 */
public class StandardPieToolTipGenerator implements PieToolTipGenerator {
    /** NumberFormat for the default locale */
    protected NumberFormat numberFormat = NumberFormat.getInstance();

    /**
     * Generates a tooltip text item for a particular category.
     *
     * @param data      The dataset.
     * @param category  The category.
     * @return a tooltip text or <code>null</code> if value is
     *      <code>null</code>.
     */
    public String generateToolTip(PieDataset data, Object category) {

        String result = null;
        Number value = data.getValue(category);
        if (value!=null) {
            String categoryName = category.toString();
            result = categoryName + " = " + this.numberFormat.format(value);
        }

        return result;

    }

}
