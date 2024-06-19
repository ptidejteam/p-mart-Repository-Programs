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
 * -----------------------------
 * MeanAndStandardDeviation.java
 * -----------------------------
 * (C) Copyright 2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: MeanAndStandardDeviation.java,v 1.1 2007/10/10 19:19:00 vauchers Exp $
 *
 * Changes:
 * --------
 * 05-Feb-2002 : Version 1 (DG);
 *
 */

package org.jfree.data;

/**
 * A simple data structure that holds a mean value and a standard deviation value.  This
 * is used in the {@link org.jfree.data.statistics.DefaultStatisticalCategoryDataset} class.
 *
 * @author David Gilbert
 */
public class MeanAndStandardDeviation {

    /** The mean. */
    private Number mean;

    /** The standard deviation. */
    private Number standardDeviation;

    /**
     * Creates a new mean and standard deviation record.
     *
     * @param mean  the mean.
     * @param standardDeviation  the standard deviation.
     */
    public MeanAndStandardDeviation(Number mean, Number standardDeviation) {
        this.mean = mean;
        this.standardDeviation = standardDeviation;
    }

    /**
     * Returns the mean.
     *
     * @return The mean.
     */
    public Number getMean() {
        return this.mean;
    }

    /**
     * Returns the standard deviation.
     *
     * @return The standard deviation.
     */
    public Number getStandardDeviation() {
        return this.standardDeviation;
    }

}
