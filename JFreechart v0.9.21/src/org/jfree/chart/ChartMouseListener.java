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
 * -----------------------
 * ChartMouseListener.java
 * -----------------------
 * (C) Copyright 2002-2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Alex Weber;
 *
 * $Id: ChartMouseListener.java,v 1.1 2007/10/10 19:50:25 vauchers Exp $
 *
 * Changes
 * -------
 * 27-May-2002 : Version 1, incorporating code and ideas by Alex Weber (DG);
 * 13-Jun-2002 : Added Javadocs (DG);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package org.jfree.chart;

/**
 * The interface that must be implemented by classes that wish to receive {@link ChartMouseEvent}
 * notifications from a {@link ChartPanel}.
 */
public interface ChartMouseListener {

    /**
     * Callback method for receiving notification of a mouse click on a chart.
     *
     * @param event  information about the event.
     */
    void chartMouseClicked(ChartMouseEvent event);

    /**
     * Callback method for receiving notification of a mouse movement on a chart.
     *
     * @param event  information about the event.
     */
    void chartMouseMoved(ChartMouseEvent event);

}
