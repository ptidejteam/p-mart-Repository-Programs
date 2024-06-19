/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            Axis.java
 * Author:          David Gilbert;
 * Contributor(s):  -;
 *
 * (C) Copyright 2000, Simba Management Limited;
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307, USA.
 *
 * $Id: Axis.java,v 1.1 2007/10/10 18:52:16 vauchers Exp $
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import com.jrefinery.chart.event.*;

/**
 * The base class for all axes used by JFreeChart.
 * @see CategoryAxis
 * @see ValueAxis
 */
public abstract class Axis {

  /** A reference back to the plot that the axis is currently assigned to (can be null if the axis
      hasn't been assigned yet); */
  protected Plot plot;

  /** The label for the axis; */
  protected String label;

  /** The font for displaying the axis label; */
  protected Font labelFont;

  /** The paint for drawing the axis label; */
  protected Paint labelPaint;

  /** The insets for the axis label; */
  protected Insets labelInsets;

  /** A flag that indicates whether or not tick labels are visible for the axis; */
  protected boolean showTickLabels;

  /** The font used to display the tick labels; */
  protected Font tickLabelFont;

  /** The color used to display the tick labels; */
  protected Paint tickLabelPaint;

  /** The blank space around each tick label; */
  protected Insets tickLabelInsets;

  /** A flag that indicates whether or not tick marks are visible for the axis; */
  protected boolean showTickMarks;

  /** The line type used to draw tick marks; */
  protected Stroke tickMarkStroke;

  /** A working list of ticks - this list is refreshed as required. */
  protected java.util.List ticks;

  /** Storage for registered listeners (objects interested in receiving change events for the
      axis); */
  protected java.util.List listeners;

  /**
   * Standard constructor - creates an axis with the specified attributes.
   * <P>
   * Note that this class is not intended to be instantiated directly - use a subclass.
   * @param label The axis label;
   * @param labelFont The font for displaying the axis label;
   * @param labelPaint The paint used to draw the axis label;
   * @param labelInsets Determines the amount of blank space around the label;
   * @param showTickLabels Flag indicating whether or not tick labels are visible;
   * @param tickLabelFont The font used to display tick labels;
   * @param tickLabelPaint The paint used to draw tick labels;
   * @param tickLabelInsets Determines the amount of blank space around tick labels;
   * @param showTickMarks Flag indicating whether or not tick marks are visible;
   * @param tickMarkStroke The stroke used to draw tick marks (if visible).
   */
  public Axis(String label, Font labelFont, Paint labelPaint, Insets labelInsets,
           boolean showTickLabels, Font tickLabelFont, Paint tickLabelPaint, Insets tickLabelInsets,
           boolean showTickMarks, Stroke tickMarkStroke) {

    this.label = label;
    this.labelFont = labelFont;
    this.labelPaint = labelPaint;
    this.labelInsets = labelInsets;
    this.showTickLabels = showTickLabels;
    this.tickLabelFont = tickLabelFont;
    this.tickLabelPaint = tickLabelPaint;
    this.tickLabelInsets = tickLabelInsets;
    this.showTickMarks = showTickMarks;
    this.tickMarkStroke = tickMarkStroke;

    this.ticks = new java.util.ArrayList();
    this.listeners = new java.util.ArrayList();

  }

  /**
   * Standard constructor - builds an axis with default values for most attributes.
   * <P>
   * Note that this class is not intended to be instantiated directly - use a subclass.
   * @param label The axis label;
   */
  public Axis(String label) {
    this(label, new Font("Arial", Font.BOLD, 14), Color.black, new Insets(4, 4, 4, 4),
         true, new Font("Arial", Font.PLAIN, 10), Color.black, new Insets(2, 2, 2, 2),
         true, new BasicStroke(1));
  }

  /**
   * Returns the plot that the axis is currently assigned to (or null if the axis is not currently
   * assigned to a plot).
   */
  public Plot getPlot() {
    return plot;
  }

