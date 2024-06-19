package com.jrefinery.chart;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.*;
import javax.swing.*;
import com.jrefinery.chart.event.*;

public abstract class WindAxis extends Axis {

  protected boolean autoRange;

  protected boolean autoTickValue;

  protected int autoTickIndex;

  /** Flag that indicates whether or not grid lines are showing for this axis. */
  protected boolean showGridLines;

  /** The stroke used to draw grid lines. */
  protected Stroke gridStroke;

  /** The paint used to draw grid lines. */
  protected Paint gridPaint;

  /**
   * Full constructor - initialises the attributes for a ValueAxis.  This is an abstract class,
   * subclasses include HorizontalValueAxis and VerticalValueAxis.
   * @param label The axis label;
   * @param labelFont The font for displaying the axis label;
   * @param labelPaint The paint used to draw the axis label;
   * @param labelInsets Determines the amount of blank space around the label;
   * @param showTickLabels Flag indicating whether or not tick labels are visible;
   * @param tickLabelFont The font used to display tick labels;
   * @param tickLabelPaint The paint used to draw tick labels;
   * @param tickLabelInsets Determines the amount of blank space around tick labels;
   * @param showTickMarks Flag indicating whether or not tick marks are visible;
   * @param tickMarkStroke The stroke used to draw tick marks (if visible);
   * @param autoRange Flag indicating whether or not the axis range is automatically adjusted to
   *                  fit the data;
   * @param autoTickValue A flag indicating whether or not the tick value is automatically
   *                      calculated;
   * @param tickUnits The tick units;
   * @param showGridLines Flag indicating whether or not grid lines are visible for this axis;
   * @param gridStroke The Stroke used to display grid lines (if visible);
   * @param gridPaint The Paint used to display grid lines (if visible).
   */
  public WindAxis(String label, Font labelFont, Paint labelPaint, Insets labelInsets,
           boolean showTickLabels, Font tickLabelFont, Paint tickLabelPaint, Insets tickLabelInsets,
           boolean showTickMarks, Stroke tickMarkStroke,
           boolean autoRange, boolean autoTickValue,
           boolean showGridLines, Stroke gridStroke, Paint gridPaint) {

    super(label, labelFont, labelPaint, labelInsets,
          showTickLabels, tickLabelFont, tickLabelPaint, tickLabelInsets,
          showTickMarks, tickMarkStroke);

    this.autoRange = autoRange;
    this.autoTickValue = autoTickValue;
    this.showGridLines = showGridLines;
    this.gridStroke = gridStroke;
    this.gridPaint = gridPaint;

  }

  /**
   * Standard constructor - initialises the attributes for a ValueAxis.
   * @param label The axis label;
   */
  public WindAxis(String label) {
    super(label);
    this.autoRange = true;
    this.autoTickValue = true;
    this.showGridLines = true;
    this.gridStroke = new BasicStroke(0.25f, BasicStroke.CAP_BUTT,
                                             BasicStroke.JOIN_ROUND, 0.0f,
                                             new float[] {2.0f, 2.0f}, 0.0f);
    this.gridPaint = Color.gray;
  }

  /**
   * Returns true if the axis range is automatically adjusted to fit the data, and false
   * otherwise.
   */
  public boolean isAutoRange() {
    return autoRange;
  }

  /**
   * Sets a flag that determines whether or not the axis range is automatically adjusted to fit the
   * data, and notifies registered listeners that the axis has been modified.
   * @param auto Flag indicating whether or not the axis is automatically scaled to fit the data.
   */
  public void setAutoRange(boolean auto) {
    if (this.autoRange!=auto) {
      this.autoRange=auto;
      if (autoRange) autoAdjustRange();
      notifyListeners(new AxisChangeEvent(this));
    }
  }

  /**
   * Returns true if the tick value is calculated automatically, and false otherwise.
   */
  public boolean getAutoTickValue() {
    return autoTickValue;
  }

  /**
   * Sets a flag indicating whether or not the tick value is calculated automatically, and
   * notifies registered listeners that the axis has been modified.
   * @param flag The new value of the flag;
   */
  public void setAutoTickValue(boolean flag) {
    this.autoTickValue = flag;
    notifyListeners(new AxisChangeEvent(this));
  }

  /**
   * Returns true if the grid lines are visible for this axis, and false otherwise.
   */
  public boolean isShowGridLines() {
    return showGridLines;
  }

  /**
   * Sets the visibility of the grid lines and notifies registered listeners that the axis has been
   * modified.
   * @param show The new setting;
   */
  public void setShowGridLines(boolean show) {
    showGridLines = show;
    notifyListeners(new AxisChangeEvent(this));
  }

  /**
   * Returns the Stroke used to draw the grid lines (if visible).
   */
  public Stroke getGridStroke() {
    return gridStroke;
  }

  /**
   * Sets the Stroke used to draw the grid lines (if visible) and notifies registered listeners
   * that the axis has been modified.
   * @param stroke The new grid line stroke.
   */
  public void setGridStroke(Stroke stroke) {
    gridStroke = stroke;
    notifyListeners(new AxisChangeEvent(this));
  }

  /**
   * Returns the Paint used to color the grid lines (if visible).
   */
  public Paint getGridPaint() {
    return gridPaint;
  }

  /**
   * Sets the Paint used to color the grid lines (if visible) and notifies registered listeners
   * that the axis has been modified.
   * @param paint The new grid paint;
   */
  public void setGridPaint(Paint paint) {
    gridPaint = paint;
    notifyListeners(new AxisChangeEvent(this));
  }

  /**
   * Automatically determines the maximum and minimum values on the axis to 'fit' the data;
   */
  public abstract void autoAdjustRange();

  /**
   * Converts a value from the data source to a Java2D user-space co-ordinate relative to the
   * specified plotArea.  The coordinate will be an x-value for horizontal axes and a y-value
   * for vertical axes (refer to the subclass).
   * <p>
   * Note that it is possible for the coordinate to fall outside the plotArea.
   */
  public abstract double translateValueToJava2D(double dataValue, Rectangle2D plotArea);

}