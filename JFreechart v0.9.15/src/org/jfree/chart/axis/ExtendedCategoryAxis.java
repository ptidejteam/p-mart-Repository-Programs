/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
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
 * -------------------------
 * ExtendedCategoryAxis.java
 * -------------------------
 * (C) Copyright 2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: ExtendedCategoryAxis.java,v 1.1 2007/10/10 19:21:57 vauchers Exp $
 *
 * Changes
 * -------
 * 07-Nov-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.axis;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import org.jfree.text.TextBlock;
import org.jfree.text.TextFragment;
import org.jfree.text.TextLine;
import org.jfree.ui.RectangleEdge;

/**
 * An extended version of the {@link CategoryAxis} class that supports sublabels on
 * the axis.
 */
public class ExtendedCategoryAxis extends CategoryAxis {

    /** Storage for the sublabels. */
    private Map sublabels;
    
    /** The sublabel font. */
    private Font sublabelFont;
    
    /**
     * Creates a new axis.
     * 
     * @param label  the axis label.
     */
    public ExtendedCategoryAxis(String label) {
        super(label);
        this.sublabels = new HashMap();
        this.sublabelFont = new Font("SansSerif", Font.PLAIN, 10);
    }
    
    /**
     * Returns the font for the sublabels.
     * 
     * @return the font.
     */
    public Font getSubLabelFont() {
        return this.sublabelFont;
    }
    
    /**
     * Sets the font for the sublabels.
     * 
     * @param font  the font.
     */
    public void setSubLabelFont(Font font) {
        this.sublabelFont = font;
    }
    
    /**
     * Adds a sublabel for a category.
     * 
     * @param category  the category.
     * @param label  the label.
     */
    public void addSubLabel(Comparable category, String label) {
        this.sublabels.put(category, label);
    }
    
    /**
     * Overrides the default behaviour by adding the sublabel to the text block that is
     * used for the category label.
     * 
     * @param category  the category.
     * @param width  the width (not used yet).
     * @param edge  the location of the axis.
     * 
     * @return a label.
     */
    protected TextBlock createLabel(Comparable category, double width, RectangleEdge edge) {
        TextBlock label = super.createLabel(category, width, edge);   
        String s = (String) this.sublabels.get(category);
        if (s != null) {
            if (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM) {
                TextLine line = new TextLine(s, this.sublabelFont);
                label.addLine(line);
            }
            else if (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT) {
                TextLine line = label.getLastLine();
                if (line != null) {
                    line.addFragment(new TextFragment("  " + s, this.sublabelFont));
                }
            }
        }
        return label; 
    }
    
}
