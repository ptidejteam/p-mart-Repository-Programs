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
 * ------------------------
 * PieLabelDistributor.java
 * ------------------------
 * (C) Copyright 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: PieLabelDistributor.java,v 1.1 2007/10/10 19:34:47 vauchers Exp $
 *
 * Changes
 * -------
 * 08-Mar-2004 : Version 1 (DG);
 *
 */

package org.jfree.chart.plot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jfree.util.LogContext;
import org.jfree.util.Log;

/**
 * This class distributes the section labels for one side of a pie chart so that they do not 
 * overlap.
 */
public class PieLabelDistributor {
    
    /** The label records. */
    private List labels;
    
    /** The minimum gap. */
    private double minGap = 4.0;
    
    /** Access to logging facilities. */
    protected static final LogContext logger = Log.createContext(PieLabelDistributor.class);

    /**
     * Creates a new distributor.
     * 
     * @param labelCount  the number of labels.
     */
    public PieLabelDistributor(int labelCount) {
        this.labels = new ArrayList(labelCount);
    }
    
    /**
     * Returns a label record from the list.
     * 
     * @param index  the index.
     * 
     * @return the label record.
     */
    public PieLabelRecord getPieLabelRecord(int index) {
        return (PieLabelRecord) this.labels.get(index);   
    }
    
    /**
     * Adds a label record.
     * 
     * @param record  the label record.
     */
    public void addPieLabelRecord(PieLabelRecord record) {
        this.labels.add(record);
    }
    
    /**
     * Returns the number of items in the list.
     * 
     * @return the item count.
     */
    public int getItemCount() {
        return this.labels.size();   
    }
    
    /**
     * Distributes the labels.
     * 
     * @param minY  the minimum y-coordinate in Java2D-space.
     * @param height  the height.
     */
    public void distributeLabels(double minY, double height) {
        if (logger.isDebugEnabled()) {
            logger.debug("Entered distributeLabels() method.");
            logger.debug(this.toString());
        }
        sort();
        if (logger.isDebugEnabled()) {
            logger.debug("After sorting...");
            logger.debug(this.toString());
        }
        if (isOverlap()) {
            if (logger.isDebugEnabled()) {
                logger.debug("First overlapping label test triggered.");   
            }
            adjustInwards();
        }
        
        // if still overlapping, do something else...
        if (isOverlap()) {
            double y = minY;
        
            double sumOfLabelHeights = 0.0;
            for (int i = 0; i < this.labels.size(); i++) {
                sumOfLabelHeights += getPieLabelRecord(i).getLabelHeight();
            }
            double gap = height - sumOfLabelHeights;
            if (this.labels.size() > 1) {
                gap = gap / (this.labels.size() - 1);   
            }
        
            for (int i = 0; i < this.labels.size(); i++) {
                PieLabelRecord record = getPieLabelRecord(i);
                y = y + record.getLabelHeight() / 2.0;
                record.setAllocatedY(y);
                y = y + record.getLabelHeight() / 2.0 + gap;
            }
        }

    }
    
    /**
     * Returns <code>true</code> if there are overlapping labels in the list, and <code>false</code>
     * otherwise.
     * 
     * @return a boolean.
     */
    private boolean isOverlap() {
        double y = 0.0;
        for (int i = 0; i < this.labels.size(); i++) {
            PieLabelRecord plr = getPieLabelRecord(i);
            if (logger.isDebugEnabled()) {
                logger.debug("y = " + y + ", plr.getLowerY() = " + plr.getLowerY());   
            }
            if (y > plr.getLowerY()) {
                return true;
            }
            y = plr.getUpperY();
            if (logger.isDebugEnabled()) {
                logger.debug("y is now " + y);   
            }
            
        }
        return false;
    }
    
    /**
     * Adjusts the y-coordinate for the labels in towards the center in an attempt to fix 
     * overlapping.
     */
    protected void adjustInwards() {
        if (logger.isDebugEnabled()) {
            logger.debug("Entering adjustInwards() method.");   
        }
        int lower = 0;
        int upper = this.labels.size() - 1;
        while (upper > lower) {
            if (lower < upper - 1) {
                PieLabelRecord r0 = getPieLabelRecord(lower);
                PieLabelRecord r1 = getPieLabelRecord(lower + 1);
                if (logger.isDebugEnabled()) {
                    logger.debug("r0.getAllocatedY() = " + r0.getAllocatedY());   
                    logger.debug("r1.getAllocatedY() = " + r1.getAllocatedY());   
                }
                if (r1.getLowerY() < r0.getUpperY()) {
                    double adjust = r0.getUpperY() - r1.getLowerY() + this.minGap;
                    if (logger.isDebugEnabled()) {
                        logger.debug("Adjust r1 DOWN by " + adjust);   
                    }
                    r1.setAllocatedY(r1.getAllocatedY() + adjust);   
                }
            }
            PieLabelRecord r2 = getPieLabelRecord(upper - 1);
            PieLabelRecord r3 = getPieLabelRecord(upper);
            if (logger.isDebugEnabled()) {
                logger.debug("r2.getAllocatedY() = " + r2.getAllocatedY());   
                logger.debug("r3.getAllocatedY() = " + r3.getAllocatedY());   
            }
            if (r2.getUpperY() > r3.getLowerY()) {
                double adjust = (r2.getUpperY() - r3.getLowerY()) + this.minGap;
                if (logger.isDebugEnabled()) {
                    logger.debug("Adjust r2 UP by " + adjust);   
                }
                r2.setAllocatedY(r2.getAllocatedY() - adjust);   
            }
                              
            lower++; 
            upper--;
        }
    }
    
    /**
     * Sorts the label records into ascending order by y-value.
     */
    public void sort() {
        Collections.sort(this.labels);  
    }
    
    /**
     * Returns a string containing a description of the object for debugging purposes.
     * 
     * @return a string.
     */
    public String toString() {
        String result = "";
        for (int i = 0; i < this.labels.size(); i++) {
            result = result + getPieLabelRecord(i).toString() + "\n";   
        }
        return result;
    }
    
}
