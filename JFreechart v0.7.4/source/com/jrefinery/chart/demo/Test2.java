package com.jrefinery.chart.demo;

import java.util.*;
import java.text.*;
import java.awt.*;
import com.jrefinery.chart.*;
import com.jrefinery.data.*;

public class Test2 {

  public JFreeChart getVerticalStackedBarChart() {

      Paint seriesPaint[] = { Color.green, Color.red, Color.yellow };

      // create a default chart based on some sample data...
      String title = "Uptime";
      String categoryAxisLabel = "";
      String valueAxisLabel = "";

      CategoryDataset data = createCategoryDataset();

      JFreeChart chart = ChartFactory.createStackedVerticalBarChart(title, categoryAxisLabel, valueAxisLabel, data, true);

      chart.setBackgroundPaint(new Color(16776424));

      TextTitle chartTitle = (TextTitle)chart.getTitle(0);
      chartTitle.setFont(new Font(null, Font.BOLD, 14));

      // then customise it a little...
      Plot plot = chart.getPlot();
      plot.setSeriesPaint(seriesPaint);

      VerticalNumberAxis yAxis = (VerticalNumberAxis)plot.getAxis(Axis.VERTICAL);
      //yAxis.setMaximumAxisValue(1.0);
      //yAxis.setTickLabelsVisible(false);

      BarPlot barPlot = (BarPlot)plot;
      barPlot.setCategoryGapsPercent(0);
      barPlot.setIntroGapPercent(0);
      barPlot.setTrailGapPercent(0);

      return chart;

  }

  private CategoryDataset createCategoryDataset() {

      String seriesName[] = { "Up", "Down", "Partial" };

      Number[][] data = new Integer[][]
            { { new Integer(10) },
              { new Integer(5) },
              { new Integer(6) } };

      DefaultCategoryDataset dataSet = new DefaultCategoryDataset(data);
      //dataSet.setSeriesNames(seriesName);

//      dataSet.setCategories(constructDateList(data[1].length));

      return dataSet;

  }

  private Object[] constructDateList(int numMeasurements) {

     Object[] result = new Object[numMeasurements];
     Date now = new Date();
     for (int i = 0; i < numMeasurements; i++)
     {
        String date = calcDateString(now, i);
        result[i]=date;
     }
     return result;

  }

  private String calcDateString(Date now, int i) {

      SimpleDateFormat formatter = new SimpleDateFormat("H:mm");
      Date date = new Date(now.getTime() + (i * 5 * 60 * 1000));
      return formatter.format(date);

  }

  public static void main(String args[]) throws Throwable {

     JFreeChart chart = new Test2().getVerticalStackedBarChart();
     JFreeChartFrame frame = new JFreeChartFrame("Title", chart);
     frame.pack();
     frame.setVisible(true);
  }

}