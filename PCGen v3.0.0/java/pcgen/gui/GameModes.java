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
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.persistence.PersistenceLayerException;

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
	private static String in_useCoCd20;
	private static String in_useDeadlands;
	private static String in_useDnD;
	private static String in_useSpycraft;
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
			gameModesProperties = ResourceBundle.getBundle("pcgen/gui/prop/LanguageBundle", currentLocale);
			in_gameModes = gameModesProperties.getString("in_gameModes");
			in_useCoCd20 = gameModesProperties.getString("in_useCoCd20");
			in_useDeadlands = gameModesProperties.getString("in_useDeadlands");

			in_useDnD = gameModesProperties.getString("in_useDnD");
			in_useSpycraft = gameModesProperties.getString("in_useSpycraft");
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


	//private JCheckBoxMenuItem CoCd20Mode = new JCheckBoxMenuItem(Constants.HWNMNBSOL_MODE);
	private JCheckBoxMenuItem CoCd20Mode = new JCheckBoxMenuItem("Call of Cthulhu D20");
	//private JCheckBoxMenuItem dndMode = new JCheckBoxMenuItem(Constants.DND_MODE);
	// For now, rename to Standard DnD to help distinguish from things
	// like Forgotten Realms or Living City
	private JMenu dndCampaigns = new JMenu("DnD");
	private JCheckBoxMenuItem dndMode = new JCheckBoxMenuItem("Standard");
	private JCheckBoxMenuItem dndFRMode = new JCheckBoxMenuItem("Forgotten Realms");
	private JCheckBoxMenuItem dndLCMode = new JCheckBoxMenuItem("Living City");
	private JCheckBoxMenuItem dndLGMode = new JCheckBoxMenuItem("Living Greyhawk");
	private JCheckBoxMenuItem deadlandsMode = new JCheckBoxMenuItem(Constants.DEADLANDS_MODE);
	//private JCheckBoxMenuItem FSd20Mode = new JCheckBoxMenuItem(Constants.FADINGSUNSD20_MODE);
	private JCheckBoxMenuItem FSd20Mode = new JCheckBoxMenuItem("Fading Suns D20");
//	private JCheckBoxMenuItem hackmasterMode = new JCheckBoxMenuItem(Constants.HACKMASTER_MODE);
	private JCheckBoxMenuItem l5rMode = new JCheckBoxMenuItem(Constants.L5R_MODE);
	private JCheckBoxMenuItem sidewinderMode = new JCheckBoxMenuItem(Constants.SIDEWINDER_MODE);
	//private JCheckBoxMenuItem SSd20Mode = new JCheckBoxMenuItem(Constants.SOVEREIGNSTONED20_MODE);
	private JCheckBoxMenuItem SSd20Mode = new JCheckBoxMenuItem("Sovereign Stone D20");
	//private JCheckBoxMenuItem starWarsMode = new JCheckBoxMenuItem(Constants.STARWARS_MODE);
	private JCheckBoxMenuItem starWarsMode = new JCheckBoxMenuItem("Star Wars");
	//private JCheckBoxMenuItem spycraftMode = new JCheckBoxMenuItem(Constants.SPYCRAFT_MODE);
	private JCheckBoxMenuItem spycraftMode = new JCheckBoxMenuItem("Spycraft");
	//private JCheckBoxMenuItem weirdWarsMode = new JCheckBoxMenuItem(Constants.WEIRDWARS_MODE);
	private JCheckBoxMenuItem weirdWarsMode = new JCheckBoxMenuItem("Weird Wars");
	//private JCheckBoxMenuItem wheelMode = new JCheckBoxMenuItem(Constants.WHEELOFTIME_MODE);
	private JCheckBoxMenuItem wheelMode = new JCheckBoxMenuItem("Wheel of Time");
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
		CoCd20Mode.setEnabled(false);
		deadlandsMode.setEnabled(true);
		dndMode.setEnabled(true);
		dndFRMode.setEnabled(false);
		dndLCMode.setEnabled(false);
		dndLGMode.setEnabled(false);
		FSd20Mode.setEnabled(true);
		//hackmasterMode.setEnabled(false);
		l5rMode.setEnabled(false);
		sidewinderMode.setEnabled(false);
		spycraftMode.setEnabled(true);
		starWarsMode.setEnabled(true);
		SSd20Mode.setEnabled(true);
		weirdWarsMode.setEnabled(false);
		wheelMode.setEnabled(true);

		this.add(dndCampaigns);
		dndCampaigns.setMnemonic('D');

		dndCampaigns.add(dndMode);
		Utility.setDescription(dndMode, in_useDnD);
		dndMode.setMnemonic('S');
		dndMode.addActionListener(checkBoxHandler);

		dndCampaigns.addSeparator();

		dndCampaigns.add(dndFRMode);
		dndMode.setMnemonic('F');

		dndCampaigns.add(dndLCMode);
		dndMode.setMnemonic('L');

		dndCampaigns.add(dndLGMode);
		dndMode.setMnemonic('G');

		this.addSeparator();

		this.add(CoCd20Mode);
		Utility.setDescription(CoCd20Mode, in_useCoCd20);
		CoCd20Mode.setMnemonic('C');
		CoCd20Mode.addActionListener(checkBoxHandler);

		this.add(deadlandsMode);
		Utility.setDescription(deadlandsMode, in_useDeadlands);
		deadlandsMode.setMnemonic('L');
		deadlandsMode.addActionListener(checkBoxHandler);

		this.add(FSd20Mode);
		Utility.setDescription(FSd20Mode, in_useFadingSuns);
		FSd20Mode.setMnemonic('F');
		FSd20Mode.addActionListener(checkBoxHandler);

