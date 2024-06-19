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
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:07:42 $
 *
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import pcgen.util.PropertyFactory;

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
	private String currencyUnit = "";
	private String currencyUnitAbbrev = "";
	private String damageResistance = "";
	private String defaultSpellBook = "Known Spells";
	private String acName = "";
	private String acAbbrev = "";
	private String heightUnitAbbrev = "";
	private String hpAbbrev = "";
	private String hpName = "";
	private String althpAbbrev = "";
	private String althpName = "";
	private String levelDownMessage = "";
	private String levelUpMessage = "";
	private String menuEntry = "";
	private String menuToolTip = "";
	private String moveUnit = "";
	private String moveUnitAbbrev = "";
	private String name = "";
	private String displayVariableText = "";
	private String displayVariableName = "";
	private String displayVariable2Text = "";
	private String displayVariable2Name = "";
	private String displayVariable3Text = "";
	private String displayVariable3Name = "";
	private String weaponTypes = "";
	private String weaponCategories = "";
	private String weightUnitAbbrev = "";

	private ArrayList allowedModes = null;
	private ArrayList bonusFeatLevels = new ArrayList();
	private ArrayList bonusStatLevels = new ArrayList();
	private ArrayList defaultDeityList = new ArrayList();
	private ArrayList loadStrings = new ArrayList();
	private ArrayList skillMultiplierLevels = new ArrayList();
	private ArrayList levelInfo = new ArrayList(100);
	private HashMap plusCalcs = null;
	private String spellBaseDC = "0";

	private int displayOrder = Integer.MAX_VALUE;
	/** no default distance for short range */
	private int shortRangeDistance = 0;

	private class TabInfo
	{
		String tabName = "";
		String contextPath = "";
		boolean visible = true;
	}

	private TabInfo[] tInfo;

	/** Creates a new instance of GameMode */
	public GameMode(String modeName)
	{
		name = modeName;

		tInfo = new TabInfo[Constants.tabNames.length];
		for (int i = 0; i < tInfo.length; ++i)
		{
			tInfo[i] = new TabInfo();
		}
		tInfo[Constants.TAB_ABILITIES].tabName = "in_abilities";
		tInfo[Constants.TAB_CLASSES].tabName = "in_class";
		tInfo[Constants.TAB_DESCRIPTION].tabName = "in_descrip";
		tInfo[Constants.TAB_DOMAINS].tabName = "in_domains";
		tInfo[Constants.TAB_FEATS].tabName = "in_feats";
		tInfo[Constants.TAB_INVENTORY].tabName = "in_inventory";
		tInfo[Constants.TAB_RACES].tabName = "in_race";
		tInfo[Constants.TAB_SKILLS].tabName = "in_skills";
		tInfo[Constants.TAB_SPELLS].tabName = "in_spells";
		tInfo[Constants.TAB_SUMMARY].tabName = "in_summary";
		tInfo[Constants.TAB_SOURCES].tabName = "Source Materials";
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

	public void setSpellBaseDC(String arg)
	{
		spellBaseDC = arg;
	}

	public String getSpellBaseDC()
	{
		return spellBaseDC;
	}

	/**
	 * array of LevelInfo objects
	 **/
	public ArrayList getLevelInfo()
	{
		return levelInfo;
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
	String getAltHPText()
	{
		return althpName;
	}

	public void setAltHPText(String aString)
	{
		althpName = aString;
	}

	String getAltHPAbbrev()
	{
		return althpAbbrev;
	}

	public void setAltHPAbbrev(String aString)
	{
		althpAbbrev = aString;
	}

	/**
	 * Variable Display
	 */
	String getVariableDisplayText()
	{
		return displayVariableText;
	}

	public void setVariableDisplayText(String aString)
	{
		displayVariableText = aString;
	}

	String getVariableDisplayName()
	{
		return displayVariableName;
	}

	public void setVariableDisplayName(String aString)
	{
		displayVariableName = aString;
	}

	String getVariableDisplay2Text()
	{
		return displayVariable2Text;
	}

	public void setVariableDisplay2Text(String aString)
	{
		displayVariable2Text = aString;
	}

	String getVariableDisplay2Name()
	{
		return displayVariable2Name;
	}

	public void setVariableDisplay2Name(String aString)
	{
		displayVariable2Name = aString;
	}

	String getVariableDisplay3Text()
	{
		return displayVariable3Text;
	}

	public void setVariableDisplay3Text(String aString)
	{
		displayVariable3Text = aString;
	}

	String getVariableDisplay3Name()
	{
		return displayVariable3Name;
	}

	public void setVariableDisplay3Name(String aString)
	{
		displayVariable3Name = aString;
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
	 * AC Info
	 */
	String getACText()
	{
		return acName;
	}

	public void setACText(String aString)
	{
		acName = aString;
	}

	String getACAbbrev()
	{
		return acAbbrev;
	}

	public void setACAbbrev(String aString)
	{
		acAbbrev = aString;
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

	public void setAlignmentText(final String aString)
	{
		alignmentName = aString;
	}

	public void setLevelUpMessage(final String aString)
	{
		levelUpMessage = aString;
	}

	public String getLevelUpMessage()
	{
		return levelUpMessage;
	}

	public void setLevelDownMessage(final String aString)
	{
		levelDownMessage = aString;
	}

	public String getLevelDownMessage()
	{
		return levelDownMessage;
	}

	/**
	 * Move Units
	 **/
	public void setMoveUnit(final String aString)
	{
		moveUnit = aString;
	}

	public String getMoveUnit()
	{
		return moveUnit;
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

	public final void addPlusCalculation(final String aString)
	{
		final int idx = aString.indexOf('|');
		if (idx > 0)
		{
			if (plusCalcs == null)
			{
				plusCalcs = new HashMap();
			}
			plusCalcs.put(aString.substring(0, idx).toUpperCase(), aString.substring(idx + 1));
		}
	}

	public final String getPlusCalculation(final String type)
	{
		String aString = null;
		if (plusCalcs != null)
		{
			aString = (String) plusCalcs.get(type);
		}
		return aString;
	}

	public final void setTabContext(final int iTab, final String argTabContext)
	{
		tInfo[iTab].contextPath = argTabContext;
	}

	public final void setTabName(final int iTab, final String argTabName)
	{
		tInfo[iTab].tabName = argTabName;
	}

	public final void setTabVisible(final int iTab, final String argTabVisible)
	{
		tInfo[iTab].visible = argTabVisible.startsWith("Y");
	}

	public final String getTabName(final int iTab)
	{
		String temp = tInfo[iTab].tabName;
		if (temp.startsWith("in_"))
		{
			temp = PropertyFactory.getString(temp);
		}
		return temp;
	}

	public final boolean getTabShown(final int iTab)
	{
		if ((iTab >= 0) && (iTab < tInfo.length))
		{
			return tInfo[iTab].visible;
		}
		return false;
	}

	public final String getContextPath(final int iTab)
	{
		if ((iTab >= 0) && (iTab < tInfo.length))
		{
			return tInfo[iTab].contextPath;
		}
		return "";
	}

	public static final int getTabNumber(final String tabName)
	{
		for (int i = 0; i < Constants.tabNames.length; ++i)
		{
			if (tabName.equalsIgnoreCase(Constants.tabNames[i]))
			{
				return i;
			}
		}
		return Constants.TAB_INVALID;
	}
}
