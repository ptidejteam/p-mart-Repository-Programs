/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
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
 *                   Jeremy Bowman;
 *                   Andreas Schneider;
 *
 * $Id: Plot.java,v 1.1 2007/10/10 19:01:18 vauchers Exp $
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
 * 06-Mar-2002 : Added AxisConstants interface (DG);
 * 26-Mar-2002 : Changed zoom method from empty to abstract (DG);
 * 23-Apr-2002 : Moved dataset from JFreeChart class (DG);
 * 11-May-2002 : Added ShapeFactory interface for getShape() methods, contributed by Jeremy
 *               Bowman (DG);
 * 28-May-2002 : Fixed bug in setSeriesPaint(int, Paint) for subplots (AS);
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
import java.util.List;
import java.util.Iterator;
import com.jrefinery.data.Dataset;
import com.jrefinery.data.DatasetChangeEvent;
import com.jrefinery.data.DatasetChangeListener;
import com.jrefinery.chart.event.AxisChangeEvent;
import com.jrefinery.chart.event.AxisChangeListener;
import com.jrefinery.chart.event.ChartChangeEvent;
import com.jrefinery.chart.event.ChartChangeListener;
import com.jrefinery.chart.event.PlotChangeEvent;
import com.jrefinery.chart.event.PlotChangeListener;

/**
 * The base class for all plots in JFreeChart.  The JFreeChart class delegates the drawing of axes
 * and data to the plot.  This base class provides facilities common to most plot types.
 */
