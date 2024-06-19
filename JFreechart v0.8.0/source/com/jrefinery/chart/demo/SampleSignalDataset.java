/* ===============
 * JFreeChart Demo
 * ===============
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
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
import com.jrefinery.data.HighLowDataset;
import com.jrefinery.data.DatasetChangeListener;

public class SampleSignalDataset implements SignalsDataset {

    private HighLowDataset data;

    public SampleSignalDataset() {
        this.data = DemoDatasetFactory.createSampleHighLowDataset();
    }

    public int getItemCount(int series) {
        return data.getItemCount(series);
    }

    public int getSeriesCount() {
        return data.getSeriesCount();
    }

    public String getSeriesName(int series) {
        return data.getSeriesName(series);
    }

    public Number getXValue(int series, int item) {
        return data.getXValue(series, item);
    }

    public Number getYValue(int series, int item) {
        return data.getYValue(series, item);
    }

    public int getType(int series, int item) {
        return SignalsDataset.ENTER_LONG;
    }

    public double getLevel(int series, int item) {
        return this.getXValue(series, item).doubleValue();
    }

    public void addChangeListener(DatasetChangeListener listener) {
        data.addChangeListener(listener);
    }

    public void removeChangeListener(DatasetChangeListener listener) {
        data.removeChangeListener(listener);
    }

}
