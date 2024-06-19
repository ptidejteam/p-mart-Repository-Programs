/*
 * PCTemplate.java
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
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:18:39 $
 *
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import pcgen.core.utils.Utility;
import pcgen.gui.utils.ChooserFactory;
import pcgen.gui.utils.ChooserInterface;
import pcgen.util.PropertyFactory;

/**
 * <code>PCTemplate</code>.
 *
 * @author Mark Hulsman <hulsmanm@purdue.edu>
 * @version $Revision: 1.1 $
 */
public final class PCTemplate extends PObject implements HasCost
{

	///////////////////////////////////////////////////////////////////////
	// Static properties
	///////////////////////////////////////////////////////////////////////
	public static final int VISIBILITY_HIDDEN = 0;
	public static final int VISIBILITY_DEFAULT = 1;
	public static final int VISIBILITY_OUTPUT_ONLY = 2;
	public static final int VISIBILITY_DISPLAY_ONLY = 3;

	private int templateVisible = VISIBILITY_DEFAULT;

	private boolean removable = true;
	private int nonProficiencyPenalty = -4;
	private int hitDiceSize = 0;
	private ArrayList hitDiceStrings = null;
	private ArrayList featStrings = null;
	private HashMap chosenFeatStrings = null;
	private ArrayList levelStrings = null;
//	private ArrayList sizeStrings = null;			// never populated--removing Byngl Oct 23,2002
	private ArrayList weaponProfBonus = null;
	private String templateSize = "";
	private int bonusSkillsPerLevel = 0;
	private int bonusInitialFeats = 0;
	private String ageString = Constants.s_NONE;
	private String heightString = Constants.s_NONE;
	private String weightString = Constants.s_NONE;
	private String subRace = Constants.s_NONE;
	private String region = Constants.s_NONE;
	private String subregion = Constants.s_NONE;
//	private String kit = "";
	private String favoredClass = "";
	private String chooseLanguageAutos = "";
	private int CR = 0;
	private String levelAdjustment = "0"; //now a string so that we can handle formulae
	private int levelsPerFeat = 3;

	// If set these two will override any other choices.
	private String gender = Constants.s_NONE;
	private String handed = Constants.s_NONE;

	private ArrayList templates = new ArrayList();
	private List templatesAdded = null;

	private TreeSet languageBonus = new TreeSet();
	private double cost = 1.0;

	public void setRemovable(boolean argRemovable)
	{
		removable = argRemovable;
	}

	private static int getListSize(List al)
	{
		int result = 0;
		if (al != null)
		{
			result = al.size();
		}
		return result;
	}

	public ArrayList getLevelStrings()
	{
		if (levelStrings == null)
		{
			levelStrings = new ArrayList();
		}
		return levelStrings;
	}

	public void addLevelString(String levelString)
	{
		if (".CLEAR".equals(levelString))
		{
			if (levelStrings != null)
			{
				levelStrings.clear();
			}
			return;
		}

		if (levelStrings == null)
		{
			levelStrings = new ArrayList();
		}
		levelStrings.add(levelString);
	}

	public ArrayList getHitDiceStrings()
	{
		if (hitDiceStrings == null)
		{
			hitDiceStrings = new ArrayList();
		}
		return hitDiceStrings;
	}

	public void addHitDiceString(String hitDiceString)
	{
		if (".CLEAR".equals(hitDiceString))
		{
			if (hitDiceStrings != null)
			{
				hitDiceStrings.clear();
			}
			return;
		}

		if (hitDiceStrings == null)
		{
			hitDiceStrings = new ArrayList();
		}
		hitDiceStrings.add(hitDiceString);
	}

	public String getTemplateSize()
	{
		return templateSize;
	}

	public void setTemplateSize(String argSize)
	{
		templateSize = argSize;
	}

	public void setNonProficiencyPenalty(int npp)
	{
		if (npp <= 0)
		{
			nonProficiencyPenalty = npp;
		}
	}

	/**
	 * <br>author: arcady June 4, 2002
	 *
	 * @return nonProficiencyPenalty
	 */
	public int getNonProficiencyPenalty()
	{
		return nonProficiencyPenalty;
	}

