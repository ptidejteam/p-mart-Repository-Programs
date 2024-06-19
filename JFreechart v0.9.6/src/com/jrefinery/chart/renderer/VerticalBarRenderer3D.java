/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
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
 * (C) Copyright 2001-2003, by Serge V. Grachov and Contributors.
 *
 * Original Author:  Serge V. Grachov;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *                   Tin Luu;
 *                   Milo Simpson;
 *                   Richard Atkinson;
 *                   Rich Unger;
 *
 * $Id: VerticalBarRenderer3D.java,v 1.1 2007/10/10 19:57:50 vauchers Exp $
 *
 * Changes
 * -------
 * 31-Oct-2001 : First version, contributed by Serge V. Grachov (DG);
 * 15-Nov-2001 : Modified to allow for null data values (DG);
 * 13-Dec-2001 : Added tooltips (DG);
 * 16-Jan-2002 : Added fix for single category or single series datasets, pointed out by
 *               Taoufik Romdhane (DG);
 * 24-May-2002 : Incorporated tooltips into chart entities (DG);
 * 11-Jun-2002 : Added check for (permitted) null info object, bug and fix reported by David
 *               Basten.  Also updated Javadocs. (DG);
 * 19-Jun-2002 : Added code to draw labels on bars (TL);
 * 26-Jun-2002 : Added bar clipping to avoid PRExceptions (DG);
 * 05-Aug-2002 : Small modification to drawCategoryItem method to support URLs for HTML image
 *               maps (RA);
 * 06-Aug-2002 : Value labels now use number formatter, thanks to Milo Simpson (DG);
 * 08-Aug-2002 : Applied fixed in bug id 592218 (DG);
 * 20-Sep-2002 : Added fix for categoryPaint by Rich Unger, and fixed errors reported by
 *               Checkstyle (DG);
 * 24-Oct-2002 : Amendments for changes in CategoryDataset interface and CategoryToolTipGenerator
 *               interface (DG);
 * 05-Nov-2002 : Replaced references to CategoryDataset with TableDataset (DG);
 * 06-Nov-2002 : Moved to the com.jrefinery.chart.renderer package (DG);
 * 28-Jan-2003 : Added an attribute to control the shading of the left and bottom walls in the
 *               plot background (DG);
 *
 */

package com.jrefinery.chart.renderer;

import java.awt.Graphics2D;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.Image;
import java.awt.Font;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import com.jrefinery.chart.Marker;
import com.jrefinery.chart.Effect3D;
import com.jrefinery.chart.axis.CategoryAxis;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.axis.VerticalAxis;
import com.jrefinery.chart.axis.VerticalNumberAxis3D;
import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.entity.CategoryItemEntity;
import com.jrefinery.chart.plot.Plot;
import com.jrefinery.chart.plot.CategoryPlot;
import com.jrefinery.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.chart.urls.CategoryURLGenerator;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.Range;

/**
 * A renderer for vertical bars with a 3D effect.
 * <p>
 * For use with the {@link com.jrefinery.chart.plot.VerticalCategoryPlot} class.
 *
 * @author Serge V. Grachov
 */
public class VerticalBarRenderer3D extends VerticalBarRenderer implements Effect3D {

    /** The default x-offset for the 3D effect. */
    public static final double DEFAULT_X_OFFSET = 12.0;

    /** The default y-offset for the 3D effect. */
    public static final double DEFAULT_Y_OFFSET = 8.0;

    /** The default wall paint. */
    public static final Paint DEFAULT_WALL_PAINT = new Color(0xDD, 0xDD, 0xDD);
    
    /** The size of x-offset for the 3D effect. */
    private double xOffset;

    /** The size of y-offset for the 3D effect. */
    private double yOffset;

    /** The paint used to shade the left and lower 3D wall. */
    private Paint wallPaint;
    
    /**
     * Default constructor, creates a renderer with a ten pixel '3D effect'.
     */
    public VerticalBarRenderer3D() {
        this(DEFAULT_X_OFFSET, DEFAULT_Y_OFFSET);
    }

