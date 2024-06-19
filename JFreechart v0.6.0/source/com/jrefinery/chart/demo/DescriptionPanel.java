package com.jrefinery.chart.demo;

/**
 * Title:        JFreeChart Development
 * Description:  JFreeChart development project (http:/sourceforge.net/projects/jfreechart).
 * Copyright:    Copyright (c) 2001
 * Company:      Simba Management Limited
 * @author
 * @version 1.0
 */
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