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
 * ------------------------
 * IntervalBarRenderer.java
 * ------------------------
 * (C) Copyright 2002-2004, by Jeremy Bowman.
 *
 * Original Author:  Jeremy Bowman;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *                   Christian W. Zuckschwerdt;
 *
 * $Id: IntervalBarRenderer.java,v 1.1 2007/10/10 19:46:14 vauchers Exp $
 *
 * Changes
 * -------
 * 29-Apr-2002 : Version 1, contributed by Jeremy Bowman (DG);
 * 11-May-2002 : Use CategoryPlot.getLabelsVisible() (JB);
 * 29-May-2002 : Added constructors (DG);
 * 26-Jun-2002 : Added axis to initialise method (DG);
 * 20-Sep-2002 : Added basic support for chart entities (DG);
 * 24-Oct-2002 : Amendments for changes in CategoryDataset interface and CategoryToolTipGenerator
 *               interface (DG);
 * 05-Nov-2002 : Base dataset is now TableDataset not CategoryDataset (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 19-Aug-2003 : Implemented Cloneable and PublicCloneable (DG);
 * 08-Sep-2003 : Added checks for null values (DG);
 * 07-Oct-2003 : Added renderer state (DG);
 * 21-Oct-2003 : Bar width moved into renderer state (DG);
 * 23-Dec-2003 : Removed the deprecated MultiIntervalCategoryDataset interface (DG);
 * 
 */

package org.jfree.chart.renderer;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryLabelGenerator;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.CategoryDataset;
import org.jfree.data.IntervalCategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;


/**
 * A renderer that handles the drawing of bars for a bar plot where
 * each bar has a high and low value.
 * <p>
 * For use with the {@link CategoryPlot} class.
 *
 * @author Jeremy Bowman
 */
public class IntervalBarRenderer extends BarRenderer
                                 implements CategoryItemRenderer, 
                                            Cloneable, 
                                            PublicCloneable, 
                                            Serializable {

    /**
     * Constructs a new renderer.
     */
    public IntervalBarRenderer() {
        super();
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

         if (dataset instanceof IntervalCategoryDataset) {
             IntervalCategoryDataset d = (IntervalCategoryDataset) dataset;
             drawInterval(g2, state, dataArea, plot, domainAxis, rangeAxis, d, row, column);
         }
         else {
             super.drawItem(g2, state, dataArea, plot, domainAxis, rangeAxis, dataset, row, column);
         } 
         
     }
                          
     /**
      * Draws a single interval.
      *
      * @param g2  the graphics device.
      * @param state  the renderer state.
      * @param dataArea  the data plot area.
      * @param plot  the plot.
      * @param domainAxis  the domain axis.
      * @param rangeAxis  the range axis.
      * @param dataset  the data.
      * @param row  the row index (zero-based).
      * @param column  the column index (zero-based).
      */
     protected void drawInterval(Graphics2D g2,
                                 CategoryItemRendererState state,
                                 Rectangle2D dataArea,
                                 CategoryPlot plot,
                                 CategoryAxis domainAxis,
                                 ValueAxis rangeAxis,
                                 IntervalCategoryDataset dataset,
                                 int row,
                                 int column) {

        int seriesCount = getRowCount();
        int categoryCount = getColumnCount();

        PlotOrientation orientation = plot.getOrientation();
        
        double rectX = 0.0;
        double rectY = 0.0;

        RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
        
        // Y0
        Number value0 = dataset.getEndValue(row, column);
        if (value0 == null) {
            return;
        }
        double java2dValue0 = rangeAxis.valueToJava2D(
            value0.doubleValue(), dataArea, rangeAxisLocation
        );

        // Y1
        Number value1 = dataset.getStartValue(row, column);
        if (value1 == null) {
            return;
        }
        double java2dValue1 = rangeAxis.valueToJava2D(
            value1.doubleValue(), dataArea, rangeAxisLocation
        );

        if (java2dValue1 < java2dValue0) {
            double temp = java2dValue1;
            java2dValue1 = java2dValue0;
            java2dValue0 = temp;
            Number tempNum = value1;
            value1 = value0;
            value0 = tempNum;
        }

        // BAR WIDTH
        double rectWidth = state.getBarWidth();

        // BAR HEIGHT
        double rectHeight = Math.abs(java2dValue1 - java2dValue0);

        if (orientation == PlotOrientation.HORIZONTAL) {
            // BAR Y
            rectY = domainAxis.getCategoryStart(column, getColumnCount(), dataArea, 
                                                domainAxisLocation);
            if (seriesCount > 1) {
                double seriesGap = dataArea.getHeight() * getItemMargin()
                                   / (categoryCount * (seriesCount - 1));
                rectY = rectY + row * (state.getBarWidth() + seriesGap);
            }
            else {
                rectY = rectY + row * state.getBarWidth();
            }
            
            rectX = java2dValue0;

            rectHeight = state.getBarWidth();
            rectWidth = Math.abs(java2dValue1 - java2dValue0);

        }
        else if (orientation == PlotOrientation.VERTICAL) {
            // BAR X
            rectX = domainAxis.getCategoryStart(column, getColumnCount(), dataArea,
                                                domainAxisLocation);

            if (seriesCount > 1) {
                double seriesGap = dataArea.getWidth() * getItemMargin()
                                   / (categoryCount * (seriesCount - 1));
                rectX = rectX + row * (state.getBarWidth() + seriesGap);
            }
            else {
                rectX = rectX + row * state.getBarWidth();
            }

            rectY = java2dValue0;

        }
        Rectangle2D bar = new Rectangle2D.Double(rectX, rectY, rectWidth, rectHeight);
        Paint seriesPaint = getItemPaint(row, column);
        g2.setPaint(seriesPaint);
        g2.fill(bar);
        
        // draw the outline...
        if (state.getBarWidth() > BAR_OUTLINE_WIDTH_THRESHOLD) {
            Stroke stroke = getItemOutlineStroke(row, column);
            Paint paint = getItemOutlinePaint(row, column);
            if (stroke != null && paint != null) {
                g2.setStroke(stroke);
                g2.setPaint(paint);
                g2.draw(bar);
            }
        }
        
        CategoryLabelGenerator generator = getLabelGenerator(row, column);
        if (generator != null && isItemLabelVisible(row, column)) {
            drawItemLabel(g2, dataset, row, column, plot, generator, bar, false);
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
                    bar, tip, url, dataset, row, dataset.getColumnKey(column), column
                );
                entities.addEntity(entity);
            }
        }

    }

}
