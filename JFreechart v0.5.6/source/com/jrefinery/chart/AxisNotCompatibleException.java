/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            AxisNotCompatibleException.java
 * Author:          David Gilbert;
 * Contributor(s):  -;
 *
 * (C) Copyright 2000, Simba Management Limited;
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307, USA.
 *
 * $Id: AxisNotCompatibleException.java,v 1.1 2007/10/10 18:52:16 vauchers Exp $
 */

package com.jrefinery.chart;

/**
 * An exception that is generated when assigning an axis to a plot *if* the axis is not compatible
 * with the plot type.  For example, a CategoryAxis is not compatible with an XYPlot.
 * <P>
 * Note:  there is more work to be done on these exceptions.
 */
public class AxisNotCompatibleException extends Exception {

  /**
   * Standard constructor.
   * @param msg A message describing the exception.
   */
  public AxisNotCompatibleException(String msg) {
    super(msg);
  }

}
