/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ---------------------------
 * CategoryLabelPositions.java
 * ---------------------------
 * (C) Copyright 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: CategoryLabelPositions.java,v 1.1 2007/10/10 19:25:36 vauchers Exp $
 *
 * Changes
 * -------
 * 06-Jan-2004 : Version 1 (DG);
 *
 */

package org.jfree.chart.axis;

import java.io.Serializable;

import org.jfree.text.TextBlockAnchor;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

/**
 * Records the label positions for a category axis.
 */
public class CategoryLabelPositions implements Serializable {

    /** STANDARD category label positions. */
    public static CategoryLabelPositions STANDARD = new CategoryLabelPositions(
        new CategoryLabelPosition( // TOP
            RectangleAnchor.BOTTOM, TextBlockAnchor.BOTTOM_CENTER, TextAnchor.CENTER, 0.0
        ),
        new CategoryLabelPosition( // BOTTOM
            RectangleAnchor.TOP, TextBlockAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0
        ),
        new CategoryLabelPosition( // LEFT
            RectangleAnchor.RIGHT, TextBlockAnchor.CENTER_RIGHT, TextAnchor.CENTER, 0.0
        ),
        new CategoryLabelPosition( // RIGHT
            RectangleAnchor.LEFT, TextBlockAnchor.CENTER_LEFT, TextAnchor.CENTER, 0.0
        )
    );
    
    /** UP_90 category label positions. */
    public static CategoryLabelPositions UP_90 = new CategoryLabelPositions(
        new CategoryLabelPosition( // TOP
            RectangleAnchor.BOTTOM, TextBlockAnchor.CENTER_LEFT, 
            TextAnchor.CENTER_LEFT, -Math.PI / 2.0
        ),
        new CategoryLabelPosition( // BOTTOM
            RectangleAnchor.TOP, TextBlockAnchor.CENTER_RIGHT, 
            TextAnchor.CENTER_RIGHT, -Math.PI / 2.0
        ),
        new CategoryLabelPosition( // LEFT
            RectangleAnchor.RIGHT, TextBlockAnchor.BOTTOM_CENTER, 
            TextAnchor.BOTTOM_CENTER, -Math.PI / 2.0
        ),
        new CategoryLabelPosition( // RIGHT
            RectangleAnchor.LEFT, TextBlockAnchor.TOP_CENTER, 
            TextAnchor.TOP_CENTER, -Math.PI / 2.0
        )
    );
    
    /** DOWN_90 category label positions. */
    public static CategoryLabelPositions DOWN_90 = new CategoryLabelPositions(
        new CategoryLabelPosition( // TOP
            RectangleAnchor.BOTTOM, TextBlockAnchor.CENTER_RIGHT, 
            TextAnchor.CENTER_RIGHT, Math.PI / 2.0
        ),
        new CategoryLabelPosition( // BOTTOM
            RectangleAnchor.TOP, TextBlockAnchor.CENTER_LEFT, 
            TextAnchor.CENTER_LEFT, Math.PI / 2.0
        ),
        new CategoryLabelPosition( // LEFT
            RectangleAnchor.RIGHT, TextBlockAnchor.TOP_CENTER, 
            TextAnchor.TOP_CENTER, Math.PI / 2.0
        ),
        new CategoryLabelPosition( // RIGHT
            RectangleAnchor.LEFT, TextBlockAnchor.BOTTOM_CENTER, 
            TextAnchor.BOTTOM_CENTER, Math.PI / 2.0
        )
    );
    
    /** UP_45 category label positions. */
    public static CategoryLabelPositions UP_45 = createUpRotationLabelPositions(Math.PI / 4.0);
    
    /** DOWN_45 category label positions. */
    public static CategoryLabelPositions DOWN_45 = createDownRotationLabelPositions(Math.PI / 4.0);
    
    /**
     * Creates a new instance where the category labels angled upwards by the specified amount.
     * 
     * @param angle  the rotation angle (should be < Math.PI / 2.0).
     * 
     * @return A category label position specification.
     */
    public static CategoryLabelPositions createUpRotationLabelPositions(double angle) {
        return new CategoryLabelPositions(
            new CategoryLabelPosition( // TOP
                RectangleAnchor.BOTTOM, TextBlockAnchor.BOTTOM_LEFT, TextAnchor.BOTTOM_LEFT, -angle
            ),
            new CategoryLabelPosition( // BOTTOM
                RectangleAnchor.TOP, TextBlockAnchor.TOP_RIGHT, TextAnchor.TOP_RIGHT, -angle
            ),
            new CategoryLabelPosition( // LEFT
                RectangleAnchor.RIGHT, TextBlockAnchor.BOTTOM_RIGHT, TextAnchor.BOTTOM_RIGHT, -angle
            ),
            new CategoryLabelPosition( // RIGHT
                RectangleAnchor.LEFT, TextBlockAnchor.TOP_LEFT, TextAnchor.TOP_LEFT, -angle
            )
        );
    }
    
