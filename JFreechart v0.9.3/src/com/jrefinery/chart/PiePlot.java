/* ============================================
 * JFreeChart : a free Java chart class library
 * ============================================
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
 * ------------
 * PiePlot.java
 * ------------
 * (C) Copyright 2000-2002, by Andrzej Porebski and Contributors.
 *
 * Original Author:  Andrzej Porebski;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *                   Martin Cordova (percentages in labels);
 *                   Richard Atkinson (URL support for image maps);
 *
 * $Id: PiePlot.java,v 1.1 2007/10/10 19:52:14 vauchers Exp $
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
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.Paint;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Insets;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Arc2D;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import com.jrefinery.data.PieDataset;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.DatasetUtilities;
import com.jrefinery.chart.entity.PieSectionEntity;
import com.jrefinery.chart.event.PlotChangeEvent;
import com.jrefinery.chart.tooltips.PieToolTipGenerator;
import com.jrefinery.chart.tooltips.StandardPieToolTipGenerator;
import com.jrefinery.chart.urls.PieURLGenerator;

/**
 * A plot that displays data in the form of a pie chart, using data from any
 * class that implements the PieDataset interface.
 * <P>
 * Special notes:
 * <ol>
 * <li>The default starting point is 12 o'clock and the pie sections proceed
 *  in a clockwise direction, but these settings can be changed</li>
 * <li>negative values in the dataset are ignored</li>
 * <li>there are utility methods for creating a PieDataset from a
 *  CategoryDataset</li>
 * </ol>
 *
 * @see Plot
 * @see PieDataset
 *
 */
public class PiePlot extends Plot {

    /** A constant indicating the clockwise direction. */
    public static final int CLOCKWISE = -1;

    /** A constant representing the anti-clockwise direction. */
    public static final int ANTICLOCKWISE = 1;

    /** The default interior gap percent (currently 20%). */
    public static final double DEFAULT_INTERIOR_GAP = 0.20;

    /** The maximum interior gap (currently 40%). */
    public static final double MAX_INTERIOR_GAP = 0.40;

    /** The default radius percent (currently 100%). */
    public static final double DEFAULT_RADIUS = 1.00;

    /** The maximum radius (currently 100%). */
    public static final double MAX_RADIUS = 1.00;

