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
 * ------------
 * PiePlot.java
 * ------------
 * (C) Copyright 2000-2003, by Andrzej Porebski and Contributors.
 *
 * Original Author:  Andrzej Porebski;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *                   Martin Cordova (percentages in labels);
 *                   Richard Atkinson (URL support for image maps);
 *                   Christian W. Zuckschwerdt;
 *
 * $Id: PiePlot.java,v 1.1 2007/10/10 19:09:07 vauchers Exp $
 *
 * Changes (from 21-Jun-2001)
 * --------------------------
 * 21-Jun-2001 : Removed redundant JFreeChart parameter from constructors (DG);
 * 18-Sep-2001 : Updated header (DG);
 * 15-Oct-2001 : Data source classes moved to com.jrefinery.data.* (DG);
 * 19-Oct-2001 : Moved series paint and stroke methods from JFreeChart.java to Plot.java (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 13-Nov-2001 : Modified plot subclasses so that null axes are possible for pie plot (DG);
 * 17-Nov-2001 : Added PieDataset interface and amended this class accordingly, and completed
 *               removal of BlankAxis class as it is no longer required (DG);
 * 19-Nov-2001 : Changed 'drawCircle' property to 'circular' property (DG);
 * 21-Nov-2001 : Added options for exploding pie sections and filled out range of properties (DG);
 *               Added option for percentages in chart labels, based on code
 *               by Martin Cordova (DG);
 * 30-Nov-2001 : Changed default font from "Arial" --> "SansSerif" (DG);
 * 12-Dec-2001 : Removed unnecessary 'throws' clause in constructor (DG);
 * 13-Dec-2001 : Added tooltips (DG);
 * 16-Jan-2002 : Renamed tooltips class (DG);
 * 22-Jan-2002 : Fixed bug correlating legend labels with pie data (DG);
 * 05-Feb-2002 : Added alpha-transparency to plot class, and updated constructors accordingly (DG);
 * 06-Feb-2002 : Added optional background image and alpha-transparency to Plot and subclasses.
 *               Clipped drawing within plot area (DG);
 * 26-Mar-2002 : Added an empty zoom method (DG);
 * 18-Apr-2002 : PieDataset is no longer sorted (oldman);
 * 23-Apr-2002 : Moved dataset from JFreeChart to Plot.  Added getLegendItemLabels() method (DG);
 * 19-Jun-2002 : Added attributes to control starting angle and direction (default is now
 *               clockwise) (DG);
 * 25-Jun-2002 : Removed redundant imports (DG);
 * 02-Jul-2002 : Fixed sign of percentage bug introduced in 0.9.2 (DG);
 * 16-Jul-2002 : Added check for null dataset in getLegendItemLabels(...) (DG);
 * 30-Jul-2002 : Moved summation code to DatasetUtilities (DG);
 * 05-Aug-2002 : Added URL support for image maps - new member variable for
 *               urlGenerator, modified constructor and minor change to the draw method (RA);
 * 18-Sep-2002 : Modified the percent label creation and added setters for the formatters (AS);
 * 24-Sep-2002 : Added getLegendItems() method (DG);
 * 02-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 09-Oct-2002 : Added check for null entity collection (DG);
 * 30-Oct-2002 : Changed PieDataset interface (DG);
 * 18-Nov-2002 : Changed CategoryDataset to TableDataset (DG);
 * 02-Jan-2003 : Fixed "no data" message (DG);
 * 23-Jan-2003 : Modified to extract data from rows OR columns in CategoryDataset (DG);
 * 14-Feb-2003 : Fixed label drawing so that foreground alpha does not apply (bug id 685536) (DG);
 * 07-Mar-2003 : Modified to pass pieIndex on to PieSectionEntity and tooltip and
 *               URL generators (DG);
 * 21-Mar-2003 : Added a minimum angle for drawing arcs (see bug id 620031) (DG);
 * 24-Apr-2003 : Switched around PieDataset and KeyedValuesDataset (DG);
 * 02-Jun-2003 : Fixed bug 721733 (DG);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 
 */

package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.labels.PieToolTipGenerator;
import org.jfree.chart.urls.PieURLGenerator;
import org.jfree.data.CategoryDataset;
import org.jfree.data.CategoryToPieDataset;
import org.jfree.data.Dataset;
import org.jfree.data.DatasetChangeEvent;
import org.jfree.data.DatasetUtilities;
import org.jfree.data.PieDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.util.ObjectUtils;
import org.jfree.util.PaintTable;
import org.jfree.util.StrokeTable;

/**
 * A plot that displays data in the form of a pie chart, using data from any class that implements
 * the {@link PieDataset} interface.
 * <P>
 * Special notes:
 * <ol>
 * <li>the default starting point is 12 o'clock and the pie sections proceed
 * in a clockwise direction, but these settings can be changed</li>
 * <li>negative values in the dataset are ignored</li>
 * <li>there are utility methods for creating a {@link PieDataset} from a
 * {@link CategoryDataset}</li>
 * </ol>
 *
 * @see Plot
 * @see PieDataset
 *
 * @author Andrzej Porebski
 */
public class PiePlot extends Plot implements Serializable {

    /** A constant indicating the clockwise direction. */
    public static final int CLOCKWISE = -1;

    /** A constant representing the anti-clockwise direction. */
    public static final int ANTICLOCKWISE = 1;

    /** Constant indicating no labels on the pie sections. */
    public static final int NO_LABELS = 0;

    /** Constant indicating name labels on the pie sections. */
    public static final int NAME_LABELS = 1;

    /** Constant indicating value labels on the pie sections. */
    public static final int VALUE_LABELS = 2;

    /** Constant indicating percent labels on the pie sections. */
    public static final int PERCENT_LABELS = 3;

    /** Constant indicating percent labels on the pie sections. */
    public static final int NAME_AND_VALUE_LABELS = 4;

    /** Constant indicating percent labels on the pie sections. */
    public static final int NAME_AND_PERCENT_LABELS = 5;

    /** Constant indicating percent labels on the pie sections. */
    public static final int VALUE_AND_PERCENT_LABELS = 6;

    /** A useful constant for multiple pie charts. */
    public static final int PER_ROW = 0;

    /** A useful constant for multiple pie charts. */
    public static final int PER_COLUMN = 1;

    /** The default interior gap. */
    public static final double DEFAULT_INTERIOR_GAP = 0.25;

    /** The maximum interior gap (currently 40%). */
    public static final double MAX_INTERIOR_GAP = 0.40;

    /** The default radius percent (currently 100%). */
    public static final double DEFAULT_RADIUS = 1.00;

    /** The maximum radius (currently 100%). */
    public static final double MAX_RADIUS = 1.00;

    /** The default starting angle for the pie chart. */
    public static final double DEFAULT_START_ANGLE = 90.0;

    /** The default direction for the pie chart. */
    public static final int DEFAULT_DIRECTION = CLOCKWISE;

    /** The default section label type. */
    public static final int DEFAULT_SECTION_LABEL_TYPE = NAME_LABELS;

    /** The default section label font. */
    public static final Font DEFAULT_SECTION_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 10);

    /** The default section label paint. */
    public static final Paint DEFAULT_SECTION_LABEL_PAINT = Color.black;

    /** The default section label gap (currently 10%). */
    public static final double DEFAULT_SECTION_LABEL_GAP = 0.10;

    /** The maximum interior gap (currently 30%). */
    public static final double MAX_SECTION_LABEL_GAP = 0.30;

    /** The default value label formatter. */
    public static final NumberFormat DEFAULT_VALUE_FORMATTER = NumberFormat.getNumberInstance();

    /** The default percent label formatter. */
    public static final NumberFormat DEFAULT_PERCENT_FORMATTER = NumberFormat.getPercentInstance();

    /** The default series label font. */
    public static final Font DEFAULT_SERIES_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 12);

    /** The default series label paint. */
    public static final Paint DEFAULT_SERIES_LABEL_PAINT = Color.black;

    /** The default for the show series labels flag */
    public static final boolean DEFAULT_SHOW_SERIES_LABELS = true;

    /** The default minimum arc angle to draw. */
    public static final double DEFAULT_MINIMUM_ARC_ANGLE_TO_DRAW = 0.00001;

    /** The dataset for the pie chart. */
    private PieDataset dataset;
    
    /** The dataset for multi pie charts. */
    private CategoryDataset multiDataset;
    
    /** The amount of space left around the outside of the pie plot, expressed as a percentage. */
    private double interiorGap;

    /** Flag determining whether to draw an ellipse or a perfect circle. */
    private boolean circular;

    /** The radius (as a percentage of the available drawing area). */
    private double radius;

    /** The starting angle. */
    private double startAngle;

    /** The direction for the pie segments. */
    private int direction;

    /** Label type (NO_LABELS, NAME_LABELS, PERCENT_LABELS, NAME_AND_PERCENT_LABELS). */
    private int sectionLabelType;

    /** The font used to display the section labels. */
    private Font sectionLabelFont;

    /** The color used to draw the section labels. */
    private transient Paint sectionLabelPaint;

    /** The gap between the labels and the pie sections, as a percentage of the radius. */
    private double sectionLabelGap;

    /** The percentage amount to explode each pie section. */
    private double[] explodePercentages;

    /** A formatter for displayed values. */
    private NumberFormat valueFormatter;

    /** A formatter for displayed percentages. */
    private NumberFormat percentFormatter;

    /** The tool tip generator. */
    private PieToolTipGenerator toolTipGenerator;

    /** The URL generator. */
    private PieURLGenerator urlGenerator;

    /** Flag used to determine whether series labels are shown */
    private boolean showSeriesLabels = true;

    /** The font used to display the series labels. */
    private Font seriesLabelFont = DEFAULT_SERIES_LABEL_FONT;

    /** The color used to draw the series labels. */
    private transient Paint seriesLabelPaint = DEFAULT_SERIES_LABEL_PAINT;

    /** A flag that controls whether the paint table or the default paint is used. */
    private boolean paintTableActive;

    /** The lookup table for paints for the plot. */
    private PaintTable paintTable;

    /** The default paint (used when the paint table is not active). */
    private transient Paint defaultPaint;

    /** A flag that controls whether the outline paint table or the default paint is used. */
    private boolean outlinePaintTableActive;

    /** The lookup table for outline paints for the plot. */
    private PaintTable outlinePaintTable;

    /** The default outline paint. */
    private transient Paint defaultOutlinePaint;

    /** A flag that controls whether the stroke table or the default stroke is used. */
    private boolean outlineStrokeTableActive;

    /** The lookup table for strokes for the plot. */
    private StrokeTable outlineStrokeTable;

    /** The default stroke. */
    private transient Stroke defaultOutlineStroke;

    /** The data extract type. */
    private int extractType;

    /**
     * The smallest arc angle that will get drawn (this is to avoid a bug in various Java
     * implementations that causes the JVM to crash).
     */
    private double minimumArcAngleToDraw;

    /**
     * Creates a pie chart.
     *
     * @param dataset  the dataset.
     */
    public PiePlot(PieDataset dataset) {

        super();
        this.dataset = dataset;
        this.multiDataset = null;
        initialise();

    }

    /**
     * Creates a new plot that will draw multiple pie charts, one for each row or column (as
     * requested) in the dataset.
     *
     * @param dataset the dataset.
     * @param type  controls how pie data is extracted (PER_ROW or PER_COLUMN).
     */
    public PiePlot(CategoryDataset dataset, int type) {

        super();
        this.dataset = null;
        this.multiDataset = dataset;
        initialise();
        this.extractType = type;

    }

    /**
     * Initialisation shared by constructors.
     */
    private void initialise() {

        this.interiorGap = DEFAULT_INTERIOR_GAP;
        this.circular = true;
        this.radius = DEFAULT_RADIUS;
        this.startAngle = DEFAULT_START_ANGLE;
        this.direction = DEFAULT_DIRECTION;
        this.sectionLabelType = DEFAULT_SECTION_LABEL_TYPE;
        this.sectionLabelFont = DEFAULT_SECTION_LABEL_FONT;
        this.sectionLabelPaint = DEFAULT_SECTION_LABEL_PAINT;
        this.sectionLabelGap = DEFAULT_SECTION_LABEL_GAP;
        this.valueFormatter = DEFAULT_VALUE_FORMATTER;
        this.percentFormatter = DEFAULT_PERCENT_FORMATTER;
        this.toolTipGenerator = null;
        this.urlGenerator = null;

        this.paintTableActive = true;
        this.paintTable = new PaintTable();
        this.defaultPaint = null;
        this.outlinePaintTableActive = false;
        this.outlinePaintTable = null;
        this.defaultOutlinePaint = Color.lightGray;
        this.outlineStrokeTableActive = false;
        this.outlineStrokeTable = null;
        this.defaultOutlineStroke = new BasicStroke(1.0f);

        this.minimumArcAngleToDraw = DEFAULT_MINIMUM_ARC_ANGLE_TO_DRAW;
    }

    /**
     * Returns the dataset.
     * 
     * @return The dataset.
     */
    public PieDataset getDataset() {
        return this.dataset;
    }
    
    /**
     * Sets the dataset.  A {@link DatasetChangeEvent} is sent to all registered listeners.
     * 
     * @param dataset  the dataset.
     */
    public void setDataset(PieDataset dataset) {
        // if there is an existing dataset, remove the plot from the list of change listeners...
        PieDataset existing = this.dataset;
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
     * Returns the dataset for multiple pies.
     * 
     * @return The dataset.
     */
    public CategoryDataset getMultiDataset() {
        return this.multiDataset;
    }
    
    /**
     * Sets the dataset for multiplie pies.  A {@link DatasetChangeEvent} is sent to all 
     * registered listeners.
     * 
     * @param dataset  the dataset.
     */
    public void setMultiDataset(CategoryDataset dataset) {
        // if there is an existing dataset, remove the plot from the list of change listeners...
        CategoryDataset existing = this.multiDataset;
        if (existing != null) {
            existing.removeChangeListener(this);
        }

        // set the new dataset, and register the chart as a change listener...
        this.multiDataset = dataset;
        if (dataset != null) {
            setDatasetGroup(dataset.getGroup());
            dataset.addChangeListener(this);
        }

        // send a dataset change event to self...
        DatasetChangeEvent event = new DatasetChangeEvent(this, dataset);
        datasetChanged(event);
    }
    
    /**
     * Returns the start angle for the first pie section.
     * <P>
     * This is measured in degrees starting from 3 o'clock and measuring anti-clockwise.
     *
     * @return the start angle.
     */
    public double getStartAngle() {
        return this.startAngle;
    }

    /**
     * Sets the starting angle.
     * <P>
     * The initial default value is 90 degrees, which corresponds to 12 o'clock.  A value of zero
     * corresponds to 3 o'clock... this is the encoding used by Java's Arc2D class.
     *
     * @param angle  the angle (in degrees).
     */
    public void setStartAngle(double angle) {
        this.startAngle = angle;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the direction in which the pie sections are drawn (clockwise or anti-clockwise).
     *
     * @return the direction.
     */
    public int getDirection() {
        return this.direction;
    }

    /**
     * Sets the direction (use the constants CLOCKWISE or ANTICLOCKWISE).
     *
     * @param direction  the new direction.
     */
    public void setDirection(int direction) {

        // check argument...
        if ((direction != CLOCKWISE) && (direction != ANTICLOCKWISE)) {
            throw new IllegalArgumentException("PiePlot.setDirection(int): invalid direction.");
        }

        // make the change...
        this.direction = direction;
        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Returns the interior gap, measured as a percentage of the available drawing space.
     *
     * @return The gap percentage.
     */
    public double getInteriorGap() {
        return this.interiorGap;
    }

    /**
     * Sets the interior gap.
     *
     * @param percent The gap.
     */
    public void setInteriorGap(double percent) {

        // check arguments...
        if ((percent < 0.0) || (percent > MAX_INTERIOR_GAP)) {
            throw new IllegalArgumentException(
                "PiePlot.setInteriorGapPercent(double): percentage outside valid range.");
        }

        // make the change...
        if (this.interiorGap != percent) {
            this.interiorGap = percent;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns a flag indicating whether the pie chart is circular, or
     * stretched into an elliptical shape.
     *
     * @return a flag indicating whether the pie chart is circular.
     */
    public boolean isCircular() {
        return circular;
    }

    /**
     * A flag indicating whether the pie chart is circular, or stretched into
     * an elliptical shape.
     *
     * @param flag  the new value.
     */
    public void setCircular(boolean flag) {

        // no argument checking required...
        // make the change...
        if (circular != flag) {
            circular = flag;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Sets the circular attribute, with no side effects.
     *
     * @param circular  the new value of the flag.
     */
    protected void setCircularAttribute(boolean circular) {
        this.circular = circular;
    }

    /**
     * Returns the radius (a percentage of the available space).
     *
     * @return The radius percentage.
     */
    public double getRadius() {
        return this.radius;
    }

    /**
     * Sets the radius.
     *
     * @param percent  the new value.
     */
    public void setRadius(double percent) {

        // check arguments...
        if ((percent <= 0.0) || (percent > MAX_RADIUS)) {
            throw new IllegalArgumentException(
                "PiePlot.setRadiusPercent(double): percentage outside valid range.");
        }

        // make the change (if necessary)...
        if (this.radius != percent) {
            this.radius = percent;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the amount that a section should be 'exploded'.
     *
     * @param section  the section number.
     *
     * @return the amount that a section should be 'exploded'.
     */
    public double getExplodePercent(int section) {

        // check argument...
        if (section < 0) {
            throw new IllegalArgumentException(
                "PiePlot.getExplodePercent(int): section outside valid range.");
        }

        // fetch the result...
        double result = 0.0;

        if (this.explodePercentages != null) {
            if (section < this.explodePercentages.length) {
                result = explodePercentages[section];
            }
        }

        return result;

    }

    /**
     * Sets the amount that a pie section should be exploded.
     * <p>
     * If you want to display a pie chart with one or more exploded sections, you first need
     * to set the pie chart radius to something less than 100%.  Then, consider a circle that
     * represents a pie chart with the maximum radius (100%) and a smaller circle that is the
     * actual pie chart (with radius x%).  Now, the explode percent determines how far out
     * towards the outer circle the pie section is shifted (exploded).
     *
     * @param section  the section index.
     * @param percent  the amount to explode the section as a percentage.
     */
    public void setExplodePercent(int section, double percent) {

        // check argument...
        int keyCount = 0;
        Collection keys = getKeys();
        if (keys != null) {
            keyCount = keys.size();
        }
        if ((section < 0) || (section >= keyCount)) {
            throw new IllegalArgumentException(
                "PiePlot.setExplodePercent(int, double): section outside valid range.");
        }

        // store the value in an appropriate data structure...
        if (this.explodePercentages != null) {
            if (section < this.explodePercentages.length) {
                explodePercentages[section] = percent;
            }
            else {
                double[] newExplodePercentages = new double[section + 1];
                for (int i = 0; i < this.explodePercentages.length; i++) {
                    newExplodePercentages[i] = this.explodePercentages[i];
                }
                this.explodePercentages = newExplodePercentages;
                this.explodePercentages[section] = percent;
            }
        }
        else {
            explodePercentages = new double[keyCount];
            explodePercentages[section] = percent;
        }

    }

    /**
     * Returns the section label type.  Defined by the constants: NO_LABELS,
     * NAME_LABELS, PERCENT_LABELS and NAME_AND_PERCENT_LABELS.
     *
     * @return the section label type.
     */
    public int getSectionLabelType() {
        return this.sectionLabelType;
    }

    /**
     * Sets the section label type.
     * <P>
     * Valid types are defined by the following constants: NO_LABELS,
     * NAME_LABELS, VALUE_LABELS, PERCENT_LABELS, NAME_AND_VALUE_LABELS,
     * NAME_AND_PERCENT_LABELS, VALUE_AND_PERCENT_LABELS.
     *
     * @param type the type.
     */
    public void setSectionLabelType(int type) {

        // check the argument...
        if ((type != NO_LABELS)
            && (type != NAME_LABELS)
            && (type != VALUE_LABELS)
            && (type != PERCENT_LABELS)
            && (type != NAME_AND_VALUE_LABELS)
            && (type != NAME_AND_PERCENT_LABELS)
            && (type != VALUE_AND_PERCENT_LABELS)) {

            throw new IllegalArgumentException(
                "PiePlot.setSectionLabelType(int): unrecognised type.");

        }

        // make the change...
        if (sectionLabelType != type) {
            this.sectionLabelType = type;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the section label font.
     *
     * @return the section label font.
     */
    public Font getSectionLabelFont() {
        return this.sectionLabelFont;
    }

    /**
     * Sets the section label font.
     * <P>
     * Notifies registered listeners that the plot has been changed.
     *
     * @param font  the new section label font.
     */
    public void setSectionLabelFont(Font font) {

        // check arguments...
        if (font == null) {
            throw new IllegalArgumentException(
                "PiePlot.setSectionLabelFont(...): null font not allowed.");
        }

        // make the change...
        if (!this.sectionLabelFont.equals(font)) {
            this.sectionLabelFont = font;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the section label paint.
     *
     * @return the section label paint.
     */
    public Paint getSectionLabelPaint() {
        return this.sectionLabelPaint;
    }

    /**
     * Sets the section label paint.
     * <P>
     * Notifies registered listeners that the plot has been changed.
     *
     * @param paint  the new section label paint.
     */
    public void setSectionLabelPaint(Paint paint) {

        // check arguments...
        if (paint == null) {
            throw new IllegalArgumentException(
                "PiePlot.setSectionLabelPaint(...): null paint not allowed.");
        }

        // make the change...
        if (!this.sectionLabelPaint.equals(paint)) {
            this.sectionLabelPaint = paint;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the section label gap, measured as a percentage of the radius.
     *
     * @return the section label gap, measured as a percentage of the radius.
     */
    public double getSectionLabelGap() {
        return this.sectionLabelGap;
    }

    /**
     * Sets the section label gap percent.
     *
     * @param percent  the gap.
     */
    public void setSectionLabelGap(double percent) {

        // check arguments...
        if ((percent < 0.0) || (percent > MAX_SECTION_LABEL_GAP)) {
            throw new IllegalArgumentException(
                "PiePlot.setSectionLabelGapPercent(double): percentage outside valid range.");
        }

        // make the change...
        if (this.sectionLabelGap != percent) {
            this.sectionLabelGap = percent;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Sets the format string for the value labels.
     *
     * @param format  The format.
     */
    public void setValueFormatString(String format) {
        this.valueFormatter = new DecimalFormat(format);
    }

    /**
     * Sets the format for the value labels.
     *
     * @param format  the format.
     */
    public void setValueFormat(NumberFormat format) {

        if (format == null) {
            return;
        }
        this.valueFormatter = format;

    }

    /**
     * Sets the format string for the percent labels.
     *
     * @param format  the format.
     */
    public void setPercentFormatString(String format) {
        this.percentFormatter = new DecimalFormat(format);
    }

    /**
     * Sets the format for the value labels.
     *
     * @param format  the format.
     */
    public void setPercentFormat(NumberFormat format) {

        if (format == null) {
            return;
        }
        this.percentFormatter = format;

    }

    /**
     * Returns the dataset for the plot, cast as a {@link PieDataset}.
     * <P>
     * Provided for convenience.
     *
     * @return the dataset for the plot, cast as a {@link PieDataset}.
     */
    public PieDataset getPieDataset() {
        return (PieDataset) getDataset();
    }

    /**
     * Returns the show series labels flag.
     *
     * @return the show series label flag.
     */
    public boolean getShowSeriesLabels () {
        return (this.showSeriesLabels);
    }

    /**
     * Sets the show series labels flag.
     * <P>
     * Notifies registered listeners that the plot has been changed.
     *
     * @param flag  the new show series labels flag.
     */
    public void setShowSeriesLabels(boolean flag) {
        if (this.showSeriesLabels != flag) {
            this.showSeriesLabels = flag;
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Returns the series label font.
     *
     * @return the series label font.
     */
    public Font getSeriesLabelFont() {
        return this.seriesLabelFont;
    }

    /**
     * Sets the series label font.
     * <P>
     * Notifies registered listeners that the plot has been changed.
     *
     * @param font the new series label font.
     */
    public void setSeriesLabelFont(Font font) {

        // check arguments...
        if (font == null) {
            throw new IllegalArgumentException("PiePlot.setSeriesLabelFont(...): "
                                               + "null font not allowed.");
        }

        // make the change...
        if (!this.seriesLabelFont.equals(font)) {
            this.seriesLabelFont = font;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the series label paint.
     *
     * @return the series label paint.
     */
    public Paint getSeriesLabelPaint() {
        return this.seriesLabelPaint;
    }

    /**
     * Sets the series label paint.
     * <P>
     * Notifies registered listeners that the plot has been changed.
     *
     * @param paint the new series label paint.
     */
    public void setSeriesLabelPaint(Paint paint) {

        // check arguments...
        if (paint == null) {
            throw new IllegalArgumentException("PiePlot.setSeriesLabelPaint(...): "
                                               + "null paint not allowed.");
        }

        // make the change...
        if (!this.seriesLabelPaint.equals(paint)) {
            this.seriesLabelPaint = paint;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns a collection of the section keys (or categories) in the dataset.
     *
     * @return the categories.
     */
    public Collection getKeys() {

        Collection result = null;

        final Dataset d = getDataset();
        if (d instanceof PieDataset) {
            PieDataset p = (PieDataset) d;
            result = Collections.unmodifiableCollection(p.getKeys());
        }
        else if (d instanceof CategoryDataset) {
            final CategoryDataset c = (CategoryDataset) d;
            if (this.extractType == PiePlot.PER_ROW) {
                result = Collections.unmodifiableCollection(c.getColumnKeys());
            }
            else if (this.extractType == PER_COLUMN) {
                result = Collections.unmodifiableCollection(c.getRowKeys());
            }
        }

        return result;

    }

    /**
     * Returns <code>true</code> if the lookup table for paint objects is being used, and
     * <code>false</code> otherwise.
     *
     * @return <code>true</code> or </code> false.
     */
    public boolean isPaintTableActive() {
        return this.paintTableActive;
    }

    /**
     * Sets a flag that controls whether or not the paint table is active.
     * <p>
     * Except in very special circumstances, you will probably want to leave this flag at
     * the default value of <code>true</code>.
     *
     * @param active  the new value of the flag.
     */
    public void setPaintTableActive(boolean active) {
        if (this.paintTableActive != active) {
            this.paintTableActive = active;
            if (active) {
                if (this.paintTable == null) {
                    this.paintTable = new PaintTable();
                }
            }
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Returns the paint used to fill a section of the pie plot.
     *
     * @param section  the section index (zero-based).
     *
     * @return The paint.
     */
    public Paint getPaint(int section) {

        Paint result = null;
        if (this.paintTableActive) {
            result = this.paintTable.getPaint(0, section);
            if (result == null) {
                DrawingSupplier supplier = getDrawingSupplier();
                if (supplier != null) {
                    result = supplier.getNextPaint();
                    this.paintTable.setPaint(0, section, result);
                }
            }
        }
        else {
            result = this.defaultPaint;
        }
        return result;

    }

    /**
     * Sets the paint used to fill a section of the pie.
     *
     * @param section  the section index (zero-based).
     * @param paint  the paint.
     */
    public void setPaint(int section, Paint paint) {
        this.paintTable.setPaint(0, section, paint);
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the default paint.  This is used to fill pie sections when the paint
     * table is inactive (not common).
     *
     * @return The paint.
     */
    public Paint getDefaultPaint() {
        return this.defaultPaint;
    }

    /**
     * Sets the default paint.
     *
     * @param paint  the paint.
     */
    public void setDefaultPaint(Paint paint) {
        this.defaultPaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns <code>true</code> if the lookup table for outline paint objects is being used, and
     * <code>false</code> otherwise.
     *
     * @return <code>true</code> or </code> false.
     */
    public boolean isOutlinePaintTableActive() {
        return this.outlinePaintTableActive;
    }

    /**
     * Sets a flag that controls whether or not the paint table is active.
     * <p>
     * Except in very special circumstances, you will probably want to leave this flag at
     * the default value of <code>true</code>.
     *
     * @param active  the new value of the flag.
     */
    public void setOutlinePaintTableActive(boolean active) {
        if (this.outlinePaintTableActive != active) {
            this.outlinePaintTableActive = active;
            if (active) {
                if (this.outlinePaintTable == null) {
                    this.outlinePaintTable = new PaintTable();
                }
            }
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Returns the paint used to outline a section of the pie plot.
     *
     * @param section  the section index (zero-based).
     *
     * @return The paint.
     */
    public Paint getOutlinePaint(int section) {

        Paint result = null;
        if (this.outlinePaintTableActive) {
            result = this.outlinePaintTable.getPaint(0, section);
            if (result == null) {
                DrawingSupplier supplier = getDrawingSupplier();
                if (supplier != null) {
                    result = supplier.getNextOutlinePaint();
                    this.outlinePaintTable.setPaint(0, section, result);
                }
            }
        }
        else {
            result = this.defaultOutlinePaint;
        }
        return result;

    }

    /**
     * Sets the paint used to outline a section of the pie.
     *
     * @param section  the section index (zero-based).
     * @param paint  the paint.
     */
    public void setOutlinePaint(int section, Paint paint) {
        this.outlinePaintTable.setPaint(0, section, paint);
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the default outline paint.  This is used to outline pie sections when the paint
     * table is inactive (the usual case).
     *
     * @return The paint.
     */
    public Paint getDefaultOutlinePaint() {
        return this.defaultOutlinePaint;
    }

    /**
     * Sets the default outline paint.
     *
     * @param paint  the paint.
     */
    public void setDefaultOutlinePaint(Paint paint) {
        this.defaultOutlinePaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns <code>true</code> if the lookup table for outline stroke objects is being used, and
     * <code>false</code> otherwise.
     *
     * @return <code>true</code> or </code> false.
     */
    public boolean isOutlineStrokeTableActive() {
        return this.outlineStrokeTableActive;
    }

    /**
     * Sets a flag that controls whether or not the outline stroke table is active.
     *
     * @param active  the new value of the flag.
     */
    public void setOutlineStrokeTableActive(boolean active) {
        if (this.outlineStrokeTableActive != active) {
            this.outlineStrokeTableActive = active;
            if (active) {
                if (this.outlineStrokeTable == null) {
                    this.outlineStrokeTable = new StrokeTable();
                }
            }
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Returns the stroke used to outline a section of the pie plot.
     *
     * @param section  the section index (zero-based).
     *
     * @return The stroke.
     */
    public Stroke getOutlineStroke(int section) {

        Stroke result = null;
        if (this.outlineStrokeTableActive) {
            result = this.outlineStrokeTable.getStroke(0, section);
            if (result == null) {
                DrawingSupplier supplier = getDrawingSupplier();
                if (supplier != null) {
                    result = supplier.getNextOutlineStroke();
                    this.outlineStrokeTable.setStroke(0, section, result);
                }
            }
        }
        else {
            result = this.defaultOutlineStroke;
        }
        return result;

    }

    /**
     * Sets the stroke used to outline a section of the pie.
     *
     * @param section  the section index (zero-based).
     * @param stroke  the stroke.
     */
    public void setOutlineStroke(int section, Stroke stroke) {
        this.outlineStrokeTable.setStroke(0, section, stroke);
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the default outline stroke.  This is used to outline pie sections when the outline
     * stroke table is inactive.
     *
     * @return The stroke.
     */
    public Stroke getDefaultOutlineStroke() {
        return this.defaultOutlineStroke;
    }

    /**
     * Sets the default outline stroke.
     *
     * @param stroke  the stroke.
     */
    public void setDefaultOutlineStroke(Stroke stroke) {
        this.defaultOutlineStroke = stroke;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the minimum arc angle that will be drawn.  Pie sections for an angle smaller than
     * this are not drawn, to avoid a JDK bug.
     *
     * @return The minimum angle.
     */
    public double getMinimumArcAngleToDraw() {
        return this.minimumArcAngleToDraw;
    }

    /**
     * Sets the minimum arc angle that will be drawn.  Pie sections for an angle smaller than
     * this are not drawn, to avoid a JDK bug.
     *
     * @param angle  the minimum angle.
     */
    public void setMinimumArcAngleToDraw(double angle) {
        this.minimumArcAngleToDraw = angle;
    }

    /**
     * Returns a collection of legend items for the pie chart.
     *
     * @return the legend items.
     */
    public LegendItemCollection getLegendItems() {

        LegendItemCollection result = new LegendItemCollection();

        List keys = null;
        Dataset data = getDataset();
        if (data instanceof PieDataset) {
            PieDataset pieData = (PieDataset) data;
            keys = pieData.getKeys();
        }
        else if (data instanceof CategoryDataset) {
            CategoryDataset categoryData = (CategoryDataset) data;
            if (this.extractType == PER_ROW) {
                keys = categoryData.getColumnKeys();
            }
            else if (this.extractType == PER_COLUMN) {
                keys = categoryData.getRowKeys();
            }
        }

        if (keys != null) {
            int section = 0;
            Iterator iterator = keys.iterator();
            while (iterator.hasNext()) {
                String label = iterator.next().toString();
                String description = label;
                Shape shape = null;
                Paint paint = getPaint(section);
                Paint outlinePaint = getOutlinePaint(section);
                Stroke stroke = getOutlineStroke(section);

                LegendItem item = new LegendItem(label, description,
                                                 shape, paint, outlinePaint,
                                                 stroke);

                result.add(item);
                section++;
            }
        }

        return result;
    }

    /**
     * Returns the tooltip generator (possibly null).
     *
     * @return the tool tip generator.
     */
    public PieToolTipGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }

    /**
     * Sets the tool tip generator.
     * <P>
     * If you set the generator to null, no tool tips will be generated for the pie chart.
     *
     * @param generator  the new tooltip generator (null permitted).
     */
    public void setToolTipGenerator(PieToolTipGenerator generator) {
        this.toolTipGenerator = generator;
    }

    /**
     * Returns the URL generator (possibly null).
     *
     * @return the URL generator.
     */
    public PieURLGenerator getURLGenerator() {
        return this.urlGenerator;
    }

    /**
     * Sets the URL generator.
     *
     * @param generator the new URL generator (null permitted).
     */
    public void setURLGenerator(PieURLGenerator generator) {
        this.urlGenerator = generator;
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the plot should be drawn.
     * @param info  collects info about the drawing.
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, ChartRenderingInfo info) {

        // adjust for insets...
        Insets insets = getInsets();
        if (insets != null) {
            plotArea.setRect(plotArea.getX() + insets.left,
                             plotArea.getY() + insets.top,
                             plotArea.getWidth() - insets.left - insets.right,
                             plotArea.getHeight() - insets.top - insets.bottom);
        }

        if (info != null) {
            info.setPlotArea(plotArea);
            info.setDataArea(plotArea);
        }

        drawBackground(g2, plotArea);
        drawOutline(g2, plotArea);

        Shape savedClip = g2.getClip();
        g2.clip(plotArea);

        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getForegroundAlpha()));

        if (this.dataset != null) {
            drawPie(g2, plotArea, info, 0, this.dataset, null);
        }
        else {
            int pieCount = 0;
            if (this.extractType == PER_ROW) {
                pieCount = this.multiDataset.getRowCount();
            }
            else {
                pieCount = this.multiDataset.getColumnCount();
            }

            // the columns variable is always >= rows
            int columns = (int) Math.ceil(Math.sqrt(pieCount));
            int rows = (int) Math.ceil((double) pieCount / (double) columns);

            // swap rows and columns to match plotArea shape
            if (columns > rows && plotArea.getWidth() < plotArea.getHeight()) {
                int temp = columns;
                columns = rows;
                rows = temp;
            }

            int fontHeight = g2.getFontMetrics(seriesLabelFont).getHeight() * 2;
            int x = (int) plotArea.getX();
            int y = (int) plotArea.getY();
            int width = ((int) plotArea.getWidth()) / columns;
            int height = ((int) plotArea.getHeight()) / rows;
            int row = 0;
            int column = 0;
            int diff = (rows * columns) - pieCount;
            int xoffset = 0;
            Rectangle rect = new Rectangle ();

            for (int pieIndex = 0; pieIndex < pieCount; pieIndex++) {
                rect.setBounds(x + xoffset + (width * column),
                               y + (height * row), width, height - fontHeight);

                PieDataset dd = new CategoryToPieDataset(this.multiDataset, this.extractType,
                                                         pieIndex);
                String title = null;
                if (this.extractType == PER_ROW) {
                    title = this.multiDataset.getRowKey(pieIndex).toString();
                }
                else {
                    title = this.multiDataset.getColumnKey(pieIndex).toString();
                }

                drawPie(g2, rect, info, pieIndex, dd, title);
                ++column;
                if (column == columns) {
                    column = 0;
                    ++row;

                    if (row == rows - 1 && diff != 0) {
                        xoffset = (diff * width) / 2;
                    }
                }
            }
        }

        g2.clip(savedClip);
        g2.setComposite(originalComposite);

        drawOutline(g2, plotArea);

    }

    /**
     * Draws the pie.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param info  chart rendering info.
     * @param pieIndex  the pie index.
     * @param data  the data.
     * @param label  the label.
     */
    protected void drawPie(Graphics2D g2, Rectangle2D plotArea, ChartRenderingInfo info,
                           int pieIndex, PieDataset data, String label) {

        // adjust the plot area by the interior spacing value
        double gapHorizontal = plotArea.getWidth() * this.interiorGap;
        double gapVertical = plotArea.getHeight() * this.interiorGap;
        double pieX = plotArea.getX() + gapHorizontal / 2;
        double pieY = plotArea.getY() + gapVertical / 2;
        double pieW = plotArea.getWidth() - gapHorizontal;
        double pieH = plotArea.getHeight() - gapVertical;

        // make the pie area a square if the pie chart is to be circular...
        if (circular) {
            double min = Math.min(pieW, pieH) / 2;
            pieX = (pieX + pieX + pieW) / 2 - min;
            pieY = (pieY + pieY + pieH) / 2 - min;
            pieW = 2 * min;
            pieH = 2 * min;
        }

        Rectangle2D explodedPieArea = new Rectangle2D.Double(pieX, pieY, pieW, pieH);
        double explodeHorizontal = (1 - radius) * pieW;
        double explodeVertical = (1 - radius) * pieH;
        Rectangle2D pieArea = new Rectangle2D.Double(pieX + explodeHorizontal / 2,
                                                     pieY + explodeVertical / 2,
                                                     pieW - explodeHorizontal,
                                                     pieH - explodeVertical);

        // plot the data (unless the dataset is null)...
        if ((data != null) && (data.getKeys().size() > 0)) {

            // get a list of categories...
            List keys = data.getKeys();

            // compute the total value of the data series skipping over the negative values
            double totalValue = DatasetUtilities.getPieDatasetTotal(data);

            // For each positive value in the dataseries, compute and draw the corresponding arc.
            double runningTotal = 0;
            int section = 0;
            Iterator iterator = keys.iterator();
            while (iterator.hasNext()) {

                Comparable currentKey = (Comparable) iterator.next();
                Number dataValue = data.getValue(currentKey);
                if (dataValue != null) {
                    double value = dataValue.doubleValue();
                    if (value > 0) {

                        // draw the pie section...
                        double angle1 = startAngle
                                        + (direction * (runningTotal * 360) / totalValue);
                        double angle2 = startAngle
                                        + (direction * (runningTotal + value) * 360 / totalValue);
                        runningTotal += value;

                        double angle = (angle2 - angle1);
                        if (Math.abs(angle) > this.minimumArcAngleToDraw) {
                            Rectangle2D arcBounds = getArcBounds(pieArea, explodedPieArea,
                                                                 angle1, angle2 - angle1,
                                                                 getExplodePercent(section));
                            Arc2D.Double arc = new Arc2D.Double(arcBounds, angle1, angle2 - angle1,
                                                                Arc2D.PIE);

                            Paint paint = getPaint(section);
                            Paint outlinePaint = getOutlinePaint(section);
                            Stroke outlineStroke = getOutlineStroke(section);

                            g2.setPaint(paint);
                            g2.fill(arc);
                            g2.setStroke(outlineStroke);
                            g2.setPaint(outlinePaint);
                            g2.draw(arc);

                            // add a tooltip for the pie section...
                            if (info != null) {
                                EntityCollection entities = info.getEntityCollection();
                                if (entities != null) {
                                    String tip = null;
                                    if (this.toolTipGenerator != null) {
                                        tip = this.toolTipGenerator.generateToolTip(data,
                                            currentKey, pieIndex);
                                    }
                                    String url = null;
                                    if (this.urlGenerator != null) {
                                        url = this.urlGenerator.generateURL(data, currentKey,
                                                                            pieIndex);
                                    }
                                    PieSectionEntity entity = new PieSectionEntity(
                                        arc, dataset, pieIndex, section, currentKey, tip, url
                                    );
                                    entities.addEntity(entity);
                                }
                            }
                        }

                        // then draw the label...
                        if (this.sectionLabelType != NO_LABELS) {
                            drawLabel(g2, pieArea, explodedPieArea, data, value,
                                      section, angle1, angle2 - angle1);
                        }

                    }
                }
                section = section + 1;
            }

            // draw the series label
            if (label != null) {
                g2.setPaint(seriesLabelPaint);
                g2.setFont(seriesLabelFont);

                Rectangle2D bounds = g2.getFontMetrics().getStringBounds(label, g2);
                double labelX = pieX + (pieW / 2) - (bounds.getWidth() / 2);
                double labelY = pieY + pieH + 2 * bounds.getHeight();
                g2.drawString(label, (int) labelX, (int) labelY);
            }
        }
        else {
            drawNoDataMessage(g2, plotArea);
        }
    }

    /**
     * Draws the label for one pie section.
     * <P>
     * You can control the label type using the <code>setSectionLabelType()</code> method.
     *
     * @param g2  the graphics device.
     * @param pieArea  the area for the unexploded pie sections.
     * @param explodedPieArea  the area for the exploded pie section.
     * @param data  the data for the plot.
     * @param value  the value of the label.
     * @param section  the section (zero-based index).
     * @param startAngle  the starting angle.
     * @param extent  the extent of the arc.
     */
    protected void drawLabel(Graphics2D g2,
                             Rectangle2D pieArea, Rectangle2D explodedPieArea,
                             PieDataset data, double value,
                             int section, double startAngle, double extent) {

        // handle label drawing...
        FontRenderContext frc = g2.getFontRenderContext();
        String label = "";
        if (this.sectionLabelType == NAME_LABELS) {
            label = data.getKey(section).toString();
        }
        else if (this.sectionLabelType == VALUE_LABELS) {
            label = valueFormatter.format(value);
        }
        else if (this.sectionLabelType == PERCENT_LABELS) {
            label = percentFormatter.format(extent / 360 * this.direction);
        }
        else if (this.sectionLabelType == NAME_AND_VALUE_LABELS) {
            label = data.getKey(section).toString()
                    + " (" + valueFormatter.format(value) + ")";
        }
        else if (this.sectionLabelType == NAME_AND_PERCENT_LABELS) {
            label = data.getKey(section).toString()
                    + " (" + percentFormatter.format(extent / 360 * this.direction) + ")";
        }
        else if (this.sectionLabelType == VALUE_AND_PERCENT_LABELS) {
            label = valueFormatter.format(value)
                    + " (" + percentFormatter.format(extent / 360 * this.direction) + ")";
        }
        Rectangle2D labelBounds = this.sectionLabelFont.getStringBounds(label, frc);
        LineMetrics lm = this.sectionLabelFont.getLineMetrics(label, frc);
        double ascent = lm.getAscent();
        Point2D labelLocation = calculateLabelLocation(labelBounds, ascent,
                                                       pieArea, explodedPieArea,
                                                       startAngle, extent,
                                                       getExplodePercent(section));

        Composite saveComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g2.setPaint(this.sectionLabelPaint);
        g2.setFont(this.sectionLabelFont);
        g2.drawString(label, (float) labelLocation.getX(), (float) labelLocation.getY());
        g2.setComposite(saveComposite);

    }

    /**
     * Returns a short string describing the type of plot.
     *
     * @return the plot type.
     */
    public String getPlotType() {
        return "Pie Plot";
    }

    /**
     * A zoom method that does nothing.
     * <p>
     * Plots are required to support the zoom operation.  In the case of a pie
     * chart, it doesn't make sense to zoom in or out, so the method is empty.
     *
     * @param percent  the zoom percentage.
     */
    public void zoom(double percent) {
    }

    /**
     * Returns a rectangle that can be used to create a pie section (taking
     * into account the amount by which the pie section is 'exploded').
     *
     * @param unexploded  the area inside which the unexploded pie sections are drawn.
     * @param exploded  the area inside which the exploded pie sections are drawn.
     * @param startAngle  the start angle.
     * @param extent  the extent of the arc.
     * @param explodePercent  the amount by which the pie section is exploded.
     *
     * @return a rectangle that can be used to create a pie section.
     */
    protected Rectangle2D getArcBounds(Rectangle2D unexploded, Rectangle2D exploded,
                                       double startAngle, double extent, double explodePercent) {

        if (explodePercent == 0.0) {
            return unexploded;
        }
        else {
            Arc2D arc1 = new Arc2D.Double(unexploded, startAngle, extent / 2, Arc2D.OPEN);
            Point2D point1 = arc1.getEndPoint();
            Arc2D.Double arc2 = new Arc2D.Double(exploded, startAngle, extent / 2, Arc2D.OPEN);
            Point2D point2 = arc2.getEndPoint();
            double deltaX = (point1.getX() - point2.getX()) * explodePercent;
            double deltaY = (point1.getY() - point2.getY()) * explodePercent;
            return new Rectangle2D.Double(unexploded.getX() - deltaX,
                                          unexploded.getY() - deltaY,
                                          unexploded.getWidth(),
                                          unexploded.getHeight());

        }
    }

    /**
     * Returns the location for a label, taking into account whether or not the
     * pie section is exploded.
     *
     * @param labelBounds  the label bounds.
     * @param ascent  the ascent.
     * @param unexploded  the area within which the unexploded pie sections are drawn.
     * @param exploded  the area within which the exploded pie sections are drawn.
     * @param startAngle  the start angle for the pie section.
     * @param extent  the extent of the arc.
     * @param explodePercent  the amount by which the pie section is exploded.
     *
     * @return the location for a label.
     */
    protected Point2D calculateLabelLocation(Rectangle2D labelBounds,
                                             double ascent,
                                             Rectangle2D unexploded,
                                             Rectangle2D exploded,
                                             double startAngle,
                                             double extent,
                                             double explodePercent) {

        Arc2D arc1 = new Arc2D.Double(unexploded, startAngle, extent / 2, Arc2D.OPEN);
        Point2D point1 = arc1.getEndPoint();
        Arc2D.Double arc2 = new Arc2D.Double(exploded, startAngle, extent / 2, Arc2D.OPEN);
        Point2D point2 = arc2.getEndPoint();
        double deltaX = (point1.getX() - point2.getX()) * explodePercent;
        deltaX = deltaX - (point1.getX() - unexploded.getCenterX()) * sectionLabelGap;
        double deltaY = (point1.getY() - point2.getY()) * explodePercent;
        deltaY = deltaY - (point1.getY() - unexploded.getCenterY()) * sectionLabelGap;

        double labelX = point1.getX() - deltaX;
        double labelY = point1.getY() - deltaY;

        if (labelX <= unexploded.getCenterX()) {
            labelX -= labelBounds.getWidth();
        }
        if (labelY > unexploded.getCenterY()) {
            labelY += ascent;
        }

        return new Point2D.Double(labelX, labelY);

    }

    /**
     * Tests this plot for equality with another object.
     *
     * @param obj  the other object.
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

        if (super.equals(obj)) {
            if (obj instanceof PiePlot) {
                PiePlot p = (PiePlot) obj;
                boolean b0 = (this.interiorGap == p.interiorGap);
                boolean b1 = (this.circular == p.circular);
                boolean b2 = (this.radius == p.radius);
                boolean b3 = (this.startAngle == p.startAngle);
                boolean b4 = (this.direction == p.direction);
                boolean b5 = (this.sectionLabelType == p.sectionLabelType);
                boolean b6 = ObjectUtils.equalOrBothNull(this.sectionLabelFont,
                                                         p.sectionLabelFont);
                boolean b7 = ObjectUtils.equalOrBothNull(this.sectionLabelPaint,
                                                         p.sectionLabelPaint);
                boolean b8 = (this.sectionLabelGap == p.sectionLabelGap);
                boolean b9 = ObjectUtils.equalOrBothNull(this.explodePercentages,
                                                         p.explodePercentages);

                boolean b10 = ObjectUtils.equalOrBothNull(this.valueFormatter, p.valueFormatter);
                boolean b11 = ObjectUtils.equalOrBothNull(this.percentFormatter,
                                                          p.percentFormatter);
                boolean b12 = ObjectUtils.equalOrBothNull(this.toolTipGenerator,
                                                          p.toolTipGenerator);
                boolean b13 = ObjectUtils.equalOrBothNull(this.urlGenerator, p.urlGenerator);

                boolean b14 = (this.showSeriesLabels == p.showSeriesLabels);
                boolean b15 = ObjectUtils.equalOrBothNull(this.seriesLabelFont, p.seriesLabelFont);
                boolean b16 = ObjectUtils.equalOrBothNull(this.seriesLabelPaint,
                                                          p.seriesLabelPaint);

                //boolean b17 = ObjectUtils.equalOrBothNull(this.supplier, p.supplier);

                boolean b18 = (this.paintTableActive == p.paintTableActive);
                boolean b19 = ObjectUtils.equalOrBothNull(this.paintTable, p.paintTable);
                boolean b20 = ObjectUtils.equalOrBothNull(this.defaultPaint, p.defaultPaint);

                boolean b21 = (this.outlinePaintTableActive == p.outlinePaintTableActive);
                boolean b22 = ObjectUtils.equalOrBothNull(this.outlinePaintTable,
                                                          p.outlinePaintTable);
                boolean b23 = ObjectUtils.equalOrBothNull(this.defaultOutlinePaint,
                                                          p.defaultOutlinePaint);

                boolean b24 = (this.outlineStrokeTableActive == p.outlineStrokeTableActive);
                boolean b25 = ObjectUtils.equalOrBothNull(this.outlineStrokeTable,
                                                          p.outlineStrokeTable);
                boolean b26 = ObjectUtils.equalOrBothNull(this.defaultOutlineStroke,
                                                          p.defaultOutlineStroke);

                boolean b27 = (this.extractType == p.extractType);

                return b0 && b1 && b2 && b3 && b4 && b5 && b6 && b7 && b8 && b9
                       && b10 && b11 && b12 && b13 && b14 && b15 && b16 && b18 && b19
                       && b20 && b21 && b22 && b23 && b24 && b25 && b26 && b27;
            }
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
        SerialUtilities.writePaint(this.sectionLabelPaint, stream);
        SerialUtilities.writePaint(this.seriesLabelPaint, stream);
        SerialUtilities.writePaint(this.defaultPaint, stream);
        SerialUtilities.writePaint(this.defaultOutlinePaint, stream);
        SerialUtilities.writeStroke(this.defaultOutlineStroke, stream);
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
        this.sectionLabelPaint = SerialUtilities.readPaint(stream);
        this.seriesLabelPaint = SerialUtilities.readPaint(stream);
        this.defaultPaint = SerialUtilities.readPaint(stream);
        this.defaultOutlinePaint = SerialUtilities.readPaint(stream);
        this.defaultOutlineStroke = SerialUtilities.readStroke(stream);
    }

}
