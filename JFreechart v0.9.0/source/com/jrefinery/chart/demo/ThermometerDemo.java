/* ===============
 * JFreeChart Demo
 * ===============
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
 * --------------------
 * ThermometerDemo.java
 * --------------------
 * (C) Copyright 2002, by Australian Antarctic Division.
 *
 * Original Author:  Bryan Scott (for Australian Antarctic Division).
 * Contributor(s):   -;
 *
 * $Id: ThermometerDemo.java,v 1.1 2007/10/10 19:01:20 vauchers Exp $
 *
 * Changes (since 24-Apr-2002)
 * ---------------------------
 * 24-Apr-2002 : added standard source header (DG);
 *
 */
package com.jrefinery.chart.demo;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.text.*;
import com.jrefinery.chart.*;
import com.jrefinery.data.*;

/**
 * A demonstration application for the thermometer plot.
 */
public class ThermometerDemo extends JPanel {

    protected final static String[] OPTIONS = {"None", "Right", "Bulb"} ;

    DefaultMeterDataset data = new DefaultMeterDataset();

    MeterPlot meterplot = new MeterPlot(data);
    JFreeChart meterchart = new JFreeChart("Meter Chart", JFreeChart.DEFAULT_TITLE_FONT,
                                           meterplot, false);
    ChartPanel panelMeter = new ChartPanel(meterchart);

    JPanel jPanel1 = new JPanel();
    JButton butUp3 = new JButton();
    JButton butDown3 = new JButton();

    JPanel jPanel2 = new JPanel();
    BorderLayout borderLayout2 = new BorderLayout();

    JPanel jPanel3 = new JPanel();
    BorderLayout borderLayout3 = new BorderLayout();

    JPanel jPanel4 = new JPanel();
    JButton butDown2 = new JButton();
    JButton butUp2 = new JButton();

    JPanel jPanel5 = new JPanel();
    GridLayout gridLayout1 = new GridLayout();

    JPanel jPanel6 = new JPanel();
    JButton butUp1 = new JButton();
    JButton butDown1 = new JButton();

    JThermometer thermo1 = new JThermometer();
    JThermometer thermo2 = new JThermometer();
    JThermometer thermo3 = new JThermometer();
    JThermometer[] thermo = new JThermometer[3];

    BorderLayout borderLayout1 = new BorderLayout();
    JPanel jPanel7 = new JPanel();
    JPanel jPanel8 = new JPanel();
    JPanel jPanel9 = new JPanel();
    GridLayout gridLayout2 = new GridLayout();
    GridLayout gridLayout3 = new GridLayout();
    GridLayout gridLayout4 = new GridLayout();
    JComboBox pickShow1 = new JComboBox(OPTIONS);
    JComboBox pickShow2 = new JComboBox(OPTIONS);
    JComboBox pickShow3 = new JComboBox(OPTIONS);
    JComboBox[] pickShow = new JComboBox[3];
    JPanel jPanel10 = new JPanel();
    BorderLayout borderLayout4 = new BorderLayout();
    JPanel jPanel11 = new JPanel();
    JButton butDown4 = new JButton();
    JButton butUp4 = new JButton();

