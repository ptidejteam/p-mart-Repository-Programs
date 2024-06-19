/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * -------------------
 * StandardLegend.java
 * -------------------
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Andrzej Porebski;
 *                   Luke Quinane;
 *
 * $Id: StandardLegend.java,v 1.1 2007/10/10 19:25:38 vauchers Exp $
 *
 * Changes (from 20-Jun-2001)
 * --------------------------
 * 20-Jun-2001 : Modifications submitted by Andrzej Porebski for legend placement;
 * 18-Sep-2001 : Updated header and fixed DOS encoding problem (DG);
 * 16-Oct-2001 : Moved data source classes to com.jrefinery.data.* (DG);
 * 19-Oct-2001 : Moved some methods [getSeriesPaint(...) etc.] from JFreeChart to Plot (DG);
 * 22-Jan-2002 : Fixed bug correlating legend labels with pie data (DG);
 * 06-Feb-2002 : Bug fix for legends in small areas (DG);
 * 23-Apr-2002 : Legend item labels are now obtained from the plot, not the chart (DG);
 * 20-Jun-2002 : Added outline paint and stroke attributes for the key boxes (DG);
 * 18-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 23-Sep-2002 : Changed the name of LegendItem --> DrawableLegendItem (DG);
 * 02-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 16-Oct-2002 : Adjusted vertical text position in legend item (DG);
 * 17-Oct-2002 : Fixed bug where legend items are not using the font that has been set (DG);
 * 11-Feb-2003 : Added title code by Donald Mitchell, removed unnecessary constructor (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 22-Sep-2003 : Added nullpointer checks (TM);
 * 23-Sep-2003 : Fixed bug in equals(...) method (DG);
 * 08-Oct-2003 : Applied patch for displaying series line style, contributed by Luke Quinane (DG);
 * 23-Dec-2003 : Added scale factors (x and y) for shapes displayed in legend (DG);
 * 
 */

package org.jfree.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.event.LegendChangeEvent;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ObjectUtils;

/**
 * A chart legend shows the names and visual representations of the series
 * that are plotted in a chart.
 *
 * @author David Gilbert
 */
public class StandardLegend extends Legend implements Serializable {

    /** The default outer gap. */
    public static final Spacer DEFAULT_OUTER_GAP = new Spacer(Spacer.ABSOLUTE, 3, 3, 3, 3);

    /** The default inner gap. */
    public static final Spacer DEFAULT_INNER_GAP = new Spacer(Spacer.ABSOLUTE, 2, 2, 2, 2);

    /** The default outline stroke. */
    public static final Stroke DEFAULT_OUTLINE_STROKE = new BasicStroke();

    /** The default outline paint. */
    public static final Paint DEFAULT_OUTLINE_PAINT = Color.gray;

    /** The default background paint. */
    public static final Paint DEFAULT_BACKGROUND_PAINT = Color.white;

    /** The default title font. */
    public static final Font DEFAULT_TITLE_FONT = new Font("SansSerif", Font.BOLD, 11);

    /** The default item font. */
    public static final Font DEFAULT_ITEM_FONT = new Font("SansSerif", Font.PLAIN, 10);

    /** The amount of blank space around the legend. */
    private Spacer outerGap;

    /** The pen/brush used to draw the outline of the legend. */
    private transient Stroke outlineStroke;

    /** The color used to draw the outline of the legend. */
    private transient Paint outlinePaint;

    /** The color used to draw the background of the legend. */
    private transient Paint backgroundPaint;

    /** The blank space inside the legend box. */
    private Spacer innerGap;

    /** An optional title for the legend. */
    private String title;

    /** The font used to display the legend title. */
    private Font titleFont;

    /** The font used to display the legend item names. */
    private Font itemFont;

    /** The color used to display the legend item names. */
    private transient Paint itemPaint;

    /** A flag controlling whether or not outlines are drawn around shapes.*/
    private boolean outlineShapes;

    /** The stroke used to outline item shapes. */
    private transient Stroke shapeOutlineStroke = new BasicStroke(0.5f);

    /** The paint used to outline item shapes. */
    private transient Paint shapeOutlinePaint = Color.lightGray;

    /** A flag that controls whether the legend displays the series shapes. */
    private boolean displaySeriesShapes;
    
    /** The x scale factor for shapes displayed in the legend. */
    private double shapeScaleX = 1.0;
    
    /** The y scale factor for shapes displayed in the legend. */
    private double shapeScaleY = 1.0;

    /** A flag that controls whether the legend displays the series line */
    private boolean displaySeriesLines;

    /**
     * Constructs a new legend with default settings.
     */
    public StandardLegend() {

        this.outerGap = DEFAULT_OUTER_GAP;
        this.innerGap = DEFAULT_INNER_GAP;
        this.backgroundPaint = DEFAULT_BACKGROUND_PAINT;
        this.outlineStroke = DEFAULT_OUTLINE_STROKE;
        this.outlinePaint = DEFAULT_OUTLINE_PAINT;
        this.title = null;
        this.titleFont = DEFAULT_TITLE_FONT;
        this.itemFont = DEFAULT_ITEM_FONT;
        this.itemPaint = Color.black;
        this.displaySeriesShapes = false;
        this.displaySeriesLines = false;
    }

    /**
     * Creates a new legend.
     *
     * @param chart  the chart that the legend belongs to.
     * 
     * @deprecated use the default constructor instead and let JFreeChart manage
     * the chart reference
     */
    public StandardLegend(JFreeChart chart) {
        this();
    }

    /**
     * Returns the outer gap for the legend.
     * <P>
     * This is the amount of blank space around the outside of the legend.
     *
     * @return The gap.
     */
    public Spacer getOuterGap() {
        return this.outerGap;
    }

    /**
     * Sets the outer gap for the legend.  A {@link LegendChangeEvent} is sent to all
     * registered listeners.
     *
     * @param outerGap  the outer gap (<code>null</code> not permitted).
     */
    public void setOuterGap(Spacer outerGap) {
        if (outerGap == null) {
            throw new NullPointerException("StandardLegend.setOuterGap(..): Null argument.");
        }
        this.outerGap = outerGap;
        notifyListeners(new LegendChangeEvent(this));
    }


    /**
     * Returns the inner gap for the legend.
     * <P>
     * This is the amount of blank space around the inside of the legend.
     *
     * @return The gap.
     */
    public Spacer getInnerGap() {
        return innerGap;
    }

    /**
     * Sets the inner gap for the legend.  A {@link LegendChangeEvent} is sent to all
     * registered listeners.
     *
     * @param innerGap  the inner gap (<code>null</code> not permitted).
     */
    public void setInnerGap(Spacer innerGap) {
        if (innerGap == null) {
            throw new NullPointerException("StandardLegend.setInnerGap(..): Null argument.");
        }
        this.innerGap = innerGap;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Returns the background color for the legend.
     *
     * @return The background color.
     */
    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    /**
     * Sets the background color of the legend.  A {@link LegendChangeEvent} is sent to all 
     * registered listeners.
     *
     * @param paint  the new background color.
     */
    public void setBackgroundPaint(Paint paint) {
        this.backgroundPaint = paint;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Returns the outline pen/brush.
     *
     * @return The outline pen/brush.
     */
    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    /**
     * Sets the outline pen/brush.  A {@link LegendChangeEvent} is sent to all registered 
     * listeners.
     *
     * @param stroke  the new outline pen/brush (<code>null</code> not permitted).
     */
    public void setOutlineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new NullPointerException("StandardLegend.setOutlineStroke(..): null argument");
        }
        this.outlineStroke = stroke;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Returns the outline color.
     *
     * @return The outline color.
     */
    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    /**
     * Sets the outline color.  A {@link LegendChangeEvent} is sent to all registered listeners.
     *
     * @param paint  the new outline color.
     */
    public void setOutlinePaint(Paint paint) {
        this.outlinePaint = paint;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Gets the title for the legend.
     *
     * @return The title of the legend; which may be null.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the legend.
     *
     * @param title The title to use (<code>null</code> permitted).
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the title font.
     *
     * @return The font.
     */
    public Font getTitleFont() {
        return this.titleFont;
    }

    /**
     * Sets the title font.
     *
     * @param font  the new font.
     */
    public void setTitleFont(Font font) {
        this.titleFont = font;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Returns the series label font.
     *
     * @return The series label font.
     */
    public Font getItemFont() {
        return this.itemFont;
    }

    /**
     * Sets the series label font.  A {@link LegendChangeEvent} is sent to all registered 
     * listeners.
     *
     * @param font  the new series label font.
     */
    public void setItemFont(Font font) {
        this.itemFont = font;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Returns the series label color.
     *
     * @return The series label color.
     */
    public Paint getItemPaint() {
        return this.itemPaint;
    }

    /**
     * Sets the series label color.  A {@link LegendChangeEvent} is sent to all registered 
     * listeners.
     *
     * @param paint  the new series label color.
     */
    public void setItemPaint(Paint paint) {
        this.itemPaint = paint;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Returns the flag that indicates whether or not outlines are drawn around shapes.
     *
     * @return The flag.
     */
    public boolean getOutlineShapes() {
        return this.outlineShapes;
    }

    /**
     * Sets the flag that controls whether or not outlines are drawn around shapes.
     *
     * @param flag  the flag.
     */
    public void setOutlineShapes(boolean flag) {
        this.outlineShapes = flag;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Returns the stroke used to outline shapes.
     *
     * @return The stroke.
     */
    public Stroke getShapeOutlineStroke() {
        return this.shapeOutlineStroke;
    }

    /**
     * Sets the stroke used to outline shapes.  A {@link LegendChangeEvent} is sent to all 
     * registered listeners.
     *
     * @param stroke  the stroke (<code>null</code> not permitted).
     */
    public void setShapeOutlineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new NullPointerException(
                "StandardLegend.setShapeOutlineStroke(..): null argument");
        }
        this.shapeOutlineStroke = stroke;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Returns the paint used to outline shapes.
     *
     * @return The paint.
     */
    public Paint getShapeOutlinePaint() {
        return this.shapeOutlinePaint;
    }

    /**
     * Sets the paint used to outline shapes.  A {@link LegendChangeEvent} is sent to all
     * registered listeners.
     *
     * @param paint  the paint.
     */
    public void setShapeOutlinePaint(Paint paint) {
        this.shapeOutlinePaint = paint;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Sets a flag that controls whether or not the legend displays the series shapes.
     *
     * @param flag  the new value of the flag.
     */
    public void setDisplaySeriesShapes(boolean flag) {
        this.displaySeriesShapes = flag;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Returns a flag that controls whether or not the legend displays the series shapes.
     *
     * @return <code>true</code> if the series shapes should be displayed, <code>false</code> 
     *         otherwise.
     */
    public boolean getDisplaySeriesShapes() {
        return this.displaySeriesShapes;
    }
    
    /**
     * Returns the x scale factor for shapes displayed in the legend.
     * 
     * @return the x scale factor.
     */
    public double getShapeScaleX() {
        return this.shapeScaleX;
    }
    
    /**
     * Sets the x scale factor for shapes displayed in the legend and sends a
     * {@link LegendChangeEvent} to all registered listeners.
     * 
     * @param factor  the factor.
     */
    public void setShapeScaleX(double factor) {
        this.shapeScaleX = factor;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Returns the y scale factor for shapes displayed in the legend.
     * 
     * @return the y scale factor.
     */
    public double getShapeScaleY() {
        return this.shapeScaleY;
    }
    
    /**
     * Sets the y scale factor for shapes displayed in the legend and sends a
     * {@link LegendChangeEvent} to all registered listeners.
     * 
     * @param factor  the factor.
     */
    public void setShapeScaleY(double factor) {
        this.shapeScaleY = factor;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Sets a flag that controls whether or not the legend displays the series line stroke.
     *
     * @param flag  the new value of the flag.
     */
    public void setDisplaySeriesLines(boolean flag) {
        this.displaySeriesLines = flag;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Returns a flag that controls whether or not the legend displays the series line stroke.
     *
     * @return <code>true</code> if the series lines should be displayed, <code>false</code> 
     *         otherwise.
     */
    public boolean getDisplaySeriesLines() {
        return this.displaySeriesLines;
    }

    /**
     * Draws the legend on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param available  the area within which the legend, and afterwards the plot, should be
     *                   drawn.
     * @param info  collects rendering info (optional).
     *
     * @return The area used by the legend.
     */
    public Rectangle2D draw(Graphics2D g2, Rectangle2D available, ChartRenderingInfo info) {

        return draw(g2, available,
                    (getAnchor() & HORIZONTAL) != 0, (getAnchor() & INVERTED) != 0,
                    info);

    }

    /**
     * Draws the legend.
     *
     * @param g2  the graphics device.
     * @param available  the area available for drawing the chart.
     * @param horizontal  a flag indicating whether the legend items are laid out horizontally.
     * @param inverted ???
     * @param info  collects rendering info (optional).
     *
     * @return The remaining available drawing area.
     */
    protected Rectangle2D draw(Graphics2D g2, Rectangle2D available,
                               boolean horizontal, boolean inverted,
                               ChartRenderingInfo info) {

        LegendItemCollection legendItems = getChart().getPlot().getLegendItems();

        if ((legendItems != null) && (legendItems.getItemCount() > 0)) {

            DrawableLegendItem legendTitle = null;

            Rectangle2D legendArea = new Rectangle2D.Double();
            double availableWidth = available.getWidth();
            double availableHeight = available.getHeight();

            // the translation point for the origin of the drawing system
            Point2D translation = new Point2D.Double();

            // Create buffer for individual rectangles within the legend
            DrawableLegendItem[] items = new DrawableLegendItem[legendItems.getItemCount()];

            // Compute individual rectangles in the legend, translation point as well
            // as the bounding box for the legend.
            if (horizontal) {
                double xstart = available.getX() + getOuterGap().getLeftSpace(availableWidth);
                double xlimit = available.getMaxX()
                    + getOuterGap().getRightSpace(availableWidth) - 1;
                double maxRowWidth = 0;
                double xoffset = 0;
                double rowHeight = 0;
                double totalHeight = 0;
                boolean startingNewRow = true;


                if (title != null && !title.equals("")) {

                    g2.setFont(getTitleFont());

                    LegendItem titleItem = new LegendItem(title,
                        title,
                        null,
                        Color.black,
                        DEFAULT_OUTLINE_PAINT,
                        DEFAULT_OUTLINE_STROKE);

                    legendTitle = createDrawableLegendItem(g2, titleItem,
                        xoffset,
                        totalHeight);

                    rowHeight = Math.max(rowHeight, legendTitle.getHeight());
                    xoffset += legendTitle.getWidth();
                }

                g2.setFont(itemFont);
                for (int i = 0; i < legendItems.getItemCount(); i++) {
                    items[i] = createDrawableLegendItem(g2, legendItems.get(i),
                        xoffset, totalHeight);
                    if ((!startingNewRow)
                        && (items[i].getX() + items[i].getWidth() + xstart > xlimit)) {

                        maxRowWidth = Math.max(maxRowWidth, xoffset);
                        xoffset = 0;
                        totalHeight += rowHeight;
                        i--;
                        startingNewRow = true;

                    }
                    else {
                        rowHeight = Math.max(rowHeight, items[i].getHeight());
                        xoffset += items[i].getWidth();
                        startingNewRow = false;
                    }
                }

                maxRowWidth = Math.max(maxRowWidth, xoffset);
                totalHeight += rowHeight;

                // Create the bounding box
                legendArea = new Rectangle2D.Double(0, 0, maxRowWidth, totalHeight);

                // The yloc point is the variable part of the translation point
                // for horizontal legends. xloc is constant.
                double yloc = (inverted)
                    ? available.getMaxY() - totalHeight
                    - getOuterGap().getBottomSpace(availableHeight)
                    : available.getY() + getOuterGap().getTopSpace(availableHeight);
                double xloc = available.getX() + available.getWidth() / 2 - maxRowWidth / 2;

                // Create the translation point
                translation = new Point2D.Double(xloc, yloc);
            }
            else {  // vertical...
                double totalHeight = 0;
                double maxWidth = 0;

                if (title != null && !title.equals("")) {

                    g2.setFont(getTitleFont());

                    LegendItem titleItem = new LegendItem(title,
                        title,
                        null,
                        Color.black,
                        DEFAULT_OUTLINE_PAINT,
                        DEFAULT_OUTLINE_STROKE);

                    legendTitle = createDrawableLegendItem(g2, titleItem, 0, totalHeight);

                    totalHeight += legendTitle.getHeight();
                    maxWidth = Math.max(maxWidth, legendTitle.getWidth());
                }

                g2.setFont(itemFont);
                for (int i = 0; i < items.length; i++) {
                    items[i] = createDrawableLegendItem(g2, legendItems.get(i), 0, totalHeight);
                    totalHeight += items[i].getHeight();
                    maxWidth = Math.max(maxWidth, items[i].getWidth());
                }

                // Create the bounding box
                legendArea = new Rectangle2D.Float(0, 0, (float) maxWidth, (float) totalHeight);

                // The xloc point is the variable part of the translation point
                // for vertical legends. yloc is constant.
                double xloc = (inverted)
                    ? available.getMaxX() - maxWidth - getOuterGap().getRightSpace(availableWidth)
                    : available.getX() + getOuterGap().getLeftSpace(availableWidth);
                double yloc = available.getY() + (available.getHeight() / 2) - (totalHeight / 2);

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

            // draw legend title
            if (legendTitle != null) {
                // XXX dsm - make title bold?
                g2.setPaint(legendTitle.getItem().getPaint());
                g2.setPaint(this.itemPaint);
                g2.setFont(getTitleFont());
                g2.drawString(legendTitle.getItem().getLabel(),
                    (float) legendTitle.getLabelPosition().getX(),
                    (float) legendTitle.getLabelPosition().getY());
            }

            EntityCollection entities = null;
            if (info != null) {
                entities = info.getEntityCollection();
            }
            // Draw individual series elements
            for (int i = 0; i < items.length; i++) {
                g2.setPaint(items[i].getItem().getPaint());
                Shape keyBox = items[i].getMarker();
                if (displaySeriesLines) {
                    g2.setStroke(items[i].getLineStroke());
                    g2.draw(items[i].getLine());

                    if (displaySeriesShapes) {
                        g2.fill(keyBox);
                    }

                } 
                else {
                    g2.fill(keyBox);
                }
                if (getOutlineShapes()) {
                    g2.setPaint(this.shapeOutlinePaint);
                    g2.setStroke(this.shapeOutlineStroke);
                    g2.draw(keyBox);
                }
                g2.setPaint(this.itemPaint);
                g2.setFont(this.itemFont);
                //g2.drawString(items[i].getItem().getLabel(),
                //              (float) items[i].getLabelPosition().getX(),
                //              (float) items[i].getLabelPosition().getY());
                RefineryUtilities.drawAlignedString(items[i].getItem().getLabel(), g2,
                    (float) items[i].getLabelPosition().getX(),
                    (float) items[i].getLabelPosition().getY(),
                    TextAnchor.CENTER_LEFT);

                if (entities != null) {
                    Rectangle2D area = new Rectangle2D.Double(translation.getX() + items[i].getX(),
                        translation.getY() + items[i].getY(),
                        items[i].getWidth(),
                        items[i].getHeight());
                    LegendItemEntity entity = new LegendItemEntity(area);
                    entity.setSeriesIndex(i);
                    entities.addEntity(entity);
                }
            }

            // translate the origin back to what it was prior to drawing the legend
            g2.translate(-translation.getX(), -translation.getY());

            if (horizontal) {
                // The remaining drawing area bounding box will have the same
                // x origin, width and height independent of the anchor's
                // location. The variable is the y coordinate. If the anchor is
                // SOUTH, the y coordinate is simply the original y coordinate
                // of the available area. If it is NORTH, we adjust original y
                // by the total height of the legend and the initial gap.
                double yy = available.getY();
                double yloc = (inverted) ? yy
                    : yy + legendArea.getHeight()
                    + getOuterGap().getBottomSpace(availableHeight);

                // return the remaining available drawing area
                return new Rectangle2D.Double(available.getX(), yloc, availableWidth,
                    availableHeight - legendArea.getHeight()
                    - getOuterGap().getTopSpace(availableHeight)
                    - getOuterGap().getBottomSpace(availableHeight));
            }
            else {
                // The remaining drawing area bounding box will have the same
                // y  origin, width and height independent of the anchor's
                // location. The variable is the x coordinate. If the anchor is
                // EAST, the x coordinate is simply the original x coordinate
                // of the available area. If it is WEST, we adjust original x
                // by the total width of the legend and the initial gap.
                double xloc = (inverted) ? available.getX()
                    : available.getX()
                    + legendArea.getWidth()
                    + getOuterGap().getLeftSpace(availableWidth)
                    + getOuterGap().getRightSpace(availableWidth);


                // return the remaining available drawing area
                return new Rectangle2D.Double(xloc, available.getY(),
                    availableWidth - legendArea.getWidth()
                    - getOuterGap().getLeftSpace(availableWidth)
                    - getOuterGap().getRightSpace(availableWidth),
                    availableHeight);
            }
        }
        else {
            return available;
        }
    }

    /**
     * Returns a rectangle surrounding a individual entry in the legend.
          * <P>
     * The marker box for each entry will be positioned next to the name of the
     * specified series within the legend area.  The marker box will be square
     * and 70% of the height of current font.
     *
     * @param graphics  the graphics context (supplies font metrics etc.).
     * @param legendItem  the legend item.
     * @param x  the upper left x coordinate for the bounding box.
     * @param y  the upper left y coordinate for the bounding box.
     *
     * @return A legend item encapsulating all necessary info for drawing.
     */
    private DrawableLegendItem createDrawableLegendItem(Graphics2D graphics,
                                                        LegendItem legendItem,
                                                        double x, double y) {

        int innerGap = 2;
        FontMetrics fm = graphics.getFontMetrics();
        LineMetrics lm = fm.getLineMetrics(legendItem.getLabel(), graphics);
        float textAscent = lm.getAscent();
        float lineHeight = textAscent + lm.getDescent() + lm.getLeading();

        DrawableLegendItem item = new DrawableLegendItem(legendItem);

        float xLabelLoc = (float) (x + innerGap + 1.15f * lineHeight);
        //float yloc = (float) (y + innerGap + 0.15f * lineHeight + textAscent);
        float yLabelLoc = (float) (y + innerGap + 0.5f * lineHeight);

        item.setLabelPosition(new Point2D.Float(xLabelLoc, yLabelLoc));

        float width = (float) (item.getLabelPosition().getX() - x
            + fm.getStringBounds(legendItem.getLabel(), graphics).getWidth()
            + 0.5 * textAscent);

        float height = (2 * innerGap + lineHeight);
        item.setBounds(x, y, width, height);
        float boxDim = lineHeight * 0.70f;
        float xloc = (float) (x + innerGap + 0.15f * lineHeight);
        float yloc = (float) (y + innerGap + 0.15f * lineHeight);
        if (this.displaySeriesLines) {
            Line2D line = new Line2D.Float(xloc, yloc + boxDim / 2, 
                                           xloc + boxDim * 3, yloc + boxDim / 2);
            item.setLineStroke(legendItem.getStroke());
            item.setLine(line);
            // lengthen the bounds to accomodate the longer item
            item.setBounds(item.getX(), item.getY(), 
                           item.getWidth() + boxDim * 2, 
                           item.getHeight());
            item.setLabelPosition(new Point2D.Float(xLabelLoc + boxDim * 2, yLabelLoc));
            if (this.displaySeriesShapes) {
                Shape marker = legendItem.getShape();
                AffineTransform t1 = AffineTransform.getScaleInstance(
                    this.shapeScaleX, this.shapeScaleY
                );
                Shape s1 = t1.createTransformedShape(marker);
                AffineTransform transformer = AffineTransform.getTranslateInstance(
                    xloc + (boxDim * 1.5), yloc + boxDim / 2);
                Shape s2 = transformer.createTransformedShape(s1);
                item.setMarker(s2);
           }

        } 
        else {
            if (this.displaySeriesShapes) {
                Shape marker = legendItem.getShape();
                AffineTransform t1 = AffineTransform.getScaleInstance(
                        this.shapeScaleX, this.shapeScaleY
                );
                Shape s1 = t1.createTransformedShape(marker);
                AffineTransform transformer = AffineTransform.getTranslateInstance(
                    xloc + boxDim / 2, yloc + boxDim / 2);
                Shape s2 = transformer.createTransformedShape(s1);
                item.setMarker(s2);
            }
            else {
                item.setMarker(new Rectangle2D.Float(xloc, yloc, boxDim, boxDim));
            }
        }
        return item;

    }

    /**
     * Tests an object for equality with this legend.
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

        if (obj instanceof StandardLegend) {
            StandardLegend l = (StandardLegend) obj;
            if (super.equals(obj)) {

                if (ObjectUtils.equal(this.outerGap, l.outerGap) == false) {
                    return false;
                }
                if (ObjectUtils.equal(this.outlineStroke, l.outlineStroke) == false) {
                    return false;
                }
                if (ObjectUtils.equal(this.outlinePaint, l.outlinePaint) == false) {
                    return false;
                }
                if (ObjectUtils.equal(this.backgroundPaint, l.backgroundPaint) == false) {
                    return false;
                }
                if (ObjectUtils.equal(this.innerGap, l.innerGap) == false) {
                    return false;
                }
                if (ObjectUtils.equal(this.title, l.title) == false) {
                    return false;
                }
                if (ObjectUtils.equal(this.titleFont, l.titleFont) == false) {
                    return false;
                }
                if (ObjectUtils.equal(this.itemFont, l.itemFont) == false) {
                    return false;
                }
                if (ObjectUtils.equal(this.itemPaint, l.itemPaint) == false) {
                    return false;
                }
                if (this.outlineShapes != l.outlineShapes) {
                    return false;
                }
                if (ObjectUtils.equal(this.shapeOutlineStroke, l.shapeOutlineStroke) == false) {
                    return false;
                }
                if (ObjectUtils.equal(this.shapeOutlinePaint, l.shapeOutlinePaint) == false) {
                    return false;
                }
                if (this.displaySeriesShapes == l.displaySeriesShapes) {
                    return true;
                }
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
        SerialUtilities.writeStroke(this.outlineStroke, stream);
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writePaint(this.backgroundPaint, stream);
        SerialUtilities.writePaint(this.itemPaint, stream);
        SerialUtilities.writeStroke(this.shapeOutlineStroke, stream);
        SerialUtilities.writePaint(this.shapeOutlinePaint, stream);
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the output stream.
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.outlineStroke = SerialUtilities.readStroke(stream);
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.backgroundPaint = SerialUtilities.readPaint(stream);
        this.itemPaint = SerialUtilities.readPaint(stream);
        this.shapeOutlineStroke = SerialUtilities.readStroke(stream);
        this.shapeOutlinePaint = SerialUtilities.readPaint(stream);
    }

}
