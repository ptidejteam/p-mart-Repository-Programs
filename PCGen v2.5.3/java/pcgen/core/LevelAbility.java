/*
 * LevelAbility.java
 * Copyright 2001 (C) Dmitry Jemerov
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
 * Created on July 23, 2001, 8:30 PM
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import pcgen.gui.Chooser;

/**
 * Represents a single ability a character gets when gaining a level
 * (an ADD: entry in the LST file).
 *
 * @author Dmitry Jemerov <yole@spb.cityline.ru>
 * @version $Revision: 1.1 $
 */

public class LevelAbility extends PObject
{
	protected PCClass ownerClass;
	private int level;
	protected String list;
	private int type;
	private String aText;
	private String eString = "";

	LevelAbility(PCClass aOwnerClass, int aLevel, String aList)
	{
		ownerClass = aOwnerClass;
		level = aLevel;
		list = aList;
	}

	public int level()
	{
		return level;
	}

	public void setOwner(PCClass aOwnerClass)
	{
		ownerClass = aOwnerClass;
	}

	public String getList()
	{
		return new String(list);
	}

	/**
	 * Factory method for creating LevelAbility instances.
	 */

	public static LevelAbility createAbility(PCClass aOwnerClass, int aLevel, String aList)
	{
		if (aList.startsWith("SPECIAL"))
			return new LevelAbilitySpecial(aOwnerClass, aLevel, aList);
		else if (aList.startsWith("FEAT"))
			return new LevelAbilityFeat(aOwnerClass, aLevel, aList);
		else if (aList.startsWith("CLASSSKILLS"))
			return new LevelAbilityClassSkills(aOwnerClass, aLevel, aList);
		else if (aList.startsWith("WEAPONBONUS"))
			return new LevelAbilityWeaponBonus(aOwnerClass, aLevel, aList);
		else if (aList.startsWith("LIST"))
			return new LevelAbilityList(aOwnerClass, aLevel, aList);
		else if (aList.startsWith("Language"))
			return new LevelAbilityLanguage(aOwnerClass, aLevel, aList);
		else
			return new LevelAbility(aOwnerClass, aLevel, aList);
	}

	/**
	 * Executes the static effects of the ability when a character gets a
	 * level.
	 */

	public void addForLevel()
	{
		StringTokenizer aStrTok = new StringTokenizer(list, ",", false);
		final PlayerCharacter aPC = Globals.getCurrentPC();
		String thisString = null;
		while (aStrTok.hasMoreTokens())
		{
			thisString = (String)aStrTok.nextToken();
			if (thisString.equals("FEAT"))
			{
				aPC.setFeats(aPC.getFeats() + 1);
			}
			else if (thisString.startsWith("INIT|"))
			{
				ownerClass.addInitMod(Integer.parseInt(thisString.substring(5)));
			}
			else if (thisString.startsWith("FORCEPOINT"))
			{
				int x = Integer.parseInt(aPC.getFPoints());
				x += 1;
				aPC.setFPoints(String.valueOf(x));
			}
		}
	}

	/**
	 * Executes the static effects of the ability when a character loses a
	 * level.
	 */

	public void subForLevel()
	{
		StringTokenizer aStrTok = new StringTokenizer(list, ",", false);
		final PlayerCharacter aPC = Globals.getCurrentPC();
		String thisString = null;
		while (aStrTok.hasMoreTokens())
		{
			thisString = (String)aStrTok.nextToken();
			if (thisString.equals("FEAT"))
			{
				aPC.setFeats(aPC.getFeats() - 1);
			}
			else if (thisString.startsWith("INIT|"))
			{
				ownerClass.addInitMod(-Integer.parseInt(thisString.substring(5)));
			}
			else if (thisString.startsWith("FORCEPOINT"))
			{
				int X = Integer.parseInt(aPC.getFPoints());
				X -= 1;
				aPC.setFPoints(String.valueOf(X));
			}
		}
	}

	/**
	 * Checks if the process() method applies to the ability.
	 */

	public boolean canProcess()
	{
		StringTokenizer aTok = new StringTokenizer(list, "(", false);
		return aTok.countTokens() > 1;
	}

	/**
	 * Performs the processing necessary when a character gets a level,
	 * depending on the ability type.
	 */

