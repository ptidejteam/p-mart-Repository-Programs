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
 * ----------------
 * ContourPlot.java
 * ----------------
 * (C) Copyright 2002, 2003, by David M. O'Donnell and Contributors.
 *
 * Original Author:  David M. O'Donnell;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: ContourPlot.java,v 1.1 2007/10/10 20:07:31 vauchers Exp $
 *
 * Changes
 * -------
 * 26-Nov-2002 : Version 1 contributed by David M. O'Donnell (DG);
 * 14-Jan-2003 : Added crosshair attributes (DG);
 * 23-Jan-2003 : Removed two constructors (DG);
 * 21-Mar-2003 : Bug fix 701744 (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 09-Jul-2003 : Changed ColorBar from extending axis classes to enclosing them (DG);
 *
 */

package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ClipPath;
import org.jfree.chart.CrosshairInfo;
import org.jfree.chart.Marker;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.ColorBar;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.ContourEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.labels.ContourToolTipGenerator;
import org.jfree.chart.labels.StandardContourToolTipGenerator;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.ContourDataset;
import org.jfree.data.Dataset;
import org.jfree.data.DatasetChangeEvent;
import org.jfree.data.DatasetUtilities;
import org.jfree.data.DefaultContourDataset;
import org.jfree.data.Range;

/**
 * A class for creating shaded contours.
 *
 * @author David M. O'Donnell
 */
public class ContourPlot extends Plot implements ContourValuePlot,
                                                 ValueAxisPlot,
                                                 PropertyChangeListener,
                                                 Serializable {

    /** The default insets. */
    protected static final Insets DEFAULT_INSETS = new Insets(2, 2, 100, 10);

    /** The domain axis (used for the x-values). */
    private ValueAxis domainAxis;

    /** The range axis (used for the y-values). */
    private ValueAxis rangeAxis;

    /** The colorbar axis (used for the z-values). */
    private ColorBar colorBar = null;

    private AxisLocation colorBarLocation;
    
    /** A flag that controls whether or not a domain crosshair is drawn..*/
    private boolean domainCrosshairVisible;

    /** The domain crosshair value. */
    private double domainCrosshairValue;

    /** The pen/brush used to draw the crosshair (if any). */
    private transient Stroke domainCrosshairStroke;

    /** The color used to draw the crosshair (if any). */
    private transient Paint domainCrosshairPaint;

    /** A flag that controls whether or not the crosshair locks onto actual data points. */
    private boolean domainCrosshairLockedOnData = true;

    /** A flag that controls whether or not a range crosshair is drawn..*/
    private boolean rangeCrosshairVisible;

    /** The range crosshair value. */
    private double rangeCrosshairValue;

    /** The pen/brush used to draw the crosshair (if any). */
    private transient Stroke rangeCrosshairStroke;

    /** The color used to draw the crosshair (if any). */
    private transient Paint rangeCrosshairPaint;

    /** A flag that controls whether or not the crosshair locks onto actual data points. */
    private boolean rangeCrosshairLockedOnData = true;

    /** A list of markers (optional) for the domain axis. */
    private List domainMarkers;

    /** A list of markers (optional) for the range axis. */
    private List rangeMarkers;

    /** A list of annotations (optional) for the plot. */
    private List annotations;

    /** The tool tip generator. */
    private ContourToolTipGenerator toolTipGenerator;

    /** The URL text generator. */
    private XYURLGenerator urlGenerator;

    /** Controls whether data are render as filled rectangles or rendered as points */
    private boolean renderAsPoints = false;

    /** Size of points rendered when renderAsPoints = true.  Size is relative to dataArea */
    private double ptSizePct = 0.05;

    /** Contains the a ClipPath to "trim" the contours. */
    private transient ClipPath clipPath = null;

    /** Set to Paint to represent missing values. */
    private transient Paint missingPaint = null;

    /**
     * Constructs a Contour Plot with the specified axes (other attributes take default values).
     *
     * @param data  The dataset.
     * @param domainAxis  The domain axis.
     * @param rangeAxis  The range axis.
     * @param colorBar  The z-axis axis.
    */
    public ContourPlot(ContourDataset data,
                       ValueAxis domainAxis, ValueAxis rangeAxis, ColorBar colorBar) {

        super(data);

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

        this.colorBar = colorBar;
        if (colorBar != null) {
            colorBar.getAxis().setPlot(this);
            colorBar.getAxis().addChangeListener(this);
            colorBar.configure(this);
        }
        this.colorBarLocation = AxisLocation.LEFT;

        toolTipGenerator = new StandardContourToolTipGenerator();

    }

    public AxisLocation getColorBarLocation() {
        return this.colorBarLocation;
    }
    
    public void setColorBarLocation(AxisLocation location) {
        this.colorBarLocation = location;
        this.notifyListeners(new PlotChangeEvent(this));    
    }
    
    /**
     * A convenience method that returns the dataset for the plot, cast as a ContourDataset.
     *
     * @return The dataset for the plot, cast as an ContourDataset.
     */
    public ContourDataset getContourDataset() {
        return (ContourDataset) getDataset();
    }

    /**
     * Returns the domain axis for the plot.
     *
     * @return The domain axis.
     */
    public ValueAxis getDomainAxis() {

        ValueAxis result = domainAxis;

        return result;

    }

    /**
     * Sets the domain axis for the plot (this must be compatible with the plot
     * type or an exception is thrown).
     *
     * @param axis The new axis.
     */
    public void setDomainAxis(ValueAxis axis) {

        if (isCompatibleDomainAxis(axis)) {

            if (axis != null) {

                try {
                    axis.setPlot(this);
                }
                catch (PlotNotCompatibleException e) {
                }
                axis.addChangeListener(this);
            }

            // plot is likely registered as a listener with the existing axis...
            if (this.domainAxis != null) {
                this.domainAxis.removeChangeListener(this);
            }

            this.domainAxis = axis;
            notifyListeners(new PlotChangeEvent(this));

        }

    }

    /**
     * Returns the range axis for the plot.
     *
     * @return The range axis.
     */
    public ValueAxis getRangeAxis() {

        ValueAxis result = rangeAxis;

        return result;

    }

    /**
     * Sets the range axis for the plot.
     * <P>
     * An exception is thrown if the new axis and the plot are not mutually
     * compatible.
     *
     * @param axis The new axis (null permitted).
     */
    public void setRangeAxis(ValueAxis axis) {

        if (axis != null) {
            try {
                axis.setPlot(this);
            }
            catch (PlotNotCompatibleException e) {
            }
            axis.addChangeListener(this);
        }

        // plot is likely registered as a listener with the existing axis...
        if (this.rangeAxis != null) {
            this.rangeAxis.removeChangeListener(this);
        }

        this.rangeAxis = axis;
        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Sets the colorbar for the plot.
     *
     * @param axis The new axis (null permitted).
     */
    public void setColorBarAxis(ColorBar axis) {

        this.colorBar = axis;
        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Adds a marker for the domain axis.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the range axis, however this is entirely up to the renderer.
     *
     * @param marker the marker.
     */
    public void addDomainMarker(Marker marker) {

        if (this.domainMarkers == null) {
            this.domainMarkers = new java.util.ArrayList();
        }
        this.domainMarkers.add(marker);
        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Clears all the domain markers.
     */
    public void clearDomainMarkers() {
        if (this.domainMarkers != null) {
            this.domainMarkers.clear();
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Adds a marker for the range axis.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the range axis, however this is entirely up to the renderer.
     *
     * @param marker The marker.
     */
    public void addRangeMarker(Marker marker) {

        if (this.rangeMarkers == null) {
            this.rangeMarkers = new java.util.ArrayList();
        }
        this.rangeMarkers.add(marker);
        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Clears all the range markers.
     */
    public void clearRangeMarkers() {
        if (this.rangeMarkers != null) {
            this.rangeMarkers.clear();
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Adds an annotation to the plot.
     *
     * @param annotation  the annotation.
     */
    public void addAnnotation(XYAnnotation annotation) {

        if (this.annotations == null) {
            this.annotations = new java.util.ArrayList();
        }
        this.annotations.add(annotation);
        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Clears all the annotations.
     */
    public void clearAnnotations() {
        if (this.annotations != null) {
            this.annotations.clear();
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Checks the compatibility of a domain axis, returning true if the axis is
     * compatible with the plot, and false otherwise.
     *
     * @param axis The proposed axis.
     *
     * @return <code>true</code> if the axis is compatible with the plot.
     */
    public boolean isCompatibleDomainAxis(ValueAxis axis) {

        return true;

    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     * <P>
     * The optional <code>info</code> argument collects information about the rendering of
     * the plot (dimensions, tooltip information etc).  Just pass in <code>null</code> if
     * you do not need this information.
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the plot (including axis labels) should be drawn.
     * @param info  collects chart drawing information (<code>null</code> permitted).
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, ChartRenderingInfo info) {

        // if the plot area is too small, just return...
        boolean b1 = (plotArea.getWidth() <= MINIMUM_WIDTH_TO_DRAW);
        boolean b2 = (plotArea.getHeight() <= MINIMUM_HEIGHT_TO_DRAW);
        if (b1 || b2) {
            return;
        }

        // record the plot area...
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
        
        ValueAxis domainAxis = getDomainAxis();
        ValueAxis rangeAxis = getRangeAxis();
        space = domainAxis.reserveSpace(g2, this, plotArea, AxisLocation.BOTTOM, space);
        space = rangeAxis.reserveSpace(g2, this, plotArea, AxisLocation.LEFT, space);

        Rectangle2D estimatedDataArea = space.shrink(plotArea, null);
        
        AxisSpace space2 = new AxisSpace();
        space2 = this.colorBar.reserveSpace(g2, this, plotArea, estimatedDataArea,
                                           this.colorBarLocation, space2);
        Rectangle2D adjustedPlotArea = space2.shrink(plotArea, null);
        
        Rectangle2D dataArea = space.shrink(adjustedPlotArea, null);

        Rectangle2D colorBarArea = space2.reserved(plotArea, this.colorBarLocation);

        // additional dataArea modifications
        if (getDataAreaRatio() != 0.0) { //check whether modification is
            double ratio = getDataAreaRatio();
            Rectangle2D tmpDataArea = (Rectangle2D) dataArea.clone();
            double h = tmpDataArea.getHeight();
            double w = tmpDataArea.getWidth();

            if (ratio > 0) { // ratio represents pixels
                if (w * ratio <= h) {
                    h = ratio * w;
                }
                else {
                    w = h / ratio;
                }
            }
            else {  // ratio represents axis units
                ratio *= -1.0;
                double xLength = getDomainAxis().getRange().getLength();
                double yLength = getRangeAxis().getRange().getLength();
                double unitRatio = yLength / xLength;

                ratio = unitRatio * ratio;

                if (w * ratio <= h) {
                    h = ratio * w;
                }
                else {
                    w = h / ratio;
                }
            }

            dataArea.setRect(tmpDataArea.getX() + tmpDataArea.getWidth() / 2 - w / 2,
                             tmpDataArea.getY(), w, h);
        }

        if (info != null) {
            info.setDataArea(dataArea);
        }

        CrosshairInfo crosshairInfo = new CrosshairInfo();

        crosshairInfo.setCrosshairDistance(Double.POSITIVE_INFINITY);
        crosshairInfo.setAnchorX(getDomainAxis().getAnchorValue());
        crosshairInfo.setAnchorY(getRangeAxis().getAnchorValue());

        // draw the plot background...
        drawBackground(g2, dataArea);

        if (this.domainAxis != null) {
            this.domainAxis.draw(g2, adjustedPlotArea, dataArea, AxisLocation.BOTTOM);
        }

        if (this.rangeAxis != null) {
            this.rangeAxis.draw(g2, adjustedPlotArea, dataArea, AxisLocation.LEFT);
        }

        if (colorBar != null) {
            this.colorBar.draw(g2, adjustedPlotArea, dataArea, colorBarArea, this.colorBarLocation);
        }
        Shape originalClip = g2.getClip();
        Composite originalComposite = g2.getComposite();

        g2.clip(dataArea);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                   getForegroundAlpha()));
        render(g2, dataArea, info, crosshairInfo);

        if (this.domainMarkers != null) {
            Iterator iterator = this.domainMarkers.iterator();
            while (iterator.hasNext()) {
                Marker marker = (Marker) iterator.next();
                drawDomainMarker(g2, this, getDomainAxis(), marker, dataArea);
            }
        }

        if (this.rangeMarkers != null) {
            Iterator iterator = this.rangeMarkers.iterator();
            while (iterator.hasNext()) {
                Marker marker = (Marker) iterator.next();
                drawRangeMarker(g2, this, getRangeAxis(), marker, dataArea);
            }
        }

// TO DO:  these annotations only work with XYPlot, see if it is possible to make ContourPlot a
// subclass of XYPlot (DG);

//        // draw the annotations...
//        if (this.annotations != null) {
//            Iterator iterator = this.annotations.iterator();
//            while (iterator.hasNext()) {
//                Annotation annotation = (Annotation) iterator.next();
//                if (annotation instanceof XYAnnotation) {
//                    XYAnnotation xya = (XYAnnotation) annotation;
//                    // get the annotation to draw itself...
//                    xya.draw(g2, this, dataArea, getDomainAxis(), getRangeAxis());
//                }
//            }
//        }

        g2.setClip(originalClip);
        g2.setComposite(originalComposite);
        drawOutline(g2, dataArea);

    }

    /**
     * Draws a representation of the data within the dataArea region, using the
     * current renderer.
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

        // now get the data and plot it (the visual representation will depend
        // on the renderer that has been set)...
        ContourDataset data = this.getContourDataset();
        if (data != null) {

 //           renderer.initialise(g2, dataArea, this, data, info);

            ValueAxis domainAxis = getDomainAxis();
            ValueAxis rangeAxis = getRangeAxis();
            ColorBar zAxis = getColorBar();

            if (clipPath != null) {
                GeneralPath clipper = getClipPath().draw(g2, dataArea, domainAxis, rangeAxis);
                if (clipPath.isClip()) {
                    g2.clip(clipper);
                }
            }

            if (renderAsPoints) {
                pointRenderer(g2, dataArea, info, this,
                              domainAxis, rangeAxis, zAxis,
                              data, crosshairInfo);
            }
            else {
                contourRenderer(g2, dataArea, info, this,
                                domainAxis, rangeAxis, zAxis,
                                data, crosshairInfo);
            }

            // draw vertical crosshair if required...
            setDomainCrosshairValue(crosshairInfo.getCrosshairX(), false);
            if (isDomainCrosshairVisible()) {
                drawVerticalLine(g2, dataArea,
                                 getDomainCrosshairValue(),
                                 getDomainCrosshairStroke(),
                                 getDomainCrosshairPaint());
            }

            // draw horizontal crosshair if required...
            setRangeCrosshairValue(crosshairInfo.getCrosshairY(), false);
            if (isRangeCrosshairVisible()) {
                drawHorizontalLine(g2, dataArea,
                                   getRangeCrosshairValue(),
                                   getRangeCrosshairStroke(),
                                   getRangeCrosshairPaint());
            }

        }
        else if (clipPath != null) {
            getClipPath().draw(g2, dataArea, domainAxis, rangeAxis);
        }

    }

    /**
     * Fills the plot.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area within which the data is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param horizontalAxis  the domain (horizontal) axis.
     * @param verticalAxis  the range (vertical) axis.
     * @param colorBarAxis  the color bar axis.
     * @param data  the dataset.
     * @param crosshairInfo  information about crosshairs on a plot.
     */
    public void contourRenderer(Graphics2D g2,
                                Rectangle2D dataArea,
                                ChartRenderingInfo info,
                                ContourPlot plot,
                                ValueAxis horizontalAxis,
                                ValueAxis verticalAxis,
                                ColorBar colorBar,
                                ContourDataset data,
                                CrosshairInfo crosshairInfo) {

        // setup for collecting optional entity info...
        Rectangle2D.Double entityArea = null;
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getEntityCollection();
        }

//        Shape clipRegion = g2.getClip();

        Rectangle2D.Double rect = null;
        rect = new Rectangle2D.Double();

        //turn off anti-aliasing when filling rectangles
        Object antiAlias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // if (tooltips!=null) tooltips.clearToolTips(); // reset collection
        // get the data points
        Number[] xNumber = data.getXValues();
        Number[] yNumber = data.getYValues();
        Number[] zNumber = data.getZValues();

        double[] x = new double[xNumber.length];
        double[] y = new double[yNumber.length];
//        double[] z = new double[zNumber.length]; // dmo:remove this line

        for (int i = 0; i < x.length; i++) {
            x[i] = xNumber[i].doubleValue();
            y[i] = yNumber[i].doubleValue();
//            z[i] = zNumber[i].doubleValue();    //dmo:remove this line
        }

        int[] xIndex = ((DefaultContourDataset) data).indexX();
        int[] indexX = ((DefaultContourDataset) data).getXIndices();
        boolean vertInverted = ((NumberAxis) verticalAxis).isInverted();
        boolean horizInverted = false;
        if (horizontalAxis instanceof NumberAxis) {
            horizInverted = ((NumberAxis) horizontalAxis).isInverted();
        }
        double transX = 0.0;
        double transXm1 = 0.0;
        double transXp1 = 0.0;
        double transDXm1 = 0.0;
        double transDXp1 = 0.0;
        double transDX = 0.0;
        double transY = 0.0;
        double transYm1 = 0.0;
        double transYp1 = 0.0;
        double transDYm1 = 0.0;
        double transDYp1 = 0.0;
        double transDY = 0.0;
        int iMax = xIndex[xIndex.length - 1];
        for (int k = 0; k < x.length; k++) {
            int i = xIndex[k];
            if (indexX[i] == k) { // this is a new column
                if (i == 0) {
                    transX = horizontalAxis.translateValueToJava2D(x[k], dataArea, AxisLocation.BOTTOM);
                    transXm1 = transX;
                    transXp1 = horizontalAxis.translateValueToJava2D(x[indexX[i + 1]], dataArea, AxisLocation.BOTTOM);
                    transDXm1 = Math.abs(0.5 * (transX - transXm1));
                    transDXp1 = Math.abs(0.5 * (transX - transXp1));
                }
                else if (i == iMax) {
                    transX = horizontalAxis.translateValueToJava2D(x[k], dataArea, AxisLocation.BOTTOM);
                    transXm1 = horizontalAxis.translateValueToJava2D(x[indexX[i - 1]], dataArea, AxisLocation.BOTTOM);
                    transXp1 = transX;
                    transDXm1 = Math.abs(0.5 * (transX - transXm1));
                    transDXp1 = Math.abs(0.5 * (transX - transXp1));
                }
                else {
                    transX = horizontalAxis.translateValueToJava2D(x[k], dataArea, AxisLocation.BOTTOM);
                    transXp1 = horizontalAxis.translateValueToJava2D(x[indexX[i + 1]], dataArea, AxisLocation.BOTTOM);
                    transDXm1 = transDXp1;
                    transDXp1 = Math.abs(0.5 * (transX - transXp1));
                }

                if (horizInverted) {
                    transX -= transDXp1;
                }
                else {
                    transX -= transDXm1;
                }

                transDX = transDXm1 + transDXp1;

                transY = verticalAxis.translateValueToJava2D(y[k], dataArea, AxisLocation.LEFT);
                transYm1 = transY;
                if (k + 1 == y.length) {
                    continue;
                }
                transYp1 = verticalAxis.translateValueToJava2D(y[k + 1], dataArea, AxisLocation.LEFT);
                transDYm1 = Math.abs(0.5 * (transY - transYm1));
                transDYp1 = Math.abs(0.5 * (transY - transYp1));
            }
            else if ((i < indexX.length - 1 && indexX[i + 1] - 1 == k) || k == x.length - 1) {
                // end of column
                transY = verticalAxis.translateValueToJava2D(y[k], dataArea, AxisLocation.LEFT);
                transYm1 = verticalAxis.translateValueToJava2D(y[k - 1], dataArea, AxisLocation.LEFT);
                transYp1 = transY;
                transDYm1 = Math.abs(0.5 * (transY - transYm1));
                transDYp1 = Math.abs(0.5 * (transY - transYp1));
            }
            else {
                transY = verticalAxis.translateValueToJava2D(y[k], dataArea, AxisLocation.LEFT);
                transYp1 = verticalAxis.translateValueToJava2D(y[k + 1], dataArea, AxisLocation.LEFT);
                transDYm1 = transDYp1;
                transDYp1 = Math.abs(0.5 * (transY - transYp1));
            }
            if (vertInverted) {
                transY -= transDYm1;
            }
            else {
                transY -= transDYp1;
            }

            transDY = transDYm1 + transDYp1;

            rect.setRect(transX, transY, transDX, transDY);
            if (zNumber[k] != null) {
                g2.setPaint(colorBar.getPaint(zNumber[k].doubleValue()));
                g2.fill(rect);
            }
            else if (missingPaint != null) {
                g2.setPaint(missingPaint);
                g2.fill(rect);
            }

            entityArea = rect;

            // add an entity for the item...
            if (entities != null) {
                String tip = "";
                if (getToolTipGenerator() != null) {
                    tip = toolTipGenerator.generateToolTip(data, k);
                }
//              Shape s = g2.getClip();
//              if (s.contains(rect) || s.intersects(rect)) {
                String url = null;
                // if (getURLGenerator() != null) {    //dmo: look at this later
                //      url = getURLGenerator().generateURL(data, series, item);
                // }
                // Unlike XYItemRenderer, we need to clone entityArea since it reused.
                ContourEntity entity = new ContourEntity((Rectangle2D.Double) entityArea.clone(),
                                                         tip, url);
                entity.setIndex(k);
                entities.addEntity(entity);
//              }
            }

            // do we need to update the crosshair values?
            if (plot.isDomainCrosshairLockedOnData()) {
                if (plot.isRangeCrosshairLockedOnData()) {
                    // both axes
                    crosshairInfo.updateCrosshairPoint(transX, transY);
                }
                else {
                    // just the horizontal axis...
                    crosshairInfo.updateCrosshairX(transX);
                }
            }
            else {
                if (plot.isRangeCrosshairLockedOnData()) {
                    // just the vertical axis...
                    crosshairInfo.updateCrosshairY(transY);
                }
            }
        }

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias);

        return;

    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area within which the data is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param domainAxis  the domain (horizontal) axis.
     * @param rangeAxis  the range (vertical) axis.
     * @param colorBar  the color bar axis.
     * @param data  the dataset.
     * @param crosshairInfo  information about crosshairs on a plot.
     */
    public void pointRenderer(Graphics2D g2,
                              Rectangle2D dataArea,
                              ChartRenderingInfo info,
                              ContourPlot plot,
                              ValueAxis domainAxis,
                              ValueAxis rangeAxis,
                              ColorBar colorBar,
                              ContourDataset data,
                              CrosshairInfo crosshairInfo) {

        // setup for collecting optional entity info...
        RectangularShape entityArea = null;
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getEntityCollection();
        }

//      Rectangle2D.Double rect = null;
//      rect = new Rectangle2D.Double();
        RectangularShape rect = new Ellipse2D.Double();


        //turn off anti-aliasing when filling rectangles
        Object antiAlias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        //      if (tooltips!=null) tooltips.clearToolTips(); // reset collection
        // get the data points
        Number[] xNumber = data.getXValues();
        Number[] yNumber = data.getYValues();
        Number[] zNumber = data.getZValues();

        double[] x = new double[xNumber.length];
        double[] y = new double[yNumber.length];
//      double[] z = new double[zNumber.length];

        for (int i = 0; i < x.length; i++) {
            x[i] = xNumber[i].doubleValue();
            y[i] = yNumber[i].doubleValue();
//          z[i] = zNumber[i].doubleValue();
        }

//      int[] xIndex = ((DefaultContourDataset) data).indexX();
//      int[] indexX = ((DefaultContourDataset) data).getXIndices();
//      boolean vertInverted = ((NumberAxis) verticalAxis).isInverted();
//      boolean horizInverted = false;
//      if (horizontalAxis instanceof NumberAxis)
//          horizInverted = ((NumberAxis) horizontalAxis).isInverted();

        double transX = 0.0;
        double transDX = 0.0;
        double transY = 0.0;
        double transDY = 0.0;
        double size = dataArea.getWidth() * ptSizePct;
        for (int k = 0;k < x.length; k++) {

            transX = domainAxis.translateValueToJava2D(x[k], dataArea, AxisLocation.BOTTOM) - 0.5 * size;
            transY = rangeAxis.translateValueToJava2D(y[k], dataArea, AxisLocation.LEFT) - 0.5 * size;
            transDX = size;
            transDY = size;

            rect.setFrame(transX, transY, transDX, transDY);

            if (zNumber[k] != null) {
                g2.setPaint(colorBar.getPaint(zNumber[k].doubleValue()));
                g2.fill(rect);
            }
            else if (missingPaint != null) {
                g2.setPaint(missingPaint);
                g2.fill(rect);
            }


            entityArea = rect;

            // add an entity for the item...
            if (entities != null) {
                String tip = null;
                if (getToolTipGenerator() != null) {
                    tip = toolTipGenerator.generateToolTip(data, k);
                }
                String url = null;
                // if (getURLGenerator() != null) {                    //dmo: look at this later
                //   url = getURLGenerator().generateURL(data, series, item);
                // }
                //Unlike XYItemRenderer, we need to clone entityArea since it reused.
                ContourEntity entity = new ContourEntity((RectangularShape) entityArea.clone(),
                                                          tip, url);
                entity.setIndex(k);
                entities.addEntity(entity);
            }

            // do we need to update the crosshair values?
            if (plot.isDomainCrosshairLockedOnData()) {
                if (plot.isRangeCrosshairLockedOnData()) {
                    // both axes
                    crosshairInfo.updateCrosshairPoint(transX, transY);
                }
                else {
                    // just the horizontal axis...
                    crosshairInfo.updateCrosshairX(transX);
                }
            }
            else {
                if (plot.isRangeCrosshairLockedOnData()) {
                    // just the vertical axis...
                    crosshairInfo.updateCrosshairY(transY);
                }
            }
        }


        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias);

        return;

    }

    /**
     * Utility method for drawing a crosshair on the chart (if required).
     *
     * @param g2  The graphics device.
     * @param dataArea  The data area.
     * @param value  The coordinate, where to draw the line.
     * @param stroke  The stroke to use.
     * @param paint  The paint to use.
     */
    protected void drawVerticalLine(Graphics2D g2, Rectangle2D dataArea,
                                    double value, Stroke stroke, Paint paint) {

        double xx = getDomainAxis().translateValueToJava2D(value, dataArea, AxisLocation.BOTTOM);
        Line2D line = new Line2D.Double(xx, dataArea.getMinY(),
                                        xx, dataArea.getMaxY());
        g2.setStroke(stroke);
        g2.setPaint(paint);
        g2.draw(line);

    }

    /**
     * Utility method for drawing a crosshair on the chart (if required).
     *
     * @param g2  The graphics device.
     * @param dataArea  The data area.
     * @param value  The coordinate, where to draw the line.
     * @param stroke  The stroke to use.
     * @param paint  The paint to use.
     */
    protected void drawHorizontalLine(Graphics2D g2, Rectangle2D dataArea,
                                      double value, Stroke stroke, Paint paint) {

        double yy = getRangeAxis().translateValueToJava2D(value, dataArea, AxisLocation.LEFT);
        Line2D line = new Line2D.Double(dataArea.getMinX(), yy,
                                        dataArea.getMaxX(), yy);
        g2.setStroke(stroke);
        g2.setPaint(paint);
        g2.draw(line);

    }

    /**
     * Handles a 'click' on the plot by updating the anchor values...
     *
     * @param x  x-coordinate, where the click occured.
     * @param y  y-coordinate, where the click occured.
     * @param info  An object for collection dimension information.
     */
    public void handleClick(int x, int y, ChartRenderingInfo info) {

/*        // set the anchor value for the horizontal axis...
        ValueAxis hva = getDomainAxis();
        if (hva != null) {
            double hvalue = hva.translateJava2DtoValue((float) x, info.getDataArea());

            hva.setAnchorValue(hvalue);
            setDomainCrosshairValue(hvalue);
        }

        // set the anchor value for the vertical axis...
        ValueAxis vva = this.getRangeAxis();
        if (vva != null) {
            double vvalue = vva.translateJava2DtoValue((float) y, info.getDataArea());
            vva.setAnchorValue(vvalue);
            setRangeCrosshairValue(vvalue);
        }
*/
    }

    /**
     * Zooms the axis ranges by the specified percentage about the anchor point.
     *
     * @param percent  The amount of the zoom.
     */
    public void zoom(double percent) {

        if (percent > 0) {
            ValueAxis domainAxis = getDomainAxis();
            double range = domainAxis.getMaximumAxisValue() - domainAxis.getMinimumAxisValue();
            double scaledRange = range * percent;
            domainAxis.setAnchoredRange(scaledRange);

            ValueAxis rangeAxis = getRangeAxis();
            range = rangeAxis.getMaximumAxisValue()
                - rangeAxis.getMinimumAxisValue();
            scaledRange = range * percent;
            rangeAxis.setAnchoredRange(scaledRange);
        }
        else {
            this.getRangeAxis().setAutoRange(true);
            this.getDomainAxis().setAutoRange(true);
        }

    }

    /**
     * Returns the plot type as a string.
     *
     * @return A short string describing the type of plot.
     */
    public String getPlotType() {
        return "Contour Plot";
    }

    /**
     * Returns the range for the horizontal axis.
     *
     * @param axis  the axis.
     *
     * @return The range for the horizontal axis.
     */
    public Range getDataRange(ValueAxis axis) {

        Dataset dataset = getDataset();
        if (dataset == null) {
            return null;
        }

        Range result = null;

        if (axis == getDomainAxis()) {
            result = DatasetUtilities.getDomainExtent(dataset);
        }
        else if (axis == getRangeAxis()) {
            result = DatasetUtilities.getRangeExtent(dataset);
        }

        return result;

    }

    /**
     * Returns the range for the vertical axis.
     *
     * @param axis  the axis.
     *
     * @return The range for the vertical axis.
     */
    public Range getVerticalDataRange(ValueAxis axis) {

        Range result = null;

        Dataset dataset = getDataset();
        if (dataset != null) {
            result = DatasetUtilities.getRangeExtent(dataset);
        }

        return result;

    }

   /**
     * Returns the range for the Contours.
     *
     * @return The range for the Contours (z-axis).
     */
    public Range getContourDataRange() {

        Range result = null;

        ContourDataset data = (ContourDataset) getDataset();

        if (data != null) {
            Range h = getHorizontalValueAxis().getRange();
            Range v = getVerticalValueAxis().getRange();
            result = this.visibleRange(data, h, v);
        }

        return result;
    }

    /**
     * Notifies all registered listeners of a property change.
     * <P>
     * One source of property change events is the plot's renderer.
     *
     * @param event  Information about the property change.
     */
    public void propertyChange(PropertyChangeEvent event) {

        this.notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Receives notification of a change to the plot's dataset.
     * <P>
     * The chart reacts by passing on a chart change event to all registered
     * listeners.
     *
     * @param event  Information about the event (not used here).
     */
    public void datasetChanged(DatasetChangeEvent event) {

        if (this.domainAxis != null) {
            this.domainAxis.configure();
        }

        if (this.rangeAxis != null) {
            this.rangeAxis.configure();
        }

        if (this.colorBar != null) {
            this.colorBar.configure(this);
        }

        PlotChangeEvent e = new PlotChangeEvent(this);
        notifyListeners(e);
   }

    /**
     * Returns the horizontal axis.
     * <P>
     * This method is part of the HorizontalValuePlot interface.
     *
     * @return The horizontal axis.
     */
    public ValueAxis getHorizontalValueAxis() {
        return getDomainAxis();
    }

    /**
     * Returns the colorbar.
     *
     * @return The colorbar.
     */
    public ColorBar getColorBar() {
        return colorBar;
    }

    /**
     * Returns the vertical axis.
     * <P>
     * This method is part of the VerticalValuePlot interface.
     *
     * @return The vertical axis.
     */
    public ValueAxis getVerticalValueAxis() {
        return getRangeAxis();
    }
    /**
     * Returns a flag indicating whether or not the domain crosshair is visible.
     *
     * @return the flag.
     */
    public boolean isDomainCrosshairVisible() {
        return this.domainCrosshairVisible;
    }

    /**
     * Sets the flag indicating whether or not the domain crosshair is visible.
     *
     * @param flag  the new value of the flag.
     */
    public void setDomainCrosshairVisible(boolean flag) {

        if (this.domainCrosshairVisible != flag) {
            this.domainCrosshairVisible = flag;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns a flag indicating whether or not the crosshair should "lock-on"
     * to actual data values.
     *
     * @return the flag.
     */
    public boolean isDomainCrosshairLockedOnData() {
        return this.domainCrosshairLockedOnData;
    }

    /**
     * Sets the flag indicating whether or not the domain crosshair should "lock-on"
     * to actual data values.
     *
     * @param flag  the flag.
     */
    public void setDomainCrosshairLockedOnData(boolean flag) {

        if (this.domainCrosshairLockedOnData != flag) {
            this.domainCrosshairLockedOnData = flag;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the domain crosshair value.
     *
     * @return The value.
     */
    public double getDomainCrosshairValue() {
        return this.domainCrosshairValue;
    }

    /**
     * Sets the domain crosshair value.
     * <P>
     * Registered listeners are notified that the plot has been modified, but
     * only if the crosshair is visible.
     *
     * @param value  the new value.
     */
    public void setDomainCrosshairValue(double value) {

        setDomainCrosshairValue(value, true);

    }

    /**
     * Sets the domain crosshair value.
     * <P>
     * Registered listeners are notified that the axis has been modified, but
     * only if the crosshair is visible.
     *
     * @param value  the new value.
     * @param notify  a flag that controls whether or not listeners are notified.
     */
    public void setDomainCrosshairValue(double value, boolean notify) {

        this.domainCrosshairValue = value;
        if (isDomainCrosshairVisible() && notify) {
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the Stroke used to draw the crosshair (if visible).
     *
     * @return the crosshair stroke.
     */
    public Stroke getDomainCrosshairStroke() {
        return domainCrosshairStroke;
    }

    /**
     * Sets the Stroke used to draw the crosshairs (if visible) and notifies
     * registered listeners that the axis has been modified.
     *
     * @param stroke  the new crosshair stroke.
     */
    public void setDomainCrosshairStroke(Stroke stroke) {
        domainCrosshairStroke = stroke;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the domain crosshair color.
     *
     * @return the crosshair color.
     */
    public Paint getDomainCrosshairPaint() {
        return this.domainCrosshairPaint;
    }

    /**
     * Sets the Paint used to color the crosshairs (if visible) and notifies
     * registered listeners that the axis has been modified.
     *
     * @param paint the new crosshair paint.
     */
    public void setDomainCrosshairPaint(Paint paint) {
        this.domainCrosshairPaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns a flag indicating whether or not the range crosshair is visible.
     *
     * @return the flag.
     */
    public boolean isRangeCrosshairVisible() {
        return this.rangeCrosshairVisible;
    }

    /**
     * Sets the flag indicating whether or not the range crosshair is visible.
     *
     * @param flag  the new value of the flag.
     */
    public void setRangeCrosshairVisible(boolean flag) {

        if (this.rangeCrosshairVisible != flag) {
            this.rangeCrosshairVisible = flag;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns a flag indicating whether or not the crosshair should "lock-on"
     * to actual data values.
     *
     * @return the flag.
     */
    public boolean isRangeCrosshairLockedOnData() {
        return this.rangeCrosshairLockedOnData;
    }

    /**
     * Sets the flag indicating whether or not the range crosshair should "lock-on"
     * to actual data values.
     *
     * @param flag  the flag.
     */
    public void setRangeCrosshairLockedOnData(boolean flag) {

        if (this.rangeCrosshairLockedOnData != flag) {
            this.rangeCrosshairLockedOnData = flag;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the range crosshair value.
     *
     * @return The value.
     */
    public double getRangeCrosshairValue() {
        return this.rangeCrosshairValue;
    }

    /**
     * Sets the domain crosshair value.
     * <P>
     * Registered listeners are notified that the plot has been modified, but
     * only if the crosshair is visible.
     *
     * @param value  the new value.
     */
    public void setRangeCrosshairValue(double value) {

        setRangeCrosshairValue(value, true);

    }

    /**
     * Sets the range crosshair value.
     * <P>
     * Registered listeners are notified that the axis has been modified, but
     * only if the crosshair is visible.
     *
     * @param value  the new value.
     * @param notify  a flag that controls whether or not listeners are notified.
     */
    public void setRangeCrosshairValue(double value, boolean notify) {

        this.rangeCrosshairValue = value;
        if (isRangeCrosshairVisible() && notify) {
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the Stroke used to draw the crosshair (if visible).
     *
     * @return the crosshair stroke.
     */
    public Stroke getRangeCrosshairStroke() {
        return rangeCrosshairStroke;
    }

    /**
     * Sets the Stroke used to draw the crosshairs (if visible) and notifies
     * registered listeners that the axis has been modified.
     *
     * @param stroke  the new crosshair stroke.
     */
    public void setRangeCrosshairStroke(Stroke stroke) {
        rangeCrosshairStroke = stroke;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the range crosshair color.
     *
     * @return the crosshair color.
     */
    public Paint getRangeCrosshairPaint() {
        return this.rangeCrosshairPaint;
    }

    /**
     * Sets the Paint used to color the crosshairs (if visible) and notifies
     * registered listeners that the axis has been modified.
     *
     * @param paint the new crosshair paint.
     */
    public void setRangeCrosshairPaint(Paint paint) {
        this.rangeCrosshairPaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the tool tip generator.
     *
     * @return the tool tip generator (possibly null).
     */
    public ContourToolTipGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }

    /**
     * Sets the tool tip generator.
     *
     * @param generator  the tool tip generator (null permitted).
     */
    public void setToolTipGenerator(ContourToolTipGenerator generator) {

        //Object oldValue = this.toolTipGenerator;
        this.toolTipGenerator = generator;

    }

    /**
     * Returns the URL generator for HTML image maps.
     *
     * @return the URL generator (possibly null).
     */
    public XYURLGenerator getURLGenerator() {
        return this.urlGenerator;
    }

    /**
     * Sets the URL generator for HTML image maps.
     *
     * @param urlGenerator  the URL generator (null permitted).
     */
    public void setURLGenerator(XYURLGenerator urlGenerator) {

        //Object oldValue = this.urlGenerator;
        this.urlGenerator = urlGenerator;

    }

    /**
     * Draws a vertical line on the chart to represent a 'range marker'.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param marker  the marker line.
     * @param dataArea  the axis data area.
     */
    public void drawDomainMarker(Graphics2D g2,
                                 ContourPlot plot,
                                 ValueAxis domainAxis,
                                 Marker marker,
                                 Rectangle2D dataArea) {

        double value = marker.getValue();
        Range range = domainAxis.getRange();
        if (!range.contains(value)) {
            return;
        }

        double x = domainAxis.translateValueToJava2D(marker.getValue(), dataArea, AxisLocation.BOTTOM);
        Line2D line = new Line2D.Double(x, dataArea.getMinY(), x, dataArea.getMaxY());
        Paint paint = marker.getOutlinePaint();
        Stroke stroke = marker.getOutlineStroke();
        g2.setPaint(paint != null ? paint : Plot.DEFAULT_OUTLINE_PAINT);
        g2.setStroke(stroke != null ? stroke : Plot.DEFAULT_OUTLINE_STROKE);
        g2.draw(line);

    }

    /**
     * Draws a horizontal line across the chart to represent a 'range marker'.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param rangeAxis  the range axis.
     * @param marker  the marker line.
     * @param dataArea  the axis data area.
     */
    public void drawRangeMarker(Graphics2D g2,
                                ContourPlot plot,
                                ValueAxis rangeAxis,
                                Marker marker,
                                Rectangle2D dataArea) {

        double value = marker.getValue();
        Range range = rangeAxis.getRange();
        if (!range.contains(value)) {
            return;
        }

        double y = rangeAxis.translateValueToJava2D(marker.getValue(), dataArea, AxisLocation.LEFT);
        Line2D line = new Line2D.Double(dataArea.getMinX(), y, dataArea.getMaxX(), y);
        Paint paint = marker.getOutlinePaint();
        Stroke stroke = marker.getOutlineStroke();
        g2.setPaint(paint != null ? paint : Plot.DEFAULT_OUTLINE_PAINT);
        g2.setStroke(stroke != null ? stroke : Plot.DEFAULT_OUTLINE_STROKE);
        g2.draw(line);

    }

    /**
     * Returns the clipPath.
     * @return ClipPath
     */
    public ClipPath getClipPath() {
        return clipPath;
    }

    /**
     * Sets the clipPath.
     * @param clipPath The clipPath to set
     */
    public void setClipPath(ClipPath clipPath) {
        this.clipPath = clipPath;
    }

    /**
     * Returns the ptSizePct.
     * @return double
     */
    public double getPtSizePct() {
        return ptSizePct;
    }

    /**
     * Returns the renderAsPoints.
     * @return boolean
     */
    public boolean isRenderAsPoints() {
        return renderAsPoints;
    }

    /**
     * Sets the ptSizePct.
     * @param ptSizePct The ptSizePct to set
     */
    public void setPtSizePct(double ptSizePct) {
        this.ptSizePct = ptSizePct;
    }

    /**
     * Sets the renderAsPoints.
     * @param renderAsPoints The renderAsPoints to set
     */
    public void setRenderAsPoints(boolean renderAsPoints) {
        this.renderAsPoints = renderAsPoints;
    }

    /**
     * Receives notification of a change to one of the plot's axes.
     *
     * @param event  information about the event.
     */
    public void axisChanged(AxisChangeEvent event) {
        Object source = event.getSource();
        if (source.equals(this.rangeAxis) || source.equals(this.domainAxis)) {
            ColorBar cba = (ColorBar) colorBar;
            if (colorBar.getAxis().isAutoRange()) {
                cba.getAxis().configure();
            }

        }
        super.axisChanged(event);
    }

    /**
     * Returns the visible z-range.
     *
     * @param data  the dataset.
     * @param x  the x range.
     * @param y  the y range.
     *
     * @return The range.
     */
    public Range visibleRange(ContourDataset data, Range x, Range y) {
        Range range = null;
        range = ((DefaultContourDataset) data).getZValueRange(x, y);
        return range;
    }

    /**
     * Returns the missingPaint.
     * @return Paint
     */
    public Paint getMissingPaint() {
        return missingPaint;
    }

    /**
     * Sets the missingPaint.
     * @param missingPaint The missingPaint to set
     */
    public void setMissingPaint(Paint missingPaint) {
        this.missingPaint = missingPaint;
    }

}
