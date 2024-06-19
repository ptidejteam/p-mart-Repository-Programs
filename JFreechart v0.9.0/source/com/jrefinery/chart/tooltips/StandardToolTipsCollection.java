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
 * -------------------------------
 * StandardToolTipsCollection.java
 * -------------------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: StandardToolTipsCollection.java,v 1.1 2007/10/10 19:01:21 vauchers Exp $
 *
 * Changes
 * -------
 * 13-Dec-2001 : Version 1 (DG);
 * 16-Jan-2002 : Completed Javadocs (DG);
 *
 */

package com.jrefinery.chart.tooltips;

import java.awt.*;
import java.util.*;

/**
 * Standard implementation of the ToolTipManager interface.
 */
public class StandardToolTipsCollection implements ToolTipsCollection {

    /** Storage for the tooltips. */
    protected Collection tooltips;

    /**
     * Constructs a new tooltip collection (initially empty).
     */
    public StandardToolTipsCollection() {
        tooltips = new ArrayList();
    }

    /**
     * Clears the tooltips.
     */
    public void clearToolTips() {
        tooltips.clear();
    }

    /**
     * Adds a tooltip.
     * @param text The tooltip text.
     * @param area The area that the tooltip is relevant to.
     */
    public void addToolTip(String text, Shape area) {
        ToolTip tooltip = new ToolTip(text, area);
        tooltips.add(tooltip);
    }

    /**
     * Returns a tooltip for the specified coordinates.
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return The tooltip text.
     */
    public String getToolTipText(double x, double y) {

        String result = null;

        Iterator iterator = tooltips.iterator();
        while (iterator.hasNext()) {
            ToolTip tooltip = (ToolTip)(iterator.next());
            if (tooltip.getArea().contains(x, y)) {
                result = tooltip.getText();
            }
        }

        return result;
    }

}