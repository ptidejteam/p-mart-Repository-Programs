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
 * ---------------------------
 * StackedXYAreaRenderer2.java
 * ---------------------------
 * (C) Copyright 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited), based on 
 *                   the StackedXYAreaRenderer class by Richard Atkinson;
 * Contributor(s):   -;
 *
 * $Id: StackedXYAreaRenderer2.java,v 1.1 2007/10/10 19:34:42 vauchers Exp $
 *
 * Changes:
 * --------
 * 30-Apr-2004 : Version 1 (DG);
 *
 */

package org.jfree.chart.renderer;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.Range;
import org.jfree.data.TableXYDataset;
import org.jfree.data.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

/**
 * A stacked area renderer for the {@link XYPlot} class.
 */
public class StackedXYAreaRenderer2 extends XYAreaRenderer2 
                                    implements Cloneable, 
                                               PublicCloneable,
                                               Serializable {

    /**
     * Creates a new renderer.
     */
    public StackedXYAreaRenderer2() {
        this(null, null);
    }

    /**
     * Constructs a new renderer.
     *
     * @param labelGenerator  the tool tip generator to use.  <code>null</code> is none.
     * @param urlGenerator  the URL generator (null permitted).
     */
    public StackedXYAreaRenderer2(XYToolTipGenerator labelGenerator, 
                                  XYURLGenerator urlGenerator) {
        super(labelGenerator, urlGenerator);
    }

    /**
     * Returns the range type.
     *
     * @return The range type (never <code>null</code>).
     */
    public RangeType getRangeType() {
        return RangeType.STACKED;
    }

    /**
     * Returns the range of values the renderer requires to display all the items from the
     * specified dataset.
     * 
     * @param dataset  the dataset (<code>null</code> permitted).
     * 
     * @return The range (or <code>null</code> if the dataset is <code>null</code> or empty).
     */
    public Range getRangeExtent(XYDataset dataset) {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        TableXYDataset d = (TableXYDataset) dataset;
        int itemCount = d.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            double[] stackValues = getStackValues(dataset, d.getSeriesCount(), i);
            min = Math.min(min, stackValues[0]);
            max = Math.max(max, stackValues[1]);
        }
        return new Range(min, max);
    }

    /**
     * Returns the number of passes required by the renderer.
     * 
     * @return 1.
     */
    public int getPassCount() {
        return 1;
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the data is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  information about crosshairs on a plot.
     * @param pass  the pass index.
     */
    public void drawItem(Graphics2D g2,
                         XYItemRendererState state,
                         Rectangle2D dataArea,
                         PlotRenderingInfo info,
                         XYPlot plot,
                         ValueAxis domainAxis,
                         ValueAxis rangeAxis,
                         XYDataset dataset,
                         int series,
                         int item,
                         CrosshairState crosshairState,
                         int pass) {

        // get the data point...
        Number x1n = dataset.getXValue(series, item);
        Number y1n = dataset.getYValue(series, item);
        if (y1n == null) {
            y1n = AbstractRenderer.ZERO;
        }        
        double x1 = x1n.doubleValue();
        double y1 = y1n.doubleValue();
        double[] stack1 = getStackValues(dataset, series, item);
        
        // get the previous point and the next point so we can calculate a "hot spot"
        // for the area (used by the chart entity)...
        Number x0n = dataset.getXValue(series, Math.max(item - 1, 0));
        Number y0n = dataset.getYValue(series, Math.max(item - 1, 0));
        if (y0n == null) {
            y0n = AbstractRenderer.ZERO;
        }
        double x0 = x0n.doubleValue();
        double y0 = y0n.doubleValue();
        double[] stack0 = getStackValues(dataset, series, Math.max(item - 1, 0));
        
        int itemCount = dataset.getItemCount(series);
        Number x2n = dataset.getXValue(series, Math.min(item + 1, itemCount - 1));
        Number y2n = dataset.getYValue(series, Math.min(item + 1, itemCount - 1));
        if (y2n == null) {
            y2n = AbstractRenderer.ZERO;
        }
        double x2 = x2n.doubleValue();
        double y2 = y2n.doubleValue();
        double[] stack2 = getStackValues(dataset, series, Math.min(item + 1, itemCount - 1));

        double xleft = (x0 + x1) / 2.0;
        double xright = (x1 + x2) / 2.0;
        double[] stackLeft = averageStackValues(stack0, stack1);
        double[] stackRight = averageStackValues(stack1, stack2);
        double[] adjStackLeft = adjustedStackValues(stack0, stack1);
        double[] adjStackRight = adjustedStackValues(stack1, stack2);
        
        RectangleEdge edge0 = plot.getDomainAxisEdge();
        float transX1 = (float) domainAxis.valueToJava2D(x1, dataArea, edge0);
        float transXLeft = (float) domainAxis.valueToJava2D(xleft, dataArea, edge0);
        float transXRight = (float) domainAxis.valueToJava2D(xright, dataArea, edge0);
        
        RectangleEdge edge1 = plot.getRangeAxisEdge();
        
        GeneralPath left = new GeneralPath();
        GeneralPath right = new GeneralPath();
        if (y1 >= 0.0) {  // handle positive value
            float transY1 = (float) rangeAxis.valueToJava2D(y1 + stack1[1], dataArea, edge1);
            float transStack1 = (float) rangeAxis.valueToJava2D(stack1[1], dataArea, edge1);
            //float transStackLeft = (float) rangeAxis.valueToJava2D(stackLeft[1], dataArea, edge1);
            float transStackLeft = (float) rangeAxis.valueToJava2D(
                adjStackLeft[1], dataArea, edge1
            );
            
            // LEFT POLYGON
            if (y0 >= 0.0) {
                double yleft = (y0 + y1) / 2.0 + stackLeft[1];
                float transYLeft = (float) rangeAxis.valueToJava2D(yleft, dataArea, edge1);
                left.moveTo(transX1, transY1);
                left.lineTo(transX1, transStack1);
                left.lineTo(transXLeft, transStackLeft);
                left.lineTo(transXLeft, transYLeft);
                left.closePath();
            }
            else {
                left.moveTo(transX1, transStack1);
                left.lineTo(transX1, transY1);
                left.lineTo(transXLeft, transStackLeft);
                left.closePath();
            }

            float transStackRight = (float) rangeAxis.valueToJava2D(
                adjStackRight[1], dataArea, edge1
            );
            // RIGHT POLYGON
            if (y2 >= 0.0) {
                double yright = (y1 + y2) / 2.0 + stackRight[1];
                float transYRight = (float) rangeAxis.valueToJava2D(yright, dataArea, edge1);
                right.moveTo(transX1, transStack1);
                right.lineTo(transX1, transY1);
                right.lineTo(transXRight, transYRight);
                right.lineTo(transXRight, transStackRight);
                right.closePath();
            }
            else {
                right.moveTo(transX1, transStack1);
                right.lineTo(transX1, transY1);
                right.lineTo(transXRight, transStackRight);
                right.closePath();
            }
        }
        else {  // handle negative value 
            float transY1 = (float) rangeAxis.valueToJava2D(y1 + stack1[0], dataArea, edge1);
            float transStack1 = (float) rangeAxis.valueToJava2D(stack1[0], dataArea, edge1);
            float transStackLeft = (float) rangeAxis.valueToJava2D(
                adjStackLeft[0], dataArea, edge1
            );

            // LEFT POLYGON
            if (y0 >= 0.0) {
                left.moveTo(transX1, transStack1);
                left.lineTo(transX1, transY1);
                left.lineTo(transXLeft, transStackLeft);
                left.clone();
            }
            else {
                double yleft = (y0 + y1) / 2.0 + stackLeft[0];
                float transYLeft = (float) rangeAxis.valueToJava2D(yleft, dataArea, edge1);
                left.moveTo(transX1, transY1);
                left.lineTo(transX1, transStack1);
                left.lineTo(transXLeft, transStackLeft);
                left.lineTo(transXLeft, transYLeft);
                left.closePath();
            }
            float transStackRight = (float) rangeAxis.valueToJava2D(
                adjStackRight[0], dataArea, edge1
            );
            
            // RIGHT POLYGON
            if (y2 >= 0.0) {
                right.moveTo(transX1, transStack1);
                right.lineTo(transX1, transY1);
                right.lineTo(transXRight, transStackRight);
                right.closePath();
            }
            else {
                double yright = (y1 + y2) / 2.0 + stackRight[0];
                float transYRight = (float) rangeAxis.valueToJava2D(yright, dataArea, edge1);
                right.moveTo(transX1, transStack1);
                right.lineTo(transX1, transY1);
                right.lineTo(transXRight, transYRight);
                right.lineTo(transXRight, transStackRight);
                right.closePath();
            }
        }

        //  Get series Paint and Stroke
        Paint itemPaint = getItemPaint(series, item);

        if (pass == 0) {

            g2.setPaint(itemPaint);
            g2.fill(left);
            g2.fill(right);

        } 

    }

    /**
     * Calculates the stacked value of the all series up to, but not including <code>series</code>
     * for the specified category, <code>category</code>.  It returns 0.0 if <code>series</code>
     * is the first series, i.e. 0.
     *
     * @param dataset  the data.
     * @param series  the series.
     * @param index  the index.
     *
     * @return double returns a cumulative value for all series' values up to
     * but excluding <code>series</code> for <code>index</code>.
     */
    private double[] getStackValues(XYDataset dataset, int series, int index) {
        double[] result = new double[2];
        for (int i = 0; i < series; i++) {
            Number n = dataset.getYValue(i, index);
            if (n != null) {
                double v = n.doubleValue();
                if (v >= 0.0) {
                    result[1] += v;   
                }
                else {
                    result[0] += v;   
                }
            }
        }
        return result;
    }
    
    /**
     * Returns a pair of "stack" values calculated from the two specified pairs.
     * 
     * @param stack1  the first stack pair.
     * @param stack2  the second stack pair.
     * 
     * @return A pair of average stack values.
     */
    private double[] averageStackValues(double[] stack1, double[] stack2) {
        double[] result = new double[2];
        result[0] = (stack1[0] + stack2[0]) / 2.0;
        result[1] = (stack1[1] + stack2[1]) / 2.0;
        return result;
    }

    /**
     * Returns a pair of "stack" values calculated from the two specified pairs.
     * 
     * @param stack1  the first stack pair.
     * @param stack2  the second stack pair.
     * 
     * @return A pair of average stack values.
     */
    private double[] adjustedStackValues(double[] stack1, double[] stack2) {
        double[] result = new double[2];
        if (stack1[0] == 0.0 || stack2[0] == 0.0) {
            result[0] = 0.0;   
        }
        else {
            result[0] = (stack1[0] + stack2[0]) / 2.0;
        }
        if (stack1[1] == 0.0 || stack2[1] == 0.0) {
            result[1] = 0.0;   
        }
        else {
            result[1] = (stack1[1] + stack2[1]) / 2.0;
        }
        return result;
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
