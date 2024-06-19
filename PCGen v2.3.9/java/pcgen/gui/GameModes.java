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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import pcgen.core.Globals;

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
	private JCheckBoxMenuItem sidewinderMode = new JCheckBoxMenuItem("Sidewinder");
//	private JCheckBoxMenuItem hackmasterMode = new JCheckBoxMenuItem("HackMaster");
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
		//deadlandsMode.setEnabled(false);
		weirdWarsMode.setEnabled(false);
		wheelMode.setEnabled(false);
		sidewinderMode.setEnabled(false);

		this.add(dndMode);
		dndMode.setToolTipText("Use D&D character creation settings");
		dndMode.setMnemonic('D');
		dndMode.addActionListener(checkBoxHandler);
		dndMode.setSelected(Globals.isDndMode());

		this.add(starWarsMode);
		starWarsMode.setToolTipText("Use Star Wars character creation settings");
		starWarsMode.setMnemonic('S');
		starWarsMode.addActionListener(checkBoxHandler);
		starWarsMode.setSelected(Globals.isStarWarsMode());

		this.add(l5rMode);
		l5rMode.setToolTipText("Use L5R character creation settings");
		l5rMode.addActionListener(checkBoxHandler);
		l5rMode.setSelected(Globals.isL5rMode());

		this.add(deadlandsMode);
		deadlandsMode.setToolTipText("Use Deadlands character creation settings");
		deadlandsMode.addActionListener(checkBoxHandler);
		deadlandsMode.setSelected(Globals.isDeadlandsMode());
		deadlandsMode.setMnemonic('L');

		this.add(weirdWarsMode);
		weirdWarsMode.setToolTipText("Use Weird Wars character creation settings");
		weirdWarsMode.addActionListener(checkBoxHandler);
		weirdWarsMode.setSelected(Globals.isWeirdWarsMode());

		this.add(wheelMode);
		wheelMode.setToolTipText("Use Wheel of Time character creation settings");
		wheelMode.addActionListener(checkBoxHandler);
		wheelMode.setSelected(Globals.isWheelMode());

		this.add(sidewinderMode);
		sidewinderMode.setToolTipText("Use Sidewinder character creation settings");
		sidewinderMode.addActionListener(checkBoxHandler);
		sidewinderMode.setSelected(Globals.isSidewinderMode());

//		this.add(hackmasterMode);
//		hackmasterMode.setToolTipText("Use HackMaster character creation settings");
//		hackmasterMode.addActionListener(checkBoxHandler);
//		hackmasterMode.setSelected(Globals.isHackMasterMode());
	}

	/**
	 * This class is used to respond to clicks on the check boxes.
	 */
	private final class CheckBoxListener implements ActionListener
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			final Object source = actionEvent.getSource();
			if (source == null)
			{
				return;
			}

			Globals.setDndMode(false);
			Globals.setStarWarsMode(false);
			Globals.setDeadlandsMode(false);
			Globals.setWeirdWarsMode(false);
			Globals.setSidewinderMode(false);
//			Globals.setHackMasterMode(false);

			final boolean isSelected = ((JCheckBoxMenuItem)source).isSelected();
			((JCheckBoxMenuItem)source).requestFocus();
			if (source == dndMode)
			{
				Globals.setDndMode(isSelected);
			}
			else if (source == starWarsMode)
			{
				Globals.setStarWarsMode(isSelected);
			}
			else if (source == deadlandsMode)
			{
				Globals.setDeadlandsMode(isSelected);
			}
			else if (source == weirdWarsMode)
			{
				Globals.setWeirdWarsMode(isSelected);
			}
			else if (source == sidewinderMode)
			{
				Globals.setSidewinderMode(isSelected);
			}
//			else if (source == hackmasterMode)
//			{
//				Globals.setHackMasterMode(isSelected);
//			}

			Globals.loadAttributeNames();

			dndMode.setSelected(Globals.isDndMode());
			starWarsMode.setSelected(Globals.isStarWarsMode());
			weirdWarsMode.setSelected(Globals.isWeirdWarsMode());
			deadlandsMode.setSelected(Globals.isDeadlandsMode());
			sidewinderMode.setSelected(Globals.isSidewinderMode());
//			hackmasterMode.setSelected(Globals.isHackMasterMode());
		}
	}
}