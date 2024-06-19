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
 * --------------
 * PiePlot3D.java
 * --------------
 * (C) Copyright 2000-2002, by Simba Management and Contributors.
 *
 * Original Author:  Tomer Peretz;
 * Contributor(s):   -;
 *
 * $Id: Pie3DPlot.java,v 1.1 2007/10/10 19:41:58 vauchers Exp $
 *
 * Changes
 * -------
 * 21-Jun-2001 : Version 1;
 *
 */

package com.jrefinery.chart;

import  com.jrefinery.chart.tooltips.StandardPieToolTipGenerator;
import  com.jrefinery.chart.entity.PieSectionEntity;
import  com.jrefinery.data.PieDataset;
import  java.awt.*;
import  java.awt.geom.*;
import  java.awt.font.*;
import  java.util.*;

/**
 * A plot that displays data in the form of a 3D pie chart, using data from any class that
 * implements the CategoryDataset interface.
 *
 * @see Plot
 */
public class Pie3DPlot extends PiePlot {

    /** The factor of the depth of the pie from the plot height */
    protected double depthFactor = 0.2;

    /**
     * Standard constructor - builds a PiePlot with mostly default attributes.
     *
     * @param data The chart that the plot belongs to;
     */
    public Pie3DPlot (PieDataset data) throws AxisNotCompatibleException
    {
        super(data);
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2 The graphics device.
     * @param plotArea The area within which the plot should be drawn.
     * @param info Collects info about the drawing.
     */
    public void draw (Graphics2D g2, Rectangle2D plotArea, ChartRenderingInfo info) {

        Shape savedClip = g2.getClip();
        Rectangle2D clipArea = savedClip != null ? savedClip.getBounds2D().createIntersection(plotArea) : plotArea;

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

        // adjust the plot area by the interior spacing value
        double gapHorizontal = plotArea.getWidth()*this.interiorGapPercent;
        double gapVertical = plotArea.getHeight()*this.interiorGapPercent;
        double pieX = plotArea.getX()+gapHorizontal/2;
        double pieY = plotArea.getY()+gapVertical/2;
        double pieW = plotArea.getWidth()-gapHorizontal;
        double pieH = plotArea.getHeight()-gapVertical;

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

        // draw the outline and background
        drawOutlineAndBackground(g2, plotArea);
        // get the data source - return if null;
        PieDataset data = getPieDataset();
        if (data == null)
            return;
        // if too any elements
        if (data.getCategories().size() > plotArea.getWidth()) {
            String text = "Too many elements";
            Font sfont = new Font("dialog", Font.BOLD, 10);
            g2.setFont(sfont);
            int stringWidth = (int)sfont.getStringBounds(text, g2.getFontRenderContext()).getWidth();

            g2.drawString(text, (int)(plotArea.getX() + (plotArea.getWidth()
                    - stringWidth)/2), (int)(plotArea.getY() + (plotArea.getHeight()/2)));
            return;
        }
        // if we are drawing a perfect circle, we need to readjust the top left coordinates of the
        // drawing area for the arcs to arrive at this effect.
        if (circular) {
            double min = Math.min(plotArea.getWidth(), plotArea.getHeight())/2;
            plotArea = new Rectangle2D.Double(plotArea.getCenterX() - min,
                    plotArea.getCenterY() - min, 2*min, 2*min);
        }
        // get a list of categories...
        java.util.List categories = data.getCategories();

        if (categories.size() == 0)
            return;

        // establish the coordinates of the top left corner of the drawing area
        double arcX = pieArea.getX();
        double arcY = pieArea.getY();

        g2.clip(clipArea);
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                   this.foregroundAlpha));
        // compute the total value of the data series skipping over the negative values
            double totalValue = 0;
            Iterator iterator = categories.iterator();
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
        int depth = (int)(pieArea.getHeight()*depthFactor);
        // if depth is negative don't draw anything
        if (depth < 0)
            return;
        ArrayList buttomArcList = new ArrayList();
        Arc2D.Double arc;
        Paint paint;
        Paint outlinePaint;
        boolean hasElement = false;
        //initial the list of arcs
        iterator = categories.iterator();
        while (iterator.hasNext()) {
            Object current = iterator.next();
            Number dataValue = data.getValue(current);
            double value = dataValue.doubleValue();
            if (value <= 0) {
                buttomArcList.add(null);
                continue;
            }
            hasElement = true;
            double startAngle = sumTotal*360/totalValue;
            double extent = (sumTotal + value)*360/totalValue - startAngle;
            buttomArcList.add(new Arc2D.Double(arcX, arcY + depth, pieArea.getWidth(),
                    pieArea.getHeight() - depth, startAngle, extent, Arc2D.PIE));
            sumTotal += value;
        }
        Rectangle2D arcBound;
        Shape oldClip = g2.getClip();
        // draw the buttom circle
        int xs[];
        int ys[];
        outlinePaint = getSeriesOutlinePaint(0);
        arc = new Arc2D.Double(arcX, arcY + depth, pieArea.getWidth(), pieArea.getHeight()
                - depth, 0, 360, Arc2D.PIE);
        g2.setPaint(outlinePaint);
        if (hasElement)
            g2.drawLine((int)arc.getMaxX(), (int)arc.getCenterY(), (int)arc.getMaxX(),
                    (int)(arc.getCenterY() - depth));

