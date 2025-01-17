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
 * -------------
 * ClipPath.java
 * -------------
 * (C) Copyright 2003, by David M. O'Donnell and Contributors.
 *
 * Original Author:  David M. O'Donnell;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *                    Nicolas Brodu;
 *
 * $Id: ClipPath.java,v 1.1 2007/10/10 19:21:54 vauchers Exp $
 *
 * Changes
 * -------
 * 22-Apr-2003 : Added standard header (DG);
 * 09-May-2003 : Added AxisLocation (DG);
 * 11-Sep-2003 :  Implemented Cloneable (NB);
 *
 */

package org.jfree.chart;

import java.awt.BasicStroke;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.ui.RectangleEdge;

/**
 * This class would typically be used with the ContourPlot type.  It allows the user to define a
 * GeneralPath curve in plot coordinates.  This curve can then be used mask off or define regions
 * within the contour plot.  The data must be sorted.
 * @author dmo
 *
 */
public class ClipPath implements Cloneable {

    /** The x values. */
    private double[] xValue = null;
    
    /** The y values. */
    private double[] yValue = null;

    /** Controls whether drawing will be clipped (
     * false would still allow the drawing or filling of path */
    private boolean clip = true;

    /** Controls whether the path is drawn as an outline. */
    private boolean drawPath = false;

    /** Controls whether the path is filled. */
    private boolean fillPath = false;

    /** The fill paint. */
    private Paint fillPaint = null;
    
    /** The draw paint. */
    private Paint drawPaint = null;
    
    /** The draw stroke. */
    private Stroke drawStroke = null;
    
    /** The composite. */
    private Composite composite = null;

    /**
     * Constructor for ClipPath.
     */
    public ClipPath() {
        super();
    }

    /**
     * Constructor for ClipPath.
     * Default values are assumed for the fillPath and drawPath options as false and true 
     * respectively.  The fillPaint is set to Color.GRAY, the drawColor is Color.BLUE, the stroke 
     * is BasicStroke(1)
     * and the composite is AlphaComposite.Src.
     *
     * @param xValue  x coordinates of curved to be created
     * @param yValue  y coordinates of curved to be created
     */
    public ClipPath(double[] xValue, double[] yValue) {
        this(xValue, yValue, true, false, true);
    }


    /**
     * Constructor for ClipPath.
     * The fillPaint is set to Color.GRAY, the drawColor is Color.BLUE, the stroke is BasicStroke(1)
     * and the composite is AlphaComposite.Src.
     *
     * @param xValue  x coordinates of curved to be created
     * @param yValue  y coordinates of curved to be created
     * @param clip  clip?
     * @param fillPath  whether the path is to filled
     * @param drawPath  whether the path is to drawn as an outline
     */
    public ClipPath(double[] xValue, double[] yValue,
                    boolean clip, boolean fillPath, boolean drawPath) {
        this.xValue = xValue;
        this.yValue = yValue;

        this.clip = clip;
        this.fillPath = fillPath;
        this.drawPath = drawPath;

        this.fillPaint = java.awt.Color.gray;
        this.drawPaint = java.awt.Color.blue;
        this.drawStroke = new BasicStroke(1);
        this.composite = java.awt.AlphaComposite.Src;
    }

    /**
     * Constructor for ClipPath.
     *
     * @param xValue  x coordinates of curved to be created
     * @param yValue  y coordinates of curved to be created
     * @param fillPath  whether the path is to filled
     * @param drawPath  whether the path is to drawn as an outline
     * @param fillPaint  the fill paint
     * @param drawPaint  the outline stroke color
     * @param drawStroke  the stroke style
     * @param composite  the composite rule
     */
    public ClipPath(double[] xValue, double[] yValue, boolean fillPath, boolean drawPath,
                     Paint fillPaint, Paint drawPaint, Stroke drawStroke, Composite composite) {

        this.xValue = xValue;
        this.yValue = yValue;

        this.fillPath = fillPath;
        this.drawPath = drawPath;

        this.fillPaint = fillPaint;
        this.drawPaint = drawPaint;
        this.drawStroke = drawStroke;
        this.composite = composite;

    }

    /**
     * Draws the clip path.
     *
     * @param g2  current graphics2D.
     * @param dataArea  the dataArea that the plot is being draw in.
     * @param horizontalAxis  the horizontal axis.
     * @param verticalAxis  the vertical axis.
     *
     * @return the GeneralPath defining the outline
     */
    public GeneralPath draw(Graphics2D g2,
                            Rectangle2D dataArea,
                            ValueAxis horizontalAxis, ValueAxis verticalAxis) {

        GeneralPath generalPath = generateClipPath(dataArea, horizontalAxis, verticalAxis);
        if (fillPath || drawPath) {
            Composite saveComposite = g2.getComposite();
            Paint savePaint = g2.getPaint();
            Stroke saveStroke = g2.getStroke();

            if (fillPaint != null) {
                g2.setPaint(fillPaint);
            }
            if (composite != null) {
                g2.setComposite(composite);
            }
            if (fillPath) {
                g2.fill(generalPath);
            }

            if (drawStroke != null) {
                g2.setStroke(drawStroke);
            }
            if (drawPath) {
                g2.draw(generalPath);
            }
            g2.setPaint(savePaint);
            g2.setComposite(saveComposite);
            g2.setStroke(saveStroke);
        }
        return generalPath;

    }

