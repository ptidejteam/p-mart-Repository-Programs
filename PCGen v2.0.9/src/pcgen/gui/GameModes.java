/*
 * GameModes.java
 * Copyright 2001 (C) Mario Bonassin
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on April 24, 2001, 10:06 PM
 */
package pcgen.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import pcgen.core.Globals;

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

/**
 * Provide a panel with most of the rule configuration options for a campaign.
 * This lots of unrelated entries.
 *
 * @author  Mario Bonassin
 * @version $Revision: 1.1 $
 */
public class GameModes extends JMenu
{
  private JCheckBoxMenuItem dndMode = new JCheckBoxMenuItem("D&D");
  private JCheckBoxMenuItem starWarsMode = new JCheckBoxMenuItem("Star Wars");
  private JCheckBoxMenuItem l5rMode = new JCheckBoxMenuItem("L5R");
  private JCheckBoxMenuItem deadlandsMode = new JCheckBoxMenuItem("Deadlands");
  private JCheckBoxMenuItem weirdWarsMode = new JCheckBoxMenuItem("Weird Wars");
  private JCheckBoxMenuItem wheelMode = new JCheckBoxMenuItem("Wheel of Time");
  private CheckBoxListener checkBoxHandler = new CheckBoxListener();

  /** Creates new form Options */
  public GameModes()
  {
    setText("Game Modes");
    try
    {
      jbInit();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  private void jbInit() throws Exception
  {

    l5rMode.setEnabled(false);
    deadlandsMode.setEnabled(false);
    weirdWarsMode.setEnabled(false);
    wheelMode.setEnabled(false);
    
    this.add(dndMode);
    dndMode.setToolTipText("Use D&D character creation settings");
    dndMode.addActionListener(checkBoxHandler);
    dndMode.setSelected(Globals.dndMode);
    this.add(starWarsMode);
    starWarsMode.setToolTipText("Use Star Wars character creation settings");
    starWarsMode.addActionListener(checkBoxHandler);
    starWarsMode.setSelected(Globals.starWarsMode);
    this.add(l5rMode);
    l5rMode.setToolTipText("Use L5R character creation settings");
    l5rMode.addActionListener(checkBoxHandler);
    l5rMode.setSelected(Globals.l5rMode);
    this.add(deadlandsMode);
    deadlandsMode.setToolTipText("Use Deadlands character creation settings");
    deadlandsMode.addActionListener(checkBoxHandler);
    deadlandsMode.setSelected(Globals.deadlandsMode);
    this.add(weirdWarsMode);
    weirdWarsMode.setToolTipText("Use Weird Wars character creation settings");
    weirdWarsMode.addActionListener(checkBoxHandler);
    weirdWarsMode.setSelected(Globals.weirdWarsMode);
    this.add(wheelMode);
    wheelMode.setToolTipText("Use Wheel of Time character creation settings");
    wheelMode.addActionListener(checkBoxHandler);
    wheelMode.setSelected(Globals.wheelMode);
  }
    /**
   * This class is used to respond to clicks on the check boxes.
   */
  private final class CheckBoxListener implements ActionListener
  {
    public void actionPerformed(ActionEvent actionEvent)
    {
      Object source = actionEvent.getSource();
      if (source == dndMode)
      {
        Globals.dndMode = dndMode.isSelected();
        starWarsMode.setSelected(Globals.starWarsMode = false);
      /*l5rMode.setSelected(Globals.l5rMode = false);
        deadlandsMode.setSelected(Globals.deadlandsMode = false);
        weirdWarsMode.setSelected(Globals.weirdWarsMode = false);
        wheelMode.setSelected(Globals.wheelMode = false);
      */
      }
      else if (source == starWarsMode)
      {
        Globals.starWarsMode = starWarsMode.isSelected();
        dndMode.setSelected(Globals.dndMode = false);
      /*l5rMode.setSelected(Globals.l5rMode = false);
        deadlandsMode.setSelected(Globals.deadlandsMode = false);
        weirdWarsMode.setSelected(Globals.weirdWarsMode = false);
        wheelMode.setSelected(Globals.wheelMode = false);
      */
      }
      /*else if (source == l5rMode)
      {
        Globals.l5rMode = l5rMode.isSelected();
        dndMode.setSelected(Globals.dndMode = false);
        starWarsMode.setSelected(Globals.starWarsMode = false);
        deadlandsMode.setSelected(Globals.deadlandsMode = false);
        weirdWarsMode.setSelected(Globals.weirdWarsMode = false);
        wheelMode.setSelected(Globals.wheelMode = false);
      }
      else if (source == deadlandsMode)
      {
        Globals.deadlandsMode = deadlandsMode.isSelected();
        dndMode.setSelected(Globals.dndMode = false);
        l5rMode.setSelected(Globals.l5rMode = false);
        starWarsMode.setSelected(Globals.starWarsMode = false);
        weirdWarsMode.setSelected(Globals.weirdWarsMode = false);
        wheelMode.setSelected(Globals.wheelMode = false);
      }
      else if (source == weirdWarsMode)
      {
        Globals.weirdWarsMode = weirdWarsMode.isSelected();
        dndMode.setSelected(Globals.dndMode = false);
        l5rMode.setSelected(Globals.l5rMode = false);
        deadlandsMode.setSelected(Globals.deadlandsMode = false);
        starWarsMode.setSelected(Globals.starWarsMode = false);
        wheelMode.setSelected(Globals.wheelMode = false);
      }
      else if (source == wheelMode)
      {
        Globals.wheelMode = wheelMode.isSelected();
        dndMode.setSelected(Globals.dndMode = false);
        l5rMode.setSelected(Globals.l5rMode = false);
        deadlandsMode.setSelected(Globals.deadlandsMode = false);
        weirdWarsMode.setSelected(Globals.weirdWarsMode = false);
        starWarsMode.setSelected(Globals.starWarsMode = false);
      }
      */
    }
  }
}