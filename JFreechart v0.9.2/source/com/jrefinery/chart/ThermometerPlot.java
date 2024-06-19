/*
 *  =======================================
 *  JFreeChart : a Java Chart Class Library
 *  =======================================
 *
 *  Project Info:  http://www.object-refinery.com/jfreechart/index.html
 *  Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 *  (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 *  This library is free software; you can redistribute it and/or modify it under the terms
 *  of the GNU Lesser General Public License as published by the Free Software Foundation;
 *  either version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307, USA.
 *
 *  --------------------
 *  ThermometerPlot.java
 *  --------------------
 *  A plot that displays a single value in a thermometer type display.
 *
 *  NOTE:
 *  The Thermometer plot utilises a meter data set, however range options within this data set are
 *  not currently utilised.  This is currently under consideration / development.
 *
 *  The Thermometer supports a number of options
 *
 *  1. 3 ranges which could be viewed as Critical, Warning and Normal ranges.
 *  2. The thermometer can be run in two modes:
 *     a. fixed range where colour changes on value, or
 *     b. 3 seperate ranges where colour changes with range changes.
 *  3. Settable units to be displayed.
 *  4. Settable display location for the value text
 *
 *  (C) Copyright 2000-2002,
 *
 *  Original Author:  Bryan Scott;
 *  Based on MeterPlot by Hari.
 *  Contributor(s):
 *
 *  Changes
 *  -------
 *  11-Apr-2002 : Version 1, contributed by Bryan Scott;
 *  15-Apr-2002 : Changed to implement VerticalValuePlot;
 *  29-Apr-2002 : Added getVerticalValueAxis() method (DG);
 *  25-Jun-2002 : Removed redundant imports (DG);
 *
 */

package com.jrefinery.chart;

import com.jrefinery.data.Range;
import com.jrefinery.data.DatasetChangeEvent;
import com.jrefinery.chart.event.PlotChangeEvent;
import com.jrefinery.data.MeterDataset;
import com.jrefinery.data.DefaultMeterDataset;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.util.List;

/**
 * A plot that displays a single value in a thermometer type display.
 *
 * NOTE:
 * The Thermometer plot utilises a meter data set, however range options within this data set are
 * not currently utilised.  This is currently under consideration / development.
 *
 * The Thermometer supports a number of options
 *
 * 1. 3 ranges which could be viewed as Critical, Warning and Normal ranges.
 * 2. The thermometer can be run in two modes:
 *    a. fixed range where colour changes on value, or
 *    b. 3 seperate ranges where colour changes with range changes.
 * 3. Settable units to be displayed.
 * 4. Settable display location for the value text
 *
 */

public class ThermometerPlot extends Plot implements VerticalValuePlot {

    /**  Units to be displayed */
    public final static int UNITS_NONE = 0;
    public final static int UNITS_FARINHEIGHT = 1;
    public final static int UNITS_CELCIUS = 2;
    public final static int UNITS_KELVIN = 3;
    protected final static String[] UNITS = { "", "°F", "°C", "°K" };

    protected MeterDataset data;

    /** The currently selected display units */
    protected int units = UNITS_CELCIUS;

    /**
     * Some general purpose graphics2D objects, used to stop excessive object creation / garbage
     * collection.
     */
    RoundRectangle2D.Double outer1, outer2, outer3;
    Ellipse2D.Double circle1, circle2;

    /**  the selection of where value is written on screen */
    public final static int NONE = 0;
    public final static int LEFT = 1;
    public final static int BULB = 2;

    /**  Current location of where the value is written on screean */
    protected int valueLocation = BULB;

    /**  Some basic sizing options for the thermometer */
    protected static final int bulbRadius = 40;
    protected static final int bulbDiameter = 80;
    protected static final int columnRadius = 20;
    protected static final int columnDiameter = 40;
    protected static final int gapRadius = 5;
    protected static final int gapDiameter = 10;
    protected static final int legendWidth = 10;

