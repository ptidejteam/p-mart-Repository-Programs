/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: CandleSticksPlot.java,v 1.1 2007/10/10 18:54:39 vauchers Exp $
 *
 * Original Author:  Sylvain Vieujot;
 * Contributor(s):   David Gilbert;
 *
 * (C) Copyright 2001 by Sylvain Vieujot;
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
 * Changes
 * -------
 * 27-Nov-2001 : Version 1, contributed by Sylvain Vieujot (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;
import java.text.DateFormat;
import java.util.*;
import javax.swing.JLabel;
import com.jrefinery.data.*;

/**
 * Candlesticks plot.
 */
public class CandleSticksPlot extends HighLowPlot {

    /**
     * Standard constructor: returns an CandleSticksPlot with attributes specified by the caller.
     * @param horizontal The horizontal axis;
     * @param vertical The vertical axis.
     */
    public CandleSticksPlot(Axis horizontal, Axis vertical) throws AxisNotCompatibleException,
                                                                   PlotNotCompatibleException {

          super(horizontal, vertical);

    }

    /**
     * Returns a list of HighLow elements that will fit inside the specified area.
     */
    private java.util.List getCandles(Rectangle2D plotArea, int serie) {

        ArrayList elements = new ArrayList();
        XYDataset data = getDataset();
        if (data == null)
            return elements;
        if( serie>= data.getSeriesCount() )
            return elements;

        int itemCount = data.getItemCount(serie);
        if (itemCount % 4 != 0)
            return elements;

        for(int itemIndex = 0; itemIndex < itemCount; itemIndex+=4){
            // Warning, here they is a change with the HiLow as the standard order it open, high,
            // low, close
            Number x = data.getXValue(serie, itemIndex);
            Number yOpen  = data.getYValue(serie,itemIndex);
            Number yHigh  = data.getYValue(serie,itemIndex+1);
            Number yLow   = data.getYValue(serie,itemIndex+2);
            Number yClose = data.getYValue(serie,itemIndex+3);

            double xx = getHorizontalValueAxis().translatedValue(x, plotArea);
            double yyHigh = getVerticalValueAxis().translatedValue(yHigh, plotArea);
            double yyLow = getVerticalValueAxis().translatedValue(yLow, plotArea);
            double yyOpen = getVerticalValueAxis().translatedValue(yOpen, plotArea);
            double yyClose = getVerticalValueAxis().translatedValue(yClose, plotArea);

            Paint p = this.getSeriesPaint(serie);
            Stroke s = this.getSeriesStroke(serie);

            DateFormat df = DateFormat.getDateInstance();
            String toolTipText =    df.format( x )+" => "+
                                    "open="+yOpen+" ; "+
                                    "high="+yHigh+" ; "+
                                    "low="+yLow+" ; "+
                                    "close="+yClose;
            elements.add( new Candle(xx,yyHigh,yyLow,yyOpen,yyClose,s,p, toolTipText) );
        }
        return elements;
    }

    private java.util.List getIndicators(Rectangle2D plotArea, int serie) {
        ArrayList elements = new ArrayList();
        XYDataset data = getDataset();
        if( data==null )
            return elements;
        if( serie>= data.getSeriesCount() )
            return elements;

        Point2D.Double prevPoint = null;
        int itemCount = data.getItemCount(serie);
        for(int itemIndex = 0; itemIndex < itemCount; itemIndex++){
            Number x = data.getXValue(serie, itemIndex);
            Number y = data.getYValue(serie, itemIndex);
            double xx = getHorizontalValueAxis().translatedValue(x, plotArea);
            double yy = getVerticalValueAxis().translatedValue(y, plotArea);
            Paint p = this.getSeriesPaint(serie);
            Stroke s = this.getSeriesStroke(serie);
            DateFormat df= DateFormat.getDateInstance();
            elements.add( new StdLineElement(prevPoint, xx, yy, s, p, df.format( x )+" : "+y) );
            prevPoint = new Point2D.Double(xx, yy);
        }
        return elements;
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     * @param g2 The graphics device;
     * @param drawArea The area within which the plot should be drawn.
     */
    public void draw(Graphics2D g2, Rectangle2D drawArea){

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

        XYDataset data = getDataset();
        if( data!= null ){
            int seriesCount = data.getSeriesCount();

            JFreeChartPanel jp = ((JFreeChartPanel) (this.getChart().listeners.get(1)));
            jp.removeAll();

            for(int serie=0; serie<seriesCount; serie++){
                if( serie==0 ){ // Candle sticks
                    java.util.List candles = getCandles(plotArea, serie);   // area should be remaining area only
                    for(int i=0; i<candles.size(); i++){
                        Candle c = (Candle)candles.get(i);
                        g2.setPaint( c.getPaint() );
                        g2.setStroke( c.getStroke() );
                        c.draw(jp, g2);
                    }
                }else{
                    java.util.List lines = getIndicators(plotArea, serie);   // area should be remaining area only
                    for (int i=0; i<lines.size(); i++){
                        StdLineElement l = (StdLineElement)lines.get(i);
                        g2.setPaint(l.getPaint());
                        g2.setStroke(l.getStroke());
                        l.draw(jp, g2);
                    }
                }
            }
        }

        g2.setClip(originalClip);
    }

    /**
     * Returns the plot type as a string. This implementation returns "HiLow Plot".
     */
    public String getPlotType() {
          return "CandelSticks Plot";
    }

    //
    // Private Candle class
    //

    private class Candle {

        private double bodyHalfSize = 2;
        private Stroke stroke;
        private Paint paint;
        private ArrayList lines;
        private Rectangle2D darkBody;
        private JLabel toolTip;

        public Candle(double x, double high, double low,
                      double open, double close,
                      Stroke stroke, Paint paint, String toolTipText) {
            this.stroke = stroke;
            this.paint = paint;

            lines = new ArrayList();
            if( high<open && high<close ) // upperShadow
                lines.add( new Line2D.Double(x, high, x, Math.min(open, close)) );
            if( low>open && low>close ) // lowerShadow
                lines.add( new Line2D.Double(x, low, x, Math.max(open, close)) );
            // Body
            if( open<close )
                darkBody = new Rectangle2D.Double(x-bodyHalfSize, open, 2*bodyHalfSize, close-open);
            else{
                darkBody = null;
                lines.add( new Line2D.Double(x-bodyHalfSize, open, x+bodyHalfSize, open) );
                lines.add( new Line2D.Double(x+bodyHalfSize, open, x+bodyHalfSize, close) );
                lines.add( new Line2D.Double(x-bodyHalfSize, close, x+bodyHalfSize, close) );
                lines.add( new Line2D.Double(x-bodyHalfSize, open, x-bodyHalfSize, close) );
            }

            toolTip = new JLabel();
            toolTip.setToolTipText( toolTipText );
            toolTip.setBounds((int)(x-bodyHalfSize), (int)(high-bodyHalfSize), (int)(2*bodyHalfSize), (int)(low-high+2*bodyHalfSize));
        }

        public void draw(JFreeChartPanel jp, Graphics2D g2){
            if( darkBody!=null )
                  g2.fill( darkBody );

            int nbrLines = lines.size();
            for(int j=0 ; j<nbrLines ; j++)
                g2.draw( (Shape)lines.get(j) );

            jp.add( toolTip );
        }

        /**
         * Returns the Stroke object used to draw the line.
         */
        public Stroke getStroke() {
            return stroke;
        }

        /**
         * Returns the Paint object used to color the line.
         */
        public Paint getPaint() {
            return paint;
        }

    }


    private class StdLineElement {

        private double pointRadius = 2;
        private Stroke stroke;
        private Paint paint;
        private Line2D line;
        private Rectangle2D point;
        private JLabel toolTip;

        public StdLineElement(Point2D prevPoint, double x, double y, Stroke stroke, Paint paint, String toolTipText) {
            this.stroke = stroke;
            this.paint = paint;

            if( prevPoint==null )
                line = null;
            else
                line = new Line2D.Double(prevPoint.getX(), prevPoint.getY(), x, y);
            point = new Rectangle2D.Double(x-pointRadius, y-pointRadius, 2*pointRadius, 2*pointRadius);

            toolTip = new JLabel();
            toolTip.setToolTipText( toolTipText );
            toolTip.setBounds((int)(x-pointRadius), (int)(y-pointRadius), (int)(2*pointRadius), (int)(2*pointRadius));
        }

        public void draw(JFreeChartPanel jp, Graphics2D g2){
            if( line!=null )
                g2.draw( line );

            g2.fill( point );
            jp.add( toolTip );
        }

        /**
         * Returns the Stroke object used to draw the line.
         */
        public Stroke getStroke() {
            return stroke;
        }

        /**
         * Returns the Paint object used to color the line.
         */
        public Paint getPaint() {
            return paint;
        }
    }
}