/*
 * InfoAbilities.java
 * Copyright 2001 (C) Thomas G. W. Epperly
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
 * Created on April 24, 2001, 9:04 PM
 */

package pcgen.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;

/**
 * Provide a panel to display a character's special abilities, languages,
 * and weapon proficiencies. There are three main text areas for each
 * of the three main pieces of information.
 *
 * @author Tom Epperly <tomepperly@home.com>
 * @version $Revision: 1.1 $
 */
public class InfoAbilities extends JPanel
{

	/** Creates new form InfoAbilities */
	public InfoAbilities()
	{
		initComponents();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		d_specAbilityLabel = new JLabel();
		d_specAbilitiesArea = new JScrollPane();
		d_specAbilityText = new JTextArea();
		d_languageArea = new JScrollPane();
		d_languageText = new JTextArea();
		d_weaponProfArea = new JScrollPane();
		d_weaponProfText = new JTextArea();
		d_languageLabel = new JLabel();
		d_languageChoose = new JButton();
		d_weaponProfLabel = new JLabel();
		d_weaponProfChoose = new JButton();

		setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints1;

		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				final PlayerCharacter aPC = Globals.getCurrentPC();
				aPC.setAggregateFeatsStable(true);
				aPC.setAutomaticFeatsStable(true);
				aPC.setVirtualFeatsStable(true);
				formComponentShown(evt);
			}
		});

		d_specAbilityLabel.setText("Special Abilities");
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.insets = new Insets(0, 0, 0, 10);
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.weighty = 1.0;
		add(d_specAbilityLabel, gridBagConstraints1);

		d_specAbilityText.setLineWrap(true);
		d_specAbilityText.setWrapStyleWord(true);
		d_specAbilityText.setEditable(false);
		d_specAbilitiesArea.setViewportView(d_specAbilityText);

		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.gridwidth = 2;
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.weighty = 50.0;
		add(d_specAbilitiesArea, gridBagConstraints1);

		d_languageText.setLineWrap(true);
		d_languageText.setWrapStyleWord(true);
		d_languageText.setEditable(false);
		d_languageArea.setViewportView(d_languageText);

		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 3;
		gridBagConstraints1.gridwidth = 2;
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.weighty = 25.0;
		add(d_languageArea, gridBagConstraints1);

		d_weaponProfText.setLineWrap(true);
		d_weaponProfText.setWrapStyleWord(true);
		d_weaponProfText.setEditable(false);
		d_weaponProfArea.setViewportView(d_weaponProfText);

		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 5;
		gridBagConstraints1.gridwidth = 2;
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.weighty = 50.0;
		add(d_weaponProfArea, gridBagConstraints1);

		d_languageLabel.setText("Languages");
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 2;
		gridBagConstraints1.insets = new Insets(0, 0, 0, 10);
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		add(d_languageLabel, gridBagConstraints1);

		d_languageChoose.setText("Choose");
		d_languageChoose.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				languageSelectPressed(evt);
			}
		});

		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.gridy = 2;
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.weightx = 1.0;
		add(d_languageChoose, gridBagConstraints1);

		d_weaponProfLabel.setText("Weapon Proficiencies");
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 4;
		gridBagConstraints1.insets = new Insets(0, 0, 0, 10);
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		add(d_weaponProfLabel, gridBagConstraints1);

		d_weaponProfChoose.setText("Choose");
		d_weaponProfChoose.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				weaponSelectPressed(evt);
			}
		});

		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.gridy = 4;
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.weightx = 1.0;
		add(d_weaponProfChoose, gridBagConstraints1);

	}

	/**
	 * This method is run when the weapon proficiency button is pressed.
	 */
	private void weaponSelectPressed(ActionEvent evt)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC != null)
		{
			aPC.setDirty(true);
			Chooser lc = new Chooser();
			lc.setVisible(false);
			SortedSet autoprofs = (SortedSet)aPC.getAutoWeaponProfs();
			SortedSet profs = (SortedSet)aPC.getWeaponProfList();
			ArrayList selected = new ArrayList(profs.size());
			int bonuschoices = aPC.getBonusWeaponChoices();
			SortedSet bonusProfs = new TreeSet();
			for (Iterator i = aPC.getBonusWeaponProfs().iterator(); i.hasNext();)
			{
				String bprof = i.next().toString();
				if (!profs.contains(bprof))
					bonusProfs.add(bprof);
				if (profs.contains(bprof))
				{
					if (!autoprofs.contains(bprof))
						selected.add(bprof);
				}
			}
			Collections.sort(selected);
			lc.setSelectedList(selected);
			int poolsize = (bonuschoices - selected.size());
			lc.setPool(poolsize);
			// System.out.println(poolsize);
			lc.setAvailableList(new ArrayList(bonusProfs));
			lc.show();

			aPC.getWeaponProfList();
			profs.addAll(lc.getSelectedList());
			aPC.getWeaponProfList().addAll(profs);
			updateCharacterInfo();
		}
	}

	/**
	 * This method is run when the language button is pressed.
	 */
	private void languageSelectPressed(ActionEvent evt)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC != null)
		{
			aPC.setDirty(true);
			Chooser lc = new Chooser();
			lc.setVisible(false);
			SortedSet autoLangs = aPC.getAutoLanguages();
			SortedSet langs = aPC.getLanguagesList();
			ArrayList selected = new ArrayList(langs.size());
			for (Iterator i = langs.iterator(); i.hasNext();)
			{
				String lang = i.next().toString();
				if (!autoLangs.contains(lang))
					selected.add(lang);
			}
			Collections.sort(selected);
			lc.setSelectedList(selected);
			lc.setPool(aPC.languageNum() - selected.size());
			SortedSet bonusLangs = new TreeSet();
			for (Iterator i = aPC.getBonusLanguages().iterator(); i.hasNext();)
			{
				bonusLangs.add(i.next().toString());
			}
			lc.setAvailableList(new ArrayList(bonusLangs));
			lc.show();
			langs.clear();
			aPC.getAutoLanguages();
			langs.addAll(lc.getSelectedList());
			updateCharacterInfo();
		}
	}

	private void formComponentShown(ComponentEvent evt)
	{
		requestDefaultFocus();
		updateCharacterInfo();
	}

	private void updateCharacterInfo()
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC != null)
		{
			aPC.getAutoWeaponProfs();
			ArrayList specialAbilities = aPC.getSpecialAbilityTimesList();
			SortedSet languages = aPC.getLanguagesList();
			SortedSet weaponProfs = aPC.getWeaponProfList();
			if (specialAbilities.size() > 0)
			{
				d_specAbilityText.setText
					(Globals.stringForList(specialAbilities.iterator(), s_delim));
			}
			else
			{
				d_specAbilityText.setText(s_none);
			}
			if (languages.size() > 0)
			{
				d_languageText.setText
					(Globals.stringForList(languages.iterator(), s_delim));
			}
			else
			{
				d_languageText.setText(s_none);
			}
			if (weaponProfs.size() > 0)
			{
				d_weaponProfText.setText
					(Globals.stringForList(weaponProfs.iterator(), s_delim));
			}
			else
			{
				d_weaponProfText.setText(s_none);
			}
		}
		else
		{
			d_specAbilityText.setText(s_none);
			d_languageText.setText(s_none);
			d_weaponProfText.setText(s_none);
		}
	}

	private static final String s_none = "None";
	private static final String s_delim = ", ";
	private JLabel d_specAbilityLabel;
	private JScrollPane d_specAbilitiesArea;
	private JTextArea d_specAbilityText;
	private JScrollPane d_languageArea;
	private JTextArea d_languageText;
	private JScrollPane d_weaponProfArea;
	private JTextArea d_weaponProfText;
	private JLabel d_languageLabel;
	private JButton d_languageChoose;
	private JLabel d_weaponProfLabel;
	private JButton d_weaponProfChoose;

}
