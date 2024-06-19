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
 * ---------------------------------
 * StackedVerticalBarRenderer3D.java
 * ---------------------------------
 * (C) Copyright 2000-2003, by Serge V. Grachov and Contributors.
 *
 * Original Author:  Serge V. Grachov;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *                   Richard Atkinson;
 *
 * $Id: StackedVerticalBarRenderer3D.java,v 1.1 2007/10/10 20:00:08 vauchers Exp $
 *
 * Changes
 * -------
 * 31-Oct-2001 : Version 1, contributed by Serge V. Grachov (DG);
 * 15-Nov-2001 : Modified to allow for null data values (DG);
 * 13-Dec-2001 : Added tooltips (DG);
 * 15-Feb-2002 : Added isStacked() method (DG);
 * 24-May-2002 : Incorporated tooltips into chart entities (DG);
 * 19-Jun-2002 : Added check for null info in drawCategoryItem method (DG);
 * 25-Jun-2002 : Removed redundant imports (DG);
 * 26-Jun-2002 : Small change to entity (DG);
 * 05-Aug-2002 : Small modification to drawCategoryItem method to support URLs for HTML image
 *               maps (RA);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 24-Oct-2002 : Amendments for changes in CategoryDataset interface and CategoryToolTipGenerator
 *               interface (DG);
 * 05-Nov-2002 : Replaced references to CategoryDataset with TableDataset (DG);
 * 26-Nov-2002 : Replaced isStacked() method with getRangeType() method (DG);
 * 17-Jan-2003 : Moved plot classes to a separate package (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 *
 */

package com.jrefinery.chart.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import com.jrefinery.chart.ChartRenderingInfo;
import com.jrefinery.chart.axis.CategoryAxis;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.axis.VerticalAxis;
import com.jrefinery.chart.axis.VerticalNumberAxis3D;
import com.jrefinery.chart.entity.CategoryItemEntity;
import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.plot.CategoryPlot;
import com.jrefinery.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.chart.urls.CategoryURLGenerator;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.KeyedValues2DDataset;

/**
 * Renders vertical stacked bars with 3D-effect.
 * <p>
 * For use with the {@link com.jrefinery.chart.plot.VerticalCategoryPlot} class.
 *
 * @author Serge V. Grachov
 */
public class StackedVerticalBarRenderer3D extends VerticalBarRenderer3D
                                          implements Serializable {

    /**
     * Constructs a new renderer.
     *
     * @param toolTipGenerator  the tool tip generator.
     * @param urlGenerator  the URL generator.
     */
    public StackedVerticalBarRenderer3D(CategoryToolTipGenerator toolTipGenerator,
                                        CategoryURLGenerator urlGenerator) {

        super(toolTipGenerator, urlGenerator);
        
    }

    /**
     * Returns the range type.
     *
     * @return the range type.
     */
    public int getRangeType() {
        return CategoryItemRenderer.STACKED;
    }

    /**
     * Initialises the renderer.
     * <p>
     * This method gets called once at the start of the process of drawing a chart.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param plot  the plot.
     * @param info  optional information collection.
     */
    public void initialise(Graphics2D g2,
                           Rectangle2D dataArea,
                           CategoryPlot plot,
                           ChartRenderingInfo info) {

        super.initialise(g2, dataArea, plot, info);

        // calculate the bar width
        CategoryAxis domainAxis = plot.getDomainAxis();
        CategoryDataset data = plot.getCategoryDataset();
        if (data != null) {
            int columns = data.getColumnCount();
            double cm = 0.0;
            if (columns > 1) {
                cm = domainAxis.getCategoryMargin();
            }

            double used = dataArea.getWidth() * (1 - domainAxis.getLowerMargin()
                                                   - domainAxis.getUpperMargin()
                                                   - cm);
            if (columns > 0) {
                setBarWidth(used / columns);
            }
            else {
                setBarWidth(used);
            }
        }

    }

    /**
     * Draws a stacked bar (with 3D-effect) for a specific item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the plot area.
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

        Paint seriesPaint = getItemPaint(dataset, row, column);
        Paint seriesOutlinePaint = getItemOutlinePaint(dataset, row, column);

        Rectangle2D adjusted = new Rectangle2D.Double(dataArea.getX(),
                                                      dataArea.getY() + getYOffset(),
                                                      dataArea.getWidth() - getXOffset(),
                                                      dataArea.getHeight() - getYOffset());
        // BAR X
        double x0 = domainAxis.getCategoryStart(column, getColumnCount(), adjusted);

        // BAR Y
        double positiveBase = 0.0;
        double negativeBase = 0.0;

        for (int i = 0; i < row; i++) {
            Number v = data.getValue(i, column);
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

        Number value = data.getValue(row, column);
        if (value != null) {

            double xx = value.doubleValue();
            double translatedBase;
            double translatedValue;
            if (xx > 0) {
                translatedBase = rangeAxis.translateValueToJava2D(positiveBase, adjusted);
                translatedValue = rangeAxis.translateValueToJava2D(positiveBase + xx, adjusted);
            }
            else {
                translatedBase = rangeAxis.translateValueToJava2D(negativeBase, adjusted);
                translatedValue = rangeAxis.translateValueToJava2D(negativeBase + xx, adjusted);
            }
            
            double y0 = Math.max(translatedBase, translatedValue);
            double y2 = Math.min(translatedBase, translatedValue);

            double x1 = x0 + getBarWidth();
            double x2 = x0 + getXOffset();
            double x3 = x1 + getXOffset();

            double y1 = y0 - getYOffset();
            double y3 = y2 - getYOffset();

            Rectangle2D bar = new Rectangle2D.Double(x0, y2, x1 - x0, y0 - y2);
            g2.setPaint(seriesPaint);
            g2.fill(bar);

            GeneralPath barR3d = null;
            GeneralPath barT3d = null;
            double xOffset = getXOffset();
            double yOffset = getYOffset();
            VerticalAxis vAxis = (VerticalAxis) plot.getRangeAxis();
            if ((y0 - y2) != 0 && vAxis instanceof VerticalNumberAxis3D) {
                barR3d = new GeneralPath();
                barR3d.moveTo((float) x1, (float) y0);
                barR3d.lineTo((float) x1, (float) y2);
                barR3d.lineTo((float) x3, (float) y3);
                barR3d.lineTo((float) x3, (float) y1);
                barR3d.closePath();
                if (seriesPaint instanceof Color) {
                    g2.setPaint(((Color) seriesPaint).darker());
                }
                g2.fill(barR3d);

                if (xx > 0) {
                    barT3d = new GeneralPath();
                    barT3d.moveTo((float) x0, (float) y2);
                    barT3d.lineTo((float) x1, (float) y2);
                    barT3d.lineTo((float) x3, (float) y3);
                    barT3d.lineTo((float) x2, (float) y3);
                    barT3d.closePath();
                    if (seriesPaint instanceof Color) {
                        g2.setPaint(((Color) seriesPaint));//.brighter());
                    }
                    g2.fill(barT3d);
                }
            }

            if ((x1 - x0) > 3) {
                g2.setStroke(getItemStroke(dataset, row, column));
                g2.setPaint(getItemOutlinePaint(dataset, row, column));
                g2.draw(bar);
                if (barR3d != null) {
                    g2.draw(barR3d);
                }
                if (barT3d != null) {
                    g2.draw(barT3d);
                }
            }

            // collect entity and tool tip information...
            if (getInfo() != null) {
                EntityCollection entities = getInfo().getEntityCollection();
                if (entities != null) {
                    String tip = "";
                    if (getToolTipGenerator() != null) {
                        tip = getToolTipGenerator().generateToolTip(data, row, column);
                    }
                    String url = null;
                    if (getURLGenerator() != null) {
                        url = getURLGenerator().generateURL(data, row, column);
                    }
                    CategoryItemEntity entity = new CategoryItemEntity(bar, tip, url, row,
                                                                       data.getColumnKey(column),
                                                                       column);
                    entities.addEntity(entity);
                }
            }
        }

    }

}
