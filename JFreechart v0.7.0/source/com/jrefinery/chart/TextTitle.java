/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: TextTitle.java,v 1.1 2007/10/10 18:54:39 vauchers Exp $
 *
 * Original Author:  David Berry;
 * Contributor(s):   David Gilbert;
 *
 * (C) Copyright 2000, 2001 David Berry;
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
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header (DG);
 * 07-Nov-2001 : Separated the JCommon Class Library classes, JFreeChart now requires
 *               jcommon.jar (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;
import com.jrefinery.chart.event.*;
import com.jrefinery.ui.Size2D;

/**
 * A standard chart title.
 */
public class TextTitle extends AbstractTitle {

    /** The title text; */
    protected String text;

    /** The font used to display the title; */
    protected Font font;

    /** The paint used to display the title text; */
    protected Paint paint;

    /** Full constructor - builds a TextTitle with the specified properties. */
    public TextTitle(String text, Font font, Paint paint, int position, int horizontalAlignment,
                     int verticalAlignment, Insets insets) {
        super(position, horizontalAlignment, verticalAlignment, insets);
        this.text = text;
        this.font = font;
        this.paint = paint;
    }

    /**
     * Default constructor - builds a TextTitle with some default attributes.
     */
    public TextTitle(String text, Font font, Paint paint) {
        this(text, font, paint, TOP, CENTER, MIDDLE, new Insets(2, 2, 2, 2));
    }

    /**
     * Default constructor - builds a TextTitle with some default attributes.
     */
    public TextTitle(String text, Font font) {
        this(text, font, Color.black);
    }

    public TextTitle(String text, Font font, int horizontalAlignment) {
        this(text, font, Color.black, TOP, horizontalAlignment, MIDDLE, new Insets(0, 0, 0, 0));
    }

    /**
     * Default constructor - builds a TextTitle with some default attributes.
     */
    public TextTitle(String text) {
        this(text, new Font("Dialog", Font.PLAIN, 12));
    }

    /**
     * Returns the current title font.
     * @return  A Font object of the font used to render this title;
     */
    public Font getFont() {
        return this.font;
    }

    /**
     * Sets the title font to the specified font and notifies registered listeners that the title
     * has been modified.
     * @param font  A Font object of the new font;
     */
    public void setFont(Font font) {
        if (!this.font.equals(font)) {
            this.font = font;
            notifyListeners(new TitleChangeEvent(this));
        }
    }

    /**
     * Returns the paint used to display the title.
     * @return  An object that implements the Paint interface used to paint this title;
     */
    public Paint getPaint() {
        return this.paint;
    }

    /**
     * Sets the Paint used to display the title and notifies registered listeners that the title has
     * been modified.
     * @param paint The new paint for displaying the chart title;
     */
    public void setPaint(Paint paint) {
        if (!this.paint.equals(paint)) {
            this.paint = paint;
            notifyListeners(new TitleChangeEvent(this));
        }
    }

    /**
     * Returns the title text.
     * @return A String of the title text;
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the title to the specified text. This method notifies registered listeners that the
     * title has been modified.
     * @param text A String of the new chart title;
     */
    public void setText(String text) {
        if (!this.text.equals(text)) {
            this.text = text;
            notifyListeners(new TitleChangeEvent(this));
        }
    }

    /**
     * Returns true for the positions that are valid for TextTitle (TOP and BOTTOM for now) and
     * false for all other positions.
     */
    public boolean isValidPosition(int position) {
        if ((position==AbstractTitle.TOP) || (position==AbstractTitle.BOTTOM)) return true;
        else return false;
    }

    /**
     * Returns the preferred width of the title.
     */
    public double getPreferredWidth(Graphics2D g2) {
        g2.setFont(font);
        FontRenderContext frc = g2.getFontRenderContext();
        Rectangle2D titleBounds = font.getStringBounds(text, frc);
        double titleWidth = insets.left+titleBounds.getWidth()+insets.right;
        return titleWidth;
    }

    /**
     * Returns the preferred height of the title.
     */
    public double getPreferredHeight(Graphics2D g2) {
        g2.setFont(font);
        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics lineMetrics = font.getLineMetrics(text, frc);
        double titleHeight = insets.top+lineMetrics.getHeight()+insets.bottom;
        return titleHeight;
    }

    /**
     * Draws the title on a Java 2D graphics device (such as the screen or a printer).
     * @param g2 The graphics device;
     * @param chartArea The area within which the title (and plot) should be drawn;
     * @return The area used by the title;
     */
    public void draw(Graphics2D g2, Rectangle2D titleArea) {
        if (this.position == TOP || this.position == BOTTOM) {
            drawHorizontal(g2, titleArea);
        }
        else throw new RuntimeException("TextTitle.draw(...) - invalid title position.");
    }

    /**
     * Draws the title on a Java 2D graphics device (such as the screen or a printer).
     * @param g2 The graphics device;
     * @param chartArea The area within which the title (and plot) should be drawn;
     */
    protected void drawHorizontal(Graphics2D g2, Rectangle2D titleArea) {

        g2.setFont(this.font);
        g2.setPaint(this.paint);

        FontRenderContext frc = g2.getFontRenderContext();
        Rectangle2D titleBounds = font.getStringBounds(text, frc);
        LineMetrics lineMetrics = font.getLineMetrics(text, frc);

        double titleWidth = titleBounds.getWidth();
        double titleHeight = lineMetrics.getHeight();

        double titleY = titleArea.getY()+insets.top;

        // work out the vertical alignment...
        if (this.verticalAlignment==TOP) {
            titleY = titleY+titleHeight-lineMetrics.getLeading()-lineMetrics.getDescent();
        }
        else if (this.verticalAlignment==MIDDLE) {
            double space = (titleArea.getHeight()-insets.top-insets.bottom-titleHeight);
            titleY = titleY+(space/2)+titleHeight-lineMetrics.getLeading()-lineMetrics.getDescent();
        }
        else if (this.verticalAlignment==BOTTOM) {
            titleY = titleArea.getMaxY()-insets.bottom-lineMetrics.getLeading()-lineMetrics.getDescent();
        }

        // work out the horizontal alignment...
        double titleX = titleArea.getX()+insets.left;
        if (this.horizontalAlignment==CENTER) {
            titleX = titleX+((titleArea.getWidth()-insets.left-insets.right)/2)-(titleWidth/2);
        }
        else if (this.horizontalAlignment==LEFT) {
            titleX = titleArea.getX()+insets.left;
        }
        else if (this.horizontalAlignment == RIGHT) {
            titleX = titleArea.getMaxX()-insets.right-titleWidth;
        }

        g2.drawString(text, (float)(titleX), (float)(titleY));

    }

}
