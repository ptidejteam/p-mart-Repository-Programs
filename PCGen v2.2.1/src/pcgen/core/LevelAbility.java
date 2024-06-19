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
 */

public class LevelAbility extends PObject
{
	protected PCClass ownerClass;
	private int level;
	protected String list;
	private int type;
	private String aText;

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
				int X = Integer.parseInt(aPC.getFPoints());
				X += 1;
				aPC.setFPoints(String.valueOf(X));
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
			type = 6;
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


		Chooser c = new Chooser();
		bString = prepareChooser(c);
		ArrayList aArrayList = getChoicesList(bString);
		c.setAvailableList(aArrayList);
		c.setVisible(false);
		if (aArrayList.size() > 0 && type != 7)
		{
			c.show();
			processChoice(aArrayList, c.getSelectedList());
		}
	}

	/**
	 * Performs the initial setup of a chooser.
	 */

	public String prepareChooser(Chooser c)
	{
		c.setPool(1);
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
		}
		return bString;
	}

	/**
	 * Parses the comma-separated list of the ADD: field and returns the
	 * list of tokens to be shown in the chooser.
	 */

	public ArrayList getChoicesList(String bString)
	{
		if (bString.endsWith(")"))
			bString = bString.substring(0, bString.length() - 1);
		StringTokenizer aTok = new StringTokenizer(bString, ",", false);
		ArrayList aArrayList = new ArrayList(); // available
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
					if (len > 0 && pString.startsWith("PRE"))
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
				PCClass bClass = null;
				for (Iterator e1 = aPC.getClassList().iterator(); e1.hasNext();)
				{
					bClass = (PCClass)e1.next();
					if (bClass.getKeyName().equals("Domain"))
						continue; // cannot include domain class
					if (!bClass.getSpellType().equals("") && !bClass.getKeyName().equals(ownerClass.getKeyName()))
					{
						if (bString.lastIndexOf("ANY") > -1 || (bString.lastIndexOf(bClass.getSpellType()) > -1))
							aArrayList.add(bClass.getKeyName());
					}
				}
				break;
		}
	}

	/**
	 * Process the choice selected by the user.
	 */

	void processChoice(ArrayList aArrayList, List selectedList)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		int index = 0;
		switch (type)
		{
			case 4:
				final String zText = aText + "(";
				for (index = 0; index < selectedList.size(); index++)
				{
					final String zString = new StringBuffer().append(zText).append(selectedList.get(index)).append(")").toString();
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
					final PCClass bClass = aPC.getClassNamed(cString);
					aPC.getSpecialAbilityList().add("Bonus Caster Level for " + cString);
					ownerClass.addBonusList(new Integer(0).toString() + "|PCLEVEL|" + bClass.getKeyName() + "|1");
					ownerClass.addSaveList("BONUS|" + new Integer(0).toString() + "|PCLEVEL|" + bClass.getKeyName() + "|1");
					bClass.setLevel(bClass.getLevel()); // just to force spellbook update for divine spellcasters
				}
				break;
		}
	}

}
