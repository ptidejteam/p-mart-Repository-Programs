/*
 * GameMode.java
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on September 22, 2002, 4:30 PM
 *
 * $Id: GameMode.java,v 1.1 2006/02/21 01:00:27 vauchers Exp $
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Handles game modes.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */

public final class GameMode implements Comparable
{
	private boolean showTabDomains = true;
	private boolean showTabFeats = true;
	private boolean showTabSpells = true;
	private boolean showClassDefense = false;

	private String alignmentName = "";
	private String currencyUnit = "Gold";
	private String currencyUnitAbbrev = "gp";
	private String damageResistance = "";
	private String defaultSpellBook = "Known Spells";
	private String defenseName = "";
	private String defenseAbbrev = "";
	private String heightUnitAbbrev = "in";
	private String hpAbbrev = "HP";
	private String hpName = "Hit Points";
	private String levelDownMessage = "";
	private String levelUpMessage = "";
	private String menuEntry = "";
	private String menuToolTip = "";
	private String moveUnit = "feet";
	private String moveUnitAbbrev = "'";
	private String name = "";
	private String reputationName = "";
	private String weaponTypes = "";
	private String weaponCategories = "";
	private String weightUnitAbbrev = "lbs";
	private String woundPointsAbbrev = "";
	private String woundPointsName = "";

	private ArrayList allowedModes = null;
	private ArrayList bonusFeatLevels = new ArrayList();
	private ArrayList bonusStatLevels = new ArrayList();
	private ArrayList defaultDeityList = new ArrayList();
	private ArrayList loadStrings = new ArrayList();
	private ArrayList skillMultiplierLevels = new ArrayList();

	private int displayOrder = Integer.MAX_VALUE;
	/** no default distance for short range */
	private int shortRangeDistance = 0;

	/** Creates a new instance of GameMode */
	public GameMode(String modeName)
	{
		name = modeName;
	}

	public int compareTo(Object obj)
	{
		if (obj != null)
		{
			final int iOrder = ((GameMode) obj).getDisplayOrder();
			if (iOrder < displayOrder)
			{
				return 1;
			}
			else if (iOrder > displayOrder)
			{
				return -1;
			}
			//
			// Order matches, so put in alphabetical order
			//

			//this should throw a ClassCastException for non-PObjects, like the Comparable interface calls for
			return this.name.compareTo(((GameMode) obj).name);
		}
		else
		{
			return 1;
		}
	}

