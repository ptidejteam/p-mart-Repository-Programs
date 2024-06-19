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
 * $Id: LevelAbility.java,v 1.1 2006/02/21 01:18:39 vauchers Exp $
*/

package pcgen.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import pcgen.util.Logging;

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

	static LevelAbility createAbility(PObject aowner, int aLevel, String aString)
	{
		if (aString.startsWith("SPECIAL"))
		{
			return new LevelAbilitySpecial(aowner, aLevel, aString);
		}
		else if (aString.startsWith("FEAT"))
		{
			return new LevelAbilityFeat(aowner, aLevel, aString, false);
		}
		else if (aString.startsWith("VFEAT"))
		{
			return new LevelAbilityFeat(aowner, aLevel, aString, true);
		}
		else if (aString.startsWith("CLASSSKILLS"))
		{
			return new LevelAbilityClassSkills(aowner, aLevel, aString);
		}
		else if (aString.startsWith("WEAPONBONUS"))
		{
			return new LevelAbilityWeaponBonus(aowner, aLevel, aString);
		}
		else if (aString.startsWith("EQUIP"))
		{
			return new LevelAbilityEquipment(aowner, aLevel, aString);
		}
		else if (aString.startsWith("LIST"))
		{
			return new LevelAbilityList(aowner, aLevel, aString);
		}
		else if (aString.startsWith("Language"))
		{
			return new LevelAbilityLanguage(aowner, aLevel, aString);
		}
		else
		{
			return new LevelAbility(aowner, aLevel, aString);
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

	public final void process(List availableList)
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
			final List aBonusList = new ArrayList();
			final StringTokenizer cTok = new StringTokenizer(bString, "[]", false);
			final String choices = cTok.nextToken();
			while (cTok.hasMoreTokens())
			{
				aBonusList.add(cTok.nextToken());
			}
			getChoices(choices, aBonusList);
			type = 7;
		}
		else if (bString.startsWith("TYPE=") || bString.startsWith("TYPE."))
		{
			type = 8;
		}

		final pcgen.gui.utils.ChooserInterface c = pcgen.gui.utils.ChooserFactory.getChooserInstance();
		bString = prepareChooser(c);
		final List aArrayList = getChoicesList(bString);

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
					processChoice(aArrayList, c.getSelectedList(), eString);
				}
			}
		}
	}

	/**
	 * Performs the initial setup of a chooser.
	 */

	String prepareChooser(pcgen.gui.utils.ChooserInterface c)
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
			default:
				Logging.errorPrint("In LevelAbility.prepareChooser the type " + type + " is not supported.");
				break;
		}
		return bString;
	}

	/**
	 * Parses the comma-separated list of the ADD: field and returns the
	 * list of tokens to be shown in the chooser.
	 **/
	List getChoicesList(String bString)
	{
		final List aArrayList = new ArrayList(); // available
		if (type == 8) // Favored Enemy type listed
		{
			final String aString = bString.substring(5);
			final List races = new ArrayList(Globals.getRaceMap().values());
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
				{
					aArrayList.add(aClass.getName());
				}
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
			List preReqArray = new ArrayList();
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
			if (owner.passesPreReqToGainForList(preReqArray))
			{
				processToken(aString, aArrayList, bString);
			}
		}
		return aArrayList;
	}

	/**
	 * Processes a single token in the comma-separated list of the ADD:
	 * field and adds the choices to be shown in the list to aArrayList.
	 * @param aChoice
	 * @param aArrayList
	 * @param bString
	 */
	void processToken(String aChoice, List aArrayList, String bString)
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
			default:
				Logging.errorPrint("In LevelAbility.processToken the type " + type + " is not supported.");
				break;
		}
	}

	/**
	 * Process the choice selected by the user.
	 **/
	public void processChoice(List aArrayList, List selectedList, String aString)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		int index = 0;
		String sString;
        String zString;
		switch (type)
		{
			case 4:
				if (aText.equals("DOMAIN"))
				{
					int dnum = 0;
					int aLevel = -9;
					String domainSource = "";
					String oName = "";
					boolean fromClass = false;
					if (owner instanceof PCClass)
					{
						fromClass = true;
						oName = owner.getName();
						aLevel = ((PCClass) owner).getLevel();
						dnum = (int) ((PCClass) owner).getBonusTo("DOMAIN", "NUMBER", aLevel);
						// always assume level 1
						domainSource = "PCClass|" + owner.toString() + "|1";
					}
					if (aLevel > 0)
					{
						dnum -= (int) ((PCClass) owner).getBonusTo("DOMAIN", "NUMBER", ((PCClass) owner).getLevel() - 1);
					}
					else
					{
						dnum = (int) owner.bonusTo("DOMAIN", "NUMBER");
					}
					Iterator i = selectedList.iterator();
					while ((dnum > 0) && i.hasNext())
					{
						CharacterDomain aCD = new CharacterDomain();
						aCD.setObjectName(oName);
						aCD.setLevel(aLevel);
						aCD.setFromPCClass(fromClass);

						Domain aDom = Globals.getDomainNamed((String) i.next());
						aDom = (Domain) aDom.clone();
						aCD.setDomain(aDom);
						aCD.setDomainSource(domainSource);
						aPC.addCharacterDomain(aCD);
						aDom.setIsLocked(true);
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
						if (sString.startsWith("TYPE=") || sString.startsWith("TYPE.")) // must be a Favored Enemy type
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
							Logging.errorPrint("ERROR:Expected PC to have a class named " + cString);
							break;
						}
						aPC.incrementClassLevel(0, bClass);
					}

					owner.addBonusList("0|PCLEVEL|" + bClass.getKeyName() + "|1");
					//owner.addSave("BONUS|0|PCLEVEL|" + bClass.getKeyName() + "|1");
					// to force spellbook update
					// for divine spellcasters
					bClass.setLevel(bClass.getLevel());

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

			default:
				Logging.errorPrint("In LevelAbility.processChoice the type " + type + " is not supported.");
				break;

		}
	}

}
