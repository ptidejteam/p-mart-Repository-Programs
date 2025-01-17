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
 * ----------------------------
 * XYBoxAndWhiskerRenderer.java
 * ----------------------------
 * (C) Copyright 2003, by David Browning and Contributors.
 *
 * Original Author:  David Browning (for Australian Institute of Marine Science);
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: XYBoxAndWhiskerRenderer.java,v 1.1 2007/10/10 19:15:26 vauchers Exp $
 *
 * Changes
 * -------
 * 05-Aug-2003 : Version 1, contributed by David Browning.  Based on code in the
 *               CandlestickRenderer class.  Additional modifications by David Gilbert to
 *               make the code work with 0.9.10 changes (DG);
 * 08-Aug-2003 : Updated some of the Javadoc
 *               Allowed BoxAndwhiskerDataset Average value to be null - the average value is an 
 *               AIMS requirement
 *               Allow the outlier and farout coefficients to be set -
 *                  though at the moment this only affects the calculation of farouts.
 *               Added artifactPaint variable and setter/getter
 * 12-Aug-2003   Rewrote code to sort out and process outliers to take advantage of changes in
 *               DefaultBoxAndWhiskerDataset
 *               Added a limit of 10% for width of box should no width be specified...
 *                  Maybe this should be setable???
 * 20-Aug-2003 : Implemented Cloneable and PublicCloneable (DG);
 * 08-Sep-2003 : Changed ValueAxis API (DG);
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 *
 *
 * DO NOT USE drawHorizontalItem() - IT IS INCOMPLETE
 * TO EXPERIMENT, USE drawVerticalItem()
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
import java.util.List;

import org.jfree.chart.CrosshairInfo;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.XYDataset;
import org.jfree.data.statistics.BoxAndWhiskerXYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

/**
 * A renderer that draws box-and-whisker items on an {@link XYPlot}.  This renderer requires a 
 * {@link BoxAndWhiskerXYDataset}).
 *
 * @author David Browning
 */
