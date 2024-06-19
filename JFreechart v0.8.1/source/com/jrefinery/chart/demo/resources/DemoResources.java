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
 * ------------------
 * DemoResources.java
 * ------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: DemoResources.java,v 1.1 2007/10/10 19:02:40 vauchers Exp $
 *
 * Changes
 * -------
 * 15-Mar-2002 : Version 1 (DG);
 * 26-Mar-2002 : Changed name from JFreeChartDemoResources.java --> DemoResources.java (DG);
 *
 */
package com.jrefinery.chart.demo.resources;

import java.util.ListResourceBundle;

/**
 * A resource bundle that stores all the user interface items that might need localisation.
 */
public class DemoResources extends ListResourceBundle {

    /**
     * Returns the array of strings in the resource bundle.
     */
    public Object[][] getContents() {
        return contents;
    }

    /** The resources to be localised. */
    static final Object[][] contents = {

        // about frame...
        { "about.title", "About..."},
        { "about.version.label", "Version"},

        // menu labels...
        { "menu.file", "File"},
        { "menu.file.mnemonic", new Character('F') },

        { "menu.file.exit", "Exit"},
        { "menu.file.exit.mnemonic", new Character('x') },

        { "menu.help", "Help"},
        { "menu.help.mnemonic", new Character('H')},

        { "menu.help.about", "About..."},
        { "menu.help.about.mnemonic", new Character('A')},

        // dialog messages...
        { "dialog.exit.title", "Confirm exit..."},
        { "dialog.exit.message", "Are you sure you want to exit?"},

        // labels for the tabs in the main window...
        { "tab.bar",      "Bar Charts"},
        { "tab.pie",      "Pie Charts"},
        {"tab.xy",       "XY Charts"},
        {"tab.time",     "Time Series Charts"},
        {"tab.other",    "Other Charts"},
        {"tab.test",     "Test Charts"},
        {"tab.combined", "Combined Charts"},

        // sample chart descriptions...
        {"chart1.title",       "Horizontal Bar Chart: "},
        {"chart1.description", "Displays horizontal bars, representing data from a "
                              +"CategoryDataset.  Notice that the numerical axis is inverted."},

        {"chart2.title",       "Horizontal Stacked Bar Chart: "},
        {"chart2.description", "Displays horizontal stacked bars, representing data from a "
                              +"CategoryDataset."},

        {"chart3.title",       "Vertical Bar Chart: "},
        {"chart3.description", "Displays vertical bars, representing data from a CategoryDataset."},

        {"chart4.title",       "Vertical 3D Bar Chart: "},
        {"chart4.description", "Displays vertical bars with a 3D effect, representing data from a "
                              +"CategoryDataset."},

        {"chart5.title",       "Vertical Stacked Bar Chart: "},
        {"chart5.description", "Displays vertical stacked bars, representing data from a "
                              +"CategoryDataset."},

        {"chart6.title",       "Vertical Stacked 3D Bar Chart: "},
        {"chart6.description", "Displays vertical stacked bars with a 3D effect, representing "
                              +"data from a CategoryDataset."},

        {"chart7.title",       "Pie Chart 1: "},
        {"chart7.description", "A pie chart showing one section exploded."},

        {"chart8.title",       "Pie Chart 2: "},
        {"chart8.description", "A pie chart showing percentages on the category labels.  Also, "
                              +"this plot has a background image."},

        {"chart9.title",       "XY Plot: "},
        {"chart9.description", "A line chart using data from an XYDataset.  Both axes are "
                              +"numerical."},

        {"chart10.title",       "Time Series 1: "},
        {"chart10.description", "A time series chart, representing data from an XYDataset.  This "
                               +"chart also demonstrates the use of multiple chart titles."},

        {"chart11.title",       "Time Series 2: "},
        {"chart11.description", "A time series chart, representing data from an XYDataset.  The "
                               +"vertical axis has a logarithmic scale."},

        {"chart12.title",       "Time Series 3: "},
        {"chart12.description", "A time series chart with a moving average."},

        {"chart13.title",       "High/Low/Open/Close Chart: "},
        {"chart13.description", "A high/low/open/close chart based on data in a HighLowDataset."},

        {"chart14.title",       "Candlestick Chart: "},
        {"chart14.description", "A candlestick chart based on data in a HighLowDataset."},

        {"chart15.title",       "Signal Chart: "},
        {"chart15.description", "A signal chart based on data in a SignalDataset."},

        {"chart16.title",       "Wind Plot: "},
        {"chart16.description", "A wind plot, represents wind direction and intensity (supplied "
                               +"via a WindDataset)."},

        {"chart17.title",       "Scatter Plot: "},
        {"chart17.description", "A scatter plot, representing data in an XYDataset."},

        {"chart18.title",       "Line Chart: "},
        {"chart18.description", "A chart displaying lines and or shapes, representing data in a "
                               +"CategoryDataset.  This plot also illustrates the use of a "
                               +"background image on the chart, and alpha-transparency on the "
                               +"plot."},

        {"chart19.title",       "Vertical XY Bar Chart: "},
        {"chart19.description", "A chart showing vertical bars, based on data in an "
                               +"IntervalXYDataset."},

        {"chart20.title",       "Null Data: "},
        {"chart20.description", "A chart with a null dataset."},

        {"chart21.title",       "Zero Data: "},
        {"chart21.description", "A chart with a dataset containing zero series."},

        {"chart22.title",       "Chart in JScrollPane: "},
        {"chart22.description", "A chart embedded in a JScrollPane."},

        {"chart23.title",       "Single Series Bar Chart: "},
        {"chart23.description", "A single series bar chart.  This chart also illustrates the use "
                               +"of a border around a JFreeChartPanel."},

        {"chart24.title",       "Dynamic Chart: "},
        {"chart24.description", "A dynamic chart, to test the event notification mechanism."},

        {"chart25.title",       "Overlaid Chart: "},
        {"chart25.description", "Displays an overlaid chart with high/low/open/close and moving "
                               +"average plots."},

        {"chart26.title",       "Horizontally Combined Chart: "},
        {"chart26.description", "Displays a horizontally combined chart of time series and XY bar "
                               +"plots."},

        {"chart27.title",       "Vertically Combined Chart: "},
        {"chart27.description", "Displays a vertically combined chart of XY, TimeSeries and "
                               +"VerticalXYBar plots."},

        {"chart28.title",       "Combined and Overlaid Chart: "},
        {"chart28.description", "A combined chart of a XY, overlaid TimeSeries and an overlaid "
                               +"HighLow & TimeSeries plots."},

        {"chart29.title",       "Combined and Overlaid Dynamic Chart: "},
        {"chart29.description", "Displays a dynamic combined and  overlaid chart, to test the "
                               +"event notification mechanism."},

        {"charts.display", "Display"},

        // chart titles and labels...
        {"bar.horizontal.title",  "Horizontal Bar Chart"},
        {"bar.horizontal.domain", "Categories"},
        {"bar.horizontal.range",  "Value"},

        {"bar.horizontal-stacked.title",  "Horizontal Stacked Bar Chart"},
        {"bar.horizontal-stacked.domain", "Categories"},
        {"bar.horizontal-stacked.range",  "Value"},

        {"bar.vertical.title",  "Vertical Bar Chart"},
        {"bar.vertical.domain", "Categories"},
        {"bar.vertical.range",  "Value"},

        {"bar.vertical3D.title",  "Vertical 3D Bar Chart"},
        {"bar.vertical3D.domain", "Categories"},
        {"bar.vertical3D.range",  "Value"},

        {"bar.vertical-stacked.title",  "Vertical Stacked Bar Chart"},
        {"bar.vertical-stacked.domain", "Categories"},
        {"bar.vertical-stacked.range",  "Value"},

        {"bar.vertical-stacked3D.title",  "Vertical Stacked 3D Bar Chart"},
        {"bar.vertical-stacked3D.domain", "Categories"},
        {"bar.vertical-stacked3D.range",  "Value"},

        {"pie.pie1.title", "Pie Chart 1"},

        {"pie.pie2.title", "Pie Chart 2"},

        {"xyplot.sample1.title",  "XY Plot"},
        {"xyplot.sample1.domain", "X Values"},
        {"xyplot.sample1.range",  "Y Values"},

        {"timeseries.sample1.title",     "Time Series Chart 1"},
        {"timeseries.sample1.subtitle",  "Value of GBP in JPY"},
        {"timeseries.sample1.domain",    "Date"},
        {"timeseries.sample1.range",     "CCY per GBP"},
        {"timeseries.sample1.copyright", "(C)opyright 2002, by Simba Management Limited"},

        {"timeseries.sample2.title",    "Time Series Chart 2"},
        {"timeseries.sample2.domain",   "Millisecond"},
        {"timeseries.sample2.range",    "Log Axis"},
        {"timeseries.sample2.subtitle", "Milliseconds"},

        {"timeseries.sample3.title",    "Time Series Chart with Moving Average"},
        {"timeseries.sample3.domain",   "Date"},
        {"timeseries.sample3.range",    "CCY per GBP"},
        {"timeseries.sample3.subtitle", "30 day moving average of GBP"},

        {"timeseries.highlow.title",    "High/Low/Open/Close Chart"},
        {"timeseries.highlow.domain",   "Date"},
        {"timeseries.highlow.range",    "Price ($ per share)"},
        {"timeseries.highlow.subtitle", "IBM Stock Price"},

        {"timeseries.candlestick.title",    "CandleStick Chart"},
        {"timeseries.candlestick.domain",   "Date"},
        {"timeseries.candlestick.range",    "Price ($ per share)"},
        {"timeseries.candlestick.subtitle", "IBM Stock Price"},

        {"timeseries.signal.title",    "Signal Chart"},
        {"timeseries.signal.domain",   "Date"},
        {"timeseries.signal.range",    "Price ($ per share)"},
        {"timeseries.signal.subtitle", "IBM Stock Price"},

        {"other.wind.title",  "Wind Plot"},
        {"other.wind.domain", "X-Axis"},
        {"other.wind.range",  "Y-Axis"},

        {"other.scatter.title",  "Scatter Plot"},
        {"other.scatter.domain", "X-Axis"},
        {"other.scatter.range",  "Y-Axis"},

        {"other.line.title",  "Line Plot"},
        {"other.line.domain", "Category"},
        {"other.line.range",  "Value"},

        {"other.xybar.title",  "Time Series Bar Chart"},
        {"other.xybar.domain", "Date"},
        {"other.xybar.range",  "Value"},

        {"test.null.title",  "XY Plot (null data)"},
        {"test.null.domain", "X"},
        {"test.null.range",  "Y"},

        {"test.zero.title",  "XY Plot (zero data)"},
        {"test.zero.domain", "X axis"},
        {"test.zero.range",  "Y axis"},

        {"test.scroll.title",    "Time Series"},
        {"test.scroll.subtitle", "Value of GBP"},
        {"test.scroll.domain",   "Date"},
        {"test.scroll.range",    "Value"},

        {"test.single.title",     "Single Series Bar Chart"},
        {"test.single.subtitle1", "Subtitle 1"},
        {"test.single.subtitle2", "Subtitle 2"},
        {"test.single.domain",    "Date"},
        {"test.single.range",     "Value"},

        {"test.dynamic.title",  "Dynamic Chart"},
        {"test.dynamic.domain", "Domain"},
        {"test.dynamic.range",  "Range"},

        {"combined.overlaid.title",     "Overlaid Chart"},
        {"combined.overlaid.subtitle",  "High/Low/Open/Close plus Moving Average"},
        {"combined.overlaid.domain",    "Date" },
        {"combined.overlaid.range",     "IBM"},

        {"combined.horizontal.title",     "Horizontal Combined Chart"},
        {"combined.horizontal.subtitle",  "Time Series and XY Bar Charts"},
        {"combined.horizontal.domains",   new String[] {"Date 1", "Date 2", "Date 3"} },
        {"combined.horizontal.range",     "CCY per GBP"},

        {"combined.vertical.title",     "Vertical Combined Chart"},
        {"combined.vertical.subtitle",  "Four charts in one"},
        {"combined.vertical.domain",    "Date"},
        {"combined.vertical.ranges",    new String[] {"CCY per GBP", "Pounds", "IBM", "Bars"} },

        {"combined.combined-overlaid.title",     "Combined and Overlaid Chart"},
        {"combined.combined-overlaid.subtitle",  "XY, Overlaid (two TimeSeries) and Overlaid "
                                                +"(HighLow and TimeSeries)"},
        {"combined.combined-overlaid.domain",    "Date"},
        {"combined.combined-overlaid.ranges",    new String[] {"CCY per GBP", "Pounds", "IBM"} },

        {"combined.dynamic.title",     "Dynamic Combined Chart"},
        {"combined.dynamic.subtitle",  "XY (series 0), XY (series 1), Overlaid (both series) "
                                      +"and XY (both series)"},
        {"combined.dynamic.domain",    "X" },
        {"combined.dynamic.ranges",    new String[] {"Y1", "Y2", "Y3", "Y4"} },

    };

}