    /** The default section label font. */
    public static final Font DEFAULT_SECTION_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 10);

    /** The default section label paint. */
    public static final Paint DEFAULT_SECTION_LABEL_PAINT = Color.black;

    /** The default section label gap (currently 10%). */
    public static final double DEFAULT_SECTION_LABEL_GAP = 0.10;

    /** The maximum interior gap (currently 30%). */
    public static final double MAX_SECTION_LABEL_GAP = 0.30;

    /** The default series label font. */
    public static final Font DEFAULT_SERIES_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 12);

    /** The default series label paint. */
    public static final Paint DEFAULT_SERIES_LABEL_PAINT = Color.black;

    /** The default for the show series labels flag */
    public static final boolean DEFAULT_SHOW_SERIES_LABELS = true;

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

    /** The amount of space left around the outside of the pie plot, expressed as a percentage. */
    protected double interiorGapPercent;

    /** Flag determining whether to draw an ellipse or a perfect circle. */
    protected boolean circular;

    /** The direction for the pie segments. */
    protected int direction = CLOCKWISE;

    /** The starting angle. */
    protected double startAngle = 90.0;  // corresponds to 12 o'clock

    /** The radius as a percentage of the available drawing area. */
    protected double radiusPercent;

    /** Label type (NO_LABELS, NAME_LABELS, PERCENT_LABELS, NAME_AND_PERCENT_LABELS). */
    protected int sectionLabelType;

    /** The font used to display the section labels. */
    protected Font sectionLabelFont;

    /** The color used to draw the section labels. */
    protected Paint sectionLabelPaint;

    /** The gap between the labels and the pie sections, as a percentage of the radius. */
    protected double sectionLabelGapPercent;

    /** The percentage amount to explode each pie section. */
    protected double[] explodePercentages;

    /** A formatter for displayed values. */
    protected DecimalFormat valueFormatter;

    /** A formatter for displayed percentages. */
    protected DecimalFormat percentFormatter;

    /** The tool tip generator. */
    protected PieToolTipGenerator toolTipGenerator;

    /** The URL generator. */
    protected PieURLGenerator urlGenerator;

    /** Flag used to determine whether series labels are shown */
    protected boolean showSeriesLabels = true;

    /** The font used to display the series labels. */
    protected Font seriesLabelFont = DEFAULT_SERIES_LABEL_FONT;

    /** The color used to draw the series labels. */
    protected Paint seriesLabelPaint = DEFAULT_SERIES_LABEL_PAINT;

    /** Dataset used when rendering multiple series as pie charts */
    private CategoryDataset categoryData;

    /**
     * Constructs a new pie plot, using default attributes as required.
     *
     * @param data  The data.
     */
    public PiePlot(PieDataset data) {

        this(data,
             Plot.DEFAULT_INSETS,
             Plot.DEFAULT_BACKGROUND_PAINT,
             null, // background image
             Plot.DEFAULT_BACKGROUND_ALPHA,
             Plot.DEFAULT_OUTLINE_STROKE,
             Plot.DEFAULT_OUTLINE_PAINT,
             Plot.DEFAULT_FOREGROUND_ALPHA,
             PiePlot.DEFAULT_INTERIOR_GAP,
             true, // circular
             DEFAULT_RADIUS,
             NAME_LABELS,
             DEFAULT_SECTION_LABEL_FONT,
             DEFAULT_SECTION_LABEL_PAINT,
             DEFAULT_SECTION_LABEL_GAP,
             "0.00",
             "0.0",
             null, null);

    }

    /**
     * Constructs a new pie plot, using default attributes as required.
     *
     * @param data  The data.
     */
    public PiePlot (CategoryDataset data) {
        this (DatasetUtilities.createPieDataset(data, 0));
        categoryData = data;
    }

    /**
     * Constructs a pie plot.
     *
     * @param data  The data.
     * @param insets  Amount of blank space around the plot area.
     * @param backgroundPaint  An optional color for the plot's background.
     * @param backgroundImage  An optional image for the plot's background.
     * @param backgroundAlpha  Alpha-transparency for the plot's background.
     * @param outlineStroke  The Stroke used to draw an outline around the plot.
     * @param outlinePaint  The color used to draw an outline around the plot.
     * @param foregroundAlpha  The alpha-transparency for the plot.
     * @param interiorGapPercent  The interior gap (space for labels) as a percentage of the
     *                            available space.
     * @param circular  Flag indicating whether the pie chart is circular or elliptical.
     * @param radiusPercent  The radius of the pie chart, as a percentage of the available space
     *                       (after accounting for interior gap).
     * @param sectionLabelType  The type of labels for the pie sections.
     * @param sectionLabelFont  The font for the section labels.
     * @param sectionLabelPaint  The color for the section labels.
     * @param sectionLabelGapPercent  The space between the pie sections and the labels.
     * @param valueFormatString  The value format string.
     * @param percentFormatString  The percent format string.
     * @param urlGenerator  The URL generator.
     * @param toolTipGenerator  The tooltip generator.
     */
    public PiePlot(PieDataset data,
                   Insets insets,
                   Paint backgroundPaint, Image backgroundImage, float backgroundAlpha,
                   Stroke outlineStroke, Paint outlinePaint,
                   float foregroundAlpha,
                   double interiorGapPercent, boolean circular, double radiusPercent,
                   int sectionLabelType,
                   Font sectionLabelFont, Paint sectionLabelPaint,
                   double sectionLabelGapPercent,
                   String valueFormatString, String percentFormatString,
                   PieToolTipGenerator toolTipGenerator, PieURLGenerator urlGenerator) {

        super(data,
              insets,
              backgroundPaint, backgroundImage, backgroundAlpha,
              outlineStroke, outlinePaint, foregroundAlpha);

        this.interiorGapPercent = interiorGapPercent;
        this.circular = circular;
        this.radiusPercent = radiusPercent;
        this.sectionLabelType = sectionLabelType;
        this.sectionLabelFont = sectionLabelFont;
        this.sectionLabelPaint = sectionLabelPaint;
        this.sectionLabelGapPercent = sectionLabelGapPercent;
        this.valueFormatter = new DecimalFormat(valueFormatString);
        this.percentFormatter = new DecimalFormat(percentFormatString);
        this.explodePercentages = null;
        this.toolTipGenerator = toolTipGenerator;
        this.urlGenerator = urlGenerator;
        setInsets(insets);

    }

    /**
     * Returns the show series labels flag.
     *
     * @return The show series label flag.
     */
    public boolean getShowSeriesLabels () {
        return (this.showSeriesLabels);
    }

    /**
     * Sets the show series labels flag.
     * <P>
     * Notifies registered listeners that the plot has been changed.
     *
     * @param flag The new show series labels flag.
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
     * @return The series label font.
     */
    public Font getSeriesLabelFont() {
        return this.seriesLabelFont;
    }

    /**
     * Sets the series label font.
     * <P>
     * Notifies registered listeners that the plot has been changed.
     *
     * @param font The new series label font.
     */
    public void setSeriesLabelFont(Font font) {

        // check arguments...
        if (font==null) {
            throw new IllegalArgumentException("PiePlot.setSeriesLabelFont(...): "
                                               +"null font not allowed.");
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
     * @return The series label paint.
     */
    public Paint getSeriesLabelPaint() {
        return this.seriesLabelPaint;
    }

    /**
     * Sets the series label paint.
     * <P>
     * Notifies registered listeners that the plot has been changed.
     *
     * @param paint The new series label paint.
     */
    public void setSeriesLabelPaint(Paint paint) {

        // check arguments...
        if (paint==null) {
            throw new IllegalArgumentException("PiePlot.setSeriesLabelPaint(...): "
                                               +"null paint not allowed.");
        }

        // make the change...
        if (!this.seriesLabelPaint.equals(paint)) {
            this.seriesLabelPaint = paint;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the start angle for the first pie section.
     * <P>
     * This is measured in degrees starting from 3 o'clock and measuring anti-clockwise.
     *
     * @return The start angle.
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
     * @param angle  The angle (in degrees).
     */
    public void setStartAngle(double angle) {
        this.startAngle = angle;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the direction in which the pie sections are drawn (clockwise or anti-clockwise).
     *
     * @return The direction.
     */
    public int getDirection() {
        return this.direction;
    }

    /**
     * Sets the direction (use the constants CLOCKWISE or ANTICLOCKWISE).
     *
     * @param direction  The new direction.
     */
    public void setDirection(int direction) {

        // check argument...
        if ((direction!=CLOCKWISE) && (direction!=ANTICLOCKWISE)) {
            throw new IllegalArgumentException(
                "PiePlot.setDirection(int): invalid direction.");
        }

        // make the change...
        this.direction = direction;
        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Returns the interior gap, measures as a percentage of the available drawing space.
     *
     * @return The interior gap percentage.
     */
    public double getInteriorGapPercent() {
        return this.interiorGapPercent;
    }

    /**
     * Sets the interior gap percent.
     *
     * @param percent The percentage.
     */
    public void setInteriorGapPercent(double percent) {

        // check arguments...
        if ((percent<0.0) || (percent>MAX_INTERIOR_GAP)) {
            throw new IllegalArgumentException(
                "PiePlot.setInteriorGapPercent(double): "
                + "percentage outside valid range.");
        }

        // make the change...
        if (this.interiorGapPercent!=percent) {
            this.interiorGapPercent = percent;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns a flag indicating whether the pie chart is circular, or
     * stretched into an elliptical shape.
     *
     * @return A flag indicating whether the pie chart is circular.
     */
    public boolean isCircular() {
        return circular;
    }

    /**
     * A flag indicating whether the pie chart is circular, or stretched into
     * an elliptical shape.
     *
     * @param flag  The new value.
     */
    public void setCircular(boolean flag) {

        // no argument checking required...
        // make the change...
        if (circular!=flag) {
            circular = flag;
            this.notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the radius percentage.
     *
     * @return The radius percentage.
     */
    public double getRadiusPercent() {
        return this.radiusPercent;
    }

    /**
     * Sets the radius percentage.
     *
     * @param percent The new value.
     */
    public void setRadiusPercent(double percent) {

        // check arguments...
        if ((percent<=0.0) || (percent>MAX_RADIUS)) {
            throw new IllegalArgumentException(
                "PiePlot.setRadiusPercent(double): "
                + "percentage outside valid range.");
        }

        // make the change (if necessary)...
        if (this.radiusPercent!=percent) {
            this.radiusPercent = percent;
            this.notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the amount that a section should be 'exploded'.
     *
     * @param section  The section number.
     *
     * @return the amount that a section should be 'exploded'.
     */
    public double getExplodePercent(int section) {

        // check argument...
        if (section<0) {
            throw new IllegalArgumentException(
                "PiePlot.getExplodePercent(int): "
                + "section outside valid range.");
        }

        // fetch the result...
        double result = 0.0;

        if (this.explodePercentages!=null) {
            if (section<this.explodePercentages.length) {
                result = explodePercentages[section];
            }
        }

        return result;

    }

    /**
     * Sets the amount that a pie section should be exploded.
     *
     * @param section  The section index.
     * @param percent  The amount to explode the section as a percentage.
     */
    public void setExplodePercent(int section, double percent) {

        // check argument...
        if ((section<0) || (section>=getPieDataset().getCategories().size())) {
            throw new IllegalArgumentException(
                "PiePlot.setExplodePercent(int, double): section outside valid range.");
        }

        // store the value in an appropriate data structure...
        if (this.explodePercentages!=null) {
            if (section<this.explodePercentages.length) {
                explodePercentages[section] = percent;
            }
            else {
                double[] newExplodePercentages = new double[section];
                for (int i=0; i<this.explodePercentages.length; i++) {
                    newExplodePercentages[i] = this.explodePercentages[i];
                }
                this.explodePercentages = newExplodePercentages;
                this.explodePercentages[section] = percent;
            }
        }
        else {
            explodePercentages = new double[getPieDataset().getCategories().size()];
            explodePercentages[section] = percent;
        }

    }

    /**
     * Returns the section label type.  Defined by the constants: NO_LABELS,
     * NAME_LABELS, PERCENT_LABELS and NAME_AND_PERCENT_LABELS.
     *
     * @return The section label type.
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
     * @param type The type.
     */
    public void setSectionLabelType(int type) {

        // check the argument...
        if ((type!=NO_LABELS)
            && (type!=NAME_LABELS)
            && (type!=VALUE_LABELS)
            && (type!=PERCENT_LABELS)
            && (type!=NAME_AND_VALUE_LABELS)
            && (type!=NAME_AND_PERCENT_LABELS)
            && (type!=VALUE_AND_PERCENT_LABELS))
        {

            throw new IllegalArgumentException(
                "PiePlot.setSectionLabelType(int): unrecognised type.");

        }

        // make the change...
        if (sectionLabelType!=type) {
            this.sectionLabelType = type;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the section label font.
     *
     * @return The section label font.
     */
    public Font getSectionLabelFont() {
        return this.sectionLabelFont;
    }

    /**
     * Sets the section label font.
     * <P>
     * Notifies registered listeners that the plot has been changed.
     *
     * @param font  The new section label font.
     */
    public void setSectionLabelFont(Font font) {

        // check arguments...
        if (font==null) {
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
     * @return The section label paint.
     */
    public Paint getSectionLabelPaint() {
        return this.sectionLabelPaint;
    }

    /**
     * Sets the section label paint.
     * <P>
     * Notifies registered listeners that the plot has been changed.
     *
     * @param paint  The new section label paint.
     */
    public void setSectionLabelPaint(Paint paint) {

        // check arguments...
        if (paint==null) {
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
     * @return The section label gap, measured as a percentage of the radius.
     */
    public double getSectionLabelGapPercent() {
        return this.sectionLabelGapPercent;
    }

    /**
     * Sets the section label gap percent.
     *
     * @param percent   The gap.
     */
    public void setSectionLabelGapPercent(double percent) {

        // check arguments...
        if ((percent<0.0) || (percent>MAX_SECTION_LABEL_GAP)) {
            throw new IllegalArgumentException(
                "PiePlot.setSectionLabelGapPercent(double): "
                + "percentage outside valid range.");
        }

        // make the change...
        if (this.sectionLabelGapPercent!=percent) {
            this.sectionLabelGapPercent = percent;
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
     * Sets the format string for the percent labels.
     *
     * @param format  The format.
     */
    public void setPercentFormatString(String format) {
        this.percentFormatter = new DecimalFormat(format);
    }

    /**
     * Returns the dataset for the plot, cast as a PieDataset.
     * <P>
     * Provided for convenience.
     *
     * @return The dataset for the plot, cast as a PieDataset.
     */
    public PieDataset getPieDataset() {
        return (PieDataset)dataset;
    }

    /**
     * Returns a collection of the categories in the dataset.
     *
     * @return The categories.
     */
    public Collection getCategories() {
        return getPieDataset().getCategories();
    }

    /**
     * Returns a list of labels for the legend.
     *
     * @return A list of labels.
     */
    public List getLegendItemLabels() {

        List result = new java.util.ArrayList();

        PieDataset data = getPieDataset();
        if (data!=null) {
            List categories = data.getCategories();
            Iterator iterator = categories.iterator();
            while (iterator.hasNext()) {
                result.add(iterator.next().toString());

            }
        }

        return result;

    }

    /**
     * Returns the tooltip generator (possibly null).
     *
     * @return The tool tip generator.
     */
    public PieToolTipGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }

    /**
     * Sets the tooltip generator.
     *
     * @param generator  The new tooltip generator (null permitted).
     */
    public void setToolTipGenerator(PieToolTipGenerator generator) {

        this.toolTipGenerator = generator;

    }

    /**
     * Returns the URL generator (possibly null).
     *
     * @return The URL generator.
     */
    public PieURLGenerator getURLGenerator() {
        return this.urlGenerator;
    }

    /**
     * Sets the URL generator.
     *
     * @param generator The new URL generator (null permitted).
     */
    public void setURLGenerator(PieURLGenerator generator) {
        this.urlGenerator = generator;
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  The graphics device.
     * @param plotArea  The area within which the plot should be drawn.
     * @param info  Collects info about the drawing.
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, ChartRenderingInfo info) {

        // adjust for insets...
        if (insets!=null) {
            plotArea.setRect(plotArea.getX()+insets.left,
                             plotArea.getY()+insets.top,
                             plotArea.getWidth()-insets.left-insets.right,
                             plotArea.getHeight()-insets.top-insets.bottom);
        }

        if (info!=null) {
            info.setPlotArea(plotArea);
            info.setDataArea(plotArea);
        }

        // draw the outline and background
        drawOutlineAndBackground(g2, plotArea);

        Shape savedClip = g2.getClip();
        g2.clip(plotArea);

        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.foregroundAlpha));

        if (categoryData == null || categoryData.getSeriesCount()<2) {
            drawPie(g2, plotArea, info, getPieDataset(), null);
        }
        else {
            // the columns variable is always >= rows
            int seriesCount = categoryData.getSeriesCount();
            int columns = (int)Math.ceil(Math.sqrt(seriesCount));
            int rows = (int)Math.ceil((double)seriesCount/(double)columns);

            // swap rows and columns to match plotArea shape
            if (columns > rows && plotArea.getWidth() < plotArea.getHeight()) {
                int temp = columns;
                columns = rows;
                rows = temp;
            }

            int fontHeight = g2.getFontMetrics(seriesLabelFont).getHeight();
            int x = (int)plotArea.getX();
            int y = (int)plotArea.getY();
            int width = ((int)plotArea.getWidth())/columns;
            int height = ((int)plotArea.getHeight())/rows;
            int row = 0;
            int column = 0;
            int diff = (rows * columns) - seriesCount;
            int xoffset = 0;
            Rectangle rect = new Rectangle ();

            for (int loop=0; loop < seriesCount; loop++) {
                rect.setBounds(x + xoffset + (width*column),
                               y + (height*row), width, height-fontHeight);
                drawPie(g2, rect, info, DatasetUtilities.createPieDataset(categoryData, loop),
                        categoryData.getSeriesName(loop));
                ++column;
                if (column == columns) {
                    column = 0;
                    ++row;

                    if (row == rows-1 && diff != 0) {
                        xoffset = (diff * width) / 2;
                    }
                }
            }
         }

         g2.clip(savedClip);
         g2.setComposite(originalComposite);

    }

//    /**
//     * Draws the plot on a Java 2D graphics device (such as the screen or a
//     * printer).
//     *
//     * @param g2 The graphics device.
//     * @param plotArea The area within which the plot should be drawn.
//     * @param info Collects info about the drawing.
//     */
//    public void draw(Graphics2D g2, Rectangle2D plotArea, ChartRenderingInfo info)
//    {
//
//        // adjust for insets...
//        if (insets!=null) {
//            plotArea.setRect(plotArea.getX()+insets.left,
//                             plotArea.getY()+insets.top,
//                             plotArea.getWidth()-insets.left-insets.right,
//                             plotArea.getHeight()-insets.top-insets.bottom);
//        }
//
//        if (info!=null) {
//            info.setPlotArea(plotArea);
//            info.setDataArea(plotArea);
//        }
//
//        // draw the outline and background
//        drawOutlineAndBackground(g2, plotArea);
//
//        // adjust the plot area by the interior spacing value
//        double gapHorizontal = plotArea.getWidth()*this.interiorGapPercent;
//        double gapVertical = plotArea.getHeight()*this.interiorGapPercent;
//        double pieX = plotArea.getX()+gapHorizontal/2;
//        double pieY = plotArea.getY()+gapVertical/2;
//        double pieW = plotArea.getWidth()-gapHorizontal;
//        double pieH = plotArea.getHeight()-gapVertical;
//
//        // make the pie area a square if the pie chart is to be circular...
//        if (circular) {
//            double min = Math.min(pieW, pieH)/2;
//            pieX = (pieX+pieX+pieW)/2 - min;
//            pieY = (pieY+pieY+pieH)/2 - min;
//            pieW = 2*min;
//            pieH = 2*min;
//        }
//
//        Rectangle2D explodedPieArea = new Rectangle2D.Double(pieX, pieY, pieW, pieH);
//        double explodeHorizontal = (1-radiusPercent)*pieW;
//        double explodeVertical = (1-radiusPercent)*pieH;
//        Rectangle2D pieArea = new Rectangle2D.Double(pieX+explodeHorizontal/2,
//                                                     pieY+explodeVertical/2,
//                                                     pieW-explodeHorizontal,
//                                                     pieH-explodeVertical);
//
//        // plot the data (unless the dataset is null)...
//        PieDataset data = getPieDataset();
//        if (data != null) {
//
//            Shape savedClip = g2.getClip();
//            g2.clip(plotArea);
//            Composite originalComposite = g2.getComposite();
//            g2.setComposite(AlphaComposite
//                .getInstance(AlphaComposite.SRC_OVER, this.foregroundAlpha));
//
//            double totalValue = DatasetUtilities.getPieDatasetTotal(data);
//            // For each positive value in the dataseries, compute and draw the
//            // corresponding arc.
//            double runningTotal = 0;
//            int section = 0;
//            List categories = data.getCategories();
//            Iterator iterator = categories.iterator();
//            while (iterator.hasNext()) {
//
//                Object current = iterator.next();
//                Number dataValue = data.getValue(current);
//                if (dataValue!=null) {
//                    double value = dataValue.doubleValue();
//                    if (value>0) {
//
//                        // draw the pie section...
//                        double angle1 =  startAngle
//                            + (direction*(runningTotal*360)/totalValue);
//                        double angle2 = startAngle
//                            + (direction*(runningTotal+value)*360/totalValue);
//
//                        Rectangle2D arcBounds = getArcBounds(pieArea,
//                            explodedPieArea, angle1, angle2-angle1,
//                            this.getExplodePercent(section));
//                        Arc2D.Double arc = new Arc2D.Double(arcBounds, angle1,
//                            angle2-angle1, Arc2D.PIE);
//                        runningTotal += value;
//
//                        Paint paint = this.getSeriesPaint(section);
//                        Paint outlinePaint = this.getSeriesOutlinePaint(section);
//
//                        g2.setPaint(paint);
//                        g2.fill(arc);
//                        g2.setStroke(new BasicStroke());
//                        g2.setPaint(outlinePaint);
//                        g2.draw(arc);
//
//                        // add a tooltip for the bar...
//                        if (info!=null) {
//                            if (this.toolTipGenerator==null) {
//                                toolTipGenerator =
//                                    new StandardPieToolTipGenerator();
//                            }
//                            String tip = this.toolTipGenerator.generateToolTip(data, current);
//                            String url = null;
//                            if (this.urlGenerator != null)
//                                url = this.urlGenerator.generateURL(data, current);
//                            PieSectionEntity entity =
//                                new PieSectionEntity(arc, tip, url, current, section);
//                            info.getEntityCollection().addEntity(entity);
//                        }
//
//                        // then draw the label...
//                        if (this.sectionLabelType!=NO_LABELS) {
//                            this.drawLabel(g2, pieArea, explodedPieArea, data,
//                                value, section, angle1, angle2-angle1);
//                        }
//
//                    }
//                }
//                section = section + 1;
//            }
//            if (categories.size()==0) {
//                this.drawNoDataMessage(g2, plotArea);
//            }
//            g2.clip(savedClip);
//            g2.setComposite(originalComposite);
//
//        }
//        else {
//            this.drawNoDataMessage(g2, plotArea);
//        }
//
//    }

    private void drawPie (Graphics2D g2, Rectangle2D plotArea, ChartRenderingInfo info,
                          PieDataset data, String label) {

        // adjust the plot area by the interior spacing value
        double gapHorizontal = plotArea.getWidth()*this.interiorGapPercent;
        double gapVertical = plotArea.getHeight()*this.interiorGapPercent;
        double pieX = plotArea.getX()+gapHorizontal/2;
        double pieY = plotArea.getY()+gapVertical/2;
        double pieW = plotArea.getWidth()-gapHorizontal;
        double pieH = plotArea.getHeight()-gapVertical;

        // make the pie area a square if the pie chart is to be circular...
        if (circular) {
            double min = Math.min(pieW, pieH)/2;
            pieX = (pieX+pieX+pieW)/2 - min;
            pieY = (pieY+pieY+pieH)/2 - min;
            pieW = 2*min;
            pieH = 2*min;
        }

        Rectangle2D explodedPieArea = new Rectangle2D.Double(pieX, pieY, pieW, pieH);
        double explodeHorizontal = (1-radiusPercent)*pieW;
        double explodeVertical = (1-radiusPercent)*pieH;
        Rectangle2D pieArea = new Rectangle2D.Double(pieX+explodeHorizontal/2,
                                                     pieY+explodeVertical/2,
                                                     pieW-explodeHorizontal,
                                                     pieH-explodeVertical);

        // plot the data (unless the dataset is null)...
        if (data!=null) {

            // get a list of categories...
            List categories = data.getCategories();

            // compute the total value of the data series skipping over the negative values
            double totalValue = DatasetUtilities.getPieDatasetTotal(data);

            // For each positive value in the dataseries, compute and draw the corresponding arc.
            double runningTotal = 0;
            int section = 0;
            Iterator iterator = categories.iterator();
            while (iterator.hasNext()) {

                Object current = iterator.next();
                Number dataValue = data.getValue(current);
                if (dataValue!=null) {
                    double value = dataValue.doubleValue();
                    if (value>0) {

                        // draw the pie section...
                        double angle1 = startAngle+(direction*(runningTotal*360)/totalValue);
                        double angle2 = startAngle+(direction*(runningTotal+value)*360/totalValue);

                        Rectangle2D arcBounds = getArcBounds(pieArea, explodedPieArea,
                                                             angle1, angle2-angle1,
                                                             this.getExplodePercent(section));
                        Arc2D.Double arc = new Arc2D.Double(arcBounds, angle1, angle2-angle1,
                                                            Arc2D.PIE);
                        runningTotal += value;

                        Paint paint = this.getSeriesPaint(section);
                        Paint outlinePaint = this.getSeriesOutlinePaint(section);
                        Stroke outlineStroke = this.getSeriesOutlineStroke(section);

                        g2.setPaint(paint);
                        g2.fill(arc);
                        g2.setStroke(outlineStroke);
                        g2.setPaint(outlinePaint);
                        g2.draw(arc);

                        // add a tooltip for the bar...
                        if (info!=null) {
                            if (this.toolTipGenerator==null) {
                                toolTipGenerator = new StandardPieToolTipGenerator();
                            }
                            String tip = this.toolTipGenerator.generateToolTip(data, current);
                            String url = null;
                            if (this.urlGenerator!=null) {
                                url = this.urlGenerator.generateURL(data, current);
                            }
                            PieSectionEntity entity = new PieSectionEntity(arc, tip, url, current,
                                                                           section);
                            info.getEntityCollection().addEntity(entity);
                        }

                        // then draw the label...
                        if (this.sectionLabelType!=NO_LABELS) {
                            this.drawLabel(g2, pieArea, explodedPieArea, data, value,
                                           section, angle1, angle2-angle1);
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
                double labelX = pieX + (pieW/2) - (bounds.getWidth()/2);
                double labelY = pieY + pieH + bounds.getHeight();
                g2.drawString(label, (int)labelX, (int)labelY);
            }
        }
    }

    /**
     * Draws the label for one pie section.  You can set the plot up for
     * different types of labels using the setSectionLabelType() method.
     *
     * @param g2  The graphics device.
     * @param pieArea  The area for the unexploded pie sections.
     * @param explodedPieArea  The area for the exploded pie section.
     * @param data  The data for the plot.
     * @param value  The value of the label.
     * @param section  The section (zero-based index).
     * @param startAngle  The starting angle.
     * @param extent  The extent of the arc.
     */
    protected void drawLabel(Graphics2D g2,
                             Rectangle2D pieArea, Rectangle2D explodedPieArea,
                             PieDataset data, double value,
                             int section, double startAngle, double extent) {

        // handle label drawing...
        FontRenderContext frc = g2.getFontRenderContext();
        List legendItemLabels = getLegendItemLabels();
        String label = "";
        if (this.sectionLabelType==NAME_LABELS) {
            label = legendItemLabels.get(section).toString();
        }
        else if (this.sectionLabelType==VALUE_LABELS) {
            label = valueFormatter.format(value);
        }
        else if (this.sectionLabelType==PERCENT_LABELS) {
            label = percentFormatter.format(extent/3.60*this.direction)+"%";
        }
        else if (this.sectionLabelType==NAME_AND_VALUE_LABELS) {
            label = legendItemLabels.get(section).toString()
                + " (" + valueFormatter.format(value) + ")";
        }
        else if (this.sectionLabelType==NAME_AND_PERCENT_LABELS) {
            label = legendItemLabels.get(section).toString()
                + " (" + percentFormatter.format(extent/3.60*this.direction) + "%)";
        }
        else if (this.sectionLabelType==VALUE_AND_PERCENT_LABELS) {
            label = valueFormatter.format(value)
                + " (" + percentFormatter.format(extent/3.60*this.direction) + "%)";
        }
        Rectangle2D labelBounds = this.sectionLabelFont.getStringBounds(label, frc);
        LineMetrics lm = this.sectionLabelFont.getLineMetrics(label, frc);
        double ascent = lm.getAscent();
        Point2D labelLocation = this.calculateLabelLocation(labelBounds, ascent,
            pieArea, explodedPieArea, startAngle, extent,
            this.getExplodePercent(section));

        g2.setPaint(this.sectionLabelPaint);
        g2.setFont(this.sectionLabelFont);
        g2.drawString(label, (float)labelLocation.getX(), (float)labelLocation.getY());

    }

    /**
     * Returns a short string describing the type of plot.
     *
     * @return The plot type.
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
     * @param percent  The zoom percentage.
     */
    public void zoom(double percent) {
    }

    /**
     * Returns a rectangle that can be used to create a pie section (taking
     * into account the amount by which the pie section is 'exploded').
     *
     * @param unexploded  The area inside which the unexploded pie sections are drawn.
     * @param exploded  The area inside which the exploded pie sections are drawn.
     * @param startAngle  The start angle.
     * @param extent  The extent of the arc.
     * @param explodePercent  The amount by which the pie section is exploded.
     *
     * @return a rectangle that can be used to create a pie section.
     */
    protected Rectangle2D getArcBounds(Rectangle2D unexploded, Rectangle2D exploded,
                                       double startAngle, double extent, double explodePercent) {

        if (explodePercent==0.0) {
            return unexploded;
        }
        else {
            Arc2D arc1 = new Arc2D.Double(unexploded, startAngle, extent/2, Arc2D.OPEN);
            Point2D point1 = arc1.getEndPoint();
            Arc2D.Double arc2 = new Arc2D.Double(exploded, startAngle, extent/2, Arc2D.OPEN);
            Point2D point2 = arc2.getEndPoint();
            double deltaX = (point1.getX()-point2.getX())*explodePercent;
            double deltaY = (point1.getY()-point2.getY())*explodePercent;
            return new Rectangle2D.Double(unexploded.getX()-deltaX,
                                          unexploded.getY()-deltaY,
                                          unexploded.getWidth(),
                                          unexploded.getHeight());

        }
    }

    /**
     * Returns the location for a label, taking into account whether or not the
     * pie section is exploded.
     *
     * @param labelBounds  The label bounds.
     * @param ascent  The ascent.
     * @param unexploded  The area within which the unexploded pie sections are drawn.
     * @param exploded  The area within which the exploded pie sections are drawn.
     * @param startAngle  The start angle for the pie section.
     * @param extent  The extent of the arc.
     * @param explodePercent  The amount by which the pie section is exploded.
     *
     * @return the location for a label.
     */
    protected Point2D calculateLabelLocation(Rectangle2D labelBounds,
        double ascent, Rectangle2D unexploded, Rectangle2D exploded,
        double startAngle, double extent, double explodePercent) {

        Arc2D arc1 = new Arc2D.Double(unexploded, startAngle, extent/2, Arc2D.OPEN);
        Point2D point1 = arc1.getEndPoint();
        Arc2D.Double arc2 = new Arc2D.Double(exploded, startAngle, extent/2, Arc2D.OPEN);
        Point2D point2 = arc2.getEndPoint();
        double deltaX = (point1.getX()-point2.getX())*explodePercent;
        deltaX = deltaX - (point1.getX()-unexploded.getCenterX()) * sectionLabelGapPercent;
        double deltaY = (point1.getY()-point2.getY())*explodePercent;
        deltaY = deltaY - (point1.getY()-unexploded.getCenterY()) * sectionLabelGapPercent;

        double labelX = point1.getX()-deltaX;
        double labelY = point1.getY()-deltaY;

        if (labelX <= unexploded.getCenterX())
            labelX -= labelBounds.getWidth();
        if (labelY > unexploded.getCenterY())
            labelY +=ascent;

        return new Point2D.Double(labelX, labelY);

    }

}
