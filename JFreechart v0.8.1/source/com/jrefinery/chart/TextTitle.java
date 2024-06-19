/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
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
 * --------------
 * TextTitle.java
 * --------------
 * (C) Copyright 2000-2002, by David Berry and Contributors.
 *
 * Original Author:  David Berry;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: TextTitle.java,v 1.1 2007/10/10 19:02:36 vauchers Exp $
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header (DG);
 * 07-Nov-2001 : Separated the JCommon Class Library classes, JFreeChart now requires
 *               jcommon.jar (DG);
 * 09-Jan-2002 : Updated Javadoc comments (DG);
 * 07-Feb-2002 : Changed Insets --> Spacer in AbstractTitle.java (DG);
 * 06-Mar-2002 : Updated import statements (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Color;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import com.jrefinery.chart.event.TitleChangeEvent;
import com.jrefinery.ui.Size2D;

/**
 * A standard chart title.
 */
public class TextTitle extends AbstractTitle {

    /** The default font. */
    public static final Font DEFAULT_FONT = new Font("Dialog", Font.BOLD, 12);

    /** The default text color. */
    public static final Paint DEFAULT_TEXT_PAINT = Color.black;

    /** The title text. */
    protected String text;

    /** The font used to display the title. */
    protected Font font;

    /** The paint used to display the title text. */
    protected Paint paint;

    /**
     * Constructs a new TextTitle, using default attributes where necessary.
     * @param text The title text.
     */
    public TextTitle(String text) {

        this(text,
             TextTitle.DEFAULT_FONT,
             TextTitle.DEFAULT_TEXT_PAINT,
             AbstractTitle.DEFAULT_POSITION,
             AbstractTitle.DEFAULT_HORIZONTAL_ALIGNMENT,
             AbstractTitle.DEFAULT_VERTICAL_ALIGNMENT,
             AbstractTitle.DEFAULT_SPACER);

    }

    /**
     * Constructs a new TextTitle, using default attributes where necessary.
     * @param text The title text.
     * @param font The title font.
     */
    public TextTitle(String text, Font font) {

        this(text, font,
             TextTitle.DEFAULT_TEXT_PAINT,
             AbstractTitle.DEFAULT_POSITION,
             AbstractTitle.DEFAULT_HORIZONTAL_ALIGNMENT,
             AbstractTitle.DEFAULT_VERTICAL_ALIGNMENT,
             AbstractTitle.DEFAULT_SPACER);

    }

    /**
     * Constructs a new TextTitle, using default attributes where necessary.
     * @param text The title text.
     * @param font The title font.
     * @param paint The title color.
     */
    public TextTitle(String text, Font font, Paint paint) {

        this(text, font, paint,
             AbstractTitle.DEFAULT_POSITION,
             AbstractTitle.DEFAULT_HORIZONTAL_ALIGNMENT,
             AbstractTitle.DEFAULT_VERTICAL_ALIGNMENT,
             AbstractTitle.DEFAULT_SPACER);

    }
    /**
     * Constructs a new TextTitle, using default attributes where necessary.
     * @param text The title text.
     * @param font The title font.
     * @param horizontalAlignment The horizontal alignment (use the constants defined in
     *                            AbstractTitle).
     */
    public TextTitle(String text, Font font, int horizontalAlignment) {

        this(text, font,
             TextTitle.DEFAULT_TEXT_PAINT,
             AbstractTitle.DEFAULT_POSITION,
             horizontalAlignment,
             AbstractTitle.DEFAULT_VERTICAL_ALIGNMENT,
             AbstractTitle.DEFAULT_SPACER);

    }

    /**
     * Constructs a TextTitle with the specified properties.
     * @param text The text for the title.
     * @param font The title font.
     * @param paint The title color.
     * @param position The title position (use the constants defined in AbstractTitle).
     * @param horizontalAlignment The horizontal alignment (use the constants defined in
     *                            AbstractTitle).
     * @param verticalAlignment The vertical alignment (use the constants defined in AbstractTitle).
     * @param spacer The space to leave around the outside of the title.
     */
    public TextTitle(String text,
                     Font font, Paint paint,
                     int position, int horizontalAlignment, int verticalAlignment,
                     Spacer spacer) {

        super(position, horizontalAlignment, verticalAlignment, spacer);
        this.text = text;
        this.font = font;
        this.paint = paint;

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

        // get the title width...
        g2.setFont(font);
        FontRenderContext frc = g2.getFontRenderContext();
        Rectangle2D titleBounds = font.getStringBounds(text, frc);
        double result = titleBounds.getWidth();

        // add extra space...
        if (this.spacer!=null) {
            result = spacer.getAdjustedWidth(result);
        }

        return result;

    }

    /**
     * Returns the preferred height of the title.
     */
    public double getPreferredHeight(Graphics2D g2) {

        // get the title height...
        g2.setFont(font);
        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics lineMetrics = font.getLineMetrics(text, frc);
        double result = lineMetrics.getHeight();

        // add extra space...
        if (this.spacer!=null) {
            result = spacer.getAdjustedHeight(result);
        }

        return result;

    }

    /**
     * Draws the title on a Java 2D graphics device (such as the screen or a printer).
     * @param g2 The graphics device.
     * @param area The area within which the title (and plot) should be drawn.
     */
    public void draw(Graphics2D g2, Rectangle2D area) {

        if (this.position == TOP || this.position == BOTTOM) {
            drawHorizontal(g2, area);
        }
        else throw new RuntimeException("TextTitle.draw(...) - invalid title position.");

    }

    /**
     * Draws the title on a Java 2D graphics device (such as the screen or a printer).
     * @param g2 The graphics device.
     * @param area The area within which the title should be drawn.
     */
    protected void drawHorizontal(Graphics2D g2, Rectangle2D area) {

        FontRenderContext frc = g2.getFontRenderContext();
        Rectangle2D titleBounds = font.getStringBounds(text, frc);
        LineMetrics lineMetrics = font.getLineMetrics(text, frc);

        double titleWidth = titleBounds.getWidth();
        double leftSpace = 0.0;
        double rightSpace = 0.0;
        double titleHeight = lineMetrics.getHeight();
        double topSpace = 0.0;
        double bottomSpace = 0.0;

        if (spacer!=null) {
            leftSpace = spacer.getLeftSpace(titleWidth);
            rightSpace = spacer.getRightSpace(titleWidth);
            topSpace = spacer.getTopSpace(titleHeight);
            bottomSpace = spacer.getBottomSpace(titleHeight);
        }

        double titleY = area.getY()+topSpace;

        // work out the vertical alignment...
        if (this.verticalAlignment==TOP) {
            titleY = titleY+titleHeight-lineMetrics.getLeading()-lineMetrics.getDescent();
        }
        else if (this.verticalAlignment==MIDDLE) {
            double space = (area.getHeight()-topSpace-bottomSpace-titleHeight);
            titleY = titleY+(space/2)+titleHeight-lineMetrics.getLeading()-lineMetrics.getDescent();
        }
        else if (this.verticalAlignment==BOTTOM) {
            titleY = area.getMaxY()-bottomSpace
                                   -lineMetrics.getLeading()
                                   -lineMetrics.getDescent();
        }

        // work out the horizontal alignment...
        double titleX = area.getX()+leftSpace;
        if (this.horizontalAlignment==CENTER) {
            titleX = titleX+((area.getWidth()-leftSpace-rightSpace)/2)-(titleWidth/2);
        }
        else if (this.horizontalAlignment==LEFT) {
            titleX = area.getX()+leftSpace;
        }
        else if (this.horizontalAlignment == RIGHT) {
            titleX = area.getMaxX()-rightSpace-titleWidth;
        }

        g2.setFont(this.font);
        g2.setPaint(this.paint);
        g2.drawString(text, (float)(titleX), (float)(titleY));

    }

}