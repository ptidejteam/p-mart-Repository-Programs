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
 * --------------------------
 * AbstractSeriesDataset.java
 * --------------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: AbstractSeriesDataset.java,v 1.1 2007/10/10 19:52:14 vauchers Exp $
 *
 * Changes
 * -------
 * 17-Nov-2001 : Version 1 (DG);
 * 28-Mar-2002 : Implemented SeriesChangeListener interface (DG);
 *
 */

package com.jrefinery.data;

/**
 * An abstract implementation of the Dataset interface, containing a mechanism
 * for registering change listeners.
 */
public abstract class AbstractSeriesDataset extends AbstractDataset
    implements SeriesDataset, SeriesChangeListener
{

    /**
     * Constructs a dataset.
     */
    protected AbstractSeriesDataset() {
        super();
    }

    /**
     * Returns the number of items that should be displayed in the legend.
     * <P>
     * For series datasets, the legend will contain the name of each series.
     *
     * @return The number of items that should be displayed in the legend.
     */
    public int getLegendItemCount() {
        return getSeriesCount();
    }

    /**
     * Returns the legend item labels.
     *
     * @return The legend item labels.
     */
    public String[] getLegendItemLabels() {

        int seriesCount = this.getSeriesCount();
        String[] labels = new String[seriesCount];
        for (int i=0; i<seriesCount; i++) {
            labels[i] = this.getSeriesName(i);
        }
        return labels;

    }

    /**
     * Returns the number of series in the dataset.
     *
     * @return The number of series in the dataset.
     */
    public abstract int getSeriesCount();

    /**
     * Returns the name of a series.
     *
     * @param series The series (zero-based index).
     *
     * @return The series name.
     */
    public abstract String getSeriesName(int series);

    /**
     * Called when a series belonging to the dataset changes.
     *
     * @param event Information about the change.
     */
    public void seriesChanged(SeriesChangeEvent event) {
        this.fireDatasetChanged();
    }

}
