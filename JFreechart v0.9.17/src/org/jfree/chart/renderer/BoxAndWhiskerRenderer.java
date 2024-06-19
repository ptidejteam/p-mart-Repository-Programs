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
 * --------------------------
 * BoxAndWhiskerRenderer.java
 * --------------------------
 * (C) Copyright 2003, 2004, by David Browning and Contributors.
 *
 * Original Author:  David Browning (for the Australian Institute of Marine Science);
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *                   Tim Bardzil;
 *
 * $Id: BoxAndWhiskerRenderer.java,v 1.1 2007/10/10 19:29:18 vauchers Exp $
 *
 * Changes
 * -------
 * 21-Aug-2003 : Version 1, contributed by David Browning (for the Australian Institute of 
 *               Marine Science);
 * 01-Sep-2003 : Incorporated outlier and farout symbols for low values also (DG);
 * 08-Sep-2003 : Changed ValueAxis API (DG);
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 07-Oct-2003 : Added renderer state (DG);
 * 12-Nov-2003 : Fixed casting bug reported by Tim Bardzil (DG);
 * 13-Nov-2003 : Added drawHorizontalItem(...) method contributed by Tim Bardzil (DG);
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.data.CategoryDataset;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.ui.RectangleEdge;

/**
 * A box-and-whisker renderer.
 */
public class BoxAndWhiskerRenderer extends AbstractCategoryItemRenderer {

    /** The color used to paint the median line and average marker. */
    private Paint artifactPaint;

    /** The box width. */
    private double boxWidth;

    /** The margin between items (boxes) within a category. */
    private double itemMargin;

    /**
     * Default constructor.
     */
    public BoxAndWhiskerRenderer () {
        this.artifactPaint = Color.black;
        this.boxWidth = 0.0;
        this.itemMargin = 0.20;
    }

    /**
     * Returns the paint used to color the median and average markers.
     * 
     * @return A paint.
     */
    public Paint getArtifactPaint() {
        return this.artifactPaint;
    }

    /**
     * Sets the paint used to color the median and average markers.
     * 
     * @param paint  the paint.
     */
    public void setArtifactPaint(Paint paint) {
        this.artifactPaint = paint;
    }

    /**
     * Returns the box width.
     * 
     * @return The box width.
     */
    public double getBoxWidth() {
        return this.boxWidth;
    }

    /**
     * Sets the box width.
     * 
     * @param width  the width.
     */
    public void setBoxWidth(double width) {
        this.boxWidth = width;
    }

    /**
     * Returns the item margin.  This is a percentage of the available space that is allocated
     * to the space between items in the chart.
     * 
     * @return The margin.
     */
    public double getItemMargin() {
        return this.itemMargin;
    }

    /**
     * Sets the item margin.
     * 
     * @param margin  the margin.
     */
    public void setItemMargin(double margin) {
        this.itemMargin = margin;
    }

    /**
     * Initialises the renderer.
     * <p>
     * This method gets called once at the start of the process of drawing a chart.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area in which the data is to be plotted.
     * @param plot  the plot.
     * @param index  the renderer index (<code>null</code> for primary index).
     * @param info  collects chart rendering information for return to caller.
     *
     * @return The renderer state.
     */
    public CategoryItemRendererState initialise(Graphics2D g2,
                                                Rectangle2D dataArea,
                                                CategoryPlot plot,
                                                Integer index,
                                                PlotRenderingInfo info) {

        CategoryItemRendererState state = super.initialise(g2, dataArea, plot, index, info);

        // calculate the box width
        CategoryAxis domainAxis = getDomainAxis(plot, index);
        
        CategoryDataset dataset = getDataset(plot, index);
        if (dataset != null) {
            int columns = dataset.getColumnCount();
            int rows = dataset.getRowCount();
            double space = 0.0;
            PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                space = dataArea.getHeight();
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                space = dataArea.getWidth();
            }
            double categoryMargin = 0.0;
            double currentItemMargin = 0.0;
            if (columns > 1) {
                categoryMargin = domainAxis.getCategoryMargin();
            }
            if (rows > 1) {
                currentItemMargin = getItemMargin();
            }
            double used = space * (1 - domainAxis.getLowerMargin() - domainAxis.getUpperMargin()
                                     - categoryMargin - currentItemMargin);
            if ((rows * columns) > 0) {
                setBoxWidth(used / (dataset.getColumnCount() * dataset.getRowCount()));
            }
            else {
                setBoxWidth(used);
            }
        }
        
