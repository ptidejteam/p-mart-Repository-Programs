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
 * ------------------------------------
 * StandardURLTagFragmentGenerator.java
 * ------------------------------------
 * (C) Copyright 2003, 2004, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson;
 *
 * $Id: StandardURLTagFragmentGenerator.java,v 1.1 2007/10/10 19:39:35 vauchers Exp $
 *
 * Changes
 * -------
 * 12-Aug-2003 : Version 1 (RA);
 * 
 */
 
package org.jfree.chart.imagemap;

/**
 * Generates URLs using the HTML href attribute for image map area tags.
 *
 * @author Richard Atkinson
 */
public class StandardURLTagFragmentGenerator implements URLTagFragmentGenerator {

    /**
     * Generates a URL string to go in an HTML image map.
     *
     * @param urlText the URL
     * @return the formatted text
     */
    public String generateURLFragment(String urlText) {
        return " href=\"" + urlText + "\"";
    }

}