  /**
   * Sets a reference to the plot that the axis is assigned to.  This method is called by Plot in
   * the setHorizontalAxis() and setVerticalAxis() methods.  You shouldn't need to call the
   * method yourself.
   * @param plot The plot that the axis belongs to;
   */
  public void setPlot(Plot plot) {
    this.plot = plot;
  }

  /**
   * Returns the label for the axis.
   */
  public String getLabel() {
    return label;
  }

  /**
   * Sets the label for the axis and notifies registered listeners that the axis has been modified.
   * @param label The new label for the axis.
   */
  public void setLabel(String label) {
    if (!label.equals(this.label)) {
      this.label = label;
      notifyListeners(new AxisChangeEvent(this));
    }
  }

  /**
   * Returns the font for the axis label.
   */
  public Font getLabelFont() {
    return labelFont;
  }

  /**
   * Sets the font for the axis label and notifies registered listeners that the axis has been
   * modified.
   * @param font The new label font.
   */
  public void setLabelFont(Font font) {
    if (!font.equals(this.labelFont)) {
      this.labelFont = font;
      notifyListeners(new AxisChangeEvent(this));
    }
  }

  /**
   * Returns the paint used to draw the axis label.
   */
  public Paint getLabelPaint() {
    return this.labelPaint;
  }

  /**
   * Sets the paint used to draw the axis label, and notifies registered listeners that the axis
   * has been modified.
   * @param paint The new label paint.
   */
  public void setLabelPaint(Paint paint) {
    if (!paint.equals(this.labelPaint)) {
      this.labelPaint = paint;
      notifyListeners(new AxisChangeEvent(this));
    }
  }

  /**
   * Returns the insets for the label (that is, the amount of blank space that should be left
   * around the label).
   */
  public Insets getLabelInsets() {
    return this.labelInsets;
  }

  /**
   * Sets the insets for the axis label, and notifies registered listeners that the axis has been
   * modified.
   * @param insets The new label insets;
   */
  public void setLabelInsets(Insets insets) {
    if (!insets.equals(this.labelInsets)) {
      this.labelInsets = insets;
      notifyListeners(new AxisChangeEvent(this));
    }
  }

  /**
   * Returns true if the tick labels are showing, and false otherwise.
   */
  public boolean isShowTickLabels() {
    return showTickLabels;
  }

  /**
   * Sets the flag that determines whether or not the tick labels are showing, and notifies
   * registered listeners that the axis has been modified.
   * @param show Flag that determines whether or not tick labels are visible.
   */
  public void setShowTickLabels(boolean show) {
    if (show!=showTickLabels) {
      showTickLabels = show;
      notifyListeners(new AxisChangeEvent(this));
    }
  }

  /**
   * Returns the font used for the tick labels.
   */
  public Font getTickLabelFont() {
    return tickLabelFont;
  }

  /**
   * Sets the font for the tick labels, and notifies registered listeners that the axis has been
   * modified.
   * @param font The new tick label font.
   */
  public void setTickLabelFont(Font font) {
    if (!font.equals(this.tickLabelFont)) {
      this.tickLabelFont = font;
      notifyListeners(new AxisChangeEvent(this));
    }
  }

  /**
   * Returns the paint used for the tick labels.
   */
  public Paint getTickLabelPaint() {
    return this.tickLabelPaint;
  }

  /** Sets the paint used to draw tick labels (if they are visible), and notifies registered
      listeners that the axis has been modified. */
  public void setTickLabelPaint(Paint paint) {
    if (!paint.equals(this.tickLabelPaint)) {
      this.tickLabelPaint = paint;
      notifyListeners(new AxisChangeEvent(this));
    }
  }

  /** Returns the insets for the tick labels. */
  public Insets getTickLabelInsets() {
    return this.tickLabelInsets;
  }

