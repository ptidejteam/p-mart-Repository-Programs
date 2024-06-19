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
 * ---------------
 * ChartFrame.java
 * ---------------
 * (C) Copyright 2001, 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: ChartFrame.java,v 1.1 2007/10/10 19:02:26 vauchers Exp $
 *
 * Changes
 * -------
 * 22-Nov-2001 : Version 1 (DG);
 * 08-Jan-2001 : Added chartPanel attribute (DG);
 * 24-May-2002 : Renamed JFreeChartFrame --> ChartFrame (DG);
 *
 */

package com.jrefinery.chart;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

/**
 * A frame for displaying a chart.
 */
public class ChartFrame extends JFrame {

    /** The chart panel. */
    protected ChartPanel chartPanel;

    /**
     * Constructs a frame for a chart.
     *
     * @param title The frame title.
     * @param chart The chart.
     */
    public ChartFrame(String title, JFreeChart chart) {
        this(title, chart, false);
    }

    /**
     * Constructs a frame for a chart.
     *
     * @param title The frame title.
     * @param chart The chart.
     */
    public ChartFrame(String title, JFreeChart chart, boolean scrollPane) {

        super(title);

        chartPanel = new ChartPanel(chart);
        if (scrollPane) {
            this.setContentPane(new JScrollPane(chartPanel));
        }
        else this.setContentPane(chartPanel);

    }

    /**
     * Returns the chart panel for the frame.
     *
     * @return The chart panel.
     */
    public ChartPanel getChartPanel() {
        return this.chartPanel;
    }

}