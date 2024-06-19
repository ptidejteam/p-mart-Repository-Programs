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
 *
 * $Id: LevelAbility.java,v 1.1 2006/02/21 01:00:27 vauchers Exp $
*/

package pcgen.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import pcgen.gui.ChooserFactory;
import pcgen.gui.ChooserInterface;

/**
 * Represents a single ability a character gets when gaining a level
 * (an ADD: entry in the LST file).
 *
 * @author Dmitry Jemerov <yole@spb.cityline.ru>
 * @version $Revision: 1.1 $
 */

public class LevelAbility extends PObject
{
	protected PObject owner;
	private int level;
	protected String list;
	private int type;
	private String aText;
	private String eString = "";

	LevelAbility(PObject aOwner, int aLevel, String aList)
	{
		owner = aOwner;
		level = aLevel;
		list = aList;
	}

	public final int level()
	{
		return level;
	}

	final void setOwner(PObject aOwner)
	{
		owner = aOwner;
	}

	public final String getList()
	{
		return list;
	}

	/**
	 * Factory method for creating LevelAbility instances.
	 */

	static LevelAbility createAbility(PObject aowner, int aLevel, String aList)
	{
		if (aList.startsWith("SPECIAL"))
		{
			return new LevelAbilitySpecial(aowner, aLevel, aList);
		}
		else if (aList.startsWith("FEAT"))
		{
			return new LevelAbilityFeat(aowner, aLevel, aList);
		}
		else if (aList.startsWith("CLASSSKILLS"))
		{
			return new LevelAbilityClassSkills(aowner, aLevel, aList);
		}
		else if (aList.startsWith("WEAPONBONUS"))
		{
			return new LevelAbilityWeaponBonus(aowner, aLevel, aList);
		}
		else if (aList.startsWith("LIST"))
		{
			return new LevelAbilityList(aowner, aLevel, aList);
		}
		else if (aList.startsWith("Language"))
		{
			return new LevelAbilityLanguage(aowner, aLevel, aList);
		}
		else
		{
			return new LevelAbility(aowner, aLevel, aList);
		}
	}

	/**
	 * Executes the static effects of the ability when a character gets a
	 * level.
	 */

	final void addForLevel()
	{
		final StringTokenizer aStrTok = new StringTokenizer(list, ",", false);
		final PlayerCharacter aPC = Globals.getCurrentPC();
		String thisString;
		while (aStrTok.hasMoreTokens())
		{
			thisString = aStrTok.nextToken();
			if ("FEAT".equals(thisString))
			{
				aPC.setFeats(aPC.getFeats() + 1);
			}
			else if (thisString.startsWith("INIT|"))
			{
				if (owner instanceof PCClass)
				{
// is this necessary? is initMod even used?
					((PCClass) owner).addInitMod(Integer.parseInt(thisString.substring(5)));
				}
			}
			else if (thisString.startsWith("FORCEPOINT"))
			{
				int x = aPC.getRawFPoints() + 1;
				aPC.setFPoints(x);
			}
		}
	}

	/**
	 * Executes the static effects of the ability when a character loses a
	 * level.
	 */

	final void subForLevel()
	{
		final StringTokenizer aStrTok = new StringTokenizer(list, ",", false);
		final PlayerCharacter aPC = Globals.getCurrentPC();
		String thisString;
		while (aStrTok.hasMoreTokens())
		{
			thisString = aStrTok.nextToken();
			if ("FEAT".equals(thisString))
			{
				aPC.setFeats(aPC.getFeats() - 1);
			}
			else if (thisString.startsWith("INIT|"))
			{
				if (owner instanceof PCClass)
				{
// is this necessary? is initMod even used?
					((PCClass) owner).addInitMod(-Integer.parseInt(thisString.substring(5)));
				}
			}
			else if (thisString.startsWith("FORCEPOINT"))
			{
				int X = aPC.getRawFPoints() - 1;
				aPC.setFPoints(X);
			}
		}
	}

	/**
	 * Checks if the process() method applies to the ability.
	 */

