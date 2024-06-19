/*
 * ChooseSpellDialog.java
 * Copyright 2002 (C) Greg Bingleman
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
 * Created on May 14, 2002, 9:34 PM
 */
package pcgen.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pcgen.core.Constants;
import pcgen.core.Domain;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.SettingsHandler;
import pcgen.core.spell.Spell;
import pcgen.util.PropertyFactory;

/**
 * <code>ChooseSpellDialog</code>
 *
 * Please complete
 *
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */

final class ChooseSpellDialog extends javax.swing.JDialog
{
	private JButton btnCancel;
	private JButton btnOkay;
	private JComboBox cmbBaseSpellLevel;
	private JComboBox cmbCasterLevel;
	private JComboBox cmbClass;
	private JComboBox cmbSpellName;
	private JComboBox cmbSpellType;
	private JComboBox cmbSpellVariant;
	private JLabel lblBaseSpellLevel;
	private JLabel lblCasterLevel;
	private JLabel lblClass;
	private JLabel lblMetamagicFeats;
	private JLabel lblSpellName;
	private JLabel lblSpellType;
	private JLabel lblSpellVariant;
	private JList lstMetamagicFeats;
	private JScrollPane jScrollPane1;

	private boolean wasCancelled = true;

	private static final int TRIGGER_ALL = -1;
	private static final int TRIGGER_CLASS = 0;
	private static final int TRIGGER_BASELEVEL = 1;
	private static final int TRIGGER_CASTERLEVEL = 2;
	private static final int TRIGGER_SPELLNAME = 3;
	private static final int TRIGGER_METAMAGIC = 4;

	private PObject castingClass = null;
	private Spell theSpell = null;
	private int baseSpellLevel = -1;
	private int eqType = EqBuilder.EQTYPE_NONE;
	private ArrayList classSpells = null;
	private int minLevel = 0;
	private int levelAdjust = 0;

	private HashMap specialists = null;

	/** Creates new form SpellDialog */

	ChooseSpellDialog(JFrame parent, int eqType)
	{
		super(parent);
		ClassLoader loader = getClass().getClassLoader();
		Toolkit kit = Toolkit.getDefaultToolkit();
		// according to the API, the following should *ALWAYS* use '/'
		Image img = kit.getImage(loader.getResource("pcgen/gui/resource/PcgenIcon.gif"));
		loader = null;
		parent.setIconImage(img);
		this.eqType = eqType;
		initComponents();
		setLocationRelativeTo(parent);	// centre on parent (Canadian spelling eh?)
	}

	private void initComponents()
	{
		GridBagConstraints gridBagConstraints;

		final boolean metaAllowed = SettingsHandler.isMetamagicAllowedInEqBuilder();

		lblClass = new JLabel();
		lblBaseSpellLevel = new JLabel();
		lblSpellName = new JLabel();
		lblSpellVariant = new JLabel();
		lblCasterLevel = new JLabel();
		lblSpellType = new JLabel();

		cmbClass = new JComboBox();
		cmbBaseSpellLevel = new JComboBox();
		cmbSpellName = new JComboBox();
		cmbSpellVariant = new JComboBox();
		cmbCasterLevel = new JComboBox();
		cmbSpellType = new JComboBox();

		btnOkay = new JButton();
		btnCancel = new JButton();

		if (metaAllowed)
		{
			lblMetamagicFeats = new JLabel();
			lstMetamagicFeats = new JList();
			jScrollPane1 = new JScrollPane();
		}


//		getContentPane().setLayout(new AbsoluteLayout());
		getContentPane().setLayout(new java.awt.GridBagLayout());

		setTitle(PropertyFactory.getString("in_csdSelect"));
		setModal(true);
		setResizable(true);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent evt)
			{
				closeDialog(evt);
			}
		});

		lblClass.setText(PropertyFactory.getString("in_class"));
//		getContentPane().add(lblClass, new AbsoluteConstraints(20, 10, -1, -1));
		lblClass.setDisplayedMnemonic(PropertyFactory.getMnemonic("in_mn_class", 0));
		lblClass.setLabelFor(cmbClass);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 12;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		//gridBagConstraints.ipadx = 18;
		//gridBagConstraints.ipady = 4;
		lblClass.setPreferredSize(new Dimension(32, 16));
		getContentPane().add(lblClass, gridBagConstraints);
		cmbClass.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent evt)
			{
				cmbClassLevelActionPerformed(evt, TRIGGER_CLASS);
			}
		});
