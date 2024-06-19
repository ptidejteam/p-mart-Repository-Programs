/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: LegendPropertyEditPanel.java,v 1.1 2007/10/10 18:57:09 vauchers Exp $
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
 * Changes (from 24-Aug-2001)
 * --------------------------
 * 24-Aug-2001 : Added standard source header. Fixed DOS encoding problem (DG);
 * 07-Nov-2001 : Separated the JCommon Class Library classes, JFreeChart now requires
 *               jcommon.jar (DG);
 */

package com.jrefinery.chart.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jrefinery.chart.*;
import com.jrefinery.layout.*;
import com.jrefinery.ui.*;

/**
 * A panel for editing the properties of a Legend.
 */
class LegendPropertyEditPanel extends JPanel
                              implements ActionListener {

    /** The stroke (pen) used to draw the legend outline. */
    private StrokeSample outlineStroke;

    /** The paint (color) used to draw the legend outline. */
    private PaintSample outlinePaint;

    /** The paint (color) used to fill the legend background. */
    private PaintSample backgroundPaint;

    /** The font used to draw the series names. */
    private Font seriesFont;

    /** The paint (color) used to draw the series names. */
    private PaintSample seriesPaint;

    /** An array of strokes (pens) to select from. */
    private StrokeSample[] availableStrokeSamples;

    /**
     * Standard constructor: builds a panel based on the specified legend.
     */
    public LegendPropertyEditPanel(Legend legend) {

        StandardLegend l = (StandardLegend)legend;
        outlineStroke = new StrokeSample(l.getOutlineStroke());
        outlinePaint = new PaintSample(l.getOutlinePaint());
        backgroundPaint = new PaintSample(l.getBackgroundPaint());
        seriesFont = l.getItemFont();
        seriesPaint = new PaintSample(l.getItemPaint());

        availableStrokeSamples = new StrokeSample[4];
        availableStrokeSamples[0] = new StrokeSample(new BasicStroke(1.0f));
        availableStrokeSamples[1] = new StrokeSample(new BasicStroke(2.0f));
        availableStrokeSamples[2] = new StrokeSample(new BasicStroke(3.0f));
        availableStrokeSamples[3] = new StrokeSample(new BasicStroke(4.0f));

        setLayout(new BorderLayout());

        JPanel general = new JPanel(new BorderLayout());
        general.setBorder(BorderFactory.createTitledBorder(
                              BorderFactory.createEtchedBorder(), "General:"));

        JPanel interior = new JPanel(new LCBLayout(5));
        interior.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        JLabel titleLabel = new JLabel("Text:");

        interior.add(new JLabel("Outline:"));
        interior.add(outlineStroke);
        JButton button = new JButton("Select...");
        button.setActionCommand("OutlineStroke");
        button.addActionListener(this);
        interior.add(button);

        interior.add(new JLabel("Outline Paint:"));
        button = new JButton("Select...");
        button.setActionCommand("OutlinePaint");
        button.addActionListener(this);
        interior.add(outlinePaint);
        interior.add(button);

        interior.add(new JLabel("Background:"));
        button = new JButton("Select...");
        button.setActionCommand("BackgroundPaint");
        button.addActionListener(this);
        interior.add(backgroundPaint);
        interior.add(button);

        interior.add(new JLabel("Series label font:"));
        button = new JButton("Select...");
        button.setActionCommand("SeriesFont");
        button.addActionListener(this);
        interior.add(new FontDisplayField(seriesFont));
        interior.add(button);

        interior.add(new JLabel("Series label paint:")) ;
        button = new JButton("Select...");
        button.setActionCommand("SeriesPaint");
        button.addActionListener(this);
        interior.add(seriesPaint) ;
        interior.add(button) ;

        general.add(interior);
        add(general, BorderLayout.NORTH);
    }

    /**
     * Returns the current outline stroke.
     */
    public Stroke getOutlineStroke() {
        return outlineStroke.getStroke();
    }

    /**
     * Returns the current outline paint.
     */
    public Paint getOutlinePaint() {
        return outlinePaint.getPaint();
    }

    /**
     * Returns the current background paint.
     */
    public Paint getBackgroundPaint() {
        return backgroundPaint.getPaint();
    }

    /**
     * Returns the current series label font.
     */
    public Font getSeriesFont() {
        return seriesFont;
    }

    /**
     * Returns the current series label paint.
     */
    public Paint getSeriesPaint() {
        return seriesPaint.getPaint();
    }

    /**
     * Handles user interactions with the panel.
     */
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("OutlineStroke")) {
            attemptModifyOutlineStroke();
        }
        else if (command.equals("OutlinePaint")) {
            attemptModifyOutlinePaint();
        }
        else if (command.equals("BackgroundPaint")) {
            attemptModifyBackgroundPaint();
        }
        else if (command.equals("SeriesFont")) {
            attemptModifySeriesFont();
        }
        else if (command.equals("SeriesPaint")) {
            attemptModifySeriesPaint();
        }
    }

    /**
     * Allows the user the opportunity to change the outline stroke.
     */
    private void attemptModifyOutlineStroke() {
        StrokeChooserPanel panel = new StrokeChooserPanel(outlineStroke, availableStrokeSamples);
        int result = JOptionPane.showConfirmDialog(this, panel, "Pen/Stroke Selection",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result==JOptionPane.OK_OPTION) {
            outlineStroke.setStroke(panel.getSelectedStroke());
        }
    }

    /**
     * Allows the user the opportunity to change the outline paint.
     */
    private void attemptModifyOutlinePaint() {
        Color c;
        c = JColorChooser.showDialog(this, "Outline Color", Color.blue);
        if (c!=null) {
            outlinePaint.setPaint(c);
        }
    }

    /**
     * Allows the user the opportunity to change the background paint.
     */
    private void attemptModifyBackgroundPaint() {
        Color c;
        c = JColorChooser.showDialog(this, "Background Color", Color.blue);
        if (c!=null) {
            backgroundPaint.setPaint(c);
        }
    }

    /**
     * Allows the user the opportunity to change the series label font.
     */
    public void attemptModifySeriesFont() {

        FontChooserPanel panel = new FontChooserPanel(seriesFont);
        int result = JOptionPane.showConfirmDialog(this, panel, "Font Selection",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result==JOptionPane.OK_OPTION) {
            seriesFont = panel.getSelectedFont();
            //fontfield.setText(titlefont.getFontName()+" "+titlefont.getSize());
        }

    }

    /**
     * Allows the user the opportunity to change the series label paint.
     */
    private void attemptModifySeriesPaint() {
        Color c;
        c = JColorChooser.showDialog(this, "Series Label Color", Color.blue);
        if (c!=null) {
            seriesPaint.setPaint(c);
        }
    }

    /**
     * Sets the properties of the specified legend to match the properties defined on this panel.
     */
    public void setLegendProperties(Legend legend) {
        if (legend instanceof StandardLegend) {  // only supports StandardLegend at present
            StandardLegend standard = (StandardLegend)legend;
            standard.setOutlineStroke(this.getOutlineStroke());
            standard.setOutlinePaint(this.getOutlinePaint());
            standard.setBackgroundPaint(this.getBackgroundPaint());
            standard.setItemFont(this.getSeriesFont());
            standard.setItemPaint(this.getSeriesPaint());
        }
        else {
            // raise exception - unrecognised legend
        }
    }

}