    /**
     * Generates the clip path.
     *
     * @param dataArea  the dataArea that the plot is being draw in.
     * @param horizontalAxis  the horizontal axis.
     * @param verticalAxis  the vertical axis.
     *
     * @return the GeneralPath defining the outline
     */
    public GeneralPath generateClipPath(Rectangle2D dataArea,
                                        ValueAxis horizontalAxis, ValueAxis verticalAxis) {

        GeneralPath generalPath = new GeneralPath();
        double transX = horizontalAxis.translateValueToJava2D(xValue[0], dataArea,
                                                              RectangleEdge.BOTTOM);
        double transY = verticalAxis.translateValueToJava2D(yValue[0], dataArea,
                                                            RectangleEdge.LEFT);
        generalPath.moveTo((float) transX, (float) transY);
        for (int k = 0; k < yValue.length; k++) {
            transX = horizontalAxis.translateValueToJava2D(xValue[k], dataArea,
                                                           RectangleEdge.BOTTOM);
            transY = verticalAxis.translateValueToJava2D(yValue[k], dataArea,
                                                         RectangleEdge.LEFT);
            generalPath.lineTo((float) transX, (float) transY);
        }
        generalPath.closePath();

        return generalPath;

    }

    /**
     * Returns the composite.
     * @return Composite
     */
    public Composite getComposite() {
        return composite;
    }

    /**
     * Returns the drawPaint.
     * @return Paint
     */
    public Paint getDrawPaint() {
        return drawPaint;
    }

    /**
     * Returns the drawPath.
     * @return boolean
     */
    public boolean isDrawPath() {
        return drawPath;
    }

    /**
     * Returns the drawStroke.
     * @return Stroke
     */
    public Stroke getDrawStroke() {
        return drawStroke;
    }

    /**
     * Returns the fillPaint.
     * @return Paint
     */
    public Paint getFillPaint() {
        return fillPaint;
    }

    /**
     * Returns the fillPath.
     * @return boolean
     */
    public boolean isFillPath() {
        return fillPath;
    }

    /**
     * Returns the xValue.
     * @return double[]
     */
    public double[] getXValue() {
        return xValue;
    }

    /**
     * Returns the yValue.
     * @return double[]
     */
    public double[] getYValue() {
        return yValue;
    }

    /**
     * Sets the composite.
     * @param composite The composite to set
     */
    public void setComposite(Composite composite) {
        this.composite = composite;
    }

    /**
     * Sets the drawPaint.
     * @param drawPaint The drawPaint to set
     */
    public void setDrawPaint(Paint drawPaint) {
        this.drawPaint = drawPaint;
    }

    /**
     * Sets the drawPath.
     * @param drawPath The drawPath to set
     */
    public void setDrawPath(boolean drawPath) {
        this.drawPath = drawPath;
    }

    /**
     * Sets the drawStroke.
     * @param drawStroke The drawStroke to set
     */
    public void setDrawStroke(Stroke drawStroke) {
        this.drawStroke = drawStroke;
    }

    /**
     * Sets the fillPaint.
     * @param fillPaint The fillPaint to set
     */
    public void setFillPaint(Paint fillPaint) {
        this.fillPaint = fillPaint;
    }

    /**
     * Sets the fillPath.
     * @param fillPath The fillPath to set
     */
    public void setFillPath(boolean fillPath) {
        this.fillPath = fillPath;
    }

    /**
     * Sets the xValue.
     * @param xValue The xValue to set
     */
    public void setXValue(double[] xValue) {
        this.xValue = xValue;
    }

    /**
     * Sets the yValue.
     * @param yValue The yValue to set
     */
    public void setYValue(double[] yValue) {
        this.yValue = yValue;
    }

    /**
     * Returns the clip.
     * @return boolean
     */
    public boolean isClip() {
        return clip;
    }

    /**
     * Sets the clip.
     * @param clip The clip to set
     */
    public void setClip(boolean clip) {
        this.clip = clip;
    }

    /** Deeper clone than default to avoid bugs when setting values in cloned object
     * @see java.lang.Object#clone()
     */
    public Object clone() throws CloneNotSupportedException {
        ClipPath clone = (ClipPath) super.clone();
        clone.xValue = (double[]) xValue.clone();
        clone.yValue = (double[]) yValue.clone();
        return clone;
    }

}
