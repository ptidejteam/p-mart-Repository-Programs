/* * PCTemplate.java * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net> * * This library is free software; you can redistribute it and/or * modify it under the terms of the GNU Lesser General Public * License as published by the Free Software Foundation; either * version 2.1 of the License, or (at your option) any later version. * * This library is distributed in the hope that it will be useful, * but WITHOUT ANY WARRANTY; without even the implied warranty of * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU * Lesser General Public License for more details. * * You should have received a copy of the GNU Lesser General Public * License along with this library; if not, write to the Free Software * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA * * Created on August 03, 2001, 2:15 PM  Kurt Wimmer */

package pcgen.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import pcgen.gui.Chooser;

/** * <code>PCTemplate</code>. * * @author Mark Hulsman <hulsmanm@purdue.edu> * @version $Revision: 1.1 $ */
public class PCTemplate extends PObject
{

	private boolean visible = true;
	private boolean removable = true;
	private int hitDiceSize = 0;
	private ArrayList levelStrings = new ArrayList();
	private ArrayList hitDiceStrings = new ArrayList();
	private ArrayList sizeStrings = new ArrayList();
	private String DR = "";
	private String subRace = "None";
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
	private int[] statMods = new int[6];
	private boolean[] nonAbilities = new boolean[6];
	private int natAC = 0;
	private ArrayList templates = new ArrayList();
	private char[] sizes = {'F', 'D', 'T', 'S', 'M', 'L', 'H', 'G', 'C'};
	/******* added by TNC 10/01/01 *****/
	private ArrayList cSkillList = new ArrayList();
	private ArrayList ccSkillList = new ArrayList();
	private ArrayList templatesAdded = null;

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
		/*if (getCSkillList().contains("LIST"))
		{
			String aString = null;
			for (Iterator e = associatedList.iterator(); e.hasNext();)
			{
				aString = (String)e.next();
				if (aName.startsWith(aString) || aString.startsWith(aName))
					return true;
			}
		}*/
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
		System.out.println("Entering setCSkillList: " + aString);
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
				System.out.println("adding to CSkillList: " + bString);
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
	
	/***********************************/
	public boolean isVisible()
	{
		return visible;
	}

	public boolean isRemovable()
	{
		return visible && removable;
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
		aTemp.visible = visible;
		aTemp.cSkillList = (ArrayList)getCSkillList().clone();
		aTemp.ccSkillList = (ArrayList)getCcSkillList().clone();
		return aTemp;
	}

	public String toString()
	{
		return name;
	}

	public String modifierString()
	{
		StringBuffer mods = new StringBuffer();
		for (int x = 0; x < statMods.length; x++)
			if (nonAbilities[x])
				mods.append(statName(x)).append(":nonability ");
			else if (statMods[x] != 0)
				mods.append(statName(x)).append(":").append(statMods[x]).append(" ");
		if (hitDiceSize != 0)
			mods.append("HITDICESIZE:").append(hitDiceSize).append(" ");
		if (Globals.getCurrentPC() == null)
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
		if (getNaturalArmor(Globals.getCurrentPC().totalLevels(), Globals.getCurrentPC().totalHitDice(), Globals.getCurrentPC().getSize()) != 0)
			mods.append("NATURALARMOR:").append(getNaturalArmor(Globals.getCurrentPC().totalLevels(), Globals.getCurrentPC().totalHitDice(), Globals.getCurrentPC().getSize())).append(" ");
		if (getCR(Globals.getCurrentPC().totalLevels(), Globals.getCurrentPC().totalHitDice(), Globals.getCurrentPC().getSize()) != 0)
			mods.append("CR:").append(getCR(Globals.getCurrentPC().totalLevels(), Globals.getCurrentPC().totalHitDice(), Globals.getCurrentPC().getSize())).append(" ");
		if (getSR(Globals.getCurrentPC().totalLevels(), Globals.getCurrentPC().totalHitDice(), Globals.getCurrentPC().getSize()) != 0)
			mods.append("SR:").append(getSR(Globals.getCurrentPC().totalLevels(), Globals.getCurrentPC().totalHitDice(), Globals.getCurrentPC().getSize())).append(" ");
		if (!getDR(Globals.getCurrentPC().totalLevels(), Globals.getCurrentPC().totalHitDice(), Globals.getCurrentPC().getSize()).equals(""))
			mods.append("DR:").append(getDR(Globals.getCurrentPC().totalLevels(), Globals.getCurrentPC().totalHitDice(), Globals.getCurrentPC().getSize())).append(" ");
		return mods.toString();
	}

