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
 * ----------------
 * BarRenderer.java
 * ----------------
 * (C) Copyright 2002-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Christian W. Zuckschwerdt;
 *
 * $Id: BarRenderer.java,v 1.1 2007/10/10 19:25:28 vauchers Exp $
 *
 * Changes
 * -------
 * 14-Mar-2002 : Version 1 (DG);
 * 23-May-2002 : Added tooltip generator to renderer (DG);
 * 29-May-2002 : Moved tooltip generator to abstract super-class (DG);
 * 25-Jun-2002 : Changed constructor to protected and removed redundant code (DG);
 * 26-Jun-2002 : Added axis to initialise method, and record upper and lower clip values (DG);
 * 24-Sep-2002 : Added getLegendItem(...) method (DG);
 * 09-Oct-2002 : Modified constructor to include URL generator (DG);
 * 05-Nov-2002 : Base dataset is now TableDataset not CategoryDataset (DG);
 * 10-Jan-2003 : Moved get/setItemMargin() method up from subclasses (DG);
 * 17-Jan-2003 : Moved plot classes into a separate package (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 01-May-2003 : Modified clipping to allow for dual axes and datasets (DG);
 * 12-May-2003 : Merged horizontal and vertical bar renderers (DG);
 * 12-Jun-2003 : Updates for item labels (DG);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 02-Sep-2003 : Changed initialise method to fix bug 790407 (DG);
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 07-Oct-2003 : Added renderer state (DG);
 * 27-Oct-2003 : Merged drawHorizontalItem(...) and drawVerticalItem(...) methods (DG);
 * 28-Oct-2003 : Added support for gradient paint on bars (DG);
 * 14-Nov-2003 : Added 'maxBarWidth' attribute (DG);
 * 
 */

package org.jfree.chart.renderer;

import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.data.CategoryDataset;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.jfree.util.PublicCloneable;

/**
 * A {@link CategoryItemRenderer} that draws individual data items as bars.
 *
 * @author David Gilbert
 */
public class BarRenderer extends AbstractCategoryItemRenderer 
                         implements Cloneable, PublicCloneable, Serializable {

    /** The default item margin percentage. */
    public static final double DEFAULT_ITEM_MARGIN = 0.20;

    /** Constant that controls the minimum width before a bar has an outline drawn. */
    public static final double BAR_OUTLINE_WIDTH_THRESHOLD = 3.0;

    /** The margin between items (bars) within a category. */
    private double itemMargin;

    /** A flag that controls whether or not bar outlines are drawn. */
    private boolean drawBarOutline;
    
    /** The maximum bar width as a percentage of the available space. */
    private double maxBarWidth;
    
    /** The minimum bar length (in Java2D units). */
    private double minimumBarLength;
    
    /** An optional class used to transform gradient paint objects to fit each bar. */
    private GradientPaintTransformer gradientPaintTransformer;
    
    /** The fallback position if a positive item label doesn't fit inside the bar. */
    private ItemLabelPosition positiveItemLabelPositionFallback;
    
    /** The fallback position if a negative item label doesn't fit inside the bar. */
    private ItemLabelPosition negativeItemLabelPositionFallback;
    
    /** The upper clip (axis) value for the axis. */
    private double upperClip;

    /** The lower clip (axis) value for the axis. */
    private double lowerClip;

    /**
     * Creates a new bar renderer with default settings.
     */
    public BarRenderer() {
        super();
        this.itemMargin = DEFAULT_ITEM_MARGIN;
        this.drawBarOutline = true;
        this.maxBarWidth = 1.0;  // 100 percent, so it will not apply unless changed
        this.positiveItemLabelPositionFallback = null;
        this.negativeItemLabelPositionFallback = null;
        this.gradientPaintTransformer = new StandardGradientPaintTransformer();
        this.minimumBarLength = 0.0;
    }

    /**
     * Returns the item margin.
     *
     * @return The margin.
     */
    public double getItemMargin() {
        return this.itemMargin;
    }

    /**
     * Sets the item margin.  The value is expressed as a percentage of the available width for
     * plotting all the bars, with the resulting amount to be distributed between all the bars
     * evenly.
     *
     * @param percent  the new margin.
     */
    public void setItemMargin(double percent) {
        this.itemMargin = percent;
        firePropertyChanged("ItemMargin", null, null);
    }

    /**
     * Returns a flag that controls whether or not bar outlines are drawn.
     * 
     * @return A boolean.
     */
    public boolean isDrawBarOutline() {
        return this.drawBarOutline;    
    }
    
    /**
     * Sets the flag that controls whether or not bar outlines are drawn.
     * 
     * @param draw  the new flag value.
     */
    public void setDrawBarOutline(boolean draw) {
        this.drawBarOutline = draw;
        firePropertyChanged("DrawBarOutline", null, null);
    }
    
    /**
     * Returns the maximum bar width, as a percentage of the available drawing space.
     * 
     * @return the maximum bar width.
     */
    public double getMaxBarWidth() {
        return this.maxBarWidth;
    }
    
    /**
     * Sets the maximum bar width, which is specified as a percentage of the available space
     * for all bars, and sends a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param percent  the percent.
     */
    public void setMaxBarWidth(double percent) {
        this.maxBarWidth = percent;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns the minimum bar length.
     * 
     * @return the minimum bar length.
     */
    public double getMinimumBarLength() {
        return this.minimumBarLength;
    }
    
    /**
     * Sets the minimum bar length and sends a {@link RendererChangeEvent} to all registered
     * listeners.
     * 
     * @param min  the minimum bar length.
     */
    public void setMinimumBarLength(double min) {
        this.minimumBarLength = min;
        notifyListeners(new RendererChangeEvent(this));
    }
    
    /**
     * Returns the gradient paint transformer (an object used to transform gradient paint objects
     * to fit each bar.
     * 
     * @return A transformer (<code>null</code> possible).
     */    
    public GradientPaintTransformer getGradientPaintTransformer() {
        return this.gradientPaintTransformer;    
    }
    
    /**
     * Sets the gradient paint transformer and sends a {@link RendererChangeEvent} to all registered
     * listeners.
     * 
     * @param transformer  the transformer (<code>null</code> permitted).
     */
    public void setGradientPaintTransformer(GradientPaintTransformer transformer) {
        this.gradientPaintTransformer = transformer;
        notifyListeners(new RendererChangeEvent(this));
    }
    
    /**
     * Returns the fallback position for positive item labels that don't fit within a bar.
     * 
     * @return The fallback position (<code>null</code> possible).
     */
    public ItemLabelPosition getPositiveItemLabelPositionFallback() {
        return this.positiveItemLabelPositionFallback;
    }
    
    /**
     * Sets the fallback position for positive item labels that don't fit within a bar, and sends
     * a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param position  the position.
     */
    public void setPositiveItemLabelPositionFallback(ItemLabelPosition position) {
        this.positiveItemLabelPositionFallback = position;
        notifyListeners(new RendererChangeEvent(this));
    }
    
    /**
     * Returns the fallback position for negative item labels that don't fit within a bar.
     * 
     * @return The fallback position (<code>null</code> possible).
     */
    public ItemLabelPosition getNegativeItemLabelPositionFallback() {
        return this.negativeItemLabelPositionFallback;
    }
    
    /**
     * Sets the fallback position for negative item labels that don't fit within a bar, and sends
     * a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param position  the position.
     */
    public void setNegativeItemLabelPositionFallback(ItemLabelPosition position) {
        this.negativeItemLabelPositionFallback = position;
        notifyListeners(new RendererChangeEvent(this));
    }
    
    /**
     * Returns the lower clip value.
     * <P>
     * This value is recalculated in the initialise() method.
     *
     * @return The value.
     */
    public double getLowerClip() {
        return this.lowerClip;
    }

    /**
     * Returns the upper clip value.
     * <P>
     * This value is recalculated in the initialise() method.
     *
     * @return The value.
     */
    public double getUpperClip() {
        return this.upperClip;
    }

    /**
     * Initialises the renderer and returns a state object that will be passed to subsequent calls 
     * to the drawItem method.
     * <p>
     * This method gets called once at the start of the process of drawing a chart.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area in which the data is to be plotted.
     * @param plot  the plot.
     * @param rendererIndex  the renderer index (<code>null</code> for the primary renderer).
     * @param info  collects chart rendering information for return to caller.
     * 
     * @return The renderer state.
     *
     */
    public CategoryItemRendererState initialise(Graphics2D g2,
                                                Rectangle2D dataArea,
                                                CategoryPlot plot,
                                                Integer rendererIndex,
                                                PlotRenderingInfo info) {

        CategoryItemRendererState state = super.initialise(g2, dataArea, plot, rendererIndex, info);

        // get the clipping values...
        ValueAxis rangeAxis = getRangeAxis(plot, rendererIndex);
        this.lowerClip = rangeAxis.getRange().getLowerBound();
        this.upperClip = rangeAxis.getRange().getUpperBound();

        // calculate the bar width
        calculateBarWidth(plot, dataArea, rendererIndex, state);

        return state;
        
    }
    
    /**
     * Calculates the bar width and stores it in the renderer state.
     * 
     * @param plot  the plot.
     * @param dataArea  the data area.
     * @param rendererIndex  the renderer index.
     * @param state  the renderer state.
     */
    protected void calculateBarWidth(CategoryPlot plot, 
                                     Rectangle2D dataArea, 
                                     Integer rendererIndex,
                                     CategoryItemRendererState state) {
                                         
        CategoryAxis domainAxis = getDomainAxis(plot, rendererIndex);
        CategoryDataset dataset = getDataset(plot, rendererIndex);
        if (dataset != null) {
            int columns = dataset.getColumnCount();
            int rows = dataset.getRowCount();
            double space = 0.0;
            PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                space = dataArea.getHeight();
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                space = dataArea.getWidth();
            }
            double maxWidth = space * getMaxBarWidth();
            double categoryMargin = 0.0;
            double currentItemMargin = 0.0;
            if (columns > 1) {
                categoryMargin = domainAxis.getCategoryMargin();
            }
            if (rows > 1) {
                currentItemMargin = getItemMargin();
            }
            double used = space * (1 - domainAxis.getLowerMargin() - domainAxis.getUpperMargin()
                                     - categoryMargin - currentItemMargin);
            if ((rows * columns) > 0) {
                state.setBarWidth(Math.min(used / (rows * columns), maxWidth));
            }
            else {
                state.setBarWidth(Math.min(used, maxWidth));
            }
        }
    }

    /**
     * Calculates the coordinate of the first "side" of a bar.  This will be the minimum 
     * x-coordinate for a vertical bar, and the minimum y-coordinate for a horizontal bar.
     * 
     * @param plot  the plot.
     * @param orientation  the plot orientation.
     * @param dataArea  the data area.
     * @param domainAxis  the domain axis.
     * @param state  the renderer state (has the bar width precalculated).
     * @param row  the row index.
     * @param column  the column index.
     * 
     * @return the coordinate.
     */
    protected double calculateBarW0(CategoryPlot plot, 
                                    PlotOrientation orientation, 
                                    Rectangle2D dataArea,
                                    CategoryAxis domainAxis,
                                    CategoryItemRendererState state,
                                    int row,
                                    int column) {
        // calculate bar width...
        double space = 0.0;
        if (orientation == PlotOrientation.HORIZONTAL) {
            space = dataArea.getHeight();
        }
        else {
            space = dataArea.getWidth();
        }
        double barW0 = domainAxis.getCategoryStart(column, getColumnCount(), dataArea,
                                                   plot.getDomainAxisEdge());
        int seriesCount = getRowCount();
        int categoryCount = getColumnCount();
        if (seriesCount > 1) {
            double seriesGap = space * getItemMargin() / (categoryCount * (seriesCount - 1));
            double seriesW = calculateSeriesWidth(space, domainAxis, categoryCount, seriesCount);
            barW0 = barW0 + row * (seriesW + seriesGap) 
                          + (seriesW / 2.0) - (state.getBarWidth() / 2.0);
        }
        else {
            barW0 = domainAxis.getCategoryMiddle(column, getColumnCount(), dataArea,
                                                 plot.getDomainAxisEdge())
                    - state.getBarWidth() / 2.0;
        }
        return barW0;
    }
    
    /**
     * Calculates the coordinates for the length of a single bar.
     * 
     * @param value  the value represented by the bar.
     * 
     * @return the coordinates for each end of the bar (or <code>null</code> if the bar is not
     *         visible for the current axis range).
     */
    protected double[] calculateBarL0L1(double value) {

        double base = 0.0;
        double lclip = getLowerClip();
        double uclip = getUpperClip();
        if (uclip <= 0.0) {  // cases 1, 2, 3 and 4
            if (value >= uclip) {
                return null; // bar is not visible
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
                return null; // bar is not visible
            }
            base = lclip;
            if (value >= uclip) {
                value = uclip;
            }
        }
        return new double[] {base, value};
    }

    /**
     * Draws the bar for a single (series, category) data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the data area.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    public void drawItem(Graphics2D g2,
                         CategoryItemRendererState state,
                         Rectangle2D dataArea,
                         CategoryPlot plot,
                         CategoryAxis domainAxis,
                         ValueAxis rangeAxis,
                         CategoryDataset dataset,
                         int row,
                         int column) {

        // nothing is drawn for null values...
        Number dataValue = dataset.getValue(row, column);
        if (dataValue == null) {
            return;
        }
        
        double value = dataValue.doubleValue();
        
        PlotOrientation orientation = plot.getOrientation();
        double barW0 = calculateBarW0(plot, orientation, dataArea, domainAxis, state, row, column);
        double[] barL0L1 = calculateBarL0L1(value);
        if (barL0L1 == null) {
            return;  // the bar is not visible
        }
        
        RectangleEdge edge = plot.getRangeAxisEdge();
        double transL0 = rangeAxis.translateValueToJava2D(barL0L1[0], dataArea, edge);
        double transL1 = rangeAxis.translateValueToJava2D(barL0L1[1], dataArea, edge);
        double barL0 = Math.min(transL0, transL1);
        double barLength = Math.max(Math.abs(transL1 - transL0), this.minimumBarLength);

        // draw the bar...
        Rectangle2D bar = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            bar = new Rectangle2D.Double(barL0, barW0, barLength, state.getBarWidth());
        }
        else {
            bar = new Rectangle2D.Double(barW0, barL0, state.getBarWidth(), barLength);
        }
        Paint itemPaint = getItemPaint(row, column);
        if (this.gradientPaintTransformer != null && itemPaint instanceof GradientPaint) {
            GradientPaint gp = (GradientPaint) itemPaint;
            itemPaint = this.gradientPaintTransformer.transform(gp, bar);
        }
        g2.setPaint(itemPaint);
        g2.fill(bar);

        // draw the outline...
        if (isDrawBarOutline() && state.getBarWidth() > BAR_OUTLINE_WIDTH_THRESHOLD) {
            Stroke stroke = getItemOutlineStroke(row, column);
            Paint paint = getItemOutlinePaint(row, column);
            if (stroke != null && paint != null) {
                g2.setStroke(stroke);
                g2.setPaint(paint);
                g2.draw(bar);
            }
        }

        CategoryItemLabelGenerator generator = getItemLabelGenerator(row, column);
        if (generator != null && isItemLabelVisible(row, column)) {
            drawItemLabel(g2, dataset, row, column, plot, generator, bar, (value < 0.0));
        }        
                
        // collect entity and tool tip information...
        if (state.getInfo() != null) {
            EntityCollection entities = state.getInfo().getOwner().getEntityCollection();
            if (entities != null) {
                String tip = null;
                if (generator != null) {
                    tip = generator.generateToolTip(dataset, row, column);
                }
                String url = null;
                if (getItemURLGenerator(row, column) != null) {
                    url = getItemURLGenerator(row, column).generateURL(dataset, row, column);
                }
                CategoryItemEntity entity = new CategoryItemEntity(
                    bar, tip, url, dataset, row, dataset.getColumnKey(column), column
                );
                entities.addEntity(entity);
            }

        }

    }

    /**
     * Calculates the available space for each series.
     * 
     * @param space  the space along the entire axis (in Java2D units).
     * @param axis  the category axis.
     * @param categories  the number of categories.
     * @param series  the number of series.
     * 
     * @return  the width of one series.
     */
    protected double calculateSeriesWidth(double space, CategoryAxis axis, 
                                          int categories, int series) {
        double factor = 1.0 - getItemMargin() - axis.getLowerMargin() - axis.getUpperMargin();
        if (categories > 1) {
            factor = factor - axis.getCategoryMargin();
        }
        return (space * factor) / (categories * series);
    }
    
    /**
     * Draws an item label.  This method is overridden so that the bar can be used
     * to calculate the label anchor point.
     * 
     * @param g2  the graphics device.
     * @param data  the dataset.
     * @param row  the row.
     * @param column  the column.
     * @param plot  the plot.
     * @param generator  the label generator.
     * @param bar  the bar.
     * @param negative  a flag indicating a negative value.
     */
    protected void drawItemLabel(Graphics2D g2,
                                 CategoryDataset data,
                                 int row,
                                 int column,
                                 CategoryPlot plot,
                                 CategoryItemLabelGenerator generator,
                                 Rectangle2D bar,
                                 boolean negative) {
                                     
        // draw the item labels if there are any...
        Font labelFont = getItemLabelFont(row, column);
        g2.setFont(labelFont);
        Paint paint = getItemLabelPaint(row, column);
        g2.setPaint(paint);
        String label = generator.generateItemLabel(data, row, column);

        // find out where to place the label...
        ItemLabelPosition position = null;
        if (!negative) {
            position = getPositiveItemLabelPosition(row, column);
        }
        else {
            position = getNegativeItemLabelPosition(row, column);
        }

        // work out the label anchor point...
        Point2D anchorPoint = calculateLabelAnchorPoint(position.getItemLabelAnchor(), 
                                                        bar, 
                                                        plot.getOrientation());
        
        if (isInternalAnchor(position.getItemLabelAnchor())) {
            Shape bounds = RefineryUtilities.calculateRotatedStringBounds(
                label, g2,
                (float) anchorPoint.getX(),
                (float) anchorPoint.getY(),
                position.getTextAnchor(), 
                position.getRotationAnchor(), 
                position.getAngle());
        
            if (!bar.contains(bounds.getBounds2D())) {
                if (!negative) {
                    position = getPositiveItemLabelPositionFallback();
                }
                else {
                    position = getNegativeItemLabelPositionFallback();
                }
                if (position != null) {
                    anchorPoint = calculateLabelAnchorPoint(position.getItemLabelAnchor(), 
                                                            bar, 
                                                            plot.getOrientation());
                }
            }
        
        }
        
        if (position != null) {
            RefineryUtilities.drawRotatedString(label, g2,
                                                (float) anchorPoint.getX(),
                                                (float) anchorPoint.getY(),
                                                position.getTextAnchor(), 
                                                position.getRotationAnchor(), 
                                                position.getAngle());
        }        
    }
    
    /**
     * Calculates the item label anchor point.
     *
     * @param anchor  the anchor.
     * @param bar  the bar.
     * @param orientation  the plot orientation.
     *
     * @return The anchor point.
     */
    private Point2D calculateLabelAnchorPoint(ItemLabelAnchor anchor,
                                              Rectangle2D bar, PlotOrientation orientation) {

        Point2D result = null;
        double offset = getItemLabelAnchorOffset();
        double x0 = bar.getX() - offset;
        double x1 = bar.getX();
        double x2 = bar.getX() + offset;
        double x3 = bar.getCenterX();
        double x4 = bar.getMaxX() - offset;
        double x5 = bar.getMaxX();
        double x6 = bar.getMaxX() + offset;

        double y0 = bar.getMaxY() + offset;
        double y1 = bar.getMaxY();
        double y2 = bar.getMaxY() - offset;
        double y3 = bar.getCenterY();
        double y4 = bar.getMinY() + offset;
        double y5 = bar.getMinY();
        double y6 = bar.getMinY() - offset;

        if (anchor == ItemLabelAnchor.CENTER) {
            result = new Point2D.Double(x3, y3);
        }
        else if (anchor == ItemLabelAnchor.INSIDE1) {
            result = new Point2D.Double(x4, y4);
        }
        else if (anchor == ItemLabelAnchor.INSIDE2) {
            result = new Point2D.Double(x4, y4);
        }
        else if (anchor == ItemLabelAnchor.INSIDE3) {
            result = new Point2D.Double(x4, y3);
        }
        else if (anchor == ItemLabelAnchor.INSIDE4) {
            result = new Point2D.Double(x4, y2);
        }
        else if (anchor == ItemLabelAnchor.INSIDE5) {
            result = new Point2D.Double(x4, y2);
        }
        else if (anchor == ItemLabelAnchor.INSIDE6) {
            result = new Point2D.Double(x3, y2);
        }
        else if (anchor == ItemLabelAnchor.INSIDE7) {
            result = new Point2D.Double(x2, y2);
        }
        else if (anchor == ItemLabelAnchor.INSIDE8) {
            result = new Point2D.Double(x2, y2);
        }
        else if (anchor == ItemLabelAnchor.INSIDE9) {
            result = new Point2D.Double(x2, y3);
        }
        else if (anchor == ItemLabelAnchor.INSIDE10) {
            result = new Point2D.Double(x2, y4);
        }
        else if (anchor == ItemLabelAnchor.INSIDE11) {
            result = new Point2D.Double(x2, y4);
        }
        else if (anchor == ItemLabelAnchor.INSIDE12) {
            result = new Point2D.Double(x3, y4);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE1) {
            result = new Point2D.Double(x5, y6);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE2) {
            result = new Point2D.Double(x6, y5);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE3) {
            result = new Point2D.Double(x6, y3);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE4) {
            result = new Point2D.Double(x6, y1);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE5) {
            result = new Point2D.Double(x5, y0);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE6) {
            result = new Point2D.Double(x3, y0);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE7) {
            result = new Point2D.Double(x1, y0);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE8) {
            result = new Point2D.Double(x0, y1);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE9) {
            result = new Point2D.Double(x0, y3);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE10) {
            result = new Point2D.Double(x0, y5);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE11) {
            result = new Point2D.Double(x1, y6);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE12) {
            result = new Point2D.Double(x3, y6);
        }

        return result;

    }
    
    /**
     * Returns <code>true</code> if the specified anchor point is inside a bar.
     * 
     * @param anchor  the anchor point.
     * 
     * @return A boolean.
     */
    private boolean isInternalAnchor(ItemLabelAnchor anchor) {
        return anchor == ItemLabelAnchor.CENTER 
               || anchor == ItemLabelAnchor.INSIDE1
               || anchor == ItemLabelAnchor.INSIDE2
               || anchor == ItemLabelAnchor.INSIDE3
               || anchor == ItemLabelAnchor.INSIDE4
               || anchor == ItemLabelAnchor.INSIDE5
               || anchor == ItemLabelAnchor.INSIDE6
               || anchor == ItemLabelAnchor.INSIDE7
               || anchor == ItemLabelAnchor.INSIDE8
               || anchor == ItemLabelAnchor.INSIDE9
               || anchor == ItemLabelAnchor.INSIDE10
               || anchor == ItemLabelAnchor.INSIDE11
               || anchor == ItemLabelAnchor.INSIDE12;
               
    }
    
    /**
     * Tests an object for equality with this instance.
     * 
     * @param object  the object.
     * 
     * @return A boolean.
     */
    public boolean equals(Object object) {
        
        if (object == null) {
            return false;
        }
        
        if (object == this) {
            return true;
        }
        
        if (super.equals(object) && (object instanceof BarRenderer)) {

            BarRenderer r = (BarRenderer) object;
            boolean b0 = (this.itemMargin == r.itemMargin);              
            boolean b1 = (this.drawBarOutline == r.drawBarOutline);
            return b0 && b1;
        }
        
        return false;
        
    }

}