	public void setAllowedModes(final String argAllowedModes)
	{
		final StringTokenizer aTok = new StringTokenizer(argAllowedModes, "|", false);
		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken();
			if (allowedModes == null)
			{
				allowedModes = new ArrayList();
			}
			allowedModes.add(aString);
		}
	}

	ArrayList getAllowedModes()
	{
		if (allowedModes == null)
		{
			ArrayList modes = new ArrayList(1);
			modes.add(name);
			return modes;
		}
		return allowedModes;
	}

	public String getMenuEntry()
	{
		if (menuEntry == null)
		{
			return name;
		}
		return menuEntry;
	}

	public String getName()
	{
		return name;
	}

	public void setModeName(String aString)
	{
		menuEntry = aString;
	}

	public String getMenuToolTip()
	{
		if (menuToolTip == null)
		{
			return "";
		}
		return menuToolTip;
	}

	public void setModeToolTip(String aString)
	{
		menuToolTip = aString;
	}

	ArrayList getSkillMultiplierLevels()
	{
		return skillMultiplierLevels;
	}

	public void setSkillMultiplierLevels(String pipeList)
	{
		final StringTokenizer aTok = new StringTokenizer(pipeList, "|", false);
		skillMultiplierLevels.clear();
		while (aTok.hasMoreTokens())
		{
			skillMultiplierLevels.add(aTok.nextToken());
		}
	}

	/**
	 * Levels at which all characters get bonus feats
	 */
	ArrayList getBonusFeatLevels()
	{
		return bonusFeatLevels;
	}

	public void setBonusFeatLevels(String aString)
	{
		bonusFeatLevels.add(aString);
	}

	/**
	 * Levels at which all characters get bonus to stats
	 */
	ArrayList getBonusStatLevels()
	{
		return bonusStatLevels;
	}

	public void setBonusStatLevels(String aString)
	{
		bonusStatLevels.add(aString);
	}

	/**
	 * show spell tab
	 */
	boolean getShowSpellTab()
	{
		return showTabSpells;
	}

	public void setShowSpellTab(boolean argShowTab)
	{
		showTabSpells = argShowTab;
	}

	boolean getShowFeatTab()
	{
		return showTabFeats;
	}

	public void setShowFeatTab(boolean argShowTab)
	{
		showTabFeats = argShowTab;
	}

	/**
	 * show domain tab
	 */
	boolean getShowDomainTab()
	{
		return showTabDomains;
	}

	public void setShowDomainTab(boolean argShowTab)
	{
		showTabDomains = argShowTab;
	}

	ArrayList getDeityList()
	{
		return defaultDeityList;
	}

	public void addDeityList(String argDeityLine)
	{
		if (".CLEAR".equals(argDeityLine))
		{
			defaultDeityList = null;
			return;
		}

		if (defaultDeityList == null)
		{
			defaultDeityList = new ArrayList();
		}
		defaultDeityList.add(argDeityLine);
	}

	/**
	 * Wound Points
	 */
	String getWoundPointsText()
	{
		return woundPointsName;
	}

	public void setWoundPointsText(String aString)
	{
		woundPointsName = aString;
	}

	String getWoundPointsAbbrev()
	{
		return woundPointsAbbrev;
	}

	public void setWoundPointsAbbrev(String aString)
	{
		woundPointsAbbrev = aString;
	}

	/**
	 * Reputation
	 */
	String getReputationText()
	{
		return reputationName;
	}

	public void setReputationText(String aString)
	{
		reputationName = aString;
	}

	/**
	 * HP
	 */
	String getHPAbbrev()
	{
		return hpAbbrev;
	}

	public void setHPAbbrev(String aString)
	{
		hpAbbrev = aString;
	}

	String getHPText()
	{
		return hpName;
	}

	public void setHPText(String aString)
	{
		hpName = aString;
	}

	/**
	 * Defense
	 */
	String getDefenseText()
	{
		return defenseName;
	}

	public void setDefenseText(String aString)
	{
		defenseName = aString;
	}

	String getDefenseAbbrev()
	{
		return defenseAbbrev;
	}

	public void setDefenseAbbrev(String aString)
	{
		defenseAbbrev = aString;
	}

	boolean getShowClassDefense()
	{
		return showClassDefense;
	}

	public void setShowClassDefense(boolean argShowDef)
	{
		showClassDefense = argShowDef;
	}

	/*
	 * Damage Resistance
	 */
	String getDamageResistanceText()
	{
		return damageResistance;
	}

	public void setDamageResistanceText(String aString)
	{
		damageResistance = aString;
	}

	/**
	 * Alignment
	 */
	String getAlignmentText()
	{
		return alignmentName;
	}

	public void setAlignmentText(String aString)
	{
		alignmentName = aString;
	}

	public void setLevelUpMessage(final String argLevelUpMessage)
	{
		levelUpMessage = argLevelUpMessage;
	}

	public void setLevelDownMessage(final String argLevelDownMessage)
	{
		levelDownMessage = argLevelDownMessage;
	}

	public void setMoveUnit(String aString)
	{
		moveUnit = aString;
	}

	/**
	 * Movement abbreviation
	 */
	String getAbbrMovementDisplay()
	{
		return moveUnitAbbrev;
	}

	public void setMoveUnitAbbrev(String aString)
	{
		moveUnitAbbrev = aString;
	}

	/**
	 * Weight abbreviation
	 */
	String getWeightDisplay()
	{
		return weightUnitAbbrev;
	}

	public void setWeightUnitAbbrev(String aString)
	{
		weightUnitAbbrev = aString;
	}

	/**
	 * Height abbreviation
	 */
	String getHeightDisplay()
	{
		return heightUnitAbbrev;
	}

	public void setHeightAbbrev(String aString)
	{
		heightUnitAbbrev = aString;
	}

	/**
	 * Currency unit
	 */
	String getLongCurrencyDisplay()
	{
		return currencyUnit;
	}

	public void setCurrencyUnit(String aString)
	{
		currencyUnit = aString;
	}

	/**
	 * Currency abbreviation
	 */
	String getCurrencyDisplay()
	{
		return currencyUnitAbbrev;
	}

	public void setCurrencyUnitAbbrev(String aString)
	{
		currencyUnitAbbrev = aString;
	}

	/**
	 * Default spell book name
	 */
	String getDefaultSpellBook()
	{
		return defaultSpellBook;
	}

	public void setDefaultSpellBook(String aString)
	{
		defaultSpellBook = aString;
	}

	public void addLoadString(String aString)
	{
		loadStrings.add(aString);
	}

	ArrayList getLoadStrings()
	{
		return loadStrings;
	}

	public void addWeaponType(String aString)
	{
		weaponTypes += '|' + aString;
	}

	public String getWeaponTypes()
	{
		return weaponTypes;
	}

	public void addWeaponCategory(String aString)
	{
		weaponCategories += '|' + aString;
	}

	public String getWeaponCategories()
	{
		return weaponCategories;
	}

	public void setDisplayOrder(String aString)
	{
		try
		{
			displayOrder = Integer.parseInt(aString);
		}
		catch (Exception exc)
		{
		}
	}

	int getDisplayOrder()
	{
		return displayOrder;
	}

	public void setShortRangeDistance(int aShortRange)
	{
		shortRangeDistance = aShortRange;
	}

	public int getShortRangeDistance()
	{
		return shortRangeDistance;
	}
}
