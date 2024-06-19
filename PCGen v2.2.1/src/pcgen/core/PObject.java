/*
 * PObject.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import pcgen.gui.Chooser;

/**
 * <code>PObject</code> This is the base class for several objects in the PCGen database.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class PObject extends Object implements Cloneable, Serializable
{
	public static final String s_STATNAMES = "STRDEXCONINTWISCHA";

	protected boolean isSpecified = false;
	protected ArrayList bonusList = new ArrayList();
	protected ArrayList variableList = new ArrayList();
	protected String name = "";
	protected ArrayList saveList = new ArrayList();
	protected String sourceFile = "";
	protected boolean visible = true;
	private String source = "";
	protected String qualifyString = "alwaysValid";
	protected String keyName = "";
	protected String choiceString = new String();
	protected ArrayList preReqArrayList = new ArrayList();

	public void setSourceFile(String sourceFile)
	{
		this.sourceFile = sourceFile;
	}

	public String getSource()
	{
		if (source.equals(""))
			return sourceFile;
		return source;
	}

	public void setSource(String aSource)
	{
		if (source.length() > 0)
			source = source + ",";
		source = source + aSource;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String aString)
	{
		name = aString;
		keyName = aString;
	}

	public String getKeyName()
	{
		return keyName;
	}

	public void setKeyName(String aString)
	{
		keyName = aString;
	}

	public String getSourceFile()
	{
		return sourceFile;
	}

	public Object clone()
	{
		PObject retVal = null;
		try
		{
			retVal = (PObject)super.clone();
			retVal.setName(name);
			retVal.setKeyName(keyName);
			retVal.choiceString = choiceString;
			retVal.bonusList = (ArrayList)bonusList.clone();
			retVal.variableList = (ArrayList)variableList.clone();
			retVal.sourceFile = sourceFile;
			retVal.visible = visible;
			retVal.preReqArrayList = (ArrayList)preReqArrayList.clone();
			retVal.saveList = (ArrayList)saveList.clone();
		}
		catch (Exception exc)
		{
			JOptionPane.showMessageDialog(null, exc.getMessage(), "PCGen", JOptionPane.ERROR_MESSAGE);
		}
		return retVal;
	}

	public boolean isInList(String aType)
	{
		return false;
	}

	public void addBonusList(String aString)
	{
		bonusList.add(aString);
	}

	public void addSaveList(String aString)
	{
		saveList.add(aString);
	}

	public boolean parseTag(String aTag)
	{
		return parseTagLevel(aTag, -9);
	}
	// return true if tag is parsed here
	public boolean parseTagLevel(String aTag, int anInt)
	{
		if (aTag.startsWith("SOURCE:"))
		{
			if (Globals.GetCurrentSource().length()>0)
				setSource(Globals.GetCurrentSource()+", "+aTag.substring(7));
			else
				setSource(aTag.substring(7));
			return true;
		}
		if (aTag.startsWith("BONUS:"))
		{
			if (anInt > -9)
				addBonusList(anInt + "|" + aTag.substring(6));
			else
				addBonusList(aTag.substring(6));
			return true;
		}
		return false;
	}

	public int bonusTo(String aType, String aName)
	{
		if (bonusList.size() == 0)
			return 0;
		int retVal = 0;
		aType = aType.toUpperCase();
		aName = aName.toUpperCase();
		final PlayerCharacter aPC = Globals.getCurrentPC();
		for (Iterator b = bonusList.iterator(); b.hasNext();)
		{
			int bonus=0;
			final String bString = ((String)b.next()).toUpperCase();
			StringTokenizer aTok = new StringTokenizer(bString, "|", false);
			if (aTok.countTokens() < 2)
				continue;
			String aString = aTok.nextToken();
			if ((!aString.equals(aType) && !aString.endsWith("%LIST")) ||
				(aString.endsWith("%LIST") && !isInList(aType)))
				continue;
			final String aList = aTok.nextToken();
			int aBonus = 0;
			aString = aTok.nextToken();
			aBonus = aPC.getVariableValue(aString, "", "").intValue();
			ArrayList preReqList = new ArrayList();
			String bonusTypeString = null;
			while (aTok.hasMoreTokens())
			{
				final String pString = aTok.nextToken();
				if (pString.startsWith("PRE"))
					preReqList.add(pString);
				else if (pString.startsWith("TYPE="))
					bonusTypeString = pString.substring(5);
			}
			// must meet criteria before adding any bonuses
			if (!passesPreReqTestsForList(preReqList))
				continue;
			if (aList.equals("LIST") && isInList(aName))
				bonus += aBonus;
			aTok = new StringTokenizer(aList, ",", false);
			if (aList.equals("LIST"))
				aTok.nextToken();
			while (aTok.hasMoreTokens())
				if (aTok.nextToken().equals(aName))
					bonus += aBonus;
			aPC.SetBonusStackFor(bonus, bonusTypeString);
			retVal+=bonus;
		}
		return retVal;
	}

	public void getChoices(String aChoice, ArrayList selectedBonusList)
	{
		getChoices(aChoice, selectedBonusList, this);
	}

	public void getChoices(String aChoice, ArrayList selectedBonusList, PObject theObj)
	{
		if (!choiceString.startsWith("SPELLLEVEL") && !aChoice.startsWith("SPELLLEVEL") &&
			!aChoice.startsWith("WEAPONPROF"))
			return;
		Chooser c = new Chooser();
		if (aChoice.length() == 0)
			aChoice = choiceString;
		StringTokenizer aTok = new StringTokenizer(aChoice, "|", false);
		aTok.nextToken(); // should be SPELLLEVEL or WEAPONPROF
		c.setPool(Integer.parseInt(aTok.nextToken()));
		c.setAllowsDups(true);
		ArrayList aArrayList = new ArrayList();
		ArrayList otherArrayList = new ArrayList();
		ArrayList cArrayList = new ArrayList();
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aChoice.startsWith("SPELLLEVEL"))
			getSpellTypeChoices(aChoice, aArrayList, cArrayList); // get appropriate choices for chooser
		else if (aChoice.startsWith("WEAPONPROF")) // determine appropriate choices for chooser
		{
			c.setAllowsDups(false);
			c.setTitle("Weapon Choice(s)");
			while (aTok.hasMoreTokens())
			{
				String aString = aTok.nextToken();
				boolean adding = false;
				String cString = aString;
				if (aString.lastIndexOf("[") > -1)
				{
					StringTokenizer bTok = new StringTokenizer(aString, "[]", false);
					String bString = bTok.nextToken();
					adding = true;
					while (bTok.hasMoreTokens())
						otherArrayList.add(bString + "|" + bTok.nextToken());
					aString = bString;
				}
				if (aString.equals("DEITYWEAPON"))
				{
					if (aPC.getDeity() != null)
					{
						StringBuffer weaponList = new StringBuffer((String)aPC.getDeity().getFavoredWeapon());
						if (weaponList.equals("ALL"))
						{
							weaponList.delete(0, 2);
							for (Iterator wi = Globals.getWeaponProfList().iterator(); wi.hasNext();)
							{
								if (weaponList.length() > 0)
									weaponList.append("|");
								weaponList.append((String)wi.next());
							}
						}
						StringTokenizer bTok = new StringTokenizer(weaponList.toString(), "|", false);
						while (bTok.hasMoreTokens())
						{
							String bString = bTok.nextToken();
							aArrayList.add(bString);
							if (adding)
							{
								StringTokenizer cTok = new StringTokenizer(cString, "[]", false);
								String dString = cTok.nextToken();
								while (cTok.hasMoreTokens())
									otherArrayList.add(bString + "|" + cTok.nextToken());
							}
						}
					}
				}
				else
					aArrayList.add(aString);
			}
		}
		c.setAvailableList(aArrayList);
		c.setUniqueList(cArrayList);
		c.show();
		if (aChoice.startsWith("WEAPONPROF"))
		{
			for (int index = 0; index < c.getSelectedList().size(); index++)
			{
				String aString = (String)c.getSelectedList().get(index);
				for (Iterator e = otherArrayList.iterator(); e.hasNext();)
				{
					String bString = (String)e.next();
					aTok = new StringTokenizer(bString, "|", false);
					if (aTok.nextToken().equals(aString))
					{
						String cString = aTok.nextToken();
						if (cString.startsWith("WEAPONPROF"))
							aPC.addWeaponProf(aString);
						else if (cString.startsWith("FEAT="))
						{
							Feat aFeat = aPC.getFeatNamed(cString.substring(5));
							if (aFeat == null)
							{
								aFeat = (Feat)Globals.getFeatKeyed(cString.substring(5)).clone();
								if (aFeat != null)
									aPC.getFeatList().add(aFeat);
							}
							if (aFeat != null && !aFeat.associatedList().contains(aString))
								aFeat.associatedList().add(aString);
						}
					}
				}
			}
		}
		else
		{
			for (int index = 0; index < c.getSelectedList().size(); index++)
			{
				String aString = (String)c.getSelectedList().get(index);
				for (Iterator e = selectedBonusList.iterator(); e.hasNext();)
				{
					String bString = (String)e.next();
					applyBonus(bString, aString, theObj);
				}
			}
		}
		return;
	}

	public void getSpellTypeChoices(String aChoice, ArrayList availList, ArrayList uniqueList)
	{
		StringTokenizer aTok = new StringTokenizer(aChoice, "|", false);
		aTok.nextToken(); // should be SPELLLEVEL
		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			while (!aString.startsWith("CLASS=") && !aString.startsWith("TYPE=") && aTok.hasMoreTokens())
				aString = aTok.nextToken();
			if (!aTok.hasMoreTokens())
				break;
			boolean endIsUnique = false;
			int minLevel = Integer.parseInt(aTok.nextToken());
			String mString = aTok.nextToken();
			if (mString.endsWith(".A"))
			{
				endIsUnique = true;
				mString = mString.substring(0, mString.lastIndexOf(".A"));
			}
			int maxLevel = minLevel;
			final PlayerCharacter aPC = Globals.getCurrentPC();
			if (aString.startsWith("CLASS="))
			{
				PCClass aClass = aPC.getClassKeyed(aString.substring(6));
				for (int i = 0; i < mString.length(); i++)
				{
					if (mString.length() > 7 + i && mString.substring(i, i + 8).equals("MAXLEVEL"))
					{
						int j = -1;
						int aLevel = aClass.getLevel().intValue()-1;
						String bString="0";
						if (aLevel>=0) // some classes, like "Domain" are level 0, so this index would be -1
							bString = aClass.getCastList().get(aLevel).toString();
						if (!bString.equals("0"))
						{
							StringTokenizer bTok = new StringTokenizer(bString, ",", false);
							j = bTok.countTokens() - 1;
						}
						bString = "";
						if (mString.length() > i + 8)
							bString = mString.substring(i + 8);
						mString = mString.substring(0, i) + new Integer(j).toString() + bString;
						i--; // back up one since we just did a replacement
					}
				}
				maxLevel = aPC.getVariableValue(mString, "", "").intValue();
				if (aClass != null)
				{
					for (int i = minLevel; i <= maxLevel; i++)
					{
						String bString = aClass.getName() + " " + i;
						if (!availList.contains(bString))
							availList.add(bString);
						if (i == maxLevel && endIsUnique)
							uniqueList.add(bString);
					}
				}
			}
			if (aString.startsWith("TYPE="))
			{
				aString = aString.substring(5);
				for (Iterator e = aPC.getClassList().iterator(); e.hasNext();)
				{
					PCClass aClass = (PCClass)e.next();
					if (aClass.getSpellType().equals(aString))
					{
						if (mString.startsWith("MAXLEVEL"))
						{
							int aLevel = aClass.getLevel().intValue()-1;
							String bString="0";
							if (aLevel>=0) // some classes, like "Domain" are level 0, so this index would be -1
								bString = aClass.getCastList().get(aLevel).toString();
							if (bString.equals("0"))
								maxLevel = -1;
							else
							{
								StringTokenizer bTok = new StringTokenizer(bString, ",", false);
								maxLevel = bTok.countTokens() - 1;
							}
							if (mString.length() > 8)
							{
								mString = mString.substring(8);
								maxLevel = maxLevel + pcgen.util.Delta.decode(mString).intValue();
							}
						}
						for (int i = minLevel; i <= maxLevel; i++)
						{
							String bString = aClass.getName() + " " + i;
							if (!availList.contains(bString))
								availList.add(bString);
							if (i == maxLevel && endIsUnique)
								uniqueList.add(bString);
						}
					}
				}
			}
		}
	}

	public String makeBonusString(String bonusString, String chooseString)
	{
		// assumption is that the chooseString is in the form class/type[space]level
		// if bonusString has "TYPE=%" or "CLASS=%" replace that with name from chooseString
		// if bonusString has "LEVEL=%" replace that with level part of chooseString
		int i = chooseString.lastIndexOf(" ");
		String classString = "";
		String levelString = "";
		if (bonusString.startsWith("BONUS:"))
			bonusString = bonusString.substring(6);
		boolean lockIt = bonusString.endsWith(".LOCK");
		if (lockIt)
			bonusString = bonusString.substring(0, bonusString.lastIndexOf(".LOCK"));

		if (i > -1)
		{
			classString = chooseString.substring(0, i);
			if (i < chooseString.length())
				levelString = chooseString.substring(i + 1);
		}
		while (bonusString.lastIndexOf("TYPE=%") > -1)
		{
			i = bonusString.lastIndexOf("TYPE=%");
			bonusString = bonusString.substring(0, i + 5) + classString + bonusString.substring(i + 6);
		}
		while (bonusString.lastIndexOf("CLASS=%") > -1)
		{
			i = bonusString.lastIndexOf("CLASS=%");
			bonusString = bonusString.substring(0, i + 6) + classString + bonusString.substring(i + 7);
		}
		while (bonusString.lastIndexOf("LEVEL=%") > -1)
		{
			i = bonusString.lastIndexOf("LEVEL=%");
			bonusString = bonusString.substring(0, i + 6) + levelString + bonusString.substring(i + 7);
		}
		if (lockIt)
		{
			i = bonusString.lastIndexOf("|");
			Float val = Globals.getCurrentPC().getVariableValue(bonusString.substring(i + 1), "", "");
			bonusString = bonusString.substring(0, i) + "|" + val;
		}
		if (isAPCClass())
		{
			bonusString = "0|" + bonusString;
		}
		return bonusString;
	}

	public void removeBonus(String bonusString, String chooseString, PObject theObj)
	{
		bonusString = makeBonusString(bonusString, chooseString);
		int index = bonusList.indexOf(bonusString);
		if (index >= 0)
		{
			bonusList.remove(index);
		}
		else
		{
			if (Globals.isDebugMode())
				System.out.println("removeBonus: Could not find bonus: " + bonusString + " in bonusList." );
		}
		bonusString = "BONUS|" + bonusString;
		index = saveList.indexOf(bonusString);
		if (index >= 0)
		{
			saveList.remove(index);
		}
		else
		{
			if (Globals.isDebugMode())
				System.out.println("removeBonus: Could not find bonus: " + bonusString + " in saveList." );
		}
	}

	public void removeBonus(String bonusString, String chooseString)
	{
		removeBonus(bonusString, chooseString, this);
	}

	public void applyBonus(String bonusString, String chooseString, PObject theObj)
	{
		bonusString = makeBonusString(bonusString, chooseString);
		bonusList.add(bonusString);
		saveList.add("BONUS|" + bonusString);
	}

	public void applyBonus(String bonusString, String chooseString)
	{
		applyBonus(bonusString, chooseString, this);
	}

	public boolean isAPCClass()
	{
		return false;
	}

	public String getQualifyString() {
		return qualifyString;
	}
	public void addToQualifyListing(String aString) { // Not working right outside of Template... arcady 10/24/2001
	//	System.out.println("This is:" + this);
		qualifyString = aString;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC != null)
			aPC.addToQualifyList(this);
	}

	//PreReqs for all objects
	public boolean passesPreReqTests()
	{
		return passesPreReqTestsForList(preReqArrayList);
	}

	//PreReqs for specified list
	public boolean passesPreReqTestsForList(ArrayList anArrayList)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null)
			return false;
		if (anArrayList.size() == 0)
			return true;
		if (aPC.getClassList().size() == 0)
		{
			final PCClass aClass = Globals.getClassNamed(name);
			if (aClass != null && aClass == this && aClass.multiPreReqs())
			{
				return true;
			}
		}
		// if the item being inquired about is in their qualify list we don't even check for prereqs.
		if (aPC.checkQualifyList(this.getName())) {
			System.out.println("In Qualify list:" + this);
			return true;
//		} else {
//			System.out.println("Not In Qualify list:" + this);
//			System.out.println("Qualify list:" + aPC.getQualifyList());
		}
		boolean flag = false;
		ArrayList aFeatList = (ArrayList)aPC.aggregateFeatList();
		if (Globals.isDebugMode())
			System.out.println("PreReq:" + name);
		String aType = "";
		String aList = "";
		for (Iterator e = anArrayList.iterator(); e.hasNext();)
		{
			flag = false;
			String preString = (String)e.next();
			StringTokenizer aaTok = new StringTokenizer(preString, ":", false);
			aType = aaTok.nextToken();
			if (aaTok.hasMoreTokens())
				aList = aaTok.nextToken();
			else
				aList = "";
			int index = -1;
			int number = 0;
			// e.g. PREFEAT:3,TYPE=Metamagic
			if (aType.equals("PREFEAT"))
			{
				StringTokenizer aTok = new StringTokenizer(aList, "|");
				aList = aTok.nextToken();
				boolean countMults = false;
				if (aTok.hasMoreTokens())
					countMults = aTok.nextToken().equals("CHECKMULT");
				aTok = new StringTokenizer(aList, ",");
				// the number of feats which must match
				number = Integer.parseInt(aTok.nextToken());
				while (aTok.hasMoreTokens() && number > 0)
				{
					String aString = aTok.nextToken();
					StringTokenizer bTok = new StringTokenizer(aString, "(", false);
					String pString = bTok.nextToken();
					int i = -1;
					if (pString.length() != aString.length())
						i = pString.length(); // begin of subchoices
					String featName = null;
					String subName = null;
					int j = -1;
					boolean isType = aString.startsWith("TYPE=");
					if (i > -1)
					{
						featName = aString.substring(0, i).trim();
						subName = aString.substring(i + 1, aString.length() - 1);
						j = subName.lastIndexOf('%');
						if (j > -1)
							subName = subName.substring(0, j);
					}
					else
						featName = aString;
					boolean foundIt = false;
					for (Iterator e1 = aFeatList.iterator(); e1.hasNext();)
					{
						if (foundIt && isType == false || number <= 0)
							break;
						Feat aFeat = (Feat)e1.next();
						if ((isType == false && (aFeat.getName().equals(featName) || aFeat.getName().equals(aString))) ||
							(isType && aFeat.getType().equals(featName.substring(5))))
						{
							if (subName != null && (aFeat.getName().equals(aString) || aFeat.associatedList().contains(subName)) ||
								subName == null)
							{
								number--;
								if (aFeat.isMultiples() && countMults)
									number -= (aFeat.associatedList().size() - 1);
								foundIt = true;
							}
							else if (subName != null && j > -1) // search for match
							{
								for (Iterator e2 = aFeat.associatedList().iterator(); e2.hasNext();)
								{
									String fString = (String)e2.next();
									if (fString.startsWith(subName.substring(0, j)))
									{
										number--;
										foundIt = true;
										if (!countMults)
											break;
									}
								}
							}
						}
					}
				}
				flag = (number <= 0);
			}
			else if (aType.equals("PRESKILL"))
			// e.g. PRESKILL:1,Hide,Move Silent=3
			{
				int i = aList.lastIndexOf('=');
				int ranks = Integer.parseInt(aList.substring(i + 1));
				final StringTokenizer aTok = new StringTokenizer(aList.substring(0, i), ",", false);
				// the number of feats which must match
				number = Integer.parseInt(aTok.nextToken());
				ArrayList sList = (ArrayList)aPC.getSkillList().clone();
				ArrayList tList = new ArrayList();
				while (aTok.hasMoreTokens() && number > 0)
				{
					String aString = aTok.nextToken();
					StringTokenizer bTok = new StringTokenizer(aString, "(", false);
					String pString = bTok.nextToken();
					i = -1;
					if (pString.length() != aString.length())
						i = pString.length();
					String skillName = null;
					String subName = null;
					boolean isType = aString.startsWith("TYPE.");
					int j = -1;
					if (i > -1)
					{
						j = -1;
						skillName = aString.substring(0, i);
						subName = aString.substring(i + 1, aString.length() - 1);
						j = subName.lastIndexOf('%');
						if (j > -1)
						{
							subName = subName.substring(0, j);
						}
					}
					else
					{
						skillName = aString;
						j = aString.lastIndexOf('%');
					}
					boolean foundIt = false;
					Skill aSkill = null;
					for (Iterator e1 = sList.iterator(); e1.hasNext();)
					{
						if (foundIt && isType == false || number <= 0)
							break;
						aSkill = (Skill)e1.next();
						String aSkillName = aSkill.getName();
						if (isType == false && (aSkillName.equals(skillName) || aSkillName.equals(aString) ||
							(j >= 0 && aSkillName.startsWith(aString.substring(0, j)))))
						{
							if (aSkill.getRank().intValue() < ranks || tList.contains(aSkillName))
							{
								aSkill = null;
								continue;
							}
							if (j > -1 && aSkillName.startsWith(aString.substring(0, j)))
								break;
							if (j == -1 && aSkillName.equals(aString))
								break;
							aSkill = null;
						}
						else if ((isType && (aSkill.getType().indexOf(skillName.substring(5)) != -1)))
						{
							if (aSkill.getRank().intValue() < ranks || tList.contains(aSkillName))
							{
								aSkill = null;
								continue;
							}
							if (j > -1 && aSkill.getType().startsWith(aString.substring(5, j)))
								break;
							if (j == -1 && (aSkill.getType().indexOf(aString.substring(5)) != -1))
								break;
							aSkill = null;
						}
						aSkill = null;
					}
					if (aSkill != null && j > -1)
					{
						sList.remove(aSkill);
					}
					flag = (aSkill != null);
					if (flag)
					{
						if (aSkill != null)
							tList.add(aSkill.getName());
						number--;
						foundIt = true;
					}
				}
				flag = (number == 0);
			}
			else if (aType.equals("PRESKILLTOT"))
			// e.g. PRESKILLTOT:Hide,Move Silent=3
			{
				int i = aList.lastIndexOf('=');
				int ranks = Integer.parseInt(aList.substring(i + 1));
				final StringTokenizer aTok = new StringTokenizer(aList.substring(0, i), ",", false);
				// the number of feats which must match
				// number = Integer.parseInt(aTok.nextToken());
				ArrayList sList = (ArrayList)aPC.getSkillList().clone();
				ArrayList tList = new ArrayList();
				while (aTok.hasMoreTokens() && ranks > 0)
				{
					String aString = aTok.nextToken();
					StringTokenizer bTok = new StringTokenizer(aString, "(", false);
					String pString = bTok.nextToken();
					i = -1;
					if (pString.length() != aString.length())
						i = pString.length();
					String skillName = null;
					String subName = null;
					boolean isType = aString.startsWith("TYPE.");
					int j = -1;
					if (i > -1)
					{
						j = -1;
						skillName = aString.substring(0, i);
						subName = aString.substring(i + 1, aString.length() - 1);
						j = subName.lastIndexOf('%');
						if (j > -1)
						{
							subName = subName.substring(0, j);
						}
					}
					else
					{
						skillName = aString;
						j = aString.lastIndexOf('%');
					}
					boolean foundIt = false;
					Skill aSkill = null;
					for (Iterator e1 = sList.iterator(); e1.hasNext();)
					{
						if (foundIt && isType == false || ranks <= 0)
							break;
						aSkill = (Skill)e1.next();
						if (isType == false && (aSkill.getName().equals(skillName) || aSkill.getName().equals(aString) ||
							(j >= 0 && aSkill.getName().startsWith(aString.substring(0, j)))))
						{
							if (tList.contains(aSkill.getName()))
							{
								aSkill = null;
								continue;
							}
							if (j > -1 && aSkill.getName().startsWith(aString.substring(0, j)))
								break;
							if (j == -1 && aSkill.getName().equals(aString))
								break;
							aSkill = null;
						}
						else if ((isType && (aSkill.getType().indexOf(skillName.substring(5)) != -1)))
						{
							if (tList.contains(aSkill.getName()))
							{
								aSkill = null;
								continue;
							}
							if (j > -1 && aSkill.getType().startsWith(aString.substring(5, j)))
								break;
							if (j == -1 && (aSkill.getType().indexOf(aString.substring(5)) != -1))
								break;
							aSkill = null;
						}
						aSkill = null;
					}
					if (aSkill != null && j > -1)
					{
						sList.remove(aSkill);
					}
					flag = (aSkill != null);
					if (flag)
					{
						if (aSkill != null) //Only here to shut jlint up
						{
							tList.add(aSkill.getName());
							ranks -= aSkill.getRank().intValue();
						}
						foundIt = true;
					}
				}
				flag = (ranks <= 0);
			}
			else if (aType.equals("PRECLASS"))
			{
				int i = aList.lastIndexOf('=');
				final int preClass = Integer.parseInt(aList.substring(i + 1));
				final StringTokenizer aTok = new StringTokenizer(aList.substring(0, i), ",", false);
				while (aTok.hasMoreTokens())
				{
					String aString = aTok.nextToken();
					if (aString.equals("Spellcaster"))
					{
						flag = aPC.isSpellCaster(preClass);
					}
					else
					{
						PCClass aClass = aPC.getClassNamed(aString);
						flag = (aClass != null && aClass.getLevel().intValue() >= preClass);
					}
					if (flag)
						break;
				}
			}
			else if (aType.equals("PRECLASSLEVELMAX"))
//			If any class is over in level this should return false.
			{
				int i = aList.lastIndexOf('=');
				boolean oneOver = false;
				final int preClass = Integer.parseInt(aList.substring(i + 1));
				final StringTokenizer aTok = new StringTokenizer(aList.substring(0, i), ",", false);
				while (aTok.hasMoreTokens())
				{
					String aString = aTok.nextToken();
					if (aString.equals("Spellcaster"))
					{
						oneOver = aPC.isSpellCastermax(preClass);
					}
					else
					{
						PCClass aClass = aPC.getClassNamed(aString);
						if (aClass != null && aClass.getLevel().intValue() <= preClass) oneOver = true;
					}
				}
				flag = oneOver;
			}
			else if (aType.equals("PRELEVEL"))
			{
				final int preLevel = Integer.parseInt(aList);
				flag = (aPC.totalLevels() >= preLevel);
			}
			else if (aType.equals("PRELEVELMAX"))
			{
				final int preLevelmax = Integer.parseInt(aList);
				flag = (aPC.totalLevels() <= preLevelmax);
			}
			else if (aType.equals("PREFORCEPTS"))
			{
				final int preFPoints = Integer.parseInt(aList);
				final int myfhold = Integer.parseInt(aPC.getFPoints());
				flag = (myfhold >= preFPoints);
			}
			else if (aType.equals("PREDSIDEPTS"))
			{
				final int preDPoints = Integer.parseInt(aList);
				final int mydhold = Integer.parseInt(aPC.getDPoints());
				flag = (mydhold >= preDPoints);
			}
			else if (aType.equals("RESTRICT"))
			{
				final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
				while (aTok.hasMoreTokens() && flag == false)
				{
					String aString = aTok.nextToken();
					PCClass aClass = aPC.getClassNamed(aString);
					flag = (aClass == null);
				}
			}
			else if (aType.equals("PRERACE"))
			{
				final StringTokenizer aTok = new StringTokenizer(aList, "|", false);
				while (aTok.hasMoreTokens())
				{
					String aString = aTok.nextToken();
					if (aString.startsWith("[") && aString.endsWith("]"))
						flag = !aPC.getRace().getName().startsWith(aString.substring(1, aString.length() - 1));
					else
						flag = (aPC.getRace().getName().startsWith(aString));
					if (!aString.startsWith("[") && flag)
						break;
					if (aString.startsWith("[") && flag == false)
						break;
				}
			}
			else if (aType.equals("PRETEMPLATE"))
			{
				final StringTokenizer aTok = new StringTokenizer(aList, "|", false);
				while (aTok.hasMoreTokens())
				{
					String aString = aTok.nextToken();
					flag = (aPC.getTemplateNamed(aString) != null);
					if (flag)
						break;
				}
			}
			else if (aType.equals("PREATT"))
			{
				final int anInt = Integer.parseInt(aList);
				int att = aPC.getBonus(0, true);
				att -= aPC.modForSize();
				flag = att >= anInt;
			}
			else if (aType.equals("PREREPUTATION"))
			{
				final int anInt = Integer.parseInt(aList);
				String rep = String.valueOf(aPC.reputation());
				int repf = Integer.parseInt(rep);
				flag = repf >= anInt;
			}
			else if (aType.equals("PREREPUTATIONLTEQ"))
			{
				final int anInt = Integer.parseInt(aList);
				final String rep = String.valueOf(aPC.reputation());
				final int repf = Integer.parseInt(rep);
				flag = repf <= anInt;
			}
			else if (aType.equals("PREUATT"))
			{
				final int requiredValue = Integer.parseInt(aList);
				int att = 0;
				for (e = aPC.getClassList().iterator(); e.hasNext();)
				{
					final PCClass aClass = (PCClass)e.next();
					String s = aClass.getUattForLevel(aClass.getLevel().intValue());
					if (s.length() == 0 || s.equals("0"))
						att = Math.max(att, aClass.baseAttackBonus(0));
					else
					{
						final StringTokenizer bTok = new StringTokenizer(s, ",", false);
						s = bTok.nextToken();
						att = Math.max(att, Integer.parseInt(s));
					}
				}
				flag = att >= requiredValue;
			}
			else if (aType.equals("PRESTAT"))
			{
				int i = -1;
				if (aList.startsWith("STR"))
					i = 0;
				else if (aList.startsWith("DEX"))
					i = 1;
				else if (aList.startsWith("CON"))
					i = 2;
				else if (aList.startsWith("INT"))
					i = 3;
				else if (aList.startsWith("WIS"))
					i = 4;
				else if (aList.startsWith("CHA"))
					i = 5;
				if (i >= 0)
				{
					flag = aPC.adjStats(i) >=
						Integer.parseInt(aList.substring(aList.lastIndexOf('=') + 1));
				}
				else
				{
					System.out.println("The stat " + aList + " is unknown.");
				}
			}
			else if (aType.equals("PRESPELLTYPE"))
			// e.g. PRESPELLTYPE=Arcane,1,1
			{
				final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
				String aString = aTok.nextToken();
				number = Integer.parseInt(aTok.nextToken());
				int minlevel = Integer.parseInt(aTok.nextToken());
				StringTokenizer bTok = new StringTokenizer(aString, "|", false);
				flag = false;
				while (bTok.hasMoreTokens() && flag == false)
				{
					ArrayList aArrayList = aPC.aggregateSpellList(bTok.nextToken(), "", "", minlevel, 20);
					flag = (aArrayList.size() >= number);
				}
			}
			else if (aType.equals("PRESPELLSCHOOL"))
			{
				// e.g. PRESPELLSCHOOL:Divination,7,1
				final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
				String aString = aTok.nextToken();
				number = Integer.parseInt(aTok.nextToken());
				int minlevel = Integer.parseInt(aTok.nextToken());
				ArrayList aArrayList = aPC.aggregateSpellList("Any", aString, "A", minlevel, 20);
				if (Globals.isDebugMode())
					System.out.println("Spells=" + aArrayList.size());
				flag = (aArrayList.size() >= number);
			}
			else if (aType.equals("PRESPELLSCHOOLSUB"))
			{
				// e.g. PRESPELLSCHOOLSUB:Shadow,7,1
				final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
				String aString = aTok.nextToken();
				number = Integer.parseInt(aTok.nextToken());
				int minlevel = Integer.parseInt(aTok.nextToken());
				ArrayList aArrayList = aPC.aggregateSpellList("Any", "A", aString, minlevel, 20);
				flag = (aArrayList.size() >= number);
			}
			else if (aType.equals("PRESPELL"))
			{
				// e.g. PRESPELL:3,
				final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
				number = Integer.parseInt(aTok.nextToken());
				ArrayList aArrayList = aPC.aggregateSpellList("Any", "", "", 0, 20);
				while (aTok.hasMoreTokens())
				{
					String bString = aTok.nextToken();
					for (Iterator e1 = aArrayList.iterator(); e1.hasNext();)
					{
						final Spell aSpell = (Spell)e1.next();
						if (aSpell.getName().equals(bString))
						{
							number--;
							break;
						}
					}
					if (number == 0)
						break;
				}
				flag = (number <= 0);
			}
			else if (aType.equals("PRESPELLCAST"))
			{
				final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
				ArrayList classList = (ArrayList)aPC.getClassList().clone();
				PCClass aClass = aPC.getClassNamed("Domain");
				if (aClass != null)
					classList.remove(aClass);
				while (aTok.hasMoreTokens())
				{
					String aString = aTok.nextToken();
					if (aString.startsWith("MEMORIZE"))
					{
						for (Iterator e1 = classList.iterator(); e1.hasNext();)
						{
							aClass = (PCClass)e1.next();
							if ((aClass.getMemorizeSpells() && aString.endsWith("N")) ||
								(!aClass.getMemorizeSpells() && aString.endsWith("Y")))
							{
								e1.remove();
							}
						}
					}
					else if (aString.startsWith("TYPE"))
					{
						for (Iterator e1 = classList.iterator(); e1.hasNext();)
						{
							aClass = (PCClass)e1.next();
							if (aString.substring(5).lastIndexOf(aClass.getSpellType()) == -1)
							{
								e1.remove();
							}
						}
					}
					flag = classList.size() > 0;
					if (!flag)
						break;
				}
			}
			else if (aType.equals("PRESA"))
			{
				final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
				flag = true;
				while (aTok.hasMoreTokens() && flag)
				{
					String aString = aTok.nextToken();
					flag = false;
					for (Iterator e1 = aPC.getSpecialAbilityList().iterator(); e1.hasNext();)
					{
						String e1String = (String)e1.next();
						flag = e1String.startsWith(aString);
						if (flag)
							break;
					}
				}
			}
			else if (aType.equals("PRELANG"))
			{
				final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
				final int storedValue = Integer.parseInt(aTok.nextToken());
				number = storedValue;
				while (aTok.hasMoreTokens() && number > 0)
				{
					String aString = aTok.nextToken();
					if (aPC.getLanguagesList().contains(aString))
						number--;
				}
				if (aList.lastIndexOf("ANY") > -1)
					flag = storedValue <= aPC.getLanguagesList().size();
				else
					flag = (number == 0);
			}
			else if (aType.equals("PREWEAPONPROF"))
			{
				final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
				flag = false;
				while (aTok.hasMoreTokens())
				{
					String aString = aTok.nextToken();
					if (aPC.getWeaponProfList().contains(aString))
						flag = true;
					else if (aString.equals("DEITYWEAPON") && aPC.getDeity() != null &&
						aPC.getWeaponProfList().contains(aPC.getDeity().getFavoredWeapon()))
						flag = true;
				}
			}
			else if (aType.equals("PREITEM"))
			{
				final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
				flag = false;
				while (aTok.hasMoreTokens())
				{
					String aString = aTok.nextToken();
					for (Iterator e1 =
						aPC.getEquipmentList().values().iterator();
							 e1.hasNext();)
					{
						Equipment eq = (Equipment)e1.next();
						if (eq.getName().equals(aString))
						{
							flag = true;
							break;
						}
					}
				}
			}
			else if (aType.equals("PREVAR"))
			{
				final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
				flag = true;
				while (aTok.hasMoreTokens() && flag)
				{
					String varName = aTok.nextToken();
					String valString = "0";
					if (aTok.hasMoreTokens())
						valString = aTok.nextToken();
					Float aFloat = aPC.getVariableValue(valString, "", "");
					if (aFloat.doubleValue() > aPC.getVariable(varName, true, true, "", "").doubleValue())
						flag = false;
				}
			}
			else if (aType.equals("PREGENDER"))
				flag = aList.startsWith(aPC.getGender());
			else if (aType.equals("PREDEITY"))
			{
				final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
				flag = false;
				while (aTok.hasMoreTokens())
				{
					String aString = aTok.nextToken();
					flag = ((aString.equals("Y") && aPC.getDeity() != null) ||
						(aString.equals("N") && aPC.getDeity() == null) ||
						(aPC.getDeity() != null && aPC.getDeity().getName().equals(aString)));
					if (flag)
						break;
				}
			}
			else if (aType.equals("PREALIGN"))
			{
				flag = false;
				String alString = String.valueOf(aPC.getAlignment());
				flag = (aList.lastIndexOf(alString) > -1);
			}
			else if (aType.equals("PREFORT"))
			{
				final int nextInt = Integer.parseInt(aList);
				int fort = aPC.getBonus(1, true) + aPC.calcStatMod(Globals.CONSTITUTION);
				flag = (fort >= nextInt);
			}
			else if (aType.equals("PREWILL"))
			{
				final int nextInt = Integer.parseInt(aList);
				int will = aPC.getBonus(3, true) + aPC.calcStatMod(Globals.CONSTITUTION);
				flag = (will >= nextInt);
			}
			else if (aType.equals("PREREFLEX"))
			{
				final int nextInt = Integer.parseInt(aList);
				int ref = aPC.getBonus(2, true) + aPC.calcStatMod(Globals.CONSTITUTION);
				flag = (ref >= nextInt);
			}
			else if (aType.equals("PREFORTBASE"))
			{
				final int nextInt = Integer.parseInt(aList);
				int base = aPC.getBonus(1, false);
				flag = (base >= nextInt);
			}
			else if (aType.equals("PREWILLBASE"))
			{
				final int nextInt = Integer.parseInt(aList);
				int base = aPC.getBonus(3, false);
				flag = (base >= nextInt);
			}
			else if (aType.equals("PREREFLEXBASE"))
			{
				final int nextInt = Integer.parseInt(aList);
				int base = aPC.getBonus(2, false);
				flag = (base >= nextInt);
			}
			else if (aType.equals("PREDOMAIN"))
			{
				final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
				number = Integer.parseInt(aTok.nextToken());
				while (aTok.hasMoreTokens() && number > 0)
				{
					String bString = aTok.nextToken();
					if (aPC.getDomainNamed(bString) != null)
						number--;
				}
				flag = (number <= 0);
			}
			else if (aType.equals("PRESIZEEQ"))
			{
				int sizeInt = aPC.sizeIntForSize(aList);
				int pcSize = aPC.sizeInt();
				flag = (pcSize == sizeInt);
			}
			else if (aType.equals("PRESIZELT"))
			{
				int sizeInt = aPC.sizeIntForSize(aList);
				int pcSize = aPC.sizeInt();
				flag = (pcSize < sizeInt);
			}
			else if (aType.equals("PRESIZELTEQ"))
			{
				int sizeInt = aPC.sizeIntForSize(aList);
				int pcSize = aPC.sizeInt();
				flag = (pcSize <= sizeInt);
			}
			else if (aType.equals("PRESIZEGT"))
			{
				int sizeInt = aPC.sizeIntForSize(aList);
				int pcSize = aPC.sizeInt();
				flag = (pcSize > sizeInt);
			}
			else if (aType.equals("PRESIZEGTEQ"))
			{
				int sizeInt = aPC.sizeIntForSize(aList);
				int pcSize = aPC.sizeInt();
				flag = (pcSize >= sizeInt);
			}
			else if (aType.equals("PRESIZENEQ"))
			{
				int sizeInt = aPC.sizeIntForSize(aList);
				int pcSize = aPC.sizeInt();
				flag = (pcSize != sizeInt);
			}
			else if (aType.equals("PREMOVE"))
			{
				if(aPC == null || aPC.getRace() == null || aPC.getRace().getMovementTypes() == null)
				{
					flag = false;
				} else {
					final StringTokenizer movereqs = new StringTokenizer(aList, ",");
					StringTokenizer movereq = null;
					String moveType = null;
					int moveAmount = 0;
					int x = 0;
					while (movereqs.hasMoreTokens())
					{
						flag = false;
						movereq = new StringTokenizer(movereqs.nextToken(), "=");
						moveType = movereq.nextToken();
						moveAmount = Integer.parseInt(movereq.nextToken());
						for (x = 0; x < aPC.getRace().getMovementTypes().length; x++)
							if (moveType.equals(aPC.getRace().getMovementType(x)) && aPC.getRace().getMovement(x).intValue() >= moveAmount)
							{
								flag = true;
								break;
							}
						if (!flag)
							break;
					}
				}
			}
			else if (aType.equals("PREHANDSEQ"))
			{
				flag = aPC.getRace().GetHands() == Integer.parseInt(aList);
			}
			else if (aType.equals("PREHANDSLT"))
			{
				flag = aPC.getRace().GetHands() < Integer.parseInt(aList);
			}
			else if (aType.equals("PREHANDSLTEQ"))
			{
				flag = aPC.getRace().GetHands() <= Integer.parseInt(aList);
			}
			else if (aType.equals("PREHANDSGT"))
			{
				flag = aPC.getRace().GetHands() > Integer.parseInt(aList);
			}
			else if (aType.equals("PREHANDSGTEQ"))
			{
				flag = aPC.getRace().GetHands() >= Integer.parseInt(aList);
			}
			else if (aType.equals("PREHANDSNEQ"))
			{
				flag = aPC.getRace().GetHands() != Integer.parseInt(aList);
			}
			else
				flag=true; // if a PRExxx tag isn't known, don't fail the test on account of it!
			if (Globals.isDebugMode())
				System.out.println(aType + ":" + aList + "=" + flag);
			if (flag == false)
				return flag;
		}
		return flag;

	}

	///Creates the requirement string for printing.
	public String preReqStrings()
	{
		return preReqStringsForList(preReqArrayList);
	}

	public String preReqStringsForList(ArrayList anArrayList)
	{
		if (anArrayList.size() == 0)
			return ("");
		StringBuffer pString = new StringBuffer();

		StringTokenizer aTok = null;
		String aType = null;
		String aList = null;
		for (Iterator e = anArrayList.iterator(); e.hasNext();)
		{
			String aString = (String)e.next();
			aTok = new StringTokenizer(aString, ":", false);
			aType = aTok.nextToken();
			aList = aTok.nextToken();
			int i = 0;
			if (pString.length() > 0)
				pString.append("  ");
			if (aType.equals("PRECLASS"))
			{
				aTok = new StringTokenizer(aList, ",", false);
				pString.append("CLASS:");
				while (aTok.hasMoreTokens())
				{
					if (i++ > 0)
						pString.append(",");
					pString.append(aTok.nextToken());
				}
			}
			else if (aType.equals("PREATT"))
			{
				pString.append("ATT=");
				pString.append(aList);
			}
			else if (aType.equals("PREUATT"))
			{
				pString.append("UATT=");
				pString.append(aList);
			}
			else if (aType.equals("PRESTAT"))
			{
				pString.append("STAT:").append(aType).append("=").append(aList);
			}
			else if (aType.equals("PREALIGN"))
			{
				aTok = new StringTokenizer(aList, ",", false);
				pString.append("Alignment:");
				while (aTok.hasMoreTokens())
				{
					if (i++ > 0)
						pString.append(",");
					final int raceNumber = Integer.parseInt(aTok.nextToken());
					pString.append(Globals.s_ALIGNSHORT[raceNumber]);
				}
			}
			else
			{
				pString.append(aType.substring(3) + ":");
				aTok = new StringTokenizer(aList, ",", false);
				while (aTok.hasMoreTokens())
				{
					if (i++ > 0)
						pString.append(",");
					pString.append(aTok.nextToken());
				}
			}
		}
		return pString.toString();
	}

	/** This function is empty because there are no PObjects that are parsed directly
	 like there are for PCClass, Race, etc. (all the subclasses of PObject). This
	 method is solely to be overridden by subclasses.
	 Why is it public?
	 */
	public void parseLine(String aString, File aSourceFile, int lineNum)
	{

	}
}
