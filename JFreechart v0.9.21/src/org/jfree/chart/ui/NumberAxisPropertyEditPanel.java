/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * --------------------------------
 * NumberAxisPropertyEditPanel.java
 * --------------------------------
 * (C) Copyright 2000-2004, Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Arnaud Lelievre;
 *
 * $Id: NumberAxisPropertyEditPanel.java,v 1.1 2007/10/10 19:50:29 vauchers Exp $
 *
 * Changes (from 24-Aug-2001)
 * --------------------------
 * 24-Aug-2001 : Added standard source header. Fixed DOS encoding problem (DG);
 * 07-Nov-2001 : Separated the JCommon Class Library classes, JFreeChart now requires
 *               jcommon.jar (DG);
 * 12-Dec-2001 : minor change due to bug fix elsewhere (DG);
 * 20-May-2003 : Enabled initialisation of paint and stroke samples to prevent
 *               NullPointers (TM)
 * 08-Sep-2003 : Added internationalization via use of properties resourceBundle (RFE 690236) (AL); 
 *
 * 08-Sep-2003 : Changed ValueAxis API (DG);
 */

package org.jfree.chart.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.layout.LCBLayout;
import org.jfree.ui.PaintSample;
import org.jfree.ui.StrokeChooserPanel;
import org.jfree.ui.StrokeSample;

/**
 * A panel for editing the properties of a value axis.
 *
 */
class NumberAxisPropertyEditPanel extends AxisPropertyEditPanel implements FocusListener {

    /** A flag that indicates whether or not the axis range is determined
     *  automatically.
     */
    private boolean autoRange;

    /** The lowest value in the axis range. */
    private double minimumValue;

    /** The highest value in the axis range. */
    private double maximumValue;

    /** A checkbox that indicates whether or not the axis range is determined
     *  automatically.
     */
    private JCheckBox autoRangeCheckBox;

    /** A text field for entering the minimum value in the axis range. */
    private JTextField minimumRangeValue;

    /** A text field for entering the maximum value in the axis range. */
    private JTextField maximumRangeValue;

    /** A checkbox that controls whether or not gridlines are showing for the
     *  axis.
     */
    //private JCheckBox showGridLinesCheckBox;

    /** The paint selected for drawing the gridlines. */
    private PaintSample gridPaintSample;

    /** The stroke selected for drawing the gridlines. */
    private StrokeSample gridStrokeSample;

    /** An array of stroke samples to choose from (since I haven't written a
     *  decent StrokeChooser component yet).
     */
    private StrokeSample[] availableStrokeSamples;
    
    /** The resourceBundle for the localization. */
    protected static ResourceBundle localizationResources = 
                            ResourceBundle.getBundle("org.jfree.chart.ui.LocalizationBundle");


    /**
     * Standard constructor: builds a property panel for the specified axis.
     *
     * @param axis  the axis, which should be changed.
     */
    public NumberAxisPropertyEditPanel(NumberAxis axis) {

        super(axis);

        this.autoRange = axis.isAutoRange();
        this.minimumValue = axis.getLowerBound();
        this.maximumValue = axis.getUpperBound();

        this.gridPaintSample = new PaintSample(Color.blue);
        this.gridStrokeSample = new StrokeSample(new BasicStroke(1.0f));

        this.availableStrokeSamples = new StrokeSample[3];
        this.availableStrokeSamples[0] = new StrokeSample(new BasicStroke(1.0f));
        this.availableStrokeSamples[1] = new StrokeSample(new BasicStroke(2.0f));
        this.availableStrokeSamples[2] = new StrokeSample(new BasicStroke(3.0f));

        JTabbedPane other = getOtherTabs();

        JPanel range = new JPanel(new LCBLayout(3));
        range.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        range.add(new JPanel());
        this.autoRangeCheckBox = new JCheckBox(localizationResources.getString("Auto-adjust_range"),
                this.autoRange);
        this.autoRangeCheckBox.setActionCommand("AutoRangeOnOff");
        this.autoRangeCheckBox.addActionListener(this);
        range.add(this.autoRangeCheckBox);
        range.add(new JPanel());

        range.add(new JLabel(localizationResources.getString("Minimum_range_value")));
        this.minimumRangeValue = new JTextField(Double.toString(this.minimumValue));
        this.minimumRangeValue.setEnabled(!this.autoRange);
        this.minimumRangeValue.setActionCommand("MinimumRange");
        this.minimumRangeValue.addActionListener(this);
        this.minimumRangeValue.addFocusListener(this);
        range.add(this.minimumRangeValue);
        range.add(new JPanel());

        range.add(new JLabel(localizationResources.getString("Maximum_range_value")));
        this.maximumRangeValue = new JTextField(Double.toString(this.maximumValue));
        this.maximumRangeValue.setEnabled(!this.autoRange);
        this.maximumRangeValue.setActionCommand("MaximumRange");
        this.maximumRangeValue.addActionListener(this);
        this.maximumRangeValue.addFocusListener(this);
        range.add(this.maximumRangeValue);
        range.add(new JPanel());

        other.add(localizationResources.getString("Range"), range);

//        JPanel grid = new JPanel(new LCBLayout(3));
//        grid.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

//        grid.add(new JPanel());
//        showGridLinesCheckBox = new JCheckBox("Show grid lines",
//            axis.isGridLinesVisible());
 //       grid.add(showGridLinesCheckBox);
 //       grid.add(new JPanel());

//        grid.add(new JLabel("Grid stroke:"));
 //       JButton button = new JButton("Set stroke...");
//        button.setActionCommand("GridStroke");
 //       button.addActionListener(this);
 //       grid.add(gridStrokeSample);
 //       grid.add(button);

//        grid.add(new JLabel("Grid paint:"));
//        button = new JButton("Set paint...");
//        button.setActionCommand("GridPaint");
 //       button.addActionListener(this);
 //       grid.add(gridPaintSample);
//        grid.add(button);

 //       other.add("Grid", grid);

    }