    /**  The three ranges */
    public static int NORMAL   = 0;
    public static int WARNING  = 1;
    public static int CRITICAL = 2;

    /**
     * Two range variables are utilised to track which range is the current for the
     * display and data.
     */
    protected int range     = WARNING;
    protected int rangeData = WARNING;

    /**  Index to Range information matrix */
    protected final static int RANGE_HI    = 0;
    protected final static int RANGE_LOW   = 1;
    protected final static int DISPLAY_HI  = 2;
    protected final static int DISPLAY_LOW = 3;
    protected final static int DISPLAY_RANGE = 4;

    /**  Matrix of range data */
    protected double[][] rangeInfo = {
      { 2.0, -10.0,  1.0, -10.0, 11},
      {29.0,   0.0, 26.0,   0.0, 26},
      {41.0,  20.0, 40.0,  20.0, 20}
    };

    /**  Paint for each range */
    protected Paint[] rangePaint = {
        Color.blue,
        Color.yellow,
        Color.red
    };

    /**  Colour for the outline of the thermometer */
    protected Color outerColour = Color.black;

    /**  Colour that the value is written in */
    protected Color valueColour = Color.white;

    /**  The font to write the value in */
    protected Font valueFont = new Font("Arial", 1, 24);

    /**  Number format for the value */
    protected DecimalFormat valueFormat = new DecimalFormat();

    /** Stokes for drawing **/
    protected Stroke outerStroke = new BasicStroke(1.0f);

    protected Stroke rangeInidcatorStroke = new BasicStroke(3.0f);
    protected int rangeIndcatorStrokeSize = 3 ;

    protected boolean followValue = false ;
    protected boolean drawLines = false ;

    /**
     * Data Model Type : Basically used to enable / disable the code for supporting data model
     * range changes.  Currently set to true as only experimetal and not functional.
     **/
    protected boolean ignoreDataModelRangeChanges = true ;

    protected ValueAxis rangeAxis;

    public ThermometerPlot() {
        this(new DefaultMeterDataset());
    }

    /**Constructor for the ThermometerPlot object */
    public ThermometerPlot(MeterDataset data) {

        this(data,
             DEFAULT_INSETS,
             DEFAULT_BACKGROUND_PAINT,
             null,
             DEFAULT_BACKGROUND_ALPHA,
             DEFAULT_OUTLINE_STROKE,
             DEFAULT_OUTLINE_PAINT,
             DEFAULT_FOREGROUND_ALPHA);

    }

    /**
     * Constructs a new plot.
     *
     * @param insets Amount of blank space around the plot area.
     * @param backgroundPaint An optional color for the plot's background.
     * @param backgroundImage An optional image for the plot's background.
     * @param backgroundAlpha Alpha-transparency for the plot's background.
     * @param outlineStroke The Stroke used to draw an outline around the plot.
     * @param outlinePaint The color used to draw an outline around the plot.
     * @param foregroundAlpha The alpha-transparency for the plot foreground.
     */
    public ThermometerPlot(MeterDataset data, Insets insets, Paint backgroundPaint,
                           Image backgroundImage, float backgroundAlpha, Stroke outlineStroke,
                           Paint outlinePaint, float foregroundAlpha) {

        super(data, insets,
              backgroundPaint, backgroundImage, backgroundAlpha,
              outlineStroke, outlinePaint, foregroundAlpha);

        this.data = data;
        if (data!=null) {
            data.addChangeListener(this);
        }
        setInsets(insets);
        circle1 = new Ellipse2D.Double();
        circle2 = new Ellipse2D.Double();
        outer1 = new RoundRectangle2D.Double();
        outer2 = new RoundRectangle2D.Double();
        outer3 = new RoundRectangle2D.Double();
        setRangeAxis(new VerticalNumberAxis(null));
        setAxisRange();
    }

