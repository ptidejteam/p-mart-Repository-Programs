/*
 * HomeBrew.java
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
public class HomeBrew extends JMenu
{
  private JCheckBoxMenuItem grittyAC = new JCheckBoxMenuItem("Gritty AC Mode");
  private JCheckBoxMenuItem grittyHP = new JCheckBoxMenuItem("Gritty HP Mode");
  private CheckBoxListener checkBoxHandler = new CheckBoxListener();
  private JMenu mnuCrossClassSkillCost = new JMenu("Cross Class Skill Cost");
  private JCheckBoxMenuItem mnuCCSC0= new JCheckBoxMenuItem("0");
  private JCheckBoxMenuItem mnuCCSC1= new JCheckBoxMenuItem("1");
  private JCheckBoxMenuItem mnuCCSC2= new JCheckBoxMenuItem("2");

  /** Creates new form MainOptions */
  public HomeBrew()
  {
    setText("House Rules");
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

    
    grittyAC.setEnabled(false);
    grittyHP.setEnabled(false);
    
    this.add(grittyAC);
    grittyAC.addActionListener(checkBoxHandler);
    this.add(grittyHP);
    grittyHP.addActionListener(checkBoxHandler);
  
    this.add(mnuCrossClassSkillCost);
    mnuCrossClassSkillCost.add(mnuCCSC0);
    mnuCrossClassSkillCost.add(mnuCCSC1);
    mnuCrossClassSkillCost.add(mnuCCSC2);
    mnuCCSC0.addActionListener(checkBoxHandler);
    mnuCCSC1.addActionListener(checkBoxHandler);
    mnuCCSC2.addActionListener(checkBoxHandler);
    
    switch (Globals.intCrossClassSkillCost)
    {
      case 0:
        mnuCCSC0.setSelected(true);
        break;
      case 1:
        mnuCCSC1.setSelected(true);
        break;
      case 2:
        mnuCCSC2.setSelected(true);
        break;
      default:    //  The checkbox menu doesn't support other values, but it's non-fatal 
        break;
    }
        

//    }
  }
    /**
   * This class is used to respond to clicks on the check boxes.
   */
  private final class CheckBoxListener implements ActionListener
  {
    public void actionPerformed(ActionEvent actionEvent)
    {
      Object source = actionEvent.getSource();
      if (source == grittyHP)
        Globals.grimHPMode = grittyHP.isSelected();
      else if (source == grittyAC)
        Globals.grittyACMode = grittyAC.isSelected();
      else if (source == mnuCCSC0)
      {
         Globals.intCrossClassSkillCost = 0;
         mnuCCSC1.setSelected(false);
         mnuCCSC2.setSelected(false);
      }
      else if (source == mnuCCSC1)
      {
         Globals.intCrossClassSkillCost = 1;
         mnuCCSC0.setSelected(false);
         mnuCCSC2.setSelected(false);
      }
      else if (source == mnuCCSC2)
      {
         Globals.intCrossClassSkillCost = 2; 
         mnuCCSC0.setSelected(false);
         mnuCCSC1.setSelected(false);
      }
     }
   }

}