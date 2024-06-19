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
import java.awt.GridBagLayout;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
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
import pcgen.gui.utils.GuiFacade;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.util.Logging;
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

final class ChooseSpellDialog extends JDialog
{
	static final long serialVersionUID = 3692925177296126937L;
	private JButton btnCancel;
	private JButton btnOk;
	private JComboBoxEx cmbBaseSpellLevel;
	private JComboBoxEx cmbCasterLevel;
	private JComboBoxEx cmbClass;
	private JComboBoxEx cmbSpellName;
	private JComboBoxEx cmbSpellType;
	private JComboBoxEx cmbSpellVariant;
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
	private List classSpells = null;
	private int minLevel = 0;
	private int levelAdjust = 0;

	private Map specialists = null;

	private boolean metaAllowed = true;
	private int spellBooks = 0;
	private List classList = null;
	private List levelList = null;

	ChooseSpellDialog(JFrame parent, final int eqType, final boolean argMetaAllowed, final List argClassList, final List argLevelList, final int argSpellBooks)
	{
		super(parent);
		IconUtilitities.maybeSetIcon(parent, "PcgenIcon.gif");
		this.eqType = eqType;

		metaAllowed = argMetaAllowed;
		spellBooks = argSpellBooks;
		classList = argClassList;
		levelList = argLevelList;

		initComponents();
		setLocationRelativeTo(parent);	// centre on parent (Canadian spelling eh?)
	}

