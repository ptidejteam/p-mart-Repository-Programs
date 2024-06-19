/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
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
 * ---------------------
 * PeriodMarkerPlot.java
 * ---------------------
 * (C) Copyright 2002, by Sylvain Vieujot.
 *
 * Original Author:  Sylvain Vieujot;
 * Contributor(s):   -;
 *
 * $Id: PeriodMarkerPlot.java,v 1.1 2007/10/10 18:56:25 vauchers Exp $
 *
 * Changes
 * -------
 * 08-Jan-2002 : Version 1, thanks to SV.  Added parameter for tooltips so that the code will
 *               compile in the current development version - tooltips ignored at this point (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import com.jrefinery.data.*;
import com.jrefinery.chart.tooltips.*;

/**
 *
 * @author  sylvain
 * @version
 */
public class PeriodMarkerPlot extends Plot implements HorizontalValuePlot, VerticalValuePlot {

    /** Creates new SignalsPlot */
    public PeriodMarkerPlot(Axis horizontal, Axis vertical) throws AxisNotCompatibleException,
                                                                   PlotNotCompatibleException {

          super(horizontal, vertical);

    }

    /**
     * Returns the plot type as a string. This implementation returns "HiLow Plot".
     */
    public String getPlotType() {
          return "Period Marker Plot";
    }

    /**
     * A convenience method that returns the dataset for the plot, cast as an HighLowDataset.
     */
    /*public SignalsDataset getDataset() {
	return (SignalsDataset)chart.getDataset();
    }*/
    public XYDataset getTempXYDataset() { // Usefull until SignalsDataset is included in jcommon.SubSeriesDataset
	return (XYDataset)chart.getDataset();
    }

    /**
     * Checks the compatibility of a horizontal axis, returning true if the axis is compatible with
     * the plot, and false otherwise.
     * @param axis The horizontal axis.
     */
    public boolean isCompatibleHorizontalAxis(Axis axis) {
	if (axis instanceof HorizontalNumberAxis) {
	    return true;
	}
	else if (axis instanceof HorizontalDateAxis) {
	    return true;
	}
        else return false;
    }

    /**
     * Checks the compatibility of a vertical axis, returning true if the axis is compatible with
     * the plot, and false otherwise.  The vertical axis for this plot must be an instance of
     * VerticalNumberAxis.
     * @param axis The vertical axis.
     */
    public boolean isCompatibleVerticalAxis(Axis axis)
    {
	if (axis instanceof VerticalNumberAxis)
	    return true;
	else
	    return false;
    }

  /**
   * A convenience method that returns a reference to the horizontal axis cast as a
   * HorizontalValueAxis.
   */
  public ValueAxis getHorizontalValueAxis()
    {
      return (ValueAxis)horizontalAxis;
    }

  /**
   * A convenience method that returns a reference to the vertical axis cast as a
   * VerticalNumberAxis.
   */
  public ValueAxis getVerticalValueAxis()
    {
      return (ValueAxis)verticalAxis;
    }

    /**
     * Returns the minimum value in the domain, since this is plotted against the horizontal axis
     * for a HighLowPlot.
     */
    public Number getMinimumHorizontalDataValue() {
	//SignalsDataset data = getDataset();
        XYDataset data = getTempXYDataset();
	if( data ==null )
            return null;

        long minimum = Long.MAX_VALUE;
        int seriesCount = data.getSeriesCount();
        for (int series=0; series<seriesCount; series++) {
            int itemCount = data.getItemCount(series);
            for(int itemIndex = 0; itemIndex < itemCount; itemIndex++){
                Number value = data.getXValue(series, itemIndex); // Adjust with type to make room for the symbols
                if (value!=null)
                    minimum = Math.min(minimum, value.longValue());
            }
        }

        return new Long(minimum);
    }

    /**
     * Returns the maximum value in the domain, since this is plotted against the horizontal axis
     * for a HighLowPlot.
     */
    public Number getMaximumHorizontalDataValue() {
     	//SignalsDataset data = getDataset();
        XYDataset data = getTempXYDataset();
	if( data ==null )
            return null;

        long maximum = Long.MIN_VALUE;
        int seriesCount = data.getSeriesCount();
        for (int series=0; series<seriesCount; series++) {
            int itemCount = data.getItemCount(series);
            for(int itemIndex = 0; itemIndex < itemCount; itemIndex++){
                Number value = data.getXValue(series, itemIndex); // Adjust with type to make room for the symbols
                if (value!=null)
                    maximum = Math.max(maximum, value.longValue());
            }
        }

        return new Long(maximum);
    }

    /**
     * Returns the minimum value in the range, since this is plotted against the vertical axis for
     * a HighLowPlot.
     */
    public Number getMinimumVerticalDataValue() {
        return new Double(Double.POSITIVE_INFINITY); // null doesn't work (??)
    }

    /**
     * Returns the maximum value in the range, since this is plotted against the vertical axis for
     * a HighLowPlot.
     */
    public Number getMaximumVerticalDataValue() {
        return null; //new Double(Double.NEGATIVE_INFINITY); doesn't work (??)
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     * @param g2 The graphics device;
     * @param drawArea The area within which the plot should be drawn;
     */
    public void draw(Graphics2D g2, Rectangle2D drawArea, DrawInfo info){
        if (insets!=null) {
            drawArea = new Rectangle2D.Double(drawArea.getX()+insets.left,
                                              drawArea.getY()+insets.top,
                                              drawArea.getWidth()-insets.left-insets.right,
                                              drawArea.getHeight()-insets.top-insets.bottom);
        }

        // we can cast the axes because HiLowPlot enforces support of these interfaces
        HorizontalAxis ha = getHorizontalAxis();
        VerticalAxis va = getVerticalAxis();

        double h = ha.reserveHeight(g2, this, drawArea);
        Rectangle2D vAxisArea = va.reserveAxisArea(g2, this, drawArea, h);

        // compute the plot area
        Rectangle2D plotArea = new Rectangle2D.Double(drawArea.getX()+vAxisArea.getWidth(),
                                                      drawArea.getY(),
                                                      drawArea.getWidth()-vAxisArea.getWidth(),
                                                      drawArea.getHeight()-h);

        drawOutlineAndBackground(g2, plotArea);

        // draw the axes

        this.horizontalAxis.draw(g2, drawArea, plotArea);
        this.verticalAxis.draw(g2, drawArea, plotArea);

        Shape originalClip = g2.getClip();
        g2.clip(plotArea);

        //SignalsDataset data = getDataset();
        XYDataset data = getTempXYDataset();
        if( data!= null ){
            int seriesCount = data.getSeriesCount();
            for(int serie=0; serie<seriesCount; serie++)
                drawMarkedPeriods(data, serie, g2, plotArea);   // area should be remaining area only
        }

        g2.setClip(originalClip);
    }

    private void drawMarkedPeriods(XYDataset data, int serie, Graphics2D g2, Rectangle2D plotArea){

        Paint thisSeriePaint = this.getSeriesPaint(serie);
        g2.setPaint( thisSeriePaint );
        g2.setStroke( this.getSeriesStroke(serie) );

        float opacity = 0.1f;
        if( thisSeriePaint instanceof Color ){
            Color thisSerieColor = (Color)thisSeriePaint;
            int colorSaturation = thisSerieColor.getRed()+thisSerieColor.getGreen()+thisSerieColor.getBlue();
            if( colorSaturation > 255 )
                opacity = opacity * colorSaturation / 255.0f;
        }
        Composite originalComposite = g2.getComposite();
        g2.setComposite( AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity) );

        double minY = plotArea.getMinY();
        double maxY = plotArea.getMaxY();

        int itemCount = data.getItemCount(serie);
        for(int itemIndex = 0; itemIndex < itemCount; itemIndex++){
            if( data.getYValue(serie, itemIndex).doubleValue() == 0 ) // un -marked period
                continue;

            Number xStart;
            if( itemIndex > 0 )
                xStart = new Long( (data.getXValue(serie, itemIndex).longValue()+data.getXValue(serie, itemIndex-1).longValue())/2 );
            else
                xStart = data.getXValue(serie, itemIndex);
            int j=itemIndex+1;
            while( j<itemCount ){
                if( data.getYValue(serie, j).doubleValue() == 0 )
                    break;
                j++;
            }
            itemIndex = j;
            Number xEnd;
            if( j < itemCount )
                xEnd = new Long( (data.getXValue(serie, j-1).longValue() + data.getXValue(serie, j).longValue())/2 );
            else
                xEnd = data.getXValue(serie, j-1);

            double xxStart = getHorizontalValueAxis().translateValueToJava2D(xStart.doubleValue(), plotArea);
            double xxEnd = getHorizontalValueAxis().translateValueToJava2D(xEnd.doubleValue(), plotArea);

            markPeriod(xxStart, xxEnd, minY, maxY, g2);
        }

        g2.setComposite( originalComposite );
    }

    private void markPeriod(double xStart, double xEnd, double minY, double maxY, Graphics2D g2){
        g2.fill( new Rectangle2D.Double(xStart, minY, xEnd-xStart, maxY-minY) );
    }
}