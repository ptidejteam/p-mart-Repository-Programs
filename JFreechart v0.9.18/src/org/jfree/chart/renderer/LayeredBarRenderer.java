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
 * -----------------------
 * LayeredBarRenderer.java
 * -----------------------
 * (C) Copyright 2003, 2004, by Arnaud Lelievre and Contributors.
 *
 * Original Author:  Arnaud Lelievre (for Garden);
 * Contributor(s):   -;
 *
 *
 * Changes
 * -------
 * 28-Aug-2003 : Version 1 (AL);
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 07-Oct-2003 : Added renderer state (DG);
 * 21-Oct-2003 : Bar width moved to renderer state (DG);
 *
 */

package org.jfree.chart.renderer;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.CategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ObjectList;

/**
 * A {@link CategoryItemRenderer} that represents data using bars which are superimposed.
 *
 * @author Arnaud Lelievre
 */
public class LayeredBarRenderer extends BarRenderer {

    /** A list of the width of each series bar. */
    protected ObjectList seriesBarWidthList;

    /**
     * Default constructor.
     */
    public LayeredBarRenderer() {
        super();
        this.seriesBarWidthList = new ObjectList();

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

        // calculate the bar width
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
                state.setBarWidth(used / (dataset.getColumnCount()));
            } 
            else {
                state.setBarWidth(used);
            }
        }
    }

    /**
     * Draws the bar for one item in the dataset.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the plot area.
     * @param plot  the plot.
     * @param domainAxis  the domain (category) axis.
     * @param rangeAxis  the range (value) axis.
     * @param data  the data.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    public void drawItem(Graphics2D g2,
                         CategoryItemRendererState state,
                         Rectangle2D dataArea,
                         CategoryPlot plot,
                         CategoryAxis domainAxis,
                         ValueAxis rangeAxis,
                         CategoryDataset data,
                         int row,
                         int column) {

        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            drawHorizontalItem(g2, state, dataArea,
                               plot, domainAxis, rangeAxis, data, row, column);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            drawVerticalItem(g2, state, dataArea,
                             plot, domainAxis, rangeAxis, data, row, column);
        }

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
     * @param data  the data.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    protected void drawHorizontalItem(Graphics2D g2,
                                      CategoryItemRendererState state,
                                      Rectangle2D dataArea,
                                      CategoryPlot plot,
                                      CategoryAxis domainAxis,
                                      ValueAxis rangeAxis,
                                      CategoryDataset data,
                                      int row,
                                      int column) {

        // nothing is drawn for null values...
        Number dataValue = data.getValue(row, column);
        if (dataValue == null) {
            return;
        }

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

        RectangleEdge edge = plot.getRangeAxisEdge();
        double transX1 = rangeAxis.valueToJava2D(base, dataArea, edge);
        double transX2 = rangeAxis.valueToJava2D(value, dataArea, edge);
        double rectX = Math.min(transX1, transX2);
        double rectWidth = Math.abs(transX2 - transX1);

        // Y
        double rectY = domainAxis.getCategoryStart(column, getColumnCount(), dataArea,
                                                   plot.getDomainAxisEdge());

        int seriesCount = getRowCount();

        // draw the bar...
        double shift = 0.0;
        double rectHeight = 0.0;
        if (getSeriesBarWidth(row, state) != state.getBarWidth()) {
            rectHeight = getSeriesBarWidth(row, state) * state.getBarWidth();
            rectY = rectY + (1 - getSeriesBarWidth(row, state)) * state.getBarWidth() / 2;
        } 
        else {
            rectHeight = state.getBarWidth();
            if (seriesCount > 1) {
                shift = rectHeight * 0.20 / (seriesCount - 1);
            }
        }

        Rectangle2D bar = new Rectangle2D.Double(rectX, 
                                                (rectY + ((seriesCount - 1 - row) * shift)),
                                                rectWidth,
                                                (rectHeight - (seriesCount - 1 - row) * shift * 2));

        g2.setPaint(getItemPaint(row, column));
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

        CategoryItemLabelGenerator generator = getLabelGenerator(row, column);
        if (generator != null && isItemLabelVisible(row, column)) {
            drawItemLabel(g2, data, row, column, plot, generator, bar, (transX1 > transX2));
        }        

        // collect entity and tool tip information...
        if (state.getInfo() != null) {
            EntityCollection entities = state.getInfo().getOwner().getEntityCollection();
            if (entities != null) {
                String tip = null;
                CategoryToolTipGenerator tipster = getToolTipGenerator(row, column);
                if (tipster != null) {
                    tip = tipster.generateToolTip(data, row, column);
                }
                String url = null;
                if (getItemURLGenerator(row, column) != null) {
                    url = getItemURLGenerator(row, column).generateURL(data, row, column);
                }
                CategoryItemEntity entity = new CategoryItemEntity(bar, tip, url, data, row,
                                                               data.getColumnKey(column), column);
                entities.addEntity(entity);
            }
        }
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
     * @param data  the data.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    protected void drawVerticalItem(Graphics2D g2,
                                    CategoryItemRendererState state,
                                    Rectangle2D dataArea,
                                    CategoryPlot plot,
                                    CategoryAxis domainAxis,
                                    ValueAxis rangeAxis,
                                    CategoryDataset data,
                                    int row,
                                    int column) {

        // nothing is drawn for null values...
        Number dataValue = data.getValue(row, column);
        if (dataValue == null) {
            return;
        }

        // BAR X
        double rectX = domainAxis.getCategoryStart(column, getColumnCount(), dataArea,
                                                   plot.getDomainAxisEdge());

        int seriesCount = getRowCount();

        // BAR Y
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
            base = getLowerClip();
            if (value >= uclip) {
               value = uclip;
            }
        }

        RectangleEdge edge = plot.getRangeAxisEdge();
        double transY1 = rangeAxis.valueToJava2D(base, dataArea, edge);
        double transY2 = rangeAxis.valueToJava2D(value, dataArea, edge);
        double rectY = Math.min(transY2, transY1);

        double rectWidth = state.getBarWidth();
        double rectHeight = Math.abs(transY2 - transY1);

        // draw the bar...
        double shift = 0.0;
        rectWidth = 0.0;
        if (getSeriesBarWidth(row, state) != state.getBarWidth()) {
            rectWidth = getSeriesBarWidth(row, state) * state.getBarWidth();
            rectX = rectX + (1 - getSeriesBarWidth(row, state)) * state.getBarWidth() / 2;
        } 
        else {
            rectWidth = state.getBarWidth();
            if (seriesCount > 1) {
                // needs to be improved !!!
                shift = rectWidth * 0.20 / (seriesCount - 1);
            }
        }

        Rectangle2D bar = new Rectangle2D.Double(
                                                (rectX + ((seriesCount - 1 - row) * shift)),
                                                rectY,
                                                (rectWidth - (seriesCount - 1 - row) * shift * 2),
                                                rectHeight);
        g2.setPaint(getItemPaint(row, column));
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

        // draw the item labels if there are any...
        double transX1 = rangeAxis.valueToJava2D(base, dataArea, edge);
        double transX2 = rangeAxis.valueToJava2D(value, dataArea, edge);

        CategoryItemLabelGenerator generator = getLabelGenerator(row, column);
        if (generator != null && isItemLabelVisible(row, column)) {
            drawItemLabel(g2, data, row, column, plot, generator, bar, (transX1 > transX2));
        }        

        // collect entity and tool tip information...
        if (state.getInfo() != null) {
            EntityCollection entities = state.getInfo().getOwner().getEntityCollection();
            if (entities != null) {
                String tip = null;
                CategoryToolTipGenerator tipster = getToolTipGenerator(row, column);
                if (tipster != null) {
                    tip = tipster.generateToolTip(data, row, column);
                }
                String url = null;
                if (getItemURLGenerator(row, column) != null) {
                    url = getItemURLGenerator(row, column).generateURL(data, row, column);
                }
                CategoryItemEntity entity = new CategoryItemEntity(
                    bar, tip, url, data, row, data.getColumnKey(column), column
                );
                entities.addEntity(entity);
            }
        }
    }

    /**
     * Returns the bar width for a series.
     *
     * @param series  the series index (zero based).
     * @param state  the renderer state.
     *
     * @return The width for the series (1.0=100%, it is the maximum).
     */
    public double getSeriesBarWidth(int series, CategoryItemRendererState state) {

        if (this.seriesBarWidthList.get(series) != null) {
            return  ((Number) this.seriesBarWidthList.get(series)).doubleValue();
        }
        else {
            return state.getBarWidth();
        }
    }

    /**
     * Sets the width of the bars of a series.
     *
     * @param series  the series index (zero based).
     * @param width  the width of the series bar in percentage (1.0=100%, it is the maximum).
     */ 
    public void setSeriesBarWidth(int series, double width) {
        this.seriesBarWidthList.set(series, new Double(width));
    }

}
