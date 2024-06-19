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
 * ------------------------------
 * OverlaidCategoryChartDemo.java
 * ------------------------------
 * (C) Copyright 2002, by Jeremy Bowman.
 *
 * Original Author:  Jeremy Bowman.
 * Contributor(s):   -;
 *
 * $Id: OverlaidCategoryChartDemo.java,v 1.1 2007/10/10 19:01:20 vauchers Exp $
 *
 * Changes
 * -------
 * 13-May-2002 : Version 1 (JB);
 *
 */

package com.jrefinery.chart.demo;

import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.ChartUtilities;
import com.jrefinery.chart.HorizontalCategoryAxis;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.LineAndShapeRenderer;
import com.jrefinery.chart.NumberTickUnit;
import com.jrefinery.chart.OverlaidVerticalCategoryPlot;
import com.jrefinery.chart.SeriesShapeFactory;
import com.jrefinery.chart.VerticalCategoryPlot;
import com.jrefinery.chart.VerticalIntervalBarRenderer;
import com.jrefinery.chart.VerticalNumberAxis;
import com.jrefinery.data.DefaultCategoryDataset;
import com.jrefinery.data.DefaultIntervalCategoryDataset;
import com.jrefinery.ui.ApplicationFrame;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

public class OverlaidCategoryChartDemo extends ApplicationFrame {

    private static final String[] categories = { "1", "3", "5", "10", "20" };
    private static Color[] barColors = null;
    private static Color[] dotColors = null;
    private static Color[] lineColors = null;
    private static Font labelFont = null;
    private static Font boldLabelFont = null;
    private static Font titleFont = null;
    private JFreeChart chart = null;

    static {
        barColors = new Color[1];
        barColors[0] = new Color(51, 102, 153);
        dotColors = new Color[1];
        dotColors[0] = Color.white;
        lineColors = new Color[4];
        lineColors[0] = Color.red;
        lineColors[1] = Color.blue;
        lineColors[2] = Color.yellow;
        lineColors[3] = Color.magenta;
        labelFont = new Font("Helvetica", Font.PLAIN, 10);
        boldLabelFont = new Font("Helvetica", Font.BOLD, 10);
        titleFont = new Font("Helvetica", Font.BOLD, 14);
    }

    public OverlaidCategoryChartDemo(String title) {

        super(title);
        DefaultIntervalCategoryDataset barData = null;
        double[][] lows = { { -.0315, .0159, .0306, .0453, .0557 } };
        double[][] highs = { { .1931, .1457, .1310, .1163, .1059 } };
        barData = new DefaultIntervalCategoryDataset(lows, highs);

        DefaultCategoryDataset dotData = null;
        double[][] vals = { { 0.0808, 0.0808, 0.0808, 0.0808, 0.0808 } };
        dotData = new DefaultCategoryDataset(vals);
        DefaultCategoryDataset lineData = null;
        double[][] lineVals = new double[4][5];
        for (int i=0; i < 4; i++) {
            for (int j=0; j < 5; j++) {
                lineVals[i][j] = (Math.random() * 0.56) - 0.18;
            }
        }
        lineData = new DefaultCategoryDataset(lineVals);

        String ctitle = "Strategie Sicherheit";
        String xTitle = "Zeitraum (in Jahren)";
        String yTitle = "Performance";
        HorizontalCategoryAxis xAxis = new HorizontalCategoryAxis(xTitle);
        xAxis.setLabelFont(titleFont);
        xAxis.setTickLabelFont(labelFont);
        xAxis.setTickMarksVisible(false);
        VerticalNumberAxis yAxis = new VerticalNumberAxis(yTitle);
        yAxis.setLabelFont(titleFont);
        yAxis.setTickLabelFont(labelFont);
        yAxis.setMinimumAxisValue(-0.2);
        yAxis.setMaximumAxisValue(0.4);
        DecimalFormat formatter = new DecimalFormat("0.##%");
        yAxis.setTickUnit(new NumberTickUnit(0.05, formatter));

        OverlaidVerticalCategoryPlot plot = new OverlaidVerticalCategoryPlot(xAxis, yAxis, categories);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setOutlinePaint(Color.black);

        VerticalIntervalBarRenderer barRenderer = null;
        barRenderer = new VerticalIntervalBarRenderer();
        VerticalCategoryPlot bars = null;
        bars = new VerticalCategoryPlot(barData, null, null, barRenderer);
        bars.setLabelsVisible(true);
        bars.setLabelFont(labelFont);
        bars.setLabelFormatString("0.##%");
        bars.setSeriesPaint(barColors);
        plot.add(bars);

        LineAndShapeRenderer dotRenderer = null;
        dotRenderer = new LineAndShapeRenderer(LineAndShapeRenderer.SHAPES,
                                               LineAndShapeRenderer.RIGHT);
        VerticalCategoryPlot dots = null;
        dots = new VerticalCategoryPlot(dotData, null, null, dotRenderer);
        dots.setLabelsVisible(true);
        dots.setLabelFont(boldLabelFont);
        dots.setLabelPaint(Color.white);
        dots.setLabelFormatString("0.##%");
        dots.setSeriesPaint(dotColors);
        dots.setShapeFactory(new SeriesShapeFactory());
        plot.add(dots);

        LineAndShapeRenderer lineRenderer = null;
        lineRenderer = new LineAndShapeRenderer(LineAndShapeRenderer.SHAPES_AND_LINES);
        VerticalCategoryPlot lines = null;
        lines = new VerticalCategoryPlot(lineData, null, null, lineRenderer);
        lines.setSeriesPaint(lineColors);
        lines.setShapeFactory(new SeriesShapeFactory());
        plot.add(lines);

        chart = new JFreeChart(ctitle, titleFont, plot, false);
        chart.setBackgroundPaint(Color.white);

        ChartPanel chartPanel = new ChartPanel(chart);
        this.setContentPane(chartPanel);

    } // ctor

    /**
     * For testing from the command line
     */
    public static void main(String[] args) {

        OverlaidCategoryChartDemo demo = new OverlaidCategoryChartDemo("Overlaid Category Chart Demo");
        demo.pack();
        demo.setVisible(true);

    }

}
