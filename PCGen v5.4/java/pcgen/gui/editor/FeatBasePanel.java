/*
 * FeatBasePanel.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on January 12, 2003, 11:20 PM
 *
 * @(#) $Id: FeatBasePanel.java,v 1.1 2006/02/21 01:18:42 vauchers Exp $
 */

package pcgen.gui.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import pcgen.core.Constants;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.WholeNumberField;
import pcgen.util.DecimalNumberField;
import pcgen.util.PropertyFactory;

/**
 * <code>FeatBasePanel</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */

public class FeatBasePanel extends BasePanel
{
	private static final String[] visibleValues = new String[]{"No", "Yes", "Export", "Display"};

	private DescriptionPanel pnlDescription;
	private WholeNumberField txtSpellLevels;
	private JPanel pnlMisc;
	private JLabel lblCost;
	private JLabel lblSpellLevels;
	private JComboBoxEx cmbVisible;
	private DecimalNumberField txtCost;
	private JLabel lblVisible;
	//private AvailableSelectedPanel pnlFeatType;
	private TypePanel pnlFeatType;
	private JCheckBox chkMultiple;
	private JCheckBox chkStack;

	/** Creates new form SkillBasePanel */
	public FeatBasePanel()
	{
		initComponents();
		initComponentContents();
	}

	private void initComponentContents()
	{
		cmbVisible.setModel(new DefaultComboBoxModel(visibleValues));
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gbc;

		pnlDescription = new DescriptionPanel();
		//pnlFeatType = new AvailableSelectedPanel();
		pnlFeatType = new TypePanel(PropertyFactory.getString("in_demEnterNewType"));
		pnlMisc = new JPanel();
		lblCost = new JLabel();
		lblVisible = new JLabel();
		cmbVisible = new JComboBoxEx();
		chkMultiple = new JCheckBox();
		chkStack = new JCheckBox();
		txtCost = new DecimalNumberField(0, 5);
		lblSpellLevels = new JLabel();
		txtSpellLevels = new WholeNumberField(0, 3);

		setLayout(new GridBagLayout());

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 0.5;
		add(pnlDescription, gbc);

		//pnlFeatType.setHeader(PropertyFactory.getString("in_type"));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		add(pnlFeatType, gbc);

		pnlMisc.setLayout(new GridBagLayout());

		lblCost.setText("Cost");
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 5, 2, 5);
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.1;
		pnlMisc.add(lblCost, gbc);

		lblVisible.setText("Visible");
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 5, 2, 5);
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.1;
		pnlMisc.add(lblVisible, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.4;
		gbc.insets = new Insets(2, 5, 2, 5);
		pnlMisc.add(cmbVisible, gbc);

		chkMultiple.setText("Multiples allowed");
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 5, 2, 5);
		pnlMisc.add(chkMultiple, gbc);

		chkStack.setText("Stacks");
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 5, 2, 5);
		pnlMisc.add(chkStack, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(2, 5, 2, 5);
		gbc.weightx = 0.4;
		pnlMisc.add(txtCost, gbc);

		lblSpellLevels.setText("Spell Levels");
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 5, 2, 5);
		gbc.anchor = GridBagConstraints.EAST;
		pnlMisc.add(lblSpellLevels, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(2, 5, 2, 5);
		pnlMisc.add(txtSpellLevels, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		add(pnlMisc, gbc);

	}

	public void setDescriptionText(String aString)
	{
		pnlDescription.setText(aString);
	}

	public String getDescriptionText()
	{
		return pnlDescription.getText();
	}

	public void setDescIsPI(final boolean descIsPI)
	{
		pnlDescription.setDescIsPI(descIsPI);
	}

	public boolean getDescIsPI()
	{
		return pnlDescription.getDescIsPI();
	}

	public void setVisible(final int aNumber)
	{
		if ((aNumber >= Feat.VISIBILITY_HIDDEN) && (aNumber <= Feat.VISIBILITY_DISPLAY_ONLY))
		{
			cmbVisible.setSelectedIndex(aNumber);
		}
	}

	public int getVisible()
	{
		return cmbVisible.getSelectedIndex();
	}

	public void setMultiples(final boolean argMultiples)
	{
		chkMultiple.setSelected(argMultiples);
	}

	public boolean getMultiples()
	{
		return chkMultiple.isSelected();
	}

	public void setStacks(final boolean argStacks)
	{
		chkStack.setSelected(argStacks);
	}

	public boolean getStacks()
	{
		return chkStack.isSelected();
	}

	public void setTypesAvailableList(final List aList, final boolean sort)
	{
		pnlFeatType.setAvailableList(aList, sort);
	}

	public void setTypesSelectedList(final List aList, final boolean sort)
	{
		pnlFeatType.setSelectedList(aList, sort);
	}

	public Object[] getTypesSelectedList()
	{
		return pnlFeatType.getSelectedList();
	}

	public void setCost(final double argCost)
	{
		txtCost.setValue(argCost);
	}

	public double getCost()
	{
		return txtCost.getValue();
	}

	public void setSpellLevels(final int argSpellLevels)
	{
		txtSpellLevels.setValue(argSpellLevels);
	}

	public int getSpellLevels()
	{
		return txtSpellLevels.getValue();
	}

	public void updateView(PObject thisPObject)
	{
		String aString;
		Iterator e;
		Feat thisFeat = (Feat) thisPObject;

		setDescriptionText(thisPObject.getDescription());	// don't want PI here
		setDescIsPI(thisPObject.getDescIsPI());

		//
		// Populate the types
		//
		List availableList = new ArrayList();
		List selectedList = new ArrayList();
		for (e = Globals.getFeatList().iterator(); e.hasNext();)
		{
			final Feat aFeat = (Feat) e.next();
			for (int i = aFeat.getMyTypeCount(); i > 0;)
			{
				aString = aFeat.getMyType(--i);
				if (!aString.equals(Constants.s_CUSTOM))
				{
					if (!availableList.contains(aString))
					{
						availableList.add(aString);
					}
				}
			}
		}
		// remove this feat's type from the available list and place into selected list
		for (int i = thisFeat.getMyTypeCount(); i > 0;)
		{
			aString = thisFeat.getMyType(--i);
			if (!aString.equals(Constants.s_CUSTOM))
			{
				selectedList.add(aString);
				availableList.remove(aString);
			}
		}
		setTypesAvailableList(availableList, true);
		setTypesSelectedList(selectedList, true);

		setVisible(thisFeat.isVisible());
		setMultiples(thisFeat.isMultiples());
		setStacks(thisFeat.isStacks());
		setCost(thisFeat.getCost());
		setSpellLevels(thisFeat.getAddSpellLevel());
	}

	public void updateData(PObject thisPObject)
	{
		Feat thisFeat = (Feat) thisPObject;
		thisFeat.setDescription(getDescriptionText());
		thisFeat.setDescIsPI(getDescIsPI());
		thisFeat.setMultiples(getMultiples() ? "Y" : "N");
		thisFeat.setStacks(getStacks() ? "Y" : "N");
		thisFeat.setVisible(getVisible());
		thisFeat.setCost(getCost());
		thisFeat.setAddSpellLevel(getSpellLevels());

		Object[] sel = getTypesSelectedList();
		thisFeat.setTypeInfo(".CLEAR");
		for (int i = 0; i < sel.length; ++i)
		{
			thisFeat.setTypeInfo(sel[i].toString());
		}
	}
}