//		this.add(hackmasterMode);
//		Utility.setDescription(hackmasterMode,in_useHackMaster);
//		hackmasterMode.setMnemonic('H');
//		hackmasterMode.addActionListener(checkBoxHandler);

		this.add(l5rMode);
		Utility.setDescription(l5rMode, in_useL5R);
		l5rMode.setMnemonic('5');
		l5rMode.addActionListener(checkBoxHandler);

		this.add(sidewinderMode);
		Utility.setDescription(sidewinderMode, in_useSidewinder);
		sidewinderMode.setMnemonic('I');
		sidewinderMode.addActionListener(checkBoxHandler);

		this.add(SSd20Mode);
		Utility.setDescription(SSd20Mode, in_useSovereign);
		SSd20Mode.setMnemonic('O');
		SSd20Mode.addActionListener(checkBoxHandler);

		this.add(spycraftMode);
		Utility.setDescription(spycraftMode, in_useSpycraft);
		spycraftMode.setMnemonic('P');
		spycraftMode.addActionListener(checkBoxHandler);

		this.add(starWarsMode);
		Utility.setDescription(starWarsMode, in_useStarWars);
		starWarsMode.setMnemonic('S');
		starWarsMode.addActionListener(checkBoxHandler);

		this.add(weirdWarsMode);
		Utility.setDescription(weirdWarsMode, in_useWeirdWars);
		weirdWarsMode.setMnemonic('E');
		weirdWarsMode.addActionListener(checkBoxHandler);

		this.add(wheelMode);
		Utility.setDescription(wheelMode, in_useWheelTime);
		wheelMode.setMnemonic('W');
		wheelMode.addActionListener(checkBoxHandler);

		updateMenu();
	}

	public void updateMenu()
	{
		CoCd20Mode.setSelected(Globals.isCoCd20Mode());
		dndMode.setSelected(Globals.isDndMode());
		deadlandsMode.setSelected(Globals.isDeadlandsMode());
		FSd20Mode.setSelected(Globals.isFSd20Mode());
//		hackmasterMode.setSelected(Globals.isHackMasterMode());
		l5rMode.setSelected(Globals.isL5rMode());
		sidewinderMode.setSelected(Globals.isSidewinderMode());
		SSd20Mode.setSelected(Globals.isSSd20Mode());
		spycraftMode.setSelected(Globals.isSpycraftMode());
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
			if (source == CoCd20Mode)
			{
				tempGameMode = Constants.HWNMNBSOL_MODE;
			}
			else if (source == deadlandsMode)
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
			else if (source == spycraftMode)
			{
				tempGameMode = Constants.SPYCRAFT_MODE;
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
				try
				{
					Globals.loadAttributeNames();
				}
				catch (PersistenceLayerException e)
				{
					Globals.errorPrint("Cannot load attribute names", e);
				}
				updateMenu();
				((MainSource)(Globals.getRootFrame()).getBaseTabbedPane().getComponent(0)).changedGameMode();
			}
		}
	}
}
