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
 * Last Edited: $Date: 2006/02/21 01:16:13 $
 *
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 * Handles game modes.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */

public final class GameMode implements Comparable
{
	private boolean showClassDefense = false;

	private String acFlatBonus = "";
	private String acTouchBonus = "";
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

	private List allowedModes = null;
	private List bonusFeatLevels = new ArrayList();
	private List bonusStatLevels = new ArrayList();
	private List classTypeList = new ArrayList();
	private List defaultDeityList = new ArrayList();
	private List loadStrings = new ArrayList();
	private List skillMultiplierLevels = new ArrayList();
	private List levelInfo = new ArrayList(100);
	private Map plusCalcs = null;
	private Map spellRangeMap = new HashMap();
	private String spellBaseDC = "0";

	private int displayOrder = Integer.MAX_VALUE;
	/** no default distance for short range */
	private int shortRangeDistance = 0;

	private int babMaxAtt = Integer.MAX_VALUE; //4
	private int babMaxLvl = Integer.MAX_VALUE; //20
	private int babAttCyc = 5; //6
	private int babMinVal = 1;
	private int checksMaxLvl = Integer.MAX_VALUE; //20

	private static class TabInfo
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
			return this.name.compareToIgnoreCase(((GameMode) obj).name);
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

	List getAllowedModes()
	{
		if (allowedModes == null)
		{
			List modes = new ArrayList(1);
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

	List getSkillMultiplierLevels()
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
	List getBonusFeatLevels()
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
	List getBonusStatLevels()
	{
		return bonusStatLevels;
	}

	public void setBonusStatLevels(String aString)
	{
		bonusStatLevels.add(aString);
	}

	/**
	 * The formula used to compute spell ranges
	 **/
	public void setSpellRangeFormula(String aString)
	{
		StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		if (aTok.countTokens() < 2)
		{
			return;
		}
		String aRange = aTok.nextToken().toUpperCase();
		String aFormula = aTok.nextToken();
		spellRangeMap.put(aRange, aFormula);
	}

	/**
	 * Returns the formula used for calculate the range of a spell
	 **/
	public String getSpellRangeFormula(String aRange)
	{
		String aString = null;
		if (spellRangeMap != null)
		{
			aString = (String) spellRangeMap.get(aRange);
		}
		return aString;
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
	public List getLevelInfo()
	{
		return levelInfo;
	}

	List getDeityList()
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

	List getLoadStrings()
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
		catch (NumberFormatException exc)
		{
			Logging.errorPrint("Will use default for displayOrder: " + displayOrder, exc);
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

	public void addPlusCalculation(final String aString)
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

	public String getPlusCalculation(final String type)
	{
		String aString = null;
		if (plusCalcs != null)
		{
			aString = (String) plusCalcs.get(type);
		}
		return aString;
	}

	public void setTabContext(final int iTab, final String argTabContext)
	{
		tInfo[iTab].contextPath = argTabContext;
	}

	public void setTabName(final int iTab, final String argTabName)
	{
		tInfo[iTab].tabName = argTabName;
	}

	public void setTabVisible(final int iTab, final String argTabVisible)
	{
		tInfo[iTab].visible = argTabVisible.startsWith("Y");
	}

	public String getTabName(final int iTab)
	{
		String temp = tInfo[iTab].tabName;
		if (temp.startsWith("in_"))
		{
			temp = PropertyFactory.getString(temp);
		}
		return temp;
	}

	public boolean getTabShown(final int iTab)
	{
		if ((iTab >= 0) && (iTab < tInfo.length))
		{
			return tInfo[iTab].visible;
		}
		return false;
	}

	public String getContextPath(final int iTab)
	{
		if ((iTab >= 0) && (iTab < tInfo.length))
		{
			return tInfo[iTab].contextPath;
		}
		return "";
	}

	public static int getTabNumber(final String tabName)
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

	public int getBabMaxAtt()
	{
		return babMaxAtt;
	}

	public void setBabMaxAtt(int arg)
	{
		babMaxAtt = arg;
	}

	public int getBabMaxLvl()
	{
		return babMaxLvl;
	}

	public void setBabMaxLvl(int arg)
	{
		babMaxLvl = arg;
	}

	public int getBabAttCyc()
	{
		return babAttCyc;
	}

	public void setBabAttCyc(int arg)
	{
		babAttCyc = arg;
	}

	public int getBabMinVal()
	{
		return babMinVal;
	}

	public void setBabMinVal(int arg)
	{
		babMinVal = arg;
	}

	public int getChecksMaxLvl()
	{
		return checksMaxLvl;
	}

	public void setChecksMaxLvl(int arg)
	{
		checksMaxLvl = arg;
	}

	public void setAcTouchBonus(String arg)
	{
		acTouchBonus = arg;
	}

	public String getAcTouchBonus()
	{
		return acTouchBonus;
	}

	public void setAcFlatBonus(String arg)
	{
		acFlatBonus = arg;
	}

	public String getAcFlatBonus()
	{
		return acFlatBonus;
	}

	public void addClassType(String aString)
	{
		if (".CLEAR".equals(aString))
		{
			classTypeList = null;
			return;
		}

		if (classTypeList == null)
		{
			classTypeList = new ArrayList();
		}
		ClassType aClassType = new ClassType();
		final StringTokenizer aTok = new StringTokenizer(aString, "\t");
		aClassType.setName(aTok.nextToken()); //Name of the Class Type
		while (aTok.hasMoreTokens())
		{
			final String bString = aTok.nextToken();
			if (bString.startsWith("CRFORMULA:"))
			{
				aClassType.setCRFormula(bString.substring(10));
			}
			else if (bString.startsWith("XPPENALTY:"))
			{
				aClassType.setXPPenalty(bString.substring(10).equals("YES"));
			}
			else if (bString.startsWith("ISMONSTER:"))
			{
				aClassType.setIsMonster(bString.substring(10).equals("YES"));
			}
//			else if (bString.startsWith("ISPRESTIGE:"))
//			{
//				aClassType.setIsPrestige(bString.substring(11).equals("YES"));
//			}
			else
			{
				Logging.errorPrint("Incorrect tag in miscinfo.CLASSTYPE: " + bString);
			}
		}

		classTypeList.add(aClassType);
	}

	public ClassType getClassTypeByName(String aClassName)
	{
		for(Iterator i = classTypeList.iterator(); i.hasNext();)
		{
			ClassType aClassType = (ClassType) i.next();
			if (aClassType.getName().equalsIgnoreCase(aClassName))
			{
				return aClassType;
			}
		}
		return null;
	}
}
