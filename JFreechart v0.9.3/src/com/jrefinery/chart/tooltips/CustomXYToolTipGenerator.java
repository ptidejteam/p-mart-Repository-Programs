package com.jrefinery.chart.tooltips;

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
 * -----------------------
 * CustomXYToolTipGenerator.java
 * -----------------------
 * (C) Copyright 2002, by Richard Atkinson.
 *
 * Original Author:  Richard Atkinson (richard_c_atkinson@ntlworld.com);
 *
 * $Id: CustomXYToolTipGenerator.java,v 1.1 2007/10/10 19:52:22 vauchers Exp $
 *
 * Changes:
 * --------
 * 05-Aug-2002 : Version 1, contributed by Richard Atkinson.  Stores custom tooltips
 * in an List of Lists.  The XYDataSet passed into the generateToolTip method
 * is not used;
 *
 */
import java.util.List;
import java.util.ArrayList;
import com.jrefinery.data.XYDataset;
 
public class CustomXYToolTipGenerator implements XYToolTipGenerator {
	protected List toolTipSeries = new ArrayList();

public CustomXYToolTipGenerator() {
	super();
}
public void addToolTipSeries(List toolTips) {
	this.toolTipSeries.add(toolTips);
}
public String generateToolTip(XYDataset xyDataSet, int series, int item) {
	String toolTip = "";
	List toolTips = (List)this.toolTipSeries.get(series);
	toolTip = (String)toolTips.get(item);
	return toolTip;
}
}
