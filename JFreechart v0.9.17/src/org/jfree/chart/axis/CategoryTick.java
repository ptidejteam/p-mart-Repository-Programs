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
 * -----------------
 * CategoryTick.java
 * -----------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: CategoryTick.java,v 1.1 2007/10/10 19:29:22 vauchers Exp $
 *
 * Changes
 * -------
 * 07-Nov-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.axis;

import org.jfree.text.TextBlock;
import org.jfree.text.TextBlockAnchor;
import org.jfree.ui.TextAnchor;

/**
 * A tick for a {@link CategoryAxis}.
 */
public class CategoryTick extends Tick {

    /** The category. */
    private Comparable category;
    
    /** The label. */
    private TextBlock label;
    
    /** The label anchor. */
    private TextBlockAnchor labelAnchor;
    
    /**
     * Creates a new tick.
     * 
     * @param category  the category.
     * @param label  the label.
     * @param labelAnchor  the label anchor.
     * @param rotationAnchor  the rotation anchor.
     * @param angle  the rotation angle (in radians).
     */
    public CategoryTick(Comparable category,
                        TextBlock label,
                        TextBlockAnchor labelAnchor,
                        TextAnchor rotationAnchor,
                        double angle) {
                            
        super("", null, rotationAnchor, angle);
        this.category = category;
        this.label = label;
        this.labelAnchor = labelAnchor;
        
    }
    
    /**
     * Returns the category.
     * 
     * @return the category.
     */
    public Comparable getCategory() {
        return this.category;
    }
    
    /**
     * Returns the label.
     * 
     * @return the label.
     */
    public TextBlock getLabel() {
        return this.label;
    }
    
    /**
     * Returns the label anchor.
     * 
     * @return the label anchor.
     */
    public TextBlockAnchor getLabelAnchor() {
        return this.labelAnchor;
    }
        
}
