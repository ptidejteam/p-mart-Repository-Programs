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
 * --------------------------
 * VerticalBarRenderer3D.java
 * --------------------------
 * (C) Copyright 2001, 2002, by Serge V. Grachov and Contributors.
 *
 * Original Author:  Serge V. Grachov;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: VerticalBarRenderer3D.java,v 1.1 2007/10/10 19:01:18 vauchers Exp $
 *
 * Changes
 * -------
 * 31-Oct-2001 : First version, contributed by Serge V. Grachov (DG);
 * 15-Nov-2001 : Modified to allow for null data values (DG);
 * 13-Dec-2001 : Added tooltips (DG);
 * 16-Jan-2002 : Added fix for single category or single series datasets, pointed out by
 *               Taoufik Romdhane (DG);
 * 24-May-2002 : Incorporated tooltips into chart entities (DG);
 *
 */

package com.jrefinery.chart;

import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.entity.CategoryItemEntity;
import com.jrefinery.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.chart.tooltips.StandardCategoryToolTipGenerator;
import com.jrefinery.data.CategoryDataset;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * A renderer for 3D-effect bars...
 */
public class VerticalBarRenderer3D extends VerticalBarRenderer {

    /** The default 3D effect. */
    private static final double DEFAULT_EFFECT3D = 10.0;

    /** The size of the 3D effect (in pixels). */
    protected double effect3d;

    /**
     * Default constructor.
     */
    public VerticalBarRenderer3D() {
        this(new StandardCategoryToolTipGenerator(), DEFAULT_EFFECT3D);
    }

    /**
     * Constructs a new renderer.
     */
    public VerticalBarRenderer3D(CategoryToolTipGenerator toolTipGenerator, double effect3d) {
        super(toolTipGenerator);
        this.effect3d = effect3d;
    }

    /**
     * Returns true, since there are (potentially) gaps between bars in this representation.
     */
    public boolean hasItemGaps() {
        return true;
    }

    /**
     * This will be a method in the renderer that tells whether there is one bar width per category
     * or onebarwidth per series per category.
     */
    public int barWidthsPerCategory(CategoryDataset data) {
        return data.getSeriesCount();
    }

    /**
     * Returns the area that the axes (and date) must fit into.
     * <P>
     * Often this is the same as the plotArea, but sometimes a smaller region should be used
     * (for example, the 3D charts require the axes to use less space in order to leave room
     * for the 'depth' part of the chart).
     */
    public Rectangle2D getAxisArea(Rectangle2D plotArea) {
        return new Rectangle2D.Double(plotArea.getX(),
                                      plotArea.getY()+this.effect3d,
                                      plotArea.getWidth()-this.effect3d,
                                      plotArea.getHeight()-this.effect3d);
    }

    /**
     * Returns the clip region...usually returns the dataArea, but some charts (e.g. 3D) have
     * non rectangular clip regions.
     */
    public Shape getDataClipRegion(Rectangle2D dataArea) {

        GeneralPath result = new GeneralPath();
        result.moveTo((float)dataArea.getX(), (float)(dataArea.getMinY()+this.effect3d));
        result.lineTo((float)(dataArea.getX()+this.effect3d), (float)dataArea.getMinY());
        result.lineTo((float)dataArea.getMaxX(), (float)dataArea.getMinY());
        result.lineTo((float)dataArea.getMaxX(), (float)(dataArea.getMaxY()-this.effect3d));
        result.lineTo((float)(dataArea.getMaxX()-this.effect3d), (float)dataArea.getMaxY());
        result.lineTo((float)dataArea.getX(), (float)dataArea.getMaxY());
        result.closePath();

        return result;
    }

    /**
     * Draws the background for the plot.
     * <P>
     * For most charts, the axisDataArea and the dataClipArea are the same.  One case where they
     * are different is the 3D-effect bar charts...here the data clip area extends above and to
     * the right of the axisDataArea.
     *
     * @param g2 The graphics device.
     * @param plot The plot.
     * @param axisDataArea The area inside the axes.
     * @param dataClipArea The data clip area.
     */
    public void drawPlotBackground(Graphics2D g2,
                                   CategoryPlot plot,
                                   Rectangle2D axisDataArea, Shape dataClipRegion) {

        super.drawPlotBackground(g2, plot, axisDataArea, dataClipRegion);

        double x1 = axisDataArea.getX();
        double x2 = x1 + this.effect3d;
        double x3 = axisDataArea.getMaxX()+this.effect3d;

        double y1 = axisDataArea.getMaxY();
        double y2 = y1 - this.effect3d;
        double y3 = axisDataArea.getMinY()-this.effect3d;

        g2.setPaint(plot.getOutlinePaint());
        Line2D line = new Line2D.Double(x1, y1, x2, y2);
        g2.draw(line);
        line.setLine(x2, y2, x2, y3);
        g2.draw(line);
        line.setLine(x2, y2, x3, y2);
        g2.draw(line);

    }

