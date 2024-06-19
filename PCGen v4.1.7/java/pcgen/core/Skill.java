/*
 * Skill.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
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
 *
 * $Id: Skill.java,v 1.1 2006/02/21 00:57:41 vauchers Exp $
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import pcgen.gui.ChooserFactory;
import pcgen.gui.ChooserInterface;
import pcgen.util.GuiFacade;

/**
 * <code>Skill</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public final class Skill extends PObject
{
	boolean isClassSkill(ArrayList aList)
	{
		for (Iterator e = aList.iterator(); e.hasNext();)
		{
			if (isClassSkill((PCClass) e.next()))
			{
				return true;
			}
		}
		return false;
	}

	public boolean isClassSkill(PCClass pc)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null || pc == null)
		{
			return false;
		}
		if (aPC.getRace().hasCSkill(name))
		{
			return true;
		}
		if (pc.skillList().contains(name))
		{
			return true;
		}
		for (Iterator i = classList.iterator(); i.hasNext();)
		{
			final String aString = i.next().toString();
			if (aString.length() > 0 && aString.charAt(0) == '!' &&
				(aString.substring(1).equalsIgnoreCase(pc.getName()) || aString.substring(1).equalsIgnoreCase(pc.getSubClassName())))
			{
				return false; // this is an excluded-from-class-skill list
			}
			if ("ALL".equals(aString) || aString.equals(pc.getName()) || aString.equals(pc.getSubClassName()) ||
				(pc.getClassSkillList() != null && pc.getClassSkillList().contains(aString)))
			{
				return true;
			}
		}
		CharacterDomain aCD = null;
		for (Iterator e = aPC.getCharacterDomainList().iterator(); e.hasNext();)
		{
			aCD = (CharacterDomain) e.next();
			if (aCD.getDomain() != null
				&& aCD.isFromPCClass(pc.getName())
				&& aCD.getDomain().hasCSkill(name))
			{
				return true;
			}
		}
		if (aPC.getDeity() != null && aPC.getDeity().hasCSkill(name))
		{
			return true;
		}
		if (pc.hasCSkill(name))
		{
			return true;
		}
		for (Iterator i = aPC.aggregateFeatList().iterator(); i.hasNext();)
		{
			final Feat aFeat = (Feat) i.next();
			if (aFeat.hasCSkill(name))
			{
				return true;
			}
		}
		for (Iterator i = aPC.getSkillList().iterator(); i.hasNext();)
		{
			final Skill aSkill = (Skill) i.next();
			if (aSkill.hasCSkill(name))
			{
				return true;
			}
		}
		for (Iterator e = aPC.getEquipmentList().iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();
			if (eq.isEquipped())
			{
				if (eq.hasCSkill(name))
				{
					return true;
				}

				ArrayList aList = eq.getEqModifierList(true);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						if (eqMod.hasCSkill(name))
						{
							return true;
						}
					}
				}
				aList = eq.getEqModifierList(false);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						if (eqMod.hasCSkill(name))
						{
							return true;
						}
					}
				}
			}
		}
		for (Iterator i = aPC.getTemplateList().iterator(); i.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) i.next();
			if (aTemplate.hasCSkill(name))
			{
				return true;
			}
		}
		return false;
	}

	private String keyStat = "";

	private String rootName = "";
	private ArrayList classList = new ArrayList(); // list of classes with class-access to this skill
	//private String isExclusive = "N";
	private boolean isExclusive = false;
	private String untrained = "Y";
	private ArrayList rankList = new ArrayList();
	private ArrayList synergyList = null;
	private ArrayList choiceList = null;
	private int outputIndex = 0;

	//constants for Cost Type String
	public static final String COST_CLASS = "CLASS";
	public static final String COST_XCLASS = "CROSS-CLASS";
	public static final String COST_EXCL = "EXCLUSIVE";
	private static final String COST_UNK = "UNKNOWN";

	public static final int ACHECK_NONE = 0;		// No
	public static final int ACHECK_YES = 1;		// Yes
	public static final int ACHECK_NONPROF = 2;	// Only if not proficient
	public static final int ACHECK_WEIGHT = 3;	// -1 per 5 lbs carried or equipped
	private int aCheck = ACHECK_NONE;

	private boolean required = false;

	public Skill()
	{
	}

	public ArrayList getClassList()
	{
		return classList;
	}

	public void addClassList(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
		{
			final String bString = aTok.nextToken();
			// could be "ALL", "some_class_name" or "!some_class_name"
			// sort the !some_class_name to the front of classList
			if (bString.length() > 0 && bString.charAt(0) == '!' && classList.size() > 0)
			{
				classList.add(0, bString);
			}
			else
			{
				classList.add(bString);
			}
		}
	}

	String getRootName()
	{
		return rootName;
	}

	public void setRequired(boolean required)
	{
		this.required = required;
	}

	boolean isRequired()
	{
		return required;
	}

	public String toString()
	{
		return name;
	}

	public String qualifiedName()
	{
		if (getAssociatedCount() == 0)
		{
			return name;
		}
		final StringBuffer buffer = new StringBuffer(getAssociatedCount() * 20);
		buffer.append(name).append("(");
		for (int i = 0; i < getAssociatedCount(); i++)
		{
			if (i > 0)
			{
				buffer.append(", ");
			}
			buffer.append(getAssociated(i));
		}
		buffer.append(")");
		return buffer.toString();
	}

	public void setRootName(String aString)
	{
		rootName = aString;
	}

	public String getKeyStat()
	{
		return keyStat;
	}

	public void setKeyStat(String aString)
	{
		keyStat = aString;
	}

	/**
	 * Return the output index, which controls the order in
	 * which the skills appear on a character sheet.
	 * Note: -1 means hidden and 0 means nto set
	 *
	 * <br>author: James Dempsey 14-Jun-02
	 *
	 * @return the output index for this skill (-1=hidden, 0=not set)
	 */
	public int getOutputIndex()
	{
		return outputIndex;
	}

	/**
	 * Set this skill's output index, which controls the order
	 * in which the skills appear on a character sheet.
	 * Note: -1 means hidden and 0 means nto set
	 *
	 * <br>author: James Dempsey 14-Jun-02
	 *
	 * @param newIndex the new output index for this skill (-1=hidden, 0=not set)
	 */
	public void setOutputIndex(int newIndex)
	{
		outputIndex = newIndex;
	}

	public Integer modifier()
	{
		final int stat = Globals.getStatFromAbbrev(keyStat);
		int bonus = 0;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null)
		{
			return new Integer(0);
		}

		if (stat >= 0)
		{
			bonus = aPC.getStatList().getStatModFor(keyStat);
			bonus += aPC.getTotalBonusTo("SKILL", "STAT=" + keyStat, false);
		}
		bonus += aPC.getTotalBonusTo("SKILL", name, true);
		//loop through all current skill types checking for boni
		for (int typesForBonus = 0; typesForBonus < getMyTypeCount(); typesForBonus++)
		{
			String singleType = getMyType(typesForBonus);
			bonus += aPC.getTotalBonusTo("SKILL", "TYPE=" + singleType, true);
		}
		bonus += aPC.getTotalBonusTo("SKILL", "LIST", false);
		//these next two if-blocks try to get BONUS:[C]CSKILL|TYPE=xxx|y to function
		if (isClassSkill(aPC.getClassList()))
		{
			bonus += aPC.getTotalBonusTo("CSKILL", name, true);
			//loop through all current skill types checking for boni
			for (int typesForBonus = 0; typesForBonus < getMyTypeCount(); typesForBonus++)
			{
				String singleType = getMyType(typesForBonus);
				bonus += aPC.getTotalBonusTo("CSKILL", "TYPE=" + singleType, true);
			}
			bonus += aPC.getTotalBonusTo("CSKILL", "LIST", false);
		}
		if (!isClassSkill(aPC.getClassList()) && !isExclusive())
		{
			bonus += aPC.getTotalBonusTo("CCSKILL", name, true);
			//loop through all current skill types checking for boni
			for (int typesForBonus = 0; typesForBonus < getMyTypeCount(); typesForBonus++)
			{
				String singleType = getMyType(typesForBonus);
				bonus += aPC.getTotalBonusTo("CCSKILL", "TYPE=" + singleType, true);
			}
			bonus += aPC.getTotalBonusTo("CCSKILL", "LIST", false);
		}
		//the above two if-blocks try to get BONUS:[C]CSKILL|TYPE=xxx|y to function
		bonus += aPC.getRace().bonusForSkill(this.getName());
		bonus += bonusTo("SKILL", name);
		if (synergyList != null && getSynergyList().size() > 0)
		{
			for (Iterator e = getSynergyList().iterator(); e.hasNext();)
			{
				boolean flag = true;
				String aString = (String) e.next();
				StringTokenizer aTok = new StringTokenizer(aString, "=", false);
				final String aList = aTok.nextToken();
				final int minRank = Integer.parseInt(aTok.nextToken());
				final int aBonus = Integer.parseInt(aTok.nextToken());
				aTok = new StringTokenizer(aList, ",", false);
				while (aTok.hasMoreTokens() && flag)
				{
					aString = aTok.nextToken();
					Skill aSkill = aPC.getSkillNamed(aString);
					flag = ((aSkill != null) && (aSkill.getRank().intValue() >= minRank));
				}
				if (flag)
				{
					bonus += aBonus;
				}
			}
		}
		if (aCheck != ACHECK_NONE)
		{
			int minBonus = 0;
			int maxBonus = 0;
			final Float totalWeight = aPC.totalWeight();
			if (aCheck == ACHECK_WEIGHT && SettingsHandler.isApplyWeightPenaltyToSkills())
			{
				maxBonus = -(int) (totalWeight.doubleValue() / 5.0);
			}
			else if (aCheck == ACHECK_WEIGHT)
			{
				//Do nothing. This is to simulate taking everything off before going swimming. Freq #505977
			}
			else
			{
				if (aCheck != ACHECK_NONPROF && SettingsHandler.isApplyLoadPenaltyToACandSkills())
				{
					final int load = Globals.loadTypeForLoadScore(aPC.getVariableValue("LOADSCORE", "").intValue(), aPC.totalWeight());
					switch (load)
					{
						case Constants.MEDIUM_LOAD:
							minBonus = -3;
							break;

						case Constants.HEAVY_LOAD:
						case Constants.OVER_LOAD:
							minBonus = -6;
							break;

						case Constants.LIGHT_LOAD:
							break;

						default:
							Globals.errorPrint(getName() + ":in Skill.modifier the load " + load + " is not supported.");
							break;
					}
				}
				for (Iterator e = aPC.getEquipmentOfType("Armor", 1).iterator(); e.hasNext();)
				{
					final Equipment eq = (Equipment) e.next();
					if ((aCheck == ACHECK_YES) || ((aCheck == ACHECK_NONPROF) && !aPC.isProficientWith(eq)))
					{
						maxBonus += eq.acCheck().intValue();
					}
				}
				for (Iterator e = aPC.getEquipmentOfType("Shield", 1).iterator(); e.hasNext();)
				{
					final Equipment eq = (Equipment) e.next();
					if ((aCheck == ACHECK_YES) || ((aCheck == ACHECK_NONPROF) && !aPC.isProficientWith(eq)))
					{
						maxBonus += eq.acCheck().intValue();
					}
				}
			}
			bonus += Math.min(maxBonus, minBonus);
		}

		return new Integer(bonus);
	}

	private boolean isCrossClassSkill(PCClass pc)
	{
		if (isClassSkill(pc))
		{
			return false;
		}
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null || pc == null)
		{
			return false;
		}
		if (aPC.getRace().hasCCSkill(name))
		{
			return true;
		}
		for (Iterator e = aPC.getCharacterDomainList().iterator(); e.hasNext();)
		{
			final CharacterDomain aCD = (CharacterDomain) e.next();
			if (aCD.getDomain() != null
				&& aCD.isFromPCClass(pc.getName())
				&& aCD.getDomain().hasCCSkill(name))
			{
				return true;
			}
		}
		if (aPC.getDeity() != null && aPC.getDeity().hasCCSkill(name))
		{
			return true;
		}
		if (pc.hasCCSkill(name))
		{
			return true;
		}
		for (Iterator i = aPC.aggregateFeatList().iterator(); i.hasNext();)
		{
			final Feat aFeat = (Feat) i.next();
			if (aFeat.hasCCSkill(name))
			{
				return true;
			}
		}
		for (Iterator i = aPC.getSkillList().iterator(); i.hasNext();)
		{
			final Skill aSkill = (Skill) i.next();
			if (aSkill.hasCCSkill(name))
			{
				return true;
			}
		}
		for (Iterator e = aPC.getEquipmentList().iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();
			if (eq.isEquipped())
			{
				if (eq.hasCCSkill(name))
				{
					return true;
				}

				ArrayList aList = eq.getEqModifierList(true);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						if (eqMod.hasCCSkill(name))
						{
							return true;
						}
					}
				}
				aList = eq.getEqModifierList(false);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						if (eqMod.hasCCSkill(name))
						{
							return true;
						}
					}
				}
			}
		}
		for (Iterator i = aPC.getTemplateList().iterator(); i.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) i.next();
			if (aTemplate.hasCCSkill(name))
			{
				return true;
			}
		}
		return false;
	}

	public boolean isExclusive()
	{
		return isExclusive;
	}

	//
	// Convenience method
	//
	public String getExclusive()
	{
		return isExclusive ? "Y" : "N";
	}

	public void setIsExclusive(boolean argExclusive)
	{
		isExclusive = argExclusive;
	}

	public String getSkillCostType(PCClass pc)
	{
		// isCrossClassSkill() doesn't appear to work, so just go by actual cost values
		if (costForPCClass(pc).intValue() == 1)
		{
			return COST_CLASS;
		}
		else if (costForPCClass(pc).intValue() == 2)
		{
			return COST_XCLASS;
		}
		else if (isExclusive)
		{
			return COST_EXCL;
		}
		return COST_UNK;
	}

	public String getUntrained()
	{
		return untrained;
	}

	public boolean isUntrained()
	{
		if (untrained.length() != 0)
		{
			return untrained.charAt(0) == 'Y';
		}
		return false;
	}

	public void setUntrained(String aString)
	{
		untrained = aString;
	}

	void replaceClassRank(String oldClass, String newClass)
	{
		final String oldClassString = oldClass + ":";
		for (int i = 0; i < rankList.size(); i++)
		{
			final String bSkill = (String) rankList.get(i);
			if (bSkill.startsWith(oldClassString))
			{
				rankList.set(i, newClass + bSkill.substring(oldClass.length()));
			}
		}
	}

	/** returns ranks taken specifically in skill
	 * @return ranks taken in skill
	 */
	public Float getRank()
	{
		double rank = 0.0;
		for (int i = 0; i < rankList.size(); i++)
		{
			final String bSkill = (String) rankList.get(i);
			final int iOffs = bSkill.indexOf(':');
			//
			// Ignore -1 return code (as -1 + 1 = 0 and that's the start of the string)
			//
			rank += Double.parseDouble(bSkill.substring(iOffs + 1));
		}
		return new Float(rank);
	}

	public ArrayList getRankList()
	{
		return rankList;
	}

	/**
	 * Returns the total ranks of a skill
	 *  rank + bonus ranks (racial, class, etc. bonuses)
	 * @return rank + bonus ranks (racial, class, etc. bonuses)
	 */
	public Float getTotalRank()
	{
		return new Float(getRank().doubleValue() + getRankAdj().doubleValue());
	}

	/** returns the adjustments to rank
	 *
	 * @return the adjustments to rank
	 */
	private Float getRankAdj()
	{
		final PlayerCharacter currentPC = Globals.getCurrentPC();
		final String name = getName();
		return new Float(currentPC.getTotalBonusTo("SKILLRANK", name, true));
	}

	/** Set the ranks for the specified class to zero
	 *
	 * @param aClass
	 */
	public void setZeroRanks(PCClass aClass)
	{
		if (aClass == null)
		{
			return;
		}

		final String aCName = aClass.getName();
		String bSkill = "";
		int idx;
		//
		// Find the skill and class in question
		//
		final String aCNameString = aCName + ":";
		for (idx = 0; idx < rankList.size(); idx++)
		{
			bSkill = (String) rankList.get(idx);
			if (bSkill.startsWith(aCNameString))
			{
				break;
			}
		}
		if (idx >= rankList.size())
		{
			return;
		}
		final double curRankCost = Double.parseDouble(bSkill.substring(aCName.length() + 1));
		final String aResp = modRanks(-curRankCost, aClass);
		if (aResp.length() != 0)
		{
			Globals.debugPrint(aResp);
		}
	}

	public String modRanks(double rankMod, PCClass aClass)
	{
		return modRanks(rankMod, aClass, false);
	}

	public String modRanks(double rankMod, PCClass aClass, boolean ignorePrereqs)
	{
		int i = 0;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (!ignorePrereqs)
		{
			if (aClass == null)
			{
				return "You must be at least level one before you can purchase skills.";
			}

			i = costForPCClass(aClass).intValue();
			if (i == 0)
			{
				return "You cannot purchase this exclusive skill.";
			}

			if (rankMod > 0.0 && aClass.getSkillPool().floatValue() < rankMod * i)
			{
				return "You do not have enough skill points.";
			}

			final double maxRank = aPC.getMaxRank(getName(), aClass).doubleValue();
			if (!SettingsHandler.isBoolBypassMaxSkillRank() && (rankMod > 0.0))
			{
				final double ttlRank = getTotalRank().doubleValue();
				if (ttlRank >= maxRank)
				{
					return "Skill rank at maximum (" + maxRank + ") for your level.";
				}
				if ((ttlRank + rankMod) > maxRank)
				{
					return "Raising skill would make it above maximum (" + maxRank + ") for your level.";
				}
			}
		}

		if ((getRank().doubleValue() + rankMod) < 0.0)
		{
			return "Cannot lower rank below 0";
		}

		String aCName = "None";
		if (aClass != null)
		{
			aCName = aClass.getName();
		}

		String bSkill = "";
		int idx;
		//
		// Find the skill and class in question
		//
		final String aCNameString = aCName + ":";
		for (idx = 0; idx < rankList.size(); idx++)
		{
			bSkill = (String) rankList.get(idx);
			if (bSkill.startsWith(aCNameString))
			{
				break;
			}
		}
		if (idx >= rankList.size())
		{
			//
			// If we are trying to lower a rank, and we happen to be using an older
			// character we've loaded, check to see if there is a value for class "None"
			// and allow the user to modify this.
			//
			if (rankMod < 0.0)
			{
				for (idx = 0; idx < rankList.size(); idx++)
				{
					bSkill = (String) rankList.get(idx);
					if (bSkill.startsWith("None:"))
					{
						break;
					}
				}
			}
			if (idx >= rankList.size())
			{
				bSkill = aCName + ":0";
			}
		}

		final int iOffs = bSkill.indexOf(':');
		final double curRank = Double.parseDouble(bSkill.substring(iOffs + 1));
		if (curRank == 0.0 && rankMod < 0.0)
		{
			return "No more ranks found for class: " + aCName + ". Try a different one.";
		}

		if (idx >= rankList.size())
		{
			rankList.add(idx, bSkill);
		}

		rankMod = modRanks2(rankMod, idx, bSkill);

		if (!ignorePrereqs)
		{
			if (aClass != null)
			{
				aClass.setSkillPool(new Integer(aClass.getSkillPool().intValue() - (int) (i * rankMod)));
			}
			aPC.setSkillPoints(aPC.getSkillPoints() - (int) (i * rankMod));
		}
		return "";
	}

	private double modRanks2(double g, int idx, String bSkill)
	{
		final int iOffs = bSkill.indexOf(':');
		final double curRank = Double.parseDouble(bSkill.substring(iOffs + 1));		// current rank for currently selected class
		double newRank = curRank + g;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (!aPC.isImporting())
		{
			if (choiceList != null && getChoiceList().size() > 0 && (g != 0) && (curRank != (int) (newRank)))
			{
				final ArrayList aArrayList = new ArrayList();
				String title = new String();
				final double rankAdjustment = 0.0;
				for (Iterator e = getChoiceList().iterator(); e.hasNext();)
				{
					String aString = (String) e.next();
					/** putting commas between lists is a way to OR lists together */
					StringTokenizer aTok = new StringTokenizer(aString, "(,)", false);
					title = aTok.nextToken();
				}

				if ("Language".equals(title))
				{
					bSkill = bSkill.substring(0, iOffs + 1) + newRank;
					rankList.set(idx, bSkill);

					//XXX TODO This must be refactored. Core cannot call gui.
					if (!pcgen.gui.Utility.chooseSpokenLanguage(aPC, getName()))
					{
						newRank = curRank;
					}
					else
					{
						final int selectedLanguages = getAssociatedCount();
						final int maxLanguages = getTotalRank().intValue();

						if (selectedLanguages > maxLanguages)
						{
							newRank = curRank;
						}
					}
					g = newRank - curRank;
				}
				else
				{
					final ChooserInterface c = ChooserFactory.getChooserInstance();
					c.setPool((int) (g + curRank + rankAdjustment) - getAssociatedCount());
					c.setPoolFlag(false);
					c.setAvailableList(aArrayList);
					final ArrayList s = new ArrayList();
					addAssociatedTo(s);
					c.setSelectedList(s);
					c.setTitle(title);
					c.show();

					final int selectedListSize = c.getSelectedList().size();
					newRank = selectedListSize - rankAdjustment;
					g = newRank - getRank().doubleValue(); // change in ranks
				}
			}
		}
		//
		// Modify for the chosen class
		//
		if (newRank == 0.0)
		{
			rankList.remove(idx);
		}
		else
		{
			bSkill = bSkill.substring(0, iOffs + 1) + newRank;
			rankList.set(idx, bSkill);
		}
		return g;
	}

	public Integer costForPCClass(PCClass pc)
	{
		Integer anInt;
		if (!passesPreReqTests())
		{
			return new Integer(SettingsHandler.getExcSkillCost()); // treat cost of unqualified skills as exclusive
		}

		if (isClassSkill(pc))
		{
			anInt = new Integer(1);
		}
		else if (!isCrossClassSkill(pc) && isExclusive)
		{
			anInt = new Integer(SettingsHandler.getExcSkillCost());
		}
		else
		{
			anInt = new Integer(SettingsHandler.getIntCrossClassSkillCost()); // assume cross-class
		}

		return anInt;
	}

	/**
	 * return of 0 means exclusive, 1=class-skill, 2=cross-class skill
	 */
	Integer costForPCClassList(ArrayList aPCClassList)
	{
		int anInt = 0; // assume exclusive (can't buy)
		final int classListSize = aPCClassList.size();
		if (classListSize == 0)
		{
			return new Integer(anInt);
		}
		for (Iterator i = aPCClassList.iterator(); i.hasNext();)
		{
			final PCClass aClass = (PCClass) i.next();
			final int cInt = costForPCClass(aClass).intValue();
			if (cInt == 1)
			{
				return new Integer(cInt);
			}
			if (cInt != anInt)
			{
				anInt = cInt; // found a cross-class
			}
		}
		return new Integer(anInt);
	}

	public ArrayList getSynergyList()
	{
		if (synergyList == null)
		{
			synergyList = new ArrayList();
		}
		return synergyList;
	}

	public void addSynergyList(String aString)
	{
		getSynergyList().add(aString);
	}

	public Object clone()
	{
		Skill newSkill = null;
		try
		{
			newSkill = (Skill) super.clone();
			newSkill.required = required;
			newSkill.setRootName(rootName);
			newSkill.setKeyStat(this.getKeyStat());
			newSkill.setIsExclusive(this.isExclusive());
			newSkill.rankList = (ArrayList) rankList.clone();
			newSkill.setUntrained(this.getUntrained());
			newSkill.classList = (ArrayList) classList.clone();
			if (choiceList != null)
			{
				newSkill.choiceList = (ArrayList) choiceList.clone();
			}
			newSkill.isSpecified = isSpecified;
			newSkill.aCheck = aCheck;
			if (synergyList != null)
			{
				newSkill.setSynergyList((ArrayList) getSynergyList().clone());
			}
			newSkill.outputIndex = outputIndex;
		}
		catch (CloneNotSupportedException exc)
		{
			GuiFacade.showMessageDialog(null, exc.getMessage(), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			return newSkill;
		}
	}

	public ArrayList getChoiceList()
	{
		return choiceList;
	}

	public void addChoiceList(String aString)
	{
		if (".CLEAR".equals(aString))
		{
			choiceList = null;
		}
		else
		{
			if (choiceList == null)
			{
				choiceList = new ArrayList();
			}
			choiceList.add(aString);
		}
	}

	public void setACheck(String aString)
	{
		if (aString.length() != 0)
		{
			switch (aString.charAt(0))
			{
				case 'N':
					aCheck = ACHECK_NONE;
					break;
				case 'Y':
					aCheck = ACHECK_YES;
					break;
				case 'P':
					aCheck = ACHECK_NONPROF;
					break;
				case 'W':
					aCheck = ACHECK_WEIGHT;
					break;
				default:
					break;
			}
		}
	}

	public void setACheck(int argACheck)
	{
		aCheck = argACheck;
	}

	public int getACheck()
	{
		return aCheck;
	}

	private void setSynergyList(ArrayList synergyList)
	{
		this.synergyList = synergyList;
	}

	String getPCCText()
	{
		final StringBuffer txt = new StringBuffer(200);
		txt.append(getName());
		if (keyStat.length() != 0)
		{
			txt.append("\tKEYSTAT:").append(keyStat);
		}
		if (isExclusive)
		{
			txt.append("\tEXCLUSIVE:YES");
		}

		if (!isUntrained())
		{
			txt.append("\tUSEUNTRAINED:NO");
		}
		for (Iterator e = getSynergyList().iterator(); e.hasNext();)
		{
			txt.append("\tSYNERGY:").append((String) e.next());
		}

		if (aCheck != ACHECK_NONE)
		{
			txt.append("\tACHECK:");
			switch (aCheck)
			{
				case ACHECK_YES:		// Yes
					txt.append("YES");
					break;

				case ACHECK_NONPROF:	// Only if not proficient
					txt.append("PROFICIENT");
					break;

				case ACHECK_WEIGHT:	// -1 per 5 lbs carried or equipped
					txt.append("WEIGHT");
					break;

				default:
					txt.append("ERROR");
					break;
			}
		}
		for (Iterator e = choiceList.iterator(); e.hasNext();)
		{
			txt.append("\tCHOOSE:").append((String) e.next());
		}
		txt.append(super.getPCCText(false));
		return txt.toString();
	}

}
