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
 * Last Edited: $Date: 2006/02/21 01:11:12 $
 *
 */

package pcgen.gui.tabs;

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
import pcgen.gui.PCGen_Frame1;
import pcgen.gui.utils.ChooserFactory;
import pcgen.gui.utils.ChooserInterface;
import pcgen.gui.utils.Utility;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 * This class is responsible for drawing Special Ability, Language and Weapon Prefociency sections.
 *
 * @author  Bryan McRoberts (merton_monk@yahoo.com)
 * @version $Revision: 1.1 $
 */

public final class InfoAbilities extends JPanel
{
	static final long serialVersionUID = -7316622743996841985L;
	private static boolean needsUpdate = true;
	private static PlayerCharacter aPC = Globals.getCurrentPC();
	private JTextArea languageText = new JTextArea();
	private JTextArea saText = new JTextArea();
	private JButton weaponButton = null;
	private JTextArea weaponText = new JTextArea();

	/**
	 * Constructor.
	 */
	public InfoAbilities()
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
		weaponButton = new JButton(PropertyFactory.getString("in_optProfs"));
		pcgen.gui.utils.Utility.setDescription(weaponButton, PropertyFactory.getString("in_iaOptTip"));
		weaponButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				weaponSelectPressed();
			}
		});
		bPanel.add(weaponButton);
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
	 */
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
		if (weaponButton != null)
		{
			ArrayList bonusCategory = getOptionalWeaponProficiencies();

			weaponButton.setEnabled(bonusCategory != null && bonusCategory.size() > 0);
		}

		SortedSet weaponProfs = aPC.getWeaponProfList();
		if (weaponProfs.size() > 0)
		{
			weaponText.setText(pcgen.core.utils.Utility.commaDelimit(weaponProfs));
		}
		else
		{
			weaponText.setText(Constants.s_NONE);
		}
		weaponText.setCaretPosition(0);
	}

	/**
	 * <code>updateCharacterInfo</code> update data when changing PC.
	 */
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
			saText.setText(pcgen.core.utils.Utility.commaDelimit(specialAbilities));
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
		if (Skill.chooseSpokenLanguage(aPC))
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
					else if (Logging.isDebugMode())
					{
						Logging.debugPrint(aLang.getName() + " excluded--prereqs not met");
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

	private ArrayList getOptionalWeaponProficiencies()
	{
		if (aPC != null)
		{
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
					if (Logging.isDebugMode())
					{
						Logging.debugPrint("TEMP WEAP=" + aTemplate.getWeaponProfBonus());
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

			return bonusCategory;
		}

		return null;
	}

	/**
	 * This method is run when the weapon proficiency button is pressed.
	 */
	private void weaponSelectPressed()
	{
		if (aPC != null)
		{
			ArrayList bonusCategory = getOptionalWeaponProficiencies();

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
						((PCClass) profBonusObject).getChoices("WEAPONPROF|1|" + pcgen.core.utils.Utility.join(profWeapons, "[WEAPONPROF]|") + "[WEAPONPROF]", new ArrayList());
					}
					else if (profBonusObject instanceof Race)
					{
						profWeapons = ((Race) profBonusObject).getWeaponProfBonus();
						((Race) profBonusObject).getChoices("WEAPONPROF|1|" + pcgen.core.utils.Utility.join(profWeapons, "[WEAPONPROF]|") + "[WEAPONPROF]", new ArrayList());
					}
					else if (profBonusObject instanceof PCTemplate)
					{
						profWeapons = ((PCTemplate) profBonusObject).getWeaponProfBonus();
						((PCTemplate) profBonusObject).getChoices("WEAPONPROF|1|" + pcgen.core.utils.Utility.join(profWeapons, "[WEAPONPROF]|") + "[WEAPONPROF]", new ArrayList());
					}
				}

				aPC.setDirty(true);

				aPC.aggregateFeatList();

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

	private void ensureFocus()
	{
		//
		// Get focus in case the chooser popped up
		//
		getRootPane().getParent().requestFocus();
	}
}
