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
 * ---------------
 * ImageTitle.java
 * ---------------
 * (C) Copyright 2000-2002, by David Berry and Contributors;
 *
 * Original Author:  David Berry;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: ImageTitle.java,v 1.1 2007/10/10 19:41:59 vauchers Exp $
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header (DG);
 * 07-Nov-2001 : Separated the JCommon Class Library classes, JFreeChart now requires
 *               jcommon.jar (DG);
 * 09-Jan-2002 : Updated Javadoc comments (DG);
 * 07-Feb-2002 : Changed blank space around title from Insets --> Spacer, to allow for relative
 *               or absolute spacing (DG);
 * 25-Jun-2002 : Updated import statements (DG);
 *
 */

package com.jrefinery.chart;

import com.jrefinery.chart.event.TitleChangeEvent;
import com.jrefinery.ui.Size2D;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;

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

    /** The title image. */
    protected Image image;

    /** The height used to draw the image (may involve scaling). */
    protected int height;

    /** The width used to draw the image (may involve scaling). */
    protected int width;

    /**
     * Constructs a new ImageTitle.
     * @param image  The image.
     * @param position  The title position (LEFT, RIGHT, TOP, BOTTOM).
     */
    public ImageTitle(Image image) {

        this(image,
             image.getHeight(null), image.getWidth(null),
             AbstractTitle.DEFAULT_POSITION,
             AbstractTitle.DEFAULT_HORIZONTAL_ALIGNMENT,
             AbstractTitle.DEFAULT_VERTICAL_ALIGNMENT,
             AbstractTitle.DEFAULT_SPACER);

    }

    /**
     * Constructs a new ImageTitle.
     * @param image  The image.
     * @param position  The title position (LEFT, RIGHT, TOP, BOTTOM).
     */
    public ImageTitle(Image image, int position, int horizontalAlignment, int verticalAlignment) {

        this(image, position, image.getHeight(null), image.getWidth(null),
             horizontalAlignment, verticalAlignment,
             AbstractTitle.DEFAULT_SPACER);

    }

    /**
     * Constructs a new ImageTitle with the given image scaled to the given width and height
     * in the given location.
     * @param image  The image.
     * @param width   The width used to draw the image.
     * @param height  The height used to draw the image.
     * @param position  The title position (LEFT, RIGHT, TOP, BOTTOM).
     */
    public ImageTitle(Image image, int height, int width,
                      int position, int horizontalAlignment, int verticalAlignment,
                      Spacer spacer) {

        super(position, horizontalAlignment, verticalAlignment, spacer);
        this.image = image;
        this.height = height;
        this.width = width;

    }

    /**
     * Returns the image for the title.
     * @return  The image for the title.
     */
    public Image getImage() {
        return this.image;
    }

    /**
     * Sets the image for the title and notifies registered listeners that the title
     * has been modified.
     * @param image  The new image.
     */
    public void setImage(Image image) {

        this.image = image;
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
     * @param g2 The graphics device.
     */
    public double getPreferredWidth(Graphics2D g2) {

        double result = this.width;

        if (this.spacer!=null) {
            result = spacer.getAdjustedWidth(result);
        }

        return result;

    }

    /**
     * Returns the preferred height of the title.
     * @param g2 The graphics device.
     */
    public double getPreferredHeight(Graphics2D g2) {

        double result = this.height;

        if (this.spacer!=null) {
            result = spacer.getAdjustedHeight(result);
        }

        return result;

    }

    /**
     * Draws the title on a Java 2D graphics device (such as the screen or a printer).
     * @param g2 The graphics device.
     * @param chartArea The area within which the title (and plot) should be drawn.
     * @return The area used by the title.
     */
    protected Size2D drawHorizontal(Graphics2D g2, Rectangle2D chartArea) {

        double startY = 0.0;
        double topSpace = 0.0;
        double bottomSpace = 0.0;
        double leftSpace = 0.0;
        double rightSpace = 0.0;

        if (this.spacer!=null) {
            topSpace = spacer.getTopSpace(this.height);
            bottomSpace = spacer.getBottomSpace(this.height);
            leftSpace = spacer.getLeftSpace(this.width);
            rightSpace = spacer.getRightSpace(this.width);
        }

        if (this.position == TOP) {
            startY =  chartArea.getY() + topSpace;
        }
        else {
            startY = chartArea.getY() + chartArea.getHeight() - bottomSpace - this.height;
        }

        // What is our alignment
        double startX = 0.0;
        if (this.horizontalAlignment == CENTER) {
            startX = chartArea.getX() + leftSpace + chartArea.getWidth() / 2 - this.width / 2 ;
        }
        else if (this.horizontalAlignment == LEFT) {
            startX = chartArea.getX() + leftSpace;
        }
        else if (this.horizontalAlignment == RIGHT) {
            startX = chartArea.getX() + chartArea.getWidth() - rightSpace - this.width;
        }

        g2.drawImage(image, (int)startX, (int)startY, this.width, this.height, null);

        return new Size2D(chartArea.getWidth()+leftSpace+rightSpace,
                          this.height+topSpace+bottomSpace);
    }

}