        int categoryCount = buttomArcList.size();
        for (int categoryIndex = 0; categoryIndex < categoryCount; categoryIndex++) {
            arc = (Arc2D.Double)buttomArcList.get(categoryIndex);
            if (arc == null || arc.getAngleStart() + arc.getAngleExtent() < 180)
                continue;
            Point2D p1 = arc.getAngleStart() > 180 ? arc.getStartPoint() :
                    new Point2D.Double(arc.getMinX(), arc.getCenterY());
            Point2D p2 = arc.getEndPoint();
            paint = getSeriesPaint(categoryIndex);
            outlinePaint = getSeriesOutlinePaint(categoryIndex);
            g2.setClip(oldClip.getBounds2D().createIntersection(new Rectangle.Double(Math.min(p2.getX(),
                    p1.getX()), arc.getMinY(), Math.abs(p2.getX() - p1.getX()),
                    arc.getHeight() + 3)));

            g2.setPaint(paint);
            g2.fill(arc);
            g2.draw(arc);
            g2.setPaint(outlinePaint);
            arc.setArcType(Arc2D.OPEN);
            g2.draw(arc);
            arc.setArcType(Arc2D.PIE);
            g2.setPaint(paint);

            // draw the height
            xs = new int[] {
                (int)p1.getX(), (int)p2.getX(), (int)p2.getX(), (int)p1.getX()
            };
            ys = new int[] {
                (int)p1.getY(), (int)p2.getY(), (int)p2.getY() - depth, (int)p1.getY()
                        - depth
            };
            Polygon ploygon = new Polygon(xs, ys, 4);
            g2.fill(ploygon);
            g2.setPaint(outlinePaint);
            g2.drawLine((int)p1.getX(), (int)p1.getY(), (int)p1.getX(), (int)p1.getY()
                    - depth);
        }
        g2.setClip(oldClip);

        // draw the upper circle
        Arc2D upperArc;
        Rectangle2D upperArcBound = new Rectangle2D.Double(pieArea.getX(), pieArea.getY(), pieArea.getWidth(),
                                    pieArea.getHeight() - depth);
        for (int categoryIndex = 0; categoryIndex < categoryCount; categoryIndex++) {
            arc = (Arc2D.Double)buttomArcList.get(categoryIndex);
            if(arc == null)
              continue;
            upperArc = new Arc2D.Double(arcX, arcY, pieArea.getWidth(),
                                        pieArea.getHeight() - depth, arc.getAngleStart(), arc.getAngleExtent(), Arc2D.PIE);
            paint = this.getSeriesPaint(categoryIndex);
            outlinePaint = this.getSeriesOutlinePaint(categoryIndex);

            g2.setPaint(paint);
            g2.fill(upperArc);
            g2.setStroke(new BasicStroke());
            g2.setPaint(outlinePaint);
            g2.draw(upperArc);
            // add a tooltip for the bar...
            Object current = categories.get(categoryIndex);
            if (info!=null) {
                if (this.toolTipGenerator==null) {
                    toolTipGenerator = new StandardPieToolTipGenerator();
                }
                String tip = this.toolTipGenerator.generateToolTip(data, current);
                PieSectionEntity entity = new PieSectionEntity(arc, tip, current, categoryIndex);
                info.getEntityCollection().addEntity(entity);
            }

            // then draw the label...
            if (this.sectionLabelType!=NO_LABELS) {
                this.drawLabel(g2, upperArcBound, explodedPieArea, data, data.getValue(current).doubleValue(),
                               categoryIndex, arc.getAngleStart(), arc.getAngleExtent());
            }
        }

        g2.clip(savedClip);
        g2.setComposite(originalComposite);
    }


    /**
     * Returns a short string describing the type of plot.
     */
    public String getPlotType () {
        return "Pie 3D Plot";
    }


    /**
     * Sets the factor of the pie depth from the plot height
     */
    public void setDepthFactor (double newDepthFactor) {
        depthFactor = newDepthFactor;
    }

    /**
     * Gets the factor of the pie depth from the plot height
     */
    public double getDepthFactor () {
        return  depthFactor;
    }

}



