/* ===============
 * JFreeChart Demo
 * ===============
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
 * ----------
 * First.java
 * ----------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited).
 * Contributor(s):   -;
 *
 * $Id: First.java,v 1.1 2007/10/10 19:52:20 vauchers Exp $
 *
 * Changes
 * -------
 * 16-Jan-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.demo;

import com.jrefinery.data.DefaultPieDataset;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFrame;

public class First {

    public static void main(String[] args) {

        // create a dataset...
        DefaultPieDataset data = new DefaultPieDataset();
        data.setValue("Category 1", new Double(43.2));
        data.setValue("Category 2", new Double(27.9));
        data.setValue("Category 3", new Double(79.5));

        // create a chart...
        JFreeChart chart = ChartFactory.createPieChart("Sample Pie Chart", data, true);

        // create and display a frame...
        ChartFrame frame = new ChartFrame("Test", chart);
        frame.pack();
        frame.setVisible(true);

    }

}