//		getContentPane().add(cmbClass, new AbsoluteConstraints(20, 30, 230, -1));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 12;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		//gridBagConstraints.ipadx = 209;
		cmbClass.setPreferredSize(new Dimension(200, 25));
		getContentPane().add(cmbClass, gridBagConstraints);

		lblBaseSpellLevel.setText(PropertyFactory.getString("in_csdSpLvl"));
//		getContentPane().add(lblBaseSpellLevel, new AbsoluteConstraints(260, 10, -1, -1));
		lblBaseSpellLevel.setDisplayedMnemonic(PropertyFactory.getMnemonic("in_mn_csdSpLvl", 0));
		lblBaseSpellLevel.setLabelFor(cmbBaseSpellLevel);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 12;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 8;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		//gridBagConstraints.ipadx = 29;
		//gridBagConstraints.ipady = 4;
		lblBaseSpellLevel.setPreferredSize(new Dimension(61, 16));
		getContentPane().add(lblBaseSpellLevel, gridBagConstraints);
		cmbBaseSpellLevel.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent evt)
			{
				cmbClassLevelActionPerformed(evt, TRIGGER_BASELEVEL);
			}
		});
//		getContentPane().add(cmbBaseSpellLevel, new AbsoluteConstraints(260, 30, 60, -1));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 12;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 8;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		//gridBagConstraints.ipadx = 39;
		cmbBaseSpellLevel.setPreferredSize(new Dimension(61, 25));
		getContentPane().add(cmbBaseSpellLevel, gridBagConstraints);

		if (metaAllowed)
		{
			lblMetamagicFeats.setText(PropertyFactory.getString("in_metaFeat"));
//			getContentPane().add(lblMetamagicFeats, new AbsoluteConstraints(20, 210, -1, -1));
			lblMetamagicFeats.setDisplayedMnemonic(PropertyFactory.getMnemonic("in_mn_metaFeat", 0));
			lblMetamagicFeats.setLabelFor(lstMetamagicFeats);
			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 8;
			gridBagConstraints.gridwidth = 11;
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			//gridBagConstraints.ipadx = 100;
			//gridBagConstraints.ipady = 4;
			lblMetamagicFeats.setPreferredSize(new Dimension(97, 16));
			getContentPane().add(lblMetamagicFeats, gridBagConstraints);
			jScrollPane1.setViewportView(lstMetamagicFeats);
//			getContentPane().add(jScrollPane1, new AbsoluteConstraints(20, 230, 170, 160));
			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 9;
			gridBagConstraints.gridwidth = 11;
			gridBagConstraints.gridheight = 15;
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			//gridBagConstraints.ipadx = -59;
			//gridBagConstraints.ipady = 49;
			jScrollPane1.setPreferredSize(new Dimension(259, 150));
			getContentPane().add(jScrollPane1, gridBagConstraints);
			lstMetamagicFeats.addListSelectionListener(new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent e)
				{
					if (!e.getValueIsAdjusting())
					{
						cmbClassLevelActionPerformed(null, TRIGGER_METAMAGIC);
					}
				}
			});
		}

		btnOkay.setMnemonic(PropertyFactory.getMnemonic("in_mn_okay", 0));
		btnOkay.setText(PropertyFactory.getString("in_okay"));
		btnOkay.setEnabled(false);
		btnOkay.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnOkayActionPerformed();
			}
		});
		btnOkay.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				btnOkayActionPerformed();
			}
		});
//		getContentPane().add(btnOkay, new AbsoluteConstraints(240, 360, 80, -1));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 12;
		gridBagConstraints.gridy = 21;
		gridBagConstraints.gridwidth = 7;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		//gridBagConstraints.ipadx = 37;
		//gridBagConstraints.ipady = 14;
		btnOkay.setPreferredSize(new Dimension(73, 26));
		getContentPane().add(btnOkay, gridBagConstraints);

		btnCancel.setMnemonic(PropertyFactory.getMnemonic("in_mn_cancel", 0));
		btnCancel.setText(PropertyFactory.getString("in_cancel"));
		btnCancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				closeDialog(null);
			}
		});
		btnCancel.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				closeDialog(null);
			}
		});