	final boolean canProcess()
	{
		final StringTokenizer aTok = new StringTokenizer(list, "(", false);
		return aTok.countTokens() > 1;
	}

	/**
	 * Performs the processing necessary when a character gets a level,
	 * depending on the ability type.
	 */

	public final void process()
	{
		process(null);
	}

	public final void process(ArrayList availableList)
	{
		String bString = list;
		aText = bString;
		type = 4;
		if (bString.startsWith("SKILL"))
		{
			type = 2;
		}
		else if (bString.startsWith("SPELLCASTER"))
		{
			type = 6;
		}
		else if (bString.startsWith("SPELLLEVEL"))
		{
			final ArrayList aBonusList = new ArrayList();
			final StringTokenizer cTok = new StringTokenizer(bString, "[]", false);
			final String choices = cTok.nextToken();
			while (cTok.hasMoreTokens())
			{
				aBonusList.add(cTok.nextToken());
			}
			getChoices(choices, aBonusList);
			type = 7;
		}
		else if (bString.startsWith("TYPE="))
		{
			type = 8;
		}
		else if (bString.startsWith("VFEAT"))
		{
			type = 9;
		}

		final ChooserInterface c = ChooserFactory.getChooserInstance();
		bString = prepareChooser(c);
		final ArrayList aArrayList = getChoicesList(bString);

		if (availableList != null)
		{
			availableList.addAll(aArrayList);
		}
		else
		{
			if (c.getPool() == Integer.MIN_VALUE)
			{
				processChoice(aArrayList, aArrayList, eString);
			}
			else
			{
				c.setAvailableList(aArrayList);
				c.setVisible(false);

				if ((aArrayList.size() > 0) && (type != 7))
				{
					c.show();
					if (Globals.isDebugMode())
					{
						Globals.debugPrint("choice: " + c.getSelectedList() + eString);
					}
					processChoice(aArrayList, c.getSelectedList(), eString);
				}
			}
		}
	}

	/**
	 * Performs the initial setup of a chooser.
	 */

