/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Object Refinery Limited.
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
 * ----------------------------
 * DataPackageResources_de.java
 * ----------------------------
 * (C) Copyright 2002, 2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  Thomas Meier;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: DataPackageResources_de.java,v 1.1 2007/10/10 19:19:12 vauchers Exp $
 *
 * Changes
 * -------
 * 04-Apr-2002 : Version 1, translation by Thomas Meier (DG);
 * 17-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package org.jfree.data.resources;

import java.util.ListResourceBundle;

/**
 * A resource bundle that stores all the items that might need localisation.
 *
 * @author Thomas Meier
 */
public class DataPackageResources_de extends ListResourceBundle {

    /**
     * Returns the array of strings in the resource bundle.
     *
     * @return the localised resources.
     */
    public Object[][] getContents() {
        return CONTENTS;
    }

    /** The resources to be localised. */
    private static final Object[][] CONTENTS = {

        {"series.default-prefix",     "Reihen"},
        {"categories.default-prefix", "Kategorien"},

    };

}
