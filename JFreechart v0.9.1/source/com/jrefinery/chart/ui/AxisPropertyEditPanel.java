/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
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
 * --------------------------
 * AxisPropertyEditPanel.java
 * --------------------------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Andrzej Porebski;
 *
 * $Id: AxisPropertyEditPanel.java,v 1.1 2007/10/10 19:02:28 vauchers Exp $
 *
 * Changes (from 24-Aug-2001)
 * --------------------------
 * 24-Aug-2001 : Added standard source header. Fixed DOS encoding problem (DG);
 * 07-Nov-2001 : Separated the JCommon Class Library classes, JFreeChart now requires
 *               jcommon.jar (DG);
 * 21-Nov-2001 : Allowed for null axes (DG);
 * 09-Apr-2002 : Minor change to import statement to fix Javadoc error (DG);
 *
 */

package com.jrefinery.chart.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jrefinery.chart.*;
import com.jrefinery.layout.*;
import com.jrefinery.ui.*;
import com.jrefinery.chart.ui.NumberAxisPropertyEditPanel;  // this is required for Javadoc

/**
 * A panel for editing the properties of an axis.
 * @see NumberAxisPropertyEditPanel
 */
public class AxisPropertyEditPanel extends JPanel implements ActionListener {

    /** The axis label. */
    protected JTextField label;

    /** The label font. */
    protected Font labelFont;

    /** The label paint. */
    protected PaintSample labelPaintSample;

    /** A field showing a description of the label font. */
    protected JTextField labelFontField;

    /** The font for displaying tick labels on the axis. */
    protected Font tickLabelFont;

    /** A field containing a description of the font for displaying tick labels on the axis. */
    protected JTextField tickLabelFontField;

    /** The paint (color) for the tick labels. */
    protected PaintSample tickLabelPaintSample;

    /** An empty sub-panel for extending the user interface to handle more complex axes. */
    protected JPanel slot1;

    /** An empty sub-panel for extending the user interface to handle more complex axes. */
    protected JPanel slot2;

    /** A flag that indicates whether or not the tick labels are visible. */
    protected JCheckBox showTickLabelsCheckBox;

    /** A flag that indicates whether or not the tick marks are visible. */
    protected JCheckBox showTickMarksCheckBox;

    // Insets related member variables
    protected InsetsTextField tickLabelInsetsTextField;
    protected InsetsTextField labelInsetsTextField;
    protected Insets _tickLabelInsets;
    protected Insets _labelInsets;

    /** A tabbed pane for... */
    private JTabbedPane otherTabs;

    /**
     * A static method that returns a panel that is appropriate for the axis type.
     * @param axis The axis whose properties are to be displayed/edited in the panel;
     */
    public static AxisPropertyEditPanel getInstance(Axis axis) {

        if (axis!=null) {
            // figure out what type of axis we have and instantiate the appropriate panel
            if (axis instanceof NumberAxis) {
                return new NumberAxisPropertyEditPanel((NumberAxis)axis);
            }
            else return new AxisPropertyEditPanel(axis);
        }
        else return null;

    }

