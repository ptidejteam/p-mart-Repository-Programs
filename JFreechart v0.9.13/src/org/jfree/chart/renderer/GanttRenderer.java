/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
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
 * ------------------
 * GanttRenderer.java
 * ------------------
 * (C) Copyright 2003 by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: GanttRenderer.java,v 1.1 2007/10/10 19:15:26 vauchers Exp $
 *
 * Changes
 * -------
 * 16-Sep-2003 : Version 1 (DG);
 * 23-Sep-2003 : Fixed Checkstyle issues (DG);
 * 
 */

package org.jfree.chart.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.CategoryDataset;
import org.jfree.data.gantt.GanttCategoryDataset;
import org.jfree.ui.RectangleEdge;

/**
 * A renderer for simple Gantt charts.
 * 
 * @author David Gilbert.
 */
public class GanttRenderer extends IntervalBarRenderer {
    
    /** The paint for displaying the percentage complete. */
    private Paint completePaint;
    
    /** The paint for displaying the incomplete part of a task. */
    private Paint incompletePaint;
    
    /** 
     * Controls the starting edge of the progress indicator (expressed as a percentage of the
     * overall bar width.
     */
    private double startPercent = 0.35;
    
    /**
     * Controls the ending edge of the progress indicator (expressed as a percentage of the
     * overall bar width. 
     */
    private double endPercent = 0.65;
    
    /**
     * Creates a new renderer.
     *
     */
    public GanttRenderer() {
        super();
        this.completePaint = Color.green;
        this.incompletePaint = Color.red;
    }
    
