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
 * CustomXYURLGenerator.java
 * -----------------------
 * (C) Copyright 2002, by Richard Atkinson.
 *
 * Original Author:  Richard Atkinson (richard_c_atkinson@ntlworld.com);
 *
 * $Id: CustomXYURLGenerator.java,v 1.1 2007/10/10 19:52:22 vauchers Exp $
 *
 * Changes:
 * --------
 * 05-Aug-2002 : Version 1, contributed by Richard Atkinson;
 *
 */
package com.jrefinery.chart.urls;

import java.util.List;
import java.util.ArrayList;
import com.jrefinery.data.XYDataset;

public class CustomXYURLGenerator implements XYURLGenerator{

    protected ArrayList urlSeries = new ArrayList();

    public CustomXYURLGenerator() {
    }

    public String generateURL(XYDataset data, int series, int item) {
        String url = "";
        List urls = (List)this.urlSeries.get(series);
        url = (String)urls.get(item);
        return url;
    }

    public void addURLSeries(List urls) {
        this.urlSeries.add(urls);
    }

}
