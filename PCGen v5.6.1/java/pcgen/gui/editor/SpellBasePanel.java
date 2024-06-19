/*
 * SkillBasePanel.java
 * Copyright 2003 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on January 20, 2003, 3:36 PM
 *
 * @(#) $Id: SpellBasePanel.java,v 1.1 2006/02/21 01:33:33 vauchers Exp $
 */

package pcgen.gui.editor;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.SystemCollections;
import pcgen.core.spell.Spell;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.WholeNumberField;
import pcgen.util.DecimalNumberField;
import pcgen.util.PropertyFactory;

/**
 * <code>SpellBasePanel</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */
public class SpellBasePanel extends BasePanel
{
	private JLabel lblXpCost;
	private JLabel lblSavingThrow;
	private WholeNumberField txtXpCost;
	private JComboBoxEx cmbSavingThrow;
	private JList lstItems;
	private JComboBoxEx cmbSpellRes;
	private DescriptionPanel pnlDescription;
	private JLabel lblSpellRes;
	private JLabel lblComponents;
	private JComboBoxEx cmbDuration;
	private JLabel lblItems;
	private JLabel lblDuration;
	private JComboBoxEx cmbTarget;
	private JLabel lblTarget;
	private JLabel lblRange;
	private JComboBoxEx cmbArea;
	private JComboBoxEx cmbRange;
	private JLabel lblArea;
	private JLabel lblCost;
	private JLabel lblSubschool;
	private DecimalNumberField txtCost;
	private JComboBoxEx cmbSubschool;
	private JLabel lblCastingTime;
	private JComboBoxEx cmbCastingTime;
	private JComboBoxEx cmbSchool;
	private JLabel lblSchool;
	private JScrollPane scpItems;
	private JComboBoxEx cmbComponents;
	private JPanel pnlMagicTypes;
	private JLabel lblRingAllowed;
	private JLabel lblPotionAllowed;
	private JLabel lblScrollAllowed;
	private JLabel lblWandAllowed;
	private JCheckBox chkRingAllowed;
	private JCheckBox chkScrollAllowed;
	private JCheckBox chkPotionAllowed;
	private JCheckBox chkWandAllowed;

	/** Creates new form SpellBasePanel */
	public SpellBasePanel()
	{
		initComponents();
		initComponentContents();
	}

