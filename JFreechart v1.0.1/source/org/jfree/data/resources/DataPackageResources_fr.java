/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ----------------------------
 * DataPackageResources_fr.java
 * ----------------------------
 *
 * Original Author: Anthony Boulestreau;
 * Contributor(s): -;
 *
 * $Id: DataPackageResources_fr.java,v 1.1 2007/10/10 20:17:16 vauchers Exp $
 *
 * Changes
 * -------
 * 26-Mar-2002 : Version 1 (AB);
 * 17-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package org.jfree.data.resources;

import java.util.ListResourceBundle;

/**
 * A resource bundle that stores all the items that might need localisation.
 *
 * @author AB
 */
public class DataPackageResources_fr extends ListResourceBundle {

    /**
     * Returns the array of strings in the resource bundle.
     *
     * @return The localised resources.
     */
    public Object[][] getContents() {
        return CONTENTS;
    }

    /** The resources to be localised. */
    private static final Object[][] CONTENTS = {

        {"series.default-prefix", "S?ries"},
        {"categories.default-prefix", "Cat?gorie"},

    };

}
