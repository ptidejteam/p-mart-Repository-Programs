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
 * -------------------------------
 * StandardXYToolTipGenerator.java
 * -------------------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: StandardXYToolTipGenerator.java,v 1.1 2007/10/10 19:52:22 vauchers Exp $
 *
 * Changes
 * -------
 * 13-Dec-2001 : Version 1 (DG);
 * 16-Jan-2002 : Completed Javadocs (DG);
 * 02-Apr-2002 : Modified to handle null y-values (DG);
 * 09-Apr-2002 : Added formatting objects for the x and y values (DG);
 * 30-May-2002 : Added series name to standard tool tip (DG);
 *
 */

package com.jrefinery.chart.tooltips;

import com.jrefinery.data.XYDataset;

import java.text.NumberFormat;

/**
 * A standard tooltip generator for plots that use data from an XYDataset.
 */
public class StandardXYToolTipGenerator implements XYToolTipGenerator {

	/** A formatter for the x value. */
	protected NumberFormat xFormat;

	/** A formatter for the y value. */
	protected NumberFormat yFormat;

	/**
	 * Default constructor.
	 */
	public StandardXYToolTipGenerator() {

		this(NumberFormat.getNumberInstance(),
			NumberFormat.getNumberInstance());

	}

	/**
	 * Constructs a new tooltip generator using the specified number formats.
	 *
	 * @param xFormat   The format object for the x values.
	 * @param yFormat   The format object for the y values.
	 */
	public StandardXYToolTipGenerator(NumberFormat xFormat,
		NumberFormat yFormat)
	{

		this.xFormat = xFormat;
		this.yFormat = yFormat;

	}

	/**
	 * Generates a tooltip text item for a particular item within a series.
	 *
	 * @param data      The dataset.
	 * @param series    The series number (zero-based index).
	 * @param item      The item number (zero-based index).
	 * @return a tooltip text.
	*/
	public String generateToolTip(XYDataset data, int series, int item) {

		String result = data.getSeriesName(series)+": ";
		Number x = data.getXValue(series, item);
		result = result+"x: "+xFormat.format(x);

		Number y = data.getYValue(series, item);
		if (y!=null) {
			result = result+", y: "+yFormat.format(y);
		}
		else {
			result = result+", y: null";
		}

		return result;
	}

}
