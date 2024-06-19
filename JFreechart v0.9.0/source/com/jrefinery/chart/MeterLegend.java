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
 * ----------------
 * MeterLegend.java
 * ----------------
 * (C) Copyright 2000-2002, by Hari and Contributors.
 *
 * Original Author:  Hari (ourhari@hotmail.com);
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: MeterLegend.java,v 1.1 2007/10/10 19:01:18 vauchers Exp $
 *
 * Changes
 * -------
 * 01-Apr-2002 : Version 1, contributed by Hari (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;
import com.jrefinery.data.*;
import com.jrefinery.chart.event.*;

public class MeterLegend extends StandardLegend {

    protected String legendText;

    public boolean showNormal = true;

    public boolean showCritical = true;

    public boolean showWarning = true;

    public MeterLegend(JFreeChart chart, String legendText) {

        this(chart,
             3,
             new Spacer(Spacer.ABSOLUTE, 2, 2, 2, 2),
             Color.white, new BasicStroke(), Color.gray,
             DEFAULT_FONT, Color.black, legendText);

    }

    public MeterLegend(JFreeChart chart,
                       int outerGap, Spacer innerGap,
                       Paint backgroundPaint,
                       Stroke outlineStroke, Paint outlinePaint,
                       Font itemFont, Paint itemPaint, String legendText) {

        super(chart, outerGap, innerGap, backgroundPaint, outlineStroke, outlinePaint,
              itemFont, itemPaint);

        this.legendText = legendText;

    }

    public String getLegendText() {
        return this.legendText;
    }

    public void setLegendText( String text) {
        this.legendText = text;
        notifyListeners(new LegendChangeEvent(this));
    }

    public Rectangle2D draw(Graphics2D g2, Rectangle2D available) {

        return draw(g2, available, (_anchor & HORIZONTAL)!=0, (_anchor & INVERTED)!=0);

    }

    private boolean updateInformation(MeterPlot plot, MeterDataset data,
                                      int type, int index,
                                      String[] legendItemLabels, Color[] legendItemColors) {

        boolean ret = false;
        String label = null;
        Number minValue = null;
        Number maxValue = null;
        Color color = null;

        switch( type) {
            case MeterDataset.CRITICAL_DATA:
                minValue = data.getMinimumCriticalValue();
                maxValue = data.getMaximumCriticalValue();
                color = plot.getCriticalColor();
                label = MeterPlot.CRITICAL_TEXT;
                break;
            case MeterDataset.WARNING_DATA:
                minValue = data.getMinimumWarningValue();
                maxValue = data.getMaximumWarningValue();
                color = plot.getWarningColor();
                label = MeterPlot.WARNING_TEXT;
                break;
            case MeterDataset.NORMAL_DATA:
                minValue = data.getMinimumNormalValue();
                maxValue = data.getMaximumNormalValue();
                color = plot.getNormalColor();
                label = MeterPlot.NORMAL_TEXT;
                break;
            case MeterDataset.FULL_DATA:
                minValue = data.getMinimumValue();
                maxValue = data.getMaximumValue();
                color = MeterPlot.DEFAULT_BACKGROUND_COLOR;
                label = "Meter Graph";
                break;
            default:
                return false;
        }

        if( minValue != null && maxValue != null) {
            double dataMin = data.getMinimumValue().doubleValue();
            if (data.getBorderType() == type) {
                label += "  Range: " +
                    data.getMinimumValue().toString() + " to " + minValue.toString() +
                    "  and  " +
                    maxValue.toString() + " to " + data.getMaximumValue().toString();
            }
            else {
                label += "  Range: " +
                    minValue.toString() + " to " + maxValue.toString();
            }
            legendItemLabels[ index] = label;
            legendItemColors[ index] = color;
            ret = true;
        }
        return ret;
    }

    protected Rectangle2D draw(Graphics2D g2, Rectangle2D available,
                               boolean horizontal, boolean inverted) {

        int legendCount = 0;
        Plot plot = chart.getPlot();
        if( !(plot instanceof MeterPlot)) {
                        throw new IllegalArgumentException( "Plot must be MeterPlot");
        }
        MeterPlot meterPlot = (MeterPlot) plot;
        MeterDataset data = meterPlot.getMeterDataset();

        legendCount = 1;	// Name of the Chart.
        legendCount++; 		// Display Full Range
        if (showCritical && data.getMinimumCriticalValue() != null) { legendCount++; }
        if (showWarning && data.getMinimumWarningValue() != null) { legendCount++; }
        if (showNormal && data.getMinimumNormalValue() != null) { legendCount++; }

        String[] legendItemLabels = new String[ legendCount];
        Color[] legendItemColors = new Color[ legendCount];

        int currentItem = 0;
        legendItemLabels[ currentItem] = this.legendText +
                (data.isValueValid() ? ( "   Current Value: " + data.getValue().toString()) : "");
        legendItemColors[ currentItem] = null;		// no color
        currentItem ++;
        if (updateInformation( meterPlot, data, MeterDataset.FULL_DATA, currentItem, legendItemLabels, legendItemColors)) {
            currentItem++;
        }
        if (showCritical && updateInformation( meterPlot, data, MeterDataset.CRITICAL_DATA, currentItem, legendItemLabels, legendItemColors)) {
            currentItem++;
        }
        if (showWarning && updateInformation( meterPlot, data, MeterDataset.WARNING_DATA, currentItem, legendItemLabels, legendItemColors)) {
            currentItem++;
        }
        if (showNormal && updateInformation( meterPlot, data, MeterDataset.NORMAL_DATA, currentItem, legendItemLabels, legendItemColors)) {
            currentItem++;
        }

        if (legendItemLabels!=null) {

            Rectangle2D legendArea = new Rectangle2D.Double();

            // the translation point for the origin of the drawing system
            Point2D translation = new Point2D.Double();

            // Create buffer for individual rectangles within the legend
            LegendItem[] items = new LegendItem[legendItemLabels.length];
            g2.setFont(itemFont);

            // Compute individual rectangles in the legend, translation point as well
            // as the bounding box for the legend.
            if (horizontal) {
                double xstart = available.getX()+outerGap;
                double xlimit = available.getX()+available.getWidth()-2*outerGap-1;
                double maxRowWidth = 0;
                double xoffset = 0;
                double rowHeight = 0;
                double totalHeight = 0;
                boolean startingNewRow = true;

                for (int i=0; i<legendItemLabels.length; i++) {
                    items[i] = createLegendItem(g2, legendItemLabels[i], xoffset, totalHeight);
                    if ((!startingNewRow) && (items[i].getX()+items[i].getWidth()+xstart>xlimit)) {
                        maxRowWidth=Math.max(maxRowWidth, xoffset);
                        xoffset = 0;
                        totalHeight += rowHeight;
                        i--;
                        startingNewRow=true;
                    }
                    else {
                        rowHeight = Math.max(rowHeight, items[i].getHeight());
                        xoffset += items[i].getWidth();
                        startingNewRow=false;
                    }
                }

                maxRowWidth=Math.max(maxRowWidth, xoffset);
                totalHeight += rowHeight;

                // Create the bounding box
                legendArea = new Rectangle2D.Double(0, 0, maxRowWidth, totalHeight);

                // The yloc point is the variable part of the translation point
                // for horizontal legends. xloc is constant.
                double yloc = (inverted) ?
                    available.getY() + available.getHeight() - totalHeight - outerGap :
                    available.getY() + outerGap;
                double xloc = available.getX() + available.getWidth()/2 - maxRowWidth/2;

                // Create the translation point
                translation = new Point2D.Double(xloc,yloc);
            }
            else {  // vertical...
                double totalHeight = 0;
                double maxWidth = 0;
                g2.setFont(itemFont);
                for (int i = 0; i < items.length; i++) {
                    items[i] = createLegendItem(g2, legendItemLabels[i], 0, totalHeight);
                    totalHeight +=items[i].getHeight();
                    maxWidth = Math.max(maxWidth, items[i].getWidth());
                }

                // Create the bounding box
                legendArea = new Rectangle2D.Float(0, 0, (float)maxWidth, (float)totalHeight);

                // The xloc point is the variable part of the translation point
                // for vertical legends. yloc is constant.
                double xloc = (inverted) ?
                                  available.getMaxX()-maxWidth-outerGap:
                                  available.getX()+outerGap;
                double yloc = available.getY()+(available.getHeight()/2)-(totalHeight/2);

                // Create the translation point
                translation = new Point2D.Double(xloc, yloc);
            }

            // Move the origin of the drawing to the appropriate location
            g2.translate(translation.getX(), translation.getY());

            // Draw the legend's bounding box
            g2.setPaint(backgroundPaint);
            g2.fill(legendArea);
            g2.setPaint(outlinePaint);
            g2.setStroke(outlineStroke);
            g2.draw(legendArea);

            // Draw individual series elements
            for (int i=0; i<items.length; i++) {
                Color color = legendItemColors[ i];
                if (color != null) {
                    g2.setPaint( color);
                    g2.fill(items[i].getMarker());
                }
                g2.setPaint(itemPaint);
                g2.drawString(items[i].label,
                              (float)items[i].labelPosition.getX(),
                              (float)items[i].labelPosition.getY());
            }

            // translate the origin back to what it was prior to drawing the legend
            g2.translate(-translation.getX(),-translation.getY());

            if (horizontal) {
                // The remaining drawing area bounding box will have the same x origin,
                // width and height independent of the anchor's location. The variable
                // is the y coordinate. If the anchor is SOUTH, the y coordinate is simply
                // the original y coordinate of the available area. If it is NORTH, we
                // adjust original y by the total height of the legend and the initial gap.
                double yloc = (inverted) ? available.getY() :
                              available.getY()+legendArea.getHeight()+outerGap;

                // return the remaining available drawing area
                return new Rectangle2D.Double(available.getX(), yloc, available.getWidth(),
                    available.getHeight()-legendArea.getHeight()-2*outerGap);
            }
            else {
                // The remaining drawing area bounding box will have the same y origin,
                // width and height independent of the anchor's location. The variable
                // is the x coordinate. If the anchor is EAST, the x coordinate is simply
                // the original x coordinate of the available area. If it is WEST, we
                // adjust original x by the total width of the legend and the initial gap.
                double xloc = (inverted) ? available.getX() :
                    available.getX()+legendArea.getWidth()+2*outerGap;

                // return the remaining available drawing area
                return new Rectangle2D.Double(xloc, available.getY(),
                    available.getWidth()-legendArea.getWidth()-2 * outerGap,
                    available.getHeight());
            }
        }
        else {
            return available;
        }
    }

    private LegendItem createLegendItem(Graphics graphics, String label, double x, double y) {

        int innerGap = 2;
        FontMetrics fm = graphics.getFontMetrics();
        LineMetrics lm = fm.getLineMetrics(label, graphics);
        float textHeight = lm.getHeight();

        LegendItem item = new LegendItem(label);

        float xloc = (float)(x+innerGap+1.15f*textHeight);
        float yloc = (float)(y+innerGap+(textHeight-lm.getLeading()-lm.getDescent()));

        item.labelPosition = new Point2D.Float(xloc, yloc);

        float boxDim = textHeight*0.70f;
        xloc = (float)(x+innerGap+0.15f*textHeight);
        yloc = (float)(y+innerGap+0.15f*textHeight);

        item.setMarker(new Rectangle2D.Float(xloc, yloc, boxDim, boxDim));

        float width = (float)(item.labelPosition.getX()-x+
                              fm.getStringBounds(label,graphics).getWidth()+0.5*textHeight);

        float height = (float)(2*innerGap+textHeight);
        item.setBounds(x, y, width, height);
        return item;

    }

}
