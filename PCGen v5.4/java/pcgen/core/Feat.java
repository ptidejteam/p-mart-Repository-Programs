/*
 * Feat.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on April 21, 2001, 2:15 PM
 *
 * $Id: Feat.java,v 1.1 2006/02/21 01:18:39 vauchers Exp $
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import pcgen.gui.utils.GuiFacade;
import pcgen.util.Logging;

/**
 * Definition and games rules for a Feat.
 *
 * @author ???
 * @version $Revision: 1.1 $
 */
public final class Feat extends PObject implements HasCost
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

	private int levelsPerRepIncrease = 0;

	private int featVisible = VISIBILITY_DEFAULT; // Hidden Feats

	private boolean stacks = false;
	private boolean multiples = false;
	//private String weaponProfType = null;
	private String addString = "";
	private int addSpellLevel = 0;
	private double cost = 1.0;

	private int featType = FEAT_NORMAL;
	private boolean needsSaving = false;

	///////////////////////////////////////
	// Fields - Associations

	/* no associations */

	///////////////////////////////////////
	// Constructor

	/* default constructor only */

	///////////////////////////////////////
	// Methods - Accessors
	/** for metamagic feats increase in spelllevel */
	public int getAddSpellLevel()
	{
		return addSpellLevel;
	}

	public double getCost()
	{
		return cost;
	}

	public boolean isMultiples()
	{
		return multiples;
	}

	public boolean isStacks()
	{
		return stacks;
	}

	public boolean needsSaving()
	{
		return needsSaving;
	}

	public void setNeedsSaving(boolean arg)
	{
		needsSaving = arg;
	}

	String getAddString()
	{
		return addString;
	}

	public void setLevelsPerRepIncrease(Integer argLevelsPerRepIncrease)
	{
		levelsPerRepIncrease = argLevelsPerRepIncrease.intValue();
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

	//public void setWeaponProfType(String argWeaponProfType)
	//{
	//	weaponProfType = argWeaponProfType;
	//}


	///////////////////////////////////////
	// Methods - Other

	public Object clone()
	{
		Feat aFeat = null;
		try
		{
			aFeat = (Feat) super.clone();
			aFeat.visible = visible;
			aFeat.multiples = isMultiples();
			aFeat.stacks = isStacks();
			aFeat.addString = getAddString();
			aFeat.levelsPerRepIncrease = levelsPerRepIncrease;
			aFeat.isSpecified = isSpecified;
			aFeat.addSpellLevel = addSpellLevel;
			//aFeat.weaponProfType = weaponProfType;
		}
		catch (CloneNotSupportedException e)
		{
			GuiFacade.showMessageDialog(null, e.getMessage(), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
		}
		return aFeat;
	}

	boolean canBeSelectedBy()
	{
		return passesPreReqToGain();
	}

	/**
	 * Bypass normal prerequisite checks for feats if
	 * Globals.boolBypassFeatPreReqs is true.  Otherwise,
	 * use the prerequisite checks from the parent object.
	 */
	public boolean passesPreReqToGainForList(PlayerCharacter aPC, PObject aObj, List argList)
	{
		return (SettingsHandler.isBoolBypassFeatPreReqs() || super.passesPreReqToGainForList(aPC, aObj, argList));
	}

	/**
	 * Returns true if the feat matches the given type (the type is
	 * contained in the type string of the feat).
	 */
	boolean matchesType(String argFeatType)
	{
		return isType(argFeatType);
	}

	void modAdds(boolean addIt)
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
			Logging.errorPrint("Badly formed ADD. " + addString);
			return;
		}

		final String addType = aTok.nextToken();
		final String addSec = aTok.nextToken();
		if ("WEAPONPROFS".equals(addType))
		{
			aPC.setAutomaticFeatsStable(false);
		}
		else if ("FAVOREDCLASS".equals(addType))
		{
			if ("LIST".equals(addSec))
			{
				for (int e = 0; e < getAssociatedCount(); ++e)
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
		// This code needs to be made to add an assortment of special abilities
		else if ("SPECIAL".equals(addType))
		// Takes a \ deliminated list of special abilities and lets you choose one to add.
		// BUG: currently adds 2 items from the list. --- arcady 10/12/2001
		{
			final List saList = aPC.getSpecialAbilityList();
			if ("LIST".equals(addSec))
			{
				for (int e = 0; e < getAssociatedCount(); ++e)
				{
					if (addIt)
					{
						final SpecialAbility sa = new SpecialAbility(getAssociated(e));
						saList.add(sa);
						//aPC.getSpecialAbilityList().add(getAssociated(e));
					}
					else
					{
						final SpecialAbility sa = new SpecialAbility(getAssociated(e));
						saList.remove(sa);
						//aPC.getSpecialAbilityList().remove(getAssociated(e));
					}

				}
			}
			else
			{
				if (addIt)
				{
					final SpecialAbility sa = new SpecialAbility(addSec);
					saList.add(sa);
					while (aTok.countTokens() > 0)
					{
						final SpecialAbility sa2 = new SpecialAbility(aTok.nextToken());
						saList.add(sa2);
					}
				}
				else
				{
					final SpecialAbility sa = new SpecialAbility(addSec);
					saList.remove(sa);
					while (aTok.countTokens() > 0)
					{
						final SpecialAbility sa2 = new SpecialAbility(aTok.nextToken());
						saList.remove(sa2);
					}
				}
			}
		}
	}

	public void setMultiples(String aString)
	{
		multiples = aString.length() > 0 && aString.charAt(0) == 'Y';
	}

	public void setStacks(String aString)
	{
		stacks = aString.length() > 0 && aString.charAt(0) == 'Y';
	}

	public void setAddString(String aString)
	{
		addString = aString;
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
		final StringBuffer aStrBuf = new StringBuffer(name);
		if (getAssociatedCount() > 0 && !name.endsWith("Weapon Proficiency") && !name.startsWith("Armor Proficiency"))
		{
			// has a sub-detail
			aStrBuf.append(" (");
			int i = 0;
			if (getChoiceString().length() == 0 || (multiples && stacks))
			{
				// number of items only (ie stacking), e.g. " (1x)"
				aStrBuf.append((int) (getAssociatedCount() * cost));
				aStrBuf.append("x)");
			}
			else
			{
				// list of items in associatedList, e.g. " (Sub1, Sub2, ...)"
				for (int e = 0; e < getAssociatedCount(true); ++e)
				{
					if (i > 0)
					{
						aStrBuf.append(", ");
					}
					aStrBuf.append(getAssociated(e, true));
					++i;
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
	int numberInList(String aType)
	{
		int iCount = 0;
		final String numString = "0123456789";
		if (aType.lastIndexOf('=') > -1)
		{
			aType = aType.substring(aType.lastIndexOf('=') + 1);
		}
		if (aType.lastIndexOf('+') > -1) // truncate at + sign if following character is a number
		{
			final String aString = aType.substring(aType.lastIndexOf('+') + 1);
			if (numString.lastIndexOf(aString.substring(0, 1)) > 0)
			{
				aType = aType.substring(0, aType.lastIndexOf('+'));
			}
		}
		if (aType.lastIndexOf('-') > -1) // truncate at - sign if following character is a number
		{
			final String aString = aType.substring(aType.lastIndexOf('-') + 1);
			if (numString.lastIndexOf(aString.substring(0, 1)) > 0)
			{
				aType = aType.substring(0, aType.lastIndexOf('-'));
			}
		}
		for (int i = 0; i < getAssociatedCount(); ++i)
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

	boolean modChoices()
	{
		final List availableList = new ArrayList(); // available list of choices
		final List selectedList = new ArrayList(); // selected list of choices
		return modChoices(this, availableList, selectedList, true);
	}

	boolean modChoices(boolean addIt, List availableList, List selectedList, boolean process)
	{
		return modChoices(this, availableList, selectedList, process);
	}

	public int isVisible()
	{
		return featVisible;
	}

	public int getFeatType()
	{
		return featType;
	}

	void setFeatType(int argFeatType)
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

	//
	// Overridden from PObject
	//
	protected List addSpecialAbilitiesToList(List aList)
	{
		List specialAbilityList = getSpecialAbilityList();
		if (specialAbilityList != null)
		{
			StringBuffer sb = new StringBuffer();
			for (Iterator it = specialAbilityList.iterator(); it.hasNext();)
			{
				SpecialAbility sa = (SpecialAbility) it.next();
				final String aName = sa.getName();
				final int idx = aName.indexOf("%CHOICE");
				if (idx >= 0)
				{
					sb.setLength(0);
					sb.append(aName.substring(0, idx));
					if (getAssociatedCount() != 0)
					{
						for (int i = 0; i < getAssociatedCount(); ++i)
						{
							if (i != 0)
							{
								sb.append(" ,");
							}
							sb.append(getAssociated(i));
						}
					}
					else
					{
						sb.append("<undefined>");
					}
					sb.append(aName.substring(idx + 7));
					sa = new SpecialAbility(sb.toString(), sa.getSASource(), sa.getSADesc());
				}
				aList.add(sa);
			}
		}
		return aList;
	}

	public String getPCCText()
	{
		final StringBuffer txt = new StringBuffer(200);
		txt.append(getName());

		txt.append("\tCOST:").append(getCost());
		if (isMultiples())
		{
			txt.append("\tMULT:Y");
		}
		if (isStacks())
		{
			txt.append("\tSTACK:Y");
		}
		if (getAddSpellLevel() != 0)
		{
			txt.append("\tADDSPELLLEVEL:").append(getAddSpellLevel());
		}
		if (getAddString().length() != 0)
		{
			txt.append("\tADD:").append(getAddString());
		}
		txt.append("\tVISIBLE:");
		switch (isVisible())
		{
			case VISIBILITY_HIDDEN:
				txt.append("EXPORT");
				break;

			case VISIBILITY_OUTPUT_ONLY:
				txt.append("EXPORT");
				break;

			case VISIBILITY_DISPLAY_ONLY:
				txt.append("DISPLAY");
				break;

			case VISIBILITY_DEFAULT:
			default:
				txt.append("YES");
				break;
		}

		txt.append(super.getPCCText(false));
		return txt.toString();
	}
}
