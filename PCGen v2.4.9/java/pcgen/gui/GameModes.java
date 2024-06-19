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
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.*;
import pcgen.core.Constants;
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
	private static String in_gameModes;
	private static String in_useDeadlands;
	private static String in_useDnD;
	private static String in_useStarWars;
	private static String in_useWeirdWars;
	private static String in_useSovereign;
	private static String in_useSidewinder;
	//private static String in_useHackMaster;
	private static String in_useWheelTime;
	private static String in_useL5R;
	private static String in_useFadingSuns;
	/**
	 * Resrouce bundles
	 */
	static
	{
		ResourceBundle gameModesProperties;
		Locale currentLocale = new Locale(Globals.getLanguage(), Globals.getCountry());
		try
		{
			gameModesProperties = ResourceBundle.getBundle("pcgen/gui/properities/LanguageBundle", currentLocale);
			in_gameModes = gameModesProperties.getString("in_gameModes");
			in_useDeadlands = gameModesProperties.getString("in_useDeadlands");
			in_useDnD = gameModesProperties.getString("in_useDnD");
			in_useStarWars = gameModesProperties.getString("in_useStarWars");
			in_useWeirdWars = gameModesProperties.getString("in_useWeirdWars");
			in_useSovereign = gameModesProperties.getString("in_useSovereign");
			in_useSidewinder = gameModesProperties.getString("in_useSidewinder");
			//in_useHackMaster = gameModesProperties.getString("in_useHackMaster");
			in_useWheelTime = gameModesProperties.getString("in_useWheelTime");
			in_useL5R = gameModesProperties.getString("in_useL5R");
			in_useFadingSuns = gameModesProperties.getString("in_useFadingSuns");

		}
		catch (MissingResourceException mrex)
		{
			mrex.printStackTrace();
		}
		finally
		{
			gameModesProperties = null;
		}
	}


	private JCheckBoxMenuItem dndMode = new JCheckBoxMenuItem(Constants.DND_MODE);
	private JCheckBoxMenuItem deadlandsMode = new JCheckBoxMenuItem(Constants.DEADLANDS_MODE);
	private JCheckBoxMenuItem FSd20Mode = new JCheckBoxMenuItem(Constants.FADINGSUNSD20_MODE);
