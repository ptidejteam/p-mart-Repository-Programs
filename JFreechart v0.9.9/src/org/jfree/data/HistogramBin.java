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
 * -----------------
 * HistogramBin.java
 * -----------------
 * (C) Copyright 2003, by Jelai Wang and Contributors.
 *
 * Original Author:  Jelai Wang (jelaiw AT mindspring.com);
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: HistogramBin.java,v 1.1 2007/10/10 20:07:42 vauchers Exp $
 *
 * Changes
 * -------
 * 06-Jul-2003 : Version 1, contributed by Jelai Wang (DG);
 * 07-Jul-2003 : Changed package and added Javadocs (DG);
 * 
 */

package org.jfree.data;

/**
 * A bin for the {@link HistogramDataset} class.
 *
 * @author Jelai Wang, jelaiw AT mindspring.com
 */
class HistogramBin {
	
    /** The number of items in the bin. */
    private int count = 0;
	
    /** The start boundary. */
    private double startBoundary;
    
    /** The end boundary. */
    private double endBoundary;

    /**
     * Creates a new bin.
     * 
     * @param startBoundary  the start boundary.
     * @param endBoundary  the end boundary.
     */
	HistogramBin(double startBoundary, double endBoundary) {
		if (startBoundary > endBoundary) {
			throw new IllegalArgumentException(
                "HistogramBin(...):  startBoundary > endBoundary."
            );
        }
		this.startBoundary = startBoundary;
		this.endBoundary = endBoundary;
	}

    /**
     * Returns the number of items in the bin.
     * 
     * @return The item count.
     */
	public int getCount() {
		return count;
	}
    
    /**
     * Increments the item count.
     */
	public void incrementCount() {
		count++;
	}
    
    /**
     * Returns the start boundary.
     * 
     * @return The start boundary.
     */
	public double getStartBoundary() {
		return startBoundary;
	}
    
  	/**
     * Returns the end boundary.
     * 
  	 * @return The end boundary.
  	 */
    public double getEndBoundary() {
		return endBoundary;
	}
    
    /**
     * Returns the bin width.
     * 
     * @return The bin width.
     */
	public double getBinWidth() {
		return endBoundary - startBoundary;
	}
    
}
