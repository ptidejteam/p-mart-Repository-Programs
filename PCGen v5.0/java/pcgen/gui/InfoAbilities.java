/*
 * InfoAbilities.java
 * Copyright 2002 (C) Bryan McRoberts
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
 * Created on April 21, 2001, 2:15 PM
 * Modified June 5, 2001 by Bryan McRoberts (merton_monk@yahoo.com)
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:07:48 $
 *
 */

package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import pcgen.core.CharacterDomain;
import pcgen.core.Constants;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.core.SpecialAbility;
import pcgen.util.PropertyFactory;

/**
 * This class is responsible for drawing Special Ability, Language and Weapon Prefociency sections.
 *
 * @author  Bryan McRoberts (merton_monk@yahoo.com)
 * @version $Revision: 1.1 $
 */

final class InfoAbilities extends JPanel
{
	private static boolean needsUpdate = true;
//	private JCheckBox purchaseCheckBox = new JCheckBox("Purchase Mode:");
	//private JCheckBox unlimitedCheckBox = new JCheckBox("Unlimited Stat Pool:");
	//private JTextField poolText = new JTextField();
	//private JTextField forceText = new JTextField();
	//private JTextField darkText = new JTextField();
	private static PlayerCharacter aPC = Globals.getCurrentPC();
	private JTextArea languageText = new JTextArea();
	private JTextArea saText = new JTextArea();
	private JTextArea weaponText = new JTextArea();

	InfoAbilities()
	{
		setName(Constants.tabNames[Constants.TAB_ABILITIES]);
		initComponents();
	}