	private void initComponents()
	{
		GridBagConstraints gridBagConstraints;

		lblClass = new JLabel();
		lblBaseSpellLevel = new JLabel();
		lblSpellName = new JLabel();
		lblSpellVariant = new JLabel();
		lblCasterLevel = new JLabel();
		lblSpellType = new JLabel();

		cmbClass = new JComboBoxEx();
		cmbBaseSpellLevel = new JComboBoxEx();
		cmbSpellName = new JComboBoxEx();
		cmbSpellVariant = new JComboBoxEx();
		cmbCasterLevel = new JComboBoxEx();
		cmbSpellType = new JComboBoxEx();

		btnOk = new JButton();
		btnCancel = new JButton();

		if (metaAllowed)
		{
			lblMetamagicFeats = new JLabel();
			lstMetamagicFeats = new JList();
			jScrollPane1 = new JScrollPane();
		}

		getContentPane().setLayout(new GridBagLayout());

		setTitle(PropertyFactory.getString("in_csdSelect"));
		setModal(true);
		setResizable(true);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent evt)
			{
				closeDialog();
			}
		});

		lblClass.setText(PropertyFactory.getString("in_class"));
		lblClass.setDisplayedMnemonic(PropertyFactory.getMnemonic("in_mn_class"));
		lblClass.setLabelFor(cmbClass);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 12;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		lblClass.setPreferredSize(new Dimension(32, 16));
		getContentPane().add(lblClass, gridBagConstraints);
		cmbClass.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent evt)
			{
				cmbClassLevelActionPerformed(evt, TRIGGER_CLASS);
			}
		});
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 12;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		cmbClass.setPreferredSize(new Dimension(200, 25));
		getContentPane().add(cmbClass, gridBagConstraints);

		lblBaseSpellLevel.setText(PropertyFactory.getString("in_csdSpLvl"));
		lblBaseSpellLevel.setDisplayedMnemonic(PropertyFactory.getMnemonic("in_mn_csdSpLvl"));
		lblBaseSpellLevel.setLabelFor(cmbBaseSpellLevel);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 12;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 8;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		lblBaseSpellLevel.setPreferredSize(new Dimension(61, 16));
		getContentPane().add(lblBaseSpellLevel, gridBagConstraints);
		cmbBaseSpellLevel.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent evt)
			{
				cmbClassLevelActionPerformed(evt, TRIGGER_BASELEVEL);
			}
		});
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 12;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 8;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		cmbBaseSpellLevel.setPreferredSize(new Dimension(61, 25));
		getContentPane().add(cmbBaseSpellLevel, gridBagConstraints);

		if (metaAllowed)
		{
			lblMetamagicFeats.setText(PropertyFactory.getString("in_metaFeat"));
			lblMetamagicFeats.setDisplayedMnemonic(PropertyFactory.getMnemonic("in_mn_metaFeat"));
			lblMetamagicFeats.setLabelFor(lstMetamagicFeats);
			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 8;
			gridBagConstraints.gridwidth = 11;
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			lblMetamagicFeats.setPreferredSize(new Dimension(97, 16));
			getContentPane().add(lblMetamagicFeats, gridBagConstraints);
			jScrollPane1.setViewportView(lstMetamagicFeats);
			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 9;
			gridBagConstraints.gridwidth = 11;
			gridBagConstraints.gridheight = 15;
			gridBagConstraints.fill = GridBagConstraints.BOTH;
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

		btnOk.setMnemonic(PropertyFactory.getMnemonic("in_mn_ok"));
		btnOk.setText(PropertyFactory.getString("in_ok"));
		btnOk.setEnabled(false);
		btnOk.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnOKActionPerformed();
			}
		});
		btnOk.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				btnOKActionPerformed();
			}
		});
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 12;
		gridBagConstraints.gridy = 21;
		gridBagConstraints.gridwidth = 7;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		btnOk.setPreferredSize(new Dimension(73, 26));
		getContentPane().add(btnOk, gridBagConstraints);

		btnCancel.setMnemonic(PropertyFactory.getMnemonic("in_mn_cancel"));
		btnCancel.setText(PropertyFactory.getString("in_cancel"));
		btnCancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				closeDialog();
			}
		});
		btnCancel.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				closeDialog();
			}
		});
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 12;
		gridBagConstraints.gridy = 18;
		gridBagConstraints.gridwidth = 7;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		btnCancel.setPreferredSize(new Dimension(73, 26));
		getContentPane().add(btnCancel, gridBagConstraints);

		lblSpellName.setText(PropertyFactory.getString("in_spellName"));
		lblSpellName.setDisplayedMnemonic(PropertyFactory.getMnemonic("in_mn_spellName"));
		lblSpellName.setLabelFor(cmbSpellName);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 20;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		lblSpellName.setPreferredSize(new Dimension(64, 16));
		getContentPane().add(lblSpellName, gridBagConstraints);
		cmbSpellName.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent evt)
			{
				cmbClassLevelActionPerformed(evt, TRIGGER_SPELLNAME);
			}
		});
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 20;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		cmbSpellName.setPreferredSize(new Dimension(280, 25));
		getContentPane().add(cmbSpellName, gridBagConstraints);

		lblSpellVariant.setText(PropertyFactory.getString("in_csdVariant"));
		lblSpellVariant.setDisplayedMnemonic(PropertyFactory.getMnemonic("in_mn_csdVariant"));
		lblSpellVariant.setLabelFor(cmbSpellVariant);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.gridwidth = 20;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		lblSpellVariant.setPreferredSize(new Dimension(41, 16));
		getContentPane().add(lblSpellVariant, gridBagConstraints);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.gridwidth = 20;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		cmbSpellVariant.setPreferredSize(new Dimension(280, 25));
		getContentPane().add(cmbSpellVariant, gridBagConstraints);

		lblCasterLevel.setText(PropertyFactory.getString("in_casterLvl"));
		lblCasterLevel.setDisplayedMnemonic(PropertyFactory.getMnemonic("in_mn_casterLvl"));
		lblCasterLevel.setLabelFor(cmbCasterLevel);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.gridwidth = 6;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		lblCasterLevel.setPreferredSize(new Dimension(71, 16));
		getContentPane().add(lblCasterLevel, gridBagConstraints);
		cmbCasterLevel.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent evt)
			{
				cmbClassLevelActionPerformed(evt, TRIGGER_CASTERLEVEL);
			}
		});
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.gridwidth = 6;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		cmbCasterLevel.setPreferredSize(new Dimension(71, 25));
		getContentPane().add(cmbCasterLevel, gridBagConstraints);

		lblSpellType.setText(PropertyFactory.getString("in_spellType"));
		lblSpellType.setDisplayedMnemonic(PropertyFactory.getMnemonic("in_mn_spellType"));
		lblSpellType.setLabelFor(cmbSpellType);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 6;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.gridwidth = 14;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		lblSpellType.setPreferredSize(new Dimension(58, 16));
		getContentPane().add(lblSpellType, gridBagConstraints);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 6;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.gridwidth = 14;
		gridBagConstraints.fill = GridBagConstraints.BOTH;

		cmbSpellType.setPreferredSize(new Dimension(60, 25));
		getContentPane().add(cmbSpellType, gridBagConstraints);

		// Generate a list of classes and domains
		// that have at least 1 spell
		List unfoundItems = new ArrayList();
		List classWithSpell = new ArrayList();
		if (classList != null)
		{
			for (Iterator i = classList.iterator(); i.hasNext();)
			{
				final String className = (String) i.next();
				PObject obj = Globals.getClassNamed(className);
				if (obj == null)
				{
					obj = Globals.getDomainNamed(className);
				}
				if (obj != null)
				{
					classWithSpell.add(obj);
				}
			}
		}
		else
		{
			final Map spellMap = Globals.getSpellMap();
			for (Iterator i = spellMap.keySet().iterator(); i.hasNext();)
			{
				final String aKey = (String) i.next();
				final Object obj = spellMap.get(aKey);
				if (obj instanceof ArrayList)
				{
					for (Iterator it2 = ((ArrayList) obj).iterator(); it2.hasNext();)
					{
						addSpellInfoToList((Spell) it2.next(), unfoundItems, classWithSpell);
					}
				}
				else
				{
					addSpellInfoToList((Spell) obj, unfoundItems, classWithSpell);
				}
			}

			if (unfoundItems.size() > 0)
			{
				specialists = null;

				for (Iterator e = unfoundItems.iterator(); e.hasNext();)
				{
					final String eMsg = (String) e.next();
					String bMsg = null;
					if (eMsg.length() <= 0)
					{
						continue;
					}
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
							final List specInfo = (ArrayList) specialists.get(sub);
							if (specInfo == null)
							{
								bMsg = "Class";
							}
							else
							{
								//
								// hack: specialists used to appear in in PObjects somehow, make it look similar
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
						Logging.errorPrint(bMsg + " not found: " + eMsg.substring(1));
					}
				}
			}

			for (Iterator iClass = Globals.getClassList().iterator(); iClass.hasNext();)
			{
				final PCClass aClass = (PCClass) iClass.next();
				if (!aClass.getSpellType().equals(Constants.s_NONE))
				{
					// Only adds if the class can cast
					if (aClass.zeroCastList())
					{
						continue;
					}

					if (!classWithSpell.contains(aClass))
					{
						classWithSpell.add(aClass);
					}
				}
			}
		}

		if (spellBooks != 0)
		{
			for (int i = classWithSpell.size() - 1; i >= 0; --i)
			{
				Object obj = classWithSpell.get(i);
				if (spellBooks < 0)		// can't have books
				{
					if ((obj instanceof PCClass) && ((PCClass) obj).getSpellBookUsed())
					{
						classWithSpell.remove(i);
					}
				}
				else					// must have books
				{
					if (!(obj instanceof PCClass) || !((PCClass) obj).getSpellBookUsed())
					{
						classWithSpell.remove(i);
					}
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
		Integer levelsForCasting[];
		if ((levelList != null) && (levelList.size() > 0))
		{
			levelsForCasting = new Integer[levelList.size()];
			for (int i = 0; i < levelList.size(); ++i)
			{
				levelsForCasting[i] = new Integer((String) levelList.get(i));
			}
		}
		else
		{
			levelsForCasting = new Integer[maxLevel + 1];
			for (int i = 0; i <= maxLevel; i++)
			{
				levelsForCasting[i] = new Integer(i);
			}
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
			List metamagicFeats = new ArrayList();
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
	private void closeDialog()
	{
		wasCancelled = true;
		setVisible(false);
		dispose();
	}

	private void btnOKActionPerformed()
	{
		if (btnOk.isEnabled())
		{
			wasCancelled = false;
			setVisible(false);
			dispose();
		}
	}

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

			List spellTypes = getSpellTypes();
			cmbSpellType.setModel(new DefaultComboBoxModel(spellTypes.toArray()));
			cmbBaseSpellLevel.setSelectedIndex(0);	// set the spell level to 0, which will set the caster level to 1
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

			int maxClassLevel = 20; //TODO: This shouldn't be hardcoded, should it?
			PCClass aClass;
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
			if (!SettingsHandler.isIgnoreLevelCap() && (casterLevel != Constants.INVALID_LEVEL) && (casterLevel > maxClassLevel))
			{
				casterLevel = maxClassLevel;
				GuiFacade.showMessageDialog(null, PropertyFactory.getString("in_csdEr4"), Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
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
			List spellsOfLevel = new ArrayList();
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
						if (SettingsHandler.guiUsesOutputName())
						{
							spellsOfLevel.add(new SpellShell(s));
						}
						else
						{
							spellsOfLevel.add(s);
						}
					}
				}
				//Globals.sortPObjectList(spellsOfLevel);
				Collections.sort(spellsOfLevel);
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
				final Object obj = cmbSpellName.getItemAt(cmbSpellName.getSelectedIndex());
				if (obj instanceof SpellShell)
				{
					theSpell = ((SpellShell) obj).getSpell();
				}
				else
				{
					theSpell = (Spell) obj;
				}
			}
			else
			{
				theSpell = null;
			}

			List variants;
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

		btnOk.setEnabled(cmbSpellName.isEnabled() && (cmbCasterLevel.getSelectedIndex() >= 0));
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
				canCreate = false;
				// fall-through intentional
			case EqBuilder.EQTYPE_SCROLL:
			case EqBuilder.EQTYPE_WAND:
			case EqBuilder.EQTYPE_RING:
				itemType = EqBuilder.validEqTypes[eqType];
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

	private List getSpellTypes()
	{
		List spellTypes = new ArrayList();
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
			final List subClasses = pcgen.core.utils.Utility.split(aClass.getSubClassString(), '|');
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
					pcgen.core.utils.Utility.fixFilenamePath(fileName);
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
						//BufferedReader reader = new BufferedReader(new FileReader(aFile));
						BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(aFile), "UTF-8"));

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
						//TODO: Should we really ignore this?
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
		return Constants.INVALID_LEVEL;
	}

	private void setCasterLevel(int casterLevel)
	{
		boolean bEnabled = true;
		if (casterLevel == Constants.INVALID_LEVEL)
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

	private void addSpellInfoToList(final Spell aSpell, List unfoundItems, List classWithSpell)
	{
		final Map levelInfo = aSpell.getLevelInfo();
		if ((levelInfo == null) || (levelInfo.size() == 0))
		{
			Logging.errorPrint("Spell: " + aSpell.getName() + "(" + aSpell.getSource() + ") has no home");
			return;
		}
		for (Iterator it = levelInfo.keySet().iterator(); it.hasNext();)
		{
			final String key = (String) it.next();
			String sub;
			if (key.startsWith("CLASS|"))
			{
				sub = key.substring(6);
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
			else if (key.startsWith("DOMAIN|"))
			{
				sub = key.substring(7);
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
				Logging.errorPrint("Unknown spell source: " + key);
			}
		}
	}

	private static final class SpellShell implements Serializable, Comparable
	{
		private Spell aSpell = null;

		SpellShell(final Spell argSpell)
		{
			aSpell = argSpell;
		}

		public int compareTo(Object obj)
		{
			if (obj != null)
			{
				//this should throw a ClassCastException for non-SpellShell, like the Comparable interface calls for
				return this.toString().compareToIgnoreCase(obj.toString());
			}
			else
			{
				return 1;
			}
		}

		public String toString()
		{
			if (aSpell != null)
			{
				return aSpell.getOutputName();
			}
			return "";
		}

		final Spell getSpell()
		{
			return aSpell;
		}
	}

}
