/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2007, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * -------------------
 * VectorRenderer.java
 * -------------------
 * (C) Copyright 2007, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: VectorRenderer.java,v 1.1 2007/10/10 20:47:09 vauchers Exp $
 *
 * Changes
 * -------
 * 30-Jan-2007 : Version 1 (DG);
 * 
 */

package org.jfree.experimental.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.data.xy.VectorXYDataset;

/**
 * A renderer that represents data from an {@link VectorXYDataset} by drawing a
 * line with an arrow at each (x, y) point.
 * 
 * WARNING: THIS CLASS IS NOT PART OF THE STANDARD JFREECHART API AND IS 
 * SUBJECT TO ALTERATION OR REMOVAL.  DO NOT RELY ON THIS CLASS FOR 
 * PRODUCTION USE.  Please experiment with this code and provide feedback.
 */
public class VectorRenderer extends AbstractXYItemRenderer 
        implements XYItemRenderer, Cloneable, Serializable {
    
    private double baseLength = 0.10;
    
    private double headLength = 0.14;
    
    
    /**
     * Creates a new <code>XYBlockRenderer</code> instance with default 
     * attributes.
     */
    public VectorRenderer() {
    }
    
    /**
     * Returns the lower and upper bounds (range) of the x-values in the 
     * specified dataset.
     * 
     * @param dataset  the dataset (<code>null</code> permitted).
     * 
     * @return The range (<code>null</code> if the dataset is <code>null</code>
     *         or empty).
     */
    public Range findDomainBounds(XYDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");   
        }
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int seriesCount = dataset.getSeriesCount();
        double lvalue;
        double uvalue;
        if (dataset instanceof VectorXYDataset) {
            VectorXYDataset vdataset = (VectorXYDataset) dataset;
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    double delta = vdataset.getDeltaXValue(series, item);
                    if (delta < 0.0) {
                        uvalue = vdataset.getXValue(series, item);
                        lvalue = uvalue + delta;
                    }
                    else {
                        lvalue = vdataset.getXValue(series, item);
                        uvalue = lvalue + delta;
                    }
                    minimum = Math.min(minimum, lvalue);
                    maximum = Math.max(maximum, uvalue);
                }
            }
        }
        else {
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    lvalue = dataset.getXValue(series, item);
                    uvalue = lvalue;
                    minimum = Math.min(minimum, lvalue);
                    maximum = Math.max(maximum, uvalue);
                }
            }
        }
        if (minimum > maximum) {
            return null;
        }
        else {
            return new Range(minimum, maximum);
        }
    }
    
    /**
     * Returns the range of values the renderer requires to display all the 
     * items from the specified dataset.
     * 
     * @param dataset  the dataset (<code>null</code> permitted).
     * 
     * @return The range (<code>null</code> if the dataset is <code>null</code> 
     *         or empty).
     */
    public Range findRangeBounds(XYDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");   
        }
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int seriesCount = dataset.getSeriesCount();
        double lvalue;
        double uvalue;
        if (dataset instanceof VectorXYDataset) {
            VectorXYDataset vdataset = (VectorXYDataset) dataset;
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    double delta = vdataset.getDeltaYValue(series, item);
                    if (delta < 0.0) {
                        uvalue = vdataset.getYValue(series, item);
                        lvalue = uvalue + delta;
                    }
                    else {
                        lvalue = vdataset.getYValue(series, item);
                        uvalue = lvalue + delta;
                    }
                    minimum = Math.min(minimum, lvalue);
                    maximum = Math.max(maximum, uvalue);
                }
            }
        }
        else {
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    lvalue = dataset.getYValue(series, item);
                    uvalue = lvalue;
                    minimum = Math.min(minimum, lvalue);
                    maximum = Math.max(maximum, uvalue);
                }
            }
        }
        if (minimum > maximum) {
            return null;
        }
        else {
            return new Range(minimum, maximum);
        }
    }
    
    /**
     * Draws the block representing the specified item.
     * 
     * @param g2  the graphics device.
     * @param state  the state.
     * @param dataArea  the data area.
     * @param info  the plot rendering info.
     * @param plot  the plot.
     * @param domainAxis  the x-axis.
     * @param rangeAxis  the y-axis.
     * @param dataset  the dataset.
     * @param series  the series index.
     * @param item  the item index.
     * @param crosshairState  the crosshair state.
     * @param pass  the pass index.
     */
    public void drawItem(Graphics2D g2, XYItemRendererState state, 
            Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, 
            ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, 
            int series, int item, CrosshairState crosshairState, int pass) {
        
        double x = dataset.getXValue(series, item);
        double y = dataset.getYValue(series, item);
        double dx = 0.0;
        double dy = 0.0;
        if (dataset instanceof VectorXYDataset) {
            dx = ((VectorXYDataset) dataset).getDeltaXValue(series, item);
            dy = ((VectorXYDataset) dataset).getDeltaYValue(series, item);
        }
        double xx0 = domainAxis.valueToJava2D(x, dataArea, 
                plot.getDomainAxisEdge());
        double yy0 = rangeAxis.valueToJava2D(y, dataArea, 
                plot.getRangeAxisEdge());
        double xx1 = domainAxis.valueToJava2D(x + dx, dataArea, 
                plot.getDomainAxisEdge());
        double yy1 = rangeAxis.valueToJava2D(y + dy, dataArea, 
                plot.getRangeAxisEdge());
        Line2D line;
        PlotOrientation orientation = plot.getOrientation();
        if (orientation.equals(PlotOrientation.HORIZONTAL)) {
            line = new Line2D.Double(yy0, xx0, yy1, xx1);
        }
        else {
            line = new Line2D.Double(xx0, yy0, xx1, yy1);
        }
        g2.setPaint(getItemPaint(series, item));
        g2.setStroke(getItemStroke(series, item));
        g2.draw(line);
        
        // calculate the arrow head and draw it...
        double dxx = (xx1 - xx0);
        double dyy = (yy1 - yy0);
        double bx = xx0 + (1.0 - this.baseLength) * dxx;
        double by = yy0 + (1.0 - this.baseLength) * dyy;
        
        double cx = xx0 + (1.0 - this.headLength) * dxx;
        double cy = yy0 + (1.0 - this.headLength) * dyy;
 
        double angle = 0.0;
        if (dxx != 0.0) {
            angle = Math.PI / 2.0 - Math.atan(dyy / dxx);
        }
        double deltaX = 2.0 * Math.cos(angle);
        double deltaY = 2.0 * Math.sin(angle);
        
        double leftx = cx + deltaX;
        double lefty = cy - deltaY;
        double rightx = cx - deltaX;
        double righty = cy + deltaY;
        
        GeneralPath p = new GeneralPath();
        p.moveTo((float) xx1, (float) yy1);
        p.lineTo((float) rightx, (float) righty);
        p.lineTo((float) bx, (float) by);
        p.lineTo((float) leftx, (float) lefty);
        p.closePath();
        g2.draw(p);
        
        
    }
    
    /**
     * Tests this <code>VectorRenderer</code> for equality with an arbitrary
     * object.  This method returns <code>true</code> if and only if:
     * <ul>
     * <li><code>obj</code> is an instance of <code>VectorRenderer</code> (not
     *     <code>null</code>);</li>
     * <li><code>obj</code> has the same field values as this 
     *     <code>VectorRenderer</code>;</li>
     * </ul>
     * 
     * @param obj  the object (<code>null</code> permitted).
     * 
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof VectorRenderer)) {
            return false;
        }
        VectorRenderer that = (VectorRenderer) obj;
        if (this.baseLength != that.baseLength) {
            return false;
        }
        if (this.headLength != that.headLength) {
            return false;
        }
        return super.equals(obj);
    }
    
    /**
     * Returns a clone of this renderer.
     * 
     * @return A clone of this renderer.
     * 
     * @throws CloneNotSupportedException if there is a problem creating the 
     *     clone.
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