	public void process()
	{
		String bString = list;
		aText = bString;
		type = 4;
		if (bString.startsWith("SKILL"))
			type = 2;
		else if (bString.startsWith("SPELLCASTER"))
		{
//			final String cString = bString.substring(bString.lastIndexOf("(")+1,bString.lastIndexOf(")"));
//			final PCClass aClass = Globals.getClassNamed(cString);
//			if (aClass!=null) {
//				bString=aClass.getName();
//			}
			type = 6;
		}
		else if (bString.startsWith("SPELLLEVEL"))
		{
			ArrayList aBonusList = new ArrayList();
			StringTokenizer cTok = new StringTokenizer(bString, "[]", false);
			String choices = cTok.nextToken();
			while (cTok.hasMoreTokens())
				aBonusList.add(cTok.nextToken());
			getChoices(choices, aBonusList);
			type = 7;
		}
		else if (bString.startsWith("TYPE="))
			type = 8;

		Chooser c = new Chooser();
		bString = prepareChooser(c);
		ArrayList aArrayList = getChoicesList(bString);
		c.setAvailableList(aArrayList);
		c.setVisible(false);
		if (aArrayList.size() > 0 && type != 7)
		{
			c.show();
			Globals.debugPrint("choice: " + c.getSelectedList() + eString);
			processChoice(aArrayList, c.getSelectedList(), eString);
		}
	}

	/**
	 * Performs the initial setup of a chooser.
	 */

	public String prepareChooser(Chooser c)
	{
		final int i = list.lastIndexOf(")");
		if ((i > -1) && (i < list.length() - 1))
		{
			try
			{
				c.setPool(Integer.parseInt(list.substring(i + 1)));
				list = list.substring(0, i + 1);
			}
			catch (NumberFormatException nfe)
			{
				Globals.debugErrorPrint("Integer.parseInt exception @" + i + " in:" + list + " (" + list.substring(i + 1) + ")", nfe);
				c.setPool(1);
			}
		}
		else
		{
			c.setPool(1);
		}
		String bString = list;
		switch (type)
		{
			case 2:
				c.setTitle("Skill Choice");
				bString = list.substring(6);
				break;
			case 4:
				int a = list.lastIndexOf('(');
				aText = list.substring(0, a);
				bString = list.substring(a + 1);
				c.setTitle(aText);
				break;
			case 6:
				c.setTitle("Spell Caster Class Choice");
				break;
			case 8:
				c.setTitle("Type Selection");
				break;
		}
		return bString;
	}

	/**
	 * Parses the comma-separated list of the ADD: field and returns the
	 * list of tokens to be shown in the chooser.
	 */

	public ArrayList getChoicesList(String bString)
	{
		ArrayList aArrayList = new ArrayList(); // available
		if (type == 8) // Favored Enemy type listed
		{
			final String aString = bString.substring(5);
			ArrayList races = new ArrayList(Globals.getRaceMap().values());
			for (Iterator e = races.iterator(); e.hasNext();)
			{
				final Race race = (Race)e.next();
				if (race.getType().equalsIgnoreCase(aString))
				{
					aArrayList.add(race.getName());
				}
			}
			for (Iterator e = Globals.getClassList().iterator(); e.hasNext();)
			{
				final PCClass aClass = (PCClass)e.next();
				if (aClass.isType(aString) && !aArrayList.contains(aClass.getName()))
					aArrayList.add(aClass.getName());
			}
			return aArrayList;
		}
		int iOpenParen = 0;
		int idx;
		for (idx = 0; idx < bString.length(); idx++)
		{
			switch (bString.charAt(idx))
			{
				case '(':
					iOpenParen += 1;
					break;
				case ')':
					iOpenParen -= 1;
					break;
			}
			if (iOpenParen < 0)
				break;
		}

		if (iOpenParen >= 0)
		{
			eString = "";
		}
		else
		{
			eString = bString.substring(idx + 1);
			eString = eString.replace('{', '(').replace('}', ')').replace('~', '|');
			bString = bString.substring(0, idx);
		}

		StringTokenizer aTok = new StringTokenizer(bString, ",", false);
		boolean flag = true;
		while (aTok.hasMoreTokens() && flag)
		{
			String aString = (String)aTok.nextToken();
			ArrayList preReqArray = new ArrayList();
			if (aString.lastIndexOf("<") > -1)
			{
				StringTokenizer bTok = new StringTokenizer(bString, "<>|", true);
				int len = 0;
				String pString = "";
				// cut out of loop on hitting second :
				while (bTok.hasMoreTokens() && !pString.equals(">"))
				{
					pString = bTok.nextToken();
					if (len == 0)
						aString = pString;
					if ((len > 0) && (pString.startsWith("PRE") || pString.startsWith("!PRE")))
						preReqArray.add(pString);
					len += pString.length();
				}
				if (len < bString.length())
				{
					bString = bString.substring(len + 1);
					aTok = new StringTokenizer(bString, ",", false);
				}
				else
					flag = false;
			}
			if (ownerClass.passesPreReqTestsForList(preReqArray))
				processToken(aString, aArrayList, bString);
		}
		return aArrayList;
	}

	/**
	 * Processes a single token in the comma-separated list of the ADD:
	 * field and adds the choices to be shown in the list to aArrayList.
	 */

