/*
 * @(#) $Id: Feat.java,v 1.1 2006/02/21 00:05:26 vauchers Exp $
 *
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
import java.util.StringTokenizer;

/**
 * Definition and games rules for a Feat.
 *
 * @author ???
 * @version $Revision: 1.1 $
 */
public class Feat extends PObject
{
	///////////////////////////////////////
	// Static properties
	public static final int VISIBILITY_HIDDEN = 0;
	public static final int VISIBILITY_DEFAULT = 1;
	public static final int VISIBILITY_OUTPUT_ONLY = 2;
	public static final int VISIBILITY_DISPLAY_ONLY = 3;

	public static final int FEAT_NORMAL = 0;
	public static final int FEAT_AUTOMATIC = 1;
	public static final int FEAT_VIRTUAL = 2;

	///////////////////////////////////////
	// Fields - Attributes

	private Integer levelsPerRepIncrease = new Integer(0);

	private int featVisible = VISIBILITY_DEFAULT; // Hidden Feats

	private String description = new String();
	private boolean stacks = false;
	private boolean multiples = false;
	private String weaponProfType = null;
	private String addString = new String();
	private int addSpellLevel = 0;
	private double cost = 1.0;

	private int featType = FEAT_NORMAL;

	///////////////////////////////////////
	// Fields - Associations

	/* no associations */

	///////////////////////////////////////
	// Constructor

	/* default constructor only */

	///////////////////////////////////////
	// Methods - Accessors
	public Integer getLevelsPerRepIncrease()
	{
		return levelsPerRepIncrease;
	}

	/** for metamagic feats increase in spelllevel */
	public int getAddSpellLevel()
	{
		return addSpellLevel;
	}

	public double getCost()
	{
		return cost;
	}

	public String getDescription()
	{
		return description;
	}

	public boolean isMultiples()
	{
		return multiples;
	}

	public boolean isStacks()
	{
		return stacks;
	}

	public String getAddString()
	{
		return addString;
	}

	public String getWeaponProfType()
	{
		return weaponProfType;
	}

	///////////////////////////////////////
	// Methods - Mutator(s)

	public void setLevelsPerRepIncrease(Integer argLevelsPerRepIncrease)
	{
		levelsPerRepIncrease = argLevelsPerRepIncrease;
	}

	public void setAddSpellLevel(int argAddSpellLevel)
	{
		addSpellLevel = argAddSpellLevel;
	}

	public void setCost(double argCost)
	{
		cost = argCost;
	}

	public void setVisible(int argVisible)
	{
		featVisible = argVisible;
	}

	public void setWeaponProfType(String argWeaponProfType)
	{
		weaponProfType = argWeaponProfType;
	}


	///////////////////////////////////////
	// Methods - Other

	public Object clone()
	{
		Feat aFeat = (Feat)super.clone();
		aFeat.visible = visible;
		aFeat.description = getDescription();
		aFeat.multiples = isMultiples();
		aFeat.stacks = isStacks();
		aFeat.addString = getAddString();
		aFeat.levelsPerRepIncrease = levelsPerRepIncrease;
		aFeat.isSpecified = isSpecified;
		aFeat.addSpellLevel = addSpellLevel;
		aFeat.weaponProfType = weaponProfType;
		return aFeat;
	}

	public String toString()
	{
		return name;
	}

	/**
	 * Factory for CharacterFeat, which links a Feat to a
	 * particular PlayerCharacter. The CharacterFeat has a link back
	 * to the Feat it relates to.
	 */
	public CharacterFeat createCharacterFeat()
	{
		return new CharacterFeat(this);
	}

	public boolean canBeSelectedBy()
	{
		return passesPreReqTests();
	}

	/**
	 * Bypass normal prerequisite checks for feats if
	 * Globals.boolBypassFeatPreReqs is true.  Otherwise,
	 * use the prerequisite checks from the parent object.
	 */
	public boolean passesPreReqTestsForList(PlayerCharacter aPC, PObject aObj, ArrayList anArrayList)
	{
		return (SettingsHandler.isBoolBypassFeatPreReqs() || super.passesPreReqTestsForList(aPC, aObj, anArrayList));
	}