    /**
     * Standard constructor: builds a panel for displaying/editing the properties of the specified
     * axis.
     * @param axis The axis whose properties are to be displayed/edited in the panel;
     */
    public AxisPropertyEditPanel(Axis axis) {

        labelFont = axis.getLabelFont();
        labelPaintSample = new PaintSample(axis.getLabelPaint());
        tickLabelFont = axis.getTickLabelFont();
        tickLabelPaintSample = new PaintSample(axis.getTickLabelPaint());

        // Insets values
        _tickLabelInsets = axis.getTickLabelInsets();
        _labelInsets = axis.getLabelInsets();

        setLayout(new BorderLayout());

        JPanel general = new JPanel(new BorderLayout());
        general.setBorder(BorderFactory.createTitledBorder(
                            BorderFactory.createEtchedBorder(), "General:"));

        JPanel interior = new JPanel(new LCBLayout(5));
        interior.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        interior.add(new JLabel("Label:"));
        label = new JTextField(axis.getLabel());
        interior.add(label);
        interior.add(new JPanel());

        interior.add(new JLabel("Font:"));
        labelFontField = new FontDisplayField(labelFont);
        interior.add(labelFontField);
        JButton b = new JButton("Select...");
        b.setActionCommand("SelectLabelFont");
        b.addActionListener(this);
        interior.add(b);

        interior.add(new JLabel("Paint:"));
        interior.add(labelPaintSample);
        b = new JButton("Select...");
        b.setActionCommand("SelectLabelPaint");
        b.addActionListener(this);
        interior.add(b);

        interior.add(new JLabel("Label Insets:"));
        b = new JButton("Edit...");
        b.setActionCommand("LabelInsets");
        b.addActionListener(this);
        labelInsetsTextField = new InsetsTextField(_labelInsets);
        interior.add(labelInsetsTextField);
        interior.add(b);

        interior.add(new JLabel("Tick Label Insets:"));
        b = new JButton("Edit...");
        b.setActionCommand("TickLabelInsets");
        b.addActionListener(this);
        tickLabelInsetsTextField = new InsetsTextField(_tickLabelInsets);
        interior.add(tickLabelInsetsTextField);
        interior.add(b);

        general.add(interior);

        add(general, BorderLayout.NORTH);

        slot1 = new JPanel(new BorderLayout());

        JPanel other = new JPanel(new BorderLayout());
        other.setBorder(BorderFactory.createTitledBorder(
                             BorderFactory.createEtchedBorder(), "Other:"));

        otherTabs = new JTabbedPane();
        otherTabs.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        JPanel ticks = new JPanel(new LCBLayout(3));
        ticks.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        showTickLabelsCheckBox = new JCheckBox("Show tick labels", axis.isTickLabelsVisible());
        ticks.add(showTickLabelsCheckBox);
        ticks.add(new JPanel());
        ticks.add(new JPanel());

        ticks.add(new JLabel("Tick label font:"));
        tickLabelFontField = new FontDisplayField(tickLabelFont);
        ticks.add(tickLabelFontField);
        b = new JButton("Select...");
        b.setActionCommand("SelectTickLabelFont");
        b.addActionListener(this);
        ticks.add(b);

        showTickMarksCheckBox = new JCheckBox("Show tick marks", axis.isTickMarksVisible());
        ticks.add(showTickMarksCheckBox);
        ticks.add(new JPanel());
        ticks.add(new JPanel());

        otherTabs.add("Ticks", ticks);

        other.add(otherTabs);

        slot1.add(other);

        slot2 = new JPanel(new BorderLayout());
        slot2.add(slot1, BorderLayout.NORTH);
        add(slot2);

    }

    /**
     * Returns the current axis label.
     */
    public String getLabel() {
        return label.getText();
    }

    /**
     * Returns the current label font.
     */
    public Font getLabelFont() {
        return labelFont;
    }

    /**
     * Returns the current label paint.
     */
    public Paint getLabelPaint() {
        return labelPaintSample.getPaint();
    }

    /**
     * Returns a flag that indicates whether or not the tick labels are visible.
     */
    public boolean isTickLabelsVisible() {
        return showTickLabelsCheckBox.isSelected();
    }

    /**
     * Returns the font used to draw the tick labels (if they are showing).
     */
    public Font getTickLabelFont() {
        return tickLabelFont;
    }

    /**
     * Returns the current tick label paint.
     */
    public Paint getTickLabelPaint() {
        return tickLabelPaintSample.getPaint();
    }

    /**
     * Returns the current value of the flag that determines whether or not tick marks are
     * visible.
     */
    public boolean isTickMarksVisible() {
        return showTickMarksCheckBox.isSelected();
    }

    /**
     * Returns the current tick label insets value
     */
    public Insets getTickLabelInsets() {
        return (_tickLabelInsets == null) ? new Insets(0,0,0,0) : _tickLabelInsets;
    }