        return state;

    }

    /**
     * Draw a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area in which the data is drawn.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the data.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    public void drawItem(Graphics2D g2,
                         CategoryItemRendererState state,
                         Rectangle2D dataArea,
                         CategoryPlot plot,
                         CategoryAxis domainAxis,
                         ValueAxis rangeAxis,
                         CategoryDataset dataset,
                         int row,
                         int column) {
                             
        if (!(dataset instanceof BoxAndWhiskerCategoryDataset)) {
            throw new IllegalArgumentException("BoxAndWhiskerRenderer.drawItem()"
                + " : the data should be of type BoxAndWhiskerCategoryDataset only.");
        }

        PlotOrientation orientation = plot.getOrientation();

        if (orientation == PlotOrientation.HORIZONTAL) {
            drawHorizontalItem(g2, dataArea, plot, domainAxis, rangeAxis, dataset, row, column);
        } 
        else if (orientation == PlotOrientation.VERTICAL) {
            drawVerticalItem(g2, dataArea, plot, domainAxis, rangeAxis, dataset, row, column);
        }
        
    }

    /**
     * Draws the visual representation of a single data item when the plot has a horizontal
     * orientation.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area within which the plot is being drawn.
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    public void drawHorizontalItem(Graphics2D g2,
                                     Rectangle2D dataArea,
                                     CategoryPlot plot,
                                     CategoryAxis domainAxis,
                                     ValueAxis rangeAxis,
                                     CategoryDataset dataset,
                                     int row,
                                     int column) {

        BoxAndWhiskerCategoryDataset bawDataset = (BoxAndWhiskerCategoryDataset) dataset;

        double categoryEnd = domainAxis.getCategoryEnd(
            column, getColumnCount(), dataArea, plot.getDomainAxisEdge());
        double categoryStart = domainAxis.getCategoryStart(
            column, getColumnCount(), dataArea, plot.getDomainAxisEdge());
        double categoryWidth = Math.abs(categoryEnd - categoryStart);

        double yy = categoryStart;
        int seriesCount = getRowCount();
        int categoryCount = getColumnCount();

        if (seriesCount > 1) {
            double seriesGap = dataArea.getWidth() * getItemMargin()
                               / (categoryCount * (seriesCount - 1));
            double usedWidth = (getBoxWidth() * seriesCount) + (seriesGap * (seriesCount - 1));
            // offset the start of the boxes if the total width used is smaller
            // than the category width
            double offset = (categoryWidth - usedWidth) / 2;
            yy = yy + offset + (row * (getBoxWidth() + seriesGap));
        } 
        else {
            // offset the start of the box if the box width is smaller than the category width
            double offset = (categoryWidth - this.boxWidth) / 2;
            yy = yy + offset;
        }

        Paint p = this.getItemPaint(row, column);
        if (p != null) {
            g2.setPaint(p);
        }
        Stroke s = getItemStroke(row, column);
        g2.setStroke(s);

        RectangleEdge location = plot.getRangeAxisEdge();

        Number xQ1 = bawDataset.getQ1Value(row, column);
        Number xQ3 = bawDataset.getQ3Value(row, column);
        Number xMax = bawDataset.getMaxRegularValue(row, column);
        Number xMin = bawDataset.getMinRegularValue(row, column);

        if (xQ1 != null && xQ3 != null && xMax != null && xMin != null) {

            double xxQ1 = rangeAxis.valueToJava2D(xQ1.doubleValue(), dataArea, location);
            double xxQ3 = rangeAxis.valueToJava2D(xQ3.doubleValue(), dataArea, location);
            double xxMax = rangeAxis.valueToJava2D(xMax.doubleValue(), dataArea, location);
            double xxMin = rangeAxis.valueToJava2D(xMin.doubleValue(), dataArea, location);
            // draw the upper shadow...
            if ((xxMax > xxQ1) && (xxMax > xxQ3)) {          // 0,0 = top left...
                g2.draw(new Line2D.Double(xxMax, yy + this.boxWidth / 2,
                                          Math.max(xxQ1, xxQ3), yy + this.boxWidth / 2));
                g2.draw(new Line2D.Double(xxMax, yy,
                                          xxMax, yy + this.boxWidth));
            }

            // draw the lower shadow...
            if ((xxMin < xxQ1) && (xxMin < xxQ3)) {
                g2.draw(new Line2D.Double(xxMin, yy + this.boxWidth / 2,
                                          Math.min(xxQ1, xxQ3), yy + this.boxWidth / 2));
                g2.draw(new Line2D.Double(xxMin, yy,
                                          xxMin, yy + this.boxWidth));
            }

            // draw the body...
            Shape body = new Rectangle2D.Double(Math.min(xxQ1, xxQ3), yy,
                                                Math.abs(xxQ1 - xxQ3), this.boxWidth);
            g2.fill(body);
            g2.draw(body);

        }

        g2.setPaint(this.artifactPaint);
        double aRadius = 0;                 // average radius

        // draw mean - SPECIAL AIMS REQUIREMENT...
        Number xMean = bawDataset.getMeanValue(row, column);
        if (xMean != null) {
            double xxMean = rangeAxis.valueToJava2D(xMean.doubleValue(), dataArea, location);
            aRadius = this.boxWidth / 4;
            Ellipse2D.Double avgEllipse = new Ellipse2D.Double(xxMean - aRadius, yy + aRadius,
                                                               aRadius * 2, aRadius * 2);
            g2.fill(avgEllipse);
            g2.draw(avgEllipse);
        }

        // draw median...
        Number xMedian = bawDataset.getMedianValue(row, column);
        if (xMedian != null) {
            double xxMedian = rangeAxis.valueToJava2D(xMedian.doubleValue(), dataArea, location);
            g2.draw(new Line2D.Double(xxMedian, yy, xxMedian, yy + this.boxWidth));
        }

    } 
        
    /**
     * Draws the visual representation of a single data item when the plot has a vertical
     * orientation.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area within which the plot is being drawn.
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    public void drawVerticalItem(Graphics2D g2, 
                                 Rectangle2D dataArea,
                                 CategoryPlot plot, 
                                 CategoryAxis domainAxis, 
                                 ValueAxis rangeAxis,
                                 CategoryDataset dataset, 
                                 int row, 
                                 int column) {

        BoxAndWhiskerCategoryDataset bawDataset = (BoxAndWhiskerCategoryDataset) dataset;
        
        double categoryEnd = domainAxis.getCategoryEnd(
            column, getColumnCount(), dataArea, plot.getDomainAxisEdge()
        );
        double categoryStart = domainAxis.getCategoryStart(
            column, getColumnCount(), dataArea, plot.getDomainAxisEdge()
        );
        double categoryWidth = categoryEnd - categoryStart;

        double xx = categoryStart;
        int seriesCount = getRowCount();
        int categoryCount = getColumnCount();

        if (seriesCount > 1) {
            double seriesGap = dataArea.getWidth() * getItemMargin()
                               / (categoryCount * (seriesCount - 1));
            double usedWidth = (getBoxWidth() * seriesCount) + (seriesGap * (seriesCount - 1));
            // offset the start of the boxes if the total width used is smaller than 
            // the category width
            double offset = (categoryWidth - usedWidth) / 2;
            xx = xx + offset + (row * (getBoxWidth() + seriesGap));
        } 
        else {
            // offset the start of the box if the box width is smaller than the category width
            double offset = (categoryWidth - this.boxWidth) / 2;
            xx = xx + offset;
        } 
        
        double yyAverage = 0.0;
        double yyOutlier;

        Paint p = this.getItemPaint(row, column);
        if (p != null) {
            g2.setPaint(p);
        }
        Stroke s = getItemStroke(row, column);
        g2.setStroke(s);

        double aRadius = 0;                 // average radius

        RectangleEdge location = plot.getRangeAxisEdge();

        Number yQ1 = bawDataset.getQ1Value(row, column);
        Number yQ3 = bawDataset.getQ3Value(row, column);
        Number yMax = bawDataset.getMaxRegularValue(row, column);
        Number yMin = bawDataset.getMinRegularValue(row, column);
        if (yQ1 != null && yQ3 != null && yMax != null && yMin != null) {

            // draw the upper shadow...
            double yyQ1 = rangeAxis.valueToJava2D(yQ1.doubleValue(), dataArea, location);
            double yyQ3 = rangeAxis.valueToJava2D(yQ3.doubleValue(), dataArea, location);
            double yyMax = rangeAxis.valueToJava2D(yMax.doubleValue(), dataArea, location);
            double yyMin = rangeAxis.valueToJava2D(yMin.doubleValue(), dataArea, location);
            if ((yyMax < yyQ1) && (yyMax < yyQ3)) {          // 0,0 = top left...
                g2.draw(new Line2D.Double(xx + this.boxWidth / 2, yyMax, 
                                          xx + this.boxWidth / 2, Math.max(yyQ1, yyQ3)));
                g2.draw(new Line2D.Double(xx, yyMax, 
                                          xx + this.boxWidth, yyMax));
            }

            // draw the lower shadow...
            if ((yyMin > yyQ1) && (yyMin > yyQ3)) {
                g2.draw(new Line2D.Double(xx + this.boxWidth / 2, yyMin, 
                                          xx + this.boxWidth / 2, Math.min(yyQ1, yyQ3)));
                g2.draw(new Line2D.Double(xx, yyMin, 
                                          xx + this.boxWidth, yyMin));
            }

            // draw the body...
            Shape body = new Rectangle2D.Double(xx, Math.min(yyQ1, yyQ3), 
                                                this.boxWidth, Math.abs(yyQ1 - yyQ3));
            g2.fill(body);
            g2.draw(body);
  
        }
        
        g2.setPaint(this.artifactPaint);

        // draw mean - SPECIAL AIMS REQUIREMENT...
        Number yMean = bawDataset.getMeanValue(row, column);
        if (yMean != null) {
            yyAverage = rangeAxis.valueToJava2D(yMean.doubleValue(), dataArea, location);
            aRadius = this.boxWidth / 4;
            Ellipse2D.Double avgEllipse = new Ellipse2D.Double(xx + aRadius, yyAverage - aRadius, 
                                                               aRadius * 2, aRadius * 2);
            g2.fill(avgEllipse);
            g2.draw(avgEllipse);
        }

        // draw median...
        Number yMedian = bawDataset.getMedianValue(row, column);
        if (yMedian != null) {
            double yyMedian = rangeAxis.valueToJava2D(yMedian.doubleValue(), dataArea, location);
            g2.draw(new Line2D.Double(xx, yyMedian, xx + this.boxWidth, yyMedian));
        }
        
        // draw yOutliers...
        double maxAxisValue = rangeAxis.valueToJava2D(rangeAxis.getUpperBound(), dataArea, location)
                              + aRadius;
        double minAxisValue = rangeAxis.valueToJava2D(rangeAxis.getLowerBound(), dataArea, location)
                              - aRadius;

        g2.setPaint(p);

        // draw outliers
        double oRadius = this.boxWidth / 3;    // outlier radius
        List outliers = new ArrayList();
        OutlierListCollection outlierListCollection = new OutlierListCollection();

        // From outlier array sort out which are outliers and put these into a list
        // If there are any farouts, set the flag on the OutlierListCollection
        List yOutliers = bawDataset.getOutliers(row, column);
        if (yOutliers != null) {
            for (int i = 0; i < yOutliers.size(); i++) {
                double outlier = ((Number) yOutliers.get(i)).doubleValue();
                if (outlier > bawDataset.getMaxOutlier(row, column).doubleValue()) {
                    outlierListCollection.setHighFarOut(true);
                } 
                else if (outlier < bawDataset.getMinOutlier(row, column).doubleValue()) {
                    outlierListCollection.setLowFarOut(true);
                }
                else if (outlier > bawDataset.getMaxRegularValue(row, column).doubleValue()) {
                    yyOutlier = rangeAxis.valueToJava2D(outlier, dataArea, location);
                    outliers.add(new Outlier(xx + this.boxWidth / 2.0, yyOutlier, oRadius));
                }
                else if (outlier < bawDataset.getMinRegularValue(row, column).doubleValue()) {
                    yyOutlier = rangeAxis.valueToJava2D(outlier, dataArea, location);
                    outliers.add(new Outlier(xx + this.boxWidth / 2.0, yyOutlier, oRadius));
                }
                Collections.sort(outliers);
            }

            // Process outliers. Each outlier is either added to the appropriate outlier list
            // or a new outlier list is made
            for (Iterator iterator = outliers.iterator(); iterator.hasNext();) {
                Outlier outlier = (Outlier) iterator.next();
                outlierListCollection.add(outlier);
            }

            for (Iterator iterator = outlierListCollection.iterator(); iterator.hasNext();) {
                OutlierList list = (OutlierList) iterator.next();
                Outlier outlier = list.getAveragedOutlier();
                Point2D point = outlier.getPoint();

                if (list.isMultiple()) {
                    drawMultipleEllipse(point, this.boxWidth, oRadius, g2);
                } 
                else {
                    drawEllipse(point, oRadius, g2);
                }
            }

            // draw farout indicators
            if (outlierListCollection.isHighFarOut()) {
                drawHighFarOut(aRadius / 2.0, g2, xx + this.boxWidth / 2.0, maxAxisValue);
            }
        
            if (outlierListCollection.isLowFarOut()) {
                drawLowFarOut(aRadius / 2.0, g2, xx + this.boxWidth / 2.0, minAxisValue);
            }
        }

    }

    /**
     * Draws a dot to represent an outlier. 
     * 
     * @param point  the location.
     * @param oRadius  the radius.
     * @param g2  the graphics device.
     */
    private void drawEllipse(Point2D point, double oRadius, Graphics2D g2) {
        Ellipse2D dot = new Ellipse2D.Double(
            point.getX() + oRadius / 2, point.getY(), oRadius, oRadius
        );
        g2.draw(dot);
    }

    /**
     * Draws two dots to represent the average value of more than one outlier.
     * 
     * @param point  the location
     * @param boxWidth  the box width.
     * @param oRadius  the radius.
     * @param g2  the graphics device.
     */
    private void drawMultipleEllipse(Point2D point, double boxWidth, double oRadius, 
                                     Graphics2D g2)  {
                                         
        Ellipse2D dot1 = new Ellipse2D.Double(point.getX() - (boxWidth / 2) + oRadius, point.getY(),
                                              oRadius, oRadius);
        Ellipse2D dot2 = new Ellipse2D.Double(point.getX() + (boxWidth / 2), point.getY(),
                                              oRadius, oRadius);
        g2.draw(dot1);
        g2.draw(dot2);
    }

    /**
     * Draws a triangle to indicate the presence of far-out values.
     * 
     * @param aRadius  the radius.
     * @param g2  the graphics device.
     * @param xx  the x coordinate.
     * @param m  the y coordinate.
     */
    private void drawHighFarOut(double aRadius, Graphics2D g2, double xx, double m) {
        double side = aRadius * 2;
        g2.draw(new Line2D.Double(xx - side, m + side, xx + side, m + side));
        g2.draw(new Line2D.Double(xx - side, m + side, xx, m));
        g2.draw(new Line2D.Double(xx + side, m + side, xx, m));
    }

    /**
     * Draws a triangle to indicate the presence of far-out values.
     * 
     * @param aRadius  the radius.
     * @param g2  the graphics device.
     * @param xx  the x coordinate.
     * @param m  the y coordinate.
     */
    private void drawLowFarOut(double aRadius, Graphics2D g2, double xx, double m) {
        double side = aRadius * 2;
        g2.draw(new Line2D.Double(xx - side, m - side, xx + side, m - side));
        g2.draw(new Line2D.Double(xx - side, m - side, xx, m));
        g2.draw(new Line2D.Double(xx + side, m - side, xx, m));
    }
}
