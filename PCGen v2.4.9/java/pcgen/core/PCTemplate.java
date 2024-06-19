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
import pcgen.gui.Chooser;

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
	private int hitDiceSize = 0;
	private ArrayList levelStrings = new ArrayList();
	private ArrayList hitDiceStrings = new ArrayList();
	private ArrayList sizeStrings = new ArrayList();
	private ArrayList weaponProfAutos = new ArrayList();
	private String DR = "";
	private String templateSize = "";
	private int bonusSkillsPerLevel = 0;
	private int bonusInitialFeats = 0;
	private String ageString = "";
	private String heightString = "";
	private String weightString = "";
	private String vision = "None";
	private int visionFlag = 1;
	private String subRace = "None";
	private String region = "None";
	private String subregion = "None";
	private String kit = "";
	private BigDecimal gold = new BigDecimal("0.00");
	private String goldString = new String();
	private boolean goldflag = false;
	private int goldReplaceFlag = 0;
	private Integer movement = new Integer(0);
	private String[] movementTypes;
	private Integer[] movements;
	private String favoredClass = "";
	private String chooseLanguageAutos = "";
	private int moveRatesFlag = 0;
	private int SR = 0;
	private int CR = 0;
	private int levelAdjustment = 0;
	private ArrayList SAs = new ArrayList();
	private ArrayList featStrings = new ArrayList();
	private int[] statMods = new int[Globals.s_ATTRIBLONG.length];
	private boolean[] nonAbilities = new boolean[Globals.s_ATTRIBLONG.length];
	private int natAC = 0;
	private int levelsPerFeat = 3;

	private ArrayList templates = new ArrayList();
	private ArrayList cSkillList = new ArrayList();
	private ArrayList ccSkillList = new ArrayList();
	private ArrayList templatesAdded = null;

	private TreeSet languageAutos = new TreeSet();
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

	public String getDR()
	{
		return DR;
	}

	public void setDR(String DR)
	{
		this.DR = DR;
	}

	public int getSR()
	{
		return SR;
	}

	public void setSR(int SR)
	{
		this.SR = SR;
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
		return levelAdjustment;
	}

	public void setLevelAdjustment(int levelAdjustment)
	{
		this.levelAdjustment = levelAdjustment;
	}

	public List getSAs()
	{
		return SAs;
	}

	public void addSA(String SA)
	{
		this.SAs.add(SA);
	}

	public List getFeatStrings()
	{
		return featStrings;
	}

	public void addFeatString(String featString)
	{
		this.featStrings.add(featString);
	}

	public int getNatAC()
	{
		return natAC;
	}

	public void setNatAC(int natAC)
	{
		this.natAC = natAC;
	}

	public List getTemplates()
	{
		return templates;
	}

	public void addTemplate(String template)
	{
		templates.add(template);
	}

	public ArrayList getCSkillList()
	{
		return cSkillList;
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

	public ArrayList getCcSkillList()
	{
		return ccSkillList;
	}

	public boolean hasCSkill(String aName)
	{
		if (getCSkillList().contains(aName))
			return true;
		String aString = null;
		for (Iterator e = getCSkillList().iterator(); e.hasNext();)
		{
			aString = (String)e.next();
			if (aString.lastIndexOf("%") > -1)
			{
				aString = aString.substring(0, aString.length() - 1);
				if (aName.startsWith(aString))
					return true;
			}
		}
		return false;
	}

	public boolean hasCCSkill(String aName)
	{
		if (getCSkillList().contains(getName()))
			return true;
		String aString = null;
		for (Iterator e = getCcSkillList().iterator(); e.hasNext();)
		{
			aString = (String)e.next();
			if (aString.lastIndexOf("%") > -1)
			{
				aString = aString.substring(0, aString.length() - 1);
				if (aName.startsWith(aString))
					return true;
			}
		}
		return false;
	}

	public void setCSkillList(String aString)
	{
		//System.out.println("Entering setCSkillList: " + aString);
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		String bString = null;
		while (aTok.hasMoreTokens())
		{
			bString = aTok.nextToken();
			if (bString.startsWith("TYPE."))
			{
				Skill aSkill = null;
				for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
				{
					aSkill = (Skill)e1.next();
					if (aSkill.getType().indexOf(bString.substring(5)) >= 0)
					{
						getCSkillList().add(aSkill.getName());
					}
				}
			}
			else
			{
				/** add skill to list of class skills **/
				//System.out.println("adding to CSkillList: " + bString);
				getCSkillList().add(bString);
			}
		}
	}

	public void setCCSkillList(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
		{
			String bString = aTok.nextToken();
			if (bString.startsWith("TYPE."))
			{
				Skill aSkill = null;
				for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
				{
					aSkill = (Skill)e1.next();
					if (aSkill.getType().indexOf(bString.substring(5)) >= 0)
					{
						getCcSkillList().add(aSkill.getName());
					}
				}
			}
			else
			{
				/** add skill to list of class skills **/
				getCcSkillList().add(bString);
			}
		}
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
		aTemp.cSkillList = (ArrayList)getCSkillList().clone();
		aTemp.ccSkillList = (ArrayList)getCcSkillList().clone();
		aTemp.languageAutos = (TreeSet)languageAutos.clone();
		aTemp.languageBonus = (TreeSet)languageBonus.clone();

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
			if (natAC != 0)
				mods.append("NATURALARMOR:").append(natAC).append(" ");
			if (CR != 0)
				mods.append("CR:").append(CR).append(" ");
			if (SR != 0)
				mods.append("SR:").append(SR).append(" ");
			if (!DR.equals(""))
				mods.append("DR:").append(DR).append(" ");
			return mods.toString();
		}
		if (getNaturalArmor(aPC.totalLevels(), aPC.totalHitDice(), aPC.getSize()) != 0)
			mods.append("NATURALARMOR:").append(getNaturalArmor(aPC.totalLevels(), aPC.totalHitDice(), aPC.getSize())).append(" ");
		if (getCR(aPC.totalLevels(), aPC.totalHitDice(), aPC.getSize()) != 0)
			mods.append("CR:").append(getCR(aPC.totalLevels(), aPC.totalHitDice(), aPC.getSize())).append(" ");
		if (getSR(aPC.totalLevels(), aPC.totalHitDice(), aPC.getSize()) != 0)
			mods.append("SR:").append(getSR(aPC.totalLevels(), aPC.totalHitDice(), aPC.getSize())).append(" ");
		if (!getDR(aPC.totalLevels(), aPC.totalHitDice(), aPC.getSize()).equals(""))
			mods.append("DR:").append(getDR(aPC.totalLevels(), aPC.totalHitDice(), aPC.getSize())).append(" ");

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
		for (int x = 0; x < levelStrings.size(); x++)
			if (contains(levelStrings.get(x), "FEAT:") && doesLevelQualify(level, x))
				feats.add(getStringAfter("FEAT:", levelStrings.get(x)));
		for (int x = 0; x < hitDiceStrings.size(); x++)
			if (contains(hitDiceStrings.get(x), "FEAT:") && doesHitDiceQualify(hitdice, x))
				feats.add(getStringAfter("FEAT:", hitDiceStrings.get(x)));
		return feats;
	}

	public int getSR(int level, int hitdice, String size)
	{
		int SR = this.SR;
		int newSR = 0;
		for (int x = 0; x < levelStrings.size(); x++)
			if (contains(levelStrings.get(x), "SR:") && doesLevelQualify(level, x))
			{
				newSR = Integer.parseInt(getStringAfter("SR:", levelStrings.get(x)));
				if (newSR > SR)
					SR = newSR;
			}
		for (int x = 0; x < hitDiceStrings.size(); x++)
			if (contains(hitDiceStrings.get(x), "SR:") && doesHitDiceQualify(hitdice, x))
			{
				newSR = Integer.parseInt(getStringAfter("SR:", hitDiceStrings.get(x)));
				if (newSR > SR)
					SR = newSR;
			}
		return SR;
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

	public List getSAs(int level, int hitdice, String size)
	{
		ArrayList SAs = (ArrayList)this.SAs.clone();
		for (int x = 0; x < levelStrings.size(); x++)
			if (contains(levelStrings.get(x), "SA:") && doesLevelQualify(level, x))
				SAs.add(getStringAfter("SA:", levelStrings.get(x)));
		for (int x = 0; x < hitDiceStrings.size(); x++)
			if (contains(hitDiceStrings.get(x), "SA:") && doesHitDiceQualify(hitdice, x))
				SAs.add(getStringAfter("SA:", hitDiceStrings.get(x)));
		return SAs;
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

			Chooser c = new Chooser();
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

	/** returns a Set of the String Language names */
	public Set getLanguageAutos()
	{
		return languageAutos;
	}

	/** returns a Set of the Language object Languages */
	public Set getAutoLanguages()
	{
		Set aSet = new TreeSet();
		for (Iterator i = languageAutos.iterator(); i.hasNext();)
		{
			Language aLang = Globals.getLanguageNamed(i.next().toString());
			if (aLang != null)
				aSet.add(aLang);
		}
		return aSet;
	}

	public void setLanguageAutos(String aString)
	{
		StringTokenizer aTok = new StringTokenizer(aString, ",", false);
		while (aTok.hasMoreTokens())
			languageAutos.add(aTok.nextToken());
	}

	public Set getLanguageBonus()
	{
		return languageBonus;
	}

	public void setLanguageBonus(String aString)
	{
		StringTokenizer aTok = new StringTokenizer(aString, ",", false);
		while (aTok.hasMoreTokens())
			getLanguageBonus().add(aTok.nextToken());
	}

	public BigDecimal getGold(BigDecimal oldGold)
	{

		if (!goldflag) setGold(rollGold().toString());
//		gold = gold.add(oldGold);
		return gold.add(oldGold);
	}

	public BigDecimal cutGold(BigDecimal oldGold)
	{
		// this won't work across saves...

		if (goldflag)
			return oldGold.subtract(gold);
		else
			return oldGold;
	}

	public void setGold(String aString)
	{
		gold = new BigDecimal(aString);
	}

	public String goldString()
	{
		return goldString;
	}

	public void setGoldString(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		goldString = aTok.nextToken();
		if (aTok.hasMoreTokens())
			goldReplaceFlag = Integer.parseInt(aTok.nextToken());
	}

	public Integer rollGold()
	{
		int dice = 4;
		int sides = 4;
		int mult = 10;
		int total = 0;
		goldflag = true;
		final StringTokenizer aTok = new StringTokenizer(goldString, ",", false);

		if (aTok.countTokens() == 1)
			return new Integer(goldString); // If it's just a number then it's not random.

		if (aTok.hasMoreTokens())
			dice = Integer.parseInt(aTok.nextToken());
		if (aTok.hasMoreTokens())
			sides = Integer.parseInt(aTok.nextToken());
		if (aTok.hasMoreTokens())
			mult = Integer.parseInt(aTok.nextToken());
		for (int roll = 0; roll < dice; roll++)
		{
			int i = Globals.getRandomInt(sides);
			if (i < 0) i = -i;
			total += i + 1;
		}
		total *= mult;
		return new Integer(total);
	}

	public String[] getMovementTypes()
	{
		return movementTypes;
	}

	public Integer[] getMovements()
	{
		return movements;
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
		if (moves.countTokens() == 1)
		{
			setMovement(new Integer(moves.nextToken()));
			movements = new Integer[1];
			movements[0] = getMovement();
			movementTypes = new String[1];
			movementTypes[0] = "Walk";
		}
		else
		{
			movements = new Integer[moves.countTokens() / 2];
			movementTypes = new String[moves.countTokens() / 2];
			int x = 0;
			while (moves.countTokens() > 1)
			{
				movementTypes[x] = moves.nextToken();
				movements[x] = new Integer(moves.nextToken());
				if (movementTypes[x].equals("Walk"))
					setMovement(movements[x]);
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

	public ArrayList weaponProfAutos()
	{
		return weaponProfAutos;
	}

	public void setWeaponProfAutos(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
		{
			weaponProfAutos().add(aTok.nextToken());
		}
	}


	public String getVision()
	{
		return vision;
	}

	public void setVision(String vision)
	{
		this.vision = vision;
	}

	public int getVisionFlag()
	{
		return visionFlag;
	}

	public void setVisionFlag(int visionFlag)
	{
		this.visionFlag = visionFlag;
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
		String DR = this.DR.trim();
		int x;
		for (x = 0; x < levelStrings.size(); x++)
			if (contains(levelStrings.get(x), "DR:") && doesLevelQualify(level, x))
				SAs.add(getStringAfter("DR:", levelStrings.get(x)));
		for (x = 0; x < hitDiceStrings.size(); x++)
			if (contains(hitDiceStrings.get(x), "DR:") && doesHitDiceQualify(hitdice, x))
				SAs.add(getStringAfter("DR:", hitDiceStrings.get(x)));
		return addDR(DR);
	}

	public static String addDR(String DR)
	{
		StringTokenizer tokens = new StringTokenizer(DR, ",");
		ArrayList tDRs = new ArrayList();
		ArrayList DRs = new ArrayList();
		int x = 0;
		int y = 0;
		boolean changed = false;
		while (tokens.hasMoreTokens())
			tDRs.add(tokens.nextToken());

		if (tDRs.size() < 2) return DR;

		for (x=0; x < tDRs.size(); x++)
		{
			StringTokenizer DRtokens = new StringTokenizer((String)tDRs.get(x), "/");
			DRs.add(DRtokens.nextToken());
			DRs.add(DRtokens.nextToken());
		}


		y = 0;
		while (y <= (DRs.size()-4))
		{

// System.out.println("addDR:while:y: " + y);
// System.out.println("addDR:DRs.get(y): " + DRs.get(y));
// System.out.println("addDR:DRs.get(y+1): " + DRs.get(y+1));
// System.out.println("addDR:while:,----------,");
// System.out.println("addDR:DRs.get(y+2): " + DRs.get(y+2));
// System.out.println("addDR:DRs.get(y+3): " + DRs.get(y+3));

			if (((String)DRs.get(y + 1)).equals("-"))
			{
				y += 2;
				continue;
			}
			else if (((String)DRs.get(y+1)).equals((String)DRs.get(y + 3)))
			{
				if (Integer.parseInt((String)DRs.get(y)) <= Integer.parseInt((String)DRs.get(y+2)))
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
		for (x = 0; x < (DRs.size()-1); x += 2)
		{
			newDR.append((String)DRs.get(x)).append("/").append((String)DRs.get(x + 1)).append(",");
		}
		if (newDR.length() != 0)
		{
			newDR = new StringBuffer(newDR.substring(0, newDR.length() - 1));
		}
		return newDR.toString();
	}

	public int getNaturalArmor(int level, int hitdice, String size)
	{
		int AC = natAC;
		int newAC = 0;
		int x;
		for (x = 0; x < levelStrings.size(); x++)
			if (contains(levelStrings.get(x), "NATURALARMOR:") && doesLevelQualify(level, x))
			{
				newAC = Integer.parseInt(getStringAfter("NATURALARMOR:", levelStrings.get(x)));
				if (newAC > AC)
					AC = newAC;
			}
		for (x = 0; x < hitDiceStrings.size(); x++)
			if (contains(hitDiceStrings.get(x), "NATURALARMOR:") && doesHitDiceQualify(hitdice, x))
			{
				newAC = Integer.parseInt(getStringAfter("NATURALARMOR:", hitDiceStrings.get(x)));

				if (newAC > AC)
					AC = newAC;
			}
		return AC;
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
				if (templateName != null)
				{
					newTemplates.add(templateName);
					templatesAdded.add(templateName);
				}
			}
		}
		return newTemplates;
	}

	public static String chooseTemplate(String templateList)
	{
		return Globals.chooseFromList("Template Choice", templateList, null, 1);
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
