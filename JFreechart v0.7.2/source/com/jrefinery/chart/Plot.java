/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
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
 * ---------
 * Plot.java
 * ---------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Sylvain Vieujot;
 *
 * $Id: Plot.java,v 1.1 2007/10/10 18:56:25 vauchers Exp $
 *
 * Changes (from 21-Jun-2001)
 * --------------------------
 * 21-Jun-2001 : Removed redundant JFreeChart parameter from constructors (DG);
 * 18-Sep-2001 : Updated header info and fixed DOS encoding problem (DG);
 * 19-Oct-2001 : Moved series paint and stroke methods from JFreeChart class (DG);
 * 23-Oct-2001 : Created renderer for LinePlot class (DG);
 * 07-Nov-2001 : Changed type names for ChartChangeEvent (DG);
 *               Tidied up some Javadoc comments (DG);
 * 13-Nov-2001 : Changes to allow for null axes on plots such as PiePlot (DG);
 *               Added plot/axis compatibility checks (DG);
 * 12-Dec-2001 : Changed constructors to protected, and removed unnecessary 'throws' clauses (DG);
 * 13-Dec-2001 : Added tooltips (DG);
 * 22-Jan-2002 : Added handleClick(...) method, as part of implementation for crosshairs (DG);
 *               Moved tooltips reference into ChartInfo class (DG);
 * 23-Jan-2002 : Added test for null axes in chartChanged(...) method, thanks to Barry Evans for
 *               the bug report (number 506979 on SourceForge) (DG);
 *               Added a zoom(...) method (DG);
 * 05-Feb-2002 : Updated setBackgroundPaint(), setOutlineStroke() and setOutlinePaint() to better
 *               handle null values, as suggested by Sylvain Vieujot (DG);
 * 06-Feb-2002 : Added background image, plus alpha transparency for background and foreground (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import com.jrefinery.chart.event.*;
import com.jrefinery.chart.tooltips.*;

/**
 * A 'plot' is a class that controls the visual representation of data - all the different types
 * of plots share a common structure that is defined by this base class.
 */
public abstract class Plot implements AxisChangeListener, ChartChangeListener {

    /** Useful constant representing zero. */
    public static final Number ZERO = new Integer(0);

    /** Useful constant for specifying the horizontal axis. */
    public static final int HORIZONTAL_AXIS = 0;

    /** Useful constant for specifying the vertical axis. */
    public static final int VERTICAL_AXIS = 1;

    /** The default insets. */
    protected static final Insets DEFAULT_INSETS = new Insets(2, 2, 2, 10);

    /** The default background color. */
    protected static final Paint DEFAULT_BACKGROUND_PAINT = Color.white;

    /** The default background alpha transparency. */
    protected static final float DEFAULT_BACKGROUND_ALPHA = 1.0f;

    /** The default outline stroke. */
    protected static final Stroke DEFAULT_OUTLINE_STROKE = new BasicStroke(1);

    /** The default outline color. */
    protected static final Paint DEFAULT_OUTLINE_PAINT = Color.gray;

    /** The default transparency. */
    protected static final float DEFAULT_FOREGROUND_ALPHA = 1.0f;

    /** The minimum width for the plot, any less space than this and it should not be drawn (not
     *  fully implemented). */
    protected static final int MINIMUM_WIDTH_TO_DRAW = 10;

    /** The minimum height for the plot, any less space than this and it should not be drawn (not
     *  fully implemented. */
    protected static final int MINIMUM_HEIGHT_TO_DRAW = 10;

    /** The chart that the plot belongs to. */
    protected JFreeChart chart;

    /** The vertical axis for the plot. */
    protected Axis verticalAxis;

    /** The horizontal axis for the plot. */
    protected Axis horizontalAxis;

    /** Amount of blank space around the plot area. */
    protected Insets insets;

    /** An optional color used to fill the plot background. */
    protected Paint backgroundPaint;

    /** An optional image for the plot background. */
    protected Image backgroundImage;

    /** The alpha transparency for the background paint. */
    protected float backgroundAlpha;

    /** The Stroke used to draw an outline around the plot. */
    protected Stroke outlineStroke;

    /** The Paint used to draw an outline around the plot. */
    protected Paint outlinePaint;

    /** Paint objects used to color each series in the chart. */
    protected Paint[] seriesPaint;

    /** Stroke objects used to draw each series in the chart. */
    protected Stroke[] seriesStroke;

    /** Paint objects used to draw the outline of each series in the chart. */
    protected Paint[] seriesOutlinePaint;

    /** Stroke objects used to draw the outline of each series in the chart. */
    protected Stroke[] seriesOutlineStroke;

    /** The alpha-transparency for the plot. */
    protected float foregroundAlpha;

    /** Storage for registered change listeners. */
    protected java.util.List listeners;

    /**
     * Constructs a new plot with the specified axes.
     * @param horizontalAxis The horizontal axis.
     * @param verticalAxis The vertical axis.
     */
    protected Plot(Axis horizontalAxis, Axis verticalAxis) {

        this(horizontalAxis, verticalAxis,
             DEFAULT_INSETS,
             DEFAULT_BACKGROUND_PAINT,
             null, // background image
             DEFAULT_BACKGROUND_ALPHA,
             DEFAULT_OUTLINE_STROKE,
             DEFAULT_OUTLINE_PAINT,
             DEFAULT_FOREGROUND_ALPHA);

    }

    /**
     * Constructs a new plot.
     * @param horizontalAxis The horizontal axis.
     * @param verticalAxis The vertical axis.
     * @param insets Amount of blank space around the plot area.
     * @param backgroundPaint An optional color for the plot's background.
     * @param backgroundImage An optional image for the plot's background.
     * @param backgroundAlpha Alpha-transparency for the plot's background.
     * @param outlineStroke The Stroke used to draw an outline around the plot.
     * @param outlinePaint The color used to draw an outline around the plot.
     * @param foregroundAlpha The alpha-transparency for the plot foreground.
     */
    protected Plot(Axis horizontalAxis, Axis verticalAxis,
                   Insets insets,
                   Paint backgroundPaint, Image backgroundImage, float backgroundAlpha,
                   Stroke outlineStroke, Paint outlinePaint,
                   float foregroundAlpha) {

	this.chart = null;

	this.horizontalAxis=horizontalAxis;
        if (horizontalAxis!=null) {
	    horizontalAxis.setPlot(this);
	    horizontalAxis.addChangeListener(this);
        }

	this.verticalAxis=verticalAxis;
        if (verticalAxis!=null) {
	    verticalAxis.setPlot(this);
	    verticalAxis.addChangeListener(this);
        }

	this.insets = insets;
	this.backgroundPaint = backgroundPaint;
        this.backgroundAlpha = backgroundAlpha;
        this.outlineStroke = outlineStroke;
	this.outlinePaint = outlinePaint;
        this.foregroundAlpha = foregroundAlpha;

      	this.seriesPaint = new Paint[] {Color.red, Color.blue, Color.green, Color.yellow,
                                        Color.cyan, Color.magenta, Color.orange, Color.pink,
                                        Color.lightGray};

	this.seriesStroke = new Stroke[] { new BasicStroke(1) };
	this.seriesOutlinePaint = new Paint[] { Color.gray };
	this.seriesOutlineStroke = new Stroke[] { new BasicStroke(0.5f) };

	this.listeners = new java.util.ArrayList();

    }

    /**
     * Returns a reference to the chart that this plot belongs to.
     * @return A reference to the chart that this plot belongs to (null possible).
     */
    public JFreeChart getChart() {
	return chart;
    }

    /**
     * Returns the specified axis.
     * @param select Determines the axis returned (use the constants HORIZONTAL_AXIS and
     *               VERTICAL_AXIS).
     * @see Plot#getHorizontalAxis
     * @see Plot#getVerticalAxis
     */
    public Axis getAxis(int select) {

	switch (select) {
    	    case HORIZONTAL_AXIS : return horizontalAxis;
	    case VERTICAL_AXIS : return verticalAxis;
	    default: return null;
	}

    }

    /**
     * Returns a reference to the horizontal axis.
     * @return A reference to the horizontal axis.
     * @see Plot#getAxis
     */
    public HorizontalAxis getHorizontalAxis() {
	return (HorizontalAxis)horizontalAxis;
    }

    /**
     * Returns a reference to the vertical axis.
     * @return A reference to the vertical axis.
     * @see Plot#getAxis
     */
    public VerticalAxis getVerticalAxis() {
	return (VerticalAxis)verticalAxis;
    }

    /**
     * Returns the insets for the plot area.
     * @return The insets for the plot area.
     */
    public Insets getInsets() {
	return this.insets;
    }

    /**
     * Sets the insets for the plot and notifies registered listeners that the plot has been
     * modified.
     * @param insets The new insets.
     */
    public void setInsets(Insets insets) {

        if (!this.insets.equals(insets)) {
	    this.insets = insets;
	    notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the background color of the plot area.
     * @return The background color of the plot area (possibly null).
     */
    public Paint getBackgroundPaint() {
	return this.backgroundPaint;
    }

    /**
     * Sets the background color of the plot area, and notifies registered listeners that the
     * plot has been modified.
     * @param paint The new background color (null permitted).
     */
    public void setBackgroundPaint(Paint paint) {

        if (paint==null) {
            if (this.backgroundPaint!=null) {
                this.backgroundPaint=null;
                notifyListeners(new PlotChangeEvent(this));
            }
        }
        else {
            if (this.backgroundPaint!=null) {
                if (this.backgroundPaint.equals(paint)) {
                    return;  // nothing to do
                }
            }
            this.backgroundPaint = paint;
	    notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the alpha transparency of the plot area background.
     * @return The alpha transparency of the plot area background.
     */
    public float getBackgroundAlpha() {
	return this.backgroundAlpha;
    }

    /**
     * Sets the alpha transparency of the plot area background, and notifies registered listeners
     * that the plot has been modified.
     * @param alpha The new alpha value.
     */
    public void setBackgroundAlpha(float alpha) {

        if (this.backgroundAlpha!=alpha) {
            this.backgroundAlpha=alpha;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    public void setBackgroundImage(Image image) {
        this.backgroundImage = image;
    }

    /**
     * Returns the pen/brush used to outline the plot area.
     * @return The pen/brush used to outline the plot area (possibly null).
     */
    public Stroke getOutlineStroke() {
	return this.outlineStroke;
    }

    /**
     * Sets the pen/brush used to outline the plot area, and notifies registered listeners that the
     * plot has been modified.
     * @param stroke The new outline pen/brush (null permitted).
     */
    public void setOutlineStroke(Stroke stroke) {

        if (stroke==null) {
            if (this.outlineStroke!=null) {
                this.outlineStroke=null;
                notifyListeners(new PlotChangeEvent(this));
            }
        }
        else {
            if (this.outlineStroke!=null) {
                if (this.outlineStroke.equals(stroke)) {
                    return;  // nothing to do
                }
            }
            this.outlineStroke = stroke;
	    notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the color used to draw the outline of the plot area.
     * @return The color used to draw the outline of the plot area (possibly null).
     */
    public Paint getOutlinePaint() {
	return this.outlinePaint;
    }

    /**
     * Sets the color of the outline of the plot area, and notifies registered listeners that the
     * Plot has been modified.
     * @param paint The new outline paint (null permitted).
     */
    public void setOutlinePaint(Paint paint) {

        if (paint==null) {
            if (this.outlinePaint!=null) {
                this.outlinePaint=null;
                notifyListeners(new PlotChangeEvent(this));
            }
        }
        else {
            if (this.outlinePaint!=null) {
                if (this.outlinePaint.equals(paint)) {
                    return;  // nothing to do
                }
            }
            this.outlinePaint = paint;
	    notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the alpha-transparency for the plot foreground.
     * @return The alpha-transparency for the plot foreground.
     */
    public float getForegroundAlpha() {
        return this.foregroundAlpha;
    }

    /**
     * Sets the alpha-transparency for the plot.
     * @param alpha The new alpha transparency.
     */
    public void setForegroundAlpha(float alpha) {

        if (this.foregroundAlpha != alpha) {
            this.foregroundAlpha = alpha;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Sets a reference back to the chart that this plot belongs to.  Reconfigures the axes
     * according to the chart's data source.
     * @param chart The chart that the plot belongs to.
     */
    public void setChart(JFreeChart chart) {

	// if replacing an existing chart, the plot may be a registered listener...
        if (this.chart!=null) {
            chart.removeChangeListener(this);
        }

	this.chart = chart;

	// new chart means new data source, so reconfigure axes...
	if (verticalAxis!=null) verticalAxis.configure();
	if (horizontalAxis!=null) horizontalAxis.configure();

    }

    /**
     * Sets the vertical axis for the plot.
     * <P>
     * An exception is thrown if the new axis and the plot are not mutually compatible.
     * @param axis The new axis (null permitted).
     */
    public void setVerticalAxis(Axis axis) throws AxisNotCompatibleException {

        if (isCompatibleVerticalAxis(axis)) {

            if (axis!=null) {
                try {
                    axis.setPlot(this);
                }
                catch (PlotNotCompatibleException e) {
                    throw new AxisNotCompatibleException("Plot.setVerticalAxis(...): "
                                                        +"plot not compatible with axis.");
                }
                axis.addChangeListener(this);
            }

            // plot is likely registered as a listener with the existing axis...
            if (this.verticalAxis!=null) {
	        this.verticalAxis.removeChangeListener(this);
            }

            this.verticalAxis = axis;

        }
        else throw new AxisNotCompatibleException("Plot.setVerticalAxis(...): "
                                                 +"axis not compatible with plot.");

    }

    /**
     * Sets the horizontal axis for the plot (this must be compatible with the plot type or an
     * exception is thrown).
     * @param axis The new axis;
     */
    public void setHorizontalAxis(Axis axis) throws AxisNotCompatibleException {

        if (isCompatibleHorizontalAxis(axis)) {

            if (axis!=null) {

                try {
                    axis.setPlot(this);
                }
                catch (PlotNotCompatibleException e) {
                    throw new AxisNotCompatibleException("Plot.setHorizontalAxis(...): "
                                                        +"plot not compatible with axis.");
                }
                axis.addChangeListener(this);
            }

            // plot is likely registered as a listener with the existing axis...
            if (this.horizontalAxis!=null) {
	        this.horizontalAxis.removeChangeListener(this);
            }

            this.horizontalAxis = axis;

        }
        else throw new AxisNotCompatibleException("Plot.setHorizontalAxis(...): "
                                                 +"axis not compatible with plot.");

    }

    /**
     * Returns the Paint used to color any shapes for the specified series.
     * @param index The index of the series of interest (zero-based);
     */
    public Paint getSeriesPaint(int index) {
	return seriesPaint[index % seriesPaint.length];
    }

    /**
     * Sets the paint used to color any shapes representing series, and notifies registered
     * listeners that the plot has been modified.
     * @param paint An array of Paint objects used to color series;
     */
    public void setSeriesPaint(Paint[] paint) {
	this.seriesPaint = paint;
	notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the Stroke used to draw any shapes for the specified series.
     * @param index The index of the series of interest (zero-based);
     */
    public Stroke getSeriesStroke(int index) {
	return seriesStroke[index % seriesStroke.length];
    }

    /**
     * Sets the stroke used to draw any shapes representing series, and notifies registered
     * listeners that the chart has been modified.
     * @param stroke An array of Stroke objects used to draw series;
     */
    public void setSeriesStroke(Stroke[] stroke) {
	this.seriesStroke = stroke;
	notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the Paint used to outline any shapes for the specified series.
     * @param index The index of the series of interest (zero-based);
     */
    public Paint getSeriesOutlinePaint(int index) {
	return seriesOutlinePaint[index % seriesOutlinePaint.length];
    }

    /**
     * Sets the paint used to outline any shapes representing series, and notifies registered
     * listeners that the chart has been modified.
     * @param paint An array of Paint objects for drawing the outline of series shapes;
     */
    public void setSeriesOutlinePaint(Paint[] paint) {
	this.seriesOutlinePaint = paint;
	notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the Stroke used to outline any shapes for the specified series.
     * @param index The index of the series of interest (zero-based);
     */
    public Stroke getSeriesOutlineStroke(int index) {
	return seriesOutlineStroke[index % seriesOutlinePaint.length];
    }

    /**
     * Sets the stroke used to draw any shapes representing series, and notifies registered
     * listeners that the chart has been modified.
     * @param stroke An array of Stroke objects;
     */
    public void setSeriesOutlineStroke(Stroke[] stroke) {
	this.seriesOutlineStroke = stroke;
	notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns a Shape that can be used in plotting data. Should allow a plugin object to
     * determine the shape (optionally)...
     */
    public Shape getShape(int series, int item, double x, double y, double scale) {

       // return new Rectangle2D.Double(x-0.5*scale, y-0.5*scale, scale, scale);
        return new Ellipse2D.Double(x-0.5*scale, y-0.5*scale, scale, scale);
    }

    /**
     * Returns a Shape that can be used in plotting data.  Should allow a plug-in object to
     * determine the shape...
     */
    public Shape getShape(int series, Object category, double x, double y, double scale) {

       // return new Rectangle2D.Double(x-0.5*scale, y-0.5*scale, scale, scale);
        return new Ellipse2D.Double(x-0.5*scale, y-0.5*scale, scale, scale);
    }

    /**
     * Notifies all registered listeners that the plot has been modified.
     * @param event Information about the change event.
     */
    public void notifyListeners(PlotChangeEvent event) {
	java.util.Iterator iterator = listeners.iterator();
	while (iterator.hasNext()) {
	    PlotChangeListener listener = (PlotChangeListener)iterator.next();
	    listener.plotChanged(event);
	}
    }

    /**
     * Registers an object for notification of changes to the plot.
     * @param listener The object to be registered.
     */
    public void addChangeListener(PlotChangeListener listener) {
	listeners.add(listener);
    }

    /**
     * Unregisters an object for notification of changes to the plot.
     * @param listener The object to be unregistered.
     */
    public void removeChangeListener(PlotChangeListener listener) {
	listeners.remove(listener);
    }

    /**
     * Checks the compatibility of a horizontal axis, returning true if the axis is compatible with
     * the plot, and false otherwise.
     * @param axis The horizontal axis.
     */
    public abstract boolean isCompatibleHorizontalAxis(Axis axis);

    /**
     * Checks the compatibility of a vertical axis, returning true if the axis is compatible with
     * the plot, and false otherwise.
     * @param axis The vertical axis;
     */
    public abstract boolean isCompatibleVerticalAxis(Axis axis);

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     * <P>
     * To support 'tool tips' for charts that are drawn on-screen, the tooltips parameter can
     * be supplied...it will collect tooltips as the plot is being drawn.  The JFreeChartPanel will
     * then query the tooltips object when it requires a tooltip to display.  If you pass null for
     * this parameter, then tooltips are not generated.
     * *
     * @param g2 The graphics device.
     * @param drawArea The area within which the plot should be drawn.
     * @param info An object for collecting information about the drawing of the chart.
     */
    public abstract void draw(Graphics2D g2, Rectangle2D drawArea, DrawInfo info);

    /**
     * Draw the plot outline and background.
     * @param g2 The graphics device.
     * @param area The area within which the plot should be drawn.
     */
    public void drawOutlineAndBackground(Graphics2D g2, Rectangle2D area) {

	if (backgroundPaint!=null) {
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                       this.backgroundAlpha));
	    g2.setPaint(backgroundPaint);
            g2.fill(area);
            g2.setComposite(originalComposite);
	}

        if (backgroundImage!=null) {
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
            g2.drawImage(this.backgroundImage,
                         (int)area.getX(), (int)area.getY(),
                         (int)area.getWidth(), (int)area.getHeight(), null);
            g2.setComposite(originalComposite);
        }

	if ((outlineStroke!=null) && (outlinePaint!=null)) {
	    g2.setStroke(outlineStroke);
	    g2.setPaint(outlinePaint);
	    g2.draw(area);
	}

    }

    /**
     * Handles a 'click' on the plot.  Since the plot does not maintain any information about where
     * it has been drawn, the plot area is supplied as an argument.
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param plotArea The area in which the plot is assumed to be drawn.
     */
    public void handleClick(int x, int y, DrawInfo info) {

        // translate x into a data value
        // if the axis is a value axis, set the crosshair value
        if (horizontalAxis instanceof ValueAxis) {
            ValueAxis hva = (ValueAxis)horizontalAxis;
            double xx = hva.translateJava2DtoValue(x, info.getDataArea());
        }

        // translate the y into a data value
        // if the axis is a value axis, set the crosshair value
        if (verticalAxis instanceof ValueAxis) {
            ValueAxis vva = (ValueAxis)verticalAxis;
            double yy = vva.translateJava2DtoValue(y, info.getDataArea());
        }
    }

    public void zoom(double percent) {
        // subclasses override or accept that zooming does nothing
    }

    /**
     * Receives notification of a change to one of the plot's axes.
     * @param event Information about the event (not used here).
     */
    public void axisChanged(AxisChangeEvent event) {
	notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Receives notification of a change to a (the) chart.
     * <P>
     * Reacts to dataset changes by reconfiguring the axes.
     * @param event Information about the chart change event.
     */
    public void chartChanged(ChartChangeEvent event) {

	if (event.getType()==ChartChangeEvent.NEW_DATASET) {
	    if (verticalAxis!=null) {
                verticalAxis.configure();
            }
            if (horizontalAxis!=null) {
	        horizontalAxis.configure();
            }
	}

	if (event.getType()==ChartChangeEvent.UPDATED_DATASET) {
	    if (verticalAxis!=null) {
                verticalAxis.configure();
            }
            if (horizontalAxis!=null) {
	        horizontalAxis.configure();
            }
	}

    }

    /**
     * Returns a short string describing the plot type.
     * <P>
     * Note: this gets used in the chart property editing user interface, but there needs to be
     * a better mechanism for identifying the plot type.
     * @return A short string describing the plot type.
     */
    public abstract String getPlotType();

}
