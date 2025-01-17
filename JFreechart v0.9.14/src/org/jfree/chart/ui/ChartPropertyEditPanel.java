/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Object Refinery Limited;
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
 * ---------------------------
 * ChartPropertyEditPanel.java
 * ---------------------------
 * (C) Copyright 2000-2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Arnaud Lelievre;
 *
 * $Id: ChartPropertyEditPanel.java,v 1.1 2007/10/10 19:19:05 vauchers Exp $
 *
 * Changes (from 22-Jun-2001)
 * --------------------------
 * 22-Jun-2001 : Disabled title panel, as it doesn't support the new title code (DG);
 * 24-Aug-2001 : Fixed DOS encoding problem (DG);
 * 07-Nov-2001 : Separated the JCommon Class Library classes, JFreeChart now requires
 *               jcommon.jar (DG);
 * 21-Nov-2001 : Allowed for null legend (DG);
 * 17-Jan-2003 : Moved plot classes into separate package (DG);
 * 20-May-2003 : Removed titlePanel until it is implemented. (TM);
 * 08-Sep-2003 : Added internationalization via use of properties resourceBundle (RFE 690236) (AL); 
 */

package org.jfree.chart.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.Legend;
import org.jfree.chart.plot.Plot;
import org.jfree.layout.LCBLayout;
import org.jfree.ui.PaintSample;

/**
 * A panel for editing chart properties (includes subpanels for the title,
 * legend and plot).
 *
 * @author David Gilbert
 */
public class ChartPropertyEditPanel extends JPanel implements ActionListener {

    /** A panel for displaying/editing the properties of the title. */
    //private TitlePropertyEditPanel titlePropertiesPanel;

    /** A panel for displaying/editing the properties of the legend. */
    private LegendPropertyEditPanel legendPropertiesPanel;

    /** A panel for displaying/editing the properties of the plot. */
    private PlotPropertyEditPanel plotPropertiesPanel;

    /** A checkbox indicating whether or not the chart is drawn with
     *  anti-aliasing.
     */
    private JCheckBox antialias;

    /** The chart background color. */
    private PaintSample background;

    /** The resourceBundle for the localization. */
    static protected ResourceBundle localizationResources = 
                            ResourceBundle.getBundle("org.jfree.chart.ui.LocalizationBundle");

    /**
     * Standard constructor - the property panel is made up of a number of
     * sub-panels that are displayed in the tabbed pane.
     *
     * @param chart  the chart, whichs properties should be changed.
     */
    public ChartPropertyEditPanel(JFreeChart chart) {
        setLayout(new BorderLayout());

        JPanel other = new JPanel(new BorderLayout());
        other.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        JPanel general = new JPanel(new BorderLayout());
        general.setBorder(BorderFactory.createTitledBorder(
                              BorderFactory.createEtchedBorder(), 
                              localizationResources.getString("General")));

        JPanel interior = new JPanel(new LCBLayout(6));
        interior.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        antialias = new JCheckBox(localizationResources.getString("Draw_anti-aliased"));
        antialias.setSelected(chart.getAntiAlias());
        interior.add(antialias);
        interior.add(new JLabel(""));
        interior.add(new JLabel(""));
        interior.add(new JLabel(localizationResources.getString("Background_paint")));
        background = new PaintSample(chart.getBackgroundPaint());
        interior.add(background);
        JButton button = new JButton(localizationResources.getString("Select..."));
        button.setActionCommand("BackgroundPaint");
        button.addActionListener(this);
        interior.add(button);

        interior.add(new JLabel(localizationResources.getString("Series_Paint")));
        JTextField info = new JTextField(localizationResources.getString("No_editor_implemented"));
        info.setEnabled(false);
        interior.add(info);
        button = new JButton(localizationResources.getString("Edit..."));
        button.setEnabled(false);
        interior.add(button);

        interior.add(new JLabel(localizationResources.getString("Series_Stroke")));
        info = new JTextField(localizationResources.getString("No_editor_implemented"));
        info.setEnabled(false);
        interior.add(info);
        button = new JButton(localizationResources.getString("Edit..."));
        button.setEnabled(false);
        interior.add(button);

        interior.add(new JLabel(localizationResources.getString("Series_Outline_Paint")));
        info = new JTextField(localizationResources.getString("No_editor_implemented"));
        info.setEnabled(false);
        interior.add(info);
        button = new JButton(localizationResources.getString("Edit..."));
        button.setEnabled(false);
        interior.add(button);

        interior.add(new JLabel(localizationResources.getString("Series_Outline_Stroke")));
        info = new JTextField(localizationResources.getString("No_editor_implemented"));
        info.setEnabled(false);
        interior.add(info);
        button = new JButton(localizationResources.getString("Edit..."));
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

        if (legend != null) {
            legendPropertiesPanel = new LegendPropertyEditPanel(legend);
            legendPropertiesPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            tabs.addTab(localizationResources.getString("Legend"), legendPropertiesPanel);
        }

        plotPropertiesPanel = new PlotPropertyEditPanel(plot);
        plotPropertiesPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        tabs.addTab(localizationResources.getString("Plot"), plotPropertiesPanel);

        tabs.add(localizationResources.getString("Other"), other);
        parts.add(tabs, BorderLayout.NORTH);
        add(parts);
    }

    /**
     * Returns a reference to the title property sub-panel.
     *
     * @return a reference to the title property sub-panel.
     * @deprecated returns null, until the title panel is implemented properly.
     */
    public TitlePropertyEditPanel getTitlePropertyEditPanel() {
        //return titlePropertiesPanel;
      return null;
    }

    /**
     * Returns a reference to the legend property sub-panel.
     *
     * @return a reference to the legend property sub-panel.
     */
    public LegendPropertyEditPanel getLegendPropertyEditPanel() {
        return legendPropertiesPanel;
    }

    /**
     * Returns a reference to the plot property sub-panel.
     *
     * @return a reference to the plot property sub-panel.
     */
    public PlotPropertyEditPanel getPlotPropertyEditPanel() {
        return plotPropertiesPanel;
    }

    /**
     * Returns the current setting of the anti-alias flag.
     *
     * @return <code>true</code> if anti-aliasing is enabled.
     */
    public boolean getAntiAlias() {
        return antialias.isSelected();
    }

    /**
     * Returns the current background paint.
     *
     * @return the current background paint.
     */
    public Paint getBackgroundPaint() {
        return background.getPaint();
    }

    /**
     * Handles user interactions with the panel.
     *
     * @param event  a BackgroundPaint action.
     */
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("BackgroundPaint")) {
            attemptModifyBackgroundPaint();
        }
    }

    /**
     * Allows the user the opportunity to select a new background paint.  Uses
     * JColorChooser, so we are only allowing a subset of all Paint objects to
     * be selected (fix later).
     */
    private void attemptModifyBackgroundPaint() {
        Color c;
        c = JColorChooser.showDialog(this, localizationResources.getString("Background_Color"),
                                     Color.blue);
        if (c != null) {
            background.setPaint(c);
        }
    }

    /**
     * Updates the properties of a chart to match the properties defined on the
     * panel.
     *
     * @param chart  the chart.
     */
    public void updateChartProperties(JFreeChart chart) {

        if (legendPropertiesPanel != null) {
            legendPropertiesPanel.setLegendProperties(chart.getLegend());
        }

        plotPropertiesPanel.updatePlotProperties(chart.getPlot());

        chart.setAntiAlias(getAntiAlias());
        chart.setBackgroundPaint(getBackgroundPaint());
    }

}
