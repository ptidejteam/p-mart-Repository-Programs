/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ----------------------
 * BoxAndWhiskerItem.java
 * ----------------------
 * (C) Copyright 2003-2005, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: BoxAndWhiskerItem.java,v 1.1 2007/10/10 20:17:12 vauchers Exp $
 *
 * Changes
 * -------
 * 27-Aug-2003 : Version 1 (DG); 
 * 01-Mar-2004 : Added equals() method and implemented Serializable (DG);
 * 
 */

package org.jfree.data.statistics;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.jfree.util.ObjectUtilities;

/**
 * Represents one data item within a box-and-whisker dataset.  This class is 
 * immutable.
 */
public class BoxAndWhiskerItem implements Serializable {
    
    /** For serialization. */
    private static final long serialVersionUID = 7329649623148167423L;
    
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
     * @param mean  the mean (<code>null</code> permitted).
     * @param median  the median (<code>null</code> permitted).
     * @param q1  the first quartile (<code>null</code> permitted).
     * @param q3  the third quartile (<code>null</code> permitted).
     * @param minRegularValue  the minimum regular value (<code>null</code> 
     *                         permitted).
     * @param maxRegularValue  the maximum regular value (<code>null</code> 
     *                         permitted).
     * @param minOutlier  the minimum outlier (<code>null</code> permitted).
     * @param maxOutlier  the maximum outlier (<code>null</code> permitted).
     * @param outliers  the outliers (<code>null</code> permitted).
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
     * @return The mean (possibly <code>null</code>).
     */
    public Number getMean() {
        return this.mean;
    }
    
    /**
     * Returns the median.
     * 
     * @return The median (possibly <code>null</code>).
     */
    public Number getMedian() {
        return this.median;
    }
    
    /**
     * Returns the first quartile. 
     * 
     * @return The first quartile (possibly <code>null</code>).
     */
    public Number getQ1() {
        return this.q1;
    }
    
    /**
     * Returns the third quartile. 
     * 
     * @return The third quartile (possibly <code>null</code>).
     */
    public Number getQ3() {
        return this.q3;
    }
    
    /**
     * Returns the minimum regular value.
     * 
     * @return The minimum regular value (possibly <code>null</code>).
     */
    public Number getMinRegularValue() {
        return this.minRegularValue;
    }
    
    /**
     * Returns the maximum regular value. 
     * 
     * @return The maximum regular value (possibly <code>null</code>).
     */
    public Number getMaxRegularValue() {
        return this.maxRegularValue;
    }
    
    /**
     * Returns the minimum outlier.
     * 
     * @return The minimum outlier (possibly <code>null</code>).
     */
    public Number getMinOutlier() {
        return this.minOutlier;
    }
    
    /**
     * Returns the maximum outlier.
     * 
     * @return The maximum outlier (possibly <code>null</code>).
     */
    public Number getMaxOutlier() {
        return this.maxOutlier;
    }
    
    /**
     * Returns a list of outliers.
     * 
     * @return A list of outliers (possibly <code>null</code>).
     */
    public List getOutliers() {
        if (this.outliers == null) {
            return null;
        }
        return Collections.unmodifiableList(this.outliers);
    }
    
    /**
     * Tests this object for equality with an arbitrary object.
     * 
     * @param obj  the object to test against (<code>null</code> permitted).
     * 
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        
        if (obj == this) {
            return true;   
        }
        if (!(obj instanceof BoxAndWhiskerItem)) {
            return false;
        }
        BoxAndWhiskerItem that = (BoxAndWhiskerItem) obj;
        if (!ObjectUtilities.equal(this.mean, that.mean)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.median, that.median)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.q1, that.q1)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.q3, that.q3)) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.minRegularValue, that.minRegularValue
        )) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.maxRegularValue, that.maxRegularValue
        )) {
            return false;
        }
        if (!ObjectUtilities.equal(this.minOutlier, that.minOutlier)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.maxOutlier, that.maxOutlier)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.outliers, that.outliers)) {
            return false;
        }
        return true;
    }
    
}
