/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
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
 * ContourPlotDemo.java
 * --------------------
 * (C) Copyright 2002, 2003, by David M. O'Donnell and Contributors.
 *
 * Original Author:  David M. O'Donnell;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: ContourPlotDemo.java,v 1.1 2007/10/10 19:54:11 vauchers Exp $
 *
 * Changes
 * -------
 * 26-Nov-2002 : Version 1, contributed by David M. O'Donnell (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.Color;
import java.awt.GradientPaint;
import java.util.Date;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.axis.HorizontalColorBarAxis;
import com.jrefinery.chart.axis.HorizontalDateAxis;
import com.jrefinery.chart.axis.HorizontalLogarithmicAxis;
import com.jrefinery.chart.axis.HorizontalLogarithmicColorBarAxis;
import com.jrefinery.chart.axis.HorizontalNumberAxis;
import com.jrefinery.chart.axis.NumberAxis;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.axis.VerticalColorBarAxis;
import com.jrefinery.chart.axis.VerticalLogarithmicAxis;
import com.jrefinery.chart.axis.VerticalLogarithmicColorBarAxis;
import com.jrefinery.chart.axis.VerticalNumberAxis;
import com.jrefinery.chart.plot.ContourPlot;
import com.jrefinery.data.ContourDataset;
import com.jrefinery.data.DefaultContourDataset;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A demonstration application to illustrate ContourPlot.
 * Command line options exist to control different plot properties
 * such as colorbar orientation, etc.  List of options are available
 * by launching with the -? option, e.g., ContourPlotDemo -?
 *
 * @author David M. O'Donnell
 */
public class ContourPlotDemo extends ApplicationFrame {

    /** The x-axis. */
    private ValueAxis xAxis = null;
    
    /** The y-axis. */
    private NumberAxis yAxis = null;
    
    /** The z-axis. */
    private NumberAxis zAxis = null;

    /** Flag for vertical z-axis. */
    private static boolean zIsVertical = false;

    /** Flag for x is date axis. */
    private static boolean xIsDate = false;

    /** Flag for x is log. */
    private static boolean xIsLog = false;
    
    /** Flag for y is log. */
    private static boolean yIsLog = false;
    
    /** Flag for z is log. */
    private static boolean zIsLog = false;

    /** Flag for x is inverted. */
    private static boolean xIsInverted = false;
    
    /** Flag for y is inverted. */
    private static boolean yIsInverted = false;
    
    /** Flag for z is inverted. */
    private static boolean zIsInverted = false;

    /** Flag for make holes. */
    private static boolean makeHoles = false;

    /** The number of x values in the dataset. */
    private static int numX = 10;
    
    /** The number of y values in the dataset. */
    private static int numY = 20;

    /** The ratio. */
    private static double ratio = 0.0;

    /**
     * Constructs a new demonstration application.
     *
     * @param title  the frame title.
     */
    public ContourPlotDemo(String title) {

        super(title);

        JFreeChart chart = createContourPlot();
        ChartPanel panel = new ChartPanel(chart, true, true, true, true, true);
        panel.setPreferredSize(new java.awt.Dimension(500, 270));
        panel.setMaximumDrawHeight(100000); //stop ChartPanel from scaling output
        panel.setMaximumDrawWidth(100000); //stop ChartPanel from scaling output
        panel.setHorizontalZoom(true);
        panel.setVerticalZoom(true);
        panel.setFillZoomRectangle(true);
        setContentPane(panel);

    }

