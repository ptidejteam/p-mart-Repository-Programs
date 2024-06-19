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
 * --------------------
 * ChartMouseEvent.java
 * --------------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Alex Weber;
 *
 * $Id: ChartMouseListener.java,v 1.1 2007/10/10 19:52:14 vauchers Exp $
 *
 * Changes
 * -------
 * 27-May-2002 : Version 1, incorporating code and ideas by Alex Weber (DG);
 * 13-Jun-2002 : Added Javadocs (DG);
 *
 */

package com.jrefinery.chart;

/**
 * The interface for classes that listen for ChartMouseEvents.
 */
public interface ChartMouseListener {

	/**
	 * Callback method for receiving notification of a mouse click on a chart.
	 *
	 * @param event Information about the event.
	 */
	public void chartMouseClicked(ChartMouseEvent event);

	/**
	 * Callback method for receiving notification of a mouse movement on a
	 * chart.
	 *
	 * @param event     Information about the event.
	 */
	public void chartMouseMoved(ChartMouseEvent event);

}