	public void setCR(int argCR)
	{
		CR = argCR;
	}

	public void setAgeString(String argAgeString)
	{
		ageString = argAgeString;
	}

	public String getGenderLock()
	{
		return gender;
	}

	/**
	 * <code>setGenderLock</code> locks gender to appropriate PropertyFactory setting if String matches 'Male','Female', or 'Neuter'.
	 *
	 * author arcady <arcady@users.sourceforge.net>
	 */
	public void setGenderLock(String genderString)
	{
		if ("Female".equalsIgnoreCase(genderString))
		{
			gender = PropertyFactory.getString("in_genderFemale");
		}
		else if ("Male".equalsIgnoreCase(genderString))
		{
			gender = PropertyFactory.getString("in_genderMale");
		}
		else if ("Neuter".equalsIgnoreCase(genderString))
		{
			gender = PropertyFactory.getString("in_genderNeuter");
		}
	}

	public void setHandedLock(String handedString)
	{
		handed = handedString;
	}

	public void setHeightString(String argHeightString)
	{
		heightString = argHeightString;
	}

	public void setWeightString(String argWeightString)
	{
		weightString = argWeightString;
	}

	public void setLevelsPerFeat(int argLevelsPerFeat)
	{
		levelsPerFeat = argLevelsPerFeat;
	}

	public String getChooseLanguageAutos()
	{
		return chooseLanguageAutos;
	}

	public void setChooseLanguageAutos(String argChooseLanguageAutos)
	{
		chooseLanguageAutos = argChooseLanguageAutos;
	}

	public int getLevelAdjustment()
	{
		int lvlAdjust;

		//if there's a current PC, go ahead and evaluate the formula
		if (Globals.getCurrentPC() != null)
		{
			return Globals.getCurrentPC().getVariableValue(levelAdjustment, "").intValue();
		}
		//otherwise do what we can
		try
		{
			//try to convert the string to an int to return
			lvlAdjust = Integer.parseInt(levelAdjustment);
		}
		catch (NumberFormatException nfe)
		{
			//if the parseInt failed then just punt... return 0
			lvlAdjust = 0;
		}
		return lvlAdjust;
	}

	public String getLevelAdjustmentFormula()
	{
		return levelAdjustment;
	}

	public void setLevelAdjustment(String argLevelAdjustment)
	{
		levelAdjustment = argLevelAdjustment;
	}

	public void addFeatString(final String featString)
	{
		if (".CLEAR".equals(featString))
		{
			if (featStrings != null)
			{
				featStrings.clear();
			}
			return;
		}

		final StringTokenizer aTok = new StringTokenizer(featString, "|", false);
		while (aTok.hasMoreTokens())
		{
			final String fs = aTok.nextToken();
			if (featStrings == null)
			{
				featStrings = new ArrayList();
			}
			featStrings.add(fs);
		}
	}

	/**
	 * Method getTemplateList. Returns an array list containing the raw
	 * templates granted by this template. This includes CHOOSE: strings
	 * which list templates a user will be asked to choose from.
	 *
	 * @return ArrayList of granted templates
	 */
	public ArrayList getTemplateList()
	{
		return templates;
	}

	public void addTemplate(String argTemplate)
	{
		if (".CLEAR".equals(argTemplate))
		{
			templates.clear();
		}
		//
		// Add a choice to a pre-existing CHOOSE
		//
		else if (argTemplate.startsWith("ADDCHOICE:"))
		{
			String template = argTemplate.substring(10);
			for (int i = 0; i < templates.size(); ++i)
			{
				String aString = (String) templates.get(i);
				if (aString.startsWith("CHOOSE:"))
				{
					aString = aString + "|" + template;
					templates.set(i, aString);
					break;
				}
			}
		}
		else
		{
			templates.add(argTemplate);
		}
	}

	public int getBonusSkillsPerLevel()
	{
		return bonusSkillsPerLevel;
	}

	public void setBonusSkillsPerLevel(int argBonusSkillsPerLevel)
	{
		bonusSkillsPerLevel = argBonusSkillsPerLevel;
	}

	public String getFavoredClass()
	{
		return favoredClass;
	}

