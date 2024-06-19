/* ============================================
 * JFreeChart : a free Java chart class library
 * ============================================
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
 * SignalsDataset.java
 * -------------------
 * (C) Copyright 2002, by Sylvain Vieujot and Contributors.
 *
 * Original Author:  Sylvain Vieujot;
 * Contributor(s):   -;
 *
 * $Id: SignalsDataset.java,v 1.1 2007/10/10 19:52:14 vauchers Exp $
 *
 * Changes
 * -------
 * 8-Jan-2002 : Version 1 (SV);
 *
 */

package com.jrefinery.data;

/**
 * @author  sylvain
 */
public interface SignalsDataset extends XYDataset {

    public static final int ENTER_LONG = 1;
    public static final int ENTER_SHORT = -1;
    public static final int EXIT_LONG = 2;
    public static final int EXIT_SHORT = -2;

    public int getType(int series, int item);

    public double getLevel(int series, int item);

}
