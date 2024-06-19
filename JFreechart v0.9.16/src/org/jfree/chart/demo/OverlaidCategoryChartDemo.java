/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ------------------------------
 * OverlaidCategoryChartDemo.java
 * ------------------------------
 * (C) Copyright 2002-2004, by Jeremy Bowman and Contributors.
 *
 * Original Author:  Jeremy Bowman.
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: OverlaidCategoryChartDemo.java,v 1.1 2007/10/10 19:25:26 vauchers Exp $
 *
 * Changes
 * -------
 * 13-May-2002 : Version 1 (JB);
 * 25-Jun-2002 : Removed unnecessary imports (DG);
 * 11-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package org.jfree.chart.demo;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.renderer.IntervalBarRenderer;
import org.jfree.chart.renderer.LineAndShapeRenderer;
import org.jfree.data.CategoryDataset;
import org.jfree.data.DatasetUtilities;
import org.jfree.data.DefaultIntervalCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * An overlaid category chart.
 *
 * @author Jeremy Bowman
 */
public class OverlaidCategoryChartDemo extends ApplicationFrame {

    /** The categories. */
    //private static final String[] CATEGORIES = { "1", "3", "5", "10", "20" };

    /** The bar colors. */
    private static Color[] barColors = null;

    /** The dot colors. */
    private static Color[] dotColors = null;

    /** The line colors. */
    private static Color[] lineColors = null;

    /** The label font. */
    private static Font labelFont = null;

    /** The bold label font. */
    //private static Font boldLabelFont = null;

    /** The title font. */
    private static Font titleFont = null;

    /** The chart. */
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
        //boldLabelFont = new Font("Helvetica", Font.BOLD, 10);
        titleFont = new Font("Helvetica", Font.BOLD, 14);
    }

    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public OverlaidCategoryChartDemo(String title) {

        super(title);
        DefaultIntervalCategoryDataset barData = null;
        double[][] lows = {{-.0315, .0159, .0306, .0453, .0557}};
        double[][] highs = {{.1931, .1457, .1310, .1163, .1059}};
        barData = new DefaultIntervalCategoryDataset(lows, highs);

        double[][] vals = {{0.0808, 0.0808, 0.0808, 0.0808, 0.0808}};
        CategoryDataset dotData = DatasetUtilities.createCategoryDataset(
            "Series ", 
            "Category ",
            vals
        );

        double[][] lineVals = new double[4][5];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                lineVals[i][j] = (Math.random() * 0.56) - 0.18;
            }
        }
        CategoryDataset lineData = DatasetUtilities.createCategoryDataset("Series ", "Category ",
                                                                          lineVals);

        String ctitle = "Strategie Sicherheit";
        String xTitle = "Zeitraum (in Jahren)";
        String yTitle = "Performance";
        CategoryAxis xAxis = new CategoryAxis(xTitle);
        xAxis.setLabelFont(titleFont);
        xAxis.setTickLabelFont(labelFont);
        xAxis.setTickMarksVisible(false);
        NumberAxis yAxis = new NumberAxis(yTitle);
        yAxis.setLabelFont(titleFont);
        yAxis.setTickLabelFont(labelFont);
        yAxis.setRange(-0.2, 0.4);
        DecimalFormat formatter = new DecimalFormat("0.##%");
        yAxis.setTickUnit(new NumberTickUnit(0.05, formatter));

        IntervalBarRenderer barRenderer = new IntervalBarRenderer();
        barRenderer.setItemLabelsVisible(Boolean.TRUE);

        CategoryPlot plot = new CategoryPlot(barData, xAxis, yAxis, barRenderer);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
        
        plot.setBackgroundPaint(Color.lightGray);
        plot.setOutlinePaint(Color.black);

        LineAndShapeRenderer dotRenderer = new LineAndShapeRenderer(LineAndShapeRenderer.SHAPES);
        dotRenderer.setItemLabelsVisible(Boolean.TRUE);
        
        plot.setSecondaryDataset(0, dotData);
        plot.setSecondaryRenderer(0, dotRenderer);

        LineAndShapeRenderer lineRenderer 
            = new LineAndShapeRenderer(LineAndShapeRenderer.SHAPES_AND_LINES);
        plot.setSecondaryDataset(1, lineData);
        plot.setSecondaryRenderer(1, lineRenderer);

        chart = new JFreeChart(ctitle, titleFont, plot, false);
        chart.setBackgroundPaint(Color.white);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }

    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    * 
    // * support us so that we can continue developing free software.             *                                             *
    // ****************************************************************************
    
    /**
     * Starting point for the demo.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        OverlaidCategoryChartDemo demo
             = new OverlaidCategoryChartDemo("Overlaid Category Chart Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
