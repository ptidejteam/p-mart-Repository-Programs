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
 * --------------------------
 * OutlierListCollection.java
 * --------------------------
 * (C) Copyright 2003, by David Browning and Contributors.
 *
 * Original Author:  David Browning;
 * Contributor(s):   -;
 *
 * $Id: OutlierListCollection.java,v 1.1 2007/10/10 19:09:11 vauchers Exp $
 *
 * Changes
 * -------
 * 05-Aug-2003 : Version 1, contributed by David Browning (DG);
 *
 */
 
package org.jfree.chart.renderer;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A collection of outlier lists in the box and whisker plot.
 *
 * Outliers are grouped in lists for each entity. Lists contain
 * one or more outliers, determined by whether overlaps have
 * occured. Overlapping outliers are grouped in the same list.
 *
 * @see org.jfree.chart.renderer.OutlierList
 *
 * @author David Browning
 */
public class OutlierListCollection extends ArrayList {

    /* Unbelievably, outliers which are more than 2 * interquartile range are
     * called far outs...  See Tukey EDA  (a classic one of a kind...)
     */
    private boolean farOut = false;

    /**
     * Appends the specified element as a new <code>OutlierList</code> to the
     * end of this list if it does not overlap an outlier in an existing list.
     *
     * If it does overlap, it is appended to the outlier list which it overlaps
     * and that list is updated.
     *
     * @param o element to be appended to this list.
     * @return <tt>true</tt> (as per the general contract of Collection.add).
     */
    public boolean add(Object o) {
        if (o instanceof Outlier) {
            Outlier outlier = (Outlier)o;
            if (this.isEmpty()) {
                //System.err.print(" making first outlier list");
                return super.add(new OutlierList(outlier));
            } else {
                boolean updated = false;
                for (Iterator iterator = this.iterator(); iterator.hasNext();) {
                    OutlierList list = (OutlierList) iterator.next();
                    if (list.isOverlapped(outlier)) {
                        updated = updateOutlierList(list, outlier);
                    }
                }
                if (!updated) {
                    //System.err.print(" creating new outlier list ");
                    updated = super.add(new OutlierList(outlier));
                }
                return updated;
            }
        } 
        else {
            new NotOutlierException("Not an outlier!");
        }
        return false;
    }

    /* Updates the outlier list by adding the outlier to the end of the list and
     * setting the averaged outlier to the average x and y coordinnate values of the
     * outliers in the list.
     *
     * @param list the outlier list to be updated.
     * @param outlier the outlier to be added
     *
     * @return <tt>true</tt> (as per the general contract of Collection.add).
     */
    private boolean updateOutlierList(OutlierList list, Outlier outlier) {
        boolean result = false;
        result = list.add(outlier);
        list.updateAveragedOutlier();
        list.setMultiple(true);
        //System.err.println(" added to list");
        return result;
    }

    /*
     * A flag to indicate whether this collection contains a far out
     *
     * @return a <code>boolean</code> representing the flag indicating whether this collection includes a far out.
     */
    protected boolean isFarOut() {
        return farOut;
    }

    /*
     * Allows the far out flag to be set.
     *
     * @param a <code>boolean</code> representing the value to set the far out flag to
     */
    protected void setFarOut(boolean farOut) {
        this.farOut = farOut;
    }
}
