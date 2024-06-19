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
 * Last Edited: $Date: 2006/02/21 01:07:41 $
 *
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import pcgen.gui.ChooserFactory;
import pcgen.gui.ChooserInterface;
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
	private ArrayList templatesAdded = null;

	private TreeSet languageBonus = new TreeSet();
	private double cost = 1.0;

	public final void setRemovable(boolean argRemovable)
	{
		removable = argRemovable;
	}

	private static int getArrayListSize(ArrayList al)
	{
		if (al != null)
		{
			return al.size();
		}
		return 0;
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

	public final String getTemplateSize()
	{
		return templateSize;
	}

	public final void setTemplateSize(String argSize)
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
	public final int getNonProficiencyPenalty()
	{
		return nonProficiencyPenalty;
	}

	public final void setCR(int argCR)
	{
		CR = argCR;
	}

	public final void setAgeString(String argAgeString)
	{
		ageString = argAgeString;
	}

	public final String getGenderLock()
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
		Globals.debugPrint("genderString:", genderString);
		if ("Female".equalsIgnoreCase(genderString))
		{
			genderString = PropertyFactory.getString("in_genderFemale");
		}
		else if ("Male".equalsIgnoreCase(genderString))
		{
			genderString = PropertyFactory.getString("in_genderMale");
		}
		else if ("Neuter".equalsIgnoreCase(genderString))
		{
			genderString = PropertyFactory.getString("in_genderNeuter");
		}

		gender = genderString;
	}

	public final void setHandedLock(String handedString)
	{
		handed = handedString;
	}

	public final void setHeightString(String argHeightString)
	{
		heightString = argHeightString;
	}

	public final void setWeightString(String argWeightString)
	{
		weightString = argWeightString;
	}

	public final void setLevelsPerFeat(int argLevelsPerFeat)
	{
		levelsPerFeat = argLevelsPerFeat;
	}

	public final String getChooseLanguageAutos()
	{
		return chooseLanguageAutos;
	}

	public final void setChooseLanguageAutos(String argChooseLanguageAutos)
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

	public final void setLevelAdjustment(String argLevelAdjustment)
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

	public void addTemplate(String template)
	{
		if (".CLEAR".equals(template))
		{
			templates.clear();
		}
		//
		// Add a choice to a pre-existing CHOOSE
		//
		else if (template.startsWith("ADDCHOICE:"))
		{
			template = template.substring(10);
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
			templates.add(template);
		}
	}

	public final int getBonusSkillsPerLevel()
	{
		return bonusSkillsPerLevel;
	}

	public final void setBonusSkillsPerLevel(int argBonusSkillsPerLevel)
	{
		bonusSkillsPerLevel = argBonusSkillsPerLevel;
	}

	public final String getFavoredClass()
	{
		return favoredClass;
	}

	public final void setFavoredClass(String newClass)
	{
		favoredClass = newClass;
	}

	public final String getSubRace()
	{
		return subRace;
	}

	public final void setSubRace(String argSubRace)
	{
		subRace = argSubRace;
	}

	public final String getRegion()
	{
		return region;
	}

	public final void setRegion(String argRegion)
	{
		region = argRegion;
	}

	public final String getSubRegion()
	{
		return subregion;
	}

	public final void setSubRegion(String argSubregion)
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

	public final int isVisible()
	{
		return templateVisible;
	}

	public final void setVisible(int argTemplateVisible)
	{
		templateVisible = argTemplateVisible;
	}

	public boolean isRemovable()
	{
		if ((templateVisible == VISIBILITY_DEFAULT) || (templateVisible == VISIBILITY_DISPLAY_ONLY))
		{
			return removable;
		}
		else
		{
			return false;
		}
	}

	public boolean isQualified()
	{
		if (Globals.getCurrentPC() == null)
		{
			return false;
		}
		return passesPreReqTests();
	}

	public Object clone() throws CloneNotSupportedException
	{
		final PCTemplate aTemp = (PCTemplate) super.clone();
		aTemp.templateVisible = templateVisible;
		aTemp.templates = (ArrayList) templates.clone();
		aTemp.languageBonus = (TreeSet) languageBonus.clone();
		if (getArrayListSize(levelStrings) != 0)
		{
			aTemp.levelStrings = (ArrayList) levelStrings.clone();
		}
		if (getArrayListSize(hitDiceStrings) != 0)
		{
			aTemp.hitDiceStrings = (ArrayList) hitDiceStrings.clone();
		}
		//if (getArrayListSize(sizeStrings) != 0)
		//{
		//	aTemp.sizeStrings = (ArrayList) sizeStrings.clone();
		//}
		if (getArrayListSize(weaponProfBonus) != 0)
		{
			aTemp.weaponProfBonus = (ArrayList) weaponProfBonus.clone();
		}
		if (getArrayListSize(featStrings) != 0)
		{
			aTemp.featStrings = (ArrayList) featStrings.clone();
		}

		return aTemp;
	}

	public String modifierString()
	{
		final StringBuffer mods = new StringBuffer(50); //More likely to be true than 16 (the default)
		for (int x = 0; x < Globals.getStatList().size(); ++x)
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

	public void setName(String newName)
	{
		super.setName(newName);
	}

	public ArrayList feats(int level, int hitdice)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		ArrayList feats;
		if (getArrayListSize(featStrings) != 0)
		{
			feats = (ArrayList) featStrings.clone();
		}
		else
		{
			feats = new ArrayList();
		}

		for (int x = 0; x < getArrayListSize(levelStrings); ++x)
		{
			if (contains((levelStrings.get(x).toString()), "FEAT:") && doesLevelQualify(level, x))
			{
				//feats.add(getStringAfter("FEAT:", levelStrings.get(x)));
				if (!aPC.hasFeat(getStringAfter("FEAT:", levelStrings.get(x).toString())))
				{
					aPC.setFeats(aPC.getFeats() + 1);
					aPC.modFeat(getStringAfter("FEAT:", levelStrings.get(x).toString()), true, false);
				}
			}
		}

		for (int x = 0; x < getArrayListSize(hitDiceStrings); ++x)
		{
			if (contains(hitDiceStrings.get(x).toString(), "FEAT:") && doesHitDiceQualify(hitdice, x))
			{
				//feats.add(getStringAfter("FEAT:", hitDiceStrings.get(x)));
				if (!aPC.hasFeat(getStringAfter("FEAT:", levelStrings.get(x).toString())))
				{
					aPC.setFeats(aPC.getFeats() + 1);
					aPC.modFeat(getStringAfter("FEAT:", levelStrings.get(x).toString()), true, false);
				}
			}
		}

		return feats;
	}

	public int getSR(int level, int hitdice)
	{
		int aSR = getSR();
		for (int x = 0; x < getArrayListSize(levelStrings); ++x)
		{
			if (contains(levelStrings.get(x).toString(), "SR:") && doesLevelQualify(level, x))
			{
				aSR = Math.max(Integer.parseInt(getStringAfter("SR:", levelStrings.get(x).toString())), aSR);
			}
		}
		for (int x = 0; x < getArrayListSize(hitDiceStrings); ++x)
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
		int CR = this.CR;
		for (int x = 0; x < getArrayListSize(levelStrings); ++x)
		{
			if (contains(levelStrings.get(x).toString(), "CR:") && doesLevelQualify(level, x))
			{
				CR += Integer.parseInt(getStringAfter("CR:", levelStrings.get(x).toString()));
			}
		}
		for (int x = 0; x < getArrayListSize(hitDiceStrings); ++x)
		{
			if (contains(hitDiceStrings.get(x).toString(), "CR:") && doesHitDiceQualify(hitdice, x))
			{
				CR += Integer.parseInt(getStringAfter("CR:", hitDiceStrings.get(x).toString()));
			}
		}
		return CR;
	}

	public ArrayList getSpecialAbilityList(int level, int hitdice)
	{
		if (specialAbilityList == null || specialAbilityList.isEmpty())
		{
			return specialAbilityList;
		}
		final ArrayList aList = (ArrayList) specialAbilityList.clone();
		for (int x = 0; x < getArrayListSize(levelStrings); ++x)
		{
			if (contains(levelStrings.get(x).toString(), "SA:") && doesLevelQualify(level, x))
			{
				final String saString = getStringAfter("SA:", levelStrings.get(x).toString());
				SpecialAbility sa = new SpecialAbility(saString);
				aList.add(sa);
			}
		}
		for (int x = 0; x < getArrayListSize(hitDiceStrings); ++x)
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

	ArrayList addSpecialAbilitiesToList(ArrayList aList, int level, int hitdice)
	{
		if (specialAbilityList != null)
		{
			aList.addAll(getSpecialAbilityList(level, hitdice));
		}
		return aList;
	}

	public final void setHitDiceSize(int argHitDiceSize)
	{
		hitDiceSize = argHitDiceSize;
	}

	public final int getHitDiceSize()
	{
		return hitDiceSize;
	}

	public boolean isNonAbility(int statIdx)
	{
		final ArrayList statList = Globals.getStatList();
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

	/** Adds one chosen language. */
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

	public final Set getLanguageBonus()
	{
		return languageBonus;
	}

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
		return getArrayListSize(weaponProfBonus);
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

	public final double getCost()
	{
		return cost;
	}

	public final void setCost(double argCost)
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
		for (x = 0; x < getArrayListSize(levelStrings); ++x)
		{
			if (contains(levelStrings.get(x).toString(), "DR:") && doesLevelQualify(level, x))
			{
				drString.append(levelStrings.get(x)).append("|");
			}
		}
		for (x = 0; x < getArrayListSize(hitDiceStrings); ++x)
		{
			if (contains(hitDiceStrings.get(x).toString(), "DR:") && doesHitDiceQualify(hitdice, x))
			{
				drString.append(getStringAfter("DR:", hitDiceStrings.get(x).toString())).append("|");
			}
		}
		return drString.toString();
	}

	ArrayList getTemplates(boolean isImporting)
	{
		final ArrayList newTemplates = new ArrayList();
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
				if ((templateName != null) && (templateName.length() != 0))
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
		final ArrayList choiceTemplates = Utility.split(templateList, '|');
		for (int i = choiceTemplates.size() - 1; i >= 0; i--)
		{
			final String templateName = (String) choiceTemplates.get(i);
			final PCTemplate template = Globals.getTemplateNamed(templateName);
			if ((template == null) || !template.passesPreReqTests())
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

	ArrayList templatesAdded()
	{
		if (templatesAdded == null)
		{
			return new ArrayList();
		}
		return templatesAdded;
	}

	public final int getBonusInitialFeats()
	{
		return bonusInitialFeats;
	}

	public final void setBonusInitialFeats(int argBonusInitialFeats)
	{
		bonusInitialFeats = argBonusInitialFeats;
	}

	private boolean doesLevelQualify(int level, int x)
	{
		if (x >= getArrayListSize(levelStrings))
		{
			return false;
		}
		final StringTokenizer stuff = new StringTokenizer((String) levelStrings.get(x), ":");
		return level >= Integer.parseInt(stuff.nextToken());
	}

	private boolean doesHitDiceQualify(int hitdice, int x)
	{
		if (x >= getArrayListSize(hitDiceStrings))
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
		if (cost != 1.0d)
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
		if (getArrayListSize(featStrings) > 0)
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
		if (getArrayListSize(hitDiceStrings) > 0)
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
		if (getArrayListSize(levelStrings) > 0)
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
		if (getMovements() != null && getMovements().length > 0)
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

			for (int index = 0; index < getMovements().length; index++)
			{
				if (index > 0)
				{
					txt.append(',');
				}
				if (getMovementTypes()[index] != null && getMovementTypes()[index].length() > 0)
				{
					txt.append(getMovementTypes()[index]).append(",");
				}
				if (getMovementMultOp()[index].length() > 0)
				{
					txt.append(getMovementMultOp()[index]).append(getMovementMult()[index]);
				}
				else
				{
					txt.append(getMovements()[index]);
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
		if (getArrayListSize(templates) > 0)
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
		if (getArrayListSize(weaponProfBonus) > 0)
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
}
