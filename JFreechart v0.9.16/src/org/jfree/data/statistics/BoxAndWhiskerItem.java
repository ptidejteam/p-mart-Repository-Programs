/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ----------------------
 * BoxAndWhiskerItem.java
 * ----------------------
 * (C) Copyright 2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: BoxAndWhiskerItem.java,v 1.1 2007/10/10 19:25:37 vauchers Exp $
 *
 * Changes
 * -------
 * 27-Aug-2003 : Version 1 (DG); 
 */

package org.jfree.data.statistics;

import java.util.Collections;
import java.util.List;

/**
 * Represents one data item within a box-and-whisker dataset.  This class is immutable.
 * 
 * @author David Gilbert
 */
public class BoxAndWhiskerItem {
    
    /** The mean. */
    private Number mean;
    
    /** The median. */
    private Number median;
    
    /** The first quarter. */
    private Number q1;
    
    /** The third quarter. */
    private Number q3;
    
    /** The minimum regular value. */
    private Number minRegularValue;
    
    /** The maximum regular value. */
    private Number maxRegularValue;
    
    /** The minimum outlier. */
    private Number minOutlier;
    
    /** The maximum outlier. */
    private Number maxOutlier;
    
    /** The outliers. */
    private List outliers;
    
    /**
     * Creates a new box-and-whisker item.
     * 
     * @param mean  the mean.
     * @param median  the median.
     * @param q1  the first quartile.
     * @param q3  the third quartile.
     * @param minRegularValue  the minimum regular value.
     * @param maxRegularValue  the maximum regular value.
     * @param minOutlier  the minimum outlier.
     * @param maxOutlier  the maximum outlier.
     * @param outliers  the outliers.
     */
    public BoxAndWhiskerItem(Number mean, 
                             Number median,
                             Number q1,
                             Number q3,
                             Number minRegularValue,
                             Number maxRegularValue,
                             Number minOutlier,
                             Number maxOutlier,
                             List outliers) {
                                 
        this.mean = mean;
        this.median = median;    
        this.q1 = q1;
        this.q3 = q3;
        this.minRegularValue = minRegularValue;
        this.maxRegularValue = maxRegularValue;
        this.minOutlier = minOutlier;
        this.maxOutlier = maxOutlier;
        this.outliers = outliers;
        
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
     * Returns the median.
     * 
     * @return The median.
     */
    public Number getMedian() {
        return this.median;
    }
    
    /**
     * Returns the first quartile. 
     * 
     * @return The first quartile.
     */
    public Number getQ1() {
        return this.q1;
    }
    
    /**
     * Returns the third quartile. 
     * 
     * @return The third quartile.
     */
    public Number getQ3() {
        return this.q3;
    }
    
    /**
     * Returns the minimum regular value.
     * 
     * @return The minimum regular value.
     */
    public Number getMinRegularValue() {
        return this.minRegularValue;
    }
    
    /**
     * Returns the maximum regular value. 
     * 
     * @return The maximum regular value.
     */
    public Number getMaxRegularValue() {
        return this.maxRegularValue;
    }
    
    /**
     * Returns the minimum outlier.
     * 
     * @return The minimum outlier.
     */
    public Number getMinOutlier() {
        return this.minOutlier;
    }
    
    /**
     * Returns the maximum outlier.
     * 
     * @return The maximum outlier.
     */
    public Number getMaxOutlier() {
        return this.maxOutlier;
    }
    
    /**
     * Returns a list of outliers.
     * 
     * @return A list of outliers.
     */
    public List getOutliers() {
        return Collections.unmodifiableList(this.outliers);
    }
    
}