    /**
     * Creates a ContourPlot chart.
     *
     * @return the chart.
     */
    private JFreeChart createContourPlot() {

        String title = "Contour Plot";
        String xAxisLabel = "X Values";
        String yAxisLabel = "Y Values";
        String zAxisLabel = "Color Values";

        if (xIsDate) {
            xAxis = new HorizontalDateAxis(xAxisLabel);
            xIsLog = false; // force axis to be linear when displaying dates
        }
        else {
            if (xIsLog) {
                xAxis = new HorizontalLogarithmicAxis(xAxisLabel);
            }
            else {
                xAxis = new HorizontalNumberAxis(xAxisLabel);
            }
        }

        if (yIsLog) {
            yAxis = new VerticalLogarithmicAxis(yAxisLabel);
        }
        else {
            yAxis = new VerticalNumberAxis(yAxisLabel);
        }

        if (zIsVertical) {
            if (zIsLog) {
                zAxis = new VerticalLogarithmicColorBarAxis(zAxisLabel);
            }
            else {
                zAxis = new VerticalColorBarAxis(zAxisLabel);
            }
        }
        else {
            if (zIsLog) {
                zAxis = new HorizontalLogarithmicColorBarAxis(zAxisLabel);
            }
            else {
                zAxis = new HorizontalColorBarAxis(zAxisLabel);
            }
        }

        if (xAxis instanceof HorizontalNumberAxis) {
            ((HorizontalNumberAxis) xAxis).setAutoRangeIncludesZero(false);
            ((HorizontalNumberAxis) xAxis).setInverted(xIsInverted);
        }

        yAxis.setAutoRangeIncludesZero(false);
        zAxis.setAutoRangeIncludesZero(false);

        yAxis.setInverted(yIsInverted);

        if (!xIsDate) {
            ((NumberAxis) xAxis).setLowerMargin(0.0);
            ((NumberAxis) xAxis).setUpperMargin(0.0);
        }

        yAxis.setLowerMargin(0.0);
        yAxis.setUpperMargin(0.0);

        zAxis.setInverted(zIsInverted);
        zAxis.setTickMarksVisible(true);

        ContourDataset data = createDataset();

        ContourPlot plot = new ContourPlot(data, xAxis, yAxis, zAxis);

        if (xIsDate) {
            ratio = Math.abs(ratio); // don't use plot units for ratios when x axis is date
        }
        plot.setDataAreaRatio(ratio);

        JFreeChart chart = new JFreeChart(title, null, plot, false);
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.green));

        return chart;

    }

    /**
     * Creates a ContourDataset.
     *
     * @return ContourDataset.
     */
    private ContourDataset createDataset() {
        
        int numValues = numX * numY;
        Date[] tmpDateX = new Date[numValues];
        double[] tmpDoubleX = new double[numValues];
        double[] tmpDoubleY = new double[numValues];
        double[] tmpDoubleZ = new double[numValues];

        Double[] oDoubleX = new Double[numValues];
        Double[] oDoubleY = new Double[numValues];
        Double[] oDoubleZ = new Double[numValues];

        int j = 0;
        int z = 0;
        int i = 0;
        int last = 0;
        double zmult = 1.0;
        for (int k = 0; k < numValues; k++) {
            i = k / numX;
            if (last != i) {
                last = i;
                z = 0;
                zmult = 1.005 * zmult;
            }
            tmpDateX[k] = new Date((long) ((i + 100) * 1.e8));
            tmpDoubleX[k] = i + 2;
            tmpDoubleY[k] = zmult * (z++);
            oDoubleX[k] = new Double(tmpDoubleX[k]);
            oDoubleY[k] = new Double(tmpDoubleY[k]);
            double rad = Math.random();
            if (makeHoles && (rad > 0.4 && rad < 0.6)) {
                oDoubleZ[k] = null;
            } 
            else {
                tmpDoubleZ[k] = 3.0 * ((tmpDoubleX[k] + 1) * (tmpDoubleY[k] + 1) + 100);
                oDoubleZ[k] = new Double(3.0 * ((tmpDoubleX[k] + 1) * (tmpDoubleY[k] + 1) + 100));
            }
            j++;
        }
        ContourDataset data = null;

        if (xIsDate) {
            data = new DefaultContourDataset("Contouring", tmpDateX, oDoubleY, oDoubleZ);
        } 
        else {
            data = new DefaultContourDataset("Contouring", oDoubleX, oDoubleY, oDoubleZ);
        }
        return data;

    }

    /**
     * Sets options passed via the command line
     *
     * @param args  the command line arguments.
     * 
     * @return Flag indicating whether program should continue.
     */
    protected static boolean processArgs(String[] args) {
        
        String[] options = { "-?", 
                             "-invert", 
                             "-log", 
                             "-date", 
                             "-vertical", 
                             "-holes", 
                             "-ratio:",
                             "-numX:", 
                             "-numY:" };

        for (int i = 0; i < args.length; i++) {
            boolean foundOption = false;
            for (int j = 0; j < options.length; j++) {
                if (args[i].startsWith(options[j])) {
                    foundOption = true;
                    int index = 0;
                    String tmpStr = null;
                    switch (j) {
                        case 0: // -?
                            usage();
                            return false;
                        case 1:
                            xIsInverted = true;
                            yIsInverted = true;
                            zIsInverted = true;
                            break;
                        case 2:
                            xIsLog = true;
                            yIsLog = true;
                            zIsLog = true;
                            break;
                        case 3:
                            xIsDate = true;
                            break;
                        case 4:
                            zIsVertical = true;
                            break;
                        case 5:
                            makeHoles = true;
                            break;
                        case 6:
                            index = args[i].indexOf(':');
                            tmpStr = args[i].substring(index + 1);
                            ratio = Double.parseDouble(tmpStr);
                            break;
                        case 7:
                            index = args[i].indexOf(':');
                            tmpStr = args[i].substring(index + 1);
                            numX = Integer.parseInt(tmpStr);
                            break;
                        case 8:
                            index = args[i].indexOf(':');
                            tmpStr = args[i].substring(index + 1);
                            numY = Integer.parseInt(tmpStr);
                            break;
                        default:
                            System.out.println("Only 9 options available, update options array");
                    }
                }
            }
            if (!foundOption) {
                System.out.println("Unknown option: " + args[i]);
                usage();
                return false;
            }
        }

        return true; // continue running application
    }

    /**
     * Prints usage information.
     */
    public static void usage() {
        System.out.println("Usage:");
        System.out.println("ContourPlotDemo -? -invert -log -date -vertical -holes -ratio:value "
                           + "-numX:value -numY:value");
        System.out.println("Where:");
        System.out.println("-? displays usage and exits");
        System.out.println("-invert cause axes to be inverted");
        System.out.println("-log all axes will be logcale");
        System.out.println("-date the X axis will be a date");
        System.out.println("-vertical the colorbar will be drawn vertically");
        System.out.println("-holes demos plotting data with missing values");
        System.out.println("-ratio forces plot to maintain aspect ratio (Y/X) indicated by value");
        System.out.println("       positive values are in pixels, while negative is in plot units");
        System.out.println("-numX number of values to generate along the X axis");
        System.out.println("-numY number of values to generate along the X axis");

    }
    /**
     * Starting point for the demonstration application.
     *
     * @param args  command line options, launch ContourDemoPlot -? for listing of options.
     */
    public static void main(String[] args) {

        if (!processArgs(args)) {
            System.exit(1);
        }
        ContourPlotDemo demo = new ContourPlotDemo("ContourPlot Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
