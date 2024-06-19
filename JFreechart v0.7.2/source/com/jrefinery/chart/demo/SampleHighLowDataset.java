/* ===============
 * JFreeChart Demo
 * ===============
 *
 * Project Info:  http://www.jrefinery.com/jfreechart
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
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
 * -------------------------
 * SampleHighLowDataset.java
 * -------------------------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Andrzej Porebski;
 *
 * $Id: SampleHighLowDataset.java,v 1.1 2007/10/10 18:56:26 vauchers Exp $
 *
 * Changes (from 24-Aug-2001)
 * --------------------------
 * 24-Aug-2001 : Added standard source header (DG);
 * 15-Oct-2001 : Data source classes in new package com.jrefinery.data.* (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 07-Nov-2001 : Updated source header (DG);
 * 13-Dec-2001 : Changed HighLowDataset extension (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.util.*;
import com.jrefinery.data.*;
import com.jrefinery.chart.*;

/**
 * A sample dataset for a high-low-open-close plot.
 * <P>
 * Note that the aim of this class is to create a self-contained dataset for demo purposes -
 * it is NOT intended to show how you should go about writing your own data sources.
 */
public class SampleHighLowDataset extends AbstractSeriesDataset implements HighLowDataset {

    private Date[] dates;
    private Double[] highs;
    private Double[] lows;
    private Double[] opens;
    private Double[] closes;

    /**
     * Default constructor.
     */
    public SampleHighLowDataset() {
        this.initialiseData();
    }

    /**
     * Returns the x-value for the specified series and item.  Series are numbered 0, 1, ...
     * @param series The index (zero-based) of the series;
     * @param item The index (zero-based) of the required item;
     * @return The x-value for the specified series and item.
     */
    public Number getXValue(int series, int item) {
        return new Long(dates[item].getTime());
    }

    public Number getStartValue(int series, int item) {
        return getXValue(series, item);
    }

    public Number getEndValue(int series, int item) {
        return getXValue(series, item);
    }

    /**
     * Returns the y-value for the specified series and item.  Series are numbered 0, 1, ...
     * @param series The index (zero-based) of the series;
     * @param item The index (zero-based) of the required item;
     * @return The y-value for the specified series and item.
     */
    public Number getYValue(int series, int item) {
        if (series==0) {
            return closes[item];
        }
        else return null;
    }

    /**
     * Returns the high-value for the specified series and item.  Series are numbered 0, 1, ...
     * @param series The index (zero-based) of the series;
     * @param item The index (zero-based) of the required item;
     * @return The high-value for the specified series and item.
     */
    public Number getHighValue(int series, int item) {
        if (series==0) {
            return highs[item];
        }
        else return null;
    }

    /**
     * Returns the low-value for the specified series and item.  Series are numbered 0, 1, ...
     * @param series The index (zero-based) of the series;
     * @param item The index (zero-based) of the required item;
     * @return The low-value for the specified series and item.
     */
    public Number getLowValue(int series, int item) {
        if (series==0) {
            return lows[item];
        }
        else return null;
    }

    /**
     * Returns the open-value for the specified series and item.  Series are numbered 0, 1, ...
     * @param series The index (zero-based) of the series;
     * @param item The index (zero-based) of the required item;
     * @return The open-value for the specified series and item.
     */
    public Number getOpenValue(int series, int item) {
        if (series==0) {
            return opens[item];
        }
        else return null;
    }

    /**
     * Returns the close-value for the specified series and item.  Series are numbered 0, 1, ...
     * @param series The index (zero-based) of the series;
     * @param item The index (zero-based) of the required item;
     * @return The close-value for the specified series and item.
     */
    public Number getCloseValue(int series, int item) {
        if (series==0) {
            return closes[item];
        }
        else return null;
    }

    public Number getVolumeValue(int series, int item) {
        return Plot.ZERO;
    }

    /**
     * Returns the number of series in the data source, ONE in this sample.
     * @return The number of series in the data source.
     */
    public int getSeriesCount() {
        return 1;
    }