	/**
	 * Returns true if the feat matches the given type (the type is
	 * contained in the type string of the feat).
	 */
	public boolean matchesType(String featType)
	{
		return isType(featType);
	}

	public void modAdds(boolean addIt)
	{
		if (addString.length() == 0)
		{
			return;
		}

		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null)
		{
			return;
		}
		final StringTokenizer aTok = new StringTokenizer(addString, "|", false);
		if (aTok.countTokens() < 2)
		{
			Globals.debugPrint("Badly formed ADD. ", addString);
			return;
		}

		final String addType = new String(aTok.nextToken());
		final String addSec = new String(aTok.nextToken());
		if (addType.equals("WEAPONPROFS"))
		{
			aPC.setAutomaticFeatsStable(false);
		}
		else if (addType.equals("FAVOREDCLASS"))
		{
			if (addSec.equals("LIST"))
			{
				for (int e = 0; e < getAssociatedCount(); e++)
				{
					if (addIt)
					{
						aPC.addFavoredClass(getAssociated(e));
					}
					else
					{
						aPC.removeFavoredClass(getAssociated(e));
					}

				}
			}
			else
			{
				if (addIt)
				{
					aPC.addFavoredClass(addSec);
					while (aTok.countTokens() > 0)
					{
						aPC.addFavoredClass(aTok.nextToken());
					}
				}
				else
				{
					aPC.removeFavoredClass(addSec);
					while (aTok.countTokens() > 0)
					{
						aPC.removeFavoredClass(aTok.nextToken());
					}
				}
			}
		}
		else if (addType.equals("FORCEPOINT"))
		{
			if (featType != FEAT_AUTOMATIC)
			{
				int addPoints = 1;
				try
				{
					addPoints = Integer.parseInt(addSec);
				}
				catch (Exception exc)
				{
				}
				if (addIt)
				{
					int X = aPC.getRawFPoints() + addPoints;
					aPC.setFPoints(X);
				}
				else
				{
					int X = aPC.getRawFPoints() - addPoints;
					aPC.setFPoints(X);
				}
			}
		}
		// This code needs to be made to add an assortment of special abilities
		else if (addType.equals("SPECIAL"))
		// Takes a \ deliminated list of special abilities and lets you choose one to add.
		// BUG: currently adds 2 items from the list. --- arcady 10/12/2001
		{
			final ArrayList specialAbilityList = aPC.getSpecialAbilityList();
			if (addSec.equals("LIST"))
			{
				for (int e = 0; e < getAssociatedCount(); e++)
				{
					if (addIt)
					{
						final SpecialAbility sa = new SpecialAbility(getAssociated(e));
						specialAbilityList.add(sa);
						//aPC.getSpecialAbilityList().add(getAssociated(e));
					}
					else
					{
						final SpecialAbility sa = new SpecialAbility(getAssociated(e));
						specialAbilityList.remove(sa);
						//aPC.getSpecialAbilityList().remove(getAssociated(e));
					}

				}
			}
			else
			{
				if (addIt)
				{
					final SpecialAbility sa = new SpecialAbility(addSec);
					specialAbilityList.add(sa);
					//aPC.getSpecialAbilityList().add(addSec);
					while (aTok.countTokens() > 0)
					{
						final SpecialAbility sa2 = new SpecialAbility(aTok.nextToken());
						specialAbilityList.add(sa2);
						//aPC.getSpecialAbilityList().add(aTok.nextToken());
					}
				}
				else
				{
					final SpecialAbility sa = new SpecialAbility(addSec);
					specialAbilityList.remove(sa);
					//aPC.getSpecialAbilityList().remove(addSec);
					while (aTok.countTokens() > 0)
					{
						final SpecialAbility sa2 = new SpecialAbility(aTok.nextToken());
						specialAbilityList.remove(sa2);
						//aPC.getSpecialAbilityList().remove(aTok.nextToken());
					}
				}
			}
		}
	}

	public void setDescription(String newString)
	{
		description = newString;
	}

	public void setMultiples(String aString)
	{
		if (aString.startsWith("Y"))
			multiples = true;
		else
			multiples = false;
	}

	public void setStacks(String aString)
	{
		if (aString.startsWith("Y"))
			stacks = true;
		else
			stacks = false;
	}

	public void setAddString(String aString)
	{
		addString = aString;
	}

	public void setChoiceString(String aString)
	{
		choiceString = aString;
	}

	///////////////////////////////////////////////
	// move to CharacterFeat

	/**
	 * Name of the full Feat, plus any sub-choices made for this
	 * character. Starts with the name of the feat, and then
	 * (for types other than weapon proficiencies), either appends
	 * a count of the times the feat is applied e.g. " (3x)",
	 * or a list of the sub-choices e.g. " (Sub1, Sub2, ...)".
	 */
	public String qualifiedName()
	{
		// start with the name of the feat
		// don't do for Weapon Profs
		StringBuffer aStrBuf = new StringBuffer(name);
		if (getAssociatedCount() > 0 && !name.endsWith("Weapon Proficiency") && !name.startsWith("Armor Proficiency"))
		{
			// has a sub-detail
			aStrBuf.append(" (");
			int i = 0;
			if (getChoiceString().length() == 0 || (multiples && stacks))
			{
				// number of items only (ie stacking), e.g. " (1x)"
				aStrBuf.append(Integer.toString((int)(getAssociatedCount() * cost)));
				aStrBuf.append("x)");
			}
			else
			{
				// list of items in associatedList, e.g. " (Sub1, Sub2, ...)"
				for (int e = 0; e < getAssociatedCount(); e++)
				{
					if (i > 0)
						aStrBuf.append(", ");
					aStrBuf.append(getAssociated(e));
					i++;
				}
				aStrBuf.append(')');
			}
		}
		return aStrBuf.toString();
	}

	/**
	 * Enhanced containsAssociated, which parses the input
	 * parameter for "=", "+num" and "-num" to extract the value to look
	 * for.
	 */
	public int numberInList(String aType)
	{
		int iCount = 0;
		final String numString = "0123456789";
		if (aType.lastIndexOf("=") > -1)
			aType = aType.substring(aType.lastIndexOf("=") + 1);
		if (aType.lastIndexOf("+") > -1) // truncate at + sign if following character is a number
		{
			final String aString = aType.substring(aType.lastIndexOf("+") + 1);
			if (numString.lastIndexOf(aString.substring(0, 1)) > 0)
				aType = aType.substring(0, aType.lastIndexOf("+"));
		}
		if (aType.lastIndexOf("-") > -1) // truncate at - sign if following character is a number
		{
			final String aString = aType.substring(aType.lastIndexOf("-") + 1);
			if (numString.lastIndexOf(aString.substring(0, 1)) > 0)
				aType = aType.substring(0, aType.lastIndexOf("-"));
		}
		for (int i = 0; i < getAssociatedCount(); i++)
		{
			if (getAssociated(i).equalsIgnoreCase(aType))
			{
				iCount += 1;
			}
		}
		return iCount;
	}

	/**
	 * Opens a Chooser to allow sub-choices for this feat.
	 * The actual items allowed to choose from are based on
	 * choiceString, as applied to current character. Choices
	 * already made (getAssociatedList) are indicated in list B.
	 */

	public boolean modChoices(boolean addIt)
	{
		ArrayList availableList = new ArrayList(); // available list of choices
		ArrayList selectedList = new ArrayList(); // selected list of choices
		//modChoices(addIt, availableList, selectedList, true);
		return Utility.modChoices(this, availableList, selectedList, true);
	}

	public boolean modChoices(boolean addIt, ArrayList availableList, ArrayList selectedList, boolean process)
	{

		return Utility.modChoices(this, availableList, selectedList, process);
	}

	public int isVisible()
	{
		return featVisible;
	}

	public int getFeatType()
	{
		return featType;
	}

	public void setFeatType(int argFeatType)
	{
		// Sanity check
		switch (argFeatType)
		{
			case FEAT_NORMAL:
			case FEAT_AUTOMATIC:
			case FEAT_VIRTUAL:
				break;

			default:
				return;
		}
		featType = argFeatType;
	}
}
