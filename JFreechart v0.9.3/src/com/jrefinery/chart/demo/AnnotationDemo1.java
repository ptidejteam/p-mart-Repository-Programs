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
 * --------------------
 * AnnotationDemo1.java
 * --------------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: AnnotationDemo1.java,v 1.1 2007/10/10 19:52:20 vauchers Exp $
 *
 * Changes
 * -------
 * 29-Aug-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.Paint;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.jrefinery.data.XYSeries;
import com.jrefinery.data.XYSeriesCollection;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.XYPlot;
import com.jrefinery.chart.ValueAxis;
import com.jrefinery.chart.NumberAxis;
import com.jrefinery.chart.TickUnits;
import com.jrefinery.chart.TextTitle;
import com.jrefinery.chart.annotations.XYTextAnnotation;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.ui.RefineryUtilities;

public class AnnotationDemo1 extends ApplicationFrame {

    /**
     * A demonstration application showing....
     */
    public AnnotationDemo1(String title) {

        super(title);
        XYSeriesCollection data = createDataset();
        JFreeChart chart = ChartFactory.createXYChart(null, "Age in Months", "kg", data, true);
        TextTitle t1 = new TextTitle("Growth Charts: United States",
                                     new Font("SansSerif", Font.BOLD, 14));
        TextTitle t2 = new TextTitle("Weight-for-age percentiles: boys, birth to 36 months",
                                     new Font("SansSerif", Font.PLAIN, 11));
        chart.addTitle(t1);
        chart.addTitle(t2);
        XYPlot plot = chart.getXYPlot();
        plot.setSeriesPaint(new Paint[] {Color.gray, Color.yellow, Color.green, Color.blue,
                                         Color.red,
                                         Color.blue, Color.green, Color.yellow, Color.gray});
        NumberAxis domainAxis = (NumberAxis)plot.getDomainAxis();
        domainAxis.setUpperMargin(0.12);
        domainAxis.setStandardTickUnits(TickUnits.createIntegerTickUnits());
        NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        rangeAxis.setAutoRangeIncludesZero(false);
        Font f = new Font("SansSerif", Font.PLAIN, 9);
        plot.addAnnotation(new XYTextAnnotation("3rd", f, 36.5, 11.5));
        plot.addAnnotation(new XYTextAnnotation("5th", f, 36.5, 11.95));
        plot.addAnnotation(new XYTextAnnotation("10th", f, 36.5, 12.25));
        plot.addAnnotation(new XYTextAnnotation("25th", f, 36.5, 13.1));
        plot.addAnnotation(new XYTextAnnotation("50th", f, 36.5, 14.1));
        plot.addAnnotation(new XYTextAnnotation("75th", f, 36.5, 15.3));
        plot.addAnnotation(new XYTextAnnotation("90th", f, 36.5, 16.75));
        plot.addAnnotation(new XYTextAnnotation("95th", f, 36.5, 17.25));
        plot.addAnnotation(new XYTextAnnotation("97th", f, 36.5, 17.75));
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(360, 500));
        this.setContentPane(chartPanel);

    }

    private XYSeriesCollection createDataset() {

        XYSeriesCollection result = new XYSeriesCollection();

        try {
            BufferedReader in = new BufferedReader(new FileReader("/home/dgilbert/wtageinf.txt"));
            String ss = in.readLine();  // ignore first line
            ss = in.readLine();  // ignore second line
            ss = in.readLine();  // ignore third line
            ss = in.readLine();  // headings

            XYSeries s3 = new XYSeries("P3", false);
            XYSeries s5 = new XYSeries("P5", false);
            XYSeries s10 = new XYSeries("P10", false);
            XYSeries s25 = new XYSeries("P25", false);
            XYSeries s50 = new XYSeries("P50", false);
            XYSeries s75 = new XYSeries("P75", false);
            XYSeries s90 = new XYSeries("P90", false);
            XYSeries s95 = new XYSeries("P95", false);
            XYSeries s97 = new XYSeries("P97", false);

            String data = in.readLine();
            while (data!=null) {
                int sex = Integer.parseInt(data.substring(1, 8).trim());
                float age = Float.parseFloat(data.substring(9, 17).trim());
                String l = data.substring(18, 32).trim();
                String m = data.substring(33, 50).trim();
                String s = data.substring(51, 68).trim();
                float p3 = Float.parseFloat(data.substring(69, 86).trim());
                float p5 = Float.parseFloat(data.substring(87, 103).trim());
                float p10 = Float.parseFloat(data.substring(104, 122).trim());
                float p25 = Float.parseFloat(data.substring(123, 140).trim());
                float p50 = Float.parseFloat(data.substring(141, 158).trim());
                float p75 = Float.parseFloat(data.substring(159, 176).trim());
                float p90 = Float.parseFloat(data.substring(177, 193).trim());
                float p95 = Float.parseFloat(data.substring(194, 212).trim());
                float p97 = Float.parseFloat(data.substring(212, data.length()).trim());
                if (sex==1) {
                    s3.add(age, p3);
                    s5.add(age, p5);
                    s10.add(age, p10);
                    s25.add(age, p25);
                    s50.add(age, p50);
                    s75.add(age, p75);
                    s90.add(age, p90);
                    s95.add(age, p95);
                    s97.add(age, p97);
                }
                data = in.readLine();
            }

            result.addSeries(s3);
            result.addSeries(s5);
            result.addSeries(s10);
            result.addSeries(s25);
            result.addSeries(s50);
            result.addSeries(s75);
            result.addSeries(s90);
            result.addSeries(s95);
            result.addSeries(s97);
        }
        catch (FileNotFoundException e) {
            System.err.println(e);
        }
        catch (IOException e) {
            System.err.println(e);
        }

        return result;

    }

    /**
     * Starting point for the demonstration application.
     */
    public static void main(String[] args) {

        AnnotationDemo1 demo = new AnnotationDemo1("Annotation Demo 1");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
