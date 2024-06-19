/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: ImageTitle.java,v 1.1 2007/10/10 18:54:39 vauchers Exp $
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
 * A chart title that displays an image.  This is useful, for example, if you have an image
 * of your corporate logo and want to use as a footnote or part of a title in a chart you create.
 * <P>
 * ImageTitle needs an image passed to it in the constructor.  For ImageTitle to work, you must
 * have already loaded this image from its source (disk or URL).  It is recomended you use
 * something like Toolkit.getDefaultToolkit().getImage() to get the image.  Then, use MediaTracker
 * or some other message to make sure the image is fully loaded from disk.
 */
public class ImageTitle extends AbstractTitle {

    /** */
    protected Image titleImage;

    /** */
    protected int titleHeight;  // The height to draw this image as.  This may be scaled.

    /** */
    protected int titleWidth;   // The width to draw this image as.  This may be scaled

    /**
     * Create an ImageTitle with the given Image scaled to the given width and height in the given
     * location;
     * @param img  An Image to use as the image in this title;
     * @param width   An int of the width to make this title in pixels;
     * @param height  An int of the height to make this title in pixels;
     * @param locate  An int of the location for this title (Use constants in AbstractTitle);
     * @param align  An int of the alignment for this title (Use constants in AbstractTitle);
     */
    public ImageTitle(Image image, int width, int height, int position, int horizontalAlignment,
                      int verticalAlignment) {

        super(position, horizontalAlignment, verticalAlignment);
        titleImage = image;
        titleHeight = height;
        titleWidth = width;
    }

    /**
     * Create an ImageTitle with the given Image default scaled in the given location.
     * @param img  An Image to use as the image in this title;
     * @param locate  An int of the location for this title (Use constants in AbstractTitle);
     * @param align  An int of the alignment for this title (Use constants in AbstractTitle);
     */
    public ImageTitle(Image image, int position, int horizontalAlignment, int verticalAlignment) {
        super(position, horizontalAlignment, verticalAlignment);
        titleImage = image;
        titleHeight = image.getHeight(null);
        titleWidth = image.getWidth(null);
    }

    /**
     * Returns the current title image.
     * @return  An Image object of the font used to render this title;
     */
    public Image getTitleImage() {
        return this.titleImage;
    }

    /**
     * Sets the title font to the specified font and notifies registered listeners that the title
     * has been modified.
     * @param font  A Font object of the new font;
     */
    public void setTitleImage(Image image) {
        this.titleImage = image;
        notifyListeners(new TitleChangeEvent((AbstractTitle)this));
    }

    /**
     * Draws the title on a Java 2D graphics device (such as the screen or a printer).
     * @param g2 The graphics device;
     * @param chartArea The area within which the title (and plot) should be drawn.
     * @return The area used by the title.
     */
    public void draw(Graphics2D g2, Rectangle2D titleArea) {
        if (this.position == TOP || this.position == BOTTOM) {
            drawHorizontal(g2, titleArea);
        }
        else throw new RuntimeException("ImageTitle.draw(...) - invalid title position.");
    }

    /**
     * Returns true for all positions, since an image can be displayed anywhere.
     */
    public boolean isValidPosition(int position) {
        if (position==AbstractTitle.TOP) return true;
        else if (position==AbstractTitle.BOTTOM) return true;
        else if (position==AbstractTitle.RIGHT) return true;
        else if (position==AbstractTitle.LEFT) return true;
        else return false;
    }

    /**
     * Returns the preferred width of the title.
     */
    public double getPreferredWidth(Graphics2D g2) {
        return insets.left+titleWidth+insets.right;
    }

    /**
     * Returns the preferred height of the title.
     */
    public double getPreferredHeight(Graphics2D g2) {
        return insets.top+titleHeight+insets.bottom;
    }

    /**
     * Draws the title on a Java 2D graphics device (such as the screen or a printer).
     * @param g2 The graphics device;
     * @param chartArea The area within which the title (and plot) should be drawn;
     * @return The area used by the title;
     */
    protected Size2D drawHorizontal(Graphics2D g2, Rectangle2D chartArea) {
        double startY = 0.0;
        if (this.position == TOP) {
            startY =  chartArea.getY() + insets.top; // + titleHeight;
        }
        else {
            startY = chartArea.getY() + chartArea.getHeight() - insets.bottom - titleHeight;
        }

        // What is our alignment
        double startX = 0.0;
        if (this.horizontalAlignment == CENTER) {
            startX = chartArea.getX() + insets.left + chartArea.getWidth() / 2 - titleWidth / 2 ;
        }
        else if (this.horizontalAlignment == LEFT) {
            startX = chartArea.getX() + insets.left;
        }
        else if (this.horizontalAlignment == RIGHT) {
            startX = chartArea.getX() + chartArea.getWidth() - insets.right - titleWidth;
        }

        g2.drawImage(titleImage, (int)startX, (int)startY, titleWidth, titleHeight, null);

        return new Size2D(chartArea.getWidth()+insets.left+insets.right,
                          titleHeight+insets.top+insets.bottom);
    }

}
