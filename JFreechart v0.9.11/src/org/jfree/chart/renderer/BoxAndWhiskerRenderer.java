/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
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
 * --------------------------
 * BoxAndWhiskerRenderer.java
 * --------------------------
 * (C) Copyright 2003, by David Browning and Contributors.
 *
 * Original Author:  David Browning;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: BoxAndWhiskerRenderer.java,v 1.1 2007/10/10 19:09:11 vauchers Exp $
 *
 * Changes
 * -------
 * 05-Aug-2003 : Version 1, contributed by David Browning.  Based on code in the 
 *               CandlestickRenderer class.  Additional modifications by David Gilbert to
 *               make the code work with 0.9.10 changes (DG);
 *
 * DO NOT USE drawHorizontalItem() - IT IS INCOMPLETE
 * TO EXPERIMENT, USE drawVerticalItem()
 *
 * NB THERE ARE SOME SPECIFIC AIMS (AUSTRALIAN INSTITUTE OF MARINE SCIENCE) REQUIREMENTS
 * WHICH I HAVE HARD-CODED FOR THE MOMENT... (DB)
 *
 */

package org.jfree.chart.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.CrosshairInfo;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.BoxAndWhiskerDataset;
import org.jfree.data.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;

/**
 * A renderer that draws box and whiskers on an {@link XYPlot} (requires a {@link BoxAndWhiskerDataset}).
 *
 * @author David Browning
 */
