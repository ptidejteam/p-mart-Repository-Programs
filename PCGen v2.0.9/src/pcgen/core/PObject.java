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
	boolean isSpecified = false;
	protected ArrayList bonusList = new ArrayList();
	protected ArrayList variableList = new ArrayList();
	String name = "";
	protected ArrayList saveList = new ArrayList();
	protected String sourceFile = "";
	protected boolean visible = true;
	public static final String statNames = "STRDEXCONINTWISCHA";
	String keyName = "";
	String choiceString = new String();
	protected ArrayList preReqArrayList = new ArrayList();

	public void setSourceFile(String sourceFile)
	{
		this.sourceFile = sourceFile;
	}

	public String name()
	{
		return name;
	}

	public void setName(String aString)
	{
		name = aString;
		keyName = aString;
	}

	public String keyName()
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
			JOptionPane.showMessageDialog(null, exc.getMessage());
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

	public int bonusTo(String aType, String aName)
	{
		if (bonusList.size() == 0)
			return 0;
		int bonus = 0;
		for (Iterator b = bonusList.iterator(); b.hasNext();)
		{
			String bString = (String)b.next();
			StringTokenizer aTok = new StringTokenizer(bString, "|", false);
			if (aTok.countTokens() < 2)
				continue;
			String aString = aTok.nextToken();
			if ((!aString.equals(aType) && !aString.endsWith("%LIST")) ||
				(aString.endsWith("%LIST") && !isInList(aType)))
				continue;
			String aList = aTok.nextToken();
			int aBonus = 0;
			aString = aTok.nextToken();
			aBonus = Globals.currentPC.getVariableValue(aString, "", "").intValue();
			if (aList.equals("LIST") && isInList(aName))
				bonus += aBonus;
			aTok = new StringTokenizer(aList, ",", false);
			if (aList.equals("LIST"))
				aTok.nextToken();
			while (aTok.hasMoreTokens())
				if (aTok.nextToken().equals(aName))
					bonus += aBonus;
		}
		return bonus;
	}

	public void getChoices(String aChoice, ArrayList selectedBonusList)
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
		if (aChoice.startsWith("SPELLLEVEL"))
			getSpellTypeChoices(aChoice, aArrayList, cArrayList);
		else if (aChoice.startsWith("WEAPONPROF"))
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
					if (Globals.currentPC.deity() != null)
					{
						StringBuffer weaponList = new StringBuffer((String)Globals.currentPC.deity().favoredWeapon());
						if (weaponList.equals("ALL"))
						{
							weaponList.delete(0, 2);
							for (Iterator wi = Globals.weaponProfList.iterator(); wi.hasNext();)
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
							if (adding == true)
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
							Globals.currentPC.addWeaponProf(aString);
						else if (cString.startsWith("FEAT="))
						{
							Feat aFeat = Globals.currentPC.getFeatNamed(cString.substring(5));
							if (aFeat == null)
							{
								aFeat = (Feat)Globals.getFeatKeyed(cString.substring(5)).clone();
								if (aFeat != null)
									Globals.currentPC.featList.add(aFeat);
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
					applyBonus(bString, aString);
				}
			}
		}
		return;
	}

	private void getSpellTypeChoices(String aChoice, ArrayList availList, ArrayList uniqueList)
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
			if (aString.startsWith("CLASS="))
			{
				PCClass aClass = Globals.currentPC.getClassKeyed(aString.substring(6));
				for (int i = 0; i < mString.length(); i++)
				{
					if (mString.length() > 7 + i && mString.substring(i, i + 8).equals("MAXLEVEL"))
					{
						int j = -1;
						String bString = aClass.castList().get(aClass.level().intValue() - 1).toString();
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
				maxLevel = Globals.currentPC.getVariableValue(mString, "", "").intValue();
				if (aClass != null)
				{
					for (int i = minLevel; i <= maxLevel; i++)
					{
						String bString = aClass.name() + " " + i;
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
				for (Iterator e = Globals.currentPC.classList.iterator(); e.hasNext();)
				{
					PCClass aClass = (PCClass)e.next();
					if (aClass.spellType().equals(aString))
					{
						if (mString.startsWith("MAXLEVEL"))
						{
							String bString = aClass.castList().get(aClass.level().intValue() - 1).toString();
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
							String bString = aClass.name() + " " + i;
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

	public void applyBonus(String bonusString, String chooseString)
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
			Float val = Globals.currentPC.getVariableValue(bonusString.substring(i + 1), "", "");
			bonusString = bonusString.substring(0, i) + "|" + val;
		}
		if (isAPCClass())
		{
			bonusList.add("0|" + bonusString);
			saveList.add("BONUS|0|" + bonusString);
		}
		else
		{
			bonusList.add(bonusString);
			saveList.add("BONUS|" + bonusString);
		}
	}

	public boolean isAPCClass()
	{
		return false;
	}

	//PreReqs for all objects
	public boolean passesPreReqTests()
	{
		PlayerCharacter aPC = Globals.currentPC;
		if (aPC == null)
			return false;
		if (preReqArrayList.size()==0)
			return true;
		if (aPC.classList.size() == 0) {
			PCClass aClass=Globals.getClassNamed(name);
			if (aClass!=null && aClass==this && aClass. multiPreReqs() == true) {
				return true;
			}
		}
		boolean flag = false;
		ArrayList aFeatList = (ArrayList)aPC.aggregateFeatList();
		if (Globals.debugMode)
			System.out.println("PreReq:" + name);
		String aType = "";
		String aList = "";
		for (Iterator e = preReqArrayList.iterator(); e.hasNext();)
		{
			flag = false;
			String preString = (String)e.next();
			StringTokenizer aTok = new StringTokenizer(preString, ":", false);
			aType = aTok.nextToken();
			aList = aTok.nextToken();
			int index = -1;
			int number = 0;
			// e.g. PREFEAT:3,TYPE=Metamagic
			if (aType.equals("PREFEAT"))
			{
				aTok = new StringTokenizer(aList, "|");
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
						featName = aString.substring(0, i);
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
						if (foundIt == true && isType == false || number <= 0)
							break;
						Feat aFeat = (Feat)e1.next();
						if ((isType == false && (aFeat.name().equals(featName) || aFeat.name().equals(aString))) ||
							(isType == true && aFeat.type().equals(featName.substring(5))))
						{
							if (subName != null && (aFeat.name().equals(aString) || aFeat.associatedList().contains(subName)) ||
								subName == null)
							{
								number--;
								if (aFeat.multiples() && countMults)
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
				aTok = new StringTokenizer(aList.substring(0, i), ",", false);
				// the number of feats which must match
				number = Integer.parseInt(aTok.nextToken());
				ArrayList sList = (ArrayList)aPC.skillList().clone();
				ArrayList tList = new ArrayList();
				while (aTok.hasMoreTokens() && number > 0)
				{
					String aString = aTok.nextToken();
					int j = aString.lastIndexOf('%');
					Skill aSkill = null;
					for (Iterator e1 = sList.iterator(); e1.hasNext();)
					{
						aSkill = (Skill)e1.next();
						if (aSkill.rank().intValue() < ranks || tList.contains(aSkill.name()))
						{
							aSkill = null;
							continue;
						}
						if (j > -1 && aSkill.name().startsWith(aString.substring(0, j)))
							break;
						if (j == -1 && aSkill.name().equals(aString))
							break;
						aSkill = null;
					}
					if (aSkill != null && j > -1)
					{
						sList.remove(aSkill);
					}
					flag = (aSkill != null);
					if (flag == true)
					{
						if (aSkill != null)
							tList.add(aSkill.name());
						number--;
					}
				}
				flag = (number == 0);
			}
			else if (aType.equals("PRECLASS"))
			{
				int i = aList.lastIndexOf('=');
				final int preClass = Integer.parseInt(aList.substring(i + 1));
				aTok = new StringTokenizer(aList.substring(0, i), ",", false);
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
						flag = (aClass != null && aClass.level().intValue() >= preClass);
					}
					if (flag == true)
						break;
				}
			}
			else if (aType.equals("PRELEVEL"))
			{
				final int preLevel = Integer.parseInt(aList);
				flag = (aPC.totalLevels() >= preLevel);
				if (flag == true)
					break;
			}
			else if (aType.equals("RESTRICT"))
			{
				aTok = new StringTokenizer(aList, ",", false);
				while (aTok.hasMoreTokens() && flag == false)
				{
					String aString = aTok.nextToken();
					PCClass aClass = aPC.getClassNamed(aString);
					flag = (aClass == null);
				}
			}
			else if (aType.equals("PRERACE"))
			{
				aTok = new StringTokenizer(aList, "|", false);
				while (aTok.hasMoreTokens())
				{
					String aString = aTok.nextToken();
					if (aString.startsWith("[") && aString.endsWith("]"))
						flag = !aPC.race().name().startsWith(aString.substring(1, aString.length() - 1));
					else
						flag = (aPC.race().name().startsWith(aString));
					if (!aString.startsWith("[") && flag == true)
						break;
					if (aString.startsWith("[") && flag == false)
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
			else if (aType.equals("PREUATT"))
			{
				final int requiredValue = Integer.parseInt(aList);
				int att = 0;
				for (e = aPC.classList.iterator(); e.hasNext();)
				{
					PCClass aClass = (PCClass)e.next();
					String s = aClass.getUattForLevel(aClass.level().intValue());
					if (s.length() == 0)
						att = Math.max(att, aClass.baseAttackBonus(0));
					else
					{
						StringTokenizer bTok = new StringTokenizer(s, ",", false);
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
				aTok = new StringTokenizer(aList, ",", false);
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
				aTok = new StringTokenizer(aList, ",", false);
				String aString = aTok.nextToken();
				number = Integer.parseInt(aTok.nextToken());
				int minlevel = Integer.parseInt(aTok.nextToken());
				ArrayList aArrayList = aPC.aggregateSpellList("Any", aString, "A", minlevel, 20);
				if (Globals.debugMode)
					System.out.println("Spells=" + aArrayList.size());
				flag = (aArrayList.size() >= number);
			}
			else if (aType.equals("PRESPELLSCHOOLSUB"))
			{
				// e.g. PRESPELLSCHOOLSUB:Shadow,7,1
				aTok = new StringTokenizer(aList, ",", false);
				String aString = aTok.nextToken();
				number = Integer.parseInt(aTok.nextToken());
				int minlevel = Integer.parseInt(aTok.nextToken());
				ArrayList aArrayList = aPC.aggregateSpellList("Any", "A", aString, minlevel, 20);
				flag = (aArrayList.size() >= number);
			}
			else if (aType.equals("PRESPELL"))
			{
				// e.g. PRESPELL:3,
				aTok = new StringTokenizer(aList, ",", false);
				number = Integer.parseInt(aTok.nextToken());
				ArrayList aArrayList = aPC.aggregateSpellList("Any", "", "", 0, 20);
				while (aTok.hasMoreTokens())
				{
					String bString = aTok.nextToken();
					for (Iterator e1 = aArrayList.iterator(); e1.hasNext();)
					{
						Spell aSpell = (Spell)e1.next();
						if (aSpell.name().equals(bString))
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
				aTok = new StringTokenizer(aList, ",", false);
				ArrayList classList = (ArrayList)aPC.classList.clone();
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
							if ((aClass.memorizeSpells && aString.endsWith("N")) ||
								(!aClass.memorizeSpells && aString.endsWith("Y")))
							{
								//classList.removeElement(aClass);
								e1.remove();
							}
						}
					}
					else if (aString.startsWith("TYPE"))
					{
						for (Iterator e1 = classList.iterator(); e1.hasNext();)
						{
							aClass = (PCClass)e1.next();
							if (aString.substring(5).lastIndexOf(aClass.spellType()) == -1)
							{
								//classList.removeElement(aClass);
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
				aTok = new StringTokenizer(aList, ",", false);
				flag = true;
				while (aTok.hasMoreTokens() && flag == true)
				{
					String aString = aTok.nextToken();
					flag = false;
					for (Iterator e1 = aPC.specialAbilityList().iterator(); e1.hasNext();)
					{
						String e1String = (String)e1.next();
						flag = e1String.startsWith(aString);
						if (flag == true)
							break;
					}
				}
			}
			else if (aType.equals("PRELANG"))
			{
				aTok = new StringTokenizer(aList, ",", false);
				final int storedValue = Integer.parseInt(aTok.nextToken());
				number = storedValue;
				while (aTok.hasMoreTokens() && number > 0)
				{
					String aString = aTok.nextToken();
					if (aPC.languagesList().contains(aString))
						number--;
				}
				if (aList.lastIndexOf("ANY") > -1)
					flag = storedValue <= aPC.languagesList().size();
				else
					flag = (number == 0);
			}
			else if (aType.equals("PREWEAPONPROF"))
			{
				aTok = new StringTokenizer(aList, ",", false);
				flag = false;
				while (aTok.hasMoreTokens())
				{
					String aString = aTok.nextToken();
					if (aPC.weaponProfList().contains(aString))
						flag = true;
					else if (aString.equals("DEITYWEAPON") && aPC.deity() != null &&
						aPC.weaponProfList().contains(aPC.deity().favoredWeapon()))
						flag = true;
				}
			}
			else if (aType.equals("PREITEM"))
			{
				aTok = new StringTokenizer(aList, ",", false);
				flag = false;
				while (aTok.hasMoreTokens())
				{
					String aString = aTok.nextToken();
					for (Iterator e1 =
						aPC.equipmentList().values().iterator();
							 e1.hasNext();)
					{
						Equipment eq = (Equipment)e1.next();
						if (eq.name().equals(aString))
						{
							flag = true;
							break;
						}
					}
				}
			}
			else if (aType.equals("PREVAR"))
			{
				aTok = new StringTokenizer(aList, ",", false);
				flag = true;
				while (aTok.hasMoreTokens() && flag == true)
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
				flag = aList.startsWith(aPC.gender());
			else if (aType.equals("PREDEITY"))
			{
				aTok = new StringTokenizer(aList, ",", false);
				flag = false;
				while (aTok.hasMoreTokens())
				{
					String aString = aTok.nextToken();
					flag = ((aString.equals("Y") && aPC.deity() != null) ||
						(aString.equals("N") && aPC.deity() == null) ||
						(aPC.deity() != null && aPC.deity().name().equals(aString)));
					if (flag == true)
						break;
				}
			}
			else if (aType.equals("PREALIGN"))
			{
				flag = false;
				aTok = new StringTokenizer(aList, ",", false);
				while (aTok.hasMoreTokens())
				{
					int aInt = Integer.parseInt(aTok.nextToken());
					flag = (aInt == aPC.alignment);
					if (flag == true)
						break;
				}
			}
			else if (aType.equals("PREFORT"))
			{
				final int nextInt = Integer.parseInt(aList);
				int fort = aPC.getBonus(1, true) + aPC.adjStats(2) / 2 - 5;
				flag = (fort >= nextInt);
			}
			else if (aType.equals("PREWILL"))
			{
				final int nextInt = Integer.parseInt(aList);
				int will = aPC.getBonus(3, true) + aPC.adjStats(2) / 2 - 5;
				flag = (will >= nextInt);
			}
			else if (aType.equals("PREREFLEX"))
			{
				final int nextInt = Integer.parseInt(aList);
				int ref = aPC.getBonus(2, true) + aPC.adjStats(2) / 2 - 5;
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
				aTok = new StringTokenizer(aList, ",", false);
				number = Integer.parseInt(aTok.nextToken());
				while (aTok.hasMoreTokens() && number > 0)
				{
					String bString = aTok.nextToken();
					if (Globals.currentPC.getDomainNamed(bString) != null)
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
				StringTokenizer movereqs = new StringTokenizer(aList, ",");
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
					for (x = 0; x < aPC.race().movementTypes().length; x++)
						if (moveType.equals(aPC.race().movementType(x)) && aPC.race().movement(x).intValue() >= moveAmount)
						{
							flag = true;
							break;
						}
					if (!flag)
						break;
				}
			}
			else if (aType.equals("PREHANDSEQ"))
			{
				flag = aPC.race().hands() == Integer.parseInt(aList);
			}
			else if (aType.equals("PREHANDSLT"))
			{
				flag = aPC.race().hands() < Integer.parseInt(aList);
			}
			else if (aType.equals("PREHANDSLTEQ"))
			{
				flag = aPC.race().hands() <= Integer.parseInt(aList);
			}
			else if (aType.equals("PREHANDSGT"))
			{
				flag = aPC.race().hands() > Integer.parseInt(aList);
			}
			else if (aType.equals("PREHANDSGTEQ"))
			{
				flag = aPC.race().hands() >= Integer.parseInt(aList);
			}
			else if (aType.equals("PREHANDSNEQ"))
			{
				flag = aPC.race().hands() >= Integer.parseInt(aList);
			}
			else if (aType.equals("PREHANDSNEQ"))
			{
				flag = aPC.race().hands() != Integer.parseInt(aList);
			}
			if (Globals.debugMode)
				System.out.println(aType + ":" + aList + "=" + flag);
			if (flag == false)
				return flag;
		}
		return flag;

	}

///Creates the requirement string for printing.
	public String preReqStrings()
	{
		if (preReqArrayList.size() == 0)
			return ("");
		String pString = new String();
		for (Iterator e = preReqArrayList.iterator(); e.hasNext();)
		{
			StringTokenizer aTok = new StringTokenizer((String)e.next(), ":", false);
			String aType = aTok.nextToken();
			String aList = aTok.nextToken();
			Integer anInt = null;
			int i = 0;
			if (pString.length() > 0)
				pString = pString.concat("  ");
			if (aType.equals("PRECLASS"))
			{
				anInt = new Integer(aList.substring(aList.lastIndexOf('=') + 1));
				aTok = new StringTokenizer(aList, ",", false);
				pString = pString.concat("CLASS:");
				while (aTok.hasMoreTokens())
				{
					if (i++ > 0)
						pString = pString.concat(",");
					pString = pString.concat(aTok.nextToken());
				}
			}
			else if (aType.equals("PREATT"))
			{
				pString = pString.concat("ATT=");
				pString = pString.concat(aList);
			}
			else if (aType.equals("PREUATT"))
			{
				pString = pString.concat("UATT=");
				pString = pString.concat(aList);
			}
			else if (aType.equals("PRESTAT"))
			{
				pString = pString.concat("STAT:" + aType + "=" + aList);
			}
			else if (aType.equals("PREALIGN"))
			{
				aTok = new StringTokenizer(aList, ",", false);
				pString = pString.concat("Alignment:");
				while (aTok.hasMoreTokens())
				{
					if (i++ > 0)
						pString = pString.concat(",");
					int aInt = Integer.parseInt(aTok.nextToken());
					if (aInt == 0)
						pString = pString.concat("LG");
					else if (aInt == 1)
						pString = pString.concat("LN");
					else if (aInt == 2)
						pString = pString.concat("LE");
					else if (aInt == 3)
						pString = pString.concat("NG");
					else if (aInt == 4)
						pString = pString.concat("TN");
					else if (aInt == 5)
						pString = pString.concat("NE");
					else if (aInt == 6)
						pString = pString.concat("CG");
					else if (aInt == 7)
						pString = pString.concat("CN");
					else if (aInt == 8)
						pString = pString.concat("CE");
				}
			}
			else
			{
				pString = pString.concat(aType.substring(3) + ":");
				aTok = new StringTokenizer(aList, ",", false);
				while (aTok.hasMoreTokens())
				{
					if (i++ > 0)
						pString = pString.concat(",");
					pString = pString.concat(aTok.nextToken());
				}
			}
		}
		return pString;
	}

	/** Is this function empty intentionally? If so, it should be documented. */
	public void parseLine(String aString, File aSourceFile, int lineNum)
	{

	}
}