public abstract class Plot implements AxisChangeListener,
                                      DatasetChangeListener,
                                      AxisConstants {

    /** Useful constant representing zero. */
    public static final Number ZERO = new Integer(0);

    /** The default insets. */
    protected static final Insets DEFAULT_INSETS = new Insets(2, 2, 2, 10);

    /** The default outline stroke. */
    protected static final Stroke DEFAULT_OUTLINE_STROKE = new BasicStroke(1);

    /** The default outline color. */
    protected static final Paint DEFAULT_OUTLINE_PAINT = Color.gray;

    /** The default foreground alpha transparency. */
    protected static final float DEFAULT_FOREGROUND_ALPHA = 1.0f;

    /** The default background alpha transparency. */
    protected static final float DEFAULT_BACKGROUND_ALPHA = 1.0f;

    /** The default background color. */
    protected static final Paint DEFAULT_BACKGROUND_PAINT = Color.white;

    /** The minimum width for the plot, any less space than this and it should not be drawn (not
     *  fully implemented). */
    protected static final int MINIMUM_WIDTH_TO_DRAW = 10;

    /** The minimum height for the plot, any less space than this and it should not be drawn (not
     *  fully implemented. */
    protected static final int MINIMUM_HEIGHT_TO_DRAW = 10;

    /** The data. */
    protected Dataset dataset;

    /**
     * The index of the first series.  This defaults to zero...when you combine plots you
     * might want to set this to a higher index to ensure that series colors are different.
     */
    protected int firstSeriesIndex = 0;

    /** Amount of blank space around the plot area. */
    protected Insets insets;

    /** The Stroke used to draw an outline around the plot. */
    protected Stroke outlineStroke;

    /** The Paint used to draw an outline around the plot. */
    protected Paint outlinePaint;

    /** An optional color used to fill the plot background. */
    protected Paint backgroundPaint;

    /** An optional image for the plot background. */
    protected Image backgroundImage;

    /** The alpha-transparency for the plot. */
    protected float foregroundAlpha;

    /** The alpha transparency for the background paint. */
    protected float backgroundAlpha;

    /** Paint objects used to color each series in the chart. */
    protected Paint[] seriesPaint;

    /** Stroke objects used to draw each series in the chart. */
    protected Stroke[] seriesStroke;

    /** Paint objects used to draw the outline of each series in the chart. */
    protected Paint[] seriesOutlinePaint;

    /** Stroke objects used to draw the outline of each series in the chart. */
    protected Stroke[] seriesOutlineStroke;

    /** Storage for registered change listeners. */
    protected List listeners;

    /** Factory for shapes used to represent data points */
    protected ShapeFactory shapeFactory;

    /**
     * Constructs a new plot with the specified axes.
     *
     * @param data The dataset.
     * @param horizontalAxis The horizontal axis.
     * @param verticalAxis The vertical axis.
     */
    protected Plot(Dataset data) {

        this(data,
             DEFAULT_INSETS,
             DEFAULT_BACKGROUND_PAINT,
             null, // background image
             DEFAULT_BACKGROUND_ALPHA,
             DEFAULT_OUTLINE_STROKE,
             DEFAULT_OUTLINE_PAINT,
             DEFAULT_FOREGROUND_ALPHA
             );

    }

    /**
     * Constructs a new plot.
     *
     * @param data The dataset.
     * @param insets Amount of blank space around the plot area.
     * @param backgroundPaint An optional color for the plot's background.
     * @param backgroundImage An optional image for the plot's background.
     * @param backgroundAlpha Alpha-transparency for the plot's background.
     * @param outlineStroke The Stroke used to draw an outline around the plot.
     * @param outlinePaint The color used to draw an outline around the plot.
     * @param foregroundAlpha The alpha-transparency for the plot foreground.
     */
    protected Plot(Dataset data,
                   Insets insets,
                   Paint backgroundPaint, Image backgroundImage, float backgroundAlpha,
                   Stroke outlineStroke, Paint outlinePaint,
                   float foregroundAlpha) {

        // set the data and register to receive change notifications...
        this.dataset = data;
        if (data!=null) {
            data.addChangeListener(this);
        }

//        this.horizontalAxis=horizontalAxis;
//        if (horizontalAxis!=null) {
//            horizontalAxis.setPlot(this);
//            horizontalAxis.addChangeListener(this);
//        }

//        this.verticalAxis=verticalAxis;
//        if (verticalAxis!=null) {
 //           verticalAxis.setPlot(this);
//            verticalAxis.addChangeListener(this);
//        }

        this.insets = insets;
        this.backgroundPaint = backgroundPaint;
        this.backgroundAlpha = backgroundAlpha;
        this.outlineStroke = outlineStroke;
        this.outlinePaint = outlinePaint;
        this.foregroundAlpha = foregroundAlpha;

        this.seriesStroke = new Stroke[] { new BasicStroke(1.0f) };
        this.seriesPaint = new Paint[] {Color.red, Color.blue, Color.green, Color.yellow,
                                        Color.orange, Color.magenta, Color.cyan, Color.pink,
                                        Color.lightGray};

        this.seriesOutlinePaint = new Paint[] { Color.gray };
        this.seriesOutlineStroke = new Stroke[] { new BasicStroke(0.5f) };

        this.listeners = new java.util.ArrayList();
        this.shapeFactory = new DefaultShapeFactory();

    }

    /**
     * Returns a short string describing the plot type.
     * <P>
     * Note: this gets used in the chart property editing user interface, but there needs to be
     * a better mechanism for identifying the plot type.
     * @return A short string describing the plot type.
     */
    public abstract String getPlotType();

    /**
     * Returns the dataset for the plot.
     *
     * @return The dataset.
     */
    public Dataset getDataset() {
        return dataset;
    }

    /**
     * Sets the data for the chart, replacing any existing data.
     * <P>
     * Registered listeners are notified that the chart has been modified.
     *
     * @param data The new dataset.
     */
    public void setDataset(Dataset data) {

        // if there is an existing dataset, remove the chart from the list of change listeners...
        Dataset existing = this.dataset;
        if (existing!=null) {
            existing.removeChangeListener(this);
        }

        // set the new dataset, and register the chart as a change listener...
        this.dataset = data;
        if (this.dataset!=null) {
            dataset.addChangeListener(this);
        }

        // notify chart change listeners...
        PlotChangeEvent event = new PlotChangeEvent(this);
        notifyListeners(event);

    }

    /**
     * Returns true if this plot is part of a combined plot structure, and false otherwise.
     *
     * @return A flag indicating if this plot is a subplot.
     */
    public boolean isSubplot() {
        return false;
    }

    /**
     * Returns the index of the first series.
     *
     * @return The index.
     */
    public int getFirstSeriesIndex() {
        return this.firstSeriesIndex;
    }

    /**
     * Sets the index of the first series.  For a single plot, this defaults to zero, but when
     * you combine plots this will be set to a higher value to ensure that all series have a
     * unique index (and therefore color).
     *
     * @param index The new index.
     */
    public void setFirstSeriesIndex(int index) {
        this.firstSeriesIndex = index;
    }

    /**
     * Returns the insets for the plot area.
     *
     * @return The insets.
     */
    public Insets getInsets() {
        return this.insets;
    }

    /**
     * Sets the insets for the plot and notifies registered listeners that the plot has been
     * modified.
     *
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
     *
     * @return The background color (null possible).
     */
    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    /**
     * Sets the background color of the plot area, and notifies registered listeners that the
     * plot has been modified.
     *
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
     *
     * @return The alpha transparency.
     */
    public float getBackgroundAlpha() {
        return this.backgroundAlpha;
    }

    /**
     * Sets the alpha transparency of the plot area background, and notifies registered listeners
     * that the plot has been modified.
     *
     * @param alpha The new alpha value.
     */
    public void setBackgroundAlpha(float alpha) {

        if (this.backgroundAlpha!=alpha) {
            this.backgroundAlpha=alpha;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Sets the background image for the plot.
     *
     * @param image The background image.
     */
    public void setBackgroundImage(Image image) {
        this.backgroundImage = image;
    }

    /**
     * Returns the pen/brush used to outline the plot area.
     *
     * @return The outline stroke (possibly null).
     */
    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    /**
     * Sets the pen/brush used to outline the plot area, and notifies registered listeners that the
     * plot has been modified.
     *
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
     *
     * @return The color (possibly null).
     */
    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    /**
     * Sets the color of the outline of the plot area, and notifies registered listeners that the
     * Plot has been modified.
     *
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
     *
     * @return The alpha-transparency.
     */
    public float getForegroundAlpha() {
        return this.foregroundAlpha;
    }

    /**
     * Sets the alpha-transparency for the plot.
     *
     * @param alpha The new alpha transparency.
     */
    public void setForegroundAlpha(float alpha) {

        if (this.foregroundAlpha != alpha) {
            this.foregroundAlpha = alpha;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    public abstract List getLegendItemLabels();

    /**
     * Sets the horizontal axis for the plot (this must be compatible with the plot type or an
     * exception is thrown).
     * @param axis The new axis;
     */
//    public void setHorizontalAxis(Axis axis) throws AxisNotCompatibleException {

//        if (isCompatibleHorizontalAxis(axis)) {

//            if (axis!=null) {

//                try {
//                    axis.setPlot(this);
//                }
//                catch (PlotNotCompatibleException e) {
//                    throw new AxisNotCompatibleException("Plot.setHorizontalAxis(...): "
//                                                        +"plot not compatible with axis.");
//                }
//                axis.addChangeListener(this);
//            }

            // plot is likely registered as a listener with the existing axis...
//            if (this.horizontalAxis!=null) {
//                this.horizontalAxis.removeChangeListener(this);
//            }

//            this.horizontalAxis = axis;

//        }
//        else throw new AxisNotCompatibleException("Plot.setHorizontalAxis(...): "
//                                                 +"axis not compatible with plot.");

//    }

    /**
     * Sets the vertical axis for the plot.
     * <P>
     * An exception is thrown if the new axis and the plot are not mutually compatible.
     * @param axis The new axis (null permitted).
     */
//    public void setVerticalAxis(Axis axis) throws AxisNotCompatibleException {

//        if (isCompatibleVerticalAxis(axis)) {

//            if (axis!=null) {
//                try {
//                    axis.setPlot(this);
//                }
//                catch (PlotNotCompatibleException e) {
//                    throw new AxisNotCompatibleException("Plot.setVerticalAxis(...): "
//                                                        +"plot not compatible with axis.");
//                }
//                axis.addChangeListener(this);
//            }

            // plot is likely registered as a listener with the existing axis...
//            if (this.verticalAxis!=null) {
//                this.verticalAxis.removeChangeListener(this);
//            }

//            this.verticalAxis = axis;

//        }
//        else throw new AxisNotCompatibleException("Plot.setVerticalAxis(...): "
//                                                 +"axis not compatible with plot.");

//    }

    /**
     * Returns a Paint object used as the main color for a series.
     *
     * @param index The series index (zero-based).
     */
    public Paint getSeriesPaint(int index) {

        if (isSubplot()) {
            index = index+this.firstSeriesIndex;
        }
        return seriesPaint[index % seriesPaint.length];

    }

    /**
     * Sets the paint used to color any shapes representing series, and notifies registered
     * listeners that the plot has been modified.
     * @param paint An array of Paint objects used to color series.
     */
    public void setSeriesPaint(Paint[] paint) {
        this.seriesPaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Sets the paint used to color any shapes representing a specific series, and notifies registered
     * listeners that the plot has been modified.
     * @param index The series index (zero-based)
     * @param paint An array of Paint objects used to color series.
     */
    public void setSeriesPaint(int index, Paint paint) {
      if (isSubplot()) {
          index = index+this.firstSeriesIndex;
      }
      this.seriesPaint[index] = paint;
      notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the Stroke used to draw any shapes for a series.
     *
     * @param index The series (zero-based index).
     */
    public Stroke getSeriesStroke(int index) {
        return seriesStroke[index % seriesStroke.length];
    }

    /**
     * Sets the stroke used to draw any shapes representing series, and notifies registered
     * listeners that the chart has been modified.
     * @param stroke An array of Stroke objects used to draw series.
     */
    public void setSeriesStroke(Stroke[] stroke) {
        this.seriesStroke = stroke;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Sets the stroke used to draw any shapes representing a specific series, and notifies registered
     * listeners that the chart has been modified.
     * @param index The series index (zero-based)
     * @param stroke An array of Stroke objects used to draw series.
     */
    public void setSeriesStroke(int index, Stroke stroke) {
        this.seriesStroke[index] = stroke;
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
     * Sets the stroke used to draw any shapes representing a specific series, and notifies registered
     * listeners that the chart has been modified.
     * @param index The series index (zero-based)
     * @param stroke An array of Stroke objects;
     */
    public void setSeriesOutlineStroke(int index, Stroke stroke) {
        this.seriesOutlineStroke[index] = stroke;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the object used to generate shapes for marking data points
     */
    public ShapeFactory getShapeFactory() {
        return shapeFactory;
    }

    /**
     * Sets the object used to generate shapes for marking data points
     * @param factory The new shape factory.
     */
    public void setShapeFactory(ShapeFactory factory) {
        this.shapeFactory = factory;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns a Shape that can be used in plotting data.  Used in XYPlots.
     */
    public Shape getShape(int series, int item, double x, double y, double scale) {

        Shape shape = null;
        if (shapeFactory != null) {
            shape = shapeFactory.getShape(series, item, x, y, scale);
        }
        return shape;

    }

    /**
     * Returns a Shape that can be used in plotting data.  Should allow a plug-in object to
     * determine the shape...
     */
    public Shape getShape(int series, Object category, double x, double y, double scale) {

        Shape shape = null;
        if (shapeFactory != null) {
            shape = shapeFactory.getShape(series, category, x, y, scale);
        }
        return shape;

    }

    /**
     * Notifies all registered listeners that the plot has been modified.
     *
     * @param event Information about the change event.
     */
    public void notifyListeners(PlotChangeEvent event) {

        Iterator iterator = listeners.iterator();
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
    public abstract void draw(Graphics2D g2, Rectangle2D drawArea, ChartRenderingInfo info);

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
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param plotArea The area in which the plot is assumed to be drawn.
     */
    public void handleClick(int x, int y, ChartRenderingInfo info) {


    }

    /**
     * Performs a zoom on the plot.  Subclasses will implement a behaviour that is appropriate to
     * the type of plot.
     *
     * @param The zoom percentage.
     */
    public abstract void zoom(double percent);

    /**
     * Receives notification of a change to one of the plot's axes.
     * @param event Information about the event (not used here).
     */
    public void axisChanged(AxisChangeEvent event) {
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Receives notification of a change to the plot's dataset.
     * <P>
     * The chart reacts by passing on a chart change event to all registered listeners.
     *
     * @param event Information about the event (not used here).
     */
    public void datasetChanged(DatasetChangeEvent event) {

//        if (this.horizontalAxis!=null) {
//            this.horizontalAxis.configure();
//        }

//        if (this.verticalAxis!=null) {
//            this.verticalAxis.configure();
//        }

        PlotChangeEvent newEvent = new PlotChangeEvent(this);
        notifyListeners(newEvent);

    }

}
