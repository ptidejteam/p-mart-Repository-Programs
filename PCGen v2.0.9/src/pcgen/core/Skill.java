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
	String keyStat = new String();
	String rootName = new String();
	ArrayList classList = new ArrayList();
	String isExclusive = "Y";
	String untrained = "Y";
	Float rank = new Float(0);
	ArrayList synergyList = new ArrayList();
	ArrayList choiceList = new ArrayList();
	ArrayList associatedList = new ArrayList();
	ArrayList featList = new ArrayList();
	int aCheck = 0; // 1=Yes, 2=Only if not proficient
	boolean isRequired = false;

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
		if (Globals.currentPC == null || keyStat.equals("None"))
			return anInt;
		bonus = (Globals.currentPC.adjStats(stat) / 2) - 5;
		bonus += Globals.currentPC.getTotalBonusTo("SKILL", name, true);
		bonus += Globals.currentPC.getTotalBonusTo("SKILL", "LIST", false);
		bonus += Globals.currentPC.getTotalBonusTo("SKILL", "STAT=" + keyStat, false);
		bonus += Globals.currentPC.race().bonusForSkill(this.name());
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
				while (aTok.hasMoreTokens() && flag == true)
				{
					aString = aTok.nextToken();
					Skill aSkill = Globals.currentPC.getSkillNamed(aString);
					flag = ((aSkill != null) && (aSkill.rank().intValue() >= minRank));
				}
				if (flag == true)
					bonus += aBonus;
			}
		}
		if (aCheck != 0)
		{
			int minBonus = 0;
			int maxBonus = 0;
			switch (Globals.loadTypeForStrength(Globals.currentPC.adjStats(0), Globals.currentPC.totalWeight()))
			{
				case 1: // medium load
					minBonus = -3;
					break;
				case 2: // heavy load
				case 3: // overload
					minBonus = -6;
					break;
			}
			for (Iterator e = Globals.currentPC.getEquipmentOfType("Armor", 1).iterator(); e.hasNext();)
			{
				Equipment eq = (Equipment)e.next();
				if (aCheck == 1 || !Globals.currentPC.isProficientWith(eq))
					maxBonus += eq.acCheck().intValue();
			}
			for (Iterator e = Globals.currentPC.getEquipmentOfType("Shield", 1).iterator(); e.hasNext();)
			{
				Equipment eq = (Equipment)e.next();
				if (aCheck == 1 || !Globals.currentPC.isProficientWith(eq))
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
		if (Globals.currentPC.race().cSkillList.contains(name))
			return true;
		if (pc == null)
			return false;
		if (classList.contains(pc.name()) || classList.contains(pc.subClassName()))
			return true;
		for (Iterator e = Globals.currentPC.domainList.iterator(); e.hasNext();)
		{
			Domain aDomain = (Domain)e.next();
			if (aDomain.hasSkill(name()))
				return true;
		}
		if (Globals.currentPC != null)
		{
			for (Iterator e = Globals.currentPC.featList.iterator(); e.hasNext();)
			{
				Feat aFeat = (Feat)e.next();
				if (aFeat.hasCSkill(name()) || aFeat.cSkillList().contains("ALL"))
					return true;
			}
		}
		return pc.skillList().contains(name());
	}

	public boolean isCrossClassSkill(PCClass pc)
	{
		if (Globals.currentPC != null)
		{
			for (Iterator e = Globals.currentPC.featList.iterator(); e.hasNext();)
			{
				Feat aFeat = (Feat)e.next();
				if (aFeat.hasCCSkill(name()) || aFeat.ccSkillList().contains("ALL"))
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
			stat = 0;
		else if (aString.equals("DEX"))
			stat = 1;
		else if (aString.equals("CON"))
			stat = 2;
		else if (aString.equals("INT"))
			stat = 3;
		else if (aString.equals("WIS"))
			stat = 4;
		else if (aString.equals("CHA"))
			stat = 5;
		return stat;
	}

	public Float rank()
	{
		return rank;
	}

	public String modRanks(double rankMod, PCClass aClass)
	{
		if (aClass == null)
			return "You must be at least level one before you can purchase skills.";
		int i = costForPCClass(aClass).intValue();
		if (i == 0)
			return "You cannot purchase this exclusive skill.";

		if (rankMod > 0.0 && aClass.skillPool.floatValue() < 1.0 / i)
			return "You do not have enough skill points.";

		Float maxRank = Globals.currentPC.getMaxRank(name(), aClass);
		if (rank.doubleValue() >= maxRank.doubleValue() && rankMod >= 0.0)
			return "Skill rank at maximum (" + maxRank.toString() + ") for your level.";
		if (rank.doubleValue() == 0.0 && rankMod < 0.0)
			return "Cannot lower rank below 0";
		rankMod = modRanks(rankMod);
		aClass.skillPool = new Integer(aClass.skillPool.intValue() - (int)(i * rankMod));
		Globals.currentPC.setSkillPoints(Globals.currentPC.skillPoints() - (int)(i * rankMod));
		return "";
	}

	private double modRanks(double g)
	{
		Float newRank = new Float(rank.doubleValue() + g);
		if (choiceList().size() > 0 && g != 0 && rank.intValue() != (int)(rank.doubleValue() + g))
		{
			ArrayList aArrayList = new ArrayList();
			String title = new String();
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
						if ((Globals.languageList.contains(aString) || Globals.languageList.size() == 0) && !Globals.currentPC.languagesList().contains(aString))
						{
							aArrayList.add(aString);
						}
					}
				}
			}
			Chooser c = new Chooser();
			c.setPool((int)(g + rank.doubleValue()) - associatedList.size());
			c.setPoolFlag(false);
			c.setAvailableList(aArrayList);
			c.setSelectedList((ArrayList)associatedList.clone());
			c.setTitle(title);
			c.show();
			double h = c.getSelectedList().size() - associatedList.size();
			newRank = new Float((double)c.getSelectedList().size());
			g = newRank.doubleValue() - rank().doubleValue(); // change in ranks
			if (title.equals("Language"))
			{
				for (int i = 0; i < c.getSelectedList().size(); i++)
				{
					String aString = c.getSelectedList().get(i).toString();
					if (!associatedList.contains(aString))
					{
						Globals.currentPC.addLanguage(aString, false);
						associatedList.add(aString);
					}
				}
				for (int i = 0; i < associatedList.size(); i++)
				{
					String aString = associatedList.get(i).toString();
					if (!c.getSelectedList().contains(aString))
					{
						Globals.currentPC.languagesList().remove(aString);
						associatedList.remove(aString);
					}
				}
			}
		}
		setRank(newRank);
		return g;
	}

	public void addRank()
	{
		modRanks(1);
	}

	public void subRank()
	{
		modRanks(-1);
	}

	public void setRank(Float aRank)
	{
		rank = aRank;
		if (aRank.doubleValue() == 0.0 && isRequired == false)
			Globals.currentPC.skillList().remove(this);
	}

	public Integer costForPCClass(PCClass pc)
	{
		if (featList().size() > 0)
		{
			for (Iterator e = featList.iterator(); e.hasNext();)
				if (!Globals.currentPC.hasFeat((String)e.next()))
					return new Integer(Globals.excSkillCost);  // treat cost of unqualified skills as exclusive
		}
		if (!passesPreReqTests())
			return new Integer(Globals.excSkillCost); // treat cost of unqualified skills as exclusive
		Integer anInt = new Integer(Globals.intCrossClassSkillCost); // assume cross-class
		if (isClassSkill(pc))
			anInt = new Integer(1);
		else if (!isCrossClassSkill(pc) && isExclusive.equals("Y"))
			anInt = new Integer(Globals.excSkillCost);
		return anInt;
	}

	public Integer costForPCClassList(ArrayList aPCClassList)
	{
		Integer anInt = new Integer(0); // assume exclusive (can't buy)
		if (aPCClassList.size() == 0)
			return anInt;
		Integer cInt;
		int i;
		for (i = 0; i < aPCClassList.size(); i++)
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
		newSkill.isRequired = isRequired;
		newSkill.setRootName(rootName);
		newSkill.setKeyStat(this.keyStat());
		newSkill.setIsExclusive(this.isExclusive());
		newSkill.setRank(new Float(0));
		newSkill.setUntrained(this.untrained());
		newSkill.classList = (ArrayList)classList.clone();
		newSkill.choiceList = (ArrayList)choiceList().clone();
		newSkill.associatedList = (ArrayList)associatedList.clone();
		newSkill.isSpecified = isSpecified;
		newSkill.featList = (ArrayList)featList.clone();
		newSkill.aCheck = aCheck;
		newSkill.synergyList = (ArrayList)synergyList().clone();
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
				isRequired = true;
			else if (colString.startsWith("PREFEAT"))
				setFeatList(colString.substring(8));
			else if (colString.startsWith("ACHECK"))
				setACheck(colString.substring(7));
			else if (colString.startsWith("ROOT"))
				setRootName(colString.substring(5));
			else if (colString.startsWith("DEFINE"))
				variableList.add("0|" + colString.substring(7));
			else if (colString.startsWith("KEY:"))
				setKeyName(colString.substring(4));
			else if (colString.startsWith("PRE"))
				preReqArrayList.add(colString);
			else if (col > 4)
				JOptionPane.showMessageDialog
					(null, "Illegal skill info " + sourceFile.getName() +
					":" + Integer.toString(lineNum) + " \"" + colString + "\"");
		}
	}

	public Skill()
	{
	}
}