	private String statName(int x)
	{
		switch (x)
		{
			case 0:
				return "STR";
			case 1:
				return "DEX";
			case 2:
				return "CON";
			case 3:
				return "INT";
			case 4:
				return "WIS";
			case 5:
				return "CHA";
		}
		return "";
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

	public void setMoveRates(String moveparse) {
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
		String newDR = "";
		for (x = 0; x < DRs.size(); x++)
		{
			newDR += (String)DRs.get(x) + "/" + (String)DRs.get(x + 1) + ",";
		}
		if (newDR.length() != 0)
			newDR = newDR.substring(0, newDR.length() - 1);
		return newDR;
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

	public ArrayList getTemplates()
	{
		ArrayList newTemplates = new ArrayList();
		templatesAdded = new ArrayList();
		for(int x = 0; x < templates.size(); x++)
		{
			if(((String)templates.get(x)).startsWith("CHOOSE:"))
				newTemplates.add(chooseTemplate(((String)templates.get(x)).substring(7)));
			else
				newTemplates.add((String)templates.get(x));
			templatesAdded.add(newTemplates.get(x));
		}
		return newTemplates;
	}

	public static String chooseTemplate(String templateList)
	{
		StringTokenizer tokens = new StringTokenizer(templateList, "|");
		if(tokens.countTokens() == 0)
			return "";
		Chooser c = new Chooser();
		c.setPool(1);
		c.setPoolFlag(false);
		c.setTitle("Template Choice");
		ArrayList list = new ArrayList();
		while(tokens.hasMoreTokens())
			list.add(tokens.nextToken());
		c.setAvailableList(list);
		c.show();
		List selectedList = c.getSelectedList();
		if(selectedList.size() == 0)
			return "";
		return (String)selectedList.get(0);
	}
	
	public ArrayList templatesAdded()
	{
		if(templatesAdded == null)
			return new ArrayList();
		return templatesAdded;
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
		for (int x = 0; x < sizes.length; x++)
			if (sizes[x] == size)
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
			String colString = new String(colToken.nextToken());
			if (col == 0) {
				setName(colString);
				templName = colString;
			}
			else if (colString.startsWith("HITDICESIZE:"))
			{
				hitDiceSize = Integer.parseInt(colString.substring(12));
				if (hitDiceSize <= 0)
					hitDiceSize = 0;
			}
			else if (colString.startsWith("LEVEL:"))
				levelStrings.add(colString.substring(6));
			else if (colString.startsWith("MOVE:"))
				setMoveRates(colString.substring(5));
			else if (colString.startsWith("MOVEA:")) {
				setMoveRates(colString.substring(6));
				moveRatesFlag = 1;
			}
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
			else if (colString.startsWith("STR:"))
			{
				if (colString.charAt(4) == '*')
				{
					statMods[0] = 0;
					nonAbilities[0] = true;
				}
				else
					statMods[0] = Integer.parseInt(colString.substring(4));
			}
			else if (colString.startsWith("DEX:"))
			{
				if (colString.charAt(4) == '*')
				{
					statMods[1] = 0;
					nonAbilities[1] = true;
				}
				else
					statMods[1] = Integer.parseInt(colString.substring(4));
			}
			else if (colString.startsWith("CON:"))
			{
				if (colString.charAt(4) == '*')
				{
					statMods[2] = 0;
					nonAbilities[2] = true;
				}
				else
					statMods[2] = Integer.parseInt(colString.substring(4));
			}
			else if (colString.startsWith("INT:"))
			{
				if (colString.charAt(4) == '*')
				{
					statMods[3] = 0;
					nonAbilities[3] = true;
				}
				else
					statMods[3] = Integer.parseInt(colString.substring(4));
			}
			else if (colString.startsWith("WIS:"))
			{
				if (colString.charAt(4) == '*')
				{
					statMods[4] = 0;
					nonAbilities[4] = true;
				}
				else
					statMods[4] = Integer.parseInt(colString.substring(4));
			}
			else if (colString.startsWith("CHA:"))
			{
				if (colString.charAt(4) == '*')
				{
					statMods[5] = 0;
					nonAbilities[5] = true;
				}
				else
					statMods[5] = Integer.parseInt(colString.substring(4));
			}
			else if (colString.startsWith("VISIBLE:"))
			{
				if (colString.substring(8).startsWith("No"))
					visible = false;
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
			else if (colString.startsWith("SUBRACE:")) {
				subRace = colString.substring(8);
				if (subRace.equals("Yes")) subRace = templName;
			}
			else if (colString.startsWith("SOURCE:"))
				super.setSource(colString.substring(7));
			else
				JOptionPane.showMessageDialog(null, "Illegal template info " + sourceFile.getName() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"", "PCGen", JOptionPane.ERROR_MESSAGE);
		}
	}

	public PCTemplate()
	{
		for (int x = 0; x < 6; x++)
		{
			statMods[x] = 0;
			nonAbilities[x] = false;
		}
	}
}