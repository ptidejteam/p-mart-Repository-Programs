/*
 * EditorBasePanel.java
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
 * Created on October 31, 2002, 4:36 PM
 *
 * @(#) $Id: EditorBasePanel.java,v 1.1 2006/02/21 01:00:36 vauchers Exp $
 */

package pcgen.gui.editor;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import pcgen.core.spell.Spell;
import pcgen.util.PropertyFactory;

/**
 * <code>EditorBasePanel</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */
final class EditorBasePanel extends JPanel
{

	private JPanel pnlProductIdentity;
	private BasePanel jPanel1;
	private JLabel lblSource;
	private JTextField txtSource;
	private JPanel pnlName;
	private JPanel pnlSource;
	private JTextField txtName;
	private JLabel lblName;
	private JCheckBox chkProductIdentity;

	private int editType = EditorConstants.EDIT_NONE;

	/** Creates new form EditorBasePanel */
	EditorBasePanel(int argEditType)
	{
		editType = argEditType;
		initComponents();
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gridBagConstraints;

		pnlName = new JPanel();
		lblName = new JLabel();
		txtName = new JTextField();
		String nameID = "";
		switch (editType)
		{
			case EditorConstants.EDIT_DEITY:
				nameID = "DeityName";
				jPanel1 = new DeityBasePanel();
				break;

			case EditorConstants.EDIT_DOMAIN:
				nameID = "DomainName";
				jPanel1 = new DomainBasePanel();
				break;

			case EditorConstants.EDIT_FEAT:
				nameID = "FeatName";
				jPanel1 = new FeatBasePanel();
				break;

			case EditorConstants.EDIT_LANGUAGE:
				nameID = "LanguageName";
				jPanel1 = new LanguageBasePanel();
				break;

			case EditorConstants.EDIT_RACE:
				nameID = "RaceName";
				jPanel1 = new RaceBasePanel();
				break;

			case EditorConstants.EDIT_SKILL:
				nameID = "SkillName";
				jPanel1 = new SkillBasePanel();
				break;

			case EditorConstants.EDIT_SPELL:
				nameID = "SpellName";
				jPanel1 = new SpellBasePanel();
				break;

			case EditorConstants.EDIT_TEMPLATE:
				nameID = "TemplateName";
				jPanel1 = new TemplateBasePanel();
				break;

			default:
				jPanel1 = new BasePanel();
				break;
		}
		pnlProductIdentity = new JPanel();
		chkProductIdentity = new JCheckBox();
		pnlSource = new JPanel();
		lblSource = new JLabel();
		txtSource = new JTextField();

		setLayout(new GridBagLayout());

		pnlName.setLayout(new GridBagLayout());

		lblName.setText(PropertyFactory.getString("in_dem" + nameID));
		lblName.setDisplayedMnemonic(PropertyFactory.getMnemonic("in_mn_" + nameID));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		pnlName.add(lblName, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		pnlName.add(txtName, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		add(pnlName, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridheight = 8;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(jPanel1, gridBagConstraints);

		pnlProductIdentity.setLayout(new FlowLayout(FlowLayout.RIGHT));

		chkProductIdentity.setText(PropertyFactory.getString("in_demProIden"));
		chkProductIdentity.setMnemonic(PropertyFactory.getMnemonic("in_mn_demProIden"));
		chkProductIdentity.setHorizontalTextPosition(SwingConstants.LEADING);
		pnlProductIdentity.add(chkProductIdentity);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 10;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		add(pnlProductIdentity, gridBagConstraints);

		pnlSource.setLayout(new GridBagLayout());

		lblSource.setLabelFor(txtSource);
		lblSource.setText(PropertyFactory.getString("in_sourceLabel"));
		lblSource.setDisplayedMnemonic(PropertyFactory.getMnemonic("in_mn_sourceLabel"));
		lblSource.setPreferredSize(new Dimension(70, 16));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		pnlSource.add(lblSource, gridBagConstraints);

		txtSource.setPreferredSize(new Dimension(280, 20));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		pnlSource.add(txtSource, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 9;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		add(pnlSource, gridBagConstraints);
	}

	public void setNameText(String aString)
	{
		txtName.setText(aString);
	}

	public String getNameText()
	{
		return txtName.getText().trim();
	}

	public void setSourceText(String aString)
	{
		txtSource.setText(aString);
	}

	public String getSourceText()
	{
		return txtSource.getText().trim();
	}

	public void setProductIdentity(boolean isPI)
	{
		chkProductIdentity.setSelected(isPI);
	}

	public boolean getProductIdentity()
	{
		return chkProductIdentity.isSelected();
	}

	public void setHolyItemText(final String aString)
	{
		jPanel1.setHolyItemText(aString);
	}

	public String getHolyItemText()
	{
		return jPanel1.getHolyItemText();
	}

	public void setDescriptionText(final String aString)
	{
		jPanel1.setDescriptionText(aString);
	}

	public String getDescriptionText()
	{
		return jPanel1.getDescriptionText();
	}

	public void setDeityAlignment(final String aString)
	{
		jPanel1.setDeityAlignment(aString);
	}

	public String getDeityAlignment()
	{
		return jPanel1.getDeityAlignment();
	}

	public void setFavoredWeaponsAvailableList(final ArrayList aList, final boolean sort)
	{
		jPanel1.setFavoredWeaponsAvailableList(aList, sort);
	}

	public Object[] getFavoredWeaponsAvailableList()
	{
		return jPanel1.getFavoredWeaponsAvailableList();
	}

	public void setFavoredWeaponsSelectedList(final ArrayList aList, final boolean sort)
	{
		jPanel1.setFavoredWeaponsSelectedList(aList, sort);
	}

	public Object[] getFavoredWeaponsSelectedList()
	{
		return jPanel1.getFavoredWeaponsSelectedList();
	}

	public void setTypesAvailableList(final ArrayList aList, final boolean sort)
	{
		jPanel1.setTypesAvailableList(aList, sort);
	}

	public void setTypesSelectedList(final ArrayList aList, final boolean sort)
	{
		jPanel1.setTypesSelectedList(aList, sort);
	}

	public Object[] getTypesSelectedList()
	{
		return jPanel1.getTypesSelectedList();
	}

	public void setIsUntrained(final boolean isUntrained)
	{
		jPanel1.setIsUntrained(isUntrained);
	}

	public boolean getIsUntrained()
	{
		return jPanel1.getIsUntrained();
	}

	public void setIsExclusive(final boolean isExclusive)
	{
		jPanel1.setIsExclusive(isExclusive);
	}

	public boolean getIsExclusive()
	{
		return jPanel1.getIsExclusive();
	}

	public void setKeyStat(final String aString)
	{
		jPanel1.setKeyStat(aString);
	}

	public String getKeyStat()
	{
		return jPanel1.getKeyStat();
	}

	public void setArmorCheck(final int aCheck)
	{
		jPanel1.setArmorCheck(aCheck);
	}

	public int getArmorCheck()
	{
		return jPanel1.getArmorCheck();
	}

	public void setIsRemovable(final boolean isRemovable)
	{
		jPanel1.setIsRemovable(isRemovable);
	}

	public boolean getIsRemovable()
	{
		return jPanel1.getIsRemovable();
	}

	public void setGenderLock(final String aString)
	{
		jPanel1.setGenderLock(aString);
	}

	public String getGenderLock()
	{
		return jPanel1.getGenderLock();
	}

	public void setVisible(final int aNumber)
	{
		jPanel1.setVisible(aNumber);
	}

	public int getVisible()
	{
		return jPanel1.getVisible();
	}

	public void setSubRegion(final String aString)
	{
		jPanel1.setSubRegion(aString);
	}

	public String getSubRegion()
	{
		return jPanel1.getSubRegion();
	}

	public void setSubRace(final String aString)
	{
		jPanel1.setSubRace(aString);
	}

	public String getSubRace()
	{
		return jPanel1.getSubRace();
	}

	public void setBonusSkillPoints(final int bonusSkillPoints)
	{
		jPanel1.setBonusSkillPoints(bonusSkillPoints);
	}

	public int getBonusSkillPoints()
	{
		return jPanel1.getBonusSkillPoints();
	}

	public void setNonProficiencyPenalty(final int nonProficiencyPenalty)
	{
		jPanel1.setNonProficiencyPenalty(nonProficiencyPenalty);
	}

	public int getNonProficiencyPenalty()
	{
		return jPanel1.getNonProficiencyPenalty();
	}

	public void setBonusFeats(final int bonusFeats)
	{
		jPanel1.setBonusFeats(bonusFeats);
	}

	public int getBonusFeats()
	{
		return jPanel1.getBonusFeats();
	}

	public void setCR(final int CR)
	{
		jPanel1.setCR(CR);
	}

	public int getCR()
	{
		return jPanel1.getCR();
	}

	public void setLevelAdjustment(final String aString)
	{
		jPanel1.setLevelAdjustment(aString);
	}

	public String getLevelAdjustment()
	{
		return jPanel1.getLevelAdjustment();
	}

	public void setTemplateSize(final String aString)
	{
		jPanel1.setTemplateSize(aString);
	}

	public String getTemplateSize()
	{
		return jPanel1.getTemplateSize();
	}

	public void setDescIsPI(boolean isPI)
	{
		jPanel1.setDescIsPI(isPI);
	}

	public boolean getDescIsPI()
	{
		return jPanel1.getDescIsPI();
	}

	public String getMonsterClass()
	{
		return jPanel1.getMonsterClass();
	}

	public void setHands(final int aNumber)
	{
		jPanel1.setHands(aNumber);
	}

	public int getHands()
	{
		return jPanel1.getHands();
	}

	public void setMonsterClass(final String aString)
	{
		jPanel1.setMonsterClass(aString);
	}

	public void setMonsterClassList(final ArrayList classList)
	{
		jPanel1.setMonsterClassList(classList);
	}

	public void setHitDiceAdvancement(final int[] advancement, final boolean isAdvancementUnlimited)
	{
		jPanel1.setHitDiceAdvancement(advancement, isAdvancementUnlimited);
	}

	public int[] getHitDiceAdvancement()
	{
		return jPanel1.getHitDiceAdvancement();
	}

	public boolean getHitDiceAdvancementUnlimited()
	{
		return false;
	}

	public void setLegs(final int aNumber)
	{
		jPanel1.setLegs(aNumber);
	}

	public int getLegs()
	{
		return jPanel1.getLegs();
	}

	public void setRaceSize(final String aString)
	{
		jPanel1.setRaceSize(aString);
	}

	public String getRaceSize()
	{
		return jPanel1.getRaceSize();
	}

	public void setDisplayName(final String displayName)
	{
		jPanel1.setDisplayName(displayName);
	}

	public String getDisplayName()
	{
		return jPanel1.getDisplayName();
	}

	public void setSkillMultiplier(final int aNumber)
	{
		jPanel1.setSkillMultiplier(aNumber);
	}

	public int getSkillMultiplier()
	{
		return jPanel1.getSkillMultiplier();
	}

	public int getMonsterLevel()
	{
		return jPanel1.getMonsterLevel();
	}

	public void setReach(final int aNumber)
	{
		jPanel1.setReach(aNumber);
	}

	public void setMonsterLevel(final int aNumber)
	{
		jPanel1.setMonsterLevel(aNumber);
	}

	public int getReach()
	{
		return jPanel1.getReach();
	}

	public void setMultiples(final boolean argMultiples)
	{
		jPanel1.setMultiples(argMultiples);
	}

	public boolean getMultiples()
	{
		return jPanel1.getMultiples();
	}

	public void setStacks(final boolean argStacks)
	{
		jPanel1.setStacks(argStacks);
	}

	public boolean getStacks()
	{
		return jPanel1.getStacks();
	}

	public void setCost(final double argCost)
	{
		jPanel1.setCost(argCost);
	}

	public double getCost()
	{
		return jPanel1.getCost();
	}

	public void setSpellLevels(final int argSpellLevels)
	{
		jPanel1.setSpellLevels(argSpellLevels);
	}

	public int getSpellLevels()
	{
		return jPanel1.getSpellLevels();
	}

	public void setRepIncrease(final int argRepInc)
	{
		jPanel1.setRepIncrease(argRepInc);
	}

	public int getRepIncrease()
	{
		return jPanel1.getRepIncrease();
	}

	public void setHitDiceNumber(final int aNumber)
	{
		jPanel1.setHitDiceNumber(aNumber);
	}

	public int getHitDiceNumber()
	{
		return jPanel1.getHitDiceNumber();
	}

	public void setHitDiceSize(final int aNumber)
	{
		jPanel1.setHitDiceSize(aNumber);
	}

	public int getHitDiceSize()
	{
		return jPanel1.getHitDiceSize();
	}

	public void setSpellInfo(final Spell aSpell)
	{
		jPanel1.setSpellInfo(aSpell);
	}

}
