/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            PlotPropertyEditPanel.java
 * Author:          David Gilbert;
 * Contributor(s):  -;
 *
 * (C) Copyright 2000, Simba Management Limited;
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
 * $Id: PlotPropertyEditPanel.java,v 1.1 2007/10/10 18:52:18 vauchers Exp $
 */

package com.jrefinery.chart.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jrefinery.util.ui.*;
import com.jrefinery.chart.*;

/**
 * A panel for editing the properties of a Plot.
 */
public class PlotPropertyEditPanel extends JPanel implements ActionListener {

  /** The insets for the plot. */
  private Insets insets;

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

  /**
   * Standard constructor - constructs a panel for editing the properties of the specified plot.
   * <P>
   * In designing the panel, we need to be aware that subclasses of Plot will need to implement
   * subclasses of PlotPropertyEditPanel - so we need to leave one or two 'slots' where the
   * subclasses can extend the user interface.
   */
  public PlotPropertyEditPanel(Plot plot) {

    insets = plot.getInsets();
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
    button.setEnabled(false);  // to be implemented
    JTextField info = new JTextField("No editor implemented");
    info.setEnabled(false);
    interior.add(info);
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
    verticalAxisPropertyPanel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
    horizontalAxisPropertyPanel = AxisPropertyEditPanel.getInstance(plot.getAxis(Plot.HORIZONTAL_AXIS));
    horizontalAxisPropertyPanel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));

    tabs.add("Vertical Axis", verticalAxisPropertyPanel);
    tabs.add("Horizontal Axis", horizontalAxisPropertyPanel);
    tabs.add("Appearance", appearance);
    panel.add(tabs);

    add(panel);
  }

  /**
   * Returns the current plot insets.
   */
  public Insets getPlotInsets() {
    return insets;
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
   * Sets the properties of the specified plot to match the properties defined on this panel.
   */
  public void setPlotProperties(Plot plot) {
    // set the plot properties
    plot.setOutlinePaint(this.getOutlinePaint());
    plot.setOutlineStroke(this.getOutlineStroke());
    plot.setBackgroundPaint(this.getBackgroundPaint());

    // set the horizontal axis properties
    this.getHorizontalAxisPropertyEditPanel().setAxisProperties(plot.getAxis(Plot.HORIZONTAL_AXIS));
    // set the vertical axis properties
    this.getVerticalAxisPropertyEditPanel().setAxisProperties(plot.getAxis(Plot.VERTICAL_AXIS));
  }

}