    /**
     * Returns the name of the series.
     * @param series The index (zero-based) of the series;
     * @return The name of the series.
     */
    public String getSeriesName(int series) {
        if (series==0) {
            return "IBM";
        }
        else return "Error";
    }

    /**
     * Returns the number of items in the specified series.
     * @param series The index (zero-based) of the series;
     * @return The number of items in the specified series.
     */
    public int getItemCount(int series) {
        return 47;  // one series with 47 items in this sample
    }

    /**
     * Sets up the data for the sample data source.
     */
    private void initialiseData() {

        dates = new Date[47];
        highs = new Double[47];
        lows = new Double[47];
        opens = new Double[47];
        closes = new Double[47];

        dates[0]  = createDate(2001, Calendar.JANUARY,4);
        highs[0]  = new Double(47.0);
        lows[0]   = new Double(33.0);
        opens[0]  = new Double(35.0);
        closes[0] = new Double(33.0);

        dates[1]  = createDate(2001, Calendar.JANUARY,5);
        highs[1]  = new Double(47.0);
        lows[1]   = new Double(32.0);
        opens[1]  = new Double(41.0);
        closes[1] = new Double(37.0);

        dates[2]  = createDate(2001, Calendar.JANUARY,6);
        highs[2]  = new Double(49.0);
        lows[2]   = new Double(43.0);
        opens[2]  = new Double(46.0);
        closes[2] = new Double(48.0);

        dates[3]  = createDate(2001, Calendar.JANUARY,7);
        highs[3]  = new Double(51.0);
        lows[3]   = new Double(39.0);
        opens[3]  = new Double(40.0);
        closes[3] = new Double(47.0);

        dates[4]  = createDate(2001, Calendar.JANUARY,8);
        highs[4]  = new Double(60.0);
        lows[4]   = new Double(40.0);
        opens[4]  = new Double(46.0);
        closes[4] = new Double(53.0);

        dates[5]  = createDate(2001, Calendar.JANUARY,9);
        highs[5]  = new Double(62.0);
        lows[5]   = new Double(55.0);
        opens[5]  = new Double(57.0);
        closes[5] = new Double(61.0);

        dates[6]  = createDate(2001, Calendar.JANUARY,10);
        highs[6]  = new Double(65.0);
        lows[6]   = new Double(56.0);
        opens[6]  = new Double(62.0);
        closes[6] = new Double(59.0);

        dates[7]  = createDate(2001, Calendar.JANUARY,11);
        highs[7]  = new Double(55.0);
        lows[7]   = new Double(43.0);
        opens[7]  = new Double(45.0);
        closes[7] = new Double(47.0);

        dates[8]  = createDate(2001, Calendar.JANUARY,12);
        highs[8]  = new Double(54.0);
        lows[8]   = new Double(33.0);
        opens[8]  = new Double(40.0);
        closes[8] = new Double(51.0);

        dates[9]  = createDate(2001, Calendar.JANUARY,13);
        highs[9]  = new Double(47.0);
        lows[9]   = new Double(33.0);
        opens[9]  = new Double(35.0);
        closes[9] = new Double(33.0);

        dates[10]  = createDate(2001, Calendar.JANUARY,14);
        highs[10]  = new Double(54.0);
        lows[10]   = new Double(38.0);
        opens[10]  = new Double(43.0);
        closes[10] = new Double(52.0);

        dates[11]  = createDate(2001, Calendar.JANUARY,15);
        highs[11]  = new Double(48.0);
        lows[11]   = new Double(41.0);
        opens[11]  = new Double(44.0);
        closes[11] = new Double(41.0);

        dates[12]  = createDate(2001, Calendar.JANUARY,17);
        highs[12]  = new Double(60.0);
        lows[12]   = new Double(30.0);
        opens[12]  = new Double(34.0);
        closes[12] = new Double(44.0);

        dates[13]  = createDate(2001, Calendar.JANUARY,18);
        highs[13]  = new Double(58.0);
        lows[13]   = new Double(44.0);
        opens[13]  = new Double(54.0);
        closes[13] = new Double(56.0);

        dates[14]  = createDate(2001, Calendar.JANUARY,19);
        highs[14]  = new Double(54.0);
        lows[14]   = new Double(32.0);
        opens[14]  = new Double(42.0);
        closes[14] = new Double(53.0);

        dates[15]  = createDate(2001, Calendar.JANUARY,20);
        highs[15]  = new Double(53.0);
        lows[15]   = new Double(39.0);
        opens[15]  = new Double(50.0);
        closes[15] = new Double(49.0);

        dates[16]  = createDate(2001, Calendar.JANUARY,21);
        highs[16]  = new Double(47.0);
        lows[16]   = new Double(33.0);
        opens[16]  = new Double(41.0);
        closes[16] = new Double(40.0);

        dates[17]  = createDate(2001, Calendar.JANUARY,22);
        highs[17]  = new Double(55.0);
        lows[17]   = new Double(37.0);
        opens[17]  = new Double(43.0);
        closes[17] = new Double(45.0);

        dates[18]  = createDate(2001, Calendar.JANUARY,23);
        highs[18]  = new Double(54.0);
        lows[18]   = new Double(42.0);
        opens[18]  = new Double(50.0);
        closes[18] = new Double(42.0);

        dates[19]  = createDate(2001, Calendar.JANUARY,24);
        highs[19]  = new Double(48.0);
        lows[19]   = new Double(37.0);
        opens[19]  = new Double(37.0);
        closes[19] = new Double(47.0);

        dates[20]  = createDate(2001, Calendar.JANUARY,25);
        highs[20]  = new Double(58.0);
        lows[20]   = new Double(33.0);
        opens[20]  = new Double(39.0);
        closes[20] = new Double(41.0);

        dates[21]  = createDate(2001, Calendar.JANUARY,26);
        highs[21]  = new Double(47.0);
        lows[21]   = new Double(31.0);
        opens[21]  = new Double(36.0);
        closes[21] = new Double(41.0);

        dates[22]  = createDate(2001, Calendar.JANUARY,27);
        highs[22]  = new Double(58.0);
        lows[22]   = new Double(44.0);
        opens[22]  = new Double(49.0);
        closes[22] = new Double(44.0);

        dates[23]  = createDate(2001, Calendar.JANUARY,28);
        highs[23]  = new Double(46.0);
        lows[23]   = new Double(41.0);
        opens[23]  = new Double(43.0);
        closes[23] = new Double(44.0);

        dates[24]  = createDate(2001, Calendar.JANUARY,29);
        highs[24]  = new Double(56.0);
        lows[24]   = new Double(39.0);
        opens[24]  = new Double(39.0);
        closes[24] = new Double(51.0);

        dates[25]  = createDate(2001, Calendar.JANUARY,30);
        highs[25]  = new Double(56.0);
        lows[25]   = new Double(39.0);
        opens[25]  = new Double(47.0);
        closes[25] = new Double(49.0);

        dates[26]  = createDate(2001, Calendar.JANUARY,31);
        highs[26]  = new Double(53.0);
        lows[26]   = new Double(39.0);
        opens[26]  = new Double(52.0);
        closes[26] = new Double(47.0);

        dates[27]  = createDate(2001, Calendar.FEBRUARY,1);
        highs[27]  = new Double(51.0);
        lows[27]   = new Double(30.0);
        opens[27]  = new Double(45.0);
        closes[27] = new Double(47.0);

        dates[28]  = createDate(2001, Calendar.FEBRUARY,2);
        highs[28]  = new Double(47.0);
        lows[28]   = new Double(30.0);
        opens[28]  = new Double(34.0);
        closes[28] = new Double(46.0);

        dates[29]  = createDate(2001, Calendar.FEBRUARY,3);
        highs[29]  = new Double(57.0);
        lows[29]   = new Double(37.0);
        opens[29]  = new Double(44.0);
        closes[29] = new Double(56.0);

        dates[30]  = createDate(2001, Calendar.FEBRUARY,4);
        highs[30]  = new Double(49.0);
        lows[30]   = new Double(40.0);
        opens[30]  = new Double(47.0);
        closes[30] = new Double(44.0);

        dates[31]  = createDate(2001, Calendar.FEBRUARY,5);
        highs[31]  = new Double(46.0);
        lows[31]   = new Double(38.0);
        opens[31]  = new Double(43.0);
        closes[31] = new Double(40.0);

        dates[32]  = createDate(2001, Calendar.FEBRUARY,6);
        highs[32]  = new Double(55.0);
        lows[32]   = new Double(38.0);
        opens[32]  = new Double(39.0);
        closes[32] = new Double(53.0);

        dates[33]  = createDate(2001, Calendar.FEBRUARY,7);
        highs[33]  = new Double(50.0);
        lows[33]   = new Double(33.0);
        opens[33]  = new Double(37.0);
        closes[33] = new Double(37.0);

        dates[34]  = createDate(2001, Calendar.FEBRUARY,8);
        highs[34]  = new Double(59.0);
        lows[34]   = new Double(34.0);
        opens[34]  = new Double(57.0);
        closes[34] = new Double(43.0);

        dates[35]  = createDate(2001, Calendar.FEBRUARY,9);
        highs[35]  = new Double(48.0);
        lows[35]   = new Double(39.0);
        opens[35]  = new Double(46.0);
        closes[35] = new Double(47.0);

        dates[36]  = createDate(2001, Calendar.FEBRUARY,10);
        highs[36]  = new Double(55.0);
        lows[36]   = new Double(30.0);
        opens[36]  = new Double(37.0);
        closes[36] = new Double(30.0);

        dates[37]  = createDate(2001, Calendar.FEBRUARY,11);
        highs[37]  = new Double(60.0);
        lows[37]   = new Double(32.0);
        opens[37]  = new Double(56.0);
        closes[37] = new Double(36.0);

        dates[38]  = createDate(2001, Calendar.FEBRUARY,12);
        highs[38]  = new Double(56.0);
        lows[38]   = new Double(42.0);
        opens[38]  = new Double(53.0);
        closes[38] = new Double(54.0);

        dates[39]  = createDate(2001, Calendar.FEBRUARY,13);
        highs[39]  = new Double(49.0);
        lows[39]   = new Double(42.0);
        opens[39]  = new Double(45.0);
        closes[39] = new Double(42.0);

        dates[40]  = createDate(2001, Calendar.FEBRUARY,14);
        highs[40]  = new Double(55.0);
        lows[40]   = new Double(42.0);
        opens[40]  = new Double(47.0);
        closes[40] = new Double(54.0);

       dates[41]  = createDate(2001, Calendar.FEBRUARY,15);
        highs[41]  = new Double(49.0);
        lows[41]   = new Double(35.0);
        opens[41]  = new Double(38.0);
        closes[41] = new Double(35.0);

        dates[42]  = createDate(2001, Calendar.FEBRUARY,16);
        highs[42]  = new Double(47.0);
        lows[42]   = new Double(38.0);
        opens[42]  = new Double(43.0);
        closes[42] = new Double(42.0);

        dates[43]  = createDate(2001, Calendar.FEBRUARY,17);
        highs[43]  = new Double(53.0);
        lows[43]   = new Double(42.0);
        opens[43]  = new Double(47.0);
        closes[43] = new Double(48.0);

        dates[44]  = createDate(2001, Calendar.FEBRUARY,18);
        highs[44]  = new Double(47.0);
        lows[44]   = new Double(44.0);
        opens[44]  = new Double(46.0);
        closes[44] = new Double(44.0);

        dates[45]  = createDate(2001, Calendar.FEBRUARY,19);
        highs[45]  = new Double(46.0);
        lows[45]   = new Double(40.0);
        opens[45]  = new Double(43.0);
        closes[45] = new Double(44.0);

        dates[46]  = createDate(2001, Calendar.FEBRUARY,20);
        highs[46]  = new Double(48.0);
        lows[46]   = new Double(41.0);
        opens[46]  = new Double(46.0);
        closes[46] = new Double(41.0);

    }

    /**
     * Returns a java.util.Date for the specified year, month and day.
     */
    private Date createDate(int year, int month, int day) {
        GregorianCalendar calendar = new GregorianCalendar(year, month, day);
        return calendar.getTime();
    }

}
