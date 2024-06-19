/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
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
 * ---------
 * Axis.java
 * ---------
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Bill Kelemen;
 *
 * $Id: Axis.java,v 1.1 2007/10/10 20:00:06 vauchers Exp $
 *
 * Changes (from 21-Aug-2001)
 * --------------------------
 * 21-Aug-2001 : Added standard header, fixed DOS encoding problem (DG);
 * 18-Sep-2001 : Updated header (DG);
 * 07-Nov-2001 : Allow null axis labels (DG);
 *             : Added default font values (DG);
 * 13-Nov-2001 : Modified the setPlot(...) method to check compatibility between the axis and the
 *               plot (DG);
 * 30-Nov-2001 : Changed default font from "Arial" --> "SansSerif" (DG);
 * 06-Dec-2001 : Allow null in setPlot(...) method (BK);
 * 06-Mar-2002 : Added AxisConstants interface (DG);
 * 23-Apr-2002 : Added a visible property.  Moved drawVerticalString to RefineryUtilities.  Added
 *               fixedDimension property for use in combined plots (DG);
 * 25-Jun-2002 : Removed unnecessary imports (DG);
 * 05-Sep-2002 : Added attribute for tick mark paint (DG);
 * 18-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 07-Nov-2002 : Added attributes to control the inside and outside length of the tick marks (DG);
 * 08-Nov-2002 : Moved to new package com.jrefinery.chart.axis (DG);
 * 18-Nov-2002 : Added axis location to refreshTicks(...) parameters (DG);
 * 15-Jan-2003 : Removed monolithic constructor (DG);
 * 17-Jan-2003 : Moved plot classes to separate package (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 *
 */

package com.jrefinery.chart.axis;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.EventListenerList;

import com.jrefinery.chart.event.AxisChangeEvent;
import com.jrefinery.chart.event.AxisChangeListener;
import com.jrefinery.chart.plot.Plot;
import com.jrefinery.chart.plot.PlotNotCompatibleException;
import com.jrefinery.io.SerialUtilities;
import com.jrefinery.ui.RefineryUtilities;
import com.jrefinery.util.ObjectUtils;

/**
 * The base class for all axes in JFreeChart.  Subclasses are divided into those that display
 * values ({@link ValueAxis}) and those that display categories ({@link CategoryAxis}).
 *
 * @author David Gilbert
 */
public abstract class Axis implements AxisConstants, Serializable {

    /** A flag indicating whether or not the axis is visible. */
    private boolean visible;

    /** The label for the axis. */
    private String label;

    /** The font for displaying the axis label. */
    private Font labelFont;

    /** The paint for drawing the axis label. */
    private transient Paint labelPaint;

    /** The insets for the axis label. */
    private Insets labelInsets;

    /** A flag that indicates whether or not tick labels are visible for the axis. */
    private boolean tickLabelsVisible;

    /** The font used to display the tick labels. */
    private Font tickLabelFont;

    /** The color used to display the tick labels. */
    private transient Paint tickLabelPaint;

    /** The blank space around each tick label. */
    private Insets tickLabelInsets;

    /** A flag that indicates whether or not tick marks are visible for the axis. */
    private boolean tickMarksVisible;

    /** The length of the tick mark inside the data area (zero permitted). */
    private float tickMarkInsideLength;

    /** The length of the tick mark outside the data area (zero permitted). */
    private float tickMarkOutsideLength;

    /** The stroke used to draw tick marks. */
    private transient Stroke tickMarkStroke;

    /** The paint used to draw tick marks. */
    private transient Paint tickMarkPaint;

    /** A working list of ticks - this list is refreshed as required. */
    private transient List ticks;

    /** A reference back to the plot that the axis is assigned to (can be <code>null</code>). */
    private transient Plot plot;

    /** The fixed (horizontal or vertical) dimension for the axis. */
    private double fixedDimension;

    /** Storage for registered listeners. */
    private transient EventListenerList listenerList;

    /**
     * Constructs an axis, using default values where necessary.
     *
     * @param label  the axis label (<code>null</code> permitted).
     */
    protected Axis(String label) {

        this.label = label;   
        this.visible = DEFAULT_AXIS_VISIBLE;
        this.labelFont = DEFAULT_AXIS_LABEL_FONT;
        this.labelPaint = DEFAULT_AXIS_LABEL_PAINT;
        this.labelInsets = DEFAULT_AXIS_LABEL_INSETS;
        this.tickLabelsVisible = DEFAULT_TICK_LABELS_VISIBLE;
        this.tickLabelFont = DEFAULT_TICK_LABEL_FONT;
        this.tickLabelPaint = DEFAULT_TICK_LABEL_PAINT;
        this.tickLabelInsets = DEFAULT_TICK_LABEL_INSETS;
        this.tickMarksVisible = DEFAULT_TICK_MARKS_VISIBLE;
        this.tickMarkStroke = DEFAULT_TICK_MARK_STROKE;
        this.tickMarkPaint = DEFAULT_TICK_MARK_PAINT;
        this.tickMarkInsideLength = DEFAULT_TICK_MARK_INSIDE_LENGTH;
        this.tickMarkOutsideLength = DEFAULT_TICK_MARK_OUTSIDE_LENGTH;

        this.plot = null;
        
        this.ticks = new java.util.ArrayList();
        this.listenerList = new EventListenerList();

    }

    /**
     * Returns true if the axis is visible, and false otherwise.
     *
     * @return a flag indicating whether or not the axis is visible.
     */
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Sets a flag that controls whether or not the axis is drawn on the chart.
     *
     * @param flag  the flag.
     */
    public void setVisible(boolean flag) {

        if (flag != this.visible) {
            this.visible = flag;
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Returns the label for the axis.
     *
     * @return the label for the axis (null possible).
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label for the axis (null permitted).
     * <P>
     * Registered listeners are notified of a general change to the axis.
     *
     * @param label  the new label.
     */
    public void setLabel(String label) {

        String existing = this.label;
        if (existing != null) {
            if (!existing.equals(label)) {
                this.label = label;
                notifyListeners(new AxisChangeEvent(this));
            }
        }
        else {
            if (label != null) {
                this.label = label;
                notifyListeners(new AxisChangeEvent(this));
            }
        }

    }

    /**
     * Returns the font for the axis label.
     *
     * @return the font.
     */
    public Font getLabelFont() {
        return labelFont;
    }

    /**
     * Sets the font for the axis label.
     * <P>
     * Registered listeners are notified of a general change to the axis.
     *
     * @param font  the new label font.
     */
    public void setLabelFont(Font font) {

        // check arguments...
        if (font == null) {
            throw new IllegalArgumentException("Axis.setLabelFont(...): null not permitted.");
        }

        // make the change (if necessary)...
        if (!this.labelFont.equals(font)) {
            this.labelFont = font;
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Returns the color/shade used to draw the axis label.
     *
     * @return the color/shade used to draw the axis label.
     */
    public Paint getLabelPaint() {
        return this.labelPaint;
    }

    /**
     * Sets the color/shade used to draw the axis label.
     * <P>
     * Registered listeners are notified of a general change to the axis.
     *
     * @param paint  the new color/shade for the axis label.
     */
    public void setLabelPaint(Paint paint) {

        // check arguments...
        if (paint == null) {
            throw new IllegalArgumentException("Axis.setLabelPaint(...): null not permitted.");
        }

        // make the change (if necessary)...
        if (!this.labelPaint.equals(paint)) {
            this.labelPaint = paint;
            notifyListeners(new AxisChangeEvent(this));
        }
    }

    /**
     * Returns the insets for the label (that is, the amount of blank space
     * that should be left around the label).
     *
     * @return the label insets.
     */
    public Insets getLabelInsets() {
        return this.labelInsets;
    }

    /**
     * Sets the insets for the axis label, and notifies registered listeners
     * that the axis has been modified.
     *
     * @param insets  the new label insets.
     */
    public void setLabelInsets(Insets insets) {
        if (!insets.equals(this.labelInsets)) {
            this.labelInsets = insets;
            notifyListeners(new AxisChangeEvent(this));
        }
    }

    /**
     * Returns a flag indicating whether or not the tick labels are visible.
     *
     * @return the flag.
     */
    public boolean isTickLabelsVisible() {
        return tickLabelsVisible;
    }

    /**
     * Sets the flag that determines whether or not the tick labels are visible.
     * <P>
     * Registered listeners are notified of a general change to the axis.
     *
     * @param flag  the flag.
     */
    public void setTickLabelsVisible(boolean flag) {

        if (flag != tickLabelsVisible) {
            tickLabelsVisible = flag;
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Returns the font used for the tick labels (if showing).
     *
     * @return the font used for the tick labels.
     */
    public Font getTickLabelFont() {
        return tickLabelFont;
    }

    /**
     * Sets the font for the tick labels.
     * <P>
     * Registered listeners are notified of a general change to the axis.
     *
     * @param font  the new tick label font.
     */
    public void setTickLabelFont(Font font) {

        // check arguments...
        if (font == null) {
            throw new IllegalArgumentException("Axis.setTickLabelFont(...): null not permitted.");
        }

        // apply change if necessary...
        if (!this.tickLabelFont.equals(font)) {
            this.tickLabelFont = font;
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Returns the color/shade used for the tick labels.
     *
     * @return the color/shade used for the tick labels.
     */
    public Paint getTickLabelPaint() {
        return this.tickLabelPaint;
    }

    /**
     * Sets the color/shade used to draw tick labels (if they are showing).
     * <P>
     * Registered listeners are notified of a general change to the axis.
     *
     * @param paint  the new color/shade.
     */
    public void setTickLabelPaint(Paint paint) {

        // check arguments...
        if (paint == null) {
            throw new IllegalArgumentException("Axis.setTickLabelPaint(...): null not permitted.");
        }

        // make the change (if necessary)...
        if (!this.tickLabelPaint.equals(paint)) {
            this.tickLabelPaint = paint;
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Returns the insets for the tick labels.
     *
     * @return the insets for the tick labels.
     */
    public Insets getTickLabelInsets() {
        return this.tickLabelInsets;
    }

    /**
     * Sets the insets for the tick labels, and notifies registered listeners
     * that the axis has been modified.
     *
     * @param insets  the new tick label insets.
     */
    public void setTickLabelInsets(Insets insets) {

        // check arguments...
        if (insets == null) {
            throw new IllegalArgumentException("Axis.setTickLabelInsets(...): null not permitted.");
        }

        // apply change if necessary...
        if (!this.tickLabelInsets.equals(insets)) {
            this.tickLabelInsets = insets;
            notifyListeners(new AxisChangeEvent(this));
        }
    }

    /**
     * Returns the flag that indicates whether or not the tick marks are
     * showing.
     *
     * @return the flag that indicates whether or not the tick marks are showing.
     */
    public boolean isTickMarksVisible() {
        return tickMarksVisible;
    }

    /**
     * Sets the flag that indicates whether or not the tick marks are showing.
     * <P>
     * Registered listeners are notified of a general change to the axis.
     *
     * @param flag  the flag.
     */
    public void setTickMarksVisible(boolean flag) {

        if (flag != tickMarksVisible) {
            tickMarksVisible = flag;
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Returns the inside length of the tick marks.
     *
     * @return the length.
     */
    public float getTickMarkInsideLength() {
        return this.tickMarkInsideLength;
    }

    /**
     * Sets the inside length of the tick marks.
     *
     * @param length  the new length.
     */
    public void setTickMarkInsideLength(float length) {
        this.tickMarkInsideLength = length;
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Returns the outside length of the tick marks.
     *
     * @return the length.
     */
    public float getTickMarkOutsideLength() {
        return this.tickMarkOutsideLength;
    }

    /**
     * Sets the outside length of the tick marks.
     *
     * @param length  the new length.
     */
    public void setTickMarkOutsideLength(float length) {
        this.tickMarkOutsideLength = length;
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Returns the pen/brush used to draw tick marks (if they are showing).
     *
     * @return the pen/brush used to draw tick marks.
     */
    public Stroke getTickMarkStroke() {
        return tickMarkStroke;
    }

    /**
     * Sets the pen/brush used to draw tick marks (if they are showing).
     * <P>
     * Registered listeners are notified of a general change to the axis.
     *
     * @param stroke  the new pen/brush (null not permitted).
     */
    public void setTickMarkStroke(Stroke stroke) {

        // check arguments...
        if (stroke == null) {
            throw new IllegalArgumentException("Axis.setTickMarkStroke(...): null not permitted.");
        }

        // make the change (if necessary)...
        if (!this.tickMarkStroke.equals(stroke)) {
            this.tickMarkStroke = stroke;
            notifyListeners(new AxisChangeEvent(this));
        }
    }

    /**
     * Returns the paint used to draw tick marks (if they are showing).
     *
     * @return the paint.
     */
    public Paint getTickMarkPaint() {
        return tickMarkPaint;
    }

    /**
     * Sets the paint used to draw tick marks (if they are showing).
     * <P>
     * Registered listeners are notified of a general change to the axis.
     *
     * @param paint  the new paint (null not permitted).
     */
    public void setTickMarkPaint(Paint paint) {

        // check arguments...
        if (paint == null) {
            throw new IllegalArgumentException("Axis.setTickMarkPaint(...): null not permitted.");
        }

        // make the change (if necessary)...
        if (!this.tickMarkPaint.equals(paint)) {
            this.tickMarkPaint = paint;
            notifyListeners(new AxisChangeEvent(this));
        }
    }

    /**
     * Returns the current list of ticks.
     *
     * @return the ticks.
     */
    public List getTicks() {
        return this.ticks;
    }

    /**
     * Returns the plot that the axis is assigned to.
     * <P>
     * This method will return null if the axis is not currently assigned to a
     * plot.
     *
     * @return The plot that the axis is assigned to.
     */
    public Plot getPlot() {
        return plot;
    }

    /**
     * Sets a reference to the plot that the axis is assigned to.
     * <P>
     * This method is used internally, you shouldn't need to call it yourself.
     *
     * @param plot  the plot.
     *
     * @throws PlotNotCompatibleException if plot is not compatible.
     */
    public void setPlot(Plot plot) throws PlotNotCompatibleException {

        if (isCompatiblePlot(plot) || plot == null) {
            this.plot = plot;
            configure();
        }
        else {
            throw new PlotNotCompatibleException(
                "Axis.setPlot(...): plot not compatible with axis.");
        }
    }
    
    /**
     * Returns the fixed dimension for the axis.
     *
     * @return the fixed dimension.
     */
    public double getFixedDimension() {
        return this.fixedDimension;
    }

    /**
     * Sets the fixed dimension for the axis.
     * <P>
     * This is used when combining more than one plot on a chart.  In this case,
     * there may be several axes that need to have the same height or width so
     * that they are aligned.  This method is used to fix a dimension for the
     * axis (the context determines whether the dimension is horizontal or
     * vertical).
     *
     * @param dimension  the fixed dimension.
     */
    public void setFixedDimension(double dimension) {
        this.fixedDimension = dimension;
    }

    /**
     * Draws the axis on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the axes and plot should be drawn.
     * @param dataArea  the area within which the data should be drawn.
     * @param location  the axis location (TOP, BOTTOM, RIGHT or LEFT).
     */
    public abstract void draw(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea,
                              int location);

    /**
     * Calculates the positions of the ticks for the axis, storing the results
     * in the tick list (ready for drawing).
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the axes and plot should be drawn.
     * @param dataArea  the area inside the axes.
     * @param location  the axis location.
     */
    public abstract void refreshTicks(Graphics2D g2,
                                      Rectangle2D plotArea, 
                                      Rectangle2D dataArea,
                                      int location);

    /**
     * Configures the axis to work with the current plot.  Override this method
     * to perform any special processing (such as auto-rescaling).
     */
    public abstract void configure();

    /**
     * Returns the maximum width of the ticks in the working list (that is set
     * up by refreshTicks()).
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the plot is to be drawn.
     *
     * @return the maximum width of the ticks in the working list.
     */
    protected double getMaxTickLabelWidth(Graphics2D g2, Rectangle2D plotArea) {

        double maxWidth = 0.0;
        Font font = getTickLabelFont();
        FontRenderContext frc = g2.getFontRenderContext();

        Iterator iterator = this.ticks.iterator();
        while (iterator.hasNext()) {
            Tick tick = (Tick) iterator.next();
            Rectangle2D labelBounds = font.getStringBounds(tick.getText(), frc);
            if (labelBounds.getWidth() > maxWidth) {
                maxWidth = labelBounds.getWidth();
            }
        }
        return maxWidth;

    }

    /**
     * Returns true if the plot is compatible with the axis.
     *
     * @param plot  the plot.
     *
     * @return <code>true</code> if the plot is compatible with the axis.
     */
    protected abstract boolean isCompatiblePlot(Plot plot);

    /**
     * Registers an object for notification of changes to the axis.
     *
     * @param listener  the object that is being registered.
     */
    public void addChangeListener(AxisChangeListener listener) {
        this.listenerList.add(AxisChangeListener.class, listener);
    }

    /**
     * Deregisters an object for notification of changes to the axis.
     *
     * @param listener  the object to deregister.
     */
    public void removeChangeListener(AxisChangeListener listener) {
        this.listenerList.remove(AxisChangeListener.class, listener);
    }

    /**
     * Notifies all registered listeners that the axis has changed.
     * The AxisChangeEvent provides information about the change.
     *
     * @param event  information about the change to the axis.
     */
    protected void notifyListeners(AxisChangeEvent event) {

        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == AxisChangeListener.class) {
                ((AxisChangeListener) listeners[i + 1]).axisChanged(event);
            }
        }

    }

    /**
     * A utility method intended for use by subclasses that are 'horizontal' axes (for example,
     * HorizontalCategoryAxis, HorizontalNumberAxis and HorizontalDateAxis.
     *
     * @param label  the label.
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param dataArea  the data area.
     * @param location  the axis location (TOP or BOTTOM).
     * @param cursorY  the Y cursor.
     *
     * @return the updated value of the Y cursor.
     */
    protected double drawHorizontalLabel(String label,
                                         Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea,
                                         int location, double cursorY) {

        if (label != null) {
            Font labelFont = getLabelFont();
            Insets labelInsets = getLabelInsets();
            g2.setFont(labelFont);
            g2.setPaint(getLabelPaint());
            FontRenderContext frc = g2.getFontRenderContext();
            Rectangle2D labelBounds = labelFont.getStringBounds(label, frc);
            LineMetrics lm = labelFont.getLineMetrics(label, frc);
            if (location == TOP) {
                cursorY = cursorY + labelInsets.top + lm.getAscent();
            }
            else {
                cursorY = cursorY - labelInsets.bottom - lm.getDescent() - lm.getLeading();
            }
            float labelx = (float) (dataArea.getX() + dataArea.getWidth() / 2
                                                    - labelBounds.getWidth() / 2);
            float labely = (float) cursorY;
            g2.drawString(label, labelx, labely);
            if (location == TOP) {
                cursorY = cursorY + lm.getDescent() + lm.getLeading() + labelInsets.bottom;
            }
            else {
                cursorY = cursorY - labelInsets.top - lm.getAscent();
            }
        }

        return cursorY;

    }

    /**
     * A utility method intended for use by subclasses that are 'vertical' axes (for example,
     * VerticalCategoryAxis, VerticalNumberAxis and VerticalDateAxis.
     *
     * @param label  the label.
     * @param vertical  rotate the label to vertical?
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param dataArea  the data area.
     * @param location  the axis location (TOP or BOTTOM).
     *
     */
    protected void drawVerticalLabel(String label, boolean vertical,
                                     Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea,
                                     int location) {

        if (label == null ? false : !label.equals("")) {
            Font labelFont = getLabelFont();
            Insets labelInsets = getLabelInsets();
            g2.setFont(labelFont);
            g2.setPaint(getLabelPaint());

            Rectangle2D labelBounds = labelFont.getStringBounds(label, g2.getFontRenderContext());
            if (location == LEFT) {
                if (vertical) {
                    double xx = plotArea.getX() + labelInsets.left + labelBounds.getHeight();
                    double yy = plotArea.getY() + dataArea.getHeight() / 2
                                                + (labelBounds.getWidth() / 2);
                    RefineryUtilities.drawRotatedString(label, g2,
                                                        (float) xx, (float) yy, -Math.PI / 2);
                }
                else {
                    double xx = plotArea.getX() + labelInsets.left;
                    double yy = plotArea.getY() + plotArea.getHeight() / 2
                                                - labelBounds.getHeight() / 2;
                    g2.drawString(label, (float) xx, (float) yy);
                }
            }
            else {
                if (vertical) {
                    double xx = plotArea.getMaxX() - labelInsets.right - labelBounds.getHeight();
                    double yy = plotArea.getMinY() + dataArea.getHeight() / 2
                                                   - (labelBounds.getWidth() / 2);
                    RefineryUtilities.drawRotatedString(label, g2,
                                                        (float) xx, (float) yy, Math.PI / 2);
                }
                else {
                    double xx = plotArea.getMaxX() - labelInsets.right - labelBounds.getWidth();
                    double yy = plotArea.getMinY() + plotArea.getHeight() / 2
                                                   + labelBounds.getHeight() / 2;
                    g2.drawString(label, (float) xx, (float) yy);
                }
            }
        }

    }
    
    /**
     * Tests this axis for equality with another object.
     * 
     * @param obj  the object.
     * 
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }
        
        if (obj == this) {
            return true;
        }
 
        if (obj instanceof Axis) {
            Axis axis = (Axis) obj;

            boolean b0 = (this.visible == axis.visible);
                
            boolean b1 = ObjectUtils.equalOrBothNull(this.label, axis.label);
            boolean b2 = ObjectUtils.equalOrBothNull(this.labelFont, axis.labelFont);
            boolean b3 = ObjectUtils.equalOrBothNull(this.labelPaint, axis.labelPaint);
            boolean b4 = ObjectUtils.equalOrBothNull(this.labelInsets, axis.labelInsets);

            boolean b5 = (this.tickLabelsVisible == axis.tickLabelsVisible);
            boolean b6 = ObjectUtils.equalOrBothNull(this.tickLabelFont, axis.tickLabelFont);
            boolean b7 = ObjectUtils.equalOrBothNull(this.tickLabelPaint, axis.tickLabelPaint);
            boolean b8 = ObjectUtils.equalOrBothNull(this.tickLabelInsets, axis.tickLabelInsets);

            boolean b9 = (this.tickMarksVisible == axis.tickMarksVisible);
            boolean b10 = (this.tickMarkInsideLength == axis.tickMarkInsideLength);
            boolean b11 = (this.tickMarkOutsideLength == axis.tickMarkOutsideLength);

            boolean b12 = ObjectUtils.equalOrBothNull(this.tickMarkPaint, axis.tickMarkPaint);
            boolean b13 = ObjectUtils.equalOrBothNull(this.tickMarkStroke, axis.tickMarkStroke);
                
            boolean b14 = (this.fixedDimension == axis.fixedDimension);

            return b0 && b1 && b2 && b3 && b4 && b5 && b6 && b7 && b8
                   && b9 && b10 && b11 && b12 && b13 && b14;

        }
        
        return false;
               
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
        SerialUtilities.writePaint(this.labelPaint, stream);
        SerialUtilities.writePaint(this.tickLabelPaint, stream);
        SerialUtilities.writeStroke(this.tickMarkStroke, stream);
        SerialUtilities.writePaint(this.tickMarkPaint, stream);
    
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
        this.labelPaint = SerialUtilities.readPaint(stream);
        this.tickLabelPaint = SerialUtilities.readPaint(stream);
        this.tickMarkStroke = SerialUtilities.readStroke(stream);
        this.tickMarkPaint = SerialUtilities.readPaint(stream);
        this.listenerList = new EventListenerList();
        
    }
    

}
