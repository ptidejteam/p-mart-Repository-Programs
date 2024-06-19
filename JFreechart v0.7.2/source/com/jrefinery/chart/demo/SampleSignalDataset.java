/* ===============
 * JFreeChart Demo
 * ===============
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307, USA.
 *
 */

package com.jrefinery.chart.demo;

import com.jrefinery.data.SignalsDataset;

public class SampleSignalDataset extends SampleHighLowDataset implements SignalsDataset {

    public SampleSignalDataset() {
        super();
    }

    public int getType(int series, int item) {
        return SignalsDataset.ENTER_LONG;
    }

    public double getLevel(int series, int item) {
        return this.getXValue(series, item).doubleValue();
    }

}
