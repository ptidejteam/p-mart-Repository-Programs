/* ==========================
 * JRefinery Utility Classes;
 * ==========================
 * Version 0.20;
 * (C) Copyright 2000, Simba Management Limited;
 * Contact: David Gilbert (david.gilbert@bigfoot.com);
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
 * $Id: StrokeChooserPanel.java,v 1.1 2007/10/10 18:52:17 vauchers Exp $
 */

package com.jrefinery.util.ui;

import java.awt.*;
import javax.swing.*;

/** A component for choosing a stroke from a list of available strokes. */
public class StrokeChooserPanel extends JPanel {

  private JComboBox selector;

  /** Standard constructor - returns a panel containing a combo-box that allows the user to
      select one stroke from a list of available strokes. */
  public StrokeChooserPanel(StrokeSample current, StrokeSample[] available) {
    setLayout(new BorderLayout());
    selector = new JComboBox(available);
    selector.setSelectedItem(current);
    selector.setRenderer(new StrokeSample(new BasicStroke(1)));
    add(selector);
  }

  /** Returns the selected stroke. */
  public Stroke getSelectedStroke() {
    StrokeSample sample = (StrokeSample)selector.getSelectedItem();
    return sample.getStroke();
  }

}