/* ================================
 * JRefinery Utility Class Library;
 * ================================
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
 * $Id: LabelledJComponent.java,v 1.1 2007/10/10 18:52:17 vauchers Exp $
 */

package com.jrefinery.util.ui;

import java.awt.*;
import javax.swing.*;

/** A panel containing a label and any JComponent - the label appears to the left of the
    JComponent. */
public class LabelledJComponent extends JPanel {

  protected JLabel label;
  protected JComponent component;

  /** Full constructor (text is the label text). */
  public LabelledJComponent(JComponent component, String text) {
    setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
    setLayout(new BorderLayout());
    add(component, BorderLayout.CENTER);
    this.component = component;
    label = new JLabel(text);
    add(label, BorderLayout.WEST);
  }

  /** Returns a reference to the component. */
  public JComponent getComponent() {
    return component;
  }

  /** Returns a reference to the label (to allow changing the font etc.) */
  public JLabel getLabel() {
    return label;
  }

}
