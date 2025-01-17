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
 * ----------------
 * GreyPalette.java
 * ----------------
 * (C) Copyright 2002, 2003 by David M. O'Donnell and Contributors.
 *
 * Original Author:  David M. O'Donnell;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: GreyPalette.java,v 1.1 2007/10/10 20:07:39 vauchers Exp $
 *
 * Changes
 * -------
 * 26-Nov-2002 : Version 1 contributed by David M. O'Donnell (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 *
 */

package org.jfree.chart.ui;

import java.io.Serializable;

/**
 * A grey color palette.
 *
 * @author David M. O'Donnell.
 */
public class GreyPalette extends ColorPalette implements Serializable {

    /**
     * Creates a new palette.
     */
    public GreyPalette() {
        super();
        initialize();
    }

    /**
     * Intializes the palette's indices.
     */
    public void initialize() {

        setPaletteName("Grey");

        r = new int[256];
        g = new int[256];
        b = new int[256];

        r[0] = 255;
        g[0] = 255;
        b[0] = 255;
        r[1] = 0;
        g[1] = 0;
        b[1] = 0;

        for (int i = 2; i < 256; i++) {
            r[i] = i;
            g[i] = i;
            b[i] = i;
        }

    }

}
