/*
 * TemplateBasePanel
 * Copyright 2002 (C) James Dempsey <jdempsey@users.sourceforge.net >
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
 * Created on December 13, 2002, 10:33 PM
 *
 * @(#) $Id: TemplateBasePanel.java,v 1.1 2006/02/21 01:28:20 vauchers Exp $
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
import javax.swing.JTextField;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.util.PropertyFactory;

/**
 * <code>TemplateBasePanel</code>
 *
 * @author  James Dempsey <jdempsey@users.sourceforge.net>
 */

public class TemplateBasePanel extends BasePanel
{

	private JCheckBox chkRemovable;
	private JComboBoxEx cmbGenderLock;
	private JComboBoxEx cmbVisible;
	private JTextField txtCR;
	private JTextField txtLevelAdj;
	private JTextField txtSubRegion;
	private JTextField txtSubRace;
	private JComboBoxEx cmbBonusSkillPoints;
	private JComboBoxEx cmbBonusFeats;
	private JComboBoxEx cmbNonProficiencyPenalty;
	private JComboBoxEx cmbSize;
	private JPanel pnlTemplateMisc;
//	private AvailableSelectedPanel pnlTemplateTypes;
	private TypePanel pnlTemplateTypes;

	private static final String[] genderLockValues = new String[]{"None", "Male", "Female", "Neuter"};
	private static final String[] visibleValues = new String[]{"No", "Yes", "Export", "Display"};
	private static final String[] sizeTitles = new String[]{"(no change)", "Fine", "Diminutive", "Tiny", "Small", "Medium", "Large", "Huge", "Gargantuan", "Colossal"};
	private static final String[] sizeAbbrev = new String[]{"", "F", "D", "T", "S", "M", "L", "H", "G", "C"};

	/** Creates new form TemplateBasePanel */
	public TemplateBasePanel()
	{
		initComponents();
		initComponentContents();
	}

	private void initComponentContents()
	{
		cmbGenderLock.setModel(new DefaultComboBoxModel(genderLockValues));
		cmbVisible.setModel(new DefaultComboBoxModel(visibleValues));

		String[] values = new String[20];
		for (int i = 0; i < values.length; ++i)
		{
			values[i] = String.valueOf(i);
		}
		cmbBonusSkillPoints.setModel(new DefaultComboBoxModel(values));
		cmbBonusFeats.setModel(new DefaultComboBoxModel(values));
		values = new String[10];
		for (int i = 0; i < values.length; ++i)
		{
			values[i] = String.valueOf(-1 * i);
		}
		cmbNonProficiencyPenalty.setModel(new DefaultComboBoxModel(values));
		cmbSize.setModel(new DefaultComboBoxModel(sizeTitles));
	}

