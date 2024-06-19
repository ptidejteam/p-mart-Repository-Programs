/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: PlotPropertyEditPanel.java,v 1.1 2007/10/10 18:55:45 vauchers Exp $
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Andrzej Porebski;
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
 * Changes (from 24-Aug-2001)
 * --------------------------
 * 24-Aug-2001 : Added standard source header. Fixed DOS encoding problem (DG);
 * 07-Nov-2001 : Separated the JCommon Class Library classes, JFreeChart now requires
 *               jcommon.jar (DG);
 * 21-Nov-2001 : Allowed for null axes (DG);
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
 * A panel for editing the properties of a Plot.
 */
public class PlotPropertyEditPanel extends JPanel implements ActionListener {

    /** The paint (color) used to fill the background of the plot. */
    private PaintSample backgroundPaintSample;

    /** The stroke (pen) used to draw the outline of the plot. */
    private StrokeSample outlineStrokeSample;

    /** The paint (color) used to draw the outline of the plot. */
    private PaintSample outlinePaintSample;

    /** A panel used to display/edit the properties of the vertical axis belonging to the plot. */
    private AxisPropertyEditPanel verticalAxisPropertyPanel;

    /** A panel used to display/edit the properties of the horizontal axis belonging to the plot. */
    private AxisPropertyEditPanel horizontalAxisPropertyPanel;

    /** An array of stroke samples to choose from. */
    private StrokeSample[] availableStrokeSamples;

    /** The insets for the plot. */
    private Insets _insets;
    private InsetsTextField insetsTextField;