//		getContentPane().add(btnCancel, new AbsoluteConstraints(240, 330, 80, -1));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 12;
		gridBagConstraints.gridy = 18;
		gridBagConstraints.gridwidth = 7;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		//gridBagConstraints.ipadx = 27;
		//gridBagConstraints.ipady = 14;
		btnCancel.setPreferredSize(new Dimension(73, 26));
		getContentPane().add(btnCancel, gridBagConstraints);

		lblSpellName.setText(PropertyFactory.getString("in_spellName"));
//		getContentPane().add(lblSpellName, new AbsoluteConstraints(20, 60, -1, -1));
		lblSpellName.setDisplayedMnemonic(PropertyFactory.getMnemonic("in_mn_spellName", 0));
		lblSpellName.setLabelFor(cmbSpellName);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 20;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		//gridBagConstraints.ipadx = 26;
		//gridBagConstraints.ipady = 4;
		lblSpellName.setPreferredSize(new Dimension(64, 16));
		getContentPane().add(lblSpellName, gridBagConstraints);
		cmbSpellName.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent evt)
			{
				cmbClassLevelActionPerformed(evt, TRIGGER_SPELLNAME);
			}
		});
//		getContentPane().add(cmbSpellName, new AbsoluteConstraints(20, 80, 300, -1));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 20;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		//gridBagConstraints.ipadx = 289;
		cmbSpellName.setPreferredSize(new Dimension(280, 25));
		getContentPane().add(cmbSpellName, gridBagConstraints);

		lblSpellVariant.setText(PropertyFactory.getString("in_csdVariant"));
//		getContentPane().add(lblSpellVariant, new AbsoluteConstraints(20, 110, -1, -1));
		lblSpellVariant.setDisplayedMnemonic(PropertyFactory.getMnemonic("in_mn_csdVariant", 0));
		lblSpellVariant.setLabelFor(cmbSpellVariant);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.gridwidth = 20;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		//gridBagConstraints.ipadx = 49;
		//gridBagConstraints.ipady = 4;
		lblSpellVariant.setPreferredSize(new Dimension(41, 16));
		getContentPane().add(lblSpellVariant, gridBagConstraints);
//		getContentPane().add(cmbSpellVariant, new AbsoluteConstraints(20, 130, 300, -1));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.gridwidth = 20;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		//gridBagConstraints.ipadx = 289;
		cmbSpellVariant.setPreferredSize(new Dimension(280, 25));
		getContentPane().add(cmbSpellVariant, gridBagConstraints);

		lblCasterLevel.setText(PropertyFactory.getString("in_casterLvl"));
//		getContentPane().add(lblCasterLevel, new AbsoluteConstraints(20, 160, -1, -1));
		lblCasterLevel.setDisplayedMnemonic(PropertyFactory.getMnemonic("in_mn_casterLvl", 0));
		lblCasterLevel.setLabelFor(cmbCasterLevel);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.gridwidth = 6;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		//gridBagConstraints.ipadx = 19;
		//gridBagConstraints.ipady = 4;
		lblCasterLevel.setPreferredSize(new Dimension(71, 16));
		getContentPane().add(lblCasterLevel, gridBagConstraints);
		cmbCasterLevel.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent evt)
			{
				cmbClassLevelActionPerformed(evt, TRIGGER_CASTERLEVEL);
			}
		});
//		getContentPane().add(cmbCasterLevel, new AbsoluteConstraints(20, 180, 60, -1));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.gridwidth = 6;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		//gridBagConstraints.ipadx = 39;
		cmbCasterLevel.setPreferredSize(new Dimension(71, 25));
		getContentPane().add(cmbCasterLevel, gridBagConstraints);

		lblSpellType.setText(PropertyFactory.getString("in_spellType"));
//		getContentPane().add(lblSpellType, new AbsoluteConstraints(130, 160, -1, -1));
		lblSpellType.setDisplayedMnemonic(PropertyFactory.getMnemonic("in_mn_spellType", 0));
		lblSpellType.setLabelFor(cmbSpellType);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 6;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.gridwidth = 14;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		//gridBagConstraints.ipadx = 19;
		//gridBagConstraints.ipady = 4;
		lblSpellType.setPreferredSize(new Dimension(58, 16));
		getContentPane().add(lblSpellType, gridBagConstraints);