    /**
     * Draws the bar for a single (series, category) data item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    public void drawItem(Graphics2D g2,
                         Rectangle2D dataArea,
                         CategoryPlot plot,
                         CategoryAxis domainAxis,
                         ValueAxis rangeAxis,
                         CategoryDataset dataset,
                         int row,
                         int column) {

         if (dataset instanceof GanttCategoryDataset) {
             GanttCategoryDataset gcd = (GanttCategoryDataset) dataset;
             drawTasks(g2, dataArea, plot, domainAxis, rangeAxis, gcd, row, column);
         }
         else {  // let the superclass handle it...
             super.drawItem(g2, dataArea, plot, domainAxis, rangeAxis, dataset, row, column);
         }
 
     }
                          
    /**
     * Draws the tasks/subtasks for one item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data plot area.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the data.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    protected void drawTasks(Graphics2D g2,
                             Rectangle2D dataArea,
                             CategoryPlot plot,
                             CategoryAxis domainAxis,
                             ValueAxis rangeAxis,
                             GanttCategoryDataset dataset,
                             int row,
                             int column) {

        int count = dataset.getSubIntervalCount(row, column);
        if (count == 0) {
            drawTask(g2, dataArea, plot, domainAxis, rangeAxis, dataset, row, column);
        }

        for (int subinterval = 0; subinterval < count; subinterval++) {
            
            RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
            RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();

            // value 0
            Number value0 = dataset.getStartValue(row, column, subinterval);
            if (value0 == null) {
                return;
            }
            double translatedValue0 = rangeAxis.translateValueToJava2D(value0.doubleValue(), 
                                                                       dataArea,
                                                                       rangeAxisLocation);
    
            // value 1
            Number value1 = dataset.getEndValue(row, column, subinterval);
            if (value1 == null) {
                return;
            }
            double translatedValue1 = rangeAxis.translateValueToJava2D(value1.doubleValue(), 
                                                                       dataArea,
                                                                       rangeAxisLocation);
    
            if (translatedValue1 < translatedValue0) {
                double temp = translatedValue1;
                translatedValue1 = translatedValue0;
                translatedValue0 = temp;
            }
    
            // rectStart 
            double rectStart = domainAxis.getCategoryStart(column, getColumnCount(), dataArea,
                                                           domainAxisLocation);
    
            int rows = getRowCount();
            int columns = getColumnCount();
            if (rows > 1) {
                double seriesGap = dataArea.getHeight() * getItemMargin()
                                   / (columns * (rows - 1));
                rectStart = rectStart + row * (getBarWidth() + seriesGap);
            }
            else {
                rectStart = rectStart + row * getBarWidth();
            }
    
            // LENGTH
            double rectLength = Math.abs(translatedValue1 - translatedValue0);
    
            // BREADTH
            double rectBreadth = getBarWidth();
    
            // DRAW THE BARS...
            Rectangle2D bar = null;
            
            if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                bar = new Rectangle2D.Double(translatedValue0, rectStart, rectLength, rectBreadth);
            }
            else if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                bar = new Rectangle2D.Double(rectStart, translatedValue0, rectBreadth, rectLength);
            }
    
            Rectangle2D completeBar = null;
            Rectangle2D incompleteBar = null;
            Number percent = dataset.getPercentComplete(row, column, subinterval);
            if (percent != null) {
                double p = percent.doubleValue();
                if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                    completeBar = new Rectangle2D.Double(
                        translatedValue0, 
                        rectStart + this.startPercent * rectBreadth, 
                        rectLength * p, 
                        rectBreadth * (this.endPercent - this.startPercent)
                    );
                    incompleteBar = new Rectangle2D.Double(
                        translatedValue0 + rectLength * p, 
                        rectStart + this.startPercent * rectBreadth, 
                        rectLength * (1 - p), 
                        rectBreadth * (this.endPercent - this.startPercent)
                    );
                }
                else if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                    completeBar = new Rectangle2D.Double(
                        rectStart + this.startPercent * rectBreadth, 
                        translatedValue0 + rectLength * (1 - p), 
                        rectBreadth * (this.endPercent - this.startPercent), 
                        rectLength * p
                    );
                    incompleteBar = new Rectangle2D.Double(
                        rectStart + this.startPercent * rectBreadth, 
                        translatedValue0, 
                        rectBreadth * (this.endPercent - this.startPercent), 
                        rectLength * (1 - p)
                    );
                }
                
            }

            Paint seriesPaint = getItemPaint(row, column);
            g2.setPaint(seriesPaint);
            g2.fill(bar);
            if (completeBar != null) {
                g2.setPaint(this.completePaint);
                g2.fill(completeBar);
            }
            if (incompleteBar != null) {
                g2.setPaint(this.incompletePaint);
                g2.fill(incompleteBar);
            }
            if (getBarWidth() > BAR_OUTLINE_WIDTH_THRESHOLD) {
                g2.setStroke(getItemStroke(row, column));
                g2.setPaint(getItemOutlinePaint(row, column));
                g2.draw(bar);
            }
    
            // collect entity and tool tip information...
            if (getInfo() != null) {
                EntityCollection entities = getInfo().getOwner().getEntityCollection();
                if (entities != null) {
                    String tip = null;
                    if (getItemLabelGenerator(row, column) != null) {
                        tip = getItemLabelGenerator(row, column).generateToolTip(dataset, 
                                                                          row, column);
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
    }
    
    /**
     * Draws a single task.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data plot area.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the data.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    protected void drawTask(Graphics2D g2,
                            Rectangle2D dataArea,
                            CategoryPlot plot,
                            CategoryAxis domainAxis,
                            ValueAxis rangeAxis,
                            GanttCategoryDataset dataset,
                            int row,
                            int column) {

        int seriesCount = getRowCount();
        int categoryCount = getColumnCount();

        PlotOrientation orientation = plot.getOrientation();

        RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
        
        // Y0
        Number value0 = dataset.getEndValue(row, column);
        if (value0 == null) {
            return;
        }
        double java2dValue0 = rangeAxis.translateValueToJava2D(value0.doubleValue(), dataArea, 
                                                               rangeAxisLocation);

        // Y1
        Number value1 = dataset.getStartValue(row, column);
        if (value1 == null) {
            return;
        }
        double java2dValue1 = rangeAxis.translateValueToJava2D(value1.doubleValue(), dataArea, 
                                                               rangeAxisLocation);

        if (java2dValue1 < java2dValue0) {
            double temp = java2dValue1;
            java2dValue1 = java2dValue0;
            java2dValue0 = temp;
            Number tempNum = value1;
            value1 = value0;
            value0 = tempNum;
        }

        double rectStart = domainAxis.getCategoryStart(column, getColumnCount(), dataArea,
                                                       domainAxisLocation);

        // BREADTH
        double rectBreadth = getBarWidth();

        // BAR HEIGHT
        double rectLength = Math.abs(java2dValue1 - java2dValue0);
        
        Rectangle2D bar = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            if (seriesCount > 1) {
                double seriesGap = dataArea.getHeight() * getItemMargin()
                                   / (categoryCount * (seriesCount - 1));
                rectStart = rectStart + row * (getBarWidth() + seriesGap);
            }
            else {
                rectStart = rectStart + row * getBarWidth();
            }
            bar = new Rectangle2D.Double(java2dValue0, rectStart, rectLength, rectBreadth);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            if (seriesCount > 1) {
                double seriesGap = dataArea.getWidth() * getItemMargin()
                                   / (categoryCount * (seriesCount - 1));
                rectStart = rectStart + row * (getBarWidth() + seriesGap);
            }
            else {
                rectStart = rectStart + row * getBarWidth();
            }
            bar = new Rectangle2D.Double(rectStart, java2dValue1, rectBreadth, rectLength);
        }

        Rectangle2D completeBar = null;
        Rectangle2D incompleteBar = null;
        Number percent = dataset.getPercentComplete(row, column);
        if (percent != null) {
            double p = percent.doubleValue();
            if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                completeBar = new Rectangle2D.Double(
                    java2dValue0, 
                    rectStart + this.startPercent * rectBreadth, 
                    rectLength * p, 
                    rectBreadth * (this.endPercent - this.startPercent)
                );
                incompleteBar = new Rectangle2D.Double(
                    java2dValue0 + rectLength * p, 
                    rectStart + this.startPercent * rectBreadth, 
                    rectLength * (1 - p), 
                    rectBreadth * (this.endPercent - this.startPercent)
                );
            }
            else if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                completeBar = new Rectangle2D.Double(
                    rectStart + this.startPercent * rectBreadth, 
                    java2dValue1 + rectLength * (1 - p), 
                    rectBreadth * (this.endPercent - this.startPercent), 
                    rectLength * p
                );
                incompleteBar = new Rectangle2D.Double(
                    rectStart + this.startPercent * rectBreadth, 
                    java2dValue1, 
                    rectBreadth * (this.endPercent - this.startPercent), 
                    rectLength * (1 - p)
                );
            }
                
        }

        Paint seriesPaint = getItemPaint(row, column);
        g2.setPaint(seriesPaint);
        g2.fill(bar);

        if (completeBar != null) {
            g2.setPaint(this.completePaint);
            g2.fill(completeBar);
        }
        if (incompleteBar != null) {
            g2.setPaint(this.incompletePaint);
            g2.fill(incompleteBar);
        }
        
        // draw the outline...
        if (getBarWidth() > BAR_OUTLINE_WIDTH_THRESHOLD) {
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
            drawItemLabel(g2, dataset, row, column, plot, generator, bar, false);
        }        

        // collect entity and tool tip information...
        if (getInfo() != null) {
            EntityCollection entities = getInfo().getOwner().getEntityCollection();
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
    

}
