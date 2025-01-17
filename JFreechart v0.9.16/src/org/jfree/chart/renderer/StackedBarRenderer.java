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
 * StackedBarRenderer.java
 * -----------------------
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Richard Atkinson;
 *                   Thierry Saura;
 *                   Christian W. Zuckschwerdt;
 *
 * $Id: StackedBarRenderer.java,v 1.1 2007/10/10 19:25:28 vauchers Exp $
 *
 * Changes
 * -------
 * 19-Oct-2001 : Version 1 (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 23-Oct-2001 : Changed intro and trail gaps on bar plots to use percentage of available space
 *               rather than a fixed number of units (DG);
 * 15-Nov-2001 : Modified to allow for null data values (DG);
 * 22-Nov-2001 : Modified to allow for negative data values (DG);
 * 13-Dec-2001 : Added tooltips (DG);
 * 16-Jan-2002 : Fixed bug for single category datasets (DG);
 * 15-Feb-2002 : Added isStacked() method (DG);
 * 14-Mar-2002 : Modified to implement the CategoryItemRenderer interface (DG);
 * 24-May-2002 : Incorporated tooltips into chart entities (DG);
 * 11-Jun-2002 : Added check for (permitted) null info object, bug and fix reported by David
 *               Basten.  Also updated Javadocs. (DG);
 * 25-Jun-2002 : Removed redundant import (DG);
 * 26-Jun-2002 : Small change to entity (DG);
 * 05-Aug-2002 : Small modification to drawCategoryItem method to support URLs for HTML image
 *               maps (RA);
 * 08-Aug-2002 : Added optional linking lines, contributed by Thierry Saura (DG);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 24-Oct-2002 : Amendments for changes in CategoryDataset interface and CategoryToolTipGenerator
 *               interface (DG);
 * 05-Nov-2002 : Replaced references to CategoryDataset with TableDataset (DG);
 * 26-Nov-2002 : Replaced isStacked() method with getRangeType() method (DG);
 * 17-Jan-2003 : Moved plot classes to a separate package (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 12-May-2003 : Merged horizontal and vertical stacked bar renderers (DG);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 08-Sep-2003 : Fixed bug 799668 (isBarOutlineDrawn() ignored) (DG);
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 21-Oct-2003 : Moved bar width into renderer state (DG);
 * 26-Nov-2003 : Added code to respect maxBarWidth attribute (DG);
 * 
 */

package org.jfree.chart.renderer;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.CategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.jfree.util.PublicCloneable;

/**
 * A stacked bar renderer for use with the {@link org.jfree.chart.plot.CategoryPlot} class.
 *
 * @author David Gilbert
 */
public class StackedBarRenderer extends BarRenderer 
                                implements Cloneable, PublicCloneable, Serializable {

    /**
     * Creates a new renderer with no tool tip generator and no URL generator.
     * <P>
     * The defaults (no tool tip or URL generators) have been chosen to minimise the processing
     * required to generate a default chart.  If you require tool tips or URLs, then you can
     * easily add the required generators.
     */
    public StackedBarRenderer() {
        super();
        
        // set the default item label positions, which will only be used if the user
        // requests visible item labels...
        ItemLabelPosition p = new ItemLabelPosition(
            ItemLabelAnchor.CENTER, TextAnchor.CENTER, TextAnchor.CENTER, 0.0
        );
        setBasePositiveItemLabelPosition(p);
        setBaseNegativeItemLabelPosition(p);
        setPositiveItemLabelPositionFallback(null);
        setNegativeItemLabelPositionFallback(null);
    }

    /**
     * Returns the range type.
     *
     * @return the range type.
     */
    public RangeType getRangeType() {
        return RangeType.STACKED;
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
        CategoryDataset data = getDataset(plot, rendererIndex);
        if (data != null) {
            PlotOrientation orientation = plot.getOrientation();
            double space = 0.0;
            if (orientation == PlotOrientation.HORIZONTAL) {
                space = dataArea.getHeight();
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                space = dataArea.getWidth();
            }
            double maxWidth = space * getMaxBarWidth();
            int columns = data.getColumnCount();
            double categoryMargin = 0.0;
            if (columns > 1) {
                categoryMargin = domainAxis.getCategoryMargin();
            }

            double used = space * (1 - domainAxis.getLowerMargin() - domainAxis.getUpperMargin()
                                     - categoryMargin);
            if (columns > 0) {
                state.setBarWidth(Math.min(used / columns, maxWidth));
            }
            else {
                state.setBarWidth(Math.min(used, maxWidth));
            }
        }

    }

    /**
     * Draws a stacked bar for a specific item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the plot area.
     * @param plot  the plot.
     * @param domainAxis  the domain (category) axis.
     * @param rangeAxis  the range (value) axis.
     * @param dataset  the data.
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
        double barW0 = domainAxis.getCategoryMiddle(column, getColumnCount(), dataArea,
                                                    plot.getDomainAxisEdge()) 
                                                    - state.getBarWidth() / 2.0;

        double positiveBase = 0.0;
        double negativeBase = 0.0;

        for (int i = 0; i < row; i++) {
            Number v = dataset.getValue(i, column);
            if (v != null) {
                double d = v.doubleValue();
                if (d > 0) {
                    positiveBase = positiveBase + d;
                }
                else {
                    negativeBase = negativeBase + d;
                }
            }
        }

        double translatedBase;
        double translatedValue;
        RectangleEdge location = plot.getRangeAxisEdge();
        if (value > 0.0) {
            translatedBase = rangeAxis.translateValueToJava2D(positiveBase, dataArea, location);
            translatedValue = rangeAxis.translateValueToJava2D(positiveBase + value, dataArea, 
                                                               location);
        }
        else {
            translatedBase = rangeAxis.translateValueToJava2D(negativeBase, dataArea, location);
            translatedValue = rangeAxis.translateValueToJava2D(negativeBase + value, dataArea, 
                                                               location);
        }
        double barL0 = Math.min(translatedBase, translatedValue);
        double barLength = Math.max(Math.abs(translatedValue - translatedBase), 
                                    getMinimumBarLength());

        Rectangle2D bar = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            bar = new Rectangle2D.Double(barL0, barW0, barLength, state.getBarWidth());
        }
        else {
            bar = new Rectangle2D.Double(barW0, barL0, state.getBarWidth(), barLength);
        }
        Paint seriesPaint = getItemPaint(row, column);
        g2.setPaint(seriesPaint);
        g2.fill(bar);
        if (isDrawBarOutline() && state.getBarWidth() > BAR_OUTLINE_WIDTH_THRESHOLD) {
            g2.setStroke(getItemStroke(row, column));
            g2.setPaint(getItemOutlinePaint(row, column));
            g2.draw(bar);
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

}
