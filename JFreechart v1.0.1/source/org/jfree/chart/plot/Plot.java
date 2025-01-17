/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
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
 * ---------
 * Plot.java
 * ---------
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Sylvain Vieujot;
 *                   Jeremy Bowman;
 *                   Andreas Schneider;
 *                   Gideon Krause;
 *                   Nicolas Brodu;
 *
 * $Id: Plot.java,v 1.1 2007/10/10 20:17:03 vauchers Exp $
 *
 * Changes (from 21-Jun-2001)
 * --------------------------
 * 21-Jun-2001 : Removed redundant JFreeChart parameter from constructors (DG);
 * 18-Sep-2001 : Updated header info and fixed DOS encoding problem (DG);
 * 19-Oct-2001 : Moved series paint and stroke methods from JFreeChart 
 *               class (DG);
 * 23-Oct-2001 : Created renderer for LinePlot class (DG);
 * 07-Nov-2001 : Changed type names for ChartChangeEvent (DG);
 *               Tidied up some Javadoc comments (DG);
 * 13-Nov-2001 : Changes to allow for null axes on plots such as PiePlot (DG);
 *               Added plot/axis compatibility checks (DG);
 * 12-Dec-2001 : Changed constructors to protected, and removed unnecessary 
 *               'throws' clauses (DG);
 * 13-Dec-2001 : Added tooltips (DG);
 * 22-Jan-2002 : Added handleClick() method, as part of implementation for 
 *               crosshairs (DG);
 *               Moved tooltips reference into ChartInfo class (DG);
 * 23-Jan-2002 : Added test for null axes in chartChanged() method, thanks 
 *               to Barry Evans for the bug report (number 506979 on 
 *               SourceForge) (DG);
 *               Added a zoom() method (DG);
 * 05-Feb-2002 : Updated setBackgroundPaint(), setOutlineStroke() and 
 *               setOutlinePaint() to better handle null values, as suggested 
 *               by Sylvain Vieujot (DG);
 * 06-Feb-2002 : Added background image, plus alpha transparency for background
 *               and foreground (DG);
 * 06-Mar-2002 : Added AxisConstants interface (DG);
 * 26-Mar-2002 : Changed zoom method from empty to abstract (DG);
 * 23-Apr-2002 : Moved dataset from JFreeChart class (DG);
 * 11-May-2002 : Added ShapeFactory interface for getShape() methods, 
 *               contributed by Jeremy Bowman (DG);
 * 28-May-2002 : Fixed bug in setSeriesPaint(int, Paint) for subplots (AS);
 * 25-Jun-2002 : Removed redundant imports (DG);
 * 30-Jul-2002 : Added 'no data' message for charts with null or empty 
 *               datasets (DG);
 * 21-Aug-2002 : Added code to extend series array if necessary (refer to 
 *               SourceForge bug id 594547 for details) (DG);
 * 17-Sep-2002 : Fixed bug in getSeriesOutlineStroke() method, reported by 
 *               Andreas Schroeder (DG);
 * 23-Sep-2002 : Added getLegendItems() abstract method (DG);
 * 24-Sep-2002 : Removed firstSeriesIndex, subplots now use their own paint 
 *               settings, there is a new mechanism for the legend to collect 
 *               the legend items (DG);
 * 27-Sep-2002 : Added dataset group (DG);
 * 14-Oct-2002 : Moved listener storage into EventListenerList.  Changed some 
 *               abstract methods to empty implementations (DG);
 * 28-Oct-2002 : Added a getBackgroundImage() method (DG);
 * 21-Nov-2002 : Added a plot index for identifying subplots in combined and 
 *               overlaid charts (DG);
 * 22-Nov-2002 : Changed all attributes from 'protected' to 'private'.  Added 
 *               dataAreaRatio attribute from David M O'Donnell's code (DG);
 * 09-Jan-2003 : Integrated fix for plot border contributed by Gideon 
 *               Krause (DG);
 * 17-Jan-2003 : Moved to com.jrefinery.chart.plot (DG);
 * 23-Jan-2003 : Removed one constructor (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 14-Jul-2003 : Moved the dataset and secondaryDataset attributes to the 
 *               CategoryPlot and XYPlot classes (DG);
 * 21-Jul-2003 : Moved DrawingSupplier from CategoryPlot and XYPlot up to this 
 *               class (DG);
 * 20-Aug-2003 : Implemented Cloneable (DG);
 * 11-Sep-2003 : Listeners and clone (NB);
 * 29-Oct-2003 : Added workaround for font alignment in PDF output (DG);
 * 03-Dec-2003 : Modified draw method to accept anchor (DG);
 * 12-Mar-2004 : Fixed clipping bug in drawNoDataMessage() method (DG);
 * 07-Apr-2004 : Modified string bounds calculation (DG);
 * 04-Nov-2004 : Added default shapes for legend items (DG);
 * 25-Nov-2004 : Some changes to the clone() method implementation (DG);
 * 23-Feb-2005 : Implemented new LegendItemSource interface (and also
 *               PublicCloneable) (DG);
 * 21-Apr-2005 : Replaced Insets with RectangleInsets (DG);
 * 05-May-2005 : Removed unused draw() method (DG);
 * 06-Jun-2005 : Fixed bugs in equals() method (DG);
 * 01-Sep-2005 : Moved dataAreaRatio from here to ContourPlot (DG);
 *
 */

package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.event.EventListenerList;

import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.event.ChartChangeEventType;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.io.SerialUtilities;
import org.jfree.text.G2TextMeasurer;
import org.jfree.text.TextBlock;
import org.jfree.text.TextBlockAnchor;
import org.jfree.text.TextUtilities;
import org.jfree.ui.Align;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

/**
 * The base class for all plots in JFreeChart.  The 
 * {@link org.jfree.chart.JFreeChart} class delegates the drawing of axes and 
 * data to the plot.  This base class provides facilities common to most plot 
 * types.
 */
public abstract class Plot implements AxisChangeListener,
                                      DatasetChangeListener,
                                      LegendItemSource,
                                      PublicCloneable,
                                      Cloneable,
                                      Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -8831571430103671324L;
    
    /** Useful constant representing zero. */
    public static final Number ZERO = new Integer(0);

    /** The default insets. */
    public static final RectangleInsets DEFAULT_INSETS 
        = new RectangleInsets(4.0, 8.0, 4.0, 8.0);

    /** The default outline stroke. */
    public static final Stroke DEFAULT_OUTLINE_STROKE = new BasicStroke(0.5f);

    /** The default outline color. */
    public static final Paint DEFAULT_OUTLINE_PAINT = Color.gray;

    /** The default foreground alpha transparency. */
    public static final float DEFAULT_FOREGROUND_ALPHA = 1.0f;

    /** The default background alpha transparency. */
    public static final float DEFAULT_BACKGROUND_ALPHA = 1.0f;

    /** The default background color. */
    public static final Paint DEFAULT_BACKGROUND_PAINT = Color.white;

    /** The minimum width at which the plot should be drawn. */
    public static final int MINIMUM_WIDTH_TO_DRAW = 10;

    /** The minimum height at which the plot should be drawn. */
    public static final int MINIMUM_HEIGHT_TO_DRAW = 10;
    
    /** A default box shape for legend items. */
    public static final Shape DEFAULT_LEGEND_ITEM_BOX 
        = new Rectangle2D.Double(-4.0, -4.0, 8.0, 8.0);
    
    /** A default circle shape for legend items. */
    public static final Shape DEFAULT_LEGEND_ITEM_CIRCLE 
        = new Ellipse2D.Double(-4.0, -4.0, 8.0, 8.0);

    /** The parent plot (<code>null</code> if this is the root plot). */
    private Plot parent;

    /** The dataset group (to be used for thread synchronisation). */
    private DatasetGroup datasetGroup;

    /** The message to display if no data is available. */
    private String noDataMessage;

    /** The font used to display the 'no data' message. */
    private Font noDataMessageFont;

    /** The paint used to draw the 'no data' message. */
    private transient Paint noDataMessagePaint;

    /** Amount of blank space around the plot area. */
    private RectangleInsets insets;

    /** The Stroke used to draw an outline around the plot. */
    private transient Stroke outlineStroke;

    /** The Paint used to draw an outline around the plot. */
    private transient Paint outlinePaint;

    /** An optional color used to fill the plot background. */
    private transient Paint backgroundPaint;

    /** An optional image for the plot background. */
    private transient Image backgroundImage;  // not currently serialized

    /** The alignment for the background image. */
    private int backgroundImageAlignment = Align.FIT;

    /** The alpha-transparency for the plot. */
    private float foregroundAlpha;

    /** The alpha transparency for the background paint. */
    private float backgroundAlpha;

    /** The drawing supplier. */
    private DrawingSupplier drawingSupplier;

    /** Storage for registered change listeners. */
    private transient EventListenerList listenerList;

    /**
     * Creates a new plot.
     */
    protected Plot() {

        this.parent = null;
        this.insets = DEFAULT_INSETS;
        this.backgroundPaint = DEFAULT_BACKGROUND_PAINT;
        this.backgroundAlpha = DEFAULT_BACKGROUND_ALPHA;
        this.backgroundImage = null;
        this.outlineStroke = DEFAULT_OUTLINE_STROKE;
        this.outlinePaint = DEFAULT_OUTLINE_PAINT;
        this.foregroundAlpha = DEFAULT_FOREGROUND_ALPHA;

        this.noDataMessage = null;
        this.noDataMessageFont = new Font("SansSerif", Font.PLAIN, 12);
        this.noDataMessagePaint = Color.black;

        this.drawingSupplier = new DefaultDrawingSupplier();

        this.listenerList = new EventListenerList();

    }

    /**
     * Returns the dataset group for the plot (not currently used).
     *
     * @return The dataset group.
     */
    public DatasetGroup getDatasetGroup() {
        return this.datasetGroup;
    }

    /**
     * Sets the dataset group (not currently used).
     *
     * @param group  the dataset group (<code>null</code> permitted).
     */
    protected void setDatasetGroup(DatasetGroup group) {
        this.datasetGroup = group;
    }

    /**
     * Returns the string that is displayed when the dataset is empty or 
     * <code>null</code>.
     *
     * @return The 'no data' message (<code>null</code> possible).
     */
    public String getNoDataMessage() {
        return this.noDataMessage;
    }

    /**
     * Sets the message that is displayed when the dataset is empty or null.
     *
     * @param message  the message (null permitted).
     */
    public void setNoDataMessage(String message) {
        this.noDataMessage = message;
    }

    /**
     * Returns the font used to display the 'no data' message.
     *
     * @return The font.
     */
    public Font getNoDataMessageFont() {
        return this.noDataMessageFont;
    }

    /**
     * Sets the font used to display the 'no data' message.
     *
     * @param font  the font.
     */
    public void setNoDataMessageFont(Font font) {
        this.noDataMessageFont = font;
    }

    /**
     * Returns the paint used to display the 'no data' message.
     *
     * @return The paint.
     */
    public Paint getNoDataMessagePaint() {
        return this.noDataMessagePaint;
    }

    /**
     * Sets the paint used to display the 'no data' message.
     *
     * @param paint  the paint.
     */
    public void setNoDataMessagePaint(Paint paint) {
        this.noDataMessagePaint = paint;
    }

    /**
     * Returns a short string describing the plot type.
     * <P>
     * Note: this gets used in the chart property editing user interface,
     * but there needs to be a better mechanism for identifying the plot type.
     *
     * @return A short string describing the plot type.
     */
    public abstract String getPlotType();

    /**
     * Returns the parent plot (or <code>null</code> if this plot is not part 
     * of a combined plot).
     *
     * @return The parent plot.
     */
    public Plot getParent() {
        return this.parent;
    }

    /**
     * Sets the parent plot.
     *
     * @param parent  the parent plot.
     */
    public void setParent(Plot parent) {
        this.parent = parent;
    }

    /**
     * Returns the root plot.
     *
     * @return The root plot.
     */
    public Plot getRootPlot() {

        Plot p = getParent();
        if (p == null) {
            return this;
        }
        else {
            return p.getRootPlot();
        }

    }

    /**
     * Returns true if this plot is part of a combined plot structure.
     *
     * @return <code>true</code> if this plot is part of a combined plot 
     *         structure.
     */
    public boolean isSubplot() {
        return (getParent() != null);
    }

    /**
     * Returns the insets for the plot area.
     *
     * @return The insets (never <code>null</code>).
     */
    public RectangleInsets getInsets() {
        return this.insets;
    }

    /**
     * Sets the insets for the plot and sends a {@link PlotChangeEvent} to 
     * all registered listeners.
     *
     * @param insets  the new insets (<code>null</code> not permitted).
     */
    public void setInsets(RectangleInsets insets) {
        setInsets(insets, true);
    }

    /**
     * Sets the insets for the plot and, if requested,  and sends a 
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param insets  the new insets (<code>null</code> not permitted).
     * @param notify  a flag that controls whether the registered listeners are
     *                notified.
     */
    public void setInsets(RectangleInsets insets, boolean notify) {
        if (insets == null) {
            throw new IllegalArgumentException("Null 'insets' argument.");
        }
        if (!this.insets.equals(insets)) {
            this.insets = insets;
            if (notify) {
                notifyListeners(new PlotChangeEvent(this));
            }
        }

    }

    /**
     * Returns the background color of the plot area.
     *
     * @return The paint (possibly <code>null</code>).
     */
    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    /**
     * Sets the background color of the plot area and sends a 
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setBackgroundPaint(Paint paint) {

        if (paint == null) {
            if (this.backgroundPaint != null) {
                this.backgroundPaint = null;
                notifyListeners(new PlotChangeEvent(this));
            }
        }
        else {
            if (this.backgroundPaint != null) {
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
     * Sets the alpha transparency of the plot area background, and notifies
     * registered listeners that the plot has been modified.
     *
     * @param alpha the new alpha value.
     */
    public void setBackgroundAlpha(float alpha) {

        if (this.backgroundAlpha != alpha) {
            this.backgroundAlpha = alpha;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the drawing supplier for the plot.
     *
     * @return The drawing supplier (possibly <code>null</code>).
     */
    public DrawingSupplier getDrawingSupplier() {
        DrawingSupplier result = null;
        Plot p = getParent();
        if (p != null) {
            result = p.getDrawingSupplier();
        }
        else {
            result = this.drawingSupplier;
        }
        return result;
    }

    /**
     * Sets the drawing supplier for the plot.  The drawing supplier is 
     * responsible for supplying a limitless (possibly repeating) sequence of 
     * <code>Paint</code>, <code>Stroke</code> and <code>Shape</code> objects 
     * that the plot's renderer(s) can use to populate its (their) tables.
     *
     * @param supplier  the new supplier.
     */
    public void setDrawingSupplier(DrawingSupplier supplier) {
        this.drawingSupplier = supplier;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the background image that is used to fill the plot's background 
     * area.
     *
     * @return The image (possibly <code>null</code>).
     */
    public Image getBackgroundImage() {
        return this.backgroundImage;
    }

    /**
     * Sets the background image for the plot.
     *
     * @param image  the image (<code>null</code> permitted).
     */
    public void setBackgroundImage(Image image) {
        this.backgroundImage = image;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the background image alignment. Alignment constants are defined 
     * in the <code>org.jfree.ui.Align</code> class in the JCommon class 
     * library.
     *
     * @return The alignment.
     */
    public int getBackgroundImageAlignment() {
        return this.backgroundImageAlignment;
    }

    /**
     * Sets the alignment for the background image and sends a 
     * {@link PlotChangeEvent} to all registered listeners.  Alignment options 
     * are defined by the {@link org.jfree.ui.Align} class in the JCommon 
     * class library.
     *
     * @param alignment  the alignment.
     */
    public void setBackgroundImageAlignment(int alignment) {
        if (this.backgroundImageAlignment != alignment) {
            this.backgroundImageAlignment = alignment;
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Returns the stroke used to outline the plot area.
     *
     * @return The stroke (possibly <code>null</code>).
     */
    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    /**
     * Sets the stroke used to outline the plot area and sends a 
     * {@link PlotChangeEvent} to all registered listeners. If you set this 
     * attribute to <code>null<.code>, no outline will be drawn.
     *
     * @param stroke  the stroke (<code>null</code> permitted).
     */
    public void setOutlineStroke(Stroke stroke) {

        if (stroke == null) {
            if (this.outlineStroke != null) {
                this.outlineStroke = null;
                notifyListeners(new PlotChangeEvent(this));
            }
        }
        else {
            if (this.outlineStroke != null) {
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
     * @return The color (possibly <code>null<code>).
     */
    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    /**
     * Sets the paint used to draw the outline of the plot area and sends a 
     * {@link PlotChangeEvent} to all registered listeners.  If you set this 
     * attribute to <code>null</code>, no outline will be drawn.
     *
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setOutlinePaint(Paint paint) {

        if (paint == null) {
            if (this.outlinePaint != null) {
                this.outlinePaint = null;
                notifyListeners(new PlotChangeEvent(this));
            }
        }
        else {
            if (this.outlinePaint != null) {
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
     * @param alpha  the new alpha transparency.
     */
    public void setForegroundAlpha(float alpha) {

        if (this.foregroundAlpha != alpha) {
            this.foregroundAlpha = alpha;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the legend items for the plot.  By default, this method returns 
     * <code>null</code>.  Subclasses should override to return a 
     * {@link LegendItemCollection}.
     *
     * @return The legend items for the plot (possibly <code>null</code>).
     */
    public LegendItemCollection getLegendItems() {
        return null;
    }

    /**
     * Registers an object for notification of changes to the plot.
     *
     * @param listener  the object to be registered.
     */
    public void addChangeListener(PlotChangeListener listener) {
        this.listenerList.add(PlotChangeListener.class, listener);
    }

    /**
     * Unregisters an object for notification of changes to the plot.
     *
     * @param listener  the object to be unregistered.
     */
    public void removeChangeListener(PlotChangeListener listener) {
        this.listenerList.remove(PlotChangeListener.class, listener);
    }

    /**
     * Notifies all registered listeners that the plot has been modified.
     *
     * @param event  information about the change event.
     */
    public void notifyListeners(PlotChangeEvent event) {

        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == PlotChangeListener.class) {
                ((PlotChangeListener) listeners[i + 1]).plotChanged(event);
            }
        }

    }

    /**
     * Draws the plot within the specified area.  The anchor is a point on the
     * chart that is specified externally (for instance, it may be the last
     * point of the last mouse click performed by the user) - plots can use or
     * ignore this value as they see fit. 
     * <br><br>
     * Subclasses need to provide an implementation of this method, obviously.
     * 
     * @param g2  the graphics device.
     * @param area  the plot area.
     * @param anchor  the anchor point (<code>null</code> permitted).
     * @param parentState  the parent state (if any).
     * @param info  carries back plot rendering info.
     */
    public abstract void draw(Graphics2D g2,
                              Rectangle2D area,
                              Point2D anchor,
                              PlotState parentState,
                              PlotRenderingInfo info);
                              
    /**
     * Draws the plot background (the background color and/or image).
     * <P>
     * This method will be called during the chart drawing process and is 
     * declared public so that it can be accessed by the renderers used by 
     * certain subclasses.  You shouldn't need to call this method directly.
     *
     * @param g2  the graphics device.
     * @param area  the area within which the plot should be drawn.
     */
    public void drawBackground(Graphics2D g2, Rectangle2D area) {
        fillBackground(g2, area);
        drawBackgroundImage(g2, area);
    }

    /**
     * Fills the specified area with the background paint.
     * 
     * @param g2  the graphics device.
     * @param area  the area.
     */
    protected void fillBackground(Graphics2D g2, Rectangle2D area) {
        if (this.backgroundPaint != null) {
            Composite originalComposite = g2.getComposite();
            g2.setComposite(
                AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, this.backgroundAlpha
                )
            );
            g2.setPaint(this.backgroundPaint);
            g2.fill(area);
            g2.setComposite(originalComposite);
        }        
    }
    
    /**
     * Draws the background image (if there is one) aligned within the 
     * specified area.
     * 
     * @param g2  the graphics device.
     * @param area  the area.
     */
    protected void drawBackgroundImage(Graphics2D g2, Rectangle2D area) {
        if (this.backgroundImage != null) {
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC, this.backgroundAlpha
            ));
            Rectangle2D dest = new Rectangle2D.Double(
                0.0, 0.0,
                this.backgroundImage.getWidth(null), 
                this.backgroundImage.getHeight(null)
            );
            Align.align(dest, area, this.backgroundImageAlignment);
            g2.drawImage(
                this.backgroundImage,
                (int) dest.getX(), (int) dest.getY(),
                (int) dest.getWidth() + 1, (int) dest.getHeight() + 1, null
            );
            g2.setComposite(originalComposite);
        }
    }
    
    /**
     * Draws the plot outline.  This method will be called during the chart 
     * drawing process and is declared public so that it can be accessed by the
     * renderers used by certain subclasses. You shouldn't need to call this 
     * method directly.
     * 
     * @param g2  the graphics device.
     * @param area  the area within which the plot should be drawn.
     */
    public void drawOutline(Graphics2D g2, Rectangle2D area) {
        if ((this.outlineStroke != null) && (this.outlinePaint != null)) {
            g2.setStroke(this.outlineStroke);
            g2.setPaint(this.outlinePaint);
            g2.draw(area);
        }
    }

    /**
     * Draws a message to state that there is no data to plot.
     *
     * @param g2  the graphics device.
     * @param area  the area within which the plot should be drawn.
     */
    protected void drawNoDataMessage(Graphics2D g2, Rectangle2D area) {

        Shape savedClip = g2.getClip();
        g2.clip(area);
        String message = this.noDataMessage;
        if (message != null) {
            g2.setFont(this.noDataMessageFont);
            g2.setPaint(this.noDataMessagePaint);
            TextBlock block = TextUtilities.createTextBlock(
                this.noDataMessage, this.noDataMessageFont, 
                this.noDataMessagePaint, 
                0.9f * (float) area.getWidth(), new G2TextMeasurer(g2)
            );
            block.draw(
                g2, (float) area.getCenterX(), (float) area.getCenterY(), 
                TextBlockAnchor.CENTER
            );
        }
        g2.setClip(savedClip);

    }

    /**
     * Handles a 'click' on the plot.  Since the plot does not maintain any
     * information about where it has been drawn, the plot rendering info is 
     * supplied as an argument.
     *
     * @param x  the x coordinate (in Java2D space).
     * @param y  the y coordinate (in Java2D space).
     * @param info  an object containing information about the dimensions of 
     *              the plot.
     */
    public void handleClick(int x, int y, PlotRenderingInfo info) {
        // provides a 'no action' default
    }

    /**
     * Performs a zoom on the plot.  Subclasses should override if zooming is 
     * appropriate for the type of plot.
     *
     * @param percent  the zoom percentage.
     */
    public void zoom(double percent) {
        // do nothing by default.
    }

    /**
     * Receives notification of a change to one of the plot's axes.
     *
     * @param event  information about the event (not used here).
     */
    public void axisChanged(AxisChangeEvent event) {
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Receives notification of a change to the plot's dataset.
     * <P>
     * The plot reacts by passing on a plot change event to all registered 
     * listeners.
     *
     * @param event  information about the event (not used here).
     */
    public void datasetChanged(DatasetChangeEvent event) {
        PlotChangeEvent newEvent = new PlotChangeEvent(this);
        newEvent.setType(ChartChangeEventType.DATASET_UPDATED);
        notifyListeners(newEvent);
    }

    /**
     * Adjusts the supplied x-value.
     *
     * @param x  the x-value.
     * @param w1  width 1.
     * @param w2  width 2.
     * @param edge  the edge (left or right).
     *
     * @return The adjusted x-value.
     */
    protected double getRectX(double x, double w1, double w2, 
                              RectangleEdge edge) {

        double result = x;
        if (edge == RectangleEdge.LEFT) {
            result = result + w1;
        }
        else if (edge == RectangleEdge.RIGHT) {
            result = result + w2;
        }
        return result;

    }

    /**
     * Adjusts the supplied y-value.
     *
     * @param y  the x-value.
     * @param h1  height 1.
     * @param h2  height 2.
     * @param edge  the edge (top or bottom).
     *
     * @return The adjusted y-value.
     */
    protected double getRectY(double y, double h1, double h2, 
                              RectangleEdge edge) {

        double result = y;
        if (edge == RectangleEdge.TOP) {
            result = result + h1;
        }
        else if (edge == RectangleEdge.BOTTOM) {
            result = result + h2;
        }
        return result;

    }

    /**
     * Tests this plot for equality with another object.
     *
     * @param obj  the object (<code>null</code> permitted).
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Plot)) {
            return false;
        }
        Plot that = (Plot) obj;
        if (!ObjectUtilities.equal(this.noDataMessage, that.noDataMessage)) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.noDataMessageFont, that.noDataMessageFont
        )) {
            return false;
        }
        if (!PaintUtilities.equal(
            this.noDataMessagePaint, that.noDataMessagePaint
        )) {
            return false;
        }
        if (!ObjectUtilities.equal(this.insets, that.insets)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.outlineStroke, that.outlineStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.outlinePaint, that.outlinePaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.backgroundPaint, that.backgroundPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.backgroundImage, that.backgroundImage
        )) {
            return false;
        }
        if (this.backgroundImageAlignment != that.backgroundImageAlignment) {
            return false;
        }
        if (this.foregroundAlpha != that.foregroundAlpha) {
            return false;
        }
        if (this.backgroundAlpha != that.backgroundAlpha) {
            return false;
        }
        if (!this.drawingSupplier.equals(that.drawingSupplier)) {
            return false;   
        }
        return true;
    }

    /**
     * Creates a clone of the plot.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if some component of the plot does not
     *         support cloning.
     */
    public Object clone() throws CloneNotSupportedException {

        Plot clone = (Plot) super.clone();
        // private Plot parent <-- don't clone the parent plot, but take care 
        // childs in combined plots instead
        if (this.datasetGroup != null) {
            clone.datasetGroup 
                = (DatasetGroup) ObjectUtilities.clone(this.datasetGroup);
        }
        clone.drawingSupplier 
            = (DrawingSupplier) ObjectUtilities.clone(this.drawingSupplier);
        clone.listenerList = new EventListenerList();
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
        SerialUtilities.writePaint(this.noDataMessagePaint, stream);
        SerialUtilities.writeStroke(this.outlineStroke, stream);
        SerialUtilities.writePaint(this.outlinePaint, stream);
        // backgroundImage
        SerialUtilities.writePaint(this.backgroundPaint, stream);
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the input stream.
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream) 
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.noDataMessagePaint = SerialUtilities.readPaint(stream);
        this.outlineStroke = SerialUtilities.readStroke(stream);
        this.outlinePaint = SerialUtilities.readPaint(stream);
        // backgroundImage
        this.backgroundPaint = SerialUtilities.readPaint(stream);

        this.listenerList = new EventListenerList();

    }

    /**
     * Resolves a domain axis location for a given plot orientation.
     *
     * @param location  the location (<code>null</code> not permitted).
     * @param orientation  the orientation (<code>null</code> not permitted).
     *
     * @return The edge (never <code>null</code>).
     */
    public static RectangleEdge resolveDomainAxisLocation(
            AxisLocation location, PlotOrientation orientation) {
        
        if (location == null) {
            throw new IllegalArgumentException("Null 'location' argument.");   
        }
        if (orientation == null) {
            throw new IllegalArgumentException("Null 'orientation' argument.");
        }

        RectangleEdge result = null;
        
        if (location == AxisLocation.TOP_OR_RIGHT) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                result = RectangleEdge.RIGHT;
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                result = RectangleEdge.TOP;
            }
        }
        else if (location == AxisLocation.TOP_OR_LEFT) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                result = RectangleEdge.LEFT;
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                result = RectangleEdge.TOP;
            }
        }
        else if (location == AxisLocation.BOTTOM_OR_RIGHT) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                result = RectangleEdge.RIGHT;
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                result = RectangleEdge.BOTTOM;
            }
        }
        else if (location == AxisLocation.BOTTOM_OR_LEFT) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                result = RectangleEdge.LEFT;
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                result = RectangleEdge.BOTTOM;
            }
        }
        // the above should cover all the options...
        if (result == null) {
            throw new IllegalStateException("resolveDomainAxisLocation()");
        }
        return result;
        
    }

    /**
     * Resolves a range axis location for a given plot orientation.
     *
     * @param location  the location (<code>null</code> not permitted).
     * @param orientation  the orientation (<code>null</code> not permitted).
     *
     * @return The edge (never <code>null</code>).
     */
    public static RectangleEdge resolveRangeAxisLocation(
            AxisLocation location, PlotOrientation orientation) {

        if (location == null) {
            throw new IllegalArgumentException("Null 'location' argument.");   
        }
        if (orientation == null) {
            throw new IllegalArgumentException("Null 'orientation' argument.");
        }

        RectangleEdge result = null;
        
        if (location == AxisLocation.TOP_OR_RIGHT) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                result = RectangleEdge.TOP;
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                result = RectangleEdge.RIGHT;
            }
        }
        else if (location == AxisLocation.TOP_OR_LEFT) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                result = RectangleEdge.TOP;
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                result = RectangleEdge.LEFT;
            }
        }
        else if (location == AxisLocation.BOTTOM_OR_RIGHT) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                result = RectangleEdge.BOTTOM;
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                result = RectangleEdge.RIGHT;
            }
        }
        else if (location == AxisLocation.BOTTOM_OR_LEFT) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                result = RectangleEdge.BOTTOM;
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                result = RectangleEdge.LEFT;
            }
        }

        // the above should cover all the options...
        if (result == null) {
            throw new IllegalStateException("resolveRangeAxisLocation()");
        }
        return result;
        
    }

}