	public void setFavoredClass(String newClass)
	{
		favoredClass = newClass;
	}

	public String getSubRace()
	{
		return subRace;
	}

	public void setSubRace(String argSubRace)
	{
		subRace = argSubRace;
	}

	public String getRegion()
	{
		return region;
	}

	public void setRegion(String argRegion)
	{
		region = argRegion;
	}

	public String getSubRegion()
	{
		return subregion;
	}

	public void setSubRegion(String argSubregion)
	{
		subregion = argSubregion;
	}

	//
	// This was never called. New functionality has been added to PObject that will
	// supercede this anyways.
	// -Byngl Oct 23, 2002
	//
	//public String getKit()
	//{
	//	return kit;
	//}

	//public void setKit(String argKit)
	//{
	//	kit = argKit;
	//}

	public int isVisible()
	{
		return templateVisible;
	}

	public void setVisible(int argTemplateVisible)
	{
		templateVisible = argTemplateVisible;
	}

	public boolean isRemovable()
	{
		boolean result = false;
		if ((templateVisible == VISIBILITY_DEFAULT) || (templateVisible == VISIBILITY_DISPLAY_ONLY))
		{
			result = removable;
		}
		return result;
	}

	public boolean isQualified()
	{
		if (Globals.getCurrentPC() == null)
		{
			return false;
		}
		return passesPreReqToGain();
	}

	public Object clone() throws CloneNotSupportedException
	{
		final PCTemplate aTemp = (PCTemplate) super.clone();
		aTemp.templateVisible = templateVisible;
		aTemp.templates = (ArrayList) templates.clone();
		aTemp.languageBonus = (TreeSet) languageBonus.clone();
		if (getListSize(levelStrings) != 0)
		{
			aTemp.levelStrings = (ArrayList) levelStrings.clone();
		}
		if (getListSize(hitDiceStrings) != 0)
		{
			aTemp.hitDiceStrings = (ArrayList) hitDiceStrings.clone();
		}
		//if (getArrayListSize(sizeStrings) != 0)
		//{
		//	aTemp.sizeStrings = (ArrayList) sizeStrings.clone();
		//}
		if (getListSize(weaponProfBonus) != 0)
		{
			aTemp.weaponProfBonus = (ArrayList) weaponProfBonus.clone();
		}
		if (getListSize(featStrings) != 0)
		{
			aTemp.featStrings = (ArrayList) featStrings.clone();
		}
		if (chosenFeatStrings != null)
		{
			aTemp.chosenFeatStrings = (HashMap) chosenFeatStrings.clone();
		}

		return aTemp;
	}

	public String modifierString()
	{
		final StringBuffer mods = new StringBuffer(50); //More likely to be true than 16 (the default)
		for (int x = 0; x < SystemCollections.getUnmodifiableStatList().size(); ++x)
		{
			if (isNonAbility(x))
			{
				mods.append(statName(x)).append(":nonability ");
			}
			else
			{
				final int statMod = getStatMod(x);
				if (statMod != 0)
				{
					mods.append(statName(x)).append(':').append(statMod).append(' ');
				}
			}
		}
		if (hitDiceSize != 0)
		{
			mods.append("HITDICESIZE:").append(hitDiceSize).append(' ');
		}
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null)
		{
			if (CR != 0)
			{
				mods.append("CR:").append(CR).append(' ');
			}
			final int x = getSR();
			if (x != 0)
			{
				mods.append("SR:").append(x).append(' ');
			}
			if (DR != null && !"".equals(DR))
			{
				mods.append("DR:").append(DR).append(' ');
			}
			return mods.toString();
		}
		final int nat = (int) bonusTo("COMBAT", "AC");
		if (nat != 0)
		{
			mods.append("AC BONUS:").append(nat);
		}
		if (getCR(aPC.getTotalLevels(), aPC.totalHitDice()) != 0)
		{
			mods.append("CR:").append(getCR(aPC.getTotalLevels(), aPC.totalHitDice())).append(' ');
		}
		if (getSR(aPC.getTotalLevels(), aPC.totalHitDice()) != 0)
		{
			mods.append("SR:").append(getSR(aPC.getTotalLevels(), aPC.totalHitDice())).append(' ');
		}
		if (!getDR(aPC.getTotalLevels(), aPC.totalHitDice()).equals(""))
		{
			mods.append("DR:").append(getDR(aPC.getTotalLevels(), aPC.totalHitDice())).append(' ');
		}