    /**
     * Creates a new instance where the category labels angled downwards by the specified amount.
     * 
     * @param angle  the rotation angle (should be < Math.PI / 2.0).
     * 
     * @return A category label position specification.
     */
    public static CategoryLabelPositions createDownRotationLabelPositions(double angle) {
        return new CategoryLabelPositions(
            new CategoryLabelPosition( // TOP
                RectangleAnchor.BOTTOM, TextBlockAnchor.BOTTOM_RIGHT, TextAnchor.BOTTOM_RIGHT, angle
            ),
            new CategoryLabelPosition( // BOTTOM
                RectangleAnchor.TOP, TextBlockAnchor.TOP_LEFT, TextAnchor.TOP_LEFT, angle
            ),
            new CategoryLabelPosition( // LEFT
                RectangleAnchor.RIGHT, TextBlockAnchor.TOP_RIGHT, TextAnchor.TOP_RIGHT, angle
            ),
            new CategoryLabelPosition( // RIGHT
                RectangleAnchor.LEFT, TextBlockAnchor.BOTTOM_LEFT, TextAnchor.BOTTOM_LEFT, angle
            )
        );
    }
    
    /** The label positioning details used when an axis is at the top of a chart. */
    private CategoryLabelPosition positionForAxisAtTop;
    
    /** The label positioning details used when an axis is at the bottom of a chart. */
    private CategoryLabelPosition positionForAxisAtBottom;
    
    /** The label positioning details used when an axis is at the left of a chart. */
    private CategoryLabelPosition positionForAxisAtLeft;
    
    /** The label positioning details used when an axis is at the right of a chart. */
    private CategoryLabelPosition positionForAxisAtRight;
 
    /**
     * Default constructor.
     */
    public CategoryLabelPositions() {
        this.positionForAxisAtTop = new CategoryLabelPosition();
        this.positionForAxisAtBottom = new CategoryLabelPosition();
        this.positionForAxisAtLeft = new CategoryLabelPosition();
        this.positionForAxisAtRight = new CategoryLabelPosition();
    }
    
    /**
     * Creates a new position specification.
     * 
     * @param positionForAxisAtTop  the label position info used when an axis is at the top;
     * @param positionForAxisAtBottom  the label position info used when an axis is at the bottom;
     * @param positionForAxisAtLeft  the label position info used when an axis is at the left;
     * @param positionForAxisAtRight  the label position info used when an axis is at the right;
     */
    public CategoryLabelPositions(CategoryLabelPosition positionForAxisAtTop,
                                  CategoryLabelPosition positionForAxisAtBottom,
                                  CategoryLabelPosition positionForAxisAtLeft,
                                  CategoryLabelPosition positionForAxisAtRight) {
        this.positionForAxisAtTop = positionForAxisAtTop;
        this.positionForAxisAtBottom = positionForAxisAtBottom;
        this.positionForAxisAtLeft = positionForAxisAtLeft;
        this.positionForAxisAtRight = positionForAxisAtRight;
    }
    
    /**
     * Returns the category label position specification for an axis at the given location.
     * 
     * @param edge  the axis location.
     * 
     * @return The category label position specification.
     */
    public CategoryLabelPosition getLabelPosition(RectangleEdge edge) {
        CategoryLabelPosition result = null;
        if (edge == RectangleEdge.TOP) {
            result = this.positionForAxisAtTop;
        }
        else if (edge == RectangleEdge.BOTTOM) {
            result = this.positionForAxisAtBottom;
        }
        else if (edge == RectangleEdge.LEFT) {
            result = this.positionForAxisAtLeft;
        }
        else if (edge == RectangleEdge.RIGHT) {
            result = this.positionForAxisAtRight;
        }
        return result;
    }
    
    /**
     * Returns a new instance based on an existing instance but with the top position changed.
     * 
     * @param base  the base.
     * @param top  the top position.
     * 
     * @return A new instance.
     */
    public static CategoryLabelPositions replaceTopPosition(CategoryLabelPositions base,
                                                            CategoryLabelPosition top) {
        return new CategoryLabelPositions(
            top, 
            base.getLabelPosition(RectangleEdge.BOTTOM),
            base.getLabelPosition(RectangleEdge.LEFT),
            base.getLabelPosition(RectangleEdge.RIGHT)
        );
    }
    
    /**
     * Returns a new instance based on an existing instance but with the bottom position changed.
     * 
     * @param base  the base.
     * @param bottom  the bottom position.
     * 
     * @return A new instance.
     */
    public static CategoryLabelPositions replaceBottomPosition(CategoryLabelPositions base,
                                                               CategoryLabelPosition bottom) {
        return new CategoryLabelPositions(
            base.getLabelPosition(RectangleEdge.TOP),
            bottom,
            base.getLabelPosition(RectangleEdge.LEFT),
            base.getLabelPosition(RectangleEdge.RIGHT)
        );
    }
    
    /**
     * Returns a new instance based on an existing instance but with the left position changed.
     * 
     * @param base  the base.
     * @param left  the left position.
     * 
     * @return A new instance.
     */
    public static CategoryLabelPositions replaceLeftPosition(CategoryLabelPositions base,
                                                             CategoryLabelPosition left) {
        return new CategoryLabelPositions(
            base.getLabelPosition(RectangleEdge.TOP),
            base.getLabelPosition(RectangleEdge.BOTTOM),
            left,
            base.getLabelPosition(RectangleEdge.RIGHT)
        );
    }
    
    /**
     * Returns a new instance based on an existing instance but with the right position changed.
     * 
     * @param base  the base.
     * @param right  the right position.
     * 
     * @return A new instance.
     */
    public static CategoryLabelPositions replaceRightPosition(CategoryLabelPositions base,
                                                              CategoryLabelPosition right) {
        return new CategoryLabelPositions(
            base.getLabelPosition(RectangleEdge.TOP),
            base.getLabelPosition(RectangleEdge.BOTTOM),
            base.getLabelPosition(RectangleEdge.LEFT),
            right
        );
    }
    
}
