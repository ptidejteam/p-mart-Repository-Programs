/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * FastScatterPlot.java
 * --------------------
 * (C) Copyright 2002, 2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: FastScatterPlot.java,v 1.1 2007/10/10 20:03:24 vauchers Exp $
 *
 * Changes (from 29-Oct-2002)
 * --------------------------
 * 29-Oct-2002 : Added standard header (DG);
 * 07-Nov-2002 : Fixed errors reported by Checkstyle (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 *
 */

package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.CrosshairInfo;
import org.jfree.chart.Marker;
import org.jfree.chart.annotations.Annotation;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.axis.HorizontalAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.VerticalAxis;
import org.jfree.data.Range;

/**
 * A fast scatter plot.
 *
 * @author David Gilbert
 */
public class FastScatterPlot extends Plot 
                             implements HorizontalValuePlot, VerticalValuePlot, Serializable {

    /** The data. */
    private float[][] data;

    /** The horizontal data range. */
    private Range horizontalDataRange;

    /** The vertical data range. */
    private Range verticalDataRange;

    /** The domain axis (used for the x-values). */
    private ValueAxis domainAxis;

    /** The range axis (used for the y-values). */
    private ValueAxis rangeAxis;

    /** A list of markers (optional) for the domain axis. */
    private List domainMarkers;

    /** A list of markers (optional) for the range axis. */
    private List rangeMarkers;

    /** A list of annotations (optional) for the plot. */
    private List annotations;

    /**
     * Creates a new fast scatter plot.
     * <P>
     * The data is an array of x, y values:  data[0][i] = x, data[1][i] = y.
     *
     * @param data  the data.
     * @param domainAxis  the domain (x) axis.
     * @param rangeAxis  the range (y) axis.
     */
    public FastScatterPlot(float[][] data, ValueAxis domainAxis, ValueAxis rangeAxis) {

        super(null);

        this.data = data;
        this.horizontalDataRange = calculateHorizontalDataRange(data);
        this.verticalDataRange = calculateVerticalDataRange(data);
        this.domainAxis = domainAxis;
        if (domainAxis != null) {
            domainAxis.setPlot(this);
            domainAxis.addChangeListener(this);
        }

        this.rangeAxis = rangeAxis;
        if (rangeAxis != null) {
            rangeAxis.setPlot(this);
            rangeAxis.addChangeListener(this);
        }

    }

    /**
     * Returns the domain axis for the plot.  If the domain axis for this plot
     * is null, then the method will return the parent plot's domain axis (if
     * there is a parent plot).
     *
     * @return the domain axis.
     */
    public ValueAxis getDomainAxis() {

        return this.domainAxis;

    }

    /**
     * Returns the range axis for the plot.  If the range axis for this plot is
     * null, then the method will return the parent plot's range axis (if
     * there is a parent plot).
     *
     * @return the range axis.
     */
    public ValueAxis getRangeAxis() {

        return this.rangeAxis;

    }

    /**
     * Draws the fast scatter plot on a Java 2D graphics device (such as the screen or
     * a printer).
     *
     * @param g2  the graphics device.
     * @param plotArea   the area within which the plot (including axis labels)
     *                   should be drawn.
     * @param info  collects chart drawing information (null permitted).
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, ChartRenderingInfo info) {

        // set up info collection...
        if (info != null) {
            info.setPlotArea(plotArea);

        }

        // adjust the drawing area for plot insets (if any)...
        Insets insets = getInsets();
        if (insets != null) {
            plotArea.setRect(plotArea.getX() + insets.left,
                             plotArea.getY() + insets.top,
                             plotArea.getWidth() - insets.left - insets.right,
                             plotArea.getHeight() - insets.top - insets.bottom);
        }

        // estimate the area required for drawing the axes...
        double hAxisAreaHeight = 0;

        if (this.domainAxis != null) {
            HorizontalAxis hAxis = (HorizontalAxis) this.domainAxis;
            hAxisAreaHeight = hAxis.reserveHeight(g2, this, plotArea, BOTTOM);
        }

        double vAxisWidth = 0;
        if (this.rangeAxis != null) {
            VerticalAxis vAxis = (VerticalAxis) this.rangeAxis;
            vAxisWidth = vAxis.reserveWidth(g2, this, plotArea, LEFT, hAxisAreaHeight, BOTTOM);
        }

        // ...and therefore what is left for the plot itself...
        Rectangle2D dataArea = new Rectangle2D.Double(plotArea.getX() + vAxisWidth,
                                                      plotArea.getY(),
                                                      plotArea.getWidth() - vAxisWidth,
                                                      plotArea.getHeight() - hAxisAreaHeight);

        if (info != null) {
            info.setDataArea(dataArea);
        }

        // draw the plot background and axes...
        drawBackground(g2, dataArea);

        if (this.domainAxis != null) {
            this.domainAxis.draw(g2, plotArea, dataArea, BOTTOM);
        }
        if (this.rangeAxis != null) {
            this.rangeAxis.draw(g2, plotArea, dataArea, LEFT);
        }

        Shape originalClip = g2.getClip();
        Composite originalComposite = g2.getComposite();

        g2.clip(dataArea);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                   getForegroundAlpha()));

        if (this.domainMarkers != null) {
            Iterator iterator = this.domainMarkers.iterator();
            while (iterator.hasNext()) {
                Marker marker = (Marker) iterator.next();
                //renderer.drawDomainMarker(g2, this, getDomainAxis(), marker, dataArea);
            }
        }

        if (this.rangeMarkers != null) {
            Iterator iterator = this.rangeMarkers.iterator();
            while (iterator.hasNext()) {
                Marker marker = (Marker) iterator.next();
                //renderer.drawRangeMarker(g2, this, getRangeAxis(), marker, dataArea);
            }
        }

        render(g2, dataArea, info, null);

        // draw the annotations...
        if (this.annotations != null) {
            Iterator iterator = this.annotations.iterator();
            while (iterator.hasNext()) {
                Annotation annotation = (Annotation) iterator.next();
                if (annotation instanceof XYAnnotation) {
                    XYAnnotation xya = (XYAnnotation) annotation;
                    // get the annotation to draw itself...
                    xya.draw(g2, dataArea, getDomainAxis(), getRangeAxis());
                }
            }
        }

        g2.setClip(originalClip);
        g2.setComposite(originalComposite);
        drawOutline(g2, dataArea);

    }

    /**
     * Draws a representation of the data within the dataArea region.
     * <P>
     * The <code>info</code> and <code>crosshairInfo</code> arguments may be <code>null</code>.
     *
     * @param g2  the graphics device.
     * @param dataArea  the region in which the data is to be drawn.
     * @param info  an optional object for collection dimension information.
     * @param crosshairInfo  an optional object for collecting crosshair info.
     */
    public void render(Graphics2D g2, Rectangle2D dataArea,
                       ChartRenderingInfo info, CrosshairInfo crosshairInfo) {

        g2.setPaint(Color.red);
        if (this.data != null) {

            ValueAxis domainAxis = getDomainAxis();
            ValueAxis rangeAxis = getRangeAxis();
            for (int i = 0; i < data[0].length; i++) {
                float x = data[0][i];
                float y = data[1][i];
                int transX = (int) domainAxis.translateValueToJava2D(x, dataArea);
                int transY = (int) rangeAxis.translateValueToJava2D(y, dataArea);
                g2.fillRect(transX, transY, 1, 1);
            }


        }

    }

    /**
     * Returns a short string describing the plot type.
     *
     * @return a short string describing the plot type.
     */
    public String getPlotType() {
        return "Fast Scatter Plot";
    }

    /**
     * Returns the range of data values to be plotted along the horizontal axis.
     *
     * @param axis  the axis.
     * 
     * @return  the range.
     */
    public Range getHorizontalDataRange(ValueAxis axis) {
        return this.horizontalDataRange;
    }

    /**
     * Get the value axis.
     *
     * @return  the value axis.
     */
    public ValueAxis getHorizontalValueAxis() {
        return getDomainAxis();
    }

    /**
     * Returns the range for the data to be plotted against the vertical axis.
     *
     * @param axis  the axis.
     * 
     * @return the range.
     */
    public Range getVerticalDataRange(ValueAxis axis) {
        return this.verticalDataRange;
    }

    /**
     * Returns the vertical axis.
     *
     * @return the axis.
     */
    public ValueAxis getVerticalValueAxis() {
        return getRangeAxis();
    }

    /**
     * Calculates the horizontal data range.
     *
     * @param data  the data.
     *
     * @return the range.
     */
    private Range calculateHorizontalDataRange(float data[][]) {
        float lowest = Float.POSITIVE_INFINITY;
        float highest = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < data[0].length; i++) {
            float v = data[0][i];
            if (v < lowest) {
                lowest = v;
            }
            if (v > highest) {
                highest = v;
            }
        }
        return new Range(lowest, highest);
    }

    /**
     * Calculates the vertical data range.
     *
     * @param data  the data.
     *
     * @return the range.
     */
    private Range calculateVerticalDataRange(float data[][]) {
        float lowest = Float.POSITIVE_INFINITY;
        float highest = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < data[0].length; i++) {
            float v = data[1][i];
            if (v < lowest) {
                lowest = v;
            }
            if (v > highest) {
                highest = v;
            }
        }
        return new Range(lowest, highest);
    }

}
