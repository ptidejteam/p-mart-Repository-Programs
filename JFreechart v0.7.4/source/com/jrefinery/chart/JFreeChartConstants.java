/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
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
 * ------------------------
 * JFreeChartConstants.java
 * ------------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: JFreeChartConstants.java,v 1.1 2007/10/10 18:57:57 vauchers Exp $
 *
 * Changes
 * -------
 * 06-Mar-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Color;
import java.awt.Image;
import java.util.List;
import java.util.Arrays;
import com.jrefinery.JCommon;
import com.jrefinery.ui.about.Contributor;
import com.jrefinery.ui.about.Library;
import com.jrefinery.ui.about.Licences;

/**
 * Useful constants relating to the JFreeChart class.
 */
public interface JFreeChartConstants {

    /** The name of the library. */
    public static final String NAME = "JFreeChart";

    /** The version number. */
    public static final String VERSION = "0.7.4";

    /** Information. */
    public static final String INFO = "http://www.object-refinery.com/jfreechart";

    /** Copyright. */
    public static final String COPYRIGHT = "(C)opyright 2000-2002, Simba Management Limited and Contributors";

    /** The licence. */
    public static final String LICENCE = Licences.LGPL;

    /** The contributors. */
    public static final List CONTRIBUTORS = Arrays.asList(

        new Contributor[] {
            new Contributor("David Gilbert", "david.gilbert@jrefinery.com"),
            new Contributor("Andrzej Porebski", "-"),
            new Contributor("Bill Kelemen", "-"),
            new Contributor("David Berry", "-"),
            new Contributor("Matthew Wright", "-"),
            new Contributor("David Li", "-"),
            new Contributor("Sylvain Vieujot", "-"),
            new Contributor("Serge V. Grachov", "-"),
            new Contributor("Joao Guilherme Del Valle", "-"),
            new Contributor("Mark Watson", "www.markwatson.com"),
            new Contributor("Søren Caspersen", "-"),
            new Contributor("Laurence Vanhelsuwe", "-"),
            new Contributor("Martin Cordova", "-"),
            new Contributor("Wolfgang Irler", "-"),
            new Contributor("Craig MacFarlane", "-"),
            new Contributor("Jonathan Nash", "-"),
            new Contributor("Hans-Jurgen Greiner", "-"),
            new Contributor("Achilleus Mantzios", "-")
        }

    );

    /** The libraries that JFreeChart uses. */
    public static final List LIBRARIES = Arrays.asList(

        new Library[] {

            new Library(JCommon.NAME,
                        JCommon.VERSION,
                        JCommon.LICENCE_NAME,
                        JCommon.INFO)

        }

    );

    /** The default font for titles. */
    public static final Font DEFAULT_TITLE_FONT = new Font("SansSerif", Font.BOLD, 18);

    /** The default background color. */
    public static final Paint DEFAULT_BACKGROUND_PAINT = Color.lightGray;

    /** The default background image. */
    public static final Image DEFAULT_BACKGROUND_IMAGE = null;

    /** The default background image alpha. */
    public static float DEFAULT_BACKGROUND_IMAGE_ALPHA = 0.5f;

}