    /**
     * Returns the current setting of the auto-range property.
     *
     * @return <code>true</code> if auto range is enabled.
     */
    public boolean isAutoRange() {
        return this.autoRange;
    }

    /**
     * Returns the current setting of the minimum value in the axis range.
     *
     * @return the current setting of the minimum value in the axis range.
     */
    public double getMinimumValue() {
        return this.minimumValue;
    }

    /**
     * Returns the current setting of the maximum value in the axis range.
     *
     * @return the current setting of the maximum value in the axis range.
     */
    public double getMaximumValue() {
        return this.maximumValue;
    }

    /**
     * Handles actions from within the property panel.
     * @param event an event.
     */
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("GridStroke")) {
            attemptGridStrokeSelection();
        }
        else if (command.equals("GridPaint")) {
            attemptGridPaintSelection();
        }
        else if (command.equals("AutoRangeOnOff")) {
            toggleAutoRange();
        }
        else if (command.equals("MinimumRange")) {
            validateMinimum();
        }
        else if (command.equals("MaximumRange")) {
            validateMaximum();
        }
        else {
            // pass to the super-class for handling
            super.actionPerformed(event);
        }
    }

    /**
     * Handle a grid stroke selection.
     */
    private void attemptGridStrokeSelection() {
        StrokeChooserPanel panel = new StrokeChooserPanel(null, this.availableStrokeSamples);
        int result = 
            JOptionPane.showConfirmDialog(this, panel,
                                          localizationResources.getString("Stroke_Selection"),
                                          JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            this.gridStrokeSample.setStroke(panel.getSelectedStroke());
        }
    }

    /**
     * Handle a grid paint selection.
     */
    private void attemptGridPaintSelection() {
        Color c;
        c = JColorChooser.showDialog(this, localizationResources.getString("Grid_Color"),
                                     Color.blue);
        if (c != null) {
            this.gridPaintSample.setPaint(c);
        }
    }

    /**
     * Does nothing.
     *
     * @param event  the event.
     */
    public void focusGained(FocusEvent event) {
        // don't need to do anything
    }

    /**
     *  Revalidates minimum/maximum range.
     *
     *  @param event  the event.
     */
    public void focusLost(FocusEvent event) {
        if (event.getSource() == this.minimumRangeValue) {
            validateMinimum();
        }
        else if (event.getSource() == this.maximumRangeValue) {
            validateMaximum();
        }
    }

    /**
     *  Toggle the auto range setting.
     */
    public void toggleAutoRange() {
        this.autoRange = this.autoRangeCheckBox.isSelected();
        if (this.autoRange) {
            this.minimumRangeValue.setText(Double.toString(this.minimumValue));
            this.minimumRangeValue.setEnabled(false);
            this.maximumRangeValue.setText(Double.toString(this.maximumValue));
            this.maximumRangeValue.setEnabled(false);
        }
        else {
            this.minimumRangeValue.setEnabled(true);
            this.maximumRangeValue.setEnabled(true);
        }
    }

    /**
     * Revalidate the range minimum.
     */
    public void validateMinimum() {
        double newMin;
        try {
            newMin = Double.parseDouble(this.minimumRangeValue.getText());
            if (newMin >= this.maximumValue) {
                newMin = this.minimumValue;
            }
        }
        catch (NumberFormatException e) {
            newMin = this.minimumValue;
        }

        this.minimumValue = newMin;
        this.minimumRangeValue.setText(Double.toString(this.minimumValue));
    }

    /**
     * Revalidate the range maximum.
     */
    public void validateMaximum() {
        double newMax;
        try {
            newMax = Double.parseDouble(this.maximumRangeValue.getText());
            if (newMax <= this.minimumValue) {
                newMax = this.maximumValue;
            }
        }
        catch (NumberFormatException e) {
            newMax = this.maximumValue;
        }

        this.maximumValue = newMax;
        this.maximumRangeValue.setText(Double.toString(this.maximumValue));
    }

    /**
     * Sets the properties of the specified axis to match the properties
     * defined on this panel.
     *
     * @param axis  the axis.
     */
    public void setAxisProperties(Axis axis) {
        super.setAxisProperties(axis);
        NumberAxis numberAxis = (NumberAxis) axis;
        numberAxis.setAutoRange(this.autoRange);
        if (!this.autoRange) {
            numberAxis.setRange(this.minimumValue, this.maximumValue);
        }
//        numberAxis.setGridLinesVisible(this.showGridLinesCheckBox.isSelected());
//        numberAxis.setGridPaint(this.gridPaintSample.getPaint());
//        numberAxis.setGridStroke(this.gridStrokeSample.getStroke());
    }

}
