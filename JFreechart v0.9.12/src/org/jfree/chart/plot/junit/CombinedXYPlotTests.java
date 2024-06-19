/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
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
 * ------------------------
 * CombinedXYPlotTests.java
 * ------------------------
 * (C) Copyright 2003 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: CombinedXYPlotTests.java,v 1.1 2007/10/10 19:12:26 vauchers Exp $
 *
 * Changes
 * -------
 * 27-Mar-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.plot.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.TimeSeriesToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.StandardXYItemRenderer;
import org.jfree.chart.renderer.XYBarRenderer;
import org.jfree.chart.renderer.XYItemRenderer;
import org.jfree.data.IntervalXYDataset;
import org.jfree.data.XYDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.date.SerialDate;

/**
 * Tests for the {@link CombinedXYPlot} class.
 *
 * @author David Gilbert
 */
public class CombinedXYPlotTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(CombinedXYPlotTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public CombinedXYPlotTests(String name) {
        super(name);
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        // create a parent plot...
        CombinedDomainXYPlot p1 = new CombinedDomainXYPlot(new NumberAxis("Value"));
        p1.setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);

        // create subplot 1...
        IntervalXYDataset data1 = createDataset1();
        XYItemRenderer renderer1 = new XYBarRenderer(0.20);
        renderer1.setToolTipGenerator(new TimeSeriesToolTipGenerator("d-MMM-yyyy", "0,000.0"));
        XYPlot subplot1 = new XYPlot(data1, new DateAxis("Date"), null, renderer1);

        // create subplot 2...
        XYDataset data2 = this.createDataset2();
        XYItemRenderer renderer2 = new StandardXYItemRenderer();
        renderer2.setToolTipGenerator(new TimeSeriesToolTipGenerator("d-MMM-yyyy", "0,000.0"));
        XYPlot subplot2 = new XYPlot(data2, new DateAxis("Date"), null, renderer2);

        // add the subplots...
        p1.add(subplot1, 1);
        p1.add(subplot2, 1);

        CombinedDomainXYPlot p2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(p1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            p2 = (CombinedDomainXYPlot) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(p1, p2);

    }

    /**
     * Creates a sample dataset.
     *
     * @return Series 1.
     */
    private IntervalXYDataset createDataset1() {

        // create dataset 1...
        TimeSeries series1 = new TimeSeries("Series 1", Day.class);
        series1.add(new Day(1, SerialDate.MARCH, 2002), 12353.3);
        series1.add(new Day(2, SerialDate.MARCH, 2002), 13734.4);
        series1.add(new Day(3, SerialDate.MARCH, 2002), 14525.3);
        series1.add(new Day(4, SerialDate.MARCH, 2002), 13984.3);
        series1.add(new Day(5, SerialDate.MARCH, 2002), 12999.4);
        series1.add(new Day(6, SerialDate.MARCH, 2002), 14274.3);
        series1.add(new Day(7, SerialDate.MARCH, 2002), 15943.5);
        series1.add(new Day(8, SerialDate.MARCH, 2002), 14845.3);
        series1.add(new Day(9, SerialDate.MARCH, 2002), 14645.4);
        series1.add(new Day(10, SerialDate.MARCH, 2002), 16234.6);
        series1.add(new Day(11, SerialDate.MARCH, 2002), 17232.3);
        series1.add(new Day(12, SerialDate.MARCH, 2002), 14232.2);
        series1.add(new Day(13, SerialDate.MARCH, 2002), 13102.2);
        series1.add(new Day(14, SerialDate.MARCH, 2002), 14230.2);
        series1.add(new Day(15, SerialDate.MARCH, 2002), 11235.2);

        TimeSeriesCollection collection = new TimeSeriesCollection(series1);
        collection.setDomainIsPointsInTime(false);  // this tells the time series collection that
                                                    // we intend the data to represent time periods
                                                    // NOT points in time.  This is required when
                                                    // determining the min/max values in the
                                                    // dataset's domain.
        return collection;

    }

    /**
     * Creates a sample dataset.
     *
     * @return Series 2.
     */
    private XYDataset createDataset2() {

        // create dataset 2...
        TimeSeries series2 = new TimeSeries("Series 2", Day.class);

        series2.add(new Day(3, SerialDate.MARCH, 2002), 16853.2);
        series2.add(new Day(4, SerialDate.MARCH, 2002), 19642.3);
        series2.add(new Day(5, SerialDate.MARCH, 2002), 18253.5);
        series2.add(new Day(6, SerialDate.MARCH, 2002), 15352.3);
        series2.add(new Day(7, SerialDate.MARCH, 2002), 13532.0);
        series2.add(new Day(8, SerialDate.MARCH, 2002), 12635.3);
        series2.add(new Day(9, SerialDate.MARCH, 2002), 13998.2);
        series2.add(new Day(10, SerialDate.MARCH, 2002), 11943.2);
        series2.add(new Day(11, SerialDate.MARCH, 2002), 16943.9);
        series2.add(new Day(12, SerialDate.MARCH, 2002), 17843.2);
        series2.add(new Day(13, SerialDate.MARCH, 2002), 16495.3);
        series2.add(new Day(14, SerialDate.MARCH, 2002), 17943.6);
        series2.add(new Day(15, SerialDate.MARCH, 2002), 18500.7);
        series2.add(new Day(16, SerialDate.MARCH, 2002), 19595.9);

        return new TimeSeriesCollection(series2);

    }

}
