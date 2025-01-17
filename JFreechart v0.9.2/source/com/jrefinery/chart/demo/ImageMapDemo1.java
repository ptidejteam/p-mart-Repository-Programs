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
 * ------------------
 * ImageMapDemo1.java
 * ------------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: ImageMapDemo1.java,v 1.1 2007/10/10 19:41:58 vauchers Exp $
 *
 * Changes
 * -------
 * 26-Jun-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.demo;

import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartUtilities;
import com.jrefinery.chart.ChartRenderingInfo;
import com.jrefinery.chart.entity.StandardEntityCollection;
import com.jrefinery.data.DefaultCategoryDataset;
import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.PrintWriter;
import java.io.IOException;

public class ImageMapDemo1 {

    public ImageMapDemo1() {
    }

    public static void main(String[] args) {

        // create a chart
        double[][] data = new double[][] {
            { 56.0, -12.0, 34.0, 76.0, 56.0, 100.0, 67.0, 45.0 },
            { 37.0, 45.0, 67.0, 25.0, 34.0, 34.0, 100.0, 53.0 },
            { 43.0, 54.0, 34.0, 34.0, 87.0, 64.0, 73.0, 12.0 }
        };
        DefaultCategoryDataset dataset = new DefaultCategoryDataset(data);
        JFreeChart chart = ChartFactory.createVerticalBarChart(
                                                     "Vertical Bar Chart",  // chart title
                                                     "Category",            // domain axis label
                                                     "Value",               // range axis label
                                                     dataset,               // data
                                                     true                   // include legend
                                                 );

        // save it to an image
        try {
            ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
            File file1 = new File("barchart100.png");
            ChartUtilities.saveChartAsPNG(file1, chart, 600, 400, info);

            // write an HTML page incorporating the image with an image map
            File file2 = new File("barchart100.html");
            OutputStream out = new BufferedOutputStream(new FileOutputStream(file2));
            PrintWriter writer = new PrintWriter(out);
            writer.println("<HTML>");
            writer.println("<HEAD><TITLE>JFreeChart Image Map Demo</TITLE></HEAD>");
            writer.println("<BODY>");
            ChartUtilities.writeImageMap(writer, "chart", "CHART1-", info);
            writer.println("<IMG SRC=\"barchart100.png\" WIDTH=\"600\" HEIGHT=\"400\" BORDER=\"0\""+
                           " USEMAP=\"#chart\">");
            writer.println("</BODY>");
            writer.println("</HTML>");
            writer.close();

        }
        catch (IOException e) {
            System.out.println(e.toString());
        }

    }

}