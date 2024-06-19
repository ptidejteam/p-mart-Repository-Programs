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
 * --------------------
 * JFreeChartFrame.java
 * --------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: JFreeChartFrame.java,v 1.1 2007/10/10 18:56:25 vauchers Exp $
 *
 * Changes
 * -------
 * 22-Nov-2001 : Version 1 (DG);
 * 08-Jan-2001 : Added chartPanel attribute (DG);
 */

package com.jrefinery.chart;

import java.awt.event.*;
import javax.swing.*;

/**
 * A frame for displaying a chart.
 */
public class JFreeChartFrame extends JFrame {

    /** The chart panel. */
    protected JFreeChartPanel chartPanel;

    /**
     * Constructs a frame for a chart.
     * @param title The frame title.
     * @param chart The chart.
     */
    public JFreeChartFrame(String title, JFreeChart chart) {
        this(title, chart, false);
    }

    /**
     * Constructs a frame for a chart.
     * @param title The frame title.
     * @param chart The chart.
     */
    public JFreeChartFrame(String title, JFreeChart chart, boolean scrollPane) {

        super(title);
        chartPanel = new JFreeChartPanel(chart);
        if (scrollPane) {
            this.setContentPane(new JScrollPane(chartPanel));
        }
        else this.setContentPane(chartPanel);

    }

    /**
     * Returns the chart panel for the frame.
     * @return The chart panel for the frame.
     */
    public JFreeChartPanel getChartPanel() {
        return this.chartPanel;
    }

}