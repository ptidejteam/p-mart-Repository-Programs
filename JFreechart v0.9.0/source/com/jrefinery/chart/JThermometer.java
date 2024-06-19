package com.jrefinery.chart;

import java.awt.*;
import java.text.*;
import javax.swing.*;
import com.jrefinery.data.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>@copyright Copyright (c) 2002</p>
 * <p>@company Australian Antarctic Division</p>
 * @author unascribed
 * @version 1.0
 *
 * An initial quick and dirty.  The concept behind this class would be to generate a gui bean
 * that could be used within JBuilder, Netbeans etc...
 */

public class JThermometer extends JPanel {

    DefaultMeterDataset data ;
    JFreeChart chart ;
    ChartPanel panel ;
    ThermometerPlot plot = new ThermometerPlot();

    public JThermometer() {
        super(new CardLayout());
        plot.setInsets(new Insets(5, 5, 5, 5));
        data = new DefaultMeterDataset();
        data.setRange(new Double(-60000), new Double(60000));
        plot.setData(data);
        chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        panel = new ChartPanel(chart);
        this.add(panel,"Panel");
        setBackground(getBackground());
    }

    public void addTitle(AbstractTitle title) {
        chart.addTitle(title);
    }

    public void addTitle(String title) {
        chart.addTitle(new TextTitle(title));
    }

    public void addTitle(String title, Font font) {
        chart.addTitle(new TextTitle(title, font));
    }

    public void setValueFormat(DecimalFormat df) {
        plot.setValueFormat(df);
    }

    public void setRangeInfo(int range, double display_low, double display_hi) {
        plot.setRangeInfo(range, display_low, display_hi);
    }

    public void setRangeInfo(int range, double range_low, double range_hi,
        double display_low, double display_hi) {
        plot.setRangeInfo(range, range_low, range_hi, display_low, display_hi);
    }

    public void setDisplayLocation(int loc) {
        plot.setDisplayLocation(loc);
        panel.repaint();
    }

    public void setValue(double value) {
        setValue(new Double(value));
    }

    public void setValue(Number value) {
        if (data != null) {
            data.setValue(value);
        }
    }

    public Number getValue() {
        if (data != null) {
            return data.getValue();
        }
        else {
            return null ;
        }
    }

    public void setUnits(int i) {
        if (plot != null)
            plot.setUnits(i);
    }

    public void setOutlinePaint(Paint p) {
        if (plot != null)
            plot.setOutlinePaint(p);
    }

    public void setForeground(Color fg) {
        super.setForeground(fg);
        if (plot != null)
            plot.setThermometerColor(fg);
    }

    public void setBackground(Color bg) {
        super.setBackground(bg);
        if (plot != null)
            plot.setBackgroundPaint(bg);

        if (chart != null)
            chart.setBackgroundPaint(bg);

        if (panel != null)
            panel.setBackground(bg);
    }

    public void setFont(Font f) {
        if (plot != null) {
            plot.setFont(f);
        }
    }

    public void setValueFont(Font f) {
        if (plot != null)
            plot.setValueFont(f);
    }

    public void setFollowData(boolean v) {
        plot.setFollowData(v);
    }

    public void setShowValueLines(boolean b) {
        plot.setShowValueLines(b);
    }

}