    public void drawRangeMarker(Graphics2D g2,
                                CategoryPlot plot, ValueAxis axis, Marker marker,
                                Rectangle2D axisDataArea, Shape dataClipRegion) {

        float y = (float)axis.translateValueToJava2D(marker.getValue(), axisDataArea);
        float x = (float)axisDataArea.getX();
        GeneralPath path = new GeneralPath();
        path.moveTo(x, y);
        path.lineTo(x+(float)effect3d, y-(float)effect3d);
        path.lineTo((float)(axisDataArea.getMaxX()+effect3d), y-(float)effect3d);
        path.lineTo((float)(axisDataArea.getMaxX()), y);
        path.closePath();
        g2.setPaint(marker.getPaint());
        g2.fill(path);
        g2.setPaint(marker.getOutlinePaint());
        g2.draw(path);

    }

    /**
     * Renders an individual bar...there are bug-fixes that have been applied to VerticalBarRenderer
     * that need to be applied here too.
     */
    public void drawCategoryItem(Graphics2D g2, Rectangle2D dataArea,
                                 CategoryPlot plot, ValueAxis axis,
                                 CategoryDataset data, int series, Object category,
                                 int categoryIndex, Object previousCategory) {

        // check the value we are plotting...
        Number value = data.getValue(series, category);
        if (value!=null) {

            // BAR X
            double rectX = dataArea.getX()+dataArea.getWidth()*plot.getIntroGapPercent();

            int categories = data.getCategoryCount();
            int seriesCount = data.getSeriesCount();
            if (categories>1) {
                rectX = rectX
                        // bars in completed categories
                        + categoryIndex*(categorySpan/categories)
                        // gaps between completed categories
                        + (categoryIndex*(categoryGapSpan/(categories-1))
                        // bars+gaps completed in current category
                        + (series*itemSpan/(categories*seriesCount)));
                if (seriesCount>1) {
                    rectX = rectX
                            + (series*itemGapSpan/(categories*(seriesCount-1)));
                }
            }
            else {
                rectX = rectX
                        // bars+gaps completed in current category
                        + (series*itemSpan/(categories*seriesCount));
                if (seriesCount>1) {
                    rectX = rectX
                            + (series*itemGapSpan/(categories*(seriesCount-1)));
                }
            }

            // BAR Y
            double translatedValue = axis.translateValueToJava2D(value.doubleValue(), dataArea);
            double rectY = Math.min(this.zeroInJava2D, translatedValue);

            // BAR WIDTH
            double rectWidth = itemWidth;

            // BAR HEIGHT
            double rectHeight = Math.abs(translatedValue-this.zeroInJava2D);

            Rectangle2D bar = new Rectangle2D.Double(rectX, rectY, rectWidth, rectHeight);
            Paint seriesPaint = plot.getSeriesPaint(series);
            g2.setPaint(seriesPaint);
            g2.fill(bar);

            GeneralPath bar3dRight = null;
            GeneralPath bar3dTop = null;
            double effect3d = 0.00;
            VerticalAxis vAxis = (VerticalAxis)plot.getRangeAxis();
            if (rectHeight != 0 && vAxis instanceof VerticalNumberAxis3D) {
                effect3d = ((VerticalNumberAxis3D) vAxis).getEffect3d();
                bar3dRight = new GeneralPath();
                bar3dRight.moveTo((float)(rectX+rectWidth), (float)rectY);
                bar3dRight.lineTo((float)(rectX+rectWidth), (float)(rectY+rectHeight));
                bar3dRight.lineTo((float)(rectX+rectWidth+effect3d),
                                  (float)(rectY+rectHeight-effect3d));
                bar3dRight.lineTo((float)(rectX+rectWidth+effect3d), (float)(rectY-effect3d));

                if (seriesPaint instanceof Color) {
                    g2.setPaint( ((Color) seriesPaint).darker());
                }
                g2.fill(bar3dRight);

                bar3dTop = new GeneralPath();
                bar3dTop.moveTo( (float) rectX, (float) rectY);
                bar3dTop.lineTo((float) (rectX+effect3d), (float) (rectY-effect3d));
                bar3dTop.lineTo((float) (rectX+rectWidth+effect3d), (float) (rectY-effect3d));
                bar3dTop.lineTo((float) (rectX+rectWidth), (float) (rectY) );
                if (seriesPaint instanceof Color) {
                    g2.setPaint( ((Color) seriesPaint)); //.brighter());
                }
                g2.fill(bar3dTop);
            }

            if (itemWidth>3) {
                g2.setStroke(plot.getSeriesOutlineStroke(series));
                //g2.setStroke(new BasicStroke(0.25f));
                g2.setPaint(plot.getSeriesOutlinePaint(series));
                g2.draw(bar);
                if (bar3dRight != null) {
                  g2.draw(bar3dRight);
                }
                if (bar3dTop != null) {
                  g2.draw(bar3dTop);
                }
            }

            EntityCollection entities = this.info.getEntityCollection();
            if (entities!=null) {
                String tip="";
                if (this.toolTipGenerator!=null) {
                    tip = this.toolTipGenerator.generateToolTip(data, series, category);
                }
                CategoryItemEntity entity = new CategoryItemEntity(bar, tip, series, category);
                entities.addEntity(entity);
            }
        }

    }

}