  /**
   * Sets the insets for the tick labels, and notifies registered listeners that the axis has
   * been modified.
   * @param insets The new tick label insets.
   */
  public void setTickLabelInsets(Insets insets) {
    if (!insets.equals(this.tickLabelInsets)) {
      this.tickLabelInsets = insets;
      notifyListeners(new AxisChangeEvent(this));
    }
  }

  /**
   * Returns true if the tick marks for the axis are visible, and false otherwise.
   */
  public boolean isShowTickMarks() {
    return showTickMarks;
  }

  /**
   * Sets the flag that determines whether or not the tick marks are visible, and notifies
   * registered listeners that the axis has been modified.
   * @param show Flag that determines whether or not tick marks are visible.
   */
  public void setShowTickMarks(boolean show) {
    if (show!=showTickMarks) {
      showTickMarks = show;
      notifyListeners(new AxisChangeEvent(this));
    }
  }

  /**
   * Returns the stroke used to draw tick marks (if they are visible).
   */
  public Stroke getTickMarkStroke() {
    return tickMarkStroke;
  }

  /**
   * Sets the stroke used to draw tick marks (if they are visible) and notifies registered
   * listeners that the axis has been changed.
   * @param stroke The new stroke;
   */
  public void setTickMarkStroke(Stroke stroke) {
    if (!stroke.equals(this.tickMarkStroke)) {
      this.tickMarkStroke = stroke;
      notifyListeners(new AxisChangeEvent(this));
    }
  }

  /**
   * Draws the Axis on a Java 2D graphics device (such as the screen or a printer).
   * @param g2 The graphics device;
   * @param drawArea The area within which the axis should be drawn;
   * @param plotArea The area within which the plot is being drawn.
   */
  public abstract void draw(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea);

  /**
   * Calculates the positions of the ticks for the axis, storing the results in the
   * tick list (ready for drawing).
   */
  public abstract void refreshTicks(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea);

  /**
   * Configures the axis to work with the specified plot.  Override this method to perform any
   * special processing (such as auto-rescaling).
   * @param plot The plot that the axis is to be configured for;
   */
  public void configure() {
    // default does nothing - subclasses can override
  }

  /**
   * Returns the maximum width of the ticks in the working list (that is set up by
   * refreshTicks()).
   */
  protected double getMaxTickLabelWidth(Graphics2D g2, Rectangle2D plotArea) {

    double maxWidth = 0.0;
    Font font = getTickLabelFont();
    FontRenderContext frc = g2.getFontRenderContext();

    Iterator iterator = this.ticks.iterator();
    while (iterator.hasNext()) {
      Tick tick = (Tick)iterator.next();
      Rectangle2D labelBounds = font.getStringBounds(tick.getText(), frc);
      if (labelBounds.getWidth()>maxWidth) {
        maxWidth = labelBounds.getWidth();
      }
    }
    return maxWidth;

  }

  /**
   * Notifies all registered listeners that the axis has changed in some way.
   * @param event An object that contains information about the change to the axis;
   */
  public void notifyListeners(AxisChangeEvent event) {
    java.util.Iterator iterator = listeners.iterator();
    while (iterator.hasNext()) {
      AxisChangeListener listener = (AxisChangeListener)iterator.next();
      listener.axisChanged(event);
    }
  }

  /**
   * Registers an object for notification of changes to the axis.
   * @param listener The object that is being registered;
   */
  public void addChangeListener(AxisChangeListener listener) {
    listeners.add(listener);
  }

  /**
   * Unregisters an object for notification of changes to the chart.
   * @param listener The object that is being unregistered;
   */
  public void removeChangeListener(AxisChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * A utility method for drawing text vertically.
   */
  protected void drawVerticalString(String text, Graphics2D g2, float x, float y) {

    AffineTransform saved = g2.getTransform();

    // apply a 90 degree rotation
    AffineTransform rotate = AffineTransform.getRotateInstance(-Math.PI/2, x, y);
    g2.transform(rotate);
    g2.drawString(text, x, y);

    g2.setTransform(saved);

  }

}