    public ThermometerDemo() {
        try {
            jbInit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    void jbInit() throws Exception {

        data.setRange(new Double(-20),new Double(20));
        thermo[0] = thermo1;
        thermo[1] = thermo2;
        thermo[2] = thermo3;
        thermo[0].setValue(0.0);
        thermo[1].setValue(0.2);
        thermo[2].setValue(0.3);

        thermo[0].setBackground(Color.white);
        thermo[2].setBackground(Color.white);

        thermo[0].setOutlinePaint(null);
        thermo[1].setOutlinePaint(null);
        thermo[2].setOutlinePaint(null);

        thermo[0].setUnits(0);
        thermo[1].setUnits(1);
        thermo[2].setUnits(2);

        thermo[0].setFont(new Font("Arial",Font.BOLD,20));
        thermo[0].setShowValueLines(true);
        thermo[0].setFollowData(true);
        thermo[1].setDisplayLocation(1);

        thermo[1].setForeground(Color.blue);
        thermo[2].setForeground(Color.pink);

        thermo[0].setRangeInfo(0,  0, 20, 0, 26);
        thermo[0].setRangeInfo(1, 20, 24, 0, 26);
        thermo[0].setRangeInfo(2, 24, 26, 0, 26);

        thermo[0].addTitle("Sea Water Temp");
        thermo[1].addTitle("Air Temp", new Font("Arial",1,24));
        thermo[2].addTitle("Ship Temp", new Font("Arial",Font.ITALIC + Font.BOLD,20));

        thermo[1].setValueFormat(new DecimalFormat("#0.0"));
        thermo[2].setValueFormat(new DecimalFormat("#0.00"));

        pickShow[0] = pickShow1;
        pickShow[1] = pickShow2;
        pickShow[2] = pickShow3;

        this.setLayout(gridLayout1);
        butDown3.setText("<");
        butDown3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setValue(2, -1);
            }
        });
        butUp3.setText(">");
        butUp3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setValue(2, 1);
            }
        });
        jPanel1.setLayout(borderLayout2);
        jPanel3.setLayout(borderLayout3);
        butDown2.setText("<");
        butDown2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setValue(1, -1);
            }
        });
        butUp2.setText(">");
        butUp2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setValue(1, 1);
            }
        });
        butUp1.setText(">");
        butUp1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setValue(0, 1);
            }
        });
        butDown1.setText("<");
        butDown1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setValue(0, -1);
            }
        });
        jPanel5.setLayout(borderLayout1);
        pickShow1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setShowValue(0);
            }
        });
        pickShow2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setShowValue(1);
            }
        });
        pickShow3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setShowValue(2);
            }
        });

        jPanel9.setLayout(gridLayout2);
        gridLayout2.setColumns(1);
        jPanel8.setLayout(gridLayout3);
        jPanel7.setLayout(gridLayout4);
        jPanel5.setBorder(BorderFactory.createEtchedBorder());
        jPanel3.setBorder(BorderFactory.createEtchedBorder());
        jPanel1.setBorder(BorderFactory.createEtchedBorder());
        jPanel6.setBackground(Color.white);
        jPanel2.setBackground(Color.white);
        jPanel9.setBackground(Color.white);
        jPanel10.setLayout(borderLayout4);
        butDown4.setText("<");
        butDown4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setMeterValue(-1.1);
            }
        });
        butUp4.setText(">");
        butUp4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setMeterValue(1.1);
            }
        });
        jPanel1.add(thermo3,  BorderLayout.CENTER);
        jPanel1.add(jPanel2, BorderLayout.SOUTH);
        jPanel2.add(butDown3, null);
        jPanel2.add(butUp3, null);
        jPanel1.add(jPanel9,  BorderLayout.NORTH);
        jPanel9.add(pickShow3, null);
        this.add(jPanel10, null);
        jPanel10.add(jPanel11, BorderLayout.SOUTH);
        jPanel11.add(butDown4, null);
        jPanel11.add(butUp4, null);
        jPanel4.add(butDown2, null);
        jPanel4.add(butUp2, null);
        jPanel3.add(jPanel8, BorderLayout.NORTH);
        jPanel8.add(pickShow2, null);
        jPanel3.add(thermo2, BorderLayout.CENTER);
        jPanel3.add(jPanel4, BorderLayout.SOUTH);
        this.add(jPanel5, null);
        jPanel5.add(thermo1,  BorderLayout.CENTER);
        jPanel5.add(jPanel6, BorderLayout.SOUTH);
        jPanel6.add(butDown1, null);
        jPanel6.add(butUp1, null);
        jPanel5.add(jPanel7, BorderLayout.NORTH);
        jPanel7.add(pickShow1, null);
        this.add(jPanel3, null);
        this.add(jPanel1, null);
        jPanel10.add(panelMeter, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        final ThermometerDemo panel = new ThermometerDemo();

        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout(5,5));
        frame.setDefaultCloseOperation(3);
        frame.setTitle("Thermometer Test");
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.setSize(700, 400);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((d.width - frame.getSize().width) / 2, (d.height - frame.getSize().height) / 2);
        frame.setVisible(true);

        /// Live Data Recpetion
        //MultiCastConnection livedata = new MultiCastConnection(7002);
        //livedata.subscribe(panel);
        //livedata.start();
    }

    private void setValue(int thermometer, double value) {
        if ((thermometer >= 0) && (thermometer < 3)) {
            try {
                thermo[thermometer].setValue(thermo[thermometer].getValue().doubleValue() + value);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void setMeterValue(double value) {
        try {
            double newValue = value ;
            if (data.isValueValid()) {
                newValue += data.getValue().doubleValue();
            }
            data.setValue(new Double(newValue));
        }
        catch (Exception ex) {
            System.err.println(ex.getMessage());
            //ex.printStackTrace();
        }
    }


    private void setShowValue(int thermometer) {
        if ((thermometer >= 0) && (thermometer < 3)) {
            thermo[thermometer].setDisplayLocation(pickShow[thermometer].getSelectedIndex());
        }
    }

}