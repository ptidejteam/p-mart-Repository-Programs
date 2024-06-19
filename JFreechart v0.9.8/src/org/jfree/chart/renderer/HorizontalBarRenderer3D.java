/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * ----------------------------
 * HorizontalBarRenderer3D.java
 * ----------------------------
 * (C) Copyright 2002, 2003 by Tin Luu and Contributors.
 *
 * Original Author:  Tin Luu (based on VerticalBarRenderer3D.java);
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *                   Richard Atkinson;
 *                   Tomer Peretz;
 *                   Rich Unger;
 *
 * $Id: HorizontalBarRenderer3D.java,v 1.1 2007/10/10 20:03:12 vauchers Exp $
 *
 * Changes
 * -------
 * 15-May-2002 : Version 1, contributed by Tin Luu based on VerticalBarRenderer3D code (DG);
 * 13-Jun-2002 : Added check to make sure marker is visible before drawing it (DG);
 * 19-Jun-2002 : Added code to draw labels on bars (TL);
 * 26-Jun-2002 : Implemented bar clipping to avoid PRExceptions (DG);
 * 05-Aug-2002 : Small modification to drawCategoryItem method to support URLs for HTML image
 *               maps (RA);
 * 08-Aug-2002 : Integrated fix for bar effect when alpha=1.0, and improved value labels, submitted
 *               by Tomer Peretz (DG);
 * 19-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 20-Sep-2002 : Added fix for categoryPaint by Rich Unger (DG);
 * 10-Oct-2002 : Added new constructors (DG);
 * 24-Oct-2002 : Amendments for changes in CategoryDataset interface and CategoryToolTipGenerator
 *               interface (DG);
 * 05-Nov-2002 : Base dataset is now TableDataset not CategoryDataset (DG);
 * 14-Nov-2002 : Changed 3D effect to separate x and y offsets (DG);
 * 17-Jan-2003 : Moved plot classes to a separate package (DG);
 * 28-Jan-2003 : Fixed adjusted areas, and added wall paint (DG);
 * 10-Apr-2003 : Removed category paint usage (DG);
 *
 */

package org.jfree.chart.renderer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.font.LineMetrics;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.Effect3D;
import org.jfree.chart.Marker;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.HorizontalAxis;
import org.jfree.chart.axis.HorizontalNumberAxis3D;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.tooltips.CategoryToolTipGenerator;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.data.KeyedValues2DDataset;
import org.jfree.data.Range;
import org.jfree.io.SerialUtilities;

/**
 * A renderer that handles the drawing of 3D bars for a horizontal bar plot.
 * <p>
 * For use with the {@link org.jfree.chart.plot.HorizontalCategoryPlot} class.
 *
 * @author Tin Luu
 */
