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
 * AbstractCategoryItemRenderer.java
 * ---------------------------------
 * (C) Copyright 2002, 2003 by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Richard Atkinson;
 *
 * $Id: AbstractCategoryItemRenderer.java,v 1.1 2007/10/10 19:57:50 vauchers Exp $
 *
 * Changes:
 * --------
 * 29-May-2002 : Version 1 (DG);
 * 06-Jun-2002 : Added accessor methods for the tool tip generator (DG);
 * 11-Jun-2002 : Made constructors protected (DG);
 * 26-Jun-2002 : Added axis to initialise method (DG);
 * 05-Aug-2002 : Added urlGenerator member variable plus accessors (RA);
 * 22-Aug-2002 : Added categoriesPaint attribute, based on code submitted by Janet Banks.
 *               This can be used when there is only one series, and you want each category
 *               item to have a different color (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 29-Oct-2002 : Fixed bug where background image for plot was not being drawn (DG);
 * 05-Nov-2002 : Replaced references to CategoryDataset with TableDataset (DG);
 * 26-Nov 2002 : Replaced the isStacked() method with getRangeType() (DG);
 * 09-Jan-2003 : Renamed grid-line methods (DG);
 * 17-Jan-2003 : Moved plot classes into separate package (DG);
 */

package com.jrefinery.chart.renderer;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import com.jrefinery.chart.ChartRenderingInfo;
import com.jrefinery.chart.LegendItem;
import com.jrefinery.chart.Marker;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.plot.Plot;
import com.jrefinery.chart.plot.CategoryPlot;
import com.jrefinery.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.chart.urls.CategoryURLGenerator;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.Range;

/**
 * An abstract base class that you can use to implement a new {@link CategoryItemRenderer}.
 * <p>
 * When you create a new {@link CategoryItemRenderer} you are not required to extend this class,
 * but it makes the job easier.
 *
 * @author David Gilbert
 */
public abstract class AbstractCategoryItemRenderer extends AbstractRenderer
                                                   implements CategoryItemRenderer {

    /** The number of rows in the dataset (temporary record). */
    private int rowCount;

    /** The number of columns in the dataset (temporary record). */
    private int columnCount;

    /** Paint objects for categories (null permitted). */
    private Paint[] categoriesPaint;

    /** The tooltip generator. */
    private CategoryToolTipGenerator toolTipGenerator;

    /** The URL generator. */
    private CategoryURLGenerator urlGenerator;

    /** An internal flag whether to use the categoriesPaint array. */
    private boolean useCategoriesPaint;

    /**
     * Creates a new renderer with no tool tip generator and no URL generator.
     * <P>
     * The defaults (no tool tip or URL generators) have been chosen to minimise the processing
     * required to generate a default chart.  If you require tool tips or URLs, then you can
     * easily add the required generators.
     */
    protected AbstractCategoryItemRenderer() {
        this(null, null);
    }

    /**
     * Creates a new renderer with the specified tooltip generator but no URL generator.
     *
     * @param toolTipGenerator  the tool tip generator.
     */
    protected AbstractCategoryItemRenderer(CategoryToolTipGenerator toolTipGenerator) {
        this(toolTipGenerator, null);
    }

    /**
     * Creates a new renderer with the specified URL generator but no tooltip generator.
     *
     * @param urlGenerator  the URL generator.
     */
    protected AbstractCategoryItemRenderer(CategoryURLGenerator urlGenerator) {
        this(null, urlGenerator);
    }

    /**
     * Creates a new renderer with the specified tooltip generator and URL generator.
     *
     * @param toolTipGenerator  the tool tip generator.
     * @param urlGenerator  the URL generator.
     */
    protected AbstractCategoryItemRenderer(CategoryToolTipGenerator toolTipGenerator,
                                           CategoryURLGenerator urlGenerator) {

        this.categoriesPaint = null;
        this.toolTipGenerator = toolTipGenerator;
        this.urlGenerator = urlGenerator;

    }

    /**
     * Returns the number of rows in the dataset.  This value is updated in the 
     * {@link AbstractCategoryItemRenderer#initialise} method.
     *
     * @return the row count.
     */
    public int getRowCount() {
        return this.rowCount;
    }

    /**
     * Returns the number of columns in the dataset.  This value is updated in the
     * {@link AbstractCategoryItemRenderer#initialise} method.
     *
     * @return the column count.
     */
    public int getColumnCount() {
        return this.columnCount;
    }

    /**
     * Returns the flag that controls whether or not the renderer uses the category paint settings.
     *
     * @return the flag.
     */
    public boolean getUseCategoriesPaint() {
        return this.useCategoriesPaint;
    }

    /**
     * Returns the paint to use for the categories when there is just one series.
     * <P>
     * If this is null, the categories will all have the same color (that of the series).
     *
     * @return the paint for the categories.
     */
    public Paint[] getCategoriesPaint() {
        return this.categoriesPaint;
    }

    /**
     * Sets the paint to be used for categories under special circumstances.
     * <P>
     * This attribute is provided for the situation where there is just one series, and you want
     * each category item to be plotted using a different color (ordinarily, the series color is
     * used for all the items in the series).
     * <P>
     * May not be observed by all subclasses yet.
     *
     * @param paint  the colors.
     */
    public void setCategoriesPaint(Paint[] paint) {

        Object oldValue = this.categoriesPaint;
        this.categoriesPaint = paint;
        firePropertyChanged("renderer.CategoriesPaint", oldValue, paint);

    }

    /**
     * Returns the paint for a specific category (possibly null).
     *
     * @param index  the category index.
     *
     * @return the paint for the category.
     */
    public Paint getCategoryPaint(int index) {

        Paint result = null;
        if (this.categoriesPaint != null) {
            result = this.categoriesPaint[index % categoriesPaint.length];
        }
        return result;
    }

    /**
     * Returns the tool tip generator.
     *
     * @return the tool tip generator.
     */
    public CategoryToolTipGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }

    /**
     * Sets the tool tip generator.
     *
     * @param generator  the tool tip generator.
     */
    public void setToolTipGenerator(CategoryToolTipGenerator generator) {

        Object oldValue = this.toolTipGenerator;
        this.toolTipGenerator = generator;
        firePropertyChanged("renderer.ToolTipGenerator", oldValue, generator);

    }

    /**
     * Returns the URL generator for HTML image maps.
     *
     * @return the URL generator.
     */
    public CategoryURLGenerator getURLGenerator() {
        return this.urlGenerator;
    }

    /**
     * Sets the URL generator for HTML image maps.
     *
     * @param urlGenerator  the URL generator.
     */
    public void setURLGenerator(CategoryURLGenerator urlGenerator) {

        Object oldValue = this.urlGenerator;
        this.urlGenerator = urlGenerator;
        firePropertyChanged("renderer.URLGenerator", oldValue, urlGenerator);

    }

    /**
     * Initialises the renderer.
     * <P>
     * Stores a reference to the {@link ChartRenderingInfo} object (which might be 
     * <code>null</code>), and then sets the useCategoriesPaint flag according to the special case 
     * conditions a) there is only one series and b) the categoriesPaint array is not null.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param plot  the plot.
     * @param info  an object for returning information about the structure of the chart 
     *              (<code>null</code> permitted).
     *
     */
    public void initialise(Graphics2D g2, Rectangle2D dataArea,
                           CategoryPlot plot, ChartRenderingInfo info) {

        setPlot(plot);
        CategoryDataset data = plot.getCategoryDataset();
        if (data != null) {
            this.rowCount = data.getRowCount();
            this.columnCount = data.getColumnCount();
            // the renderer can use different colors for the categories if there is one series and
            // the categoriesPaint array has been populated...
            if ((data.getRowCount() == 1) && (this.categoriesPaint != null)) {
                this.useCategoriesPaint = this.categoriesPaint.length > 0;
            }
        }
        else {
            this.rowCount = 0;
            this.columnCount = 0;
        }
        setInfo(info);

    }

    /**
     * Returns the range type for the renderer.
     * <p>
     * The default implementation returns <code>STANDARD</code>, subclasses may override this
     * behaviour.
     * <p>
     * The {@link CategoryPlot} uses this information when auto-calculating the range for the axis.
     *
     * @return the range type.
     */
    public int getRangeType() {
        return CategoryItemRenderer.STANDARD;
    }

    /**
     * Draws a background for the data area.  The default implementation just gets the plot to 
     * draw the outline, but some renderers will override this behaviour.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the data area.
     */
    public void drawBackground(Graphics2D g2, 
                               CategoryPlot plot,
                               Rectangle2D dataArea) {

        plot.drawBackground(g2, dataArea);
        
    }
    
    /**
     * Draws an outline for the data area.  The default implementation just gets the plot to 
     * draw the outline, but some renderers will override this behaviour.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the data area.
     */
    public void drawOutline(Graphics2D g2, 
                            CategoryPlot plot,
                            Rectangle2D dataArea) {

        plot.drawOutline(g2, dataArea);
        
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
     */
    public void drawDomainGridline(Graphics2D g2,
                                   CategoryPlot plot,
                                   Rectangle2D dataArea,
                                   double value) {

        Line2D line = new Line2D.Double(value, dataArea.getMinY(),
                                        value, dataArea.getMaxY());
        Paint paint = plot.getDomainGridlinePaint();
        Stroke stroke = plot.getDomainGridlineStroke();
        g2.setPaint(paint != null ? paint : Plot.DEFAULT_OUTLINE_PAINT);
        g2.setStroke(stroke != null ? stroke : Plot.DEFAULT_OUTLINE_STROKE);
        g2.draw(line);

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

        double y = axis.translateValueToJava2D(value, dataArea);
        Line2D line = new Line2D.Double(dataArea.getMinX(), y,
                                        dataArea.getMaxX(), y);
        Paint paint = plot.getRangeGridlinePaint();
        Stroke stroke = plot.getRangeGridlineStroke();
        g2.setPaint(paint != null ? paint : Plot.DEFAULT_OUTLINE_PAINT);
        g2.setStroke(stroke != null ? stroke : Plot.DEFAULT_OUTLINE_STROKE);
        g2.draw(line);

    }

    /**
     * Draws a marker for the range axis.
     * <P>
     * A marker is a constant value, usually represented by a line.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the range axis.
     * @param marker  the marker to be drawn.
     * @param dataArea  the area inside the axes.
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

        double y = axis.translateValueToJava2D(marker.getValue(), dataArea);
        Line2D line = new Line2D.Double(dataArea.getMinX(), y,
                                        dataArea.getMaxX(), y);
        Paint paint = marker.getOutlinePaint();
        Stroke stroke = marker.getOutlineStroke();
        g2.setPaint(paint != null ? paint : Plot.DEFAULT_OUTLINE_PAINT);
        g2.setStroke(stroke != null ? stroke : Plot.DEFAULT_OUTLINE_STROKE);
        g2.draw(line);

    }

    /**
     * Returns a legend item for a series.
     *
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     *
     * @return the legend item.
     */
    public LegendItem getLegendItem(int datasetIndex, int series) {

        CategoryPlot plot = (CategoryPlot) getPlot();
        if (plot == null) {
            return null;
        }

        CategoryDataset dataset;
        if (datasetIndex == 0) {
            dataset = plot.getCategoryDataset();
        }
        else {
            dataset = plot.getSecondaryCategoryDataset();
        }
        String label = dataset.getRowKey(series).toString();
        String description = label;
        Shape shape = getSeriesShape(datasetIndex, series);
        Paint paint = getSeriesPaint(datasetIndex, series);
        Paint outlinePaint = getSeriesOutlinePaint(datasetIndex, series);
        Stroke stroke = getSeriesStroke(datasetIndex, series);

        return new LegendItem(label, description,
                              shape, paint, outlinePaint, stroke);

    }

}
