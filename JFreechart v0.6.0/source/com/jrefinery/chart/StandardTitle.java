/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: StandardTitle.java,v 1.1 2007/10/10 18:53:20 vauchers Exp $
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 * (C) Copyright 2000, 2001 Simba Management Limited;
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
 * Changes (from 22-Jun-2001)
 * --------------------------
 * 22-Jun-2001 : This file made obsolete by new title code (DG);
 * 18-Sep-2001 : Updated e-mail address and fixed DOS encoding problem (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;
import com.jrefinery.chart.event.*;

/**
 * A chart title displays a description for a chart.  It should be fairly easy to extend this
 * class to display a subtitle or an image.
 */
public class StandardTitle extends Title {

    /** The Font for displaying the chart title. */
    protected Font titleFont;

    /** The Paint used to draw the chart title. */
    protected Paint titlePaint;

    /** The amount of blank space around the title. */
    protected Insets insets;

    /**
     * Full constructor: returns a StandardTitle object with attributes as specified by the caller.
     * @param title Text of the chart title;
     * @param font Font for displaying the chart title;
     * @param paint Paint for displaying the chart title.
     */
    public StandardTitle(String title, Font font, Paint paint, Insets insets) {
	super(title);
	this.titleFont = font;
	this.titlePaint = paint;
	this.insets = insets;
    }

    /**
     * Standard constructor: returns a StandardTitle object with some default attributes.
     * @param title Text of the chart title;
     * @param font Font for displaying the chart title.
     */
    public StandardTitle(String title, Font font) {
	this(title, font, Color.black, new Insets(2, 2, 2, 2));
    }

    /**
     * Returns the current title font.
     */
    public Font getTitleFont() {
	return this.titleFont;
    }

    /**
     * Sets the title font to the specified font and notifies registered listeners that the title has
     * been modified.
     * @param font The new font;
     */
    public void setTitleFont(Font font) {
	this.titleFont = font;
	//notifyListeners(new TitleChangeEvent(this));
    }

    /**
     * Returns the paint used to display the title.
     */
    public Paint getTitlePaint() {
	return this.titlePaint;
    }

    /**
     * Sets the Paint used to display the title and notifies registered listeners that the title has
     * been modified.
     * @param paint The new paint for displaying the chart title;
     */
    public void setTitlePaint(Paint paint) {
	this.titlePaint = paint;
	//notifyListeners(new TitleChangeEvent(this));
    }

    /**
     * Draws the title on a Java 2D graphics device (such as the screen or a printer).
     * @param g2 The graphics device;
     * @param chartArea The area within which the title (and plot) should be drawn;
     * @return The area used by the title.
     */
    public Rectangle2D draw(Graphics2D g2, Rectangle2D chartArea) {

	g2.setFont(titleFont);
	g2.setPaint(titlePaint);
	FontRenderContext frc = g2.getFontRenderContext();
	Rectangle2D titleBounds = titleFont.getStringBounds(title, frc);
	LineMetrics lineMetrics = titleFont.getLineMetrics(title, frc);

	double titleWidth = titleBounds.getWidth();
	double titleHeight = lineMetrics.getHeight();
	double baseLine = chartArea.getY()+insets.top
	    +titleHeight-lineMetrics.getLeading()-lineMetrics.getDescent();

	double centerX = chartArea.getX()+insets.left+chartArea.getWidth()/2;
	g2.drawString(title, (float)(centerX-titleWidth/2),
		      (float)(baseLine));
	return new Rectangle2D.Double(chartArea.getX(), chartArea.getY(),
				      chartArea.getWidth()+insets.left+insets.right,
				      titleHeight+insets.top+insets.bottom);
    }

}
