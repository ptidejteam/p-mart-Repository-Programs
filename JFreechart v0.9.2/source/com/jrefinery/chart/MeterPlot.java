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
 * --------------
 * MeterPlot.java
 * --------------
 * (C) Copyright 2000-2002, by Hari and Contributors.
 *
 * Original Author:  Hari (ourhari@hotmail.com);
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: MeterPlot.java,v 1.1 2007/10/10 19:41:58 vauchers Exp $
 *
 * Changes
 * -------
 * 01-Apr-2002 : Version 1, contributed by Hari (DG);
 * 23-Apr-2002 : Moved dataset from JFreeChart to Plot (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Shape;
import java.awt.Polygon;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.List;
import com.jrefinery.data.MeterDataset;
import com.jrefinery.chart.event.*;
import com.jrefinery.chart.tooltips.*;

/**
 * A plot that displays a single value in the context of a range of levels (normal, warning and
 * critical).
 */
public class MeterPlot extends Plot {

    /** The default text for the critical level. */
    public static final String CRITICAL_TEXT = "Critical";

    /** The default text for the warning level. */
    public static final String WARNING_TEXT = "Warning";

    /** The default text for the normal level. */
    public static final String NORMAL_TEXT = "Normal";

    static final Color DEFAULT_NORMAL_COLOR = Color.green;

    static final Color DEFAULT_WARNING_COLOR = Color.yellow;

    static final Color DEFAULT_CRITICAL_COLOR = Color.red;

    public static final int DEFAULT_METER_ANGLE = 270;
    public static final float DEFAULT_BORDER_SIZE = 3f;
    public static final float DEFAULT_CIRCLE_SIZE = 10f;
    public static final Color DEFAULT_BACKGROUND_COLOR = Color.green;


    public static final Font DEFAULT_LABEL_FONT = new Font("SansSerif", Font.BOLD, 10);

    public static final int NO_LABELS = 0;

    public static final int VALUE_LABELS = 1;

    /** Label type (NO_LABELS, VALUE_LABELS). */
    protected int labelType;

    protected Font labelFont;

    protected int meterCalcAngle = -1;

    protected double meterRange = -1;

    protected int meterAngle = DEFAULT_METER_ANGLE;

    protected double minMeterValue = 0.0;

    protected TickUnits tickUnits = null;

    protected NumberTickUnit tickUnit = null;

    Color colorNormal = DEFAULT_NORMAL_COLOR;
    Color colorCritical = DEFAULT_CRITICAL_COLOR;
    Color colorWarning = DEFAULT_WARNING_COLOR;

    public static final int DIALTYPE_PIE = 0;
    public static final int DIALTYPE_CIRCLE = 1;
    public static final int DIALTYPE_CHORD = 2;
    protected int dialType = DIALTYPE_CIRCLE;

    protected Color dialBorderColor;
    protected boolean drawBorder;

    /**
     * Default constructor.
     */
    public MeterPlot(MeterDataset data) {

        this(data,
             Plot.DEFAULT_INSETS,
             Plot.DEFAULT_BACKGROUND_PAINT,
             null, // background image
             Plot.DEFAULT_BACKGROUND_ALPHA,
             Plot.DEFAULT_OUTLINE_STROKE,
             Plot.DEFAULT_OUTLINE_PAINT,
             Plot.DEFAULT_FOREGROUND_ALPHA,
             VALUE_LABELS,
             DEFAULT_LABEL_FONT);

    }

    /**
     * Constructs a new meter plot.
     *
     * @param insets The plot insets.
     * @param backgroundPaint The background color.
     * @param backgroundImage The background image.
     * @param backgroundAlpha The background alpha-transparency.
     * @param outlineStroke The outline stroke.
     * @param outlinePaint The outline paint.
     * @param foregroundAlpha The foreground alpha-transparency.
     * @param labelType The label type.
     * @param labelFont The label font.
     *
     */
    public MeterPlot(MeterDataset data,
                     Insets insets,
                     Paint backgroundPaint, Image backgroundImage, float backgroundAlpha,
                     Stroke outlineStroke, Paint outlinePaint,
                     float foregroundAlpha,
                     int labelType,
                     Font labelFont) {

        super(data,
              insets,
              backgroundPaint, backgroundImage, backgroundAlpha,
              outlineStroke, outlinePaint, foregroundAlpha);

        this.labelType = labelType;
        this.labelFont = labelFont;
        setInsets(insets);

    }

    public List getLegendItemLabels() {
        return null;
    }

    public Color getNormalColor() { return colorNormal; }

    public Color getWarningColor() { return colorWarning; }

    public Color getCriticalColor() { return colorCritical; }

    public void setWarningColor( Color color) {
        this.colorWarning = color == null ? DEFAULT_WARNING_COLOR : color;
    }

    public void setCriticalColor( Color color) {
        this.colorCritical = color == null ? DEFAULT_CRITICAL_COLOR : color;
    }

    public void setNormalColor( Color color) {
        this.colorNormal = color == null ? DEFAULT_NORMAL_COLOR : color;
    }

    public int getMeterAngle() {
        return this.meterAngle;
    }

    public void setMeterAngle(int angle) {
        this.meterAngle = angle;
    }

    public int getDialType() {
        return this.dialType;
    }

    public void setDialType( int type) {
        this.dialType = type;
    }

    public Color getDialBorderColor() {
        return this.dialBorderColor;
    }

    public void setDialBorderColor( Color color) {
        this.dialBorderColor = color;
    }

    public boolean getDrawBorder() {
        return this.drawBorder;
    }

    public void setDrawBorder( boolean draw) {
        this.drawBorder = draw;
    }

    /**
     * Returns the label type.  Defined by the constants: NO_LABELS, VALUE_LABELS.
     *
     * @return The label type.
     */
    public int getLabelType() {
        return this.labelType;
    }

    /**
     * Sets the label type.
     * <P>
     * Valid types are defined by the following constants: NO_LABELS, VALUE_LABELS.
     */
    public void setLabelType(int type) {

        // check the argument...
        if ((type!=NO_LABELS) && (type!=VALUE_LABELS)) {
            throw new IllegalArgumentException("MeterPlot.setLabelType(int): unrecognised type.");
        }

        // make the change...
        if (labelType!=type) {
            this.labelType = type;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the label font.
     *
     * @return The label font.
     */
    public Font getLabelFont() {
        return this.labelFont;
    }

    /**
     * Sets the label font.
     * <P>
     * Notifies registered listeners that the plot has been changed.
     * @param font The new label font.
     */
    public void setLabelFont(Font font) {

        // check arguments...
        if (font==null) {
            throw new IllegalArgumentException("MeterPlot.setLabelFont(...): "+
                                               "null font not allowed.");
        }

        // make the change...
        if (!this.labelFont.equals(font)) {
            this.labelFont = font;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the dataset for the plot, cast as a MeterDataset.
     * <P>
     * Provided for convenience.
     * @return The dataset for the plot, cast as a MeterDataset.
     */
    public MeterDataset getMeterDataset() {
        return (MeterDataset)dataset;
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2 The graphics device.
     * @param plotArea The area within which the plot should be drawn.
     * @param info Collects info about the drawing.
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, ChartRenderingInfo info) {

//        ToolTipsCollection tooltips = null;
        if (info!=null) {
            info.setPlotArea(plotArea);
//            tooltips = info.getToolTipsCollection();
        }

        // adjust for insets...
        if (insets!=null) {
            plotArea.setRect(plotArea.getX()+insets.left,
                             plotArea.getY()+insets.top,
            plotArea.getWidth()-insets.left-insets.right,
            plotArea.getHeight()-insets.top-insets.bottom);
        }

        plotArea.setRect(plotArea.getX()+4,
                         plotArea.getY()+4,
                         plotArea.getWidth()-8,
                         plotArea.getHeight()-8);

        // draw the outline and background
        if (drawBorder) {
            drawOutlineAndBackground(g2, plotArea);
        }

        // adjust the plot area by the interior spacing value
        double gapHorizontal = (2 * DEFAULT_BORDER_SIZE);
        double gapVertical = (2 * DEFAULT_BORDER_SIZE);
        double meterX = plotArea.getX()+gapHorizontal/2;
        double meterY = plotArea.getY()+gapVertical/2;
        double meterW = plotArea.getWidth()-gapHorizontal;
        double meterH = plotArea.getHeight()-gapVertical +
                        (meterAngle <= 180 && dialType != DIALTYPE_CIRCLE ? plotArea.getHeight()/1.25 : 0);

        {
            double min = Math.min(meterW, meterH)/2;
            meterX = (meterX+meterX+meterW)/2 - min;
            meterY = (meterY+meterY+meterH)/2 - min;
            meterW = 2*min;
            meterH = 2*min;
        }

        Rectangle2D meterArea = new Rectangle2D.Double(meterX,
                                                       meterY,
                                                       meterW,
                                                       meterH);

        Rectangle2D.Double originalArea = new Rectangle2D.Double(
                    meterArea.getX()-4, meterArea.getY()-4,
                    meterArea.getWidth()+8, meterArea.getHeight()+8 );

        double meterMiddleX = meterArea.getCenterX();
        double meterMiddleY = meterArea.getCenterY();

        // plot the data (unless the dataset is null)...
        MeterDataset data = getMeterDataset();
        if (data != null) {
            double dataMin = data.getMinimumValue().doubleValue();
            double dataMax = data.getMaximumValue().doubleValue();
            minMeterValue = dataMin;

            meterCalcAngle = 180 + ((meterAngle - 180) / 2);
            meterRange = dataMax - dataMin;

            Shape savedClip = g2.getClip();
            g2.clip(originalArea);
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                       this.foregroundAlpha));

            drawArc(g2, originalArea, dataMin, dataMax, Color.black, 1);
            drawTicks(g2, meterArea, dataMin, dataMax);
            drawArcFor(g2, meterArea, data, MeterDataset.FULL_DATA);
            drawArcFor(g2, meterArea, data, MeterDataset.NORMAL_DATA);
            drawArcFor(g2, meterArea, data, MeterDataset.WARNING_DATA);
            drawArcFor(g2, meterArea, data, MeterDataset.CRITICAL_DATA);

            if (data.isValueValid()) {
                double dataVal = data.getValue().doubleValue();
                drawTick( g2, meterArea, dataVal, true, Color.cyan, true, data.getUnits());

                g2.setColor(Color.green);
                g2.setStroke(new BasicStroke(2.0f));

                double radius = (meterArea.getWidth() / 2) + DEFAULT_BORDER_SIZE + 15;
                double valueAngle = calculateAngle( dataVal);
                double valueP1 = meterMiddleX + (radius * Math.cos(Math.PI * (valueAngle/180)));
                double valueP2 = meterMiddleY - (radius * Math.sin(Math.PI * (valueAngle/180)));

                Polygon arrow = new Polygon();
                if ((valueAngle > 135 && valueAngle < 225) ||
                        (valueAngle < 45 && valueAngle > -45)) {
                    double valueP3 = (meterMiddleY-DEFAULT_CIRCLE_SIZE/4);
                    double valueP4 = (meterMiddleY+DEFAULT_CIRCLE_SIZE/4);
                    arrow.addPoint( (int) meterMiddleX, (int) valueP3);
                    arrow.addPoint( (int) meterMiddleX, (int) valueP4);
                }
                                else {
                    arrow.addPoint((int)(meterMiddleX-DEFAULT_CIRCLE_SIZE/4), (int) meterMiddleY);
                    arrow.addPoint((int)(meterMiddleX+DEFAULT_CIRCLE_SIZE/4), (int) meterMiddleY);
                }
                arrow.addPoint( (int) valueP1, (int) valueP2);

                Ellipse2D circle = new Ellipse2D.Double(
                            meterMiddleX - DEFAULT_CIRCLE_SIZE/2,
                            meterMiddleY - DEFAULT_CIRCLE_SIZE/2,
                            DEFAULT_CIRCLE_SIZE,
                            DEFAULT_CIRCLE_SIZE);

                g2.fill(arrow);
                g2.fill(circle);
            }

            g2.clip(savedClip);
            g2.setComposite(originalComposite);

        }

    }

    void drawArcFor( Graphics2D g2, Rectangle2D meterArea, MeterDataset data, int type) {

        Number minValue = null;
        Number maxValue = null;
        Color color = null;
        switch (type) {

            case MeterDataset.CRITICAL_DATA:
                minValue = data.getMinimumCriticalValue();
                maxValue = data.getMaximumCriticalValue();
                color = getCriticalColor();
                break;

            case MeterDataset.WARNING_DATA:
                minValue = data.getMinimumWarningValue();
                maxValue = data.getMaximumWarningValue();
                color = getWarningColor();
                break;

            case MeterDataset.NORMAL_DATA:
                minValue = data.getMinimumNormalValue();
                maxValue = data.getMaximumNormalValue();
                color = getNormalColor();
                break;

            case MeterDataset.FULL_DATA:
                minValue = data.getMinimumValue();
                maxValue = data.getMaximumValue();
                color = DEFAULT_BACKGROUND_COLOR;
                break;

            default:
                return;
        }

        if( minValue != null && maxValue != null) {
            double dataMin = data.getMinimumValue().doubleValue();
            if (data.getBorderType() == type) {
                drawArc(g2, meterArea,
                        minValue.doubleValue(),
                        data.getMinimumValue().doubleValue(),
                        color);
                drawArc(g2, meterArea,
                        data.getMaximumValue().doubleValue(),
                        maxValue.doubleValue(),
                        color);
            }
            else {
                drawArc(g2, meterArea,
                        minValue.doubleValue(),
                        maxValue.doubleValue(),
                        color);
            }
            drawTick(g2, meterArea, minValue.doubleValue(), true, color);
            drawTick(g2, meterArea, maxValue.doubleValue(), true, color);
        }

    }

    /**
     * Draws an arc.
     */
    void drawArc(Graphics2D g2, Rectangle2D area, double minValue, double maxValue, Color color) {
        drawArc(g2, area, minValue, maxValue, color, 0);
    }

    /**
     * Draws an arc.
     */
    void drawArc(Graphics2D g2, Rectangle2D area, double minValue, double maxValue, Color color,
                 int outlineType) {

        double startAngle = calculateAngle(maxValue);
        double endAngle = calculateAngle(minValue);
        double extent = endAngle - startAngle;

        double x = area.getX();
        double y = area.getY();
        double w = area.getWidth();
        double h = area.getHeight();
        g2.setColor(color);

        if (outlineType>0) {
            g2.setStroke(new BasicStroke(10.0f));
        }
        else {
            g2.setStroke(new BasicStroke(DEFAULT_BORDER_SIZE));
        }

        int joinType = Arc2D.OPEN;
        if( outlineType > 0) {
            switch( dialType) {
                case DIALTYPE_PIE:
                    joinType = Arc2D.PIE;
                    break;
                case DIALTYPE_CHORD:
                    if( meterAngle > 180) {
                        joinType = Arc2D.CHORD;
                    } else {
                        joinType = Arc2D.PIE;
                    }
                    break;
                case DIALTYPE_CIRCLE:
                    joinType = Arc2D.PIE;
                    extent = 360;
                    break;
            }
        }
        Arc2D.Double arc = new Arc2D.Double(x, y, w, h, startAngle, extent, joinType);
        if (outlineType>0) {
            g2.fill(arc);
        }
        else {
            g2.draw(arc);
        }

    }

    double calculateAngle(double value) {
        value -= minMeterValue;
        double ret = meterCalcAngle - ((value/meterRange) * meterAngle);
        return ret;
    }


    void drawTicks(Graphics2D g2, Rectangle2D meterArea, double minValue, double maxValue) {

        int numberOfTicks = 20;
        double diff = (maxValue - minValue) / numberOfTicks;

        for (double i = minValue; i <= maxValue; i+=diff) {
            drawTick(g2, meterArea, i);
        }

    }

    void drawTick(Graphics2D g2, Rectangle2D meterArea, double value) {
        drawTick(g2, meterArea, value, false, null, false, null);
    }

    void drawTick(Graphics2D g2, Rectangle2D meterArea, double value, boolean label, Color color) {
        drawTick(g2, meterArea, value, label, color, false, null);
    }

    void drawTick(Graphics2D g2, Rectangle2D meterArea, double value,
                  boolean label, Color labelColor, boolean curValue, String units) {

        double valueAngle = calculateAngle(value);

        double meterMiddleX = meterArea.getCenterX();
        double meterMiddleY = meterArea.getCenterY();

        if (labelColor==null) {
            labelColor = Color.white;
        }
        g2.setColor(labelColor);
        g2.setStroke(new BasicStroke(2.0f));

        double valueP2X = 0;
        double valueP2Y = 0;

        if(!curValue) {
            double radius = (meterArea.getWidth() / 2) + DEFAULT_BORDER_SIZE;
            double radius1 = radius - 15;

            double valueP1X = meterMiddleX + (radius * Math.cos( Math.PI * ( valueAngle/180)));
            double valueP1Y = meterMiddleY - (radius * Math.sin( Math.PI * ( valueAngle/180)));

            valueP2X = meterMiddleX + (radius1 * Math.cos( Math.PI * ( valueAngle/180)));
            valueP2Y = meterMiddleY - (radius1 * Math.sin( Math.PI * ( valueAngle/180)));

            Line2D.Double line = new Line2D.Double(valueP1X, valueP1Y, valueP2X, valueP2Y);
            g2.draw(line);
        }
        else {
            valueP2X = meterMiddleX;
            valueP2Y = meterMiddleY;
            valueAngle = 90;
        }

        if (this.labelType==VALUE_LABELS && label) {

            DecimalFormat df = new DecimalFormat("#,###,###,##0.00");
            String tickLabel =  df.format(value);
            if (curValue && units != null) {
                tickLabel += "  " + units;
            }
            Rectangle2D tickLabelBounds = g2.getFont().getStringBounds(tickLabel,
                                                                       g2.getFontRenderContext());

            double x = valueP2X;
            double y = valueP2Y;
            if (curValue) {
                y+=DEFAULT_CIRCLE_SIZE;
            }
            if (valueAngle==90 || valueAngle==270) {
                x = x - tickLabelBounds.getWidth()/2;
            }
            else if (valueAngle<90 || valueAngle>270) {
                x = x - tickLabelBounds.getWidth();
            }
            if ((valueAngle>135 && valueAngle<225) || valueAngle>315 || valueAngle<45) {
                y = y - tickLabelBounds.getHeight()/2;
            }
            else {
                y = y + tickLabelBounds.getHeight()/2;
            }
            if (labelFont!=null) {
                g2.setFont(labelFont);
            }
            g2.drawString(tickLabel, (float)x, (float)y);
        }
    }

    /**
     * Returns a short string describing the type of plot.
     */
    public String getPlotType() {
        return "Meter Plot";
    }

    /**
     * A zoom method that does nothing.
     * <p>
     * Plots are required to support the zoom operation.  In the case of a pie chart, it doesn't
     * make sense to zoom in or out, so the method is empty.
     *
     * @param percent The zoom percentage.
     */
    public void zoom(double percent) {
    }

    /**
     * Returns true if the axis is compatible with the meter plot, and false otherwise.  Since a meter
     * plot requires no axes, only a null axis is compatible.
     * @param axis The axis.
     */
    public boolean isCompatibleHorizontalAxis(Axis axis) {
        if (axis==null) return true;
        else return false;
    }

    /**
     * Returns true if the axis is compatible with the meter plot, and false otherwise.  Since a meter
     * plot requires no axes, only a null axis is compatible.
     * @param axis The axis.
     */
    public boolean isCompatibleVerticalAxis(Axis axis) {
        if (axis==null) return true;
        else return false;
    }

}
