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
 * --------------------
 * FastScatterPlot.java
 * --------------------
 * (C) Copyright 2002, 2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Arnaud Lelievre;
 *
 * $Id: FastScatterPlot.java,v 1.1 2007/10/10 19:19:05 vauchers Exp $
 *
 * Changes (from 29-Oct-2002)
 * --------------------------
 * 29-Oct-2002 : Added standard header (DG);
 * 07-Nov-2002 : Fixed errors reported by Checkstyle (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 19-Aug-2003 : Implemented Cloneable (DG);
 * 08-Sep-2003 : Added internationalization via use of properties resourceBundle (RFE 690236) (AL); 
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 12-Nov-2003 : Implemented zooming (DG);
 *
 */

package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ResourceBundle;

import org.jfree.chart.CrosshairInfo;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.data.Range;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ArrayUtils;
import org.jfree.util.ObjectUtils;

/**
 * A fast scatter plot.
 *
 * @author David Gilbert
 */
public class FastScatterPlot extends Plot implements ValueAxisPlot, Cloneable, Serializable {

    /** The data. */
    private float[][] data;

    /** The x data range. */
    private Range xDataRange;

    /** The y data range. */
    private Range yDataRange;

    /** The domain axis (used for the x-values). */
    private ValueAxis domainAxis;

    /** The range axis (used for the y-values). */
    private ValueAxis rangeAxis;

    /** The paint used to plot data points. */
    private transient Paint paint;

    /** The resourceBundle for the localization. */
    static protected ResourceBundle localizationResources = 
                            ResourceBundle.getBundle("org.jfree.chart.plot.LocalizationBundle");

    /**
     * Creates an empty plot.
     */
    public FastScatterPlot() {
        this(null, null, null);    
    }
    
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

        super();

        this.data = data;
        this.xDataRange = calculateXDataRange(data);
        this.yDataRange = calculateYDataRange(data);
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

        this.paint = Color.red;

    }

    /**
     * Returns a short string describing the plot type.
     *
     * @return a short string describing the plot type.
     */
    public String getPlotType() {
        return localizationResources.getString("Fast_Scatter_Plot");
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
     * Returns the paint used to plot data points.
     *
     * @return The paint.
     */
    public Paint getPaint() {
        return this.paint;
    }

    /**
     * Sets the color for the data points.
     *
     * @param paint  the paint.
     */
    public void setPaint(Paint paint) {
        this.paint = paint;
        this.notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Draws the fast scatter plot on a Java 2D graphics device (such as the screen or
     * a printer).
     *
     * @param g2  the graphics device.
     * @param plotArea   the area within which the plot (including axis labels) should be drawn.
     * @param parentState  the state from the parent plot, if there is one.
     * @param info  collects chart drawing information (<code>null</code> permitted).
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, PlotState parentState, 
                     PlotRenderingInfo info) {

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

        AxisSpace space = new AxisSpace();
        space = this.domainAxis.reserveSpace(g2, this, plotArea, RectangleEdge.BOTTOM, space);
        space = this.rangeAxis.reserveSpace(g2, this, plotArea, RectangleEdge.LEFT, space);
        Rectangle2D dataArea = space.shrink(plotArea, null);

        if (info != null) {
            info.setDataArea(dataArea);
        }

        // draw the plot background and axes...
        drawBackground(g2, dataArea);

        double cursor;
        if (this.domainAxis != null) {
            cursor = dataArea.getMaxY();
            AxisState state = this.domainAxis.draw(g2, cursor, plotArea, dataArea, 
                                                   RectangleEdge.BOTTOM);
            cursor = state.getCursor();
        }
        if (this.rangeAxis != null) {
            cursor = dataArea.getMinX();
            AxisState state = this.rangeAxis.draw(g2, cursor, plotArea, dataArea, 
                                                  RectangleEdge.LEFT);
            cursor = state.getCursor();
        }

        Shape originalClip = g2.getClip();
        Composite originalComposite = g2.getComposite();

        g2.clip(dataArea);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                   getForegroundAlpha()));

        render(g2, dataArea, info, null);

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
                       PlotRenderingInfo info, CrosshairInfo crosshairInfo) {
    
 
        //long start = System.currentTimeMillis();
        //System.out.println("Start: " + start);
        g2.setPaint(Color.red);

        // if the axes use a linear scale, you can uncomment the code below and
        // switch to the alternative transX/transY calculation inside the loop that 
        // follows - it is a little bit faster then.
        // 
        // int xx = (int) dataArea.getMinX();
        // int ww = (int) dataArea.getWidth();
        // int yy = (int) dataArea.getMaxY();
        // int hh = (int) dataArea.getHeight();
        // double domainMin = this.domainAxis.getLowerBound();
        // double domainLength = this.domainAxis.getUpperBound() - domainMin;
        // double rangeMin = this.rangeAxis.getLowerBound();
        // double rangeLength = this.rangeAxis.getUpperBound() - rangeMin;

        if (this.data != null) {
            for (int i = 0; i < data[0].length; i++) {
                float x = data[0][i];
                float y = data[1][i];
                
                //int transX = (int) (xx + ww * (x - domainMin) / domainLength); 
                //int transY = (int) (yy - hh * (y - rangeMin) / rangeLength); 
                int transX = (int) this.domainAxis.translateValueToJava2D(x, dataArea,
                                                                          RectangleEdge.BOTTOM);
                int transY = (int) this.rangeAxis.translateValueToJava2D(y, dataArea,
                                                                         RectangleEdge.LEFT);
                g2.fillRect(transX, transY, 1, 1);
            }
        }
        //long finish = System.currentTimeMillis();
        //System.out.println("Finish: " + finish);
        //System.out.println("Time: " + (finish - start));

    }

    /**
     * Returns the range of data values to be plotted along the axis.
     *
     * @param axis  the axis.
     *
     * @return  the range.
     */
    public Range getDataRange(ValueAxis axis) {

        Range result = null;
        if (axis == this.domainAxis) {
            result = this.xDataRange;
        }
        else if (axis == this.rangeAxis) {
            result = this.yDataRange;
        }
        return result;
    }

    /**
     * Calculates the X data range.
     *
     * @param data  the data.
     *
     * @return the range.
     */
    private Range calculateXDataRange(float[][] data) {
        
        Range result = null;
        
        if (data != null) {
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
            if (lowest <= highest) {
                result = new Range(lowest, highest);
            }
        }
        
        return result;
        
    }

    /**
     * Calculates the Y data range.
     *
     * @param data  the data.
     *
     * @return the range.
     */
    private Range calculateYDataRange(float[][] data) {
        
        Range result = null;
        
        if (data != null) {
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
            if (lowest <= highest) {
                result = new Range(lowest, highest);
            }
        }
        return result;
        
    }

    /**
     * Multiplies the range on the horizontal axis/axes by the specified factor (not yet 
     * implemented).
     *
     * @param factor  the zoom factor.
     */
    public void zoomHorizontalAxes(double factor) {
        this.domainAxis.resizeRange(factor);
    }

    /**
     * Zooms in on the horizontal axes (not yet implemented).
     * 
     * @param lowerPercent  the new lower bound as a percentage of the current range.
     * @param upperPercent  the new upper bound as a percentage of the current range.
     */
    public void zoomHorizontalAxes(double lowerPercent, double upperPercent) {
        this.domainAxis.zoomRange(lowerPercent, upperPercent);
    }

    /**
     * Multiplies the range on the vertical axis/axes by the specified factor (not yet implemented).
     *
     * @param factor  the zoom factor.
     */
    public void zoomVerticalAxes(double factor) {
        this.rangeAxis.resizeRange(factor);
    }

    /**
     * Zooms in on the vertical axes (not yet implemented).
     * 
     * @param lowerPercent  the new lower bound as a percentage of the current range.
     * @param upperPercent  the new upper bound as a percentage of the current range.
     */
    public void zoomVerticalAxes(double lowerPercent, double upperPercent) {
        this.rangeAxis.zoomRange(lowerPercent, upperPercent);
    }

    /**
     * Tests an object for equality with this instance.
     * 
     * @param object  the object to test.
     * 
     * @return A boolean.
     */
    public boolean equals(Object object) {
    
        if (object ==  null) {
            return false;    
        }
        
        if (object == this) {
            return true;
        }
        
        if (super.equals(object) && object instanceof FastScatterPlot) {
            FastScatterPlot fsp = (FastScatterPlot) object;
            boolean b0 = ArrayUtils.equal(this.data, fsp.data);
            boolean b1 = ObjectUtils.equal(this.domainAxis, fsp.domainAxis);
            boolean b2 = ObjectUtils.equal(this.rangeAxis, fsp.rangeAxis);
            boolean b3 = ObjectUtils.equal(this.paint, fsp.paint);
            
            return b0 && b1 && b2 && b3;

        }
        
        return false;
    }
    
    /**
     * Returns a clone of the plot.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException if some component of the plot does not support cloning.
     */
    public Object clone() throws CloneNotSupportedException {
    
        FastScatterPlot clone = (FastScatterPlot) super.clone();    
        
        if (this.data != null) {
            clone.data = ArrayUtils.clone(this.data);    
        }
        
        if (this.domainAxis != null) {
            clone.domainAxis = (ValueAxis) this.domainAxis.clone();
            clone.domainAxis.setPlot(clone);
            clone.domainAxis.addChangeListener(clone);
        }
        
        if (this.rangeAxis != null) {
            clone.rangeAxis = (ValueAxis) this.rangeAxis.clone();
            clone.rangeAxis.setPlot(clone);
            clone.rangeAxis.addChangeListener(clone);
        }
            
        return clone;
        
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

        if (this.domainAxis != null) {
            this.domainAxis.addChangeListener(this);
        }

        if (this.rangeAxis != null) {
            this.rangeAxis.addChangeListener(this);
        }
    }
    
}