	private void initComponents()
	{
		this.setLayout(new BorderLayout());
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		GridBagLayout gridbag = new GridBagLayout();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTH;

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(gridbag);

		// Languages setup
		JPanel langPanel = new JPanel();
		langPanel.setLayout(new BorderLayout());
		JPanel lPanel = new JPanel();
		lPanel.setLayout(new FlowLayout());
		lPanel.add(new JLabel(PropertyFactory.getString("in_languages")));
		JButton langButton = new JButton(PropertyFactory.getString("in_other"));
		langButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				racialLanguageSelectPressed();
			}
		});

		JButton langButton2 = new JButton(PropertyFactory.getString("in_skill"));
		langButton2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				skillLanguageSelectPressed();
			}
		});

		lPanel.add(langButton2);
		lPanel.add(langButton);
		langPanel.add(lPanel, BorderLayout.NORTH);

		JScrollPane languageScroll = new JScrollPane();
		languageText.setLineWrap(true);
		languageText.setWrapStyleWord(true);
		languageText.setEditable(false);
		languageScroll.setViewportView(languageText);
		langPanel.add(languageScroll, BorderLayout.CENTER);

		Utility.buildConstraints(gbc, 0, 0, 1, 1, 1.0, .5);
		gridbag.setConstraints(langPanel, gbc);
		bottomPanel.add(langPanel);

		// Special abilities panel setup
		JPanel specialPanel = new JPanel();
		specialPanel.setLayout(new BorderLayout());
		JPanel sPanel = new JPanel();
		sPanel.setLayout(new FlowLayout());
		sPanel.add(new JLabel(PropertyFactory.getString("in_specialAb")));
		JButton spAddButton = new JButton(PropertyFactory.getString("in_add"));
		spAddButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addSpecialAbility();
			}
		});
		sPanel.add(spAddButton);
		JButton spRemButton = new JButton(PropertyFactory.getString("in_remove"));
		spRemButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				removeSpecialAbility();
			}
		});
		sPanel.add(spRemButton);

		specialPanel.add(sPanel, BorderLayout.NORTH);

		JScrollPane saScroll = new JScrollPane();
		saText.setLineWrap(true);
		saText.setWrapStyleWord(true);
		saText.setEditable(false);
		saScroll.setViewportView(saText);
		specialPanel.add(saScroll, BorderLayout.CENTER);

		Utility.buildConstraints(gbc, 0, 1, 1, 1, 0.0, .5);
		gridbag.setConstraints(specialPanel, gbc);
		bottomPanel.add(specialPanel);

		// Weapon profs setup
		JPanel weaponProfPanel = new JPanel();
		weaponProfPanel.setLayout(new BorderLayout());
		JPanel bPanel = new JPanel();
		bPanel.setLayout(new FlowLayout());
		JLabel aLabel = new JLabel(PropertyFactory.getString("in_weaProfs"));
		bPanel.add(aLabel);
		JButton aButton = new JButton(PropertyFactory.getString("in_optProfs"));
		pcgen.gui.Utility.setDescription(aButton, PropertyFactory.getString("in_iaOptTip"));
		aButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				weaponSelectPressed();
			}
		});
		bPanel.add(aButton);
		weaponProfPanel.add(bPanel, BorderLayout.NORTH);

		weaponText.setLineWrap(true);
		weaponText.setWrapStyleWord(true);
		weaponText.setEditable(false);
		JScrollPane weaponScroll = new JScrollPane();
		weaponScroll.setViewportView(weaponText);
		weaponProfPanel.add(weaponScroll, BorderLayout.CENTER);

		Utility.buildConstraints(gbc, 0, 2, 1, 1, 0.0, .5);
		gridbag.setConstraints(weaponProfPanel, gbc);
		bottomPanel.add(weaponProfPanel);

		// Now finish the layout of the outer panel
		add(bottomPanel, BorderLayout.CENTER);

		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				formComponentShown();
			}
		});

	}

	/**
	 * This is called when the tab is shown.
	 **/
	private void formComponentShown()
	{
		requestFocus();
		PCGen_Frame1.getStatusBar().setText(PropertyFactory.getString("in_iaLangTip"));
		updateCharacterInfo();
	}

	private void addSpecialAbility()
	{
		if (aPC == null || aPC.getClassList().isEmpty())
		{
			return;
		}
		Object selectedValue = JOptionPane.showInputDialog(null, PropertyFactory.getString("in_iaSpeAbTip"), Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE, null, null, "");
		if (selectedValue != null)
		{
			PCClass aClass = (PCClass) aPC.getClassList().get(0);
			String aString = ((String) selectedValue).trim();
			SpecialAbility sa = new SpecialAbility(aString, "PCCLASS|" + aClass.getName() + "|0");
			aClass.addSpecialAbilityToList(sa);
			aClass.addSave(aString);
			aPC = null; // forces everything to re-display
			updateCharacterInfo();
		}
	}

	private void removeSpecialAbility()
	{
		ArrayList aList = new ArrayList();
		ArrayList bList = new ArrayList();
		ArrayList cList = new ArrayList();
		for (Iterator i = aPC.getClassList().iterator(); i.hasNext();)
		{
			PCClass aClass = (PCClass) i.next();
			for (Iterator ii = aClass.getSpecialAbilityList().iterator(); ii.hasNext();)
			{
				SpecialAbility sa = (SpecialAbility) ii.next();
				if (sa.getSource().endsWith("|0"))
				{
					aList.add(sa.getName());
					cList.add(sa);
				}
			}
		}

		ChooserInterface lc = ChooserFactory.getChooserInstance();
		lc.setVisible(false);
		lc.setTitle(PropertyFactory.getString("in_iaReSpeAb"));
		lc.setMessageText(PropertyFactory.getString("in_iaSelSpeAb"));
		lc.setAvailableList(aList);
		lc.setSelectedList(bList);
		lc.setPool(aList.size());
		lc.setPoolFlag(false);
		lc.show();

		for (Iterator i = lc.getSelectedList().iterator(); i.hasNext();)
		{
			final String aString = (String) i.next();
			final int ix = aList.indexOf(aString);
			if (ix < 0 || ix >= cList.size())
			{
				continue;
			}
			SpecialAbility sa = (SpecialAbility) cList.get(ix);
			final String bString = sa.getSource();
			PCClass aClass = aPC.getClassNamed(bString.substring(bString.indexOf("|") + 1, bString.lastIndexOf("|")));
			if (aClass == null)
			{
				continue;
			}
			aClass.removeSave(sa.getName());
			aClass.getSpecialAbilityList().remove(sa);
		}
		aPC = null; // forces everything to re-display
		updateCharacterInfo();
		ensureFocus();
	}

	private void showWeaponProfList()
	{
		SortedSet weaponProfs = aPC.getWeaponProfList();
		if (weaponProfs.size() > 0)
		{
			weaponText.setText(pcgen.core.Utility.commaDelimit(weaponProfs));
		}
		else
		{
			weaponText.setText(Constants.s_NONE);
		}
		weaponText.setCaretPosition(0);
	}

	/**
	 * <code>updateCharacterInfo</code> update data when changing PC
	 **/
	public void updateCharacterInfo()
	{
		final PlayerCharacter bPC = Globals.getCurrentPC();
		needsUpdate = !(bPC != null && bPC.equals(aPC)) || (bPC == null && aPC != null);
		aPC = bPC;
		if (aPC == null)
		{
			return;
		}
		ArrayList specialAbilities = aPC.getSpecialAbilityTimesList();
		aPC.getAutoLanguages();
		String languages = aPC.getLanguagesListNames();
		if (specialAbilities.size() > 0)
		{
			saText.setText(pcgen.core.Utility.commaDelimit(specialAbilities));
		}
		else
		{
			saText.setText(Constants.s_NONE);
		}
		if (languages.length() > 0)
		{
			languageText.setText(languages);
		}
		else
		{
			languageText.setText(Constants.s_NONE);
		}
		showWeaponProfList();
		needsUpdate = false;
	}

	private void skillLanguageSelectPressed()
	{
		if (Utility.chooseSpokenLanguage(aPC))
		{
			updateCharacterInfo();
		}
		ensureFocus();
	}

	private void racialLanguageSelectPressed()
	{
		if (aPC != null)
		{
			aPC.setDirty(true);

			SortedSet autoLangs = aPC.getAutoLanguages();

			ArrayList availableLangs = new ArrayList();
			ArrayList selectedLangNames = new ArrayList();
			ArrayList excludedLangs = new ArrayList();
			ArrayList selLangs = new ArrayList();

			Skill speakLanguage = null;
			for (Iterator a = aPC.getSkillList().iterator(); a.hasNext();)
			{
				Skill aSkill = (Skill) a.next();
				if (aSkill.getChoiceString().indexOf("Language") >= 0)
				{
					speakLanguage = aSkill;
				}
			}

			int numLanguages = aPC.languageNum(false);

			for (Iterator i = Globals.getLanguageList().iterator(); i.hasNext();)
			{
				final Language aLang = (Language) i.next();
				if (aLang != null)
				{
					if (aLang.passesPreReqTestsForList(aPC, null, aLang.getPreReqList()))
					{
						availableLangs.add(aLang);
					}
					else if (Globals.isDebugMode())
					{
						Globals.debugPrint(aLang.getName() + " excluded--prereqs not met");
					}
				}
			}

			//
			// Only show selections that are not automatically
			// granted or granted via the "Speak Language" skill
			// Remove any language selected via "Speak Language"
			// from the list of available selections
			//
			for (Iterator i = aPC.getLanguagesList().iterator(); i.hasNext();)
			{
				final Language aLang = (Language) i.next();
				boolean addLang = false;
				if ((speakLanguage != null) && speakLanguage.containsAssociated(aLang.getName()))
				{
					addLang = false;
				}
				else if (!autoLangs.contains(aLang))
				{
					addLang = true;
				}

				if (addLang)
				{
					selectedLangNames.add(aLang.getName());
				}
				else
				{
					availableLangs.remove(aLang);
					excludedLangs.add(aLang);
				}
			}

			Globals.sortPObjectList(availableLangs);

			ChooserInterface lc = ChooserFactory.getChooserInstance();
			lc.setVisible(false);
			lc.setAvailableList(availableLangs);
			lc.setSelectedList(selectedLangNames);
			lc.setPool(numLanguages - selectedLangNames.size());
			lc.setPoolFlag(false);
			lc.show();

			if (lc.getSelectedList().size() > (numLanguages))
			{
				return;
			}

			for (Iterator e = lc.getSelectedList().iterator(); e.hasNext();)
			{
				String aString = (String) e.next();
				Language aLang = Globals.getLanguageNamed(aString);
				if (aLang != null)
				{
					selLangs.add(aLang);
				}
			}

			aPC.getLanguagesList().clear();
			aPC.getLanguagesList().addAll(selLangs);
			aPC.getLanguagesList().addAll(excludedLangs);

			updateCharacterInfo();
			ensureFocus();
		}
	}

	/**
	 * This method is run when the weapon proficiency button is pressed.
	 */
	private void weaponSelectPressed()
	{
		if (aPC != null)
		{
			//
			// Get a list of the race/class(es) that have a bonus weapon allowed
			//
			ArrayList bonusCategory = new ArrayList();
			final Race pcRace = aPC.getRace();
			if (pcRace != null)
			{
				if (pcRace.getWeaponProfBonus().size() != 0)
				{
					bonusCategory.add(pcRace);
				}
			}
			for (Iterator e = aPC.getClassList().iterator(); e.hasNext();)
			{
				final PCClass aClass = (PCClass) e.next();
				if (aClass.getWeaponProfBonus().size() != 0)
				{
					bonusCategory.add(aClass);
				}
			}
			for (Iterator e = aPC.getTemplateList().iterator(); e.hasNext();)
			{
				final PCTemplate aTemplate = (PCTemplate) e.next();
				if (aTemplate.getWeaponProfBonusSize() != 0)
				{
					bonusCategory.add(aTemplate);
					if (Globals.isDebugMode())
					{
						Globals.debugPrint("TEMP WEAP=" + aTemplate.getWeaponProfBonus());
					}
				}
			}

			final ArrayList pcDomains = aPC.getCharacterDomainList();
			for (Iterator e = pcDomains.iterator(); e.hasNext();)
			{
				final CharacterDomain aCD = (CharacterDomain) e.next();
				if ((aCD.isFromPCClass() || aCD.isFromFeat()) && (aCD.toString().length() != 0) && aCD.getDomain().getChoiceString().startsWith("WEAPONPROF|"))
				{
					bonusCategory.add(aCD);
				}
			}

			if (bonusCategory.size() == 0)
			{
				JOptionPane.showMessageDialog(null, PropertyFactory.getString("in_iaNoOptProfs"), Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			int selIdx = 0;
			for (; ;)
			{
				//
				// If there is only one set of choices allowed, then use it
				//
				Object profBonusObject;
				if (bonusCategory.size() == 1)
				{
					profBonusObject = bonusCategory.get(0);
				}
				else
				{
					for (; ;)
					{
						Object selectedValue = JOptionPane.showInputDialog(null, PropertyFactory.getString("in_iaMultiChoice1") + Constants.s_LINE_SEP + PropertyFactory.getString("in_iaMultiChoice2"), Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE, null, bonusCategory.toArray(), bonusCategory.get(selIdx));
						if (selectedValue != null)
						{
							profBonusObject = selectedValue;
							selIdx = bonusCategory.indexOf(selectedValue);
							break;
						}
						ensureFocus();
						return;
					}
				}
				if (profBonusObject instanceof CharacterDomain)
				{
					final Domain aDomain = ((CharacterDomain) profBonusObject).getDomain();
					aDomain.getChoices(aDomain.getChoiceString(), new ArrayList());
				}
				else
				{
					ArrayList profWeapons;
					if (profBonusObject instanceof PCClass)
					{
						profWeapons = ((PCClass) profBonusObject).getWeaponProfBonus();
						((PCClass) profBonusObject).getChoices("WEAPONPROF|1|" + pcgen.core.Utility.unSplit(profWeapons, "[WEAPONPROF]|") + "[WEAPONPROF]", new ArrayList());
					}
					else if (profBonusObject instanceof Race)
					{
						profWeapons = ((Race) profBonusObject).getWeaponProfBonus();
						((Race) profBonusObject).getChoices("WEAPONPROF|1|" + pcgen.core.Utility.unSplit(profWeapons, "[WEAPONPROF]|") + "[WEAPONPROF]", new ArrayList());
					}
					else if (profBonusObject instanceof PCTemplate)
					{
						profWeapons = ((PCTemplate) profBonusObject).getWeaponProfBonus();
						((PCTemplate) profBonusObject).getChoices("WEAPONPROF|1|" + pcgen.core.Utility.unSplit(profWeapons, "[WEAPONPROF]|") + "[WEAPONPROF]", new ArrayList());
					}
				}

				aPC.setDirty(true);

//				aPC.setAutomaticFeatsStable(false);
				aPC.aggregateFeatList();
				//updateModelSelected();

				showWeaponProfList();
				if (bonusCategory.size() == 1)
				{
					break;
				}
			}
			ensureFocus();
			return;
		}
	}


/*
	private final void updateCharacterForce()
	{
		if (aPC != null)
		{
			final String forceAmt = forceText.getText();
			if (!forceAmt.equals(aPC.getStrFPoints()))
			{
				aPC.setFPoints(forceAmt);
				aPC.setDirty(true);
			}
			forceText.setText(aPC.getStrFPoints());
		}
	}

	private boolean sensitiveCheck()
	{
		final String sens = "Force Sensitive";
		return (aPC.hasFeat(sens) || aPC.hasFeatAutomatic(sens) ||
		  aPC.hasFeatVirtual(sens));
	}

	private final void updateCharacterDside()
	{
		if (aPC != null)
		{
			try
			{
				final String dsideAmt = darkText.getText();
				if (!dsideAmt.equals(aPC.getDPoints()))
				{
					aPC.setDPoints(dsideAmt);
					aPC.setDirty(true);
				}
			}
			catch (NumberFormatException nfe)
			{
				darkText.setText(aPC.getDPoints());
			}
		}
	}

	private void statTableMouseClicked(MouseEvent evt)
	{
		final int selectedStat = statTable.getSelectedRow();
		int stat = aPC.getStatList().getBaseStatFor(Globals.s_ATTRIBSHORT[selectedStat]);
		boolean makeChange = false;
		boolean checkPurchase = false;
		int increment = 0;

		final int column = statTable.columnAtPoint(evt.getPoint());
		switch (column)
		{
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
				break;

			case 5:
//				if (!Globals.s_ATTRIBROLL[selectedStat])
//					return;

				increment = 1;
				if (aPC.getTotalLevels() < 1 && stat >= SettingsHandler.getInitialStatMax() && !SettingsHandler.isPurchaseStatMode())
				{
					JOptionPane.showMessageDialog(null, "Cannot raise stat above " + new Integer(SettingsHandler.getInitialStatMax()).toString(), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				}
				else if ((!SettingsHandler.isPurchaseStatMode() || (SettingsHandler.isPurchaseStatMode() && aPC.getTotalLevels() > 0)) && aPC.getPoolAmount() < 1 && !SettingsHandler.isStatPoolUnlimited())
				{
					JOptionPane.showMessageDialog(null, "You have no pool points to spend.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				}
				else if (aPC.getTotalLevels() < 1 && stat >= SettingsHandler.getPurchaseScoreMax() && SettingsHandler.isPurchaseStatMode())
				{
					JOptionPane.showMessageDialog(null, "Cannot raise stat above " + SettingsHandler.getPurchaseScoreMax() + " in Purchase Mode", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				}
				else if (aPC.isNonability(selectedStat))
				{
					JOptionPane.showMessageDialog(null, "Cannot increment a nonability", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					makeChange = true;
					if (SettingsHandler.isPurchaseStatMode() && (aPC.getTotalLevels() == 0))
					{
						checkPurchase = true;
					}
					else if (!SettingsHandler.isPurchaseStatMode() || aPC.getTotalLevels() > 0)
					{
						aPC.setPoolAmount(Math.max(aPC.getPoolAmount() - 1, 0));
					}
				}
				break;
			case 6:
//				if (!Globals.s_ATTRIBROLL[selectedStat])
//					return;

				increment = -1;
/////////////////////////////////////////////////
// Yanked for WotC compliance
//				final int minPurchaseScore = SettingsHandler.getPurchaseScoreMin();
				final int minPurchaseScore = SettingsHandler.getPurchaseModeBaseStatScore();
/////////////////////////////////////////////////
				if (aPC.getTotalLevels() < 1 && (stat <= minPurchaseScore) && SettingsHandler.isPurchaseStatMode())
				{
					JOptionPane.showMessageDialog(null, "Cannot lower stat below " + minPurchaseScore + " in Purchase Mode", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				}
				else if (aPC.getTotalLevels() < 1 && (stat <= SettingsHandler.getInitialStatMin() ||
				  ((stat <= minPurchaseScore) && SettingsHandler.isPurchaseStatMode())))
				{
					JOptionPane.showMessageDialog(null, "Cannot lower stat below " + new Integer(SettingsHandler.getInitialStatMin()).toString(), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				}
				else if (aPC.isNonability(selectedStat))
				{
					JOptionPane.showMessageDialog(null, "Cannot decrement a nonability", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					makeChange = true;
					if (!SettingsHandler.isPurchaseStatMode() || aPC.getTotalLevels() > 0)
					{
						aPC.setPoolAmount(aPC.getPoolAmount() + 1);
					}
				}
				break;
			default:
				Globals.errorPrint("In InfoAbilities.statTableMouseClicked the column " + column + " is not handled.");
				break;
		}
		if (makeChange)
		{
			aPC.setDirty(true);
			((PCStat)aPC.getStatList().getStats().get(selectedStat)).setBaseScore(stat + increment);
			updatePool(checkPurchase);
			statTableModel.fireTableRowsUpdated(selectedStat, selectedStat);
			final PCGen_Frame1 rootFrame = (PCGen_Frame1)Globals.getRootFrame();
			if (aPC.getStatBonusTo("HP", "BONUS") != 0)
			{
				rootFrame.hpTotal_Changed();
			}
			// if INT changed then skill points need recalc
			// and there are all kinds of skills that have bonus
			// associated with stats, so just update the mother
			Globals.getRootFrame().forceUpdate_InfoSkills();

			// I could check for INT, WIS and CHA here, but then
			// there would probably be some custom class that uses
			// DEX or CON for spell info and that would be wack
			// so just update the mother
			Globals.getRootFrame().forceUpdate_InfoSpells();
		}
	}

	private int getUsedStatPool()
	{
		int i = 0;
		for (int stat = 0; stat < Globals.s_ATTRIBLONG.length; stat++)
		{
			final int statValue = aPC.getStatList().getBaseStatFor(Globals.s_ATTRIBSHORT[stat]);
			if (!Globals.s_ATTRIBROLL[stat])
			{
				continue;
			}
			if (statValue >= SettingsHandler.getPurchaseScoreMin() &&
			  statValue <= SettingsHandler.getPurchaseScoreMax())
			{
				i += SettingsHandler.getAbilityScoreCost(statValue - SettingsHandler.getPurchaseScoreMin());
			}
		}
		return i;
	}

	private void updatePool()
	{
		updatePool(false);
	}

	private void updatePool(boolean checkPurchasePoints)
	{
		final int usedStatPool = getUsedStatPool();
		if (checkPurchasePoints)
		{
			final int availablePool = SettingsHandler.getPurchaseModeMethodPool();
			if ((availablePool > 0) && (usedStatPool >= availablePool))
			{
				JOptionPane.showMessageDialog(null,
				  "You have reached or exceded the maximum points of " + availablePool + " as specified by the method \"" + SettingsHandler.getPurchaseModeMethodName() + "\"",
				  Constants.s_APPNAME,
				  JOptionPane.INFORMATION_MESSAGE);
			}
		}

//		int[] stats = new int[Globals.s_ATTRIBLONG.length];
		String bString = "";
		if (SettingsHandler.isPurchaseStatMode())
		{
			if (aPC.getTotalLevels() == 0)
			{
				aPC.setDirty(true);
				aPC.setCostPool(usedStatPool);
				aPC.setPoolAmount(usedStatPool);
			}
			bString = " (" + aPC.getCostPool() + ")";
		}
		StringBuffer aString = new StringBuffer(String.valueOf(aPC.getPoolAmount()));

		if (bString.length() > 0)
		{
			aString.append(bString);
		}
		poolText.setText(aString.toString());
	}
*/
/*
	protected class StatTableModel extends AbstractTableModel
	{
		public int getColumnCount()
		{
			return 7;
		}

		public Class getColumnClass(int columnIndex)
		{
			return getValueAt(0, columnIndex).getClass();
		}

		/* * <code>getRowCount()
		 * returns the number of rows. Gets the number of stats from Globals.s_ATTRIBLONG
		 * /
		public int getRowCount()
		{
			if (Globals.getCurrentPC() != null)
			{
				return Globals.s_ATTRIBLONG.length;
			}
			else
			{
				return 0;
			}
		}

		public String getColumnName(int columnIndex)
		{
			switch (columnIndex)
			{
				case 0:
					return "Stat";
				case 1:
					return "Score";
				case 2:
					return "Adj";
				case 3:
					return "Total";
				case 4:
					return "Mod";
				case 5:
					return "+";
				case 6:
					return "-";
				default:
					return "Out of Bounds";
			}
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if (columnIndex == 0)
			{
				if ((rowIndex >= 0) && (rowIndex < Globals.s_ATTRIBLONG.length))
					return Globals.s_ATTRIBLONG[rowIndex];
				else
					return "Out of Bounds";
			}
			final PlayerCharacter aPC = Globals.getCurrentPC();
			switch (columnIndex)
			{
				case 1:
					return new Integer(aPC.getStatList().getBaseStatFor(Globals.s_ATTRIBSHORT[rowIndex]));
				case 2:
					if (aPC.isNonability(rowIndex))
						return "-";
					return new Integer(aPC.getStatList().getTotalStatFor(Globals.s_ATTRIBSHORT[rowIndex]) - aPC.getStatList().getBaseStatFor(Globals.s_ATTRIBSHORT[rowIndex]));
				case 3:
					if (aPC.isNonability(rowIndex))
						return "--";
					return new Integer(aPC.getStatList().getTotalStatFor(Globals.s_ATTRIBSHORT[rowIndex]));
				case 4:
					if (aPC.isNonability(rowIndex))
						return new Integer(0);
					return new Integer(aPC.getStatList().getStatModFor(Globals.s_ATTRIBSHORT[rowIndex]));
				case 5:
					if (!Globals.s_ATTRIBROLL[rowIndex])
						return null;
					return "+";
				case 6:
					if (!Globals.s_ATTRIBROLL[rowIndex])
						return null;
					return "-";
				default:
					return "Out of Bounds";
			}
		}
	}

	protected class RendererEditor implements TableCellRenderer
	{
		JButton plusButton = new JButton("+");
		JButton minusButton = new JButton("-");
		DefaultTableCellRenderer def = new DefaultTableCellRenderer();

		public RendererEditor()
		{
			def.setBackground(InfoAbilities.this.getBackground());
			def.setAlignmentX(Component.CENTER_ALIGNMENT);
			def.setHorizontalAlignment(SwingConstants.CENTER);
			plusButton.setPreferredSize(new Dimension(30, 24));
			plusButton.setMinimumSize(new Dimension(30, 24));
			plusButton.setMaximumSize(new Dimension(30, 24));
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
		  boolean isSelected, boolean hasFocus, int row, int column)
		{
			if (column == 5)
			{
				def.setText("+");
				def.setBorder(BorderFactory.createEtchedBorder());
				return def;
			}
			else if (column == 6)
			{
				def.setText("-");
				def.setBorder(BorderFactory.createEtchedBorder());
				return def;
			}
			return null;
		}
	}
*/

	private void ensureFocus()
	{
		//
		// Get focus in case the chooser popped up
		//
		getRootPane().getParent().requestFocus();
	}
}
