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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import pcgen.gui.Chooser;

/**
 * <code>Skill</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class Skill extends PObject
{
	private String keyStat = new String();

	private String rootName = new String();
	private ArrayList classList = new ArrayList();
	private String isExclusive = "Y";
	private String untrained = "Y";
	private ArrayList rankList = new ArrayList();
	private ArrayList synergyList = new ArrayList();
	private ArrayList choiceList = new ArrayList();
	private ArrayList associatedList = new ArrayList();
	private ArrayList featList = new ArrayList();
	private int aCheck = 0; // 1=Yes, 2=Only if not proficient
	private boolean required = false;

	public ArrayList getAssociatedList()
	{
		return associatedList;
	}

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

	/*** added 8/13/01 by TNC ***/
	private String type = new String();

	public String getType()
	{
		return type;
	}

	/****************************/

	public String toString()
	{
		return name;
	}

	public String qualifiedName()
	{
		if (associatedList.size() == 0)
			return name;
		StringBuffer aString = new StringBuffer(name).append("(");
		for (int i = 0; i < associatedList.size(); i++)
		{
			if (i > 0)
				aString.append(", ");
			aString.append(associatedList.get(i));
		}
		return aString + ")";
	}

	private void setRootName(String aString)
	{
		rootName = aString;
	}

	public String keyStat()
	{
		return keyStat;
	}

	private void setKeyStat(String aString)
	{
		keyStat = aString;
	}

	public Integer modifier()
	{
		int stat = statIndex(keyStat);
		int bonus = 0;
		Integer anInt = new Integer(0);
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null)
			return anInt;

		if (stat >= 0)
		{
			bonus = aPC.calcStatMod(stat);
			bonus += aPC.getTotalBonusTo("SKILL", "STAT=" + keyStat, false);
		}
		bonus += aPC.getTotalBonusTo("SKILL", name, true);
		bonus += aPC.getTotalBonusTo("SKILL", "LIST", false);
		bonus += aPC.getRace().bonusForSkill(this.getName());
		bonus += bonusTo("SKILL", name);
		if (synergyList().size() > 0)
		{
			for (Iterator e = synergyList().iterator(); e.hasNext();)
			{
				boolean flag = true;
				String aString = (String)e.next();
				StringTokenizer aTok = new StringTokenizer(aString, "=", false);
				String aList = aTok.nextToken();
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
		if (aCheck != 0)
		{
			int minBonus = 0;
			int maxBonus = 0;
			switch (Globals.loadTypeForStrength(aPC.adjStats(Globals.STRENGTH), aPC.totalWeight()))
			{
				case 1: // medium load
					minBonus = -3;
					break;
				case 2: // heavy load
				case 3: // overload
					minBonus = -6;
					break;
			}
			for (Iterator e = aPC.getEquipmentOfType("Armor", 1).iterator(); e.hasNext();)
			{
				Equipment eq = (Equipment)e.next();
				if (aCheck == 1 || !aPC.isProficientWith(eq))
					maxBonus += eq.acCheck().intValue();
			}
			for (Iterator e = aPC.getEquipmentOfType("Shield", 1).iterator(); e.hasNext();)
			{
				Equipment eq = (Equipment)e.next();
				if (aCheck == 1 || !aPC.isProficientWith(eq))
					maxBonus += eq.acCheck().intValue();
			}
			bonus += Math.min(maxBonus, minBonus);
		}
		anInt = new Integer(bonus);
		return anInt;
	}

	private void addToClassList(String className)
	{
		if (!classList.contains(className))
			classList.add(className);
	}

	public boolean isClassSkill(PCClass pc)
	{
		int i;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC.getRace().getCSkillList().contains(name))
			return true;
		if (pc == null)
			return false;
		if (classList.contains(pc.getName()) || classList.contains(pc.getSubClassName()))
			return true;
		for (Iterator e = aPC.getDomainList().iterator(); e.hasNext();)
		{
			Domain aDomain = (Domain)e.next();
			if (aDomain.hasSkill(getName()))
				return true;
		}
		if (aPC != null)
		{
			for (Iterator e = aPC.aggregateFeatList().iterator(); e.hasNext();)
			{
				Feat aFeat = (Feat)e.next();
				if (aFeat.hasCSkill(getName()) || aFeat.getCSkillList().contains("ALL"))
					return true;
			}

			for (Iterator e = aPC.getTemplateList().iterator(); e.hasNext();)
			{
				PCTemplate aTemplate = (PCTemplate)e.next();
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
				Feat aFeat = (Feat)e.next();
				if (aFeat.hasCCSkill(getName()) || aFeat.getCcSkillList().contains("ALL"))
					return true;
			}

			for (Iterator e = aPC.getTemplateList().iterator(); e.hasNext();)
			{
				PCTemplate aTemplate = (PCTemplate)e.next();
				if (aTemplate.hasCCSkill(getName()) || aTemplate.getCcSkillList().contains("ALL"))
					return true;
			}
		}
		return false;
	}

	public boolean isClassSkill(ArrayList aList)
	{
		int j;
		for (Iterator e = aList.iterator(); e.hasNext();)
		{
			if (isClassSkill((PCClass)e.next()))
				return true;
		}
		return false;
	}

	public String isExclusive()
	{
		return isExclusive;
	}

	private void setIsExclusive(String aString)
	{
		isExclusive = aString;
	}

	public String untrained()
	{
		return untrained;
	}

	private void setUntrained(String aString)
	{
		untrained = aString;
	}

	public int statIndex(String aString)
	{
		int stat = -1;
		if (aString.equals("STR"))
			stat = Globals.STRENGTH;
		else if (aString.equals("DEX"))
			stat = Globals.DEXTERITY;
		else if (aString.equals("CON"))
			stat = Globals.CONSTITUTION;
		else if (aString.equals("INT"))
			stat = Globals.INTELLIGENCE;
		else if (aString.equals("WIS"))
			stat = Globals.WISDOM;
		else if (aString.equals("CHA"))
			stat = Globals.CHARISMA;
		return stat;
	}

	// returns ranks taken specifically in skill
	public Float getRank()
	{
		double rank = 0.0;
		for (int i = 0; i < rankList.size(); i++)
		{
			String bSkill = (String)rankList.get(i);
			int iOffs = bSkill.indexOf(':');
			//
			// Ignore -1 return code (as -1 + 1 = 0 and that's the start of the string)
			//
			Float aFloat = new Float(bSkill.substring(iOffs+1));
			rank += aFloat.doubleValue();
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
		String bSkill = new String("");
		int idx;
		//
		// Find the skill and class in question
		//
		for (idx = 0; idx < rankList.size(); idx++)
		{
			bSkill = (String)rankList.get(idx);
			if (bSkill.startsWith(aCName + ":"))
			{
				break;
			}
		}
		if (idx >= rankList.size())
		{
			return;
		}
		Float curRankCost = new Float(bSkill.substring(aCName.length()+1));

		String aResp = modRanks(-curRankCost.doubleValue(),aClass);
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
		if (!ignorePrereqs)
		{
			if (aClass == null)
				return "You must be at least level one before you can purchase skills.";

			i = costForPCClass(aClass).intValue();
			if (i == 0)
				return "You cannot purchase this exclusive skill.";

			if (rankMod > 0.0 && aClass.getSkillPool().floatValue() < 1.0 / i)
				return "You do not have enough skill points.";

			final Float maxRank = Globals.getCurrentPC().getMaxRank(getName(), aClass);
			if (!Globals.isBoolBypassMaxSkillRank() && (rankMod > 0.0))
			{
				final Float ttlRank = getTotalRank();
				if (ttlRank.doubleValue() >= maxRank.doubleValue())
					return "Skill rank at maximum (" + maxRank.toString() + ") for your level.";
				if ((ttlRank.doubleValue() + rankMod) > maxRank.doubleValue())
					return "Raising skill would make it above maximum (" + maxRank.toString() + ") for your level.";
			}
		}

		if ((getRank().doubleValue()+ rankMod) < 0.0)
			return "Cannot lower rank below 0";


		String aCName = "None";
		if (aClass != null)
			aCName = aClass.getName();

		String bSkill = new String("");
		int idx;
		//
		// Find the skill and class in question
		//
		for (idx = 0; idx < rankList.size(); idx++)
		{
			bSkill = (String)rankList.get(idx);
			if (bSkill.startsWith(aCName + ":"))
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
		Float curRank = new Float(bSkill.substring(iOffs+1));
		if (curRank.doubleValue() == 0.0 && rankMod < 0.0)
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
			Globals.getCurrentPC().setSkillPoints(Globals.getCurrentPC().getSkillPoints() - (int)(i * rankMod));
		}
		return "";
	}


	private double modRanks2(double g,int idx,String bSkill)
	{
		final int iOffs = bSkill.indexOf(':');
		Float curRank = new Float(bSkill.substring(iOffs+1));
		Float newRank = new Float(curRank.doubleValue() + g);
		if (choiceList().size() > 0 && g != 0 && curRank.intValue() != (int)(curRank.doubleValue() + g))
		{
			ArrayList aArrayList = new ArrayList();
			String title = new String();
			final PlayerCharacter aPC = Globals.getCurrentPC();
			for (Iterator e = choiceList().iterator(); e.hasNext();)
			{
				String aString = (String)e.next();
				StringTokenizer aTok = new StringTokenizer(aString, "(,)", false);
				title = aTok.nextToken();
				if (title.equals("Language"))
				{
					while (aTok.hasMoreTokens())
					{
						aString = aTok.nextToken();
						if ((Globals.getLanguageSet().contains(aString) || Globals.getLanguageSet().size() == 0) && !aPC.getLanguagesList().contains(aString))
						{
							aArrayList.add(aString);
						}
					}
				}
			}

			Chooser c = new Chooser();
			c.setPool((int)(g + curRank.doubleValue()) - associatedList.size());
			c.setPoolFlag(false);
			c.setAvailableList(aArrayList);
			c.setSelectedList((ArrayList)associatedList.clone());
			c.setTitle(title);
			c.show();

			double h = c.getSelectedList().size() - associatedList.size();
			newRank = new Float((double)c.getSelectedList().size());
			g = newRank.doubleValue() - getRank().doubleValue(); // change in ranks
			if (title.equals("Language"))
			{
				for (int i = 0; i < c.getSelectedList().size(); i++)
				{
					String aString = c.getSelectedList().get(i).toString();
					if (!associatedList.contains(aString))
					{
						aPC.addLanguage(aString, false);
						associatedList.add(aString);
					}
				}
				for (int i = 0; i < associatedList.size(); i++)
				{
					String aString = associatedList.get(i).toString();
					if (!c.getSelectedList().contains(aString))
					{
						aPC.getLanguagesList().remove(aString);
						associatedList.remove(aString);
					}
				}
			}
		}
		//
		// Modify for the chosen class
		//
		if (newRank.floatValue() == 0.0)
		{
			rankList.remove(idx);
		}
		else
		{
			bSkill = bSkill.substring(0,iOffs+1) + newRank;
			rankList.set(idx,bSkill);
		}
		return g;
	}


	public Integer costForPCClass(PCClass pc)
	{
		if (featList().size() > 0)
		{
			final PlayerCharacter aPC = Globals.getCurrentPC();
			for (Iterator e = featList().iterator(); e.hasNext();)
			{
				final String featName = (String)e.next();
				if (!aPC.hasFeat(featName) && !aPC.hasFeatAutomatic(featName))
					return new Integer(Globals.getExcSkillCost());  // treat cost of unqualified skills as exclusive
			}
		}
		if (!passesPreReqTests())
			return new Integer(Globals.getExcSkillCost()); // treat cost of unqualified skills as exclusive
		Integer anInt = new Integer(Globals.getIntCrossClassSkillCost()); // assume cross-class
		if (isClassSkill(pc))
			anInt = new Integer(1);
		else if (!isCrossClassSkill(pc) && isExclusive.equals("Y"))
			anInt = new Integer(Globals.getExcSkillCost());
		return anInt;
	}

	public Integer costForPCClassList(ArrayList aPCClassList)
	{
		Integer anInt = new Integer(0); // assume exclusive (can't buy)
		final int classListSize = aPCClassList.size();
		if (classListSize == 0)
			return anInt;
		Integer cInt = null;
		for (int i = 0; i < classListSize; i++)
		{
			cInt = costForPCClass((PCClass)aPCClassList.get(i));
			if (cInt.intValue() == 1)
				return cInt;
			if (!cInt.equals(anInt))
				anInt = cInt; // found a cross-class
		}
		return anInt;
	}

	public ArrayList synergyList()
	{
		return synergyList;
	}

	private void addSynergyList(String aString)
	{
		synergyList().add(aString);
	}

	public Object clone()
	{
		Skill newSkill = (Skill)super.clone();
		newSkill.required = required;
		newSkill.setRootName(rootName);
		newSkill.setKeyStat(this.keyStat());
		newSkill.setIsExclusive(this.isExclusive());
		newSkill.rankList = (ArrayList)rankList.clone();
		newSkill.setUntrained(this.untrained());
		newSkill.classList = (ArrayList)classList.clone();
		newSkill.choiceList = (ArrayList)choiceList().clone();
		newSkill.associatedList = (ArrayList)associatedList.clone();
		newSkill.isSpecified = isSpecified;
		newSkill.featList = (ArrayList)featList.clone();
		newSkill.aCheck = aCheck;
		newSkill.synergyList = (ArrayList)synergyList().clone();
		newSkill.type = getType();
		return newSkill;
	}

	public ArrayList choiceList()
	{
		return choiceList;
	}

	private void addChoiceList(String aString)
	{
		choiceList().add(aString);
	}

	public ArrayList featList()
	{
		return featList;
	}

	private void setFeatList(String aString)
	{
		StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
			featList.add(aTok.nextToken());
	}

	private void setACheck(String aString)
	{
		if (aString.startsWith("Y"))
			aCheck = 1;
		if (aString.startsWith("P"))
			aCheck = 2;
	}

	/*** added 08/13/01 by TNC ***/
	private void setType(String aString)
	{
		if (aString.lastIndexOf(":") > -1)
		{
			type = aString.substring(aString.lastIndexOf(":"));
			if (Globals.isDebugMode())
				System.out.println("Type=" + type);
		}
		else
		{
			type = aString;
			if (Globals.isDebugMode())
				System.out.println("Type=" + type);
		}
	}

	public ArrayList typeList()
	{
		ArrayList aArrayList = new ArrayList();
		StringTokenizer aTok = new StringTokenizer(type, ".", false);
		while (aTok.hasMoreTokens())
			aArrayList.add(aTok.nextToken());
		return aArrayList;
	}

	/*****************************/

	public boolean hasType(String aString)
	{
		if (type.length() < 1)
			return false;
		for (StringTokenizer aTok = new StringTokenizer(type, ".", false); aTok.hasMoreTokens();)
			if (aString.equalsIgnoreCase(aTok.nextToken()))
				return true;
		return false;
	}

	public void parseLine(String inputLine, File sourceFile, int lineNum)
	{
		StringTokenizer colToken = new StringTokenizer(inputLine, "\t", false);
		String colString = null;
		int colMax = colToken.countTokens();
		int col = 0;
		Integer anInt = new Integer(0);
		if (colMax == 0)
			return;
		for (col = 0; col < colMax; col++)
		{
			colString = colToken.nextToken();
			if (super.parseTag(colString))
				continue;
			switch (col)
			{
				case 0:
					setName(colString);
					break;
				case 1:
					setKeyStat(colString);
					break;
				case 2:
					StringTokenizer fieldToken = new StringTokenizer(colString, ",", false);
					while (fieldToken.hasMoreTokens())
						addToClassList(fieldToken.nextToken());
					break;
				case 3:
					setIsExclusive(colString);
					break;
				case 4:
					setUntrained(colString);
					break;
			}
			if (colString.startsWith("SYNERGY"))
				addSynergyList(colString.substring(8));
			else if (colString.startsWith("CHOOSE"))
				addChoiceList(colString.substring(7));
			else if (colString.equals("REQ"))
				required = true;

			/**** Obsoleted by PRExxx tag search
			 *			else if (colString.startsWith("PREFEAT"))
			 *				setFeatList(colString.substring(8));
			 ****/

			else if (colString.startsWith("ACHECK"))
				setACheck(colString.substring(7));

			/*** added 8/13/01 by TNC ***/
			else if (colString.startsWith("TYPE"))
				setType(colString.substring(5));
			/****************************/

			else if (colString.startsWith("ROOT"))
				setRootName(colString.substring(5));
			else if (colString.startsWith("DEFINE"))
				variableList.add("0|" + colString.substring(7));
			else if (colString.startsWith("KEY:"))
				setKeyName(colString.substring(4));
			else if (colString.startsWith("PRE"))
				preReqArrayList.add(colString);
			else if (colString.startsWith("QUALIFY:"))
				addToQualifyListing(colString.substring(8));
			else if (col > 4)
				JOptionPane.showMessageDialog
					(null, "Illegal skill info " + sourceFile.getName() +
					":" + Integer.toString(lineNum) + " \"" + colString + "\"", "PCGen", JOptionPane.ERROR_MESSAGE);
		}
	}

	public Skill()
	{
	}
}