    /**
     * Sets the range axis for the plot.
     * <P>
     * An exception is thrown if the new axis and the plot are not mutually compatible.
     *
     * @param axis The new axis (null permitted).
     */
    public void setRangeAxis(ValueAxis axis) throws AxisNotCompatibleException {

        if (isCompatibleRangeAxis(axis)) {

            if (axis!=null) {
                try {
                    axis.setPlot(this);
                }
                catch (PlotNotCompatibleException e) {
                    throw new AxisNotCompatibleException("Plot.setRangeAxis(...): "
                                                        +"plot not compatible with axis.");
                }
                axis.addChangeListener(this);
            }

            // plot is likely registered as a listener with the existing axis...
            if (this.rangeAxis!=null) {
                this.rangeAxis.removeChangeListener(this);
            }

            this.rangeAxis = axis;

        }
        else throw new AxisNotCompatibleException("Plot.setRangeAxis(...): "
                                                 +"axis not compatible with plot.");

    }

    /**
     * A zoom method that does nothing.
     * <p>
     * Plots are required to support the zoom operation.  In the case of a thermometer chart, it
     * doesn't make sense to zoom in or out, so the method is empty.
     *
     * @param  percent  The zoom percentage.
     */
    public void zoom(double percent) { }

    /**
     * Returns a short string describing the type of plot.
     *
     * @return    The plotType value
     */
    public String getPlotType() {
        return "Thermometer Plot";
    }

    /**
     * Returns the dataset.
     * <P>
     * Provided for convenience.
     *
     * @return    The dataset for the plot, cast as a MeterDataset.
     */
    public MeterDataset getData() {
        return data;
    }


    /**
     * Sets the data for the chart, replacing any existing data.
     * <P>
     * Registered listeners are notified that the chart has been modified.
     *
     * @param data The new dataset.
     */
    public void setData(MeterDataset data) {

        // if there is an existing dataset, remove the chart from the list of change listeners...
        MeterDataset existing = this.data;
        if (existing!=null) {
            existing.removeChangeListener(this);
        }

        // set the new dataset, and register the plot as a change listener...
        this.data = data;
        if (this.data!=null) {
            data.addChangeListener(this);
        }

        // notify plot change listeners...
        PlotChangeEvent event = new PlotChangeEvent(this);
        notifyListeners(event);

    }

