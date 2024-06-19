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
 * ---------------------
 * JDBCPieChartDemo.java
 * ---------------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: JDBCPieChartDemo.java,v 1.1 2007/10/10 19:52:20 vauchers Exp $
 *
 * Changes
 * -------
 * 13-Aug-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.Color;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.PiePlot;
import com.jrefinery.data.PieDataset;
import com.jrefinery.data.JdbcPieDataset;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A pie chart that obtains data from a database via JDBC.
 * <P>
 * To run this demo, you need to have a database that you can access via JDBC, and you need to
 * create a table called 'PieData1' and populate it with sample data.  The table I have used
 * looks like this:
 *
 * CATEGORY | VALUE
 * ---------+------
 * London   |  54.3
 * New York |  43.4
 * Paris    |  17.9
 *
 * ...but you can use any data you like, as long as the SQL query you use returns two columns,
 * the first containing VARCHAR data and the second containing numerical data.
 *
 */
public class JDBCPieChartDemo extends ApplicationFrame {

    /**
     * Constructs the demo application.
     *
     * @param title The frame title.
     */
    public JDBCPieChartDemo(String title) {

        super(title);

        // read the data from the database...
        PieDataset data = readData();

        // create the chart...
        JFreeChart chart = ChartFactory.createPieChart("JDBC Pie Chart Demo", // chart title
                                                       data,                  // data
                                                       true                   // include legend
                                                       );

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.yellow);
        PiePlot plot = (PiePlot)chart.getPlot();
        plot.setSectionLabelType(PiePlot.NAME_AND_PERCENT_LABELS);
        plot.setNoDataMessage("No data available");

        // add the chart to a panel...
        ChartPanel chartPanel = new ChartPanel(chart);
        this.setContentPane(chartPanel);

    }

    /**
     * Reads the data from a table called 'PieData1' in the 'JFREECHARTDB' database.
     * <P>
     * You need to create this database and table before running the demo.  In the example
     * I have used the username 'jfreechart' and the password 'password' to access the
     * database.  Change these values to match your configuration.
     *
     * @return A dataset.
     */
    private PieDataset readData() {

        JdbcPieDataset data = null;

        String url = "jdbc:postgresql://nomad/jfreechartdb";
        Connection con;

        try {
            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException e) {
            System.err.print("ClassNotFoundException: ");
            System.err.println(e.getMessage());
        }

        try {
            con = DriverManager.getConnection(url, "jfreechart", "password");

            data = new JdbcPieDataset(con);
            String sql = "SELECT * FROM PIEDATA1;";
            data.executeQuery(sql);
            con.close();
        }

        catch (SQLException e) {
            System.err.print("SQLException: ");
            System.err.println(e.getMessage());
        }

        catch (Exception e) {
            System.err.print("Exception: ");
            System.err.println(e.getMessage());
        }

        return data;

    }

    /**
     * Starting point for the demo...
     */
    public static void main(String[] args) {

        JDBCPieChartDemo demo = new JDBCPieChartDemo("JDBC Pie Chart Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
