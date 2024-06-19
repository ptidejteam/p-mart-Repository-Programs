package org.jfree.chart.demo;

import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JScrollPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.DefaultCategoryDataset;

/*
* Created on 24/07/2003
*
* To change the template for this generated file go to
* Window&Preferences&Java&Code Generation&Code and Comments
*/

/**
* @author kenta
*
* To change the template for this generated type comment go to
* Window&Preferences&Java&Code Generation&Code and Comments
*/
public class BarChartTest extends JDialog {

    private String[] clones;
    private Integer[] clusters;

    private JScrollPane chartScrollPane;

    /* Os dados de entrada devem estar organizados da seguinte forma:
    data[i][0] - dados da base do gráfico (x)
    data[i][1] - dados da altura do gráfico (y)
    */
    public BarChartTest(int[][] data) {
        super();
        chartScrollPane = new JScrollPane();
        setData(data);

        criaGraficoPanel();
        getContentPane().add(this.chartScrollPane);
        pack();
    }

    // Cria o gráfico
    public void criaGraficoPanel() {
        DefaultCategoryDataset data = new DefaultCategoryDataset();

        for (int i = 0; i < clones.length; i++) {
            data.addValue(clusters[i], clones[i], "series");
        }

        // create the chart...
            JFreeChart chart = ChartFactory.createBarChart("Vertical Bar Chart", // chart title
        "Category", // domain axis label
        "Value", // range axis label
        data, // data
        PlotOrientation.VERTICAL, true, // include legend
    true, false);

        ChartPanel barChartPanel = new ChartPanel(chart);
        barChartPanel.setPreferredSize(
            new Dimension(this.clusters.length * 11, this.getHeight() / 2));
        chartScrollPane.setViewportView(barChartPanel);
    }

    public void setData(int[][] data) {
        int len = data.length;
        this.clones = new String[len];
        this.clusters = new Integer[len];

        for (int i = 0; i < data.length; i++) {
            this.clones[i] = String.valueOf(data[i][0]);
            this.clusters[i] = new Integer(data[i][1]);
        }
    }

    public static void main(String[] args) {
        int[][] data = { { 123, 56 }, {
                94, 67 }, {
                90, 34 }, {
                87, 24 }, {
                82, 35 }, {
                73, 42 }, {
                64, 87 }, {
                61, 3 }, {
                56, 23 }, {
                51, 64 }, {
                49, 54 }, {
                43, 98 }, {
                33, 14 }, {
                23, 16 }, {
                13, 6 }, };

        BarChartTest bc = new BarChartTest(data);
        bc.show();
    }
}