//	private JCheckBoxMenuItem hackmasterMode = new JCheckBoxMenuItem(Constants.HACKMASTER_MODE);
	private JCheckBoxMenuItem l5rMode = new JCheckBoxMenuItem(Constants.L5R_MODE);
	private JCheckBoxMenuItem sidewinderMode = new JCheckBoxMenuItem(Constants.SIDEWINDER_MODE);
	private JCheckBoxMenuItem SSd20Mode = new JCheckBoxMenuItem(Constants.SOVEREIGNSTONED20_MODE);
	private JCheckBoxMenuItem starWarsMode = new JCheckBoxMenuItem(Constants.STARWARS_MODE);
	private JCheckBoxMenuItem weirdWarsMode = new JCheckBoxMenuItem(Constants.WEIRDWARS_MODE);
	private JCheckBoxMenuItem wheelMode = new JCheckBoxMenuItem(Constants.WHEELOFTIME_MODE);
	private CheckBoxListener checkBoxHandler = new CheckBoxListener();

	/** Creates new form Options */
	public GameModes()
	{
		setText(in_gameModes);
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

		deadlandsMode.setEnabled(true);
		dndMode.setEnabled(true);
		FSd20Mode.setEnabled(true);
		//hackmasterMode.setEnabled(false);
		l5rMode.setEnabled(false);
		sidewinderMode.setEnabled(false);
		starWarsMode.setEnabled(true);
		SSd20Mode.setEnabled(true);
		weirdWarsMode.setEnabled(false);
		wheelMode.setEnabled(true);

		this.add(deadlandsMode);
		deadlandsMode.setToolTipText(in_useDeadlands);
		deadlandsMode.setMnemonic('L');
		deadlandsMode.addActionListener(checkBoxHandler);

		this.add(dndMode);
		dndMode.setToolTipText(in_useDnD);
		dndMode.setMnemonic('D');
		dndMode.addActionListener(checkBoxHandler);

		this.add(FSd20Mode);
		FSd20Mode.setToolTipText(in_useFadingSuns);
		FSd20Mode.setMnemonic('F');
		FSd20Mode.addActionListener(checkBoxHandler);

//		this.add(hackmasterMode);
//		hackmasterMode.setToolTipText(in_useHackMaster);
//		hackmasterMode.setMnemonic('H');
//		hackmasterMode.addActionListener(checkBoxHandler);

		this.add(l5rMode);
		l5rMode.setToolTipText(in_useL5R);
		l5rMode.setMnemonic('L');
		l5rMode.addActionListener(checkBoxHandler);

		this.add(sidewinderMode);
		sidewinderMode.setToolTipText(in_useSidewinder);
		sidewinderMode.setMnemonic('I');
		sidewinderMode.addActionListener(checkBoxHandler);

		this.add(SSd20Mode);
		SSd20Mode.setToolTipText(in_useSovereign);
		SSd20Mode.setMnemonic('O');
		SSd20Mode.addActionListener(checkBoxHandler);

		this.add(starWarsMode);
		starWarsMode.setToolTipText(in_useStarWars);
		starWarsMode.setMnemonic('S');
		starWarsMode.addActionListener(checkBoxHandler);

		this.add(weirdWarsMode);
		weirdWarsMode.setToolTipText(in_useWeirdWars);
		weirdWarsMode.setMnemonic('E');
		weirdWarsMode.addActionListener(checkBoxHandler);

		this.add(wheelMode);
		wheelMode.setToolTipText(in_useWheelTime);
		wheelMode.setMnemonic('W');
		wheelMode.addActionListener(checkBoxHandler);

		updateMenu();
	}

	public void updateMenu()
	{
		dndMode.setSelected(Globals.isDndMode());
		deadlandsMode.setSelected(Globals.isDeadlandsMode());
		FSd20Mode.setSelected(Globals.isFSd20Mode());
//		hackmasterMode.setSelected(Globals.isHackMasterMode());
		l5rMode.setSelected(Globals.isL5rMode());
		sidewinderMode.setSelected(Globals.isSidewinderMode());
		SSd20Mode.setSelected(Globals.isSSd20Mode());
		starWarsMode.setSelected(Globals.isStarWarsMode());
		weirdWarsMode.setSelected(Globals.isWeirdWarsMode());
		wheelMode.setSelected(Globals.isWheelMode());

		if (Globals.getRootFrame() != null)
			((PCGen_Frame1)Globals.getRootFrame()).setGameModeTitle();
	}

	/**
	 * This class is used to respond to clicks on the check boxes.
	 */
	private final class CheckBoxListener implements ActionListener
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			final Object source = actionEvent.getSource();
			String tempGameMode = Constants.DND_MODE;

			if (source == null)
			{
				return;
			}

			((JCheckBoxMenuItem)source).requestFocus();
			if (source == deadlandsMode)
			{
				tempGameMode = Constants.DEADLANDS_MODE;
			}
			else if (source == dndMode)
			{
				tempGameMode = Constants.DND_MODE;
			}
			else if (source == FSd20Mode)
			{
				tempGameMode = Constants.FADINGSUNSD20_MODE;
			}
//			else if (source == hackmasterMode)
//			{
//				tempGameMode = Constants.HACKMASTER_MODE;
//			}
			else if (source == l5rMode)
			{
				tempGameMode = Constants.L5R_MODE;
			}
			else if (source == sidewinderMode)
			{
				tempGameMode = Constants.SIDEWINDER_MODE;
			}
			else if (source == SSd20Mode)
			{
				tempGameMode = Constants.SOVEREIGNSTONED20_MODE;
			}
			else if (source == starWarsMode)
			{
				tempGameMode = Constants.STARWARS_MODE;
			}
			else if (source == weirdWarsMode)
			{
				tempGameMode = Constants.WEIRDWARS_MODE;
			}
			else if (source == wheelMode)
			{
				tempGameMode = Constants.WHEELOFTIME_MODE;
			}

			if (!Globals.getGameMode().equals(tempGameMode))
			{
				Globals.setGameMode(tempGameMode);
				Globals.loadAttributeNames();
				updateMenu();
				((MainSource)((PCGen_Frame1)Globals.getRootFrame()).getBaseTabbedPanel().getComponent(0)).changedGameMode();
			}
		}
	}
}