    /**
     * Standard constructor - constructs a panel for editing the properties of the specified plot.
     * <P>
     * In designing the panel, we need to be aware that subclasses of Plot will need to implement
     * subclasses of PlotPropertyEditPanel - so we need to leave one or two 'slots' where the
     * subclasses can extend the user interface.
     */
    public PlotPropertyEditPanel(Plot plot) {

        _insets = plot.getInsets();
        backgroundPaintSample = new PaintSample(plot.getBackgroundPaint());
        outlineStrokeSample = new StrokeSample(plot.getOutlineStroke());
        outlinePaintSample = new PaintSample(plot.getOutlinePaint());

        setLayout(new BorderLayout());

        availableStrokeSamples = new StrokeSample[3];
        availableStrokeSamples[0] = new StrokeSample(new BasicStroke(1.0f));
        availableStrokeSamples[1] = new StrokeSample(new BasicStroke(2.0f));
        availableStrokeSamples[2] = new StrokeSample(new BasicStroke(3.0f));

        // create a panel for the settings...
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                            BorderFactory.createEtchedBorder(), plot.getPlotType()+":"));

        JPanel general = new JPanel(new BorderLayout());
        general.setBorder(BorderFactory.createTitledBorder(
                              BorderFactory.createEtchedBorder(), "General:"));

        JPanel interior = new JPanel(new LCBLayout(4));
        interior.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        interior.add(new JLabel("Insets:"));
        JButton button = new JButton("Edit...");
        button.setActionCommand("Insets");
        button.addActionListener(this);

        insetsTextField = new InsetsTextField(_insets);
        insetsTextField.setEnabled(false);
        interior.add(insetsTextField);
        interior.add(button);

        interior.add(new JLabel("Outline stroke:"));
        button = new JButton("Select...");
        button.setActionCommand("OutlineStroke");
        button.addActionListener(this);
        interior.add(outlineStrokeSample);
        interior.add(button);

        interior.add(new JLabel("Outline paint:"));
        button = new JButton("Select...");
        button.setActionCommand("OutlinePaint");
        button.addActionListener(this);
        interior.add(outlinePaintSample);
        interior.add(button);

        interior.add(new JLabel("Background paint:"));
        button = new JButton("Select...");
        button.setActionCommand("BackgroundPaint");
        button.addActionListener(this);
        interior.add(backgroundPaintSample);
        interior.add(button);

        general.add(interior, BorderLayout.NORTH);

        JPanel appearance = new JPanel(new BorderLayout());
        appearance.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        appearance.add(general, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        verticalAxisPropertyPanel = AxisPropertyEditPanel.getInstance(plot.getAxis(Plot.VERTICAL_AXIS));
        if (verticalAxisPropertyPanel!=null) {
            verticalAxisPropertyPanel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
            tabs.add("Vertical Axis", verticalAxisPropertyPanel);
        }
        horizontalAxisPropertyPanel = AxisPropertyEditPanel.getInstance(plot.getAxis(Plot.HORIZONTAL_AXIS));
        if (horizontalAxisPropertyPanel!=null) {
            horizontalAxisPropertyPanel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
            tabs.add("Horizontal Axis", horizontalAxisPropertyPanel);
        }
        tabs.add("Appearance", appearance);
        panel.add(tabs);

        add(panel);
    }

    /**
     * Returns the current plot insets.
     */
    public Insets getPlotInsets() {
        if (_insets == null)
            _insets = new Insets(0,0,0,0);
        return _insets;
    }

    /**
     * Returns the current background paint.
     */
    public Paint getBackgroundPaint() {
        return backgroundPaintSample.getPaint();
    }

    /**
     * Returns the current outline stroke.
     */
    public Stroke getOutlineStroke() {
        return outlineStrokeSample.getStroke();
    }

    /**
     * Returns the current outline paint.
     */
    public Paint getOutlinePaint() {
        return outlinePaintSample.getPaint();
    }

    /**
     * Returns a reference to the panel for editing the properties of the vertical axis.
     */
    public AxisPropertyEditPanel getVerticalAxisPropertyEditPanel() {
        return verticalAxisPropertyPanel;
    }

    /**
     * Returns a reference to the panel for editing the properties of the horizontal axis.
     */
    public AxisPropertyEditPanel getHorizontalAxisPropertyEditPanel() {
        return horizontalAxisPropertyPanel;
    }

    /**
     * Handles user actions generated within the panel.
     */
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("BackgroundPaint")) {
            attemptBackgroundPaintSelection();
        }
        else if (command.equals("OutlineStroke")) {
            attemptOutlineStrokeSelection();
        }
        else if (command.equals("OutlinePaint")) {
            attemptOutlinePaintSelection();
        }
        else if (command.equals("Insets")) {
            editInsets();
        }

    }

    /**
     * Allow the user to change the background paint.
     */
    private void attemptBackgroundPaintSelection() {
        Color c;
        c = JColorChooser.showDialog(this, "Background Color", Color.blue);
        if (c!=null) {
            backgroundPaintSample.setPaint(c);
        }
    }

    /**
     * Allow the user to change the outline stroke.
     */
    private void attemptOutlineStrokeSelection() {
        StrokeChooserPanel panel = new StrokeChooserPanel(null, availableStrokeSamples);
        int result = JOptionPane.showConfirmDialog(this, panel, "Stroke Selection",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result==JOptionPane.OK_OPTION) {
            outlineStrokeSample.setStroke(panel.getSelectedStroke());
        }
    }

    /**
     * Allow the user to change the outline paint.  We use JColorChooser, so the user can only
     * choose colors (a subset of all possible paints).
     */
    private void attemptOutlinePaintSelection() {
        Color c;
        c = JColorChooser.showDialog(this, "Outline Color", Color.blue);
        if (c!=null) {
            outlinePaintSample.setPaint(c);
        }
    }

    /**
     * Allow the user to edit the individual insets' values.
     */
    private void editInsets() {
        InsetsChooserPanel panel = new InsetsChooserPanel(_insets);
        int result =
            JOptionPane.showConfirmDialog(this, panel, "Edit Insets",
                                          JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result==JOptionPane.OK_OPTION) {
            _insets = panel.getInsets();
            insetsTextField.setInsets(_insets);
        }

    }

    /**
     * Updates the plot properties to match the properties defined on the panel.
     * @param plot The plot.
     */
    public void updatePlotProperties(Plot plot) {

        // set the plot properties...
        plot.setOutlinePaint(this.getOutlinePaint());
        plot.setOutlineStroke(this.getOutlineStroke());
        plot.setBackgroundPaint(this.getBackgroundPaint());
        plot.setInsets(this.getPlotInsets());

        // then the axis properties...
        if (this.horizontalAxisPropertyPanel!=null) {
            this.horizontalAxisPropertyPanel.setAxisProperties(plot.getAxis(Plot.HORIZONTAL_AXIS));
        }

        if (this.verticalAxisPropertyPanel!=null) {
            this.verticalAxisPropertyPanel.setAxisProperties(plot.getAxis(Plot.VERTICAL_AXIS));
        }

    }

}
