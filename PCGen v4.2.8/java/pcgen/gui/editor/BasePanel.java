/*
 * BasePanel.java
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
 * Created on November 5, 2002, 4:29 PM
 *
 * @(#) $Id: BasePanel.java,v 1.1 2006/02/21 01:00:36 vauchers Exp $
 */

package pcgen.gui.editor;

import java.util.ArrayList;
import javax.swing.JPanel;
import pcgen.core.Skill;
import pcgen.core.spell.Spell;

/**
 * <code>BasePanel</code>
 * TODO: Shouldn't this be abstract?
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */

class BasePanel extends JPanel
{

	public void setTypesAvailableList(final ArrayList aList, final boolean sort)
	{
	}

	public void setHolyItemText(final String aString)
	{
	}

	public String getHolyItemText()
	{
		return null;
	}

	public void setDescriptionText(final String aString)
	{
	}

	public String getDescriptionText()
	{
		return null;
	}

	public void setDeityAlignment(final String aString)
	{
	}

	public String getDeityAlignment()
	{
		return null;
	}

	public void setFavoredWeaponsAvailableList(final ArrayList aList, final boolean sort)
	{
	}

	public Object[] getFavoredWeaponsAvailableList()
	{
		return null;
	}

	public void setFavoredWeaponsSelectedList(final ArrayList aList, final boolean sort)
	{
	}

	public Object[] getFavoredWeaponsSelectedList()
	{
		return null;
	}

	public void setTypesSelectedList(final ArrayList aList, final boolean sort)
	{
	}

	public Object[] getTypesSelectedList()
	{
		return null;
	}

	public void setIsUntrained(final boolean isUntrained)
	{
	}

	public boolean getIsUntrained()
	{
		return false;
	}

	public void setIsExclusive(final boolean isExclusive)
	{
	}

	public boolean getIsExclusive()
	{
		return false;
	}

	public void setKeyStat(final String aString)
	{
	}

	public String getKeyStat()
	{
		return null;
	}

	public void setArmorCheck(final int aCheck)
	{
	}

	public int getArmorCheck()
	{
		return Skill.ACHECK_NONE;
	}

	public void setIsRemovable(final boolean isRemovable)
	{
	}

	public boolean getIsRemovable()
	{
		return false;
	}

	public void setGenderLock(final String aString)
	{
	}

	public String getGenderLock()
	{
		return null;
	}

	public void setVisible(final int aNumber)
	{
	}

	public int getVisible()
	{
		return 0;
	}

	public void setSubRegion(final String aString)
	{
	}

	public String getSubRegion()
	{
		return null;
	}

	public void setSubRace(final String aString)
	{
	}

	public String getSubRace()
	{
		return null;
	}

	public void setBonusSkillPoints(final int bonusSkillPoints)
	{
	}

	public int getBonusSkillPoints()
	{
		return 0;
	}

	public void setNonProficiencyPenalty(final int nonProficiencyPenalty)
	{
	}

	public int getNonProficiencyPenalty()
	{
		return 0;
	}

	public void setBonusFeats(final int bonusFeats)
	{
	}

	public int getBonusFeats()
	{
		return 0;
	}

	public void setCR(final int CR)
	{
	}

	public int getCR()
	{
		return 0;
	}

	public void setLevelAdjustment(final String aString)
	{
	}

	public String getLevelAdjustment()
	{
		return null;
	}

	public void setTemplateSize(final String aString)
	{
	}

	public String getTemplateSize()
	{
		return null;
	}

	public void setDescIsPI(final boolean descIsPI)
	{
	}

	public boolean getDescIsPI()
	{
		return false;
	}

	public String getMonsterClass()
	{
		return null;
	}

	public void setHands(final int aNumber)
	{
	}

	public int getHands()
	{
		return 0;
	}

	public void setMonsterClass(final String aString)
	{
	}

	public void setMonsterClassList(final ArrayList classList)
	{
	}

	public void setHitDiceAdvancement(final int[] advancement, final boolean isAdvancementUnlimited)
	{
	}

	public int[] getHitDiceAdvancement()
	{
		return null;
	}

	public boolean getHitDiceAdvancementUnlimited()
	{
		return false;
	}

	public void setLegs(final int aNumber)
	{
	}

	public int getLegs()
	{
		return 0;
	}

	public void setRaceSize(final String aString)
	{
	}

	public String getRaceSize()
	{
		return null;
	}

	public void setDisplayName(final String displayName)
	{
	}

	public String getDisplayName()
	{
		return null;
	}

	public void setSkillMultiplier(final int aNumber)
	{
	}

	public int getSkillMultiplier()
	{
		return 0;
	}

	public int getMonsterLevel()
	{
		return 0;
	}

	public void setReach(final int aNumber)
	{
	}

	public void setMonsterLevel(final int aNumber)
	{
	}

	public int getReach()
	{
		return 0;
	}

	public void setMultiples(final boolean argMultiples)
	{
	}

	public boolean getMultiples()
	{
		return false;
	}

	public void setStacks(final boolean argStacks)
	{
	}

	public boolean getStacks()
	{
		return false;
	}

	public void setCost(final double argCost)
	{
	}

	public double getCost()
	{
		return 0.0;
	}

	public void setSpellLevels(final int argSpellLevels)
	{
	}

	public int getSpellLevels()
	{
		return 0;
	}

	public void setRepIncrease(final int argRepInc)
	{
	}

	public int getRepIncrease()
	{
		return 0;
	}

	public void setHitDiceNumber(final int aNumber)
	{
	}

	public int getHitDiceNumber()
	{
		return 0;
	}

	public void setHitDiceSize(final int aNumber)
	{
	}

	public int getHitDiceSize()
	{
		return 0;
	}

	public void setSpellInfo(Spell aSpell)
	{
	}
}
