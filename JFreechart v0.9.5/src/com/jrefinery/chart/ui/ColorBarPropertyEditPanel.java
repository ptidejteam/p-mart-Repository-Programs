/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
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
 * ------------------------------
 * ColorBarPropertyEditPanel.java
 * ------------------------------
 * (C) Copyright 2002, 2003, by David M. O'Donnell and Contributors.
 *
 * Original Author:  David M. O'Donnell;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: ColorBarPropertyEditPanel.java,v 1.1 2007/10/10 19:54:25 vauchers Exp $
 *
 * Changes
 * -------
 * 26-Nov-2002 : Version 1 contributed by David M. O'Donnell (DG);
 *
 */

package com.jrefinery.chart.ui;

import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.jrefinery.chart.axis.Axis;
import com.jrefinery.chart.axis.ColorBarAxis;
import com.jrefinery.chart.axis.NumberAxis;
import com.jrefinery.layout.LCBLayout;
import com.jrefinery.ui.GreyPalette;
import com.jrefinery.ui.PaletteChooserPanel;
import com.jrefinery.ui.PaletteSample;
import com.jrefinery.ui.RainbowPalette;


/**
 * A ColorBarPropertyEditPanel.  Extends NumberAxisPropertyEditPanel to allow change general
 * axis type parameters.
 * 
 * @author David M. O'Donnell
 */
public class ColorBarPropertyEditPanel extends NumberAxisPropertyEditPanel {

    /** A checkbox that indicates whether or not the color indices should run high to low. */
    private JCheckBox invertPaletteCheckBox;

    /** Flag set by invertPaletteCheckBox. */
    private boolean invertPalette = false;

    /** A checkbox that indicates whether the palette is stepped. */
    private JCheckBox stepPaletteCheckBox;

    /** Flag set by stepPaletteCheckBox. */
    private boolean stepPalette = false;

    /** The Palette Sample displaying the current Palette. */
    private PaletteSample currentPalette;

    /** An array of availiable sample palettes. */
    private PaletteSample[] availablePaletteSamples;

    /**
     * ColorBarPropertyEditPanel constructor comment.
     * @param axis com.jrefinery.chart.NumberAxis
     */
    public ColorBarPropertyEditPanel(com.jrefinery.chart.axis.NumberAxis axis) {
        super(axis);
        invertPalette = ((ColorBarAxis) axis).getColorPalette().isInverse(); //dmo added
        stepPalette = ((ColorBarAxis) axis).getColorPalette().isStepped(); //dmo added
        currentPalette = new PaletteSample(((ColorBarAxis) axis).getColorPalette());
        availablePaletteSamples = new PaletteSample[2];
        availablePaletteSamples[0] = new PaletteSample(new RainbowPalette());
        availablePaletteSamples[1] = new PaletteSample(new GreyPalette());

        JTabbedPane other = getOtherTabs();

        JPanel palettePanel = new JPanel(new LCBLayout(4));
        palettePanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        palettePanel.add(new JPanel());
        invertPaletteCheckBox = new JCheckBox("Invert Palette:", invertPalette);
        invertPaletteCheckBox.setActionCommand("invertPalette");
        invertPaletteCheckBox.addActionListener(this);
        palettePanel.add(invertPaletteCheckBox);
        palettePanel.add(new JPanel());

        palettePanel.add(new JPanel());
        stepPaletteCheckBox = new JCheckBox("Step Palette:", stepPalette);
        stepPaletteCheckBox.setActionCommand("stepPalette");
        stepPaletteCheckBox.addActionListener(this);
        palettePanel.add(stepPaletteCheckBox);
        palettePanel.add(new JPanel());

        palettePanel.add(new JLabel("Palette:"));
        JButton button = new JButton("Set palette...");
        button.setActionCommand("PaletteChoice");
        button.addActionListener(this);
        palettePanel.add(currentPalette);
        palettePanel.add(button);

        other.add("Palette", palettePanel);

    }

    /**
     * Handles actions from within the property panel.
     * 
     * @param event  the event.
     */
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("PaletteChoice")) {
            attemptPaletteSelection();
        } 
        else if (command.equals("invertPalette")) {
            invertPalette = invertPaletteCheckBox.isSelected();
        } 
        else if (command.equals("stepPalette")) {
            stepPalette = stepPaletteCheckBox.isSelected();
        }
        else {
            super.actionPerformed(event);  // pass to the super-class for handling
        }
    }

    /**
     * Handle a palette selection.
     */
    private void attemptPaletteSelection() {
        PaletteChooserPanel panel = new PaletteChooserPanel(null, availablePaletteSamples);
        int result = JOptionPane.showConfirmDialog(this, panel, "Palette Selection",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            double zmin = currentPalette.getPalette().getMinZ();
            double zmax = currentPalette.getPalette().getMaxZ();
            currentPalette.setPalette(panel.getSelectedPalette());
            currentPalette.getPalette().setMinZ(zmin);
            currentPalette.getPalette().setMaxZ(zmax);
        }
    }

    /**
     * Sets the properties of the specified axis to match the properties defined on this panel.
     * @param axis The axis;
     */
    public void setAxisProperties(Axis axis) {
        super.setAxisProperties(axis);
        ((ColorBarAxis) axis).setColorPalette(currentPalette.getPalette());
        ((ColorBarAxis) axis).getColorPalette().setInverse(invertPalette); //dmo added
        ((ColorBarAxis) axis).getColorPalette().setStepped(stepPalette); //dmo added
    } 

    /**
     * A static method that returns a panel that is appropriate for the axis
     * type.
     *
     * @param axis  the axis whose properties are to be displayed/edited in the panel.
     *
     * @return a panel or <code>null</code< if axis is <code>null</code>.
     */
    public static AxisPropertyEditPanel getInstance(Axis axis) {

        if (axis != null) {
            // figure out what type of axis we have and instantiate the
            // appropriate panel
            if (axis instanceof ColorBarAxis) {
                return new ColorBarPropertyEditPanel((NumberAxis) axis);
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }

    }

}