	/*
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gridBagConstraints;
		JLabel tempLabel;

		pnlTemplateMisc = new JPanel();
		chkRemovable = new JCheckBox();
		cmbGenderLock = new JComboBoxEx();
		cmbBonusSkillPoints = new JComboBoxEx();
		cmbBonusFeats = new JComboBoxEx();
		cmbNonProficiencyPenalty = new JComboBoxEx();
		cmbVisible = new JComboBoxEx();
		txtCR = new JTextField();
		txtLevelAdj = new JTextField();
		cmbSize = new JComboBoxEx();
		txtSubRegion = new JTextField();
		txtSubRace = new JTextField();
		//pnlTemplateTypes = new AvailableSelectedPanel();
		pnlTemplateTypes = new TypePanel(PropertyFactory.getString("in_demEnterNewType"));

		setLayout(new GridBagLayout());

		pnlTemplateMisc.setLayout(new GridBagLayout());

		tempLabel = new JLabel("Visible");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbVisible, gridBagConstraints);

		tempLabel = new JLabel("Removable");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(chkRemovable, gridBagConstraints);

		tempLabel = new JLabel("Gender Locked");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbGenderLock, gridBagConstraints);

		tempLabel = new JLabel("Wpn Non Prof Penalty");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbNonProficiencyPenalty, gridBagConstraints);

		tempLabel = new JLabel("Bonus Skill Pts / Level");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbBonusSkillPoints, gridBagConstraints);

		tempLabel = new JLabel("Bonus Starting Feats");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbBonusFeats, gridBagConstraints);

		tempLabel = new JLabel("CR");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(txtCR, gridBagConstraints);

		tempLabel = new JLabel("Level Adjustment");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(txtLevelAdj, gridBagConstraints);

		tempLabel = new JLabel("Size");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbSize, gridBagConstraints);

		tempLabel = new JLabel("Sub Race");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 5;
		//gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(txtSubRace, gridBagConstraints);

		tempLabel = new JLabel("Sub Region");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 7;
		//gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(txtSubRegion, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		add(pnlTemplateMisc, gridBagConstraints);

		//pnlTemplateTypes.setHeader(PropertyFactory.getString("in_type"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 5;
		gridBagConstraints.gridheight = 4;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(pnlTemplateTypes, gridBagConstraints);

	}

	public void setTypesAvailableList(final List aList, final boolean sort)
	{
		pnlTemplateTypes.setAvailableList(aList, sort);
	}

	public void setTypesSelectedList(final List aList, final boolean sort)
	{
		pnlTemplateTypes.setSelectedList(aList, sort);
	}

	public Object[] getTypesSelectedList()
	{
		return pnlTemplateTypes.getSelectedList();
	}

	public void setIsRemovable(final boolean isRemovable)
	{
		chkRemovable.setSelected(isRemovable);
	}

	public boolean getIsRemovable()
	{
		return chkRemovable.isSelected();
	}

	public void setGenderLock(final String aString)
	{
		if (aString.length() == 0)
		{
			cmbGenderLock.setSelectedItem(Constants.s_NONE);
		}
		else
		{
			for (int i = genderLockValues.length - 1; i >= 0; --i)
			{
				if (genderLockValues[i].equals(aString))
				{
					cmbGenderLock.setSelectedItem(genderLockValues[i]);
					break;
				}
			}
		}
	}

	public String getGenderLock()
	{
		return (String) cmbGenderLock.getSelectedItem();
	}

	public void setVisible(final int aNumber)
	{
		if (aNumber >= PCTemplate.VISIBILITY_HIDDEN && aNumber <= PCTemplate.VISIBILITY_DISPLAY_ONLY)
		{
			cmbVisible.setSelectedIndex(aNumber);
		}
	}

	public int getVisible()
	{
		return cmbVisible.getSelectedIndex();
	}

	public void setSubRegion(final String aString)
	{
		if (Constants.s_NONE.equals(aString) || aString == null || ("".equals(aString)))
		{
			txtSubRegion.setText("");
		}
		else
		{
			txtSubRegion.setText(aString);
		}
	}

	public String getSubRegion()
	{
		if (txtSubRegion.getText() == null || txtSubRegion.getText().trim().length() == 0)
		{
			return Constants.s_NONE;
		}
		else
		{
			return txtSubRegion.getText();
		}
	}

	public void setSubRace(final String aString)
	{
		if (Constants.s_NONE.equals(aString) || aString == null || ("".equals(aString)))
		{
			txtSubRace.setText("");
		}
		else
		{
			txtSubRace.setText(aString);
		}
	}

	public String getSubRace()
	{
		if (txtSubRace.getText() == null || txtSubRace.getText().trim().length() == 0)
		{
			return Constants.s_NONE;
		}
		else
		{
			return txtSubRace.getText();
		}
	}

	public void setBonusSkillPoints(final int bonusSkillPoints)
	{
		if ((bonusSkillPoints >= 0) && (bonusSkillPoints < cmbBonusSkillPoints.getItemCount()))
		{
			cmbBonusSkillPoints.setSelectedIndex(bonusSkillPoints);
		}
	}

	public int getBonusSkillPoints()
	{
		return cmbBonusSkillPoints.getSelectedIndex();
	}

	public void setBonusFeats(final int bonusFeats)
	{
		if ((bonusFeats >= 0) && (bonusFeats < cmbBonusFeats.getItemCount()))
		{
			cmbBonusFeats.setSelectedIndex(bonusFeats);
		}
	}

	public int getBonusFeats()
	{
		return cmbBonusFeats.getSelectedIndex();
	}

	public void setCR(final int argCR)
	{
		txtCR.setText(String.valueOf(argCR));
	}

	public int getCR()
	{
		return Integer.parseInt(txtCR.getText());
	}

	public void setLevelAdjustment(final String levelAdj)
	{
		txtLevelAdj.setText(levelAdj);
	}

	public String getLevelAdjustment()
	{
		return txtLevelAdj.getText();
	}

	public void setNonProficiencyPenalty(final int nonProficiencyPenalty)
	{
		if ((nonProficiencyPenalty <= 0) && (nonProficiencyPenalty * -1 < cmbNonProficiencyPenalty.getItemCount()))
		{
			cmbNonProficiencyPenalty.setSelectedIndex(nonProficiencyPenalty * -1);
		}
	}

	public int getNonProficiencyPenalty()
	{
		return cmbNonProficiencyPenalty.getSelectedIndex() * -1;
	}

	public void setTemplateSize(final String aString)
	{
		cmbSize.setSelectedIndex(0);
		for (int index = 0; index < sizeAbbrev.length; index++)
		{
			if (sizeAbbrev[index].equals(aString))
			{
				cmbSize.setSelectedIndex(index);
				break;
			}
		}
	}

	public String getTemplateSize()
	{
		int index = cmbSize.getSelectedIndex();
		if (index >= 0 && index < sizeAbbrev.length)
		{
			return sizeAbbrev[index];
		}
		else
		{
			return "";
		}
	}

	public void updateView(PObject thisPObject)
	{
		Iterator e;
		String aString;
		PCTemplate thisPCTemplate = (PCTemplate) thisPObject;
		//
		// Populate the types
		//
		List availableList = new ArrayList();
		List selectedList = new ArrayList();
		for (e = Globals.getTemplateList().iterator(); e.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) e.next();
			for (int i = aTemplate.getMyTypeCount(); i > 0;)
			{
				aString = aTemplate.getMyType(--i);
				if (!aString.equals(Constants.s_CUSTOM))
				{
					if (!availableList.contains(aString))
					{
						availableList.add(aString);
					}
				}
			}
		}
		//
		// remove this template's type from the available list and place into selected list
		//
		for (int i = thisPCTemplate.getMyTypeCount(); i > 0;)
		{
			aString = thisPCTemplate.getMyType(--i);
			if (!aString.equals(Constants.s_CUSTOM))
			{
				selectedList.add(aString);
				availableList.remove(aString);
			}
		}
		setTypesAvailableList(availableList, true);
		setTypesSelectedList(selectedList, true);

		setIsRemovable(thisPCTemplate.isRemovable());
		setGenderLock(thisPCTemplate.getGenderLock());
		setVisible(thisPCTemplate.isVisible());
		setSubRegion(thisPCTemplate.getSubRegion());
		setSubRace(thisPCTemplate.getSubRace());
		setBonusSkillPoints(thisPCTemplate.getBonusSkillsPerLevel());
		setNonProficiencyPenalty(thisPCTemplate.getNonProficiencyPenalty());
		setBonusFeats(thisPCTemplate.getBonusInitialFeats());
		setCR(thisPCTemplate.getCR(-1, -1));
		setLevelAdjustment(thisPCTemplate.getLevelAdjustmentFormula());
		setTemplateSize(thisPCTemplate.getTemplateSize());
	}

	public void updateData(PObject thisPObject)
	{
		PCTemplate thisPCTemplate = (PCTemplate) thisPObject;
		thisPCTemplate.setRemovable(getIsRemovable());
		thisPCTemplate.setGenderLock(getGenderLock());
		thisPCTemplate.setVisible(getVisible());
		thisPCTemplate.setSubRegion(getSubRegion());
		thisPCTemplate.setSubRace(getSubRace());
		thisPCTemplate.setBonusSkillsPerLevel(getBonusSkillPoints());
		thisPCTemplate.setBonusInitialFeats(getBonusFeats());
		thisPCTemplate.setCR(getCR());
		thisPCTemplate.setLevelAdjustment(getLevelAdjustment());
		thisPCTemplate.setNonProficiencyPenalty(getNonProficiencyPenalty());
		thisPCTemplate.setTemplateSize(getTemplateSize());

		//
		// Save types
		//
		Object[] sel = getTypesSelectedList();
		thisPObject.setTypeInfo(".CLEAR");
		for (int i = 0; i < sel.length; ++i)
		{
			thisPCTemplate.setTypeInfo(sel[i].toString());
		}

	}

}
