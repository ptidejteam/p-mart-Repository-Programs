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

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import pcgen.gui.Chooser;

/**
 * <code>PCTemplate</code>.
 *
 * @author Mark Hulsman <hulsmanm@purdue.edu>
 * @version $Revision: 1.1 $
 */
public class PCTemplate extends PObject
{

	private int templateVisible = 1; // Hidden Templates
	//	private boolean visible = true;
	private boolean removable = true;
	private int hitDiceSize = 0;
	private ArrayList levelStrings = new ArrayList();
	private ArrayList hitDiceStrings = new ArrayList();
	private ArrayList sizeStrings = new ArrayList();
	private String DR = "";
	private int bonusSkillsPerLevel = 0;
	private int bonusInitialFeats = 0;
	private String ageString = "";
	private String heightString = "";
	private String weightString = "";
	private String vision = "None";
	private int visionFlag = 1;
	private String subRace = "None";
	private String region = "None";
	private BigDecimal gold = new BigDecimal("0.00");
	private String goldString = new String();
	private boolean goldflag = false;
	private Integer movement = new Integer(0);
	private String[] movementTypes;
	private Integer[] movements;
	private String favoredClass = "";
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

	public ArrayList getCSkillList()
	{
		return cSkillList;
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

	private void setCSkillList(String aString)
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

	private void setCCSkillList(String aString)
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

	public String getFavoredClass()
	{
		return favoredClass;
	}

	private void setFavoredClass(String newClass)
	{
		favoredClass = newClass;
	}

	public String getSubRace()
	{
		return subRace;
	}

	public String getRegion()
	{
		return region;
	}

	public int isVisible()
	{
		return templateVisible;
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
		for (int x = 0; x < sizeStrings.size(); x++)
			if (contains(sizeStrings.get(x), "FEAT:") && doesSizeQualify(size, x))
				feats.add(getStringAfter("FEAT:", sizeStrings.get(x)));
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
		for (int x = 0; x < sizeStrings.size(); x++)
			if (contains(sizeStrings.get(x), "SR:") && doesSizeQualify(size, x))
			{
				newSR = Integer.parseInt(getStringAfter("SR:", sizeStrings.get(x)));
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
		for (int x = 0; x < sizeStrings.size(); x++)
			if (contains(sizeStrings.get(x), "CR:") && doesSizeQualify(size, x))
				CR += Integer.parseInt(getStringAfter("CR:", sizeStrings.get(x)));
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
		for (int x = 0; x < sizeStrings.size(); x++)
			if (contains(sizeStrings.get(x), "SA:") && doesSizeQualify(size, x))
				SAs.add(getStringAfter("SA:", sizeStrings.get(x)));
		return SAs;
	}

	public int getLevelAdjustment()
	{
		return levelAdjustment;
	}

	public int getHitDiceSize()
	{
		return hitDiceSize;
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

	private void setLanguageAutos(String aString)
	{
		StringTokenizer aTok = new StringTokenizer(aString, ",", false);
		while (aTok.hasMoreTokens())
			languageAutos.add(aTok.nextToken());
	}

	public Set getLanguageBonus()
	{
		return languageBonus;
	}

	private void setLanguageBonus(String aString)
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

	private void setGoldString(String aString)
	{
		goldString = aString;
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

	public String getVision()
	{
		return vision;
	}

	public int getVisionFlag()
	{
		return visionFlag;
	}

	public double getCost()
	{
		return cost;
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
		for (x = 0; x < sizeStrings.size(); x++)
			if (contains(sizeStrings.get(x), "DR:") && doesSizeQualify(size, x))
				SAs.add(getStringAfter("DR:", sizeStrings.get(x)));
		return addDR(DR);
	}

	public static String addDR(String DR)
	{
		StringTokenizer tokens = new StringTokenizer(DR, "/,");
		ArrayList DRs = new ArrayList();
		int x = 0;
		int y = 0;
		boolean changed = false;
		while (tokens.hasMoreTokens())
			DRs.add(tokens.nextToken());
		while (x < DRs.size())
		{
			y = 0;
			changed = false;
			while (y < DRs.size())
			{
				if ((((String)DRs.get(x + 1)).equals("-") && !((String)DRs.get(y + 1)).equals("-")) || (!((String)DRs.get(x + 1)).equals("-") && ((String)DRs.get(y + 1)).equals("-")))
				{
					y += 2;
					continue;
				}
				if (((String)DRs.get(x + 1)).equals((String)DRs.get(y + 1)) && Integer.parseInt((String)DRs.get(x)) <= Integer.parseInt((String)DRs.get(y)))
				{
					DRs.remove(x + 1);
					DRs.remove(x);
					changed = true;
					break;
				}
				y += 2;
			}
			if (!changed)
				x += 2;
		}
		StringBuffer newDR = new StringBuffer();
		for (x = 0; x < DRs.size(); x++)
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
		for (x = 0; x < sizeStrings.size(); x++)
			if (contains(sizeStrings.get(x), "NATURALARMOR:") && doesSizeQualify(size, x))
			{
				newAC = Integer.parseInt(getStringAfter("NATURALARMOR:", sizeStrings.get(x)));
				if (newAC > AC)
					AC = newAC;
			}
		return AC;
	}

	public ArrayList getTemplates(boolean flag)
	{
		ArrayList newTemplates = new ArrayList();
		templatesAdded = new ArrayList();

		final PlayerCharacter aPC = Globals.getCurrentPC();

		for (int x = 0; x < templates.size(); x++)
		{
			if (((String)templates.get(x)).startsWith("CHOOSE:") && !flag)
			{
				newTemplates.add(PCTemplate.chooseTemplate(((String)templates.get(x)).substring(7)));
				templatesAdded.add(newTemplates.get(x));
			}
			else if (!flag)
			{
				newTemplates.add((String)templates.get(x));
				templatesAdded.add(newTemplates.get(x));
			}
		}
		return newTemplates;
	}

	public static String chooseTemplate(String templateList)
	{
		StringTokenizer tokens = new StringTokenizer(templateList, "|");
		if (tokens.countTokens() == 0)
			return "";
		Chooser c = new Chooser();
		c.setPool(1);
		c.setPoolFlag(false);
		c.setTitle("Template Choice");
		ArrayList list = new ArrayList();
		while (tokens.hasMoreTokens())
			list.add(tokens.nextToken());
		c.setAvailableList(list);
		c.show();
		List selectedList = c.getSelectedList();
		if (selectedList.size() == 0)
			return "";
		return (String)selectedList.get(0);
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

	private boolean doesSizeQualify(String size, int x)
	{
		final StringTokenizer tokens = new StringTokenizer((String)sizeStrings.get(x), ":");
		final String sizeString = tokens.nextToken();
		if (sizeString.length() == 1)
		{
			if (sizeString.equals(size))
				return true;
			return false;
		}
		else if (sizeString.charAt(1) != '+')
			return false;
		if (getSizeNum(size.charAt(0)) >= getSizeNum(sizeString.charAt(0)))
			return true;
		return false;
	}

	private int getSizeNum(char size)
	{
		for (int x = 0; x < Globals.s_SIZESHORTCHAR.length; x++)
			if (Globals.s_SIZESHORTCHAR[x] == size)
				return x;
		return -1;
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

	public void parseLine(String inputLine, File sourceFile, int lineNum)
	{
		final String tabdelim = "\t";
		final StringTokenizer colToken = new StringTokenizer(inputLine, tabdelim, false);
		String templName = "None";
		final int colMax = colToken.countTokens();
		int col = 0;
		if (colMax == 0)
			return;
		boolean stats = true;
		for (col = 0; col < colMax; col++)
		{
			final String colString = new String(colToken.nextToken());

			if (super.parseTag(colString))
				continue;
			if (col == 0)
			{
				setName(colString);
				templName = colString;
			}
			else if (colString.startsWith("HITDICESIZE:"))
			{
				hitDiceSize = Integer.parseInt(colString.substring(12));
				if (hitDiceSize <= 0)
					hitDiceSize = 0;
			}

			// Dummy functions. I will add soon --- arcady
			else if (colString.startsWith("HEIGHT:"))
			{
				heightString = colString.substring(7);
			} // replace racial height
			else if (colString.startsWith("WEIGHT:"))
			{
				weightString = colString.substring(7);
			} // replace racial weight
			else if (colString.startsWith("VISION:"))
			{
				StringTokenizer visToken = new StringTokenizer(colString.substring(7), "|", false);

				visionFlag = Integer.parseInt(visToken.nextToken());
				vision = visToken.nextToken();
			} // replace, add to, or remove from vision type.
			else if (colString.startsWith("AGE:"))
			{
				ageString = colString.substring(4);
			} // replaces racial age
			else if (colString.startsWith("LEVELSPERFEAT:"))
			{
				int newLevels = Integer.parseInt(colString.substring(14));
				if (newLevels >= 0)
					levelsPerFeat = newLevels;
			} // how many levels per feat.
			else if (colString.startsWith("BONUSSKILLPOINTS:"))
			{
				bonusSkillsPerLevel = Integer.parseInt(colString.substring(17));
			} // additional skill points per level
			else if (colString.startsWith("BONUSFEATS:"))
			{
				bonusInitialFeats = Integer.parseInt(colString.substring(11));
			} // number of additional feats to spend
			else if (colString.startsWith(Globals.s_TAG_TYPE))
			{
				setType(colString.substring(Globals.s_TAG_TYPE.length()));
			} // What is the 'Type' of this here critter.
			else if (colString.startsWith("POPUPALERT:"))
			{
				JOptionPane.showMessageDialog(null, colString.substring(11), "PCGen", JOptionPane.ERROR_MESSAGE);
			} // pops the message to the screen.


			else if (colString.startsWith("COST:"))
			{
				cost = Double.parseDouble(colString.substring(5));
			}

			else if (colString.startsWith("LEVEL:"))
				levelStrings.add(colString.substring(6));
			else if (colString.startsWith("MOVECLONE:"))
			{
				setMoveRates(colString.substring(10));
				moveRatesFlag = 2;
			}
			else if (colString.startsWith("MOVEA:"))
			{
				setMoveRates(colString.substring(6));
				moveRatesFlag = 1;
			}
			else if (colString.startsWith("MOVE:"))
				setMoveRates(colString.substring(5));
			else if (colString.startsWith("GOLD:"))
				setGoldString(colString.substring(5));
			else if (colString.startsWith("HD:"))
				hitDiceStrings.add(colString.substring(3));
			else if (colString.startsWith("SIZE:"))
				sizeStrings.add(colString.substring(5));
			else if (colString.startsWith("DR:"))
				if (!DR.equals(""))
					DR += "," + colString.substring(3);
				else
					DR = colString.substring(3);
			else if (colString.startsWith("SR:"))
				SR = Integer.parseInt(colString.substring(3));
			else if (colString.startsWith("CR:"))
				CR = Integer.parseInt(colString.substring(3));
			else if (colString.startsWith("LANGAUTO"))
				setLanguageAutos(colString.substring(9));
			else if (colString.startsWith("LANGBONUS"))
				setLanguageBonus(colString.substring(10));
			else if (colString.startsWith("LEVELADJUSTMENT:"))
				levelAdjustment = Integer.parseInt(colString.substring(16));
			else if (colString.startsWith("SA:"))
				SAs.add(colString.substring(3));
			else if (colString.startsWith("PRE"))
				preReqArrayList.add(colString);
			else if (colString.startsWith("QUALIFY:"))
				addToQualifyListing(colString.substring(8));
			else if (colString.startsWith("CSKILL:"))
				setCSkillList(colString.substring(7));
			else if (colString.startsWith("CCSKILL:"))
				setCCSkillList(colString.substring(8));
			else if (colString.startsWith("FAVOREDCLASS:"))
			{
				setFavoredClass(colString.substring(13));
			}
			else if (colString.startsWith("FEAT:"))
				featStrings.add(colString.substring(5));
			else if (colString.startsWith("VISIBLE:"))
			{
				if (colString.substring(8).startsWith("Export"))
				{
					templateVisible = 2; // output, no display: character sheet only
				}
				else if (colString.substring(8).startsWith("No"))
				{
					templateVisible = 0; // no Output, no display: Stealth
				}
				else if (colString.substring(8).startsWith("Display"))
				{
					templateVisible = 3; // Display only, no output: DisplayOnly
				}
				else
				{
					templateVisible = 1; // default. Display and output: Yes
				}
			}

			else if (colString.startsWith("NATURALARMOR:"))
				natAC = Integer.parseInt(colString.substring(13));
			else if (colString.startsWith("TEMPLATE:"))
				templates.add(colString.substring(9));
			else if (colString.startsWith("REMOVABLE:"))
			{
				if (colString.substring(10).startsWith("No"))
					removable = false;
			}
			else if (colString.startsWith("SUBRACE:"))
			{
				subRace = colString.substring(8);
				if (subRace.equals("Yes")) subRace = templName;
			}
			else if (colString.startsWith("REGION:"))
			{
				region = colString.substring(7);
				if (region.equals("Yes")) region = templName;
			}
			else if (colString.startsWith("DEFINE"))
				variableList.add("0|" + colString.substring(7));
			else if (colString.startsWith("SOURCE:"))
				super.setSource(colString.substring(7));
			else
			{
				int iStat;
				for (iStat = 0; iStat < Globals.s_ATTRIBSHORT.length; iStat++)
				{

					final String statName = Globals.s_ATTRIBSHORT[iStat] + ":";
					if (colString.startsWith(statName))
					{
						if (colString.charAt(4) == '*')
						{
							statMods[iStat] = 0;
							nonAbilities[iStat] = true;
						}
						else
						{
							statMods[iStat] = Integer.parseInt(colString.substring(4));
						}
						break;
					}
				}
				if (iStat >= Globals.s_ATTRIBSHORT.length)
					JOptionPane.showMessageDialog(null, "Illegal template info " + sourceFile.getName() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"", "PCGen", JOptionPane.ERROR_MESSAGE);
			}
		}
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