//		getContentPane().add(cmbSpellType, new AbsoluteConstraints(130, 180, 190, -1));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 6;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.gridwidth = 14;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		//gridBagConstraints.ipadx = 199;
		cmbSpellType.setPreferredSize(new Dimension(60, 25));
		getContentPane().add(cmbSpellType, gridBagConstraints);

		//
		// Stick a dummy label in the bottom-right corner to maintain a uniform boundary after
		// packing
		//
//		jLabel1.setText(" ");
//		getContentPane().add(jLabel1, new AbsoluteConstraints(330, 390, -1, -1));

		//
		// Generate a list of classes and domains that have at least 1 spell
		//
		ArrayList unfoundItems = new ArrayList();
		ArrayList classWithSpell = new ArrayList();
		final Map spellMap = Globals.getSpellMap();
		for (Iterator i = spellMap.keySet().iterator(); i.hasNext();)
		{
			final String aKey = (String) i.next();
			final Spell aSpell = (Spell) spellMap.get(aKey);
			final String levelString = aSpell.getLevelString();
			if (levelString.length() == 0)
			{
				Globals.errorPrint("Spell: " + aSpell.getName() + "(" + aSpell.getSource() + ") has no home");
				continue;
			}
			final StringTokenizer aTok = new StringTokenizer(levelString, "|", false);
			while (aTok.countTokens() >= 3)
			{
				final String main = aTok.nextToken();		// should be CLASS or DOMAIN
				String sub = aTok.nextToken();		// should be name of main object
				//final int lev = Integer.parseInt(aTok.nextToken());
				aTok.nextToken(); //Throw away token, previously named lev
				if ("CLASS".equalsIgnoreCase(main))
				{
					final PCClass aClass = Globals.getClassNamed(sub);
					if (aClass != null)
					{
						if (!classWithSpell.contains(aClass))
						{
							classWithSpell.add(aClass);
						}
					}
					else
					{
						sub = 'C' + sub;
						if (!unfoundItems.contains(sub))
						{
							unfoundItems.add(sub);
						}
					}
				}
				else if ("DOMAIN".equalsIgnoreCase(main))
				{
					final Domain aDomain = Globals.getDomainNamed(sub);
					if (aDomain != null)
					{
						if (!classWithSpell.contains(aDomain))
						{
							classWithSpell.add(aDomain);
						}
					}
					else
					{
						sub = 'D' + sub;
						if (!unfoundItems.contains(sub))
						{
							unfoundItems.add(sub);
						}
					}
				}
				else
				{
					Globals.errorPrint("Unknown spell source: " + main);
				}
			}
		}

		if (unfoundItems.size() != 0)
		{
			specialists = null;

			for (Iterator e = unfoundItems.iterator(); e.hasNext();)
			{
				final String eMsg = (String) e.next();
				String bMsg = null;
				switch (eMsg.charAt(0))
				{
					case 'C':
						//
						// Get a list of all possible specialists
						//
						if (specialists == null)
						{
							getSpecialists();
						}

						final String sub = eMsg.substring(1);
						final ArrayList specInfo = (ArrayList) specialists.get(sub);
						if (specInfo == null)
						{
							bMsg = "Class";
						}
						else
						{
							//
							// hack: specialists used to appearin in PObjects somehow, make it look similar
							//
							PObject pobj = new PObject();
							pobj.setName(sub);
							pobj.addAllToAssociated(specInfo);
							if (!classWithSpell.contains(pobj))
							{
								classWithSpell.add(pobj);
							}
						}
						break;

					case 'D':
						bMsg = "Domain";
						break;

					default:
						break;
				}
				if (bMsg != null)
				{
					Globals.errorPrint(bMsg + " not found: " + eMsg.substring(1));
				}
			}
		}

		for (Iterator iClass = Globals.getClassList().iterator(); iClass.hasNext();)
		{
			final PCClass aClass = (PCClass) iClass.next();
			if (!aClass.getSpellType().equals(Constants.s_NONE))
			{
				// if it's a prestige class, but it uses a CastAs, then we can ignore
				// it because the spell lists are the same. Otherwise, we have to add it
				// because the spell lists might !be=
				//
				// MotW Animal Lord PrC's use a non-visible class for CASTAS
				//

				if (!aClass.isPrestige())
				{
					continue;
				}
				//if (aClass.isPrestige() && (aClass.getCastAs().length() > 0))
				if (aClass.getCastAs().length() > 0)
				{
					final PCClass castClass = Globals.getClassNamed(aClass.getCastAs());
					if ((castClass != null) && !castClass.isPrestige())
					{
						continue;
					}
				}

				if (!classWithSpell.contains(aClass))
				{
//Globals.errorPrint("Adding class: " + aClass.getName());
					classWithSpell.add(aClass);
				}
			}
		}

		Globals.sortPObjectList(classWithSpell);

		//
		// classWithSpell can contain the following objects:
		// pcgen.core.PCClass
		// pcgen.core.Domain
		// pcgen.core.PObject
		//
		cmbClass.setModel(new DefaultComboBoxModel(classWithSpell.toArray()));

		//
		// Set up spell level in range 0 to 9
		//
		int maxLevel = 9;
		switch (eqType)
		{
			case EqBuilder.EQTYPE_POTION:
				maxLevel = SettingsHandler.getMaxPotionSpellLevel();
				break;

			case EqBuilder.EQTYPE_WAND:
				maxLevel = SettingsHandler.getMaxWandSpellLevel();
				break;

			default:
				break;
		}
		Integer levelsForCasting[] = new Integer[maxLevel + 1];
		for (int i = 0; i <= maxLevel; i++)
		{
			levelsForCasting[i] = new Integer(i);
		}
		cmbBaseSpellLevel.setModel(new DefaultComboBoxModel(levelsForCasting));

		//
		// Set up caster level in range 1 to 20
		//
		levelsForCasting = new Integer[20];
		for (int i = 1; i <= 20; i++)
		{
			levelsForCasting[i - 1] = new Integer(i);
		}
		cmbCasterLevel.setModel(new DefaultComboBoxModel(levelsForCasting));

		if (metaAllowed)
		{
			//
			// Make a sorted list of all available metamagic feats
			//
			ArrayList metamagicFeats = new ArrayList();
			for (Iterator e = Globals.getFeatList().iterator(); e.hasNext();)
			{
				final Feat aFeat = (Feat) e.next();
				if (aFeat.isType("Metamagic"))
				{
					metamagicFeats.add(aFeat);
				}
			}
			Globals.sortPObjectListByName(metamagicFeats);
			lstMetamagicFeats.setListData(metamagicFeats.toArray());
		}

		cmbClassLevelActionPerformed(null, TRIGGER_ALL);

		pack();
	}

	/** Closes the dialog */
	private void closeDialog(WindowEvent evt)
	{
		wasCancelled = true;
		setVisible(false);
		dispose();
	}

	private void btnOkayActionPerformed()
	{
		wasCancelled = false;
		setVisible(false);
		dispose();
	}

	/**
	 * @param args the command line arguments
	 */
