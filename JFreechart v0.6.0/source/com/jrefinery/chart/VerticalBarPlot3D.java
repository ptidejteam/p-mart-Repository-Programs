/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: VerticalBarPlot3D.java,v 1.1 2007/10/10 18:53:20 vauchers Exp $
 *
 * Original Author:  Serge V. Grachov;
 * Contributor(s):   David Gilbert;
 *
 * (C) Copyright 2001 Serge V. Grachov and Simba Management Limited;
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
 * Change History:
 * ---------------
 * 31-Oct-2001 : Initial version contributed by Serge V. Grachov (DG);
 * 13-Nov-2001 : Constructor now throws PlotNotCompatibleException in some circumstances (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import com.jrefinery.data.*;
import com.jrefinery.chart.event.*;

/**
 * A general class for plotting vertical bars with a 3D effect, using data from any class that
 * implements the CategoryDataset interface.
 * <P>
 * This class now relies on a renderer to draw the individual bars, giving some flexibility to
 * change the visual representation of the data.
 * @see Plot
 * @see CategoryDataset
 * @see VerticalBarRenderer
 */
public class VerticalBarPlot3D extends VerticalBarPlot {

    /**
     * Standard constructor: returns a BarPlot with attributes specified by the caller.
     * @param horizontal The horizontal axis.
     * @param vertical The vertical axis.
     * @param introGap The gap before the first bar in the plot.
     * @param trailGap The gap after the last bar in the plot.
     * @param categoryGap The gap between the last bar in one category and the first bar in the next
     *                    category.
     * @param seriesGap The gap between bars within the same category.
     */
    public VerticalBarPlot3D(Axis horizontal, Axis vertical, Insets insets,
			     double introGap, double trailGap, double categoryGap, double seriesGap)
	throws AxisNotCompatibleException, PlotNotCompatibleException
    {

	super(horizontal, vertical, insets,
	      introGap, trailGap, categoryGap, seriesGap);

    }

    /**
     * Standard constructor - builds a VerticalBarPlot with mostly default attributes.
     * @param horizontalAxis The horizontal axis;
     * @param verticalAxis The vertical axis;
     */
    public VerticalBarPlot3D(Axis horizontalAxis, Axis verticalAxis)
        throws AxisNotCompatibleException, PlotNotCompatibleException
    {
	this(horizontalAxis, verticalAxis, new Insets(2,2,2,2), 0.1, 0.1, 0.2, 0.0);
    }

    /**
     * Checks the compatibility of a vertical axis, returning true if the axis is compatible with
     * the plot, and false otherwise.
     * @param axis The vertical axis;
     */
    public boolean isCompatibleVerticalAxis(Axis axis) {
	if (axis instanceof VerticalNumberAxis3D) {
	    return true;
	}
	else return false;
    }

    /**
     * Returns the shape of the background for the 3D-effect bar plot.
     */
    protected Shape calculateBackgroundPlotArea(Rectangle2D plotArea) {

	VerticalAxis vAxis = getVerticalAxis();
	double effect3d = ((VerticalNumberAxis3D) vAxis).getEffect3d();

        GeneralPath backgroundPlotArea = new GeneralPath();
        backgroundPlotArea.moveTo((float) plotArea.getX(), (float)plotArea.getY());
        backgroundPlotArea.lineTo((float)(plotArea.getX()+effect3d),
                                  (float)(plotArea.getY()-effect3d));
        backgroundPlotArea.lineTo((float)(plotArea.getX()+plotArea.getWidth()),
                                  (float)(plotArea.getY()-effect3d));
        backgroundPlotArea.lineTo((float)(plotArea.getX()+plotArea.getWidth()),
                                  (float)(plotArea.getY()+plotArea.getHeight()-effect3d));
        backgroundPlotArea.lineTo((float)(plotArea.getX()+plotArea.getWidth()-effect3d),
                                  (float)(plotArea.getY()+plotArea.getHeight()));
        backgroundPlotArea.lineTo((float) plotArea.getX(),
                                  (float)(plotArea.getY()+plotArea.getHeight()));
        backgroundPlotArea.lineTo((float) plotArea.getX(),
                                  (float) plotArea.getY());

        return backgroundPlotArea;

    }

    /**
     * Draws the bars...
     */
    protected void drawBars(Graphics2D g2, Shape backgroundPlotArea, Rectangle2D plotArea) {

        VerticalAxis vAxis = getVerticalAxis();
	double effect3d = ((VerticalNumberAxis3D) vAxis).getEffect3d();
        // draw far 3d axis
	if ((outlineStroke!=null) && (outlinePaint!=null)) {
	    g2.setStroke(outlineStroke);
	    g2.setPaint(outlinePaint);
	    g2.draw(new Line2D.Double(plotArea.getX()+effect3d, plotArea.getY()-effect3d,
                          plotArea.getX()+effect3d, plotArea.getY()+plotArea.getHeight()-effect3d));
	    g2.draw(new Line2D.Double(plotArea.getX(), plotArea.getY()+plotArea.getHeight(),
                          plotArea.getX()+effect3d, plotArea.getY()+plotArea.getHeight()-effect3d));
	    g2.draw(new Line2D.Double(plotArea.getX()+effect3d,
                                      plotArea.getY()+plotArea.getHeight()-effect3d,
                                      plotArea.getX()+plotArea.getWidth(),
                                      plotArea.getY()+plotArea.getHeight()-effect3d));
	}
	super.drawBars(g2, backgroundPlotArea, plotArea);

    }

    /**
     * Returns a short string describing the type of plot.
     */
    public String getPlotType() {
	return "Bar3d Plot";
    }

}
