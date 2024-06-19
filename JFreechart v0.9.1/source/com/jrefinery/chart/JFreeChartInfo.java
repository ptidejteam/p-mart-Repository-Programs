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
 * -------------------
 * JFreeChartInfo.java
 * -------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: JFreeChartInfo.java,v 1.1 2007/10/10 19:02:26 vauchers Exp $
 *
 * Changes
 * -------
 * 11-Mar-2002 : Version 1 (DG);
 * 14-Mar-2002 : Added Bryan Scott and Mike Duffy to contributors list (DG);
 * 22-Mar-2002 : Changed version number to 0.8.0 (DG);
 * 03-Apr-2002 : Added Hari, Thomas Meier and Anthony Boulestreau to contributors list (DG);
 * 05-Apr-2002 : Changed version number to 0.8.1 (DG);
 * 19-Apr-2002 : Added Sam (oldman) to contributors and changed version number to 0.8.1-dev (DG);
 * 29-Apr-2002 : Added Jeremy Bowman to contributors (DG);
 * 13-May-2002 : Added Roger Studner and Andreas Schneider to contributors (DG);
 * 21-May-2002 : Added Eric Thomas and Jon Iles to contributors (DG);
 * 31-May-2002 : Added Tin Luu to contributors (DG);
 * 07-Jun-2002 : Changed version number to 0.9.0 (DG);
 * 13-Jun-2002 : Added Alex Weber to contributors, and changed version number to 0.9.1 (DG);
 *
 */

package com.jrefinery.chart;

import java.util.List;
import java.util.Arrays;
import com.jrefinery.JCommon;
import com.jrefinery.ui.about.Contributor;
import com.jrefinery.ui.about.Library;
import com.jrefinery.ui.about.Licences;

/**
 * This interface contains constants that provide information about the JFreeChart library.
 */
public interface JFreeChartInfo {

    /** The name of the library. */
    public static final String NAME = "JFreeChart";

    /** The version number. */
    public static final String VERSION = "0.9.1";

    /** Information. */
    public static final String INFO = "http://www.object-refinery.com/jfreechart/index.html";

    /** Copyright. */
    public static final String COPYRIGHT = "(C)opyright 2000-2002, Simba Management Limited and "+
                                           "Contributors";

    /** The licence name. */
    public static final String LICENCE_NAME = "LGPL";

    /** The licence. */
    public static final String LICENCE = Licences.LGPL;

    /** The contributors. */
    public static final List CONTRIBUTORS = Arrays.asList(

        new Contributor[] {
            new Contributor("David Gilbert", "david.gilbert@object-refinery.com"),
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
            new Contributor("Achilleus Mantzios", "-"),
            new Contributor("Bryan Scott", "-"),
            new Contributor("Mike Duffy", "-"),
            new Contributor("Thomas Meier", "-"),
            new Contributor("Hari", "-"),
            new Contributor("Anthony Boulestreau", "-"),
            new Contributor("Sam (oldman)", "-"),
            new Contributor("Jeremy Bowman", "-"),
            new Contributor("Jean-Luc SCHWAB", "-"),
            new Contributor("Roger Studner", "-"),
            new Contributor("Andreas Schneider", "-"),
            new Contributor("Eric Thomas", "-"),
            new Contributor("Jon Iles", "-"),
            new Contributor("Tin Luu", "-"),
            new Contributor("Krzysztof Paz", "-"),
            new Contributor("Alex Weber", "-")
        }

    );

    /** The libraries that JFreeChart uses. */
    public static final List LIBRARIES = Arrays.asList(

        new Library[] {

            new Library(JCommon.INFO)

        }

    );

}