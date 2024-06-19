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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import pcgen.core.Globals;

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
	private JCheckBoxMenuItem mnuCCSC0 = new JCheckBoxMenuItem("0");
	private JCheckBoxMenuItem mnuCCSC1 = new JCheckBoxMenuItem("1");
	private JCheckBoxMenuItem mnuCCSC2 = new JCheckBoxMenuItem("2");
	private JCheckBoxMenuItem mnuBypassMaxSkill = new JCheckBoxMenuItem("Bypass Max Skill Rank");
	private JCheckBoxMenuItem mnuBypassFeatPreReqs = new JCheckBoxMenuItem("Bypass Feat Prerequisites");
	private JCheckBoxMenuItem mnuBypassClassPreReqs = new JCheckBoxMenuItem("Bypass Class Prerequisites");

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
		mnuCrossClassSkillCost.setMnemonic('C');
		mnuCrossClassSkillCost.add(mnuCCSC0);
		mnuCCSC0.setMnemonic('0');
		mnuCrossClassSkillCost.add(mnuCCSC1);
		mnuCCSC1.setMnemonic('1');
		mnuCrossClassSkillCost.add(mnuCCSC2);
		mnuCCSC2.setMnemonic('2');
		mnuCCSC0.addActionListener(checkBoxHandler);
		mnuCCSC1.addActionListener(checkBoxHandler);
		mnuCCSC2.addActionListener(checkBoxHandler);

		this.add(mnuBypassMaxSkill);
		mnuBypassMaxSkill.setMnemonic('B');
		mnuBypassMaxSkill.addActionListener(checkBoxHandler);

		this.add(mnuBypassFeatPreReqs);
		mnuBypassFeatPreReqs.setMnemonic('F');
		mnuBypassFeatPreReqs.addActionListener(checkBoxHandler);
		mnuBypassFeatPreReqs.setSelected(Globals.isBoolBypassFeatPreReqs());

		this.add(mnuBypassClassPreReqs);
		mnuBypassClassPreReqs.setMnemonic('L');
		mnuBypassClassPreReqs.addActionListener(checkBoxHandler);
		mnuBypassClassPreReqs.setSelected(Globals.isBoolBypassClassPreReqs());

		switch (Globals.getIntCrossClassSkillCost())
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
				Globals.setGrimHPMode(grittyHP.isSelected());
			else if (source == grittyAC)
				Globals.setGrittyACMode(grittyAC.isSelected());
			else if (source == mnuCCSC0)
			{
				Globals.setIntCrossClassSkillCost(0);
				mnuCCSC1.setSelected(false);
				mnuCCSC2.setSelected(false);
			}
			else if (source == mnuCCSC1)
			{
				Globals.setIntCrossClassSkillCost(1);
				mnuCCSC0.setSelected(false);
				mnuCCSC2.setSelected(false);
			}
			else if (source == mnuCCSC2)
			{
				Globals.setIntCrossClassSkillCost(2);
				mnuCCSC0.setSelected(false);
				mnuCCSC1.setSelected(false);
			}
			else if (source == mnuBypassMaxSkill)
			{
				Globals.setBoolBypassMaxSkillRank(mnuBypassMaxSkill.isSelected());
			}
			else if (source == mnuBypassFeatPreReqs)
			{
				Globals.setBoolBypassFeatPreReqs(mnuBypassFeatPreReqs.isSelected());
			}
			else if (source == mnuBypassClassPreReqs)
			{
				Globals.setBoolBypassClassPreReqs(mnuBypassClassPreReqs.isSelected());
			}
		}
	}

}