public class XYBoxAndWhiskerRenderer extends AbstractXYItemRenderer 
                                     implements XYItemRenderer, 
                                                Cloneable,
                                                PublicCloneable,
                                                Serializable {

    /** The box width. */
    private double boxWidth;

    /** The paint used to fill the box. */
    private transient Paint paint;

    /** 
     * The paint used to draw various artifacts such as outliers, farout symbol, average 
     * ellipse and median line. 
     */
    private Paint artifactPaint = Color.black;

    /**
     * Creates a new renderer for box and whisker charts.
     */
    public XYBoxAndWhiskerRenderer() {
        this(-1.0);
    }

    /**
     * Creates a new renderer for box and whisker charts.
     * <P>
     * Use -1 for the box width if you prefer the width to be calculated automatically.
     *
     * @param boxWidth  The box width.
     */
    public XYBoxAndWhiskerRenderer(double boxWidth) {
        super();
        this.boxWidth = boxWidth;
        this.paint = Color.green;
        setToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
    }

    /**
     * Returns the width of each box.
     *
     * @return The box width.
     */
    public double getBoxWidth() {
        return this.boxWidth;
    }

    /**
     * Sets the box width.
     * <P>
     * If you set the width to a negative value, the renderer will calculate
     * the box width automatically based on the space available on the chart.
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
     * Returns the paint used to fill boxes.
     *
     * @return The paint.
     */
    public Paint getPaint() {
        return this.paint;
    }

    /**
     * Sets the paint used to fill boxes.
     * <P>
     * Registered property change listeners are notified that the
     * "BoxAndWhiskerRenderer.paint" property has changed.
     *
     * @param paint The paint.
     */
    public void setPaint(Paint paint) {

        Paint old = this.paint;
        this.paint = paint;
        this.firePropertyChanged("BoxAndWhiskerRenderer.paint", old, paint);

    }

    /**
     * Returns the paint used to paint the various artifacts such as outliers, farout symbol,
     * median line and the averages ellipse.
     *
     * @return The paint.
     */
    public Paint getArtifactPaint() {
        return artifactPaint;
    }

    /**
     * Sets the paint used to paint the various artifacts such as outliers, farout symbol,
     * median line and the averages ellipse.
     * 
     * @param artifactPaint  the paint.
     */
    public void setArtifactPaint(Paint artifactPaint) {
        this.artifactPaint = artifactPaint;
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
    public void drawItem(Graphics2D g2, 
                         Rectangle2D dataArea,
                         PlotRenderingInfo info,
                         XYPlot plot, 
                         ValueAxis domainAxis, 
                         ValueAxis rangeAxis,
                         XYDataset dataset, 
                         int series, 
                         int item,
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
    public void drawHorizontalItem(Graphics2D g2, 
                                   Rectangle2D dataArea,
                                   PlotRenderingInfo info,
                                   XYPlot plot, 
                                   ValueAxis domainAxis, 
                                   ValueAxis rangeAxis,
                                   XYDataset dataset, 
                                   int series, 
                                   int item,
                                   CrosshairInfo crosshairInfo,
                                   int pass) {

        // setup for collecting optional entity info...
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }


        BoxAndWhiskerXYDataset boxAndWhiskerData = (BoxAndWhiskerXYDataset) dataset;

        Collection extremeOutliers = new ArrayList();
        Number x = boxAndWhiskerData.getXValue(series, item);
        Number yMax = boxAndWhiskerData.getMaxRegularValue(series, item);
        Number yMin = boxAndWhiskerData.getMinRegularValue(series, item);
        Number yMedian = boxAndWhiskerData.getMedianValue(series, item);
        Number yAverage = boxAndWhiskerData.getMeanValue(series, item);
        Number yQ1Median = boxAndWhiskerData.getQ1Value(series, item);
        Number yQ3Median = boxAndWhiskerData.getQ3Value(series, item);
        List yOutliers = boxAndWhiskerData.getOutliers(series, item);

        double xx = domainAxis.translateValueToJava2D(x.doubleValue(), dataArea,
                                                      plot.getDomainAxisEdge());

        RectangleEdge location = plot.getRangeAxisEdge();
        double yyMax = rangeAxis.translateValueToJava2D(yMax.doubleValue(), dataArea, location);
        double yyMin = rangeAxis.translateValueToJava2D(yMin.doubleValue(), dataArea, location);

        double yyMedian = rangeAxis.translateValueToJava2D(yMedian.doubleValue(), 
                                                           dataArea, location);
        double yyAverage = rangeAxis.translateValueToJava2D(yAverage.doubleValue(), 
                                                            dataArea, location);

        double yyQ1Median = rangeAxis.translateValueToJava2D(yQ1Median.doubleValue(), 
                                                             dataArea, location);
        double yyQ3Median = rangeAxis.translateValueToJava2D(yQ3Median.doubleValue(),
                                                             dataArea, location);
        List yyOutliers = new ArrayList();

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
    public void drawVerticalItem(Graphics2D g2, 
                                 Rectangle2D dataArea,
                                 PlotRenderingInfo info,
                                 XYPlot plot, 
                                 ValueAxis domainAxis, 
                                 ValueAxis rangeAxis,
                                 XYDataset dataset, 
                                 int series, 
                                 int item,
                                 CrosshairInfo crosshairInfo,
                                 int pass) {

        // setup for collecting optional entity info...
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }

        BoxAndWhiskerXYDataset boxAndWhiskerData = (BoxAndWhiskerXYDataset) dataset;

        Number x = boxAndWhiskerData.getXValue(series, item);
        Number yMax = boxAndWhiskerData.getMaxRegularValue(series, item);
        Number yMin = boxAndWhiskerData.getMinRegularValue(series, item);
        Number yMedian = boxAndWhiskerData.getMedianValue(series, item);
        Number yAverage = boxAndWhiskerData.getMeanValue(series, item);
        Number yQ1Median = boxAndWhiskerData.getQ1Value(series, item);
        Number yQ3Median = boxAndWhiskerData.getQ3Value(series, item);
        List yOutliers = boxAndWhiskerData.getOutliers(series, item);

        double xx = domainAxis.translateValueToJava2D(x.doubleValue(), dataArea,
                                                      plot.getDomainAxisEdge());

        RectangleEdge location = plot.getRangeAxisEdge();
        double yyMax = rangeAxis.translateValueToJava2D(yMax.doubleValue(), dataArea, location);
        double yyMin = rangeAxis.translateValueToJava2D(yMin.doubleValue(), dataArea, location);
        double yyMedian = rangeAxis.translateValueToJava2D(yMedian.doubleValue(), 
                                                           dataArea, location);
        double yyAverage = 0.0;
        if (yAverage != null) {
            yyAverage = rangeAxis.translateValueToJava2D(yAverage.doubleValue(), 
                                                         dataArea, location);
        }
        double yyQ1Median = rangeAxis.translateValueToJava2D(yQ1Median.doubleValue(),
                                          dataArea, location);    // should be low range
        double yyQ3Median = rangeAxis.translateValueToJava2D(yQ3Median.doubleValue(), 
                                          dataArea, location);    // should be high range
        double yyOutlier;


        double exactBoxWidth = boxWidth;
        double width = this.boxWidth;
        double dataAreaX = dataArea.getMaxX() - dataArea.getMinX();
        double maxBoxPercent = 0.1;
        double maxBoxWidth = dataAreaX * maxBoxPercent;
        if (boxWidth <= 0.0) {
            int itemCount = boxAndWhiskerData.getItemCount(series);
            exactBoxWidth = dataAreaX / itemCount * 4.5 / 7;
            if (exactBoxWidth < 3) {
                width = 3;
            } 
            else if (exactBoxWidth > maxBoxWidth) {
                width = maxBoxWidth;
            } 
            else {
                width = exactBoxWidth;
            }
        }

        Paint p = this.getPaint();
        if (p != null) {
            g2.setPaint(p);
        }
        Stroke s = getItemStroke(series, item);

        g2.setStroke(s);

        // draw the upper shadow
        if ((yyMax < yyQ1Median) && (yyMax < yyQ3Median)) {          // 0,0 = top left...
            g2.draw(new Line2D.Double(xx, yyMax, xx, Math.max(yyQ1Median, yyQ3Median)));
            g2.draw(new Line2D.Double(xx - width / 2, yyMax, xx + width / 2, yyMax));
        }

        // draw the lower shadow
        if ((yyMin > yyQ1Median) && (yyMin > yyQ3Median)) {
            g2.draw(new Line2D.Double(xx, yyMin, xx,
                Math.min(yyQ1Median, yyQ3Median)));
            g2.draw(new Line2D.Double(xx - width / 2, yyMin, xx + width / 2, yyMin));
        }


        // draw the body
        Shape body = null;
        if (yyQ1Median > yyQ3Median) {
            body = new Rectangle2D.Double(xx - width / 2, yyQ3Median, 
                                          width, yyQ1Median - yyQ3Median);
        }
        else {
            body = new Rectangle2D.Double(xx - width / 2, yyQ1Median,
                                          width, yyQ3Median - yyQ1Median);
        }
        g2.fill(body);
        g2.draw(body);

        // draw median
        g2.setPaint(this.artifactPaint);
        g2.draw(new Line2D.Double(xx - width / 2, yyMedian, xx + width / 2, yyMedian));

        double aRadius = 0;                 // average radius
        double oRadius = width / 3;    // outlier radius

        // draw average - SPECIAL AIMS REQUIREMENT
        if (yAverage != null) {
            aRadius = width / 4;
            Ellipse2D.Double avgEllipse = new Ellipse2D.Double(xx - aRadius, yyAverage - aRadius, 
                                                               aRadius * 2, aRadius * 2);
            g2.fill(avgEllipse);
            g2.draw(avgEllipse);
        }

        List outliers = new ArrayList();
        OutlierListCollection outlierListCollection = new OutlierListCollection();


        /* From outlier array sort out which are outliers and put these into an arraylist
         * If there are any farouts, set the flag on the OutlierListCollection
         */

        for (int i = 0; i < yOutliers.size(); i++) {
            double outlier = ((Number) yOutliers.get(i)).doubleValue();
            if (outlier > boxAndWhiskerData.getMaxOutlier(series, item).doubleValue()) {
                outlierListCollection.setHighFarOut(true);
            } 
            else if (outlier < boxAndWhiskerData.getMinOutlier(series, item).doubleValue()) {
                outlierListCollection.setLowFarOut(true);
            } 
            else if (outlier > boxAndWhiskerData.getMaxRegularValue(series, item).doubleValue()) {
                yyOutlier = rangeAxis.translateValueToJava2D(outlier, dataArea, location);
                outliers.add(new Outlier(xx, yyOutlier, oRadius));
            }
            else if (outlier < boxAndWhiskerData.getMinRegularValue(series, item).doubleValue()) {
                yyOutlier = rangeAxis.translateValueToJava2D(outlier, dataArea, location);
                outliers.add(new Outlier(xx, yyOutlier, oRadius));                
            }
            Collections.sort(outliers);
        }

        // Process outliers. Each outlier is either added to the appropriate outlier list
        // or a new outlier list is made
        for (Iterator iterator = outliers.iterator(); iterator.hasNext();) {
            Outlier outlier = (Outlier) iterator.next();
            outlierListCollection.add(outlier);
        }

        // draw yOutliers
        double maxAxisValue = rangeAxis.translateValueToJava2D(rangeAxis.getUpperBound(), 
                                  dataArea, location) + aRadius;
        double minAxisValue = rangeAxis.translateValueToJava2D(rangeAxis.getLowerBound(), 
                                  dataArea, location) - aRadius;

        //g2.setPaint(p);

        // draw outliers
        for (Iterator iterator = outlierListCollection.iterator(); iterator.hasNext();) {
            OutlierList list = (OutlierList) iterator.next();
            Outlier outlier = list.getAveragedOutlier();
            Point2D point = outlier.getPoint();

            if (list.isMultiple()) {
                drawMultipleEllipse(point, width, oRadius, g2);
            } 
            else {
                drawEllipse(point, oRadius, g2);
            }
        }

        // draw farout
        if (outlierListCollection.isHighFarOut()) {
            drawHighFarOut(aRadius, g2, xx, maxAxisValue);
        }

        if (outlierListCollection.isLowFarOut()) {
            drawLowFarOut(aRadius, g2, xx, minAxisValue);
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
     * Draws an ellipse to represent an outlier.
     * 
     * @param point  the location.
     * @param oRadius  the radius.
     * @param g2  the graphics device.
     */
    private void drawEllipse(Point2D point, double oRadius, Graphics2D g2) {
        Ellipse2D.Double dot = new Ellipse2D.Double(point.getX() + oRadius / 2, point.getY(), 
                                                    oRadius, oRadius);
        g2.draw(dot);
    }

    /**
     * Draws two ellipses to represent overlapping outliers.
     * 
     * @param point  the location.
     * @param boxWidth  the box width.
     * @param oRadius  the radius.
     * @param g2  the graphics device.
     */
    private void drawMultipleEllipse(Point2D point, double boxWidth, double oRadius, 
                                     Graphics2D g2) {
                                         
        Ellipse2D.Double dot1 = new Ellipse2D.Double(point.getX() - (boxWidth / 2) + oRadius, 
                                                     point.getY(), oRadius, oRadius);
        Ellipse2D.Double dot2 = new Ellipse2D.Double(point.getX() + (boxWidth / 2), 
                                                     point.getY(), oRadius, oRadius);
        g2.draw(dot1);
        g2.draw(dot2);
        
    }

    /**
     * Draws a triangle to indicate the presence of far out values.
     * 
     * @param aRadius  the radius.
     * @param g2  the graphics device.
     * @param xx  the x value.
     * @param m  the max y value.
     */
    private void drawHighFarOut(double aRadius, Graphics2D g2, double xx, double m) {
        double side = aRadius * 2;
        g2.draw(new Line2D.Double(xx - side, m + side, xx + side, m + side));
        g2.draw(new Line2D.Double(xx - side, m + side, xx, m));
        g2.draw(new Line2D.Double(xx + side, m + side, xx, m));
    }

    /**
     * Draws a triangle to indicate the presence of far out values.
     * 
     * @param aRadius  the radius.
     * @param g2  the graphics device.
     * @param xx  the x value.
     * @param m  the min y value.
     */
    private void drawLowFarOut(double aRadius, Graphics2D g2, double xx, double m) {
        double side = aRadius * 2;
        g2.draw(new Line2D.Double(xx - side, m - side, xx + side, m - side));
        g2.draw(new Line2D.Double(xx - side, m - side, xx, m));
        g2.draw(new Line2D.Double(xx + side, m - side, xx, m));
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

        if (obj instanceof XYBoxAndWhiskerRenderer) {
            XYBoxAndWhiskerRenderer renderer = (XYBoxAndWhiskerRenderer) obj;
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

    /**
     * Returns a clone of the renderer.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException  if the renderer cannot be cloned.
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