	void processToken(String aChoice, ArrayList aArrayList, String bString)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		switch (type)
		{
			case 4:
				if (!aPC.hasSpecialAbility(aText + "(" + aChoice + ")"))
					aArrayList.add(aChoice);
				break;
			case 6:
				final String cString = bString.substring(bString.lastIndexOf("(") + 1, bString.lastIndexOf(")"));
				final PCClass aClass = Globals.getClassNamed(cString);
				if (aClass != null)
				{
					bString = aClass.getName();
				}
				PCClass bClass = null;
				for (Iterator e1 = aPC.getClassList().iterator(); e1.hasNext();)
				{
					bClass = (PCClass)e1.next();
					if (bClass.getKeyName().equals("Domain"))
						continue; // cannot include domain class
					// if the class has a valid spelltype and the class is not the owning class
					else if (!bClass.getSpellType().equals("") && !bClass.getKeyName().equals(ownerClass.getKeyName()))
					{
						// if the string is ANY or if the string matches the class' spell type
						if (bString.lastIndexOf("ANY") > -1 || (bString.lastIndexOf(bClass.getSpellType()) > -1))
						{
							aArrayList.add(bClass.getKeyName());
						}
					}
				}
				if (bString.startsWith("SPELLCASTER("))
					bString = bString.substring(12, bString.length() - 1);
				final StringTokenizer aTok = new StringTokenizer(bString, "|", false);
				while (aTok.hasMoreTokens())
				{
					final String aString = aTok.nextToken();
					if (aArrayList.contains(aString))
						continue;
					if (aString.startsWith("EXCLUDE=") && aArrayList.contains(aString.substring(8)))
					{
						aArrayList.remove(aString.substring(8));
					}
					bClass = Globals.getClassNamed(aString);
					if (bClass != null)
						aArrayList.add(bClass.getKeyName());
				}
				break;
		}
	}

	/**
	 * Process the choice selected by the user.
	 */

	void processChoice(ArrayList aArrayList, List selectedList, String aString)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		int index = 0;
		switch (type)
		{
			case 4:
				final String zText = aText + "(";
				for (index = 0; index < selectedList.size(); index++)
				{
					String sString = selectedList.get(index).toString();
					if (sString.startsWith("TYPE=")) // must be a Favored Enemy type
					{
						LevelAbility la = new LevelAbility(ownerClass, level, sString);
						la.process();
						sString = la.list;
					}
					String zString = new StringBuffer().append(zText).append(sString).append(")").append(aString).toString();
					if (!aPC.hasSpecialAbility(zString))
					{
						aPC.getSpecialAbilityList().add(zString);
						ownerClass.addSaveList(zString);
					}
				}
				break;
			case 6:
				if (selectedList.size() > 0)
				{
					final String cString = selectedList.get(index).toString();
					PCClass bClass = aPC.getClassNamed(cString);
					if (bClass == null)
					{
						bClass = Globals.getClassNamed(cString);
						if (bClass == null)
						{
							System.out.println("ERROR:Expected to find a class named " + cString);
							break;
						}
						aPC.incrementClassLevel(0, bClass);
						bClass = aPC.getClassNamed(cString);
						if (bClass == null)
						{
							System.out.println("ERROR:Expected PC to have a class named " + cString);
							break;
						}
					}
					aPC.getSpecialAbilityList().add("Bonus Caster Level for " + cString + aString);
					ownerClass.addBonusList(0 + "|PCLEVEL|" + bClass.getKeyName() + "|1");
					ownerClass.addSaveList("BONUS|" + 0 + "|PCLEVEL|" + bClass.getKeyName() + "|1");
					bClass.setLevel(bClass.getLevel()); // just to force spellbook update for divine spellcasters
					// assume that classes which gain Domains, but also have ADD:SPELLCASTER will actually
					// grant the domains to the selected class, so this should be handled here.
					int dnum = ownerClass.getBonusTo("DOMAIN", "NUMBER", ownerClass.getLevel().intValue());
					if (ownerClass.getLevel().intValue() > 0)
						dnum -= ownerClass.getBonusTo("DOMAIN", "NUMBER", ownerClass.getLevel().intValue() - 1);
					final String domainSource = "PCClass|" + cString + "|1"; // always assume level 1 for this case (merton_monk@yahoo.com)
					final ArrayList pcCharacterDomainList = aPC.getCharacterDomainList();
					while (dnum > 0)
					{
						CharacterDomain aCD = new CharacterDomain();
						aCD.setDomainSource(domainSource);
						pcCharacterDomainList.add(aCD);
						dnum--;
					}
				}
				break;
			case 8:
				if (selectedList.size() > 0)
				{
					final String cString = selectedList.get(index).toString();
					list = cString;
				}
				break;
		}
	}

}