//	public static void main(String args[])
//	{
//		new ChooseSpellDialog(new JFrame(), EqBuilder.EQTYPE_NONE).show();
//	}


	private void cmbClassLevelActionPerformed(ItemEvent evt, int trigger)
	{
		//
		// We only care about the selection event, not the deselection
		//
		if ((evt != null) && (evt.getStateChange() != ItemEvent.SELECTED))
		{
			return;
		}

		boolean isEnabled;
		if (((trigger == TRIGGER_ALL) || (trigger == TRIGGER_CLASS)) && (cmbClass.getSelectedIndex() >= 0))
		{
			//
			// Build a list of all spells for this class, so we don't have to
			// check everything when the spell level changes
			//
			castingClass = (PObject) cmbClass.getItemAt(cmbClass.getSelectedIndex());
			String cName = "";
			String dName = "";
			if (castingClass instanceof PCClass)
			{
				cName = ((PCClass) castingClass).getCastAs();
				if (cName.length() == 0)
				{
					cName = castingClass.getName();
				}
			}
			else if (castingClass instanceof Domain)
			{
				dName = castingClass.getName();
			}
			else
			{
				cName = castingClass.getName();
			}

			classSpells = new ArrayList();
			for (Iterator e = Globals.getSpellsIn(-1, cName, dName).iterator(); e.hasNext();)
			{
				final Spell s = (Spell) e.next();
				if (canCreateItem(s))
				{
					classSpells.add(s);
				}
			}

			ArrayList spellTypes = getSpellTypes();
			//isEnabled = (spellTypes.size() > 1);
			//lblSpellType.setEnabled(isEnabled);
			//cmbSpellType.setEnabled(isEnabled);
			cmbSpellType.setModel(new DefaultComboBoxModel(spellTypes.toArray()));
			cmbBaseSpellLevel.setSelectedItem(new Integer(0));	// set the spell level to 0, which will set the caster level to 1
		}
		if (castingClass == null)
		{
			return;
		}

		if (((trigger == TRIGGER_ALL) || (trigger == TRIGGER_BASELEVEL)) && (cmbBaseSpellLevel.getSelectedIndex() >= 0))
		{
			baseSpellLevel = ((Integer) cmbBaseSpellLevel.getItemAt(cmbBaseSpellLevel.getSelectedIndex())).intValue();

		}
		if (baseSpellLevel < 0)
		{
			return;
		}

		if ((trigger == TRIGGER_ALL) || (trigger == TRIGGER_METAMAGIC))
		{
			levelAdjust = 0;
			final Object[] selectedMetamagicFeats = getMetamagicFeats();
			if (selectedMetamagicFeats != null)
			{
				for (int i = 0; i < selectedMetamagicFeats.length; i++)
				{
					levelAdjust += ((Feat) selectedMetamagicFeats[i]).getAddSpellLevel();
				}
			}
		}

		if ((trigger == TRIGGER_ALL) || (trigger == TRIGGER_BASELEVEL) || (trigger == TRIGGER_CASTERLEVEL) || (trigger == TRIGGER_METAMAGIC))
		{
			//
			// No variants yet
			//
			if ((trigger != TRIGGER_METAMAGIC) && (trigger != TRIGGER_CASTERLEVEL) && lblSpellVariant.isEnabled())
			{
				lblSpellVariant.setEnabled(false);
				cmbSpellVariant.setEnabled(false);
				cmbSpellVariant.setModel(new DefaultComboBoxModel(new ArrayList().toArray()));
			}

			int maxClassLevel = 20;
			PCClass aClass = null;
			if (castingClass instanceof PCClass)
			{
				aClass = (PCClass) castingClass;
			}
			else if (castingClass instanceof Domain)
			{
				aClass = Globals.getClassNamed("Cleric");
			}
			else
			{
				aClass = Globals.getClassNamed(castingClass.getName());
			}

			if (aClass != null)
			{
				minLevel = Globals.minLevelForSpellLevel(aClass, baseSpellLevel + levelAdjust, true);
				maxClassLevel = aClass.getMaxLevel();
			}
			else
			{
				minLevel = 1;
			}

			int casterLevel = getCasterLevel();

			if ((trigger == TRIGGER_BASELEVEL) || (trigger == TRIGGER_METAMAGIC) || (casterLevel < minLevel))
			{
				casterLevel = minLevel;
			}
			if (!SettingsHandler.isIgnoreLevelCap() && (casterLevel != 9999) && (casterLevel > maxClassLevel))
			{
				casterLevel = maxClassLevel;
				JOptionPane.showMessageDialog(null, PropertyFactory.getString("in_csdEr4"), Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
			}
			if (getCasterLevel() != casterLevel)
			{
				setCasterLevel(casterLevel);
			}
		}


		//
		// If just changed the class or the base spell level, then need to repopulate the
		// spell name list
		//
		if ((trigger == TRIGGER_ALL) || (trigger == TRIGGER_CLASS) || (trigger == TRIGGER_BASELEVEL))
		{
			theSpell = null;
			ArrayList spellsOfLevel = new ArrayList();
			if (classSpells != null)
			{
				String caster;
				String casterName;
				if (castingClass instanceof PCClass)
				{
					casterName = ((PCClass) castingClass).getCastAs();
					if (casterName.length() == 0)
					{
						casterName = castingClass.getName();
					}
					caster = "CLASS";
				}
				else if (castingClass instanceof Domain)
				{
					casterName = castingClass.getName();
					caster = "DOMAIN";
				}
				else
				{
					casterName = castingClass.getName();
					caster = "CLASS";
				}

				for (Iterator e = classSpells.iterator(); e.hasNext();)
				{
					final Spell s = (Spell) e.next();
					if (s.levelForKey(caster, casterName) == baseSpellLevel)
					{
						spellsOfLevel.add(s);
					}
				}
				Globals.sortPObjectList(spellsOfLevel);
			}

			isEnabled = (spellsOfLevel.size() != 0);
			lblSpellName.setEnabled(isEnabled);
			cmbSpellName.setEnabled(isEnabled);
			cmbSpellName.setModel(new DefaultComboBoxModel(spellsOfLevel.toArray()));
		}

		if ((trigger == TRIGGER_ALL) || (trigger == TRIGGER_CLASS) || (trigger == TRIGGER_BASELEVEL) || (trigger == TRIGGER_SPELLNAME))
		{
			if (cmbSpellName.getSelectedIndex() >= 0)
			{
				theSpell = (Spell) cmbSpellName.getItemAt(cmbSpellName.getSelectedIndex());
			}
			else
			{
				theSpell = null;
			}

			ArrayList variants;
			if (theSpell != null)
			{
				variants = theSpell.getVariants();
			}
			else
			{
				variants = new ArrayList();
			}
			isEnabled = (variants.size() != 0);
			if (isEnabled || (!isEnabled && lblSpellVariant.isEnabled()))
			{
				cmbSpellVariant.setModel(new DefaultComboBoxModel(variants.toArray()));
			}
			if (isEnabled != lblSpellVariant.isEnabled())
			{
				lblSpellVariant.setEnabled(isEnabled);
				cmbSpellVariant.setEnabled(isEnabled);
			}
		}

		btnOkay.setEnabled(cmbSpellName.isEnabled() && (cmbCasterLevel.getSelectedIndex() >= 0));



//		Globals.minCasterLevel(aSpell, castingClass, castAs, allowBonus, levelAdjustment);


/*		if ((cmbClass.getSelectedIndex() >= 0) && (cmbBaseSpellLevel.getSelectedIndex() >= 0))
		{
			final String className = (String)cmbClass.getItemAt(cmbClass.getSelectedIndex());
			int iLevel = Integer.parseInt((String)cmbBaseSpellLevel.getItemAt(cmbBaseSpellLevel.getSelectedIndex()));

			if (!Globals.isIgnoreLevelCap())
			{
				final PCClass aClass = Globals.getClassNamed(className);
				if (aClass != null)
				{
					final int maxLevel = aClass.getMaxLevel();
					if (iLevel > maxLevel)
					{
						cmbBaseSpellLevel.setSelectedIndex(maxLevel - 1);
						iLevel = maxLevel;
					}
				}
			}
*/
/*			final Spell aSpell = getSelectedSpell();

			dataModel.setClassFilter(className, iLevel, levelAdjust);
			if (aSpell != null)
			{
				jListAvailable.getSelectionModel().clearSelection();
				final String selectedSpell = aSpell.getName();
				final int spellTableRowCount = jListAvailable.getRowCount();
				for (int row = 0; row < spellTableRowCount; row++)
				{
					final String spellName = (String)jListAvailable.getValueAt(row, 0);
					if (spellName.equals(selectedSpell))
					{
						jListAvailable.getSelectionModel().setSelectionInterval(row, row);
						break;
					}
				}
			}
			showSpellInfo(false);
		}
*/
	}

	private boolean canCreateItem(Spell aSpell)
	{
		boolean canCreate = true;
		String itemType;
		switch (eqType)
		{
			case EqBuilder.EQTYPE_NONE:
				return true;

			case EqBuilder.EQTYPE_POTION:
				itemType = "potion";
				canCreate = false;
				break;

			case EqBuilder.EQTYPE_SCROLL:
				itemType = "scroll";
				break;

			case EqBuilder.EQTYPE_WAND:
				itemType = "wand";
				break;

			case EqBuilder.EQTYPE_RING:
				itemType = "ring";
				break;

			default:
				return false;
		}
		final String items = aSpell.getCreatableItem().toLowerCase();
		if (items.indexOf("[" + itemType + "]") >= 0)
		{
			canCreate = false;
		}
		else if (items.indexOf(itemType) >= 0)
		{
			canCreate = true;
		}
		return canCreate;
	}

	private ArrayList getSpellTypes()
	{
		ArrayList spellTypes = new ArrayList();
		if (castingClass instanceof PCClass)
		{
			spellTypes.add(((PCClass) castingClass).getSpellType());
		}
		else if (castingClass instanceof Domain)
		{
			spellTypes.add("Divine");
		}
		else
		{
			final PCClass aClass = Globals.getClassNamed(castingClass.getAssociated(0));
			if (aClass != null)
			{
				spellTypes.add(aClass.getSpellType());
			}
		}
		return spellTypes;
	}

	private void getSpecialists()
	{
		specialists = new HashMap();
		for (Iterator e = Globals.getClassList().iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();
			final ArrayList subClasses = pcgen.core.Utility.split(aClass.getSubClassString(), '|');
			if (subClasses.size() > 1)
			{
				subClasses.remove(0);	// level
			}

			for (int idx = 0; idx < subClasses.size(); idx++)
			{
				String fileName = (String) subClasses.get(idx);
				int i = fileName.indexOf('(');
				if (i >= 0)
				{
					fileName = fileName.substring(0, i);
				}
				if (fileName.startsWith("FILE="))
				{
					pcgen.core.Utility.fixFilenamePath(fileName);
					i = 5;
					if (fileName.charAt(6) == File.separatorChar)
					{
						i = 6;
					}
					fileName = fileName.substring(i);
					fileName = SettingsHandler.getPccFilesLocation() + File.separator + fileName;
					File aFile = new File(fileName);
					try
					{
						BufferedReader reader = new BufferedReader(new FileReader(aFile));

						String line = reader.readLine();
						while (line != null)
						{
							if (!(line.length() > 0 && line.charAt(0) == '#'))
							{
								final StringTokenizer aTok = new StringTokenizer(line.trim(), "\t");
								if (aTok.countTokens() > 2)
								{
									final String specialty = aTok.nextToken();
									final String cost = aTok.nextToken();
									final String className = aTok.nextToken();
									List aList = new ArrayList(5);
									aList.add(aClass.getName());
									aList.add(specialty);
									aList.add(cost);
									//specialists.put(className, aClass.getName() + "\t" + specialty + "\t" + cost);
									specialists.put(className, aList);
								}
							}
							line = reader.readLine();
						}
						reader.close();
					}
					catch (IOException exception)
					{
					}
				}
			}
		}
	}

	public boolean getWasCancelled()
	{
		return wasCancelled;
	}

	public Object getCastingClass()
	{
		return castingClass;
	}

	public int getBaseSpellLevel()
	{
		return baseSpellLevel + levelAdjust;
	}

	public Spell getSpell()
	{
		return theSpell;
	}

	public String getVariant()
	{
		if (cmbSpellVariant.isEnabled())
		{
			return (String) cmbSpellVariant.getItemAt(cmbSpellVariant.getSelectedIndex());
		}
		return "";
	}

	public int getCasterLevel()
	{
		if (cmbCasterLevel.getSelectedIndex() >= 0)
		{
			return ((Integer) cmbCasterLevel.getItemAt(cmbCasterLevel.getSelectedIndex())).intValue();
		}
		return 9999;
	}

	private void setCasterLevel(int casterLevel)
	{
		boolean bEnabled = true;
		if (casterLevel == 9999)
		{
			cmbCasterLevel.setSelectedIndex(-1);
			bEnabled = false;
		}
		else
		{
			cmbCasterLevel.setSelectedItem(new Integer(casterLevel));
		}
		if (lblCasterLevel.isEnabled() != bEnabled)
		{
			lblCasterLevel.setEnabled(bEnabled);
			cmbCasterLevel.setEnabled(bEnabled);
		}
	}

	public String getSpellType()
	{
		if (cmbSpellType.getSelectedIndex() >= 0)
		{
			return (String) cmbSpellType.getItemAt(cmbSpellType.getSelectedIndex());
		}
		return null;
	}

	public Object[] getMetamagicFeats()
	{
		if (lstMetamagicFeats != null)
		{
			return lstMetamagicFeats.getSelectedValues();
		}
		return null;
	}
}
