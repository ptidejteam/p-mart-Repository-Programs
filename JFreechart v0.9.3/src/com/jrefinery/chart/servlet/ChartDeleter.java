/* ============================================
 * JFreeChart : a free Java chart class library
 * ============================================
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
 * -------------------
 * ChartDeleter.java
 * -------------------
  * (C) Copyright 2002, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson (richard_c_atkinson@ntlworld.com);
 * Contributor(s):   -;
 *
 * $Id: ChartDeleter.java,v 1.1 2007/10/10 19:52:24 vauchers Exp $
 *
 * Changes
 * -------
 * 19-Aug-2002 : Version 1;
 *
 */
package com.jrefinery.chart.servlet;

import javax.servlet.http.*;
import java.util.*;
import java.io.*;

/**
 * Used for deleting charts from the temporary directory when the users session
 * expires.
 */
public class ChartDeleter implements HttpSessionBindingListener {
	protected ArrayList chartNames = new ArrayList();

	/* Blank constructor */
    public ChartDeleter() {
    }

	/**
	 * Add a chart to be deleted when the session expires
	 * @param filename The name of the chart in the temporary directory to be deleted
	 */
	public void addChart(String filename) {
		chartNames.add(filename);
	}

	/**
	 * Checks to see if a chart is in the list of charts to be deleted
	 * @param filename The name of the chart in the temporary directory
	 * @return A boolean value indicating whether the chart is present in the list
	 */
	public boolean isChartAvailable(String filename) {
		return (this.chartNames.contains(filename));
	}

	/**
	 * Binding this object to the session has no additional effects
	 * @param event The session bind event
	 */
	public void valueBound(HttpSessionBindingEvent event) {
		return;
	}

	/**
	 * When this object is unbound from the session (including upon session
	 * expiry) the files that have been added to the ArrayList are iterated
	 * and deleted.
	 * @param event The session unbind event
	 */
	public void valueUnbound(HttpSessionBindingEvent event) {
		Iterator iter = this.chartNames.listIterator();
		while (iter.hasNext()) {
			String filename = (String)iter.next();
			File file = new File(System.getProperty("java.io.tmpdir"),filename);
			if (file.exists()) file.delete();
		}
		return;
	}

}
