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
 */

package pcgen.core;

import java.math.BigDecimal;
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
public class PCTemplate extends PObject
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
	private ArrayList levelStrings = new ArrayList();
	private ArrayList hitDiceStrings = new ArrayList();
	private ArrayList sizeStrings = new ArrayList();
	private ArrayList weaponProfBonus = new ArrayList();
	private String templateSize = "";
	private int bonusSkillsPerLevel = 0;
	private int bonusInitialFeats = 0;
	private String ageString = Constants.s_NONE;
	private String heightString = Constants.s_NONE;
	private String weightString = Constants.s_NONE;
	private String subRace = Constants.s_NONE;
	private String region = Constants.s_NONE;
	private String subregion = Constants.s_NONE;
	private String kit = "";
	private Integer movement = new Integer(0);
	private String[] movementTypes;
	private Integer[] movements;
	private String[] movementMultOp;
	private Integer[] movementsMult;
	private String favoredClass = "";
	private String chooseLanguageAutos = "";
	private int moveRatesFlag = 0;
	private int CR = 0;
	private String levelAdjustment = "0"; //now a string so that we can handle formulae
	private ArrayList featStrings = new ArrayList();
	private int[] statMods = new int[Globals.s_ATTRIBLONG.length];
	private boolean[] nonAbilities = new boolean[Globals.s_ATTRIBLONG.length];
	private int levelsPerFeat = 3;

	// If set these two will override any other choices.
	private String gender = Constants.s_NONE;
	private String handed = Constants.s_NONE;

	private ArrayList templates = new ArrayList();
	private ArrayList templatesAdded = null;

	private TreeSet languageBonus = new TreeSet();
	private double cost = 1.0;

	public void setRemovable(boolean removable)
	{
		this.removable = removable;
	}

	public List getLevelStrings()
	{
		return levelStrings;
	}

	public void addLevelString(String levelString)
	{
		levelStrings.add(levelString);
	}

	public List getHitDiceStrings()
	{
		return hitDiceStrings;
	}

	public void addHitDiceString(String hitDiceString)
	{
		hitDiceStrings.add(hitDiceString);
	}

	public String getTemplateSize()
	{
		return templateSize;
	}

	public void setTemplateSize(String size)
	{
		this.templateSize = size;
	}

	public void setNonProficiencyPenalty(int npp)
	{
		if (npp <= 0)
			nonProficiencyPenalty = npp;
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

	public int getCR()
	{
		return CR;
	}

	public void setCR(int CR)
	{
		this.CR = CR;
	}

	public String getAgeString()
	{
		return ageString;
	}

	public void setAgeString(String ageString)
	{
		this.ageString = ageString;
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
		Globals.debugPrint("genderString:", genderString);
		if (genderString.equals("Female"))
			genderString = PropertyFactory.getString("in_genderFemale");
		else if (genderString.equals("Male"))
			genderString = PropertyFactory.getString("in_genderMale");
		else if (genderString.equals("Neuter"))
			genderString = PropertyFactory.getString("in_genderNeuter");

		this.gender = genderString;
	}

	public String getHandedLock()
	{
		return handed;
	}

	public void setHandedLock(String handedString)
	{
		this.handed = handedString;
	}

	public String getHeightString()
	{
		return heightString;
	}

	public void setHeightString(String heightString)
	{
		this.heightString = heightString;
	}

	public String getWeightString()
	{
		return weightString;
	}

	public void setWeightString(String weightString)
	{
		this.weightString = weightString;
	}

	public int getLevelsPerFeat()
	{
		return levelsPerFeat;
	}

	public void setLevelsPerFeat(int levelsPerFeat)
	{
		this.levelsPerFeat = levelsPerFeat;
	}

	public String getChooseLanguageAutos()
	{
		return chooseLanguageAutos;
	}

	public void setChooseLanguageAutos(String chooseLanguageAutos)
	{
		this.chooseLanguageAutos = chooseLanguageAutos;
	}

	public int getLevelAdjustment()
	{
		int lvlAdjust = 0;

		//if there's a current PC, go ahead and evaluate the formula
		if (Globals.getCurrentPC() != null)
			return Globals.getCurrentPC().getVariableValue(levelAdjustment, "").intValue();
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

	public void setLevelAdjustment(String levelAdjustment)
	{
		this.levelAdjustment = levelAdjustment;
	}

	public List getFeatStrings()
	{
		return featStrings;
	}

	public void addFeatString(String featString)
	{
		this.featStrings.add(featString);
	}

	public List getTemplates()
	{
		return templates;
	}

	public void addTemplate(String template)
	{
		if (template.equals(".CLEAR"))
		{
			templates.clear();
		}
		else if (template.startsWith("ADDCHOICE:"))
		{
			template = template.substring(10);
			for (int i = 0; i < templates.size(); i++)
			{
				String aString = (String)templates.get(i);
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

	public int[] getStatMods()
	{
		return statMods;
	}

	public void setStatMod(int index, int value)
	{
		statMods[index] = value;
	}

	public boolean[] getNonAbilities()
	{
		return nonAbilities;
	}

	public void setNonAbility(int index, boolean value)
	{
		nonAbilities[index] = value;
	}

	public int getBonusSkillsPerLevel()
	{
		return bonusSkillsPerLevel;
	}

	public void setBonusSkillsPerLevel(int bonusSkillsPerLevel)
	{
		this.bonusSkillsPerLevel = bonusSkillsPerLevel;
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

	public void setSubRace(String subRace)
	{
		this.subRace = subRace;
	}

	public String getRegion()
	{
		return region;
	}

	public void setRegion(String region)
	{
		this.region = region;
	}

	public String getSubRegion()
	{
		return subregion;
	}

	public void setSubRegion(String subregion)
	{
		this.subregion = subregion;
	}

	public String getKit()
	{
		return kit;
	}

	public void setKit(String kit)
	{
		this.kit = kit;
	}

	public int isVisible()
	{
		return templateVisible;
	}

	public void setVisible(int templateVisible)
	{
		this.templateVisible = templateVisible;
	}

	public boolean isRemovable()
	{
		if (templateVisible == 1 || templateVisible == 3)
			return removable;
		else
			return false;
	}

	public boolean isQualified()
	{
		if (Globals.getCurrentPC() == null)
			return false;
		return passesPreReqTests();
	}

	public Object clone()
	{
		PCTemplate aTemp = (PCTemplate)super.clone();
		aTemp.templateVisible = templateVisible;
		aTemp.templates = (ArrayList)templates.clone();
		aTemp.languageBonus = (TreeSet)languageBonus.clone();
		aTemp.levelStrings = (ArrayList)levelStrings.clone();
		aTemp.hitDiceStrings = (ArrayList)hitDiceStrings.clone();
		aTemp.sizeStrings = (ArrayList)sizeStrings.clone();
		aTemp.weaponProfBonus = (ArrayList)weaponProfBonus.clone();
		aTemp.featStrings = (ArrayList)featStrings.clone();

		return aTemp;
	}

	public String toString()
	{
		return name;
	}

	public String modifierString()
	{
		StringBuffer mods = new StringBuffer(50); //More likely to be true than 16 (the default)
		for (int x = 0; x < statMods.length; x++)
			if (nonAbilities[x])
				mods.append(statName(x)).append(":nonability ");
			else if (statMods[x] != 0)
				mods.append(statName(x)).append(":").append(statMods[x]).append(" ");
		if (hitDiceSize != 0)
			mods.append("HITDICESIZE:").append(hitDiceSize).append(" ");
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null)
		{
			if (CR != 0)
				mods.append("CR:").append(CR).append(" ");
			int x = getSR();
			if (x != 0)
				mods.append("SR:").append(x).append(" ");
			if (DR != null && !DR.equals(""))
				mods.append("DR:").append(DR).append(" ");
			return mods.toString();
		}
		int nat = aPC.getACCalculator().calculateACBonusRestrictiveBySourceByType("TEMPLATE", true, "NATURAL", true);
		if (nat != 0)
			mods.append("BONUS:COMBAT|AC|").append(nat).append("TYPE=Natural ");
		int mod = aPC.getACCalculator().calculateACBonusRestrictiveBySourceByType("TEMPLATE", true, "NATURAL", false);
		if (mod != 0)
			mods.append("BONUS:COMBAT|AC|").append(mod).append(" ");
		if (getCR(aPC.getTotalLevels(), aPC.totalHitDice(), aPC.getSize()) != 0)
			mods.append("CR:").append(getCR(aPC.getTotalLevels(), aPC.totalHitDice(), aPC.getSize())).append(" ");
		if (getSR(aPC.getTotalLevels(), aPC.totalHitDice(), aPC.getSize()) != 0)
			mods.append("SR:").append(getSR(aPC.getTotalLevels(), aPC.totalHitDice(), aPC.getSize())).append(" ");
		if (!getDR(aPC.getTotalLevels(), aPC.totalHitDice(), aPC.getSize()).equals(""))
			mods.append("DR:").append(getDR(aPC.getTotalLevels(), aPC.totalHitDice(), aPC.getSize())).append(" ");

		return mods.toString();
	}

	private String statName(int x)
	{
		return Globals.s_ATTRIBSHORT[x];
	}

	public void setName(String newName)
	{
		super.setName(newName);
	}

	public ArrayList feats(int level, int hitdice, String size)
	{
		ArrayList feats = (ArrayList)featStrings.clone();
		final PlayerCharacter aPC = Globals.getCurrentPC();

		for (int x = 0; x < levelStrings.size(); x++)
			if (contains(levelStrings.get(x), "FEAT:") && doesLevelQualify(level, x))
			//feats.add(getStringAfter("FEAT:", levelStrings.get(x)));
				if (!aPC.hasFeat(getStringAfter("FEAT:", levelStrings.get(x))))
				{
					aPC.setFeats(aPC.getFeats() + 1);
					aPC.modFeat(getStringAfter("FEAT:", levelStrings.get(x)), true, false);
				}

		for (int x = 0; x < hitDiceStrings.size(); x++)
			if (contains(hitDiceStrings.get(x), "FEAT:") && doesHitDiceQualify(hitdice, x))
			//feats.add(getStringAfter("FEAT:", hitDiceStrings.get(x)));
				if (!aPC.hasFeat(getStringAfter("FEAT:", levelStrings.get(x))))
				{
					aPC.setFeats(aPC.getFeats() + 1);
					aPC.modFeat(getStringAfter("FEAT:", levelStrings.get(x)), true, false);
				}

		return feats;
	}

	public int getSR(int level, int hitdice, String size)
	{
		int aSR = getSR();
		for (int x = 0; x < levelStrings.size(); x++)
			if (contains(levelStrings.get(x), "SR:") && doesLevelQualify(level, x))
			{
				aSR = Math.max(Integer.parseInt(getStringAfter("SR:", levelStrings.get(x))), aSR);
			}
		for (int x = 0; x < hitDiceStrings.size(); x++)
			if (contains(hitDiceStrings.get(x), "SR:") && doesHitDiceQualify(hitdice, x))
			{
				aSR = Math.max(Integer.parseInt(getStringAfter("SR:", hitDiceStrings.get(x))), aSR);
			}
		return aSR;
	}

	public int getCR(int level, int hitdice, String size)
	{
		int CR = this.CR;
		for (int x = 0; x < levelStrings.size(); x++)
			if (contains(levelStrings.get(x), "CR:") && doesLevelQualify(level, x))
				CR += Integer.parseInt(getStringAfter("CR:", levelStrings.get(x)));
		for (int x = 0; x < hitDiceStrings.size(); x++)
			if (contains(hitDiceStrings.get(x), "CR:") && doesHitDiceQualify(hitdice, x))
				CR += Integer.parseInt(getStringAfter("CR:", hitDiceStrings.get(x)));
		return CR;
	}

	public ArrayList getSpecialAbilityList(int level, int hitdice, String size)
	{
		if (specialAbilityList == null || specialAbilityList.isEmpty())
			return specialAbilityList;
		ArrayList aList = (ArrayList)specialAbilityList.clone();
		for (int x = 0; x < levelStrings.size(); x++)
			if (contains(levelStrings.get(x), "SA:") && doesLevelQualify(level, x))
			{
				final String saString = getStringAfter("SA:", levelStrings.get(x));
				SpecialAbility sa = new SpecialAbility(saString);
				aList.add(sa);
			}
		for (int x = 0; x < hitDiceStrings.size(); x++)
		{
			if (contains(hitDiceStrings.get(x), "SA:") && doesHitDiceQualify(hitdice, x))
			{
				final String saString = getStringAfter("SA:", hitDiceStrings.get(x));
				SpecialAbility sa = new SpecialAbility(saString);
				aList.add(sa);
			}
		}
		return aList;
	}

	public ArrayList addSpecialAbilitiesToList(ArrayList aList, int level, int hitdice, String size)
	{
		if (specialAbilityList != null)
			aList.addAll(getSpecialAbilityList(level, hitdice, size));
		return aList;
	}

	public int getHitDiceSize()
	{
		return hitDiceSize;
	}

	public void setHitDiceSize(int hitDiceSize)
	{
		this.hitDiceSize = hitDiceSize;
	}

	public int getStatMod(int x)
	{
		if (nonAbilities[x])
			return 0;
		return statMods[x];
	}

	public boolean isNonAbility(int x)
	{
		return nonAbilities[x];
	}

	/** Adds one chosen language. */
	public void chooseLanguageAutos(boolean flag)
	{
		if (!flag && !chooseLanguageAutos.equals(""))
		{
			StringTokenizer tokens = new StringTokenizer(chooseLanguageAutos, "|", false);
			List selectedList = new ArrayList(); // selected list of choices
			final PlayerCharacter aPC = Globals.getCurrentPC();

			ChooserInterface c = ChooserFactory.getChooserInstance();
			c.setPool(1);
			c.setPoolFlag(false);
			c.setTitle("Pick a Language: ");
			SortedSet list = new TreeSet();
			while (tokens.hasMoreTokens())
				list.add(tokens.nextToken());
			list = Globals.extractLanguageList(list);
			c.setAvailableList(new ArrayList(list));
			c.show();
			selectedList = c.getSelectedList();
			aPC.addFreeLanguage((String)selectedList.get(0));
		}
	}

	public Set getLanguageBonus()
	{
		return languageBonus;
	}

	public void setLanguageBonus(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ",", false);
		while (aTok.hasMoreTokens())
		{
			String token = aTok.nextToken();
			if (token.equals(".CLEAR"))
			{
				getLanguageBonus().clear();
			}
			else
			{
				getLanguageBonus().add(token);
			}
		}
	}


	public String[] getMovementTypes()
	{
		return movementTypes;
	}

	public Integer[] getMovements()
	{
		return movements;
	}

	public Integer[] getMovementsMult()
	{
		return movementsMult;
	}

	public String[] getMovementMultOp()
	{
		return movementMultOp;
	}

	public int getMoveRatesFlag()
	{
		return moveRatesFlag;
	}

	public void setMoveRatesFlag(int moveRatesFlag)
	{
		this.moveRatesFlag = moveRatesFlag;
	}

	public void setMoveRates(String moveparse)
	{
		final StringTokenizer moves = new StringTokenizer(moveparse, ",");
		String tok;
		int newmove = 0;
		if (moves.countTokens() == 1)
		{
			tok = moves.nextToken(); // modified in preparation for future code changes. Do not remove commented section

			Globals.debugPrint("single option in ", this.toString());

			if (tok.startsWith("*") || tok.startsWith("/"))
			{
				movementsMult = new Integer[1];
				movementsMult[0] = new Integer(tok.substring(1));
				movementMultOp = new String[1];
				movementMultOp[0] = new String(tok.substring(0, 1));
				setMovement(0);
				movements = new Integer[1];
				movements[0] = getMovement();
			}
			else if (tok.length() > 0)
			{
				newmove = Integer.parseInt(tok);
				setMovement(newmove);
				movements = new Integer[1];
				movements[0] = getMovement();
				movementTypes = new String[1];
				movementsMult = new Integer[1];
				movementsMult[0] = new Integer(0);
				movementMultOp = new String[1];
				movementMultOp[0] = new String("");
			} // added by Syndaryl 31/03/02
//			Globals.debugPrint("MoveRate token first char: " + op);
			movementTypes[0] = "Walk";
		}
		else
		{
			movements = new Integer[moves.countTokens() / 2];
			movementTypes = new String[moves.countTokens() / 2];
			movementsMult = new Integer[moves.countTokens() / 2];
			movementMultOp = new String[moves.countTokens() / 2];
			int x = 0;
			while (moves.countTokens() > 1)
			{
				movementTypes[x] = moves.nextToken();
				tok = moves.nextToken(); // modified in preparation for future code changes. Do not remove commented section

				Globals.debugPrint("multiple option in " + this.toString() + ":" + movementTypes[x] + " " + tok);

				if (tok.startsWith("*") || tok.startsWith("/"))
				{
					Globals.debugPrint("* and /");
					movementsMult[x] = new Integer(tok.substring(1));
					movementMultOp[x] = new String(tok.substring(0, 1));
					movements[x] = new Integer(0);
				}
				else if (tok.length() > 0)
				{
					Globals.debugPrint("normal");
					movementsMult[x] = new Integer(0);
					movementMultOp[x] = new String("");
					newmove = Integer.parseInt(tok);
					movements[x] = new Integer(newmove);
					if (movementTypes[x].equals("Walk"))
						setMovement(movements[x]);
				}

/*
				op = tok.charAt(0);
				Globals.debugPrint("MoveRate token first char: " + op);
				/*
				tok = tok.substring(1);
				if (tok.startsWith("*")) {
					tok = String.valueOf(getPCMovement(x) * Integer.valueOf(tok.substring(1)));
				} else if (tok.startsWith("/")) {
					tok = String.valueOf(getPCMovement(x) / Integer.valueOf(tok.substring(1)));
				} else {
					newmove = Integer.valueOf(tok);
				}  // added by Syndaryl 31/03/02

				newmove = Integer.valueOf(tok);
				movements[x] = newmove;
				if (movementTypes[x].equals("Walk"))
					setMovement(movements[x]);
*/
				x++;
			}
		}
	}

	public Integer getMovement()
	{
		return movement;
	}

	private void setMovement(Integer anInt)
	{
		movement = new Integer(anInt.toString());
	}

	private void setMovement(int anInt)
	{
		movement = new Integer(anInt);
	}

	public ArrayList getWeaponProfBonus()
	{
		return weaponProfBonus;
	}

	public void setWeaponProfBonus(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
		{
			getWeaponProfBonus().add(aTok.nextToken());
		}
	}

	public double getCost()
	{
		return cost;
	}

	public void setCost(double cost)
	{
		this.cost = cost;
	}

	public String getDR(int level, int hitdice, String size)
	{
		String drString = "";
		if (DR != null)
			drString = DR.trim();
		int x;
		for (x = 0; x < levelStrings.size(); x++)
			if (contains(levelStrings.get(x), "DR:") && doesLevelQualify(level, x))
				drString = drString + levelStrings.get(x) + "|";
		for (x = 0; x < hitDiceStrings.size(); x++)
			if (contains(hitDiceStrings.get(x), "DR:") && doesHitDiceQualify(hitdice, x))
				drString = drString + getStringAfter("DR:", hitDiceStrings.get(x)) + "|";
		return drString;
	}

	private static String addDR(String DR)
	{
		StringTokenizer tokens = new StringTokenizer(DR, ",");
		ArrayList tDRs = new ArrayList();
		ArrayList DRs = new ArrayList();
		int x = 0;
		int y = 0;
		while (tokens.hasMoreTokens())
			tDRs.add(tokens.nextToken());

		if (tDRs.size() < 2) return DR;

		for (x = 0; x < tDRs.size(); x++)
		{
			StringTokenizer DRtokens = new StringTokenizer((String)tDRs.get(x), "/");
			DRs.add(DRtokens.nextToken());
			DRs.add(DRtokens.nextToken());
		}

		y = 0;
		while (y <= (DRs.size() - 4))
		{

// Globals.debugPrint("addDR:while:y: " + y);
// Globals.debugPrint("addDR:DRs.get(y): " + DRs.get(y));
// Globals.debugPrint("addDR:DRs.get(y+1): " + DRs.get(y+1));
// Globals.debugPrint("addDR:while:,----------,");
// Globals.debugPrint("addDR:DRs.get(y+2): " + DRs.get(y+2));
// Globals.debugPrint("addDR:DRs.get(y+3): " + DRs.get(y+3));

			if ((DRs.get(y + 1)).equals("-"))
			{
				y += 2;
				continue;
			}
			else if ((DRs.get(y + 1)).equals(DRs.get(y + 3)))
			{
				if (Integer.parseInt((String)DRs.get(y)) <= Integer.parseInt((String)DRs.get(y + 2)))
				{
					DRs.remove(y + 1);
					DRs.remove(y);
					y += 2;
				}
				else
				{
					DRs.remove(y + 3);
					DRs.remove(y + 2);
					y += 2;
				}
			}
			y += 2;
		}

		StringBuffer newDR = new StringBuffer();
		for (x = 0; x < (DRs.size() - 1); x += 2)
		{
			newDR.append((String)DRs.get(x)).append("/").append((String)DRs.get(x + 1)).append(",");
		}
		if (newDR.length() != 0)
		{
			newDR = new StringBuffer(newDR.substring(0, newDR.length() - 1));
		}
		return newDR.toString();
	}

	public ArrayList getTemplates(boolean isImporting)
	{
		ArrayList newTemplates = new ArrayList();
		templatesAdded = new ArrayList();

		if (!isImporting)
		{
			for (Iterator e = templates.iterator(); e.hasNext();)
			{
				String templateName = (String)e.next();
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
	public static String chooseTemplate(String templateList)
	{
		ArrayList choiceTemplates = Utility.split(templateList, '|');
		for (int i = choiceTemplates.size() - 1; i >= 0; i--)
		{
			final String templateName = (String)choiceTemplates.get(i);
			final PCTemplate template = Globals.getTemplateNamed(templateName);
			if ((template == null) || !template.passesPreReqTests())
			{
				choiceTemplates.remove(i);
			}
		}
		if (choiceTemplates.size() > 0)
		{
			return Globals.chooseFromList("Template Choice", choiceTemplates, null, 1);
		}
		return "";
	}

	public ArrayList templatesAdded()
	{
		if (templatesAdded == null)
			return new ArrayList();
		return templatesAdded;
	}

	public int getBonusInitialFeats()
	{
		return bonusInitialFeats;
	}

	public void setBonusInitialFeats(int bonusInitialFeats)
	{
		this.bonusInitialFeats = bonusInitialFeats;
	}

	private boolean doesLevelQualify(int level, int x)
	{
		final StringTokenizer stuff = new StringTokenizer((String)levelStrings.get(x), ":");
		return level >= Integer.parseInt(stuff.nextToken());
	}

	private boolean doesHitDiceQualify(int hitdice, int x)
	{
		StringTokenizer tokens = new StringTokenizer((String)hitDiceStrings.get(x), ":");
		String hitDiceString = tokens.nextToken();
		if (hitDiceString.endsWith("+"))
			return Integer.parseInt(hitDiceString.substring(0, hitDiceString.length() - 1)) <= hitdice;
		tokens = new StringTokenizer(hitDiceString, "-");
		return hitdice >= Integer.parseInt(tokens.nextToken()) && hitdice <= Integer.parseInt(tokens.nextToken());
	}

	private String getStringAfter(String stuff, Object object)
	{
		String string = (String)object;
		while (string.length() >= stuff.length() && !string.startsWith(stuff))
			string = string.substring(1);
		if (string.length() <= stuff.length())
			return "";
		return string.substring(stuff.length());
	}

	private boolean contains(Object object, String stuff)
	{
		String string = (String)object;
		while (string.length() >= stuff.length())
		{
			if (string.startsWith(stuff))
				return true;
			string = string.substring(1);
		}
		return false;
	}

	public PCTemplate()
	{
		for (int x = 0; x < Globals.s_ATTRIBSHORT.length; x++)
		{
			statMods[x] = 0;
			nonAbilities[x] = false;
		}
	}
}