public class HorizontalBarRenderer3D extends HorizontalBarRenderer 
                                     implements Effect3D, Serializable {

    /** The default x-offset for the 3D effect. */
    public static final double DEFAULT_X_OFFSET = 10.0;

    /** The default y-offset for the 3D effect. */
    public static final double DEFAULT_Y_OFFSET = 10.0;

    /** The default wall paint. */
    public static final Paint DEFAULT_WALL_PAINT = new Color(0xDD, 0xDD, 0xDD);

    /** The size of x-offset for the 3D effect. */
    private double xOffset;

    /** The size of y-offset for the 3D effect. */
    private double yOffset;

    /** The gap in pixels between the end of the bar and the value's text. */
    private int valuesGap = 2;

    /** The clipping area. */
    private transient Area hiddenClip;

    /** The paint used to shade the left and lower 3D wall. */
    private transient Paint wallPaint;
    
    /**
     * Default constructor.
     */
    public HorizontalBarRenderer3D() {
        this(DEFAULT_X_OFFSET, DEFAULT_Y_OFFSET);
    }

    /**
     * Creates a new renderer.
     *
     * @param xOffset  the x-offset for the 3D effect.
     * @param yOffset  the y-offset for the 3D effect.
     */
    public HorizontalBarRenderer3D(double xOffset, double yOffset) {
        this(xOffset, yOffset, null, null);
    }

    /**
     * Constructs a new renderer.
     *
     * @param xOffset  the x-offset for the 3D effect.
     * @param yOffset  the y-offset for the 3D effect.
     * @param toolTipGenerator  the tooltip generator.
     * @param urlGenerator  the URL generator.
     */
    public HorizontalBarRenderer3D(double xOffset, double yOffset,
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

        // draw background image...
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

        double y0 = value;
        double y1 = value - getYOffset();
        double x0 = dataArea.getMinX();
        double x1 = x0 + getXOffset();
        double x2 = dataArea.getMaxY();
        Line2D line1 = new Line2D.Double(x0, y0, x1, y1);
        Line2D line2 = new Line2D.Double(x1, y1, x2, y1);
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
        double x0 = axis.translateValueToJava2D(value, adjusted);
        double x1 = x0 + getXOffset();
        double y0 = dataArea.getMaxY();
        double y1 = y0 - getYOffset();
        double y2 = dataArea.getMinY();
        Line2D line1 = new Line2D.Double(x0, y0, x1, y1);
        Line2D line2 = new Line2D.Double(x1, y1, x1, y2);
        Paint paint = plot.getRangeGridlinePaint();
        Stroke stroke = plot.getRangeGridlineStroke();
        g2.setPaint(paint != null ? paint : Plot.DEFAULT_OUTLINE_PAINT);
        g2.setStroke(stroke != null ? stroke : Plot.DEFAULT_OUTLINE_STROKE);
        g2.draw(line1);
        g2.draw(line2);

    }

    /**
     * Draws a vertical line across the chart to represent the marker.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param marker  the marker line.
     * @param dataArea  the axis data area.
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
        float x = (float) axis.translateValueToJava2D(marker.getValue(), adjusted);
        float y = (float) adjusted.getMaxY();
        GeneralPath path = new GeneralPath();
        path.moveTo(x, y);
        path.lineTo((float) (x + this.xOffset), y - (float) this.yOffset);
        path.lineTo((float) (x + this.xOffset), (float) (adjusted.getMinY() - this.yOffset));
        path.lineTo(x, (float) adjusted.getMinY());
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
     * @param dataArea  the data area.
     * @param plot  the plot.
     * @param domainAxis  the domain (category) axis.
     * @param rangeAxis  the range (value) axis.
     * @param data  the data.
     * @param dataset  the dataset index (zero-based).
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    public void drawItem(Graphics2D g2,
                         Rectangle2D dataArea,
                         CategoryPlot plot,
                         CategoryAxis domainAxis,
                         ValueAxis rangeAxis,
                         KeyedValues2DDataset data,
                         int dataset,
                         int row,
                         int column) {

        if (row == 0 && column == 0) {
            hiddenClip = new Area(g2.getClip());
        }

        // first check the value we are plotting...
        Number dataValue = data.getValue(row, column);
        if (dataValue != null) {

            Rectangle2D adjusted = new Rectangle2D.Double(dataArea.getX(),
                                                          dataArea.getY() + getYOffset(),
                                                          dataArea.getWidth() - getXOffset(),
                                                          dataArea.getHeight() - getYOffset());

            // X
            double value = dataValue.doubleValue();
            double base = 0.0;
            double lclip = getLowerClip();
            double uclip = getUpperClip();

            if (uclip <= 0.0) {  // cases 1, 2, 3 and 4
                if (value >= uclip) {
                    return; // bar is not visible
                }
                base = uclip;
                if (value <= lclip) {
                    value = lclip;
                }
            }
            else if (lclip <= 0.0) { // cases 5, 6, 7 and 8
                if (value >= uclip) {
                    value = uclip;
                }
                else {
                    if (value <= lclip) {
                        value = lclip;
                    }
                }
            }
            else { // cases 9, 10, 11 and 12
                if (value <= lclip) {
                   return; // bar is not visible
                }
                base = lclip;
                if (value >= uclip) {
                    value = uclip;
                }
            }

            double transX1 = rangeAxis.translateValueToJava2D(base, adjusted);
            double transX2 = rangeAxis.translateValueToJava2D(value, adjusted);
            double x0 = Math.min(transX1, transX2);
            double x2 = Math.max(transX1, transX2);
            double x1 = x0 + this.xOffset;
            double x3 = x2 + this.xOffset;

            // Y
            double y2 = domainAxis.getCategoryStart(column, getColumnCount(), adjusted);

            int seriesCount = getRowCount();
            int categoryCount = getColumnCount();
            if (seriesCount > 1) {
                double seriesGap = dataArea.getHeight() * getItemMargin()
                                   / (categoryCount * (seriesCount - 1));
                y2 = y2 + row * (getBarWidth() + seriesGap);
            }
            else {
                y2 = y2 + row * getBarWidth();
            }

            // HEIGHT
            double y0 = y2 + getBarWidth();

            double y1 = y0 - this.yOffset;
            double y3 = y2 - this.yOffset;
            
            // draw the bar...
            if (plot.getForegroundAlpha() == 1.0) {
                g2.setClip(hiddenClip);
            }
            Rectangle2D bar = new Rectangle2D.Double(x0, y2, x2 - x0, y0 - y2);

            // choose the color...
//            Paint itemPaint = null;
//            if (!getUseCategoriesPaint()) {
//                itemPaint = getItemPaint(dataset, row, column);
//            }
//            else {
//                itemPaint = getCategoryPaint(column);
//            }
            g2.setPaint(getItemPaint(dataset, row, column));
            g2.fill(bar);

            hiddenClip.subtract(new Area(bar));
            GeneralPath bar3dRight = null;
            GeneralPath bar3dTop = null;
            double effect3d = 0.00;
            HorizontalAxis hAxis = (HorizontalAxis) plot.getRangeAxis();
            if ((x2 - x0) != 0 && hAxis instanceof HorizontalNumberAxis3D) {
                bar3dRight = new GeneralPath();

                bar3dRight.moveTo((float) x2, (float) y2);
                bar3dRight.lineTo((float) x2, (float) y0);
                bar3dRight.lineTo((float) x3, (float) y1);
                bar3dRight.lineTo((float) x3, (float) y3);
                bar3dRight.closePath();
                
                g2.fill(bar3dRight);
                hiddenClip.subtract(new Area(bar3dRight));

                bar3dTop = new GeneralPath();

                bar3dTop.moveTo((float) x0, (float) y2);
                bar3dTop.lineTo((float) x1, (float) y3);
                bar3dTop.lineTo((float) x3, (float) y3);
                bar3dTop.lineTo((float) x2, (float) y2);
                bar3dTop.closePath();
                g2.fill(bar3dTop);
                hiddenClip.subtract(new Area(bar3dTop));
            }

            if (getBarWidth() > BAR_OUTLINE_WIDTH_THRESHOLD) {
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
                    String str = plot.getValueLabelFormatter().format(dataValue.doubleValue());
                    LineMetrics lm
                        = plot.getValueLabelFont().getLineMetrics(str, g2.getFontRenderContext());
                    double middleOffset = (y0 - y2 + lm.getAscent() - lm.getDescent()) / 2;
                    double stringOffset
                        = labelFont.getStringBounds(str, g2.getFontRenderContext()).getWidth();
                    double stringX = x2 - stringOffset - valuesGap;
                    g2.drawString(str, (int) stringX, (int) (y2 + middleOffset));
                }
            }

            // collect entity and tool tip information...
            if (getInfo() != null) {
                EntityCollection entities = getInfo().getEntityCollection();
                if (entities != null) {
                    String tip = null;
                    if (getToolTipGenerator() != null) {
                        tip = getToolTipGenerator().generateToolTip(data, row, column);
                    }
                    String url = null;
                    if (getURLGenerator() != null) {
                        url = getURLGenerator().generateURL(data, row, column);
                    }
                    CategoryItemEntity entity = new CategoryItemEntity(bar, tip,
                        url, row, data.getColumnKey(column), column);
                    entities.addEntity(entity);
                }
            }

        }

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
        SerialUtilities.writePaint(this.wallPaint, stream);
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
        this.wallPaint = SerialUtilities.readPaint(stream);
    }

}
