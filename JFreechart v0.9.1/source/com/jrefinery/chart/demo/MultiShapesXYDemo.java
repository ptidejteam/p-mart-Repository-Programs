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
 * ----------------------
 * MultiShapesXYDemo.java
 * ----------------------
 * (C) Copyright 2002, by Andreas Schneider.
 *
 * Original Author:  Andreas Schneider;
 * Contributor(s):   -;
 *
 * $Id: MultiShapesXYDemo.java,v 1.1 2007/10/10 19:02:28 vauchers Exp $
 *
 * Changes
 * -------
 * 13-May-2002 : Version 1, contributed by Andreas Schneider (DG);
 * 30-May-2002 : Added title to application frame (DG);
 */

package com.jrefinery.chart.demo;

import java.awt.Paint;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Point;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.geom.*;
import java.net.URL;
import com.jrefinery.data.XYSeries;
import com.jrefinery.data.XYSeriesCollection;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.chart.*;

public class MultiShapesXYDemo extends ApplicationFrame {

    protected XYSeries series;
    private static final int numPoints = 200;
    private static final double inc = 0.1;
    // colors used
    private static final float R1=0.0f;
    private static final float G1=1.0f;
    private static final float B1=0.0f;
    private static final float R2=1.0f;
    private static final float G2=0.0f;
    private static final float B2=0.0f;
    private Image ballImage;
    private Image arrowImage;

    /**
     * A demonstration application showing a series with different shape attributes per item
     */
    public MultiShapesXYDemo(String title) {

        super(title);

        URL url1 = getClass().getClassLoader().getResource("com/jrefinery/chart/demo/redball.png");
        URL url2 = getClass().getClassLoader().getResource("com/jrefinery/chart/demo/arrow.png");
        if (url1 != null && url2 != null) {
        ballImage = new javax.swing.ImageIcon(url1).getImage();
        arrowImage = new javax.swing.ImageIcon(url2).getImage();
        MediaTracker tracker = new MediaTracker(this);
        tracker.addImage(ballImage,0);
        tracker.addImage(arrowImage,1);
        try {
            tracker.waitForID(0);
            tracker.waitForID(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
            System.out.println("Images loaded");
        } else {
            System.err.println("Can't find images");
        }
        this.series = new XYSeries("Some Data");
        for (int i=0; i< numPoints; i++) {
            double x = inc * (double)i;
            double y = Math.sin(x);
            this.series.add(x,y);
        }
        XYSeriesCollection data = new XYSeriesCollection(series);
        HorizontalNumberAxis domainAxis = new HorizontalNumberAxis("x");
        VerticalNumberAxis rangeAxis = new VerticalNumberAxis("sin(x)");
        DemoRenderer renderer = new DemoRenderer();
        Plot plot = new XYPlot(data, domainAxis, rangeAxis,renderer);
        JFreeChart chart = new JFreeChart(plot);
        ChartPanel chartPanel = new ChartPanel(chart);
        this.setContentPane(chartPanel);

    }

    class DemoRenderer extends StandardXYItemRenderer {

        public DemoRenderer() {
            super(StandardXYItemRenderer.SHAPES | StandardXYItemRenderer.IMAGES,null);
        }
        protected boolean isShapeFilled(Plot plot, int series, int item, double x, double y) {
            return (item % 2 == 0);
        }
        protected double getShapeScale(Plot plot, int series, int item, double x, double y) {
            float rat = (float)item/(float)numPoints; // 0..1
            return 6.0f-rat*4.0;
        }
        protected Paint getPaint(Plot plot, int series, int item, double x, double y) {
            float rat = (float)item/(float)numPoints; // 0..1
            float R = (1.0f-rat)*R1+rat*R2;
            float G = (1.0f-rat)*G1+rat*G2;
            float B = (1.0f-rat)*B1+rat*B2;
            Color c = new Color(R,G,B);
            return c;
        }
        protected Shape getShape(Plot plot, int series, int item, double x, double y, double scale) {
            Shape shape;
            switch (item % 2) {
              case 0:
                shape = new Rectangle2D.Double(x-0.5*scale, y-0.5*scale, scale, scale);
                break;
              default:
                shape = new Ellipse2D.Double(x-0.5*scale, y-0.5*scale, scale, scale);
                break;
            }
            return shape;
        }
        protected Image getImage(Plot plot, int series, int item, double x, double y) {
            if (item % 10 == 0){
                return ballImage;
            } else if (item==42) {
                return arrowImage;
            }
            return null;
        }
        protected Point getImageHotspot(Plot plot, int series, int item, double x, double y, Image image) {
            if (image == arrowImage) {
                // the arrow has the hot spot at the bottom left
                return new Point(0,image.getHeight(null));
            }
            return super.getImageHotspot(plot, series, item, x, y, image);
        }
    }

    /**
     * Starting point for the demonstration application.
     */
    public static void main(String[] args) {

        MultiShapesXYDemo demo = new MultiShapesXYDemo("XY Plot With Multiple Shapes");
        demo.pack();
        demo.setVisible(true);

    }

}
