/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
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
 * ----------------------------
 * StandardXYZURLGenerator.java
 * ----------------------------
 * (C) Copyright 2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributors:     -;
 *
 * $Id: StandardXYZURLGenerator.java,v 1.1 2007/10/10 19:57:58 vauchers Exp $
 *
 * Changes:
 * --------
 * 03-Feb-2003 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.urls;

import com.jrefinery.data.XYZDataset;

/**
 * A URL generator.
 * 
 * @author David Gilbert
 */
public class StandardXYZURLGenerator extends StandardXYURLGenerator 
                                     implements XYZURLGenerator {
                                        
    /**
     * Generates a URL for a particular item within a series.
     *
     * @param data  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return a string containing the generated URL.
     */
    public String generateURL(XYZDataset data, int series, int item) {
        return super.generateURL(data, series, item);
    }

}