		return mods.toString();
	}

	private static String statName(int x)
	{
		return Globals.s_ATTRIBSHORT[x];
	}

	public List feats(int level, int hitdice)
	{
		List feats;
		if (getListSize(featStrings) != 0)
		{
			feats = (ArrayList) featStrings.clone();
		}
		else
		{
			feats = new ArrayList();
		}

		// arknight modified this back in 1.27 with the comment: Added support for Spycraft Game Mode
		// we no longer support Spycraft (at this time), and this breaks other modes, so I've reverting back to
		// the old method. I am also fixing a bug in the code I'm commenting out. levelStrings is used in the 2nd loop instead of hitDiceStrings.
		// - Byngl Sept 25, 2003
		//
		// Scrap all that. I'm using a HashMap to save those feats that have been taken when the required level/hitdie has been met.
		// We need to do this so that removing the template will also remove the selected feat(s).
		// PCTemplate instances will also need to be cloned() when adding them to PlayerCharacter.
		//
		if (chosenFeatStrings != null)
		{
			feats.addAll(chosenFeatStrings.values());
		}

		for (int x = 0; x < getListSize(levelStrings); ++x)
		{
			String featKey = "L" + Integer.toString(x);
			String featName = null;
			if (chosenFeatStrings != null)
			{
				featName = (String) chosenFeatStrings.get(featKey);
			}
			if (featName == null)
			{
				if (doesLevelQualify(level, x))
				{
					getLevelFeat(levelStrings.get(x).toString(), level, -1, featKey);
				}
			}
		}

		for (int x = 0; x < getListSize(hitDiceStrings); ++x)
		{
			String featKey = "H" + Integer.toString(x);
			String featName = null;
			if (chosenFeatStrings != null)
			{
				featName = (String) chosenFeatStrings.get(featKey);
			}
			if (featName == null)
			{
				if (doesHitDiceQualify(hitdice, x))
				{
					getLevelFeat(hitDiceStrings.get(x).toString(), -1, hitdice, featKey);
				}
			}
		}

		return feats;
	}

	private void getLevelFeat(String levelString, int lvl, int hd, String featKey)
	{
		if (contains(levelString, "FEAT:"))
		{
			String featName = getStringAfter("FEAT:", levelString);
			for(;;)
			{
				ArrayList featList = new ArrayList();
				LevelAbility la = LevelAbility.createAbility(this, lvl, "FEAT(" + featName + ")");
				la.process(featList);
				switch (featList.size())
				{
					case 1:
						featName = featList.get(0).toString();
						break;

					default:
						final PlayerCharacter aPC = Globals.getCurrentPC();
						if (!aPC.isImporting())
						{
							Collections.sort(featList);
							final pcgen.gui.utils.ChooserInterface c = pcgen.gui.utils.ChooserFactory.getChooserInstance();
							c.setPool(1);
							c.setTitle("Feat Choice");
							c.setAvailableList(featList);
							c.show();
							featList = c.getSelectedList();
							if ((featList != null) && (featList.size() != 0))
							{
								featName = featList.get(0).toString();
								continue;
							}
						}
					// fall-through intentional
					case 0:
						return;
				}
				break;
			}
			addChosenFeat(featKey, featName);
		}
	}

	public int getSR(int level, int hitdice)
	{
		int aSR = getSR();
		for (int x = 0; x < getListSize(levelStrings); ++x)
		{
			if (contains(levelStrings.get(x).toString(), "SR:") && doesLevelQualify(level, x))
			{
				aSR = Math.max(Integer.parseInt(getStringAfter("SR:", levelStrings.get(x).toString())), aSR);
			}
		}
		for (int x = 0; x < getListSize(hitDiceStrings); ++x)
		{
			if (contains(hitDiceStrings.get(x).toString(), "SR:") && doesHitDiceQualify(hitdice, x))
			{
				aSR = Math.max(Integer.parseInt(getStringAfter("SR:", hitDiceStrings.get(x).toString())), aSR);
			}
		}
		return aSR;
	}

	public int getCR(int level, int hitdice)
	{
		int _CR = this.CR;
		for (int x = 0; x < getListSize(levelStrings); ++x)
		{
			if (contains(levelStrings.get(x).toString(), "CR:") && doesLevelQualify(level, x))
			{
				_CR += Integer.parseInt(getStringAfter("CR:", levelStrings.get(x).toString()));
			}
		}
		for (int x = 0; x < getListSize(hitDiceStrings); ++x)
		{
			if (contains(hitDiceStrings.get(x).toString(), "CR:") && doesHitDiceQualify(hitdice, x))
			{
				_CR += Integer.parseInt(getStringAfter("CR:", hitDiceStrings.get(x).toString()));
			}
		}
		return _CR;
	}

	public List getSpecialAbilityList(int level, int hitdice)
	{
		ArrayList specialAbilityList = super.getSpecialAbilityList();
		if (specialAbilityList == null || specialAbilityList.isEmpty())
		{
			return specialAbilityList;
		}
		final List aList = (ArrayList) specialAbilityList.clone();
		for (int x = 0; x < getListSize(levelStrings); ++x)
		{
			if (contains(levelStrings.get(x).toString(), "SA:") && doesLevelQualify(level, x))
			{
				final String saString = getStringAfter("SA:", levelStrings.get(x).toString());
				SpecialAbility sa = new SpecialAbility(saString);
				aList.add(sa);
			}
		}
		for (int x = 0; x < getListSize(hitDiceStrings); ++x)
		{
			if (contains(hitDiceStrings.get(x).toString(), "SA:") && doesHitDiceQualify(hitdice, x))
			{
				final String saString = getStringAfter("SA:", hitDiceStrings.get(x).toString());
				SpecialAbility sa = new SpecialAbility(saString);
				aList.add(sa);
			}
		}
		return aList;
	}

	List addSpecialAbilitiesToList(List aList, int level, int hitdice)
	{
		List specialAbilityList = getSpecialAbilityList();
		if (specialAbilityList != null)
		{
			aList.addAll(getSpecialAbilityList(level, hitdice));
		}
		return aList;
	}

	public void setHitDiceSize(int argHitDiceSize)
	{
		hitDiceSize = argHitDiceSize;
	}

	public int getHitDiceSize()
	{
		return hitDiceSize;
	}

	public boolean isNonAbility(int statIdx)
	{
		final List statList = SystemCollections.getUnmodifiableStatList();
		if ((statIdx < 0) || (statIdx >= statList.size()))
		{
			return true;
		}

		final String aStat = "|LOCK." + ((PCStat) statList.get(statIdx)).getAbb() + "|10";
		for (int i = 0, x = getVariableCount(); i < x; ++i)
		{
			final String varString = getVariableDefinition(i);
			if (varString.endsWith(aStat))
			{
				return true;
			}
		}
		return false;
	}

	/** Adds one chosen language.
	 * Identical method in Race.java. Refactor. XXX
	 */
	void chooseLanguageAutos(final boolean flag)
	{
		if (!flag && !"".equals(chooseLanguageAutos))
		{
			final StringTokenizer tokens = new StringTokenizer(chooseLanguageAutos, "|", false);
			List selectedList; // selected list of choices
			final PlayerCharacter aPC = Globals.getCurrentPC();

			final ChooserInterface c = ChooserFactory.getChooserInstance();
			c.setPool(1);
			c.setPoolFlag(false);
			c.setTitle("Pick a Language: ");
			SortedSet list = new TreeSet();
			while (tokens.hasMoreTokens())
			{
				list.add(tokens.nextToken());
			}
			list = Globals.extractLanguageListNames(list);
			c.setAvailableList(new ArrayList(list));
			c.show();
			selectedList = c.getSelectedList();
			if ((selectedList != null) && (selectedList.size() != 0))
			{
				aPC.addFreeLanguage((String) selectedList.get(0));
			}
		}
	}

	public Set getLanguageBonus()
	{
		return languageBonus;
	}

	/**
	 * Identical function exists in PCClass.java. Refactor. XXX
	 * @param aString
	 */
	public void setLanguageBonus(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ",", false);
		while (aTok.hasMoreTokens())
		{
			String token = aTok.nextToken();
			if (".CLEAR".equals(token))
			{
				getLanguageBonus().clear();
			}
			else
			{
				final Language aLang = Globals.getLanguageNamed(token);
				if (aLang != null)
				{
					getLanguageBonus().add(aLang);
				}
			}
		}
	}

	public int getWeaponProfBonusSize()
	{
		return getListSize(weaponProfBonus);
	}

	public ArrayList getWeaponProfBonus()
	{
		if (weaponProfBonus == null)
		{
			return new ArrayList();
		}
		return weaponProfBonus;
	}

	public void setWeaponProfBonus(String aString)
	{
		if (weaponProfBonus == null)
		{
			weaponProfBonus = new ArrayList();
		}

		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
		{
			weaponProfBonus.add(aTok.nextToken());
		}
	}

	public double getCost()
	{
		return cost;
	}

	public void setCost(double argCost)
	{
		cost = argCost;
	}

	public String getDR(int level, int hitdice)
	{
		final StringBuffer drString = new StringBuffer();
		if (DR != null)
		{
			drString.append(DR.trim());
		}
		int x;
		for (x = 0; x < getListSize(levelStrings); ++x)
		{
			if (contains(levelStrings.get(x).toString(), "DR:") && doesLevelQualify(level, x))
			{
				drString.append(levelStrings.get(x)).append("|");
			}
		}
		for (x = 0; x < getListSize(hitDiceStrings); ++x)
		{
			if (contains(hitDiceStrings.get(x).toString(), "DR:") && doesHitDiceQualify(hitdice, x))
			{
				drString.append(getStringAfter("DR:", hitDiceStrings.get(x).toString())).append("|");
			}
		}
		return drString.toString();
	}

	List getTemplates(boolean isImporting)
	{
		final List newTemplates = new ArrayList();
		templatesAdded = new ArrayList();

		if (!isImporting)
		{
			for (Iterator e = templates.iterator(); e.hasNext();)
			{
				String templateName = (String) e.next();
				if (templateName.startsWith("CHOOSE:"))
				{
					for (; ;)
					{
						final String newTemplate = chooseTemplate(templateName.substring(7));
						if (newTemplate != null)
						{
							templateName = newTemplate;
							break;
						}
					}
				}
				if (templateName.length() != 0)
				{
					newTemplates.add(templateName);
					templatesAdded.add(templateName);
				}
			}
		}
		return newTemplates;
	}

	/*
	 * Returns:
	 *    null for no choice made
	 *    "" for no choice available
	 *    templateName of chosen template
	 */
	static String chooseTemplate(String templateList)
	{
		final List choiceTemplates = Utility.split(templateList, '|');
		for (int i = choiceTemplates.size() - 1; i >= 0; i--)
		{
			final String templateName = (String) choiceTemplates.get(i);
			final PCTemplate template = Globals.getTemplateNamed(templateName);
			if ((template == null) || !template.passesPreReqToGain())
			{
				choiceTemplates.remove(i);
			}
		}
		//
		// If only 1 choice, use it without asking
		//
		if (choiceTemplates.size() == 1)
		{
			return (String) choiceTemplates.get(0);
		}
		else if (choiceTemplates.size() > 0)
		{
			return Globals.chooseFromList("Template Choice", choiceTemplates, null, 1);
		}
		return "";
	}

	List templatesAdded()
	{
		if (templatesAdded == null)
		{
			return new ArrayList();
		}
		return templatesAdded;
	}

	public int getBonusInitialFeats()
	{
		return bonusInitialFeats;
	}

	public void setBonusInitialFeats(int argBonusInitialFeats)
	{
		bonusInitialFeats = argBonusInitialFeats;
	}

	private boolean doesLevelQualify(int level, int x)
	{
		if (x >= getListSize(levelStrings))
		{
			return false;
		}
		final StringTokenizer stuff = new StringTokenizer((String) levelStrings.get(x), ":");
		return level >= Integer.parseInt(stuff.nextToken());
	}

	private boolean doesHitDiceQualify(int hitdice, int x)
	{
		if (x >= getListSize(hitDiceStrings))
		{
			return false;
		}
		StringTokenizer tokens = new StringTokenizer((String) hitDiceStrings.get(x), ":");
		final String hitDiceString = tokens.nextToken();
		if (hitDiceString.endsWith("+"))
		{
			return Integer.parseInt(hitDiceString.substring(0, hitDiceString.length() - 1)) <= hitdice;
		}
		tokens = new StringTokenizer(hitDiceString, "-");
		return hitdice >= Integer.parseInt(tokens.nextToken()) && hitdice <= Integer.parseInt(tokens.nextToken());
	}

	private static String getStringAfter(String stuff, String string)
	{
		int index = string.indexOf(stuff) + stuff.length();
		return string.substring(index);


		/*String string = (String) object;
		while (string.length() >= stuff.length() && !string.startsWith(stuff))
			string = string.substring(1);
		if (string.length() <= stuff.length())
			return "";
		return string.substring(stuff.length());*/
	}

	private static boolean contains(String string, String stuff)
	{
		return string.indexOf(stuff) > -1;
	}

	/**
	 * Produce a tailored PCC output, used for saving custom templates.
	 * @see pcgen.core.PObject#getPCCText()
	 */
	public String getPCCText()
	{
		final StringBuffer txt = new StringBuffer(200);
		txt.append(getName());
		if (!Constants.s_NONE.equals(ageString))
		{
			txt.append("\tAGE:").append(ageString);
		}
		if (bonusInitialFeats != 0)
		{
			txt.append("\tBONUSFEATS:").append(bonusInitialFeats);
		}
		if (bonusSkillsPerLevel != 0)
		{
			txt.append("\tBONUSSKILLPOINTS:").append(bonusSkillsPerLevel);
		}
		if (chooseLanguageAutos != null && chooseLanguageAutos.length() > 0)
		{
			txt.append("\tCHOOSE:LANGAUTO:").append(chooseLanguageAutos);
		}
		if (!Utility.doublesEqual(cost, 1.0d))
		{
			txt.append("\tCOST:").append(String.valueOf(cost));
		}
		if (CR != 0)
		{
			txt.append("\tCR:").append(CR);
		}
		if (favoredClass != null && favoredClass.length() > 0)
		{
			txt.append("\tFAVOREDCLASS:").append(favoredClass);
		}
		if (getListSize(featStrings) > 0)
		{
			StringBuffer buffer = new StringBuffer();
			for (Iterator e = featStrings.iterator(); e.hasNext();)
			{
				if (buffer.length() != 0)
				{
					buffer.append('|');
				}
				buffer.append((String) e.next());
			}
			txt.append("\tFEAT:").append(buffer.toString());
		}
		if (!Constants.s_NONE.equals(gender))
		{
			txt.append("\tGENDERLOCK:").append(gender);
		}
		if (!Constants.s_NONE.equals(handed))
		{
			txt.append("\tHANDEDLOCK:").append(handed);
		}
		if (getListSize(hitDiceStrings) > 0)
		{
			for (Iterator e = hitDiceStrings.iterator(); e.hasNext();)
			{
				txt.append("\tHD:").append((String) e.next());
			}
		}
		if (!Constants.s_NONE.equals(heightString))
		{
			txt.append("\tHEIGHT:").append(heightString);
		}
		if (hitDiceSize > 0)
		{
			txt.append("\tHITDICESIZE:").append(hitDiceSize);
		}
		if (languageBonus != null && !languageBonus.isEmpty())
		{
			StringBuffer buffer = new StringBuffer();
			for (Iterator e = languageBonus.iterator(); e.hasNext();)
			{
				if (buffer.length() != 0)
				{
					buffer.append(',');
				}
				buffer.append((String) e.next());
			}
			txt.append("\tLANGBONUS:").append(buffer.toString());
		}
		if (getListSize(levelStrings) > 0)
		{
			for (Iterator e = levelStrings.iterator(); e.hasNext();)
			{
				txt.append("\tLEVEL:").append((String) e.next());
			}
		}
		if (!"0".equals(levelAdjustment))
		{
			txt.append("\tLEVELADJUSTMENT:").append(levelAdjustment);
		}
		if (levelsPerFeat != 3)
		{
			txt.append("\tLEVELSPERFEAT:").append(levelsPerFeat);
		}
		if (getNumberOfMovements() > 0)
		{
			if (getMoveRatesFlag() == 1)
			{
				txt.append("\tMOVEA:");
			}
			else if (getMoveRatesFlag() == 2)
			{
				txt.append("\tMOVECLONE:");
			}
			else
			{
				txt.append("\tMOVE:");
			}

			for (int index = 0; index < getNumberOfMovements(); index++)
			{
				if (index > 0)
				{
					txt.append(',');
				}
				if (getMovementTypes()[index] != null && getMovementTypes()[index].length() > 0)
				{
					txt.append(getMovementTypes()[index]).append(",");
				}
				if (getMovementMultOp(index).length() > 0)
				{
					txt.append(getMovementMultOp(index)).append(getMovementMult(index));
				}
				else
				{
					txt.append(getMovement(index));
				}
			}
		}
		if (nonProficiencyPenalty != -4)
		{
			txt.append("\tNONPP:").append(nonProficiencyPenalty);
		}
		if (templateSize != null && templateSize.length() > 0)
		{
			txt.append("\tSIZE:").append(templateSize);
		}
		if (!Constants.s_NONE.equals(weightString))
		{
			txt.append("\tWEIGHT:").append(weightString);
		}
		if (!"alwaysValid".equals(getQualifyString()))
		{
			txt.append("\tQUALIFY:").append(getQualifyString());
		}
		if (!Constants.s_NONE.equals(region))
		{
			txt.append("\tREGION:");
			if (region.equals(getName()))
			{
				txt.append("Yes");
			}
			else
			{
				txt.append(region);
			}
		}
		if (!removable)
		{
			txt.append("\tREMOVABLE:No");
		}
		if (!Constants.s_NONE.equals(subRace))
		{
			txt.append("\tSUBRACE:");
			if (subRace.equals(getName()))
			{
				txt.append("Yes");
			}
			else
			{
				txt.append(subRace);
			}
		}
		if (!Constants.s_NONE.equals(subregion))
		{
			txt.append("\tSUBREGION:");
			if (subregion.equals(getName()))
			{
				txt.append("Yes");
			}
			else
			{
				txt.append(subregion);
			}
		}
		if (getListSize(templates) > 0)
		{
			for (Iterator e = templates.iterator(); e.hasNext();)
			{
				txt.append("\tTEMPLATE:").append((String) e.next());
			}
		}
		switch (templateVisible)
		{
			case PCTemplate.VISIBILITY_DISPLAY_ONLY:
				txt.append("\tVISIBLE:DISPLAY");
				break;
			case PCTemplate.VISIBILITY_OUTPUT_ONLY:
				txt.append("\tVISIBLE:EXPORT");
				break;
			case PCTemplate.VISIBILITY_HIDDEN:
				txt.append("\tVISIBLE:NO");
				break;
			default:
				txt.append("\tVISIBLE:YES");
				break;
		}
		if (getListSize(weaponProfBonus) > 0)
		{
			StringBuffer buffer = new StringBuffer();
			for (Iterator e = weaponProfBonus.iterator(); e.hasNext();)
			{
				if (buffer.length() != 0)
				{
					buffer.append('|');
				}
				buffer.append((String) e.next());
			}
			txt.append("\tWEAPONBONUS:").append(buffer.toString());
		}
		txt.append(super.getPCCText(false));
		return txt.toString();
	}

	public PCTemplate()
	{
	}

	public HashMap getChosenFeatStrings()
	{
		return chosenFeatStrings;
	}

	public void addChosenFeat(final String mapKey, final String mapValue)
	{
		if (chosenFeatStrings == null)
		{
			chosenFeatStrings = new HashMap();
		}
		chosenFeatStrings.put(mapKey, mapValue);
	}
}
