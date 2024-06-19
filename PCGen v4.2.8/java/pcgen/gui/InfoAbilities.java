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
import java.util.TreeSet;
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
		initComponents();
	}

	private void initComponents()
	{
		this.setLayout(new BorderLayout());
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
/*
		JScrollPane statScrollPane = new JScrollPane();
		statTable = new JTableEx();

		statTable.setModel(statTableModel);
		statTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		statTable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				statTableMouseClicked(evt);
			}
		});

		TableColumn col = statTable.getColumnModel().getColumn(0);
		int width = Globals.getCustColumnWidth("AbilitiesS", 0);
		if (width == 0)
			col.setPreferredWidth(50);
		else
			col.setPreferredWidth(width);
		col.addPropertyChangeListener(new ResizeColumnListener(statTable, "AbilitiesS", 0));
		col.setCellRenderer(new AlignCellRenderer(SwingConstants.LEFT));
		col = statTable.getColumnModel().getColumn(1);
		width = Globals.getCustColumnWidth("AbilitiesS", 1);
		if (width == 0)
			col.setPreferredWidth(40);
		else
			col.setPreferredWidth(width);
		col.addPropertyChangeListener(new ResizeColumnListener(statTable, "AbilitiesS", 1));
		col.setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));
		col = statTable.getColumnModel().getColumn(2);
		width = Globals.getCustColumnWidth("AbilitiesS", 2);
		if (width == 0)
			col.setPreferredWidth(30);
		else
			col.setPreferredWidth(width);
		col.addPropertyChangeListener(new ResizeColumnListener(statTable, "AbilitiesS", 2));
		col.setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));
		col = statTable.getColumnModel().getColumn(3);
		width = Globals.getCustColumnWidth("AbilitiesS", 3);
		if (width == 0)
			col.setPreferredWidth(40);
		else
			col.setPreferredWidth(width);
		col.addPropertyChangeListener(new ResizeColumnListener(statTable, "AbilitiesS", 3));
		col.setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));
		col = statTable.getColumnModel().getColumn(4);
		width = Globals.getCustColumnWidth("AbilitiesS", 4);
		if (width == 0)
			col.setPreferredWidth(30);
		else
			col.setPreferredWidth(width);
		col.addPropertyChangeListener(new ResizeColumnListener(statTable, "AbilitiesS", 4));
		col.setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));
		col = statTable.getColumnModel().getColumn(5);
		col.setCellRenderer(plusMinusRenderer);
		col.setMaxWidth(30);
		col.setMinWidth(30);
		col = statTable.getColumnModel().getColumn(6);
		col.setCellRenderer(plusMinusRenderer);
		col.setMaxWidth(30);
		col.setMinWidth(30);
		statScrollPane.setPreferredSize(new Dimension(350, 160));

		statScrollPane.setViewportView(statTable);
		topPanel.add(statScrollPane, BorderLayout.WEST);

		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new BorderLayout());
		JPanel t1Panel = new JPanel();
		t1Panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		t1Panel.add(lblPool);
		t1Panel.add(poolText);
		poolText.setPreferredSize(new Dimension(60, 20));
		t1Panel.add(rollButton);
		t1Panel.add(zeroButton);

		JPanel t2Panel = new JPanel();
		t2Panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		t2Panel.add(lblForcePoints);
		forceText.setPreferredSize(new Dimension(30, 15));
		darkText.setPreferredSize(new Dimension(30, 15));
		t2Panel.add(forceText);
		t2Panel.add(lblDarkSidePoints);
		t2Panel.add(darkText);
		forceText.setPreferredSize(new Dimension(30, 15));
		darkText.setPreferredSize(new Dimension(30, 15));

		JPanel t3Panel = new JPanel();
		t3Panel.setLayout(new BorderLayout());
//		t3Panel.add(t1Panel, BorderLayout.NORTH);
		t3Panel.add(t2Panel, BorderLayout.CENTER);
		optionsPanel.add(t3Panel, BorderLayout.NORTH);

		JPanel t4Panel = new JPanel();
		t4Panel.setLayout(new FlowLayout(FlowLayout.LEFT));
//		t4Panel.add(purchaseCheckBox);
		t4Panel.add(unlimitedCheckBox);

		JPanel t5Panel = new JPanel();
		t5Panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		statMinText.setPreferredSize(new Dimension(30, 20));
		statMaxText.setPreferredSize(new Dimension(30, 20));
		t5Panel.add(lblStatMin);
		t5Panel.add(statMinText);
		t5Panel.add(lblStatMax);
		t5Panel.add(statMaxText);
		statMinText.setPreferredSize(new Dimension(30, 20));
		statMaxText.setPreferredSize(new Dimension(30, 20));

		JPanel t6Panel = new JPanel();
		t6Panel.setLayout(new BorderLayout());
		t6Panel.add(t4Panel, BorderLayout.NORTH);
		t6Panel.add(t5Panel, BorderLayout.CENTER);
		optionsPanel.add(t6Panel, BorderLayout.CENTER);
		topPanel.add(optionsPanel, BorderLayout.CENTER);
		add(topPanel, BorderLayout.NORTH);

		rollButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				characterRolled();
				PCGen_Frame1.getStatusBar().setText("Change Roll Methods under Options");
			}
		});
		zeroButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				zeroPool();
			}
		});

		forceText.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				updateCharacterForce();
			}
		});

		forceText.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				updateCharacterForce();
			}
		});
		darkText.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				updateCharacterDside();
			}
		});

		darkText.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				updateCharacterDside();
			}
		});
*/
//		Utility.setDescription(purchaseCheckBox, "Buy stats using a point system");
//		purchaseCheckBox.setSelected(SettingsHandler.isPurchaseStatMode());
//		purchaseCheckBox.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent evt)
//			{
//				SettingsHandler.setPurchaseStatMode(purchaseCheckBox.isSelected());
//				PCGen_Frame1.getStatusBar().setText("You may alter the costs under the Options Menu.");
//				updateCharacterInfo();
//			}
//		});
//		purchaseCheckBox.setVisible(SettingsHandler.isPurchaseStatModeAllowed());
/*
		Utility.setDescription(unlimitedCheckBox, "Allow unlimited stat editing");
		unlimitedCheckBox.setSelected(SettingsHandler.isStatPoolUnlimited());
		unlimitedCheckBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				SettingsHandler.setUnlimitedStatPool(unlimitedCheckBox.isSelected());
			}
		});
		statMinText.setText(Integer.toString(SettingsHandler.getInitialStatMin()));
		statMinText.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				Object source = evt.getSource();
				updateTextFields(source);
			}
		});
		statMinText.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				Object source = evt.getSource();
				updateTextFields(source);
			}
		});
		statMaxText.setText(Integer.toString(SettingsHandler.getInitialStatMax()));
		statMaxText.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				Object source = evt.getSource();
				updateTextFields(source);
			}
		});
		statMaxText.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				Object source = evt.getSource();
				updateTextFields(source);
			}
		});
*/
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
				weaponSelectPressed(evt);
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
				formComponentShown(evt);
			}
		});

	}