public class BoxAndWhiskerRenderer extends AbstractXYItemRenderer
                                 implements XYItemRenderer, Serializable {

    /** The candle width. */
    private double boxWidth;

    /** The paint used to fill the candle when the price moved up from open to close. */
    private transient Paint paint;

    /**
     * Creates a new renderer for candlestick charts.
     */
    public BoxAndWhiskerRenderer() {
        this(-1.0);
    }

    /**
     * Creates a new renderer for candlestick charts.
     * <P>
     * Use -1 for the candle width if you prefer the width to be calculated automatically.
     *
     * @param boxWidth  The box width.
     */
    public BoxAndWhiskerRenderer(double boxWidth) {

        super();
        this.boxWidth = boxWidth;
        this.paint = Color.green;
        setToolTipGenerator(new BoxAndWhiskerToolTipGenerator());

    }

    /**
     * Returns the width of each candle.
     *
     * @return The candle width.
     */
    public double getBoxWidth() {
        return this.boxWidth;
    }

    /**
     * Sets the candle width.
     * <P>
     * If you set the width to a negative value, the renderer will calculate
     * the candle width automatically based on the space available on the chart.
     *
     * @param width  The width.
     */
    public void setBoxWidth(double width) {

        if (width != this.boxWidth) {
            Double old = new Double(this.boxWidth);
            this.boxWidth = width;
            this.firePropertyChanged("BoxAndWhiskerRenderer.boxWidth", old, new Double(width));
        }

    }

    /**
     * Returns the paint used to fill candles when the price moves up from open
     * to close.
     *
     * @return The paint.
     */
    public Paint getPaint() {
        return this.paint;
    }

    /**
     * Sets the paint used to fill candles when the price moves up from open
     * to close.
     * <P>
     * Registered property change listeners are notified that the
     * "CandleStickRenderer.upPaint" property has changed.
     *
     * @param paint The paint.
     */
    public void setPaint(Paint paint) {

        Paint old = this.paint;
        this.paint = paint;
        this.firePropertyChanged("BoxAndWhiskerRenderer.paint", old, paint);

    }


    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area within which the plot is being drawn.
     * @param info  collects info about the drawing.
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairInfo  information about crosshairs on a plot.
     * @param pass  the pass index.
     */
    public void drawItem(Graphics2D g2, Rectangle2D dataArea,
                         ChartRenderingInfo info,
                         XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
                         XYDataset dataset, int series, int item,
                         CrosshairInfo crosshairInfo,
                         int pass) {
                             
        PlotOrientation orientation = plot.getOrientation();
        this.setPaint(getItemPaint(series, item));

        if (orientation == PlotOrientation.HORIZONTAL) {
            drawHorizontalItem(g2, dataArea, info, plot, domainAxis, rangeAxis, 
                               dataset, series, item, 
                               crosshairInfo, pass);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            drawVerticalItem(g2, dataArea, info, plot, domainAxis, rangeAxis, 
                             dataset, series, item, 
                             crosshairInfo, pass);
        }
    
    }

    /**
     * Draws the visual representation of a single data item. Horizontal variant is
     * unlikely to be used. THIS METHOD IS INCOMPLETE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     *
     * @param g2  the graphics device.
     * @param dataArea  the area within which the plot is being drawn.
     * @param info  collects info about the drawing.
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairInfo  information about crosshairs on a plot.
     * @param pass  the pass index.
     */
    public void drawHorizontalItem(Graphics2D g2, Rectangle2D dataArea,
                                   ChartRenderingInfo info,
                                   XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
                                   XYDataset dataset, int series, int item,
                                   CrosshairInfo crosshairInfo,
                                   int pass) {   

        // setup for collecting optional entity info...
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getEntityCollection();
        }


        BoxAndWhiskerDataset boxAndWhiskerData = (BoxAndWhiskerDataset) dataset;
        //System.err.println(series + "|" + item + " : " + boxAndWhiskerData.makeString(series, item));
        Collection extremeOutliers = new ArrayList();

        Number x = boxAndWhiskerData.getXValue(series, item);
        Number yMax = boxAndWhiskerData.getMaxValue(series, item);
        Number yMin = boxAndWhiskerData.getMinValue(series, item);
        Number yMedian = boxAndWhiskerData.getMedianValue(series, item);
        Number yAverage = boxAndWhiskerData.getAverageValue(series, item);
        Number yQ1Median = boxAndWhiskerData.getQ1MedianValue(series, item);
        Number yQ3Median = boxAndWhiskerData.getQ3MedianValue(series, item);
        Number[] yOutliers = boxAndWhiskerData.getOutliersArray(series, item);

        double xx = domainAxis.translateValueToJava2D(x.doubleValue(), dataArea,
                                                      plot.getDomainAxisEdge());

        RectangleEdge location = plot.getRangeAxisEdge();
        double yyMax = rangeAxis.translateValueToJava2D(yMax.doubleValue(), dataArea, location);
        double yyMin = rangeAxis.translateValueToJava2D(yMin.doubleValue(), dataArea, location);
        double yyMedian = rangeAxis.translateValueToJava2D(yMedian.doubleValue(), dataArea, location);
        double yyAverage = rangeAxis.translateValueToJava2D(yAverage.doubleValue(), dataArea, location);
        double yyQ1Median = rangeAxis.translateValueToJava2D(yQ1Median.doubleValue(), dataArea, location);
        double yyQ3Median = rangeAxis.translateValueToJava2D(yQ3Median.doubleValue(), dataArea, location);
        double[] yyOutliers = new double[yOutliers.length];

        // populate yyOutliers
        for (int i = 0; i < yyOutliers.length; i++) {
            yyOutliers[i] = rangeAxis.translateValueToJava2D(yOutliers[i].doubleValue(), dataArea, location);
        }



        double exactCandleWidth = boxWidth;
        double thisCandleWidth = boxWidth;
        if (boxWidth <= 0.0) {
            int itemCount = boxAndWhiskerData.getItemCount(series);
            exactCandleWidth = (dataArea.getHeight()) / itemCount * 4.5 / 7;
            if (exactCandleWidth < 1) {
                exactCandleWidth = 1;
            }
            thisCandleWidth = exactCandleWidth;
            if (thisCandleWidth < 3) {
                thisCandleWidth = 3;
            }
        }

        Stroke s = getItemStroke(series, item);

        g2.setStroke(s);

        // draw the upper shadow
        if ((yyMax > yyQ1Median) && (yyMax > yyQ3Median)) {
            g2.draw(new Line2D.Double(yyMax, xx, Math.max(yyQ1Median, yyQ3Median), xx));
        }

        // draw the lower shadow
        if ((yyMin < yyQ1Median) && (yyMin < yyQ3Median)) {
            g2.draw(new Line2D.Double(yyMin, xx, Math.min(yyQ1Median, yyQ3Median), xx));
        }


        // draw the body
        Shape body = null;
        if (yyQ1Median < yyQ3Median) {
            body = new Rectangle2D.Double(yyQ1Median, xx - thisCandleWidth / 2,
                                          yyQ3Median - yyQ1Median, thisCandleWidth);
        }
        else {
            body = new Rectangle2D.Double(yyQ3Median, xx - thisCandleWidth / 2,
                                          yyQ1Median - yyQ3Median, thisCandleWidth);
            if (paint != null) {
                g2.setPaint(paint);
                g2.fill(body);
            }
            g2.draw(body);
        }

        // add an entity for the item...
        if (entities != null) {
            String tip = null;
            if (getToolTipGenerator() != null) {
                tip = getToolTipGenerator().generateToolTip(dataset, series, item);
            }
            String url = null;
            if (getURLGenerator() != null) {
                url = getURLGenerator().generateURL(dataset, series, item);
            }
            XYItemEntity entity = new XYItemEntity(body, dataset, series, item, tip, url);
            entities.addEntity(entity);
        }

    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area within which the plot is being drawn.
     * @param info  collects info about the drawing.
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairInfo  information about crosshairs on a plot.
     * @param pass  the pass index.
     */
    public void drawVerticalItem(Graphics2D g2, Rectangle2D dataArea,
                                 ChartRenderingInfo info,
                                 XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
                                 XYDataset dataset, int series, int item,
                                 CrosshairInfo crosshairInfo,
                                 int pass) {   

        // setup for collecting optional entity info...
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getEntityCollection();
        }

        BoxAndWhiskerDataset boxAndWhiskerData = (BoxAndWhiskerDataset) dataset;
        //System.err.print("\n\n" +series + "|" + item + " : " + boxAndWhiskerData.makeString(series, item));
        Collection outliers = new ArrayList();

        Number x = boxAndWhiskerData.getXValue(series, item);
        Number yMax = boxAndWhiskerData.getMaxValue(series, item);
        Number yMin = boxAndWhiskerData.getMinValue(series, item);
        Number yMedian = boxAndWhiskerData.getMedianValue(series, item);
        Number yAverage = boxAndWhiskerData.getAverageValue(series, item);
        Number yQ1Median = boxAndWhiskerData.getQ1MedianValue(series, item);
        Number yQ3Median = boxAndWhiskerData.getQ3MedianValue(series, item);
        Number[] yOutliers = boxAndWhiskerData.getOutliersArray(series, item);

        double xx = domainAxis.translateValueToJava2D(x.doubleValue(), dataArea,
                                                      plot.getDomainAxisEdge());

        RectangleEdge location = plot.getRangeAxisEdge();
        double yyMax = rangeAxis.translateValueToJava2D(yMax.doubleValue(), dataArea, location);
        double yyMin = rangeAxis.translateValueToJava2D(yMin.doubleValue(), dataArea, location);
        double yyMedian = rangeAxis.translateValueToJava2D(yMedian.doubleValue(), dataArea, location);
        double yyAverage = rangeAxis.translateValueToJava2D(yAverage.doubleValue(), dataArea, location);
        double yyQ1Median = rangeAxis.translateValueToJava2D(yQ1Median.doubleValue(), dataArea, location);    // should be low range
        double yyQ3Median = rangeAxis.translateValueToJava2D(yQ3Median.doubleValue(), dataArea, location);    // should be high range
        double[] yyOutliers = new double[yOutliers.length];

        // populate yyOutliers
        for (int i = 0; i < yyOutliers.length; i++) {
            yyOutliers[i] = rangeAxis.translateValueToJava2D(yOutliers[i].doubleValue(), dataArea, location);
        }

        double exactCandleWidth = boxWidth;
        double thisCandleWidth = boxWidth;
        if (boxWidth <= 0.0) {
            int itemCount = boxAndWhiskerData.getItemCount(series);
            exactCandleWidth = (dataArea.getMaxX() - dataArea.getMinX()) / itemCount * 4.5 / 7;
            if (exactCandleWidth < 1) {
                exactCandleWidth = 1;
            }
            thisCandleWidth = exactCandleWidth;
            if (thisCandleWidth < 3) {
                thisCandleWidth = 3;
            }
        }

        Paint p = this.getPaint();
        if (p != null) {
            g2.setPaint(p);
        }
        Stroke s = getItemStroke(series, item);

        g2.setStroke(s);

        // draw the upper shadow
        if ((yyMax < yyQ1Median) && (yyMax < yyQ3Median)) {
            g2.draw(new Line2D.Double(xx, yyMax, xx,
                Math.min(yyQ1Median, yyQ3Median)));
            g2.draw(new Line2D.Double(xx - thisCandleWidth / 2,
                    yyMax, xx + thisCandleWidth / 2, yyMax));
        }

        // draw the lower shadow
        if ((yyMin > yyQ1Median) && (yyMin > yyQ3Median)) {
            g2.draw(new Line2D.Double(xx, yyMin, xx,
                Math.max(yyQ1Median, yyQ3Median)));
            g2.draw(new Line2D.Double(xx - thisCandleWidth / 2,
                    yyMin, xx + thisCandleWidth / 2, yyMin));
        }


        // draw the body
        Shape body = null;
        if (yyQ1Median > yyQ3Median) {
            body = new Rectangle2D.Double(xx - thisCandleWidth / 2, yyQ3Median,
                                          thisCandleWidth, yyQ1Median - yyQ3Median);
        }
        else {
            body = new Rectangle2D.Double(xx - thisCandleWidth / 2, yyQ1Median,
                                          thisCandleWidth, yyQ3Median - yyQ1Median);
            g2.fill(body);
            g2.draw(body);
        }

        // draw median
        g2.setPaint(Color.black);
        g2.draw(new Line2D.Double(xx - thisCandleWidth / 2, yyMedian, xx + thisCandleWidth / 2, yyMedian));

        // draw average - SPECIAL AIMS REQUIREMENT
        double aRadius = thisCandleWidth/4;
        Ellipse2D.Double avgEllipse = new Ellipse2D.Double(xx - aRadius, yyAverage - aRadius, aRadius * 2, aRadius * 2);
        g2.fill(avgEllipse);
        g2.draw(avgEllipse);

        // draw yOutliers
        double oRadius = thisCandleWidth/3;
        double m = rangeAxis.translateValueToJava2D(rangeAxis.getMaximumAxisValue(), dataArea, location) + aRadius;
        OutlierListCollection collection = new OutlierListCollection();

        for (int i = 0; i < yyOutliers.length; i++) {
            if (yyOutliers[i] > (yyMedian + ((yyQ1Median - yyQ3Median) * 1.2))) {    // hard-coded value is a SPECIFIC AIMS REQUIEMENT
                outliers.add(new Outlier(xx, yyOutliers[i], oRadius));
            } else {    // draw extreme outliers symbol
                collection.setFarOut(true);
            }
            Collections.sort((ArrayList)outliers);
        }

        // process outliers
        for (Iterator iterator = outliers.iterator(); iterator.hasNext();) {
            Outlier outlier = (Outlier) iterator.next();
            //System.err.print("\noutlier = " + outlier.toString());
            collection.add(outlier);
        }

        for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
            OutlierList list = (OutlierList) iterator.next();
            Outlier outlier = list.getAveragedOutlier();
            Point2D point = outlier.getPoint();

            if (list.isMultiple()) {
                drawMultipleEllipse(point, thisCandleWidth, oRadius, g2);
            } else {
                drawEllipse(point, oRadius, g2);
            }
        }

        if (collection.isFarOut()) {
            drawFarOut(aRadius, g2, xx, m);
        }


        g2.setPaint(p);


        // add an entity for the item...
        if (entities != null) {
            String tip = null;
            if (getToolTipGenerator() != null) {
                tip = getToolTipGenerator().generateToolTip(dataset, series, item);
            }
            String url = null;
            if (getURLGenerator() != null) {
                url = getURLGenerator().generateURL(dataset, series, item);
            }
            XYItemEntity entity = new XYItemEntity(body, dataset, series, item, tip, url);
            entities.addEntity(entity);
        }

    }

    private void drawEllipse(Point2D point, double oRadius, Graphics2D g2) {
        Ellipse2D.Double dot = new Ellipse2D.Double(point.getX() + oRadius/2, point.getY(), oRadius, oRadius);
        g2.draw(dot);
    }

    private void drawMultipleEllipse(Point2D point, double thisCandleWidth, double oRadius, Graphics2D g2) {
        Ellipse2D.Double dot1 = new Ellipse2D.Double(point.getX() - (thisCandleWidth/2) + oRadius, point.getY(), oRadius, oRadius);
        Ellipse2D.Double dot2 = new Ellipse2D.Double(point.getX() + (thisCandleWidth/2), point.getY(), oRadius, oRadius);
        g2.draw(dot1);
        g2.draw(dot2);
    }

    private void drawFarOut(double aRadius, Graphics2D g2, double xx, double m) {
        double side = aRadius * 2;
        g2.draw(new Line2D.Double(xx - side, m + side, xx + side, m + side));
        g2.draw(new Line2D.Double(xx - side, m + side, xx, m));
        g2.draw(new Line2D.Double(xx + side, m + side, xx, m));
    }

    /**
     * Tests this renderer for equality with another object.
     *
     * @param obj  the object.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (obj instanceof BoxAndWhiskerRenderer) {
            BoxAndWhiskerRenderer renderer = (BoxAndWhiskerRenderer) obj;
            boolean result = super.equals(obj);
            result = result && (this.boxWidth == renderer.getBoxWidth());
            result = result && (this.paint.equals(renderer.getPaint()));
            return result;
        }

        return false;

    }

    /**
     * Provides serialization support.
     *
     * @param stream  the output stream.
     *
     * @throws IOException  if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {

        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.paint, stream);

    }

    /**
     * Provides serialization support.
     *
     * @param stream  the input stream.
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {

        stream.defaultReadObject();
        this.paint = SerialUtilities.readPaint(stream);

    }


}