	String prepareChooser(ChooserInterface c)
	{
		final int i = list.lastIndexOf(')');
		if ((i >= 0) && (i < (list.length() - 1)))
		{
			try
			{
				c.setPool(Integer.parseInt(list.substring(i + 1)));
				list = list.substring(0, i + 1);
			}
			catch (NumberFormatException nfe)
			{
				//Globals.errorPrint("Integer.parseInt exception @" + i + " in:" + list + " (" + list.substring(i + 1) + ")", nfe);
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
			case 9:
				a = list.indexOf('(');
				aText = list.substring(0, a);
				bString = list.substring(a + 1);
				c.setTitle("Virtual Feat Selection");
				break;
			default:
				Globals.errorPrint("In LevelAbility.prepareChooser the type " + type + " is not supported.");
				break;
		}
		return bString;
	}

	/**
	 * Parses the comma-separated list of the ADD: field and returns the
	 * list of tokens to be shown in the chooser.
	 */

	ArrayList getChoicesList(String bString)
	{
		final ArrayList aArrayList = new ArrayList(); // available
		if (type == 8) // Favored Enemy type listed
		{
			final String aString = bString.substring(5);
			final ArrayList races = new ArrayList(Globals.getRaceMap().values());
			for (Iterator e = races.iterator(); e.hasNext();)
			{
				final Race race = (Race) e.next();
				if (race.getType().equalsIgnoreCase(aString))
				{
					aArrayList.add(race.getName());
				}
			}
			for (Iterator e = Globals.getClassList().iterator(); e.hasNext();)
			{
				final PCClass aClass = (PCClass) e.next();
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
				default :
			}
			if (iOpenParen < 0)
			{
				break;
			}
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
			String aString = aTok.nextToken();
			ArrayList preReqArray = new ArrayList();
			if (aString.lastIndexOf('<') > -1)
			{
				StringTokenizer bTok = new StringTokenizer(bString, "<>|", true);
				int len = 0;
				String pString = "";
				// cut out of loop on hitting second :
				while (bTok.hasMoreTokens() && !(">").equals(pString))
				{
					pString = bTok.nextToken();
					if (len == 0)
					{
						aString = pString;
					}
					if ((len > 0) && (pString.startsWith("PRE") || pString.startsWith("!PRE")))
					{
						preReqArray.add(pString);
					}
					len += pString.length();
				}
				if (len < bString.length())
				{
					bString = bString.substring(len + 1);
					aTok = new StringTokenizer(bString, ",", false);
				}
				else
				{
					flag = false;
				}
			}
			if (owner.passesPreReqTestsForList(preReqArray))
			{
				processToken(aString, aArrayList, bString);
			}
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
				if (!aPC.hasSpecialAbility(aText + '(' + aChoice + ')'))
				{
					aArrayList.add(aChoice);
				}
				break;
			case 6:
				final String cString = bString.substring(bString.lastIndexOf('(') + 1, bString.lastIndexOf(')'));
				final PCClass aClass = Globals.getClassNamed(cString);
				if (aClass != null)
				{
					bString = aClass.getName();
				}
				PCClass bClass;
				for (Iterator e1 = aPC.getClassList().iterator(); e1.hasNext();)
				{
					bClass = (PCClass) e1.next();
					if ("Domain".equals(bClass.getKeyName()))
					{
						continue; // cannot include domain class
					}
					// if the class has a valid spelltype and the class is not the owning class
					else if (!"".equals(bClass.getSpellType()) && !(Constants.s_NONE.equals(bClass.getSpellType())) && !bClass.getKeyName().equals(owner.getKeyName()))
					{
						// if the string is ANY or if the string matches the class' spell type
						if (bString.lastIndexOf("ANY") > -1 || (bString.lastIndexOf(bClass.getSpellType()) > -1))
						{
							aArrayList.add(bClass.getKeyName());
						}
					}
				}
				if (bString.startsWith("SPELLCASTER("))
				{
					bString = bString.substring(12, bString.length() - 1);
				}
				StringTokenizer aTok = new StringTokenizer(bString, "|", false);
				while (aTok.hasMoreTokens())
				{
					final String aString = aTok.nextToken();
					if (aArrayList.contains(aString))
					{
						continue;
					}
					if (aString.startsWith("EXCLUDE=") && aArrayList.contains(aString.substring(8)))
					{
						aArrayList.remove(aString.substring(8));
					}
					bClass = Globals.getClassNamed(aString);
					if (bClass != null)
					{
						aArrayList.add(bClass.getKeyName());
					}
				}
				break;
			case 9:
				aTok = new StringTokenizer(bString, "|", false);
				while (aTok.hasMoreTokens())
				{
					final String aString = aTok.nextToken();
					if (!aArrayList.contains(aString))
					{
						aArrayList.add(aString);
					}
				}
				break;
			default:
				Globals.errorPrint("In LevelAbility.processToken the type " + type + " is not supported.");
				break;
		}
	}

	/**
	 * Process the choice selected by the user.
	 */

	public void processChoice(ArrayList aArrayList, List selectedList, String aString)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		int index = 0;
		String sString, zString;
		switch (type)
		{
			case 4:
				if (aText.equals("DOMAIN"))
				{
					int dnum = 0;
					int aLevel = -9;
					String domainSource = "";
					if (owner instanceof PCClass)
					{
						aLevel = ((PCClass) owner).getLevel();
						dnum = (int) ((PCClass) owner).getBonusTo("DOMAIN", "NUMBER", aLevel);
						domainSource = "PCClass|" + owner.toString() + "|1"; // always assume level 1 for this case (merton_monk@yahoo.com)
					}
					if (aLevel > 0)
					{
						dnum -= (int) ((PCClass) owner).getBonusTo("DOMAIN", "NUMBER", ((PCClass) owner).getLevel() - 1);
					}
					else
					{
						dnum = (int) owner.bonusTo("DOMAIN", "NUMBER");
					}
					final ArrayList pcCharacterDomainList = aPC.getCharacterDomainList();
					Iterator i = selectedList.iterator();
					while (dnum > 0 && i.hasNext())
					{
						CharacterDomain aCD = new CharacterDomain();
						aCD.setDomainSource(domainSource);
						pcCharacterDomainList.add(aCD);
						Domain aD = Globals.getDomainNamed((String) i.next());
						aCD.setDomain(aD);
						--dnum;
					}
				}
				else
				{
					final String zText = aText + '(';
					final int listSize = selectedList.size();
					for (index = 0; index < listSize; ++index)
					{
						sString = selectedList.get(index).toString();
						if (sString.startsWith("TYPE=")) // must be a Favored Enemy type
						{
							LevelAbility la = new LevelAbility(owner, level, sString);
							la.process();
							sString = la.list;
						}
						zString = new StringBuffer().append(zText).append(sString).append(')').append(aString).toString();
						SpecialAbility sa = new SpecialAbility(zString, "PCCLASS|" + owner.getName() + '|' + level);
						owner.addSpecialAbilityToList(sa);
						owner.addSave(zString);
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
							Globals.errorPrint("ERROR:Expected to find a class named " + cString);
							break;
						}
						aPC.incrementClassLevel(0, bClass);
						bClass = aPC.getClassNamed(cString);
						if (bClass == null)
						{
							Globals.errorPrint("ERROR:Expected PC to have a class named " + cString);
							break;
						}
					}

					//The following line is unused. TODO Check if it can be removed safely. - it can it is a local variable
					//final SpecialAbility sa = new SpecialAbility("Bonus Caster Level for " + cString + aString, "PCCLASS|" + owner.getName() + "|" + level);

					owner.addBonusList(0 + "|PCLEVEL|" + bClass.getKeyName() + "|1");
//					owner.addSave("BONUS|" + 0 + "|PCLEVEL|" + bClass.getKeyName() + "|1");
					bClass.setLevel(bClass.getLevel()); // just to force spellbook update for divine spellcasters
					// assume that classes which gain Domains, but also have ADD:SPELLCASTER will actually
					// grant the domains to the selected class, so this should be handled here.
					int dnum = 0;
					int aLevel = -9;
					if (owner instanceof PCClass)
					{
						aLevel = ((PCClass) owner).getLevel();
						dnum = (int) ((PCClass) owner).getBonusTo("DOMAIN", "NUMBER", aLevel);
					}
					if (aLevel > 0)
					{
						dnum -= (int) ((PCClass) owner).getBonusTo("DOMAIN", "NUMBER", ((PCClass) owner).getLevel() - 1);
					}
					final String domainSource = "PCClass|" + cString + "|1"; // always assume level 1 for this case (merton_monk@yahoo.com)
					final ArrayList pcCharacterDomainList = aPC.getCharacterDomainList();
					while (dnum > 0)
					{
						CharacterDomain aCD = new CharacterDomain();
						aCD.setDomainSource(domainSource);
						pcCharacterDomainList.add(aCD);
						--dnum;
					}
					addAllToAssociated(selectedList);
				}
				break;

			case 8:
				if (selectedList.size() > 0)
				{
					final String cString = selectedList.get(index).toString();
					list = cString;
				}
				break;

			case 9:
				if (selectedList.size() > 0)
				{					
					final String cString = selectedList.get(index).toString();
					list = cString;
					Feat aFeat = Globals.getFeatKeyed(cString);
					if (aFeat!=null)
					{
						aPC.addVirtualFeat(aFeat.getName(), aPC.vFeatList());
						aFeat = aPC.getFeatNamedInList(aPC.vFeatList(), aFeat.getName());
						if (aFeat.isMultiples())
						{
							int x = aPC.getFeats();
							aPC.setFeats(1); // temporarily assume 1 choice
							aFeat.modChoices();
							aPC.setFeats(x); // reset to original count
						}
						aFeat.setNeedsSaving(true);
					}
				}
				break;

			default:
				Globals.errorPrint("In LevelAbility.processChoice the type " + type + " is not supported.");
				break;

		}
	}

}
