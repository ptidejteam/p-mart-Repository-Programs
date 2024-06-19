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
 * ---------------
 * LegendItem.java
 * ---------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: LegendItemCollection.java,v 1.1 2007/10/10 18:56:25 vauchers Exp $
 *
 * Changes
 * -------
 * 07-Feb-2002 : Version 1. INCOMPLETE, PLEASE IGNORE. (DG);
 *
 */

package com.jrefinery.chart;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Graphics2D;

/**
 * A collection of legend items.
 */
public class LegendItemCollection {

    /** Storage for the legend items. */
    protected List items;

    /**
     * Constructs a new legend item collection, initially empty.
     */
    public LegendItemCollection() {
        items = new ArrayList();
    }

    /**
     * Adds a legend item to the collection.
     */
    public void add(LegendItem item) {
        items.add(item);
    }

    /**
     * Returns an iterator that provides access to all the legend items.
     */
    public Iterator iterator() {
        return items.iterator();
    }

    /**
     * Arranges the legend items according to a specific layout.
     */
    public void layoutLegendItems(LegendItemLayout layout) {

        layout.layoutLegendItems(this);

    }

    /**
     * Draws the legend item collection at the specified location.
     * @param g2 The graphics device.
     * @param x The x location.
     * @param y The y location.
     */
    public void draw(Graphics2D g2, double x, double y) {

        Iterator iterator = items.iterator();
        while (iterator.hasNext()) {
            LegendItem item = (LegendItem)iterator.next();

        }

    }

}