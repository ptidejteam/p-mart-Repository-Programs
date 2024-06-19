/* ===============
 * JFreeChart Demo
 * ===============
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307, USA.
 *
 * ---------------------
 * DescriptionPanel.java
 * ---------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: DescriptionPanel.java,v 1.1 2007/10/10 18:57:07 vauchers Exp $
 *
 * Changes
 * -------
 * 10-Dec-2001 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.*;
import javax.swing.*;

public class DescriptionPanel extends JPanel {

    public static final Dimension PREFERRED_SIZE = new Dimension(150, 50);

    public DescriptionPanel(JTextArea text) {

        this.setLayout(new BorderLayout());
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        this.add(new JScrollPane(text, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                       JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        //this.setBorder(BorderFactory.createEtchedBorder());
    }

    public Dimension getPreferredSize() {
        return PREFERRED_SIZE;
    }

}