    /**
     * Constructs a new renderer with the specified '3D effect'.
     *
     * @param xOffset  the x-offset for the 3D effect.
     * @param yOffset  the y-offset for the 3D effect.
     */
    public VerticalBarRenderer3D(double xOffset, double yOffset) {

        this(xOffset, yOffset, null, null);

    }

    /**
     * Constructs a new renderer.
     *
     * @param toolTipGenerator  the tool tip generator.
     * @param urlGenerator  the URL generator.
     */
    public VerticalBarRenderer3D(CategoryToolTipGenerator toolTipGenerator,
                                 CategoryURLGenerator urlGenerator) {

        this(DEFAULT_X_OFFSET, DEFAULT_Y_OFFSET, toolTipGenerator, urlGenerator);
        
    }

    /**
     * Constructs a new renderer.
     *
     * @param xOffset  the x-offset for the 3D effect.
     * @param yOffset  the y-offset for the 3D effect.
     * @param toolTipGenerator  the tool tip generator.
     * @param urlGenerator  the URL generator.
    */
    public VerticalBarRenderer3D(double xOffset, double yOffset,
                                 CategoryToolTipGenerator toolTipGenerator,
                                 CategoryURLGenerator urlGenerator) {

        super(toolTipGenerator, urlGenerator);
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.wallPaint = DEFAULT_WALL_PAINT;

    }

    /**
     * Returns the x-offset for the 3D effect.
     *
     * @return the 3D effect.
     */
    public double getXOffset() {
        return this.xOffset;
    }

    /**
     * Returns the y-offset for the 3D effect.
     *
     * @return the 3D effect.
     */
    public double getYOffset() {
        return this.yOffset;
    }

    /**
     * Returns the paint used to highlight the left and bottom wall in the plot background.
     * 
     * @return The paint.
     */
    public Paint getWallPaint() {
        return this.wallPaint;
    }
    
    /**
     * Sets the paint used to hightlight the left and bottom walls in the plot background.
     * 
     * @param paint  the paint.
     */
    public void setWallPaint(Paint paint) {
        this.wallPaint = paint;
    }
    
