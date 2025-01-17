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
 * ChartDeleter.java
 * -----------------
  * (C) Copyright 2002, 2003, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson (richard_c_atkinson@ntlworld.com);
 * Contributor(s):   -;
 *
 * $Id: ChartDeleter.java,v 1.1 2007/10/10 20:07:44 vauchers Exp $
 *
 * Changes
 * -------
 * 19-Aug-2002 : Version 1;
 * 17-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */
package org.jfree.chart.servlet;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

/**
 * Used for deleting charts from the temporary directory when the users session
 * expires.
 *
 * @author Richard Atkinson
 */
public class ChartDeleter implements HttpSessionBindingListener {

    /** The chart names. */
    private List chartNames = new java.util.ArrayList();

    /**
     * Blank constructor.
     */
    public ChartDeleter() {
    }

    /**
     * Add a chart to be deleted when the session expires
     *
     * @param filename  the name of the chart in the temporary directory to be deleted.
     */
    public void addChart(String filename) {
        chartNames.add(filename);
    }

    /**
     * Checks to see if a chart is in the list of charts to be deleted
     *
     * @param filename  the name of the chart in the temporary directory.
     *
     * @return a boolean value indicating whether the chart is present in the list.
     */
    public boolean isChartAvailable(String filename) {
        return (this.chartNames.contains(filename));
    }

    /**
     * Binding this object to the session has no additional effects.
     *
     * @param event  the session bind event.
     */
    public void valueBound(HttpSessionBindingEvent event) {
        return;
    }

    /**
     * When this object is unbound from the session (including upon session
     * expiry) the files that have been added to the ArrayList are iterated
     * and deleted.
     *
     * @param event  the session unbind event.
     */
    public void valueUnbound(HttpSessionBindingEvent event) {

        Iterator iter = this.chartNames.listIterator();
        while (iter.hasNext()) {
            String filename = (String) iter.next();
            File file = new File(System.getProperty("java.io.tmpdir"), filename);
            if (file.exists()) {
                file.delete();
            }
        }
        return;

    }

}