    /**
     * Returns true if the axis is compatible with the meter plot, and false otherwise.  Since a
     * Thermometer plot requires no horizontal axes, only a null axis is compatible.
     *
     * @param  axis  The axis.
     * @return       The compatibleHorizontalAxis value
     */
    public boolean isCompatibleHorizontalAxis(Axis axis) {
        if (axis == null) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Returns true if the axis is compatible with the meter plot, and false otherwise.  Since a
     * Thermometer plot requires a VerticalNumberAxis, only a VerticalNumberAxis axis is compatible.
     *
     * @param  axis  The axis.
     * @return       The compatibleVerticalAxis value
     */
    public boolean isCompatibleVerticalAxis(Axis axis) {
        if (axis instanceof VerticalNumberAxis) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Determine whether a number is valid and finite
     *
     * @param  i  The number to be tested
     * @return    where or not the number is valid and not infinite.
     */
    protected static boolean isValidNumber(double i) {
        return (!(Double.isNaN(i) || Double.isInfinite(i)));
    }

    /**
     *  Sets the format attribute for the value label
     *
     * @param  fo  The new value format
     */
    public void setValueFormat(DecimalFormat fo) {
        if (fo != null) {
            valueFormat = fo;
        }
    }

    /**
     *  Gets the display location for the value
     *
     * @return    The display location for the value
     */
    public int getValueDisplayLocation() {
        return valueLocation;
    }

    /**
     *  Sets the units for this thermometer
     *
     * @param  u  The new units value
     */
    public void setUnits(int u) {
        if ((u >= 0) && (u < UNITS.length)) {
            if (units != u) {
                units = u;
                notifyListeners(new PlotChangeEvent(this));
            }
        }
    }

    public void setUnits(String u) {
      if (u == null)
        return ;

      u = u.toUpperCase().trim();
      for (int i = 0; i < UNITS.length; ++i) {
        if (u.equals(UNITS[i].toUpperCase().trim())){
          setUnits(i);
          i = UNITS.length;
        }
      }
    }

    /**
     * Sets the font for the thermometer plot.
     *
     * @param f The new font value.
     */
    public void setFont(Font f) {

        if (f!=null) {
            rangeAxis.setTickLabelFont(f);
            setValueFont(f);
        }
    }

    /**
     *  Sets the font for the current value display
     *
     * @param  f  The new value font
     */
    public void setValueFont(Font f) {
        if ((f != null) && (!valueFont.equals(f))) {
            valueFont = f;
            notifyListeners(new PlotChangeEvent(this));
        }
    }


    /**
     *  Gets the display units
     *
     * @return    The units value
     */
    public int getUnits() {
        return units;
    }

    /**
     *  Gets the Font used to display the current value
     *
     * @return    The current value font
     */
    public Font getValueFont() {
        return valueFont;
    }

    /**
     *  Sets the paint to be used for a range
     *
     * @param  range  The range
     * @param  paint  The paint to be applied
     */
    public void setRangePaint(int range, Paint paint) {
        if ((range >= 0) && (range < rangePaint.length) && (paint != null)) {
            rangePaint[range] = paint;
        }
    }

    /**
     *  Gets the paint used for a particular range
     *
     * @param  range  The range
     * @return        The paint used
     */
    public Paint getRangePaint(int range) {
        if ((range >= 0) && (range < rangePaint.length)) {
            return rangePaint[range];
        }
        else {
            return Color.black;
        }
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param  g2        The graphics device.
     * @param  plotArea  The area within which the plot should be drawn.
     * @param  info      Collects info about the drawing.
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, ChartRenderingInfo info) {
        Area a1, a2, a3, a4;
        int i = 0;
        int j = 0;
        int l = 0;
        int k = 0;
        int tickY = 0;
        int tickX1 = 0 ;
        int tickX2 = 0 ;

        String temp = null;
        FontMetrics metrics = null;

        /// Setup tool tips
//        ToolTipsCollection tooltips = null;
        if (info != null) {
            info.setPlotArea(plotArea);
//            tooltips = info.getToolTipsCollection();
        }

        // adjust for insets...
        if (insets != null) {
            plotArea.setRect(plotArea.getX() + insets.left,
            plotArea.getY() + insets.top,
            plotArea.getWidth() - insets.left - insets.right,
            plotArea.getHeight() - insets.top - insets.bottom);
        }
        drawOutlineAndBackground(g2, plotArea);

        int midX = (int) (plotArea.getX() + (plotArea.getWidth() / 2));
        int midY = (int) (plotArea.getY() + (plotArea.getHeight() / 2));
        int ticksYStart = (int) (plotArea.getMinY() + bulbRadius);
        int ticksYStop  = (int) (plotArea.getMaxY() - bulbDiameter);

        /// Setup a1 and a2 with gauge outline
        circle1.setFrame(midX - bulbRadius, ticksYStop, bulbDiameter, bulbDiameter);
        k = ticksYStop + gapRadius;
        i = bulbDiameter - gapDiameter;
        j = bulbRadius - gapRadius;
        circle2.setFrame(midX - j, k, i, i);

        outer1.setRoundRect(midX - columnRadius, plotArea.getMinY(), columnDiameter, k, columnDiameter, columnDiameter);
        i = columnDiameter - gapDiameter;
        j = columnRadius - gapRadius;
        outer2.setRoundRect(midX - j, plotArea.getMinY() + gapRadius, i, k, i, i);

        a1 = new Area(circle1);
        a2 = new Area(outer1);
        a1.add(a2);

        a2 = new Area(outer2);
        a3 = new Area(circle2);
        a2.add(a3);

        if ((data != null) && (data.isValueValid())) {
            double current = data.getValue().doubleValue();

            /// Determine Display Range
            rangeData = range ;
            switch (range) {
                case 0:
                    if (current > rangeInfo[1][RANGE_HI]) {range = 2; }
                    if (current > rangeInfo[0][RANGE_HI]) {range = 1; }
                    break;
                case 1:
                    if (current > rangeInfo[1][DISPLAY_HI]) {range = 2; }
                    if (current < rangeInfo[1][DISPLAY_LOW]){range = 0; }
                    break;
                case 2:
                    if (current < rangeInfo[2][RANGE_LOW]) {range = 1;}
                    if (current < rangeInfo[1][RANGE_LOW]) {range = 0;}
                    break;
                default:
                    range = 1;
            }

      /// Detect a change in the display range, if so update the vertical axis.
      if (rangeData != range) {
        setAxisRange();
      }

      /// Determine Data Range, always the same as display except in the middle range
      rangeData = range ;
      if (followValue && (range == 1)) {
        if (current < rangeInfo[0][RANGE_HI]) {rangeData = 0; }
        if (current > rangeInfo[2][RANGE_LOW]){rangeData = 2; }
      }

      /// Draw gauge display
      double ds = ((current - rangeInfo[range][DISPLAY_LOW]) / rangeInfo[range][DISPLAY_RANGE])
                * (ticksYStop - ticksYStart);
      i = columnDiameter - gapDiameter;  // Already calculated
      j = columnRadius - gapRadius;      // Already calculated
      l = (int) (i / 2);
      k = (int) (ticksYStop - Math.round(ds));
      if (k < (gapRadius + plotArea.getMinY())) {
        k = (int) (gapRadius + plotArea.getMinY());
        l = bulbRadius;
      }

      if (k < (ticksYStop + bulbRadius)) {
        outer3.setRoundRect(midX - j, k, i, (ticksYStop + bulbRadius) - k, l, l);
        a4 = new Area(outer3);
        a3.add(a4);
      }

      g2.setPaint(rangePaint[rangeData]);
      g2.fill(a3);

      /// Draw Axis
      i = 0;
      if (drawLines)
        i += columnDiameter ;
      Rectangle2D drawArea = new Rectangle2D.Double(midX-columnRadius-legendWidth,
                             ticksYStart, legendWidth+i , (ticksYStop - ticksYStart + 1));
      this.rangeAxis.draw(g2, plotArea, drawArea);

      /// Draw Range Inidcators
      g2.setStroke(rangeInidcatorStroke);

      if (rangeIndcatorStrokeSize > 0) {
        double incY = ((ticksYStop - ticksYStart) / rangeInfo[range][DISPLAY_RANGE]);
        tickX1 = midX + columnRadius + rangeIndcatorStrokeSize ;
        tickX2 = tickX1 + 10 ;
        if (inRange(range, rangeInfo[1][RANGE_LOW])) {
            tickY = ticksYStart ;
            tickY += (int) (incY * (rangeInfo[range][DISPLAY_HI] - rangeInfo[1][RANGE_LOW])) ;
            g2.setPaint(rangePaint[1]);
            g2.drawLine(tickX1, tickY, tickX2, tickY);
        }

        if (inRange(range, rangeInfo[2][RANGE_LOW])) {
            tickY = ticksYStart ;
            tickY += (int) (incY *(rangeInfo[range][DISPLAY_HI]-rangeInfo[2][RANGE_LOW])) ;
            g2.setPaint(rangePaint[2]);
            g2.drawLine(tickX1, tickY, tickX2, tickY);
        }
      }

      /// Draw text value on screen
      g2.setFont(valueFont);
      g2.setColor(valueColour);
      metrics = g2.getFontMetrics();
      switch (valueLocation) {
        case LEFT:
          g2.drawString(valueFormat.format(current), midX + columnRadius + gapRadius, midY);
          break;
        case BULB:
          temp = valueFormat.format(current);
          i = (int) (metrics.stringWidth(temp) / 2);
          g2.drawString(temp, midX - i, ticksYStop + bulbRadius + gapRadius);
          break;
        default:
      }
    }

    g2.setColor(outerColour);
    g2.setFont(valueFont);

    /// Draw units incidicator
    metrics = g2.getFontMetrics();
    tickX1 = midX - columnRadius - gapDiameter - metrics.stringWidth(UNITS[units]);
    if (tickX1 > plotArea.getMinX()) {
      g2.drawString(UNITS[units], tickX1, (int) (plotArea.getMinY() + 20));
    }

        /// draw thermometer gauge outline
        if (outerColour != null) {
            g2.setStroke(outerStroke);
            g2.draw(a1);
            g2.draw(a2);
        }
    }

  /**
   *  Sets information for a particular range
   *
   * @param  range        The range to specify information about
   * @param  hi   The High value for the range
   * @param  low  The Low value for the range
   */
  public void setRangeInfo(int range, double low, double hi) {
    setRangeInfo(range, low, hi, low, hi);
  }

  /**
   *  Sets the rangeInfo attribute of the ThermometerPlot object
   *
   * @param  range        The new rangeInfo value
   * @param  range_hi     The new rangeInfo value
   * @param  range_low    The new rangeInfo value
   * @param  display_hi   The new rangeInfo value
   * @param  display_low  The new rangeInfo value
   */
  public void setRangeInfo(int range, double range_low, double range_hi,
      double display_low, double display_hi) {
    if ((range >= 0) && (range < 3)) {
      setRange(range, range_low, range_hi);
      setDisplayRange(range, display_low, display_hi);
    }
  }

  public void setRange(int range, double low, double hi) {
    if ((range >= 0) && (range < 3)) {
      rangeInfo[range][RANGE_HI] = hi;
      rangeInfo[range][RANGE_LOW] = low;
    }
  }

  public void setDisplayRange(int range, double low, double hi) {
    if ((range >= 0) && (range < rangePaint.length)
       && isValidNumber(hi) && isValidNumber(low)) {

      if (hi > low) {
        rangeInfo[range][DISPLAY_HI] = hi;
        rangeInfo[range][DISPLAY_LOW] = low;
      } else {
        rangeInfo[range][DISPLAY_HI] = hi;
        rangeInfo[range][DISPLAY_LOW] = low;
      }

      rangeInfo[range][DISPLAY_RANGE] = rangeInfo[range][DISPLAY_HI]
                                      - rangeInfo[range][DISPLAY_LOW] ;
    }
  }


  public void setDisplayLocation(int loc) {
    if ((loc >= 0) && (loc < 3)) {
      valueLocation = loc ;
    }
  }

  /**
   * Set the outline colour of the thermometer
   * @param c new colour to set the outline of the thermometer
   */
  public void setThermometerColor(Color c) {
    outerColour = c ;
  }

  /**
   * Whether value is in the display range of specified range.
   *
   * @param range  The range to compare against
   * @param value  the value to compare
   * @return       whether value is in the display range of specified range
   */
  private boolean inRange(int range, double value) {
    return (
      (rangeInfo[range][DISPLAY_HI]  > value) &&
      (rangeInfo[range][DISPLAY_LOW] < value)
    ) ;
  }

  private void checkDataModelData() {

    if (data != null) {
      Number test = data.getMaximumCriticalValue() ;
      if (test != null)
        rangeInfo[CRITICAL][RANGE_HI] = test.doubleValue();
      test = data.getMinimumCriticalValue();
      if (test != null)
        rangeInfo[CRITICAL][RANGE_LOW] = test.doubleValue();

      test = data.getMaximumWarningValue();
      if (test != null)
        rangeInfo[WARNING][RANGE_HI] = test.doubleValue();
      test = data.getMinimumWarningValue();
      if (test != null)
        rangeInfo[WARNING][RANGE_LOW] = test.doubleValue();

      test = data.getMaximumNormalValue();
      if (test != null)
        rangeInfo[NORMAL][RANGE_HI] = test.doubleValue();
      test = data.getMinimumNormalValue();
      if (test != null)
        rangeInfo[NORMAL][RANGE_LOW] = test.doubleValue();
    }
  } ;

  /***
   * This is experimental, an early attempt to support data model ranges being changed
   */
  public void datasetChanged(DatasetChangeEvent event) {
    if (!ignoreDataModelRangeChanges) {
      checkDataModelData() ;
    }
    super.datasetChanged(event);
  }

    /**
     * Returns the minimum value in either the domain or the range, whichever is displayed against
     * the vertical axis for the particular type of plot implementing this interface.
     */
    public Number getMinimumVerticalDataValue() {
      return new Double(rangeInfo[range][DISPLAY_LOW]);
    }

    /**
     * Returns the maximum value in either the domain or the range, whichever is displayed against
     * the vertical axis for the particular type of plot implementing this interface.
     */
    public Number getMaximumVerticalDataValue() {
      return new Double(rangeInfo[range][DISPLAY_HI]);
    }

    public Range getVerticalDataRange() {
        return new Range(rangeInfo[range][DISPLAY_LOW], rangeInfo[range][DISPLAY_HI]);
    }

    /**
     * Sets the display as to whether to show value lines in the output.
     *
     * @param b Whether to show value lines in the thermometer
     */
    public void setShowValueLines(boolean b) {
      drawLines = b ;
    }

    public boolean getShowValueLines(boolean b) {
      return drawLines ;
    }

    /**
     * Sets the range colour change option.
     *
     * @param b The new range colour change option
     */
    public void setFollowData(boolean v) {
        followValue = v ;
    }

    /**
     * Get whether the thermometer paint changes with data (true) or range (false)
     * @return the value for follow data
     */
    public boolean getFollowData() {
        return followValue ;
    }

    /**
     * Sets the width of the range inidctators.  Set to 0 to disable range inidcator
     * display.
     * @param size the width of the inidcator. valid range 0-15 inclusive
     */
    public void setRangeIndicatorWidth(int width) {
        if ((width != rangeIndcatorStrokeSize) && (width >=0) && (width < 16)) {
            rangeIndcatorStrokeSize = width ;
            rangeInidcatorStroke = new BasicStroke(width);
        }
    }

    /**
     * Gets the width of the range inidictators
     * @return the width of range indicators
     */
    public int getRangeIndicatorWidth() {
        return rangeIndcatorStrokeSize ;
    }

    public void setOuterStoke(Stroke s) {
        if (s != null) {
            outerStroke = s ;
        }
    }

    public Stroke getOuterStroke() {
        return outerStroke ;
    }

    public void propertyChange() {
        this.notifyListeners(new PlotChangeEvent(this));
    }

    protected void setAxisRange() {
        rangeAxis.setRange(new Range(rangeInfo[range][DISPLAY_LOW], rangeInfo[range][DISPLAY_HI]));
    }

    public List getLegendItemLabels() {
        return null;
    }

    /**
     * Checks the compatibility of a range axis, returning true if the axis is compatible with
     * the plot, and false otherwise.
     *
     * @param axis The proposed axis.
     *
     * @return True if the axis is compatible with the plot, and false otherwise.
     */
    public boolean isCompatibleRangeAxis(ValueAxis axis) {

        if (axis==null) {
            return true;
        }
        if (axis instanceof VerticalAxis) {
            return true;
        }
        else return false;
    }

    public ValueAxis getVerticalValueAxis() {
        return this.rangeAxis;
    }

}