/*
	private final void updateTextFields(Object source)
	{
		if (source == statMinText)
		{
			try
			{
				SettingsHandler.setInitialStatMin((Integer.parseInt(statMinText.getText())));
				Options.initializeStatRollingMethod8();
			}
			catch (NumberFormatException nfe)
			{
				statMinText.setText(Integer.toString(SettingsHandler.getInitialStatMin()));
			}
		}
		else if (source == statMaxText)
		{
			try
			{
				SettingsHandler.setInitialStatMax(Integer.parseInt(statMaxText.getText()));
				Options.initializeStatRollingMethod8();
			}
			catch (NumberFormatException nfe)
			{
				statMaxText.setText(Integer.toString(SettingsHandler.getInitialStatMax()));
			}
		}
	}
*/
	// This is called when the tab is shown.
	private void formComponentShown(ComponentEvent evt)
	{
		requestFocus();
/*
		if (Globals.isStarWarsMode())
		{
			lblForcePoints.setVisible(true);
			forceText.setVisible(true);
			lblDarkSidePoints.setVisible(true);
			darkText.setVisible(true);
		}
		else
		{
			lblForcePoints.setVisible(false);
			forceText.setVisible(false);
			lblDarkSidePoints.setVisible(false);
			darkText.setVisible(false);
		}
*/
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

/*
	private void characterRolled()
	{
		aPC.setDirty(true);
		aPC.rollStats(SettingsHandler.getRollMethod());
		updatePool();
		statTableModel.fireTableDataChanged();
	}

	private void zeroPool()
	{
		aPC.setDirty(true);
		aPC.setPoolAmount(0);
		poolText.setText(Integer.toString(aPC.getRemainingPool()));
		updateCharacterInfo();
	}
*/
	/** <code>updateCharacterInfo</code> update data listening for a changed PC
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
		SortedSet languages = aPC.getLanguagesListNames();
		if (specialAbilities.size() > 0)
		{
			saText.setText(pcgen.core.Utility.commaDelimit(specialAbilities));
		}
		else
		{
			saText.setText(Constants.s_NONE);
		}
		if (languages.size() > 0)
		{
			languageText.setText(pcgen.core.Utility.commaDelimit(languages));
		}
		else
		{
			languageText.setText(Constants.s_NONE);
		}
		showWeaponProfList();
/*
		if (SettingsHandler.isPurchaseStatMode())
			poolText.setText(Integer.toString(aPC.getPoolAmount()));
		else
			poolText.setText(Integer.toString(aPC.getRemainingPool()));

		forceText.setText(aPC.getStrFPoints());
		darkText.setText(aPC.getDPoints());
		//updatePool();
*/
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
			SortedSet autoLangs = aPC.getAutoLanguageNames();
			//SortedSet langs = aPC.getLanguagesListNames();
			TreeSet langs = aPC.getLanguagesList();
			ArrayList selected = new ArrayList(langs.size());

			ArrayList bonusLangs = new ArrayList();
			ArrayList excludedLangs = new ArrayList();

			final pcgen.core.Skill speakLanguage = aPC.getSkillNamed(PropertyFactory.getString("in_iaSkillNamed"));
			int numLanguages = aPC.languageNum(false);
			for (Iterator i = aPC.getBonusLanguages(false).iterator(); i.hasNext();)
			{
				final String langName = (String) i.next();
				final Language aLang = Globals.getLanguageNamed(langName);
				if (aLang != null)
				{
					if (aLang.passesPreReqTestsForList(aPC, null, aLang.getPreReqList()))
					{
						bonusLangs.add(aLang.getName());
					}
					else if (Globals.isDebugMode())
					{
						Globals.debugPrint(aLang.getName() + " excluded--prereqs not met");
					}
				}
				else
				{
					Globals.errorPrint("Language '" + langName + "' not found");
				}
			}

			//
			// Only show selections that are not automatically granted or granted via the "Speak Language" skill
			// Remove any language selected via "Speak Language" from the list of available selections
			//
			for (Iterator i = langs.iterator(); i.hasNext();)
			{
				final String lang = (String) i.next();
				boolean addLang = false;
				if (autoLangs.contains(lang))
				{
				}
				else if ((speakLanguage != null) && speakLanguage.containsAssociated(lang))
				{
				}
				else
				{
					addLang = true;
				}

				if (addLang)
				{
					selected.add(lang);
				}
				else
				{
					bonusLangs.remove(lang);
					excludedLangs.add(lang);
				}
			}

			Globals.sortChooserLists(bonusLangs, selected);

			ChooserInterface lc = ChooserFactory.getChooserInstance();
			lc.setVisible(false);
			lc.setAvailableList(bonusLangs);
			lc.setSelectedList(selected);
			lc.setPool(numLanguages - selected.size());
			lc.setPoolFlag(false);
			lc.show();

			langs.clear();
			langs.addAll(lc.getSelectedList());
			langs.addAll(excludedLangs);

			updateCharacterInfo();
			ensureFocus();
		}
	}

	/**
	 * This method is run when the weapon proficiency button is pressed.
	 */
	private void weaponSelectPressed(ActionEvent evt)
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
					ArrayList profWeapons = new ArrayList();
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