	private void initComponentContents()
	{
		cmbCastingTime.setModel(new DefaultComboBoxModel(Globals.getCastingTimesSet().toArray()));
		cmbRange.setModel(new DefaultComboBoxModel(Globals.getRangesSet().toArray()));
		cmbSpellRes.setModel(new DefaultComboBoxModel(Globals.getSrSet().toArray()));
		cmbSchool.setModel(new DefaultComboBoxModel(SystemCollections.getUnmodifiableSchoolsList().toArray()));
		cmbSavingThrow.setModel(new DefaultComboBoxModel(Globals.getSaveInfoSet().toArray()));
		cmbTarget.setModel(new DefaultComboBoxModel(Globals.getTargetSet().toArray()));
		cmbComponents.setModel(new DefaultComboBoxModel(Globals.getComponentSet().toArray()));
		cmbDuration.setModel(new DefaultComboBoxModel(Globals.getDurationSet().toArray()));
		List subschools = new ArrayList(10);
		subschools.add("(None)");
		subschools.addAll(Globals.getSubschools());
		cmbSubschool.setModel(new DefaultComboBoxModel(subschools.toArray()));
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gbc;

		pnlDescription = new DescriptionPanel();
		lblComponents = new JLabel();
		cmbComponents = new JComboBoxEx();
		lblCastingTime = new JLabel();
		cmbCastingTime = new JComboBoxEx();
		lblRange = new JLabel();
		cmbRange = new JComboBoxEx();
		lblTarget = new JLabel();
		cmbTarget = new JComboBoxEx();
		lblArea = new JLabel();
		cmbArea = new JComboBoxEx();
		lblDuration = new JLabel();
		cmbDuration = new JComboBoxEx();
		lblSavingThrow = new JLabel();
		cmbSavingThrow = new JComboBoxEx();
		lblSpellRes = new JLabel();
		cmbSpellRes = new JComboBoxEx();
		lblCost = new JLabel();
		txtCost = new DecimalNumberField(0.0, 6);
		lblSchool = new JLabel();
		cmbSchool = new JComboBoxEx();
		lblSubschool = new JLabel();
		cmbSubschool = new JComboBoxEx();
		lblItems = new JLabel();
		scpItems = new JScrollPane();
		lstItems = new JList();
		lblXpCost = new JLabel();
		txtXpCost = new WholeNumberField(0, 6);
		pnlMagicTypes = new JPanel();
		lblPotionAllowed = new JLabel();
		chkPotionAllowed = new JCheckBox();
		chkRingAllowed = new JCheckBox();
		lblRingAllowed = new JLabel();
		lblScrollAllowed = new JLabel();
		chkScrollAllowed = new JCheckBox();
		lblWandAllowed = new JLabel();
		chkWandAllowed = new JCheckBox();

		setLayout(new GridBagLayout());

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 8;
		gbc.gridheight = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(2, 4, 2, 2);
		gbc.weightx = 1.0;
		gbc.weighty = 0.3;
		add(pnlDescription, gbc);

		lblComponents.setText(PropertyFactory.getString("in_demComponents"));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		add(lblComponents, gbc);

		lblCastingTime.setText(PropertyFactory.getString("in_demCastingTime"));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		add(lblCastingTime, gbc);

		cmbCastingTime.setEditable(true);
		cmbCastingTime.setPreferredSize(new Dimension(120, 25));
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 0.25;
		add(cmbCastingTime, gbc);

		lblRange.setText(PropertyFactory.getString("in_demRange"));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		add(lblRange, gbc);

		cmbRange.setEditable(true);
		cmbRange.setPreferredSize(new Dimension(120, 25));
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.WEST;
		add(cmbRange, gbc);

		lblTarget.setText(PropertyFactory.getString("in_demTargetArea"));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		add(lblTarget, gbc);

		cmbTarget.setEditable(true);
		cmbTarget.setPreferredSize(new Dimension(120, 25));
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 5;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.WEST;
		add(cmbTarget, gbc);

		lblDuration.setText(PropertyFactory.getString("in_demDuration"));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		add(lblDuration, gbc);

		cmbDuration.setEditable(true);
		cmbDuration.setPreferredSize(new Dimension(120, 25));
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 6;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.WEST;
		add(cmbDuration, gbc);

		lblSavingThrow.setText(PropertyFactory.getString("in_demSavingThrow"));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 7;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		add(lblSavingThrow, gbc);

		cmbSavingThrow.setEditable(true);
		cmbSavingThrow.setPreferredSize(new Dimension(120, 25));
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 7;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.WEST;
		add(cmbSavingThrow, gbc);

		lblSpellRes.setText(PropertyFactory.getString("in_demSpellResistance"));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 8;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		add(lblSpellRes, gbc);

		cmbSpellRes.setEditable(true);
		cmbSpellRes.setPreferredSize(new Dimension(120, 25));
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 8;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.WEST;
		add(cmbSpellRes, gbc);

		lblCost.setText(PropertyFactory.getString("in_demComponentCost"));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 9;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		add(lblCost, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 9;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.WEST;
		add(txtCost, gbc);

		lblSchool.setText(PropertyFactory.getString("in_demSchool"));
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = 2;
		gbc.insets = new Insets(2, 10, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		add(lblSchool, gbc);

		cmbSchool.setEditable(true);
		cmbSchool.setPreferredSize(new Dimension(120, 25));
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.WEST;
		add(cmbSchool, gbc);

		lblSubschool.setText(PropertyFactory.getString("in_demSubschool"));
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = 3;
		gbc.insets = new Insets(2, 10, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		add(lblSubschool, gbc);

		cmbSubschool.setEditable(true);
		cmbSubschool.setPreferredSize(new Dimension(120, 25));
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 0.25;
		add(cmbSubschool, gbc);

		lblXpCost.setText(PropertyFactory.getString("in_demXPCost"));
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 9;
		gbc.insets = new Insets(2, 10, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		add(lblXpCost, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = 9;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.WEST;
		add(txtXpCost, gbc);

		cmbComponents.setEditable(true);
		cmbComponents.setPreferredSize(new Dimension(120, 25));
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.WEST;
		add(cmbComponents, gbc);

		pnlMagicTypes.setLayout(new GridBagLayout());

		pnlMagicTypes.setBorder(new TitledBorder(PropertyFactory.getString("in_demAllowedMagicItemType")));
		lblPotionAllowed.setLabelFor(chkPotionAllowed);
		lblPotionAllowed.setText(PropertyFactory.getString("in_demPotion"));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(2, 0, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		pnlMagicTypes.add(lblPotionAllowed, gbc);

		chkPotionAllowed.setHorizontalTextPosition(SwingConstants.LEADING);
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 0);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 0.5;
		pnlMagicTypes.add(chkPotionAllowed, gbc);

		lblScrollAllowed.setLabelFor(chkScrollAllowed);
		lblScrollAllowed.setText(PropertyFactory.getString("in_demScroll"));
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(2, 0, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		pnlMagicTypes.add(lblScrollAllowed, gbc);

		chkScrollAllowed.setHorizontalTextPosition(SwingConstants.LEADING);
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 0;
		gbc.insets = new Insets(2, 2, 2, 0);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 0.5;
		pnlMagicTypes.add(chkScrollAllowed, gbc);

		lblWandAllowed.setLabelFor(chkWandAllowed);
		lblWandAllowed.setText(PropertyFactory.getString("in_demWand"));
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(2, 0, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		pnlMagicTypes.add(lblWandAllowed, gbc);

		chkWandAllowed.setHorizontalTextPosition(SwingConstants.LEADING);
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 0);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 0.5;
		pnlMagicTypes.add(chkWandAllowed, gbc);

		lblRingAllowed.setLabelFor(chkRingAllowed);
		lblRingAllowed.setText(PropertyFactory.getString("in_demRing"));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(2, 0, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		pnlMagicTypes.add(lblRingAllowed, gbc);

		chkRingAllowed.setHorizontalTextPosition(SwingConstants.LEADING);
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 0);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 0.5;
		pnlMagicTypes.add(chkRingAllowed, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = 4;
		gbc.gridwidth = 3;
		gbc.gridheight = 5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 10, 2, 2);
		gbc.anchor = GridBagConstraints.NORTHWEST;
		add(pnlMagicTypes, gbc);
	}

	private static boolean canCreateItem(final String itemType, final String items)
	{
		boolean canCreate = true;
		if (itemType.equals("potion"))
		{
			canCreate = false;
		}

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

	public void updateView(PObject thisPObject)
	{
		Spell thisSpell = (Spell) thisPObject;
		pnlDescription.setText(thisSpell.getDescription());	// don't want PI here
		pnlDescription.setDescIsPI(thisSpell.getDescIsPI());
		cmbComponents.setSelectedItem(thisSpell.getComponentList());
		cmbCastingTime.setSelectedItem(thisSpell.getCastingTime());
		cmbRange.setSelectedItem(thisSpell.getRange());
		cmbTarget.setSelectedItem(thisSpell.getTarget());
		cmbDuration.setSelectedItem(thisSpell.getDuration());
		cmbSavingThrow.setSelectedItem(thisSpell.getSaveInfo());
		cmbSpellRes.setSelectedItem(thisSpell.getSpellResistance());
		cmbSchool.setSelectedItem(thisSpell.getSchool());
		cmbSubschool.setSelectedItem(thisSpell.getSubschool());
		if (cmbSubschool.getSelectedIndex() < 0)
		{
			cmbSubschool.setSelectedIndex(0);
		}
		txtCost.setValue(thisSpell.getCost().doubleValue());
		txtXpCost.setValue(thisSpell.getXPCost());

		final String items = thisSpell.getCreatableItem().toLowerCase();
		if (canCreateItem("potion", items))
		{
			chkPotionAllowed.setSelected(true);
		}
		if (canCreateItem("ring", items))
		{
			chkRingAllowed.setSelected(true);
		}
		if (canCreateItem("scroll", items))
		{
			chkScrollAllowed.setSelected(true);
		}
		if (canCreateItem("wand", items))
		{
			chkWandAllowed.setSelected(true);
		}
	}

	public void updateData(PObject thisPObject)
	{
		String aString;
		final Spell s = (Spell) thisPObject;

		s.setDescription(pnlDescription.getText());
		s.setDescIsPI(pnlDescription.getDescIsPI());

		s.setComponentList(".CLEAR");
		aString = (String) cmbComponents.getSelectedItem();
		if (aString != null)
		{
			s.setComponentList(aString);
		}

		s.setCastingTime(".CLEAR");
		aString = (String) cmbCastingTime.getSelectedItem();
		if (aString != null)
		{
			s.setCastingTime(aString);
		}

		s.setRange(".CLEAR");
		aString = (String) cmbRange.getSelectedItem();
		if (aString != null)
		{
			s.setRange(aString);
		}

		aString = (String) cmbTarget.getSelectedItem();
		s.setTarget(aString);

		s.setDuration(".CLEAR");
		aString = (String) cmbDuration.getSelectedItem();
		if (aString != null)
		{
			s.setDuration(aString);
		}

		s.setSaveInfo(".CLEAR");
		aString = (String) cmbSavingThrow.getSelectedItem();
		if (aString != null)
		{
			s.setSaveInfo(aString);
		}

		s.setSpellResistance(".CLEAR");
		aString = (String) cmbSpellRes.getSelectedItem();
		if (aString != null)
		{
			s.setSpellResistance(aString);
		}

		s.setSchool(".CLEAR");
		aString = (String) cmbSchool.getSelectedItem();
		if (aString != null)
		{
			s.setSchool(aString);
		}

		s.setSubschool(".CLEAR");
		aString = (String) cmbSubschool.getSelectedItem();
		if ((aString != null) && !aString.equals("(None)"))
		{
			s.setSubschool(aString);
		}

		s.setCost(Double.toString(txtCost.getValue()));
		s.setXPCost(Integer.toString(txtXpCost.getValue()));

		//
		// potion defaults to not-creatable if not in list; scroll and wand to creatable
		//
		StringBuffer sb = new StringBuffer();
		if (chkPotionAllowed.isSelected())
		{
			sb.append("potion");
		}
		if (!chkRingAllowed.isSelected())
		{
			sb.append("[ring]");
		}
		if (!chkScrollAllowed.isSelected())
		{
			sb.append("[scroll]");
		}
		if (!chkWandAllowed.isSelected())
		{
			sb.append("[wand]");
		}
		s.setCreatableItem(sb.toString());
	}
}
