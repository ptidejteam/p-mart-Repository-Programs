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
 * ----------------
 * OutlierList.java
 * ----------------
 * (C) Copyright 2003, by David Browning and Contributors.
 *
 * Original Author:  David Browning;
 * Contributor(s):   -;
 *
 * $Id: OutlierList.java,v 1.1 2007/10/10 19:09:11 vauchers Exp $
 *
 * Changes
 * -------
 * 05-Aug-2003 : Version 1, contributed by David Browning (DG);
 *
 */
package org.jfree.chart.renderer;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A collection of outliers for a single entity in a box and whisker plot.
 *
 * Outliers are grouped in lists for each entity. Lists contain
 * one or more outliers, determined by whether overlaps have
 * occured. Overlapping outliers are grouped in the same list.
 *
 * Each list contains an averaged outlier, which is the same as a single
 * outlier if there is only one outlier in the list, but the average of
 * all the outliers in the list if there is more than one.
 *
 * NB This is simply my scheme for displaying outliers, and might not be
 * acceptable by the wider community.
 *
 * @author David Browning
 */
public class OutlierList extends ArrayList {

    private Outlier averagedOutlier;
    private boolean multiple = false;

    public OutlierList(Outlier outlier) {
        this.setAveragedOutlier(outlier);
    }

    public Outlier getAveragedOutlier() {
        return averagedOutlier;
    }

    public void setAveragedOutlier(Outlier averagedOutlier) {
        this.averagedOutlier = averagedOutlier;
    }

    public boolean isOverlapped(Object o) {

        if (o != null && o instanceof Outlier ) {
            Outlier aO =  this.getAveragedOutlier();
            boolean result = ((Outlier)o).overlaps(aO);
            //System.err.print(" checking overlap...  \n[" + (aO.getX() + aO.getRadius())
            //                                              + "-" +
            //                                             (aO.getX() - aO.getRadius())
            //                                              + ","  +
            //                                              (aO.getY() + aO.getRadius())
            //                                              + "-"  +
            //                                              (aO.getY() - aO.getRadius())
            //                                              + "]\n" + result + " --> ");
            return result;
        } 
        else {
            new NotOutlierException("Not an outlier!");
        }
        return false;
    }

    public void updateAveragedOutlier() {
        double totalXCoords = 0.0;
        double totalYCoords = 0.0;
        int size = this.size();
        for (Iterator iterator = this.iterator(); iterator.hasNext();) {
            Outlier o = (Outlier) iterator.next();
            totalXCoords += o.getX();
            totalYCoords += o.getY();
        }
        this.getAveragedOutlier().getPoint().setLocation(new Point2D.Double(totalXCoords/size, totalYCoords/size));
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

}
