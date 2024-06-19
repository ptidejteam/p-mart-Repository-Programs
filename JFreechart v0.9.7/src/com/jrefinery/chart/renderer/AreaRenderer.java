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
 * -----------------
 * AreaRenderer.java
 * -----------------
 * (C) Copyright 2002, 2003, by Jon Iles and Contributors.
 *
 * Original Author:  Jon Iles;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: AreaRenderer.java,v 1.1 2007/10/10 20:00:08 vauchers Exp $
 *
 * Changes:
 * --------
 * 21-May-2002 : Version 1, contributed by John Iles (DG);
 * 29-May-2002 : Now extends AbstractCategoryItemRenderer (DG);
 * 11-Jun-2002 : Updated Javadoc comments (DG);
 * 25-Jun-2002 : Removed unnecessary imports (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 10-Oct-2002 : Added constructors and basic entity support (DG);
 * 24-Oct-2002 : Amendments for changes in CategoryDataset interface and CategoryToolTipGenerator
 *               interface (DG);
 * 05-Nov-2002 : Replaced references to CategoryDataset with TableDataset (DG);
 * 06-Nov-2002 : Renamed drawCategoryItem(...) --> drawItem(...) and now using axis for
 *               category spacing.  Renamed AreaCategoryItemRenderer --> AreaRenderer (DG);
 * 17-Jan-2003 : Moved plot classes into a separate package (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 10-Apr-2003 : Changed CategoryDataset to KeyedValues2DDataset in drawItem(...) method (DG);
 *
 */

package com.jrefinery.chart.renderer;

import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import com.jrefinery.chart.axis.CategoryAxis;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.entity.CategoryItemEntity;
import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.plot.CategoryPlot;
import com.jrefinery.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.chart.urls.CategoryURLGenerator;
import com.jrefinery.data.KeyedValues2DDataset;

/**
 * A category item renderer that draws area charts.  
 * <p>
 * You can use this renderer with the {@link com.jrefinery.chart.plot.VerticalCategoryPlot} class.
 *
 * @author Jon Iles
 */
public class AreaRenderer extends AbstractCategoryItemRenderer implements Serializable {

    /**
     * Creates a new renderer.
     */
    public AreaRenderer() {
        this(null, null);
    }

    /**
     * Creates a new renderer.
     *
     * @param toolTipGenerator  the tool tip generator (null permitted).
     * @param urlGenerator  the URL generator (null permitted).
     */
    public AreaRenderer(CategoryToolTipGenerator toolTipGenerator,
                        CategoryURLGenerator urlGenerator) {

        super(toolTipGenerator, urlGenerator);

    }

    /**
     * Draw a single data item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data plot area.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
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

        // plot non-null values...
        Number value = data.getValue(row, column);
        if (value != null) {
            float x0 = (float) domainAxis.getCategoryStart(column, data.getColumnCount(), dataArea);
            float x1 = (float) domainAxis.getCategoryMiddle(column, data.getColumnCount(), 
                                                            dataArea);
            float x2 = (float) domainAxis.getCategoryEnd(column, data.getColumnCount(), dataArea);
 
            x0 = (float) Math.round(x0);
            x1 = (float) Math.round(x1);
            x2 = (float) Math.round(x2);
            
            double yy1 = value.doubleValue();

            double yy0 = 0.0;
            if (column > 0) {
                Number n0 = data.getValue(row, column - 1);
                if (n0 != null) {
                    yy0 = (n0.doubleValue() + yy1) / 2.0;
                }
            }

            double yy2 = 0.0;
            if (column < data.getColumnCount() - 1) {
                Number n2 = data.getValue(row, column + 1);
                if (n2 != null) {
                    yy2 = (n2.doubleValue() + yy1) / 2.0;
                }
            }
            
            float y0 = (float) rangeAxis.translateValueToJava2D(yy0, dataArea);
            float y1 = (float) rangeAxis.translateValueToJava2D(yy1, dataArea);
            float y2 = (float) rangeAxis.translateValueToJava2D(yy2, dataArea);
            float yz = (float) rangeAxis.translateValueToJava2D(0.0, dataArea);

            g2.setPaint(getItemPaint(dataset, row, column));
            g2.setStroke(getSeriesStroke(dataset, row));

            GeneralPath area = new GeneralPath();
            
            area.moveTo(x0, yz);
            area.lineTo(x0, y0);
            area.lineTo(x1, y1);
            area.lineTo(x2, y2);
            area.lineTo(x2, yz);
            area.closePath();
            
            g2.setPaint(getItemPaint(dataset, row, column));
            g2.fill(area);
            
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
                    CategoryItemEntity entity
                        = new CategoryItemEntity(area, tip, url, row,
                                                 data.getColumnKey(column), column);
                    entities.addEntity(entity);
                }
            }
        }

    }

}
