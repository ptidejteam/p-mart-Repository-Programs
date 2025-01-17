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
 * ------------------
 * CombinedChart.java
 * ------------------
 * (C) Copyright 2001, 2002, by Bill Kelemen.
 *
 * Original Author:  Bill Kelemen;
 * Contributor(s):   -;
 *
 * $Id: CombinedChart.java,v 1.1 2007/10/10 19:02:39 vauchers Exp $
 *
 * Changes:
 * --------
 * 06-Dec-2001 : Version 1 (BK);
 * 08-Jan-2002 : Moved to new package com.jrefinery.chart.combination (DG);
 * 25-Feb-2002 : Updated import statements (DG);
 *
 */

package com.jrefinery.chart.combination;

import com.jrefinery.data.Dataset;
import com.jrefinery.data.DatasetChangeEvent;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.Plot;
import com.jrefinery.chart.event.ChartChangeEvent;
import com.jrefinery.chart.event.PlotChangeEvent;
import com.jrefinery.chart.event.TitleChangeEvent;
import com.jrefinery.chart.event.LegendChangeEvent;

/**
 * A CombinedChart can be added to a CombinedPlot.  Many of the facilities inherited from
 * JFreeChart aren't needed, so the event handling methods are overidden with empty methods.
 *
 * @see CombinedPlot
 * @author Bill Kelemen (bill@kelemen-usa.com)
 */
public class CombinedChart extends JFreeChart {

    /**
     * Constructs a CombinedChart for displaying a dataset and a plot.
     *
     * @param data The data to be represented in the chart.
     * @param plot Controller of the visual representation of the data.
     */
    public CombinedChart(Dataset data, Plot plot) {

        super(data,
              plot,
              null, // title
              null, // title font
              false // create legend
              );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Event handling - let top level JFreeChart takes care of events
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Does nothing.
     * @param event Information about the event that triggered the notification.
     */
    protected void notifyListeners(ChartChangeEvent event) {
    }

    /**
     * Does nothing.
     * @param event Information about the event (not used here).
     */
    public void datasetChanged(DatasetChangeEvent event) {
    }

    /**
     * Does nothing.
     * @param event Information about the chart title change.
     */
    public void titleChanged(TitleChangeEvent event) {
    }

    /**
     * Does nothing.
     * @param event Information about the chart legend change.
     */
    public void legendChanged(LegendChangeEvent event) {
    }

    /**
     * Does nothing.
     * @param event Information about the plot change.
     */
    public void plotChanged(PlotChangeEvent event) {
    }

}