    /**
     * Draws the background for the plot.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the area inside the axes.
     */
    public void drawBackground(Graphics2D g2, CategoryPlot plot, Rectangle2D dataArea) {

        float x0 = (float) dataArea.getX();
        float x1 = x0 + (float) Math.abs(this.xOffset);
        float x3 = (float) dataArea.getMaxX();
        float x2 = x3 - (float) Math.abs(this.xOffset);

        float y0 = (float) dataArea.getMaxY();
        float y1 = y0 - (float) Math.abs(this.yOffset);
        float y3 = (float) dataArea.getMinY();
        float y2 = y3 + (float) Math.abs(this.yOffset);

        GeneralPath clip = new GeneralPath();
        clip.moveTo(x0, y0);
        clip.lineTo(x0, y2);
        clip.lineTo(x1, y3);
        clip.lineTo(x3, y3);
        clip.lineTo(x3, y1);
        clip.lineTo(x2, y0);
        clip.closePath();

        // fill background...
        Paint backgroundPaint = plot.getBackgroundPaint();
        if (backgroundPaint != null) {
            g2.setPaint(backgroundPaint);
            g2.fill(clip);
        }

        GeneralPath leftWall = new GeneralPath();
        leftWall.moveTo(x0, y0);
        leftWall.lineTo(x0, y2);
        leftWall.lineTo(x1, y3);
        leftWall.lineTo(x1, y1);
        leftWall.closePath();
        g2.setPaint(getWallPaint());
        g2.fill(leftWall);
                
        GeneralPath bottomWall = new GeneralPath();
        bottomWall.moveTo(x0, y0);
        bottomWall.lineTo(x1, y1);
        bottomWall.lineTo(x3, y1);
        bottomWall.lineTo(x2, y0);
        bottomWall.closePath();
        g2.setPaint(getWallPaint());
        g2.fill(bottomWall);
           
        // higlight the background corners...   
        g2.setPaint(Color.lightGray);  
        Line2D corner = new Line2D.Double(x0, y0, x1, y1);
        g2.draw(corner); 
        corner.setLine(x1, y1, x1, y3);
        g2.draw(corner);
        corner.setLine(x1, y1, x3, y1);
        g2.draw(corner);
        
        // draw background image, if there is one...
        Image backgroundImage = plot.getBackgroundImage();
        if (backgroundImage != null) {
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC,
                                                       plot.getBackgroundAlpha()));
            g2.drawImage(backgroundImage,
                         (int) x1, (int) y3,
                         (int) (x3 - x1 + 1), (int) (y1 - y3 + 1),
                         null);
            g2.setComposite(originalComposite);
        }

    }

    /**
     * Draws the outline for the plot.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the area inside the axes.
     */
    public void drawOutline(Graphics2D g2, CategoryPlot plot, Rectangle2D dataArea) {

        float x0 = (float) dataArea.getX();
        float x1 = x0 + (float) Math.abs(this.xOffset);
        float x3 = (float) dataArea.getMaxX();
        float x2 = x3 - (float) Math.abs(this.xOffset);

        float y0 = (float) dataArea.getMaxY();
        float y1 = y0 - (float) Math.abs(this.yOffset);
        float y3 = (float) dataArea.getMinY();
        float y2 = y3 + (float) Math.abs(this.yOffset);

        GeneralPath clip = new GeneralPath();
        clip.moveTo(x0, y0);
        clip.lineTo(x0, y2);
        clip.lineTo(x1, y3);
        clip.lineTo(x3, y3);
        clip.lineTo(x3, y1);
        clip.lineTo(x2, y0);
        clip.closePath();

        // put an outline around the data area...
        Stroke outlineStroke = plot.getOutlineStroke();
        Paint outlinePaint = plot.getOutlinePaint();
        if ((outlineStroke != null) && (outlinePaint != null)) {
            g2.setStroke(outlineStroke);
            g2.setPaint(outlinePaint);
            g2.draw(clip);
        }
                
    }

    /**
     * Draws a grid line against the domain axis.
     * <P>
     * Note that this default implementation assumes that the horizontal axis is the domain axis.
     * If this is not the case, you will need to override this method.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the area for plotting data (not yet adjusted for any 3D effect).
     * @param value  the Java2D value at which the grid line should be drawn.
     *
     */
    public void drawDomainGridline(Graphics2D g2,
                                   CategoryPlot plot,
                                   Rectangle2D dataArea,
                                   double value) {

        double x0 = value;
        double x1 = value + getXOffset();
        double y0 = dataArea.getMaxY();
        double y1 = y0 - getYOffset();
        double y2 = dataArea.getMinY();
        Line2D line1 = new Line2D.Double(x0, y0, x1, y1);
        Line2D line2 = new Line2D.Double(x1, y1, x1, y2);
        Paint paint = plot.getDomainGridlinePaint();
        Stroke stroke = plot.getDomainGridlineStroke();
        g2.setPaint(paint != null ? paint : Plot.DEFAULT_OUTLINE_PAINT);
        g2.setStroke(stroke != null ? stroke : Plot.DEFAULT_OUTLINE_STROKE);
        g2.draw(line1);
        g2.draw(line2);

    }

    /**
     * Draws a grid line against the range axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param dataArea  the area for plotting data (not yet adjusted for any 3D effect).
     * @param value  the value at which the grid line should be drawn.
     *
     */
    public void drawRangeGridline(Graphics2D g2,
                                  CategoryPlot plot,
                                  ValueAxis axis,
                                  Rectangle2D dataArea,
                                  double value) {

        Range range = axis.getRange();

        if (!range.contains(value)) {
            return;
        }

        Rectangle2D adjusted = new Rectangle2D.Double(dataArea.getX(),
                                                      dataArea.getY() + getYOffset(),
                                                      dataArea.getWidth() - getXOffset(),
                                                      dataArea.getHeight() - getYOffset());
                                                      
        double y0 = axis.translateValueToJava2D(value, adjusted);
        double y1 = y0 - getYOffset();
        double x0 = dataArea.getMinX();
        double x1 = x0 + getXOffset();
        double x2 = dataArea.getMaxX();
        Line2D line1 = new Line2D.Double(x0, y0, x1, y1);
        Line2D line2 = new Line2D.Double(x1, y1, x2, y1);
        Paint paint = plot.getRangeGridlinePaint();
        Stroke stroke = plot.getRangeGridlineStroke();
        g2.setPaint(paint != null ? paint : Plot.DEFAULT_OUTLINE_PAINT);
        g2.setStroke(stroke != null ? stroke : Plot.DEFAULT_OUTLINE_STROKE);
        g2.draw(line1);
        g2.draw(line2);

    }

    /**
     * Draws a range marker.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param marker  the marker.
     * @param dataArea  the area for plotting data (not including 3D effect).
     */
    public void drawRangeMarker(Graphics2D g2,
                                CategoryPlot plot,
                                ValueAxis axis,
                                Marker marker,
                                Rectangle2D dataArea) {

        double value = marker.getValue();
        Range range = axis.getRange();
        if (!range.contains(value)) {
            return;
        }
        
        Rectangle2D adjusted = new Rectangle2D.Double(dataArea.getX(),
                                                      dataArea.getY() + getYOffset(),
                                                      dataArea.getWidth() - getXOffset(),
                                                      dataArea.getHeight() - getYOffset());
                                                      

        float y = (float) axis.translateValueToJava2D(marker.getValue(), adjusted);
        float x = (float) dataArea.getX();
        GeneralPath path = new GeneralPath();
        path.moveTo(x, y);
        path.lineTo(x + (float) this.xOffset,
                    y - (float) this.yOffset);
        path.lineTo((float) (adjusted.getMaxX() + this.xOffset),
                             y - (float) this.yOffset);
        path.lineTo((float) (adjusted.getMaxX()), y);
        path.closePath();
        g2.setPaint(marker.getPaint());
        g2.fill(path);
        g2.setPaint(marker.getOutlinePaint());
        g2.draw(path);

    }

    /**
     * Draws a 3D bar to represent one data item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area for plotting the data.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param data  the dataset.
     * @param dataset  the dataset index (zero-based).
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    public void drawItem(Graphics2D g2,
                         Rectangle2D dataArea,
                         CategoryPlot plot,
                         CategoryAxis domainAxis,
                         ValueAxis rangeAxis,
                         CategoryDataset data,
                         int dataset,
                         int row,
                         int column) {

        // check the value we are plotting...
        Number value = data.getValue(row, column);
        if (value != null) {

            Rectangle2D adjusted = new Rectangle2D.Double(dataArea.getX(),
                                                          dataArea.getY() + getYOffset(),
                                                          dataArea.getWidth() - getXOffset(),
                                                          dataArea.getHeight() - getYOffset());
                                                          
            // BAR X
            double x0 = domainAxis.getCategoryStart(column, getColumnCount(), adjusted);

            int seriesCount = getRowCount();
            int categoryCount = getColumnCount();
            if (seriesCount > 1) {
                double seriesGap = adjusted.getWidth() * getItemMargin()
                                   / (categoryCount * (seriesCount - 1));
                x0 = x0 + row * (getBarWidth() + seriesGap);
            }
            else {
                x0 = x0 + row * getBarWidth();
            }

            // BAR Y
            double y = value.doubleValue();
            double base = 0.0;
            double lclip = getLowerClip();
            double uclip = getUpperClip();

            if (uclip <= 0.0) {  // cases 1, 2, 3 and 4
                if (y >= uclip) {
                    return; // bar is not visible
                }
                base = uclip;
                if (y <= lclip) {
                    y = lclip;
                }
            }
            else {
                if (lclip <= 0.0) { // cases 5, 6, 7 and 8
                    if (y >= uclip) {
                       y = uclip;
                    }
                    else {
                        if (y <= lclip) {
                            y = lclip;
                        }
                    }
                }
                else { // cases 9, 10, 11 and 12
                    if (y <= lclip) {
                        return; // bar is not visible
                    }
                    base = lclip;
                    if (y >= uclip) {
                        y = uclip;
                    }
                }
            }

            double transY1 = rangeAxis.translateValueToJava2D(base, adjusted);
            double transY2 = rangeAxis.translateValueToJava2D(y, adjusted);
            double y2 = Math.min(transY1, transY2);

            double x1 = x0 + getBarWidth();
            double y0 = Math.max(transY1, transY2);

            double x2 = x0 + this.xOffset;
            double x3 = x1 + this.xOffset;
            double y1 = y0 - this.yOffset;
            double y3 = y2 - this.yOffset;
            
            Rectangle2D bar = new Rectangle2D.Double(x0, y2, x1 - x0, y0 - y2);
            Paint itemPaint = null;
            if (!getUseCategoriesPaint()) {
                itemPaint = getItemPaint(dataset, row, column);
            }
            else {
                itemPaint = getCategoryPaint(column);
            }
            g2.setPaint(itemPaint);
            g2.fill(bar);

            GeneralPath bar3dRight = null;
            GeneralPath bar3dTop = null;
            VerticalAxis vAxis = (VerticalAxis) plot.getRangeAxis();
            if ((y0 - y2) != 0 && vAxis instanceof VerticalNumberAxis3D) {
                bar3dRight = new GeneralPath();
                bar3dRight.moveTo((float) x1, (float) y2);
                bar3dRight.lineTo((float) x1, (float) y0);
                bar3dRight.lineTo((float) x3, (float) y1);
                bar3dRight.lineTo((float) x3, (float) y3);
                bar3dRight.closePath();

                if (itemPaint instanceof Color) {
                    g2.setPaint(((Color) itemPaint).darker());
                }
                g2.fill(bar3dRight);

                bar3dTop = new GeneralPath();
                bar3dTop.moveTo((float) x0, (float) y2);
                bar3dTop.lineTo((float) x2, (float) y3);
                bar3dTop.lineTo((float) x3, (float) y3);
                bar3dTop.lineTo((float) x1, (float) y2);
                bar3dTop.closePath();
                g2.fill(bar3dTop);
            }

            if (getBarWidth() > 3) {
                g2.setStroke(getItemStroke(dataset, row, column));
                g2.setPaint(getItemOutlinePaint(dataset, row, column));
                g2.draw(bar);
                if (bar3dRight != null) {
                    g2.draw(bar3dRight);
                }
                if (bar3dTop != null) {
                    g2.draw(bar3dTop);
                }
                if (plot.getValueLabelsVisible()) {
                    Font labelFont = plot.getValueLabelFont();
                    g2.setFont(labelFont);
                    Paint paint = plot.getValueLabelPaint();
                    g2.setPaint(paint);

                    NumberFormat formatter = plot.getValueLabelFormatter();
                    String s = formatter.format(value);
                    java.awt.FontMetrics fm = g2.getFontMetrics();
                    int ix = (int) ((getBarWidth() - fm.stringWidth(s)) / 2);
                    g2.drawString(s, (int) (x0 + this.xOffset + ix),
                                     (int) (y2 - this.yOffset - 5));

                }
            }

            // collect entity and tool tip information...
            if (getInfo() != null) {
                EntityCollection entities = getInfo().getEntityCollection();
                if (entities != null) {
                    GeneralPath barOutline = new GeneralPath();
                    barOutline.moveTo((float) x0, (float) y0);
                    barOutline.lineTo((float) x0, (float) y2);
                    barOutline.lineTo((float) x2, (float) y3);
                    barOutline.lineTo((float) x3, (float) y3);
                    barOutline.lineTo((float) x3, (float) y1);
                    barOutline.lineTo((float) x1, (float) y0);
                    barOutline.closePath();
                    
                    String tip = null;
                    if (getToolTipGenerator() != null) {
                        tip = getToolTipGenerator().generateToolTip(data, row, column);
                    }
                    String url = null;
                    if (getURLGenerator() != null) {
                        url = getURLGenerator().generateURL(data, row, column);
                    }
                    CategoryItemEntity entity
                        = new CategoryItemEntity(barOutline, tip, url, row,
                                                 data.getColumnKey(column), column);
                    entities.addEntity(entity);
                }
            }
        }

    }

}
