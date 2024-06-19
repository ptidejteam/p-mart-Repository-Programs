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
 * ------------
 * PiePlot.java
 * ------------
 * (C) Copyright 2000-2002, by Andrzej Porebski and Contributors.
 *
 * Original Author:  Andrzej Porebski;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *                   Martin Cordova (percentages in labels);
 *
 * $Id: PiePlot.java,v 1.1 2007/10/10 18:56:25 vauchers Exp $
 *
 * Changes (from 21-Jun-2001)
 * --------------------------
 * 21-Jun-2001 : Removed redundant JFreeChart parameter from constructors (DG);
 * 18-Sep-2001 : Updated e-mail address (DG);
 * 15-Oct-2001 : Data source classes moved to com.jrefinery.data.* (DG);
 * 19-Oct-2001 : Moved series paint and stroke methods from JFreeChart.java to Plot.java (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 13-Nov-2001 : Modified plot subclasses so that null axes are possible for pie plot (DG);
 * 17-Nov-2001 : Added PieDataset interface and amended this class accordingly, and completed
 *               removal of BlankAxis class as it is no longer required (DG);
 * 19-Nov-2001 : Changed 'drawCircle' property to 'circular' property (DG);
 * 21-Nov-2001 : Added options for exploding pie sections and filled out range of properties (DG);
 *               Added option for percentages in chart labels, based on code by Martin Cordova (DG);
 * 30-Nov-2001 : Changed default font from "Arial" --> "SansSerif" (DG);
 * 12-Dec-2001 : Removed unnecessary 'throws' clause in constructor (DG);
 * 13-Dec-2001 : Added tooltips (DG);
 * 16-Jan-2002 : Renamed tooltips class (DG);
 * 22-Jan-2002 : Fixed bug correlating legend labels with pie data (DG);
 * 05-Feb-2002 : Added alpha-transparency to plot class, and updated constructors accordingly (DG);
 * 06-Feb-2002 : Added optional background image and alpha-transparency to Plot and subclasses.
 *               Clipped drawing within plot area (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.text.*;
import java.util.*;
import com.jrefinery.data.PieDataset;
import com.jrefinery.chart.event.*;
import com.jrefinery.chart.tooltips.*;

/**
 * A plot that displays data in the form of a pie chart, using data from any class that implements
 * the PieDataset interface.
 * <P>
 * Notes:
 * (1) negative values in the dataset are ignored;
 * (2) vertical axis and horizontal axis are set to null;
 * (3) there are utility methods for creating a PieDataset from a CategoryDataset;
 * @see Plot
 * @see PieDataset
 */
public class PiePlot extends Plot {

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

    protected DecimalFormat valueFormatter;
    protected DecimalFormat percentFormatter;

    /** The tool tip generator. */
    protected PieToolTipGenerator toolTipGenerator;

    /**
     * Constructs a new pie plot, using default attributes as required.
     */
    public PiePlot() {

        this(Plot.DEFAULT_INSETS,
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
             null);

    }

    /**
     * Constructs a pie plot.
     * @param insets Amount of blank space around the plot area.
     * @param backgroundPaint An optional color for the plot's background.
     * @param backgroundImage An optional image for the plot's background.
     * @param backgroundAlpha Alpha-transparency for the plot's background.
     * @param outlineStroke The Stroke used to draw an outline around the plot.
     * @param outlinePaint The color used to draw an outline around the plot.
     * @param foregroundAlpha The alpha-transparency for the plot.
     * @param interiorGapPercent The interior gap (space for labels) as a percentage of the
     *        available space.
     * @param circular Flag indicating whether the pie chart is circular or elliptical.
     * @param radiusPercent The radius of the pie chart, as a percentage of the available space
     *        (after accounting for interior gap).
     * @param sectionLabelFont The font for the section labels.
     * @param sectionLabelPaint The color for the section labels.
     * @param sectionLabelGapPercent The space between the pie sections and the labels.
     */
    public PiePlot(Insets insets,
                   Paint backgroundPaint, Image backgroundImage, float backgroundAlpha,
                   Stroke outlineStroke, Paint outlinePaint,
                   float foregroundAlpha,
                   double interiorGapPercent, boolean circular, double radiusPercent,
                   int sectionLabelType,
                   Font sectionLabelFont, Paint sectionLabelPaint, double sectionLabelGapPercent,
                   String valueFormatString, String percentFormatString,
                   PieToolTipGenerator tooltipGenerator) {

	super(null, null,
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
        this.toolTipGenerator = tooltipGenerator;
        setInsets(insets);

    }

    /**
     * Returns the interior gap, measures as a percentage of the available drawing space.
     * @return The interior gap, measured as a percentage of the available drawing space.
     */
    public double getInteriorGapPercent() {
        return this.interiorGapPercent;
    }

    /**
     * Sets the interior gap percent.
     */
    public void setInteriorGapPercent(double percent) {

        // check arguments...
        if ((percent<0.0) || (percent>MAX_INTERIOR_GAP)) {
            throw new IllegalArgumentException("PiePlot.setInteriorGapPercent(double): "
                                               +"percentage outside valid range.");
        }

        // make the change...
        if (this.interiorGapPercent!=percent) {
            this.interiorGapPercent = percent;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns a flag indicating whether the pie chart is circular, or stretched into an elliptical
     * shape.
     * @return A flag indicating whether the pie chart is circular.
     */
    public boolean isCircular() {
	return circular;
    }

    /**
     * A flag indicating whether the pie chart is circular, or stretched into an elliptical shape.
     * @param flag The new value.
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
     * @return The radius percentage.
     */
    public double getRadiusPercent() {
        return this.radiusPercent;
    }

    /**
     * Sets the radius percentage.
     * @param percent The new value.
     */
    public void setRadiusPercent(double percent) {

        // check arguments...
        if ((percent<=0.0) || (percent>MAX_RADIUS)) {
            throw new IllegalArgumentException("PiePlot.setRadiusPercent(double): "
                                               +"percentage outside valid range.");
        }

        // make the change (if necessary)...
        if (this.radiusPercent!=percent) {
            this.radiusPercent = percent;
            this.notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the section label type.  Defined by the constants: NO_LABELS, NAME_LABELS,
     * PERCENT_LABELS and NAME_AND_PERCENT_LABELS.
     * @return The section label type.
     */
    public int getSectionLabelType() {
        return this.sectionLabelType;
    }

    /**
     * Sets the section label type.
     * <P>
     * Valid types are defined by the following constants: NO_LABELS, NAME_LABELS, PERCENT_LABELS,
     *                                                     NAME_AND_PERCENT_LABELS.
     */
    public void setSectionLabelType(int type) {

        // check the argument...
        if ((type!=NO_LABELS)
            && (type!=NAME_LABELS)
            && (type!=VALUE_LABELS)
            && (type!=PERCENT_LABELS)
            && (type!=NAME_AND_VALUE_LABELS)
            && (type!=NAME_AND_PERCENT_LABELS)
            && (type!=VALUE_AND_PERCENT_LABELS)) {

            throw new IllegalArgumentException("PiePlot.setSectionLabelType(int): "
                                               +"unrecognised type.");

        }

        // make the change...
        if (sectionLabelType!=type) {
            this.sectionLabelType = type;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the section label font.
     * @return The section label font.
     */
    public Font getSectionLabelFont() {
	return this.sectionLabelFont;
    }

    /**
     * Sets the section label font.
     * <P>
     * Notifies registered listeners that the plot has been changed.
     * @param font The new section label font.
     */
    public void setSectionLabelFont(Font font) {

        // check arguments...
        if (font==null) {
            throw new IllegalArgumentException("PiePlot.setSectionLabelFont(...): "
                                               +"null font not allowed.");
        }

        // make the change...
        if (!this.sectionLabelFont.equals(font)) {
	    this.sectionLabelFont = font;
	    notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the section label paint.
     * @return The section label paint.
     */
    public Paint getSectionLabelPaint() {
	return this.sectionLabelPaint;
    }

    /**
     * Sets the section label paint.
     * <P>
     * Notifies registered listeners that the plot has been changed.
     * @param paint The new section label paint.
     */
    public void setSectionLabelPaint(Paint paint) {

        // check arguments...
        if (paint==null) {
            throw new IllegalArgumentException("PiePlot.setSectionLabelPaint(...): "
                                               +"null paint not allowed.");
        }

        // make the change...
        if (!this.sectionLabelPaint.equals(paint)) {
	    this.sectionLabelPaint = paint;
	    notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the section label gap, measures as a percentage of the radius.
     * @return The section label gap, measures as a percentage of the radius.
     */
    public double getSectionLabelGapPercent() {
        return this.sectionLabelGapPercent;
    }

    /**
     * Sets the section label gap percent.
     */
    public void setSectionLabelGapPercent(double percent) {

        // check arguments...
        if ((percent<0.0) || (percent>MAX_SECTION_LABEL_GAP)) {
            throw new IllegalArgumentException("PiePlot.setSectionLabelGapPercent(double): "
                                               +"percentage outside valid range.");
        }

        // make the change...
        if (this.sectionLabelGapPercent!=percent) {
            this.sectionLabelGapPercent = percent;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Sets the format string for the value labels.
     */
    public void setValueFormatString(String format) {
        this.valueFormatter = new DecimalFormat(format);
    }

    /**
     * Sets the format string for the percent labels.
     */
    public void setPercentFormatString(String format) {
        this.percentFormatter = new DecimalFormat(format);
    }

    /**
     * Returns the amount that a section should be 'exploded'.
     * <P>
     */
    public double getExplodePercent(int section) {

        // check argument...
        if (section<0) {
            throw new IllegalArgumentException("PiePlot.getExplodePercent(int): "
                                               +"section outside valid range.");
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
     */
    public void setExplodePercent(int section, double percent) {

        // check argument...
        if ((section<0) || (section>=this.getDataset().getCategories().size())) {
            throw new IllegalArgumentException("PiePlot.setExplodePercent(int, double): "
                                               +"section outside valid range.");
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
            explodePercentages = new double[this.getDataset().getCategories().size()];
            explodePercentages[section] = percent;
        }

    }

    /**
     * Returns the dataset for the plot, cast as a PieDataset.
     * <P>
     * Provided for convenience.
     * @return The dataset for the plot, cast as a PieDataset.
     */
    public PieDataset getDataset() {
	return (PieDataset)chart.getDataset();
    }

    /**
     * Returns a collection of the categories in the dataset.
     * @return A collection of the categories in the dataset.
     */
    public Collection getCategories() {
	return getDataset().getCategories();
    }

    /**
     * Returns the tooltip generator (possibly null).
     */
    public PieToolTipGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }

    /**
     * Sets the tooltip generator.
     * @param generator The new tooltip generator (null permitted).
     */
    public void setToolTipGenerator(PieToolTipGenerator generator) {

        this.toolTipGenerator = generator;

    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     * @param g2 The graphics device.
     * @param plotArea The area within which the plot should be drawn.
     * @param info Collects info about the drawing.
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, DrawInfo info) {

        ToolTipsCollection tooltips = null;
        if (info!=null) {
            info.setPlotArea(plotArea);
            tooltips = info.getToolTipsCollection();
        }

	// adjust for insets...
	if (insets!=null) {
	    plotArea.setRect(plotArea.getX()+insets.left,
                             plotArea.getY()+insets.top,
			     plotArea.getWidth()-insets.left-insets.right,
			     plotArea.getHeight()-insets.top-insets.bottom);
	}

	// draw the outline and background
	drawOutlineAndBackground(g2, plotArea);

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
	PieDataset data = (PieDataset)chart.getDataset();
	if (data != null) {

            Shape savedClip = g2.getClip();
            g2.clip(plotArea);
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                       this.foregroundAlpha));

            // get a sorted collection of categories...
            Set categories = data.getCategories();
            SortedSet ss = new TreeSet(categories);

            // compute the total value of the data series skipping over the negative values
            double totalValue = 0;
            Iterator iterator = ss.iterator();
            while (iterator.hasNext()) {
                Object current = iterator.next();
                if (current!=null) {
                    Number value = data.getValue(current);
                    double v = value.doubleValue();
                    if (v>0) {
                        totalValue = totalValue + v;
                    }
                }
            }

            // For each positive value in the dataseries, compute and draw the corresponding arc.
            double sumTotal = 0;
            int section = 0;
            iterator = ss.iterator();
            while (iterator.hasNext()) {

                Object current = iterator.next();
                Number dataValue = data.getValue(current);
                if (dataValue!=null) {
                    double value = dataValue.doubleValue();
                    if (value>0) {

                        // draw the pie section...
                        double startAngle = sumTotal * 360 / totalValue;
                        double extent = (sumTotal+value) * 360 / totalValue - startAngle;

                        Rectangle2D arcBounds = getArcBounds(pieArea, explodedPieArea,
                                                             startAngle, extent,
                                                             this.getExplodePercent(section));
                        Arc2D.Double arc = new Arc2D.Double(arcBounds, startAngle, extent,
                                                            Arc2D.PIE);
                        sumTotal += value;

                        Paint paint = this.getSeriesPaint(section);
                        Paint outlinePaint = this.getSeriesOutlinePaint(section);

                        g2.setPaint(paint);
                        g2.fill(arc);
                        g2.setStroke(new BasicStroke());
                        g2.setPaint(outlinePaint);
                        g2.draw(arc);
                        // add a tooltip for the bar...
                        if (tooltips!=null) {
                            if (this.toolTipGenerator==null) {
                                toolTipGenerator = new StandardPieToolTipGenerator();
                            }
                            String tip = this.toolTipGenerator.generateToolTip(data, current);
                            if (arc!=null) {
                                tooltips.addToolTip(tip, arc);
                            }
                        }


                        // then draw the label...
                        if (this.sectionLabelType!=NO_LABELS) {
                            this.drawLabel(g2, pieArea, explodedPieArea, data, value,
                                           section, startAngle, extent);
                        }

                    }
                }
                section = section + 1;
            }
            g2.clip(savedClip);
            g2.setComposite(originalComposite);

        }
    }

    /**
     * Draws the label for one pie section.  You can set the plot up for different types of labels
     * using the setSectionLabelType() method.  Available types are: NO_LABELS, NAME_LABELS,
     * PERCENT_LABELS and NAME_AND_PERCENT_LABELS.
     *
     * @param g2 The graphics device.
     * @param pieArea The area for the unexploded pie sections.
     * @param explodedPieArea The area for the exploded pie section.
     * @param data The data for the plot.
     * @param section The section (zero-based index).
     * @param startAngle The starting angle.
     * @param extent The extent of the arc.
     */
    protected void drawLabel(Graphics2D g2, Rectangle2D pieArea, Rectangle2D explodedPieArea,
                             PieDataset data, double value, int section,
                             double startAngle, double extent) {

        // handle label drawing...
        FontRenderContext frc = g2.getFontRenderContext();
        String[] legendItemLabels = chart.getLegendItemLabels();
        String label = "";
        if (this.sectionLabelType==NAME_LABELS) {
            label = legendItemLabels[section];
        }
        else if (this.sectionLabelType==VALUE_LABELS) {
            label = valueFormatter.format(value);
        }
        else if (this.sectionLabelType==PERCENT_LABELS) {
            label = percentFormatter.format(extent/3.60)+"%";
        }
        else if (this.sectionLabelType==NAME_AND_VALUE_LABELS) {
            label = legendItemLabels[section]+" ("+valueFormatter.format(value)+")";
        }
        else if (this.sectionLabelType==NAME_AND_PERCENT_LABELS) {
            label = legendItemLabels[section]+" ("+percentFormatter.format(extent/3.60)+"%)";
        }
        else if (this.sectionLabelType==VALUE_AND_PERCENT_LABELS) {
            label = valueFormatter.format(value)+" ("+percentFormatter.format(extent/3.60)+"%)";
        }
        Rectangle2D labelBounds = this.sectionLabelFont.getStringBounds(label, frc);
        LineMetrics lm = this.sectionLabelFont.getLineMetrics(label, frc);
        double ascent = lm.getAscent();
        Point2D labelLocation = this.calculateLabelLocation(labelBounds, ascent,
                                                            pieArea, explodedPieArea,
                                                            startAngle, extent,
                                                            this.getExplodePercent(section));

        g2.setPaint(this.sectionLabelPaint);
        g2.setFont(this.sectionLabelFont);
        g2.drawString(label, (float)labelLocation.getX(), (float)labelLocation.getY());

    }

    /**
     * Returns a short string describing the type of plot.
     */
    public String getPlotType() {
	return "Pie Plot";
    }

    /**
     * Returns true if the axis is compatible with the pie plot, and false otherwise.  Since a pie
     * plot requires no axes, only a null axis is compatible.
     * @param axis The axis.
     */
    public boolean isCompatibleHorizontalAxis(Axis axis) {
	if (axis==null) return true;
	else return false;
    }

    /**
     * Returns true if the axis is compatible with the pie plot, and false otherwise.  Since a pie
     * plot requires no axes, only a null axis is compatible.
     * @param axis The axis.
     */
    public boolean isCompatibleVerticalAxis(Axis axis) {
	if (axis==null) return true;
	else return false;
    }

    /**
     * Returns a rectangle that can be used to create a pie section (taking into account the
     * amount by which the pie section is 'exploded').
     * @param unexploded The area inside which the unexploded pie sections are drawn.
     * @param exploded The area inside which the exploded pie sections are drawn.
     * @param startAngle The start angle.
     * @param extent The extent of the arc.
     * @param explodePercent The amount by which the pie section is exploded.
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
            return new Rectangle2D.Double(unexploded.getX()-deltaX, unexploded.getY()-deltaY,
                                          unexploded.getWidth(), unexploded.getHeight());

        }
    }

    /**
     * Returns the location for a label, taking into account whether or not the pie section is
     * exploded.
     * @param labelBounds The label bounds.
     * @param ascent The ascent.
     * @param unexploded The area within which the unexploded pie sections are drawn.
     * @param exploded The area within which the exploded pie sections are drawn.
     * @param startAngle The start angle for the pie section.
     * @param extent The extent of the arc.
     * @param explodePercent The amount by which the pie section is exploded.
     */
    protected Point2D calculateLabelLocation(Rectangle2D labelBounds, double ascent,
                                             Rectangle2D unexploded, Rectangle2D exploded,
                                             double startAngle, double extent,
                                             double explodePercent) {

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
