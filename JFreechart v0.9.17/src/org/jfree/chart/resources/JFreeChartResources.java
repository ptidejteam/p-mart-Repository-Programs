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
 * ------------------------
 * JFreeChartResources.java
 * ------------------------
 * (C) Copyright 2002-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: JFreeChartResources.java,v 1.1 2007/10/10 19:29:24 vauchers Exp $
 *
 * Changes:
 * --------
 * 01-Oct-2002 : Version 1 (DG);
 * 16-Oct-2002 : Changed version number to 0.9.4 (DG);
 * 14-Feb-2003 : Changed version number to 0.9.6 (DG);
 * 03-Apr-2003 : Changed version number to 0.9.7 (DG);
 * 24-Apr-2003 : Changed version number to 0.9.8 (DG);
 * 17-Jul-2003 : Changed version number to 0.9.10 (DG);
 * 05-Aug-2003 : Changed version number to 0.9.11 (DG);
 * 02-Sep-2003 : Changed version number to 0.9.12 (DG);
 * 24-Sep-2003 : Changed version number to 0.9.13 (DG);
 * 13-Nov-2003 : Changed version number to 0.9.14 (DG);
 * 27-Nov-2003 : Changed version number to 0.9.15 (DG);
 * 23-Dec-2003 : Changed version number to 0.9.16 (DG);
 *
 */

package org.jfree.chart.resources;

import java.util.ListResourceBundle;

/**
 * Localised resources for JFreeChart.
 *
 * @author David Gilbert
 */
public class JFreeChartResources extends ListResourceBundle {

    /**
     * Returns the array of strings in the resource bundle.
     *
     * @return the array of strings in the resource bundle.
     */
    public Object[][] getContents() {
        return CONTENTS;
    }

    /** The resources to be localised. */
    private static final Object[][] CONTENTS = {

        {"project.name",      "JFreeChart"},
        {"project.version",   "0.9.17"},
        {"project.info",      "http://www.jfree.org/jfreechart/index.html"},
        {"project.copyright", "(C)opyright 2000-2004, by Object Refinery Limited and"
                            + " Contributors"}

    };

}