    /**
     * Returns the current label insets value
     */
    public Insets getLabelInsets() {
        return (_labelInsets == null) ? new Insets(0,0,0,0) : _labelInsets;
    }

    /**
     * Returns a reference to the tabbed pane.
     */
    public JTabbedPane getOtherTabs() {
        return otherTabs;
    }

    /**
     * Handles user interaction with the property panel.
     * @param event Information about the event that triggered the call to this method.
     */
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("SelectLabelFont")) {
            attemptLabelFontSelection();
        }
        else if (command.equals("SelectLabelPaint")) {
            attemptModifyLabelPaint();
        }
        else if (command.equals("SelectTickLabelFont")) {
            attemptTickLabelFontSelection();
        }
      else if (command.equals("LabelInsets"))
        {
          editLabelInsets();
        }
      else if (command.equals("TickLabelInsets"))
        {
          editTickLabelInsets();
        }
    }

    /**
     * Presents a font selection dialog to the user.
     */
    private void attemptLabelFontSelection() {

        FontChooserPanel panel = new FontChooserPanel(labelFont);
        int result = JOptionPane.showConfirmDialog(this, panel, "Font Selection",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result==JOptionPane.OK_OPTION) {
            labelFont = panel.getSelectedFont();
            labelFontField.setText(labelFont.getFontName()+" "+labelFont.getSize());
        }

    }

    /**
     * Allows the user the opportunity to change the outline paint.
     */
    private void attemptModifyLabelPaint() {
        Color c;
        c = JColorChooser.showDialog(this, "Label Color", Color.blue);
        if (c!=null) {
            labelPaintSample.setPaint(c);
        }
    }

    /**
     * Presents a tick label font selection dialog to the user.
     */
    public void attemptTickLabelFontSelection() {

        FontChooserPanel panel = new FontChooserPanel(tickLabelFont);
        int result = JOptionPane.showConfirmDialog(this, panel, "Font Selection",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result==JOptionPane.OK_OPTION) {
            tickLabelFont = panel.getSelectedFont();
            tickLabelFontField.setText(tickLabelFont.getFontName()+" "+tickLabelFont.getSize());
        }

    }

    /**
     * Presents insets chooser panel allowing user to modify tick label's
     * individual insets values. Updates the current insets text field if edit
     * is accepted.
     */
    private void editTickLabelInsets() {
        InsetsChooserPanel panel = new InsetsChooserPanel(_tickLabelInsets);
        int result =
            JOptionPane.showConfirmDialog(this, panel, "Edit Insets",
                                          JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result==JOptionPane.OK_OPTION) {
            _tickLabelInsets = panel.getInsets();
            tickLabelInsetsTextField.setInsets(_tickLabelInsets);
        }
    }

    /**
     * Presents insets chooser panel allowing user to modify label's
     * individual insets values. Updates the current insets text field if edit
     * is accepted.
     */
    private void editLabelInsets() {
        InsetsChooserPanel panel = new InsetsChooserPanel(_labelInsets);
        int result =
          JOptionPane.showConfirmDialog(this, panel, "Edit Insets",
                                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result==JOptionPane.OK_OPTION) {
            _labelInsets = panel.getInsets();
            labelInsetsTextField.setInsets(_labelInsets);
        }
    }

    /**
     * Sets the properties of the specified axis to match the properties defined on this panel.
     */
    public void setAxisProperties(Axis axis) {
        axis.setLabel(this.getLabel());
        axis.setLabelFont(this.getLabelFont());
        axis.setLabelPaint(this.getLabelPaint());
        axis.setTickMarksVisible(this.isTickMarksVisible());
        // axis.setTickMarkStroke(this.getTickMarkStroke());
        axis.setTickLabelsVisible(this.isTickLabelsVisible());
        axis.setTickLabelFont(this.getTickLabelFont());
        axis.setTickLabelPaint(this.getTickLabelPaint());
        axis.setTickLabelInsets(this.getTickLabelInsets());
        axis.setLabelInsets(this.getLabelInsets());
    }

}
