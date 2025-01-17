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
 * ------------------
 * LevelRenderer.java
 * ------------------
 * (C) Copyright 2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: LevelRenderer.java,v 1.1 2007/10/10 19:34:42 vauchers Exp $
 *
 * Changes
 * -------
 * 09-Jan-2004 : Version 1 (DG);
 * 
 */

package org.jfree.chart.renderer;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.CategoryLabelGenerator;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.data.CategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

/**
 * A {@link CategoryItemRenderer} that draws individual data items as horizontal lines, 
 * spaced in the same way as bars in a bar chart.
 */
public class LevelRenderer extends AbstractCategoryItemRenderer 
                           implements Cloneable, PublicCloneable, Serializable {

    /** The default item margin percentage. */
    public static final double DEFAULT_ITEM_MARGIN = 0.20;

    /** The margin between items within a category. */
    private double itemMargin;

    /** The maximum item width as a percentage of the available space. */
    private double maxItemWidth;
    
    /**
     * Creates a new renderer with default settings.
     */
    public LevelRenderer() {
        super();
        this.itemMargin = DEFAULT_ITEM_MARGIN;
        this.maxItemWidth = 1.0;  // 100 percent, so it will not apply unless changed
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
        notifyListeners(new RendererChangeEvent(this));
    }
    
    /**
     * Returns the maximum width, as a percentage of the available drawing space.
     * 
     * @return the maximum width.
     */
    public double getMaxItemWidth() {
        return this.maxItemWidth;
    }
    
    /**
     * Sets the maximum item width, which is specified as a percentage of the available space
     * for all items, and sends a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param percent  the percent.
     */
    public void setMaxItemWidth(double percent) {
        this.maxItemWidth = percent;
        notifyListeners(new RendererChangeEvent(this));
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
     * @param rendererIndex  the renderer index.
     * @param info  collects chart rendering information for return to caller.
     * 
     * @return The renderer state.
     *
     */
    public CategoryItemRendererState initialise(Graphics2D g2,
                                                Rectangle2D dataArea,
                                                CategoryPlot plot,
                                                int rendererIndex,
                                                PlotRenderingInfo info) {

        CategoryItemRendererState state = super.initialise(g2, dataArea, plot, rendererIndex, info);
        calculateItemWidth(plot, dataArea, rendererIndex, state);
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
    protected void calculateItemWidth(CategoryPlot plot, 
                                      Rectangle2D dataArea, 
                                      int rendererIndex,
                                      CategoryItemRendererState state) {
                                         
        CategoryAxis domainAxis = getDomainAxis(plot, rendererIndex);
        CategoryDataset dataset = plot.getDataset(rendererIndex);
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
            double maxWidth = space * getMaxItemWidth();
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
        RectangleEdge edge = plot.getRangeAxisEdge();
        double barL = rangeAxis.valueToJava2D(value, dataArea, edge);

        // draw the bar...
        Line2D line = null;
        double x = 0.0;
        double y = 0.0;
        if (orientation == PlotOrientation.HORIZONTAL) {
            x = barL;
            y = barW0 + state.getBarWidth() / 2.0;
            line = new Line2D.Double(barL, barW0, barL, barW0 + state.getBarWidth());
        }
        else {
            x = barW0 + state.getBarWidth() / 2.0;
            y = barL;
            line = new Line2D.Double(barW0, barL, barW0 + state.getBarWidth(), barL);
        }
        Stroke itemStroke = getItemStroke(row, column);
        Paint itemPaint = getItemPaint(row, column);
        g2.setStroke(itemStroke);
        g2.setPaint(itemPaint);
        g2.draw(line);

        CategoryLabelGenerator generator = getLabelGenerator(row, column);
        if (generator != null && isItemLabelVisible(row, column)) {
            drawItemLabel(g2, orientation, dataset, row, column, x, y, (value < 0.0));
        }        
                
        // collect entity and tool tip information...
        if (state.getInfo() != null) {
            EntityCollection entities = state.getInfo().getOwner().getEntityCollection();
            if (entities != null) {
                String tip = null;
                CategoryToolTipGenerator tipster = getToolTipGenerator(row, column);
                if (tipster != null) {
                    tip = tipster.generateToolTip(dataset, row, column);
                }
                String url = null;
                if (getItemURLGenerator(row, column) != null) {
                    url = getItemURLGenerator(row, column).generateURL(dataset, row, column);
                }
                CategoryItemEntity entity = new CategoryItemEntity(
                    line.getBounds(), tip, url, dataset, row, dataset.getColumnKey(column), column
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
        
        if (super.equals(object) && (object instanceof LevelRenderer)) {

            LevelRenderer r = (LevelRenderer) object;
            boolean b0 = (this.itemMargin == r.itemMargin);              
            return b0;
        }
        
        return false;
        
    }

}
