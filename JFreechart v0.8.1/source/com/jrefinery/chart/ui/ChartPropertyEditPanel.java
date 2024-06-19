/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * This file...
 * $Id: ChartPropertyEditPanel.java,v 1.1 2007/10/10 19:02:39 vauchers Exp $
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 * (C) Copyright 2000, 2001, Simba Management Limited;
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307, USA.
 *
 * Changes (from 22-Jun-2001)
 * --------------------------
 * 22-Jun-2001 : Disabled title panel, as it doesn't support the new title code (DG);
 * 24-Aug-2001 : Fixed DOS encoding problem (DG);
 * 07-Nov-2001 : Separated the JCommon Class Library classes, JFreeChart now requires
 *               jcommon.jar (DG);
 * 21-Nov-2001 : Allowed for null legend (DG);
 *
 */

package com.jrefinery.chart.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jrefinery.chart.*;
import com.jrefinery.layout.*;
import com.jrefinery.ui.*;

/**
 * A panel for editing chart properties (includes subpanels for the title, legend and plot).
 */
public class ChartPropertyEditPanel extends JPanel implements ActionListener {

    /** A panel for displaying/editing the properties of the title. */
    TitlePropertyEditPanel titlePropertiesPanel;

    /** A panel for displaying/editing the properties of the legend. */
    LegendPropertyEditPanel legendPropertiesPanel;

    /** A panel for displaying/editing the properties of the plot. */
    PlotPropertyEditPanel plotPropertiesPanel;

    /** A checkbox indicating whether or not the chart is drawn with anti-aliasing. */
    JCheckBox antialias;

    /** The chart background color. */
    PaintSample background;

    /**
     * Standard constructor - the property panel is made up of a number of sub-panels that are
     * displayed in the tabbed pane.
     */
    public ChartPropertyEditPanel(JFreeChart chart) {
        setLayout(new BorderLayout());

        JPanel other = new JPanel(new BorderLayout());
        other.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        JPanel general = new JPanel(new BorderLayout());
        general.setBorder(BorderFactory.createTitledBorder(
                              BorderFactory.createEtchedBorder(), "General:"));

        JPanel interior = new JPanel(new LCBLayout(6));
        interior.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        antialias = new JCheckBox("Draw anti-aliased");
        antialias.setSelected(chart.getAntiAlias());
        interior.add(antialias);
        interior.add(new JLabel(""));
        interior.add(new JLabel(""));
        interior.add(new JLabel("Background paint:"));
        background = new PaintSample(chart.getBackgroundPaint());
        interior.add(background);
        JButton button = new JButton("Select...");
        button.setActionCommand("BackgroundPaint");
        button.addActionListener(this);
        interior.add(button);

        interior.add(new JLabel("Series Paint:"));
        JTextField info = new JTextField("No editor implemented");
        info.setEnabled(false);
        interior.add(info);
        button = new JButton("Edit...");
        button.setEnabled(false);
        interior.add(button);

        interior.add(new JLabel("Series Stroke:"));
        info = new JTextField("No editor implemented");
        info.setEnabled(false);
        interior.add(info);
        button = new JButton("Edit...");
        button.setEnabled(false);
        interior.add(button);

        interior.add(new JLabel("Series Outline Paint:"));
        info = new JTextField("No editor implemented");
        info.setEnabled(false);
        interior.add(info);
        button = new JButton("Edit...");
        button.setEnabled(false);
        interior.add(button);

        interior.add(new JLabel("Series Outline Stroke:"));
        info = new JTextField("No editor implemented");
        info.setEnabled(false);
        interior.add(info);
        button = new JButton("Edit...");
        button.setEnabled(false);
        interior.add(button);

        general.add(interior, BorderLayout.NORTH);
        other.add(general, BorderLayout.NORTH);

        JPanel parts = new JPanel(new BorderLayout());

        //Title title = chart.getTitle();
        Legend legend = chart.getLegend();
        Plot plot = chart.getPlot();

        JTabbedPane tabs = new JTabbedPane();

        //StandardTitle t = (StandardTitle)title;
        //titlePropertiesPanel = new TitlePropertyEditPanel(t);
        //titlePropertiesPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        //tabs.addTab("Title", titlePropertiesPanel);

        if (legend!=null) {
            legendPropertiesPanel = new LegendPropertyEditPanel(legend);
            legendPropertiesPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            tabs.addTab("Legend", legendPropertiesPanel);
        }

        plotPropertiesPanel = new PlotPropertyEditPanel(plot);
        plotPropertiesPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        tabs.addTab("Plot", plotPropertiesPanel);

        tabs.add("Other", other);
        parts.add(tabs, BorderLayout.NORTH);
        add(parts);
    }

    /**
     * Returns a reference to the title property sub-panel.
     */
    public TitlePropertyEditPanel getTitlePropertyEditPanel() {
        return titlePropertiesPanel;
    }

    /**
     * Returns a reference to the legend property sub-panel.
     */
    public LegendPropertyEditPanel getLegendPropertyEditPanel() {
        return legendPropertiesPanel;
    }

    /**
     * Returns a reference to the plot property sub-panel.
     */
    public PlotPropertyEditPanel getPlotPropertyEditPanel() {
        return plotPropertiesPanel;
    }

    /**
     * Returns the current setting of the anti-alias flag.
     */
    public boolean getAntiAlias() {
        return antialias.isSelected();
    }

    /**
     * Returns the current background paint.
     */
    public Paint getBackgroundPaint() {
        return background.getPaint();
    }

    /**
     * Handles user interactions with the panel.
     */
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("BackgroundPaint")) {
            attemptModifyBackgroundPaint();
        }
    }

    /**
     * Allows the user the opportunity to select a new background paint.  Uses JColorChooser,
     * so we are only allowing a subset of all Paint objects to be selected (fix later).
     */
    private void attemptModifyBackgroundPaint() {
        Color c;
        c = JColorChooser.showDialog(this, "Background Color", Color.blue);
        if (c!=null) {
            background.setPaint(c);
        }
    }

    /**
     * Updates the properties of a chart to match the properties defined on the panel.
     * @param chart The chart.
     */
    public void updateChartProperties(JFreeChart chart) {

        if (legendPropertiesPanel!=null) {
            legendPropertiesPanel.setLegendProperties(chart.getLegend());
        }

        plotPropertiesPanel.updatePlotProperties(chart.getPlot());

        chart.setAntiAlias(this.getAntiAlias());
        chart.setBackgroundPaint(this.getBackgroundPaint());
    }

}
