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
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import pcgen.gui.Chooser;

/**
 * <code>Skill</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class Skill extends PObject
{
	private String keyStat = "";

	private String rootName = "";
	private ArrayList classList = new ArrayList();
	private String isExclusive = "N";
	private String untrained = "Y";
	private ArrayList rankList = new ArrayList();
	private ArrayList synergyList = new ArrayList();
	private ArrayList choiceList = new ArrayList();
	private ArrayList featList = new ArrayList();

	//constants for Cost Type String
	public static final String COST_CLASS = "CLASS";
	public static final String COST_XCLASS = "CROSS-CLASS";
	public static final String COST_EXCL = "EXCLUSIVE";
	public static final String COST_UNK = "UNKNOWN";

	private static final int ACHECK_NONE = 0;	// No
	private static final int ACHECK_YES = 1;	// Yes
	private static final int ACHECK_NONPROF = 2;	// Only if not proficient
	private static final int ACHECK_WEIGHT = 3;	// -1 per 5 lbs carried or equipped
	private int aCheck = ACHECK_NONE;

	private boolean required = false;

	public ArrayList getClassList()
	{
		return classList;
	}

	public String getRootName()
	{
		return rootName;
	}

	public void setRequired(boolean required)
	{
		this.required = required;
	}

	public boolean isRequired()
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
			return name;
		StringBuffer buffer = new StringBuffer(getAssociatedCount() * 20);
		buffer.append(name).append("(");
		for (int i = 0; i < getAssociatedCount(); i++)
		{
			if (i > 0)
				buffer.append(", ");
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

	public Integer modifier()
	{
		int stat = Globals.getStatFromAbbrev(keyStat);
		int bonus = 0;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null)
			return new Integer(0);

		if (stat >= 0)
		{
			bonus = aPC.calcStatMod(stat);
			bonus += aPC.getTotalBonusTo("SKILL", "STAT=" + keyStat, false);
		}
		bonus += aPC.getTotalBonusTo("SKILL", name, true);
		//loop through all current skill types checking for boni
		for (int typesForBonus=0; typesForBonus<getMyTypeCount(); typesForBonus++)
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
			for (int typesForBonus=0; typesForBonus<getMyTypeCount(); typesForBonus++)
			{
				String singleType = getMyType(typesForBonus);
				bonus += aPC.getTotalBonusTo("CSKILL", "TYPE=" + singleType, true);
			}
			bonus += aPC.getTotalBonusTo("CSKILL", "LIST", false);
		}
		//if (isCrossClassSkill(aPC.getClassList())) //this doesn't work, isCrossClassSkill is broken
		if (!isClassSkill(aPC.getClassList()) && !isExclusive().startsWith("Y"))
		{
			bonus += aPC.getTotalBonusTo("CCSKILL", name, true);
//loop through all current skill types checking for boni
			for (int typesForBonus=0; typesForBonus<getMyTypeCount(); typesForBonus++)
			{
				String singleType = getMyType(typesForBonus);
				bonus += aPC.getTotalBonusTo("CCSKILL", "TYPE=" + singleType, true);
			}
			bonus += aPC.getTotalBonusTo("CCSKILL", "LIST", false);
		}
		//the above two if-blocks try to get BONUS:[C]CSKILL|TYPE=xxx|y to function
		bonus += aPC.getRace().bonusForSkill(this.getName());
		bonus += bonusTo("SKILL", name);
		if (getSynergyList().size() > 0)
		{
			for (Iterator e = getSynergyList().iterator(); e.hasNext();)
			{
				boolean flag = true;
				String aString = (String)e.next();
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
					bonus += aBonus;
			}
		}
		if (aCheck != ACHECK_NONE)
		{
			int minBonus = 0;
			int maxBonus = 0;
			final Float totalWeight = aPC.totalWeight();
			if (aCheck == ACHECK_WEIGHT && Globals.isApplyWeightPenaltyToSkills())
			{
				maxBonus = -(int)(totalWeight.doubleValue() / 5.0);
			}
			else if (aCheck == ACHECK_WEIGHT)
			{
				//Do nothing. This is to simulate taking everything off before going swimming. Freq #505977
			}
			else
			{
				if (aCheck != ACHECK_NONPROF && Globals.isApplyLoadPenaltyToACandSkills())
				{
					switch (Globals.loadTypeForStrength(aPC.adjStats(Constants.STRENGTH), totalWeight))
					{
						case Constants.MEDIUM_LOAD:
							minBonus = -3;
							break;
						case Constants.HEAVY_LOAD:
						case Constants.OVER_LOAD:
							minBonus = -6;
							break;
					}
				}
				for (Iterator e = aPC.getEquipmentOfType("Armor", 1).iterator(); e.hasNext();)
				{
					final Equipment eq = (Equipment)e.next();
					if ((aCheck == ACHECK_YES) || ((aCheck == ACHECK_NONPROF) && !aPC.isProficientWith(eq)))
					{
						maxBonus += eq.acCheck().intValue();
					}
				}
				for (Iterator e = aPC.getEquipmentOfType("Shield", 1).iterator(); e.hasNext();)
				{
					final Equipment eq = (Equipment)e.next();
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

	public boolean isClassSkill(PCClass pc)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC.getRace().getCSkillList().contains(name))
			return true;
		if (pc == null)
			return false;
		if (classList.contains(pc.getName()) || classList.contains(pc.getSubClassName()))
			return true;
		for (Iterator e = aPC.getCharacterDomainList().iterator(); e.hasNext();)
		{
			final CharacterDomain aCD = (CharacterDomain)e.next();
			if (aCD.getDomain() != null && aCD.getDomainSource().startsWith("PCClass|" + pc.getName()) && aCD.getDomain().hasSkill(name))
				return true;
		}
		if (aPC != null)
		{
			for (Iterator e = aPC.aggregateFeatList().iterator(); e.hasNext();)
			{
				final Feat aFeat = (Feat)e.next();
				if (aFeat.hasCSkill(getName()) || aFeat.getCSkillList().contains("ALL"))
					return true;
			}

			for (Iterator e = aPC.getTemplateList().iterator(); e.hasNext();)
			{
				final PCTemplate aTemplate = (PCTemplate)e.next();
				if (aTemplate.hasCSkill(getName()) || aTemplate.getCSkillList().contains("ALL"))
					return true;
			}
		}
		return pc.skillList().contains(getName());
	}

	public boolean isCrossClassSkill(PCClass pc)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC.getRace().getCcSkillList().contains(name))
			return true;
		if (aPC != null)
		{
			for (Iterator e = aPC.aggregateFeatList().iterator(); e.hasNext();)
			{
				final Feat aFeat = (Feat)e.next();
				if (aFeat.hasCCSkill(getName()) || aFeat.getCcSkillList().contains("ALL"))
					return true;
			}

			for (Iterator e = aPC.getTemplateList().iterator(); e.hasNext();)
			{
				final PCTemplate aTemplate = (PCTemplate)e.next();
				if (aTemplate.hasCCSkill(getName()) || aTemplate.getCcSkillList().contains("ALL"))
					return true;
			}
		}
		return false;
	}

	public boolean isClassSkill(ArrayList aList)
	{
		for (Iterator e = aList.iterator(); e.hasNext();)
		{
			if (isClassSkill((PCClass)e.next()))
				return true;
		}
		return false;
	}

	public boolean isCrossClassSkill(ArrayList aList)
	{
		for (Iterator e = aList.iterator(); e.hasNext();)
		{
			if (isCrossClassSkill((PCClass)e.next()))
				return true;
		}
		return false;
	}

	public String isExclusive()
	{
		return isExclusive;
	}

	public void setIsExclusive(String aString)
	{
		isExclusive = aString;
	}

	public String getSkillCostType(PCClass pc)
	{
		// isCrossClassSkill() doesn't appear to work, so just go by actual cost values
		if (costForPCClass(pc).intValue() == 1)
			return COST_CLASS;
		else if (costForPCClass(pc).intValue() == 2)
			return COST_XCLASS;
		else if (isExclusive.startsWith("Y"))
			return COST_EXCL;
		return COST_UNK;
	}

	public String getUntrained()
	{
		return untrained;
	}

	public void setUntrained(String aString)
	{
		untrained = aString;
	}

	/**
	 * @deprecated use Globals.getStatFromAbbrev(attribute)
	 */
	public int statIndex(String aString)
	{
		return Globals.getStatFromAbbrev(aString);
	}


	public void replaceClassRank(String oldClass, String newClass)
	{
		final String oldCLassString = oldClass + ":";
		for (int i = 0; i < rankList.size(); i++)
		{
			final String bSkill = (String)rankList.get(i);
			if (bSkill.startsWith(oldCLassString))
			{
				rankList.set(i, newClass + bSkill.substring(oldClass.length()));
			}
		}
	}

	// returns ranks taken specifically in skill
	public Float getRank()
	{
		double rank = 0.0;
		for (int i = 0; i < rankList.size(); i++)
		{
			final String bSkill = (String)rankList.get(i);
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

	// rank + bonus ranks (racial, class, etc. bonuses)
	public Float getTotalRank()
	{
		return new Float(getRank().doubleValue() + getRankAdj().doubleValue());
	}

	// returns the total adjustments to rank
	public Float getRankAdj()
	{
		return new Float(Globals.getCurrentPC().getTotalBonusTo("SKILLRANK", getName(), true));
	}

	// Set the ranks for the specified class to zero
	public void setZeroRanks(PCClass aClass)
	{
		if (aClass == null)
			return;

		final String aCName = aClass.getName();
		String bSkill = "";
		int idx;
		//
		// Find the skill and class in question
		//
		final String aCNameString = aCName + ":";
		for (idx = 0; idx < rankList.size(); idx++)
		{
			bSkill = (String)rankList.get(idx);
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
			System.out.println(aResp);
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
				return "You must be at least level one before you can purchase skills.";

			i = costForPCClass(aClass).intValue();
			if (i == 0)
				return "You cannot purchase this exclusive skill.";

			if (rankMod > 0.0 && aClass.getSkillPool().floatValue() < rankMod * i)
				return "You do not have enough skill points.";

			final double maxRank = aPC.getMaxRank(getName(), aClass).doubleValue();
			if (!Globals.isBoolBypassMaxSkillRank() && (rankMod > 0.0))
			{
				final double ttlRank = getTotalRank().doubleValue();
				if (ttlRank >= maxRank)
					return "Skill rank at maximum (" + maxRank + ") for your level.";
				if ((ttlRank + rankMod) > maxRank)
					return "Raising skill would make it above maximum (" + maxRank + ") for your level.";
			}
		}

		if ((getRank().doubleValue() + rankMod) < 0.0)
			return "Cannot lower rank below 0";


		String aCName = "None";
		if (aClass != null)
			aCName = aClass.getName();

		String bSkill = "";
		int idx;
		//
		// Find the skill and class in question
		//
		final String aCNameString = aCName + ":";
		for (idx = 0; idx < rankList.size(); idx++)
		{
			bSkill = (String)rankList.get(idx);
			if (bSkill.startsWith(aCNameString))
				break;
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
					bSkill = (String)rankList.get(idx);
					if (bSkill.startsWith("None:"))
						break;
				}
			}
			if (idx >= rankList.size())
				bSkill = aCName + ":0";
		}

		final int iOffs = bSkill.indexOf(':');
		double curRank = Double.parseDouble(bSkill.substring(iOffs + 1));
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
				aClass.setSkillPool(new Integer(aClass.getSkillPool().intValue() - (int)(i * rankMod)));
			aPC.setSkillPoints(aPC.getSkillPoints() - (int)(i * rankMod));
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
			if (getChoiceList().size() > 0 && (g != 0) && (curRank != (int)(newRank)))
			{
				ArrayList aArrayList = new ArrayList();
				String title = new String();
				double rankAdjustment = 0.0;
				for (Iterator e = getChoiceList().iterator(); e.hasNext();)
				{
					String aString = (String)e.next();
					/** putting commas between lists is a way to OR lists together */
					StringTokenizer aTok = new StringTokenizer(aString, "(,)", false);
					title = aTok.nextToken();
/*					if (title.equals("Language"))
					{
						//
						// Need to take into account total ranks for all classes
						//
						rankAdjustment = getRank().doubleValue() - curRank;
						while (aTok.hasMoreTokens())
						{
							aString = aTok.nextToken();
							/** putting . between TYPE requirements is a way to AND them.
							 *   e.g. Written.MyType would create a list of all the Global langauges
							 *   that had both Written and MyType in the TYPE: string.
							 */
/*							final StringTokenizer bTok = new StringTokenizer(aString, ".", false);
							int col = 0;
							ArrayList aSet = new ArrayList();
							/** if first token (col==0) is "PC" then our base list is from the PC's own list of Languages.
							 *   otherwise the base list comes from the Global list of Langauges. */
/*							while (bTok.hasMoreTokens())
							{
								String bString = bTok.nextToken();
								if (col == 0)
								{
									if (bString.equalsIgnoreCase("PC") && bTok.hasMoreTokens())
										aSet.addAll(aPC.getLanguagesList());
									else
										aSet = (ArrayList)Globals.getLanguageList();
								}
								if (col > 0 || !bString.equalsIgnoreCase("PC"))
									aSet = Globals.getLanguagesFromListOfType(aSet, bString);
								col++;
							}
							for (Iterator li = aSet.iterator(); li.hasNext();)
							{
								Object anObj = li.next();
								if (!aArrayList.contains(anObj.toString()))
									aArrayList.add(anObj.toString());
							}
						}
					}
*/
				}


				if (title.equals("Language"))
				{
					bSkill = bSkill.substring(0, iOffs + 1) + newRank;
					rankList.set(idx, bSkill);

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
				}
				else
				{
					final Chooser c = new Chooser();
					c.setPool((int)(g + curRank + rankAdjustment) - getAssociatedCount());
					c.setPoolFlag(false);
					c.setAvailableList(aArrayList);
					ArrayList s=new ArrayList();
					addAssociatedTo(s);
					c.setSelectedList(s);
					c.setTitle(title);
					c.show();

					final int selectedListSize = c.getSelectedList().size();
					newRank = selectedListSize - rankAdjustment;
					g = newRank - getRank().doubleValue(); // change in ranks
/*					if (title.equals("Language"))
					{
						for (int i = 0; i < selectedListSize; i++)
						{
							final String aString = c.getSelectedList().get(i).toString();
							if (!containsAssociated(aString))
							{
								aPC.addLanguage(aString);
								addAssociated(aString);
							}
						}
						for (int i = 0; i < getAssociatedCount(); i++)
						{
							final String aString = getAssociated(i);
							if (!c.getSelectedList().contains(aString))
							{
								aPC.getLanguagesList().remove(aString);
								removeAssociated(aString);
							}
						}
					}
*/
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
		Integer anInt = null;
		if (getFeatList().size() > 0)
		{
			final PlayerCharacter aPC = Globals.getCurrentPC();
			for (Iterator e = getFeatList().iterator(); e.hasNext();)
			{
				final String featName = (String)e.next();
				if (!aPC.hasFeat(featName) && !aPC.hasFeatAutomatic(featName))
					return new Integer(Globals.getExcSkillCost());  // treat cost of unqualified skills as exclusive
			}
		}
		if (!passesPreReqTests())
			return new Integer(Globals.getExcSkillCost()); // treat cost of unqualified skills as exclusive

		if (isClassSkill(pc))
			anInt = new Integer(1);
		else if (!isCrossClassSkill(pc) && isExclusive.equals("Y"))
			anInt = new Integer(Globals.getExcSkillCost());
		else
			anInt = new Integer(Globals.getIntCrossClassSkillCost()); // assume cross-class

		return anInt;
	}

	public Integer costForPCClassList(ArrayList aPCClassList)
	{
		int anInt = 0; // assume exclusive (can't buy)
		final int classListSize = aPCClassList.size();
		if (classListSize == 0)
			return new Integer(anInt);
		for (int i = 0; i < classListSize; i++)
		{
			final int cInt = costForPCClass((PCClass)aPCClassList.get(i)).intValue();
			if (cInt == 1)
				return new Integer(cInt);
			if (cInt != anInt)
				anInt = cInt; // found a cross-class
		}
		return new Integer(anInt);
	}

	public ArrayList getSynergyList()
	{
		return synergyList;
	}

	public void addSynergyList(String aString)
	{
		getSynergyList().add(aString);
	}

	public Object clone()
	{
		Skill newSkill = (Skill)super.clone();
		newSkill.required = required;
		newSkill.setRootName(rootName);
		newSkill.setKeyStat(this.getKeyStat());
		newSkill.setIsExclusive(this.isExclusive());
		newSkill.rankList = (ArrayList)rankList.clone();
		newSkill.setUntrained(this.getUntrained());
		newSkill.classList = (ArrayList)classList.clone();
		newSkill.choiceList = (ArrayList)getChoiceList().clone();
		newSkill.isSpecified = isSpecified;
		newSkill.featList = (ArrayList)featList.clone();
		newSkill.aCheck = aCheck;
		newSkill.synergyList = (ArrayList)getSynergyList().clone();
		return newSkill;
	}

	public ArrayList getChoiceList()
	{
		return choiceList;
	}

	public void addChoiceList(String aString)
	{
		getChoiceList().add(aString);
	}

	public ArrayList getFeatList()
	{
		return featList;
	}

	public void setFeatList(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
			featList.add(aTok.nextToken());
	}

	public void setACheck(String aString)
	{
		if (aString.startsWith("N"))
			aCheck = ACHECK_NONE;
		else if (aString.startsWith("Y"))
			aCheck = ACHECK_YES;
		else if (aString.startsWith("P"))
			aCheck = ACHECK_NONPROF;
		else if (aString.startsWith("W"))
			aCheck = ACHECK_WEIGHT;
	}

	public Skill()
	{
	}

}
