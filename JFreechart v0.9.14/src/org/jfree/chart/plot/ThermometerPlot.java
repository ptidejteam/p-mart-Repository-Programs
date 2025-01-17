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
 * ThermometerPlot.java
 * --------------------
 *
 * (C) Copyright 2000-2003, by Bryan Scott and Contributors.
 *
 * Original Author:  Bryan Scott (based on MeterPlot by Hari).
 * Contributor(s):   David Gilbert (for Object Refinery Limited).
 *                   Arnaud Lelievre;
 *
 * Changes
 * -------
 * 11-Apr-2002 : Version 1, contributed by Bryan Scott;
 * 15-Apr-2002 : Changed to implement VerticalValuePlot;
 * 29-Apr-2002 : Added getVerticalValueAxis() method (DG);
 * 25-Jun-2002 : Removed redundant imports (DG);
 * 17-Sep-2002 : Reviewed with Checkstyle utility (DG);
 * 18-Sep-2002 : Extensive changes made to API, to iron out bugs and inconsistencies (DG);
 * 13-Oct-2002 : Corrected error datasetChanged which would generate exceptions when value set
 *               to null (BRS).
 * 23-Jan-2003 : Removed one constructor (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 02-Jun-2003 : Removed test for compatible range axis (DG);
 * 01-Jul-2003 : Added additional check in draw method to ensure value not null (BRS);
 * 08-Sep-2003 : Added internationalization via use of properties resourceBundle (RFE 690236) (AL);#
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 29-Sep-2003 : Updated draw to set value of cursor to non-zero and allow painting of axis.
 *               An incomplete fix and needs to be set for left or right drawing (BRS);
 *
 */

package org.jfree.chart.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.ResourceBundle;

import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.Spacer;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.data.DatasetChangeEvent;
import org.jfree.data.DefaultValueDataset;
import org.jfree.data.Range;
import org.jfree.data.ValueDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ObjectUtils;

/**
 * A plot that displays a single value (from a {@link ValueDataset}) in a thermometer type display.
 * <p>
 * This plot supports a number of options:
 * <ol>
 * <li>three sub-ranges which could be viewed as 'Normal', 'Warning' and 'Critical' ranges.</li>
 * <li>the thermometer can be run in two modes:
 *      <ul>
 *      <li>fixed range, or</li>
 *      <li>range adjusts to current sub-range.</li>
 *      </ul>
 * </li>
 * <li>settable units to be displayed.</li>
 * <li>settable display location for the value text.</li>
 * </ol>
 *
 * @author Bryan Scott
 */
public class ThermometerPlot extends Plot implements ValueAxisPlot,
                                                     Cloneable,
                                                     Serializable {

    /** A constant for unit type 'None'. */
    public static final int UNITS_NONE = 0;

    /** A constant for unit type 'Fahrenheit'. */
    public static final int UNITS_FAHRENHEIT = 1;

    /** A constant for unit type 'Celcius'. */
    public static final int UNITS_CELCIUS = 2;

    /** A constant for unit type 'Kelvin'. */
    public static final int UNITS_KELVIN = 3;

    /** A constant for the value label position (no label). */
    public static final int NONE = 0;

    /** A constant for the value label position (right of the thermometer). */
    public static final int RIGHT = 1;

    /** A constant for the value label position (in the thermometer bulb). */
    public static final int BULB = 2;

    /** A constant for the 'normal' range. */
    public static final int NORMAL   = 0;

    /** A constant for the 'warning' range. */
    public static final int WARNING  = 1;

    /** A constant for the 'critical' range. */
    public static final int CRITICAL = 2;

    /** The bulb radius. */
    protected static final int BULB_RADIUS = 40;

    /** The bulb diameter. */
    protected static final int BULB_DIAMETER = BULB_RADIUS * 2;

    /** The column radius. */
    protected static final int COLUMN_RADIUS = 20;

    /** The column diameter.*/
    protected static final int COLUMN_DIAMETER = COLUMN_RADIUS * 2;

    /** The gap radius. */
    protected static final int GAP_RADIUS = 5;

    /** The gap diameter. */
    protected static final int GAP_DIAMETER = GAP_RADIUS * 2;

    /** The axis gap. */
    protected static final int AXIS_GAP = 10;

    /** The unit strings. */
    protected static final String[] UNITS = {"", "\u00B0F", "\u00B0C", "\u00B0K"};

    /** Index for low value in subrangeInfo matrix. */
    protected static final int RANGE_LOW = 0;

    /** Index for high value in subrangeInfo matrix. */
    protected static final int RANGE_HIGH = 1;

    /** Index for display low value in subrangeInfo matrix. */
    protected static final int DISPLAY_LOW = 2;

    /** Index for display high value in subrangeInfo matrix. */
    protected static final int DISPLAY_HIGH  = 3;

    /** The default lower bound. */
    protected static final double DEFAULT_LOWER_BOUND = 0.0;

    /** The default upper bound. */
    protected static final double DEFAULT_UPPER_BOUND = 100.0;

    /** The dataset for the plot. */
    private ValueDataset dataset;

    /** The range axis. */
    private ValueAxis rangeAxis;

    /** The lower bound for the thermometer. */
    private double lowerBound = DEFAULT_LOWER_BOUND;

    /** The upper bound for the thermometer. */
    private double upperBound = DEFAULT_UPPER_BOUND;

    /** Blank space inside the plot area around the outside of the thermometer. */
    private Spacer padding;

    /** Stroke for drawing the thermometer */
    private transient Stroke thermometerStroke = new BasicStroke(1.0f);

    /** Paint for drawing the thermometer */
    private transient Paint thermometerPaint = Color.black;

    /** The display units */
    private int units = UNITS_CELCIUS;

    /** The value label position. */
    private int valueLocation = BULB;

    /** The font to write the value in */
    private Font valueFont = new Font("SansSerif", Font.BOLD, 16);

    /** Colour that the value is written in */
    private transient Paint valuePaint = Color.white;

    /** Number format for the value */
    private NumberFormat valueFormat = new DecimalFormat();

    /** The default paint for the mercury in the thermometer. */
    private transient Paint mercuryPaint = Color.lightGray;

    /** A flag that controls whether value lines are drawn. */
    private boolean showValueLines = false;

    /** The display sub-range. */
    private int subrange = -1;

    /** The start and end values for the subranges. */
    private double[][] subrangeInfo = {
        {0.0,  50.0,  0.0,  50.0},
        {50.0,  75.0, 50.0,  75.0},
        {75.0, 100.0, 75.0, 100.0}
    };

    /** A flag that controls whether or not the axis range adjusts to the sub-ranges. */
    private boolean followDataInSubranges = false;

    /** A flag that controls whether or not the mercury paint changes with the subranges. */
    private boolean useSubrangePaint = true;

    /** Paint for each range */
    private Paint[] subrangePaint = {
        Color.green,
        Color.orange,
        Color.red
    };

    /** A flag that controls whether the sub-range indicators are visible. */
    private boolean subrangeIndicatorsVisible = true;

    /** The stroke for the sub-range indicators. */
    private transient Stroke subrangeIndicatorStroke = new BasicStroke(2.0f);

    /** The range indicator stroke. */
    private transient Stroke rangeIndicatorStroke = new BasicStroke(3.0f);

    /** The resourceBundle for the localization. */
    static protected ResourceBundle localizationResources =
                            ResourceBundle.getBundle("org.jfree.chart.plot.LocalizationBundle");

    /**
     * Creates a new thermometer plot.
     */
    public ThermometerPlot() {
        this(new DefaultValueDataset());
    }

    /**
     * Creates a new thermometer plot, using default attributes where necessary.
     *
     * @param dataset  the data set.
     */
    public ThermometerPlot(ValueDataset dataset) {

        super();

        this.padding = new Spacer(Spacer.RELATIVE, 0.05, 0.05, 0.05, 0.05);
        this.dataset = dataset;
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        NumberAxis axis = new NumberAxis(null);
        axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        setRangeAxis(axis);
        setAxisRange();
    }

    /**
     * Returns the primary dataset for the plot.
     *
     * @return The primary dataset (possibly <code>null</code>).
     */
    public ValueDataset getDataset() {
        return this.dataset;
    }

    /**
     * Sets the dataset for the plot, replacing the existing dataset if there is one.
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     */
    public void setDataset(ValueDataset dataset) {

        // if there is an existing dataset, remove the plot from the list of change listeners...
        ValueDataset existing = this.dataset;
        if (existing != null) {
            existing.removeChangeListener(this);
        }

        // set the new dataset, and register the chart as a change listener...
        this.dataset = dataset;
        if (dataset != null) {
            setDatasetGroup(dataset.getGroup());
            dataset.addChangeListener(this);
        }

        // send a dataset change event to self...
        DatasetChangeEvent event = new DatasetChangeEvent(this, dataset);
        datasetChanged(event);

    }

    /**
     * Returns the dataset cast to {@link ValueDataset} (provided for convenience).
     *
     * @return  the dataset for the plot, cast as a {@link ValueDataset}.
     *
     * @deprecated Use getDataset() instead.
     */
    public ValueDataset getData() {
        return dataset;
    }

    /**
     * Sets the data for the chart, replacing any existing data.
     * <P>
     * Registered listeners are notified that the plot has been modified (this will normally
     * trigger a chart redraw).
     *
     * @param dataset  the new dataset.
     *
     * @deprecated Use setDataset(...) instead.
     */
    public void setData(ValueDataset dataset) {

        // if there is an existing dataset, remove the chart from the list of
        // change listeners...
        ValueDataset existing = this.dataset;
        if (existing != null) {
            existing.removeChangeListener(this);
        }

        // set the new dataset, and register the plot as a change listener...
        this.dataset = dataset;
        if (this.dataset != null) {
            dataset.addChangeListener(this);
        }

        // notify plot change listeners...
        PlotChangeEvent event = new PlotChangeEvent(this);
        notifyListeners(event);

    }

    /**
     * Returns the range axis.
     *
     * @return the range axis.
     */
    public ValueAxis getRangeAxis() {
        return this.rangeAxis;
    }

    /**
     * Sets the range axis for the plot.
     * <P>
     * An exception is thrown if the new axis and the plot are not mutually compatible.
     *
     * @param axis  the new axis.
     */
    public void setRangeAxis(ValueAxis axis) {

        if (axis != null) {
            axis.setPlot(this);
            axis.addChangeListener(this);
        }

        // plot is likely registered as a listener with the existing axis...
        if (this.rangeAxis != null) {
            this.rangeAxis.removeChangeListener(this);
        }

        this.rangeAxis = axis;

    }

    /**
     * Returns the lower bound for the thermometer.
     * <p>
     * The data value can be set lower than this, but it will not be shown in the thermometer.
     *
     * @return the lower bound.
     *
     */
    public double getLowerBound() {
        return this.lowerBound;
    }

    /**
     * Sets the lower bound for the thermometer.
     *
     * @param lower the lower bound.
     */
    public void setLowerBound(double lower) {
        this.lowerBound = lower;
        setAxisRange();
    }

    /**
     * Returns the upper bound for the thermometer.
     * <p>
     * The data value can be set higher than this, but it will not be shown in the thermometer.
     *
     * @return the upper bound.
     *
     */
    public double getUpperBound() {
        return this.upperBound;
    }

    /**
     * Sets the upper bound for the thermometer.
     *
     * @param upper the upper bound.
     */
    public void setUpperBound(double upper) {
        this.upperBound = upper;
        setAxisRange();
    }

    /**
     * Sets the lower and upper bounds for the thermometer.
     *
     * @param lower  the lower bound.
     * @param upper  the upper bound.
     */
    public void setRange(double lower, double upper) {
        this.lowerBound = lower;
        this.upperBound = upper;
        setAxisRange();
    }

    /**
     * Returns the padding for the thermometer.  This is the space inside the plot area.
     *
     * @return the padding.
     */
    public Spacer getPadding() {
        return this.padding;
    }

    /**
     * Sets the padding for the thermometer.
     *
     * @param padding  the padding.
     */
    public void setPadding(Spacer padding) {
        this.padding = padding;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the stroke used to draw the thermometer outline.
     *
     * @return the stroke.
     */
    public Stroke getThermometerStroke() {
        return this.thermometerStroke;
    }

    /**
     * Sets the stroke used to draw the thermometer outline.
     *
     * @param s  the new stroke (null ignored).
     */
    public void setThermometerStroke(Stroke s) {
        if (s != null) {
            this.thermometerStroke = s;
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Returns the paint used to draw the thermometer outline.
     *
     * @return the paint.
     */
    public Paint getThermometerPaint() {
        return this.thermometerPaint;
    }

    /**
     * Sets the paint used to draw the thermometer outline.
     *
     * @param paint  the new paint (null ignored).
     */
    public void setThermometerPaint(Paint paint) {
        if (paint != null) {
            this.thermometerPaint = paint;
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Returns the unit display type (none/Fahrenheit/Celcius/Kelvin).
     *
     * @return  the units type.
     */
    public int getUnits() {
        return units;
    }

    /**
     * Sets the units to be displayed in the thermometer.
     * <p>
     * Use one of the following constants:
     *
     * <ul>
     * <li>UNITS_NONE : no units displayed.</li>
     * <li>UNITS_FAHRENHEIT : units displayed in Fahrenheit.</li>
     * <li>UNITS_CELCIUS : units displayed in Celcius.</li>
     * <li>UNITS_KELVIN : units displayed in Kelvin.</li>
     * </ul>
     *
     * @param u  the new unit type.
     */
    public void setUnits(int u) {
        if ((u >= 0) && (u < UNITS.length)) {
            if (this.units != u) {
                this.units = u;
                notifyListeners(new PlotChangeEvent(this));
            }
        }
    }

    /**
     * Sets the unit type.
     *
     * @param u  the unit type (null ignored).
     */
    public void setUnits(String u) {
        if (u == null) {
            return;
        }

        u = u.toUpperCase().trim();
        for (int i = 0; i < UNITS.length; ++i) {
            if (u.equals(UNITS[i].toUpperCase().trim())) {
                setUnits(i);
                i = UNITS.length;
            }
        }
    }

    /**
     * Returns the value location.
     *
     * @return the location.
     */
    public int getValueLocation() {
        return this.valueLocation;
    }

    /**
     * Sets the location at which the current value is displayed.
     * <P>
     * The location can be one of the constants:  <code>NONE</code>, <code>RIGHT</code> and
     * <code>BULB</code>.
     *
     * @param location  the location.
     */
    public void setValueLocation(int location) {
        if ((location >= 0) && (location < 3)) {
            valueLocation = location;
            notifyListeners(new PlotChangeEvent(this));
        }
        else {
            throw new IllegalArgumentException(
                "ThermometerPlot.setDisplayLocation: location not recognised.");
        }
    }

    /**
     * Gets the font used to display the current value.
     *
     * @return The font.
     */
    public Font getValueFont() {
        return this.valueFont;
    }

    /**
     * Sets the font used to display the current value.
     *
     * @param f  the new font.
     */
    public void setValueFont(Font f) {
        if ((f != null) && (!this.valueFont.equals(f))) {
            this.valueFont = f;
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Gets the paint used to display the current value.
     *
     * @return  the paint.
     */
    public Paint getValuePaint() {
        return this.valuePaint;
    }

    /**
     * Sets the paint used to display the current value.
     *
     * @param p  the new paint.
     */
    public void setValuePaint(Paint p) {
        if ((p != null) && (!this.valuePaint.equals(p))) {
            this.valuePaint = p;
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Sets the formatter for the value label.
     *
     * @param formatter  the new formatter.
     */
    public void setValueFormat(NumberFormat formatter) {
        if (formatter != null) {
            this.valueFormat = formatter;
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Returns the default mercury paint.
     *
     * @return the paint.
     */
    public Paint getMercuryPaint() {
        return this.mercuryPaint;
    }

    /**
     * Sets the default mercury paint.
     *
     * @param paint  the new paint.
     */
    public void setMercuryPaint(Paint paint) {
        this.mercuryPaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the flag that controls whether not value lines are displayed.
     *
     * @return the flag.
     */
    public boolean getShowValueLines() {
        return this.showValueLines;
    }

    /**
     * Sets the display as to whether to show value lines in the output.
     *
     * @param b Whether to show value lines in the thermometer
     */
    public void setShowValueLines(boolean b) {
        this.showValueLines = b;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Sets information for a particular range.
     *
     * @param range  the range to specify information about.
     * @param low  the low value for the range
     * @param hi  the high value for the range
     */
    public void setSubrangeInfo(int range, double low, double hi) {
        setSubrangeInfo(range, low, hi, low, hi);
    }

    /**
     * Sets the subrangeInfo attribute of the ThermometerPlot object
     *
     * @param range  the new rangeInfo value.
     * @param rangeLow  the new rangeInfo value
     * @param rangeHigh  the new rangeInfo value
     * @param displayLow  the new rangeInfo value
     * @param displayHigh  the new rangeInfo value
     */
    public void setSubrangeInfo(int range,
                                double rangeLow, double rangeHigh,
                                double displayLow, double displayHigh) {

        if ((range >= 0) && (range < 3)) {
            setSubrange(range, rangeLow, rangeHigh);
            setDisplayRange(range, displayLow, displayHigh);
            setAxisRange();
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Sets the range.
     *
     * @param range  the range type.
     * @param low  the low value.
     * @param high  the high value.
     */
    public void setSubrange(int range, double low, double high) {
        if ((range >= 0) && (range < 3)) {
            subrangeInfo[range][RANGE_HIGH] = high;
            subrangeInfo[range][RANGE_LOW] = low;
        }
    }

    /**
     * Sets the display range.
     *
     * @param range  the range type.
     * @param low  the low value.
     * @param high  the high value.
     */
    public void setDisplayRange(int range, double low, double high) {

        if ((range >= 0) && (range < subrangeInfo.length)
            && isValidNumber(high) && isValidNumber(low)) {

            if (high > low) {
                subrangeInfo[range][DISPLAY_HIGH] = high;
                subrangeInfo[range][DISPLAY_LOW] = low;
            }
            else {
                subrangeInfo[range][DISPLAY_HIGH] = high;
                subrangeInfo[range][DISPLAY_LOW] = low;
            }

        }

    }

    /**
     * Gets the paint used for a particular subrange.
     *
     * @param range  the range.
     *
     * @return the paint.
     */
    public Paint getSubrangePaint(int range) {

        if ((range >= 0) && (range < subrangePaint.length)) {
            return subrangePaint[range];
        }
        else {
            return this.mercuryPaint;
        }

    }

    /**
     * Sets the paint to be used for a range.
     *
     * @param range  the range.
     * @param paint  the paint to be applied.
     */
    public void setSubrangePaint(int range, Paint paint) {
        if ((range >= 0) && (range < subrangePaint.length) && (paint != null)) {
            subrangePaint[range] = paint;
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Returns a flag that controls whether or not the thermometer axis zooms to display the
     * subrange within which the data value falls.
     *
     * @return the flag.
     */
    public boolean getFollowDataInSubranges() {
        return this.followDataInSubranges;
    }

    /**
     * Sets the flag that controls whether or not the thermometer axis zooms to display the
     * subrange within which the data value falls.
     *
     * @param flag  the flag.
     */
    public void setFollowDataInSubranges(boolean flag) {
        this.followDataInSubranges = flag;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns a flag that controls whether or not the mercury color changes for each
     * subrange.
     *
     * @return the flag.
     */
    public boolean getUseSubrangePaint() {
        return this.useSubrangePaint;
    }

    /**
     * Sets the range colour change option.
     *
     * @param flag The new range colour change option
     */
    public void setUseSubrangePaint(boolean flag) {
        this.useSubrangePaint = flag;
        notifyListeners(new PlotChangeEvent(this));
    }


    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the plot should be drawn.
     * @param parentState  the state from the parent plot, if there is one.
     * @param info  collects info about the drawing.
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, PlotState parentState,
                     PlotRenderingInfo info) {

        RoundRectangle2D outerStem = new RoundRectangle2D.Double();
        RoundRectangle2D innerStem = new RoundRectangle2D.Double();
        RoundRectangle2D mercuryStem = new RoundRectangle2D.Double();
        Ellipse2D outerBulb = new Ellipse2D.Double();
        Ellipse2D innerBulb = new Ellipse2D.Double();
        String temp = null;
        FontMetrics metrics = null;
        if (info != null) {
            info.setPlotArea(plotArea);
        }

        // adjust for insets...
        Insets insets = getInsets();
        if (insets != null) {
            plotArea.setRect(plotArea.getX() + insets.left,
            plotArea.getY() + insets.top,
            plotArea.getWidth() - insets.left - insets.right,
            plotArea.getHeight() - insets.top - insets.bottom);
        }
        drawBackground(g2, plotArea);

        // adjust for padding...
        //this.padding.trim(plotArea);
        int midX = (int) (plotArea.getX() + (plotArea.getWidth() / 2));
        int midY = (int) (plotArea.getY() + (plotArea.getHeight() / 2));
        int stemTop = (int) (plotArea.getMinY() + BULB_RADIUS);
        int stemBottom  = (int) (plotArea.getMaxY() - BULB_DIAMETER);
        Rectangle2D dataArea = new Rectangle2D.Double(midX - COLUMN_RADIUS,
                                                      stemTop,
                                                      COLUMN_RADIUS,
                                                      stemBottom - stemTop);

        outerBulb.setFrame(midX - BULB_RADIUS,
                           stemBottom,
                           BULB_DIAMETER,
                           BULB_DIAMETER);

        outerStem.setRoundRect(midX - COLUMN_RADIUS,
                               plotArea.getMinY(),
                               COLUMN_DIAMETER,
                               stemBottom + BULB_DIAMETER - stemTop,
                               COLUMN_DIAMETER,
                               COLUMN_DIAMETER);

        Area outerThermometer = new Area(outerBulb);
        Area tempArea = new Area(outerStem);
        outerThermometer.add(tempArea);

        innerBulb.setFrame(midX - BULB_RADIUS + GAP_RADIUS,
                           stemBottom + GAP_RADIUS,
                           BULB_DIAMETER - GAP_DIAMETER,
                           BULB_DIAMETER - GAP_DIAMETER);

        innerStem.setRoundRect(midX - COLUMN_RADIUS + GAP_RADIUS,
                               plotArea.getMinY()  + GAP_RADIUS,
                               COLUMN_DIAMETER - GAP_DIAMETER,
                               stemBottom + BULB_DIAMETER - GAP_DIAMETER - stemTop,
                               COLUMN_DIAMETER - GAP_DIAMETER,
                               COLUMN_DIAMETER - GAP_DIAMETER);

        Area innerThermometer = new Area(innerBulb);
        tempArea = new Area(innerStem);
        innerThermometer.add(tempArea);

        if ((this.dataset != null) && (this.dataset.getValue() != null)) {
            double current = this.dataset.getValue().doubleValue();
            double ds = rangeAxis.translateValueToJava2D(current, dataArea, RectangleEdge.LEFT);

            int i = COLUMN_DIAMETER - GAP_DIAMETER;  // already calculated
            int j = COLUMN_RADIUS - GAP_RADIUS;      // already calculated
            int l = (i / 2);
            int k = (int) Math.round(ds);
            if (k < (GAP_RADIUS + plotArea.getMinY())) {
                k = (int) (GAP_RADIUS + plotArea.getMinY());
                l = BULB_RADIUS;
            }

            Area mercury = new Area(innerBulb);

            if (k < (stemBottom + BULB_RADIUS)) {
                mercuryStem.setRoundRect(midX - j, k, i, (stemBottom + BULB_RADIUS) - k, l, l);
                tempArea = new Area(mercuryStem);
                mercury.add(tempArea);
            }

            g2.setPaint(getCurrentPaint());
            g2.fill(mercury);

            // draw the axis...
            int drawWidth = AXIS_GAP;
            if (showValueLines) {
                drawWidth += COLUMN_DIAMETER;
            }

            Rectangle2D drawArea = new Rectangle2D.Double(midX - COLUMN_RADIUS - AXIS_GAP,
                                                          stemTop,
                                                          drawWidth,
                                                          (stemBottom - stemTop + 1));

            /** @todo Fix to correctly support axis to the left or right */
            double cursor = midX - COLUMN_RADIUS - AXIS_GAP;
            AxisState state = rangeAxis.draw(g2, cursor, plotArea, drawArea, RectangleEdge.LEFT);
            cursor = state.getCursor();
            // draw range indicators...
            if (this.subrangeIndicatorsVisible) {
                g2.setStroke(this.subrangeIndicatorStroke);
                Range range = rangeAxis.getRange();

                // draw start of normal range
                double value = this.subrangeInfo[NORMAL][RANGE_LOW];
                if (range.contains(value)) {
                    double x = midX + COLUMN_RADIUS + 2;
                    double y = rangeAxis.translateValueToJava2D(value, dataArea,
                                                                RectangleEdge.LEFT);
                    Line2D line = new Line2D.Double(x, y, x + 10, y);
                    g2.setPaint(subrangePaint[NORMAL]);
                    g2.draw(line);
                }

                // draw start of warning range
                value = this.subrangeInfo[WARNING][RANGE_LOW];
                if (range.contains(value)) {
                    double x = midX + COLUMN_RADIUS + 2;
                    double y = rangeAxis.translateValueToJava2D(value, dataArea,
                                                                RectangleEdge.LEFT);
                    Line2D line = new Line2D.Double(x, y, x + 10, y);
                    g2.setPaint(subrangePaint[WARNING]);
                    g2.draw(line);
                }

                // draw start of critical range
                value = this.subrangeInfo[CRITICAL][RANGE_LOW];
                if (range.contains(value)) {
                    double x = midX + COLUMN_RADIUS + 2;
                    double y = rangeAxis.translateValueToJava2D(value, dataArea,
                                                                RectangleEdge.LEFT);
                    Line2D line = new Line2D.Double(x, y, x + 10, y);
                    g2.setPaint(subrangePaint[CRITICAL]);
                    g2.draw(line);
                }
            }

            // draw text value on screen
            g2.setFont(this.valueFont);
            g2.setPaint(this.valuePaint);
            metrics = g2.getFontMetrics();
            switch (valueLocation) {
                case RIGHT:
                    g2.drawString(valueFormat.format(current),
                                  midX + COLUMN_RADIUS + GAP_RADIUS, midY);
                    break;
                case BULB:
                    temp = valueFormat.format(current);
                    i = metrics.stringWidth(temp) / 2;
                    g2.drawString(temp, midX - i, stemBottom + BULB_RADIUS + GAP_RADIUS);
                    break;
                default:
            }
          /***/
        }

        g2.setPaint(thermometerPaint);
        g2.setFont(valueFont);

        //  draw units indicator
        metrics = g2.getFontMetrics();
        int tickX1 = midX - COLUMN_RADIUS - GAP_DIAMETER - metrics.stringWidth(UNITS[units]);
        if (tickX1 > plotArea.getMinX()) {
            g2.drawString(UNITS[units], tickX1, (int) (plotArea.getMinY() + 20));
        }

        // draw thermometer outline
        g2.setStroke(thermometerStroke);
        g2.draw(outerThermometer);
        g2.draw(innerThermometer);

        drawOutline(g2, plotArea);
    }

    /**
     * A zoom method that does nothing.
     * <p>
     * Plots are required to support the zoom operation.  In the case of a
     * thermometer chart, it doesn't make sense to zoom in or out, so the
     * method is empty.
     *
     * @param percent  the zoom percentage.
     */
    public void zoom(double percent) { }

    /**
     * Returns a short string describing the type of plot.
     *
     * @return  a short string describing the type of plot.
     */
    public String getPlotType() {
        return localizationResources.getString("Thermometer_Plot");
    }

    /**
     * Checks to see if a new value means the axis range needs adjusting.
     *
     * @param event  the dataset change event.
     */
    public void datasetChanged(DatasetChangeEvent event) {

        Number vn = this.dataset.getValue();
        if (vn != null) {
            double value = vn.doubleValue();
            if (inSubrange(NORMAL, value)) {
                this.subrange = NORMAL;
            }
            else if (inSubrange(WARNING, value)) {
                this.subrange = WARNING;
            }
            else if (inSubrange(CRITICAL, value)) {
                this.subrange = CRITICAL;
            }
            else {
                this.subrange = -1;
            }
            setAxisRange();
        }
        super.datasetChanged(event);
    }

    /**
     * Returns the minimum value in either the domain or the range, whichever
     * is displayed against the vertical axis for the particular type of plot
     * implementing this interface.
     *
     * @return the minimum value in either the domain or the range.
     */
    public Number getMinimumVerticalDataValue() {
        return new Double(this.lowerBound);
    }

    /**
     * Returns the maximum value in either the domain or the range, whichever
     * is displayed against the vertical axis for the particular type of plot
     * implementing this interface.
     *
     * @return the maximum value in either the domain or the range
     */
    public Number getMaximumVerticalDataValue() {
        return new Double(this.upperBound);
    }

    /**
     * Returns the data range.
     *
     * @param axis  the axis.
     *
     * @return The range of data displayed.
     */
    public Range getDataRange(ValueAxis axis) {
        return new Range(this.lowerBound, this.upperBound);
    }

    /**
     * Sets the axis range to the current values in the rangeInfo array.
     */
    protected void setAxisRange() {
        if ((this.subrange >= 0) && (this.followDataInSubranges)) {
            rangeAxis.setRange(new Range(subrangeInfo[subrange][DISPLAY_LOW],
                                         subrangeInfo[subrange][DISPLAY_HIGH]));
        }
        else {
            rangeAxis.setRange(this.lowerBound, this.upperBound);
        }
    }

    /**
     * Returns null, since the thermometer plot won't require a legend.
     *
     * @return null.
     *
     * @deprecated use getLegendItems().
     */
    public List getLegendItemLabels() {
        return null;
    }

    /**
     * Returns the legend items for the plot.
     *
     * @return null.
     */
    public LegendItemCollection getLegendItems() {
        return null;
    }

    /**
     * Returns the vertical value axis.
     * <p>
     * This is required by the VerticalValuePlot interface, but not used in this class.
     *
     * @return the vertical value axis.
     */
    public ValueAxis getVerticalValueAxis() {
        return this.rangeAxis;
    }

    /**
     * Determine whether a number is valid and finite.
     *
     * @param d  the number to be tested.
     *
     * @return true if the number is valid and finite, and false otherwise.
     */
    protected static boolean isValidNumber(double d) {
        return (!(Double.isNaN(d) || Double.isInfinite(d)));
    }

    /**
     * Returns true if the value is in the specified range, and false otherwise.
     *
     * @param subrange  the subrange.
     * @param value  the value to check.
     *
     * @return true or false.
     */
    private boolean inSubrange(int subrange, double value) {
        return (value > subrangeInfo[subrange][RANGE_LOW]
                && value <= subrangeInfo[subrange][RANGE_HIGH]);
    }

    /**
     * Returns the mercury paint corresponding to the current data value.
     *
     * @return the paint.
     */
    private Paint getCurrentPaint() {

        Paint result = this.mercuryPaint;
        if (this.useSubrangePaint) {
            double value = this.dataset.getValue().doubleValue();
            if (inSubrange(NORMAL, value)) {
                result = this.subrangePaint[NORMAL];
            }
            else if (inSubrange(WARNING, value)) {
                result = this.subrangePaint[WARNING];
            }
            else if (inSubrange(CRITICAL, value)) {
                result = this.subrangePaint[CRITICAL];
            }
        }
        return result;
    }

    /**
     * Tests this plot for equality with another object.
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

        if (obj instanceof ThermometerPlot) {
            ThermometerPlot p = (ThermometerPlot) obj;
            if (super.equals(obj)) {
                boolean b0 = ObjectUtils.equal(this.dataset, p.dataset);
                boolean b1 = ObjectUtils.equal(this.rangeAxis, p.rangeAxis);
                boolean b2 = (this.lowerBound == p.lowerBound);
                boolean b3 = (this.upperBound == p.upperBound);
                boolean b4 = ObjectUtils.equal(this.padding, p.padding);
                boolean b5 = ObjectUtils.equal(this.thermometerStroke, p.thermometerStroke);
                boolean b6 = ObjectUtils.equal(this.thermometerPaint, p.thermometerPaint);
                boolean b7 = (this.units == p.units);
                boolean b8 = (this.valueLocation == p.valueLocation);
                boolean b9 = ObjectUtils.equal(this.valueFont, p.valueFont);
                boolean b10 = ObjectUtils.equal(this.valuePaint, p.valuePaint);
                boolean b11 = ObjectUtils.equal(this.valueFormat, p.valueFormat);
                boolean b12 = ObjectUtils.equal(this.mercuryPaint, p.mercuryPaint);
                boolean b13 = (this.showValueLines == p.showValueLines);
                boolean b14 = (this.subrange == p.subrange);
                boolean b15 = true; //Arrays.equals(this.subRangeInfo, p.subRangeInfo);
                boolean b16 = (this.followDataInSubranges == p.followDataInSubranges);
                boolean b17 = (this.useSubrangePaint == p.useSubrangePaint);
                return b0 && b1 && b2 && b3 && b4 && b5 && b6 && b7 && b8 && b9
                       && b10 && b11 && b12 && b13 && b14 && b15 && b16 && b17;

            }
        }

        return false;

    }

    /**
    * Returns a clone of the plot.
    *
    * @return A clone.
    *
    * @throws CloneNotSupportedException  if the plot cannot be cloned.
    */
   public Object clone() throws CloneNotSupportedException {

       ThermometerPlot clone = (ThermometerPlot) super.clone();

       //private ValueDataset dataset <-- don't clone the dataset
       if (clone.dataset != null) {
           clone.dataset.addChangeListener(clone);
       }
       clone.rangeAxis = (ValueAxis) ObjectUtils.clone(this.rangeAxis);
       if (clone.rangeAxis != null) {
           clone.rangeAxis.setPlot(clone);
           clone.rangeAxis.addChangeListener(clone);
       }
       //private double lowerBound <-- primitive
       //private double upperBound <-- primitive
       //private Spacer padding <-- immutable
       //private transient Stroke thermometerStroke <-- immutable
       //private transient Paint thermometerPaint <-- immutable
       //private int units <-- primitive
       //private int valueLocation <-- primitive
       //private Font valueFont <-- immutable
       //private transient Paint valuePaint<-- immutable
       clone.valueFormat = (NumberFormat) this.valueFormat.clone();
       //private transient Paint mercuryPaint <-- immutable
       //private boolean showValueLines <-- primitive
       //private int subrange <-- primitive
       //private double[][] subrangeInfo ????????????????
       //private boolean followDataInSubranges <-- primitive
       //private boolean useSubrangePaint <-- primitive
       clone.subrangePaint = (Paint[]) this.subrangePaint.clone();
       //private boolean subrangeIndicatorsVisible <-- primitive
       //private transient Stroke subrangeIndicatorStroke <-- immutable
       //private transient Stroke rangeIndicatorStroke <-- immutable

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
        SerialUtilities.writeStroke(this.thermometerStroke, stream);
        SerialUtilities.writePaint(this.thermometerPaint, stream);
        SerialUtilities.writePaint(this.valuePaint, stream);
        SerialUtilities.writePaint(this.mercuryPaint, stream);
        SerialUtilities.writeStroke(this.subrangeIndicatorStroke, stream);
        SerialUtilities.writeStroke(this.rangeIndicatorStroke, stream);
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
        this.thermometerStroke = SerialUtilities.readStroke(stream);
        this.thermometerPaint = SerialUtilities.readPaint(stream);
        this.valuePaint = SerialUtilities.readPaint(stream);
        this.mercuryPaint = SerialUtilities.readPaint(stream);
        this.subrangeIndicatorStroke = SerialUtilities.readStroke(stream);
        this.rangeIndicatorStroke = SerialUtilities.readStroke(stream);

        if (this.rangeAxis != null) {
            this.rangeAxis.addChangeListener(this);
        }

    }

    /**
     * Multiplies the range on the horizontal axis/axes by the specified factor.
     *
     * @param factor  the zoom factor.
     */
    public void zoomHorizontalAxes(double factor) {
        // do nothing
    }

    /**
     * Multiplies the range on the vertical axis/axes by the specified factor.
     *
     * @param factor  the zoom factor.
     */
    public void zoomVerticalAxes(double factor) {
        // zoom the range axis
    }

    /**
     * Zooms the horizontal axes.
     *
     * @param lowerPercent  the lower percent.
     * @param upperPercent  the upper percent.
     */
    public void zoomHorizontalAxes(double lowerPercent, double upperPercent) {
        // zoom the domain axis
    }

    /**
     * Zooms the vertical axes.
     *
     * @param lowerPercent  the lower percent.
     * @param upperPercent  the upper percent.
     */
    public void zoomVerticalAxes(double lowerPercent, double upperPercent) {
        // zoom the domain axis
    }


}

